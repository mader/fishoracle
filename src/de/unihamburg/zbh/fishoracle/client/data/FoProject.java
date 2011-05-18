package de.unihamburg.zbh.fishoracle.client.data;

import com.google.gwt.user.client.rpc.IsSerializable;

public class FoProject implements IsSerializable{

	private int id;
	private String name;
	private String description;
	private FoProjectAccess[] projectAccess;
	private FoMicroarraystudy[] mstudies;
	
	public FoProject() {
		super();
	}

	public FoProject(int id, String name, String description) {
		this.id = id;
		this.name = name;
		this.description = description;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public FoMicroarraystudy[] getMstudies() {
		return mstudies;
	}

	public void setMstudies(FoMicroarraystudy[] mstudies) {
		this.mstudies = mstudies;
	}

	public FoProjectAccess[] getProjectAccess() {
		return projectAccess;
	}

	public void setProjectAccess(FoProjectAccess[] projectAccess) {
		this.projectAccess = projectAccess;
	}
	
}
