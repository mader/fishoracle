package de.unihamburg.zbh.fishoracle.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import de.unihamburg.zbh.fishoracle.client.data.FoMicroarraystudy;
import de.unihamburg.zbh.fishoracle.client.data.FoProject;
import de.unihamburg.zbh.fishoracle.client.data.FoUser;
import de.unihamburg.zbh.fishoracle.client.data.OperationId;
import de.unihamburg.zbh.fishoracle.client.rpc.MicroarrayStudyService;
import de.unihamburg.zbh.fishoracle.server.data.DBInterface;
import de.unihamburg.zbh.fishoracle.server.data.SessionData;

public class MicroarrayStudyServiceImpl extends RemoteServiceServlet implements MicroarrayStudyService {
	
	private static final long serialVersionUID = 1L;
	
	private SessionData getSessionData(){
		return new SessionData(this.getThreadLocalRequest());
	}
	
	@Override
	public FoMicroarraystudy add(FoMicroarraystudy foMicroarraystudy) {
		return foMicroarraystudy;
		// TODO Auto-generated method stub
	}

	@Override
	public FoMicroarraystudy[] fetch(String operationId, int projectId) throws Exception {
		
		FoMicroarraystudy[] mstudies = null;
		
		String servletContext = this.getServletContext().getRealPath("/");
		
		DBInterface db = new DBInterface(servletContext);
		
		SessionData sessionData = getSessionData();
		
		FoUser u = sessionData.getSessionUserObject();
		
		FoProject[] projects = null;
		
		if(operationId.equals(OperationId.MSTUDY_FETCH_ALL)){
		
			if(u.getIsAdmin()){
			
				projects = db.getAllProjects();
			} else {
				projects = db.getProjectsForUser(u, false, false);
			}
		
			int[] projectIds = new int[projects.length];
				
			for(int i = 0; i < projects.length; i++){
			
				projectIds[i] = projects[i].getId();
			}
			
			mstudies =  db.getMicroarraystudiesForProject(projectIds, false);
		
		}
		
		if(operationId.equals(OperationId.MSTUDY_FETCH_FOR_PROJECT)){
			
			mstudies = db.getMicroarraystudiesForProject(new int[]{projectId}, true);
		
		}
		return mstudies;
	}

	@Override
	public void update(FoMicroarraystudy foMicroarraystudy) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(int mstudyId) {
		
		String servletContext = this.getServletContext().getRealPath("/");
		
		DBInterface db = new DBInterface(servletContext);
		
		db.removeMstudy(mstudyId);
	}
}