/*
  Copyright (c) 2009-2014 Malte Mader <mader@zbh.uni-hamburg.de>
  Copyright (c) 2009-2014 Center for Bioinformatics, University of Hamburg

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
import de.unihamburg.zbh.fishoracle.client.data.FoStudy;

@RemoteServiceRelativePath("admin")
public interface AdminService extends RemoteService {

	public String[] getUploadedFiles() throws Exception;
	void deleteFiles(String[] files);
	public DBConfigData fetchDBConfigData() throws Exception;
	public boolean writeConfigData(DBConfigData dbcdata) throws Exception;
	int[] importData(FoStudy[] foStudy,
						String importType,
						String dataSubType,
						boolean createStudy,
						int projectId,
						String tool,
						int importNumber,
						int nofImports) throws Exception;
	
	public static class Util {

		public static AdminServiceAsync getInstance() {

			return GWT.create(AdminService.class);
		}
	}
}