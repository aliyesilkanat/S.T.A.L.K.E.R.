package com.aliyesilkanat.stalker.fetcher.twitter;

import twitter4j.IDs;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

import com.aliyesilkanat.stalker.data.UnfinishedOperationException;
import com.aliyesilkanat.stalker.fetcher.Fetcher;
import com.aliyesilkanat.stalker.tracker.twitter.TwitterTracker;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class TwitterFetcher extends Fetcher {

	/**
	 * @author Iþýk Erhan & Ali Yesilkanat
	 */
	private static final String consumerKey = "OD0R13BiOyq38jgxxFDxSoXoZ";
	private static final String consumerSecret = "WXq4FY7C7P1Rk1coTdTsdT8wgGCyaAXHapZ0prAK5fuq3M2T6i";
	private static final String accessToken = "18263321-ZKQRWyQTvcYikOO6kDNW0zLBNPjFoTKlR9wVgOSXR";
	private static final String accessTokenSecret = "ZGd0MtOvOg62V00tWnbke4EuPtN5Glolx1cNo4Na0kY74";

	private Configuration config;

	public TwitterFetcher(String userId) {
		super(userId);
		ConfigurationBuilder configBuilder = new ConfigurationBuilder();
		configBuilder.setDebugEnabled(true).setOAuthConsumerKey(consumerKey)
				.setOAuthConsumerSecret(consumerSecret)
				.setOAuthAccessToken(accessToken)
				.setOAuthAccessTokenSecret(accessTokenSecret);
		config = configBuilder.build();
	}

	@Override
	protected void executeAction() throws UnfinishedOperationException {
		String msg = "Requesting fetch followings {\"userId\":\"%s\"}";
		getLogger().info(String.format(msg, getUserId()));
		String response = requestFollowings(getUserId());
		msg = "Fetched followings {\"userId\":\"%s\", \"responseJson\":\"%s\"}";
		getLogger().info(String.format(msg, getUserId(), response));

		new TwitterTracker(response, getUserId()).catchChange();
	}

	public String requestFollowings(String userId)
			throws UnfinishedOperationException {

		Twitter twitter = new TwitterFactory(config).getInstance();
		long userIDNo = Long.parseLong(userId);
		try {
			getLogger().info(
					"Fetching friends of " + userIDNo + ": "
							+ twitter.showUser(userIDNo).getName());
		} catch (TwitterException e) {
			getLogger().error("TwitterException handled", e);
			throw new UnfinishedOperationException();
		}
		long cursor = -1;
		IDs friends = null;
		JsonArray friendsJson = new JsonArray();
		do {
			try {
				friends = twitter.getFriendsIDs(userIDNo, cursor);
				for (long id : friends.getIDs()) {
					JsonObject friend = new JsonObject();
					User twitterUser = twitter.showUser(id);
					friend.addProperty("username", twitterUser.getScreenName());
					friend.addProperty("p_pic", twitterUser
							.getProfileImageURLHttps().replace("_normal", ""));
					friend.addProperty("real_name", twitterUser.getName());
					friendsJson.add(friend);
				}
			} catch (TwitterException e) {
				String errorMessage = String
						.format("TwitterException handled while fetching friends of %d, cursor: %d",
								userIDNo, cursor);
				getLogger().error(errorMessage, e);
				throw new UnfinishedOperationException();
			}
			// has more page to fetch?
		} while ((cursor = friends.getNextCursor()) != 0);

		return friendsJson.toString();
	}

}
