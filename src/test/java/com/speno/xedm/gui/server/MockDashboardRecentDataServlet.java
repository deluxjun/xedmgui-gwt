package com.speno.xedm.gui.server;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MockDashboardRecentDataServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {

		String sid = request.getParameter("sid");
		if (sid == null)
			throw new IOException("Invalid session");

		response.setContentType("text/xml");
		response.setCharacterEncoding("UTF-8");

		response.setHeader("Pragma", "public");
		response.setHeader("Cache-Control", "must-revalidate, post-check=0,pre-check=0");
		response.setHeader("Expires", "0");

		PrintWriter writer = response.getWriter();
		writer.write("<list>");
		writer.print("<recent>");
		writer.print("<id>10001</id>");
		writer.print("<path>/11/2114</path>");
		writer.print("<title>10001</title>");
		writer.print("<modifieddate>2013-02-25T12:22:13</modifieddate>");
		writer.print("<doctype>word</doctype>");
		writer.print("<version>0.1.64</version>");
		writer.print("</recent>");
		writer.write("</list>");
	}
}
