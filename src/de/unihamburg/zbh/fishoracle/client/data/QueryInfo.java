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
	private String globalLowerTh;
	private String globalUpperTh;
	private String imageType;
	private TrackData[] tracks;
	private int winWidth;
	private boolean sorted;
	private boolean globalTh;
	
	public QueryInfo() {
		
	}

	public QueryInfo(String queryString,
						String searchType,
						String globalLowerTh,
						String globalUpperTh,
						boolean sorted,
						boolean globalTh,
						String imageType,
						TrackData[] tracks,
						int winWidth) throws Exception {
		super();
		this.queryString = queryString;
		this.searchType = searchType;
		if(globalLowerTh != null){
			this.globalLowerTh = globalLowerTh;
		} else 	{
			this.globalLowerTh = null;
		}
		
		if(globalUpperTh != null){
			this.globalUpperTh = globalUpperTh;
		} else {
			this.globalUpperTh = null;
		}
		this.sorted = sorted;
		this.globalTh = globalTh; 
		this.imageType = imageType;
		this.tracks = tracks;
		this.winWidth = winWidth;
	}

	public QueryInfo clone(){
		
		QueryInfo query = new QueryInfo();
		query.setImageType(this.imageType);
		query.setGlobalLowerTh(this.globalLowerTh);
		query.setQueryString(this.queryString);
		query.setSearchType(this.searchType);
		query.setGlobalUpperTh(this.globalUpperTh);
		query.setTracks(this.tracks);
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
	
	public String getGlobalLowerTh() {
		return globalLowerTh;
	}

	public Double getGlobalLowerThAsDouble() {
		
		Double r;
		
		if(globalLowerTh == null){
			r = null;
		} else {
			r = Double.parseDouble(globalLowerTh);
		}
		return r;
	}
	
	public void setGlobalLowerTh(String globalLowerTh) {
		this.globalLowerTh = globalLowerTh;
	}

	public String getGlobalUpperTh() {
		return globalUpperTh;
	}

	public Double getGlobalUpperThAsDouble() {
		Double r;
		
		if(globalUpperTh == null){
			r = null;
		} else {
			r = Double.parseDouble(globalUpperTh);
		}
		return r;
	}
	
	public void setGlobalUpperTh(String globalUpperTh) {
		this.globalUpperTh = globalUpperTh;
	}

	public String getImageType() {
		return imageType;
	}

	public void setImageType(String imageType) {
		this.imageType = imageType;
	}

	public TrackData[] getTracks() {
		return tracks;
	}

	public void setTracks(TrackData[] tracks) {
		this.tracks = tracks;
	}

	public int getWinWidth() {
		return winWidth;
	}

	public void setWinWidth(int winWidth) {
		this.winWidth = winWidth;
	}
	
	public boolean isSorted() {
		return sorted;
	}

	public void setSorted(boolean sorted) {
		this.sorted = sorted;
	}

	public boolean isGlobalTh() {
		return globalTh;
	}

	public void setGlobalTh(boolean globalTh) {
		this.globalTh = globalTh;
	}
}
