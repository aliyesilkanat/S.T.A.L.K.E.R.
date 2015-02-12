package com.aliyesilkanat.stalker.extractor.instagram;

import com.aliyesilkanat.stalker.extractor.Extractor;
import com.aliyesilkanat.stalker.storer.instagram.InstagramStorer;
import com.aliyesilkanat.stalker.tracker.instagram.InstagramTracker;
import com.aliyesilkanat.stalker.util.Tag;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class InstagramExtractor extends Extractor {

	private static final String INSTAGRAM_BASED_URL = "http://instagram.com/";
	private static final String PROFILE_PICTURE = "profile_picture";

	public InstagramExtractor(String friendsArray, String userId) {
		super(friendsArray, userId);
	}

	@Override
	public void execute() {
		String msg = "converting json into jsonld {\"json\":\"%s\"}";
		getLogger().info(String.format(msg, getFriendsArray()));
		convertJsonIntoJsonLd();
		msg = "converted jsons into jsonld {\"jsonld\":\"%s\"}";
		getLogger().debug(String.format(msg, getFriendsArrayLD()));
		new InstagramStorer(this.getFriendsArrayLD().toString(), getUserId())
				.catchContent();
	}

	private void convertJsonIntoJsonLd() {
		JsonArray userArrayLD = new JsonArray();
		for (JsonElement jsonElement : getFriendsArray()) {
			JsonObject userObjectRaw = jsonElement.getAsJsonObject();
			userArrayLD.add(fillJsonLdObject(userObjectRaw));
		}
		this.setFriendsArrayLD(userArrayLD);
	}

	/**
	 * Fills raw json object data into json ld object
	 * 
	 * @param userObjectRaw
	 *            raw json object comes from instagram api.
	 * @return
	 */
	private JsonObject fillJsonLdObject(JsonObject userObjectRaw) {
		JsonObject userObjectLD = new JsonObject();
		userObjectLD.addProperty(Tag.CONTEXT.text(), Tag.SCHEMA.text());
		userObjectLD.addProperty(Tag.ID.text(), setUserUri(userObjectRaw));
		userObjectLD.addProperty(Tag.TYPE.text(), Tag.PERSON.text());
		userObjectLD.addProperty(Tag.IMAGE.text(),
				userObjectRaw.get(PROFILE_PICTURE).getAsString());
		return userObjectLD;
	}

	private String setUserUri(JsonObject userObjectRaw) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(INSTAGRAM_BASED_URL);
		stringBuilder.append(userObjectRaw.get(Tag.USER_NAME.text())
				.getAsString());
		return stringBuilder.toString();
	}

}
