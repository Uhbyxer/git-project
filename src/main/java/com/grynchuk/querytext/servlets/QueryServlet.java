package com.grynchuk.querytext.servlets;

import java.io.IOException;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.grynchuk.querytext.dao.QueryTextDao;


public class QueryServlet extends HttpServlet {
	
	private static final String PATH = "/WEB-INF/FileStorage/info.txt";

	private static final long serialVersionUID = 1L;
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		QueryTextDao dao = new QueryTextDao(getServletContext().getRealPath(PATH));
		
		String limit = request.getParameter("limit");
		String q = request.getParameter("q");
		String length = request.getParameter("length");		
		
		
		try {

			if (limit != null && limit.trim().length() > 0)
				dao.setLimit(Integer.parseInt(limit));

			if (length != null && length.trim().length() > 0)
				dao.setLength(Integer.parseInt(length));

		} catch (NumberFormatException e) {
			response.sendError(400);
			return;
		}
		
		if(q != null && q.trim().length() > 0)
			dao.setQ(q);
		
		//get Text
		Map<String, Object> responseHolder = new LinkedHashMap<>();
		responseHolder.put("text", dao.getText());
		
		//get File MetaData
		if("true".equals(request.getParameter("includeMetaData"))) 
			responseHolder.put("metaData", dao.getMetaData());
		
		response.setContentType("application/json");
		response.getWriter().write(new Gson().toJson(responseHolder));
	}
	
}


