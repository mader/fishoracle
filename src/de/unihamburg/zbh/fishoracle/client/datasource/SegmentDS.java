/*
  Copyright (c) 2012-2014 Malte Mader <mader@zbh.uni-hamburg.de>
  Copyright (c) 2012-2014 Center for Bioinformatics, University of Hamburg

  Permission to use, copy, modify, and distribute this software for any
  purpose with or without fee is hereby granted, provided that the above
  copyright notice and this permission notice appear in all copies.

  THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
  WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
  MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
  ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
  WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
  ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
  OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
*/

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
import com.smartgwt.client.data.fields.DataSourceLinkField;
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
        
        field = new DataSourceFloatField("mean", "Mean Intensity");
        addField (field);
        
        field = new DataSourceIntegerField("markers", "Number of Markers");
        addField (field);
        
        field = new DataSourceIntegerField("status", "Status");
        addField (field);
        
        field = new DataSourceFloatField("statusScore", "Score");
        addField (field);
        
        field = new DataSourceTextField("type", "Segment Type");
        addField (field);
        
        field = new DataSourceTextField("platformName", "Platform");
        addField (field);
        
        field = new DataSourceTextField("studyName", "Study");
        addField (field);
        
        field = new DataSourceLinkField("dgv", "DGV Link");
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
						record.setAttribute("chromosome", result[i].getFoLocation().getChromosome());
						record.setAttribute("start", result[i].getFoLocation().getStart());
						record.setAttribute("end", result[i].getFoLocation().getEnd());
						record.setAttribute("mean", result[i].getMean());
						record.setAttribute("markers", result[i].getNumberOfMarkers());
						record.setAttribute("status", result[i].getStatus());
						record.setAttribute("statusScore", result[i].getStatusScore());
						record.setAttribute("type", result[i].getType());
						record.setAttribute("platformName", result[i].getPlatformName());
						record.setAttribute("studyName", result[i].getStudyName());
						record.setAttribute("dgv", "<a href=\"http://dgv.tcag.ca/gb2/gbrowse/dgv2_hg19/?name=chr"
								+ result[i].getFoLocation().getChromosome() + ":"
								+ result[i].getFoLocation().getStart() 
								+ "-" +  result[i].getFoLocation().getEnd()
								+ ";search=Search\" target=_blank> Inspect in DGV </a>");
						
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
		
		int studyId = 0;
		int segmentId = 0;
		
		Criteria c = request.getCriteria();
		
		if(operationId.equals(OperationId.SEGMENT_FETCH_FOR_STUDY)){
		
			studyId = Integer.parseInt(c.getAttribute("studyId"));
			req.fetch(studyId, callback);
		}
		
		if(operationId.equals(OperationId.SEGMENT_FETCH_FOR_ID)){
			segmentId = Integer.parseInt(c.getAttribute("elementName"));
			req.fetchForSegmentId(segmentId, callback);
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