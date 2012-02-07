package de.unihamburg.zbh.fishoracle.client;

import com.smartgwt.client.types.MultipleAppearance;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.CheckboxItem;
import com.smartgwt.client.widgets.form.fields.LinkItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ClickHandler;

public class Track {

	private DynamicForm trackForm;
	private TextItem trackNameItem;
	private SelectItem selectItemFilter;
	private SelectItem selectItemProjects;
	private SelectItem selectItemTissues;
	private LinkItem segMean;
	private SelectItem selectItemExperiments;
	
	private int trackNumber;

	public Track(int numberOfTracks, CheckboxItem globalThresholdCheckbox){
		
		trackNumber = numberOfTracks;
		
		trackForm = new DynamicForm();
		trackForm.setGroupTitle("Track" + numberOfTracks);
		trackForm.setIsGroup(true);
		
		trackNameItem = new TextItem();
		trackNameItem.setTitle("Track Title");
		trackNameItem.setValue("Track" + numberOfTracks);
		
		selectItemFilter = new SelectItem();
		selectItemFilter.setTitle("Filter");
		selectItemFilter.setDefaultToFirstOption(true);
		if((Boolean) globalThresholdCheckbox.getValue()){
			selectItemFilter.setValueMap("Project", "Tissue", "Experiments");
		} else {
			selectItemFilter.setValueMap("Project", "Tissue", "Segment Mean", "Experiments");
		}
		
		selectItemProjects = new SelectItem();
		selectItemProjects.setTitle("Project Filter");
		selectItemProjects.setMultiple(true);
		selectItemProjects.setMultipleAppearance(MultipleAppearance.PICKLIST);
		//loadProjectFilterData();
		selectItemProjects.setDefaultToFirstOption(true);
		selectItemProjects.setVisible(false);
		
		selectItemTissues = new SelectItem();
		selectItemTissues.setTitle("Tissue Filter");
		selectItemTissues.setMultiple(true);
		selectItemTissues.setMultipleAppearance(MultipleAppearance.PICKLIST);
		//loadTissueFilterData();
		selectItemTissues.setDefaultToFirstOption(true);
		selectItemTissues.setVisible(false);
		
		segMean = new LinkItem();
		segMean.setTitle("Segment Mean");
		segMean.setVisible(false);
		
		selectItemExperiments = new SelectItem();
		selectItemExperiments.setTitle("Experiment Filter");
		selectItemExperiments.setMultiple(true);
		selectItemExperiments.setMultipleAppearance(MultipleAppearance.PICKLIST);
		//loadExperimentFilterData();
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
					if(selectItemFilter.getValue().equals("Segment Mean")){
						segMean.show();
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
					if(selectItemFilter.getValue().equals("Segment Mean")){
						segMean.hide();
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
							selectItemProjects,
							selectItemTissues,
							segMean,
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

	public LinkItem getSegMean() {
		return segMean;
	}

	public void setSegMean(LinkItem segMean) {
		this.segMean = segMean;
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
