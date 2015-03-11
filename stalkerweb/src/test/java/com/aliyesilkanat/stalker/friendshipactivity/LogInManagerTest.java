package com.aliyesilkanat.stalker.friendshipactivity;

import static org.junit.Assert.*;

import org.junit.Test;

import com.aliyesilkanat.stalker.LogInManager;

public class LogInManagerTest {
	@Test
	public void logIn() throws Exception {
		assertEquals("{\"email\":\"aliyesilkanat@gmail.com\"}", LogInManager
				.getInstance().logIn("aliyesilkanat@gmail.com", "123456a"));
	}
}
