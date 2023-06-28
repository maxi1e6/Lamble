package ca.utoronto.utm.mcs;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import static com.mongodb.client.model.Filters.eq;

public class MongoDao {
	
	public MongoCollection<Document> collection;



	public MongoDao() {
        // TODO: 
        // Connect to the mongodb database and create the database and collection. 
        // Use Dotenv like in the DAOs of the other microservices.
		com.mongodb.client.MongoClient mongoClient = MongoClients.create("mongodb://root:123456@mongodb:27017");
	//	MongoClient mongoClient = MongoClient.create("mongodb://root:123456@mongodb:27017");
		MongoDatabase database = mongoClient.getDatabase("a2db");
		this.collection = database.getCollection("a2db");
	}

	// *** implement database operations here *** //
	public JSONObject addTrip(String driver, String passenger, int startTime) throws JSONException {
		Document tripinfo = new Document();
		tripinfo.put("driver", driver);
		tripinfo.put("passenger", passenger);
		tripinfo.put("startTime", startTime);
		collection.insertOne(tripinfo);
		ObjectId objectId = tripinfo.getObjectId("_id");
		JSONObject json = new JSONObject();
		json.put("_id", objectId.toString());
		JSONObject data = new JSONObject();
		data.put("data", json);
		return data;
	}

	public void updateAttributes(String _id, Integer distance, String totalCost, Integer endTime, Integer timeElapsed) {
		Document changes = new Document();
		if (distance != null) {
			changes.put("distance", distance);
		}
		if (totalCost != null) {
			changes.put("totalCost", totalCost);
		}
		if ((endTime != null)) {
			changes.put("endTime", endTime);
		}
		if (timeElapsed != null) {
			changes.put("timeElapsed", timeElapsed);
		}
		collection.updateOne(eq("_id", new ObjectId(_id)), new Document("$set", changes));
		return;
	}

	public Document getTrip(String _id) throws JSONException {
		Document tripinfo = collection.find(eq("_id", new ObjectId(_id))).first();
		return tripinfo;
	}

	public ArrayList getpassengerinfo(String pid) throws JSONException {
		FindIterable<Document> trip = collection.find(eq("passenger", pid));
		ArrayList arr = new ArrayList();
		for(Document doc : trip){
			JSONObject j1 = new JSONObject(doc.toJson());
			arr.add(j1);
		}
		return arr;
	}
	public ArrayList getdriverinfo(String did) throws JSONException {
		FindIterable<Document> trip = collection.find(eq("driver", did));
		ArrayList arr = new ArrayList();
		for(Document doc : trip){
			JSONObject j1 = new JSONObject(doc.toJson());
			j1.remove("driver");
			j1.remove("passenger");
			arr.add(j1);
		}
		return arr;
	}

	public ArrayList getpassengerid(String did) throws JSONException {
		FindIterable<Document> trip = collection.find(eq("driver", did));
		ArrayList arr = new ArrayList();
		for(Document doc : trip){
			arr.add(doc.get("passenger"));
		}
		return arr;
	}
}
