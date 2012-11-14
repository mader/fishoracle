package de.unihamburg.zbh.fishoracle.client.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;

import de.unihamburg.zbh.fishoracle.client.data.FoGenericFeature;

public interface FeatureServiceAsync {

	void fetchTypes(AsyncCallback<String[]> callback);

	void fetch(int studyId, AsyncCallback<FoGenericFeature[]> featureCallback);
	
}
