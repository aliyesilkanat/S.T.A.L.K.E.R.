package com.aliyesilkanat.stalker.storer.friendshipactivity;

import java.sql.SQLException;
import java.sql.Statement;

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
	 * Operation name which represents which domain are we working on e.g.
	 * FriendshipActivityLogging
	 */
	private String operationName;
	/**
	 * Which sql table would have been working on.
	 */
	private String tableName;
	/**
	 * Activity constant for followings deletion or addition.
	 */
	private int activity;
	/**
	 * Relational operation context interface
	 */
	private FriendshipActivityAction operation;
	/**
	 * Data which will be inserted or update if suitable activity passed.
	 */
	private Object data;
	/**
	 * User uri which is represents user.
	 */
	private String userUri;

	public FriendshipActivityMysqlOperation(String operationName,
			String tableName, int activity, Object data, String userUri) {
		this.setOperationName(operationName);
		this.setTableName(tableName);
		this.setActivity(activity);
		this.operation = new FriendshipActivityAction(activity);
		this.data = data;
		this.setUserUri(userUri);
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
		operation.execute(statement, data, getUserUri());
	}

	public String getUserUri() {
		return userUri;
	}

	public void setUserUri(String userUri) {
		this.userUri = userUri;
	}
}
