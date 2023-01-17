package com.speno.xedm.gui.server;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MockDocumentRightDataServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		
		response.setContentType("text/xml");
		response.setCharacterEncoding("UTF-8");

		response.setHeader("Pragma", "public");
		response.setHeader("Cache-Control", "must-revalidate, post-check=0,pre-check=0");
		response.setHeader("Expires", "0");

		PrintWriter writer = response.getWriter();
		writer.write("<list>");
		writer.write("<right>");
		
		writer.write("<groupId>1</groupId>");
		writer.write("<group>admin</group>");
		writer.write("<read>true</read>");
		writer.write("<write>true</write>");
		writer.write("<add>true</add>");
		writer.write("<delete>true</delete>");
		writer.write("<rename>true</rename>");
		writer.write("<download>true</download>");
		writer.write("<view>true</view>");
		writer.write("<write>true</write>");
		writer.write("<check>true</check>");
		writer.write("<type>0</type>");
		writer.write("</right>");
		writer.write("<right>");
		writer.write("<groupId>4423680</groupId>");
		writer.write("<group>deluxjun</group>");
		writer.write("<read>true</read>");
		writer.write("<write>false</write>");
		writer.write("<add>false</add>");
		writer.write("<delete>true</delete>");
		writer.write("<rename>true</rename>");
		writer.write("<download>true</download>");
		writer.write("<view>true</view>");
		writer.write("<write>true</write>");
		writer.write("<check>true</check>");
		writer.write("<type>0</type>");
		  		
		writer.write("</right>");
		writer.write("</list>");
		
	}
}
