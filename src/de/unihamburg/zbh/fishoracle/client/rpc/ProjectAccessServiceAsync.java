package de.unihamburg.zbh.fishoracle.client.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;

import de.unihamburg.zbh.fishoracle.client.data.FoProjectAccess;

public interface ProjectAccessServiceAsync {

	void add(FoProjectAccess foProjectAccess,
			AsyncCallback<FoProjectAccess> callback);

	void delete(int projectAccessId, AsyncCallback<Void> callback);

	void fetch(int projectId, AsyncCallback<FoProjectAccess[]> callback);

	void update(FoProjectAccess foProjectAccess, AsyncCallback<Void> callback);

}
