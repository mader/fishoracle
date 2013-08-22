/*
  Copyright (c) 2012-2013 Malte Mader <mader@zbh.uni-hamburg.de>
  Copyright (c) 2012-2013 Center for Bioinformatics, University of Hamburg

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

import java.util.HashMap;

import com.google.gwt.user.client.rpc.IsSerializable;

public class FoConfig implements IsSerializable {

	private HashMap<String, String[]> strArrays;

	public FoConfig() {
		strArrays = new HashMap<String, String[]>();
	}
	
	public void addStrArray(String key, String[] value){
		strArrays.put(key, value);
	}
	
	public String[] getStrArray(String key){
		return strArrays.get(key);
	}

	public HashMap<String, String[]> getStrArrays() {
		return strArrays;
	}
	
	public void setStrArrays(HashMap<String, String[]> strArrays) {
		this.strArrays = strArrays;
	}
	
	public HashMap<String, String[]>  getStrArray(){
		return strArrays;
	}
}
