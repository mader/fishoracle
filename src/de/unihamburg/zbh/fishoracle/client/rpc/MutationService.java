package de.unihamburg.zbh.fishoracle.client.rpc;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;

import de.unihamburg.zbh.fishoracle.client.data.FoConfigData;
import de.unihamburg.zbh.fishoracle.client.data.FoSNPMutation;
import de.unihamburg.zbh.fishoracle.client.rpc.MutationServiceAsync;

public interface MutationService extends RemoteService {

	FoSNPMutation add(FoSNPMutation foMutation);
	FoSNPMutation[] fetch(int studyId) throws Exception;
	FoSNPMutation[] fetchForConfig(String geneId, int trackId, FoConfigData cd);
	public void update(FoSNPMutation mutation);
	public void delete(int mutationId);
	
	public static class Util {

		public static MutationServiceAsync getInstance() {

			return GWT.create(MutationService.class);
		}
	}
}
