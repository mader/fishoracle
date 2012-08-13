package de.unihamburg.zbh.fishoracle.client;

import com.smartgwt.client.types.MultipleAppearance;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.CheckboxItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ChangeEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangeHandler;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.form.fields.events.ClickHandler;
import com.smartgwt.client.widgets.form.fields.events.KeyPressEvent;
import com.smartgwt.client.widgets.form.fields.events.KeyPressHandler;

import de.unihamburg.zbh.fishoracle.client.datasource.StudyDS;
import de.unihamburg.zbh.fishoracle.client.datasource.OperationId;
import de.unihamburg.zbh.fishoracle.client.datasource.OrganDS;
import de.unihamburg.zbh.fishoracle.client.datasource.ProjectDS;

public class Track {

	private MainPanel mp = null;
	private DynamicForm trackForm;
	private TextItem trackNameItem;
	private SelectItem selectItemFilter;
	private SelectItem selectItemFilterType;
	private SelectItem selectItemProjects;
	private SelectItem selectItemTissues;
	private SelectItem segmentThresholdSelectItem;
	private TextItem greaterTextItem;
	private TextItem lessTextItem;
	
	private SelectItem selectItemExperiments;
	
	private TextItem textItemQuality;
	private SelectItem selectItemSomatic;
	private SelectItem selectItemConfidence;
	private SelectItem selectItemSNPTool;
	
	private int trackNumber;

	public Track(int numberOfTracks, CheckboxItem globalThresholdCheckbox, MainPanel m){
		
		this.mp = m;
		trackNumber = numberOfTracks;
		
		trackForm = new DynamicForm();
		trackForm.setGroupTitle("Track" + numberOfTracks);
		trackForm.setIsGroup(true);
		
		trackNameItem = new TextItem();
		trackNameItem.setTitle("Track Title");
		trackNameItem.setValue("Track" + numberOfTracks);
		
		selectItemFilterType = new SelectItem();
		selectItemFilterType.setTitle("Filter Type");
		selectItemFilterType.setValueMap("Segments","Mutations");
		selectItemFilterType.setDefaultToFirstOption(true);
		selectItemFilterType.addChangedHandler(new ChangedHandler(){

			@Override
			public void onChanged(ChangedEvent event) {
				String val =  event.getValue().toString();
				if(val.equals("Segments")){
					selectItemFilter.setValueMap("Project","Tissue","Experiments");
					selectItemFilter.setValue("Project");
					if(!(Boolean) mp.getWestPanel().getGlobalThresholdCheckbox().getValue()){
						segmentThresholdSelectItem.show();
						greaterTextItem.show();
						lessTextItem.show();
					}
					textItemQuality.hide();
					selectItemSomatic.hide();
					selectItemConfidence.hide();
					selectItemSNPTool.hide();
				}
				if(val.equals("Mutations")){
					selectItemFilter.setValueMap("Project",
							"Tissue",
							"Experiments",
							"Quality",
							"Somatic",
							"Confidence",
							"SNP Tool");
					selectItemFilter.setValue("Project");
					segmentThresholdSelectItem.hide();
					greaterTextItem.hide();
					lessTextItem.hide();
				}	
			}
		});
		
		selectItemFilter = new SelectItem();
		selectItemFilter.setTitle("Filter");
		selectItemFilter.setValueMap("Project","Tissue","Experiments");
		selectItemFilter.setDefaultToFirstOption(true);
		
		segmentThresholdSelectItem = new SelectItem();
		segmentThresholdSelectItem.setTitle("");
		segmentThresholdSelectItem.setType("Select"); 
		segmentThresholdSelectItem.setValueMap("greater than", "less than");
		segmentThresholdSelectItem.setDefaultValue("less than");
		segmentThresholdSelectItem.setVisible(false);
		segmentThresholdSelectItem.addChangeHandler(new ChangeHandler(){

			@Override
			public void onChange(ChangeEvent event) {
				if(event.getValue().equals("greater than")){
					greaterTextItem.enable();
					greaterTextItem.setValue("0.5");
					lessTextItem.disable();
					lessTextItem.setValue("");
				}
				if(event.getValue().equals("less than")){
					greaterTextItem.disable();
					greaterTextItem.setValue("");
					lessTextItem.enable();
					lessTextItem.setValue("-0.5");
				}
				if(event.getValue().equals("between")){
					greaterTextItem.enable();
					greaterTextItem.setValue("0.5");
					lessTextItem.enable();
					lessTextItem.setValue("-0.5");
				}
			}
		});
		
		greaterTextItem = new TextItem();
		greaterTextItem.setTitle("greater than");
		greaterTextItem.setDisabled(true);
		greaterTextItem.setVisible(false);
		greaterTextItem.addKeyPressHandler(new KeyPressHandler(){
			
			@Override
			public void onKeyPress(KeyPressEvent event) {
				if(event.getKeyName().equals("Enter")){
					mp.getWestPanel().startSearch();
				}
			}
		});
		
		lessTextItem = new TextItem();
		lessTextItem.setTitle("less than");
		lessTextItem.setValue("-0.5");
		lessTextItem.setVisible(false);
		lessTextItem.addKeyPressHandler(new KeyPressHandler(){
			
			@Override
			public void onKeyPress(KeyPressEvent event) {
				if(event.getKeyName().equals("Enter")){
					mp.getWestPanel().startSearch();
				}
			}
		});
		
		if(!(Boolean) globalThresholdCheckbox.getValue()){
			segmentThresholdSelectItem.setVisible(true);
			greaterTextItem.setVisible(true);
			lessTextItem.setVisible(true);
		}
		
		selectItemProjects = new SelectItem();
		selectItemProjects.setTitle("Project Filter");
		selectItemProjects.setMultiple(true);
		selectItemProjects.setMultipleAppearance(MultipleAppearance.PICKLIST);
		selectItemProjects.setDisplayField("projectName");
		selectItemProjects.setValueField("projectId");		
		selectItemProjects.setAutoFetchData(false);
		ProjectDS pDS = new ProjectDS();
		
		selectItemProjects.setOptionDataSource(pDS);
		selectItemProjects.setOptionOperationId(OperationId.PROJECT_FETCH_ALL);
		
		selectItemProjects.setDefaultToFirstOption(true);
		selectItemProjects.setVisible(false);
		
		selectItemTissues = new SelectItem();
		selectItemTissues.setTitle("Tissue Filter");
		selectItemTissues.setMultiple(true);
		selectItemTissues.setMultipleAppearance(MultipleAppearance.PICKLIST);
		
		selectItemTissues.setDisplayField("organNamePlusType");
		selectItemTissues.setValueField("organId");		
		selectItemTissues.setAutoFetchData(false);
		OrganDS oDS = new OrganDS();
		
		selectItemTissues.setOptionDataSource(oDS);
		selectItemTissues.setOptionOperationId(OperationId.ORGAN_FETCH_ENABLED);
		
		selectItemTissues.setDefaultToFirstOption(true);
		selectItemTissues.setVisible(false);
		
		selectItemExperiments = new SelectItem();
		selectItemExperiments.setTitle("Experiment Filter");
		selectItemExperiments.setMultiple(true);
		selectItemExperiments.setMultipleAppearance(MultipleAppearance.PICKLIST);
		
		selectItemExperiments.setDisplayField("studyName");
		selectItemExperiments.setValueField("studyId");		
		selectItemExperiments.setAutoFetchData(false);
		StudyDS mDS = new StudyDS();
		
		selectItemExperiments.setOptionDataSource(mDS);
		selectItemExperiments.setOptionOperationId(OperationId.STUDY_FETCH_ALL);
		
		selectItemExperiments.setDefaultToFirstOption(true);
		selectItemExperiments.setVisible(false);
		
		textItemQuality = new TextItem();
		textItemQuality.setTitle("Quality Filter");
		textItemQuality.setValue(20.0);
		textItemQuality.setVisible(false);
		
		//fetch filter options from database...
		selectItemSomatic = new SelectItem();
		selectItemSomatic.setTitle("Somatic Filter");
		selectItemSomatic.setMultiple(true);
		selectItemSomatic.setMultipleAppearance(MultipleAppearance.PICKLIST);
		selectItemSomatic.setValueMap("somatic", "germline");
		selectItemSomatic.setDefaultToFirstOption(true);
		selectItemSomatic.setVisible(false);
		
		//fetch filter options from database...
		selectItemConfidence = new SelectItem();
		selectItemConfidence.setTitle("Confidence Filter");
		selectItemConfidence.setMultiple(true);
		selectItemConfidence.setMultipleAppearance(MultipleAppearance.PICKLIST);
		selectItemConfidence.setValueMap("high confidence", "moderate confidence", "low confidence");
		selectItemConfidence.setDefaultToFirstOption(true);
		selectItemConfidence.setVisible(false);
		
		//fetch filter options from database...
		selectItemSNPTool = new SelectItem();
		selectItemSNPTool.setTitle("SNP Tool Filter");
		selectItemSNPTool.setMultiple(true);
		selectItemSNPTool.setMultipleAppearance(MultipleAppearance.PICKLIST);
		selectItemSNPTool.setValueMap("gatk", "varscan", "snvmix", "samtools");
		selectItemSNPTool.setDefaultToFirstOption(true);
		selectItemSNPTool.setVisible(false);
		
		ButtonItem addFilterButton = new ButtonItem();
		addFilterButton.setTitle("add Filter");
		addFilterButton.setEndRow(false);
		addFilterButton.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(
					com.smartgwt.client.widgets.form.fields.events.ClickEvent event) {
				
					if(selectItemFilter.getValue().equals("Tissue")){
						selectItemTissues.show();
					}
					if(selectItemFilter.getValue().equals("Project")){
						selectItemProjects.show();
					}
					if(selectItemFilter.getValue().equals("Experiments")){
						selectItemExperiments.show();
					}
					if(selectItemFilter.getValue().equals("Quality")){
						textItemQuality.show();
					}
					if(selectItemFilter.getValue().equals("Somatic")){
						selectItemSomatic.show();
					}
					if(selectItemFilter.getValue().equals("Confidence")){
						selectItemConfidence.show();
					}
					if(selectItemFilter.getValue().equals("SNP Tool")){
						selectItemSNPTool.show();
					}
			}
		});
		
		ButtonItem removeFilterButton = new ButtonItem();
		removeFilterButton.setTitle("remove Filter");
		removeFilterButton.setStartRow(false);
		removeFilterButton.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(
					com.smartgwt.client.widgets.form.fields.events.ClickEvent event) {
				
					if(selectItemFilter.getValue().equals("Tissue")){
						selectItemTissues.hide();
					}
					if(selectItemFilter.getValue().equals("Project")){
						selectItemProjects.hide();
					}
					if(selectItemFilter.getValue().equals("Experiments")){
						selectItemExperiments.hide();
					}
					if(selectItemFilter.getValue().equals("Quality")){
						textItemQuality.hide();
					}
					if(selectItemFilter.getValue().equals("Somatic")){
						selectItemSomatic.hide();
					}
					if(selectItemFilter.getValue().equals("Confidence")){
						selectItemConfidence.hide();
					}
					if(selectItemFilter.getValue().equals("SNP Tool")){
						selectItemSNPTool.hide();
					}
			}
		});
		
		trackForm.setItems(trackNameItem, 
							selectItemFilterType,
							selectItemFilter,
							segmentThresholdSelectItem,
							greaterTextItem,
							lessTextItem,
							selectItemProjects,
							selectItemTissues,
							selectItemExperiments,
							textItemQuality,
							selectItemSomatic,
							selectItemConfidence,
							selectItemSNPTool,
							addFilterButton,
							removeFilterButton);
	}

	public DynamicForm getTrackForm() {
		return trackForm;
	}

	public void setTrackForm(DynamicForm trackForm) {
		this.trackForm = trackForm;
	}

	public TextItem getTrackNameItem() {
		return trackNameItem;
	}

	public void setTrackNameItem(TextItem trackNameItem) {
		this.trackNameItem = trackNameItem;
	}
	
	public SelectItem getSelectItemFilterType() {
		return selectItemFilterType;
	}

	public void setSelectItemFilterType(SelectItem selectItemFilterType) {
		this.selectItemFilterType = selectItemFilterType;
	}

	public SelectItem getSelectItemFilter() {
		return selectItemFilter;
	}

	public void setSelectItemFilter(SelectItem selectItemFilter) {
		this.selectItemFilter = selectItemFilter;
	}

	public SelectItem getSelectItemProjects() {
		return selectItemProjects;
	}

	public void setSelectItemProjects(SelectItem selectItemProjects) {
		this.selectItemProjects = selectItemProjects;
	}

	public SelectItem getSelectItemTissues() {
		return selectItemTissues;
	}

	public void setSelectItemTissues(SelectItem selectItemTissues) {
		this.selectItemTissues = selectItemTissues;
	}

	public SelectItem getSegmentThresholdSelectItem() {
		return segmentThresholdSelectItem;
	}

	public void setSegmentThresholdSelectItem(SelectItem segmentThresholdSelectItem) {
		this.segmentThresholdSelectItem = segmentThresholdSelectItem;
	}

	public TextItem getGreaterTextItem() {
		return greaterTextItem;
	}

	public void setGreaterTextItem(TextItem greaterTextItem) {
		this.greaterTextItem = greaterTextItem;
	}

	public TextItem getLessTextItem() {
		return lessTextItem;
	}

	public void setLessTextItem(TextItem lessTextItem) {
		this.lessTextItem = lessTextItem;
	}

	public SelectItem getSelectItemExperiments() {
		return selectItemExperiments;
	}

	public void setSelectItemExperiments(SelectItem selectItemExperiments) {
		this.selectItemExperiments = selectItemExperiments;
	}
	
	public TextItem getTextItemQuality() {
		return textItemQuality;
	}

	public void setTextItemQuality(TextItem textItemQuality) {
		this.textItemQuality = textItemQuality;
	}

	public SelectItem getSelectItemSomatic() {
		return selectItemSomatic;
	}

	public void setSelectItemSomatic(SelectItem selectItemSomatic) {
		this.selectItemSomatic = selectItemSomatic;
	}

	public SelectItem getSelectItemConfidence() {
		return selectItemConfidence;
	}

	public void setSelectItemConfidence(SelectItem selectItemConfidence) {
		this.selectItemConfidence = selectItemConfidence;
	}

	public SelectItem getSelectItemSNPTool() {
		return selectItemSNPTool;
	}

	public void setSelectItemSNPTool(SelectItem selectItemSNPTool) {
		this.selectItemSNPTool = selectItemSNPTool;
	}

	public int getTrackNumber() {
		return trackNumber;
	}

	public void setTrackNumber(int trackNumber) {
		this.trackNumber = trackNumber;
	}
}