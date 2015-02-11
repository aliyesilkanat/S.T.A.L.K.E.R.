package com.aliyesilkanat.stalker.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;

import com.github.jsonldjava.core.JsonLdProcessor;
import com.github.jsonldjava.core.JsonLdTripleCallback;
import com.github.jsonldjava.core.RDFDataset;
import com.github.jsonldjava.core.RDFDatasetUtils;
import com.github.jsonldjava.utils.JsonUtils;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.FileUtils;

public class JsonLDUtils {
	private static Logger logger = Logger.getLogger(JsonLDUtils.class);

	/**
	 * Gets RDF {@link Model} from given Json-LD string.
	 * 
	 * @param jsonLD
	 *            given Json-LD string.
	 * @return created RDF {@link Model} instance.
	 */
	public static Object getRdf(String jsonLD) {
		Object expectedRdfStr = null;
		try {
			// log..
			logger.trace(String.format("Getting rdf from jsonld: \"%s\"",
					jsonLD));
			// get rdf..
			Object jsonLdObj = JsonUtils.fromString(jsonLD);
			expectedRdfStr = JsonLdProcessor.toRDF(jsonLdObj,
					new JsonLdTripleCallback() {
						public Object call(RDFDataset dataset) {
							String nQuads = RDFDatasetUtils.toNQuads(dataset);
							return nQuads;
						}
					});
			// log..
			logger.debug(String.format("Got rdf from jsonld: \"%s\".",
					expectedRdfStr.toString()));
		} catch (Exception e) {
			logger.error("Occured exception while getting rdf from json-ld.", e);
		}
		return expectedRdfStr;
	}

	protected String read(InputStream input) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		byte[] buf = new byte[8 * 1024 * 1024];
		while (true) {
			int n = input.read(buf);
			if (n < 0)
				break;
			baos.write(buf, 0, n);
		}

		String json = baos.toString("UTF-8");
		baos.close();
		return json;
	}

	/**
	 * Converts given Json-LD string to {@link Model} instance.
	 * 
	 * @param modelStr
	 *            given Json-LD str.
	 * @return create {@link Model}
	 */
	public static Model convert2Model(String modelStr) {
		// create model..
		Model model = ModelFactory.createDefaultModel();
		// get json ld content as rdf..
		Object rdf = getRdf(modelStr);
		// log..
		logger.trace(String.format("Converting nquads to model: \"%s\"",
				rdf.toString()));
		// read to model..
		InputStream is = new ByteArrayInputStream(rdf.toString().getBytes());
		model.read(is, "", FileUtils.langNTriple);
		// log..
		logger.debug("Converted nquads to model.");
		return model;
	}
}
