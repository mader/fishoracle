package de.unihamburg.zbh.fishoracle.client.rpc;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;

import de.unihamburg.zbh.fishoracle.client.data.FoGenericFeature;

public interface FeatureService extends RemoteService {

	//FoGenericFeature add(FoGenericFeature foFeature);
	FoGenericFeature[] fetch(int studyId) throws Exception;
	//public void update(FoGenericFeature fiFeature);
	//public void delete(int featureId);
	String[] fetchTypes() throws Exception;
	
	public static class Util {

		public static FeatureServiceAsync getInstance() {

			return GWT.create(SegmentService.class);
		}
	}
	
}
