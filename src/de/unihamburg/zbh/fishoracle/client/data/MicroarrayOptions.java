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

public class MicroarrayOptions implements IsSerializable{

	private String[] chipName;
	private String[] tissue;
	private String[] pStage;
	private String[] pGrade;
	private String[] metaStatus;
	
	public MicroarrayOptions() {
		
	}

	public String[] getChipName() {
		return chipName;
	}

	public void setChipName(String[] chipName) {
		this.chipName = chipName;
	}

	public String[] getTissue() {
		return tissue;
	}

	public void setTissue(String[] tissue) {
		this.tissue = tissue;
	}

	public String[] getPStage() {
		return pStage;
	}

	public void setPStage(String[] stage) {
		pStage = stage;
	}

	public String[] getPGrade() {
		return pGrade;
	}

	public void setPGrade(String[] grade) {
		pGrade = grade;
	}

	public String[] getMetaStatus() {
		return metaStatus;
	}

	public void setMetaStatus(String[] metaStatus) {
		this.metaStatus = metaStatus;
	}
	
}
