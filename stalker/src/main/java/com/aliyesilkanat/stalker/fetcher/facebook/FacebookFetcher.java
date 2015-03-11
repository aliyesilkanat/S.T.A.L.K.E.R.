package com.aliyesilkanat.stalker.fetcher.facebook;

import com.aliyesilkanat.stalker.data.UnfinishedOperationException;
import com.aliyesilkanat.stalker.fetcher.Fetcher;

import facebook4j.Facebook;
import facebook4j.FacebookFactory;
import facebook4j.auth.AccessToken;

public class FacebookFetcher extends Fetcher {

	private static final String PERMISSIONS = "public_profile,user_friends";
	private static final String ACCESS_TOKEN = "CAAIxlc9USw4BAOuGRGoVo8ZAIc1q97voy9ljksBHybIFkZCxH7ycreOZBaZCwWZAOBikNfP7N2r3wCVEW9LwIPgn8ZBWPZAjpwZAxwy5iKCbhFwLxlXRzUEjo3bu8gZC6wuLr2iXUs7vjffLPrkUlDTZCgDyV5KN5VBIYXkqmZBih0xrk5WIDa6x5vRdj2C00cZAki8a0O3qqcfl0F2Cxia9hQmZA";
	private static final String CLIENT_SECRET = "aedbaf5c46111337057328e796cdc603";
	private static final String CLIENT_ID = "617469451717390";

	public FacebookFetcher(String userId) {
		super(userId);
	}

	@Override
	protected void executeAction() throws UnfinishedOperationException {
		createFacebookInstance();
	}

	public Facebook createFacebookInstance() {
		Facebook facebook = new FacebookFactory().getInstance();
		facebook.setOAuthAppId(CLIENT_ID, CLIENT_SECRET);
		facebook.setOAuthPermissions(PERMISSIONS);
		facebook.setOAuthAccessToken(new AccessToken(ACCESS_TOKEN, null));
		return facebook;
	}

}
