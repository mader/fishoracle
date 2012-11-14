package de.unihamburg.zbh.fishoracle.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import de.unihamburg.zbh.fishoracle.client.data.FoSNPMutation;
import de.unihamburg.zbh.fishoracle.client.rpc.MutationService;
import de.unihamburg.zbh.fishoracle.server.data.DBInterface;

public class MutationServiceImpl extends RemoteServiceServlet implements MutationService {

	private static final long	serialVersionUID	= 1L;

	@Override
	public FoSNPMutation add(FoSNPMutation foMutation) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FoSNPMutation[] fetch(int studyId) throws Exception {
		
		String servletContext = this.getServletContext().getRealPath("/");
		
		DBInterface db = new DBInterface(servletContext);
		
		return db.getMutationsForStudyId(studyId);
	}

	@Override
	public void update(FoSNPMutation segment) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(int segmentId) {
		// TODO Auto-generated method stub
		
	}
}
