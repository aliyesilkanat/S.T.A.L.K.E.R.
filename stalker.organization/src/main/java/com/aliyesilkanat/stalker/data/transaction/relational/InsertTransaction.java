package com.aliyesilkanat.stalker.data.transaction.relational;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class InsertTransaction extends Transaction{

	public InsertTransaction(Connection conn, DataSet dataSet) {
		super(conn, dataSet);
	}

	@Override
	protected void operation() throws SQLException {
		Statement statement = conn.createStatement();
		for(int i = 0; i < dataSet.size(); i++){
			String query = generateQuery(dataSet.get(i));
			statement.executeUpdate(query);
		}
		statement.close();
	}
	
	private String generateQuery(Data data){
		StringBuilder query = new StringBuilder();
		query.append("INSERT INTO " + dataSet.getRelationName());
		ArrayList<String> attributeNames = dataSet.getAttributeNames();
		query.append(" (");
		for(String attr : attributeNames)
			query.append(attr + ", ");
		query.replace(query.length() - 3, query.length() - 1, "");
		query.append(") VALUES (");
		for(String attr : attributeNames)
			query.append(data.getAttribute(attr).toString() + ", ");
		query.replace(query.length() - 3, query.length() - 1, "");
		query.append(");");
		return query.toString();
	}

}
