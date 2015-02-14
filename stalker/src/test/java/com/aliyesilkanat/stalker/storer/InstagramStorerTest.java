package com.aliyesilkanat.stalker.storer;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.mockito.Mockito;

import virtuoso.jena.driver.VirtGraph;

import com.aliyesilkanat.stalker.data.RDFDataLayer;
import com.aliyesilkanat.stalker.storer.instagram.InstagramStorer;
import com.aliyesilkanat.stalker.util.JsonLDUtils;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;

public class InstagramStorerTest {
	private static final String EXAMPLE_ADDED_NEW_FOLLOWINGS = ""
			+ "[{\"@context\":\"http://schema.org/\","
			+ "\"@id\":\"http://instagram.com/gokce_dmr\","
			+ "\"@type\":\"Person\",\"name\":\"Gökçe Demir\",\"image\":\"https://igcdn-photos-d-a.akamaihd.net/hphotos-ak-xfa1/t51.2885-19/10894913_914936041864403_1107920400_a.jpg\"},{\"@context\":\"http://schema.org/\",\"@id\":\"http://instagram.com/capayto\",\"@type\":\"Person\",\"name\":\"Catherine Payton\",\"image\":\"https://igcdn-photos-d-a.akamaihd.net/hphotos-ak-xaf1/t51.2885-19/10890684_1555106351403643_757772799_a.jpg\"}]";
	private InstagramStorer storer;

	@Test
	public void testCharacterInModels() throws Exception {
		Model convert2Model = JsonLDUtils
				.convert2Model(EXAMPLE_ADDED_NEW_FOLLOWINGS);
		assertEquals(
				"<ModelCom   {http://instagram.com/gokce_dmr @http://www.w3.org/1999/02/22-rdf-syntax-ns#type http://schema.org/Person; http://instagram.com/gokce_dmr @http://schema.org/name \"G\u00F6k\u00E7e Demir\"; http://instagram.com/gokce_dmr @http://schema.org/image https://igcdn-photos-d-a.akamaihd.net/hphotos-ak-xfa1/t51.2885-19/10894913_914936041864403_1107920400_a.jpg; http://instagram.com/capayto @http://www.w3.org/1999/02/22-rdf-syntax-ns#type http://schema.org/Person; http://instagram.com/capayto @http://schema.org/name \"Catherine Payton\"; http://instagram.com/capayto @http://schema.org/image https://igcdn-photos-d-a.akamaihd.net/hphotos-ak-xaf1/t51.2885-19/10890684_1555106351403643_757772799_a.jpg} |  [http://instagram.com/gokce_dmr, http://www.w3.org/1999/02/22-rdf-syntax-ns#type, http://schema.org/Person] [http://instagram.com/gokce_dmr, http://schema.org/name, \"G\u00F6k\u00E7e Demir\"] [http://instagram.com/gokce_dmr, http://schema.org/image, https://igcdn-photos-d-a.akamaihd.net/hphotos-ak-xfa1/t51.2885-19/10894913_914936041864403_1107920400_a.jpg] [http://instagram.com/capayto, http://www.w3.org/1999/02/22-rdf-syntax-ns#type, http://schema.org/Person] [http://instagram.com/capayto, http://schema.org/name, \"Catherine Payton\"] [http://instagram.com/capayto, http://schema.org/image, https://igcdn-photos-d-a.akamaihd.net/hphotos-ak-xaf1/t51.2885-19/10890684_1555106351403643_757772799_a.jpg]>",
				convert2Model.toString());
	}

	@Test
	public void addToRdfStore() throws Exception {

		// setup
		// clearing graph...
		VirtGraph graph = RDFDataLayer.getInstance().createVirtGraph(
				GraphConstants.TEST_GRAPH);
		graph.clear();
		storer = Mockito.spy(new InstagramStorer(EXAMPLE_ADDED_NEW_FOLLOWINGS,
				"http//instagram.com/aliyesilkanat", "[]"));
		Mockito.doReturn(GraphConstants.TEST_GRAPH).when(storer).chooseGraph();

		// execute
		storer.addToRdfStore(storer.getAddedNewFollowings(),
				GraphConstants.TEST_GRAPH);

		// assert
		// querying additions...
		List<String> checkingList = fillExpectedTriplesList();
		String query = "select * where {graph <" + GraphConstants.TEST_GRAPH
				+ ">{?s ?p ?o }}";
		ResultSet execSelect = RDFDataLayer.getInstance().execSelect(query);
		while (execSelect.hasNext()) {
			QuerySolution next = execSelect.next();
			String uri = next.get("s").toString();
			String predicate = next.get("p").toString();
			String object = next.get("o").toString();
			System.out.println(uri + " " + predicate + " " + object);
			assertTrue(checkingList.contains(uri + " " + predicate + " "
					+ object));
		}
	}

	@Test
	public void deleteFromRdfStore() throws Exception {
		// setup
		// clearing graph...
		VirtGraph graph = RDFDataLayer.getInstance().createVirtGraph(
				GraphConstants.TEST_GRAPH);
		graph.clear();
		storer = Mockito.spy(new InstagramStorer(EXAMPLE_ADDED_NEW_FOLLOWINGS,
				"http//instagram.com/aliyesilkanat",
				"[http://instagram.com/gokce_dmr]"));
		Mockito.doReturn(GraphConstants.TEST_GRAPH).when(storer).chooseGraph();

		storer.addToRdfStore(storer.getAddedNewFollowings(),
				GraphConstants.TEST_GRAPH);

		storer.deleteFromRdfStore(storer.getDeletedFollowings(),
				GraphConstants.TEST_GRAPH);
	}

	private List<String> fillExpectedTriplesList() {
		List<String> checkingList = new ArrayList<String>();
		checkingList
				.add("http://instagram.com/gokce_dmr http://www.w3.org/1999/02/22-rdf-syntax-ns#type http://schema.org/Person");
		checkingList
				.add("http://instagram.com/capayto http://www.w3.org/1999/02/22-rdf-syntax-ns#type http://schema.org/Person");
		checkingList
				.add("http://instagram.com/capayto http://schema.org/name Catherine Payton");
		checkingList
				.add("http://instagram.com/gokce_dmr http://schema.org/name Gökçe Demir");
		checkingList
				.add("http://instagram.com/gokce_dmr http://schema.org/image https://igcdn-photos-d-a.akamaihd.net/hphotos-ak-xfa1/t51.2885-19/10894913_914936041864403_1107920400_a.jpg");
		checkingList
				.add("http://instagram.com/capayto http://schema.org/image https://igcdn-photos-d-a.akamaihd.net/hphotos-ak-xaf1/t51.2885-19/10890684_1555106351403643_757772799_a.jpg");
		return checkingList;
	}
}
