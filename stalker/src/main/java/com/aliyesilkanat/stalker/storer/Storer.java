package com.aliyesilkanat.stalker.storer;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.datatypes.xsd.XSDDateTime;
import com.hp.hpl.jena.graph.GraphUtil;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

public abstract class Storer {
	private String content;

	public Storer(String content) {
		this.setContent(content);
	}

	private final Logger logger = Logger.getLogger(getClass());

	public Logger getLogger() {
		return logger;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * Writes given model to virtuoso.
	 * 
	 * @param model
	 *            given {@link Model} instance.
	 * @param virtGraph
	 *            graph uri to store given model on it.s
	 */
	// protected void writeModel2Virtuoso(Model model) {
	// try {
	// // log..
	// getLogger().info(
	// String.format("Storing into \"%s\" graph, model \"%s\"",
	// virtGraph.getGraphName(), model));
	// // change data and times in model..
	// StmtIterator statements = model.listStatements();
	// ArrayList<Triple> triples = new ArrayList<Triple>();
	// while (statements.hasNext()) {
	// Statement statement = (Statement) statements.next();
	// // if statent contains a time convert it to xsd:dateTime.
	// String timeProperty = statement.getPredicate().getURI();
	// if (timeProperty.contains("commentTime")
	// || timeProperty.contains("datePublished")
	// || timeProperty
	// .contains(AISHub.LAST_UPDATE_TIME_PROP_NAME)) {
	// // Create xsd:dateTime..
	// XSDDateTime xsdDateTime = XSDDateTimeUtil
	// .convert2XsdDate(statement.getObject().toString());
	// triples.add(ResourceFactory.createStatement(
	// statement.getSubject(), statement.getPredicate(),
	// ResourceFactory.createTypedLiteral(xsdDateTime))
	// .asTriple());
	// } else {
	// // add to triple list..
	// triples.add(statement.asTriple());
	// }
	// }
	// // add to virtuoso..
	// GraphUtil.add(virtGraph, triples);
	// if (getLogger().isDebugEnabled()) {
	// // log..
	// getLogger().debug(
	// String.format("Stored model into \"%s\" graph.",
	// virtGraph.getGraphName()));
	// }
	// } catch (Exception e) {
	// // log..
	// getLogger().error(
	// "Occured exception while writing model to virtuoso: ", e);
	// }
	// }

}
