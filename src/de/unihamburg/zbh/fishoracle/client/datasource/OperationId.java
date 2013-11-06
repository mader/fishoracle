/*
  Copyright (c) 2012-2013 Malte Mader <mader@zbh.uni-hamburg.de>
  Copyright (c) 2012-2013 Center for Bioinformatics, University of Hamburg

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

package de.unihamburg.zbh.fishoracle.client.datasource;

public class OperationId {
	
	public static final String PROJECT_FETCH_ALL = "getAllProjects";
	public static final String PROJECT_FETCH_READ_WRITE = "getReadWriteProjectsForUserGroup";
	
	public static final String STUDY_FETCH_ALL = "getAllStudies";
	public static final String STUDY_FETCH_FOR_PROJECT = "getAllStudiesForProject";
	public static final String STUDY_ADD_TO_PROJECT = "addStudyToProject";
	public static final String STUDY_FETCH_NOT_IN_PROJECT = "getNotInProject";
	
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
	public static final String USER_FETCH_FOR_GROUP = "getUsersForGroup";
	public static final String USER_UPDATE_PASSWORD = "getUpdateUserPw";
	public static final String USER_UPDATE_PASSWORD_ADMIN = "getUpdateUserPwAdmin";
	public static final String USER_UPDATE_PROFILE = "getUpdateUserProfile";
	public static final String USER_UPDATE_ISADMIN = "getUpdateUserIsAdmin";
	public static final String USER_UPDATE_ISACTIVE = "getUpdateUserIsActive";
	
	public static final String GROUP_FETCH_ALL = "getAllGroups";
	public static final String GROUP_FETCH_FOR_USER = "getAllGroupsForUsers";
	public static final String GROUP_ADD_USER = "addUserToGroup";
	
	public static final String FEATURE_FETCH_ALL = "getAllFeatures";
	public static final String FEATURE_FETCH_FOR_STUDY_ID = "getFeaturesForStudyId";
	public static final String FEATURE_FETCH_TYPES = "getAllFeatureTypes";
	
	public static final String MUTATION_FETCH_FOR_STUDY_ID = "getMutationsForStudyId";
	public static final String MUTATION_FETCH_FOR_ATTRIBS = "getMutationsForAttributes";
	
	public OperationId() {
	}
}
