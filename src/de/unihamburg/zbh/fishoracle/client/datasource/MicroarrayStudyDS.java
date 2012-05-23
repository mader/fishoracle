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

import de.unihamburg.zbh.fishoracle.client.data.FoMicroarraystudy;
import de.unihamburg.zbh.fishoracle.client.rpc.MicroarrayStudyService;
import de.unihamburg.zbh.fishoracle.client.rpc.MicroarrayStudyServiceAsync;

public class MicroarrayStudyDS extends FoDataSource {
	
	public MicroarrayStudyDS() {
		
		DataSourceField field;
		field = new DataSourceIntegerField("mstudyId", "Microarraystudy ID");
		field.setPrimaryKey(true);
		field.setRequired(true);
		addField(field);
		
		field = new DataSourceTextField("mstudyName", "Name");
		field.setRequired(true);
        addField (field);
        
        field = new DataSourceTextField("mstudyDescription", "Description");
        addField (field);
        
        field = new DataSourceTextField("chipName", "Chip");
        addField (field);
        
        field = new DataSourceTextField("tissueName", "Tissue");
        addField (field);
        
        field = new DataSourceTextField("date", "Date");
        addField (field);
	}
	
	@Override
	protected void executeFetch(final String requestId, final DSRequest request,
			final DSResponse response) {
		
		final MicroarrayStudyServiceAsync req = (MicroarrayStudyServiceAsync) GWT.create(MicroarrayStudyService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) req;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "MicroarrayStudyService";
		endpoint.setServiceEntryPoint(moduleRelativeURL);
		final AsyncCallback<FoMicroarraystudy[]> callback = new AsyncCallback<FoMicroarraystudy[]>(){
			
			public void onSuccess(FoMicroarraystudy[] result){
				
				ListGridRecord[] list = new ListGridRecord[result.length];
				
				if (result.length > 0) {
					for (int i = 0; i < result.length; i++) {
						
						ListGridRecord record = new ListGridRecord (); 
						record.setAttribute("mstudyId", new Integer(result[i].getId()).toString());
						record.setAttribute("mstudyName", result[i].getName());
						record.setAttribute("mstudyDescription", result[i].getDescription());
						record.setAttribute("date", result[i].getDate());
						
						if(result[i].getChip() != null){
							record.setAttribute("chipName", result[i].getChip().getName());
						}
						
						if(result[i].getTissue() != null){
							record.setAttribute("tissueName", result[i].getTissue().getOrgan().getLabel() + 
									" (" + result[i].getTissue().getOrgan().getType() + ")");
						}
						
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
		
		int projectId = 0;
		
		if(request.getOperationId().equals(OperationId.MSTUDY_FETCH_FOR_PROJECT)){
			Criteria c = request.getCriteria();
			projectId = Integer.parseInt(c.getAttribute("projectId"));
		}
		
		req.fetch(request.getOperationId(), projectId, callback);
		
	}

	@Override
	protected void executeAdd(final String requestId, final DSRequest request,
			final DSResponse response) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void executeUpdate(final String requestId, final DSRequest request,
			final DSResponse response) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void executeRemove(final String requestId, final DSRequest request,
			final DSResponse response) {
		
		JavaScriptObject data = request.getData();
		final ListGridRecord rec = new ListGridRecord(data);
		
		final MicroarrayStudyServiceAsync req = (MicroarrayStudyServiceAsync) GWT.create(MicroarrayStudyService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) req;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "MicroarrayStudyService";
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
		
		int mstudyId = Integer.parseInt(rec.getAttribute("mstudyId"));
		
		req.delete(mstudyId, callback);
	}
}