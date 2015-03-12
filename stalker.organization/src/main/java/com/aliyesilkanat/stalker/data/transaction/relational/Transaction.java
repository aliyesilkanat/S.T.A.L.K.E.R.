package com.aliyesilkanat.stalker.data.transaction.relational;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.log4j.Logger;

public abstract class Transaction {
	protected Connection conn;
	protected DataSet dataSet;
	private final Logger logger = Logger.getLogger(this.getClass());
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
		conn.setAutoCommit(false);
		try{
			operation();
			conn.commit();
		} catch(SQLException e){
				System.err.println(e.getMessage());
				conn.rollback();
		} finally{
			if(conn != null)
				conn.close();
		}
	}
	
	public final Logger getLogger(){
		return logger;
	}
	
	protected abstract void operation() throws SQLException;
}
