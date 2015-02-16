package com.aliyesilkanat.stalker.friendshipactivity;

import org.junit.Test;

import com.google.gson.JsonArray;

public class FriendshipActivityReporterTest {
	@Test
	public void testQuery() throws Exception {
		JsonArray createReportObject = new FriendshipActivityReporter(1423564894000L, 1424428894000L)
				.createReportObject("http://instagram.com/aliyesilkanat");
		System.err.println(createReportObject);
	}
}
