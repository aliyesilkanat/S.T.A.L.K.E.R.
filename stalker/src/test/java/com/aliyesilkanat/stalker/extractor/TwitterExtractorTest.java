package com.aliyesilkanat.stalker.extractor;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.aliyesilkanat.stalker.extractor.twitter.TwitterExtractor;
import com.aliyesilkanat.stalker.util.FileUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;


/**
 * @author Iþýk Erhan
 */

public class TwitterExtractorTest {
	private String originalJson;
	private String jsonLd;
	private static final String userID = "3032812463";
	
	@Before
	public void setUp() {
		originalJson = FileUtil.readFile("testHtml\\followingsTwitter.json");
		jsonLd = FileUtil.readFile("testHtml\\followingsTwitterLd.json");
	}
	
	@Test
	public void testName() throws Exception {
		Extractor extractor = new TwitterExtractor(originalJson, userID);
		extractor.getLogger().info("Starting execution");
		extractor.execute();
		JsonArray result = extractor.getFriendsArrayLD();
		JsonArray expected = new JsonParser().parse(jsonLd).getAsJsonArray();
		Assert.assertEquals(expected, result);
	}
}
