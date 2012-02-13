/*
  Copyright (c) 2009-2011 Malte Mader <mader@zbh.uni-hamburg.de>
  Copyright (c) 2009-2011 Center for Bioinformatics, University of Hamburg

  Permission to use, copy, modify, and distribute this software for any
  purpose with or without fee is hereby granted, provided that the above
  copyright notice and this permission notice appear in all copies.

  THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
  WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
  MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
  ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
  WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
  ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
  OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
*/

package de.unihamburg.zbh.fishoracle.client.data;

import com.google.gwt.user.client.rpc.IsSerializable;

public class QueryInfo  implements IsSerializable, Cloneable {

	private String queryString;
	private String searchType;
	private Double globalLowerTh;
	private Double globalUpperTh;
	private String imageType;
	private TrackData[] td;
	private String[] organFilter;
	private int winWidth;
	
	public QueryInfo() {
		
	}

	public QueryInfo(String queryString, String searchType, String lowerTh, String upperTh, String imageType, String[] organFilter, int winWidth) throws Exception {
		super();
		this.queryString = queryString;
		this.searchType = searchType;
		try {
			if(lowerTh.equals("")){
				this.globalLowerTh = null;
			} else 	{
				this.globalLowerTh = Double.parseDouble(lowerTh);
			}
			if(upperTh.equals("")){
				this.globalUpperTh = null;
			} else {
				this.globalUpperTh = Double.parseDouble(upperTh);
			}
		} catch (Exception e){
			e.getMessage();
			e.printStackTrace();
			throw new Exception("The segment threshold has to be a real number e.g. 0.5!");
		}
		
		this.imageType = imageType;
		this.organFilter = organFilter;
		this.winWidth = winWidth;
	}

	public QueryInfo clone(){
		
		QueryInfo query = new QueryInfo();
		query.setImageType(this.imageType);
		try {
			if(globalLowerTh != null){
				query.setLowerTh(this.globalLowerTh.toString());
			} else {
				query.setLowerTh("");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		query.setQueryString(this.queryString);
		query.setSearchType(this.searchType);
		try {
			if((globalUpperTh != null)){
				query.setUpperTh(this.globalUpperTh.toString());
			} else {
				query.setUpperTh("");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		query.setOrganFilter(this.organFilter);
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
		return globalLowerTh;
	}
	
	public String getLowerThAsString() {
		if(globalLowerTh != null){
			return Double.toString(globalLowerTh);
		} else {
			return "";
		}
	}
	
	public void setLowerTh(String lowerTh) throws Exception {
		
		try {
			if(lowerTh.equals("")){
				this.globalLowerTh = null;
			} else 	{
				this.globalLowerTh = Double.parseDouble(lowerTh);
			}
		} catch (Exception e){
			e.getMessage();
			e.printStackTrace();
			throw new Exception("The segment threshold has to be a real number e.g. 0.5!");
		}
		
	}

	public Double getUpperTh() {
		return globalUpperTh;
	}
	
	public String getUpperThAsString() {
		if(globalUpperTh != null){
			return Double.toString(globalUpperTh);
		} else {
			return "";
		}
	}
	
	public void setUpperTh(String upperTh) throws Exception {		
		try {
			if(upperTh.equals("")){
				this.globalUpperTh = null;
			} else {
				this.globalUpperTh = Double.parseDouble(upperTh);
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

	public String[] getOrganFilter() {
		return organFilter;
	}

	public void setOrganFilter(String[] organFilter) {
		this.organFilter = organFilter;
	}

	public int getWinWidth() {
		return winWidth;
	}

	public void setWinWidth(int winWidth) {
		this.winWidth = winWidth;
	}
	
}
