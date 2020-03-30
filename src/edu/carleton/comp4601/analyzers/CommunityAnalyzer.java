package edu.carleton.comp4601.analyzers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.carleton.comp4601.database.DatabaseManager;
import edu.carleton.comp4601.model.Community;
import edu.carleton.comp4601.model.Page;
import edu.carleton.comp4601.model.Review;
import edu.carleton.comp4601.model.User;
import edu.carleton.comp4601.utility.DataLoader;
import edu.carleton.comp4601.utility.Kmeans;

public class CommunityAnalyzer {
	
	private static String[] communityNames = {"Community 1", "Community 2", "Community 3"};
	private static List<Community> communities = new ArrayList<Community>();;

	private CommunityAnalyzer() {}
	
	public static void analyze(List<User> users) {
		
		for (int i = 0; i < communityNames.length; i++) {
			communities.add(new Community(communityNames[i]));
		}
		
		Map<String, Integer> clusterMap = Kmeans.getUserClusters(users);
		for(Map.Entry<String, Integer> entry : clusterMap.entrySet()) {
			try {
				Integer cluster = entry.getValue();
				User user = DatabaseManager.getInstance().getUserById(entry.getKey());
				user.setCluster(cluster);

				Community community = communities.get(cluster);
				community.addMember(user);
				System.out.println("Assigning "+ user.getUserId() + " to " + community.getCommunityName());
				
				DatabaseManager.getInstance().updateUser(user);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	public static List<Community> getCommunities(){
		return communities;
	}
}
