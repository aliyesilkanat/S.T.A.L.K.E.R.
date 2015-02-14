package com.aliyesilkanat.stalker.storer.instagram;

import virtuoso.jena.driver.VirtGraph;

import com.aliyesilkanat.stalker.data.RDFDataLayer;
import com.aliyesilkanat.stalker.data.constants.FriendshipActivityLogConst;
import com.aliyesilkanat.stalker.storer.GraphConstants;
import com.aliyesilkanat.stalker.storer.Storer;
import com.aliyesilkanat.stalker.storer.friendshipactivity.FriendshipActivityMysqlOperation;
import com.aliyesilkanat.stalker.util.JsonLDUtils;
import com.google.gson.JsonArray;
import com.hp.hpl.jena.rdf.model.Model;

public class InstagramStorer extends Storer {
	public InstagramStorer(String addedNewFollowings, String userUri,
			String deletedNewFollowings) {
		super(addedNewFollowings, deletedNewFollowings, userUri);
	}

	public void fetchFriendsFromDb() {
		String query = "PREFIX schema: <http://schema.org/> "
				+ "PREFIX schema: <http://schema.org/>"
				+ "select * where {?s rdf:type schema:Person }";
	}

	public void store() {
		getLogger()
				.info(String.format("Storing data {\"userUri\"\"%s\"} ",
						getUserURI()));
		if (!isEmptyJsonArray(getAddedNewFollowings())) {
			String msg = "added new followings {\"jsonldArray\":\"%s\"}";
			getLogger().debug(String.format(msg, getAddedNewFollowings()));
			addNewFollowings(getAddedNewFollowings());
			// write content to virtuoso..
			// writeModel2Virtuoso(model, chooseGraph(model));
		}
		if (!isEmptyJsonArray(getDeletedFollowings())) {
			String msg = "deleted followings {\"jsonArray\":\"%s\"}";
			getLogger().debug(String.format(msg, getDeletedFollowings()));
			addDeletedFollowings(getDeletedFollowings());
		}
	}

	private void addDeletedFollowings(JsonArray deletedFollowings) {
		new FriendshipActivityMysqlOperation(
				FriendshipActivityLogConst.FRIENDSHIP_ACTIVITY_LOG_OPERATION_NAME,
				FriendshipActivityLogConst.FRIENDSHIP_ACTIVITY_LOG_TABLE_NAME,
				FriendshipActivityLogConst.ACTIVITY_NEW_FOLLOWING_DELETION,
				deletedFollowings, getUserURI()).execute();
	}

	private void addNewFollowings(JsonArray addedNewFollowings) {

		// convert given json ld to a rdf model....
		addToRdfStore(addedNewFollowings, chooseGraph());
		new FriendshipActivityMysqlOperation(
				FriendshipActivityLogConst.FRIENDSHIP_ACTIVITY_LOG_OPERATION_NAME,
				FriendshipActivityLogConst.FRIENDSHIP_ACTIVITY_LOG_TABLE_NAME,
				FriendshipActivityLogConst.ACTIVITY_NEW_FOLLOWING_ADDITION,
				addedNewFollowings, getUserURI()).execute();
	}

	public String chooseGraph() {
		return GraphConstants.FRIENDSHIP_ACTIVITY;
	}

	public void addToRdfStore(JsonArray addedNewFollowings, String graphName) {
		Model model = JsonLDUtils.convert2Model(addedNewFollowings.toString());
		RDFDataLayer.getInstance().writeModel2Virtuoso(model, graphName);
	}

	/**
	 * Compares a json array is empty or not...
	 * 
	 * @return is array empty?
	 */
	private boolean isEmptyJsonArray(JsonArray array) {
		return array.toString().equals("[]");
	}

	public void executeFollowingsChange() {
		store();
	}

	public void deleteFromRdfStore(JsonArray deletedFollowings, String graphUri) {
		VirtGraph graph = RDFDataLayer.getInstance().createVirtGraph(graphUri);
	}
}
