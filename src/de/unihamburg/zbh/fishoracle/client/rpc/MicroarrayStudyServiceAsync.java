package de.unihamburg.zbh.fishoracle.client.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;

import de.unihamburg.zbh.fishoracle.client.data.FoMicroarraystudy;

public interface MicroarrayStudyServiceAsync {

	void add(FoMicroarraystudy foMicroarraystudy,
			AsyncCallback<FoMicroarraystudy> callback);

	void delete(int mstudyId, AsyncCallback<Void> callback);

	void fetch(String operationId, int projectId, AsyncCallback<FoMicroarraystudy[]> callback);

	void update(FoMicroarraystudy foMicroarraystudy, AsyncCallback<Void> callback);

}
