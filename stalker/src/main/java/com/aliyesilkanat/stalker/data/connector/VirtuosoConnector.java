package com.aliyesilkanat.stalker.data.connector;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import virtuoso.jena.driver.VirtGraph;

import com.aliyesilkanat.stalker.storer.DBUtils;
import com.aliyesilkanat.stalker.storer.GraphConstants;
import com.aliyesilkanat.stalker.util.XSDDateTimeUtil;
import com.hp.hpl.jena.datatypes.xsd.XSDDateTime;
import com.hp.hpl.jena.graph.GraphUtil;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

public class VirtuosoConnector {
	private static final String VIRTUOSO_JDBC_CONNECTION = "jdbc:virtuoso://54.213.0.71:1111";
	private static final String VIRTUOSO_USER_NAME = "dba";
	private static final String VIRTUOSO_USER_PASSWORD = "dba";
	private final Logger logger = Logger.getLogger(getClass());

	/**
	 * Writes given model to virtuoso.
	 * 
	 * @param model
	 *            given {@link Model} instance.
	 * @param virtGraph
	 *            graph uri to store given model on it.s
	 */
	public void writeModel2Virtuoso(Model model, String graph) {

		try {

			VirtGraph virtGraph = createVirtGraph(graph);
			// log..
			getLogger().info(
					String.format("Storing into \"%s\" graph, model \"%s\"",
							virtGraph.getGraphName(), model));
			// change data and times in model..
			StmtIterator statements = model.listStatements();
			ArrayList<Triple> triples = new ArrayList<Triple>();
			while (statements.hasNext()) {
				Statement statement = (Statement) statements.next();
				// if statent contains a time convert it to xsd:dateTime.
				String timeProperty = statement.getPredicate().getURI();
				if (timeProperty.contains("commentTime")
						|| timeProperty.contains("datePublished")) {
					// Create xsd:dateTime..
					XSDDateTime xsdDateTime = XSDDateTimeUtil
							.convert2XsdDate(statement.getObject().toString());
					triples.add(ResourceFactory.createStatement(
							statement.getSubject(), statement.getPredicate(),
							ResourceFactory.createTypedLiteral(xsdDateTime))
							.asTriple());
				} else {
					// add to triple list..
					triples.add(statement.asTriple());
				}
			}
			// add to virtuoso..
			GraphUtil.add(virtGraph, triples);
			if (getLogger().isDebugEnabled()) {
				// log..
				getLogger().debug(
						String.format("Stored model into \"%s\" graph.",
								virtGraph.getGraphName()));
			}
		} catch (Exception e) {
			// log..
			getLogger().error(
					"Occured exception while writing model to virtuoso: ", e);
		}
	}

	public ResultSet execSelect(String query) {
		String msg;
		ResultSet selectFromEndpoint = null;
		try {
			selectFromEndpoint = DBUtils.selectFromEndpoint(query);

		} catch (Exception e) {
			msg = "error while executing query on endpoint {\"query\":\"%s\"}";
			getLogger().error(String.format(msg, query), e);
		}

		return selectFromEndpoint;
	}

	public VirtGraph createVirtGraph(String graphURI) {
		return new VirtGraph(graphURI, VIRTUOSO_JDBC_CONNECTION,
				VIRTUOSO_USER_NAME, VIRTUOSO_USER_PASSWORD);
	}

	public Logger getLogger() {
		return logger;
	}

}
