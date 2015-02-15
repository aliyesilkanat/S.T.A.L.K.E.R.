package com.aliyesilkanat.stalker.extractor;

import static org.junit.Assert.assertEquals;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.aliyesilkanat.stalker.extractor.instagram.InstagramExtractor;
import com.aliyesilkanat.stalker.util.FileUtil;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class InstagramExtractorTest {

	JsonArray sampleFollowingsLd;

	@Before
	public void setUp() {
		String jSONAsString = FileUtil.readFile("testHtml\\followingsLd.json");
		sampleFollowingsLd = new JsonParser().parse(jSONAsString)
				.getAsJsonArray();
	}

	@Test
	public void testExecution() throws Exception {
		JsonParser parser = new JsonParser();
		JsonObject element = parser.parse(
				FileUtil.readFile("testHtml/followings.json"))
				.getAsJsonObject();
		JsonArray friends = element.get("data").getAsJsonArray();
		Extractor extractor = new InstagramExtractor(
				friends.toString(),
				"239984780",
				"{\"username\":\"aliyesilkanat\",\"bio\":\"\",\"website\":\"\",\"profile_picture\":\"https:\\/\\/instagramimages-a.akamaihd.net\\/profiles\\/profile_239984780_75sq_1350721189.jpg\",\"full_name\":\"Ali Ye\\u015filkanat\",\"counts\":{\"media\":16,\"followed_by\":114,\"follows\":64},\"id\":\"239984780\"}");
		extractor.execute();
		JsonArray result = extractor.getFriendsArrayLD();
		Assert.assertEquals(sampleFollowingsLd.toString(), result.toString());
	}

	@Test
	public void checkFillJsonLdObject() throws Exception {
		JsonObject object = new InstagramExtractor("", "", "")
				.fillJsonLdObject(new Gson()
						.fromJson(
								"{\"username\":\"aliyesilkanat\",\"bio\":\"\",\"website\":\"\",\"profile_picture\":\"https:\\/\\/instagramimages-a.akamaihd.net\\/profiles\\/profile_239984780_75sq_1350721189.jpg\",\"full_name\":\"Ali Ye\\u015filkanat\",\"counts\":{\"media\":16,\"followed_by\":114,\"follows\":64},\"id\":\"239984780\"}",
								JsonObject.class));
		assertEquals(
				"{\"@context\":\"http://schema.org/\",\"@id\":\"http://instagram.com/aliyesilkanat\",\"@type\":\"Person\",\"name\":\"Ali Ye\u015Filkanat\",\"image\":\"https://instagramimages-a.akamaihd.net/profiles/profile_239984780_75sq_1350721189.jpg\"}",
				object.toString());
	}
}
