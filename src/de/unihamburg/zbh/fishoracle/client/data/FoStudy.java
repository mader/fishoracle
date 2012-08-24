/*
  Copyright (c) 2009-2012 Malte Mader <mader@zbh.uni-hamburg.de>
  Copyright (c) 2009-2012 Center for Bioinformatics, University of Hamburg

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

import java.util.Date;

import com.google.gwt.user.client.rpc.IsSerializable;

import de.unihamburg.zbh.fishoracle_db_api.data.SNPMutation;

public class FoStudy implements IsSerializable {
	
	private int id;
	private FoCnSegment segments[];
	private FoSNPMutation mutations[];
	private FoPlatform platform;
	private FoTissueSample tissue;
	private Date date;
	private String name;
	private String type;
	private String assembly;
	private String Description;
	private int platformId;
	private int organId;
	private int[] propertyIds;
	private int userId;
	private String files[];
	
	public FoStudy() {
	}
	
	public FoStudy(int id,
					Date date,
					String name,
					String type,
					String assembly,
					String description,
					int userId) {
		this.id = id;
		this.date = date;
		this.name = name;
		this.type = type;
		this.assembly = assembly;
		Description = description;
		this.userId = userId;
	}

	public FoStudy(FoCnSegment[] segments,
					String name,
					String type,
					String assembly,
					String description,
					int platformId,
					int organId,
					int[] propertyIds,
					int userId) {
		this.segments = segments;
		this.name = name;
		this.type = type;
		this.assembly = assembly;
		Description = description;
		this.platformId = platformId;
		this.organId = organId;
		this.propertyIds = propertyIds;
		this.userId = userId;
	}

	public FoStudy(int id,
					FoCnSegment[] segments,
					FoPlatform platform,
					FoTissueSample tissue,
					Date date,
					String name,
					String description) {
		this.id = id;
		this.segments = segments;
		this.platform = platform;
		this.tissue = tissue;
		this.date = date;
		this.name = name;
		Description = description;
	}
	
	public FoSNPMutation[] getMutations() {
		return mutations;
	}

	public void setMutations(FoSNPMutation[] mutations) {
		this.mutations = mutations;
	}

	public FoCnSegment[] getSegments() {
		return segments;
	}

	public void setSegments(FoCnSegment[] segments) {
		this.segments = segments;
	}

	public FoPlatform getPlatform() {
		return platform;
	}

	public String[] getFiles() {
		return files;
	}

	public void setFiles(String[] files) {
		this.files = files;
	}
	
	public void setPlatform(FoPlatform platform) {
		this.platform = platform;
	}

	public FoTissueSample getTissue() {
		return tissue;
	}

	public void setTissue(FoTissueSample tissue) {
		this.tissue = tissue;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getAssembly() {
		return assembly;
	}

	public void setAssembly(String assembly) {
		this.assembly = assembly;
	}
	
	public String getDescription() {
		return Description;
	}

	public void setDescription(String description) {
		Description = description;
	}

	public int getPlatformId() {
		return platformId;
	}

	public void setPlatformId(int platformId) {
		this.platformId = platformId;
	}

	public int getOrganId() {
		return organId;
	}

	public void setOrganId(int organId) {
		this.organId = organId;
	}

	public int[] getPropertyIds() {
		return propertyIds;
	}

	public void setPropertyIds(int[] propertyIds) {
		this.propertyIds = propertyIds;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}