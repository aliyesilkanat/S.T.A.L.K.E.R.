package com.aliyesilkanat.stalker.tracker.instagram;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.aliyesilkanat.stalker.data.RDFDataLayer;
import com.aliyesilkanat.stalker.endpoint.EndpointUtils;
import com.aliyesilkanat.stalker.extractor.instagram.InstagramExtractor;
import com.aliyesilkanat.stalker.retriever.Retriever;
import com.aliyesilkanat.stalker.storer.instagram.InstagramStorer;
import com.aliyesilkanat.stalker.tracker.Tracker;
import com.aliyesilkanat.stalker.util.wrapper.InstagramApiJsonWrapper;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

public class InstagramTracker extends Tracker {

	/**
	 * UserUri which extracted by using user id via instagram api.
	 */
	private String userURI;

	public InstagramTracker(String fetcherResponse, String userId) {
		super(fetcherResponse, userId);

	}

	@Override
	public void catchChange() {
		// setting user uri using user ID via Instagram API
		setUserURI(retrieveUserURI());

		String msg = "catching change {\"userURI\":\"%s\", \"follows\":\"%s\"}";
		getLogger().info(String.format(msg, getUserURI(), getResponse()));

		List<InstagramApiJsonWrapper> followingsList = getFollowingsAsList();
		List<String> traverseRDFStore = null;
		ResultSet execSelect = getFollowingsFromRDFStore();
		if (execSelect != null) {
			traverseRDFStore = traverseRDFStore(execSelect, followingsList);
		}
		String deletedFollowingsJsonArrayString = convertStringListToJsonArray(
				traverseRDFStore).toString();

		JsonArray addedFollowings = convertFollowingsListIntoJsonArray(followingsList);

		addedFollowingsLog(addedFollowings);
		deletedFollowingsLog(traverseRDFStore, deletedFollowingsJsonArrayString);
		String jsonLDArray = new InstagramExtractor(addedFollowings.toString(),
				getUserURI()).execute();
		send2Storer(deletedFollowingsJsonArrayString, jsonLDArray);
	}

	public void send2Storer(String deletedFollowingsJsonArrayString,
			String jsonLDArray) {
		new InstagramStorer(jsonLDArray, getUserURI(),
				deletedFollowingsJsonArrayString).executeFollowingsChange();
	}

	public ResultSet getFollowingsFromRDFStore() {
		String query = setFollowingsQuery(getUserURI());
		ResultSet execSelect = RDFDataLayer.getInstance().execSelect(query);
		return execSelect;
	}

	private void deletedFollowingsLog(List<String> traverseRDFStore,
			String deletedFollowingsAsJsonArrayString) {
		String msg;
		if (traverseRDFStore.size() != 0) {
			msg = "deleted followings {\"jsonArray\":\"%s\"}";
			getLogger().info(
					String.format(msg, deletedFollowingsAsJsonArrayString));
		}
	}

	private JsonArray convertStringListToJsonArray(List<String> traverseRDFStore) {
		return new Gson().toJsonTree(traverseRDFStore,
				new TypeToken<List<String>>() {
				}.getType()).getAsJsonArray();
	}

	private void addedFollowingsLog(JsonArray addedFollowings) {
		String msg;
		if (addedFollowings.size() != 0) {
			msg = "added new followings {\"jsonArray\":\"%s\"}";
			getLogger().info(String.format(msg, addedFollowings));
		}
	}

	/**
	 * Converts given followings list using {@link InstagramApiJsonWrapper}
	 * wrapper class into json array.
	 * 
	 * @param followingsList
	 *            Followings list.
	 * @return Followings people in JsonArray.
	 */
	private JsonArray convertFollowingsListIntoJsonArray(
			List<InstagramApiJsonWrapper> followingsList) {
		return new Gson().toJsonTree(followingsList,
				new TypeToken<List<InstagramApiJsonWrapper>>() {
				}.getType()).getAsJsonArray();
	}

	/**
	 * Traverse rdf store and compares followings of user with fetcher results.
	 * 
	 * @param execSelect
	 *            RDF Store followings of people query results.
	 * @param followingsList
	 *            Fetched followsings list from instagram api.
	 * @return Deleted followings from RDF store.
	 */
	private List<String> traverseRDFStore(ResultSet execSelect,
			List<InstagramApiJsonWrapper> followingsList) {

		List<String> stoppedFollowingPeople = new ArrayList<String>();
		while (execSelect.hasNext()) {
			QuerySolution solution = execSelect.next();
			// extracts ?p variable from sparql endpoint result
			String followingUserUri = solution.get("p").toString();

			if (!traverseFetcherResults(followingsList, followingUserUri)) {
				String msg = "detected following deletion {\"userUri\":\"\", \"deleted userUri\":\"%s\"}";
				if (getLogger().isDebugEnabled()) {
					getLogger().debug(
							String.format(msg, getUserURI(), followingUserUri));
				}
				stoppedFollowingPeople.add(followingUserUri);
			}
		}
		return stoppedFollowingPeople;
	}

	private boolean traverseFetcherResults(
			List<InstagramApiJsonWrapper> followingsList,
			String followingUserUri) {
		boolean foundOnFetcherResult = false;
		for (InstagramApiJsonWrapper instagramApiJsonWrapper : followingsList) {
			if (followingUserUri
					.contains(instagramApiJsonWrapper.getUsername())) {
				followingsList.remove(instagramApiJsonWrapper);
				foundOnFetcherResult = true;
				break;
			}
		}
		return foundOnFetcherResult;
	}

	private List<InstagramApiJsonWrapper> getFollowingsAsList() {
		InstagramApiJsonWrapper[] instagramArray = new Gson().fromJson(
				getResponse(), InstagramApiJsonWrapper[].class);
		List<InstagramApiJsonWrapper> asList = new ArrayList<InstagramApiJsonWrapper>();
		asList.addAll(Arrays.asList(instagramArray));
		return asList;
	}

	private String setFollowingsQuery(String userURI) {
		return "PREFIX schema: <http://schema.org/> Select ?p where {<"
				+ userURI + "> schema:follows ?p } ";
	}

	public String retrieveUserURI() {
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

	public String getUserURI() {
		return userURI;
	}

	public void setUserURI(String userURI) {
		this.userURI = userURI;
	}
}
