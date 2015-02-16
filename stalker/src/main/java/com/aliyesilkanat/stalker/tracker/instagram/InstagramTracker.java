package com.aliyesilkanat.stalker.tracker.instagram;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.aliyesilkanat.stalker.data.RDFDataLayer;
import com.aliyesilkanat.stalker.data.UnfinishedOperationException;
import com.aliyesilkanat.stalker.endpoint.EndpointUtils;
import com.aliyesilkanat.stalker.extractor.instagram.InstagramExtractor;
import com.aliyesilkanat.stalker.retriever.Retriever;
import com.aliyesilkanat.stalker.storer.instagram.InstagramStorer;
import com.aliyesilkanat.stalker.tracker.Tracker;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

public class InstagramTracker extends Tracker {

	private static final String INSTAGRAM_BASE_URL = "http://instagram.com/";
	/**
	 * UserUri which extracted by using user id via instagram api.
	 */
	private String userURI;
	/**
	 * Json object which comes from Instagram Api... It's used on linking
	 * followings and this user.
	 */
	private JsonObject userApiObject;

	public InstagramTracker(String fetcherResponse, String userId) {
		super(fetcherResponse, userId);

	}

	@Override
	public void catchChange() throws UnfinishedOperationException {
		// setting user uri using user ID via Instagram API
		setUserURI(retrieveUserURI());

		String msg = "catching change {\"userURI\":\"%s\", \"follows\":\"%s\"}";
		getLogger().info(String.format(msg, getUserURI(), getResponse()));

		List<InstagramApiJsonWrapper> followingsList = createFollowingsAsList();
		List<String> traverseRDFStore = null;
		ResultSet execSelect = getFollowingsFromRDFStore();
		if (execSelect != null) {
			traverseRDFStore = traverseRDFStore(execSelect, followingsList);
		}
		String deletedFollowingsJsonArrayString = convertStringListToJsonArray(
				traverseRDFStore).toString();

		JsonArray addedFollowings = convertFollowingsListIntoJsonArray(followingsList);

		// log...
		addedFollowingsLog(addedFollowings);
		deletedFollowingsLog(traverseRDFStore, deletedFollowingsJsonArrayString);

		// convert added followings (new fetcher results) to json ld.
		String jsonLDObject = new InstagramExtractor(addedFollowings.toString(),
				getUserURI(), getUserApiObject().toString()).execute();

		// send added and deleted followings of a person to storer...
		send2Storer(deletedFollowingsJsonArrayString, jsonLDObject);
	}

	public void send2Storer(String deletedFollowingsJsonArrayString,
			String jsonLDArray) {
		new InstagramStorer(jsonLDArray, getUserURI(),
				deletedFollowingsJsonArrayString).execute();
	}

	/**
	 * Get followings of {@link InstagramTracker#response} from rdf store.
	 * 
	 * @return followings of a person which fetched from rdf store.
	 * @throws UnfinishedOperationException
	 */
	public ResultSet getFollowingsFromRDFStore() throws UnfinishedOperationException {
		String query = createPersonsFollowingsQuery(getUserURI());
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

	/**
	 * Converts string list into json array.
	 * 
	 * @param list
	 *            String list
	 * @return json array.
	 */
	private JsonArray convertStringListToJsonArray(List<String> list) {
		return new Gson().toJsonTree(list, new TypeToken<List<String>>() {
		}.getType()).getAsJsonArray();
	}

	/**
	 * Log for added new followings
	 * 
	 * @param addedFollowings
	 *            added new followings json array.
	 */
	private void addedFollowingsLog(JsonArray addedFollowings) {
		String msg;
		if (addedFollowings.size() != 0) {
			msg = "added new followings {\"userURI\":\"%s\", \"jsonArray\":\"%s\"}";
			getLogger().info(String.format(msg, getUserURI(), addedFollowings));
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
	 * Traverse rdf store and compares followings of user with
	 * {@link InstagramTracker#getResponse()}.
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

	/**
	 * Traverse fetcher results. If fetcher results contains given userURI, then
	 * that object contains user uri from fetcher results deleted.
	 * 
	 * @param followingsList
	 *            Fetcher results as list.
	 * @param followingUserUri
	 *            userUri which one following of people from rdf store.
	 * @return If fetcher results contains followingsUserUri then method returns
	 *         true, otherwise it returns false.
	 */
	private boolean traverseFetcherResults(
			List<InstagramApiJsonWrapper> followingsList,
			String followingUserUri) {
		boolean foundOnFetcherResult = false;
		for (InstagramApiJsonWrapper instagramApiJsonWrapper : followingsList) {
			// Instagram api returns only username, RDF store stores people's
			// uris e.g. "http://instagram.com/username"
			// so it contains username...
			if (followingUserUri
					.contains(instagramApiJsonWrapper.getUsername())) {
				followingsList.remove(instagramApiJsonWrapper);
				foundOnFetcherResult = true;
				break;
			}
		}
		return foundOnFetcherResult;
	}

	/**
	 * Creates followings list from fetcher results field as
	 * {@link InstagramApiJsonWrapper} list.
	 * 
	 * @return List as {@link InstagramApiJsonWrapper}.
	 */
	private List<InstagramApiJsonWrapper> createFollowingsAsList() {
		InstagramApiJsonWrapper[] instagramArray = new Gson().fromJson(
				getResponse(), InstagramApiJsonWrapper[].class);
		List<InstagramApiJsonWrapper> asList = new ArrayList<InstagramApiJsonWrapper>();
		asList.addAll(Arrays.asList(instagramArray));
		return asList;
	}

	/**
	 * Creates a person's followings sparql query.
	 * 
	 * @param userURI
	 *            The person's uri whose questioned followings
	 * @return Sparql query string.
	 */
	private String createPersonsFollowingsQuery(String userURI) {
		return "PREFIX schema: <http://schema.org/> Select ?p where {<"
				+ userURI + "> schema:follows ?p } ";
	}

	/**
	 * Retrieve user uri by using user id field via instagram api.
	 * 
	 * @return username uri.
	 * @throws UnfinishedOperationException
	 */
	public String retrieveUserURI() throws UnfinishedOperationException {
		String msg = "retrieving uri for user {\"userId\":\"%s\"}";
		getLogger().debug(String.format(msg, getUserId()));
		String requestDocument = new Retriever().requestDocument(EndpointUtils
				.getInstance().getPersonDetails(getUserId()));

		String userName = "";

		// If api founds sutiable username for that user id, append it to
		// Instagram Base Url
		JsonObject personDetailObject = new Gson().fromJson(requestDocument,
				JsonObject.class);
		if (personDetailObject.has("data")) {
			JsonObject dataObject = personDetailObject.get("data")
					.getAsJsonObject();
			if (dataObject.has("username")) {
				userName = INSTAGRAM_BASE_URL;
				userName += dataObject.get("username").getAsString();
			}
			this.setUserApiObject(dataObject);

		}
		return userName;
	}

	public String getUserURI() {
		return userURI;
	}

	public void setUserURI(String userURI) {
		this.userURI = userURI;
	}

	public JsonObject getUserApiObject() {
		return userApiObject;
	}

	public void setUserApiObject(JsonObject userApiObject) {
		this.userApiObject = userApiObject;
	}
}
