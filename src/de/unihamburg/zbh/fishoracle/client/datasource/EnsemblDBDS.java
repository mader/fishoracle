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

import de.unihamburg.zbh.fishoracle.client.data.FoEnsemblDBs;
import de.unihamburg.zbh.fishoracle.client.rpc.EnsemblDBsService;
import de.unihamburg.zbh.fishoracle.client.rpc.EnsemblDBsServiceAsync;

public class EnsemblDBDS extends FoDataSource {

	public EnsemblDBDS() {
		DataSourceField field;
		field = new DataSourceIntegerField("ensemblDBId", "ID");
		field.setPrimaryKey(true);
		field.setRequired(true);
		addField(field);
		
		field = new DataSourceTextField("ensemblDBName", "Name");
		field.setRequired(true);
        addField (field);
        
        field = new DataSourceTextField("ensemblDBLabel", "Label");
        addField (field);
        
        field = new DataSourceIntegerField("ensemblDBVersion", "Version");
        addField (field);
	}

	
	@Override
	protected void executeFetch(final String requestId, final DSRequest request,
			final DSResponse response) {
		
		final EnsemblDBsServiceAsync req = (EnsemblDBsServiceAsync) GWT.create(EnsemblDBsService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) req;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "EnsemblDBsService";
		endpoint.setServiceEntryPoint(moduleRelativeURL);
		final AsyncCallback<FoEnsemblDBs[]> ChipCallback = new AsyncCallback<FoEnsemblDBs[]>(){
			
			public void onSuccess(FoEnsemblDBs[] result){
				
				ListGridRecord[] list = new ListGridRecord[result.length];
				
				if (result.length > 0) {
					for (int i = 0; i < result.length; i++) {
						
						ListGridRecord record = new ListGridRecord (); 
						record.setAttribute("ensemblDBId", new Integer(result[i].getId()).toString());
						record.setAttribute("ensemblDBName", result[i].getDBName());
						record.setAttribute("ensemblDBLabel", result[i].getLabel());
						record.setAttribute("ensemblDBVersion", result[i].getVersion());
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
		
		req.fetch(ChipCallback);
	}

	@Override
	protected void executeAdd(final String requestId, final DSRequest request,
			final DSResponse response) {
		
		final EnsemblDBsServiceAsync req = (EnsemblDBsServiceAsync) GWT.create(EnsemblDBsService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) req;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "EnsemblDBsService";
		endpoint.setServiceEntryPoint(moduleRelativeURL);
		final AsyncCallback<FoEnsemblDBs> callback = new AsyncCallback<FoEnsemblDBs>(){
			
			public void onSuccess(FoEnsemblDBs result){
				
				ListGridRecord[] list = new ListGridRecord[1];
				
				ListGridRecord record = new ListGridRecord (); 
				record.setAttribute("ensemblDBId", new Integer(result.getId()).toString());
				record.setAttribute("ensemblDBName", result.getDBName());
				record.setAttribute("ensemblDBLabel", result.getLabel());
				record.setAttribute("ensemblDBVersion", result.getVersion());
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
        FoEnsemblDBs edbs = new FoEnsemblDBs();
        
        edbs.setDBName(rec.getAttribute("ensemblDBName"));
        edbs.setLabel(rec.getAttribute("ensemblDBLabel"));
        edbs.setVersion(Integer.parseInt(rec.getAttribute("ensemblDBVersion")));
        
		req.add(edbs, callback);
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
		
		final EnsemblDBsServiceAsync req = (EnsemblDBsServiceAsync) GWT.create(EnsemblDBsService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) req;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "EnsemblDBsService";
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
		
		int edbsId = Integer.parseInt(rec.getAttribute("ensemblDBId"));
		
		req.delete(edbsId, callback);
	}
}