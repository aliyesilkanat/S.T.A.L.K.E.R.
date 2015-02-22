package com.aliyesilkanat.stalker;

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
 * Servlet implementation class UserDetailsServlet
 */
@WebServlet("/UserDetailsServlet")
public class UserDetailsServlet extends HttpServlet {
	private final Logger logger = Logger.getLogger(getClass());

	private static final long serialVersionUID = -2643133430212976349L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public UserDetailsServlet() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String userURI = request.getParameter("userURI");
		String msg = "requested user details {\"userURI\":\"%s\"}";
		getLogger().info(String.format(msg, userURI));
		if (userURI != null && !userURI.isEmpty()) {
			fetchDetailsOfUser(response, userURI);
		} else {
			getLogger().warn("requested user details for invalid user uri");
			writeResponse(response, "Please give valid userURI parameter");
		}
	}

	/**
	 * Fetch details of user using
	 * {@link UserDetailsReporter#getDetails(String)}.
	 * 
	 * @param response
	 *            {@link HttpServlet}
	 * @param userURI
	 *            Uri of the user.
	 * @throws IOException
	 */
	private void fetchDetailsOfUser(HttpServletResponse response, String userURI)
			throws IOException {
		try {
			JsonObject details = new UserDetailsReporter().getDetails(userURI);
			if (details != null) {
				writeResponse(response, details.toString());
			} else {
				cannotFetchDetailsLog(userURI);
			}
		} catch (UnfinishedOperationException e) {
			cannotFetchDetailsLog(userURI);
			writeResponse(response, "Internal server error.");
		}
	}

	private void cannotFetchDetailsLog(String userURI) {
		String msg = "cannot fetch user details {\"userURI\":\"%s\"}";
		getLogger().error(String.format(msg, userURI));
	}

	private void writeResponse(HttpServletResponse response,
			String d3GraphAsJson) throws IOException {
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/json; charset=UTF-8");
		response.addHeader("Access-Control-Allow-Origin", "*");
		response.getWriter().write(d3GraphAsJson);
		response.getWriter().flush();
	}

	public Logger getLogger() {
		return logger;
	}

}
