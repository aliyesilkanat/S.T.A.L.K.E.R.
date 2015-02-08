package com.aliyesilkanat.stalker.fetcher;

import org.apache.log4j.Logger;

/**
 * @author Ali Yesilkanat
 *
 */
public abstract class Fetcher {
	private final Logger logger = Logger.getLogger(getClass());

	/**
	 * Abstract method for fetching.
	 */
	public abstract void fetch(String userId);

	public Logger getLogger() {
		return logger;
	}
}
