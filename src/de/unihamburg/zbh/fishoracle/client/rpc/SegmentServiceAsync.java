package de.unihamburg.zbh.fishoracle.client.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;

import de.unihamburg.zbh.fishoracle.client.data.FoCnSegment;

public interface SegmentServiceAsync {

	void add(FoCnSegment foMicroarraystudy, AsyncCallback<FoCnSegment> callback);

	void delete(int mstudyId, AsyncCallback<Void> callback);

	void fetch(int mstudyId,
			AsyncCallback<FoCnSegment[]> callback);

	void update(FoCnSegment foMicroarraystudy, AsyncCallback<Void> callback);
}