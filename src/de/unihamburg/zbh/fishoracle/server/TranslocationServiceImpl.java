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
