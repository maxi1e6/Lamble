package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpExchange;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.ArrayList;

public class Driver extends Endpoint {

    /**
     * GET /trip/driver/:uid
     * @param uid
     * @return 200, 400, 404
     * Get all trips driver with the given uid has.
     */

    @Override
    public void handleGet(HttpExchange r) throws IOException, JSONException {
        String[] splitUrl = r.getRequestURI().getPath().split("/");
        if (splitUrl.length != 4) {
            System.out.println("no lenght");
            this.sendStatus(r, 400);
            return;
        }
        String uid = splitUrl[3];
        JSONObject fjson = new JSONObject();  //final json
        ArrayList arr = new ArrayList();
        arr = this.dao.getdriverinfo(uid);
        if(arr.isEmpty()) {
            this.sendStatus(r, 404);
            return;
        }
        JSONObject trips = new JSONObject();
        trips.put("trips", arr);
        fjson.put("data", trips);
        this.sendResponse(r, fjson, 200);
        return;
    }
}
