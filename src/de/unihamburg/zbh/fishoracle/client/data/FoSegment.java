/*
  Copyright (c) 2011-2012 Malte Mader <mader@zbh.uni-hamburg.de>
  Copyright (c) 2011-2012 Center for Bioinformatics, University of Hamburg

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

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * @author Malte Mader
 *
 */
public class FoSegment implements IsSerializable {

	private int id;
	private FoLocation location;
	private double mean;
	private int numberOfMarkers;
	private int status;
	private double statusScore;
	private String type;
	private int studyId;
	private String studyName;
	
	public FoSegment() {
	}

	public FoSegment(int id,
						FoLocation loc,
						String type) {
		this.id = id;
		this.location = loc;
		this.type = type;
		
		this.mean = -23;
		this.numberOfMarkers = -1;
		this.status = -1;
		this.statusScore = -1.0;
		this.studyId = 0;
		this.studyName = "";
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public FoLocation getLocation() {
		return location;
	}

	public void setLocation(FoLocation location) {
		this.location = location;
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
	
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public double getStatusScore() {
		return statusScore;
	}

	public void setStatusScore(double statusScore) {
		this.statusScore = statusScore;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getStudyId() {
		return studyId;
	}

	public void setStudyId(int studyId) {
		this.studyId = studyId;
	}

	public String getStudyName() {
		return studyName;
	}

	public void setStudyName(String studyName) {
		this.studyName = studyName;
	}
}