package com.aliyesilkanat.stalker.storer;

import static org.junit.Assert.*;

import org.junit.Test;

import com.aliyesilkanat.stalker.storer.instagram.InstagramStorer;

public class InstagramStorerTest {
	@Test
	// WARNING - THIS INSERTS VALUES INTO DB, SO execute it wisely!
	public void insertValuesIntoDb() throws Exception {
		new InstagramStorer("", "http://instagram.com/aliyesilkanat");
	}
}
