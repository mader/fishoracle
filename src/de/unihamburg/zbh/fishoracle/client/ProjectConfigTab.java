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

import java.util.LinkedHashMap;

import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.MultipleAppearance;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.TextAreaItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.form.fields.events.DataArrivedEvent;
import com.smartgwt.client.widgets.form.fields.events.DataArrivedHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

import de.unihamburg.zbh.fishoracle.client.data.FoProject;
import de.unihamburg.zbh.fishoracle.client.data.FoUser;
import de.unihamburg.zbh.fishoracle.client.datasource.GroupDS;
import de.unihamburg.zbh.fishoracle.client.datasource.OperationId;
import de.unihamburg.zbh.fishoracle.client.datasource.ProjectAccessDS;
import de.unihamburg.zbh.fishoracle.client.datasource.ProjectDS;
import de.unihamburg.zbh.fishoracle.client.datasource.StudyDS;

public class ProjectConfigTab extends Tab {

	private ListGrid projectGrid;
	private ListGrid projectAccessGrid;
	private ListGrid projectStudyGrid;
	
	private TextItem projectNameTextItem;
	private TextAreaItem projectDescriptionItem;
	
	private SelectItem accessRightSelectItem;
	private SelectItem groupSelectItem;
	
	private SelectItem studySelectItem;
	private SelectItem projectSelectItem;
	
	public ProjectConfigTab(FoUser user){
		
		this.setTitle("Manage Projects");
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
		
		ToolStrip projectToolStrip = new ToolStrip();
		projectToolStrip.setWidth100();
		
		ToolStripButton addProjectButton = new ToolStripButton();  
		addProjectButton.setTitle("add Project");
		
		if(user.getIsAdmin() == false){
			addProjectButton.setDisabled(true);
		}
		
		addProjectButton.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				loadProjectManageWindow();
			}});
		
		projectToolStrip.addButton(addProjectButton);
		
		ToolStripButton deleteProjectButton = new ToolStripButton();  
		deleteProjectButton.setTitle("delete Project");
		
		if(user.getIsAdmin() == false){
			deleteProjectButton.setDisabled(true);
		}
		
		deleteProjectButton.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				
				final ListGridRecord lgr = projectGrid.getSelectedRecord();
				
				if (lgr != null) {
				
					SC.confirm("Do you really want to delete " + lgr.getAttribute("projectName") + "?", new BooleanCallback(){

						@Override
						public void execute(Boolean value) {
							if(value != null && value){
								
								String projectId = lgr.getAttribute("projectId");
								
								projectAccessGrid.selectAllRecords();
								projectAccessGrid.removeSelectedData();
								
								projectStudyGrid.selectAllRecords();
								ListGridRecord[] lgrStudy = projectStudyGrid.getSelectedRecords();
								
								for(int i = 0; i < lgrStudy.length; i++){
								
									lgrStudy[i].setAttribute("projectId", projectId);
								
								}
								
								projectStudyGrid.removeSelectedData();
								
								projectGrid.removeData(lgr);
							}
						}
					});
				
				} else {
					SC.say("Select a project.");
				}
			}});
		
		projectToolStrip.addButton(deleteProjectButton);
		
		
		ToolStripButton addProjectAccessButton = new ToolStripButton();  
		addProjectAccessButton.setTitle("add Project Access");
		if(user.getIsAdmin() == false){
			addProjectAccessButton.setDisabled(true);
		}
		addProjectAccessButton.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				ListGridRecord lgr = projectGrid.getSelectedRecord();
				
				if (lgr != null){
				
					FoProject project = new FoProject(Integer.parseInt(lgr.getAttribute("projectId")),
																		lgr.getAttribute("projectName"),
																		lgr.getAttribute("projectDescription"));
					loadProjectAccessManageWindow(project);
					
				} else {
					SC.say("Select a project.");
				}
			}});
		
		projectToolStrip.addButton(addProjectAccessButton);
		
		ToolStripButton removeProjectAccessButton = new ToolStripButton();  
		removeProjectAccessButton.setTitle("remove Project Access");
		if(user.getIsAdmin() == false){
			removeProjectAccessButton.setDisabled(true);
		}
		removeProjectAccessButton.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				ListGridRecord projectAccessLgr = projectAccessGrid.getSelectedRecord();

				if (projectAccessLgr != null){
					
					projectAccessGrid.removeData(projectAccessLgr);	
				} else {
					SC.say("Select a group.");
				}
			}});
		
		projectToolStrip.addButton(removeProjectAccessButton);
		
		ToolStripButton addStudyToProjectButton = new ToolStripButton();  
		addStudyToProjectButton.setTitle("add study to project");
		if(user.getIsAdmin() == false){
			addStudyToProjectButton.setDisabled(true);
		}
		addStudyToProjectButton.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				
				ListGridRecord lgr = projectGrid.getSelectedRecord();
				
				if (lgr != null){
				
					FoProject project = new FoProject(Integer.parseInt(lgr.getAttribute("projectId")),
																		lgr.getAttribute("projectName"),
																		lgr.getAttribute("projectDescription"));
					loadAddStudyProjectWindow(project);	
				} else {
					SC.say("Select a project.");
				}
			}});
		
		projectToolStrip.addButton(addStudyToProjectButton);
		
		controlsPanel.addMember(projectToolStrip);
		
		headerContainer.addMember(controlsPanel);
		
		pane.addMember(headerContainer);
		
		HLayout gridContainer = new HLayout();
		gridContainer.setWidth100();
		gridContainer.setHeight100();
		
		projectGrid = new ListGrid();
		projectGrid.setWidth("50%");
		projectGrid.setHeight100();  
		projectGrid.setAlternateRecordStyles(true);
		projectGrid.setWrapCells(true);
		projectGrid.setFixedRecordHeights(false);
		projectGrid.setAutoFetchData(false);
		projectGrid.setShowAllRecords(false);
		projectGrid.markForRedraw();
		
		ProjectDS pDS = new ProjectDS();
		
		projectGrid.setDataSource(pDS);
		projectGrid.setFetchOperation(OperationId.PROJECT_FETCH_ALL);
		
		projectGrid.fetchData();
		
		gridContainer.addMember(projectGrid);
		
		projectStudyGrid = new ListGrid();
		projectStudyGrid.setWidth("50%");
		projectStudyGrid.setHeight100();
		projectStudyGrid.setAlternateRecordStyles(true);
		projectStudyGrid.setWrapCells(true);
		projectStudyGrid.setFixedRecordHeights(false);
		projectStudyGrid.setShowAllRecords(false);
		projectStudyGrid.setShowGridSummary(true);
		projectStudyGrid.setAutoFetchData(false);
		projectStudyGrid.markForRedraw();
		
		StudyDS mDS = new StudyDS();
		mDS.getField("cnv").setCanView(false);
		mDS.getField("snp").setCanView(false);
		mDS.getField("transloc").setCanView(false);
		mDS.getField("generic").setCanView(false);
		
		projectStudyGrid.setDataSource(mDS);
		projectStudyGrid.setFetchOperation(OperationId.STUDY_FETCH_FOR_PROJECT);
		
		gridContainer.addMember(projectStudyGrid);
		
		pane.addMember(gridContainer);
		
		if(user.getIsAdmin()){
			projectAccessGrid = new ListGrid();
			projectAccessGrid.setWidth100();
			projectAccessGrid.setHeight("50%");
			projectAccessGrid.setAlternateRecordStyles(true);
			projectAccessGrid.setWrapCells(true);
			projectAccessGrid.setFixedRecordHeights(false);
			projectAccessGrid.setAutoFetchData(false);
			projectAccessGrid.setShowAllRecords(false);
			projectAccessGrid.markForRedraw();
		
			ListGridField lgfProjectAccessId = new ListGridField("projectAccessId", "ID");
			ListGridField lgfProjectAccessGroup = new ListGridField("groupName", "Group");
			ListGridField lgfProjectAccessRight = new ListGridField("accessRight", "Access Right");
		
			projectAccessGrid.setFields(lgfProjectAccessId, lgfProjectAccessGroup, lgfProjectAccessRight);
		
			ProjectAccessDS paDS = new ProjectAccessDS();
			
			projectAccessGrid.setDataSource(paDS);
			
			pane.addMember(projectAccessGrid);
		}
		
		projectGrid.addRecordClickHandler(new MyProjectRecordClickHandler(projectStudyGrid, projectAccessGrid, user));
		
		this.setPane(pane);	
	}
	
	public void loadProjectManageWindow(){
		
		final Window window = new Window();

		window.setTitle("Add Project");
		window.setWidth(250);
		window.setHeight(200);
		window.setAlign(Alignment.CENTER);
		
		window.setAutoCenter(true);
		window.setIsModal(true);
		window.setShowModalMask(true);
		
		DynamicForm projectForm = new DynamicForm();
		projectNameTextItem = new TextItem();
		projectNameTextItem.setTitle("Project Name");
		
		projectDescriptionItem = new TextAreaItem();
		projectDescriptionItem.setTitle("Description");
		projectDescriptionItem.setLength(5000);
		
		ButtonItem addProjectButton = new ButtonItem("Add");
		addProjectButton.setWidth(50);

		addProjectButton.addClickHandler(new com.smartgwt.client.widgets.form.fields.events.ClickHandler(){

			@Override
			public void onClick(
					com.smartgwt.client.widgets.form.fields.events.ClickEvent event) {
				
				ListGridRecord lgr = new ListGridRecord();
				lgr.setAttribute("projectId", 0);
				lgr.setAttribute("projectName",projectNameTextItem.getDisplayValue());
				lgr.setAttribute("projectDescription", projectDescriptionItem.getDisplayValue());
				
				projectGrid.addData(lgr);
				
				window.hide();
			}
		});
		
		projectForm.setItems(projectNameTextItem, projectDescriptionItem, addProjectButton);
	
		window.addItem(projectForm);	
		window.show();
	}
	
	public void loadProjectAccessManageWindow(final FoProject project){
		
		final Window window = new Window();

		window.setTitle("Add project access to project " + project.getName());
		window.setWidth(250);
		window.setHeight(200);
		window.setAlign(Alignment.CENTER);
		
		window.setAutoCenter(true);
		window.setIsModal(true);
		window.setShowModalMask(true);
		
		final DynamicForm projectAccessForm = new DynamicForm();
		
		groupSelectItem = new SelectItem();
        groupSelectItem.setTitle("Group");
        
        GroupDS gDS = new GroupDS();
        groupSelectItem.setOptionDataSource(gDS);
        groupSelectItem.setOptionOperationId(OperationId.GROUP_FETCH_EXCEPT_PROJECT);
        
		Criteria c = new Criteria("projectId", String.valueOf(project.getId()));
		groupSelectItem.setOptionCriteria(c);
		
		groupSelectItem.setDisplayField("groupName");
		groupSelectItem.setValueField("groupId");			
		groupSelectItem.setAutoFetchData(false);
        
		accessRightSelectItem = new SelectItem();
		accessRightSelectItem.setTitle("Access right");
		
		LinkedHashMap<String, String> valueMap = new LinkedHashMap<String, String>();
        valueMap.put("r", "r");
        valueMap.put("rw", "rw");
		
        accessRightSelectItem.setValueMap(valueMap);
        
		ButtonItem addProjectAccessButton = new ButtonItem("Add");
		addProjectAccessButton.setWidth(50);

		addProjectAccessButton.addClickHandler(new com.smartgwt.client.widgets.form.fields.events.ClickHandler(){

			@Override
			public void onClick(
					com.smartgwt.client.widgets.form.fields.events.ClickEvent event) {
				
				ListGridRecord lgr = new ListGridRecord();
				lgr.setAttribute("projectId", project.getId());
				lgr.setAttribute("groupId", groupSelectItem.getValueAsString());
				lgr.setAttribute("accessRight", accessRightSelectItem.getDisplayValue());
				
				projectAccessGrid.addData(lgr);
				
				window.hide();
			}
		});
		
		projectAccessForm.setItems(groupSelectItem, accessRightSelectItem, addProjectAccessButton);
	
		window.addItem(projectAccessForm);
		window.show();
	}

	public void loadAddStudyProjectWindow(final FoProject project){
	
		final Window window = new Window();
		
		window.setTitle("Add study to project " + project.getName());
		window.setWidth(250);
		window.setHeight(200);
		window.setAlign(Alignment.CENTER);
	
		window.setAutoCenter(true);
		window.setIsModal(true);
		window.setShowModalMask(true);
	
		final DynamicForm addStudyForm = new DynamicForm();
	
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
				String notInProjectId = "" + project.getId();
				
				Criteria c = new Criteria("projectId", projectId);
				c.addCriteria("notInProjectId", notInProjectId);
				
				studySelectItem.setPickListCriteria(c);
				
				studySelectItem.setOptionOperationId(OperationId.STUDY_FETCH_NOT_IN_PROJECT);
				
				studySelectItem.fetchData();
			}
		});
		
		projectSelectItem.addDataArrivedHandler(new DataArrivedHandler(){
			
			@Override
			public void onDataArrived(DataArrivedEvent event) {
				
				String projectId = projectSelectItem.getValueAsString();
				String notInProjectId = "" + project.getId();
				
				Criteria c = new Criteria("projectId", projectId);
				c.addCriteria("notInProjectId", notInProjectId);
			
				studySelectItem.setPickListCriteria(c);
				studySelectItem.setOptionOperationId(OperationId.STUDY_FETCH_NOT_IN_PROJECT);
				studySelectItem.fetchData();
			}
		});
		
		studySelectItem = new SelectItem();
		studySelectItem.setTitle("Study");
		studySelectItem.setDisplayField("studyName");
		studySelectItem.setValueField("studyId");
		studySelectItem.setMultiple(true);
		studySelectItem.setMultipleAppearance(MultipleAppearance.PICKLIST);
		studySelectItem.setAutoFetchData(false);	
			
		StudyDS mDS = new StudyDS();
		
		studySelectItem.setOptionDataSource(mDS);
		studySelectItem.setOptionOperationId(OperationId.STUDY_FETCH_NOT_IN_PROJECT);
		
		ButtonItem addStudyButton = new ButtonItem("Add");
		addStudyButton.setWidth(50);
		
		addStudyButton.addClickHandler(new com.smartgwt.client.widgets.form.fields.events.ClickHandler(){
			
			@Override
			public void onClick(
					com.smartgwt.client.widgets.form.fields.events.ClickEvent event) {
				
				ListGridRecord[] lgr = studySelectItem.getSelectedRecords();
				
				String projectId = "" + project.getId();
				
				projectStudyGrid.setAddOperation(OperationId.STUDY_ADD_TO_PROJECT);
				
				for(int i = 0; i < lgr.length; i++){
					
					lgr[i].setAttribute("projectId", projectId);
					projectStudyGrid.addData(lgr[i]);	
				}	
				window.hide();
			}
		});
		
		addStudyForm.setItems(projectSelectItem, studySelectItem, addStudyButton);
		
		window.addItem(addStudyForm);
		window.show();
	}
}