/*
  Copyright (c) 2012-2013 Malte Mader <mader@zbh.uni-hamburg.de>
  Copyright (c) 2012-2013 Center for Bioinformatics, University of Hamburg

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

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import de.unihamburg.zbh.fishoracle.client.data.FoProject;
import de.unihamburg.zbh.fishoracle.client.data.FoUser;
import de.unihamburg.zbh.fishoracle.client.datasource.OperationId;
import de.unihamburg.zbh.fishoracle.client.exceptions.UserException;
import de.unihamburg.zbh.fishoracle.client.rpc.ProjectService;
import de.unihamburg.zbh.fishoracle.server.data.DBInterface;
import de.unihamburg.zbh.fishoracle.server.data.SessionData;

public class ProjectServiceImpl extends RemoteServiceServlet implements ProjectService {
	
	private static final long serialVersionUID = 1L;
	
	private SessionData getSessionData(){
		return new SessionData(this.getThreadLocalRequest());
	}
	
	@Override
	public FoProject add(FoProject foProject) throws UserException {
		
		SessionData sessionData = getSessionData();
		
		sessionData.isAdmin();
		
		String servletContext = this.getServletContext().getRealPath("/");
		
		DBInterface db = new DBInterface(servletContext);
		
		return db.addFoProject(foProject);
	}
	
	@Override
	public FoProject[] fetch(String operationId) throws Exception {
		
		String servletContext = this.getServletContext().getRealPath("/");
		
		DBInterface db = new DBInterface(servletContext);
		
		SessionData sessionData = getSessionData();
		
		FoUser u = sessionData.getSessionUserObject();
		
		FoProject[] projects = null;
		
		if(u.getIsAdmin()){
			
			projects = db.getAllProjects();
			
		} else if (!u.getIsAdmin() && operationId.equals(OperationId.PROJECT_FETCH_ALL)) {
			
			projects = db.getProjectsForUser(u, false, false);
			
		} else if(!u.getIsAdmin() && operationId.equals(OperationId.PROJECT_FETCH_READ_WRITE)){
			
			projects = db.getProjectsForUser(u, false, true);
		}
		
		return projects;
	}

	@Override
	public void update(FoProject project) {
		// TODO Auto-generated method stub
	}

	@Override
	public void delete(int projectId) throws UserException {
		
		SessionData sessionData = getSessionData();
		
		sessionData.isAdmin();
		
		String servletContext = this.getServletContext().getRealPath("/");
		
		DBInterface db = new DBInterface(servletContext);
		
		db.removeProject(projectId);	
	}
}