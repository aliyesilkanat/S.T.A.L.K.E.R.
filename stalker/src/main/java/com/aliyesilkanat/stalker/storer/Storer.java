package com.aliyesilkanat.stalker.storer;

import org.apache.log4j.Logger;

public abstract class Storer {

	/**
	 * JsonLd of followings.
	 */
	private String content;
	/**
	 * User uri of person.
	 */
	private String userURI;
	/**
	 * Added new followings of person.
	 */
	private String addedNewFollowings;

	public Storer(String content, String userURI, String addedNewFollowings) {
		this.setContent(content);
		this.setUserURI(userURI);
		this.setAddedNewFollowings(addedNewFollowings);
	}

	private final Logger logger = Logger.getLogger(getClass());

	public Logger getLogger() {
		return logger;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getUserURI() {
		return userURI;
	}

	public void setUserURI(String userURI) {
		this.userURI = userURI;
	}

	public String getAddedNewFollowings() {
		return addedNewFollowings;
	}

	public void setAddedNewFollowings(String addedNewFollowings) {
		this.addedNewFollowings = addedNewFollowings;
	}

}
