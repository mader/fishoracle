package de.unihamburg.zbh.fishoracle.client.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;

import de.unihamburg.zbh.fishoracle.client.data.FoChip;

public interface ChipServiceAsync {

	void add(FoChip foChip, AsyncCallback<FoChip> callback);

	void delete(int chipId, AsyncCallback<Void> callback);

	void fetch(AsyncCallback<FoChip[]> callback);
	
	void fetchTypes(AsyncCallback<String[]> typesCallback);
	
	void update(FoChip foChip, AsyncCallback<Void> callback);
}