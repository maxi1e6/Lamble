package ca.utoronto.utm.mcs;
import java.io.IOException;
import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
public class App {
	static int PORT = 8000;
	public static void main(String[] args) throws IOException {
		String hostname = "0.0.0.0";
		int backlog = 0;
		HttpServer server = HttpServer.create(new InetSocketAddress(hostname, PORT), backlog);
		// TODO: Add server contexts here.
		// NOTE: Do not set executors for the server, you shouldn't need them.
		server.createContext("/user", new UserRequestRouter());
		server.createContext("/location", new LocationRequestRouter());
		server.createContext("/trip", new TripRequestRouter());
		server.start();
		System.out.printf("Server started on port %d...\n", PORT);
	}
}
// see ServerModule
