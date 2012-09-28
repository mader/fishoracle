package de.unihamburg.zbh.fishoracle.client.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface FeatureServiceAsync {

	void fetchTypes(AsyncCallback<String[]> callback);
	
}
