package de.unihamburg.zbh.fishoracle.client.rpc;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;

import de.unihamburg.zbh.fishoracle.client.data.FoChip;
import de.unihamburg.zbh.fishoracle.client.exceptions.UserException;

public interface ChipService extends RemoteService {

	FoChip add(FoChip foChip) throws UserException;
	public FoChip[] fetch();
	String[] fetchTypes();
	public void update(FoChip foChip);
	public void delete(int chipId);

	public static class Util {

		public static ChipServiceAsync getInstance() {

			return GWT.create(ChipService.class);
		}
	}

	
}