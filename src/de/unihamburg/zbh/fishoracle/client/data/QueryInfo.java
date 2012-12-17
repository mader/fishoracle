/*
  Copyright (c) 2009-2012 Malte Mader <mader@zbh.uni-hamburg.de>
  Copyright (c) 2009-2012 Center for Bioinformatics, University of Hamburg

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
	private String imageType;
	private int winWidth;
	private FoConfigData config;
	
	public QueryInfo() {
		
	}

	public QueryInfo(String queryString,
						String searchType,
						String imageType,
						int winWidth,
						FoConfigData config) throws Exception {
		super();
		this.queryString = queryString;
		this.searchType = searchType;
		this.imageType = imageType;
		this.winWidth = winWidth;
		this.config = config;
	}

	public QueryInfo clone(){
		
		QueryInfo query = new QueryInfo();
		query.setQueryString(this.queryString);
		query.setSearchType(this.searchType);
		query.setImageType(this.imageType);
		query.setWinWidth(this.winWidth);
		query.setConfig(this.config);
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

	public FoConfigData getConfig() {
		return config;
	}

	public void setConfig(FoConfigData config) {
		this.config = config;
	}
}