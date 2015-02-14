package com.aliyesilkanat.stalker.storer.relational;

import java.sql.SQLException;
import java.sql.Statement;

public interface RelationalOperation {

	public void execute(Statement statement, Object data, String userUri)
			throws SQLException;

	public String createSQLQuery(String userUri, String followingUserUri,
			String tableName, int activityType);
}
