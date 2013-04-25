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

import com.google.gwt.user.client.rpc.IsSerializable;

public class RecMapInfo implements IsSerializable,Cloneable {

	private double northwestX;
	private double northwestY;
	private double southeastX;
	private double southeastY;
	private String type;
	private String elementName;
	private int trackNumber;
	
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

	public int getTrackNumber() {
		return trackNumber;
	}

	public void setTrackNumber(int trackNumber) {
		this.trackNumber = trackNumber;
	}
}