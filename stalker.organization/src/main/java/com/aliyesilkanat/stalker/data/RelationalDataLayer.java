package com.aliyesilkanat.stalker.data;

import com.aliyesilkanat.stalker.data.connector.MysqlConnectorFactory;

public class RelationalDataLayer {
	private static MysqlConnectorFactory instance;

	public static MysqlConnectorFactory getInstance() {
		if (instance == null) {
			instance = new MysqlConnectorFactory();
		}
		return instance;
	}

}
