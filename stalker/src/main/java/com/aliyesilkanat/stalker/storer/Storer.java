package com.aliyesilkanat.stalker.storer;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonArray;

public abstract class Storer {

	/**
	 * JsonLd of added new followings.
	 */
	private JsonArray addedNewFollowings;
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
				JsonArray.class));
		this.setDeletedFollowings(new Gson().fromJson(deletedNewFollowings,
				JsonArray.class));
		this.setUserURI(userURI);
	}

	private final Logger logger = Logger.getLogger(getClass());

	public Logger getLogger() {
		return logger;
	}

	public JsonArray getAddedNewFollowings() {
		return addedNewFollowings;
	}

	public void setAddedNewFollowings(JsonArray addedNewFollowings) {
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
