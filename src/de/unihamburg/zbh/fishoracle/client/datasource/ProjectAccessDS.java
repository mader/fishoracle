package de.unihamburg.zbh.fishoracle.client.datasource;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.DataSourceField;
import com.smartgwt.client.data.fields.DataSourceIntegerField;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.rpc.RPCResponse;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.grid.ListGridRecord;

import de.unihamburg.zbh.fishoracle.client.data.FoProjectAccess;
import de.unihamburg.zbh.fishoracle.client.rpc.ProjectAccessService;
import de.unihamburg.zbh.fishoracle.client.rpc.ProjectAccessServiceAsync;

public class ProjectAccessDS extends FoDataSource {

	public ProjectAccessDS() {
		DataSourceField field;
		field = new DataSourceIntegerField("projectAccessId", "Project Access ID");
		field.setPrimaryKey(true);
		field.setRequired(true);
		addField(field);
		
		field = new DataSourceTextField("groupName", "Group");
		field.setRequired(true);
        addField (field);
        
        field = new DataSourceIntegerField("accessRight", "Access Right");
        field.setRequired(true);
        addField (field);
	}

	@Override
	protected void executeFetch(final String requestId, final DSRequest request,
			final DSResponse response) {
		
		final ProjectAccessServiceAsync req = (ProjectAccessServiceAsync) GWT.create(ProjectAccessService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) req;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "ProjectAccessService";
		endpoint.setServiceEntryPoint(moduleRelativeURL);
		final AsyncCallback<FoProjectAccess[]> callback = new AsyncCallback<FoProjectAccess[]>(){
			
			public void onSuccess(FoProjectAccess[] result){
				
				ListGridRecord[] list = new ListGridRecord[result.length];
				
				if (result.length > 0) {
					for (int i = 0; i < result.length; i++) {
						
						ListGridRecord record = new ListGridRecord ();
						record.setAttribute("projectAccessId", new Integer(result[i].getId()).toString());
						record.setAttribute("groupName", result[i].getFoGroup().getName());
						record.setAttribute("accessRight", result[i].getAccess());
						
						list[i] = record;
						
					}
				}
				response.setData(list);
				processResponse(requestId, response);
			}
			
			public void onFailure(Throwable caught){
				response.setStatus(RPCResponse.STATUS_FAILURE);
				processResponse(requestId, response);
				SC.say(caught.getMessage());
			}
		};
		
		int projectId = 0;
		
		Criteria c = request.getCriteria();
		projectId = Integer.parseInt(c.getAttribute("projectId"));
		
		req.fetch(projectId, callback);
	}

	@Override
	protected void executeAdd(String requestId, DSRequest request,
			DSResponse response) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void executeUpdate(String requestId, DSRequest request,
			DSResponse response) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void executeRemove(String requestId, DSRequest request,
			DSResponse response) {
		// TODO Auto-generated method stub
		
	}

}
