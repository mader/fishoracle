package de.unihamburg.zbh.fishoracle.server.data;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import de.unihamburg.zbh.fishoracle.client.data.FoUser;
import de.unihamburg.zbh.fishoracle.client.exceptions.UserException;

public class SessionData {
	
	HttpSession session;

	public SessionData(HttpServletRequest request) {
		session = request.getSession();
	}
	
	public FoUser getSessionUserObject(){
		
		FoUser user = (FoUser) session.getAttribute("user");
		
		return user;
	}
	
	public FoUser isAdmin() throws UserException {
		
		FoUser user = getSessionUserObject();
		
		if(!user.getIsAdmin()){
			
			throw new UserException("Permission denied!");
			
		}
		return user;
	}
}