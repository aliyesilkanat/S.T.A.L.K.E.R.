package com.aliyesilkanat.stalker.data;

import com.aliyesilkanat.stalker.data.connector.VirtuosoConnector;

public class RDFDataLayer {
	private static VirtuosoConnector instance;

	public static VirtuosoConnector getInstance() {
		if (instance == null) {
			instance = new VirtuosoConnector();
		}
		return instance;
	}
}
