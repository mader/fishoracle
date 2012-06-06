package de.unihamburg.zbh.fishoracle.client.rpc;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;

import de.unihamburg.zbh.fishoracle.client.data.FoEnsemblDBs;
import de.unihamburg.zbh.fishoracle.client.exceptions.UserException;

public interface EnsemblDBsService extends RemoteService {
	
	FoEnsemblDBs add(FoEnsemblDBs foEdbs) throws UserException;
	public FoEnsemblDBs[] fetch();
	public void delete(int edbsId) throws UserException;

	public static class Util {

		public static EnsemblDBsServiceAsync getInstance() {

			return GWT.create(ChipService.class);
		}
	}
}