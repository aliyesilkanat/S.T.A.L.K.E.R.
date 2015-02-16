package com.aliyesilkanat.stalker.friendshipactivity;

import static org.junit.Assert.*;

import org.junit.Test;

import com.google.gson.JsonArray;

public class FriendshipActivityReporterTest {
	@Test
	public void testQuery() throws Exception {
		JsonArray createReportObject = new FriendshipActivityReporter()
				.createReportObject("http://instagram.com/aliyesilkanat");
		System.err.println(createReportObject);
	}
}
