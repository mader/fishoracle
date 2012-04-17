package de.unihamburg.zbh.fishoracle.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import de.unihamburg.zbh.fishoracle.client.data.FoProjectAccess;
import de.unihamburg.zbh.fishoracle.client.rpc.ProjectAccessService;
import de.unihamburg.zbh.fishoracle.server.data.DBInterface;
import de.unihamburg.zbh.fishoracle.server.data.SessionData;

public class ProjectAccessServiceImpl extends RemoteServiceServlet implements ProjectAccessService {

	private static final long serialVersionUID = 1L;

	private SessionData getSessionData(){
		return new SessionData(this.getThreadLocalRequest());
	}
	
	@Override
	public FoProjectAccess add(FoProjectAccess foProjectAccess) {
		// TODO Auto-generated method stub
		return null;
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
	public void delete(int projectAccessId) {
		// TODO Auto-generated method stub
		
	}

	
	
}
