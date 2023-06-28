package ca.utoronto.utm.mcs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    public String url = "http://localhost:8000";
    @Test
    @Order(1)
    public void getNearbyDriver400() throws JSONException, IOException, InterruptedException {
        JSONObject user1 = new JSONObject();
        user1.put("uid", "1");
        user1.put("is_driver", true);
        HttpResponse<String> s1 = this.sendRequestPut("PUT", url, "/location/user", user1.toString());
        assertEquals(HttpURLConnection.HTTP_BAD_REQUEST, s1.statusCode());
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
    public HttpResponse<String> sendRequestPut(String method, String url, String uri, String body) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest req = HttpRequest.newBuilder()
                .method(method, HttpRequest.BodyPublishers.ofString(body))
                .uri(URI.create(url + uri))
                .build();
        return client.send(req, HttpResponse.BodyHandlers.ofString());
    }

}
