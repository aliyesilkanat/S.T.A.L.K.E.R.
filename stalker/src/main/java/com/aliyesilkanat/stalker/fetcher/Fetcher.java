package com.aliyesilkanat.stalker.fetcher;

import org.apache.log4j.Logger;

import com.aliyesilkanat.stalker.Role;

/**
 * @author Ali Yesilkanat
 *
 */
public abstract class Fetcher extends Role {
	private final Logger logger = Logger.getLogger(getClass());
	/**
	 * User id field for fetching values.
	 */
	private String userId;

	public Fetcher(String userId) {
		this.setUserId(userId);
	}

	public Logger getLogger() {
		return logger;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
}
