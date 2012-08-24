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

package de.unihamburg.zbh.fishoracle.client.rpc;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import de.unihamburg.zbh.fishoracle.client.data.DBConfigData;
import de.unihamburg.zbh.fishoracle.client.data.FoCnSegment;
import de.unihamburg.zbh.fishoracle.client.data.FoGroup;
import de.unihamburg.zbh.fishoracle.client.data.FoStudy;
import de.unihamburg.zbh.fishoracle.client.data.FoOrgan;
import de.unihamburg.zbh.fishoracle.client.data.FoProject;
import de.unihamburg.zbh.fishoracle.client.data.FoProjectAccess;
import de.unihamburg.zbh.fishoracle.client.data.FoProperty;
import de.unihamburg.zbh.fishoracle.client.data.FoUser;
import de.unihamburg.zbh.fishoracle.client.exceptions.UserException;

@RemoteServiceRelativePath("admin")
public interface Admin extends RemoteService  {

	public String[] getUploadedFiles() throws Exception;
	public FoUser[] getAllUsers() throws Exception;
	public String[] toggleFlag(int id, String flag, String type, int rowNum, int colNum) throws Exception;
	public DBConfigData fetchDBConfigData() throws Exception;
	public boolean writeConfigData(DBConfigData dbcdata) throws Exception;
	int[] importData(FoStudy foStudy,
						String importType,
						boolean createStudy,
						int projectId,
						String tool,
						int importNumber,
						int nofImports) throws Exception;
	boolean canAccessDataImport() throws UserException;
	void unlockDataImport() throws UserException;
	FoGroup[] getAllFoGroups() throws Exception;
	FoUser[] getUsersForGroup(int groupId) throws UserException;
	FoGroup addGroup(FoGroup foGroup) throws UserException;
	void deleteGroup(FoGroup foGroup) throws UserException;
	FoOrgan addOrgan(FoOrgan foOrgan) throws UserException;
	FoCnSegment[] getCnSegmentsForMstudyId(int mstudyId);
	void removeMstudy(int mstudyId);
	FoStudy[] getMicorarrayStudiesForProject() throws Exception;
	FoStudy[] getMicorarrayStudiesForProject(int[] pId);
	FoOrgan[] getOrgans();
	FoOrgan[] getAllFoOrgans() throws UserException;
	String[] getAllOrganTypes();
	FoProperty addProperty(FoProperty foProperty) throws UserException;
	FoProperty[] getAllFoProperties() throws UserException;
	String[] getAllPropertyTypes();
	FoUser[] getAllUsersExceptFoGroup(FoGroup foGroup) throws UserException;
	FoUser addUserToFoGroup(FoGroup foGroup, int userId) throws UserException;
	void setPassword(int userId, String pw) throws UserException;
	FoProject[] getFoProjects() throws Exception;
	FoProject addFoProject(FoProject foProject) throws UserException;
	FoGroup[] getAllGroupsExceptFoProject(FoProject foProject);
	FoProjectAccess addAccessToFoProject(FoProjectAccess foProjectAccess,
			int projectId) throws UserException;
	FoProjectAccess[] getProjectAccessesForProject(int projectId) throws UserException;
	boolean removeAccessFromFoProject(int projectAccessId) throws UserException;
	boolean removeFoProject(int projectId) throws UserException;
	boolean removeUserFromFoGroup(int groupId, int userId) throws UserException;
	
	public static class Util {

		public static AdminAsync getInstance() {

			return GWT.create(Admin.class);
		}
	}
}