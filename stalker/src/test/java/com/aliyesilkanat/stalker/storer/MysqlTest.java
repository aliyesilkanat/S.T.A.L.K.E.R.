package com.aliyesilkanat.stalker.storer;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.Test;

import com.aliyesilkanat.stalker.data.RelationalDataLayer;

public class MysqlTest {

	@Test
	public void checkConnection() throws Exception {
		String url = "jdbc:mysql://54.213.0.71:3306/stalker";
		String username = "java";
		String password = "root";
		Connection connection = null;
		boolean connected = false;
		try {
			connection = DriverManager.getConnection(url, username, password);
			connected = true;
		} catch (SQLException e) {
			throw new RuntimeException("Cannot connect the database!", e);
		} finally {
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException ignore) {
				}
		}
		assertTrue(connected);
	}

	@Test
	public void checkInsertionAndSelection() throws Exception {
		Connection connection = RelationalDataLayer.getInstance()
				.createConnection();
		Statement statement = connection.createStatement();
		statement.execute("Truncate table Test");
		statement
				.execute("insert into Test (Value,Time) values (2,timestamp'2015-02-13 16:39:36')");
		ResultSet resultSet = statement.executeQuery("select * from Test");
		while (resultSet.next()) {
			assertEquals("2", resultSet.getString(1));
			assertEquals("2015-02-13 16:39:36.0", resultSet.getString(2));
		}
		statement.close();
		connection.close();
	}

}
