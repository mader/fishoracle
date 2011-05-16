package de.unihamburg.zbh.fishoracle.client.data;

import com.google.gwt.user.client.rpc.IsSerializable;

public class FoGroup implements IsSerializable {

	private int id;
	private String name;
	private boolean isactive;
	private FoUser[] users;
	
	public FoGroup() {
	}

	public FoGroup(int id, String name, boolean isactive) {
		this.id = id;
		this.name = name;
		this.isactive = isactive;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isIsactive() {
		return isactive;
	}

	public void setIsactive(boolean isactive) {
		this.isactive = isactive;
	}
	
	public int getIsactiveAsInt() {
		return (isactive) ? 1 : 0;
	}

	public FoUser[] getUsers() {
		return users;
	}

	public void setUsers(FoUser[] users) {
		this.users = users;
	}
	
}
