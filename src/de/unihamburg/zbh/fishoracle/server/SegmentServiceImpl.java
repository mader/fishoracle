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

import de.unihamburg.zbh.fishoracle.client.data.FoSegment;
import de.unihamburg.zbh.fishoracle.client.rpc.SegmentService;
import de.unihamburg.zbh.fishoracle.server.data.DBInterface;

public class SegmentServiceImpl extends RemoteServiceServlet implements SegmentService {
	
	private static final long serialVersionUID = 1L;
	
	@Override
	public FoSegment add(FoSegment foStudy) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FoSegment[] fetch(int studyId)
			throws Exception {
		
		String servletContext = this.getServletContext().getRealPath("/");
		
		DBInterface db = new DBInterface(servletContext);
		
		return db.getSegmentsForStudyId(studyId);
		
	}

	@Override
	public void update(FoSegment foStudy) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(int studyId) {
		// TODO Auto-generated method stub
		
	}
}