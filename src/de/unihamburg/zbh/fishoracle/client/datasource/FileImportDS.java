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
import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.fields.DataSourceIntegerField;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.rpc.RPCResponse;
import com.smartgwt.client.util.JSOHelper;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.grid.ListGridRecord;

import de.unihamburg.zbh.fishoracle.client.rpc.Admin;
import de.unihamburg.zbh.fishoracle.client.rpc.AdminAsync;

public class FileImportDS extends FoDataSource {

	
	public FileImportDS() {
		DataSourceField field;
		
		field = new DataSourceTextField("fileName", "File Name");
		field.setPrimaryKey(true);
		field.setRequired(true);
        addField (field);
        
        field = new DataSourceTextField("studyName", "Study Name");
        field.setCanEdit(true);
        addField (field);
        
        field = new DataSourceIntegerField("studyId", "Study ID");
        field.setHidden(true);
        addField (field);
	}

	@Override
	protected void executeFetch(final String requestId, final DSRequest request,
			final DSResponse response) {
		
		final AdminAsync req = (AdminAsync) GWT.create(Admin.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) req;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "AdminService";
		endpoint.setServiceEntryPoint(moduleRelativeURL);
		final AsyncCallback<String[]> callback = new AsyncCallback<String[]>(){
			
			public void onSuccess(String[] result){
				
				ListGridRecord[] list = new ListGridRecord[result.length];
				
				if (result.length > 0) {
					for (int i = 0; i < result.length; i++) {
						
						ListGridRecord record = new ListGridRecord ();
						record.setAttribute("fileName", result[i]);
						
						String[] split = result[i].split("\\.");
						String studyName = "";
						
						
						for(int j = 0; j < split.length - 1; j++){
							if(j > 0){
								studyName += "." + split[j];
							} else {
								studyName += split[j];
							}
						}
						
						record.setAttribute("studyName", studyName);
						
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
		
		req.getUploadedFiles(callback);
	}

	@Override
	protected void executeAdd(String requestId, DSRequest request,
			DSResponse response) {
		//  We don't need this...
		
	}

	@Override
	protected void executeUpdate(String requestId, DSRequest request,
			DSResponse response) {
		// We don't need this...
		//SC.say("Saving");
		JavaScriptObject oldValues = request.getAttributeAsJavaScriptObject("oldValues");
        // Creating new record for combining old values with changes
        ListGridRecord newRecord = new ListGridRecord();
        // Copying properties from old record
        JSOHelper.apply(oldValues, newRecord.getJsObj());
        // Retrieving changed values
        JavaScriptObject data = request.getData();
        // Apply changes
        JSOHelper.apply(data, newRecord.getJsObj());
        
        response.setData(new ListGridRecord[]{newRecord});
		processResponse(requestId, response);
	}

	@Override
	protected void executeRemove(final String requestId, final DSRequest request,
			final DSResponse response) {
		
		JavaScriptObject data = request.getData();
		final Record[] rec = (Record[]) Record.convertToRecordArray(data);
		
		final AdminAsync req = (AdminAsync) GWT.create(Admin.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) req;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "AdminService";
		endpoint.setServiceEntryPoint(moduleRelativeURL);
		final AsyncCallback<Void> callback = new AsyncCallback<Void>(){
			
			public void onSuccess(Void v){
				
				ListGridRecord[] list = new ListGridRecord[rec.length];
				
				for(int i = 0; i < rec.length; i++){
					list[i] = (ListGridRecord) rec[i];
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
		
		String[] files = new String[rec.length];
		
		for(int i = 0; i < rec.length; i++){
			files[i] = rec[i].getAttribute("fileName");
		}
		
		req.deleteFiles(files, callback);
	}
}
