package de.unihamburg.zbh.fishoracle.client.rpc;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;

import de.unihamburg.zbh.fishoracle.client.data.FoSegment;

public interface SegmentService extends RemoteService {
	
	FoSegment add(FoSegment foSegment);
	FoSegment[] fetch(int studyId) throws Exception;
	public void update(FoSegment segment);
	public void delete(int segmentId);
	
	public static class Util {

		public static SegmentServiceAsync getInstance() {

			return GWT.create(SegmentService.class);
		}
	}
}