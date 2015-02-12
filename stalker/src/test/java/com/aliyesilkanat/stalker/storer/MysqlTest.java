package com.aliyesilkanat.stalker.storer;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.junit.Test;

public class MysqlTest {

	@Test
	public void checkConnection() throws Exception {
		String url = "jdbc:mysql://54.213.0.71:3306/stalker";
		String username = "java";
		String password = "root";
		Connection connection = null;
		boolean connected = false;
		try {
			System.out.println("Connecting database...");
			connection = DriverManager.getConnection(url, username, password);
			System.out.println("Database connected!");
			connected = true;
		} catch (SQLException e) {
			throw new RuntimeException("Cannot connect the database!", e);
		} finally {
			System.out.println("Closing the connection.");
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException ignore) {
				}
		}
		assertTrue(connected);
	}
}
