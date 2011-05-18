package de.unihamburg.zbh.fishoracle.client.data;

import com.google.gwt.user.client.rpc.IsSerializable;

public class FoProjectAccess implements IsSerializable {

	private int id;
	private FoGroup group;
	private int groupId;
	private String access;
	
	public FoProjectAccess() {
	}
	
	public FoProjectAccess(int id, int groupId, String access) {
		this.id = id;
		this.groupId = groupId;
		this.access = access;
	}

	public FoProjectAccess(int id, FoGroup group, String access) {
		this.id = id;
		this.group = group;
		this.access = access;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public FoGroup getFoGroup() {
		return group;
	}

	public void setFoGroup(FoGroup group) {
		this.group = group;
	}

	public String getAccess() {
		return access;
	}

	public void setAccess(String access) {
		this.access = access;
	}

	public int getGroupId() {
		return groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}
}
