package de.unihamburg.zbh.fishoracle.client.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;

import de.unihamburg.zbh.fishoracle.client.data.CopyNumberChange;
import de.unihamburg.zbh.fishoracle.client.data.GWTImageInfo;
import de.unihamburg.zbh.fishoracle.client.data.Gen;

public interface SearchAsync {

	public void generateImage(String query, String searchType, int winWidth, AsyncCallback<GWTImageInfo> callback);
	public void redrawImage(GWTImageInfo imageInfo, AsyncCallback<GWTImageInfo> callback);
	public void getCNCInfo(String query, AsyncCallback<CopyNumberChange> callback);
	public void getGeneInfo(String query, AsyncCallback<Gen> callback);
}
