package com.aliyesilkanat.stalker.fetcher;

import org.junit.Assert;
import org.junit.Test;

import com.aliyesilkanat.stalker.fetcher.twitter.TwitterFetcher;
import com.aliyesilkanat.stalker.util.FileUtil;

public class TwitterFetcherTest {
	private static final String userID = "3032812463";
	@Test
	public void fetchTest() throws Exception {
		String response = new TwitterFetcher().requestFollowings(userID);
		String actual = FileUtil.readFile("testHtml\\followingsTwitter.json");
		System.out.println(response);
		System.out.println(actual);
		Assert.assertEquals(response, actual);
	}
}
