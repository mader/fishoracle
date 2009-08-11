package de.unihamburg.zbh.fishoracle.server;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import de.unihamburg.zbh.fishoracle.client.data.User;
import de.unihamburg.zbh.fishoracle.client.rpc.UserService;
import de.unihamburg.zbh.fishoracle.server.data.DBQuery;

public class UserServiceImpl extends RemoteServiceServlet implements UserService {

	private static final long serialVersionUID = 1929980857354870885L;

	public User register(User user){
		
		String servletContext = this.getServletContext().getRealPath("/");
		
		DBQuery db = new DBQuery(servletContext);
		
		
		
		db.insertUserData(user);
		
		return null;
	}
	
	public User login(String userName, String password){
		
		String servletContext = this.getServletContext().getRealPath("/");
		
		DBQuery db = new DBQuery(servletContext);
		
		User user = db.getUserData(userName, password);
		
		HttpServletRequest request=this.getThreadLocalRequest();
		HttpSession session=request.getSession();
		System.out.println("Session ID: " + session.getId());
		
		session.setAttribute("user", user);
		
		return user;
	}

	public void logout(){
		HttpServletRequest request=this.getThreadLocalRequest();
		HttpSession session=request.getSession();
		session.invalidate();
	}

}
