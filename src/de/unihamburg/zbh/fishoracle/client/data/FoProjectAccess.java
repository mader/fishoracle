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

public class FoProjectAccess implements IsSerializable {

	private int id;
	private FoGroup group;
	private int groupId;
	private int projectId;
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

	public FoProjectAccess(int id, int projectId, int groupId, String access) {
		this.id = id;
		this.projectId = projectId;
		this.groupId = groupId;
		this.access = access;
	}
	
	public FoProjectAccess(int id, int projectId, FoGroup group, String access){
		this.id = id;
		this.projectId = projectId;
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

	public int getFoProjectId() {
		return projectId;
	}

	public void setFoProjectId(int projectId) {
		this.projectId = projectId;
	}	
}