package com.aliyesilkanat.stalker.data.transaction.relational;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;

public abstract class Transaction {
	protected Connection conn;
	protected DataSet dataSet;
	public Transaction(Connection conn, DataSet dataSet) {
		this.conn = conn;
		this.dataSet = dataSet;
	}
	
	/**
	 * <strong><i>Template method</i></strong> that executes transaction. 
	 * <br>SQLExceptions thrown during transaction execution are handled, others are thrown
	 * @throws SQLException
	 */
		
	public final void execute() throws SQLException {
		Savepoint savePoint = conn.setSavepoint();
		conn.setAutoCommit(false);
		try{
			operation();
			conn.commit();
		} catch(SQLException e){
				conn.rollback(savePoint);
		} finally{
			if(conn != null)
				conn.close();
		}
	}
	
	protected abstract void operation() throws SQLException;
}
