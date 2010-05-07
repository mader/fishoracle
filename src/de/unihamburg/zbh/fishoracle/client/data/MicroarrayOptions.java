package de.unihamburg.zbh.fishoracle.client.data;

import com.google.gwt.user.client.rpc.IsSerializable;

public class MicroarrayOptions implements IsSerializable{

	private String[] chipName;
	private String[] tissue;
	private String[] pStage;
	private String[] pGrade;
	private String[] metaStatus;
	
	public MicroarrayOptions() {
		
	}

	public String[] getChipName() {
		return chipName;
	}

	public void setChipName(String[] chipName) {
		this.chipName = chipName;
	}

	public String[] getTissue() {
		return tissue;
	}

	public void setTissue(String[] tissue) {
		this.tissue = tissue;
	}

	public String[] getPStage() {
		return pStage;
	}

	public void setPStage(String[] stage) {
		pStage = stage;
	}

	public String[] getPGrade() {
		return pGrade;
	}

	public void setPGrade(String[] grade) {
		pGrade = grade;
	}

	public String[] getMetaStatus() {
		return metaStatus;
	}

	public void setMetaStatus(String[] metaStatus) {
		this.metaStatus = metaStatus;
	}
	
}
