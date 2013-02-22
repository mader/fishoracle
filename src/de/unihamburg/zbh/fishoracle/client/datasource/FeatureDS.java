package de.unihamburg.zbh.fishoracle.client.datasource;

import com.google.gwt.core.client.GWT;
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

import de.unihamburg.zbh.fishoracle.client.data.FoConstants;
import de.unihamburg.zbh.fishoracle.client.data.FoGenericFeature;
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
        
        field = new DataSourceTextField("platformName", "Platform");
        addField (field);
        
	}
	
	@Override
	protected void executeFetch(final String requestId, DSRequest request,
			final DSResponse response) {
		
		
		
		final FeatureServiceAsync req = (FeatureServiceAsync) GWT.create(FeatureService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) req;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "FeatureService";
		endpoint.setServiceEntryPoint(moduleRelativeURL);
		
		final AsyncCallback<FoGenericFeature[]> featureCallback = new AsyncCallback<FoGenericFeature[]>(){
			
			public void onSuccess(FoGenericFeature[] result){
				
				ListGridRecord[] list = new ListGridRecord[result.length];
				
				if (result.length > 0) {
					for (int i = 0; i < result.length; i++) {
						
						ListGridRecord record = new ListGridRecord (); 
						record.setAttribute("featureId", new Integer(result[i].getId()).toString());
						record.setAttribute("chromosome", result[i].getFoLocation().getChromosome());
						record.setAttribute("start", result[i].getFoLocation().getStart());
						record.setAttribute("end", result[i].getFoLocation().getEnd());
						record.setAttribute("featureType", result[i].getType());
						record.setAttribute("platformName", result[i].getPlatformName());
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
				
				ListGridRecord[] list = new ListGridRecord[result.length + 4];
				
				ListGridRecord record = new ListGridRecord(); 
				record.setAttribute("featureId", 0);
				record.setAttribute("featureType", FoConstants.ACGH_INTENSITY);
				record.setAttribute("type", "dnacopy");
				
				list[0] = record;
				
				record = new ListGridRecord(); 
				record.setAttribute("featureId", 0);
				record.setAttribute("featureType", FoConstants.ACGH_STATUS);
				record.setAttribute("type", "penncnv");
				
				list[1] = record;
				
				record = new ListGridRecord(); 
				record.setAttribute("featureId", 0);
				record.setAttribute("featureType", "Mutations");
				
				list[2] = record;
				
				record = new ListGridRecord(); 
				record.setAttribute("featureId", 0);
				record.setAttribute("featureType", "Translocations");
				
				list[3] = record;
				
				if (result.length > 0) {
					for (int i = 0; i < result.length; i++) {
						
						record = new ListGridRecord(); 
						record.setAttribute("featureId", new Integer(i).toString());
						record.setAttribute("featureType", result[i]);
						list[i + 4] = record;
						
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
		} else {
		
			int studyId = 0;
		
			Criteria c = request.getCriteria();
			studyId = Integer.parseInt(c.getAttribute("studyId"));
		
			req.fetch(studyId, featureCallback);
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
