package de.unihamburg.zbh.fishoracle.client.data;

import com.google.gwt.user.client.rpc.IsSerializable;

public class FoTissueSample implements IsSerializable {
	
	private int id;
	private FoOrgan organ;
	private FoProperty[] properties;
	
	public FoTissueSample() {
	}
	
	public FoTissueSample(int id, FoOrgan organ, FoProperty[] properties) {
		this.id = id;
		this.organ = organ;
		this.properties = properties;
	}
	
	public int[] getFoPropertyIds(){
		int[] ids = new int[properties.length];
		for(int i=0; i< properties.length; i++){
			ids[i] = properties[i].getId();
		}
		return ids;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public FoOrgan getOrgan() {
		return organ;
	}

	public void setFoOrgan(FoOrgan organ) {
		this.organ = organ;
	}

	public FoProperty[] getFoProperties() {
		return properties;
	}

	public void setProperties(FoProperty[] properties) {
		this.properties = properties;
	}
	
}
