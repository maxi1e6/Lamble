package ca.utoronto.utm.mcs;
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
public class Nearby extends Endpoint {
	/**
	 * GET /location/nearbyDriver/:uid?radius=:radius
	 * @param uid, radius
	 * @return 200, 400, 404, 500
	 * Get drivers that are within a certain radius around a user.
	 */
	@Override
	public void handleGet(HttpExchange r) throws IOException, JSONException {
		// format: GET /location/nearbyDriver/:uid?radius=:radius
		// check uri P1:
		String[] uriPartsP1 = r.getRequestURI().toString().split("/");
		if (uriPartsP1.length != 4) {
			System.out.println("length issue 26");
			this.sendStatus(r, 400);
			return;
		}

		// System.out.println("uriPartsP1");
		// for (int i = 0; i < uriPartsP1.length; i++) {
		// 	System.out.println(uriPartsP1[i]);
		// }

		// check uri P2:
		String[] uriPartsP2 = uriPartsP1[3].split("\\?");
		if (uriPartsP2.length != 2) {
			System.out.println("length issue 38");
			this.sendStatus(r, 400);
			return;
		}

		// System.out.println("uriPartsP2");
		// for (int j = 0; j < uriPartsP2.length; j++) {
		// 	System.out.println(uriPartsP2[j]);
		// }

		/// check if uid given is integer, return 400 if not
		String uidString = uriPartsP2[0];
		int uid;
		try {
			uid = Integer.parseInt(uidString);
			System.out.println("uid parse");
		} catch (Exception e) {
			e.printStackTrace();
			this.sendStatus(r, 400);
			return;
		}
		/// check if query string is well formed, return 400 if not
		String[] uriPartsP3queryStr = uriPartsP2[1].split("=");
		if (uriPartsP3queryStr.length != 2 || !uriPartsP3queryStr[0].equals("radius")) {
			System.out.println("url issue");
			this.sendStatus(r, 400);
			return;
		}

		// System.out.println("uriPartsP3queryStr");
		// for (int k = 0; k < uriPartsP3queryStr.length; k++) {
		// 	System.out.println(uriPartsP3queryStr[k]);
		// }

		//// check if radius given is integer, return 400 if not
		String radiusString = uriPartsP3queryStr[1];
		int radius;
		try {
			radius = Integer.parseInt(radiusString);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("radius");
			this.sendStatus(r, 400);
			return;
		}


		try {
			Result result = this.dao.getDistances(uidString, radius);
			Record record;
			JSONObject fjson = new JSONObject();
			JSONObject driverID = new JSONObject();
			while (result.hasNext()) {
				record = result.next();
				String duid = record.get("uid").asString();
				Double lat = record.get("latitude").asDouble();
				Double lon = record.get("longitude").asDouble();
				String street = record.get("street").asString();
				System.out.println(duid + lat + lon + street);
				JSONObject details = new JSONObject();
				details.put("latitude", lat);
				details.put("longitude", lon);
				details.put("street", street);
				driverID.put(duid, details);
			}
			fjson.put("data", driverID);
			this.sendResponse(r, fjson, 200);
			return;
		} catch (Exception e){
			this.sendStatus(r, 400);
			return;
		}
	}
}
