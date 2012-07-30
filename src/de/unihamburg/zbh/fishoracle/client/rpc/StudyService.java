package de.unihamburg.zbh.fishoracle.client.rpc;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import de.unihamburg.zbh.fishoracle.client.data.FoStudy;

@RemoteServiceRelativePath("StudyService")
public interface StudyService extends RemoteService {

	FoStudy add(FoStudy foStudy);
	FoStudy[] fetch(String operationId, int projectId) throws Exception;
	public void update(FoStudy foStudy);
	public void delete(int studyId);
	
	public static class Util {

		public static StudyServiceAsync getInstance() {

			return GWT.create(StudyService.class);
		}
	}
}