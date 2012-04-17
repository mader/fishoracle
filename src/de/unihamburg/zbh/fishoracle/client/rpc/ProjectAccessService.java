package de.unihamburg.zbh.fishoracle.client.rpc;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;

import de.unihamburg.zbh.fishoracle.client.data.FoProjectAccess;

public interface ProjectAccessService extends RemoteService {

	FoProjectAccess add(FoProjectAccess foProjectAccess);
	FoProjectAccess[] fetch(int projectId) throws Exception;
	public void update(FoProjectAccess foProjectAccess);
	public void delete(int projectAccessId);
	
	public static class Util {

		public static ProjectAccessServiceAsync getInstance() {

			return GWT.create(ProjectAccessService.class);
		}
	}
}
