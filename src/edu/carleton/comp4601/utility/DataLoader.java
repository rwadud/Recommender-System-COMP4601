package edu.carleton.comp4601.utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import edu.carleton.comp4601.analyzers.SentimentAnalyzer;
import edu.carleton.comp4601.database.DatabaseManager;
import edu.carleton.comp4601.model.Page;
import edu.carleton.comp4601.model.Review;
import edu.carleton.comp4601.model.Sentiment;
import edu.carleton.comp4601.model.User;
import edu.carleton.comp4601.resources.CorpusParser;
import javafx.scene.chart.PieChart.Data;

public class DataLoader {
	
	private static final String resourceDir = Utils.TEMP_DIR;
	private static final String userArchive = resourceDir + "/users.zip";
	public static final String reviewArchive = resourceDir + "/reviews.zip";
	private static final String pageArchive = resourceDir + "/pages.zip";
	private static Map<String, Map<String, Integer>> sentimentValues = null;
	private static final String COMMA_DELIMITER = ",";
	private static DatabaseManager db = DatabaseManager.getInstance();

	public static void loadUserData() throws IOException {
		System.out.println("Loading users");
		
	    ZipFile zipFile = new ZipFile(userArchive);
	    Enumeration<? extends ZipEntry> entries = zipFile.entries();

	    while(entries.hasMoreElements()){
	        ZipEntry entry = entries.nextElement();
	        InputStream stream = zipFile.getInputStream(entry);
	        String file = IOUtils.toString(stream, StandardCharsets.UTF_8);
	        
	        if(file.contains("<html>")) {
		        Document doc = Jsoup.parse(file);
				String userid = doc.title();
	    		User user = new User(userid);
	    		try {
	    			db.insertUser(user);
				} catch (Exception e) {}
	        }
	    }
	}
	
	public static void loadPageData() throws IOException {

		System.out.println("Loading pages");
		
	    ZipFile zipFile = new ZipFile(pageArchive);
	    Enumeration<? extends ZipEntry> entries = zipFile.entries();

	    while(entries.hasMoreElements()){
	        ZipEntry entry = entries.nextElement();
	        InputStream stream = zipFile.getInputStream(entry);
	        String file = IOUtils.toString(stream, StandardCharsets.UTF_8);
	        
	        if(file.contains("<html>")) {
		        Document doc = Jsoup.parse(file);
		        
				String pageid = doc.title();
				Page page = new Page(pageid);
				
				try {
					String genre = db.getPageCategory(pageid);
					page.setCategory(genre);
					db.insertPage(page);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
	        }
	    }

	}
	
	public static void loadReviews() throws IOException {

		System.out.println("Loading reviews");
		if(sentimentValues == null)
			loadSentimentValues();
		
		ZipFile zipFile = new ZipFile(reviewArchive);
	    Enumeration<? extends ZipEntry> entries = zipFile.entries();

	    while(entries.hasMoreElements()){
	    	ZipEntry entry = entries.nextElement();
	        InputStream stream = zipFile.getInputStream(entry);
	        
	        String file = IOUtils.toString(stream, StandardCharsets.UTF_8).trim();
	        
	        if(file.contains("<html>")) {
		        Review review = loadReview(file);
				String genre = CorpusParser.getDominantCategory(review);
				review.setCategory(genre);
				
				Map<String, Integer> sentimentScores = sentimentValues.get(review.getId());
				if(sentimentScores == null)
					sentimentScores = SentimentAnalyzer.getDefaultValues();
				
				review.setSentimentScores(sentimentScores);
				try {
					if(review!=null)
						db.insertReview(review);
				} catch (Exception e) {}
	        }

	    }
		
		sentimentValues.clear();
	}
	
	
	public static Review loadReview(String file) throws IOException {
		Review review = null;

		Document doc = Jsoup.parse(file);
		String pageid = doc.title();
		String userid = doc.select("meta[name=userId]").get(0).attr("content");
		String content = doc.body().text();
		float score = Float.valueOf(doc.select("meta[name=score]").get(0).attr("content"));
		review = new Review(pageid, userid, content, score);

		return review;
	}

	public static void loadSentimentValues() throws IOException {
		if(sentimentValues == null) {
			System.out.println("Loading sentiment values from csv.");
			sentimentValues = new HashMap<String, Map<String,Integer>>();
			try {
				loadSentimentFile("./WebContent/resources/sentiment-reviews.csv");
			} catch (Exception e) {
				loadSentimentFile(resourceDir + "/" + "sentiment-reviews-individual.csv");
				loadSentimentFile(resourceDir + "/" + "sentiment-reviews-individual2.csv");
			}
		} else {
			System.out.println("Sentiment values already loaded.");
		}
	}
	
	private static void loadSentimentFile(String file) throws IOException{
	
		System.out.println("Attempting to load "+ file);
		//file = resourceDir + "/" + file;
		BufferedReader br = new BufferedReader(new FileReader(file));
	    String line;
	    
	    int i = 0;
		while ((line = br.readLine()) != null) {
			if(i>0) {
				Map<String, Integer> map = new HashMap<String, Integer>();
				
			    String[] values = line.split(COMMA_DELIMITER);
			    String id = values[0].replace(".html", "");
			    
			    map.put("Very positive", Integer.valueOf(values[1]));
			    map.put("Positive", Integer.valueOf(values[2]));
			    map.put("Neutral", Integer.valueOf(values[3]));
			    map.put("Negative", Integer.valueOf(values[4]));
			    map.put("Very negative", Integer.valueOf(values[5]));
			    
			    sentimentValues.put(id, map);
			}
			i++;
		}

	}

	public static void fetchFromSikaman() throws Exception {
		if(!resourceExists()) {
			long start = System.currentTimeMillis();
			System.out.println("Begin fetching data");
			
			String url = "https://sikaman.dyndns.org:8443/WebSite/rest/site/courses/4601/assignments/archive";
			Document doc = Jsoup.connect(url).get();
			Elements links = doc.select("a[href]");
			for (Element element : links) {
				String link = element.attr("abs:href");

				Utils.downloadFile(link);
			}
			Utils.printTimeElapsed(start);
		}
	}
	
	/*
	public static void fetchData() throws Exception {
		if(!resourceExists()) {
			long start = System.currentTimeMillis();
			System.out.println("Begin fetching data");
			
			String url = "https://ws8.xsys.tech/archive/resourcess.zip";
			
			Utils.downloadFile(url);
			
			Utils.printTimeElapsed(start);
		}
	}*/
	
	private static boolean resourceExists(){

	    if(Files.exists(Paths.get(userArchive)) && Files.exists(Paths.get(pageArchive)) && Files.exists(Paths.get(reviewArchive)) ) {
	    	System.out.println("Resources already exist");
	    	return true;
	    }

	    System.out.println("Resources do not exist");
		return false;
	}
	
	public static void load() throws IOException {
		
		if(!db.dataExists()) {
			try {
				fetchFromSikaman();	
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				long start = System.currentTimeMillis();
				System.out.println("Begin loading data");

				loadSentimentValues();
				loadReviews();
				loadUserData();
				loadPageData();

				Utils.printTimeElapsed(start);
			}

		} else {
			System.out.println("Data already exists in db");
		}
		
	}
	
	public static void main(String[] args){
		try {
			load();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
