package de.unihamburg.zbh.fishoracle.client.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;

import de.unihamburg.zbh.fishoracle.client.data.FoOrgan;

public interface OrganServiceAsync {

	void add(FoOrgan foMicroarraystudy, AsyncCallback<FoOrgan> callback);

	void delete(int organId, AsyncCallback<Void> callback);

	void fetch(String operationId, AsyncCallback<FoOrgan[]> callback);

	void update(FoOrgan foOrgan, AsyncCallback<Void> callback);
}