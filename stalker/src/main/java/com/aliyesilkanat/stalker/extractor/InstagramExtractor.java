package com.aliyesilkanat.stalker.extractor;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class InstagramExtractor extends Extractor {

	public InstagramExtractor(JsonArray friendsArray) {
		super(friendsArray);
	}

	@Override
	public void execute() {
		JsonArray userArrayLD = new JsonArray();
		for (JsonElement jsonElement : getFriendsArray()) {
			JsonObject userObjectRaw = jsonElement.getAsJsonObject();
			userArrayLD.add(fillJsonLdObject(userObjectRaw));
		}
	}

	private JsonObject fillJsonLdObject(JsonObject userObjectRaw) {
		JsonObject userObjectLD = new JsonObject();
		userObjectLD.addProperty("@context", "http://schema.org");
		userObjectLD.addProperty("@id", setUserUri(userObjectRaw));
		userObjectLD.addProperty("@type", "Person");
		userObjectLD.addProperty("image", userObjectRaw.get("profile_picture")
				.getAsString());
		return userObjectLD;
	}

	private String setUserUri(JsonObject userObjectRaw) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("http://instagram.com/");
		stringBuilder.append(userObjectRaw.get("username").getAsString());
		return stringBuilder.toString();
	}

}
