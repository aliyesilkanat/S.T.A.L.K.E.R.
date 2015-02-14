package com.aliyesilkanat.stalker.storer.friendshipactivity;

import java.sql.SQLException;
import java.sql.Statement;

import com.aliyesilkanat.stalker.storer.relational.RelationalOperation;
import com.aliyesilkanat.stalker.storer.relational.RelationalOperationContext;

/**
 * Processes mysql opereations.
 * 
 * @author Ali Yesilkanat
 *
 */
public class FriendshipActivityMysqlOperation extends
		RelationalOperationContext {

	/**
	 * Operation name which
	 */
	private String operationName;
	private String tableName;
	private int activity;
	private RelationalOperation operation;
	private Object data;

	public FriendshipActivityMysqlOperation(String operationName,
			String tableName, int activity, Object data) {
		this.setOperationName(operationName);
		this.setTableName(tableName);
		this.setActivity(activity);
		this.operation = new FriendshipActivityContext(activity).getContext();
		this.data = data;
	}

	public String getOperationName() {
		return operationName;
	}

	public void setOperationName(String operationName) {
		this.operationName = operationName;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public int getActivity() {
		return activity;
	}

	public void setActivity(int activity) {
		this.activity = activity;
	}

	@Override
	public void executeOperation(Statement statement) throws SQLException {
		operation.execute(statement, data);
	}
}
