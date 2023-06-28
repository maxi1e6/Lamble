package ca.utoronto.utm.mcs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.sun.net.httpserver.HttpExchange;
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
    public String userurl = "http://localhost:8001";

    @Test
    @Order(1)
    public void postRegister200() throws IOException, InterruptedException, JSONException {
        JSONObject reg = toRegister("name", "email", "password");
        HttpResponse<String> s = this.sendRequestPost("POST", userurl, "/user/register", reg.toString());
        assertEquals(HttpURLConnection.HTTP_OK, s.statusCode());
    }
    @Test
    @Order(2)
    public void postRegister409() throws IOException, InterruptedException, JSONException {
        JSONObject reg = toRegister("name", "email", null);
        HttpResponse<String> s = this.sendRequestPost("POST", userurl, "/user/register", reg.toString());
        assertEquals(HttpURLConnection.HTTP_BAD_REQUEST, s.statusCode());
    }

    @Test
    @Order(3)
    public void postLogin200() throws JSONException, IOException, InterruptedException {
        JSONObject log = toLogin("email", "password");
        HttpResponse<String> s = this.sendRequestPost("POST", userurl, "/user/login", log.toString());
        assertEquals(HttpURLConnection.HTTP_OK, s.statusCode());
    }

    @Test
    @Order(4)
    public void postLogin404() throws JSONException, IOException, InterruptedException {
        JSONObject log = toLogin("email19478", "password208429");
        HttpResponse<String> s = this.sendRequestPost("POST", userurl, "/user/login", log.toString());
        assertEquals(HttpURLConnection.HTTP_NOT_FOUND, s.statusCode());
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


    public JSONObject toRegister(String name, String email, String password) throws JSONException {
        JSONObject reg = new JSONObject();
        reg.put("name", name);
        reg.put("email", email);
        reg.put("password", password);
        return reg;
    }
    public JSONObject toLogin(String email, String password) throws JSONException {
        JSONObject reg = new JSONObject();
        reg.put("email", email);
        reg.put("password", password);
        return reg;
    }
}
