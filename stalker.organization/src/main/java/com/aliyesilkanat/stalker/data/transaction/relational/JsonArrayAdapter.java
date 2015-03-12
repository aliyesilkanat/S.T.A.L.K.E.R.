package com.aliyesilkanat.stalker.data.transaction.relational;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.jena.atlas.json.JsonArray;
import org.apache.jena.atlas.json.JsonObject;
import org.apache.jena.atlas.json.JsonValue;

public class JsonArrayAdapter implements DataSet{
	
	private ArrayList<Data> data;
	private ArrayList<String> attributeNames;
	private String relationName;
	/**
	 * 
	 * @param array JsonArray to be adapted
	 * @param mapping Attribute mapping. <pre><strong><i>	Keys</i></strong> represent attribute names in database, 
	 * <strong><i>	Values</i></strong> represent attribute name in Data object</pre>
	 * @param relationName Relation(table) name of data on database model
	 */
	public JsonArrayAdapter(JsonArray array, HashMap<String, String> mapping, String relationName) {
		this.relationName = relationName;
		
		data = new ArrayList<Data>();
		Iterator<JsonValue> it = array.iterator();
		while(it.hasNext()){
			JsonObject object = it.next().getAsObject();
			data.add(new JsonObjectAdapter(object, mapping));
		}
		
		attributeNames = new ArrayList<String>();
		Iterator<String> it2 = mapping.keySet().iterator();
		while (it2.hasNext())
			attributeNames.add(it2.next());
	}
	@Override
	public int size(){
		return data.size();
	}
	@Override
	public String getRelationName(){
		return relationName;
	}

	@Override
	public ArrayList<String> getAttributeNames(){
		return attributeNames;
	}
	
	@Override
	public Data get(int index){
		return data.get(index);
	}
}
