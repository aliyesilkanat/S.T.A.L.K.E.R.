package com.aliyesilkanat.stalker.integration;

import static org.junit.Assert.*;

import org.junit.Test;

import com.aliyesilkanat.stalker.trigger.instagram.InstagramFollowingsTrigger;

public class TriggerToFetcher {
	@Test
	public void checkMultiPagedFollowings() throws Exception {
		InstagramFollowingsTrigger trigger = new InstagramFollowingsTrigger("239984780");
		trigger.execute();
	}
}
