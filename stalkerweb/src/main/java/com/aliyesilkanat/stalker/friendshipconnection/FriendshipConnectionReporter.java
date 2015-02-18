package com.aliyesilkanat.stalker.friendshipconnection;



import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.aliyesilkanat.stalker.data.RDFDataLayer;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

public class FriendshipConnectionReporter {
	
	private final Logger logger = Logger.getLogger(getClass());
	
	
	public Logger getLogger() {
		return logger;
	}
	
	public String FollowingQuery(){
		StringBuilder query= new StringBuilder();
		query.append("PREFIX schema: <http://schema.org/>");
		query.append("select * where");
		query.append("{");
		query.append("?s schema:follows ?o");
		query.append("}");
		
		return query.toString();
	}
	
	
	public JsonObject createReportObject(String userURI) {	
		JsonObject followingsArray = extractsResultsFromGraphToJsonObject(FollowingQuery());
		return followingsArray;

	}
	
	private JsonObject extractsResultsFromGraphToJsonObject(String query) {
		String msg;
		JsonObject graph=null;
		
		try {
			ResultSet execSelect = RDFDataLayer.getInstance().execSelect(query);
			graph=traverseResultSet(execSelect);
		} catch (Exception e) {
			msg = "cannot execute sparql query {\"query\":\"%s\"}";
			getLogger().error(String.format(msg, query));
		}
		
		return graph;
	}
	
	  
	  private JsonObject fillJsonObjectWithLinks(QuerySolution next,JsonArray relation)
	  {
		  JsonObject followingObject = new JsonObject();
		  String uri=extractUserUri(next);
		  String uri2=extractfollowingsUri(next);
		  		 
		  
		  followingObject.addProperty("source", indexJsonObject(uri, relation));
		  followingObject.addProperty("target", indexJsonObject(uri2, relation));
		  followingObject.addProperty("value", 1);
		  
		  return followingObject;
	  }
	  
	  private int indexJsonObject(String uri ,JsonArray nodes)
	  {
		  for(int i=0;i<nodes.size();i++) {
			    String s=nodes.get(i).getAsJsonObject().get("UserUri").toString();
	            if(uri.equals(s.substring(1, s.length()-1))) {
	                return i;
	            }
	        }
		return 0;
	  }
		
    
		private JsonObject traverseResultSet(ResultSet resultSet)
		{
			JsonArray followingsArray;
			followingsArray = new JsonArray();
			
			JsonArray links=new JsonArray();
			ArrayList<String> c= new ArrayList<String>();
			
			JsonObject graph=new JsonObject();
			while (resultSet.hasNext()) {
				QuerySolution next = resultSet.next();
	            
				if(!c.contains(extractUserUri(next)))
				followingsArray.add(fillJsonObjectWithResult(next));
				
				if(!c.contains(extractfollowingsUri(next)))
				followingsArray.add(fillJsonObectWithResultRelation(next));
				
				c.add(extractUserUri(next));
				c.add(extractfollowingsUri(next));
				
				links.add(fillJsonObjectWithLinks(next, followingsArray));
				
			} 
			graph.add("nodes", followingsArray);
			graph.add("links", links);
			return graph; 
		}
		
		private JsonObject fillJsonObjectWithResult(QuerySolution resultSet)
		{
			JsonObject followingObject = new JsonObject();
			String uri= extractUserUri(resultSet);
				
			
				followingObject.addProperty("UserUri", uri);
			    followingObject.addProperty("group" , 1);
		
					
			return followingObject;
		}
		
		private JsonObject fillJsonObectWithResultRelation(QuerySolution resultSet)
		{
			JsonObject followingObject = new JsonObject();	
			String uri2 = extractfollowingsUri(resultSet);
			
				followingObject.addProperty("UserUri", uri2);
				followingObject.addProperty("group" , 2);
		
			return followingObject;
		}
		
		private String extractUserUri(QuerySolution resultSet)  {
			return resultSet.get("s").toString();
		}
		
		private String extractfollowingsUri(QuerySolution resultSet) {
			return resultSet.get("o").toString();
		}


}
