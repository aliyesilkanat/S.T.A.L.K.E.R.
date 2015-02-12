package com.aliyesilkanat.stalker.tracker.instagram;

import java.util.Arrays;
import java.util.List;

import com.aliyesilkanat.stalker.data.RDFDataLayer;
import com.aliyesilkanat.stalker.endpoint.EndpointUtils;
import com.aliyesilkanat.stalker.retriever.Retriever;
import com.aliyesilkanat.stalker.tracker.Tracker;
import com.aliyesilkanat.stalker.util.wrapper.InstagramApiJsonWrapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

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
		ResultSet execSelect = RDFDataLayer.getInstance().execSelect(query);
		List<InstagramApiJsonWrapper> followingsList = getFollowingsAsList();
		if (execSelect != null) {
			while (execSelect.hasNext()) {
				QuerySolution solution = execSelect.next();
				String userUri = solution.get("p").asLiteral().getString();
				for (InstagramApiJsonWrapper instagramApiJsonWrapper : followingsList) {
					if (userUri.contains(instagramApiJsonWrapper.getUsername())) {
						followingsList.remove(instagramApiJsonWrapper);
						break;
					}
				}

			}
		}
	}

	private List<InstagramApiJsonWrapper> getFollowingsAsList() {
		InstagramApiJsonWrapper[] instagramArray = new Gson().fromJson(
				getResponse(), InstagramApiJsonWrapper[].class);
		List<InstagramApiJsonWrapper> asList = Arrays.asList(instagramArray);
		return asList;
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
