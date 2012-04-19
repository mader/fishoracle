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

import de.unihamburg.zbh.fishoracle.client.data.FoProperty;
import de.unihamburg.zbh.fishoracle.client.rpc.PropertyService;
import de.unihamburg.zbh.fishoracle.client.rpc.PropertyServiceAsync;

public class PropertyDS extends FoDataSource  {

	public PropertyDS() {
		
		DataSourceField field;
		field = new DataSourceIntegerField("propertyId", "Property ID");
		field.setPrimaryKey(true);
		field.setRequired(true);
		addField(field);
		
		field = new DataSourceTextField("propertyName", "Property Name");
		field.setRequired(true);
        addField (field);
        
        field = new DataSourceTextField("propertyType", "Type");
        addField (field);
        
        field = new DataSourceTextField("propertyActivity", "Enabled");
        addField (field);
		
	}

	@Override
	protected void executeFetch(final String requestId, final DSRequest request,
			final DSResponse response) {
		
		final PropertyServiceAsync req = (PropertyServiceAsync) GWT.create(PropertyService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) req;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "PropertyService";
		endpoint.setServiceEntryPoint(moduleRelativeURL);
		final AsyncCallback<FoProperty[]> organCallback = new AsyncCallback<FoProperty[]>(){
			
			public void onSuccess(FoProperty[] result){
				
				ListGridRecord[] list = new ListGridRecord[result.length];
				
				if (result.length > 0) {
					for (int i = 0; i < result.length; i++) {
						
						ListGridRecord record = new ListGridRecord (); 
						record.setAttribute("propertyId", new Integer(result[i].getId()).toString());
						record.setAttribute("propertyName", result[i].getLabel());
						record.setAttribute("propertyType", result[i].getType());
						record.setAttribute("propertyActivity", result[i].getActivty());
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
		
		if (operationId.equals(OperationId.PROPERTY_FETCH_TYPES)) {
			req.fetchTypes(typesCallback);
		} else {
			req.fetch(request.getOperationId(), organCallback);
		}
	}

	@Override
	protected void executeAdd(final String requestId, final DSRequest request,
			final DSResponse response) {
		
		final PropertyServiceAsync req = (PropertyServiceAsync) GWT.create(PropertyService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) req;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "PropertyService";
		endpoint.setServiceEntryPoint(moduleRelativeURL);
		final AsyncCallback<FoProperty> callback = new AsyncCallback<FoProperty>(){
			
			public void onSuccess(FoProperty result){
				
				ListGridRecord[] list = new ListGridRecord[1];
				
				ListGridRecord record = new ListGridRecord (); 
				record.setAttribute("propertyId", new Integer(result.getId()).toString());
				record.setAttribute("propertyName", result.getLabel());
				record.setAttribute("propertyType", result.getType());
				record.setAttribute("propertyActivity", result.getActivty());
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
        FoProperty property = new FoProperty();
        
        property.setLabel(rec.getAttribute("propertyName"));
        property.setType(rec.getAttribute("propertyType"));
        property.setActivty("enabled");
        
		req.add(property, callback);
		
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