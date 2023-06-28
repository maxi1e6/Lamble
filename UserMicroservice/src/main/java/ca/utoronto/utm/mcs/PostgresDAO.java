package ca.utoronto.utm.mcs;

import java.sql.*;
import io.github.cdimascio.dotenv.Dotenv;

import javax.xml.transform.Result;

public class PostgresDAO {

	public Connection conn;
	public Statement st;

	public PostgresDAO() {
		Dotenv dotenv = Dotenv.load();
		String addr = dotenv.get("POSTGRES_ADDR");
		String url = "jdbc:postgresql://" + addr + ":5432/root";
		try {
			Class.forName("org.postgresql.Driver");
			this.conn = DriverManager.getConnection(url, "root", "123456");
			this.st = this.conn.createStatement();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	// *** implement database operations here *** //

	public ResultSet getUsersFromUid(int uid) throws SQLException {
		String query = "SELECT * FROM users WHERE uid = %d";
		query = String.format(query, uid);
		return this.st.executeQuery(query);
	}

	public ResultSet getUserData(int uid) throws SQLException {
		String query = "SELECT prefer_name as name, email, rides, isdriver FROM users WHERE uid = %d";
		query = String.format(query, uid);
		return this.st.executeQuery(query);
	}

	public void updateUserAttributes(int uid, String email, String password, String prefer_name, Integer rides, Boolean isDriver) throws SQLException {
		String query;
		if (email != null) {
			query = "UPDATE users SET email = '%s' WHERE uid = %d";
			query = String.format(query, email, uid);
			this.st.execute(query);
		}
		if (password != null) {
			query = "UPDATE users SET password = '%s' WHERE uid = %d";
			query = String.format(query, password, uid);
			this.st.execute(query);
		}
		if (prefer_name != null) {
			query = "UPDATE users SET prefer_name = '%s' WHERE uid = %d";
			query = String.format(query, prefer_name, uid);
			this.st.execute(query);
		}
		if ((rides != null)) {
			query = "UPDATE users SET rides = %d WHERE uid = %d";
			query = String.format(query, rides, uid);
			this.st.execute(query);
		}
		if (isDriver != null) {
			query = "UPDATE users SET isdriver = %s WHERE uid = %d";
			query = String.format(query, isDriver.toString(), uid);
			this.st.execute(query);
		}
	}

	// *** added database operations here *** //
	public ResultSet checkLogin(String email, String password) throws SQLException {
		String query = "SELECT * FROM users WHERE email = '%s' AND password = '%s'";
		query = String.format(query, email, password);
		return this.st.executeQuery(query);
	}
	public ResultSet getRegisteredUsers(String prefer_name, String email, String password) throws SQLException {
		String query = "SELECT * FROM users WHERE prefer_name = '%s' AND email = '%s' AND password = '%s'";
		query = String.format(query, prefer_name, email, password);
		return this.st.executeQuery(query);
	}

	public void addUser(String prefer_name, String email, String password, Integer rides, Boolean isDriver) throws SQLException {
		if(rides == null){
			rides = 0;
		}
		String query = "INSERT INTO users (prefer_name, email, password, rides, isDriver) VALUES ('%s', '%s', '%s', %d, %b)";
			query = String.format(query, prefer_name, email, password, rides, isDriver);
		this.st.execute(query);
	}

	public ResultSet checkEmail(String email) throws SQLException {
		String query = "SELECT * FROM users WHERE email = '%s'";
		query = String.format(query, email);
		return this.st.executeQuery(query);
	}
	public ResultSet getUid(String email) throws SQLException {
		String query = "SELECT * FROM users WHERE email = '%s'";
		query = String.format(query, email);
		return this.st.executeQuery(query);
	}
}
