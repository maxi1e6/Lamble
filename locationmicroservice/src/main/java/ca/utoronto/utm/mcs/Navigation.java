package ca.utoronto.utm.mcs;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.util.ArrayList;

import org.json.*;
import org.neo4j.driver.Result;
import java.util.List;
import org.neo4j.driver.Record;
import org.neo4j.driver.Value;
import java.util.ArrayList;
import java.util.Collections;
public class Navigation extends Endpoint {
	/**
	 * GET /location/navigation/:driverUid?passengerUid=:passengerUid
	 * @param driverUid, passengerUid
	 * @return 200, 400, 404, 500
	 * Get the shortest path from a driver to passenger weighted by the
	 * travel_time attribute on the ROUTE_TO relationship.
	 */
	@Override
	public void handleGet(HttpExchange r) throws IOException, JSONException {
		// format: GET /location/navigation/:driverUid?passengerUid=:passengerUid

		// check uri P1:
		String[] uriPartsP1 = r.getRequestURI().toString().split("/");
		if (uriPartsP1.length != 4) {
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
		} catch (Exception e) {
			e.printStackTrace();
			this.sendStatus(r, 400);
			return;
		}

		/// check if query string is well formed, return 400 if not
		String[] uriPartsP3queryStr = uriPartsP2[1].split("=");
		if (uriPartsP3queryStr.length != 2 || !uriPartsP3queryStr[0].equals("passengerUid")) {
			this.sendStatus(r, 400);
			return;
		}

		// System.out.println("uriPartsP3queryStr");
		// for (int k = 0; k < uriPartsP3queryStr.length; k++) {
		// 	System.out.println(uriPartsP3queryStr[k]);
		// }

		//// check if radius given is integer, return 400 if not
		String passengerUidString = uriPartsP3queryStr[1];
		int passengerUid;
		try {
			passengerUid = Integer.parseInt(passengerUidString);
		} catch (Exception e) {
			e.printStackTrace();
			this.sendStatus(r, 400);
			return;
		}





		System.out.println("Navigation handleGet"); // TODO: rm

		try {
			Result result = this.dao.getPath(uidString, passengerUidString);
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

/*
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
			this.sendResponse(r, res, 200);
			return;
*/

			// Record shortestPathRecord = recordList.get(0);
			// List<Value> shortestPathValueAsListInList = shortestPathRecord.values();
			// Value shortestPathValueAsList = shortestPathValueAsListInList.get(0);
			// List<String>s = new ArrayList<>();
			// for (int i = 0; i < shortestPathValueAsList.size(); i++) {
			// 	if ((i % 2) == 0) {
			// 		s.add(shortestPathValueAsList.get(i).get("name").toString());
			// 	} else {
			// 		s.add(shortestPathValueAsList.get(i).get("name").toString());
			// 	}
			// }
			// Collections.reverse(s);
			// System.out.println(s); // TODO: rm

			this.sendStatus(r, 500);
			return;
		} catch (Exception e) {
			e.printStackTrace();
			this.sendStatus(r, 500);
			return;
		}





	}
}
