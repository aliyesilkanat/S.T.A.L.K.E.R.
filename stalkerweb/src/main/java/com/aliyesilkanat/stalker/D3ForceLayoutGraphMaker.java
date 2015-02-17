package com.aliyesilkanat.stalker;

import java.util.LinkedHashMap;
import java.util.LinkedList;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/**
 * Class for extracting dependency graphs' json formatted sparql query results
 * into d3js graph format
 */
public class D3ForceLayoutGraphMaker {

	private static final String HOPS = "hops";
	private static final String HOP_TITLES = "hopTitles";
	private static final String LINKS = "links";
	private static final String NODES = "nodes";

	private String edgeURI;
	private String nodeText;
	private String nodeURI;
	private String edgeText;

	public D3ForceLayoutGraphMaker(String edgeUri, String edgeText,
			String nodeUri, String nodeText) {
		this.edgeURI = edgeUri;
		this.nodeURI = nodeUri;
		this.nodeText = nodeText;
		this.edgeText = edgeText;
	}

	private void addNewAuthorToLists(LinkedList<String> userList,
			LinkedList<JsonObject> userJsonList, JsonArray usersArray,
			String userUri, String userName) {
		JsonObject userObj = new JsonObject();
		userObj.addProperty("name", userName);
		userObj.addProperty("group", 1);
		userObj.addProperty("uri", userUri);
		userJsonList.add(userObj);
		userList.add(userUri);
		usersArray.add(userObj);
	}

	/**
	 * Extracts graph's json object which contains nodes array and links array.
	 * 
	 * @param selectResultSetInJson
	 *            Json result of sparql query.
	 * @return graph's json object.
	 */
	public JsonObject execute(String selectResultSetInJson) {
		JsonArray jsonArray = extractTuplesFromResult(selectResultSetInJson);
		return createResultObject(jsonArray);
	}

	/**
	 * Extracts node array of graph.
	 * 
	 * @param dbResultArray
	 *            Json result of sparql query.
	 * @param userList
	 *            List which contains users' uris.
	 * @param userJsonList
	 *            List which contains json object nodes(users or authors).
	 * @param topicMap
	 *            Map which contains authors of comments of topics.
	 * @return Extracted nodes array.
	 */
	private JsonObject extractNodesArray(JsonArray dbResultArray,
			LinkedList<String> userList, LinkedList<JsonObject> userJsonList,
			LinkedHashMap<String, LinkedList<Integer>> topicMap) {
		JsonObject userTopicsMap = new JsonObject();
		JsonObject topicTitlesMap = new JsonObject();
		JsonArray usersArray = new JsonArray();
		for (JsonElement jsonElement : dbResultArray) {
			JsonObject resultTuple = jsonElement.getAsJsonObject();

			// extracts user propertys from tuple...
			String userUri = resultTuple.get(nodeURI).getAsJsonObject()
					.get("value").getAsString();
			String userName = resultTuple.get(nodeText).getAsJsonObject()
					.get("value").getAsString();

			// extracts entry uri from tuple...
			String entryUri = resultTuple.get(edgeURI).getAsJsonObject()
					.get("value").getAsString();

			if (!topicTitlesMap.has(entryUri)) {
				String entryTitle = resultTuple.get(edgeText).getAsJsonObject()
						.get("value").getAsString();
				topicTitlesMap.addProperty(entryUri, entryTitle);
			}
			addToTopicUriList(userTopicsMap, userUri, entryUri);
			// checks if user added before to the list...
			int indexOfUser = userList.indexOf(userUri);

			// if not...
			if (indexOfUser == -1) {
				// add new user to lists...
				addNewAuthorToLists(userList, userJsonList, usersArray,
						userUri, userName);
			}

			// checks if entry added before...
			LinkedList<Integer> authorsList = topicMap.get(entryUri);
			if (authorsList == null) {
				authorsList = new LinkedList<Integer>();
			}

			// add user index into
			authorsList.add(userList.indexOf(userUri));
			topicMap.put(entryUri, authorsList);
		}
		JsonObject resultObject = new JsonObject();
		resultObject.add(NODES, usersArray);
		resultObject.add(HOP_TITLES, topicTitlesMap);
		resultObject.add(HOPS, userTopicsMap);
		return resultObject;
	}

	private void addToTopicUriList(JsonObject userTopicsMap, String userUri,
			String entryUri) {
		JsonArray topicUriList;
		if (!userTopicsMap.has(userUri)) {
			topicUriList = new JsonArray();
		} else {
			topicUriList = userTopicsMap.get(userUri).getAsJsonArray();
		}
		boolean add = true;
		for (JsonElement topicUris : topicUriList) {
			if (topicUris.getAsJsonPrimitive().getAsString().equals(entryUri)) {
				add = false;
			}
		}
		if (add) {
			topicUriList.add(new JsonPrimitive(entryUri));
		}
		userTopicsMap.add(userUri, topicUriList);
	}

	/**
	 * Get tuples from sparql query result.
	 * 
	 * @param dbResults
	 *            Raw query result.
	 * @return Extracted tuples from query result.
	 */
	private JsonArray extractTuplesFromResult(String dbResults) {
		JsonObject mainObject = new Gson()
				.fromJson(dbResults, JsonObject.class);
		JsonArray resultArray = mainObject.get("results").getAsJsonObject()
				.get("bindings").getAsJsonArray();
		return resultArray;
	}

	/**
	 * Sets result object using tuples extracted from store.
	 * 
	 * @param dbResultArray
	 *            Tuples extracted from store.
	 * @return Graph result object contains nodes and links array.
	 */
	private JsonObject createResultObject(JsonArray dbResultArray) {

		// User list which contains uris of users...
		LinkedList<String> userList = new LinkedList<String>();
		// Same as userList except contains json object which will be sent in
		// result array...
		LinkedList<JsonObject> userJsonList = new LinkedList<JsonObject>();
		// Indexes are sync above 2 LinkedLists.

		// HashMap for holding topics and authors...
		// Keys represent entry URIs, values represent authors by holding a list
		// of their indexes in userList.
		LinkedHashMap<String, LinkedList<Integer>> topicMap = new LinkedHashMap<String, LinkedList<Integer>>();

		// Traverses db result tuples one time, extract nodes array.
		JsonObject resultObject = extractNodesArray(dbResultArray, userList,
				userJsonList, topicMap);

		LinkedHashMap<String, Byte> linkMap = new LinkedHashMap<String, Byte>();

		// traverses topic map
		JsonArray linksArray = traverseTopicMap(topicMap, linkMap);

		resultObject.add(LINKS, linksArray);
		return resultObject;

	}

	public JsonArray traverseTopicMap(
			LinkedHashMap<String, LinkedList<Integer>> topicMap,
			LinkedHashMap<String, Byte> linkMap) {
		JsonArray linksArray = new JsonArray();
		for (String entryUri : topicMap.keySet()) {
			LinkedList<Integer> authorOfTopics = topicMap.get(entryUri);
			for (int i = 0; i < authorOfTopics.size(); i++) {
				for (int j = 0; j < authorOfTopics.size(); j++) {
					Integer first = authorOfTopics.get(i);
					Integer second = authorOfTopics.get(j);
					String firstVariation = new StringBuilder().append(first)
							.append("_").append(second).toString();
					String secondVariation = new StringBuilder().append(second)
							.append("_").append(first).toString();
					if (null == linkMap.get(firstVariation)
							&& null == linkMap.get(secondVariation)) {
						addLink(linkMap, linksArray, first, second,
								firstVariation);
					}
				}
			}
		}
		return linksArray;
	}

	/**
	 * Add links object to links array.
	 * 
	 * @param linkMap
	 * @param linksArray
	 * @param first
	 * @param second
	 * @param firstVariation
	 */
	private void addLink(LinkedHashMap<String, Byte> linkMap,
			JsonArray linksArray, Integer first, Integer second,
			String firstVariation) {
		linkMap.put(firstVariation, Byte.MIN_VALUE);
		JsonObject linkObject = new JsonObject();
		linkObject.addProperty("source", first);
		linkObject.addProperty("target", second);
		linkObject.addProperty("value", 1.0);
		linksArray.add(linkObject);
	}

}
