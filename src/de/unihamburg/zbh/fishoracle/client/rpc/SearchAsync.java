package de.unihamburg.zbh.fishoracle.client.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface SearchAsync {

	
	public void generateImage(String query, String searchType, AsyncCallback<String> callback);

}
