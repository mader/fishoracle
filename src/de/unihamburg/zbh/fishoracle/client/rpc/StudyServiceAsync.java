package de.unihamburg.zbh.fishoracle.client.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;

import de.unihamburg.zbh.fishoracle.client.data.FoStudy;

public interface StudyServiceAsync {

	void add(FoStudy foStudy,
			AsyncCallback<FoStudy> callback);

	void delete(int studyId, AsyncCallback<Void> callback);

	void fetch(String operationId, int projectId, AsyncCallback<FoStudy[]> callback);

	void update(FoStudy foStudy, AsyncCallback<Void> callback);
}
