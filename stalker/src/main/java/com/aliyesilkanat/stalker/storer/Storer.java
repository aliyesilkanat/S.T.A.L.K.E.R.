package com.aliyesilkanat.stalker.storer;

import org.apache.log4j.Logger;

public abstract class Storer {
	
	/**
	 * JsonLd of followings.
	 */
	private String content;
	/**
	 * User id of person.
	 */
	private String userId;

	public Storer(String content, String userId) {
		this.setContent(content);
		this.setUserId(userId);
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

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
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
