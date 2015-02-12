package com.aliyesilkanat.stalker.fetcher.instagram;

import com.aliyesilkanat.stalker.endpoint.EndpointUtils;
import com.aliyesilkanat.stalker.extractor.instagram.InstagramExtractor;
import com.aliyesilkanat.stalker.fetcher.Fetcher;
import com.aliyesilkanat.stalker.retriever.Retriever;
import com.aliyesilkanat.stalker.storer.InstagramStorer;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * @author Ali Yesilkanat
 *
 */
public class InstagramFetcher extends Fetcher {

	@Override
	public void fetch(String userId) {
		String msg = "fetching friends {\"friend Id\":\"%s\"}";
		getLogger().info(String.format(msg, userId));

		// fetches followings of a person.
		String response = requestFollowings(userId);

		if (getLogger().isTraceEnabled()) {
			msg = "fetched friends {\"userId\":\"%s\", \"responseJson\":\"%s\"}";
			getLogger().trace(String.format(msg, userId, response));
		}
		new InstagramExtractor(response).execute();
	}

	/**
	 * Request followings of given user.
	 * 
	 * @param userId
	 *            Id of User
	 * @return Json Array as String.
	 */
	public String requestFollowings(String userId) {
		// gets initial request uri for given user...
		String initialRequestUri = EndpointUtils.getInstance()
				.setUriForFetchingFollowings(userId);

		JsonObject apiResultJsonObject = getJsonFromApi(initialRequestUri);
		extractedJsonFromApiLogging(apiResultJsonObject);
		// adds followings to json array...
		JsonArray resultArray = new JsonArray();
		addFollowingsIntoResultArray(apiResultJsonObject, resultArray);

		// if there is more page, add them too ...
		while (isThereAreMorePageToFetch(apiResultJsonObject)) {
			apiResultJsonObject = addOtherPages(initialRequestUri,
					apiResultJsonObject, resultArray);
			extractedJsonFromApiLogging(apiResultJsonObject);
		}
		return new Gson().toJson(resultArray);
	}

	/**
	 * Checks if there are more pages to fetch.
	 * 
	 * @param apiResultJsonObject
	 *            result object of api.
	 * @return result.
	 */
	private boolean isThereAreMorePageToFetch(JsonObject apiResultJsonObject) {
		if (apiResultJsonObject.has("pagination")) {
			JsonElement jsonElement = apiResultJsonObject.get("pagination");
			if (jsonElement != null) {
				return jsonElement.getAsJsonObject().has("next_cursor");
			}
		}
		return false;
	}

	private void extractedJsonFromApiLogging(JsonObject apiResultJsonObject) {
		if (getLogger().isTraceEnabled()) {
			String msg = "requested followings {\"json\":\"%s\"}";
			getLogger().trace(String.format(msg, apiResultJsonObject));
		}
	}

	/**
	 * Fetchs and adds followings of people which send by more than one page via
	 * instagram api.
	 * 
	 * @param initialRequestUri
	 *            Initial request uri for followings of a person in instagram.
	 * @param apiResultJsonObject
	 *            result object of api.
	 * @param resultArray
	 * @return result object of api for given uri.
	 */
	private JsonObject addOtherPages(String initialRequestUri,
			JsonObject apiResultJsonObject, JsonArray resultArray) {
		// add next pages cursor param to initial request uri.
		String addParameterToUri = getNextUri(apiResultJsonObject,
				initialRequestUri);

		// get next page from api...
		apiResultJsonObject = getJsonFromApi(addParameterToUri);

		addFollowingsIntoResultArray(apiResultJsonObject, resultArray);
		return apiResultJsonObject;
	}

	private String getDocument(String addParameterToUri) {
		String nextResultPage = new Retriever()
				.requestDocument(addParameterToUri);
		return nextResultPage;
	}

	/**
	 * Gets next page's uri using initial request uri and cursor property on
	 * result json object.
	 * 
	 * @param apiResultJsonObject
	 *            Api result json object.
	 * @param initialRequestUri
	 *            Initial request uri for followings of a person.
	 * @return Next page's uri.
	 */
	private String getNextUri(JsonObject apiResultJsonObject,
			String initialRequestUri) {
		JsonObject paginationObject = apiResultJsonObject.get("pagination")
				.getAsJsonObject();
		String nextCursor = paginationObject.get("next_cursor").getAsString();
		String addParameterToUri = EndpointUtils.getInstance()
				.addParameterToUri(initialRequestUri, "cursor", nextCursor);
		return addParameterToUri;
	}

	private void addFollowingsIntoResultArray(JsonObject apiResultJsonObject,
			JsonArray resultArray) {
		JsonArray paginationData = apiResultJsonObject.get("data")
				.getAsJsonArray();
		for (JsonElement jsonElement : paginationData) {
			resultArray.add(jsonElement.getAsJsonObject());
		}
	}

	/**
	 * Gets json from api using retriever.
	 * 
	 * @param initialRequestUri
	 *            requested uri.
	 * @return Extracted json.
	 */
	public JsonObject getJsonFromApi(String initialRequestUri) {
		String response = getDocument(initialRequestUri);
		JsonObject apiResultJsonObject = new Gson().fromJson(response,
				JsonObject.class);
		return apiResultJsonObject;
	}
}
