package de.unihamburg.zbh.fishoracle.server.data;

public class Gen {

	private String genName;
	private String chr = null;
	private int start;
	private int end;
	private String strand;
	
	
	public Gen(String genName, String chr, int start, int end, String strand) {
		this.genName = genName;
		this.chr = chr;
		this.start = start;
		this.end = end;
		if(strand.equals("-1") || strand.equals("1")){
			
			this.strand = this.convertEnsembl2GFF3Strand(strand);
			
		} else {
			
			this.strand = strand;
		}
	}


	public String getGenName() {
		return genName;
	}


	public void setGenName(String genName) {
		this.genName = genName;
	}

	
	public String getChr() {
		return chr;
	}


	public void setChr(String chr) {
		this.chr = chr;
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


	public String getStrand() {
		return strand;
	}


	public void setStrand(String strand) {
		this.strand = strand;
	}
	
	public String convertEnsembl2GFF3Strand(String strand2){
		String ret = null;
		if(strand2.equals("1")){
			ret = "+";
		} else if(strand2.equals("-1")) {
			ret = "-";
		} else if (strand2.equals("0")){
			ret = ".";
		}
		return ret;
	}

	public int convertGFF32EnsemblStrand(String strand){
		int ret = 0;
		if(strand.equals("+")){
			ret = 1;
		} else if(strand.equals("-")) {
			ret = -1;
		} else if(strand.equals(".")){
			ret = 0;
		}
		return ret;
	}
}
