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

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import de.unihamburg.zbh.fishoracle.client.data.CopyNumberChange;
import de.unihamburg.zbh.fishoracle.client.data.GWTImageInfo;
import de.unihamburg.zbh.fishoracle.client.data.Gen;
import de.unihamburg.zbh.fishoracle.client.data.FoOrgan;
import de.unihamburg.zbh.fishoracle.client.data.QueryInfo;

@RemoteServiceRelativePath("search")
public interface Search extends RemoteService {

	
	public GWTImageInfo generateImage(QueryInfo q) throws Exception;
	public GWTImageInfo redrawImage(GWTImageInfo imageInfo) throws Exception;
	public CopyNumberChange getCNCInfo(String query) throws Exception;
	public Gen getGeneInfo(String query) throws Exception;
	public String exportData(GWTImageInfo imageInfo) throws Exception;
	public FoOrgan[] getOrganData() throws Exception;
	
	public static class Util {

		public static SearchAsync getInstance() {

			return GWT.create(Search.class);
		}
	}

	

}
