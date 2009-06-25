package de.unihamburg.zbh.fishoracle.client.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;

import de.unihamburg.zbh.fishoracle.client.data.ImageInfo;

public interface SearchAsync {

	
	public void generateImage(String query, String searchType, int winWidth, AsyncCallback<ImageInfo> callback);
	public void resizeImage(ImageInfo imageInfo, AsyncCallback<String> callback);

}
