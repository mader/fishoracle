package de.unihamburg.zbh.fishoracle.client.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;

import de.unihamburg.zbh.fishoracle.client.data.FoConfigData;
import de.unihamburg.zbh.fishoracle.client.data.FoSNPMutation;

public interface MutationServiceAsync {

	void add(FoSNPMutation foMutation, AsyncCallback<FoSNPMutation> callback);

	void delete(int mutationId, AsyncCallback<Void> callback);

	void fetch(int studyId, AsyncCallback<FoSNPMutation[]> callback);

	void fetchForConfig(String geneId, int trackId, FoConfigData cd, AsyncCallback<FoSNPMutation[]> callback);
	
	void update(FoSNPMutation mutation, AsyncCallback<Void> callback);

}
