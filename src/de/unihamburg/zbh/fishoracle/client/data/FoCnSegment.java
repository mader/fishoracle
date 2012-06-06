package de.unihamburg.zbh.fishoracle.client.data;

import com.google.gwt.user.client.rpc.IsSerializable;

public class FoCnSegment implements IsSerializable {

	private int id;
	private String chromosome;
	private int start;
	private int end;
	private double mean;
	private int numberOfMarkers;
	private int microarraystudyId;
	private String microarraystudyName;
	
	public FoCnSegment() {
	}

	public FoCnSegment(int id, String chromosome, int start, int end,
			double mean, int numberOfMarkers) {
		this.id = id;
		this.chromosome = chromosome;
		this.start = start;
		this.end = end;
		this.mean = mean;
		this.numberOfMarkers = numberOfMarkers;
		this.microarraystudyId = 0;
	}
	
	public FoCnSegment(int id, String chromosome, int start,
			int end, double mean, int numberOfMarkers, int microarraystudyId) {
		this.id = id;
		this.chromosome = chromosome;
		this.start = start;
		this.end = end;
		this.mean = mean;
		this.numberOfMarkers = numberOfMarkers;
		this.microarraystudyId = microarraystudyId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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

	public double getMean() {
		return mean;
	}

	public void setMean(double mean) {
		this.mean = mean;
	}

	public int getNumberOfMarkers() {
		return numberOfMarkers;
	}

	public void setNumberOfMarkers(int numberOfMarkers) {
		this.numberOfMarkers = numberOfMarkers;
	}

	public int getMicroarraystudyId() {
		return microarraystudyId;
	}

	public void setMicroarraystudyId(int microarraystudyId) {
		this.microarraystudyId = microarraystudyId;
	}

	public String getMicroarraystudyName() {
		return microarraystudyName;
	}

	public void setMicroarraystudyName(String microarraystudyName) {
		this.microarraystudyName = microarraystudyName;
	}
}
