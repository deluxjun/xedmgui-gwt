package com.speno.xedm.gui.server;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.speno.xedm.gui.common.client.Constants;

public class MockGroupsDataServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,	IOException {
		System.out.println("********* MockGroupsDataServlet ********* ");
		
		String type = request.getParameter("type");
		String parentId = request.getParameter("parentid");
		
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

		//그룹
		//if ("0".equals(type) && StringUtils.isNotEmpty(parentId)) {
		if ("0".equals(type)) {
			selectGoupList(writer);
		}
		//직무
		else if ("1".equals(type)) {
			selectDutyList(writer);
		}
		//직위
		else if ("2".equals(type)) {
			selectPositionList(writer);
		}
		else {
			throw new IOException("Invalid paramenters type["+type+"], parentId["+parentId+"]");
		}
	}
	
	private void selectGoupList(PrintWriter writer) {		
		writer.print("<list>");
		
		writer.print("<group>");
		writer.print("<id>0</id>");
		writer.print("<parentid>-100</parentid>");
		writer.print("<name>first</name>");
		writer.print("<type>0</type>");
		writer.print("<description>first group7</description>");
		writer.print("<path>first</path>");
		writer.print("<IDPath>0</IDPath>");
		writer.print("</group>");
		
		writer.print("<group>");
		writer.print("<id>1</id>");
		writer.print("<parentid>0</parentid>");
		writer.print("<name>second</name>");
		writer.print("<type>0</type>");
		writer.print("<description>second group</description>");
		writer.print("<path>first > second</path>");
		writer.print("<IDPath>0>1</IDPath>");
		writer.print("</group>");
		/*
		writer.print("<group>");
		writer.print("<id>3</id>");
		writer.print("<parentid>1</parentid>");
		writer.print("<type>0</type>");
		writer.print("<name>child1 of first</name>");
		writer.print("<description>child1 of first group</description>");
		writer.print("</group>");
		
		writer.print("<group>");
		writer.print("<id>4</id>");
		writer.print("<parentid>1</parentid>");
		writer.print("<type>0</type>");
		writer.print("<name>child2 of first</name>");
		writer.print("<description>child2 of first</description>");
		writer.print("</group>");
		
		writer.print("<group>");
		writer.print("<id>5</id>");
		writer.print("<parentid>2</parentid>");
		writer.print("<type>0</type>");
		writer.print("<name>child of second</name>");
		writer.print("<description>child of second</description>");
		writer.print("</group>");
		*/
		writer.print("</list>");
	}
	
	private void selectDutyList(PrintWriter writer) {
		writer.print("<list>");
		for (int i = 0; i < 50; i++) {
			writer.print("<group>");
			writer.print("<id>" + i + "</id>");
			writer.print("<type>Duty_Type_"+i+"</type>");
			writer.print("<name>Duty_Name_"+i+"</name>");
			writer.print("<description>Duty_Description_"+i+"</description>");
			writer.print("</group>");
		}
		writer.print("</list>");
	}
	
	private void selectPositionList(PrintWriter writer) {
		writer.print("<list>");
		for (int i = 0; i < 50; i++) {
			writer.print("<group>");
			writer.print("<id>" + i + "</id>");
			writer.print("<type>Pos_Type_"+i+"</type>");
			writer.print("<name>Pos_Name_"+i+"</name>");
			writer.print("<description>Pos_Description_"+i+"</description>");
			writer.print("</group>");
		}
		writer.print("</list>");
	}
}