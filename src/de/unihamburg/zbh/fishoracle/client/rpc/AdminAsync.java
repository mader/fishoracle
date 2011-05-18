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

package de.unihamburg.zbh.fishoracle.client.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;

import de.unihamburg.zbh.fishoracle.client.data.DBConfigData;
import de.unihamburg.zbh.fishoracle.client.data.FoGroup;
import de.unihamburg.zbh.fishoracle.client.data.FoProject;
import de.unihamburg.zbh.fishoracle.client.data.FoProjectAccess;
import de.unihamburg.zbh.fishoracle.client.data.MicroarrayOptions;
import de.unihamburg.zbh.fishoracle.client.data.FoUser;

public interface AdminAsync {

	void getAllUsers(AsyncCallback<FoUser[]> callback);

	void toggleFlag(int id, String flag, String type, int rowNum, int colNum, AsyncCallback<String[]> callback);

	void fetchDBConfigData(AsyncCallback<DBConfigData> callback);
	
	void writeConfigData(DBConfigData dbcdata, AsyncCallback<Boolean> callback);
	
	void getMicroarrayOptions(AsyncCallback<MicroarrayOptions> callback);

	void importData(String fileName,
					String studyName,
					int chipId,
					int OrganId,
					int projectId,
					int[] propertyIds,
					String description, AsyncCallback<Boolean> callback);

	void canAccessDataImport(AsyncCallback<Boolean> callback);

	void unlockDataImport(AsyncCallback<Void> callback);

	void getAllFoGroups(AsyncCallback<FoGroup[]> callback);

	void addGroup(FoGroup foGroup, AsyncCallback<FoGroup> callback);

	void addUserToFoGroup(FoGroup foGroup, int userId,
			AsyncCallback<FoUser> callback);

	void getAllFoProjects(AsyncCallback<FoProject[]> callback);

	void addFoProject(FoProject foProject, AsyncCallback<FoProject> callback);

	void getAllUsersExceptFoGroup(FoGroup foGroup,
			AsyncCallback<FoUser[]> callback);

	void getAllGroupsExceptFoProject(FoProject foProject,
			AsyncCallback<FoGroup[]> callback);

	void addAccessToFoProject(FoProjectAccess foProjectAccess, int projectId,
			AsyncCallback<FoProjectAccess> callback);
}
