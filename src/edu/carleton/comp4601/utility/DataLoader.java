package edu.carleton.comp4601.utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import edu.carleton.comp4601.analyzers.SentimentAnalyzer;
import edu.carleton.comp4601.database.DatabaseManager;
import edu.carleton.comp4601.model.Page;
import edu.carleton.comp4601.model.Review;
import edu.carleton.comp4601.model.Reviews;
import edu.carleton.comp4601.model.Sentiment;
import edu.carleton.comp4601.model.User;
import edu.carleton.comp4601.resources.CorpusParser;
import javafx.scene.chart.PieChart.Data;

public class DataLoader {
	
	private static final String resourceDir = "./resources";
	private static final String userDir = "./resources/users";
	private static final String reviewDir = "./resources/reviews";
	private static final String pageDir = "./resources/pages";
	private static Map<String, Map<String, Integer>> sentimentValues = null;
	private static final String COMMA_DELIMITER = ",";
	
	public static void loadUserData() throws IOException {
		//Map<String, User> map = new HashMap<String, User>();
		
		File dir = new File(userDir);
		File[] dirList = dir.listFiles();
		if (dirList != null) {
			for (File file : dirList) {
				if (file.getName().contains(".html")) {

					Document doc = Jsoup.parse(file, "UTF-8", "");
					
					String userid = doc.title();

		    		User user = new User(userid);
					
					DatabaseManager.getInstance().insertUser(user);
					
					/*
					Elements links = doc.select("a[href]");
					for (Element link : links) {
						String pageid = link.text();
						String reviewid = userid+"-"+pageid;
						user.addReview(Reviews.get(reviewid));
					}
					
					map.put(userid, user);
					*/
				}
			}
		}
		
		//return map;
	}

	public static void loadPageData() throws IOException {
		//Map<String, Page> map = new HashMap<String, Page>();
		
		File dir = new File(pageDir);
		File[] dirList = dir.listFiles();
		if (dirList != null) {
			for (File file : dirList) {
				if (file.getName().contains(".html")) {
					Document doc = Jsoup.parse(file, "UTF-8", "");
					
					String pageid = doc.title();
					Page page = new Page(pageid);
					
					try {
						String genre = DatabaseManager.getInstance().getPageCategory(pageid);
						page.setCategory(genre);
						DatabaseManager.getInstance().insertPage(page);
						
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					/*
					Elements links = doc.select("a[href]");
					for (Element link : links) {
						String userid = link.text();
						String reviewid = userid+"-"+pageid;
						page.addReview(Reviews.get(reviewid));
					}
					
					map.put(pageid, page);
					*/
				}
			}
		}
		//return map;
	}
	
	public static void loadReviews() throws IOException {

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
					
					System.out.println(review.toString());
					Map<String, Integer> sentimentScores = sentimentValues.get(review.getId());
					
					if(sentimentScores == null)
						sentimentScores = SentimentAnalyzer.annotateAndScore(review.getContent());
					
					review.setSentimentScores(sentimentScores);
					
					//System.out.println(reviewid + " detected genre for review is "+genre);
					//System.out.println(reviewid);
					if(review!=null)
						DatabaseManager.getInstance().insertReview(review);

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
				loadSentimentFile("sentiment-reviews-individual.csv");
				loadSentimentFile("sentiment-reviews-individual2.csv");
			} catch (Exception e) {
				// TODO Auto-generated catch block
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

	
	public static void main(String[] args) {

		try {
			long start = System.currentTimeMillis();

			DatabaseManager.getInstance().reset();
			DataLoader.loadSentimentValues();
			DataLoader.loadReviews();
			DataLoader.loadUserData();
			DataLoader.loadPageData();

			
			long finish = System.currentTimeMillis();
			long timeElapsed = finish - start;
			
			System.out.println("Took "+ (timeElapsed/1000) + " seconds");
      
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
}
