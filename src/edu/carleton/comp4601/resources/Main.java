package edu.carleton.comp4601.resources;

import java.io.IOException;

import edu.carleton.comp4601.analyzers.SentimentAnalyzer;
import edu.carleton.comp4601.database.DatabaseManager;
import edu.carleton.comp4601.model.Review;
import edu.carleton.comp4601.utility.DataLoader;

public class Main {

	public static void main(String[] args) {
		
		// tests
		try {
			
			System.out.println("B00018D454 movie is mainly " + DatabaseManager.getInstance().getPageCategory("B002KGREJC"));
			
			System.out.println("A100JCBNALJFAW user reviewed mostly " + DatabaseManager.getInstance().getUserCategory("A2RHQMV5GD18Z8") + " movies");
			
			Review review = DatabaseManager.getInstance().getReview("A2RHQMV5GD18Z8", "B002KGREJC");
			
			
			SentimentAnalyzer.calculate(review);
			System.out.println(review.getSentiment().toString());
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
