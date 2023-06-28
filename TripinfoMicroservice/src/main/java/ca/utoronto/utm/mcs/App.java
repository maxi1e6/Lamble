package ca.utoronto.utm.mcs;
import java.io.IOException;
import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
public class App {
	static int PORT = 8000;
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0", PORT), 0);
		// TODO: Add server contexts here. Do not set executors for the server, you shouldn't need them.
		server.createContext("/trip/request", new Request());
		server.createContext("/trip/confirm", new Confirm());
		server.createContext("/trip/passenger", new Passenger());
		server.createContext("/trip/driver", new Driver());
		server.createContext("/trip/driverTime", new Drivetime());
		server.createContext("/trip", new Trip());
		server.start();
		System.out.printf("Server started on port %d...\n", PORT);
	}
}
