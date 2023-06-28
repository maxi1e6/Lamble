package ca.utoronto.utm.mcs;

import org.neo4j.driver.*;
import io.github.cdimascio.dotenv.Dotenv;

import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import org.json.JSONException;
import org.neo4j.driver.Result;
import java.util.List;
import org.neo4j.driver.Record;
import org.neo4j.driver.util.Pair;
import org.neo4j.driver.Value;
import org.json.JSONObject;
import org.neo4j.driver.*;
import java.util.ArrayList;

public class Neo4jDAO {

	private final Session session;
	private final Driver driver;
	private final String username = "neo4j";
	private final String password = "123456";

	public Neo4jDAO() {
		Dotenv dotenv = Dotenv.load();
		String addr = dotenv.get("NEO4J_ADDR");
		String uriDb = "bolt://" + addr + ":7687";

		this.driver = GraphDatabase.driver(uriDb, AuthTokens.basic(this.username, this.password));
		this.session = this.driver.session();
	}

	// *** implement database operations here *** //

	public Result addUser(String uid, boolean is_driver) {
		String query = "CREATE (n: user {uid: '%s', is_driver: %b, longitude: 0, latitude: 0, street: ''}) RETURN n";
		query = String.format(query, uid, is_driver);
		return this.session.run(query);
	}

	public Result deleteUser(String uid) {
		String query = "MATCH (n: user {uid: '%s' }) DETACH DELETE n RETURN n";
		query = String.format(query, uid);
		return this.session.run(query);
	}

	public Result getUserLocationByUid(String uid) {
		String query = "MATCH (n: user {uid: '%s' }) RETURN n.longitude, n.latitude, n.street";
		query = String.format(query, uid);
		return this.session.run(query);
	}

	public Result getUserByUid(String uid) {
		String query = "MATCH (n: user {uid: '%s' }) RETURN n";
		query = String.format(query, uid);
		return this.session.run(query);
	}

	public Result updateUserIsDriver(String uid, boolean isDriver) {
		String query = "MATCH (n:user {uid: '%s'}) SET n.is_driver = %b RETURN n";
		query = String.format(query, uid, isDriver);
		return this.session.run(query);
	}

	public Result updateUserLocation(String uid, double longitude, double latitude, String street) {
		String query = "MATCH(n: user {uid: '%s'}) SET n.longitude = %f, n.latitude = %f, n.street = \"%s\" RETURN n";
		query = String.format(query, uid, longitude, latitude, street);
		return this.session.run(query);
	}

	public Result getRoad(String roadName) {
		String query = "MATCH (n :road) where n.name='%s' RETURN n";
		query = String.format(query, roadName);
		return this.session.run(query);
	}

	public Result createRoad(String roadName, boolean has_traffic) {
		String query = "CREATE (n: road {name: '%s', has_traffic: %b}) RETURN n";
		query = String.format(query, roadName, has_traffic);
		return this.session.run(query);
	}

	public Result updateRoad(String roadName, boolean has_traffic) {
		String query = "MATCH (n:road {name: '%s'}) SET n.has_traffic = %b RETURN n";
		query = String.format(query, roadName, has_traffic);
		return this.session.run(query);
	}

	public Result createRoute(String roadname1, String roadname2, int travel_time, boolean has_traffic) {
		String query = "MATCH (r1:road {name: '%s'}), (r2:road {name: '%s'}) CREATE (r1) -[r:ROUTE_TO {travel_time: %d, has_traffic: %b}]->(r2) RETURN type(r)";
		query = String.format(query, roadname1, roadname2, travel_time, has_traffic);
		return this.session.run(query);
	}

	public Result deleteRoute(String roadname1, String roadname2) {
		String query = "MATCH (r1:road {name: '%s'})-[r:ROUTE_TO]->(r2:road {name: '%s'}) DELETE r RETURN COUNT(r) AS numDeletedRoutes";
		query = String.format(query, roadname1, roadname2);
		return this.session.run(query);
	}

/*
	public void getDistances(Nearby n, HttpExchange r, String uid, String radius) throws IOException, JSONException {
		try (Session session = this.driver.session()) {
			try (Transaction tx = session.beginTransaction()){
				String query = "MATCH (u1:user {uid: '%s'}), (u2:user) WHERE 2*6378137*asin(sqrt((sin((radians(u2.latitude)-radians(u1.latitude))/2))^2+cos(radians(u1.latitude))*cos(radians(u2.latitude))*(sin((radians(u2.longitude)-radians(u1.longitude))/2))^2)) <= %s RETURN u2";
				query = String.format(query, uid, radius);
				Result result = tx.run(query);
				// if (!result.hasNext()) {
				// 	this.sendStatus(r, 500);
				// 	return;
				// }

				List<Record> recordList = result.list();

				// List<Record> recordList = new ArrayList<Record>();
				// Record rc;
				// while (result.hasNext()) {
				// 	rc = result.next();
				// 	recordList.add(rc);
				// }

				System.out.println(recordList); // TODO: rm
				Record record;
				List<Pair<String,Value>> recordFields;
				Pair<String,Value> p;
				Value v;
				JSONObject res = new JSONObject();
				res.put("status", "OK");

				JSONObject driver = new JSONObject();
				for (int i = 0; i < recordList.size(); i++) {
					record = recordList.get(i);
					recordFields = record.fields();
					for (int j = 0; j < recordFields.size(); j++) {
						p = recordFields.get(j);
						if ("u2".equals(p.key())) {
							v = p.value();

							double longitude = v.get("longitude").asDouble();
							System.out.println(String.valueOf(longitude)); // TODO: rm
							driver.put("longitude", longitude);
							double latitude = v.get("latitude").asDouble();
							System.out.println(String.valueOf(latitude)); // TODO: rm
							driver.put("latitude", latitude);
							String street = v.get("street").asString();
							System.out.println(street); // TODO: rm
							driver.put("street", street);
							String driverID = v.get("uid").asString();
							System.out.println(driverID); // TODO: rm
							driver.put(driverID, driver);
							break;
						}
					}
				}
				res.put("data", driver);
				System.out.println(res.toString()); // TODO: rm
				tx.commit();
				n.sendResponse(r, res, 200);
				return;
			} catch (Exception e1) {
				e1.printStackTrace();
				n.sendStatus(r, 500);
				return;
			}
		} catch (Exception e2){
			e2.printStackTrace();
			n.sendStatus(r, 500);
			return;
		}
	}
*/

	public Result getDistances(String uid, int radius) {
		String query = "MATCH (u1:user {uid: \"%s\"}), (u2:user) WHERE point.distance(point({x: u1.latitude, y: u1.longitude, crs: 'cartesian'}), point({x: u2.latitude, y: u2.longitude, crs: 'cartesian'})) < %d RETURN u2.uid AS uid, u2.latitude AS latitude, u2.longitude AS longitude, u2.street AS street";
		query = String.format(query, uid, radius);
		System.out.println(query); // TODO: rm
		return this.session.run(query);
	}


	public Result getPath(String driverUid, String passengerUid) {
		String query = "MATCH (u1:user {uid: '%s'}), (r1:road), (u2:user {uid: '%s'}), (r2:road), p = shortestPath((r1)-[:ROUTE_TO*]-(r2)) WHERE u1.street = r1.name AND u2.street = r2.name RETURN nodes(p)";
		query = String.format(query, driverUid, passengerUid);

		System.out.println(query); // TODO: rm

		return this.session.run(query);
	}
}
