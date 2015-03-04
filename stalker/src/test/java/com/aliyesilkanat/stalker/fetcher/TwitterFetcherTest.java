package com.aliyesilkanat.stalker.fetcher;

import static org.junit.Assert.*;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import com.aliyesilkanat.stalker.data.UnfinishedOperationException;
import com.aliyesilkanat.stalker.fetcher.twitter.TwitterFetcher;
import com.aliyesilkanat.stalker.util.FileUtil;

public class TwitterFetcherTest {

	/**
	 * @author Isik Erhan
	 */

	private static final String userID = "3032812463";

	@Test
	public void fetchTest() throws Exception {
		String response = new TwitterFetcher(userID).requestFollowings(userID);
		String actual = FileUtil.readFile("testHtml\\followingsTwitter.json");
		System.out.println(response);
		System.out.println(actual);
		Assert.assertEquals(response, actual);
	}

	// Needs api connection.
	@Test
	public void stressTest() throws Exception {
		TwitterFetcher twitterFetcher = Mockito.spy(new TwitterFetcher(userID));
		for (int i = 0; i < 1500; i++) {
			try {
				twitterFetcher.requestFollowings(userID);
			} catch (UnfinishedOperationException e) {
				break;
			}
		}
		Mockito.doThrow(UnfinishedOperationException.class)
				.when(twitterFetcher).requestFollowings(userID);

	}
}
