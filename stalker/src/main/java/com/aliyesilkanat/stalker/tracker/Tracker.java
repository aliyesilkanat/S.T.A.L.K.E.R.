package com.aliyesilkanat.stalker.tracker;

import org.apache.log4j.Logger;

public abstract class Tracker {
	private final Logger logger = Logger.getLogger(getClass());

	/**
	 * Followings as json array
	 */
	private String response;
	/**
	 * User id of person.
	 */
	private String userId;

	public Tracker(String response, String userId) {
		this.setResponse(response);
		this.setUserId(userId);
	}

	public abstract void catchChange();

	public String getResponse() {
		return response;
	}

	public String getUserId() {
		return userId;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Logger getLogger() {
		return logger;
	}
}
