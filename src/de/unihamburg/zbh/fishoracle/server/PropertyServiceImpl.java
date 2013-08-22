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