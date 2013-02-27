package de.unihamburg.zbh.fishoracle.client;

import com.smartgwt.client.types.MultipleAppearance;
import com.smartgwt.client.widgets.form.fields.SelectItem;

import de.unihamburg.zbh.fishoracle.client.data.FoConstants;
import de.unihamburg.zbh.fishoracle.client.datasource.OperationId;
import de.unihamburg.zbh.fishoracle.client.datasource.OrganDS;

public class TissueSelectItem extends SelectItem {

	public TissueSelectItem(String config) {
		super();

		this.setTitle("Tissue");
		if(config.equals(FoConstants.TISSUE_SELECT_MULTI)){
			this.setMultiple(true);
			this.setMultipleAppearance(MultipleAppearance.PICKLIST);
			this.setVisible(false);
		}	
		
		this.setDisplayField("organNamePlusType");
		this.setValueField("organId");		
		this.setAutoFetchData(false);
		OrganDS oDS = new OrganDS();
		
		this.setOptionDataSource(oDS);
		this.setOptionOperationId(OperationId.ORGAN_FETCH_ENABLED);
		
		this.setDefaultToFirstOption(true);
	}
}
