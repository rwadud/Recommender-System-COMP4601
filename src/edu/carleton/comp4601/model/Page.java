package edu.carleton.comp4601.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.carleton.comp4601.database.DatabaseManager;

public class Page implements Serializable, Cloneable{
	private static final long serialVersionUID = 4L;
	private String pageid;
	private String genre;
	public Page(String id) {
		this.pageid = id;
	}
	
	public String getPageId() {
		return pageid;
	}
	
	public String getCategory() {
		return genre;
	}
	
	public void setCategory(String s) {
		genre = s;
	}
	
	public List<Review> getReviews() throws Exception {
		return DatabaseManager.getInstance().getReviewsForMovie(pageid);
	}
	
	public String toString() {
		return (pageid+" "+genre).trim();
	}
}
