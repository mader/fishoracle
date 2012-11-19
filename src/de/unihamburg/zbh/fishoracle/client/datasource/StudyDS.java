package de.unihamburg.zbh.fishoracle.client.datasource;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.DataSourceField;
import com.smartgwt.client.data.fields.DataSourceBooleanField;
import com.smartgwt.client.data.fields.DataSourceIntegerField;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.rpc.RPCResponse;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.grid.ListGridRecord;

import de.unihamburg.zbh.fishoracle.client.data.FoStudy;
import de.unihamburg.zbh.fishoracle.client.rpc.StudyService;
import de.unihamburg.zbh.fishoracle.client.rpc.StudyServiceAsync;

public class StudyDS extends FoDataSource {
	
	public StudyDS() {
		
		DataSourceField field;
		field = new DataSourceIntegerField("studyId", "Study ID");
		field.setPrimaryKey(true);
		field.setRequired(true);
		addField(field);
		
		field = new DataSourceTextField("studyName", "Name");
		field.setRequired(true);
        addField (field);
        
        field = new DataSourceTextField("studyDescription", "Description");
        addField (field);
        
        field = new DataSourceTextField("tissueName", "Tissue");
        addField (field);
        
        field = new DataSourceTextField("date", "Date");
        addField (field);
        
        field = new DataSourceBooleanField("cnv", "CNV?");
        addField (field);
        
        field = new DataSourceBooleanField("snp", "SNP?");
        addField (field);
        
        field = new DataSourceBooleanField("transloc", "Translocation?");
        addField (field);
        
        field = new DataSourceBooleanField("generic", "Generic?");
        addField (field);
        
	}
	
	@Override
	protected void executeFetch(final String requestId, final DSRequest request,
			final DSResponse response) {
		
		final StudyServiceAsync req = (StudyServiceAsync) GWT.create(StudyService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) req;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "StudyService";
		endpoint.setServiceEntryPoint(moduleRelativeURL);
		final AsyncCallback<FoStudy[]> callback = new AsyncCallback<FoStudy[]>(){
			
			public void onSuccess(FoStudy[] result){
				
				ListGridRecord[] list = new ListGridRecord[result.length];
				
				if (result.length > 0) {
					for (int i = 0; i < result.length; i++) {
						
						ListGridRecord record = new ListGridRecord (); 
						record.setAttribute("studyId", new Integer(result[i].getId()).toString());
						record.setAttribute("studyName", result[i].getName());
						record.setAttribute("studyDescription", result[i].getDescription());
						record.setAttribute("date", result[i].getDate());
						
						if(result[i].getTissue() != null){
							record.setAttribute("tissueName", result[i].getTissue().getOrgan().getLabel() + 
									" (" + result[i].getTissue().getOrgan().getType() + ")");
						}
						
						record.setAttribute("cnv", result[i].isHasSegment());
						record.setAttribute("snp", result[i].isHasMutation());
						record.setAttribute("transloc", result[i].isHasTranslocation());
						record.setAttribute("generic", result[i].isHasGeneric());
						
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
		int notInProjectId = 0;
		
		Criteria c = request.getCriteria();
			
		if(c.getAttribute("projectId") != null){
			projectId = Integer.parseInt(c.getAttribute("projectId"));
		} else {
			projectId = 1;
		}
		
		if(request.getOperationId().equals(OperationId.STUDY_FETCH_FOR_PROJECT)){
		
			req.fetch(request.getOperationId(), projectId, callback);
		}
		
		if(request.getOperationId().equals(OperationId.STUDY_FETCH_NOT_IN_PROJECT)){
			
			if(c.getAttribute("notInProjectId") != null){
				notInProjectId = Integer.parseInt(c.getAttribute("notInProjectId"));
			} else {
				notInProjectId = 1;
			}
			
			req.fetchNotInProject(request.getOperationId(), projectId, notInProjectId, callback);
		}
	}

	@Override
	protected void executeAdd(final String requestId, final DSRequest request,
			final DSResponse response) {
		
		final StudyServiceAsync req = (StudyServiceAsync) GWT.create(StudyService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) req;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "StudyService";
		endpoint.setServiceEntryPoint(moduleRelativeURL);
		final AsyncCallback<FoStudy> callback = new AsyncCallback<FoStudy>(){
			
			public void onSuccess(FoStudy result){
				
				ListGridRecord[] list = new ListGridRecord[1];
				
				ListGridRecord record = new ListGridRecord (); 
				record.setAttribute("studyId", new Integer(result.getId()).toString());
				record.setAttribute("studyName", result.getName());
				record.setAttribute("studyDescription", result.getDescription());
				record.setAttribute("date", result.getDate());
				
				if(result.getTissue() != null){
					record.setAttribute("tissueName", result.getTissue().getOrgan().getLabel() + 
							" (" + result.getTissue().getOrgan().getType() + ")");
				}
				
				record.setAttribute("cnv", result.isHasSegment());
				record.setAttribute("snp", result.isHasMutation());
				record.setAttribute("transloc", result.isHasTranslocation());
				record.setAttribute("generic", result.isHasGeneric());
				
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
		
		if(request.getOperationId().equals(OperationId.STUDY_ADD_TO_PROJECT)){
			
			int projectId = 0;
			
			if(rec.getAttribute("projectId") != null){
				projectId = Integer.parseInt(rec.getAttribute("projectId"));
			}
        
			int studyId = Integer.parseInt(rec.getAttribute("studyId"));
			
			req.addToProject(studyId, projectId, callback);
		}
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
		
		final StudyServiceAsync req = (StudyServiceAsync) GWT.create(StudyService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) req;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "StudyService";
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
		
		int studyId = Integer.parseInt(rec.getAttribute("studyId"));
		
		int projectId = 0;
		
		if(rec.getAttribute("projectId") != null){
			projectId = Integer.parseInt(rec.getAttribute("projectId"));
		}
		
		req.delete(studyId, projectId, callback);
	}
}