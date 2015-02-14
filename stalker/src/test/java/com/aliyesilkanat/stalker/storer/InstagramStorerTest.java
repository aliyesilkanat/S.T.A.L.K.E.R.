package com.aliyesilkanat.stalker.storer;

import static org.junit.Assert.*;

import org.junit.Test;

import riotcmd.json;

import com.aliyesilkanat.stalker.storer.instagram.InstagramStorer;
import com.aliyesilkanat.stalker.util.JsonLDUtils;
import com.google.gson.JsonArray;
import com.hp.hpl.jena.rdf.model.Model;

public class InstagramStorerTest {
	private static final String EXAMPLE_ADDED_NEW_FOLLOWINGS = "[{\"@context\":\"http://schema.org/\",\"@id\":\"http://instagram.com/gokce_dmr\",\"@type\":\"Person\",\"name\":\"G\u00F6k\u00E7e Demir\",\"image\":\"https://igcdn-photos-d-a.akamaihd.net/hphotos-ak-xfa1/t51.2885-19/10894913_914936041864403_1107920400_a.jpg\"},{\"@context\":\"http://schema.org/\",\"@id\":\"http://instagram.com/capayto\",\"@type\":\"Person\",\"name\":\"Catherine Payton\",\"image\":\"https://igcdn-photos-d-a.akamaihd.net/hphotos-ak-xaf1/t51.2885-19/10890684_1555106351403643_757772799_a.jpg\"}]";
	private InstagramStorer storer;

	@Test
	public void insertValuesIntoDb() throws Exception {
	}

	@Test
	public void addNewFollowings() throws Exception {
		storer = new InstagramStorer(EXAMPLE_ADDED_NEW_FOLLOWINGS, "[]",
				"http://instagram.com/aliyesilkanat");
		storer.executeFollowingsChange();
	}

}
