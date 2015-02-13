package com.aliyesilkanat.stalker.tracker;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;

import org.junit.Before;
import org.junit.Ignore;
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
	private static final String FETCHER_RESULT_EXAMPLE = "[{\"username\":\"capayto\",\"bio\":\"\",\"website\":\"\",\"profile_picture\":\"https://igcdn-photos-d-a.akamaihd.net/hphotos-ak-xaf1/t51.2885-19/10890684_1555106351403643_757772799_a.jpg\",\"full_name\":\"Catherine Payton\",\"id\":\"4635551\"},{\"username\":\"sahara_ray\",\"bio\":\"ONE.1 management NY \\nsahara@kittenagency.com\\n@sahararayswim\",\"website\":\"http://www.sahararayswim.com\",\"profile_picture\":\"https://igcdn-photos-h-a.akamaihd.net/hphotos-ak-xaf1/t51.2885-19/10890693_1401134760183647_261151147_a.jpg\",\"full_name\":\"Sahara Ray\",\"id\":\"4154429\"}]";
	private InstagramTracker tracker;
	private Model model;

	@Before
	public void before() {
		tracker = Mockito.spy(new InstagramTracker(FETCHER_RESULT_EXAMPLE,
				"239984780"));
		ResultSet resultSet = setExampleResultSet();
		Mockito.doReturn(resultSet).when(tracker).getFollowingsFromRDFStore();
		Mockito.doReturn(USER_URI_EXAMPLE).when(tracker).retrieveUserURI();
		tracker.setUserURI(USER_URI_EXAMPLE);
	}

	private ResultSet setExampleResultSet() {
		String followingsAsJsonLdString = new InstagramExtractor(
				FETCHER_RESULT_EXAMPLE, USER_URI_EXAMPLE).execute();
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
		Query query = QueryFactory
				.create("PREFIX schema: <http://schema.org/> Select ?p where {<"
						+ USER_URI_EXAMPLE + "> schema:follows ?p } ");

		QueryExecution queryExec = QueryExecutionFactory.create(query, model);
		ResultSet resultSet = queryExec.execSelect();
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		ResultSetFormatter.outputAsJSON(b, resultSet);
		String json = b.toString("UTF-8");
		JsonObject fromJson = new Gson().fromJson(json, JsonObject.class);
		assertEquals(EXPECTED_QUERY_RESULTS, fromJson.toString());
	}

	@Test
	public void catchChange() throws Exception {
		tracker.catchChange();

	}
}
