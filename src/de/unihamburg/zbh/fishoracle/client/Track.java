package de.unihamburg.zbh.fishoracle.client;

import com.smartgwt.client.types.MultipleAppearance;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.CheckboxItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ChangeEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangeHandler;
import com.smartgwt.client.widgets.form.fields.events.ClickHandler;
import com.smartgwt.client.widgets.form.fields.events.KeyPressEvent;
import com.smartgwt.client.widgets.form.fields.events.KeyPressHandler;

import de.unihamburg.zbh.fishoracle.client.datasource.MicroarrayStudyDS;
import de.unihamburg.zbh.fishoracle.client.datasource.OperationId;
import de.unihamburg.zbh.fishoracle.client.datasource.OrganDS;
import de.unihamburg.zbh.fishoracle.client.datasource.ProjectDS;

public class Track {

	private MainPanel mp = null;
	private DynamicForm trackForm;
	private TextItem trackNameItem;
	private SelectItem selectItemFilter;
	private SelectItem selectItemProjects;
	private SelectItem selectItemTissues;
	private SelectItem segmentThresholdSelectItem;
	private TextItem greaterTextItem;
	private TextItem lessTextItem;
	
	private SelectItem selectItemExperiments;
	
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
		
		selectItemExperiments.setDisplayField("mstudyName");
		selectItemExperiments.setValueField("mstudyId");		
		selectItemExperiments.setAutoFetchData(false);
		MicroarrayStudyDS mDS = new MicroarrayStudyDS();
		
		selectItemExperiments.setOptionDataSource(mDS);
		selectItemExperiments.setOptionOperationId(OperationId.MSTUDY_FETCH_ALL);
		
		selectItemExperiments.setDefaultToFirstOption(true);
		selectItemExperiments.setVisible(false);
		
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
			}
		});
		
		trackForm.setItems(trackNameItem, 
							selectItemFilter,
							segmentThresholdSelectItem,
							greaterTextItem,
							lessTextItem,
							selectItemProjects,
							selectItemTissues,
							selectItemExperiments,
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

	public int getTrackNumber() {
		return trackNumber;
	}

	public void setTrackNumber(int trackNumber) {
		this.trackNumber = trackNumber;
	}
}