/*
  Copyright (c) 2014 Malte Mader <mader@zbh.uni-hamburg.de>
  Copyright (c) 2014 Center for Bioinformatics, University of Hamburg

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
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.DataSourceField;
import com.smartgwt.client.data.fields.DataSourceBooleanField;
import com.smartgwt.client.data.fields.DataSourceIntegerField;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.rpc.RPCResponse;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.grid.ListGridRecord;

import de.unihamburg.zbh.fishoracle.client.data.FoGroup;
import de.unihamburg.zbh.fishoracle.client.rpc.GroupService;
import de.unihamburg.zbh.fishoracle.client.rpc.GroupServiceAsync;

public class GroupDS extends FoDataSource {

	public GroupDS() {
		
		DataSourceField field;
		field = new DataSourceIntegerField("groupId", "Group ID");
		field.setPrimaryKey(true);
		field.setRequired(true);
		addField(field);
		
		field = new DataSourceTextField("groupName", "Group Name");
		field.setRequired(true);
        addField(field);
        
        field = new DataSourceBooleanField("isActive", "Active");
        field.setHidden(true);
        addField(field);
        
        field = new DataSourceIntegerField("userId", "User ID");
		field.setRequired(false);
		field.setHidden(true);
		addField(field);
	}
	
	@Override
	protected void executeFetch(final String requestId,
									final DSRequest request,
									final DSResponse response) {
		
		final GroupServiceAsync req = (GroupServiceAsync) GWT.create(GroupService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) req;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "GroupService";
		endpoint.setServiceEntryPoint(moduleRelativeURL);
		final AsyncCallback<FoGroup[]> callback = new AsyncCallback<FoGroup[]>(){
			
			public void onSuccess(FoGroup[] result){
				
				ListGridRecord[] list = new ListGridRecord[result.length];
				
				if (result.length > 0) {
					for (int i = 0; i < result.length; i++) {
						
						ListGridRecord record = new ListGridRecord (); 
						record.setAttribute("groupId", new Integer(result[i].getId()).toString());
						record.setAttribute("groupName", result[i].getName());
						record.setAttribute("isActive", result[i].isIsactive());
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
		
		String operationId = request.getOperationId();
		
		if (operationId.equals(OperationId.GROUP_FETCH_ALL)) {
			req.getAllFoGroups(callback);
		}
		if(operationId.equals(OperationId.GROUP_FETCH_FOR_USER)){
			req.getAllGroupsForUser(callback);
		}
		if(operationId.equals(OperationId.GROUP_FETCH_EXCEPT_PROJECT)){
			Criteria c = request.getCriteria();
			int projectId = Integer.parseInt(c.getAttribute("projectId"));
			req.getAllGroupsExceptFoProject(projectId, callback);
		}
	}

	@Override
	protected void executeAdd(final String requestId,
								final DSRequest request,
								final DSResponse response) {
		
		final GroupServiceAsync req = (GroupServiceAsync) GWT.create(GroupService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) req;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "GroupService";
		endpoint.setServiceEntryPoint(moduleRelativeURL);
		
		String operationId = request.getOperationId();
		JavaScriptObject data = request.getData();
		ListGridRecord rec = new ListGridRecord(data);
		
		if (operationId.equals(OperationId.GROUP_ADD)){
		
			final AsyncCallback<FoGroup> callbackAdd = new AsyncCallback<FoGroup>(){
			
				public void onSuccess(FoGroup result){
				
					ListGridRecord[] list = new ListGridRecord[1];
				
					ListGridRecord record = new ListGridRecord (); 
					record.setAttribute("groupId", new Integer(result.getId()).toString());
					record.setAttribute("groupName", result.getName());
					record.setAttribute("isActive", result.isIsactive());
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
			
			FoGroup group = new FoGroup();
			group.setName(rec.getAttribute("groupName"));
			group.setIsactive(true);
        
			req.addGroup(group, callbackAdd);
		}
		
		if (operationId.equals(OperationId.GROUP_ADD_USER)) {
			
			final AsyncCallback<Void> callbackAddUser = new AsyncCallback<Void>(){
				
				public void onSuccess(Void result){
				
					SC.say("User was successfully added to Group.");
				}
			
				public void onFailure(Throwable caught){
					response.setStatus(RPCResponse.STATUS_FAILURE);
					processResponse(requestId, response);
					SC.say(caught.getMessage());
				}
			};

	        int userId;
	        int groupId;
	        
	        userId = Integer.parseInt(rec.getAttribute("userId"));
	        groupId =  Integer.parseInt(rec.getAttribute("groupId"));
	        req.addUserToFoGroup(groupId, userId, callbackAddUser);
		}
	}

	@Override
	protected void executeUpdate(
			String requestId,
			DSRequest request,
			DSResponse response) {
	}

	@Override
	protected void executeRemove(final String requestId,
									final DSRequest request,
									final DSResponse response) {
		
		String operationId = request.getOperationId();
		JavaScriptObject data = request.getData();
		final ListGridRecord rec = new ListGridRecord(data);
		
		final GroupServiceAsync req = (GroupServiceAsync) GWT.create(GroupService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) req;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "GroupService";
		endpoint.setServiceEntryPoint(moduleRelativeURL);
		
		if (operationId.equals(OperationId.GROUP_REMOVE)) {
			
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
		
			int groupId = Integer.parseInt(rec.getAttribute("groupId"));
		
			FoGroup group = new FoGroup(groupId, "", true);
		
			//TODO just use the id as parameter.
			req.deleteGroup(group, callback);
		}
		
		if (operationId.equals(OperationId.GROUP_REMOVE_USER)) {
			
			final AsyncCallback<Void> callbackRemoveUser = new AsyncCallback<Void>(){
			
				public void onSuccess(Void v){
					
					SC.say("User was successfully removed from Group.");
				}
			
				public void onFailure(Throwable caught){
					response.setStatus(RPCResponse.STATUS_FAILURE);
					processResponse(requestId, response);
					SC.say(caught.getMessage());
				}
			};
			
			int userId = Integer.parseInt(rec.getAttribute("userId"));
			int groupId = Integer.parseInt(rec.getAttribute("groupId"));
			
			req.removeUserFromFoGroup(groupId, userId, callbackRemoveUser);
		}
		
	}
}