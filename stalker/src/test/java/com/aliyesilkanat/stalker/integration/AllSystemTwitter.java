package com.aliyesilkanat.stalker.integration;

import org.junit.Test;

import com.aliyesilkanat.stalker.trigger.twitter.TwitterFollowingsTrigger;

public class AllSystemTwitter {
	@Test
	public void checkIntegration() throws Exception {
		new TwitterFollowingsTrigger("18263321").execute();
	}
}
