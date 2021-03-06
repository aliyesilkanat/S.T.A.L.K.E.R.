package com.aliyesilkanat.stalker.endpoint;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.apache.log4j.Logger;

import com.aliyesilkanat.stalker.data.UnfinishedOperationException;
import com.aliyesilkanat.stalker.retriever.ClientSingleton;

public class EndpointUtils {
	private static EndpointUtils instance = null;
	private final Logger logger = Logger.getLogger(EndpointUtils.class);
	private static final String ACCESS_TOKEN = "239984780.fe09684.e9be582082be4858a9bdb170c9007774";

	public static EndpointUtils getInstance() {

		if (instance == null) {
			instance = new EndpointUtils();
		}
		return instance;
	}

	public String setUriForFetchingFollowings(String userId) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("https://api.instagram.com/v1/users/");
		stringBuilder.append(userId);
		stringBuilder.append("/follows?access_token=");
		stringBuilder.append(ACCESS_TOKEN);
		String endpointUri = stringBuilder.toString();
		return endpointUri;
	}

	public Logger getLogger() {
		return logger;
	}

	public String getFollowings(String userId, String nextCursor)
			throws UnfinishedOperationException {
		String endpointUri = setUriForFetchingFollowings(userId);
		endpointUri = addParameterToUri(endpointUri, "cursor", nextCursor);
		String response = "";
		try {
			response = ClientSingleton.getInstance().newRequest(endpointUri)
					.send().getContentAsString();
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			String msg = "cannot act get method on endpoint {\"uri\":\"%s\"}";
			getLogger().error(String.format(msg, endpointUri), e);
			throw new UnfinishedOperationException();
		}
		return response;
	}

	public String getPersonDetails(String userID) {
		return "https://api.instagram.com/v1/users/" + userID
				+ "/?access_token=" + ACCESS_TOKEN;
	}

	public String addParameterToUri(String endpointUri, String parameterType,
			String nextCursor) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(endpointUri);
		stringBuilder.append("&");
		stringBuilder.append(parameterType);
		stringBuilder.append("=");
		stringBuilder.append(nextCursor);
		return stringBuilder.toString();
	}
}
