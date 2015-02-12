package com.aliyesilkanat.stalker.extractor;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.aliyesilkanat.stalker.extractor.instagram.InstagramExtractor;
import com.aliyesilkanat.stalker.util.FileUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class InstagramExtractorTest {
	JsonArray sampleFollowingsLd;
	@Before
	public void setUp() {
		String jSONAsString = FileUtil.readFile("testHtml\\followingsLd.json");
		sampleFollowingsLd = new JsonParser().parse(jSONAsString).getAsJsonArray();
	}
	@Test
	public void testExecution() throws Exception {
		JsonParser parser = new JsonParser();
		JsonObject element = parser.parse(FileUtil.readFile("testHtml/followings.json")).getAsJsonObject();
		JsonArray friends = element.get("data").getAsJsonArray();
		Extractor extractor = new InstagramExtractor(friends.toString());
		extractor.execute();
		JsonArray result = extractor.getFriendsArrayLD();
		Assert.assertEquals(sampleFollowingsLd, result);
	}
}
