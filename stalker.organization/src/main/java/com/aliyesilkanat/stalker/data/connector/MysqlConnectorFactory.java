package com.aliyesilkanat.stalker.data.connector;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.log4j.Logger;

public class MysqlConnectorFactory {
	private static final String MYSQL_CONNECTION = "jdbc:mysql://54.213.0.71:3306/stalker";
	private static final String USER_NAME = "java";
	private static final String PASSWORD = "root";

	private final Logger logger = Logger.getLogger(getClass());

	/**
	 * Creates connection to mysql database
	 * 
	 * @return jdbc connection
	 */
	public Connection createConnection() {
		String url = MYSQL_CONNECTION;
		Connection connection = null;
		try {
			String msg = "connecting mysql database {\"connection\":\"%s\"}";
			getLogger().debug(String.format(msg, MYSQL_CONNECTION));
			connection = DriverManager.getConnection(url, USER_NAME, PASSWORD);
			msg = "connected mysql database {\"connection\":\"%s\"}";
			getLogger().trace(String.format(msg, MYSQL_CONNECTION));

		} catch (SQLException e) {
			String msg = "cannot connect database {\"connection\":\"%s\"}";
			getLogger().error(String.format(msg, MYSQL_CONNECTION), e);
		}
		return connection;
	}

	public Logger getLogger() {
		return logger;
	}
}
