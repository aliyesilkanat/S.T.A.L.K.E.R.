package com.aliyesilkanat.stalker.data.transaction.relational;

public interface Data {
	Object getAttribute(String attributeName);
	void setAttribute(String attributeName, Object value);
}
