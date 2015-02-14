package com.aliyesilkanat.stalker.storer.friendshipactivity;

import org.apache.jena.iri.impl.Main;
import org.junit.Test;
import org.mockito.Mockito;

import com.aliyesilkanat.stalker.data.constants.FriendshipActivityLogConst;
import com.google.gson.Gson;
import com.google.gson.JsonArray;

public class FriendshipActivityActionTest {
	private static final String EXAMPLE_ADDED_NEW_FOLLOWINGS = "[{\"@context\":\"http://schema.org/\",\"@id\":\"http://instagram.com/gokce_dmr\",\"@type\":\"Person\",\"name\":\"G\u00F6k\u00E7e Demir\",\"image\":\"https://igcdn-photos-d-a.akamaihd.net/hphotos-ak-xfa1/t51.2885-19/10894913_914936041864403_1107920400_a.jpg\"},{\"@context\":\"http://schema.org/\",\"@id\":\"http://instagram.com/capayto\",\"@type\":\"Person\",\"name\":\"Catherine Payton\",\"image\":\"https://igcdn-photos-d-a.akamaihd.net/hphotos-ak-xaf1/t51.2885-19/10890684_1555106351403643_757772799_a.jpg\"}]";
	private static final String EXAMPLE_DELETED_FOLLOWINGS = "[\"http://instagram.com/gokce_dmr\",\"http://instagram.com/capayto\"]";

	@Test
	public void testQueryOfNewFollowingsAddition() throws Exception {

		FriendshipActivityAction addition = Mockito
				.spy(new FriendshipActivityAction(
						FriendshipActivityLogConst.ACTIVITY_NEW_FOLLOWING_ADDITION));
		Mockito.doNothing().when(addition)
				.executeQuery(Mockito.any(), Mockito.any());
		addition.execute(null, new Gson().fromJson(
				EXAMPLE_ADDED_NEW_FOLLOWINGS, JsonArray.class),
				"http://instagram.com/aliyesilkanat");
		Mockito.verify(addition, Mockito.atLeastOnce()).createSQLQuery(
				"http://instagram.com/aliyesilkanat",
				"http://instagram.com/gokce_dmr",
				FriendshipActivityLogConst.FRIENDSHIP_ACTIVITY_LOG_TABLE_NAME,
				FriendshipActivityLogConst.ACTIVITY_NEW_FOLLOWING_ADDITION);
		Mockito.verify(addition, Mockito.atLeastOnce()).createSQLQuery(
				"http://instagram.com/aliyesilkanat",
				"http://instagram.com/capayto",
				FriendshipActivityLogConst.FRIENDSHIP_ACTIVITY_LOG_TABLE_NAME,
				FriendshipActivityLogConst.ACTIVITY_NEW_FOLLOWING_ADDITION);
	}

	@Test
	public void testQueryOfFollowingsDeletion() throws Exception {
		FriendshipActivityAction addition = Mockito
				.spy(new FriendshipActivityAction(
						FriendshipActivityLogConst.ACTIVITY_NEW_FOLLOWING_DELETION));
		Mockito.doNothing().when(addition)
				.executeQuery(Mockito.any(), Mockito.any());
		addition.execute(null, new Gson().fromJson(EXAMPLE_DELETED_FOLLOWINGS,
				JsonArray.class), "http://instagram.com/aliyesilkanat");
		Mockito.verify(addition, Mockito.atLeastOnce()).createSQLQuery(
				"http://instagram.com/aliyesilkanat",
				"http://instagram.com/gokce_dmr",
				FriendshipActivityLogConst.FRIENDSHIP_ACTIVITY_LOG_TABLE_NAME,
				FriendshipActivityLogConst.ACTIVITY_NEW_FOLLOWING_DELETION);
		Mockito.verify(addition, Mockito.atLeastOnce()).createSQLQuery(
				"http://instagram.com/aliyesilkanat",
				"http://instagram.com/capayto",
				FriendshipActivityLogConst.FRIENDSHIP_ACTIVITY_LOG_TABLE_NAME,
				FriendshipActivityLogConst.ACTIVITY_NEW_FOLLOWING_DELETION);
	}
}
