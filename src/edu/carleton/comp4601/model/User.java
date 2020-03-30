package edu.carleton.comp4601.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import edu.carleton.comp4601.database.DatabaseManager;
import edu.carleton.comp4601.utility.Utils;

public class User implements Serializable, Cloneable{
	private static final long serialVersionUID = 4L;
	private String userid;
	private Integer cluster;
	private Map<String, Integer> genrePreferences; 
	
	public User(String id) {
		this.userid = id;
	}
	
	public Integer getCluster() {
		return cluster;
	}
	
	public String getUserId() {
		return userid;
	}

	public void setCluster(Integer cluster) {
		this.cluster = cluster;
	}
	
	public void setGenrePreferences(Map<String, Integer> m) {
		genrePreferences = m;
	}
	
	public Map<String, Integer> getGenrePreferences() {
		return genrePreferences;
	}
	
	public Double[] getFeatures() {
		Double[] features = new Double[genrePreferences.size()];

		int i = 0;
		for (Integer value : genrePreferences.values()) {
			features[i] = value.doubleValue();
			i++;
		}
		
		return features;
	}
	
	public String getPreferredGenre() {
		return Utils.maxEntryInMap(genrePreferences).getKey();
	}
	
	public List<Review> getReviews() throws Exception{
		return DatabaseManager.getInstance().getUserReviews(userid);
	}
}
