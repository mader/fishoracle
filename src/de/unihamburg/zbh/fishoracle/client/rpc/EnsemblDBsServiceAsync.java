package de.unihamburg.zbh.fishoracle.client.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;

import de.unihamburg.zbh.fishoracle.client.data.FoEnsemblDBs;

public interface EnsemblDBsServiceAsync {
	
	void add(FoEnsemblDBs foEdbs, AsyncCallback<FoEnsemblDBs> callback);
	void fetch(AsyncCallback<FoEnsemblDBs[]> callback);
	void delete(int edbsId, AsyncCallback<Void> callback);
	
}
