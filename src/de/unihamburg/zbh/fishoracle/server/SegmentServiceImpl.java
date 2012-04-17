package de.unihamburg.zbh.fishoracle.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import de.unihamburg.zbh.fishoracle.client.data.FoCnSegment;
import de.unihamburg.zbh.fishoracle.client.rpc.SegmentService;
import de.unihamburg.zbh.fishoracle.server.data.DBInterface;

public class SegmentServiceImpl extends RemoteServiceServlet implements SegmentService {
	
	private static final long serialVersionUID = 1L;
	
	@Override
	public FoCnSegment add(FoCnSegment foMicroarraystudy) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FoCnSegment[] fetch(int mstudyId)
			throws Exception {
		
		String servletContext = this.getServletContext().getRealPath("/");
		
		DBInterface db = new DBInterface(servletContext);
		
		return db.getCnSegmentsForMstudyId(mstudyId);
		
	}

	@Override
	public void update(FoCnSegment foMicroarraystudy) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(int mstudyId) {
		// TODO Auto-generated method stub
		
	}
}