package com.speno.xedm.gui.server;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author deluxjun
 * 
 */
public class MockDirectUploadServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	private long FILE_SIZE_LIMIT = 100 * 1024 * 1024; // 100 MiB

	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		try {
			DiskFileItemFactory fileItemFactory = new DiskFileItemFactory();
			ServletFileUpload fileUpload = new ServletFileUpload(fileItemFactory);
			fileUpload.setSizeMax(FILE_SIZE_LIMIT);

			List<FileItem> items = fileUpload.parseRequest(req);

			for (FileItem item : items) {
				if (item.isFormField()) {
					System.out.println("Received form field:");
					System.out.println("Name: " + item.getFieldName());
					System.out.println("Value: " + item.getString());
				} else {
					System.out.println("Received file:");
					System.out.println("Name: " + item.getName());
					System.out.println("Size: " + item.getSize());
				}

				if (!item.isFormField()) {
					if (item.getSize() > FILE_SIZE_LIMIT) {
						resp.sendError(HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE, "File size exceeds limit");

						return;
					}

					// Typically here you would process the file in some way:
					// InputStream in = item.getInputStream();
					// ...

					if (!item.isInMemory())
						item.delete();
				}
			}
			
			Thread.sleep(1000);
		} catch (Exception e) {
			System.out.println("Throwing servlet exception for unhandled exception :"+  e.getMessage());
			throw new ServletException(e);
		}
	}

}