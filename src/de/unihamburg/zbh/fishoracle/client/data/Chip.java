package de.unihamburg.zbh.fishoracle.client.data;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Chip implements IsSerializable{

	private String chipName;
	private String chipType;
	private String cdfFileName;
	
	public Chip() {

	}

	public Chip(String chipName, String chipType, String cdfFileName) {
		super();
		this.chipName = chipName;
		this.chipType = chipType;
		this.cdfFileName = cdfFileName;
	}

	public String getChipName() {
		return chipName;
	}

	public void setChipName(String chipName) {
		this.chipName = chipName;
	}

	public String getChipType() {
		return chipType;
	}

	public void setChipType(String chipType) {
		this.chipType = chipType;
	}

	public String getCdfFileName() {
		return cdfFileName;
	}

	public void setCdfFileName(String cdfFileName) {
		this.cdfFileName = cdfFileName;
	}
}
