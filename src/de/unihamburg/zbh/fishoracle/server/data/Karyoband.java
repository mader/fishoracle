package de.unihamburg.zbh.fishoracle.server.data;

/**
 * Stores karyoband information that is needed for the image generation.
 * 
 * */
public class Karyoband {

	private String chr;
	private String band;
	private int start;
	private int end;

	public Karyoband(String chr, String band, int start, int end) {
		super();
		this.chr = chr;
		this.band = band;
		this.start = start;
		this.end = end;
	}

	public String getChr() {
		return chr;
	}

	public void setChr(String chr) {
		this.chr = chr;
	}

	public String getBand() {
		return band;
	}

	public void setBand(String band) {
		this.band = band;
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

}
