package de.unihamburg.zbh.fishoracle.client.rpc;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import de.unihamburg.zbh.fishoracle.client.data.FoProject;
import de.unihamburg.zbh.fishoracle.client.exceptions.UserException;

@RemoteServiceRelativePath("project")
public interface ProjectService extends RemoteService {
	
	FoProject add(FoProject foProject) throws UserException;
	public FoProject[] fetch(String operationId) throws Exception;
	public void update(FoProject project);
	public void delete(int projectId) throws UserException;
	
	public static class Util {

		public static ProjectServiceAsync getInstance() {

			return GWT.create(ProjectService.class);
		}
	}
}