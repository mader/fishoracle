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
import com.smartgwt.client.data.fields.DataSourceBooleanField;
import com.smartgwt.client.data.fields.DataSourceIntegerField;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.rpc.RPCResponse;
import com.smartgwt.client.util.JSOHelper;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.grid.ListGridRecord;

import de.unihamburg.zbh.fishoracle.client.data.FoUser;
import de.unihamburg.zbh.fishoracle.client.rpc.UserService;
import de.unihamburg.zbh.fishoracle.client.rpc.UserServiceAsync;

public class UserDS extends FoDataSource {
	
	public UserDS() {
	
		DataSourceField field;
		field = new DataSourceIntegerField("userId", "User ID");
		field.setPrimaryKey(true);
		field.setRequired(true);
		field.setHidden(true);
		addField(field);
		
		field = new DataSourceTextField("userName", "Username");
		field.setRequired(true);
        addField (field);
        
        field = new DataSourceTextField("firstName", "First Name");
        field.setRequired(true);
        addField (field);
        
        field = new DataSourceTextField("lastName", "Last Name");
        field.setRequired(true);
        addField (field);
        
        field = new DataSourceTextField("email", "E-Mail");
        field.setRequired(true);
        addField (field);
		
        field = new DataSourceTextField("pw", "Password");
        field.setRequired(true);
        addField (field);
        
        field = new DataSourceBooleanField("isActive", "Active");
        field.setHidden(true);
        addField (field);
        
        field = new DataSourceBooleanField("isAdmin", "Admin");
        field.setHidden(true);
        addField (field);

	}

	@Override
	protected void executeFetch(final String requestId, final DSRequest request,
			final DSResponse response) {
		
		final UserServiceAsync req = (UserServiceAsync) GWT.create(UserService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) req;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "UserService";
		endpoint.setServiceEntryPoint(moduleRelativeURL);
		final AsyncCallback<FoUser[]> callback = new AsyncCallback<FoUser[]>(){
			
			public void onSuccess(FoUser[] result){
				
				ListGridRecord[] list = new ListGridRecord[result.length];
				
				if (result.length > 0) {
					for (int i = 0; i < result.length; i++) {
						
						ListGridRecord record = new ListGridRecord (); 
						record.setAttribute("userId", new Integer(result[i].getId()).toString());
						record.setAttribute("userName", result[i].getUserName());
						record.setAttribute("firstName", result[i].getFirstName());
						record.setAttribute("lastName", result[i].getLastName());
						record.setAttribute("email", result[i].getEmail());
						record.setAttribute("isActive", result[i].getIsActive());
						record.setAttribute("isAdmin", result[i].getIsAdmin());
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
		
		if (operationId.equals(OperationId.USER_FETCH_PROFILE)) {
			req.getSessionUserObject(callback);
		}
		if(operationId.equals(OperationId.USER_FETCH_ALL)){
			req.fetch(callback);
		}
	}

	@Override
	protected void executeAdd(final String requestId, final DSRequest request,
			final DSResponse response) {
		
		final UserServiceAsync req = (UserServiceAsync) GWT.create(UserService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) req;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "UserService";
		endpoint.setServiceEntryPoint(moduleRelativeURL);
		final AsyncCallback<FoUser> callback = new AsyncCallback<FoUser>(){
			
			public void onSuccess(FoUser result){
				
				ListGridRecord[] list = new ListGridRecord[1];
				
				ListGridRecord record = new ListGridRecord (); 
				record.setAttribute("userId", new Integer(result.getId()).toString());
				record.setAttribute("userName", result.getUserName());
				record.setAttribute("firstName", result.getFirstName());
				record.setAttribute("lastName", result.getLastName());
				record.setAttribute("email", result.getEmail());
				record.setAttribute("isActive", result.getIsActive());
				record.setAttribute("isAdmin", result.getIsAdmin());
				
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
        FoUser user = new FoUser();
        
        user.setUserName(rec.getAttribute("userName"));
        user.setFirstName(rec.getAttribute("firstName"));
        user.setLastName(rec.getAttribute("lastName"));
        user.setEmail(rec.getAttribute("email"));
        user.setPw(rec.getAttribute("pw"));
        user.setIsActive(rec.getAttributeAsBoolean("isActive"));
        user.setIsAdmin(rec.getAttributeAsBoolean("isAdmin"));
        
		req.add(user, callback);
	}

	@Override
	protected void executeUpdate(final String requestId, DSRequest request,
			final DSResponse response) {
		
        final UserServiceAsync req = (UserServiceAsync) GWT.create(UserService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) req;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "UserService";
		endpoint.setServiceEntryPoint(moduleRelativeURL);
		final AsyncCallback<FoUser> callback = new AsyncCallback<FoUser>(){
			
			public void onSuccess(FoUser result){
				
				ListGridRecord[] list = new ListGridRecord[1];
				
				ListGridRecord record = new ListGridRecord (); 
				record.setAttribute("userId", new Integer(result.getId()).toString());
				record.setAttribute("userName", result.getUserName());
				record.setAttribute("firstName", result.getFirstName());
				record.setAttribute("lastName", result.getLastName());
				record.setAttribute("email", result.getEmail());
				record.setAttribute("isActive", result.getIsActive());
				record.setAttribute("isAdmin", result.getIsAdmin());
				list[0] = record;
				
				response.setData(list);
				processResponse(requestId, response);
				
				SC.say("Update successful!");
			}
			
			public void onFailure(Throwable caught){
				response.setStatus(RPCResponse.STATUS_FAILURE);
				processResponse(requestId, response);
				SC.say(caught.getMessage());
			}
		};
		
		JavaScriptObject oldValues = request.getAttributeAsJavaScriptObject("oldValues");
        // Creating new record for combining old values with changes
        ListGridRecord newRecord = new ListGridRecord();
        // Copying properties from old record
        JSOHelper.apply(oldValues, newRecord.getJsObj());
        // Retrieving changed values
        JavaScriptObject data = request.getData();
        // Apply changes
        JSOHelper.apply(data, newRecord.getJsObj());
        
		String operationId = request.getOperationId();
		
		ListGridRecord rec = newRecord;
        FoUser user = new FoUser();
        	
        user.setId(Integer.parseInt(rec.getAttribute("userId")));
        user.setUserName(rec.getAttribute("userName"));
        user.setFirstName(rec.getAttribute("firstName"));
        user.setLastName(rec.getAttribute("lastName"));
        user.setEmail(rec.getAttribute("email"));
        user.setIsActive(rec.getAttributeAsBoolean("isActive"));
        user.setIsAdmin(rec.getAttributeAsBoolean("isAdmin"));
        
        if(operationId.equals(OperationId.USER_UPDATE_PASSWORD_ADMIN) || 
        		operationId.equals(OperationId.USER_UPDATE_PASSWORD)){
        	user.setPw(rec.getAttribute("pw"));
        }
        
        req.update(operationId, user, callback);
	}

	@Override
	protected void executeRemove(String requestId, DSRequest request,
			DSResponse response) {
		// TODO Auto-generated method stub	
	}
}