/*
  Copyright (c) 2012-2013 Malte Mader <mader@zbh.uni-hamburg.de>
  Copyright (c) 2012-2013 Center for Bioinformatics, University of Hamburg

  Permission to use, copy, modify, and distribute this software for any
  purpose with or without fee is hereby granted, provided that the above
  copyright notice and this permission notice appear in all copies.

  THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
  WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
  MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
  ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
  WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
  ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
  OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
*/

package de.unihamburg.zbh.fishoracle.client.datasource;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.DataSourceField;
import com.smartgwt.client.data.fields.DataSourceIntegerField;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.rpc.RPCResponse;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.grid.ListGridRecord;

import de.unihamburg.zbh.fishoracle.client.data.FoProject;
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
				response.setStatus(RPCResponse.STATUS_FAILURE);
				processResponse(requestId, response);
				SC.say(caught.getMessage());
			}
		};
		
		req.fetch(request.getOperationId(), callback);
	}

	@Override
	protected void executeAdd(final String requestId, final DSRequest request,
			final DSResponse response) {
		
		final ProjectServiceAsync req = (ProjectServiceAsync) GWT.create(ProjectService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) req;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "ProjectService";
		endpoint.setServiceEntryPoint(moduleRelativeURL);
		final AsyncCallback<FoProject> callback = new AsyncCallback<FoProject>(){
			
			public void onSuccess(FoProject result){
				
				ListGridRecord[] list = new ListGridRecord[1];
				
				ListGridRecord record = new ListGridRecord (); 
				record.setAttribute("projectId", new Integer(result.getId()).toString());
				record.setAttribute("projectName", result.getName());
				record.setAttribute("projectDescription", result.getDescription());
				list[0] = record;
				
				response.setData(list);
				processResponse(requestId, response);
			}
			
			public void onFailure(Throwable caught){
				response.setStatus(RPCResponse.STATUS_FAILURE);
				processResponse(requestId, response);
				SC.say(caught.getMessage());
			}
		};
		
		JavaScriptObject data = request.getData();
		ListGridRecord rec = new ListGridRecord(data);
        FoProject project = new FoProject();
        
        project.setId(rec.getAttributeAsInt("projectId"));
        project.setName(rec.getAttribute("projectName"));
        project.setDescription(rec.getAttribute("projectDescription"));
        
		req.add(project, callback);
		
	}

	@Override
	protected void executeUpdate(String requestId, DSRequest request,
			DSResponse response) {
		// TODO Auto-generated method stub
	}

	@Override
	protected void executeRemove(final String requestId, final DSRequest request,
			final DSResponse response) {
		
		JavaScriptObject data = request.getData();
		final ListGridRecord rec = new ListGridRecord(data);
		
		final ProjectServiceAsync req = (ProjectServiceAsync) GWT.create(ProjectService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) req;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "ProjectService";
		endpoint.setServiceEntryPoint(moduleRelativeURL);
		final AsyncCallback<Void> callback = new AsyncCallback<Void>(){
			
			public void onSuccess(Void v){
				
				ListGridRecord[] list = new ListGridRecord[1];
				
				list[0] = rec;
				
				response.setData(list);
				processResponse(requestId, response);
			}
			
			public void onFailure(Throwable caught){
				response.setStatus(RPCResponse.STATUS_FAILURE);
				processResponse(requestId, response);
				SC.say(caught.getMessage());
			}
		};
		
		int projectId = Integer.parseInt(rec.getAttribute("projectId"));
		
		req.delete(projectId, callback);
	}
}