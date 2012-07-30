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

import de.unihamburg.zbh.fishoracle.client.data.FoPlatform;
import de.unihamburg.zbh.fishoracle.client.rpc.PlatformService;
import de.unihamburg.zbh.fishoracle.client.rpc.PlatformServiceAsync;

public class PlatformDS extends FoDataSource {

	public PlatformDS() {
		DataSourceField field;
		field = new DataSourceIntegerField("platformId", "Platform ID");
		field.setPrimaryKey(true);
		field.setRequired(true);
		addField(field);
		
		field = new DataSourceTextField("platformName", "Platform Name");
		field.setRequired(true);
        addField (field);
        
        field = new DataSourceTextField("platformType", "Type");
        addField (field);
	}

	@Override
	protected void executeFetch(final String requestId, final DSRequest request,
			final DSResponse response) {
		
		final PlatformServiceAsync req = (PlatformServiceAsync) GWT.create(PlatformService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) req;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "PlatformService";
		endpoint.setServiceEntryPoint(moduleRelativeURL);
		final AsyncCallback<FoPlatform[]> PlatformCallback = new AsyncCallback<FoPlatform[]>(){
			
			public void onSuccess(FoPlatform[] result){
				
				ListGridRecord[] list = new ListGridRecord[result.length];
				
				if (result.length > 0) {
					for (int i = 0; i < result.length; i++) {
						
						ListGridRecord record = new ListGridRecord (); 
						record.setAttribute("platformId", new Integer(result[i].getId()).toString());
						record.setAttribute("platformName", result[i].getName());
						record.setAttribute("platformType", result[i].getType());
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
		
		if (operationId.equals(OperationId.PLATFORM_FETCH_TYPES)) {
			req.fetchTypes(typesCallback);
		} else {
			req.fetch(PlatformCallback);
		}
	}

	@Override
	protected void executeAdd(final String requestId, final DSRequest request,
			final DSResponse response) {
		
		final PlatformServiceAsync req = (PlatformServiceAsync) GWT.create(PlatformService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) req;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "PlatformService";
		endpoint.setServiceEntryPoint(moduleRelativeURL);
		final AsyncCallback<FoPlatform> callback = new AsyncCallback<FoPlatform>(){
			
			public void onSuccess(FoPlatform result){
				
				ListGridRecord[] list = new ListGridRecord[1];
				
				ListGridRecord record = new ListGridRecord (); 
				record.setAttribute("platformId", new Integer(result.getId()).toString());
				record.setAttribute("platformName", result.getName());
				record.setAttribute("platformType", result.getType());
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
        FoPlatform platform = new FoPlatform();
        
        platform.setName(rec.getAttribute("platformName"));
        platform.setType(rec.getAttribute("platformType"));
        
		req.add(platform, callback);
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