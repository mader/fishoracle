package de.unihamburg.zbh.fishoracle.client.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;

import de.unihamburg.zbh.fishoracle.client.data.Amplicon;
import de.unihamburg.zbh.fishoracle.client.data.GWTImageInfo;
import de.unihamburg.zbh.fishoracle.client.data.Gen;

public interface SearchAsync {

	public void generateImage(String query, String searchType, int winWidth, AsyncCallback<GWTImageInfo> callback);
	public void redrawImage(GWTImageInfo imageInfo, AsyncCallback<GWTImageInfo> callback);
	public void getAmpliconInfo(String query, AsyncCallback<Amplicon> callback);
	public void getGeneInfo(String query, AsyncCallback<Gen> callback);
}
