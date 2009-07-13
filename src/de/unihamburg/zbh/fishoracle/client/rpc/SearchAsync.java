package de.unihamburg.zbh.fishoracle.client.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;

import de.unihamburg.zbh.fishoracle.client.data.GWTImageInfo;

public interface SearchAsync {

	
	public void generateImage(String query, String searchType, int winWidth, AsyncCallback<GWTImageInfo> callback);
	public void redrawImage(GWTImageInfo imageInfo, AsyncCallback<GWTImageInfo> callback);

}
