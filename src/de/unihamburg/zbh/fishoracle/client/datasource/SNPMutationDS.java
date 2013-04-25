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

import de.unihamburg.zbh.fishoracle.client.data.FoConfig;
import de.unihamburg.zbh.fishoracle.client.data.FoConfigData;
import de.unihamburg.zbh.fishoracle.client.data.FoSNPMutation;
import de.unihamburg.zbh.fishoracle.client.data.FoTrackData;
import de.unihamburg.zbh.fishoracle.client.rpc.MutationService;
import de.unihamburg.zbh.fishoracle.client.rpc.MutationServiceAsync;
import de.unihamburg.zbh.fishoracle_db_api.data.Constants;

public class SNPMutationDS extends FoDataSource {

	public SNPMutationDS() {
		DataSourceField field;
		field = new DataSourceIntegerField("mutationId", "Mutation ID");
		field.setPrimaryKey(true);
		field.setRequired(true);
		addField(field);
		
		field = new DataSourceTextField("chromosome", "Chromosome");
		field.setRequired(true);
        addField (field);
        
        field = new DataSourceIntegerField("pos", "Position");
        field.setRequired(true);
        addField (field);
        
        field = new DataSourceTextField("snpId", "DB SNP ID");
        addField (field);
        
        field = new DataSourceTextField("ref", "REF");
        addField (field);
        
        field = new DataSourceTextField("alt", "ALT");
        addField (field);
        
        field = new DataSourceIntegerField("quality", "Quality");
        addField (field);
        
        field = new DataSourceTextField("somatic", "Somatic Status");
        addField (field);
        
        field = new DataSourceTextField("conf", "Confidence");
        addField (field);
        
        field = new DataSourceTextField("tool", "SNP Tool");
        addField (field);
        
        field = new DataSourceTextField("platformName", "Platform");
        addField (field);
        
	}
	
	@Override
	protected void executeFetch(
			final String requestId,
			DSRequest request,
			final DSResponse response) {
		
		final MutationServiceAsync req = (MutationServiceAsync) GWT.create(MutationService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) req;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "MutationService";
		endpoint.setServiceEntryPoint(moduleRelativeURL);
		final AsyncCallback<FoSNPMutation[]> callback = new AsyncCallback<FoSNPMutation[]>(){
			
			public void onSuccess(FoSNPMutation[] result){
				
				ListGridRecord[] list = new ListGridRecord[result.length];
				
				if (result.length > 0) {
					for (int i = 0; i < result.length; i++) {
						
						ListGridRecord record = new ListGridRecord ();
						record.setAttribute("mutationId", new Integer(result[i].getId()).toString());
						record.setAttribute("chromosome", result[i].getLocation().getChromosome());
						record.setAttribute("pos", result[i].getLocation().getStart());
						record.setAttribute("snpId", result[i].getDbSnpId());
						record.setAttribute("ref", result[i].getRef());
						record.setAttribute("alt", result[i].getAlt());
						record.setAttribute("quality", result[i].getQuality());
						record.setAttribute("somatic", result[i].getSomatic());
						record.setAttribute("conf", result[i].getConfidence());
						record.setAttribute("tool", result[i].getSnpTool());
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
		
		int studyId = 0;
		String geneId;
		String ensemblId;
		
		Criteria c = request.getCriteria();
		
		if(request.getOperationId().equals(OperationId.MUTATION_FETCH_FOR_ATTRIBS)){
			
			geneId = c.getAttribute("geneId");
			ensemblId = c.getAttribute("ensemblId");
			
			FoTrackData conf = new FoTrackData(); 
			
			conf.setTrackNumber(Integer.parseInt(c.getAttribute("trackId")));
			conf.addStrArray(Constants.QUALTY_SCORE, c.getAttributeAsStringArray(Constants.QUALTY_SCORE));
			conf.addStrArray(Constants.SOMATIC, c.getAttributeAsStringArray(Constants.SOMATIC));
			conf.addStrArray(Constants.CONFIDENCE, c.getAttributeAsStringArray(Constants.CONFIDENCE));
			conf.addStrArray(Constants.SNP_TOOL, c.getAttributeAsStringArray(Constants.SNP_TOOL));
			conf.addStrArray(Constants.PROJECT_IDS, c.getAttributeAsStringArray(Constants.PROJECT_IDS));
			conf.addStrArray(Constants.TISSUE_IDS, c.getAttributeAsStringArray(Constants.TISSUE_IDS));
			conf.addStrArray(Constants.EXPERIMENT_IDS, c.getAttributeAsStringArray(Constants.EXPERIMENT_IDS));
			
			//FoConfigData cd = (FoConfigData) c.getAttributeAsObject("config");
			
			req.fetchForConfig(geneId, ensemblId, conf, callback);
		}
		
		if(request.getOperationId().equals(OperationId.MUTATION_FETCH_FOR_STUDY_ID)){
			studyId = Integer.parseInt(c.getAttribute("studyId"));
			req.fetch(studyId, callback);
		}
		
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
