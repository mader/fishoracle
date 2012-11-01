package de.unihamburg.zbh.fishoracle.client.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;

import de.unihamburg.zbh.fishoracle.client.data.FoSegment;

public interface SegmentServiceAsync {

	void add(FoSegment foSegment, AsyncCallback<FoSegment> callback);

	void delete(int studyId, AsyncCallback<Void> callback);

	void fetch(int studyId,
			AsyncCallback<FoSegment[]> callback);

	void update(FoSegment foSegment, AsyncCallback<Void> callback);
}