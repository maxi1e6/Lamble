package ca.utoronto.utm.mcs;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import org.json.JSONException;
import java.lang.InterruptedException;
import org.json.JSONObject;
import java.net.http.HttpResponse;
public class LocationRequestRouter extends RequestRouter {
	public String url = "http://locationmicroservice:8000";
	@Override
	public void handleGet(HttpExchange r) throws IOException, JSONException, InterruptedException {
		// check if request url isn't malformed:
		String uri = r.getRequestURI().toString();
		String[] splitUrl = uri.split("/");
		if (splitUrl.length != 3 && splitUrl.length != 4) {
			System.out.println("16");
			this.sendStatus(r, 400);
			return;
		}
		// case 1:
		if (splitUrl.length == 3) {
			// check if uid given is integer, return 400 if not:
			String uidString = splitUrl[2];
			int uid;
			try {
				uid = Integer.parseInt(uidString);
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("29");
				this.sendStatus(r, 400);
				return;
			}
			HttpResponse<String> s = this.sendRequest(r.getRequestMethod(), url, r.getRequestURI().toString(), "{}");
			this.sendStatus(r, s.statusCode());
			return;
		}
		// case 2:
		else if (splitUrl.length == 4) {
			String[] uriPartsP2 = splitUrl[3].split("\\?");
			if (uriPartsP2.length != 2) {
				System.out.println("41");
				this.sendStatus(r, 400);
				return;
			}
			// check if id given is integer, return 400 if not:
			String idString = uriPartsP2[0];
			int id;
			try {
				id = Integer.parseInt(idString);
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("52");
				this.sendStatus(r, 400);
				return;
			}
			// check if query string is well formed, return 400 if not:
			String[] uriPartsP3queryStr = uriPartsP2[1].split("=");
			if (uriPartsP3queryStr.length != 2) {
				System.out.println("59");
				this.sendStatus(r, 400);
				return;
			}
			// check if query var is valid, return 400 if not:
			String queryVar = uriPartsP3queryStr[0];
			switch (queryVar) {
				case "radius":
					// check if radius given is integer, return 400 if not:
					String radiusString = uriPartsP3queryStr[1];
					int radius;
					try {
						radius = Integer.parseInt(radiusString);
					} catch (Exception e) {
						e.printStackTrace();
						System.out.println("74");
						this.sendStatus(r, 400);
						return;
					}
					// check if action is valid, return 400 if not:
					if (!splitUrl[2].equals("nearbyDriver")) {
						System.out.println("80");
						this.sendStatus(r, 400);
						return;
					}
					HttpResponse<String> s = this.sendRequest(r.getRequestMethod(), url, r.getRequestURI().toString(), "{}");
					this.sendResponse(r, new JSONObject(s.body()), s.statusCode());
					return;
				case "passengerUid":
					// check if passengerUid given is integer, return 400 if not:
					String passengerUidString = uriPartsP3queryStr[1];
					int passengerUid;
					try {
						passengerUid = Integer.parseInt(passengerUidString);
					} catch (Exception e) {
						e.printStackTrace();
						this.sendStatus(r, 400);
						return;
					}
					// check if action is valid, return 400 if not:
					if (!splitUrl[2].equals("navigation")) {
						this.sendStatus(r, 400);
						return;
					}
					HttpResponse<String> s1 = this.sendRequest(r.getRequestMethod(), url, r.getRequestURI().toString(), "{}");
					this.sendStatus(r, s1.statusCode());
					return;
				default:
					this.sendStatus(r, 400);
					return;
			}
		}
		// try: `curl -X GET http://localhost:8004/user/2`
		// and: `curl --location --request PUT 'localhost:8004/location/user' --data-raw '{"uid": "2", "is_driver": true}'`
		// and: `curl --location --request GET 'localhost:8004/location/2'`
	}
	@Override
	public void handlePatch(HttpExchange r) throws IOException, JSONException, InterruptedException {
		// check if request url isn't malformed:
		String[] splitUrl = r.getRequestURI().getPath().split("/");
		if (splitUrl.length != 3) {
			this.sendStatus(r, 400);
			return;
		}
		// check if uid given is integer, return 400 if not:
		String uidString = splitUrl[2];
		int uid;
		try {
			uid = Integer.parseInt(uidString);
		} catch (Exception e) {
			e.printStackTrace();
			this.sendStatus(r, 400);
			return;
		}
		String bodyStr = Utils.convert(r.getRequestBody());
		JSONObject bodyJson = new JSONObject(bodyStr);
		String bodyJsonStr = bodyJson.toString();
		HttpResponse<String> s = this.sendRequestPatch(r.getRequestMethod(), url, r.getRequestURI().toString(), bodyJsonStr);
		this.sendStatus(r, s.statusCode());
		return;
		// try: `curl -X GET http://localhost:8004/user/2`
		// and: `curl --location --request PUT 'localhost:8004/location/user' --data-raw '{"uid": "2", "is_driver": true}'`
		// and: `curl --location --request PATCH 'localhost:8004/location/2' --data-raw '{"longitude": 1234.56, "latitude": 78.91, "street": "my street"}'`
		// bug: `curl --location --request PATCH 'localhost:8004/location/2' --data-raw '{"longitude": 1.00, "latitude": 1.00, "street": "X"}'`
	}
	@Override
	public void handlePost(HttpExchange r) throws IOException, JSONException, InterruptedException {
		// check if request url isn't malformed:
		String[] splitUrl = r.getRequestURI().getPath().split("/");
		if (splitUrl.length != 3) {
			this.sendStatus(r, 400);
			return;
		}
		if (!splitUrl[2].equals("hasRoute")) {
			this.sendStatus(r, 400);
			return;
		}
		String bodyStr = Utils.convert(r.getRequestBody());
		JSONObject bodyJson = new JSONObject(bodyStr);
		String bodyJsonStr = bodyJson.toString();
		HttpResponse<String> s = this.sendRequestPost(r.getRequestMethod(), url, r.getRequestURI().toString(), bodyJsonStr);
		this.sendStatus(r, s.statusCode());
		return;
		// try: `curl --location --request PUT 'localhost:8004/location/road' --data-raw '{"roadName": "my street", "hasTraffic": true}'`
		// and: `curl --location --request PUT 'localhost:8004/location/road' --data-raw '{"roadName": "their street", "hasTraffic": false}'`
		// and: `curl --location --request POST 'localhost:8004/location/hasRoute' --data-raw '{"roadName1": "my street", "roadName2": "their street", "hasTraffic": true, "time": 7}'`
		// bug: `curl --location --request POST 'localhost:8004/location/hasRoute' --data-raw '{"roadName1": "my street",, "roadName2": "their street", "hasTraffic": true, "time": 7}'`
	}
	@Override
	public void handlePut(HttpExchange r) throws IOException, JSONException, InterruptedException {
		// check if request url isn't malformed:
		String[] splitUrl = r.getRequestURI().getPath().split("/");
		if (splitUrl.length != 3) {
			this.sendStatus(r, 400);
			return;
		}
		if (!splitUrl[2].equals("user") && !splitUrl[2].equals("road")) {
			this.sendStatus(r, 400);
			return;
		}
		String bodyStr = Utils.convert(r.getRequestBody());
		JSONObject bodyJson = new JSONObject(bodyStr);
		String bodyJsonStr = bodyJson.toString();
		HttpResponse<String> s = this.sendRequestPut(r.getRequestMethod(), url, r.getRequestURI().toString(), bodyJsonStr);
		this.sendStatus(r, s.statusCode());
		return;
		// try: `curl -X GET http://localhost:8004/user/2`
		// and: `curl --location --request PUT 'localhost:8004/location/user' --data-raw '{"uid": "2", "is_driver": true}'`
		// ---
		// try: `curl --location --request PUT 'localhost:8004/location/road' --data-raw '{"roadName": "my street", "hasTraffic": true}'`
	}
	@Override
	public void handleDelete(HttpExchange r) throws IOException, JSONException, InterruptedException {
		// check if request url isn't malformed:
		String[] splitUrl = r.getRequestURI().getPath().split("/");
		if (splitUrl.length != 3) {
			this.sendStatus(r, 400);
			return;
		}
		if (!splitUrl[2].equals("user") && !splitUrl[2].equals("route")) {
			this.sendStatus(r, 400);
			return;
		}
		String bodyStr = Utils.convert(r.getRequestBody());
		JSONObject bodyJson = new JSONObject(bodyStr);
		String bodyJsonStr = bodyJson.toString();
		HttpResponse<String> s = this.sendRequestDelete(r.getRequestMethod(), url, r.getRequestURI().toString(), bodyJsonStr);
		this.sendStatus(r, s.statusCode());
		return;
		// try: `curl -X GET http://localhost:8004/user/2`
		// and: `curl --location --request PUT 'localhost:8004/location/user' --data-raw '{"uid": "2", "is_driver": true}'`
		// and: `curl --location --request DELETE 'localhost:8004/location/user' --data-raw '{"uid": "2"}'`
		// ---
		// try: `curl --location --request DELETE 'localhost:8004/location/route' --data-raw '{"roadName1": "my street", "roadName2": "their street"}'`
	}
}
