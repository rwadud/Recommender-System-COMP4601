package edu.carleton.comp4601.model;

import java.io.Serializable;

public class Review implements Serializable, Cloneable{

	private static final long serialVersionUID = 4L;
	private String pageid;
	private String userid;
	private String content;
	private Float score;
	private Float sentimentScore = null;
	
	public Review(String id) {
		pageid = id;
	}
	
	public Review(String id, String userid, String content, float score) {
		this.pageid = id;
		this.userid = userid;
		this.content = content;
		this.score = score;
	}
	
	public void setContent(String text) {
		this.content = text;
	}
	
	public String getContent() {
		return content;
	}
	
	public float getScore() {
		return score;
	}
	
	public float getSentimentScore() {
		return sentimentScore;
	}
	
	public void setSentimentScore(float s) {
		sentimentScore = s;
	}
	
	public String getPageId() {
		return pageid;
	}
	
	public String getUserId() {
		return userid;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return userid+"-"+pageid;
	}
}
