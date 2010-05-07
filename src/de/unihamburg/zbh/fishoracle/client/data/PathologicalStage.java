package de.unihamburg.zbh.fishoracle.client.data;

import com.google.gwt.user.client.rpc.IsSerializable;

public class PathologicalStage implements IsSerializable{

	private int id;
	private String label;
	private String activity;
	
	public PathologicalStage() {
		
	}

	public PathologicalStage(int id, String label, String activity) {
		super();
		this.id = id;
		this.label = label;
		this.activity = activity;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getActivity() {
		return activity;
	}

	public void setActivity(String activity) {
		this.activity = activity;
	}

}
