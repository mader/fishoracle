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

public class TrackData implements IsSerializable {

	private String dataType;
	private String dataSubType;
	private String trackName;
	private int trackNumber;
	private int[] ProjectIds;
	private int[] tissueIds;
	private int[] experimentIds;
	
	private double qualityScore;
	private String[] somatic;
	private String[] confidence;
	private String[] snpTool;
	
	private String lowerTh;
	private String upperTh;
	
	private int[] cnvStati;
	
	private FoSegment[] trackSegments;
	
	public TrackData() {
	}

	public TrackData(String trackName, int trackNumber) {
		this.trackName = trackName;
		this.trackNumber = trackNumber;
	}
	
	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	
	public String getDataSubType() {
		return dataSubType;
	}

	public void setDataSubType(String dataSubType) {
		this.dataSubType = dataSubType;
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
	
	public double getQualityScore() {
		return qualityScore;
	}

	public void setQualityScore(double qualityScore) {
		this.qualityScore = qualityScore;
	}

	public String[] getSomatic() {
		return somatic;
	}

	public void setSomatic(String[] somatic) {
		this.somatic = somatic;
	}

	public String[] getConfidence() {
		return confidence;
	}

	public void setConfidence(String[] confidence) {
		this.confidence = confidence;
	}

	public String[] getSnpTool() {
		return snpTool;
	}

	public void setSnpTool(String[] snpTool) {
		this.snpTool = snpTool;
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
	
	public int[] getCnvStati() {
		return cnvStati;
	}

	public void setCnvStati(int[] cnvStati) {
		this.cnvStati = cnvStati;
	}

	public FoSegment[] getTrackSegments() {
		return trackSegments;
	}

	public void setTrackSegments(FoSegment[] trackSegments) {
		this.trackSegments = trackSegments;
	}
}
