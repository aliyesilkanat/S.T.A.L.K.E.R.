package com.aliyesilkanat.stalker.storer.friendshipactivity;

import com.aliyesilkanat.stalker.data.constants.FriendshipActivityLogConst;
import com.aliyesilkanat.stalker.storer.relational.RelationalOperation;

public class FriendshipActivityContext {

	private RelationalOperation context;

	public FriendshipActivityContext(int activity) {
		if (activity == FriendshipActivityLogConst.ACTIVITY_NEW_FOLLOWING_ADDITION) {
			this.setContext(new FriendshipActivityAddition());
		} else if (activity == FriendshipActivityLogConst.ACTIVITY_NEW_FOLLOWING_DELETION) {
			this.setContext(new FriendshipActivityDeletion());
		}
	}

	public RelationalOperation getContext() {
		return context;
	}

	public void setContext(RelationalOperation context) {
		this.context = context;
	}

}
