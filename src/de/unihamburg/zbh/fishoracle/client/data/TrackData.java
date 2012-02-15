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
}
