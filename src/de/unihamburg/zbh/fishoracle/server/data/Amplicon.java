package de.unihamburg.zbh.fishoracle.server.data;

public class Amplicon {

	private double ampliconStableId;
	private int chromosome;
	private int start;
	private int end;
	
	
	public Amplicon(double ampliconStableId, int chromosome, int start, int end) {
	
		this.ampliconStableId = ampliconStableId;
		this.chromosome = chromosome;
		this.start = start;
		this.end = end;
		
	}


	public double getAmpliconStableId() {
		return ampliconStableId;
	}


	public void setAmpliconStableId(double ampliconStableId) {
		this.ampliconStableId = ampliconStableId;
	}


	public int getChromosome() {
		return chromosome;
	}


	public void setChromosome(int chromosome) {
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

	
	
}
