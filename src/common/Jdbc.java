package common;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Jdbc {

	/*
	 * Configuration for Oracle private static String DRIVER =
	 * "oracle.jdbc.driver.OracleDriver"; private static String URL =
	 * "jdbc:oracle:thin:@156.35.94.99:1521:DESA"; private static String USER = "";
	 * private static String PASS = "";
	 */
	/*
	 * Configuration for SQLite
	 */
	private static String DRIVER = "org.sqlite.JDBC";
	private static String URL = "jdbc:sqlite:database/SPRINT2_DB_V3.0/ips_db_v3.0.db";
	private static String USER = "";
	private static String PASS = "";

	static {
		try {
			Class.forName(DRIVER);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Driver not found in classpath", e);
		}
	}

	public static Connection getConnection() throws SQLException {
		return DriverManager.getConnection(URL, USER, PASS);
	}

	public static void close(ResultSet rs, Statement st, Connection c) {
		close(rs);
		close(st);
		close(c);
	}

	public static void close(ResultSet rs, Statement st) {
		close(rs);
		close(st);
	}

	protected static void close(ResultSet rs) {
		if (rs != null)
			try {
				rs.close();
			} catch (SQLException e) {
				/* ignore */}
	}

	public static void close(Statement st) {
		if (st != null)
			try {
				st.close();
			} catch (SQLException e) {
				/* ignore */}
	}

	public static void close(Connection c) {
		if (c != null)
			try {
				c.close();
			} catch (SQLException e) {
				/* ignore */}
	}

	/**
	 * Used to create a connection to be shared among tasks in a single thread.
	 * 
	 * @return
	 * @throws SQLException
	 */
	public static Connection createThreadConnection() throws SQLException {
		Connection con = getConnection();
		threadConnection.set(con);
		return con;
	}

	/**
	 * Create a thread connection to be shared among tasks
	 * 
	 * @param URL Specific URL to obtain the connection
	 * @return The connection
	 * @throws SQLException If connection could not be established
	 */
	public static Connection createThreadConnection(String URL) throws SQLException {
		Connection con = getConnection(URL);
		threadConnection.set(con);
		return con;
	}

	/**
	 * Create a connection with the given URL
	 * 
	 * @param URL The URL of the connection
	 * @return
	 * @throws SQLException If connection could not be established, not having the
	 *                      appropiate driver for the URL will throw this exception
	 */

	private static Connection getConnection(String URL) throws SQLException {
		return DriverManager.getConnection(URL);
	}

	private static ThreadLocal<Connection> threadConnection = new ThreadLocal<Connection>();

	/**
	 * Get the current connection
	 * 
	 * @return
	 */
	public static Connection getCurrentConnection() {
		return threadConnection.get();
	}

}
