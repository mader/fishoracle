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

package de.unihamburg.zbh.fishoracle.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import de.unihamburg.zbh.fishoracle.client.data.FoTranslocation;
import de.unihamburg.zbh.fishoracle.client.rpc.TranslocationService;
import de.unihamburg.zbh.fishoracle.server.data.DBInterface;

public class TranslocationServiceImpl extends RemoteServiceServlet implements TranslocationService {

	private static final long	serialVersionUID	= 1L;

	@Override
	public FoTranslocation add(FoTranslocation foTranslocation) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FoTranslocation[][] fetch(int studyId) throws Exception {
		
		String servletContext = this.getServletContext().getRealPath("/");
		
		DBInterface db = new DBInterface(servletContext);
		
		return db.getTranslocationsForStudyId(studyId);
	}

	@Override
	public void update(FoTranslocation transloc) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(int translocId) {
		// TODO Auto-generated method stub
		
	}

}
