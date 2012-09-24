package de.unihamburg.zbh.fishoracle.client.datasource;

public class OperationId {
	
	public static final String PROJECT_FETCH_ALL = "getAllProjects";
	
	public static final String STUDY_FETCH_ALL = "getAllStudies";
	public static final String STUDY_FETCH_FOR_PROJECT = "getAllStudiesForProject";
	
	public static final String ORGAN_FETCH_ALL = "getAllOrgans";
	public static final String ORGAN_FETCH_ENABLED = "getAllEnabledOrgans";
	public static final String ORGAN_FETCH_TYPES = "getAllOrganTypes";
	
	public static final String PROPERTY_FETCH_ALL = "getAllProperties";
	public static final String PROPERTY_FETCH_ENABLED = "getAllEnabledProperties";
	public static final String PROPERTY_FETCH_TYPES = "getAllPropertyTypes";
	
	public static final String PLATFORM_FETCH_ALL = "getAllPlatforms";
	public static final String PLATFORM_FETCH_TYPES = "getAllPlatformTypes";
	
	public static final String USER_FETCH_ALL = "getAllUsers";
	public static final String USER_FETCH_PROFILE = "getUserProfile";
	public static final String USER_UPDATE_PASSWORD = "getUpdateUserPw";
	public static final String USER_UPDATE_PASSWORD_ADMIN = "getUpdateUserPwAdmin";
	public static final String USER_UPDATE_PROFILE = "getUpdateUserProfile";
	public static final String USER_UPDATE_ISADMIN = "getUpdateUserIsAdmin";
	public static final String USER_UPDATE_ISACTIVE = "getUpdateUserIsActive";
	
	public static final String FEATURE_FETCH_ALL = "getAllFeatures";
	public static final String FEATURE_FETCH_TYPES = "getAllFeatureTypes";
	
	public OperationId() {
	}
}
