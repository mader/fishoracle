/*
  Copyright (c) 2014 Malte Mader <mader@zbh.uni-hamburg.de>
  Copyright (c) 2014 Center for Bioinformatics, University of Hamburg

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

import de.unihamburg.zbh.fishoracle.client.data.EnsemblGene;
import de.unihamburg.zbh.fishoracle.client.rpc.EnsemblService;
import de.unihamburg.zbh.fishoracle.client.rpc.EnsemblServiceAsync;

public class EnsemblGeneDS extends FoDataSource {

	public EnsemblGeneDS() {
		
		DataSourceField field;
		field = new DataSourceTextField("stableId", "Stable ID");
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
        
        field = new DataSourceIntegerField("geneName", "Gene Name");
        addField (field);
        
        field = new DataSourceTextField("biotype", "Biotype");
        addField (field);
        
        field = new DataSourceTextField("description", "Description");
        addField (field);
        
        field = new DataSourceTextField("strand", "Strand");
        addField (field);
        
        field = new DataSourceIntegerField("length", "Length");
        addField (field);
	}
	
	@Override
	protected void executeFetch(
			final String requestId,
			DSRequest request,
			final DSResponse response) {
		
		final EnsemblServiceAsync req = (EnsemblServiceAsync) GWT.create(EnsemblService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) req;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "EnsemblService";
		endpoint.setServiceEntryPoint(moduleRelativeURL);
		final AsyncCallback<EnsemblGene> callback = new AsyncCallback<EnsemblGene>(){
			
			public void onSuccess(EnsemblGene result){
				
				ListGridRecord lgr = new ListGridRecord();
				
				if (result != null) {
						
					ListGridRecord record = new ListGridRecord ();
					record.setAttribute("stableId", result.getAccessionID());
					record.setAttribute("chromosome", result.getChr());
					record.setAttribute("start", result.getStart());
					record.setAttribute("end", result.getEnd());
					record.setAttribute("geneName", result.getGenName());
					record.setAttribute("biotype", result.getBioType());
					record.setAttribute("description", result.getDescription());
					record.setAttribute("strand", result.getStrand());
					record.setAttribute("length", result.getLength());
					
					lgr = record;		
				}
				response.setData(lgr);
				processResponse(requestId, response);
			}
			
			public void onFailure(Throwable caught){
				response.setStatus(RPCResponse.STATUS_FAILURE);
				processResponse(requestId, response);
				SC.say(caught.getMessage());
			}
		};
		
		String stableId = null;
		String ensemblDB = null;
		
		Criteria c = request.getCriteria();
		
		stableId = c.getAttribute("elementName");
		ensemblDB = c.getAttribute("ensemblDB");
		req.fetchGeneForId(stableId, ensemblDB, callback);
	}

	@Override
	protected void executeAdd(
			String requestId,
			DSRequest request,
			DSResponse response) {
	}

	@Override
	protected void executeUpdate(
			String requestId,
			DSRequest request,
			DSResponse response) {
	}

	@Override
	protected void executeRemove(
			String requestId,
			DSRequest request,
			DSResponse response) {	
	}
}