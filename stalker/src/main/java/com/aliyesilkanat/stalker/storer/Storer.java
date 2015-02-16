package com.aliyesilkanat.stalker.storer;

import org.apache.log4j.Logger;

import com.aliyesilkanat.stalker.Role;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public abstract class Storer extends Role {

	/**
	 * JsonLd of added new followings.
	 */
	private JsonObject addedNewFollowings;
	/**
	 * Deleted new followings of person as json array (Array contains only
	 * userUris).
	 */
	private JsonArray deletedFollowings;
	/**
	 * User uri of person.
	 */
	private String userURI;

	public Storer(String addedNewFollownigs, String deletedNewFollowings,
			String userURI) {
		this.setAddedNewFollowings(new Gson().fromJson(addedNewFollownigs,
				JsonObject.class));
		this.setDeletedFollowings(new Gson().fromJson(deletedNewFollowings,
				JsonArray.class));
		this.setUserURI(userURI);
	}

	private final Logger logger = Logger.getLogger(getClass());

	public Logger getLogger() {
		return logger;
	}

	public JsonObject getAddedNewFollowings() {
		return addedNewFollowings;
	}

	public void setAddedNewFollowings(JsonObject addedNewFollowings) {
		this.addedNewFollowings = addedNewFollowings;
	}

	public String getUserURI() {
		return userURI;
	}

	public void setUserURI(String userURI) {
		this.userURI = userURI;
	}

	public JsonArray getDeletedFollowings() {
		return deletedFollowings;
	}

	public void setDeletedFollowings(JsonArray deletedFollowings) {
		this.deletedFollowings = deletedFollowings;
	}

}
