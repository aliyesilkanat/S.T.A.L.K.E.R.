package com.aliyesilkanat.stalker.data.transaction.relational;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.apache.jena.atlas.json.JsonObject;

public class JsonObjectAdapter implements Data {

	JsonObject object;
	public JsonObjectAdapter(JsonObject object, HashMap<String, String> mapping) {
		this.object = new JsonObject();
		Iterator<Entry<String, String>> it = mapping.entrySet().iterator();
		while(it.hasNext()){
			Entry<String, String> entry = it.next();
			this.object.put(entry.getKey(), object.get(entry.getValue()));
		}
	}

	@Override
	public Object getAttribute(String attributeName) {
		return object.get(attributeName).getAsString().value();
	}

	@Override
	public void setAttribute(String attributeName, Object value) {
		object.put(attributeName, value.toString());
	}

}
