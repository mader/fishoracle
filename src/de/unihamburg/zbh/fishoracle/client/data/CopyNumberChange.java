/*
  Copyright (c) 2009-2011 Malte Mader <mader@zbh.uni-hamburg.de>
  Copyright (c) 2009-2011 Center for Bioinformatics, University of Hamburg

  Permission to use, copy, modify, and distribute this software for any
  purpose with or without fee is hereby granted, provided that the above
  copyright notice and this permission notice appear in all copies.

  THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
  WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
  MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
  ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
  WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
  ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
  OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
*/

package de.unihamburg.zbh.fishoracle.client.data;

import java.util.Date;

import com.google.gwt.user.client.rpc.IsSerializable;

public class CopyNumberChange implements IsSerializable{

	private String cncStableId;
	private String chromosome;
	private int start;
	private int end;
	
	private String microarrayStudy;
	private String microarrayStudyDescr;
	private String organ;
	private double segmentMean;
	private int numberOfMarkers;
	
	private Date insertionDate;
	private String chip;
	private String pstage;
	private String pgrade;
	private String metaStatus;
	private String sampleId;
	
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

	public CopyNumberChange(String cncStableId,
			String chromosome,
			int start,
			int end,
			double segmentMean,
			int numberOfMarkers,
			String microarrayStudy,
			String microarrayStudyDescr,
			Date insertionDate,
			String chip,
			String organ,
			String pstage,
			String pgrade,
			String metaStatus,
			String sampleId) {
		this.cncStableId = cncStableId;
		this.chromosome = chromosome;
		this.start = start;
		this.end = end;
		this.microarrayStudy = microarrayStudy;
		this.microarrayStudyDescr = microarrayStudyDescr;
		this.organ = organ;
		this.segmentMean = segmentMean;
		this.numberOfMarkers = numberOfMarkers;
		this.insertionDate = insertionDate;
		this.chip = chip;
		this.pstage = pstage;
		this.pgrade = pgrade;
		this.metaStatus = metaStatus;
		this.sampleId = sampleId;
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

	public String getMicroarrayStudy() {
		return microarrayStudy;
	}

	public void setMicroarrayStudy(String microarrayStudy) {
		this.microarrayStudy = microarrayStudy;
	}
	
	public String getMicroarrayStudyDescr() {
		return microarrayStudyDescr;
	}

	public void setMicroarrayStudyDescr(String microarrayStudyDescr) {
		this.microarrayStudyDescr = microarrayStudyDescr;
	}

	public String getOrgan() {
		return organ;
	}

	public void setOrgan(String organ) {
		this.organ = organ;
	}

	public double getSegmentMean() {
		return segmentMean;
	}

	public void setSegmentMean(double segmentMean) {
		this.segmentMean = segmentMean;
	}

	public int getNumberOfMarkers() {
		return numberOfMarkers;
	}

	public void setNumberOfMarkers(int numberOfMarkers) {
		this.numberOfMarkers = numberOfMarkers;
	}

	public Date getInsertionDate() {
		return insertionDate;
	}

	public void setInsertionDate(Date insertionDate) {
		this.insertionDate = insertionDate;
	}

	public String getChip() {
		return chip;
	}

	public void setChip(String chip) {
		this.chip = chip;
	}

	public String getPstage() {
		return pstage;
	}

	public void setPstage(String pstage) {
		this.pstage = pstage;
	}

	public String getPgrade() {
		return pgrade;
	}

	public void setPgrade(String pgrade) {
		this.pgrade = pgrade;
	}

	public String getMetaStatus() {
		return metaStatus;
	}

	public void setMetaStatus(String metaStatus) {
		this.metaStatus = metaStatus;
	}

	public String getSampleId() {
		return sampleId;
	}

	public void setSampleId(String sampleId) {
		this.sampleId = sampleId;
	}
}
