package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpExchange;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class Confirm extends Endpoint {

    /**
     * POST /trip/confirm
     * @body driver, passenger, startTime
     * @return 200, 400
     * Adds trip info into the database after trip has been requested.
     */

    @Override
    public void handlePost(HttpExchange r) throws IOException, JSONException {
        String body = Utils.convert(r.getRequestBody());
        JSONObject deserialized = new JSONObject(body);
        String driver = null;
        String passenger = null;
        int startTime = 0;
        if(!deserialized.has("driver") || !deserialized.has("passenger") || !deserialized.has("startTime"))
        {
            System.out.println("Params");
            this.sendStatus(r, 400);
            return;
        }
        if (deserialized.has("driver")) {
            if (deserialized.get("driver").getClass() != String.class) {
                System.out.println("driver");
                this.sendStatus(r, 400);
                return;
            }
            driver = deserialized.getString("driver");
        }
        if (deserialized.has("passenger")) {
            if (deserialized.get("passenger").getClass() != String.class) {
                System.out.println("pass");
                this.sendStatus(r, 400);
                return;
            }
            passenger = deserialized.getString("passenger");
        }
        if (deserialized.has("startTime")) {
            if (deserialized.get("startTime").getClass() != Integer.class) {
                System.out.println("ST");
                this.sendStatus(r, 400);
                return;
            }
            startTime = deserialized.getInt("startTime");
        }
        JSONObject data = this.dao.addTrip(driver, passenger, startTime);
        if(data != null) {
            System.out.println("dao");
            this.sendResponse(r, data, 200);
            return;
        }
        this.sendStatus(r,500);
        return;
    }
}
