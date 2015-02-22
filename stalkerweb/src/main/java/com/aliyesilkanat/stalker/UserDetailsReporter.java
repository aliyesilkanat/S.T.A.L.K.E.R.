package com.aliyesilkanat.stalker;

import org.apache.log4j.Logger;

import com.aliyesilkanat.stalker.data.RDFDataLayer;
import com.aliyesilkanat.stalker.data.UnfinishedOperationException;
import com.google.gson.JsonObject;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

public class UserDetailsReporter {

	private final Logger logger = Logger.getLogger(getClass());

	public JsonObject getDetails(String userURI)
			throws UnfinishedOperationException {
		String msg = "getting user details {\"userURI\":\"%s\"}";
		getLogger().debug(String.format(msg, userURI));

		return getDetailsFromResult(userURI, RDFDataLayer.getInstance()
				.execSelect(createQuery(userURI)));

	}

	/**
	 * Extracts details from {@link ResultSet}.
	 * 
	 * @param userURI
	 *            Uri of the user whom requested details.
	 * @param execSelect
	 *            Query {@link ResultSet}.
	 * @return Json object which contains details of user.
	 */
	private JsonObject getDetailsFromResult(String userURI, ResultSet execSelect) {
		if (execSelect.hasNext()) {
			QuerySolution next = execSelect.next();
			JsonObject jsonObject = createJsonObject(userURI,
					extractUsersName(next), extractUsersImage(next));
			String msg = "found user details {\"userURI\":\"%s\", \"detailsObject\":\"%s\"}";
			getLogger().trace(String.format(msg, userURI, jsonObject));
			return jsonObject;
		} else {
			String msg = "not found user details {\"userURI\":\"%s\"}";
			getLogger().warn(String.format(msg, userURI));
			return null;
		}
	}

	private JsonObject createJsonObject(String userURI, String userName,
			String userImage) {
		JsonObject userObject = new JsonObject();
		userObject.addProperty("name", userName);
		userObject.addProperty("img", userImage);
		userObject.addProperty("uri", userURI);
		return userObject;
	}

	private String extractUsersImage(QuerySolution next) {
		String userImage = next.get("img").toString();
		return userImage;
	}

	private String extractUsersName(QuerySolution querySolution) {
		String userName = querySolution.get("name").toString();
		return userName;
	}

	/**
	 * Creates sparql query for getting details for given userURI.
	 * 
	 * @param userURI
	 *            Uri of the user.
	 * @return Sparql query as {@link String}.
	 */
	private String createQuery(String userURI) {
		return "PREFIX schema: <http://schema.org/> select distinct * where { "
				+ "<" + userURI + "> " + "schema:name ?name. " + "<" + userURI
				+ "> " + "schema:image ?img}";
	}

	public Logger getLogger() {
		return logger;
	}

}
