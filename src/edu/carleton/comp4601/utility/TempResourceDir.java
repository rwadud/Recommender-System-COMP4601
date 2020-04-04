package edu.carleton.comp4601.utility;

import edu.carleton.comp4601.database.DatabaseManager;

public class TempResourceDir {
	public static void main(String[] args) {
		System.out.println(DatabaseManager.getInstance().getTempDir());
	}
}
