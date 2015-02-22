package com.aliyesilkanat.stalker.friendshipconnection;

import org.apache.log4j.Logger;

import com.aliyesilkanat.stalker.UserDetailsReporter;
import com.aliyesilkanat.stalker.data.RDFDataLayer;
import com.aliyesilkanat.stalker.data.UnfinishedOperationException;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

public class FriendshipConnectionReporter {

	private final Logger logger = Logger.getLogger(getClass());

	public Logger getLogger() {
		return logger;
	}

	/**
	 * Sets followings query for given user uri.
	 * 
	 * @param useruri
	 *            Instagram user uri.
	 * @return Sparql query as {@link String}.
	 */
	public String setFollowingQuery(String useruri) {
		StringBuilder query = new StringBuilder();
		query.append("PREFIX schema: <http://schema.org/>");
		query.append("select distinct * where");
		query.append("{");
		query.append("<");
		query.append(useruri);
		query.append(">");
		query.append(" schema:follows ?o. ?o schema:name ?name. ?o schema:image ?img");
		query.append("}");

		return query.toString();
	}

	/**
	 * Create graph report object for given user uri.
	 * 
	 * @param userURI
	 *            User uri.
	 * @return D3 json object.
	 */
	public JsonObject createReportObject(String userURI) {
		JsonObject followingsObject = extractsResultsFromGraphToJsonObject(
				setFollowingQuery(userURI), userURI);
		return followingsObject;

	}

	/**
	 * Executes sparql query, fills results into json object.
	 * 
	 * @param query
	 *            Sparql query.
	 * @param userURI
	 *            Uri of the user.
	 * @return D3 json object.
	 */
	private JsonObject extractsResultsFromGraphToJsonObject(String query,
			String userURI) {
		String msg;
		JsonObject graph = null;

		try {
			ResultSet execSelect = RDFDataLayer.getInstance().execSelect(query);
			graph = traverseResultSet(execSelect, userURI);
		} catch (Exception e) {
			msg = "cannot execute sparql query {\"query\":\"%s\"}";
			getLogger().error(String.format(msg, query));
		}

		return graph;
	}

	/**
	 * Creates d3 json link object.
	 * 
	 * @param i
	 *            Target index of other node of connection.
	 * @return D3 Link {@link JsonObject}.
	 */
	private JsonObject createLinkObject(int i) {
		JsonObject followingLink = new JsonObject();
		followingLink.addProperty("source", 0);
		followingLink.addProperty("target", i);
		followingLink.addProperty("value", 1);

		return followingLink;

	}

	/**
	 * Traverses result set, extracts graph object.
	 * 
	 * @param resultSet
	 *            {@link ResultSet} for executed sparql.
	 * @param userURI
	 *            Uri of user.
	 * @return D3 graph {@link JsonObject}.
	 */
	private JsonObject traverseResultSet(ResultSet resultSet, String userURI) {
		int i = 1;
		JsonArray nodes = new JsonArray();
		JsonArray links = new JsonArray();
		JsonObject graph = new JsonObject();

		nodes.add(fillJsonObjectWithUserUriNode(userURI));
		while (resultSet.hasNext()) {
			QuerySolution next = resultSet.next();

			nodes.add(fillJsonObjectWithNode(next));
			links.add(createLinkObject(i));
			i++;
		}

		graph.add("nodes", nodes);
		graph.add("links", links);
		return graph;
	}

	private JsonObject fillJsonObjectWithUserUriNode(String useruri) {
		JsonObject followingNode = new JsonObject();

		try {
			JsonObject details = new UserDetailsReporter().getDetails(useruri);
			followingNode.addProperty("UserName", details.get("name")
					.getAsString());
			followingNode.addProperty("UserImage", details.get("img")
					.getAsString());

		} catch (UnfinishedOperationException e) {
			String msg = "cannot retrieve user details {\"userURI\":\"%s\"}";
			getLogger().error(String.format(msg, useruri));
		}
		followingNode.addProperty("UserUri", useruri);
		followingNode.addProperty("group", 1);

		return followingNode;
	}

	private JsonObject fillJsonObjectWithNode(QuerySolution solution) {
		JsonObject followingNode = new JsonObject();
		followingNode.addProperty("UserUri", extractUserUri(solution));
		followingNode.addProperty("group", 2);
		followingNode.addProperty("UserName", extractUserName(solution));
		followingNode.addProperty("UserImage", extractUserImage(solution));

		return followingNode;
	}

	private String extractUserImage(QuerySolution solution) {
		String userImage = solution.get("img").toString();
		return userImage;
	}

	private String extractUserName(QuerySolution solution) {
		String userName = solution.get("name").toString();
		return userName;
	}

	private String extractUserUri(QuerySolution solution) {
		return solution.get("o").toString();
	}

}
