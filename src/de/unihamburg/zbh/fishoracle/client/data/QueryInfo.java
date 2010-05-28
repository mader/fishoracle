package de.unihamburg.zbh.fishoracle.client.data;

import com.google.gwt.user.client.rpc.IsSerializable;

public class QueryInfo  implements IsSerializable, Cloneable {

	private String queryString;
	private String searchType;
	private Double lowerTh;
	private Double upperTh;
	private String imageType;
	private int winWidth;
	
	public QueryInfo() {
		
	}

	public QueryInfo(String queryString, String searchType, String lowerTh, String upperTh, String imageType, int winWidth) throws Exception {
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
		
		this.imageType = imageType;
		
		this.winWidth = winWidth;
	}

	public QueryInfo clone(){
		
		QueryInfo query = new QueryInfo();
		query.setImageType(this.imageType);
		try {
			query.setLowerTh(this.lowerTh.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		query.setQueryString(this.queryString);
		query.setSearchType(this.searchType);
		try {
			query.setUpperTh(this.upperTh.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		query.setWinWidth(this.winWidth);
		
		return query;
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
	
	public String getLowerThAsString() {
		if(lowerTh != null){
			return Double.toString(lowerTh);
		} else {
			return "";
		}
	}
	
	public void setLowerTh(String lowerTh) throws Exception {
		
		try {
			if(lowerTh.equals("")){
				this.lowerTh = null;
			} else 	{
				this.lowerTh = Double.parseDouble(lowerTh);
			}
		} catch (Exception e){
			e.getMessage();
			e.printStackTrace();
			throw new Exception("The segment threshold has to be a real number e.g. 0.5!");
		}
		
	}

	public Double getUpperTh() {
		return upperTh;
	}
	
	public String getUpperThAsString() {
		if(upperTh != null){
			return Double.toString(upperTh);
		} else {
			return "";
		}
	}
	
	public void setUpperTh(String upperTh) throws Exception {		
		try {
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
	}
	
	public String getImageType() {
		return imageType;
	}

	public void setImageType(String imageType) {
		this.imageType = imageType;
	}

	public int getWinWidth() {
		return winWidth;
	}

	public void setWinWidth(int winWidth) {
		this.winWidth = winWidth;
	}
	
}
