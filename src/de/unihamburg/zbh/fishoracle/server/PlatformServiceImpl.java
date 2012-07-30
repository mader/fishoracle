package de.unihamburg.zbh.fishoracle.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import de.unihamburg.zbh.fishoracle.client.data.FoPlatform;
import de.unihamburg.zbh.fishoracle.client.exceptions.UserException;
import de.unihamburg.zbh.fishoracle.client.rpc.PlatformService;
import de.unihamburg.zbh.fishoracle.server.data.DBInterface;
import de.unihamburg.zbh.fishoracle.server.data.SessionData;

public class PlatformServiceImpl extends RemoteServiceServlet implements PlatformService {
	
	private static final long serialVersionUID = 1L;

	private SessionData getSessionData(){
		return new SessionData(this.getThreadLocalRequest());
	}
	
	@Override
	public FoPlatform add(FoPlatform foPlatform) throws UserException {
		
		getSessionData().isAdmin();
		
		String servletContext = this.getServletContext().getRealPath("/");
		
		DBInterface db = new DBInterface(servletContext);
		
		return db.addPlatform(foPlatform);
		
	}

	@Override
	public FoPlatform[] fetch() {
		
		String servletContext = this.getServletContext().getRealPath("/");
		
		DBInterface db = new DBInterface(servletContext);
		
		return db.getAllPlatforms();
		
	}

	@Override
	public String[] fetchTypes() {
		
		String servletContext = this.getServletContext().getRealPath("/");
		
		DBInterface db = new DBInterface(servletContext);
		
		return db.getPlatformTypes();
		
	}
	
	@Override
	public void update(FoPlatform foPlatform) {
		// TODO Auto-generated method stub
	}

	@Override
	public void delete(int platformId) {
		// TODO Auto-generated method stub
	}
}