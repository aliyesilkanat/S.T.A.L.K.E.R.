package com.aliyesilkanat.stalker.storer.relational;

import java.sql.Connection;
import java.sql.Statement;

import org.apache.log4j.Logger;

import com.aliyesilkanat.stalker.data.RelationalDataLayer;
import com.aliyesilkanat.stalker.data.UnfinishedOperationException;

public abstract class RelationalOperationContext {
	private final Logger logger = Logger.getLogger(getClass());

	/**
	 * Wrapper method for {@link RelationalOperationContext#executeOperation()}
	 * Allows us to control connections to database.
	 */
	public void execute() {

		Connection connection = null;
		Statement statement = null;
		try {
			connection = RelationalDataLayer.getInstance().createConnection();
			statement = connection.createStatement();
			executeOperation(statement);
		} catch (Exception e) {
			getLogger().error("error while executing mysql operation", e);
		} finally {
			try {
				statement.close();
				connection.close();
			} catch (Exception e) {
				getLogger().error("error while closing mysql connection", e);
			}
		}

	}

	public abstract void executeOperation(Statement statement)
			throws UnfinishedOperationException;

	public Logger getLogger() {
		return logger;
	}
}
