package de.unihamburg.zbh.fishoracle.server;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import de.unihamburg.zbh.fishoracle.client.data.FoProject;
import de.unihamburg.zbh.fishoracle.client.data.FoUser;
import de.unihamburg.zbh.fishoracle.client.exceptions.UserException;
import de.unihamburg.zbh.fishoracle.client.rpc.ProjectService;
import de.unihamburg.zbh.fishoracle.server.data.DBInterface;

public class ProjectServiceImpl extends RemoteServiceServlet implements ProjectService {
	
	private static final long serialVersionUID = 1L;

	public FoUser isAdmin() throws UserException{
		HttpServletRequest request=this.getThreadLocalRequest();
		HttpSession session=request.getSession();
		
		FoUser user = (FoUser) session.getAttribute("user");
		
		if(!user.getIsAdmin()){
			
			throw new UserException("Permission denied!");
			
		}
		return user;
	}
	
	public FoUser getSessionUserObject(){
		
		HttpServletRequest request=this.getThreadLocalRequest();
		HttpSession session=request.getSession();
		
		FoUser user = (FoUser) session.getAttribute("user");
		
		return user;
	}
	
	@Override
	public void add(FoProject project) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public FoProject[] fetch() throws Exception {
		
		String servletContext = this.getServletContext().getRealPath("/");
		
		DBInterface db = new DBInterface(servletContext);
		
		FoUser u = getSessionUserObject();
		
		FoProject[] projects = null;
		 
		if(u.getIsAdmin()){
			
			projects = db.getAllProjects();
		} else {
			projects = db.getProjectsForUser(u, false, false);
		}
		
		return projects;
	}

	@Override
	public void update(FoProject project) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(int projectId) {
		// TODO Auto-generated method stub
		
	}

}
