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

public class MicroarrayOptions implements IsSerializable {

	private FoPlatform[] platforms;
	private FoOrgan[] organs;
	private FoProperty[] properties;
	private String[] propertyTypes;
	private FoProject[] projects;
	
	public MicroarrayOptions() {
	}

	public FoPlatform[] getChips() {
		return platforms;
	}

	public void setPlatforms(FoPlatform[] chips) {
		this.platforms = chips;
	}

	public FoOrgan[] getOrgans() {
		return organs;
	}

	public void setOrgans(FoOrgan[] organs) {
		this.organs = organs;
	}

	public FoProperty[] getProperties() {
		return properties;
	}

	public void setProperties(FoProperty[] properties) {
		this.properties = properties;
	}

	public String[] getPropertyTypes() {
		return propertyTypes;
	}

	public void setPropertyTypes(String[] propertyTypes) {
		this.propertyTypes = propertyTypes;
	}

	public FoProject[] getProjects() {
		return projects;
	}

	public void setProjects(FoProject[] projects) {
		this.projects = projects;
	}	
}