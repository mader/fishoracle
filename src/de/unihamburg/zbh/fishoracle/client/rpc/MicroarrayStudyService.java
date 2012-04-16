package de.unihamburg.zbh.fishoracle.client.rpc;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import de.unihamburg.zbh.fishoracle.client.data.FoMicroarraystudy;

@RemoteServiceRelativePath("microarraytsudy")
public interface MicroarrayStudyService extends RemoteService {

	FoMicroarraystudy add(FoMicroarraystudy foMicroarraystudy);
	FoMicroarraystudy[] fetch(String operationId, int projectId) throws Exception;
	public void update(FoMicroarraystudy foMicroarraystudy);
	public void delete(int mstudyId);
	
	public static class Util {

		public static MicroarrayStudyServiceAsync getInstance() {

			return GWT.create(MicroarrayStudyService.class);
		}
	}
}