package de.unihamburg.zbh.fishoracle.client.rpc;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.client.rpc.ServiceDefTarget;

@RemoteServiceRelativePath("testbild")
public interface Search extends RemoteService {

	
	public String generateImage(String query, String searchType);
	
	
	public static class Util {

		public static SearchAsync getInstance() {

			return GWT.create(Search.class);
		}
	}

}
