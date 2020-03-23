package edu.carleton.comp4601.model;

import java.util.ArrayList;
import java.util.List;

public class Community {
	
	private String name;
	private List<User> members;
	
	public Community(String s) {
		name = s;
		members = new ArrayList<User>();
	}
	
	public String getCommunityName() {
		return name;
	}
	
	public void addMember(User u) {
		members.add(u);
	}
	
	public void setMembers(List<User> users) {
		members = users;
	}
	
	public List<User> getMembers(){
		return members;
	}
	
}
