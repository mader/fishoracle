package de.unihamburg.zbh.fishoracle.client.rpc;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;

import de.unihamburg.zbh.fishoracle.client.data.FoProperty;
import de.unihamburg.zbh.fishoracle.client.exceptions.UserException;

public interface PropertyService extends RemoteService {

	FoProperty add(FoProperty foProperty) throws UserException;
	FoProperty[] fetch(String operationId) throws Exception;
	String[] fetchTypes() throws Exception;
	public void update(FoProperty foProperty);
	public void delete(int propertyId);
	
	public static class Util {

		public static PropertyServiceAsync getInstance() {

			return GWT.create(PropertyService.class);
		}
	}
}