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
