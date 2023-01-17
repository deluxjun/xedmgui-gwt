package com.speno.xedm.gui.server;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MockFoldersDataServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		
		//long parent = Long.parseLong(request.getParameter("parent"));
		String sid = request.getParameter("sid");
		String type = request.getParameter("type");
		
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

		if("xvarm".equals(type)){
			writer.print("<folder>");
			writer.print("<folderId>1</folderId>");
			writer.print("<parent>5</parent>");
			writer.print("<name>XVARM_MAIN_RB</name>");
			writer.print("<type>2</type>");
			writer.print("</folder>");
			
			writer.print("<folder>");
			writer.print("<folderId>2</folderId>");
			writer.print("<parent>5</parent>");
			writer.print("<name>DOC</name>");
			writer.print("<type>2</type>");
			writer.print("</folder>");		
			
		}else if("home".equals(type)){
			
			writer.print("<folder>");
			writer.print("<folderId>4358144</folderId>");
			writer.print("<parent>1</parent>");
			writer.print("<name>documents</name>");
			writer.print("<type>1</type>");
			writer.print("</folder>");
			
			
//			writer.print("<folder>");
//			writer.print("<folderId>30</folderId>");
//			writer.print("<parent>5</parent>");
//			writer.print("<name>documents</name>");
//			writer.print("<type>1234</type>");
//			writer.print("</folder>");
//			
//			writer.print("<folder>");
//			writer.print("<folderId>40</folderId>");
//			writer.print("<parent>30</parent>");
//			writer.print("<name>my paint</name>");
//			writer.print("<type>1234</type>");
//			writer.print("</folder>");
//			
//			writer.print("<folder>");
//			writer.print("<folderId>50</folderId>");
//			writer.print("<parent>30</parent>");
//			writer.print("<name>my music</name>");
//			writer.print("<type>1234</type>");
//			writer.print("</folder>");		
//			
//			writer.print("<folder>");
//			writer.print("<folderId>60</folderId>");
//			writer.print("<parent>5</parent>");
//			writer.print("<name>technical docs</name>");
//			writer.print("<type>1234</type>");
//			writer.print("</folder>");					
//			
//			writer.print("<folder>");
//			writer.print("<folderId>61</folderId>");
//			writer.print("<parent>60</parent>");
//			writer.print("<name>2013</name>");
//			writer.print("<type>1234</type>");
//			writer.print("</folder>");		
//			
//			writer.print("<folder>");
//			writer.print("<folderId>70</folderId>");
//			writer.print("<parent>5</parent>");
//			writer.print("<name>sales docs</name>");
//			writer.print("<type>1234</type>");
//			writer.print("</folder>");			
//			
//			writer.print("<folder>");
//			writer.print("<folderId>71</folderId>");
//			writer.print("<parent>70</parent>");
//			writer.print("<name>2012</name>");
//			writer.print("<type>1234</type>");
//			writer.print("</folder>");				
//			
//			writer.print("<folder>");
//			writer.print("<folderId>72</folderId>");
//			writer.print("<parent>70</parent>");
//			writer.print("<name>2013</name>");
//			writer.print("<type>1234</type>");
//			writer.print("</folder>");		
			
		}else if("shared".equals(type)){
			writer.print("<folder>");
			writer.print("<folderId>80</folderId>");
			writer.print("<parent>5</parent>");
			writer.print("<name>tech team</name>");
			writer.print("<type>1234</type>");
			writer.print("</folder>");
			
			writer.print("<folder>");
			writer.print("<folderId>90</folderId>");
			writer.print("<parent>80</parent>");
			writer.print("<name>2012</name>");
			writer.print("<type>1234</type>");
			writer.print("</folder>");
			
			writer.print("<folder>");
			writer.print("<folderId>91</folderId>");
			writer.print("<parent>80</parent>");
			writer.print("<name>2011</name>");
			writer.print("<type>1234</type>");
			writer.print("</folder>");		
			
			writer.print("<folder>");
			writer.print("<folderId>92</folderId>");
			writer.print("<parent>5</parent>");
			writer.print("<name>sales team 1</name>");
			writer.print("<type>1234</type>");
			writer.print("</folder>");			
			
			writer.print("<folder>");
			writer.print("<folderId>93</folderId>");
			writer.print("<parent>92</parent>");
			writer.print("<name>2013</name>");
			writer.print("<type>1234</type>");
			writer.print("</folder>");
			
			writer.print("<folder>");
			writer.print("<folderId>94</folderId>");
			writer.print("<parent>92</parent>");
			writer.print("<name>2012</name>");
			writer.print("<type>1234</type>");
			writer.print("</folder>");					
			
			writer.print("<folder>");
			writer.print("<folderId>95</folderId>");
			writer.print("<parent>5</parent>");
			writer.print("<name>sales team 2</name>");
			writer.print("<type>1234</type>");
			writer.print("</folder>");					
		
			writer.print("<folder>");
			writer.print("<folderId>96</folderId>");
			writer.print("<parent>95</parent>");
			writer.print("<name>2012</name>");
			writer.print("<type>1234</type>");
			writer.print("</folder>");	
		}else{
			writer.print("<folder>");
			writer.print("<folderId>97</folderId>");
			writer.print("<parent>5</parent>");
			writer.print("<name>folder1</name>");
			writer.print("<type>1234</type>");
			writer.print("</folder>");				
			
			writer.print("<folder>");
			writer.print("<folderId>98</folderId>");
			writer.print("<parent>97</parent>");
			writer.print("<name>2012</name>");
			writer.print("<type>1234</type>");
			writer.print("</folder>");			
			
			writer.print("<folder>");
			writer.print("<folderId>99</folderId>");
			writer.print("<parent>5</parent>");
			writer.print("<name>folder2</name>");
			writer.print("<type>1234</type>");
			writer.print("</folder>");					
			
		}
		
		writer.write("</list>");
		
	}
}
