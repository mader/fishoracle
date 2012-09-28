package de.unihamburg.zbh.fishoracle.client.datasource;

import com.google.gwt.core.client.GWT;
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

import de.unihamburg.zbh.fishoracle.client.rpc.FeatureService;
import de.unihamburg.zbh.fishoracle.client.rpc.FeatureServiceAsync;

public class FeatureDS extends FoDataSource {

	public FeatureDS() {
		DataSourceField field;
		field = new DataSourceIntegerField("featureId", "Feature ID");
		field.setPrimaryKey(true);
		field.setRequired(true);
		addField(field);
		
		field = new DataSourceTextField("chromosome", "Chromosome");
		field.setRequired(true);
        addField (field);
        
        field = new DataSourceIntegerField("start", "Start");
        field.setRequired(true);
        addField (field);
        
        field = new DataSourceIntegerField("end", "End");
        field.setRequired(true);
        addField (field);
        
        field = new DataSourceTextField("featureType", "FeatureType");
        addField (field);
	}
	
	@Override
	protected void executeFetch(final String requestId, DSRequest request,
			final DSResponse response) {
		final FeatureServiceAsync req = (FeatureServiceAsync) GWT.create(FeatureService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) req;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "FeatureService";
		endpoint.setServiceEntryPoint(moduleRelativeURL);
		final AsyncCallback<String[]> typesCallback = new AsyncCallback<String[]>(){
			
			public void onSuccess(String[] result){
				
				ListGridRecord[] list = new ListGridRecord[result.length + 3];
				
				ListGridRecord record = new ListGridRecord(); 
				record.setAttribute("featureId", 0);
				record.setAttribute("featureType", "Segments");
				
				list[0] = record;
				
				record = new ListGridRecord(); 
				record.setAttribute("featureId", 0);
				record.setAttribute("featureType", "Mutations");
				
				list[1] = record;
				
				record = new ListGridRecord(); 
				record.setAttribute("featureId", 0);
				record.setAttribute("featureType", "Translocations");
				
				list[2] = record;
				
				if (result.length > 0) {
					for (int i = 0; i < result.length; i++) {
						
						record = new ListGridRecord(); 
						record.setAttribute("featureId", new Integer(i).toString());
						record.setAttribute("featureType", result[i]);
						list[i + 3] = record;
						
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
		
		if (operationId.equals(OperationId.FEATURE_FETCH_TYPES)) {
			req.fetchTypes(typesCallback);
		}
	}

	@Override
	protected void executeAdd(String requestId, DSRequest request,
			DSResponse response) {
		// TODO Auto-generated method stub
		
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
