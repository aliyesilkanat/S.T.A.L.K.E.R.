package com.aliyesilkanat.stalker.data.connector;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import virtuoso.jena.driver.VirtGraph;

import com.aliyesilkanat.stalker.data.UnfinishedOperationException;
import com.aliyesilkanat.stalker.util.DBUtils;
import com.aliyesilkanat.stalker.util.XSDDateTimeUtil;
import com.hp.hpl.jena.datatypes.xsd.XSDDateTime;
import com.hp.hpl.jena.graph.GraphUtil;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.ResultSet;
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
	 * @throws UnfinishedOperationException
	 */
	public void writeModel2Virtuoso(Model model, String graph)
			throws UnfinishedOperationException {

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
			throw new UnfinishedOperationException();
		}
	}

	public ResultSet execSelect(String query)
			throws UnfinishedOperationException {
		String msg;
		ResultSet selectFromEndpoint = null;
		try {
			selectFromEndpoint = DBUtils.selectFromEndpoint(query);

		} catch (Exception e) {
			msg = "error while executing query on endpoint {\"query\":\"%s\"}";
			getLogger().error(String.format(msg, query), e);
			throw new UnfinishedOperationException();
		}

		return selectFromEndpoint;
	}

	public VirtGraph createVirtGraph(String graphURI)
			throws UnfinishedOperationException {
		VirtGraph graph = null;
		try {
			graph = new VirtGraph(graphURI, VIRTUOSO_JDBC_CONNECTION,
					VIRTUOSO_USER_NAME, VIRTUOSO_USER_PASSWORD);
		} catch (Exception e) {
			String msg = "error while creating virtuoso graph {\"graphUri\":\"%s\"}";
			getLogger().error(String.format(msg, graphURI), e);
			throw new UnfinishedOperationException();
		}
		return graph;
	}

	public Logger getLogger() {
		return logger;
	}

}
