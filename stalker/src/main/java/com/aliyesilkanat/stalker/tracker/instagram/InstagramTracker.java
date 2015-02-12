package com.aliyesilkanat.stalker.tracker.instagram;

import java.io.ByteArrayOutputStream;

import com.aliyesilkanat.stalker.endpoint.EndpointUtils;
import com.aliyesilkanat.stalker.retriever.Retriever;
import com.aliyesilkanat.stalker.storer.DBUtils;
import com.aliyesilkanat.stalker.tracker.Tracker;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;

public class InstagramTracker extends Tracker {

	public InstagramTracker(String response, String userId) {
		super(response, userId);
	}

	@Override
	public void catchChange() {
		String msg = "catching change {\"user id\":\"%s\", \"follows\":\"%s\"}";
		getLogger().info(String.format(msg, getResponse(), getUserId()));
		String userURI = retrieveUserURI();
		String query = setFollowingsQuery(userURI);
		ResultSet execSelect = execSelect(query);
		if (execSelect != null) {
			while (execSelect.hasNext()) {
				QuerySolution solution = execSelect.next();
			}
		}
	}

	private ResultSet execSelect(String query) {
		String msg;
		ResultSet selectFromEndpoint = null;
		try {
			selectFromEndpoint = DBUtils.selectFromEndpoint(query);
			if (selectFromEndpoint != null) {
				// convert resultset to json..
				ByteArrayOutputStream b = new ByteArrayOutputStream();
				ResultSetFormatter.outputAsJSON(b, selectFromEndpoint);
				String json = b.toString("UTF-8");
				msg = "query results {\"results\":\"%s\"}";
				getLogger().debug(String.format(msg, json));
			}
		} catch (Exception e) {
			msg = "error while executing query on endpoint {\"query\":\"%s\"}";
			getLogger().error(String.format(msg, query), e);
		}
		return selectFromEndpoint;
	}

	private String setFollowingsQuery(String userURI) {
		return "PREFIX schema: <http://schema.org/> Select ?p where {<"
				+ userURI + "> schema:follows ?p } ";
	}

	private String retrieveUserURI() {
		String msg = "retrieving uri for user {\"userId\":\"%s\"}";
		getLogger().debug(String.format(msg, getUserId()));
		String requestDocument = new Retriever().requestDocument(EndpointUtils
				.getInstance().getPersonDetails(getUserId()));

		String userName = "";

		JsonObject personDetailObject = new Gson().fromJson(requestDocument,
				JsonObject.class);
		if (personDetailObject.has("data")) {
			JsonObject dataObject = personDetailObject.get("data")
					.getAsJsonObject();
			if (dataObject.has("username")) {
				userName = "http://instagram.com/";
				userName += dataObject.get("username").getAsString();
			}
		}
		return userName;
	}
}
