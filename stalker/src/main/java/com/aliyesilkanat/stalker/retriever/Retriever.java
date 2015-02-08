package com.aliyesilkanat.stalker.retriever;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.apache.log4j.Logger;
import org.eclipse.jetty.client.HttpClient;

public class Retriever {
	private final Logger logger = Logger.getLogger(Retriever.class);

	public String requestDocument(String uri) {
		if (getLogger().isDebugEnabled()) {
			String msg = "requesting document {\"uri\":\"%s\"}";
			getLogger().debug(String.format(msg, uri));
		}
		String result = null;
		try {
			HttpClient instance = ClientSingleton.getInstance();
			result = instance.newRequest(uri).send().getContentAsString();
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
