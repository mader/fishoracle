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

public class TrackData  implements IsSerializable {

	private String trackName;
	private int trackNumber;
	private int[] ProjectIds;
	private int[] tissueIds;
	private int[] experimentIds;
	
	private String lowerTh;
	private String upperTh;
	
	private FoCnSegment[] trackSegments;
	
	public TrackData() {
	}

	public TrackData(String trackName, int trackNumber) {
		this.trackName = trackName;
		this.trackNumber = trackNumber;
	}

	public String getTrackName() {
		return trackName;
	}

	public void setTrackName(String trackName) {
		this.trackName = trackName;
	}

	public int getTrackNumber() {
		return trackNumber;
	}

	public void setTrackNumber(int trackNumber) {
		this.trackNumber = trackNumber;
	}

	public int[] getProjectIds() {
		return ProjectIds;
	}

	public void setProjectIds(int[] projectIds) {
		ProjectIds = projectIds;
	}

	public int[] getTissueIds() {
		return tissueIds;
	}

	public void setTissueIds(int[] tissueIds) {
		this.tissueIds = tissueIds;
	}

	public int[] getExperimentIds() {
		return experimentIds;
	}

	public void setExperimentIds(int[] experimentIds) {
		this.experimentIds = experimentIds;
	}

	public String getLowerTh() {
		return lowerTh;
	}

	public Double getLowerThAsDouble() {
		
		Double r;
		
		if(lowerTh == null){
			r = null;
		} else {
			r = Double.parseDouble(lowerTh);
		}
		return r;
	}

	
	public void setLowerTh(String lowerTh) {
		this.lowerTh = lowerTh;
	}

	public String getUpperTh() {
		return upperTh;
	}
	
	public Double getUpperThasDouble() {
		
		Double r;
		
		if(upperTh == null){
			r = null;
		} else {
			r = Double.parseDouble(upperTh);
		}
		return r;
	}

	public void setUpperTh(String upperTh) {
		this.upperTh = upperTh;
	}

	public FoCnSegment[] getTrackSegments() {
		return trackSegments;
	}

	public void setTrackSegments(FoCnSegment[] trackSegments) {
		this.trackSegments = trackSegments;
	}
}
