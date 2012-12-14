package de.unihamburg.zbh.fishoracle.client.datasource;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
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

import de.unihamburg.zbh.fishoracle.client.data.FoConfigData;
import de.unihamburg.zbh.fishoracle.client.rpc.ConfigService;
import de.unihamburg.zbh.fishoracle.client.rpc.ConfigServiceAsync;

public class ConfigDS extends FoDataSource {

	public ConfigDS() {
		
		DataSourceField field;
		field = new DataSourceIntegerField("configId", "Study ID");
		field.setPrimaryKey(true);
		field.setRequired(true);
		addField(field);
		
		field = new DataSourceTextField("configName", "Name");
		field.setRequired(true);
        addField (field);
	}
	
	@Override
	protected void executeAdd(
			final String requestId,
			DSRequest request,
			final DSResponse response) {
		
		final ConfigServiceAsync req = (ConfigServiceAsync) GWT.create(ConfigService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) req;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "ConfigService";
		endpoint.setServiceEntryPoint(moduleRelativeURL);
		final AsyncCallback<FoConfigData> callback = new AsyncCallback<FoConfigData>(){
			
			public void onSuccess(FoConfigData result){
				
				ListGridRecord[] list = new ListGridRecord[1];
				
				ListGridRecord record = new ListGridRecord (); 
				record.setAttribute("configId", result.getId());
				record.setAttribute("configName", result.getName());
				
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
		
		// how do I get the necessary object?
		
		req.add(foConf, callback);
	}
	
	@Override
	protected void executeFetch(
			final String requestId,
			DSRequest request,
			final DSResponse response) {
		
		final ConfigServiceAsync req = (ConfigServiceAsync) GWT.create(ConfigService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) req;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "ConfigService";
		endpoint.setServiceEntryPoint(moduleRelativeURL);
		final AsyncCallback<FoConfigData[]> callback = new AsyncCallback<FoConfigData[]>(){
			
			public void onSuccess(FoConfigData[] result){
				
				ListGridRecord[] list = new ListGridRecord[result.length];
				
				if (result.length > 0) {
					for (int i = 0; i < result.length; i++) {
						
						ListGridRecord record = new ListGridRecord (); 
						record.setAttribute("configId", new Integer(result[i].getId()).toString());
						record.setAttribute("configName", result[i].getName());
						
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
		
		int userId = 0;
		int configId = 0;
		
		Criteria c = request.getCriteria();
			
		if(c.getAttribute("userId") != null){
			userId = Integer.parseInt(c.getAttribute("userId"));
		} else {
			userId = 1;
		}
		
		if(request.getOperationId().equals(OperationId.CONFIG_FETCH_FOR_USER)){
		
			req.fetchForUserId(request.getOperationId(), userId, callback);
		}
		
		if(request.getOperationId().equals(OperationId.STUDY_FETCH_FOR_ID)){
			
			req.fetch(configId, callback);
		}
	}

	@Override
	protected void executeUpdate(
			String requestId,
			DSRequest request,
			DSResponse response) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void executeRemove(
			final String requestId,
			DSRequest request,
			final DSResponse response) {
		
		JavaScriptObject data = request.getData();
		final ListGridRecord rec = new ListGridRecord(data);
		
		final ConfigServiceAsync req = (ConfigServiceAsync) GWT.create(ConfigService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) req;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "ConfigService";
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
		
		int configId = Integer.parseInt(rec.getAttribute("configId"));
		
		req.delete(configId, callback);		
	}
}
