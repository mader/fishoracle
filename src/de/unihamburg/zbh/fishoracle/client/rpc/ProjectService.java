package de.unihamburg.zbh.fishoracle.client.rpc;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import de.unihamburg.zbh.fishoracle.client.data.FoProject;

@RemoteServiceRelativePath("project")
public interface ProjectService extends RemoteService {
	
	public void add(FoProject project);
	public FoProject[] fetch() throws Exception;
	public void update(FoProject project);
	public void delete(int projectId);
	
	public static class Util {

		public static ProjectServiceAsync getInstance() {

			return GWT.create(ProjectService.class);
		}
	}
}
