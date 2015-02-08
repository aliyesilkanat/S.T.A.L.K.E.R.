package com.aliyesilkanat.stalker.retriever;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.apache.log4j.Logger;

public class Retriever {
	private final Logger logger = Logger.getLogger(Retriever.class);

	public String requestDocument(String uri) {
		String result = null;
		try {
			result = ClientSingleton.getInstance().newRequest(uri).send()
					.getContentAsString();
		} catch (InterruptedException | TimeoutException | ExecutionException e) {
			String msg = "error while requesting document {\"uri\":\"%s\"}";
			getLogger().error(String.format(msg, uri), e);
		}
		return result;
	}

	public Logger getLogger() {
		return logger;
	}
}