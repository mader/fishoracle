package de.unihamburg.zbh.fishoracle.client.data;

import com.google.gwt.user.client.rpc.IsSerializable;

public class QueryInfo  implements IsSerializable {

	private String queryString;
	private String searchType;
	private String cncPrio;
	private boolean showAmps;
	private boolean showDels;
	private int winWidth;
	
	public QueryInfo() {
		
	}

	public QueryInfo(String queryString, String searchType, String cncPrio,
			boolean showAmps, boolean showDels, int winWidth) {
		super();
		this.queryString = queryString;
		this.searchType = searchType;
		this.cncPrio = cncPrio;
		this.showAmps = showAmps;
		this.showDels = showDels;
		this.winWidth = winWidth;
	}

	public String getQueryString() {
		return queryString;
	}

	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}

	public String getSearchType() {
		return searchType;
	}

	public void setSearchType(String searchType) {
		this.searchType = searchType;
	}

	public String getCncPrio() {
		return cncPrio;
	}

	public void setCncPrio(String cncPrio) {
		this.cncPrio = cncPrio;
	}

	public boolean isShowAmps() {
		return showAmps;
	}

	public void setShowAmps(boolean showAmps) {
		this.showAmps = showAmps;
	}

	public boolean isShowDels() {
		return showDels;
	}

	public void setShowDels(boolean showDels) {
		this.showDels = showDels;
	}

	public int getWinWidth() {
		return winWidth;
	}

	public void setWinWidth(int winWidth) {
		this.winWidth = winWidth;
	}
	
}
