package ca.utoronto.utm.mcs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.bson.types.ObjectId;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Please write your tests in this class. 
 */
 
public class AppTest {
    public String userurl = "http://localhost:8002";
    public static String _id;
    @Test
    @Order(1)
    public void postRequest200() throws JSONException, IOException, InterruptedException {
        JSONObject req;
        req = torequest("2", 5);
        HttpResponse<String> s = this.sendRequestPost("POST", userurl, "/trip/request", req.toString());
        assertEquals(HttpURLConnection.HTTP_OK, s.statusCode());
    }

    @Test
    @Order(2)
    public void postRequest400() throws JSONException, IOException, InterruptedException {
        JSONObject req;
        req = torequest("2", -5);
        HttpResponse<String> s = this.sendRequestPost("POST", userurl, "/trip/request", req.toString());
        assertEquals(HttpURLConnection.HTTP_BAD_REQUEST, s.statusCode());
    }

    @Test
    @Order(3)
    public void postConfirm200() throws JSONException, IOException, InterruptedException {
        JSONObject conf;
        conf = toconfirm("1", "2", 1645917102);
        HttpResponse<String> s = this.sendRequestPost("POST", userurl, "/trip/confirm", conf.toString());
        JSONObject json = new JSONObject(s.body());
        JSONObject data = json.getJSONObject("data");
        AppTest._id = data.get("_id").toString();
        System.out.println(_id);
        assertEquals(HttpURLConnection.HTTP_OK, s.statusCode());
    }

    @Test
    @Order(4)
    public void postConfirm400() throws JSONException, IOException, InterruptedException {
        JSONObject conf;
        conf = toconfirm("1", null, 1645917102);
        HttpResponse<String> s = this.sendRequestPost("POST", userurl, "/trip/confirm", conf.toString());
        assertEquals(HttpURLConnection.HTTP_BAD_REQUEST, s.statusCode());
    }


    @Test
    @Order(5)
    public void patchTrip200() throws IOException, InterruptedException, JSONException {
        JSONObject patchtrip = new JSONObject();
        patchtrip.put("distance", 200);
        HttpResponse<String> s = this.sendRequestPost("PATCH", userurl, "/trip/" + AppTest._id,  patchtrip.toString());
        assertEquals(HttpURLConnection.HTTP_OK, s.statusCode());
    }

    @Test
    @Order(6)
    public void patchTrip400() throws IOException, InterruptedException {
        HttpResponse<String> s = this.sendRequestPost("PATCH", userurl, "/trip/" + AppTest._id, "{}");
        assertEquals(HttpURLConnection.HTTP_BAD_REQUEST, s.statusCode());
    }

    @Test
    @Order(7)
    public void getpassenger200() throws IOException, InterruptedException, JSONException {
        HttpResponse<String> s = this.sendRequest("GET", userurl, "/trip/passenger/" + "2", "{}");
        assertEquals(HttpURLConnection.HTTP_OK, s.statusCode());
    }

    @Test
    @Order(8)
    public void getPassenger404() throws IOException, InterruptedException {
        HttpResponse<String> s = this.sendRequest("GET", userurl, "/trip/passenger/" + "-1", "{}");
        assertEquals(HttpURLConnection.HTTP_NOT_FOUND, s.statusCode());
    }


    @Test
    @Order(9)
    public void getDriver200() throws IOException, InterruptedException, JSONException {
        HttpResponse<String> s = this.sendRequest("GET", userurl, "/trip/driver/1", "{}");
        assertEquals(HttpURLConnection.HTTP_OK, s.statusCode());
    }

    @Test
    @Order(10)
    public void getDriver404() throws IOException, InterruptedException {
        HttpResponse<String> s = this.sendRequest("GET", userurl, "/trip/driver/" + "-1", "{}");
        assertEquals(HttpURLConnection.HTTP_NOT_FOUND, s.statusCode());
    }

    @Test
    @Order(11)
    public void getDriverTime200() throws IOException, InterruptedException, JSONException {
        HttpResponse<String> s = this.sendRequest("GET", userurl, "/trip/driverTime/" + AppTest._id, "{}");
        assertEquals(HttpURLConnection.HTTP_OK, s.statusCode());
    }

    @Test
    @Order(12)
    public void getDriverTime400() throws IOException, InterruptedException {
        HttpResponse<String> s = this.sendRequest("GET", userurl, "/trip/driverTime/" + "-1", "{}");
        assertEquals(HttpURLConnection.HTTP_BAD_REQUEST, s.statusCode());
    }
    public JSONObject torequest(String uid, Integer radius) throws JSONException {
        JSONObject req = new JSONObject();
        req.put("uid", uid);
        req.put("radius", radius);
        return req;
    }

    public JSONObject toconfirm(String driver, String passenger, Integer startTime) throws JSONException {
        JSONObject conf = new JSONObject();
        conf.put("driver", driver);
        conf.put("passenger", passenger);
        conf.put("startTime", startTime);

        return conf;
    }
    public HttpResponse<String> sendRequestPost(String method, String url, String uri, String body) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest req = HttpRequest.newBuilder()
                .method(method, HttpRequest.BodyPublishers.ofString(body))
                .header("Content-Type", "application/json")
                .uri(URI.create(url + uri))
                .build();
        return client.send(req, HttpResponse.BodyHandlers.ofString());
    }
    public HttpResponse<String> sendRequest(String method, String url, String uri, String body) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest req = HttpRequest.newBuilder()
                .method(method, HttpRequest.BodyPublishers.ofString(body))
                .uri(URI.create(url + uri))
                .build();
        return client.send(req, HttpResponse.BodyHandlers.ofString());
    }
    public HttpResponse<String> sendRequestPatch(String method, String url, String uri, String body) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest req = HttpRequest.newBuilder()
                .method(method, HttpRequest.BodyPublishers.ofString(body))
                .uri(URI.create(url + uri))
                .build();
        return client.send(req, HttpResponse.BodyHandlers.ofString());
    }
}
