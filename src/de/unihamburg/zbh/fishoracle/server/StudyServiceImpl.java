package de.unihamburg.zbh.fishoracle.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import de.unihamburg.zbh.fishoracle.client.data.FoStudy;
import de.unihamburg.zbh.fishoracle.client.data.FoProject;
import de.unihamburg.zbh.fishoracle.client.data.FoUser;
import de.unihamburg.zbh.fishoracle.client.datasource.OperationId;
import de.unihamburg.zbh.fishoracle.client.rpc.StudyService;
import de.unihamburg.zbh.fishoracle.server.data.DBInterface;
import de.unihamburg.zbh.fishoracle.server.data.SessionData;

public class StudyServiceImpl extends RemoteServiceServlet implements StudyService {
	
	private static final long serialVersionUID = 1L;
	
	private SessionData getSessionData(){
		return new SessionData(this.getThreadLocalRequest());
	}
	
	@Override
	public FoStudy add(FoStudy foStudy) {
		return foStudy;
		// TODO Auto-generated method stub
	}

	@Override
	public FoStudy[] fetch(String operationId, int projectId) throws Exception {
		
		FoStudy[] studies = null;
		
		String servletContext = this.getServletContext().getRealPath("/");
		
		DBInterface db = new DBInterface(servletContext);
		
		SessionData sessionData = getSessionData();
		
		FoUser u = sessionData.getSessionUserObject();
		
		FoProject[] projects = null;
		
		if(operationId.equals(OperationId.STUDY_FETCH_ALL)){
		
			if(u.getIsAdmin()){
			
				projects = db.getAllProjects();
			} else {
				projects = db.getProjectsForUser(u, false, false);
			}
		
			int[] projectIds = new int[projects.length];
				
			for(int i = 0; i < projects.length; i++){
			
				projectIds[i] = projects[i].getId();
			}
			
			studies =  db.getStudiesForProject(projectIds, false);
		
		}
		
		if(operationId.equals(OperationId.STUDY_FETCH_FOR_PROJECT)){
			
			studies = db.getStudiesForProject(new int[]{projectId}, true);
		
		}
		return studies;
	}

	@Override
	public void update(FoStudy foStudy) {
		// TODO Auto-generated method stub
	}

	@Override
	public void delete(int studyId) {
		
		String servletContext = this.getServletContext().getRealPath("/");
		
		DBInterface db = new DBInterface(servletContext);
		
		db.removeStudy(studyId);
	}
}