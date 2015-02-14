package com.aliyesilkanat.stalker.storer.instagram;

import com.aliyesilkanat.stalker.data.constants.FriendshipActivityLogConst;
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
		if (!isEmptyJsonArray()) {
			String msg = "added new followings {\"jsonldArray\":\"%s\"}";
			getLogger().debug(String.format(msg, getAddedNewFollowings()));

			addNewFollowings(getAddedNewFollowings());
		}
		// write content to virtuoso..
		// writeModel2Virtuoso(model, chooseGraph(model));
	}

	private void addNewFollowings(JsonArray addedNewFollowings) {

		// convert given json ld to a rdf model....
		// Model model =
		// JsonLDUtils.convert2Model(addedNewFollowings.toString());
		new FriendshipActivityMysqlOperation(
				FriendshipActivityLogConst.FRIENDSHIP_ACTIVITY_LOG_OPERATION_NAME,
				FriendshipActivityLogConst.FRIENDSHIP_ACTIVITY_LOG_TABLE_NAME,
				FriendshipActivityLogConst.ACTIVITY_NEW_FOLLOWING_ADDITION,
				addedNewFollowings).execute();
	}

	/**
	 * Compares a json array is empty or not...
	 * 
	 * @return is array empty?
	 */
	private boolean isEmptyJsonArray() {
		return getAddedNewFollowings().toString().equals("[]");
	}

	public void executeFollowingsChange() {
		store();
	}
}
