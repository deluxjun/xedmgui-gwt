package com.speno.xedm.gui.server;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * MockDocumentCodeDataServlet
 * 
 * @author ¹Ú»ó±â
 * @since 1.0
 */
public class MockDocumentCodeDataServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,	IOException {
		System.out.println("********* MockDocumentTypeDataServlet ********* ");
		
		String type = request.getParameter("type");
		
		if ( request.getParameter("sid") == null) {
			throw new IOException("Invalid session");
		}
		
		response.setContentType("text/xml");
		response.setCharacterEncoding("UTF-8");

		// Headers required by Internet Explorer
		response.setHeader("Pragma", "public");
		response.setHeader("Cache-Control", "must-revalidate, post-check=0,pre-check=0");
		response.setHeader("Expires", "0");
		
		PrintWriter writer = response.getWriter();
		
		 if ("doctype".equals(type)) {
			 selectDocTypeList(writer);
		}
		else if ("filetype".equals(type)) {
			selectFileTypeList(writer);
		}
		else if ("retention".equals(type)) {
			selectRetentionProfileList(writer);
		}
		else if ("securityprofile".equals(type)) {
			selectSecurityProfileList(writer);
		}
		else {
			throw new IOException("Invalid paramenters type["+type+"]");
		}
	}
	
	private void selectDocTypeList(PrintWriter writer) {		
		writer.print("<list>");
		for (int i = 0; i < 50; i++) {
			writer.print("<doccode>");
			writer.print("<id>"+i+"</id>");
			writer.print("<name>name_"+i+"</name>");
			writer.print("<description>description_" + i + "</description>");			
			writer.print("<retentionid>retentionid_"+i+"</retentionid>");
			writer.print("<retentionname>retentionname_"+i+"</retentionname>");
			writer.print("<retentionperiod>retentionperiod_"+i+"</retentionperiod>");
			writer.print("<eclassid>eclassid_"+i+"</eclassid>");
			writer.print("<cclassid>cclassid_"+i+"</cclassid>");
			writer.print("<indexid>indexid_"+i+"</indexid>");
			writer.print("<uclassid>uclassid_"+i+"</uclassid>");
			writer.print("</doccode>");
		}
		writer.print("</list>");
	}
	
	private void selectFileTypeList(PrintWriter writer) {		
		writer.print("<list>");
		for (int i = 0; i < 50; i++) {
			writer.print("<doccode>");
			writer.print("<id>"+i+"</id>");
			writer.print("<name>name_"+i+"</name>");
			writer.print("<size>"+((i<10)?10:((i<20)?1000:((1<30)?0:50)))+"</size>");
			writer.print("<description>description_" + i + "</description>");			
			writer.print("<viewer>"+((i<10)?"T":((i<20)?"S":((1<30)?"U":"U")))+"</viewer>");
			writer.print("</doccode>");
		}
		writer.print("</list>");
	}
	
	private void selectRetentionProfileList(PrintWriter writer) {		
		writer.print("<list>");
		for (int i = 1; i <= 50; i++) {
			writer.print("<doccode>");
			writer.print("<id>"+i+"</id>");
			writer.print("<name>name_"+i+"</name>");
			writer.print("<description>description_" + i + "</description>");			
			writer.print("<retentionperiod>"+((i<6)?i:10)+"</retentionperiod>");
			writer.print("</doccode>");
		}
		writer.print("</list>");
	}
	
	private void selectSecurityProfileList(PrintWriter writer) {		
		writer.print("<list>");
		for (int i = 1; i <= 50; i++) {
			writer.print("<securityprofile>");
			writer.print("<id>"+i+"</id>");
			writer.print("<name>name_"+i+"</name>");
			writer.print("<description>description_" + i + "</description>");
			writer.print("</securityprofile>");
		}
		writer.print("</list>");
	}
}