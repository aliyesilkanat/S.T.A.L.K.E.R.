package com.aliyesilkanat.stalker.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

/**
 * @author Pinar Gocebe.
 * 
 *         This component used to create Model utilities. For example resource,
 *         property, literal and statement creation.
 * 
 */
public class ModelUtils {

	private static Logger logger = Logger.getLogger(ModelUtils.class);

	public static String HTTP_BASE = "http";

	/**
	 * Create {@link Resource} instance with given uri.
	 * 
	 * @param uri
	 *            given resource uri.
	 * @return created {@link Resource} instance.
	 */
	public static Resource createResource(String uri) {
		return ResourceFactory.createResource(uri);
	}

	/**
	 * Create {@link Property} instance with given property uri.
	 * 
	 * @param uri
	 *            given property uri.
	 * @return created {@link Property} instance.
	 */
	public static Property createProperty(String uri) {
		return ResourceFactory.createProperty(uri);
	}

	/**
	 * Create {@link RDFNode} instance with given literal.
	 * 
	 * @param literal
	 *            given literal.
	 * @return created {@link RDFNode} instance.
	 */
	public static RDFNode createLiteral(String literal) {
		return ResourceFactory.createPlainLiteral(literal);
	}

	/**
	 * Reads givem model string to a {@link Model} instance.
	 * 
	 * @param content
	 *            given model string.
	 * @return created {@link Model} instacne.
	 */
	public static Model readString2Model(String content) {
		// create model..
		Model model = ModelFactory.createDefaultModel();
		try {
			// read content to a model..
			ByteArrayInputStream is = new ByteArrayInputStream(
					content.getBytes());
			model.read(is, "");
		} catch (Exception e) {
			String msg = "Occured exception while reading string \"%s\" into model.";
			logger.error(String.format(msg, content), e);
		}
		return model;
	}

	/**
	 * Writes given {@link Model} instance to a {@link String}.
	 * 
	 * @param model
	 *            givem {@link Model} instance.
	 * @return created {@link String}.
	 */
	public static String writeModel2String(Model model) {
		String results = null;
		try {
			StringWriter stringWriter = new StringWriter();
			model.write(stringWriter);
			results = stringWriter.toString();
			stringWriter.flush();
			stringWriter.close();
		} catch (IOException e) {
			String msg = "occured an error while writing model: {\"message\":\"%s\"}";
			getLogger().error(String.format(msg, e.getMessage()));
		}
		return results;
	}

	public static Logger getLogger() {
		return logger;
	}

	public static final String N_TRIPLE = "N-TRIPLE";

}
