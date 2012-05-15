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

public class EnsemblGene implements IsSerializable{

	
	private String stableId = null;
	private String bioType = null;
	private String description = null;
	private String geneName = null;
	private String chr = null;
	private int start;
	private int end;
	private int length;
	private String strand = null;
	
	public EnsemblGene() {
		
	}

	public EnsemblGene(String genName, String chr, int start, int end, String strand) {
		this.geneName = genName;
		this.chr = chr;
		this.start = start;
		this.end = end;
		if(strand.equals("-1") || strand.equals("1") || strand.equals("0")){
			
			this.strand = this.convertEnsembl2GFF3Strand(strand);
			
		} else {
			
			this.strand = strand;
		}
	}

	public String getGenName() {
		return geneName;
	}


	public void setGenName(String genName) {
		this.geneName = genName;
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
		if(strand.equals("-1") || strand.equals("1") || strand.equals("0")){
			
			this.strand = this.convertEnsembl2GFF3Strand(strand);
			
		} else {
			
			this.strand = strand;
		}
		
	}
	
	
	public String getAccessionID() {
		return stableId;
	}

	public void setAccessionID(String accessionID) {
		this.stableId = accessionID;
	}

	public String getBioType() {
		return bioType;
	}

	public void setBioType(String bioType) {
		this.bioType = bioType;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public String convertEnsembl2GFF3Strand(String strand){
		String ret = null;
		if(strand.equals("1")){
			ret = "+";
		} else if(strand.equals("-1")) {
			ret = "-";
		} else if (strand.equals("0")){
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
