package de.unihamburg.zbh.fishoracle.client;

import com.smartgwt.client.types.MultipleAppearance;
import com.smartgwt.client.widgets.form.fields.SelectItem;

import de.unihamburg.zbh.fishoracle.client.data.FoConstants;
import de.unihamburg.zbh.fishoracle.client.datasource.OperationId;
import de.unihamburg.zbh.fishoracle.client.datasource.ProjectDS;

public class ProjectSelectItem extends SelectItem {

	public ProjectSelectItem(String config) {
		super();
		this.setTitle("Project");
		if(config.equals(FoConstants.PROJECT_SELECT_MULTI)){
			this.setMultiple(true);
			this.setMultipleAppearance(MultipleAppearance.PICKLIST);
			this.setVisible(false);
		}
		this.setDisplayField("projectName");
		this.setValueField("projectId");
		this.setAutoFetchData(false);
		ProjectDS pDS = new ProjectDS();
		
		this.setOptionDataSource(pDS);
		this.setOptionOperationId(OperationId.PROJECT_FETCH_ALL);
		
		this.setDefaultToFirstOption(true);
	}
}
