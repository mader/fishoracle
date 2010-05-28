package de.unihamburg.zbh.fishoracle.client.data;

import com.google.gwt.user.client.rpc.IsSerializable;

public class RecMapInfo implements IsSerializable,Cloneable {

	private double northwestX;
	private double northwestY;
	private double southeastX;
	private double southeastY;
	private String type;
	private String elementName;
	
	public RecMapInfo() {
		
	}

	public RecMapInfo(double northwestX, double northwestY, double southeastX,
			double southeastY, String type, String elementName) {
		super();
		this.northwestX = northwestX;
		this.northwestY = northwestY;
		this.southeastX = southeastX;
		this.southeastY = southeastY;
		this.type = type;
		this.elementName = elementName;
	}

	public RecMapInfo clone(){
		
		RecMapInfo rmi = new RecMapInfo();
		
		rmi.setElementName(this.elementName);
		rmi.setNorthwestX(this.northwestX);
		rmi.setNorthwestY(this.northwestY);
		rmi.setSoutheastX(this.southeastX);
		rmi.setSoutheastY(this.southeastY);
		rmi.setType(this.type);
		
		return rmi;
	}
	
	public double getNorthwestX() {
		return northwestX;
	}

	public void setNorthwestX(double northwestX) {
		this.northwestX = northwestX;
	}

	public double getNorthwestY() {
		return northwestY;
	}

	public void setNorthwestY(double northwestY) {
		this.northwestY = northwestY;
	}

	public double getSoutheastX() {
		return southeastX;
	}

	public void setSoutheastX(double southeastX) {
		this.southeastX = southeastX;
	}

	public double getSoutheastY() {
		return southeastY;
	}

	public void setSoutheastY(double southeastY) {
		this.southeastY = southeastY;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getElementName() {
		return elementName;
	}

	public void setElementName(String elementName) {
		this.elementName = elementName;
	}

}
