package com.aliyesilkanat.stalker;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.aliyesilkanat.stalker.data.RelationalDataLayer;
import com.aliyesilkanat.stalker.data.UnfinishedOperationException;

/**
 * Servlet implementation class LoginServlet
 */
@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public LoginServlet() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		String email = request.getParameter("email");
		String password = request.getParameter("password");
		String resultObject = LogInManager.getInstance().logIn(email, password);
		writeResponse(response, resultObject);
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

}
