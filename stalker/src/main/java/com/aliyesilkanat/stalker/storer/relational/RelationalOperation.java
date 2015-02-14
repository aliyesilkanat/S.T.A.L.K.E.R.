package com.aliyesilkanat.stalker.storer.relational;

import java.sql.SQLException;
import java.sql.Statement;

public interface RelationalOperation {

	void execute(Statement statement, Object data) throws SQLException;

	String createSQLQuery(String userUri, String tableName, int activityType);
}
