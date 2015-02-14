package com.aliyesilkanat.stalker.storer.friendshipactivity;

import java.sql.SQLException;
import java.sql.Statement;

import com.aliyesilkanat.stalker.storer.relational.RelationalOperation;

public class FriendshipActivityDeletion implements RelationalOperation {

	@Override
	public void execute(Statement statement, Object data) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public String createSQLQuery(String userUri, String tableName,
			int activityType) {
		// TODO Auto-generated method stub
		return null;
	}

}
