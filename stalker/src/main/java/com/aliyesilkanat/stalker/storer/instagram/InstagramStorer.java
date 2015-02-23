package com.aliyesilkanat.stalker.storer.instagram;

import virtuoso.jena.driver.VirtGraph;

import com.aliyesilkanat.stalker.data.RDFDataLayer;
import com.aliyesilkanat.stalker.data.UnfinishedOperationException;
import com.aliyesilkanat.stalker.data.constants.FriendshipActivityLogConst;
import com.aliyesilkanat.stalker.storer.GraphConstants;
import com.aliyesilkanat.stalker.storer.Storer;
import com.aliyesilkanat.stalker.storer.friendshipactivity.FriendshipActivityMysqlOperation;
import com.aliyesilkanat.stalker.util.JsonLDUtils;
import com.aliyesilkanat.stalker.util.Tag;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hp.hpl.jena.graph.NodeFactory;
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

	@Override
	public void executeAction() throws UnfinishedOperationException {
		getLogger()
				.info(String.format("Storing data {\"userUri\"\"%s\"} ",
						getUserURI()));
		if (!hasNewFollowings(getAddedNewFollowings())) {
			String msg = "added new followings {\"jsonldArray\":\"%s\"}";
			getLogger().debug(String.format(msg, getAddedNewFollowings()));
			addNewFollowings(getAddedNewFollowings());
		}

		if (!isEmptyJsonArray(getDeletedFollowings())) {
			String msg = "deleted followings {\"jsonArray\":\"%s\"}";
			getLogger().debug(String.format(msg, getDeletedFollowings()));
			addDeletedFollowings(getDeletedFollowings());

			// delete following triple from rdf store...
			deleteFromRdfStore(getUserURI(), getDeletedFollowings(),
					chooseGraph());

		}
	}

	/**
	 * Checks if user has new followings.
	 * 
	 * @param userObject
	 *            User object contains follow predicate
	 * @return boolean result new followings or not.
	 */
	private boolean hasNewFollowings(JsonObject userObject) {
		return isEmptyJsonArray(userObject.get(Tag.FOLLOWS.text())
				.getAsJsonArray());
	}

	private void addDeletedFollowings(JsonArray deletedFollowings) {
		new FriendshipActivityMysqlOperation(
				FriendshipActivityLogConst.FRIENDSHIP_ACTIVITY_LOG_OPERATION_NAME,
				FriendshipActivityLogConst.FRIENDSHIP_ACTIVITY_LOG_TABLE_NAME,
				FriendshipActivityLogConst.ACTIVITY_NEW_FOLLOWING_DELETION,
				deletedFollowings, getUserURI()).execute();
	}

	private void addNewFollowings(JsonObject addedNewFollowings)
			throws UnfinishedOperationException {

		// convert given json ld to a rdf model....
		addToRdfStore(addedNewFollowings, chooseGraph());
		new FriendshipActivityMysqlOperation(
				FriendshipActivityLogConst.FRIENDSHIP_ACTIVITY_LOG_OPERATION_NAME,
				FriendshipActivityLogConst.FRIENDSHIP_ACTIVITY_LOG_TABLE_NAME,
				FriendshipActivityLogConst.ACTIVITY_NEW_FOLLOWING_ADDITION,
				addedNewFollowings.get(Tag.FOLLOWS.text()).getAsJsonArray(),
				getUserURI()).execute();
	}

	public String chooseGraph() {
		return GraphConstants.FRIENDSHIP_ACTIVITY;
	}

	public void addToRdfStore(JsonObject addedNewFollowings, String graphName)
			throws UnfinishedOperationException {
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

	public void deleteFromRdfStore(String userUri, JsonArray deletedFollowings,
			String graphUri) {
		String msg = "deleting followings from Rdf store {\"userUri\":\"%s\", \"deleted followings\",\"%s\"}";
		getLogger().info(
				String.format(msg, userUri, deletedFollowings.toString()));
		try {
			VirtGraph graph = RDFDataLayer.getInstance().createVirtGraph(
					graphUri);
			for (JsonElement userElement : deletedFollowings) {
				String deletedFollowingsUserUri = userElement.getAsString();
				if (getLogger().isTraceEnabled()) {
					msg = "deleting followings {\"userUri\":\"%s\", \"deletedUser\":\"%s\"}";
					getLogger().trace(
							String.format(msg, userUri,
									deletedFollowingsUserUri));
				}
				graph.remove(NodeFactory.createURI(userUri),
						NodeFactory.createURI("http://schema.org/follows"),
						NodeFactory.createURI(deletedFollowingsUserUri));
			}
		} catch (Exception e) {
			msg = "error while deleteing followings {\"userUri\":\"%s\", \"deleted followings\":\"%s\"}";
			getLogger().error(String.format(msg, userUri, deletedFollowings));
		}
		if (getLogger().isDebugEnabled()) {
			msg = "deleted followings {\"userUri\":\"%s\", \"followings\":\"%s\"}";
			getLogger().debug(String.format(msg, userUri, deletedFollowings));
		}

	}
}
