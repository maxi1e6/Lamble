package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpExchange;
import org.bson.Document;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class Passenger extends Endpoint {

    /**
     * GET /trip/passenger/:uid
     * @param uid
     * @return 200, 400, 404
     * Get all trips the passenger with the given uid has.
     */

    @Override
    public void handleGet(HttpExchange r) throws IOException,JSONException{
        String[] splitUrl = r.getRequestURI().getPath().split("/");
        if (splitUrl.length != 4) {
            System.out.println("no lenght");
            this.sendStatus(r, 400);
            return;
        }
        String uid = splitUrl[3];
        JSONObject trips = new JSONObject();
        ArrayList arr = new ArrayList();
        arr = this.dao.getpassengerinfo(uid);
        if(arr.isEmpty())
        {
            this.sendStatus(r, 404);
        }
        JSONObject trip = new JSONObject();
        trip.put("trips", arr);
        trips.put("data", trip);
        this.sendResponse(r, trips, 200);
    }
}
