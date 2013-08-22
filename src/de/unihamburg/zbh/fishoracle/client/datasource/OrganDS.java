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

import de.unihamburg.zbh.fishoracle.client.data.FoOrgan;
import de.unihamburg.zbh.fishoracle.client.rpc.OrganService;
import de.unihamburg.zbh.fishoracle.client.rpc.OrganServiceAsync;

public class OrganDS extends FoDataSource {

	public OrganDS() {
		DataSourceField field;
		field = new DataSourceIntegerField("organId", "Organ ID");
		field.setPrimaryKey(true);
		field.setRequired(true);
		addField(field);
		
		field = new DataSourceTextField("organName", "Organ Name");
		field.setRequired(true);
        addField (field);
        
        field = new DataSourceTextField("organType", "Type");
        addField (field);
        
        field = new DataSourceTextField("organNamePlusType", "NameType");
        addField (field);
        
        field = new DataSourceTextField("organActivity", "Enabled");
        addField (field);
	}

	@Override
	protected void executeFetch(final String requestId, final DSRequest request,
			final DSResponse response) {
		
		final OrganServiceAsync req = (OrganServiceAsync) GWT.create(OrganService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) req;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "OrganService";
		endpoint.setServiceEntryPoint(moduleRelativeURL);
		final AsyncCallback<FoOrgan[]> organCallback = new AsyncCallback<FoOrgan[]>(){
			
			public void onSuccess(FoOrgan[] result){
				
				ListGridRecord[] list = new ListGridRecord[result.length];
				
				if (result.length > 0) {
					for (int i = 0; i < result.length; i++) {
						
						ListGridRecord record = new ListGridRecord (); 
						record.setAttribute("organId", new Integer(result[i].getId()).toString());
						record.setAttribute("organName", result[i].getLabel());
						record.setAttribute("organType", result[i].getType());
						record.setAttribute("organNamePlusType", result[i].getLabel() + " ("+ result[i].getType() + ")");
						record.setAttribute("organActivity", result[i].getActivty());
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
		
		final AsyncCallback<String[]> typesCallback = new AsyncCallback<String[]>(){
			
			public void onSuccess(String[] result){
			
				ListGridRecord[] list = new ListGridRecord[result.length];
				
				if (result.length > 0) {
					for (int i = 0; i < result.length; i++) {
						
						ListGridRecord record = new ListGridRecord (); 
						record.setAttribute("typeId", new Integer(i).toString());
						record.setAttribute("typeName", result[i]);
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
		
		if (operationId.equals(OperationId.ORGAN_FETCH_TYPES)) {
			req.fetchTypes(typesCallback);
		} else {
			req.fetch(request.getOperationId(), organCallback);
		}
	}

	@Override
	protected void executeAdd(final String requestId, DSRequest request,
			final DSResponse response) {
		
		final OrganServiceAsync req = (OrganServiceAsync) GWT.create(OrganService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) req;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "OrganService";
		endpoint.setServiceEntryPoint(moduleRelativeURL);
		final AsyncCallback<FoOrgan> callback = new AsyncCallback<FoOrgan>(){
			
			public void onSuccess(FoOrgan result){
				
				ListGridRecord[] list = new ListGridRecord[1];
				
				ListGridRecord record = new ListGridRecord (); 
				record.setAttribute("organId", new Integer(result.getId()).toString());
				record.setAttribute("organName", result.getLabel());
				record.setAttribute("organType", result.getType());
				record.setAttribute("organActivity", result.getActivty());
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
        FoOrgan organ = new FoOrgan();
        
        organ.setLabel(rec.getAttribute("organName"));
        organ.setType(rec.getAttribute("organType"));
        organ.setActivty("enabled");
        
		req.add(organ, callback);
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