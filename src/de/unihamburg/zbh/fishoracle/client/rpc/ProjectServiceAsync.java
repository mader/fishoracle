package de.unihamburg.zbh.fishoracle.client.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;

import de.unihamburg.zbh.fishoracle.client.data.FoProject;

public interface ProjectServiceAsync {

	void add(FoProject foProject, AsyncCallback<FoProject> callback);

	void fetch(String operationId, AsyncCallback<FoProject[]> callback);

	void update(FoProject project, AsyncCallback<Void> callback);

	void delete(int projectId, AsyncCallback<Void> callback);
}