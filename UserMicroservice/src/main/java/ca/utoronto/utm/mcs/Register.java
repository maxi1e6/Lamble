package ca.utoronto.utm.mcs;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import org.json.JSONException;
import org.json.JSONObject;
import java.sql.*;
public class Register extends Endpoint {
	/**
	 * POST /user/register
	 * @body name, email, password
	 * @return 200, 400, 500
	 * Register a user into the system using the given information.
	 */
	@Override
	public void handlePost(HttpExchange r) throws IOException, JSONException, SQLException {
		// uri checked completely at this point
		// check body:
		String body = Utils.convert(r.getRequestBody());
		JSONObject deserialized = new JSONObject(body);
		String name = null;
		String email = null;
		String password = null;
		Integer rides = null;
		Boolean isDriver = false;
		if(!deserialized.has("name") || !deserialized.has("email") || !deserialized.has("password"))
		{
			this.sendStatus(r, 400);
			return;
		}
		if (deserialized.has("name")) {
			if (deserialized.get("name").getClass() != String.class) {
				this.sendStatus(r, 400);
				return;
			}
			name = deserialized.getString("name");
		}
		if (deserialized.has("email")) {
			if (deserialized.get("email").getClass() != String.class) {
				this.sendStatus(r, 400);
				return;
			}
			email = deserialized.getString("email");
		}
		if (deserialized.has("password")) {
			if (deserialized.get("password").getClass() != String.class) {
				this.sendStatus(r, 400);
				return;
			}
			password = deserialized.getString("password");
		}
		if (deserialized.has("rides")) {
			if (deserialized.get("rides").getClass() != Integer.class) {
				this.sendStatus(r, 400);
				return;
			}
			rides = deserialized.getInt("rides");
		}
		if (deserialized.has("isDriver")) {
			if (deserialized.get("isDriver").getClass() != Boolean.class) {
				this.sendStatus(r, 400);
				return;
			}
			isDriver = deserialized.getBoolean("isDriver");
		}
		// if all the variables are still null then there's no variables in request so retrun 400:
		if (name == null && email == null && password == null) {
			this.sendStatus(r, 400);
			return;
		}
		// make query to check if user with given name, email, password exists, return 500 if error:
		ResultSet rs1;
		boolean resultHasNext;
		try {
			rs1 = this.dao.checkEmail(email);
			resultHasNext = rs1.next();
		} catch (SQLException e) {
			e.printStackTrace();
			this.sendStatus(r, 500);
			return;
		}
		// check if user with given name, email, password exists, return 400 if yes:
		if (resultHasNext) {
			this.sendStatus(r, 409);
			return;
		}
		// update db, return 500 if error:
		//ResultSet rs2;
		try {
			this.dao.addUser(name, email, password, rides, isDriver);
		} catch (SQLException e) {
			e.printStackTrace();
			this.sendStatus(r, 500);
			return;
		}
//		this.sendStatus(r, 200);
//		return;
		// return 200 if everything is updated without error:
		JSONObject json = new JSONObject();
		ResultSet rs2 = this.dao.getUid(email);
		if(rs2.next()) {
			json.put("uid", String.valueOf(rs2.getInt("uid")));
			this.sendResponse(r, json, 200);
			return;
		}
	}
}