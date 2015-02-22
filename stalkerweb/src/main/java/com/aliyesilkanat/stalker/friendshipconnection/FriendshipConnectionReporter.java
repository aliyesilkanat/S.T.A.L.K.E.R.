package com.aliyesilkanat.stalker.friendshipconnection;

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
	
	
	
	public String FollowingQuery(String useruri){
		StringBuilder query= new StringBuilder();
		query.append("PREFIX schema: <http://schema.org/>");
		query.append("select * where");
		query.append("{");
		query.append("<");
		query.append(useruri);
		query.append(">");
		query.append(" schema:follows ?o");
		query.append("}");
		
		return query.toString();
	}
	
	
	public JsonObject createReportObject(String userURI) {	
		JsonObject followingsObject = extractsResultsFromGraphToJsonObject(FollowingQuery(userURI),userURI);
		return followingsObject;

	}
	
	private JsonObject extractsResultsFromGraphToJsonObject(String query,String userURI) {
		String msg;
		JsonObject graph=null;
		
		try {
			ResultSet execSelect = RDFDataLayer.getInstance().execSelect(query);
			graph=traverseResultSet(execSelect,userURI);
		} catch (Exception e) {
			msg = "cannot execute sparql query {\"query\":\"%s\"}";
			getLogger().error(String.format(msg, query));
		}
		
		return graph;
	}
	
	  
	  private JsonObject fillJsonObjectWithLinks(int i)
	  {
		  JsonObject followingLink = new JsonObject();
            
			  followingLink.addProperty("source", 0);
			  followingLink.addProperty("target", i);
			  followingLink.addProperty("value", 1);
		  
		  return followingLink;
		  
	  }
	  
    
		private JsonObject traverseResultSet(ResultSet resultSet,String userURI)
		{
			int i=1;
			JsonArray nodes= new JsonArray();
			JsonArray links=new JsonArray();
			JsonObject graph=new JsonObject();
			
			nodes.add(fillJsonObjectWithUserUriNode(userURI));
			while (resultSet.hasNext()) {
				QuerySolution next = resultSet.next();
	            
			    nodes.add(fillJsonObjectWithNode(next));
			    links.add(fillJsonObjectWithLinks(i));
				i++;
			}
			
			graph.add("nodes", nodes);
			graph.add("links", links);
			return graph; 
		}
		
		private JsonObject fillJsonObjectWithUserUriNode(String useruri)
		{
			JsonObject followingNode = new JsonObject();
			
			followingNode.addProperty("UserUri", useruri);
		    followingNode.addProperty("group" , 1);
		    
		    return followingNode;
		}
		
		private JsonObject fillJsonObjectWithNode(QuerySolution solution)
		{
			JsonObject followingNode = new JsonObject();
			String uri= extractUserUri(solution);
				
				followingNode.addProperty("UserUri", uri);
			    followingNode.addProperty("group" , 2);
			
			return followingNode;
		}
		
		private String extractUserUri(QuerySolution solution)  {
			return solution.get("o").toString();
		}

}
