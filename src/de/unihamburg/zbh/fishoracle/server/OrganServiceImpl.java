package de.unihamburg.zbh.fishoracle.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import de.unihamburg.zbh.fishoracle.client.data.FoOrgan;
import de.unihamburg.zbh.fishoracle.client.datasource.OperationId;
import de.unihamburg.zbh.fishoracle.client.exceptions.UserException;
import de.unihamburg.zbh.fishoracle.client.rpc.OrganService;
import de.unihamburg.zbh.fishoracle.server.data.DBInterface;
import de.unihamburg.zbh.fishoracle.server.data.SessionData;

public class OrganServiceImpl extends RemoteServiceServlet implements OrganService {

	private static final long serialVersionUID = 1L;

	private SessionData getSessionData(){
		return new SessionData(this.getThreadLocalRequest());
	}
	
	@Override
	public FoOrgan add(FoOrgan foOrgan) throws UserException {
		
		getSessionData().isAdmin();
		
		String servletContext = this.getServletContext().getRealPath("/");
		
		DBInterface db = new DBInterface(servletContext);
		
		return db.addOrgan(foOrgan);
		
	}

	@Override
	public FoOrgan[] fetch(String operationId) throws Exception {
		
		String servletContext = this.getServletContext().getRealPath("/");
		
		DBInterface db = new DBInterface(servletContext);
		
		FoOrgan[] organs = null;
				
		if(operationId.equals(OperationId.ORGAN_FETCH_ENABLED)){
			organs = db.getOrgans(true);
		}
		
		if(operationId.equals(OperationId.ORGAN_FETCH_ALL)){
			organs = db.getAllOrgans();
		}
		
		return organs;
	}
	
	@Override
	public String[] fetchTypes() throws Exception {
		
		String servletContext = this.getServletContext().getRealPath("/");
		
		DBInterface db = new DBInterface(servletContext);
		
		return db.getOrganTypes();
	}

	@Override
	public void update(FoOrgan foOrgan) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(int organId) {
		// TODO Auto-generated method stub	
	}
}