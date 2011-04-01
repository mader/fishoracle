/*
  Copyright (c) 2009-2011 Malte Mader <mader@zbh.uni-hamburg.de>
  Copyright (c) 2009-2011 Center for Bioinformatics, University of Hamburg

  Permission to use, copy, modify, and distribute this software for any
  purpose with or without fee is hereby granted, provided that the above
  copyright notice and this permission notice appear in all copies.

  THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
  WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
  MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
  ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
  WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
  ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
  OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
*/

package de.unihamburg.zbh.fishoracle.server;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;

public class FileUploadServlet extends HttpServlet{

	private static final long serialVersionUID = -7351031304166429877L;

	public FileUploadServlet() {
		// TODO Auto-generated constructor stub
	}
	 private static final String UPLOAD_DIRECTORY = "tmp" + System.getProperty("file.separator");
	  
	      @Override
	      protected void doGet(HttpServletRequest req, HttpServletResponse resp)
	              throws ServletException, IOException {
	          super.doGet(req, resp);
	      }
	  
	      @Override
	      protected void doPost(HttpServletRequest req, HttpServletResponse resp)
	              throws ServletException, IOException {
	          
	    	  String servletContext = this.getServletContext().getRealPath("/");
	    	  
	          // process only multipart requests
	          if (ServletFileUpload.isMultipartContent(req)) {
	  
	              // Create a factory for disk-based file items
	              FileItemFactory factory = new DiskFileItemFactory();
	  
	              // Create a new file upload handler
	              ServletFileUpload upload = new ServletFileUpload(factory);
	  
	              // Parse the request
	              try {
	                  List<FileItem> items = upload.parseRequest(req);
	                  for (FileItem item : items) {
	                      // process only file upload - discard other form item types
	                      if (item.isFormField()) continue;
	                      
	                      String fileName = item.getName();
	                      // get only the file name not whole path
	                      if (fileName != null) {
	                          fileName = FilenameUtils. getName(fileName);
	                      }
	  
	                      File uploadedFile = new File(servletContext + UPLOAD_DIRECTORY, fileName);
	                      if (uploadedFile.createNewFile()) {
	                          item.write(uploadedFile);
	                          resp.setStatus(HttpServletResponse.SC_CREATED);
	                          resp.getWriter().print("The file was created successfully.");
	                          resp.flushBuffer();
	                      } else
	                          throw new IOException("The file already exists in repository.");
	                  }
	              } catch (Exception e) {
	                  resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
	                          "An error occurred while creating the file : " + e.getMessage());
	              }
	  
	          } else {
	              resp.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE,
	                              "Request contents type is not supported by the servlet.");
	          }
	      }
}
