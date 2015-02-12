package com.aliyesilkanat.stalker.storer;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import virtuoso.jena.driver.VirtGraph;

import com.aliyesilkanat.stalker.util.XSDDateTimeUtil;
import com.hp.hpl.jena.datatypes.xsd.XSDDateTime;
import com.hp.hpl.jena.graph.GraphUtil;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

public abstract class Storer {

	/**
	 * JsonLd of followings.
	 */
	private String content;
	/**
	 * User uri of person.
	 */
	private String userURI;

	public Storer(String content, String userURI) {
		this.setContent(content);
		this.setUserURI(userURI);
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

	public String getUserURI() {
		return userURI;
	}

	public void setUserURI(String userURI) {
		this.userURI = userURI;
	}


}
