package ca.utoronto.utm.mcs;

/** 
 * Everything you need in order to send and recieve httprequests to 
 * other microservices is given here. Do not use anything else to send 
 * and/or recieve http requests from other microservices. Any other 
 * imports are fine.
 */
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;

import com.sun.net.httpserver.HttpExchange;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class Drivetime extends Endpoint {

    /**
     * GET /trip/driverTime/:_id
     * @param _id
     * @return 200, 400, 404, 500
     * Get time taken to get from driver to passenger on the trip with
     * the given _id. Time should be obtained from navigation endpoint
     * in location microservice.
     */

    @Override
    public void handleGet(HttpExchange r) throws IOException, JSONException, InterruptedException {
        String[] splitUrl = r.getRequestURI().getPath().split("/");
        if (splitUrl.length != 4) {
            System.out.println("no length");
            this.sendStatus(r, 400);
            return;
        }
        String _id = splitUrl[3];
        try{
            if(!ObjectId.isValid(_id))
            {
                this.sendStatus(r, 400);
                System.out.println("wrong format of _id");
                return;
            }
        } catch (IllegalArgumentException e){
            this.sendStatus(r, 400);
        }
        Document tripinfo = this.dao.getTrip(_id);
        String driver = tripinfo.getString("driver");
        String passenger = tripinfo.getString("passenger");
        String uri = "/location/navigation/" + driver + "?passengerUid=" + passenger;
        String location_url = "http://locationmicroservice:8000";
        HttpResponse<String> s2 = this.sendRequest("GET", location_url, uri, "{}");
        if(s2.body().isEmpty())
        {
            System.out.println("no time");
            this.sendStatus(r, 404);
        }
        JSONObject locjson = new JSONObject(s2.body());
        JSONObject data;
        data = locjson.getJSONObject("data");
        JSONObject fjson = new JSONObject();
        Integer arrival_time = data.getInt("total_time");
        fjson.put("arrival_time", arrival_time);
        this.sendResponse(r, fjson, 200);
    }
}
