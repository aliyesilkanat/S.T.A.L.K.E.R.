package com.aliyesilkanat.stalker.integration;

import static org.junit.Assert.*;

import org.junit.Test;

import com.aliyesilkanat.stalker.trigger.instagram.FollowingsTrigger;

public class AllSystemInstagram {
	@Test
	public void checkIntegration() throws Exception {
		new FollowingsTrigger("239984780").execute();
	}
}
