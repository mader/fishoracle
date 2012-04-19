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

import de.unihamburg.zbh.fishoracle.client.data.FoChip;
import de.unihamburg.zbh.fishoracle.client.rpc.ChipService;
import de.unihamburg.zbh.fishoracle.client.rpc.ChipServiceAsync;

public class ChipDS extends FoDataSource {

	public ChipDS() {
		DataSourceField field;
		field = new DataSourceIntegerField("chipId", "Chip ID");
		field.setPrimaryKey(true);
		field.setRequired(true);
		addField(field);
		
		field = new DataSourceTextField("chipName", "Chip Name");
		field.setRequired(true);
        addField (field);
        
        field = new DataSourceTextField("chipType", "Type");
        addField (field);
	}

	@Override
	protected void executeFetch(final String requestId, final DSRequest request,
			final DSResponse response) {
		
		final ChipServiceAsync req = (ChipServiceAsync) GWT.create(ChipService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) req;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "ChipService";
		endpoint.setServiceEntryPoint(moduleRelativeURL);
		final AsyncCallback<FoChip[]> ChipCallback = new AsyncCallback<FoChip[]>(){
			
			public void onSuccess(FoChip[] result){
				
				ListGridRecord[] list = new ListGridRecord[result.length];
				
				if (result.length > 0) {
					for (int i = 0; i < result.length; i++) {
						
						ListGridRecord record = new ListGridRecord (); 
						record.setAttribute("chipId", new Integer(result[i].getId()).toString());
						record.setAttribute("chipName", result[i].getName());
						record.setAttribute("chipType", result[i].getType());
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
		
		if (operationId.equals(OperationId.CHIP_FETCH_TYPES)) {
			req.fetchTypes(typesCallback);
		} else {
			req.fetch(ChipCallback);
		}
	}

	@Override
	protected void executeAdd(final String requestId, final DSRequest request,
			final DSResponse response) {
		
		final ChipServiceAsync req = (ChipServiceAsync) GWT.create(ChipService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) req;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "ChipService";
		endpoint.setServiceEntryPoint(moduleRelativeURL);
		final AsyncCallback<FoChip> callback = new AsyncCallback<FoChip>(){
			
			public void onSuccess(FoChip result){
				
				ListGridRecord[] list = new ListGridRecord[1];
				
				ListGridRecord record = new ListGridRecord (); 
				record.setAttribute("chipId", new Integer(result.getId()).toString());
				record.setAttribute("chipName", result.getName());
				record.setAttribute("chipType", result.getType());
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
        FoChip chip = new FoChip();
        
        chip.setName(rec.getAttribute("chipName"));
        chip.setType(rec.getAttribute("chipType"));
        
		req.add(chip, callback);
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