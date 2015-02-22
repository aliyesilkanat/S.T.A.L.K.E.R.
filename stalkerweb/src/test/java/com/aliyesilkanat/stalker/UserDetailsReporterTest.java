package com.aliyesilkanat.stalker;

import static org.junit.Assert.*;

import org.junit.Test;

import com.aliyesilkanat.stalker.UserDetailsReporter;
import com.google.gson.JsonObject;

public class UserDetailsReporterTest {

	@Test
	public void checkQuery() throws Exception {
		JsonObject jsonObject = new UserDetailsReporter()
				.getDetails("http://instagram.com/aliyesilkanat");
		assertEquals("Ali Yeþilkanat", jsonObject.get("name").getAsString());
	}
}
