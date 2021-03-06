package com.aliyesilkanat.stalker.tracker;

import org.apache.log4j.Logger;

import com.aliyesilkanat.stalker.data.UnfinishedOperationException;

public abstract class Tracker {
	private final Logger logger = Logger.getLogger(getClass());

	/**
	 * Followings as json array
	 */
	protected String response;
	/**
	 * User id of person.
	 */
	protected String userId;

	public Tracker(String response, String userId) {
		this.setResponse(response);
		this.setUserId(userId);
	}

	public void execute() {
		try {
			catchChange();
		} catch (UnfinishedOperationException e) {
			String msg = "Unfinished operation, terminating tracker {\"userId\":\"%s\", \"response\":\"%s\"}";
			getLogger().error(String.format(msg, response));
		}
	}

	protected abstract void catchChange() throws UnfinishedOperationException;

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
