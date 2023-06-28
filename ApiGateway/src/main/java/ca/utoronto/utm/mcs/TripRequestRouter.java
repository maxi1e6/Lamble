package ca.utoronto.utm.mcs;
import com.sun.net.httpserver.HttpExchange;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.http.HttpResponse;

public class TripRequestRouter extends RequestRouter {
	public String url = "http://tripinfomicroservice:8000";
	@Override
	public void handleGet(HttpExchange r) throws IOException, JSONException, InterruptedException {
		// check if request url isn't malformed:
		String[] splitUrl = r.getRequestURI().getPath().split("/");
		if (splitUrl.length != 4) {
			System.out.println(splitUrl.length);
			System.out.println("no length");
			this.sendStatus(r, 400);
			return;
		}
		// check if uid given is integer, return 400 if not
		String uidString = splitUrl[3];
		// check if action is valid, return 400 if not:
		String actionString = splitUrl[2];
		switch (actionString) {
			case "passenger":
			case "driver":
			case "driverTime":
				HttpResponse<String> s = this.sendRequest(r.getRequestMethod(), url, r.getRequestURI().toString(), "{}");
				this.sendResponse(r, new JSONObject(s.body()), s.statusCode());
				return;
			default:
				this.sendStatus(r, 400);
				return;
		}
		// try: ...
	}
	@Override
	public void handlePatch(HttpExchange r) throws IOException, JSONException, InterruptedException {
		// check if request url isn't malformed:
		String[] splitUrl = r.getRequestURI().getPath().split("/");
		if (splitUrl.length != 3) {
			this.sendStatus(r, 400);
			return;
		}
		// check if id given is integer, return 400 if not
		String bodyStr = Utils.convert(r.getRequestBody());
		JSONObject bodyJson = new JSONObject(bodyStr);
		String bodyJsonStr = bodyJson.toString();
		HttpResponse<String> s = this.sendRequestPatch(r.getRequestMethod(), url, r.getRequestURI().toString(), bodyJsonStr);
		this.sendStatus(r, s.statusCode());
		return;
		// try: ...
	}
	@Override
	public void handlePost(HttpExchange r) throws IOException, JSONException, InterruptedException {
		// check if request url isn't malformed:
		String[] splitUrl = r.getRequestURI().getPath().split("/");
		if (splitUrl.length != 3) {
			this.sendStatus(r, 400);
			return;
		}
		// check if action is valid, return 400 if not:
		String actionString = splitUrl[2];
		switch (actionString) {
			case "request":
			case "confirm":
				String bodyStr = Utils.convert(r.getRequestBody());
				JSONObject bodyJson = new JSONObject(bodyStr);
				String bodyJsonStr = bodyJson.toString();
				HttpResponse<String> s = this.sendRequestPost(r.getRequestMethod(), url, r.getRequestURI().toString(), bodyJsonStr);
				this.sendResponse(r, new JSONObject(s.body()), s.statusCode());
				return;
			default:
				this.sendStatus(r, 400);
				return;
		}
		// try: ...
	}
}
