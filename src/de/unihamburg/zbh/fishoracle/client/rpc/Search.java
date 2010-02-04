package de.unihamburg.zbh.fishoracle.client.rpc;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import de.unihamburg.zbh.fishoracle.client.data.CopyNumberChange;
import de.unihamburg.zbh.fishoracle.client.data.GWTImageInfo;
import de.unihamburg.zbh.fishoracle.client.data.Gen;
import de.unihamburg.zbh.fishoracle.client.data.QueryInfo;

@RemoteServiceRelativePath("search")
public interface Search extends RemoteService {

	
	public GWTImageInfo generateImage(QueryInfo q) throws Exception;
	public GWTImageInfo redrawImage(GWTImageInfo imageInfo) throws Exception;
	public CopyNumberChange getCNCInfo(String query);
	public Gen getGeneInfo(String query) throws Exception;
	public CopyNumberChange[] getListOfCncs(boolean isAmplicon);
	public String exportData(GWTImageInfo imageInfo);
	
	public static class Util {

		public static SearchAsync getInstance() {

			return GWT.create(Search.class);
		}
	}

}
