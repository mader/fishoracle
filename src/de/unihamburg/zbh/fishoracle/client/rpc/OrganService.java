package de.unihamburg.zbh.fishoracle.client.rpc;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;

import de.unihamburg.zbh.fishoracle.client.data.FoOrgan;
import de.unihamburg.zbh.fishoracle.client.exceptions.UserException;

public interface OrganService extends RemoteService {

	FoOrgan add(FoOrgan foOrgan) throws UserException;
	FoOrgan[] fetch(String operationId) throws Exception;
	String[] fetchTypes() throws Exception;
	public void update(FoOrgan foOrgan);
	public void delete(int organId);
	
	public static class Util {

		public static OrganServiceAsync getInstance() {

			return GWT.create(OrganService.class);
		}
	}

	
}