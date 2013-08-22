/*
  Copyright (c) 2012-2013 Malte Mader <mader@zbh.uni-hamburg.de>
  Copyright (c) 2012-2013 Center for Bioinformatics, University of Hamburg

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

import de.unihamburg.zbh.fishoracle.client.data.FoStudy;
import de.unihamburg.zbh.fishoracle.client.data.FoProject;
import de.unihamburg.zbh.fishoracle.client.data.FoUser;
import de.unihamburg.zbh.fishoracle.client.datasource.OperationId;
import de.unihamburg.zbh.fishoracle.client.rpc.StudyService;
import de.unihamburg.zbh.fishoracle.server.data.DBInterface;
import de.unihamburg.zbh.fishoracle.server.data.DataTypeConverter;
import de.unihamburg.zbh.fishoracle.server.data.SessionData;
import de.unihamburg.zbh.fishoracle_db_api.data.Study;

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
	public void delete(int studyId, int projectId) {
		
		String servletContext = this.getServletContext().getRealPath("/");
		
		DBInterface db = new DBInterface(servletContext);
		
		db.removeStudy(studyId, projectId);
	}

	@Override
	public FoStudy addToProject(int studyId, int projectId) throws Exception {
		
		if(projectId == 0){
			throw new Exception("No valid project ID given.");
		}
		
		String servletContext = this.getServletContext().getRealPath("/");
		
		DBInterface db = new DBInterface(servletContext);
		
		Study study = null;
		
		db.addStudyToProject(studyId, projectId);
		
		study = db.getStudyForId(studyId);
		
		return DataTypeConverter.studyToFoStudy(study);
	}

	@Override
	public FoStudy[] fetchNotInProject(String operationId, 
										int projectId, 
										int notInProject) {
		
		FoStudy[] studies = null;
		
		String servletContext = this.getServletContext().getRealPath("/");
		
		DBInterface db = new DBInterface(servletContext);
		
			
		studies = db.getStudieNotInProject(projectId, notInProject);
		
		return studies;
	}
}