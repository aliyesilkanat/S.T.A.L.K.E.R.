package com.aliyesilkanat.stalker.extractor;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonArray;

/**
 * @author Ali Yesilkanat Class for extracting raw json arrays into json ld
 *         formats.
 */
public abstract class Extractor {
	/**
	 * Field for holding friends array.
	 */
	private JsonArray friendsArray;

	/**
	 * Field for holding friends array in json ld.
	 */
	private JsonArray friendsArrayLD;
	/**
	 * Logger instance
	 */
	private final Logger logger = Logger.getLogger(getClass());

	/**
	 * User id of person.
	 */
	private String userURI;

	/**
	 * User json object came from API. Used for linkage between followings.
	 */
	private String userApiJson;

	public Extractor(String friendsArray, String userURI, String userApiJson) {
		this.setFriendsArray(new Gson().fromJson(friendsArray, JsonArray.class));
		this.setUserURI(userURI);
		this.setUserApiJson(userApiJson);
	}

	abstract public String execute();

	public JsonArray getFriendsArray() {
		return friendsArray;
	}

	public JsonArray getFriendsArrayLD() {
		return friendsArrayLD;
	}

	public Logger getLogger() {
		return logger;
	}

	public void setFriendsArray(JsonArray friendsArray) {
		this.friendsArray = friendsArray;
	}

	public void setFriendsArrayLD(JsonArray friendsArrayLD) {
		this.friendsArrayLD = friendsArrayLD;
	}

	public String getUserURI() {
		return userURI;
	}

	public void setUserURI(String userURI) {
		this.userURI = userURI;
	}

	public String getUserApiJson() {
		return userApiJson;
	}

	public void setUserApiJson(String userApiJson) {
		this.userApiJson = userApiJson;
	}
}
