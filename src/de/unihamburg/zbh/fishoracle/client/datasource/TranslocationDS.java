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
import com.smartgwt.client.data.fields.DataSourceIntegerField;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.rpc.RPCResponse;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.grid.ListGridRecord;

import de.unihamburg.zbh.fishoracle.client.data.FoTranslocation;
import de.unihamburg.zbh.fishoracle.client.rpc.TranslocationService;
import de.unihamburg.zbh.fishoracle.client.rpc.TranslocationServiceAsync;

public class TranslocationDS  extends FoDataSource {

	public TranslocationDS() {
		DataSourceField field;
		field = new DataSourceIntegerField("translocationId1", "Translocation ID");
		field.setPrimaryKey(true);
		field.setRequired(true);
		addField(field);
		
		field = new DataSourceTextField("chromosome1", "Chromosome");
		field.setRequired(true);
        addField (field);
        
        field = new DataSourceIntegerField("pos1", "Position");
        field.setRequired(true);
        addField (field);
        
        field = new DataSourceIntegerField("translocationId2", "Translocation ID");
		addField(field);
        
        field = new DataSourceTextField("chromosome2", "Chromosome");
		field.setRequired(true);
        addField (field);
        
        field = new DataSourceIntegerField("pos2", "Position");
        field.setRequired(true);
        addField (field);
        
        field = new DataSourceTextField("platformName", "Platform");
		field.setRequired(true);
        addField (field);
        
	}
	
	@Override
	protected void executeFetch(
			final String requestId,
			DSRequest request,
			final DSResponse response) {
		
		final TranslocationServiceAsync req = (TranslocationServiceAsync) GWT.create(TranslocationService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) req;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "TranslocationService";
		endpoint.setServiceEntryPoint(moduleRelativeURL);
		final AsyncCallback<FoTranslocation[][]> callback = new AsyncCallback<FoTranslocation[][]>(){
			
			public void onSuccess(FoTranslocation[][] result){
				
				ListGridRecord[] list = new ListGridRecord[result.length];
				
				if (result.length > 0) {
					for (int i = 0; i < result.length; i++) {
						
						ListGridRecord record = new ListGridRecord ();
						record.setAttribute("translocationId1", new Integer(result[i][0].getId()).toString());
						record.setAttribute("chromosome1", result[i][0].getFoLocation().getChromosome());
						record.setAttribute("pos1", result[i][0].getFoLocation().getStart());
						record.setAttribute("translocationId2", new Integer(result[i][1].getId()).toString());
						record.setAttribute("chromosome2", result[i][1].getFoLocation().getChromosome());
						record.setAttribute("pos2", result[i][1].getFoLocation().getStart());
						record.setAttribute("platformName", result[i][1].getPlatformName());
						
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
		
		int studyId = 0;
		
		Criteria c = request.getCriteria();
		studyId = Integer.parseInt(c.getAttribute("studyId"));
		
		req.fetch(studyId, callback);
		
	}

	@Override
	protected void executeAdd(
			String requestId,
			DSRequest request,
			DSResponse response) {
		// TODO Auto-generated method stub
		
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
			String requestId,
			DSRequest request,
			DSResponse response) {
		// TODO Auto-generated method stub
		
	}
}
