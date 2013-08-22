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