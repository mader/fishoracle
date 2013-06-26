package de.unihamburg.zbh.fishoracle.client;

import com.smartgwt.client.types.MultipleAppearance;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.ImgButton;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.CanvasItem;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.form.fields.events.KeyPressEvent;
import com.smartgwt.client.widgets.form.fields.events.KeyPressHandler;

import de.unihamburg.zbh.fishoracle.client.datasource.FeatureDS;
import de.unihamburg.zbh.fishoracle.client.datasource.StudyDS;
import de.unihamburg.zbh.fishoracle.client.datasource.OperationId;
import de.unihamburg.zbh.fishoracle.client.data.FoConstants;

public class Track {

	private ConfigLayout cl = null;
	private DynamicForm trackForm;
	private TextItem trackNameItem;
	private SelectItem selectItemFilter;
	private SelectItem selectItemFilterType;
	private ProjectSelectItem selectItemProjects;
	private RemoveButton removeProjectButtonItem;
	private TissueSelectItem selectItemTissues;
	private RemoveButton removeTissueButtonItem;
	
	private IntensitySpinnerItem thrItem;
	private SelectItem statusSelectItem;
	
	private CanvasItem addButtonItem;
	private SelectItem selectItemExperiments;
	private RemoveButton removeExperimentsButtonItem;
	
	private TextItem textItemQuality;
	private RemoveButton removeQualityButtonItem;
	private SelectItem selectItemSomatic;
	private RemoveButton removeSomaticButtonItem;
	private SelectItem selectItemConfidence;
	private RemoveButton removeConfidenceButtonItem;
	private SelectItem selectItemSNPTool;
	private RemoveButton removeSNPToolButtonItem;
	
	private FoColorPickerItem colorPicker;
	
	private int trackNumber;

	public Track(int trackPos, boolean globalTh, final boolean newSearch, ConfigLayout c){
		
		this.cl = c;
		trackNumber = trackPos;
		
		trackForm = new DynamicForm();
		trackForm.setNumCols(3);
		trackForm.setGroupTitle("Track" + trackPos);
		trackForm.setIsGroup(true);
		
		trackNameItem = new TextItem();
		trackNameItem.setTitle("Track Title");
		trackNameItem.setValue("Track" + trackPos);
		
		CanvasItem removeTrack = new CanvasItem();
		removeTrack.setShowTitle(false);
		removeTrack.setEndRow(true);
		removeTrack.setWidth(18);
		removeTrack.setHeight(18);
		Canvas remove = new Canvas();
		ImgButton removeButton = new ImgButton();
		removeButton.setWidth(18);
		removeButton.setHeight(18);
		removeButton.setSrc("[SKIN]/actions/remove.png");
		removeButton.setShowRollOver(false);
		removeButton.setShowDown(false);
		removeButton.setTooltip("Remove Track");
		
		removeButton.addClickHandler(new com.smartgwt.client.widgets.events.ClickHandler(){
		
			@Override
			public void onClick(
				com.smartgwt.client.widgets.events.ClickEvent event) {
				cl.removeTrack(trackNumber);
			}
		});
		
		remove.addChild(removeButton);
		removeTrack.setCanvas(remove);
		
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
				if(val.equals(FoConstants.CNV_STATUS) || val.equals(FoConstants.CNV_INTENSITY)){
					selectItemFilter.setValueMap("Project","Tissue"); //deactivated additional samples 
					selectItemFilter.setValue("Project");
					if(!(Boolean) cl.getGlobalThresholdCheckbox().getValue()){
						
						
						if(val.equals(FoConstants.CNV_INTENSITY)){
							thrItem.show();
							
							statusSelectItem.hide();
						}
						
						if(val.equals(FoConstants.CNV_STATUS)){
							statusSelectItem.show();
							
							thrItem.hide();
						}
						
					}
					textItemQuality.hide();
					removeQualityButtonItem.hide();
					selectItemSomatic.hide();
					removeSomaticButtonItem.hide();
					selectItemConfidence.hide();
					removeConfidenceButtonItem.hide();
					selectItemSNPTool.hide();
					removeSNPToolButtonItem.hide();
				} else if(val.equals("Mutations")){
					selectItemFilter.setValueMap("Project",
							"Tissue",
							"Quality",
							"Somatic",
							"Confidence",
							"SNP Tool");
					selectItemFilter.setValue("Project");
					thrItem.hide();
					statusSelectItem.hide();
				} else {
					selectItemFilter.setValueMap("Project","Tissue");
					selectItemFilter.setValue("Project");
					thrItem.hide();
					statusSelectItem.hide();
				}
			}
		});
		
		selectItemFilter = new SelectItem();
		selectItemFilter.setTitle("Filter");
		selectItemFilter.setValueMap("Project","Tissue");
		selectItemFilter.setDefaultToFirstOption(true);
		selectItemFilter.setStartRow(true);
		selectItemFilter.setEndRow(false);
		selectItemFilter.addChangedHandler(new ChangedHandler(){

			@Override
			public void onChanged(ChangedEvent event) {
				
				if(selectItemFilter.getValue().equals("Tissue") && !selectItemTissues.isVisible()){
					addButtonItem.enable();
				} else if(selectItemFilter.getValue().equals("Tissue")) {
					addButtonItem.disable();
				}
				if(selectItemFilter.getValue().equals("Project") && !selectItemProjects.isVisible()){
					addButtonItem.enable();
				} else if(selectItemFilter.getValue().equals("Project")) {
					addButtonItem.disable();
				}
				if(selectItemFilter.getValue().equals("Experiments") && !selectItemExperiments.isVisible()){
					addButtonItem.enable();
				} else if(selectItemFilter.getValue().equals("Experiments")) {
					addButtonItem.disable();
				}
				if(selectItemFilter.getValue().equals("Quality") && !textItemQuality.isVisible()){
					addButtonItem.enable();
				} else if(selectItemFilter.getValue().equals("Quality")) {
					addButtonItem.disable();
				}
				if(selectItemFilter.getValue().equals("Somatic") && !selectItemSomatic.isVisible()){
					addButtonItem.enable();
				} else if(selectItemFilter.getValue().equals("Somatic")) {
					addButtonItem.disable();
				}
				if(selectItemFilter.getValue().equals("Confidence") && !selectItemConfidence.isVisible()){
					addButtonItem.enable();
				} else if(selectItemFilter.getValue().equals("Confidence")) {
					addButtonItem.disable();
				}
				if(selectItemFilter.getValue().equals("SNP Tool") && !selectItemSNPTool.isVisible()){
					addButtonItem.enable();
				} else if(selectItemFilter.getValue().equals("SNP Tool")) {
					addButtonItem.disable();
				}
			}
		});
		
		addButtonItem = new CanvasItem();
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
		addButton.setTooltip("Add Filter");
		addButton.addClickHandler(new com.smartgwt.client.widgets.events.ClickHandler(){

			@Override
			public void onClick(
					com.smartgwt.client.widgets.events.ClickEvent event) {
				
					if(selectItemFilter.getValue().equals("Tissue")){
						selectItemTissues.show();
						removeTissueButtonItem.show();
						addButtonItem.disable();
					}
					if(selectItemFilter.getValue().equals("Project")){
						selectItemProjects.show();
						removeProjectButtonItem.show();
						addButtonItem.disable();
					}
					if(selectItemFilter.getValue().equals("Experiments")){
						selectItemExperiments.show();
						removeExperimentsButtonItem.show();
						addButtonItem.disable();
					}
					if(selectItemFilter.getValue().equals("Quality")){
						textItemQuality.show();
						removeQualityButtonItem.show();
						addButtonItem.disable();
					}
					if(selectItemFilter.getValue().equals("Somatic")){
						selectItemSomatic.show();
						removeSomaticButtonItem.show();
						addButtonItem.disable();
					}
					if(selectItemFilter.getValue().equals("Confidence")){
						selectItemConfidence.show();
						removeConfidenceButtonItem.show();
						addButtonItem.disable();
					}
					if(selectItemFilter.getValue().equals("SNP Tool")){
						selectItemSNPTool.show();
						removeSNPToolButtonItem.show();
						addButtonItem.disable();
					}
			}
		});
		
		add.addChild(addButton);
		addButtonItem.setCanvas(add);
		
		thrItem = new IntensitySpinnerItem();
		thrItem.setVisible(false);
		thrItem.addKeyPressHandler(new KeyPressHandler(){
			
			@Override
			public void onKeyPress(KeyPressEvent event) {
				if(event.getKeyName().equals("Enter")){
					cl.startSearch(newSearch);
				}
			}
		});
		
		statusSelectItem = new SelectItem();
		statusSelectItem.setTitle("Status");
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
					cl.startSearch(newSearch);
				}
			}
		});
		
		if(!globalTh){
			thrItem.setVisible(true);
		}
		
		selectItemProjects = new ProjectSelectItem(FoConstants.PROJECT_SELECT_MULTI);
		removeProjectButtonItem = new RemoveButton(selectItemProjects, addButtonItem, selectItemFilter, "Project");
		
		selectItemTissues = new TissueSelectItem(FoConstants.TISSUE_SELECT_MULTI);
		removeTissueButtonItem = new RemoveButton(selectItemTissues, addButtonItem, selectItemFilter, "Tissue");
		
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
		removeExperimentsButtonItem = new RemoveButton(selectItemExperiments, addButtonItem, selectItemFilter, "Experiments");
		
		textItemQuality = new TextItem();
		textItemQuality.setTitle("Quality");
		textItemQuality.setValue(20.0);
		textItemQuality.setVisible(false);
		removeQualityButtonItem = new RemoveButton(textItemQuality, addButtonItem, selectItemFilter, "Quality");
		
		//fetch filter options from database...
		selectItemSomatic = new SelectItem();
		selectItemSomatic.setTitle("Somatic");
		selectItemSomatic.setMultiple(true);
		selectItemSomatic.setMultipleAppearance(MultipleAppearance.PICKLIST);
		selectItemSomatic.setValueMap("Somatic", "germline");
		selectItemSomatic.setDefaultToFirstOption(true);
		selectItemSomatic.setVisible(false);
		removeSomaticButtonItem = new RemoveButton(selectItemSomatic, addButtonItem, selectItemFilter, "Somatic");
		
		//fetch filter options from database...
		selectItemConfidence = new SelectItem();
		selectItemConfidence.setTitle("Confidence");
		selectItemConfidence.setMultiple(true);
		selectItemConfidence.setMultipleAppearance(MultipleAppearance.PICKLIST);
		selectItemConfidence.setValueMap("high confidence", "moderate confidence", "low confidence");
		selectItemConfidence.setDefaultToFirstOption(true);
		selectItemConfidence.setVisible(false);
		removeConfidenceButtonItem = new RemoveButton(selectItemConfidence, addButtonItem, selectItemFilter, "Confidence");
		
		//fetch filter options from database...
		selectItemSNPTool = new SelectItem();
		selectItemSNPTool.setTitle("SNP Tool");
		selectItemSNPTool.setMultiple(true);
		selectItemSNPTool.setMultipleAppearance(MultipleAppearance.PICKLIST);
		selectItemSNPTool.setValueMap("gatk", "varscan", "snvmix", "samtools");
		selectItemSNPTool.setDefaultToFirstOption(true);
		selectItemSNPTool.setVisible(false);
		removeSNPToolButtonItem = new RemoveButton(selectItemSNPTool, addButtonItem, selectItemFilter, "SNP Tool");
		
		colorPicker = new FoColorPickerItem();
		colorPicker.setTitle("Color");
		
		trackForm.setItems(trackNameItem,
							removeTrack,
							selectItemFilterType,
							selectItemFilter,
							addButtonItem,
							thrItem,
							statusSelectItem,
							selectItemProjects,
							removeProjectButtonItem,
							selectItemTissues,
							removeTissueButtonItem,
							selectItemExperiments,
							removeExperimentsButtonItem,
							textItemQuality,
							removeQualityButtonItem,
							selectItemSomatic,
							removeSomaticButtonItem,
							selectItemConfidence,
							removeConfidenceButtonItem,
							selectItemSNPTool,
							removeSNPToolButtonItem,
							colorPicker);
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
	
	public SelectItem getStatusSelectItem() {
		return statusSelectItem;
	}

	public IntensitySpinnerItem getThrItem() {
		return thrItem;
	}

	public void setThrItem(IntensitySpinnerItem thrItem) {
		this.thrItem = thrItem;
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

	public RemoveButton getRemoveProjectButtonItem() {
		return removeProjectButtonItem;
	}

	public void setRemoveProjectButtonItem(RemoveButton removeProjectButtonItem) {
		this.removeProjectButtonItem = removeProjectButtonItem;
	}

	public RemoveButton getRemoveTissueButtonItem() {
		return removeTissueButtonItem;
	}

	public void setRemoveTissueButtonItem(RemoveButton removeTissueButtonItem) {
		this.removeTissueButtonItem = removeTissueButtonItem;
	}

	public RemoveButton getRemoveExperimentsButtonItem() {
		return removeExperimentsButtonItem;
	}

	public void setRemoveExperimentsButtonItem(
			RemoveButton removeExperimentsButtonItem) {
		this.removeExperimentsButtonItem = removeExperimentsButtonItem;
	}

	public RemoveButton getRemoveQualityButtonItem() {
		return removeQualityButtonItem;
	}

	public void setRemoveQualityButtonItem(RemoveButton removeQualityButtonItem) {
		this.removeQualityButtonItem = removeQualityButtonItem;
	}

	public RemoveButton getRemoveSomaticButtonItem() {
		return removeSomaticButtonItem;
	}

	public void setRemoveSomaticButtonItem(RemoveButton removeSomaticButtonItem) {
		this.removeSomaticButtonItem = removeSomaticButtonItem;
	}

	public RemoveButton getRemoveConfidenceButtonItem() {
		return removeConfidenceButtonItem;
	}

	public void setRemoveConfidenceButtonItem(
			RemoveButton removeConfidenceButtonItem) {
		this.removeConfidenceButtonItem = removeConfidenceButtonItem;
	}

	public RemoveButton getRemoveSNPToolButtonItem() {
		return removeSNPToolButtonItem;
	}

	public void setRemoveSNPToolButtonItem(RemoveButton removeSNPToolButtonItem) {
		this.removeSNPToolButtonItem = removeSNPToolButtonItem;
	}

	public FoColorPickerItem getColorPicker() {
		return colorPicker;
	}

	public void setColorPicker(FoColorPickerItem colorPicker) {
		this.colorPicker = colorPicker;
	}
}

class RemoveButton extends CanvasItem {
	
	private FormItem fi;
	private CanvasItem addButton;
	private CanvasItem self;
	private SelectItem dataType;
	private String type;
	
	RemoveButton(FormItem f, CanvasItem aButton, SelectItem dt, String t){
		super();
		this.type = t;
		this.fi = f;
		this.addButton = aButton;
		this.dataType = dt;
		this.self = this;
		
		this.setShowTitle(false);
		this.setEndRow(true);
		this.setWidth(18);
		this.setHeight(18);
		Canvas remove = new Canvas();
		ImgButton removeButton = new ImgButton();
		removeButton.setWidth(18);
		removeButton.setHeight(18);
		removeButton.setSrc("[SKIN]/actions/remove.png");
		removeButton.setShowRollOver(false);
		removeButton.setShowDown(false);
		removeButton.setTooltip("Remove " + type + " Filter");
		this.setVisible(false);
		
		removeButton.addClickHandler(new com.smartgwt.client.widgets.events.ClickHandler(){
		
			@Override
			public void onClick(
				com.smartgwt.client.widgets.events.ClickEvent event) {
			
				fi.hide();
				self.hide();
				
				if(dataType.getValue().equals(type)){
					addButton.enable();
				}
			}
		});
	
		remove.addChild(removeButton);
		this.setCanvas(remove);
	}
}