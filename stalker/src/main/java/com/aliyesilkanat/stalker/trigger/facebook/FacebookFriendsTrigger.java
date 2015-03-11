package com.aliyesilkanat.stalker.trigger.facebook;

import com.aliyesilkanat.stalker.fetcher.facebook.FacebookFetcher;
import com.aliyesilkanat.stalker.trigger.Trigger;

public class FacebookFriendsTrigger extends Trigger {

	public FacebookFriendsTrigger(String userID) {
		super(userID);
	}

	@Override
	public void execute() {
		new FacebookFetcher(getUserID());
	}
}
