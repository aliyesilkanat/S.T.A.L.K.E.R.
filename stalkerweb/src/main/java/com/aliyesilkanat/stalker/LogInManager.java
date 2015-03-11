package com.aliyesilkanat.stalker;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

import com.aliyesilkanat.stalker.data.RelationalDataLayer;
import com.aliyesilkanat.stalker.data.UnfinishedOperationException;
import com.google.gson.JsonObject;

/**
 * @author Ali Yesilkanat <h1>Log In manager class for website.</h1>
 */
public class LogInManager {
	private static final String DB_ERROR = "DB ERROR";

	private static final String NOT_FOUND_USER_ON_DB = "NOT FOUND USER ON DB";

	private final Logger logger = Logger.getLogger(getClass());

	private static LogInManager instance = new LogInManager();

	public static LogInManager getInstance() {
		return instance;
	}

	public Logger getLogger() {
		return logger;
	}

	public String logIn(String email, String password) {
		String msg = "logging into system {\"userName\":\"%s\"}";
		getLogger().info(String.format(msg, email));
		String query = createQuery(email, password);
		JsonObject object;
		try {
			object = getResult(query);
		} catch (SQLException | UnfinishedOperationException e) {
			msg = "exception while executing sql {\"query\":\"%s\"}";
			getLogger().error(String.format(msg, query));
			object = setErrorDetails(DB_ERROR);
		}
		return object.toString();

	}

	private JsonObject getResult(String query) throws SQLException,
			UnfinishedOperationException {
		JsonObject object;
		Statement createStatement = RelationalDataLayer.getInstance()
				.createConnection().createStatement();
		ResultSet set = createStatement.executeQuery(query);
		if (set.next()) {
			object = setUserDetails(set.getString("Email"));
		} else {
			object = setErrorDetails(NOT_FOUND_USER_ON_DB);
		}
		return object;
	}

	private JsonObject setUserDetails(String email) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("email", email);
		return jsonObject;
	}

	private JsonObject setErrorDetails(String details) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("error", details);
		return jsonObject;
	}

	private String createQuery(String email, String password) {
		return "select * from Users where Email='" + email + "' and Password='"
				+ password + "'";
	}
}
