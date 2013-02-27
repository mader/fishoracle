package de.unihamburg.zbh.fishoracle.client;

import com.smartgwt.client.types.MultipleAppearance;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.ImgButton;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.CanvasItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ChangeEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangeHandler;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.form.fields.events.ClickHandler;
import com.smartgwt.client.widgets.form.fields.events.KeyPressEvent;
import com.smartgwt.client.widgets.form.fields.events.KeyPressHandler;

import de.unihamburg.zbh.fishoracle.client.datasource.FeatureDS;
import de.unihamburg.zbh.fishoracle.client.datasource.StudyDS;
import de.unihamburg.zbh.fishoracle.client.datasource.OperationId;
import de.unihamburg.zbh.fishoracle.client.datasource.OrganDS;
import de.unihamburg.zbh.fishoracle.client.datasource.ProjectDS;
import de.unihamburg.zbh.fishoracle.client.data.FoConstants;

public class Track {

	private ConfigLayout cl = null;
	private DynamicForm trackForm;
	private TextItem trackNameItem;
	private SelectItem selectItemFilter;
	private SelectItem selectItemFilterType;
	private ProjectSelectItem selectItemProjects;
	private TissueSelectItem selectItemTissues;
	private SelectItem segmentThresholdSelectItem;
	private TextItem greaterTextItem;
	private TextItem lessTextItem;
	private SelectItem statusSelectItem;
	
	private SelectItem selectItemExperiments;
	
	private TextItem textItemQuality;
	private SelectItem selectItemSomatic;
	private SelectItem selectItemConfidence;
	private SelectItem selectItemSNPTool;
	
	private int trackNumber;

	public Track(int numberOfTracks, boolean globalTh, ConfigLayout c){
		
		this.cl = c;
		trackNumber = numberOfTracks;
		
		trackForm = new DynamicForm();
		trackForm.setNumCols(3);
		trackForm.setGroupTitle("Track" + numberOfTracks);
		trackForm.setIsGroup(true);
		
		trackNameItem = new TextItem();
		trackNameItem.setTitle("Track Title");
		trackNameItem.setValue("Track" + numberOfTracks);
		
		selectItemFilterType = new SelectItem();
		selectItemFilterType.setTitle("Data Type");
		
		selectItemFilterType.setDisplayField("featureType");
		selectItemFilterType.setValueField("featureType");
		selectItemFilterType.setAutoFetchData(false);
		FeatureDS fDS = new FeatureDS();
		
		selectItemFilterType.setOptionDataSource(fDS);
		selectItemFilterType.setOptionOperationId(OperationId.FEATURE_FETCH_TYPES);
		
		selectItemFilterType.setDefaultToFirstOption(true);
		selectItemFilterType.addChangedHandler(new ChangedHandler(){

			@Override
			public void onChanged(ChangedEvent event) {
				String val =  event.getValue().toString();
				//TODO write external method
				if(val.equals(FoConstants.ACGH_STATUS) || val.equals(FoConstants.ACGH_INTENSITY)){
					selectItemFilter.setValueMap("Project","Tissue","Experiments");
					selectItemFilter.setValue("Project");
					if(!(Boolean) cl.getGlobalThresholdCheckbox().getValue()){
						
						
						if(val.equals(FoConstants.ACGH_INTENSITY)){
							segmentThresholdSelectItem.show();
							greaterTextItem.show();
							lessTextItem.show();
							
							statusSelectItem.hide();
						}
						
						if(val.equals(FoConstants.ACGH_STATUS)){
							statusSelectItem.show();
							
							segmentThresholdSelectItem.hide();
							greaterTextItem.hide();
							lessTextItem.hide();
							
						}
						
					}
					textItemQuality.hide();
					selectItemSomatic.hide();
					selectItemConfidence.hide();
					selectItemSNPTool.hide();
				} else if(val.equals("Mutations")){
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
					statusSelectItem.hide();
				} else {
					selectItemFilter.setValueMap("Project","Tissue","Experiments");
					selectItemFilter.setValue("Project");
					segmentThresholdSelectItem.hide();
					greaterTextItem.hide();
					lessTextItem.hide();
					statusSelectItem.hide();
				}
			}
		});
		
		selectItemFilter = new SelectItem();
		selectItemFilter.setTitle("Filter");
		selectItemFilter.setValueMap("Project","Tissue","Experiments");
		selectItemFilter.setDefaultToFirstOption(true);
		selectItemFilter.setStartRow(true);
		selectItemFilter.setEndRow(false);
		
		CanvasItem addButtonItem = new CanvasItem();
		addButtonItem.setShowTitle(false);
		addButtonItem.setEndRow(true);
		addButtonItem.setWidth(18);
		addButtonItem.setHeight(18);
		Canvas add = new Canvas();
		ImgButton addButton = new ImgButton();
		addButton.setWidth(18);
		addButton.setHeight(18);
		addButton.setSrc("[SKIN]/actions/add.png");
		addButton.setShowRollOver(false);
		addButton.setShowDown(false);
		addButton.addClickHandler(new com.smartgwt.client.widgets.events.ClickHandler(){

			@Override
			public void onClick(
					com.smartgwt.client.widgets.events.ClickEvent event) {
				
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
		
		
		add.addChild(addButton);
		addButtonItem.setCanvas(add);
		
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
					cl.startSearch();
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
					cl.startSearch();
				}
			}
		});
		
		statusSelectItem = new SelectItem();
		statusSelectItem.setTitle("CNV Status");
		statusSelectItem.setMultiple(true);
		statusSelectItem.setMultipleAppearance(MultipleAppearance.PICKLIST);
		statusSelectItem.setValueMap("0",
										"1",
										"2",
										"3",
										"4");
		statusSelectItem.setDefaultValues("0","1");
		statusSelectItem.setVisible(false);
		statusSelectItem.addKeyPressHandler(new KeyPressHandler(){
			
			@Override
			public void onKeyPress(KeyPressEvent event) {
				if(event.getKeyName().equals("Enter")){
					cl.startSearch();
				}
			}
		});
		
		if(!globalTh){
			segmentThresholdSelectItem.setVisible(true);
			greaterTextItem.setVisible(true);
			lessTextItem.setVisible(true);
		}
		
		
		selectItemProjects = new ProjectSelectItem(FoConstants.PROJECT_SELECT_MULTI);
		
		selectItemTissues = new TissueSelectItem(FoConstants.TISSUE_SELECT_MULTI);
		
		selectItemExperiments = new SelectItem();
		selectItemExperiments.setTitle("Experiment");
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
		textItemQuality.setTitle("Quality");
		textItemQuality.setValue(20.0);
		textItemQuality.setVisible(false);
		
		//fetch filter options from database...
		selectItemSomatic = new SelectItem();
		selectItemSomatic.setTitle("Somatic");
		selectItemSomatic.setMultiple(true);
		selectItemSomatic.setMultipleAppearance(MultipleAppearance.PICKLIST);
		selectItemSomatic.setValueMap("somatic", "germline");
		selectItemSomatic.setDefaultToFirstOption(true);
		selectItemSomatic.setVisible(false);
		
		//fetch filter options from database...
		selectItemConfidence = new SelectItem();
		selectItemConfidence.setTitle("Confidence");
		selectItemConfidence.setMultiple(true);
		selectItemConfidence.setMultipleAppearance(MultipleAppearance.PICKLIST);
		selectItemConfidence.setValueMap("high confidence", "moderate confidence", "low confidence");
		selectItemConfidence.setDefaultToFirstOption(true);
		selectItemConfidence.setVisible(false);
		
		//fetch filter options from database...
		selectItemSNPTool = new SelectItem();
		selectItemSNPTool.setTitle("SNP Tool");
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
							addButtonItem,
							segmentThresholdSelectItem,
							greaterTextItem,
							lessTextItem,
							statusSelectItem,
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

	public ProjectSelectItem getSelectItemProjects() {
		return selectItemProjects;
	}

	public void setSelectItemProjects(ProjectSelectItem selectItemProjects) {
		this.selectItemProjects = selectItemProjects;
	}

	public TissueSelectItem getSelectItemTissues() {
		return selectItemTissues;
	}

	public void setSelectItemTissues(TissueSelectItem selectItemTissues) {
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
	
	public SelectItem getStatusSelectItem() {
		return statusSelectItem;
	}

	public void setStatusSelectItem(SelectItem statusSelectItem) {
		this.statusSelectItem = statusSelectItem;
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