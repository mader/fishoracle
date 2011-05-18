package de.unihamburg.zbh.fishoracle.client.data;

import java.util.Date;

import com.google.gwt.user.client.rpc.IsSerializable;

public class FoMicroarraystudy implements IsSerializable {
	private int id;
	private FoCnSegment segments[];
	private FoChip chip;
	private FoTissueSample tissue;
	private Date date;
	private String name;
	private String Description;
	private int chipId;
	private int organ_id;
	private int[] propertyIds;
	private int userId;
	
	public FoMicroarraystudy() {
	}
	
	public FoMicroarraystudy(int id, Date date, String name,
			String description, int userId) {
		super();
		this.id = id;
		this.date = date;
		this.name = name;
		Description = description;
		this.userId = userId;
	}

	public FoMicroarraystudy(FoCnSegment[] segments, String name,
			String description, int chipId, int organId, int[] propertyIds,
			int userId) {
		super();
		this.segments = segments;
		this.name = name;
		Description = description;
		this.chipId = chipId;
		organ_id = organId;
		this.propertyIds = propertyIds;
		this.userId = userId;
	}

	public FoMicroarraystudy(int id, FoCnSegment[] segments, FoChip chip,
			FoTissueSample tissue, Date date, String name,
			String description) {
		super();
		this.id = id;
		this.segments = segments;
		this.chip = chip;
		this.tissue = tissue;
		this.date = date;
		this.name = name;
		Description = description;
	}

	public FoCnSegment[] getSegments() {
		return segments;
	}

	public void setSegments(FoCnSegment[] segments) {
		this.segments = segments;
	}

	public FoChip getChip() {
		return chip;
	}

	public void setChip(FoChip chip) {
		this.chip = chip;
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

	public String getDescription() {
		return Description;
	}

	public void setDescription(String description) {
		Description = description;
	}

	public int getChipId() {
		return chipId;
	}

	public void setChipId(int chipId) {
		this.chipId = chipId;
	}

	public int getOrgan_id() {
		return organ_id;
	}

	public void setOrgan_id(int organId) {
		organ_id = organId;
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
