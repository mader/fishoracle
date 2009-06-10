package de.unihamburg.zbh.fishoracle.server.data;

public class Gen {

	private String genName;
	private int start;
	private int end;
	private int strand;
	
	
	public Gen(String genName, int start, int end, int strand) {
		this.genName = genName;
		this.start = start;
		this.end = end;
		this.strand = strand;
	}


	public String getGenName() {
		return genName;
	}


	public void setGenName(String genName) {
		this.genName = genName;
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


	public int getStrand() {
		return strand;
	}


	public void setStrand(int strand) {
		this.strand = strand;
	}
	
	

}
