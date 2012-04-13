package de.unihamburg.zbh.fishoracle.client.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;

import de.unihamburg.zbh.fishoracle.client.data.FoProject;

public interface ProjectServiceAsync {

	void add(FoProject project, AsyncCallback<Void> callback);

	void fetch(AsyncCallback<FoProject[]> callback);

	void update(FoProject project, AsyncCallback<Void> callback);

	void delete(int projectId, AsyncCallback<Void> callback);
}