package com.speno.xedm.gui.server;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MockDocumentsDataServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {

		long folderId = 0;
		
		if (request.getParameter("folderId") != null && !"".equals(request.getParameter("folderId"))) {
			folderId = Long.parseLong(request.getParameter("folderId"));
		}
		
		
		String filename = request.getParameter("filename");

		boolean indexable = "true".equals(request.getParameter("index")) ? true : false;

		String sid = request.getParameter("sid");
		if (sid == null)
			throw new IOException("Invalid session");

		response.setContentType("text/xml");
		response.setCharacterEncoding("UTF-8");

		// Headers required by Internet Explorer
		response.setHeader("Pragma", "public");
		response.setHeader("Cache-Control", "must-revalidate, post-check=0,pre-check=0");
		response.setHeader("Expires", "0");

		PrintWriter writer = response.getWriter();
		writer.write("<list>");

		for (int i = 0; i < 10; i++) {
			
			writer.print("<document>");
			writer.print("<id>" + Long.toString(folderId + 1000 + i) + "</id>");
			writer.print("<attachs>+2</attachs>");
			if(i/2 == 0){
				writer.print("<lockyn>lock</lockyn>");
			}else{
				writer.print("<lockyn>lock_open</lockyn>");
			}
//			writer.print("<title>Request doc " + Long.toString(i) + "</title>");
			writer.print("<titlenm>Request doc " + Long.toString(i) + "</titlenm>");
			if (filename != null && !"".equals(filename))
				writer.print("<filename>" + Long.toString(folderId + 1000 + i) + ".doc</filename>");
			else
				writer.print("<filename>" + filename + Long.toString(folderId + 1000 + i) + ".doc</filename>");
			
			writer.print("<doctype>DevTeam Docs</doctype>");
			writer.print("<version>1.0</version>");
			writer.print("<owner>spenocom</owner>");
			writer.print("<created>2013-02-25T11:32:23</created>");
			writer.print("<modified>2013-02-25T12:22:13</modified>");
			writer.print("<retention>10 years</retention>");
			writer.write("<icon>word</icon>  ");
			writer.write("<description>doc" + i + "</description>");  			
			writer.write("<elementid>2012121213" + i + "</elementid>");  			
			writer.write("<fielda>request doc1</fielda>");  			
			writer.write("<fieldb>DevTeam docs</fieldb>");  			
			writer.write("<fieldc>2</fieldc>");  			
			writer.print("</document>");
		}
		
		
		writer.write("</list>");
	}
}
