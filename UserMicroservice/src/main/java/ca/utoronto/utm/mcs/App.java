package ca.utoronto.utm.mcs;
import java.io.IOException;
import java.sql.SQLException;
import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
public class App {
	static int PORT = 8000;
	public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException {
		HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0", PORT), 0);
		server.createContext("/user", new User());
		// TODO: Add server contexts here. Do not set executors for the server, you shouldn't need them.
		server.createContext("/user/register", new Register());
		server.createContext("/user/login", new Login());

		server.start();
		System.out.printf("Server started on port %d...\n", PORT);
	}
}
