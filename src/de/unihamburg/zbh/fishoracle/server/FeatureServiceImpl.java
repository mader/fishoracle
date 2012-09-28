package de.unihamburg.zbh.fishoracle.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import de.unihamburg.zbh.fishoracle.client.rpc.FeatureService;
import de.unihamburg.zbh.fishoracle.server.data.DBInterface;

public class FeatureServiceImpl extends RemoteServiceServlet implements FeatureService {

	private static final long serialVersionUID = 1L;
	
	@Override
	public String[] fetchTypes() throws Exception {
		
		String servletContext = this.getServletContext().getRealPath("/");
		
		DBInterface db = new DBInterface(servletContext);
		
		return db.getFeatureTypes();
	}
}
