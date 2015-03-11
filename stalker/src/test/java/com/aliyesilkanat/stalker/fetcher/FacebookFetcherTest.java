package com.aliyesilkanat.stalker.fetcher;

import org.junit.Test;

import com.aliyesilkanat.stalker.fetcher.facebook.FacebookFetcher;

import facebook4j.Facebook;

public class FacebookFetcherTest {
	@Test
	public void createFacebookInstance() throws Exception {
		Facebook createFacebookInstance = new FacebookFetcher("719297449")
				.createFacebookInstance();
		System.err.println(createFacebookInstance.getFriends("100003540499561"));
	}
}
