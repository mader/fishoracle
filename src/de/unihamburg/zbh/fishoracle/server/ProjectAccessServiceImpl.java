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

import de.unihamburg.zbh.fishoracle.client.data.FoProjectAccess;
import de.unihamburg.zbh.fishoracle.client.exceptions.UserException;
import de.unihamburg.zbh.fishoracle.client.rpc.ProjectAccessService;
import de.unihamburg.zbh.fishoracle.server.data.DBInterface;
import de.unihamburg.zbh.fishoracle.server.data.SessionData;

public class ProjectAccessServiceImpl extends RemoteServiceServlet implements ProjectAccessService {

	private static final long serialVersionUID = 1L;

	private SessionData getSessionData(){
		return new SessionData(this.getThreadLocalRequest());
	}
	
	@Override
	public FoProjectAccess add(FoProjectAccess foProjectAccess) throws UserException {
		
		getSessionData().isAdmin();
		
		String servletContext = this.getServletContext().getRealPath("/");
		
		DBInterface db = new DBInterface(servletContext);
		
		return db.addAccessToProject(foProjectAccess);
	}

	@Override
	public FoProjectAccess[] fetch(int projectId) throws Exception {
		
		getSessionData().isAdmin();
		
		String servletContext = this.getServletContext().getRealPath("/");
		
		DBInterface db = new DBInterface(servletContext);
		
		return db.getProjectAccessForProject(projectId);
	}

	@Override
	public void update(FoProjectAccess foProjectAccess) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(int projectAccessId) throws UserException {
		
		getSessionData().isAdmin();
		
		String servletContext = this.getServletContext().getRealPath("/");
		
		DBInterface db = new DBInterface(servletContext);
		
		db.removeAccessFromProject(projectAccessId);
	}
}