package de.unihamburg.zbh.fishoracle.client.data;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Organ implements IsSerializable{

	private int id;
	private String label;
	private String activty;
	
	public Organ() {
		
	}
	
	public Organ(int id, String label, String activty) {
		super();
		this.id = id;
		this.label = label;
		this.activty = activty;
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

	public String getActivty() {
		return activty;
	}

	public void setActivty(String activty) {
		this.activty = activty;
	}
}
