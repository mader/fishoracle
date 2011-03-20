package de.unihamburg.zbh.fishoracle.client.data;

import com.google.gwt.user.client.rpc.IsSerializable;

public class DBConfigData implements IsSerializable {

	//ensembl connection parameters
	private String ehost = null;
	private int eport;
	private String edb = null;
	private String euser = null;
	private String epw = null;
	
	//fish oracle connection parameters
	private String fhost = null;
	private String fdb = null;
	private String fuser = null;
	private String fpw = null;
	
	public DBConfigData() {
	}

	public DBConfigData(String ehost, int eport, String edb, String euser,
			String epw, String fhost, String fdb, String fuser, String fpw) {
		super();
		this.ehost = ehost;
		this.eport = eport;
		this.edb = edb;
		this.euser = euser;
		this.epw = epw;
		this.fhost = fhost;
		this.fdb = fdb;
		this.fuser = fuser;
		this.fpw = fpw;
	}

	public String getEhost() {
		return ehost;
	}

	public void setEhost(String ehost) {
		this.ehost = ehost;
	}

	public int getEport() {
		return eport;
	}

	public void setEport(int eport) {
		this.eport = eport;
	}

	public String getEdb() {
		return edb;
	}

	public void setEdb(String edb) {
		this.edb = edb;
	}

	public String getEuser() {
		return euser;
	}

	public void setEuser(String euser) {
		this.euser = euser;
	}

	public String getEpw() {
		return epw;
	}

	public void setEpw(String epw) {
		this.epw = epw;
	}

	public String getFhost() {
		return fhost;
	}

	public void setFhost(String fhost) {
		this.fhost = fhost;
	}

	public String getFdb() {
		return fdb;
	}

	public void setFdb(String fdb) {
		this.fdb = fdb;
	}

	public String getFuser() {
		return fuser;
	}

	public void setFuser(String fuser) {
		this.fuser = fuser;
	}

	public String getFpw() {
		return fpw;
	}

	public void setFpw(String fpw) {
		this.fpw = fpw;
	}

}
