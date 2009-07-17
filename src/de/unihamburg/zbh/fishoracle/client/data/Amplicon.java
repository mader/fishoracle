package de.unihamburg.zbh.fishoracle.client.data;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Amplicon implements IsSerializable{

	private double ampliconStableId;
	private String chromosome;
	private int start;
	private int end;
	
	private String caseName;
	private String tumorType;
	private int continuous;
	private int amplevel;
	
	public Amplicon() {
		
	}

	public Amplicon(double ampliconStableId, String newChr, int start, int end) {
	
		this.ampliconStableId = ampliconStableId;
		this.chromosome = newChr;
		this.start = start;
		this.end = end;
		
	}
	
	public Amplicon(double ampliconStableId, String chromosome, int start,
			int end, String caseName, String tumorType, int continuous,
			int amplevel) {
		super();
		this.ampliconStableId = ampliconStableId;
		this.chromosome = chromosome;
		this.start = start;
		this.end = end;
		this.caseName = caseName;
		this.tumorType = tumorType;
		this.continuous = continuous;
		this.amplevel = amplevel;
	}



	public double getAmpliconStableId() {
		return ampliconStableId;
	}


	public void setAmpliconStableId(double ampliconStableId) {
		this.ampliconStableId = ampliconStableId;
	}


	public String getChromosome() {
		return chromosome;
	}


	public void setChromosome(String chromosome) {
		this.chromosome = chromosome;
	}


	public int getStart() {
		return start;
	}


	public void setStart(int start) {
		this.start = start;
	}


	public int getEnd() {
		return end;
	}


	public void setEnd(int end) {
		this.end = end;
	}

	public String getCaseName() {
		return caseName;
	}

	public void setCaseName(String caseName) {
		this.caseName = caseName;
	}

	public String getTumorType() {
		return tumorType;
	}

	public void setTumorType(String tumorType) {
		this.tumorType = tumorType;
	}

	public int getContinuous() {
		return continuous;
	}

	public void setContinuous(int continuous) {
		this.continuous = continuous;
	}

	public int getAmplevel() {
		return amplevel;
	}

	public void setAmplevel(int amplevel) {
		this.amplevel = amplevel;
	}
	
}
