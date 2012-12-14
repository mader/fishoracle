package de.unihamburg.zbh.fishoracle.client.data;

import com.google.gwt.user.client.rpc.IsSerializable;

public class FoConfigData extends FoConfig implements IsSerializable {

	private int id;
	private int userId;
	private int ensemblDBId;
	private String name;
	private FoTrackData[] tracks;
	
	public FoConfigData() {
		super();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getEnsemblDBId() {
		return ensemblDBId;
	}

	public void setEnsemblDBId(int ensemblDBId) {
		this.ensemblDBId = ensemblDBId;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public FoTrackData[] getTracks() {
		return tracks;
	}

	public void setTracks(FoTrackData[] tracks) {
		this.tracks = tracks;
	}
	
}
