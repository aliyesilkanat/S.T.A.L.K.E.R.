package com.aliyesilkanat.stalker.friendshipactivity;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.aliyesilkanat.stalker.data.UnfinishedOperationException;
import com.google.gson.JsonObject;

/**
 * Servlet implementation class FriendshipActivityServlet
 */
@WebServlet("/FriendshipActivityServlet")
public class FriendshipActivityServlet extends HttpServlet {
	private final Logger logger = Logger.getLogger(getClass());
	private static final long serialVersionUID = 1L;

	/**
	 * Default constructor.
	 */
	public FriendshipActivityServlet() {
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String userURI = request.getParameter("userURI");
		String msg = "requesting followings array {\"userURI\":\"%s\"}";
		getLogger().debug(String.format(msg, userURI));
		long startTime, endTime;
		try {
			startTime = Long.parseLong(request.getParameter("start_date"));
			endTime = Long.parseLong(request.getParameter("end_date"));
		} catch (NumberFormatException e) {
			writeResponse(response, "Please give start_date and end_date parameters");
			return;
		}
		if (userURI != null && !userURI.isEmpty()) {
			try {
				JsonObject result = new FriendshipActivityReporter(startTime, endTime)
						.createReportObject(userURI);
				msg = "response of followings array {\"userURI\":\"%s\", \"followings\":\"%s\"}";
				getLogger().info(String.format(msg, userURI, result));
				writeResponse(response, result.toString());
			} catch (UnfinishedOperationException e) {
				getLogger().error("Error while creating report", e);
			}
		} else {
			writeResponse(response, "Please give userURI parameter");
		}
	}

	/**
	 * Flushes the report result in json format to response writer.
	 * 
	 * @param response
	 *            servlet response.
	 * @param d3GraphAsJson
	 *            report result in json format.
	 * @throws IOException
	 */
	private void writeResponse(HttpServletResponse response,
			String d3GraphAsJson) throws IOException {
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/json; charset=UTF-8");
		response.addHeader("Access-Control-Allow-Origin", "*");
		response.getWriter().print(d3GraphAsJson);
		response.getWriter().flush();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
	}

	public Logger getLogger() {
		return logger;
	}

}
