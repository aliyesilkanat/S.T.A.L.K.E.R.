package com.aliyesilkanat.stalker.data.transaction.relational;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Iterator;

public class InsertTransaction extends Transaction{

	public InsertTransaction(Connection conn, DataSet dataSet) {
		super(conn, dataSet);
	}

	@Override
	protected void operation() throws SQLException {
		String query = generateQuery();
		PreparedStatement statement = null;
		for(int i = 0; i < dataSet.size(); i++){
			statement = prepareStatement(query, dataSet.get(i));
			statement.execute();
		}
		statement.close();
	}
	
	private PreparedStatement prepareStatement(String query, Data data) throws SQLException{
		PreparedStatement statement = conn.prepareStatement(query);
		
		Iterator<String> it = dataSet.getAttributeNames().iterator();
		int i = 1;
		while(it.hasNext()){
			statement.setObject(i, data.getAttribute(it.next()));
			i++;
		}

		return statement;
	}
	
	private String generateQuery(){
		StringBuilder query = new StringBuilder();
		query.append("INSERT INTO " + dataSet.getRelationName() + "(");
		for(String attributeName : dataSet.getAttributeNames())
			query.append(attributeName + ", ");
		query.setLength(query.length() - 2);
		query.append(") VALUES(");
		
		for(int i = 0; i < dataSet.getAttributeNames().size(); i++)
			query.append("?, ");
		query.setLength(query.length() - 2);
		query.append(");");
		
		getLogger().info(String.format("Generated query: {\"query\" : \"%s\"}", query.toString()));
		System.out.println(String.format("Generated query: {\"query\" : \"%s\"}", query.toString()));
		return query.toString();
	}
}
