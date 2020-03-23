package edu.carleton.comp4601.resources;

import java.io.IOException;

import edu.carleton.comp4601.database.DatabaseManager;
import edu.carleton.comp4601.utility.DataLoader;

public class Main {

	public static void main(String[] args) {

		try {
			DatabaseManager.getInstance().reset();
			DataLoader.loadUserData();
			DataLoader.loadPageData();
			DataLoader.loadReviews();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
