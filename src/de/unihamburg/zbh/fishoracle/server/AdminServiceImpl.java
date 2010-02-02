package de.unihamburg.zbh.fishoracle.server;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import de.unihamburg.zbh.fishoracle.client.rpc.Admin;

import de.unihamburg.zbh.fishoracle.client.data.User;
import de.unihamburg.zbh.fishoracle.client.exceptions.UserException;
import de.unihamburg.zbh.fishoracle.server.data.DBQuery;

public class AdminServiceImpl extends RemoteServiceServlet implements Admin {

	private static final long serialVersionUID = 4434993420272783276L;

	public User[] getAllUsers() throws Exception {
		
		HttpServletRequest request=this.getThreadLocalRequest();
		HttpSession session=request.getSession();
		
		User user = (User) session.getAttribute("user");
		
		if(!user.getIsAdmin()){
			
			throw new UserException("Permission denied!");
			
		}
		
		String servletContext = this.getServletContext().getRealPath("/");
		
		DBQuery db = new DBQuery(servletContext);
		
		User[] users = db.fetchAllUsers();
		
		return users;
		
	}
	
	public int[] toggleFlag(int id, String flag, int rowIndex, int colIndex) throws Exception {
	
		HttpServletRequest request=this.getThreadLocalRequest();
		HttpSession session=request.getSession();
		
		User user = (User) session.getAttribute("user");
		
		if(!user.getIsAdmin()){
			
			throw new UserException("Permission denied!");
			
		}
		
		String servletContext = this.getServletContext().getRealPath("/");
		
		DBQuery db = new DBQuery(servletContext);
		
		String activeOrAdmin = null;
		
		if(colIndex == 5){
			
			activeOrAdmin = "isactive";
			
		}
		
		if(colIndex == 6){
			
			activeOrAdmin = "isadmin";
		}
		
		int isActivated = db.setIsActive(id, flag, activeOrAdmin);
		
		int isActiveRonIndex[] = {rowIndex, colIndex, isActivated};
		
		return isActiveRonIndex;
		
	}
	
	
}
