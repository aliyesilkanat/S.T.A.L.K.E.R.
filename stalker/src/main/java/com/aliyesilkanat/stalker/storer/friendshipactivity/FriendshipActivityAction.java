package com.aliyesilkanat.stalker.storer.friendshipactivity;

import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

import com.aliyesilkanat.stalker.data.constants.FriendshipActivityLogConst;
import com.aliyesilkanat.stalker.storer.relational.RelationalOperation;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * @author Ali Yesilkanat
 *
 */
public class FriendshipActivityAction implements RelationalOperation {
	private final Logger logger = Logger.getLogger(getClass());

	/**
	 * Friendship Activity for representing following addition or deletion e.g.
	 * {@link FriendshipActivityLogConst#ACTIVITY_NEW_FOLLOWING_ADDITION}
	 */
	private int friendShipActivity;

	public FriendshipActivityAction(int activity) {
		this.friendShipActivity = activity;
	}

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
	public void execute(Statement statement, Object data, String userUri)
			throws SQLException {
		JsonArray addedNewFollowings = (JsonArray) data;
		for (JsonElement jsonElement : addedNewFollowings) {
			String query = null;
			try {
				String followingUserUri = getUserUri(jsonElement,
						getFriendShipActivity());

				// create sql insert query
				query = createSQLQuery(
						userUri,
						followingUserUri,
						FriendshipActivityLogConst.FRIENDSHIP_ACTIVITY_LOG_TABLE_NAME,
						getFriendShipActivity());

				// inserting log
				if (getLogger().isDebugEnabled()) {
					String msg = "inserting new addition {\"userUri\":\"%s\", \"sqlQuery\":\"%s\"}";
					getLogger().debug(String.format(msg, userUri, query));
				}
				executeQuery(statement, query);

				// inserted log
				if (getLogger().isTraceEnabled()) {
					String msg = "inserted new addition {\"userUri\":\"%s\", \"sqlQuery\":\"%s\"}";
					getLogger().trace(String.format(msg, userUri, query));
				}
			} catch (Exception e) {
				String msg = "cannot insert to mysql {\"query\":\"%s\"}";
				getLogger().error(String.format(msg, query), e);
			}
		}
	}

	private String getUserUri(JsonElement jsonElement, int activity)
			throws UnknownFriendShipActivityException {
		String followingUserUri = null;
		if (activity == FriendshipActivityLogConst.ACTIVITY_NEW_FOLLOWING_ADDITION) {
			// get user uri from json ld object...
			JsonObject userObject = jsonElement.getAsJsonObject();
			followingUserUri = userObject.get("@id").getAsString();
		} else if (activity == FriendshipActivityLogConst.ACTIVITY_NEW_FOLLOWING_DELETION) {
			followingUserUri = jsonElement.getAsString();
		} else {
			throw new UnknownFriendShipActivityException();
		}
		return followingUserUri;
	}

	public void executeQuery(Statement statement, String query)
			throws SQLException {
		statement.execute(query);
	}

	public String createSQLQuery(String userUri, String followingUserUri,
			String tableName, int activityType) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder
				.append("insert into FriendshipActivityLog (UserUri,FollowingUserUri,Activity) values (");
		stringBuilder.append("'");
		stringBuilder.append(userUri);
		stringBuilder.append("'");
		stringBuilder.append(",'");
		stringBuilder.append(followingUserUri);
		stringBuilder.append("',");
		stringBuilder.append(activityType);
		stringBuilder.append(")");
		String sql = stringBuilder.toString();
		return sql;
	}

	public Logger getLogger() {
		return logger;
	}

	public int getFriendShipActivity() {
		return friendShipActivity;
	}

	public void setFriendShipActivity(int friendShipActivity) {
		this.friendShipActivity = friendShipActivity;
	}

}
