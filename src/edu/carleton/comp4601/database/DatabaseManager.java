package edu.carleton.comp4601.database;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.bson.json.JsonMode;
import org.bson.json.JsonWriterSettings;
import org.bson.types.Binary;
import org.bson.types.ObjectId;

import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;

import edu.carleton.comp4601.model.Page;
import edu.carleton.comp4601.model.Review;
import edu.carleton.comp4601.model.User;

public class DatabaseManager {
	private static String dbName = "rs";
	private static DatabaseManager instance = null;
	
	private MongoClient mongoClient;
	private MongoDatabase db;
	private MongoCollection<Document> pages;
	private MongoCollection<Document> users;
	private MongoCollection<Document> reviews;
	
	
	protected Gson gson = new Gson();
	
	
	private DatabaseManager(String dbName) {
		mongoClient = new MongoClient("localhost", 27017);
		db = mongoClient.getDatabase(dbName);
		pages = db.getCollection("pages");
		users = db.getCollection("users");
		reviews = db.getCollection("reviews");
		
		createUniqueIndex();
	}
	
	public void reset() {
		db.drop();
	}
	
	
	public static DatabaseManager getInstance() {
		if(instance == null){
			instance = new DatabaseManager(dbName);
		}
		return instance;	
	}
	
	protected void createUniqueIndex() {
		
		Document index1 = new Document("userid", 1);
		Document index2 = new Document("pageid", 1);

		Document index3 = new Document("userid", 1);
		index3.append("pageid", 1);
		
		users.createIndex(index1, new IndexOptions().unique(true));
		pages.createIndex(index2, new IndexOptions().unique(true));
		reviews.createIndex(index3, new IndexOptions().unique(true));
		
	}

	public synchronized void insertPage(Page p) {
		pages.insertOne(Document.parse(gson.toJson(p)));
	}
		
	public synchronized void insertUser(User u) {
		users.insertOne(Document.parse(gson.toJson(u)));
	}
	
	public synchronized void insertReview(Review r) {
		reviews.insertOne(Document.parse(gson.toJson(r)));
	}
	
	public synchronized List<User> getUsers(){
		FindIterable<Document> result = users.find();
		List<User> list = new ArrayList<>();
		
		JsonWriterSettings relaxed = JsonWriterSettings.builder().outputMode(JsonMode.RELAXED).build();
		for (Document doc : result) {
			list.add(gson.fromJson(doc.toJson(relaxed), User.class));
		}   
		return list;
	}
	
	public synchronized List<Page> getPages(){
		FindIterable<Document> result = pages.find();
		List<Page> list = new ArrayList<>();
		
		JsonWriterSettings relaxed = JsonWriterSettings.builder().outputMode(JsonMode.RELAXED).build();
		for (Document doc : result) {
			list.add(gson.fromJson(doc.toJson(relaxed), Page.class));
		}   
		return list;
	}
	
	public synchronized User getUserById(String userid) throws Exception{
		BasicDBObject query = new BasicDBObject("userid", userid);
		Document doc = users.find(query).first();
		if(doc!=null) {
			JsonWriterSettings relaxed = JsonWriterSettings.builder().outputMode(JsonMode.RELAXED).build();
			return gson.fromJson(doc.toJson(relaxed), User.class);
		} else {
			throw new Exception("User not found");
		}
	}
	
	public synchronized Page getPageById(String pageid) throws Exception{
		BasicDBObject query = new BasicDBObject("pageid", pageid);
		Document doc = pages.find(query).first();
		if(doc!=null) {
			JsonWriterSettings relaxed = JsonWriterSettings.builder().outputMode(JsonMode.RELAXED).build();
			return gson.fromJson(doc.toJson(relaxed), Page.class);
		} else {
			throw new Exception("Page not found");
		}
	}
	
	public synchronized List<Review> getReviews() throws Exception{
		FindIterable<Document> result = reviews.find();
		List<Review> list = new ArrayList<>();
		JsonWriterSettings relaxed = JsonWriterSettings.builder().outputMode(JsonMode.RELAXED).build();
		for (Document doc : result) {
			list.add(gson.fromJson(doc.toJson(relaxed), Review.class));
		}
		return list;
	}
	
	public synchronized Review getReview(String userid, String pageid) throws Exception{
		BasicDBObject query = new BasicDBObject("userid", userid);
		query.append("pageid", pageid);
		Document doc = reviews.find(query).first();
		if(doc!=null) {
			JsonWriterSettings relaxed = JsonWriterSettings.builder().outputMode(JsonMode.RELAXED).build();
			return gson.fromJson(doc.toJson(relaxed), Review.class);
		} else {
			throw new Exception("Page not found");
		}
	}
	
	public synchronized List<Review> getUserReviews(String userid) throws Exception{
		BasicDBObject query = new BasicDBObject("userid", userid);
		
		FindIterable<Document> result = reviews.find(query);
		
		List<Review> list = new ArrayList<>();
		
		JsonWriterSettings relaxed = JsonWriterSettings.builder().outputMode(JsonMode.RELAXED).build();
		for (Document doc : result) {
			list.add(gson.fromJson(doc.toJson(relaxed), Review.class));
		}
		
		return list;
	}
	
	public synchronized List<Review> getReviewsForMovie(String pageid) throws Exception{
		BasicDBObject query = new BasicDBObject("pageid", pageid);
		
		FindIterable<Document> result = reviews.find(query);
		
		List<Review> list = new ArrayList<>();
		
		JsonWriterSettings relaxed = JsonWriterSettings.builder().outputMode(JsonMode.RELAXED).build();
		for (Document doc : result) {
			list.add(gson.fromJson(doc.toJson(relaxed), Review.class));
		}
		
		return list;
	}
	
	public void updateReview(Review review) {
		Document query = new Document("userid", review.getUserId());
		query.append("pageid", review.getUserId());
		users.replaceOne(query, Document.parse(gson.toJson(review)));
	}
	
}
