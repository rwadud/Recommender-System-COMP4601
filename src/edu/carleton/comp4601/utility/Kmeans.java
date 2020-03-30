package edu.carleton.comp4601.utility;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

import edu.carleton.comp4601.database.DatabaseManager;
import edu.carleton.comp4601.model.User;


public class Kmeans {

	private int no_users;
	private KUser[] users;
	private int no_features;
	private boolean changed;
	private int no_clusters;
	private KUser[] centroids;

	/*
	 * Constructor that reads the data in from a file. You must specify the number
	 * of clusters.
	 */
	public Kmeans(int noClusters, List<User> ulist) throws FileNotFoundException {
		changed = true;

		no_users = ulist.size();
		no_features = 3;
		users = new KUser[no_users];
		this.no_clusters = noClusters;
		centroids = new KUser[noClusters];

		for (int i = 0; i < no_users; i++) {
			User user = ulist.get(i);
			String name = user.getUserId();
			users[i] = new KUser(name, no_features, noClusters);
			users[i].features = user.getFeatures();
		}

		System.out.println("genrating");
		generate_centroids();
	}

	//generate random centroids
	public void generate_centroids() {

		for (int i = 0; i < centroids.length; i++) {
			Random ran = new Random(System.nanoTime()); 
			int randomNum = ran.nextInt(users.length); 
			KUser u = users[i];
			centroids[i] = (KUser) u.clone();
			System.out.println(centroids[i]);
		}
	}

	public static Double arraySumSquared(double[] arr) {
		double sum = 0.0;
		for (int i = 0; i < arr.length; i++) {
			sum+=Math.pow(arr[i], 2);
		}
		return sum;
	}
	
	public static Double sse(KUser[] users) {
	    double sum = 0;
	    /*
	    for (int i = 0; i < centroids.length; i++) {
	    	for (int j = 0; j < users.length; j++) {
	    		if(i==users[j].cluster) {
	    			sum+=Math.pow(users[j].distance[i], 2);
	    		}
	    	}
		}       
		*/
	    for (int i = 0; i < users.length; i++) {
			for (int j = 0; j < users[i].distance.length; j++) {
				if(users[i].cluster==j) {
					sum+=Math.pow(users[i].distance[j], 2);
				}
			}
		}
	    return sum;
	}
	
	/*
	 * This is where your implementation goes
	 */
	private KUser[] algorithm() {
		int iteration = 1;
		while (changed) {
			System.out.println("Iteration "+ iteration++);
			// Your code here
			int changes = 0;
			
			//assign users to a cluster
			for (int i = 0; i < users.length; i++) {
				for (int j = 0; j < centroids.length; j++) {
					users[i].distance[j] = distance(users[i], centroids[j]);
				}
				users[i].cluster = minIndex(users[i].distance);
			}

			//look for changes to user clusters
			for (int i = 0; i < users.length; i++) {
				if(users[i].changed()) {
					//System.out.println("User "+ users[i].name +" Old cluster "+users[i].last_cluster + " New Cluster "+users[i].cluster);
					users[i].update();
					changes++;
				}
			}
			
			if(changes == 0)
				break;
			
			// relocate centroids
			for (int i = 0; i < centroids.length; i++) {
				System.out.println(centroids[i]);
				Double[] avg_features = {0.0,0.0,0.0};
				for (int j = 0; j < users.length; j++) {
					if(i==users[j].cluster) {
						for (int k = 0; k < users[j].features.length; k++) {
							avg_features[k] += users[j].features[k];
						}
					}
				}
				
				for (int k = 0; k < avg_features.length; k++) {
					avg_features[k] = avg_features[k]/no_users;
				}
				centroids[i].features = avg_features;
			}

		}
		return users;
	}

	//get index of minimum value
	public static int minIndex(Double[] arr) {
		Double min = arr[0];
		int index = 0;
		for (int i = 1; i < arr.length; i++) {
			if (arr[i] < min) {
				min = arr[i];
				index = i;
			}
		}
		return index;
	}

	/*
	 * Computes distance between two users Could implement this on User too.
	 */
	private double distance(KUser a, KUser b) {
		double rtn = 0.0;
		// Assumes a and b have same number of features
		for (int i = 0; i < a.features.length; i++) {
			rtn += (a.features[i] - b.features[i]) * (a.features[i] - b.features[i]);
		}
		return Math.sqrt(rtn);
	}

	public static Map<String, Integer> getUserClusters(List<User> users){
		Map<String, Integer> userClusterMap = new HashMap<String, Integer>();
		try {
			int numberOfClusters = 3;
			Kmeans knn = new Kmeans(numberOfClusters, users);
			KUser[] kUsers = knn.algorithm();
			
			for (KUser kUser : kUsers) {
				userClusterMap.put(kUser.name, kUser.cluster);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return userClusterMap;
	}
	
	public static void main(String[] args) {
		try {
			getUserClusters(DatabaseManager.getInstance().getUsers());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Private class for representing user
	private class KUser implements Cloneable {
		public Double[] features;
		public Double[] distance;
		public String name;
		public int cluster;
		public int last_cluster;

		public KUser(String name, int noFeatures, int noClusters) {
			this.name = name;
			this.features = new Double[noFeatures];
			this.distance = new Double[noClusters];
			this.cluster = -1;
			this.last_cluster = -1;
		}

		// Check if cluster association has changed.
		public boolean changed() {
			return last_cluster != cluster;
		}

		// Update the saved cluster from iteration to iteration
		public void update() {
			last_cluster = cluster;
		}
		
		@Override
		public Object clone() {
		    try {
		        return (KUser) super.clone();
		    } catch (CloneNotSupportedException e) {
		    	KUser u = new KUser(this.name, this.features.length, this.distance.length);
		    	u.features = this.features;
		    	u.distance = this.distance;
		    	u.cluster = this.cluster;
		    	u.last_cluster = this.last_cluster;
		        return u;
		    }
		}
		
		public String toString() {
			StringBuffer b = new StringBuffer(name);
			for (int i = 0; i < features.length; i++) {
				b.append(' ');
				b.append(features[i]);
			}
			return b.toString();
		}
	}

}
