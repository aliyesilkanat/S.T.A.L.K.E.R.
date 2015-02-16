package com.aliyesilkanat.stalker.friendshipactivity;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;

import org.apache.log4j.Logger;

import com.aliyesilkanat.stalker.data.RelationalDataLayer;
import com.aliyesilkanat.stalker.data.constants.FriendshipActivityLogConst;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class FriendshipActivityReporter {

	private final Logger logger = Logger.getLogger(getClass());

	private long startDate;
	private long endDate;
	private int numberOfFollowings;
	
	public FriendshipActivityReporter(long startDate, long endDate) {
		this.startDate = startDate;
		this.endDate = endDate;
		numberOfFollowings = 0;
	}

	private void closeConnection(Connection createConnection,
			Statement statement) {
		getLogger().debug("closing mysql connection");
		if (statement != null) {
			try {
				statement.close();
			} catch (SQLException e) {
			}
		}
		if (createConnection != null) {
			try {
				createConnection.close();
			} catch (SQLException e) {
			}
		}
		getLogger().trace("closed mysql connection");
	}

	private void createdReportLog(String userURI, JsonArray followingsArray) {
		String msg = "created report object {\"userUri\":\"%s\", \"followings\":\"%s\"}";
		getLogger().info(String.format(msg, userURI, followingsArray));
	}

	/**
	 * Creates query for findings latest followings events for given user URI.
	 * 
	 * @param userURI
	 *            User URI of Person.
	 * @return Mysql query.
	 */
	private String createQuery(String userURI) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("select * from ");
		stringBuilder
				.append(FriendshipActivityLogConst.FRIENDSHIP_ACTIVITY_LOG_TABLE_NAME);
		stringBuilder.append(" where ");
		stringBuilder.append("UserUri='");
		stringBuilder.append(userURI);
		stringBuilder.append("'");
		stringBuilder.append("order by Time");
		String query = stringBuilder.toString();
		String msg = "created mysql query for user {\"uri\":\"\"}";
		getLogger().trace(String.format(msg, userURI));
		return query;
	}

	public JsonArray createReportObject(String userURI) {
		creatingReportLog(userURI);
		JsonArray followingsArray = extractsResultsFromDbToJsonArray(createQuery(userURI));
		createdReportLog(userURI, followingsArray);
		return followingsArray;

	}

	private void creatingReportLog(String userURI) {
		String msg = "creating report object {\"userUri\":\"%s\"}";
		getLogger().debug(String.format(msg, userURI));
	}

	private int extractActivity(ResultSet resultSet) throws SQLException {
		return resultSet.getInt("Activity");
	}

	/**
	 * Executes query and traverses result set. Add needed columns in set to
	 * json objects.
	 * 
	 * @param query
	 *            MYsql query to Execute.
	 * @return JsonArray which contains followings information logs.
	 */
	private JsonArray extractsResultsFromDbToJsonArray(String query) {
		String msg;
		JsonArray followingsArray = null;
		Connection createConnection = null;
		Statement statement = null;
		try {
			createConnection = RelationalDataLayer.getInstance()
					.createConnection();
			statement = createConnection.createStatement();
			ResultSet resultSet = statement.executeQuery(query);
			followingsArray = traverseResultSet(resultSet);
		} catch (Exception e) {
			msg = "cannot execute mysql query {\"query\":\"%s\"}";
			getLogger().error(String.format(msg, query));
		} finally {
			closeConnection(createConnection, statement);
		}
		return followingsArray;
	}

	private Timestamp extractTime(ResultSet resultSet) throws SQLException {
		return resultSet.getTimestamp("Time");
	}

	private String extractUserUri(ResultSet resultSet) throws SQLException {
		return resultSet.getString("FollowingUserUri");
	}

	/**
	 * Fills json object with results.
	 * 
	 * @param resultSet
	 *            result set of query.
	 * @return JsonObject of a following user.
	 * @throws SQLException
	 */
	private JsonObject fillJsonObjectWithResult(ResultSet resultSet)
			throws SQLException {
		JsonObject followingObject = new JsonObject();
		followingObject.addProperty("UserUri", extractUserUri(resultSet));
		int activity = extractActivity(resultSet);
		followingObject.addProperty("Activity", activity);
		if(activity == 1)
			numberOfFollowings++;
		else numberOfFollowings--;
		followingObject.addProperty("Followings", numberOfFollowings);
		Date time = extractTime(resultSet);
		followingObject.addProperty("Time", time.toString());
		followingObject.addProperty("TimeScale", ((double)(time.getTime() - startDate)/(endDate - startDate) * 100));
		return followingObject;
	}

	public Logger getLogger() {
		return logger;
	}

	/**
	 * Traverses result set.
	 * 
	 * @param resultSet
	 *            result of query.
	 * @return json array of user's followings objects.
	 * @throws SQLException
	 */
	private JsonArray traverseResultSet(ResultSet resultSet)
			throws SQLException {
		JsonArray followingsArray;
		followingsArray = new JsonArray();
		while (resultSet.next()) {
			followingsArray.add(fillJsonObjectWithResult(resultSet));
		}
		return followingsArray;
	}

};