package com.aliyesilkanat.stalker.fetcher;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.aliyesilkanat.stalker.endpoint.EndpointUtils;
import com.aliyesilkanat.stalker.fetcher.instagram.InstagramFetcher;
import com.aliyesilkanat.stalker.util.FileUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class InstagramFetcherTest {
	private InstagramFetcher fetcher;

	@Before
	public void before() {
		fetcher = Mockito.spy(new InstagramFetcher());
	}

	@Test
	public void fetchFollowingsOfAUser() throws Exception {
		String file = FileUtil.readFile("testHtml/followings.json");
		Mockito.doReturn(new Gson().fromJson(file, JsonObject.class))
				.when(fetcher).getJsonFromApi(Mockito.anyString());

		String fetchFriends = fetcher.requestFollowings("239984780");
		assertEquals(
				"[{\"username\":\"summervibing\",\"bio\":\"Australia. Things that inspire the wild at heart.\",\"website\":\"\",\"profile_picture\":\"https://igcdn-photos-f-a.akamaihd.net/hphotos-ak-xaf1/t51.2885-19/10932498_2031817810292269_995097487_a.jpg\",\"full_name\":\"POSITIVE Vibes \u2728\uD83C\uDF1F\u2728\",\"id\":\"1481973879\"},{\"username\":\"michelle_lewin_\",\"bio\":\"BUSINESS ONLY: MichelleLewinManagement@gmail.com (not handled by me)\",\"website\":\"https://m.youtube.com/watch?v\\u003d8vOXSm9a4xU\",\"profile_picture\":\"https://igcdn-photos-h-a.akamaihd.net/hphotos-ak-xfa1/t51.2885-19/10950500_585916678175183_1517908897_a.jpg\",\"full_name\":\"Michelle Lewin #lacuerpa1\u20E3\",\"id\":\"775740654\"}]",
				fetchFriends);
	}

	@Test
	public void fetchFollowingsOfAUserWithSecondCursor() throws Exception {
		String fileWithoutCursor = FileUtil
				.readFile("testHtml/followings.json");
		String cursor = FileUtil.readFile("testHtml/followingsWithCursor.json");
		Mockito.doReturn(
				new Gson().fromJson(fileWithoutCursor, JsonObject.class))
				.when(fetcher).getJsonFromApi(Mockito.endsWith("cursor=1398087631922"));
		Mockito.doReturn(new Gson().fromJson(cursor, JsonObject.class))
				.when(fetcher)
				.getJsonFromApi(
						Mockito.endsWith("239984780.fe09684.e9be582082be4858a9bdb170c9007774"));
		String fetchFollowings = fetcher.requestFollowings("239984780");
		assertEquals(
				"[{\"username\":\"sahara_ray\",\"bio\":\"ONE.1 management NY \\nsahara@kittenagency.com\\n@sahararayswim\",\"website\":\"http://www.sahararayswim.com\",\"profile_picture\":\"https://igcdn-photos-h-a.akamaihd.net/hphotos-ak-xaf1/t51.2885-19/10890693_1401134760183647_261151147_a.jpg\",\"full_name\":\"Sahara Ray\",\"id\":\"4154429\"},{\"username\":\"yavuzozturk\",\"bio\":\"\",\"website\":\"http://yavuzozturk.tumblr.com\",\"profile_picture\":\"https://igcdn-photos-g-a.akamaihd.net/hphotos-ak-xap1/t51.2885-19/10809681_785204641541926_1283549945_a.jpg\",\"full_name\":\"yavuz ozturk\",\"id\":\"32515108\"},{\"username\":\"summervibing\",\"bio\":\"Australia. Things that inspire the wild at heart.\",\"website\":\"\",\"profile_picture\":\"https://igcdn-photos-f-a.akamaihd.net/hphotos-ak-xaf1/t51.2885-19/10932498_2031817810292269_995097487_a.jpg\",\"full_name\":\"POSITIVE Vibes \u2728\uD83C\uDF1F\u2728\",\"id\":\"1481973879\"},{\"username\":\"michelle_lewin_\",\"bio\":\"BUSINESS ONLY: MichelleLewinManagement@gmail.com (not handled by me)\",\"website\":\"https://m.youtube.com/watch?v\\u003d8vOXSm9a4xU\",\"profile_picture\":\"https://igcdn-photos-h-a.akamaihd.net/hphotos-ak-xfa1/t51.2885-19/10950500_585916678175183_1517908897_a.jpg\",\"full_name\":\"Michelle Lewin #lacuerpa1\u20E3\",\"id\":\"775740654\"}]",
				fetchFollowings);
	}
}
