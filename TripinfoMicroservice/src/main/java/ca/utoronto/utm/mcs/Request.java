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
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class Request extends Endpoint {

    /**
     * POST /trip/request
     * @body uid, radius
     * @return 200, 400, 404, 500
     * Returns a list of drivers within the specified radius 
     * using location microservice. List should be obtained
     * from navigation endpoint in location microservice
     */

    @Override
    public void handlePost(HttpExchange r) throws IOException, JSONException, InterruptedException {
        String body = Utils.convert(r.getRequestBody());
        JSONObject deserialized = new JSONObject(body);
        String uid = null;
        int radius = 0;
        if(!deserialized.has("uid") || !deserialized.has("radius"))
        {
            System.out.println("Params");
            this.sendStatus(r, 400);
            return;
        }
        if (deserialized.has("uid")) {
            if (deserialized.get("uid").getClass() != String.class) {
                System.out.println("uid");
                this.sendStatus(r, 400);
                return;
            }
            uid = deserialized.getString("uid");
        }
        if (deserialized.has("radius")) {
            if (deserialized.get("radius").getClass() != Integer.class) {
                System.out.println("rad");
                this.sendStatus(r, 400);
                return;
            }
            radius = deserialized.getInt("radius");
            if (radius < 0){
                System.out.println("<0");
                this.sendStatus(r, 400);
                return;
            }
        }
        //getting UID info
        String getuseruri = "/user/" + uid;
        String user_url = "http://usermicroservice:8000";
        HttpResponse<String> s1 = this.sendRequest("GET", user_url, getuseruri, "{}");
        if (s1.statusCode() != 200) {
            System.out.println("user");
            this.sendStatus(r, s1.statusCode());
            return;
        }
        JSONObject json = new JSONObject(s1.body());
        System.out.println(json);
//        this.sendResponse(r, json, 200);
//        return;
        JSONObject userdata = json.getJSONObject("data");
        boolean isDriver = userdata.getBoolean("isDriver");
        //Sending request to location to get Nearby driver's info
        String uri = "/location/nearbyDriver/" + uid + "?radius=" + radius;
        String location_url = "http://locationmicroservice:8000";
        HttpResponse<String> s2 = this.sendRequest("GET", location_url, uri, "{}");
        if(s2.statusCode() != 200) {
            System.out.println("loc");
            this.sendStatus(r, s1.statusCode());
            return;
        }
        JSONObject j1 = new JSONObject(s2.body());
        JSONObject data;
        data = (JSONObject) j1.get("data");
        ArrayList<String> arr = new ArrayList<>();
        Iterator keys = data.keys();
        while(keys.hasNext())
        {
            arr.add(keys.next().toString());
        }
        if(isDriver)
        {
            arr.add(uid);
        }
        if(arr.isEmpty())
        {
            this.sendStatus(r, 404);
            return;
        }
        j1.put("data", arr);
        this.sendResponse(r, j1, 200);
        return;

//        JSONObject nearbydrivers = new JSONObject(s2.body());
//        String data = nearbydrivers.get("data").toString();
//        System.out.println(data);
//        this.sendStatus(r, s2.statusCode());
//        return;
    }
}
