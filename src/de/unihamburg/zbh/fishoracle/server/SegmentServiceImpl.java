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