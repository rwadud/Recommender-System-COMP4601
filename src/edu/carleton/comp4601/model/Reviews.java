package edu.carleton.comp4601.model;

import java.util.HashMap;

public class Reviews {

	private static HashMap<String,Review> map = new HashMap<String, Review>();
	
	public static void add(String id, Review r) {
		map.put(id, r);
	}
	
	public static Review get(String id) {
		return map.get(id);
	}
	
	public static void update(String id, Review r) {
		add(id, r);
	}
}
