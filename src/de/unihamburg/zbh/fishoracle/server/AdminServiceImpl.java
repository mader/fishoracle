package de.unihamburg.zbh.fishoracle.server;

import java.util.ArrayList;

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
		
		ArrayList<User> users = db.fetchAllUsers();
		
		User[] alluser = new User[users.size()];
		
		int i = 0;
		
		while(users.size() > i){
			
			alluser[i] = users.get(i);
			
			i++;
			
		}
		
		return alluser;
		
	}
	
}
