package com.aliyesilkanat.stalker.extractor.twitter;

import com.aliyesilkanat.stalker.extractor.Extractor;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class TwitterExtractor extends Extractor{
	
	/**
	 * @author Iþýk Erhan
	 */
	
	private static final String TWITTER_BASED_URL = "http://twitter.com/";

	public TwitterExtractor(String friendsArray, String userId) {
		super(friendsArray, userId);
	}

	@Override
	public void execute() {
		getLogger().info("Converting json into jsonLd");
		
		convertJsonIntoJsonLd();
		
		getLogger().info("Json elements in JsonArray converted into jsonLDs");
		
	}
	
	private void convertJsonIntoJsonLd() {
		JsonArray friendsArrayLD = new JsonArray();
		JsonArray original = getFriendsArray();
		for(int i = 0; i < original.size(); i++) {
			JsonObject currentObject = original.get(i).getAsJsonObject();
			final String username = currentObject.get("username").getAsString();
			final String name = currentObject.get("real_name").getAsString();
			final String profilePic = currentObject.get("p_pic").getAsString();
			friendsArrayLD.add(createLDObject(username, name, profilePic));
		}
		this.setFriendsArrayLD(friendsArrayLD);
	}
	
	private JsonObject createLDObject(String username, String name, String profilePic) {
		JsonObject ldObject = new JsonObject();
		ldObject.addProperty("@context", "http://schema.org/");
		ldObject.addProperty("@id", getProfileURL(username));
		ldObject.addProperty("@type", "Person");
		ldObject.addProperty("name", name);
		ldObject.addProperty("image", profilePic);
		
		return ldObject;
	}
	
	private String getProfileURL(String username) {
		return TWITTER_BASED_URL + username;
	}

}
