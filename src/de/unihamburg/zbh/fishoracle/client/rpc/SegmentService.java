package de.unihamburg.zbh.fishoracle.client.rpc;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;

import de.unihamburg.zbh.fishoracle.client.data.FoCnSegment;

public interface SegmentService extends RemoteService {
	
	FoCnSegment add(FoCnSegment foCnSegment);
	FoCnSegment[] fetch(int mstudyId) throws Exception;
	public void update(FoCnSegment CnSegment);
	public void delete(int segmentId);
	
	public static class Util {

		public static SegmentServiceAsync getInstance() {

			return GWT.create(SegmentService.class);
		}
	}
}