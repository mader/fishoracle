/*
  Copyright (c) 2013 Malte Mader <mader@zbh.uni-hamburg.de>
  Copyright (c) 2013 Center for Bioinformatics, University of Hamburg

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

import de.unihamburg.zbh.fishoracle.client.data.FoGroup;
import de.unihamburg.zbh.fishoracle.client.data.FoProject;
import de.unihamburg.zbh.fishoracle.client.data.FoUser;
import de.unihamburg.zbh.fishoracle.client.exceptions.UserException;
import de.unihamburg.zbh.fishoracle.client.rpc.GroupService;
import de.unihamburg.zbh.fishoracle.server.data.DBInterface;
import de.unihamburg.zbh.fishoracle.server.data.SessionData;

public class GroupServiceImpl extends RemoteServiceServlet implements GroupService {

	private static final long	serialVersionUID	= 1L;

	private SessionData getSessionData(){
		return new SessionData(this.getThreadLocalRequest());
	}
	
	@Override
	public FoGroup[] getAllFoGroups() throws Exception {
		SessionData s = getSessionData();
		s.isAdmin();
		String servletContext = this.getServletContext().getRealPath("/");
		
		DBInterface db = new DBInterface(servletContext);
		
		return db.getAllGroups();
	}
	
	@Override
	public FoGroup[] getAllGroupsForUser() throws Exception {
		
		SessionData s = getSessionData();
		FoUser user = s.getSessionUserObject();
		
		String servletContext = this.getServletContext().getRealPath("/");
		
		DBInterface db = new DBInterface(servletContext);
		
		return db.getAllGroupsForUser(user);
	}
	
	@Override
	public FoGroup addGroup(FoGroup foGroup) throws UserException {
		SessionData s = getSessionData();
		s.isAdmin();
		String servletContext = this.getServletContext().getRealPath("/");
		
		DBInterface db = new DBInterface(servletContext);
		
		return db.addGroup(foGroup);
	}
	
	@Override
	public void deleteGroup(FoGroup foGroup) throws UserException {
		
		SessionData s = getSessionData();
		s.isAdmin();
		String servletContext = this.getServletContext().getRealPath("/");
		
		DBInterface db = new DBInterface(servletContext);
		
		db.deleteGroup(foGroup);
		
	}
	
	@Override
	public void addUserToFoGroup(int groupId, int userId) throws UserException {
		
		SessionData s = getSessionData();
		s.isAdmin();
		String servletContext = this.getServletContext().getRealPath("/");
		
		DBInterface db = new DBInterface(servletContext);
		
		db.addUserGroup(groupId, userId);
	}
	
	@Override
	public boolean removeUserFromFoGroup(int groupId, int userId) throws UserException {
		
		SessionData s = getSessionData();
		s.isAdmin();
		String servletContext = this.getServletContext().getRealPath("/");
		
		DBInterface db = new DBInterface(servletContext);
		
		db.removeUserFromGroup(groupId, userId);
		
		return true;
	}
	
	@Override
	public FoGroup[] getAllGroupsExceptFoProject(FoProject foProject) {
		String servletContext = this.getServletContext().getRealPath("/");
		
		DBInterface db = new DBInterface(servletContext);
		
		return db.getAllGroupsExceptProject(foProject);
	}
}
