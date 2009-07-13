package de.unihamburg.zbh.fishoracle.client.data;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.IsSerializable;

public class GWTImageInfo implements IsSerializable{

	private String imgUrl;
	private int height;
	private int width;
	private String chromosome;
	private int start;
	private int end;
	private String query;
	private String searchType;
	private ArrayList<RecMapInfo> recmapinfo;
	
	
	public GWTImageInfo() {
		
	}

	public GWTImageInfo(String imgUrl, int height, int width, String chromosome, int start, int end,
			String query, String searchType) {
		super();
		this.imgUrl = imgUrl;
		this.height = height;
		this.width = width;
		this.chromosome = chromosome;
		this.start = start;
		this.end = end;
		this.query = query;
		this.searchType = searchType;
	}

	public GWTImageInfo(String imgUrl, int height, int width,
			ArrayList<RecMapInfo> recmapinfo) {
		super();
		this.imgUrl = imgUrl;
		this.height = height;
		this.width = width;
		this.recmapinfo = recmapinfo;
	}

	public String getImgUrl() {
		return imgUrl;
	}


	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}


	public int getHeight() {
		return height;
	}


	public void setHeight(int height) {
		this.height = height;
	}


	public int getWidth() {
		return width;
	}


	public void setWidth(int width) {
		this.width = width;
	}


	public String getChromosome() {
		return chromosome;
	}

	public void setChromosome(String chromosome) {
		this.chromosome = chromosome;
	}

	public int getStart() {
		return start;
	}


	public void setStart(int start) {
		this.start = start;
	}


	public int getEnd() {
		return end;
	}


	public void setEnd(int end) {
		this.end = end;
	}


	public String getQuery() {
		return query;
	}


	public void setQuery(String query) {
		this.query = query;
	}


	public String getSearchType() {
		return searchType;
	}


	public void setSearchType(String searchType) {
		this.searchType = searchType;
	}

	public ArrayList<RecMapInfo> getRecmapinfo() {
		return recmapinfo;
	}

	public void setRecmapinfo(ArrayList<RecMapInfo> recmapinfo) {
		this.recmapinfo = recmapinfo;
	}

	
}
