package edu.carleton.comp4601.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.carleton.comp4601.database.DatabaseManager;

public class User implements Serializable, Cloneable{
	private static final long serialVersionUID = 4L;
	private String userid;
	private Integer cluster;
	
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
	
	public List<Review> getReview() throws Exception{
		return DatabaseManager.getInstance().getUserReviews(userid);
	}
}
