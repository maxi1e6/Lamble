package ca.utoronto.utm.mcs;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import org.json.JSONException;
import java.lang.InterruptedException;
import org.json.JSONObject;
import java.net.http.HttpResponse;
public class UserRequestRouter extends RequestRouter {
	public String url = "http://usermicroservice:8000";
	@Override
	public void handleGet(HttpExchange r) throws IOException, JSONException, InterruptedException {
		HttpResponse<String> s = this.sendRequest(r.getRequestMethod(), url, r.getRequestURI().toString(), "{}");
		this.sendResponse(r, new JSONObject(s.body()), s.statusCode());
		return;
		// try: `curl -X GET http://localhost:8004/user/2`
	}

	// @Override
	// public void handlePatch(HttpExchange r) throws IOException, JSONException, InterruptedException {
	// 	HttpResponse<String> s = this.sendRequest(r.getRequestMethod(), url, r.getRequestURI().toString(), "{}");
	// 	this.sendStatus(r, s.statusCode());
	// 	return;
	// 	// try: `curl -X GET http://localhost:8004/user/2`
	// }
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
			case "register":
			case "login":
				String bodyStr = Utils.convert(r.getRequestBody());
				JSONObject bodyJson = new JSONObject(bodyStr);
				String bodyJsonStr = bodyJson.toString();
				HttpResponse<String> s = this.sendRequestPost(r.getRequestMethod(), url, r.getRequestURI().toString(), bodyJsonStr);
				this.sendResponse(r, new JSONObject(s.body()), s.statusCode());
				return;
			default:
				this.sendStatus(r, 400);
				break;
		}
		// try: `curl -v -X POST -H 'Content-Type: application/json' -d '{"name": "name", "email": "email1", "password": "password1"}' http://localhost:8004/user/register`
		// NOTE: email has to be unique
	}

	public void handlePatch(HttpExchange r) throws IOException, JSONException, InterruptedException {
		String[] splitUrl = r.getRequestURI().getPath().split("/");
		if (splitUrl.length != 3) {
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
	}

}
