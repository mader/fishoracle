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

public class FoTissueSample implements IsSerializable {
	
	private int id;
	private FoOrgan organ;
	private FoProperty[] properties;
	
	public FoTissueSample() {
	}
	
	public FoTissueSample(int id, FoOrgan organ, FoProperty[] properties) {
		this.id = id;
		this.organ = organ;
		this.properties = properties;
	}
	
	public int[] getFoPropertyIds(){
		int[] ids = new int[properties.length];
		for(int i=0; i< properties.length; i++){
			ids[i] = properties[i].getId();
		}
		return ids;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public FoOrgan getOrgan() {
		return organ;
	}

	public void setFoOrgan(FoOrgan organ) {
		this.organ = organ;
	}

	public FoProperty[] getProperties() {
		return properties;
	}

	public void setProperties(FoProperty[] properties) {
		this.properties = properties;
	}	
}