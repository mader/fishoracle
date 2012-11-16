package de.unihamburg.zbh.fishoracle.client.rpc;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import de.unihamburg.zbh.fishoracle.client.data.FoStudy;

@RemoteServiceRelativePath("StudyService")
public interface StudyService extends RemoteService {

	FoStudy add(FoStudy foStudy);
	FoStudy addToProject(int studyId, int projectId) throws Exception;
	FoStudy[] fetch(String operationId, int projectId) throws Exception;
	public void update(FoStudy foStudy);
	void delete(int studyId, int projectId);
	
	FoStudy[] fetchNotInProject(
			String operationId,
			int projectId,
			int notInProjectId);
	
	public static class Util {

		public static StudyServiceAsync getInstance() {

			return GWT.create(StudyService.class);
		}
	}
}