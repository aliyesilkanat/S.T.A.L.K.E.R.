package com.aliyesilkanat.stalker.storer.friendshipactivity;

import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

import com.aliyesilkanat.stalker.data.constants.FriendshipActivityLogConst;
import com.aliyesilkanat.stalker.storer.relational.RelationalOperation;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class FriendshipActivityAddition implements RelationalOperation {
	private final Logger logger = Logger.getLogger(getClass());

	/**
	 * Insert new following additions to mysql.
	 * 
	 * @param statement
	 *            Sql {@link Statement} for executing query.
	 * @param addedNewFollowings
	 *            added new followings as {@link JsonArray}.
	 * @throws SQLException
	 */
	@Override
	public void execute(Statement statement, Object data) throws SQLException {
		JsonArray addedNewFollowings = (JsonArray) data;
		for (JsonElement jsonElement : addedNewFollowings) {
			String query = null;
			try {
				// get user uri from json ld object...
				JsonObject userObject = jsonElement.getAsJsonObject();
				String userUri = userObject.get("@id").getAsString();

				// create sql insert query
				query = createSQLQuery(
						userUri,
						FriendshipActivityLogConst.FRIENDSHIP_ACTIVITY_LOG_TABLE_NAME,
						FriendshipActivityLogConst.ACTIVITY_NEW_FOLLOWING_ADDITION);

				// inserting log
				String msg = "inserting new addition {\"userUri\":\"%s\", \"sqlQuery\":\"%s\"}";
				getLogger().info(String.format(msg, userUri, query));

				statement.execute(query);

				// inserted log
				msg = "inserted new addition {\"userUri\":\"%s\", \"sqlQuery\":\"%s\"}";
				getLogger().info(String.format(msg, userUri, query));
			} catch (Exception e) {
				String msg = "cannot insert to mysql {\"query\":\"%s\"}";
				getLogger().error(String.format(msg, query), e);
			}
		}
	}

	public String createSQLQuery(String userUri, String tableName,
			int activityType) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder
				.append("insert into FriendshipActivityLog (UserUri,Activity) values (");
		stringBuilder.append("'");
		stringBuilder.append(userUri);
		stringBuilder.append("'");
		stringBuilder.append(",");
		stringBuilder.append(activityType);
		stringBuilder.append(")");
		String sql = stringBuilder.toString();
		return sql;
	}

	public Logger getLogger() {
		return logger;
	}

}
