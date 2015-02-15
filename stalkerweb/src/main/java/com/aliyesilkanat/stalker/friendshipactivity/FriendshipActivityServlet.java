package com.aliyesilkanat.stalker.friendshipactivity;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.aliyesilkanat.stalker.data.constants.FriendshipActivityLogConst;

/**
 * Servlet implementation class FriendshipActivityServlet
 */
@WebServlet("/FriendshipActivityServlet")
public class FriendshipActivityServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * Default constructor.
	 */
	public FriendshipActivityServlet() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String userId = request.getParameter("userId");
		String query = "select * from "
				+ FriendshipActivityLogConst.FRIENDSHIP_ACTIVITY_LOG_TABLE_NAME
				+ " where " + "'UserID'=" + userId;
		

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
