/*
  Copyright (c) 2009-2012 Malte Mader <mader@zbh.uni-hamburg.de>
  Copyright (c) 2009-2012 Center for Bioinformatics, University of Hamburg

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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import de.unihamburg.zbh.fishoracle.client.data.FoUser;
import de.unihamburg.zbh.fishoracle.client.datasource.OperationId;
import de.unihamburg.zbh.fishoracle.client.exceptions.UserException;
import de.unihamburg.zbh.fishoracle.client.rpc.UserService;
import de.unihamburg.zbh.fishoracle.server.data.DBInterface;
import de.unihamburg.zbh.fishoracle.server.data.SessionData;

public class UserServiceImpl extends RemoteServiceServlet implements UserService {

	private static final long serialVersionUID = 1929980857354870885L;

	private SessionData getSessionData(){
		return new SessionData(this.getThreadLocalRequest());
	}
	
	@Override
	public FoUser add(FoUser user) throws Exception{
		
		String servletContext = this.getServletContext().getRealPath("/");
		
		DBInterface db = new DBInterface(servletContext);
		
		FoUser storedUser = db.insertUser(user);
		
		return storedUser;
	}
	
	@Override
	public FoUser[] fetch() throws Exception{
		
		String servletContext = this.getServletContext().getRealPath("/");
		
		DBInterface db = new DBInterface(servletContext);
		
		FoUser[] users = db.getAllUsers();
		
		return users;
	}
	
	@Override
	public FoUser[] fetchUsersForGroup(int groupId) throws UserException {
		
		SessionData s = getSessionData();
		s.isAdmin();
		String servletContext = this.getServletContext().getRealPath("/");
		
		DBInterface db = new DBInterface(servletContext);
		
		return db.getUsersForGroup(groupId);
	}
	
	public FoUser[] getSessionUserObject(){

		HttpServletRequest request=this.getThreadLocalRequest();
		HttpSession session=request.getSession();

		FoUser user = (FoUser) session.getAttribute("user");

		return new FoUser[]{user};
	}
	
	@Override
	public FoUser login(String userName, String password) throws Exception {
		
		String servletContext = this.getServletContext().getRealPath("/");
		
		DBInterface db = new DBInterface(servletContext);
		
		FoUser user = db.getUser(userName, password);
		
		HttpServletRequest request=this.getThreadLocalRequest();
		HttpSession session=request.getSession();
		
		session.setMaxInactiveInterval(-1);
		session.setAttribute("user", user);
		
		return user;
	}
	
	@Override
	public FoUser update(String operationId, FoUser user) throws Exception {
		
		String servletContext = this.getServletContext().getRealPath("/");
		
		FoUser[] sessionUser = getSessionUserObject();
		
		DBInterface db = new DBInterface(servletContext);
		
		if(operationId.equals(OperationId.USER_UPDATE_PROFILE)){
		
			db.updateProfile(user, sessionUser[0]);
			
			getSessionData().getSession().setAttribute("user", user);
		}
		
		if(operationId.equals(OperationId.USER_UPDATE_PASSWORD)){
			db.updatePassword(user, sessionUser[0]);
			user.setPw("");
		}
		
		if(operationId.equals(OperationId.USER_UPDATE_PASSWORD_ADMIN)){
			getSessionData().isAdmin();
			db.setPassword(user.getId(), user.getPw());
			user.setPw("");
		}
		
		if(operationId.equals(OperationId.USER_UPDATE_ISADMIN)){
			getSessionData().isAdmin();
			boolean bool = (1 == db.setAdminStatus(user.getId(), user.getIsAdmin())) ? true : false;
			user.setIsAdmin(bool);
		}
		
		if(operationId.equals(OperationId.USER_UPDATE_ISACTIVE)){
			getSessionData().isAdmin();
			boolean bool = (1 == db.setActiveStatus(user.getId(), user.getIsActive())) ? true : false;
			user.setIsActive(bool);
		}
		
		return user;
	}

	@Override
	public void logout(){
		HttpServletRequest request=this.getThreadLocalRequest();
		HttpSession session=request.getSession();
		session.invalidate();
	}
	
	@Override
	public FoUser remove(FoUser user) {
		return null;
	}
}