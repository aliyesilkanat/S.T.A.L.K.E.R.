package com.aliyesilkanat.stalker.friendshipconnection;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.google.gson.JsonObject;

/**
 * Servlet implementation class FriendshipConnectionServlet
 */
@WebServlet("/FriendshipConnectionServlet")
public class FriendshipConnectionServlet extends HttpServlet {
	private final Logger logger = Logger.getLogger(getClass());
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public FriendshipConnectionServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		String userURI = request.getParameter("userURI");
		String msg = "requesting followings array {\"userURI\":\"%s\"}";
		getLogger().debug(String.format(msg, userURI));
		
		
		if (userURI != null && !userURI.isEmpty()) {
			JsonObject object = new FriendshipConnectionReporter().createReportObject(userURI);
			msg = "response of followings array {\"userURI\":\"%s\", \"followings\":\"%s\"}";
			getLogger().info(String.format(msg, userURI, object));
			writeResponse(response, object.toString());
		} else {
			writeResponse(response, "Please give userURI parameter");
		}
	}
	
	private void writeResponse(HttpServletResponse response,
			String d3GraphAsJson) throws IOException {
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/json; charset=UTF-8");
		response.addHeader("Access-Control-Allow-Origin", "*");
		response.getWriter().write(d3GraphAsJson);
		response.getWriter().flush();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}
	
	public Logger getLogger() {
		return logger;
	}

}
