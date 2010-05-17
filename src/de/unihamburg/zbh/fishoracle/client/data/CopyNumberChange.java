package de.unihamburg.zbh.fishoracle.client.data;

import com.google.gwt.user.client.rpc.IsSerializable;

public class CopyNumberChange implements IsSerializable{

	private String cncStableId;
	private String chromosome;
	private int start;
	private int end;
	
	private String caseName;
	private String tumorType;
	private int continuous;
	private int cnclevel;
	
	public CopyNumberChange() {
		
	}
	
	public CopyNumberChange(String cncStableId, String chromosome, int start,
			int end) {
		super();
		this.cncStableId = cncStableId;
		this.chromosome = chromosome;
		this.start = start;
		this.end = end;
	}

	public CopyNumberChange(String cncStableId, String chromosome, int start,
			int end, String caseName, String tumorType, int continuous,
			int cnclevel, boolean isAmplicon) {
		super();
		this.cncStableId = cncStableId;
		this.chromosome = chromosome;
		this.start = start;
		this.end = end;
		this.caseName = caseName;
		this.tumorType = tumorType;
		this.continuous = continuous;
		this.cnclevel = cnclevel;
	}

	public String getCncStableId() {
		return cncStableId;
	}

	public void setCncStableId(String cncStableId) {
		this.cncStableId = cncStableId;
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

	public int getCnclevel() {
		return cnclevel;
	}

	public void setCnclevel(int cnclevel) {
		this.cnclevel = cnclevel;
	}
}
