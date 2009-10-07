package de.unihamburg.zbh.fishoracle.client.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;

import de.unihamburg.zbh.fishoracle.client.data.CopyNumberChange;
import de.unihamburg.zbh.fishoracle.client.data.GWTImageInfo;
import de.unihamburg.zbh.fishoracle.client.data.Gen;
import de.unihamburg.zbh.fishoracle.client.data.QueryInfo;

public interface SearchAsync {
	
	public void generateImage(QueryInfo q, AsyncCallback<GWTImageInfo> callback);
	public void redrawImage(GWTImageInfo imageInfo, AsyncCallback<GWTImageInfo> callback);
	public void getCNCInfo(String query, AsyncCallback<CopyNumberChange> callback);
	public void getGeneInfo(String query, AsyncCallback<Gen> callback);
	
}
