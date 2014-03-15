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

package de.unihamburg.zbh.fishoracle.client;

import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

import de.unihamburg.zbh.fishoracle.client.data.FoConstants;
import de.unihamburg.zbh.fishoracle.client.data.FoUser;
import de.unihamburg.zbh.fishoracle.client.datasource.FeatureDS;
import de.unihamburg.zbh.fishoracle.client.datasource.OperationId;
import de.unihamburg.zbh.fishoracle.client.datasource.ProjectDS;
import de.unihamburg.zbh.fishoracle.client.datasource.SNPMutationDS;
import de.unihamburg.zbh.fishoracle.client.datasource.SegmentDS;
import de.unihamburg.zbh.fishoracle.client.datasource.StudyDS;
import de.unihamburg.zbh.fishoracle.client.datasource.TranslocationDS;

public class StudyConfigTab extends Tab {

	private SelectItem projectSelectItem;
	private ListGrid studyGrid;
	
	private ListGrid dataTypeGrid;
	
	private CenterPanel cp;
	
	public StudyConfigTab(FoUser user, CenterPanel lcp){
		
		this.cp = lcp;
		
		this.setTitle("Manage Studies");
		this.setCanClose(true);
		
		VLayout pane = new VLayout();
		pane.setWidth100();
		pane.setHeight100();
		pane.setDefaultLayoutAlign(Alignment.CENTER);
		
		VLayout headerContainer = new VLayout();
		headerContainer.setDefaultLayoutAlign(Alignment.CENTER);
		headerContainer.setWidth100();
		headerContainer.setAutoHeight();
		
		HLayout controlsPanel = new HLayout();
		controlsPanel.setWidth100();
		controlsPanel.setAutoHeight();
		
		ToolStrip sToolStrip = new ToolStrip();
		sToolStrip.setWidth100();
		
		projectSelectItem = new SelectItem();
		projectSelectItem.setTitle("Project");
		
		projectSelectItem.setDisplayField("projectName");
		projectSelectItem.setValueField("projectId");		
		
		projectSelectItem.setAutoFetchData(false);
		
		ProjectDS pDS = new ProjectDS();
		
		projectSelectItem.setOptionDataSource(pDS);
		projectSelectItem.setOptionOperationId(OperationId.PROJECT_FETCH_ALL);
		
		projectSelectItem.setDefaultToFirstOption(true);
		projectSelectItem.addChangedHandler(new ChangedHandler() {

			@Override
			public void onChanged(ChangedEvent event) {
				
				String projectId = projectSelectItem.getValueAsString();
				
				studyGrid.fetchData(new Criteria("projectId", projectId));
			}
		});
		
		sToolStrip.addFormItem(projectSelectItem);
		
		ToolStripButton showSegmentsButton = new ToolStripButton();
		showSegmentsButton.setTitle("show segments");
		showSegmentsButton.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				
				ListGridRecord lgr = studyGrid.getSelectedRecord();
				
				if (lgr != null){
				
					openDataTab("Segments", lgr.getAttributeAsString("studyId"), cp);	
				} else {
					SC.say("Select a study.");
				}
			}});
		
		sToolStrip.addButton(showSegmentsButton);
		
		ToolStripButton showMutationsButton = new ToolStripButton();
		showMutationsButton.setTitle("show SNVs");
		showMutationsButton.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				
				ListGridRecord lgr = studyGrid.getSelectedRecord();
				
				if (lgr != null){
				
					openDataTab(FoConstants.SNV, lgr.getAttributeAsString("studyId"), cp);	
				} else {
					SC.say("Select a study.");
				}				
			}});
		
		sToolStrip.addButton(showMutationsButton);
		
		ToolStripButton showTranslocationsButton = new ToolStripButton();
		showTranslocationsButton.setTitle("show translocations");
		showTranslocationsButton.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				
				ListGridRecord lgr = studyGrid.getSelectedRecord();
				
				if (lgr != null){
				
					openDataTab("Translocations", lgr.getAttributeAsString("studyId"), cp);	
				} else {
					SC.say("Select a study.");
				}
			}});
		
		sToolStrip.addButton(showTranslocationsButton);
		
		ToolStripButton showFeaturesButton = new ToolStripButton();
		showFeaturesButton.setTitle("show features");
		showFeaturesButton.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				
				ListGridRecord lgr = studyGrid.getSelectedRecord();
				
				if (lgr != null){
				
					openDataTab("Features", lgr.getAttributeAsString("studyId"), cp);	
				} else {
					SC.say("Select a study.");
				}				
			}});
		
		sToolStrip.addButton(showFeaturesButton);
		
		ToolStripButton removeStudyButton = new ToolStripButton();
		removeStudyButton.setTitle("remove study");
		if(!user.getIsAdmin()){
			removeStudyButton.setDisabled(true);
		}
		removeStudyButton.addClickHandler(new ClickHandler(){
		
			@Override
			public void onClick(ClickEvent event) {
				
				final ListGridRecord lgr = studyGrid.getSelectedRecord();
				
				if(lgr != null) {
				
					SC.confirm("Do you really want to delete " + lgr.getAttribute("studyName") + "?", new BooleanCallback(){

						@Override
						public void execute(Boolean value) {
							if(value != null && value){
								
								lgr.setAttribute("projectId", projectSelectItem.getValueAsString());
								
								studyGrid.removeData(lgr);
							}
						}
					});
				} else {
					SC.say("Select a study.");
				}
			}});
		
		sToolStrip.addButton(removeStudyButton);
		
		controlsPanel.addMember(sToolStrip);
		
		headerContainer.addMember(controlsPanel);
		
		pane.addMember(headerContainer);
		
		HLayout gridContainer = new HLayout();
		gridContainer.setWidth100();
		gridContainer.setHeight100();
		
		studyGrid = new ListGrid();
		studyGrid.setWidth100();
		studyGrid.setHeight100();
		studyGrid.setShowAllRecords(true);
		studyGrid.setAlternateRecordStyles(true);
		studyGrid.setWrapCells(true);
		studyGrid.setShowAllRecords(false);
		studyGrid.setShowGridSummary(true);
		studyGrid.setAutoFetchData(true);
		studyGrid.setFixedRecordHeights(false);
		
		StudyDS mDS = new StudyDS();
		
		studyGrid.setDataSource(mDS);
		studyGrid.setFetchOperation(OperationId.STUDY_FETCH_FOR_PROJECT);
		
		gridContainer.addMember(studyGrid);
		
		pane.addMember(gridContainer);
		
		this.setPane(pane);		
	}
	
	public void openDataTab(String type, String studyId, CenterPanel cp){
		Tab segmentAdminTab = new Tab(type);
		segmentAdminTab.setCanClose(true);
		
		VLayout pane = new VLayout();
		pane.setWidth100();
		pane.setHeight100();
		pane.setDefaultLayoutAlign(Alignment.CENTER);
		
		HLayout gridContainer = new HLayout();
		gridContainer.setWidth100();
		gridContainer.setHeight100();
		
		dataTypeGrid = new ListGrid();
		dataTypeGrid.setWidth100();
		dataTypeGrid.setHeight100();
		dataTypeGrid.setShowAllRecords(true);
		dataTypeGrid.setAlternateRecordStyles(true);
		dataTypeGrid.setWrapCells(true);
		dataTypeGrid.setFixedRecordHeights(false);
		dataTypeGrid.setAutoFetchData(false);
		dataTypeGrid.setShowAllRecords(false);
		
		
		if(type.equals("Segments")){
			SegmentDS sDS = new SegmentDS();
		
			dataTypeGrid.setDataSource(sDS);
			dataTypeGrid.setFetchOperation(OperationId.SEGMENT_FETCH_FOR_STUDY);
		}
		
		if(type.equals(FoConstants.SNV)){
			SNPMutationDS mDS = new SNPMutationDS();
		
			dataTypeGrid.setDataSource(mDS);
			dataTypeGrid.setFetchOperation(OperationId.MUTATION_FETCH_FOR_STUDY_ID);
		}
		
		if(type.equals("Translocations")){
			TranslocationDS tDS = new TranslocationDS();
		
			dataTypeGrid.setDataSource(tDS);
		}
		
		if(type.equals("Features")){
			FeatureDS fDS = new FeatureDS();
			
			dataTypeGrid.setDataSource(fDS);
			
			dataTypeGrid.setFetchOperation(OperationId.FEATURE_FETCH_FOR_STUDY_ID);
		}
		
		dataTypeGrid.fetchData(new Criteria("studyId", studyId));
		
		gridContainer.addMember(dataTypeGrid);
		
		pane.addMember(gridContainer);
		
		segmentAdminTab.setPane(pane);
		
		cp.getCenterTabSet().addTab(segmentAdminTab);
		cp.getCenterTabSet().selectTab(segmentAdminTab);
	}
}