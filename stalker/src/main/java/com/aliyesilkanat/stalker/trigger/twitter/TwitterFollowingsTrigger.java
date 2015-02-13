package com.aliyesilkanat.stalker.trigger.twitter;

import com.aliyesilkanat.stalker.fetcher.instagram.InstagramFetcher;
import com.aliyesilkanat.stalker.trigger.Trigger;

public class TwitterFollowingsTrigger extends Trigger{

	public TwitterFollowingsTrigger(String userID) {
		super(userID);
	}

	@Override
	public void execute() {
		String msg = "executing followings trigger {\"userID\":\"%s\"}";
		getLogger().info(String.format(msg, getUserID()));
		new InstagramFetcher().fetch(getUserID());
		if (getLogger().isTraceEnabled()) {
			msg = "executed followings trigger {\"userID\":\"%s\"}";
			getLogger().trace(String.format(msg, getUserID()));
		}
	}

}
