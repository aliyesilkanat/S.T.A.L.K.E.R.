package com.aliyesilkanat.stalker.fetcher.instagram;

import com.aliyesilkanat.stalker.endpoint.EndpointLayer;
import com.aliyesilkanat.stalker.fetcher.Fetcher;
import com.aliyesilkanat.stalker.retriever.Retriever;
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
		String response = requestFollowings(userId);
		if (getLogger().isTraceEnabled()) {
			msg = "fetched friends {\"userId\":\"%s\", \"responseJson\":\"%s\"}";
			getLogger().trace(String.format(msg, userId, response));
		}
	}

	String requestFollowings(String userId) {
		String initialRequestUri = EndpointLayer.getInstance()
				.setUriForFetchingFollowings(userId);
		JsonObject apiResultJsonObject = getJsonFromApi(initialRequestUri);
		JsonObject resultObject = new JsonObject();

		JsonArray resultArray = apiResultJsonObject.get("data")
				.getAsJsonArray();

		while (apiResultJsonObject.has("pagination")) {
			JsonObject paginationObject = apiResultJsonObject.get("pagination")
					.getAsJsonObject();
			if (paginationObject.has("next_url")) {
				String nextCursor = paginationObject.get("next_cursor")
						.getAsString();
				String nextResultPage = EndpointLayer.getInstance()
						.getFollowings(userId, nextCursor);
				apiResultJsonObject = new Gson().fromJson(nextResultPage,
						JsonObject.class);
				JsonArray paginationData = apiResultJsonObject.get("data")
						.getAsJsonArray();
				for (JsonElement jsonElement : paginationData) {
					resultArray.add(jsonElement.getAsJsonObject());
				}
			}
		}
		return new Gson().toJson(resultArray);
	}

	public JsonObject getJsonFromApi(String initialRequestUri) {
		String response = new Retriever().requestDocument(initialRequestUri);
		JsonObject apiResultJsonObject = new Gson().fromJson(response,
				JsonObject.class);
		return apiResultJsonObject;
	}
}
