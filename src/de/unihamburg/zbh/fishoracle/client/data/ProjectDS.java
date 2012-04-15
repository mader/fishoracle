package de.unihamburg.zbh.fishoracle.client.data;

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

import de.unihamburg.zbh.fishoracle.client.rpc.ProjectService;
import de.unihamburg.zbh.fishoracle.client.rpc.ProjectServiceAsync;

public class ProjectDS extends FoDataSource {

	public ProjectDS() {
		
		DataSourceField field;
		field = new DataSourceIntegerField("projectId", "Project ID");
		field.setPrimaryKey(true);
		field.setRequired(true);
		addField(field);
		
		field = new DataSourceTextField("projectName", "Project Name");
		field.setRequired(true);
        addField (field);
        
        field = new DataSourceTextField("projectDescription", "Project Description");
        addField (field);
	}

	@Override
	protected void executeFetch(final String requestId, final DSRequest request,
			final DSResponse response) {
		
		final ProjectServiceAsync req = (ProjectServiceAsync) GWT.create(ProjectService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) req;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "ProjectService";
		endpoint.setServiceEntryPoint(moduleRelativeURL);
		final AsyncCallback<FoProject[]> callback = new AsyncCallback<FoProject[]>(){
			
			public void onSuccess(FoProject[] result){
				
				ListGridRecord[] list = new ListGridRecord[result.length];
				
				if (result.length > 0) {
					for (int i = 0; i < result.length; i++) {
						
						ListGridRecord record = new ListGridRecord (); 
						record.setAttribute("projectId", new Integer(result[i].getId()).toString());
						record.setAttribute("projectName", result[i].getName());
						record.setAttribute("projectDescription", result[i].getDescription());
						list[i] = record;
						
					}
				}
				response.setData(list);
				processResponse(requestId, response);
			}
			
			public void onFailure(Throwable caught){
				response.setStatus (RPCResponse.STATUS_FAILURE);
				processResponse (requestId, response);
				SC.say(caught.getMessage());
			}
		};
		
		Criteria criteria = request.getCriteria();
		req.fetch(criteria, callback);
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