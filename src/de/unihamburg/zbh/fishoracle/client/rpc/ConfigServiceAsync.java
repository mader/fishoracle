package de.unihamburg.zbh.fishoracle.client.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;

import de.unihamburg.zbh.fishoracle.client.data.FoConfigData;

public interface ConfigServiceAsync {

	void add(FoConfigData foConf, AsyncCallback<Void> callback);
	void fetch(int configId, AsyncCallback<FoConfigData> callback);
	void fetchForUser(int userId, AsyncCallback<FoConfigData[]> callback);	
	void delete(int configId, AsyncCallback<Void> callback);
}
