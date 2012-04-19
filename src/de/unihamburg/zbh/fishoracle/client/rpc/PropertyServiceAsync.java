package de.unihamburg.zbh.fishoracle.client.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;

import de.unihamburg.zbh.fishoracle.client.data.FoProperty;

public interface PropertyServiceAsync {

	void add(FoProperty foProperty, AsyncCallback<FoProperty> callback);

	void delete(int propertyId, AsyncCallback<Void> callback);

	void fetch(String operationId, AsyncCallback<FoProperty[]> callback);

	void fetchTypes(AsyncCallback<String[]> callback);

	void update(FoProperty foProperty, AsyncCallback<Void> callback);
}