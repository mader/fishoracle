package de.unihamburg.zbh.fishoracle.client.data;

import com.google.gwt.user.client.rpc.IsSerializable;

public class QueryInfo  implements IsSerializable {

	private String queryString;
	private String searchType;
	private Double lowerTh;
	private Double upperTh;
	private int winWidth;
	
	public QueryInfo() {
		
	}

	public QueryInfo(String queryString, String searchType, String lowerTh, String upperTh, int winWidth) throws Exception {
		super();
		this.queryString = queryString;
		this.searchType = searchType;
		try {
			if(lowerTh.equals("")){
				this.lowerTh = null;
			} else 	{
				this.lowerTh = Double.parseDouble(lowerTh);
			}
			if(upperTh.equals("")){
				this.upperTh = null;
			} else {
				this.upperTh = Double.parseDouble(upperTh);
			}
		} catch (Exception e){
			e.getMessage();
			e.printStackTrace();
			throw new Exception("The segment threshold has to be a real number e.g. 0.5!");
		}
		
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

	
	public Double getLowerTh() {
		return lowerTh;
	}

	public void setLowerTh(Double lowerTh) {
		this.lowerTh = lowerTh;
	}

	public Double getUpperTh() {
		return upperTh;
	}

	public void setUpperTh(Double upperTh) {
		this.upperTh = upperTh;
	}

	public int getWinWidth() {
		return winWidth;
	}

	public void setWinWidth(int winWidth) {
		this.winWidth = winWidth;
	}
	
}
