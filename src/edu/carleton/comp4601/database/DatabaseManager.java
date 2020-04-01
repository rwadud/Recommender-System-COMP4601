package edu.carleton.comp4601.database;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bson.Document;
import org.bson.json.JsonMode;
import org.bson.json.JsonWriterSettings;
import org.bson.types.Binary;
import org.bson.types.ObjectId;

import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;

import edu.carleton.comp4601.model.Page;
import edu.carleton.comp4601.model.Review;
import edu.carleton.comp4601.model.User;
import edu.carleton.comp4601.utility.Utils;

public class DatabaseManager {
	private static String dbName = "rs";
	private static DatabaseManager instance = null;

	private MongoClient mongoClient;
	private MongoDatabase db;
	private MongoCollection<Document> pages;
	private MongoCollection<Document> users;
	private MongoCollection<Document> reviews;
	private MongoCollection<Document> settings;

	protected Gson gson = new Gson();

	private DatabaseManager(String dbName) {
		mongoClient = new MongoClient("localhost", 27017);
		db = mongoClient.getDatabase(dbName);
		pages = db.getCollection("pages");
		users = db.getCollection("users");
		reviews = db.getCollection("reviews");
		settings = db.getCollection("settings");

		createUniqueIndex();
	}

	public void reset() {
		db.drop();
	}

	public static DatabaseManager getInstance() {
		if (instance == null) {
			instance = new DatabaseManager(dbName);
		}
		return instance;
	}

	protected void createUniqueIndex() {

		Document index1 = new Document("userid", 1);
		Document index2 = new Document("pageid", 1);
		Document index3 = new Document("userid", 1);
		Document index4 = new Document("value", 1);
		index3.append("pageid", 1);

		users.createIndex(index1, new IndexOptions().unique(true));
		pages.createIndex(index2, new IndexOptions().unique(true));
		reviews.createIndex(index3, new IndexOptions().unique(true));
		settings.createIndex(index4, new IndexOptions().unique(true));

	}

	public boolean dataExists() {
		if(pages.count() == 1079 && users.count() == 1252 && reviews.count() == 82201)
			return true;
		return false;
	}
	
	public String getTempDir() {
		
		String dir;
		Document doc = settings.find(new Document("option","temp_dir")).first();
		
		if(doc !=null ) {
			dir = doc.getString("value");
		} else {
			dir = Utils.newTempDir();
			settings.insertOne(new Document("option","temp_dir").append("value", dir));
		}
		
		return dir;
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

	public synchronized List<User> getUsers() {
		FindIterable<Document> result = users.find();
		List<User> list = new ArrayList<>();

		JsonWriterSettings relaxed = JsonWriterSettings.builder().outputMode(JsonMode.RELAXED).build();
		for (Document doc : result) {
			list.add(gson.fromJson(doc.toJson(relaxed), User.class));
		}
		return list;
	}

	public synchronized List<Page> getPages() {
		FindIterable<Document> result = pages.find();
		List<Page> list = new ArrayList<>();

		JsonWriterSettings relaxed = JsonWriterSettings.builder().outputMode(JsonMode.RELAXED).build();
		for (Document doc : result) {
			list.add(gson.fromJson(doc.toJson(relaxed), Page.class));
		}
		return list;
	}

	public synchronized User getUserById(String userid) throws Exception {
		BasicDBObject query = new BasicDBObject("userid", userid);
		Document doc = users.find(query).first();
		if (doc != null) {
			JsonWriterSettings relaxed = JsonWriterSettings.builder().outputMode(JsonMode.RELAXED).build();
			return gson.fromJson(doc.toJson(relaxed), User.class);
		} else {
			throw new Exception("User not found");
		}
	}

	public synchronized Page getPageById(String pageid) throws Exception {
		BasicDBObject query = new BasicDBObject("pageid", pageid);
		Document doc = pages.find(query).first();
		if (doc != null) {
			JsonWriterSettings relaxed = JsonWriterSettings.builder().outputMode(JsonMode.RELAXED).build();
			return gson.fromJson(doc.toJson(relaxed), Page.class);
		} else {
			throw new Exception("Page not found");
		}
	}

	public synchronized List<Review> getReviews() throws Exception {
		FindIterable<Document> result = reviews.find();
		List<Review> list = new ArrayList<>();
		JsonWriterSettings relaxed = JsonWriterSettings.builder().outputMode(JsonMode.RELAXED).build();
		for (Document doc : result) {
			list.add(gson.fromJson(doc.toJson(relaxed), Review.class));
		}
		return list;
	}

	public synchronized Review getReview(String userid, String pageid) throws Exception {
		BasicDBObject query = new BasicDBObject("userid", userid);
		query.append("pageid", pageid);
		Document doc = reviews.find(query).first();
		if (doc != null) {
			JsonWriterSettings relaxed = JsonWriterSettings.builder().outputMode(JsonMode.RELAXED).build();
			return gson.fromJson(doc.toJson(relaxed), Review.class);
		} else {
			throw new Exception("Page not found");
		}
	}

	public synchronized List<Review> getUserReviews(String userid) throws Exception {
		BasicDBObject query = new BasicDBObject("userid", userid);

		FindIterable<Document> result = reviews.find(query);

		List<Review> list = new ArrayList<>();

		JsonWriterSettings relaxed = JsonWriterSettings.builder().outputMode(JsonMode.RELAXED).build();
		for (Document doc : result) {
			list.add(gson.fromJson(doc.toJson(relaxed), Review.class));
		}

		return list;
	}

	public synchronized List<Review> getReviewsForMovie(String pageid) throws Exception {
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
	
	public void updatePage(Page page) {
		Document query = new Document("pageid", page.getPageId());
		users.replaceOne(query, Document.parse(gson.toJson(page)));
	}
	
	public void updateUser(User user) {
		Document query = new Document("userid", user.getUserId());
		users.replaceOne(query, Document.parse(gson.toJson(user)));
	}
	
	// returns the the genre the user has the most reviews in
	public String getUserCategory(String userid) throws Exception {
		return getCategory(userid, "userid");
	}
	
	public String getPageCategory(String pageid) throws Exception {
		return getCategory(pageid, "pageid");
	}
	
	private String getCategory(String id, String type) throws Exception {	
		if(!(type.equals("userid") || type.equals("pageid")))
			throw new Exception("Wrong type identfier");
		
		Document matchFields = new Document(type, id);
		matchFields.append("genre", new BasicDBObject("$exists", true));
		Document match = new Document("$match", matchFields);

		Document groupFields = new Document("_id", "$genre");
		groupFields.append("count", new BasicDBObject( "$sum", 1));
		Document group = new Document("$group", groupFields);

		AggregateIterable<Document> output = reviews.aggregate(Arrays.asList(
		        match,
		        group,
		        new Document("$sort", new Document("count", -1)),
		        new Document("$limit", 1))
				);
		
		return output.first().getString("_id");
	}

}
