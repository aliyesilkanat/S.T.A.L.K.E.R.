package com.aliyesilkanat.stalker.fetcher.twitter;

import twitter4j.IDs;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

import com.aliyesilkanat.stalker.fetcher.Fetcher;
import com.aliyesilkanat.stalker.tracker.twitter.TwitterTracker;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class TwitterFetcher extends Fetcher{
	
	/**
	 * @author Iþýk Erhan
	 */

	private static final String consumerKey = "OD0R13BiOyq38jgxxFDxSoXoZ";
	private static final String consumerSecret = "WXq4FY7C7P1Rk1coTdTsdT8wgGCyaAXHapZ0prAK5fuq3M2T6i";
	private static final String accessToken = "18263321-ZKQRWyQTvcYikOO6kDNW0zLBNPjFoTKlR9wVgOSXR";
	private static final String accessTokenSecret = "ZGd0MtOvOg62V00tWnbke4EuPtN5Glolx1cNo4Na0kY74";
	
	private Configuration config;
	
	public TwitterFetcher() {
		ConfigurationBuilder configBuilder = new ConfigurationBuilder();
		configBuilder.setDebugEnabled(true)
						.setOAuthConsumerKey(consumerKey)
						.setOAuthConsumerSecret(consumerSecret)
						.setOAuthAccessToken(accessToken)
						.setOAuthAccessTokenSecret(accessTokenSecret);
		config = configBuilder.build();
	}
	
	@Override
	public void fetch(String userId){
		getLogger().info("Requesting to fetch followers, ID: " + userId);
		String response = requestFollowings(userId);
		getLogger().info("Fetched friends, response Json: " + response);
		
		new TwitterTracker(response, userId).catchChange();
	}
	
	public String requestFollowings(String userId) {
		
		Twitter twitter = new TwitterFactory(config).getInstance();
		long userIDNo = Long.parseLong(userId);
		try {
			getLogger().info("Fetching friends of " + userIDNo + ": " + twitter.showUser(userIDNo).getName());
		} catch (TwitterException e) {
			getLogger().warn("TwitterException handled", e);
		}
		long cursor = -1;
		IDs friends = null;
		JsonArray friendsJson = new JsonArray();
		do{
			try {
				friends = twitter.getFriendsIDs(userIDNo, cursor);
				for(long id : friends.getIDs()) {
					JsonObject friend = new JsonObject();
					User twitterUser = twitter.showUser(id);
					friend.addProperty("username", twitterUser.getScreenName());
					friend.addProperty("p_pic", twitterUser.getProfileImageURLHttps().replace("_normal", ""));
					friend.addProperty("real_name", twitterUser.getName());
					friendsJson.add(friend);
				}
			} catch (TwitterException e) {
				String errorMessage = String.format("TwitterException handled while fetching friends of %d, cursor: %d", userIDNo, cursor);
				getLogger().error(errorMessage, e);
			}
		} while((cursor = friends.getNextCursor()) != 0);
		
		return friendsJson.toString();
	}

}
