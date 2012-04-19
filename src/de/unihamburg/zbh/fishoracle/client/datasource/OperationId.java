package de.unihamburg.zbh.fishoracle.client.datasource;

public class OperationId {
	
	public static final String PROJECT_FETCH_ALL = "getAllProjects";
	
	public static final String MSTUDY_FETCH_ALL = "getAllMicroarrayStudies";
	public static final String MSTUDY_FETCH_FOR_PROJECT = "getAllMicroarrayStudiesForProject";
	
	public static final String ORGAN_FETCH_ALL = "getAllOrgans";
	public static final String ORGAN_FETCH_ENABLED = "getAllEnabledOrgans";
	public static final String ORGAN_FETCH_TYPES = "getAllOrganTypes";
	
	public static final String PROPERTY_FETCH_ALL = "getAllProperties";
	public static final String PROPERTY_FETCH_ENABLED = "getAllEnabledProperties";
	public static final String PROPERTY_FETCH_TYPES = "getAllPropertyTypes";
	
	public static final String CHIP_FETCH_ALL = "getAllChips";
	public static final String CHIP_FETCH_TYPES = "getAllChipTypes";
	
	public static final String USER_UPDATE_ALL = "getAllUsers";
	public static final String USER_UPDATE_PROFILE = "getRegisterUser";
	
	
	public OperationId() {
	}
}
