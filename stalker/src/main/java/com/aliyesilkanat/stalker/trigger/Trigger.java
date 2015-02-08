package com.aliyesilkanat.stalker.trigger;

import org.apache.log4j.Logger;

/**
 * Abstract Trigger class for starting session.
 * 
 * @author Ali Yesilkanat
 *
 */
public abstract class Trigger {
	private final Logger logger = Logger.getLogger(getClass());
	private String userID;

	/**
	 * Sets given user Id into field.
	 * 
	 * @param userID
	 */
	public Trigger(String userID) {
		this.setUserID(userID);
	}

	/**
	 * Starts session.
	 */
	public abstract void execute();

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public Logger getLogger() {
		return logger;
	}
}
