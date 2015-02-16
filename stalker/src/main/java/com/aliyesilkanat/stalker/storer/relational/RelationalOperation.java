package com.aliyesilkanat.stalker.storer.relational;

import java.sql.Statement;

import com.aliyesilkanat.stalker.data.UnfinishedOperationException;

public interface RelationalOperation {

	public void execute(Statement statement, Object data, String userUri)
			throws UnfinishedOperationException;

	public String createSQLQuery(String userUri, String followingUserUri,
			String tableName, int activityType);
}
