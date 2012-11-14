package de.unihamburg.zbh.fishoracle.client.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;

import de.unihamburg.zbh.fishoracle.client.data.FoTranslocation;

public interface TranslocationServiceAsync {

	void add(FoTranslocation foTranslocation,
				AsyncCallback<FoTranslocation> callback);

	void fetch(int studyId, AsyncCallback<FoTranslocation[][]> callback);

	void delete(int translocId, AsyncCallback<Void> callback);

	void update(FoTranslocation transloc, AsyncCallback<Void> callback);

}
