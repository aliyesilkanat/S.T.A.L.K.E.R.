package com.aliyesilkanat.stalker.extractor;

import com.aliyesilkanat.stalker.util.Tag;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class InstagramExtractor extends Extractor {

	public InstagramExtractor(String friendsArray) {
		super(friendsArray);
	}

	@Override
	public void execute() {
		JsonArray userArrayLD = new JsonArray();
		for (JsonElement jsonElement : getFriendsArray()) {
			JsonObject userObjectRaw = jsonElement.getAsJsonObject();
			userArrayLD.add(fillJsonLdObject(userObjectRaw));
		}
		this.setFriendsArray(userArrayLD);
	}

	private JsonObject fillJsonLdObject(JsonObject userObjectRaw) {
		JsonObject userObjectLD = new JsonObject();
		userObjectLD.addProperty(Tag.CONTEXT.text(), Tag.SCHEMA.text());
		userObjectLD.addProperty(Tag.ID.text(), setUserUri(userObjectRaw));
		userObjectLD.addProperty(Tag.TYPE.text(), Tag.PERSON.text());
		userObjectLD.addProperty(Tag.IMAGE.text(),
				userObjectRaw.get("profile_picture").getAsString());
		return userObjectLD;
	}

	private String setUserUri(JsonObject userObjectRaw) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("http://instagram.com/");
		stringBuilder.append(userObjectRaw.get(Tag.USER_NAME.text())
				.getAsString());
		return stringBuilder.toString();
	}

}
