package com.aliyesilkanat.stalker.fetcher;

import org.apache.log4j.Logger;

import com.aliyesilkanat.stalker.endpoint.EndpointLayer;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class InstagramFetcher {

	private final Logger logger = Logger.getLogger(InstagramFetcher.class);

	public String fetchFollowings(String userId) {
		String msg = "fetching friends {\"friend Id\":\"%s\"}";
		getLogger().debug(String.format(msg, userId));
		String response = requestFollowings(userId);
		msg = "fetched friends {\"userId\":\"%s\", \"responseJson\":\"%s\"}";
		getLogger().trace(String.format(msg, userId, response));
		return response;
	}

	String requestFollowings(String userId) {
		JsonObject resultObject = new JsonObject();
		String response = EndpointLayer.getInstance().getFollowings(userId);

		JsonObject resultJsonObject = new Gson().fromJson(response,
				JsonObject.class);
		JsonArray resultArray = resultJsonObject.get("data").getAsJsonArray();

		while (resultJsonObject.has("pagination")) {
			JsonObject paginationObject = resultJsonObject.get("pagination")
					.getAsJsonObject();
			if (paginationObject.has("next_url")) {
				String nextCursor = paginationObject.get("next_cursor")
						.getAsString();
				String nextResultPage = EndpointLayer.getInstance()
						.getFollowings(userId, nextCursor);
				resultJsonObject = new Gson().fromJson(nextResultPage,
						JsonObject.class);
				JsonArray paginationData = resultJsonObject.get("data")
						.getAsJsonArray();
				for (JsonElement jsonElement : paginationData) {
					resultArray.add(jsonElement.getAsJsonObject());
				}
			}
		}
		return new Gson().toJson(resultArray);
	}

	public Logger getLogger() {
		return logger;
	}

}
