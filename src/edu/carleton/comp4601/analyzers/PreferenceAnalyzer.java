package edu.carleton.comp4601.analyzers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import edu.carleton.comp4601.database.DatabaseManager;
import edu.carleton.comp4601.model.Page;
import edu.carleton.comp4601.model.Review;
import edu.carleton.comp4601.model.Sentiment;
import edu.carleton.comp4601.model.User;
import edu.carleton.comp4601.utility.Utils;

public class PreferenceAnalyzer {
	
	private static Map<String, String> preferredGenreMap = new HashMap<String, String>();
	
	private PreferenceAnalyzer() {}
	
	public static void analyze(List<User> users) {
		
		long start = System.currentTimeMillis();
		
		DatabaseManager db = DatabaseManager.getInstance();
		Map<String, String> genreMap = new HashMap<String, String>();
		List<Page> pages = db.getPages();
		
		for (Page page : pages) {
			genreMap.put(page.getPageId(), page.getCategory());
		}
			
		for (User user : users) {
			Map<String, Integer> genrePreferences  = Maps.newHashMap(ImmutableMap.of("action", 0, "comedy", 0, "horror", 0));
			try {
				List<Review> reviews = user.getReviews();
				
				for (Review review : reviews) {
					String movieGenre = genreMap.get(review.getPageId());

					if(review.getSentiment() == Sentiment.POSITIVE) {
						genrePreferences.put(movieGenre, genrePreferences.get(movieGenre)+1);
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
			//genrePreferences.values().remove(0);
			user.setGenrePreferences(genrePreferences);
			System.out.println(user.getUserId() + " prefers " + user.getPreferredGenre());
			preferredGenreMap.put(user.getUserId(), user.getPreferredGenre());
			db.updateUser(user);

		}
		
		Utils.printTimeElapsed(start);
		
	}
	
	/*
	 * Returns a map of userid and genre
	 */
	public Map<String, String> getPreferredGenreMap(){
		return preferredGenreMap;
	}
}
