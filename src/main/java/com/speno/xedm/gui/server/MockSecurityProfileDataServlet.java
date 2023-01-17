package com.speno.xedm.gui.server;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MockSecurityProfileDataServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,	IOException {
		System.out.println("********* MockSecurityProfileDataServlet ********* ");
		
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

		//직무
		 if ("0".equals(type)) {
			 selectSecurityProfileList(writer);
		}
		//직위
		else if ("1".equals(type)) {
			selectAclList(writer);
		}
		else {
			throw new IOException("Invalid paramenters type["+type+"]");
		}
	}
	
	private void selectSecurityProfileList(PrintWriter writer) {		
		writer.print("<list>");		
		for (int i = 0; i < 50; i++) {
			writer.print("<secuprofile>");
			writer.print("<id>" + i + "</id>");
			writer.print("<name>test_name_" + i + "</name>");
			writer.print("<description>Security_Profile_Description_"+i+"</description>");
			writer.print("</secuprofile>");
		}
		writer.print("</list>");
	}
	
	private void selectAclList(PrintWriter writer) {
		
		writer.print("<list>");
		for (int i = 0; i < 5; i++) {
			writer.print("<secuprofile>");
			writer.print("<label>DUTY</label>");
			writer.print("<type>1</type>");
			writer.print("<entityId>" + i + "</entityId>");			
			writer.print("<entityName>DUTY_"+i+"</entityName>");
			writer.print("<view>false</view>");
			writer.print("<print>false</print>");
			writer.print("<read>false</read>");
			writer.print("<write>false</write>");
			writer.print("<rename>false</rename>");
			writer.print("<pdelete>false</pdelete>");
			writer.print("<check>false</check>");
			writer.print("<download>false</download>");
			writer.print("</secuprofile>");
		}
		
		for (int i = 5; i < 10; i++) {
			writer.print("<secuprofile>");
			writer.print("<label>POSITION</label>");
			writer.print("<type>2</type>");
			writer.print("<entityId>" + i + "</entityId>");			
			writer.print("<entityName>POSITION_"+i+"</entityName>");
			writer.print("<view>true</view>");
			writer.print("<print>true</print>");
			writer.print("<read>true</read>");
			writer.print("<write>false</write>");
			writer.print("<rename>true</rename>");
			writer.print("<pdelete>false</pdelete>");
			writer.print("<check>true</check>");
			writer.print("<download>true</download>");
			writer.print("</secuprofile>");
		}
		
		for (int i = 10; i < 15; i++) {
			writer.print("<secuprofile>");
			writer.print("<label>GROUP</label>");
			writer.print("<type>0</type>");
			writer.print("<entityId>" + i + "</entityId>");			
			writer.print("<entityName>GROUP_"+i+"</entityName>");
			writer.print("<view>true</view>");
			writer.print("<print>false</print>");
			writer.print("<read>true</read>");
			writer.print("<write>false</write>");
			writer.print("<rename>true</rename>");
			writer.print("<pdelete>false</pdelete>");
			writer.print("<check>true</check>");
			writer.print("<download>true</download>");
			writer.print("</secuprofile>");
		}
		
		for (int i = 15; i < 20; i++) {
			writer.print("<secuprofile>");
			writer.print("<label>USER</label>");
			writer.print("<type>-1</type>");
			writer.print("<entityId>" + i + "</entityId>");			
			writer.print("<entityName>USER_"+i+"</entityName>");
			writer.print("<view>true</view>");
			writer.print("<print>false</print>");
			writer.print("<read>true</read>");
			writer.print("<write>true</write>");
			writer.print("<rename>true</rename>");
			writer.print("<pdelete>true</pdelete>");
			writer.print("<check>true</check>");
			writer.print("<download>true</download>");
			writer.print("</secuprofile>");
		}
		writer.print("</list>");
	}
}