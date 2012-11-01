package de.unihamburg.zbh.fishoracle.client.datasource;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.DataSourceField;
import com.smartgwt.client.data.fields.DataSourceFloatField;
import com.smartgwt.client.data.fields.DataSourceIntegerField;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.rpc.RPCResponse;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.grid.ListGridRecord;

import de.unihamburg.zbh.fishoracle.client.data.FoSegment;
import de.unihamburg.zbh.fishoracle.client.rpc.SegmentService;
import de.unihamburg.zbh.fishoracle.client.rpc.SegmentServiceAsync;

public class SegmentDS extends FoDataSource {

	public SegmentDS() {
		DataSourceField field;
		field = new DataSourceIntegerField("segmentId", "Segment ID");
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
        
        field = new DataSourceFloatField("score", "Mean Intensity/Status");
        addField (field);
        
        field = new DataSourceIntegerField("markers", "Number of Markers");
        addField (field);
        
        field = new DataSourceTextField("type", "Segment Type");
        addField (field);
	}

	@Override
	protected void executeFetch(final String requestId, final DSRequest request,
			final DSResponse response) {
		
		final SegmentServiceAsync req = (SegmentServiceAsync) GWT.create(SegmentService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) req;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "SegmentService";
		endpoint.setServiceEntryPoint(moduleRelativeURL);
		final AsyncCallback<FoSegment[]> callback = new AsyncCallback<FoSegment[]>(){
			
			public void onSuccess(FoSegment[] result){
				
				ListGridRecord[] list = new ListGridRecord[result.length];
				
				if (result.length > 0) {
					for (int i = 0; i < result.length; i++) {
						
						ListGridRecord record = new ListGridRecord ();
						record.setAttribute("segmentId", new Integer(result[i].getId()).toString());
						record.setAttribute("chromosome", result[i].getLocation().getChromosome());
						record.setAttribute("start", result[i].getLocation().getStart());
						record.setAttribute("end", result[i].getLocation().getEnd());
						record.setAttribute("score", result[i].getMean());
						record.setAttribute("markers", result[i].getNumberOfMarkers());
						record.setAttribute("type", result[i].getType());
						
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
		
		int mstudyId = 0;
		
		Criteria c = request.getCriteria();
		mstudyId = Integer.parseInt(c.getAttribute("studyId"));
		
		req.fetch(mstudyId, callback);
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