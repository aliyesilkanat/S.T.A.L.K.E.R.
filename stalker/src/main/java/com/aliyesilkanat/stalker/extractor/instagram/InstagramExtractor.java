package com.aliyesilkanat.stalker.extractor.instagram;

import com.aliyesilkanat.stalker.extractor.Extractor;
import com.aliyesilkanat.stalker.util.Tag;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class InstagramExtractor extends Extractor {

	private static final String FULL_NAME = "full_name";
	private static final String INSTAGRAM_BASED_URL = "http://instagram.com/";
	private static final String PROFILE_PICTURE = "profile_picture";

	public InstagramExtractor(String friendsArray, String userURI,
			String userApiJson) {
		super(friendsArray, userURI, userApiJson);
	}

	@Override
	public String execute() {
		String msg = "converting json into jsonld {\"json\":\"%s\"}";
		getLogger().info(String.format(msg, getFriendsArray()));

		JsonObject followingsJsonLd = convertJsonArrayIntoJsonLdArray(new Gson()
				.fromJson(getUserApiJson(), JsonObject.class));

		msg = "converted jsons into jsonld {\"jsonld\":\"%s\"}";
		getLogger().debug(String.format(msg, followingsJsonLd));

		return followingsJsonLd.toString();
	}

	private JsonObject convertJsonArrayIntoJsonLdArray(JsonObject userObject) {
		JsonObject userObjectLd = fillJsonLdObject(userObject);
		JsonArray userArrayLD = new JsonArray();
		for (JsonElement jsonElement : getFriendsArray()) {
			JsonObject userObjectRaw = jsonElement.getAsJsonObject();
			userArrayLD.add(fillJsonLdObject(userObjectRaw));
		}
		setFriendsArrayLD(userArrayLD);
		// setting link between user and it's followings...
		userObjectLd.add(Tag.FOLLOWS.text(), userArrayLD);
		return userObjectLd;
	}

	/**
	 * Fills raw json object data into json ld object
	 * 
	 * @param userObjectRaw
	 *            raw json object comes from instagram api.
	 * @return JsonLD object contains data about person.
	 */
	public JsonObject fillJsonLdObject(JsonObject userObjectRaw) {
		JsonObject userObjectLD = new JsonObject();
		userObjectLD.addProperty(Tag.CONTEXT.text(), Tag.SCHEMA.text());
		userObjectLD.addProperty(Tag.ID.text(), setUserUri(userObjectRaw));
		userObjectLD.addProperty(Tag.TYPE.text(), Tag.PERSON.text());
		userObjectLD.addProperty(Tag.NAME.text(), userObjectRaw.get(FULL_NAME)
				.getAsString());
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
