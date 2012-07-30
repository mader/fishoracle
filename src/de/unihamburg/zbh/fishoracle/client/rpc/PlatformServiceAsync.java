package de.unihamburg.zbh.fishoracle.client.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;

import de.unihamburg.zbh.fishoracle.client.data.FoPlatform;

public interface PlatformServiceAsync {

	void add(FoPlatform foPlatform, AsyncCallback<FoPlatform> callback);

	void delete(int platformId, AsyncCallback<Void> callback);

	void fetch(AsyncCallback<FoPlatform[]> callback);
	
	void fetchTypes(AsyncCallback<String[]> typesCallback);
	
	void update(FoPlatform foPlatform, AsyncCallback<Void> callback);
}