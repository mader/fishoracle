package de.unihamburg.zbh.fishoracle.client.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;

import de.unihamburg.zbh.fishoracle.client.data.FoCnSegment;

public interface SegmentServiceAsync {

	void add(FoCnSegment foCnSegment, AsyncCallback<FoCnSegment> callback);

	void delete(int studyId, AsyncCallback<Void> callback);

	void fetch(int studyId,
			AsyncCallback<FoCnSegment[]> callback);

	void update(FoCnSegment foCnSegment, AsyncCallback<Void> callback);
}