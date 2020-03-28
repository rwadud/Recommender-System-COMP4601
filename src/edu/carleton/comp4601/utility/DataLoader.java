package edu.carleton.comp4601.utility;

import java.io.File;
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
import edu.carleton.comp4601.model.User;
import edu.carleton.comp4601.resources.CorpusParser;
import javafx.scene.chart.PieChart.Data;

public class DataLoader {
	
	private static final String userDir = "./resources/users";
	private static final String reviewDir = "./resources/reviews";
	private static final String pageDir = "./resources/pages";
	
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
					
					DatabaseManager.getInstance().insertPage(page);
					
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
					
					//SentimentAnalyzer.calculate(review);
					
					//System.out.println(reviewid + " detected genre for review is "+genre);
					//System.out.println(reviewid);
					if(review!=null)
						DatabaseManager.getInstance().insertReview(review);
					
					//Reviews.add(reviewid, loadReview(file));
				}
			}
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
			DataLoader.loadUserData();
			DataLoader.loadPageData();
			DataLoader.loadReviews();
			
			long finish = System.currentTimeMillis();
			long timeElapsed = finish - start;
			
			System.out.println("Took "+ (timeElapsed/1000) + " seconds");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
}
