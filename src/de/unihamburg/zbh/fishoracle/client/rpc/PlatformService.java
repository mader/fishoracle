package de.unihamburg.zbh.fishoracle.client.rpc;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;

import de.unihamburg.zbh.fishoracle.client.data.FoPlatform;
import de.unihamburg.zbh.fishoracle.client.exceptions.UserException;

public interface PlatformService extends RemoteService {

	FoPlatform add(FoPlatform foPlatform) throws UserException;
	public FoPlatform[] fetch();
	String[] fetchTypes();
	public void update(FoPlatform foPlatform);
	public void delete(int platformId);

	public static class Util {

		public static PlatformServiceAsync getInstance() {

			return GWT.create(PlatformService.class);
		}
	}
}