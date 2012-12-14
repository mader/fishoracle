package de.unihamburg.zbh.fishoracle.client.data;

import com.google.gwt.user.client.rpc.IsSerializable;

public class FoTrackData extends FoConfig implements IsSerializable {

	private int id;
	private int configId;
	private String trackName;
	private int trackNumber;
	
	public FoTrackData(){
		super();
	}

	public FoTrackData(String trackName, int trackNumber) {
		super();
		this.trackName = trackName;
		this.trackNumber = trackNumber;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public int getConfigId() {
		return configId;
	}

	public void setConfigId(int configId) {
		this.configId = configId;
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
}
