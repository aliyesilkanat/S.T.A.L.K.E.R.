package com.aliyesilkanat.stalker.storer.instagram;

import com.aliyesilkanat.stalker.storer.Storer;
import com.aliyesilkanat.stalker.util.JsonLDUtils;
import com.hp.hpl.jena.rdf.model.Model;

public class InstagramStorer extends Storer {
	public InstagramStorer(String content, String userId) {
		super(content, userId);
	}

	public void fetchFriendsFromDb() {
		String query = "PREFIX schema: <http://schema.org/> "
				+ "PREFIX schema: <http://schema.org/>"
				+ "select * where {?s rdf:type schema:Person }";
	}

	public void catchContent() {
		getLogger().debug(
				String.format("Catching content {\"%s\"} ", getContent()));
		// convert given json ld to a rdf model....
		Model model = JsonLDUtils.convert2Model(getContent());
		// write content to virtuoso..
		// writeModel2Virtuoso(model, chooseGraph(model));
	}
}
