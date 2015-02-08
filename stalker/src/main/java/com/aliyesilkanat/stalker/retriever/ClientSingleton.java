package com.aliyesilkanat.stalker.retriever;

import org.apache.log4j.Logger;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.util.ssl.SslContextFactory;

public class ClientSingleton {
	private final static Logger logger = Logger
			.getLogger(ClientSingleton.class);
	private static HttpClient client;

	public static HttpClient getInstance() {

		if (client == null) {
			SslContextFactory context = new SslContextFactory(true);
			client = new HttpClient(context);
			try {
				client.start();
			} catch (Exception e) {
				getLogger().error("cannot start client", e);
			}
		}
		return client;
	}

	public static Logger getLogger() {
		return logger;
	}
}
