package com.aliyesilkanat.stalker.tracker;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.aliyesilkanat.stalker.extractor.instagram.InstagramExtractor;
import com.aliyesilkanat.stalker.tracker.instagram.InstagramTracker;
import com.aliyesilkanat.stalker.util.JsonLDUtils;
import com.aliyesilkanat.stalker.util.Tag;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Model;

public class InstagramTrackerTest {
	private static final String EXPECTED_QUERY_RESULTS = "{\"head\":{\"vars\":[\"p\"]},\"results\":{\"bindings\":[{\"p\":{\"type\":\"uri\",\"value\":\"http://instagram.com/sahara_ray\"}},{\"p\":{\"type\":\"uri\",\"value\":\"http://instagram.com/capayto\"}}]}}";
	private static final String USER_URI_EXAMPLE = "http://instagram.com/aliyesilkanat";
	private static final String FETCHER_RESULT_EXAMPLE_1 = "[{\"username\":\"capayto\",\"bio\":\"\",\"website\":\"\",\"profile_picture\":\"https://igcdn-photos-d-a.akamaihd.net/hphotos-ak-xaf1/t51.2885-19/10890684_1555106351403643_757772799_a.jpg\",\"full_name\":\"Catherine Payton\",\"id\":\"4635551\"},{\"username\":\"sahara_ray\",\"bio\":\"ONE.1 management NY \\nsahara@kittenagency.com\\n@sahararayswim\",\"website\":\"http://www.sahararayswim.com\",\"profile_picture\":\"https://igcdn-photos-h-a.akamaihd.net/hphotos-ak-xaf1/t51.2885-19/10890693_1401134760183647_261151147_a.jpg\",\"full_name\":\"Sahara Ray\",\"id\":\"4154429\"}]";
	private static final String FETCHER_RESULT_EXAMPLE_2 = "[{\"username\":\"capayto\",\"bio\":\"\",\"website\":\"\",\"profile_picture\":\"https://igcdn-photos-d-a.akamaihd.net/hphotos-ak-xaf1/t51.2885-19/10890684_1555106351403643_757772799_a.jpg\",\"full_name\":\"Catherine Payton\",\"id\":\"4635551\"}]";
	private InstagramTracker tracker;
	private Model model;

	private ResultSet setExampleResultSet(String exampleJson) {
		String followingsAsJsonLdString = new InstagramExtractor(exampleJson,
				USER_URI_EXAMPLE).execute();
		JsonArray followingsAsJsonLd = new Gson().fromJson(
				followingsAsJsonLdString, JsonArray.class);
		JsonObject userObject = createExamplePersonJsonObject(followingsAsJsonLd);
		model = JsonLDUtils.convert2Model(userObject.toString());
		Query query = QueryFactory
				.create("PREFIX schema: <http://schema.org/> Select ?p where {<"
						+ USER_URI_EXAMPLE + "> schema:follows ?p } ");

		QueryExecution queryExec = QueryExecutionFactory.create(query, model);
		ResultSet resultSet = queryExec.execSelect();
		return resultSet;
	}

	private JsonObject createExamplePersonJsonObject(
			JsonArray followingsAsJsonLd) {
		JsonObject userJsonObject = new JsonObject();
		userJsonObject.addProperty(Tag.CONTEXT.text(), Tag.SCHEMA.text());
		userJsonObject.addProperty(Tag.ID.text(), USER_URI_EXAMPLE);
		userJsonObject.addProperty(Tag.TYPE.text(), Tag.PERSON.text());
		userJsonObject.add("follows", followingsAsJsonLd);
		return userJsonObject;
	}

	@Test
	public void exampleModelCreation() throws Exception {

		tracker = Mockito.spy(new InstagramTracker(FETCHER_RESULT_EXAMPLE_1,
				"239984780"));

		tracker.setUserURI(USER_URI_EXAMPLE);
		ResultSet resultSet = setExampleResultSet(FETCHER_RESULT_EXAMPLE_1);
		Mockito.doReturn(resultSet).when(tracker).getFollowingsFromRDFStore();
		Mockito.doReturn(USER_URI_EXAMPLE).when(tracker).retrieveUserURI();
		Mockito.doNothing().when(tracker)
				.send2Storer(Mockito.any(), Mockito.any());
		Query query = QueryFactory
				.create("PREFIX schema: <http://schema.org/> Select ?p where {<"
						+ USER_URI_EXAMPLE + "> schema:follows ?p } ");

		QueryExecution queryExec = QueryExecutionFactory.create(query, model);
		ResultSet resul2tSet = queryExec.execSelect();
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		ResultSetFormatter.outputAsJSON(b, resul2tSet);
		String json = b.toString("UTF-8");
		JsonObject fromJson = new Gson().fromJson(json, JsonObject.class);
		assertEquals(EXPECTED_QUERY_RESULTS, fromJson.toString());
	}

	/**
	 * Detects no change by Asserting sending to storer null arrays.
	 * 
	 * @throws Exception
	 */
	@Test
	public void detectNoChange() throws Exception {
		tracker = Mockito.spy(new InstagramTracker(FETCHER_RESULT_EXAMPLE_1,
				"239984780"));
		tracker.setUserURI(USER_URI_EXAMPLE);
		ResultSet resultSet = setExampleResultSet(FETCHER_RESULT_EXAMPLE_1);
		Mockito.doReturn(resultSet).when(tracker).getFollowingsFromRDFStore();
		Mockito.doReturn(USER_URI_EXAMPLE).when(tracker).retrieveUserURI();
		Mockito.doNothing().when(tracker)
				.send2Storer(Mockito.any(), Mockito.any());
		tracker.catchChange();
		Mockito.verify(tracker).send2Storer("[]", "[]");
	}

	@Test
	public void detectsNewAddition() throws Exception {
		tracker = Mockito.spy(new InstagramTracker(FETCHER_RESULT_EXAMPLE_1,
				"239984780"));
		tracker.setUserURI(USER_URI_EXAMPLE);
		ResultSet resultSet = setExampleResultSet(FETCHER_RESULT_EXAMPLE_2);
		Mockito.doReturn(resultSet).when(tracker).getFollowingsFromRDFStore();
		Mockito.doReturn(USER_URI_EXAMPLE).when(tracker).retrieveUserURI();
		Mockito.doNothing().when(tracker)
				.send2Storer(Mockito.any(), Mockito.any());
		tracker.catchChange();
		Mockito.verify(tracker)
				.send2Storer(
						"[]",
						"[{\"@context\":\"http://schema.org/\",\"@id\":\"http://instagram.com/sahara_ray\",\"@type\":\"Person\",\"name\":\"Sahara Ray\",\"image\":\"https://igcdn-photos-h-a.akamaihd.net/hphotos-ak-xaf1/t51.2885-19/10890693_1401134760183647_261151147_a.jpg\"}]");
	}

	@Test
	public void detectsFollowingDeletion() throws Exception {
		tracker = Mockito.spy(new InstagramTracker(FETCHER_RESULT_EXAMPLE_2,
				"239984780"));
		tracker.setUserURI(USER_URI_EXAMPLE);
		ResultSet resultSet = setExampleResultSet(FETCHER_RESULT_EXAMPLE_1);
		Mockito.doReturn(resultSet).when(tracker).getFollowingsFromRDFStore();
		Mockito.doReturn(USER_URI_EXAMPLE).when(tracker).retrieveUserURI();
		Mockito.doNothing().when(tracker)
				.send2Storer(Mockito.any(), Mockito.any());
		tracker.catchChange();
		Mockito.verify(tracker).send2Storer(
				"[\"http://instagram.com/sahara_ray\"]", "[]");
	}
}
