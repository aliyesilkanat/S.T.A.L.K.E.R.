package com.aliyesilkanat.stalker.util;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;

public class DBUtils {
	private static final String VIRTUOSO_ENDPOINT_URI = "http://54.213.0.71:8890/sparql";

	public static ResultSet selectFromEndpoint(String queryString)
			throws Exception {
		Query query = QueryFactory.create(queryString);

		QueryExecution queryExec = QueryExecutionFactory.sparqlService(
				VIRTUOSO_ENDPOINT_URI, query);
		ResultSet resultSet = queryExec.execSelect();
		return resultSet;
	}
}
