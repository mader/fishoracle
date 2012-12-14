package de.unihamburg.zbh.fishoracle.client.rpc;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;

import de.unihamburg.zbh.fishoracle.client.data.FoConfigData;

public interface ConfigService extends RemoteService {

	public void add(FoConfigData foConf);
	public FoConfigData fetch(int configId);
	public FoConfigData[] fetchForUser(int userId);
	public void delete(int configId);

	public static class Util {

		public static ConfigServiceAsync getInstance() {

			return GWT.create(ConfigService.class);
		}
	}

	
	
}
