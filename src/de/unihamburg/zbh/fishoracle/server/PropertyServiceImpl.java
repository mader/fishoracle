package de.unihamburg.zbh.fishoracle.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import de.unihamburg.zbh.fishoracle.client.data.FoProperty;
import de.unihamburg.zbh.fishoracle.client.datasource.OperationId;
import de.unihamburg.zbh.fishoracle.client.exceptions.UserException;
import de.unihamburg.zbh.fishoracle.client.rpc.PropertyService;
import de.unihamburg.zbh.fishoracle.server.data.DBInterface;
import de.unihamburg.zbh.fishoracle.server.data.SessionData;

public class PropertyServiceImpl extends RemoteServiceServlet implements PropertyService {

	private static final long serialVersionUID = 1L;

	private SessionData getSessionData(){
		return new SessionData(this.getThreadLocalRequest());
	}
	
	@Override
	public FoProperty add(FoProperty foProperty) throws UserException {
		
		getSessionData().isAdmin();
		
		String servletContext = this.getServletContext().getRealPath("/");
		
		DBInterface db = new DBInterface(servletContext);
		
		return db.addProperty(foProperty);
	}

	@Override
	public FoProperty[] fetch(String operationId) throws Exception {
		
		String servletContext = this.getServletContext().getRealPath("/");
		
		DBInterface db = new DBInterface(servletContext);
		
		FoProperty[] properties = null;
				
		if(operationId.equals(OperationId.PROPERTY_FETCH_ENABLED)){
			properties = db.getProperties(true);
		}
		
		if(operationId.equals(OperationId.PROPERTY_FETCH_ALL)){
			properties = db.getAllProperties();
		}
		
		return properties;
	}

	@Override
	public String[] fetchTypes() throws Exception {
		
		String servletContext = this.getServletContext().getRealPath("/");
		
		DBInterface db = new DBInterface(servletContext);
		
		return db.getPropertyTypes();
	}

	@Override
	public void update(FoProperty foProperty) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(int propertyId) {
		// TODO Auto-generated method stub
		
	}
}