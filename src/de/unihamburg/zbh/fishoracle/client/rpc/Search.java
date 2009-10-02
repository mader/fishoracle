package de.unihamburg.zbh.fishoracle.client.rpc;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import de.unihamburg.zbh.fishoracle.client.data.CopyNumberChange;
import de.unihamburg.zbh.fishoracle.client.data.GWTImageInfo;
import de.unihamburg.zbh.fishoracle.client.data.Gen;

@RemoteServiceRelativePath("testbild")
public interface Search extends RemoteService {

	
	public GWTImageInfo generateImage(String query, String searchType, int winWidth);
	public GWTImageInfo redrawImage(GWTImageInfo imageInfo);
	public CopyNumberChange getCNCInfo(String query);
	public Gen getGeneInfo(String query);
	
	public static class Util {

		public static SearchAsync getInstance() {

			return GWT.create(Search.class);
		}
	}

}
