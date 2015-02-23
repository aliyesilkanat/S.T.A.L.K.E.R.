package com.aliyesilkanat.stalker.data.transaction.relational;

import java.util.ArrayList;

public interface DataSet {

	public int size();	
	/**
	 * 
	 * @return Table(relation) name of DataSet object
	 */
	
	public String getRelationName();
	/**
	 * 
	 * @return Attribute names of DataSet object
	 */
	public ArrayList<String> getAttributeNames();
	
	public Data get(int index);
}
