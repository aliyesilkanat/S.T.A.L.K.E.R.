package com.aliyesilkanat.stalker.integration;

import static org.junit.Assert.*;

import org.junit.Test;

import com.aliyesilkanat.stalker.trigger.instagram.FollowingsTrigger;

public class TriggerToFetcher {
	@Test
	public void checkMultiPagedFollowings() throws Exception {
		FollowingsTrigger trigger = new FollowingsTrigger("239984780");
		trigger.execute();
	}
}
