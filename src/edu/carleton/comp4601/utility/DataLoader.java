package edu.carleton.comp4601.utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.apache.commons.io.FilenameUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.common.io.Files;

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
	private static final String userDir = resourceDir + "/users";
	private static final String reviewDir = resourceDir + "/reviews";
	private static final String pageDir = resourceDir + "/pages";
	private static Map<String, Map<String, Integer>> sentimentValues = null;
	private static final String COMMA_DELIMITER = ",";
	private static DatabaseManager db = DatabaseManager.getInstance();
	private static boolean fetchedFromSikaman = false;
	
	public static void loadUserData() throws IOException {
		System.out.println("Loading users");
		
		File dir = new File(userDir);
		File[] dirList = dir.listFiles();
		if (dirList != null) {
			for (File file : dirList) {
				if (file.getName().contains(".html")) {

					Document doc = Jsoup.parse(file, "UTF-8", "");
					
					String userid = doc.title();

		    		User user = new User(userid);
					
		    		db.insertUser(user);

				}
			}
		}
		
		//return map;
	}

	public static void loadPageData() throws IOException {

		System.out.println("Loading pages");
		File dir = new File(pageDir);
		File[] dirList = dir.listFiles();
		if (dirList != null) {
			for (File file : dirList) {
				if (file.getName().contains(".html")) {
					Document doc = Jsoup.parse(file, "UTF-8", "");
					
					String pageid = doc.title();
					Page page = new Page(pageid);
					
					try {
						String genre = db.getPageCategory(pageid);
						page.setCategory(genre);
						db.insertPage(page);
						
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}
		}
		//return map;
	}
	
	public static void loadReviews() throws IOException {

		System.out.println("Loading reviews");
		if(sentimentValues == null)
			loadSentimentValues();
		
		File dir = new File(reviewDir);
		File[] dirList = dir.listFiles();
		if (dirList != null) {
			for (File file : dirList) {
				if (file.getName().contains(".html")) {
					Document doc = Jsoup.parse(file, "UTF-8", "");
					String reviewid = file.getName().replace(".html", "");
					
					Review review = loadReview(file);
					
					String genre = CorpusParser.getDominantCategory(review);
					
					review.setCategory(genre);
					
					Map<String, Integer> sentimentScores = sentimentValues.get(review.getId());
					
					if(sentimentScores == null)
						sentimentScores = SentimentAnalyzer.getDefaultValues();
					
					review.setSentimentScores(sentimentScores);
					
					if(review!=null)
						db.insertReview(review);

				}
			}
		}
		
		sentimentValues.clear();
	}
	
	public static void loadSentimentValues() {
		if(sentimentValues == null) {
			System.out.println("Loading sentiment values from csv.");
			sentimentValues = new HashMap<String, Map<String,Integer>>();
			try {
				
				if(fetchedFromSikaman) {
					loadSentimentFile("sentiment-reviews-individual.csv");
					loadSentimentFile("sentiment-reviews-individual2.csv");
				} else {
					loadSentimentFile("sentiment-reviews.csv");
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("Sentiment values already loaded.");
		}
		
	}
	
	private static void loadSentimentFile(String file) throws Exception{
	
		file = resourceDir + "/" + file;
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
	
	public static Review loadReview(File file) throws IOException {
		Review review = null;

		if (file.getName().contains(".html")) {
			Document doc = Jsoup.parse(file, "UTF-8", "");
			String pageid = doc.title();
			String userid = doc.select("meta[name=userId]").get(0).attr("content");
			String content = doc.body().text();
			float score = Float.valueOf(doc.select("meta[name=score]").get(0).attr("content"));
			review = new Review(pageid, userid, content, score);
		}
		
		return review;
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
			fetchedFromSikaman = true;
			Utils.printTimeElapsed(start);
		}
	}
	
	public static void fetchData() throws Exception {
		if(!resourceExists()) {
			long start = System.currentTimeMillis();
			System.out.println("Begin fetching data");
			
			String url = "https://ws8.xsys.tech/archive/resources.zip";
			Utils.downloadFile(url);
			
			Utils.printTimeElapsed(start);
		}
	}
	
	private static boolean resourceExists(){
		
		try {
			Stream<Path> reviews = java.nio.file.Files.list(Paths.get(reviewDir));
			Stream<Path> users = java.nio.file.Files.list(Paths.get(userDir));
			Stream<Path> pages = java.nio.file.Files.list(Paths.get(pageDir));
			
		    if(pages.count() == 1079 && users.count() == 1252 && reviews.count() == 82201) {
		    	System.out.println("Resources already exist");
		    	return true;
		    }
		    
		} catch (IOException e) {

		}

	    System.out.println("Resources do not exist");
		return false;
	}
	
	public static void load() throws IOException {
		
		if(!db.dataExists()) {
			try {
				fetchData();
			} catch (Exception e) {
				try {
					fetchFromSikaman();	
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
			long start = System.currentTimeMillis();
			System.out.println("Begin loading data");

			loadSentimentValues();
			loadReviews();
			loadUserData();
			loadPageData();

			Utils.printTimeElapsed(start);
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
