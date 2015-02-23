package com.aliyesilkanat.stalker.friendshipactivity;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

import com.aliyesilkanat.stalker.data.RelationalDataLayer;
import com.aliyesilkanat.stalker.data.UnfinishedOperationException;
import com.aliyesilkanat.stalker.data.constants.FriendshipActivityLogConst;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class FriendshipActivityReporter {

	private final Logger logger = Logger.getLogger(getClass());

	private static final int numberOfRecentActivities = 5;
	private long startDate;
	private long endDate;
	private int numberOfFollowings;
	private Connection conn;
	
	public FriendshipActivityReporter(long startDate, long endDate) throws UnfinishedOperationException {
		this.startDate = startDate;
		this.endDate = endDate;
		numberOfFollowings = 0;
		try {
			conn = RelationalDataLayer.getInstance()
					.createConnection();
		} catch (UnfinishedOperationException e) {
			getLogger().error("Error occured while creating MySQL connection");
			throw e;
		}
	}

	private void closeConnection() {
		getLogger().debug("closing mysql connection");
		if (conn != null) {
			try {
				conn.close();
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
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
		String startDateAsString = formatter.format(new Date(startDate));
		formatter.applyPattern("yyyy-MM-dd 23:59:59");
		String endDateAsString = formatter.format(new Date(endDate));
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("select * from ");
		stringBuilder
				.append(FriendshipActivityLogConst.FRIENDSHIP_ACTIVITY_LOG_TABLE_NAME);
		stringBuilder.append(" where ");
		stringBuilder.append("UserUri='");
		stringBuilder.append(userURI);
		stringBuilder.append("'");
		stringBuilder.append(" and Time > '" + startDateAsString + "'");
		stringBuilder.append(" and Time < '" + endDateAsString + "' ");
		stringBuilder.append("order by Time asc");
		String query = stringBuilder.toString();
		String msg = "created mysql query for user {\"uri\":\"\"}";
		getLogger().trace(String.format(msg, userURI));
		return query;
	}

	public JsonObject createReportObject(String userURI) {
		creatingReportLog(userURI);
		
		JsonObject resultObject = new JsonObject();
		JsonArray followingsArray = extractsResultsFromDbToJsonArray(createQuery(userURI));
		
		createdReportLog(userURI, followingsArray);
		
		resultObject.add("followings", followingsArray);
		
		JsonArray recentFollowings = getRecentActivitiesFromDb(userURI, 1);
		
		resultObject.add("recentFollowings", recentFollowings);
		
		JsonArray recentUnfollowings = getRecentActivitiesFromDb(userURI, 0);
		
		resultObject.add("recentUnfollowings", recentUnfollowings);
		
		closeConnection();
		return resultObject;

	}

	private JsonArray getRecentActivitiesFromDb(String userURI, int activity) {
		JsonArray array = new JsonArray();
		StringBuilder query = new StringBuilder();
		query.append("SELECT FollowingUserUri, Time FROM ");
		query
			.append(FriendshipActivityLogConst.FRIENDSHIP_ACTIVITY_LOG_TABLE_NAME);
		query.append(" WHERE ");
		query.append("UserUri='");
		query.append(userURI);
		query.append("'");
		query.append(" AND Activity = " + activity);
		query.append(" ORDER BY Time DESC ");
		query.append("LIMIT " + numberOfRecentActivities);
		
		ResultSet results = null;
		
		try {
			Statement statement = conn.createStatement();
			results = statement.executeQuery(query.toString());
			while(results.next()){
				JsonObject obj = new JsonObject();
				obj.addProperty("userURI", results.getString(1));
				obj.addProperty("time", Long.toString(results.getDate(2).getTime()));
				array.add(obj);
			}
		} catch (SQLException e) {
			getLogger().error(String.format("Error while executing query {\"query\":\"%s\", \"error\":\"%s\"}", query.toString(), e.getMessage()));
		} finally {
			if(results != null)
				try {
					results.close();
				} catch (SQLException e) {
				}
		}
		return array;
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
		Statement statement = null;
		try {
			statement = conn.createStatement();
			ResultSet resultSet = statement.executeQuery(query);
			followingsArray = traverseResultSet(resultSet);
		} catch (Exception e) {
			msg = "cannot execute mysql query {\"query\":\"%s\"}";
			getLogger().error(String.format(msg, query));
		} finally {
			if(statement != null)
				try {
					statement.close();
				} catch (SQLException e) {
				}
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