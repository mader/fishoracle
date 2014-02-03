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
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

import de.unihamburg.zbh.fishoracle.client.data.FoGroup;
import de.unihamburg.zbh.fishoracle.client.datasource.GroupDS;
import de.unihamburg.zbh.fishoracle.client.datasource.OperationId;
import de.unihamburg.zbh.fishoracle.client.datasource.UserDS;

public class GroupAdminTab extends Tab {

	private ListGrid groupGrid;
	private ListGrid groupUserGrid;
	
	private DynamicForm userToGroupForm;
	private TextItem groupNameTextItem;
	
	private SelectItem userSelectItem;
	
	
	public GroupAdminTab(){
		
		this.setTitle("Manage Groups");
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
		
		ToolStrip groupToolStrip = new ToolStrip();
		groupToolStrip.setWidth100();
		
		ToolStripButton addGroupButton = new ToolStripButton();  
		addGroupButton.setTitle("add Group");
		addGroupButton.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				loadGroupManageWindow();
			}});
		
		groupToolStrip.addButton(addGroupButton);
		
		ToolStripButton deleteGroupButton = new ToolStripButton();  
		deleteGroupButton.setTitle("delete Group");
		deleteGroupButton.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				
				final ListGridRecord lgr = groupGrid.getSelectedRecord();
				
				if (lgr != null){
				
					SC.confirm("Do you really want to delete " + lgr.getAttribute("groupName") + "?", new BooleanCallback(){

						@Override
						public void execute(Boolean value) {
							if(value != null && value){
				
								final ListGridRecord lgr = groupGrid.getSelectedRecord();
								groupGrid.setRemoveOperation(OperationId.GROUP_REMOVE);
								groupGrid.removeData(lgr);
							}
						}
					});
				} else {
					SC.say("Select a group.");
				}
			}});
		
		groupToolStrip.addButton(deleteGroupButton);
		
		ToolStripButton addUserGroupButton = new ToolStripButton();  
		addUserGroupButton.setTitle("add User to Group");
		addUserGroupButton.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				ListGridRecord lgr = groupGrid.getSelectedRecord();
				
				if (lgr != null){
					
					FoGroup group = new FoGroup(Integer.parseInt(lgr.getAttribute("groupId")),
															lgr.getAttribute("groupName"),
															Boolean.parseBoolean(lgr.getAttribute("isactive")));
				
					loadUserToGroupWindow(group);
				} else {
					SC.say("Select a group.");
				}
		}});
		
		groupToolStrip.addButton(addUserGroupButton);
		
		ToolStripButton removeUserGroupButton = new ToolStripButton();
		removeUserGroupButton.setTitle("remove User from Group");
		removeUserGroupButton.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				ListGridRecord lgr = groupUserGrid.getSelectedRecord();
				
				if (lgr != null){
					ListGridRecord groupLgr = groupGrid.getSelectedRecord();
					lgr.setAttribute("groupId", groupLgr.getAttribute("groupId"));
				
					groupGrid.setRemoveOperation(OperationId.GROUP_REMOVE_USER);
					groupGrid.removeData(lgr);
				
					groupUserGrid.invalidateCache();
					groupUserGrid.fetchData(new Criteria("groupId", groupLgr.getAttribute("groupId")));
				} else {
					SC.say("Select a group and an user.");
				}
		}});
		
		groupToolStrip.addButton(removeUserGroupButton);
		
		controlsPanel.addMember(groupToolStrip);
		
		headerContainer.addMember(controlsPanel);
		
		pane.addMember(headerContainer);
		
		HLayout gridContainer = new HLayout();
		gridContainer.setWidth100();
		gridContainer.setHeight100();
		
		groupGrid = new ListGrid();
		groupGrid.setWidth("50%");
		groupGrid.setHeight100();
		groupGrid.setShowAllRecords(true);
		groupGrid.setAlternateRecordStyles(true);
		groupGrid.setWrapCells(true);
		groupGrid.setFixedRecordHeights(false);
		groupGrid.markForRedraw();
		
		GroupDS gDS = new GroupDS();
		
		groupGrid.setDataSource(gDS);
		groupGrid.setFetchOperation(OperationId.GROUP_FETCH_ALL);
		
		groupGrid.fetchData();
		
		gridContainer.addMember(groupGrid);
		
		groupUserGrid = new ListGrid();
		groupUserGrid.setWidth("50%");
		groupUserGrid.setHeight100();
		groupUserGrid.setShowAllRecords(true);  
		groupUserGrid.setAlternateRecordStyles(true);
		groupUserGrid.setWrapCells(true);
		groupUserGrid.setFixedRecordHeights(false);
		groupUserGrid.markForRedraw();
		
		UserDS uDS = new UserDS();
		uDS.getField("firstName").setCanView(false);
		uDS.getField("lastName").setCanView(false);
		uDS.getField("email").setCanView(false);
		uDS.getField("pw").setCanView(false);
		uDS.getField("isActive").setCanView(false);
		uDS.getField("isAdmin").setCanView(false);
		
		groupUserGrid.setDataSource(uDS);
		groupUserGrid.setFetchOperation(OperationId.USER_FETCH_FOR_GROUP);
		
		gridContainer.addMember(groupUserGrid);
		
		pane.addMember(gridContainer);
		
		groupGrid.addRecordClickHandler(new MyGroupRecordClickHandler(groupUserGrid));
		
		this.setPane(pane);
	}
	
	public void loadGroupManageWindow(){
		
		final Window window = new Window();

		window.setTitle("Add Group");
		window.setWidth(250);
		window.setHeight(100);
		window.setAlign(Alignment.CENTER);
		
		window.setAutoCenter(true);
		window.setIsModal(true);
		window.setShowModalMask(true); 
		
		DynamicForm groupForm = new DynamicForm();
		groupNameTextItem = new TextItem();
		groupNameTextItem.setTitle("Group Name");
		
		ButtonItem addGroupButton = new ButtonItem("Add");
		addGroupButton.setWidth(50);
		
		addGroupButton.addClickHandler(new com.smartgwt.client.widgets.form.fields.events.ClickHandler(){

			@Override
			public void onClick(
					com.smartgwt.client.widgets.form.fields.events.ClickEvent event) {
				
				ListGridRecord lgr = new ListGridRecord();
				lgr.setAttribute("groupName", groupNameTextItem.getDisplayValue());
				groupGrid.setAddOperation(OperationId.GROUP_ADD);
				groupGrid.addData(lgr);
				window.hide();
			}
			
		});

		groupForm.setItems(groupNameTextItem, addGroupButton);
	
		window.addItem(groupForm);
		
		window.show();
	}
	
public void loadUserToGroupWindow(final FoGroup foGroup){
		
		final Window window = new Window();
		
		window.setTitle("Add User to Group: " + foGroup.getName());
		window.setWidth(250);
		window.setHeight(120);
		window.setAlign(Alignment.CENTER);
		
		window.setAutoCenter(true);
		window.setIsModal(true);
		window.setShowModalMask(true);
		
		userToGroupForm = new DynamicForm();
		
		GroupDS gDS = new GroupDS();
		
		TextItem groupIdTextItem = new TextItem();
		groupIdTextItem.setName("groupId");
		groupIdTextItem.setValue(foGroup.getId());
		groupIdTextItem.setDisabled(true);
		
		userToGroupForm.setDataSource(gDS);
		userToGroupForm.setAddOperation((OperationId.GROUP_ADD_USER));
		
		userSelectItem = new SelectItem();
        userSelectItem.setTitle("User");
        
        UserDS uDS = new UserDS();
		
        userSelectItem.setOptionDataSource(uDS);
        userSelectItem.setOptionOperationId(OperationId.USER_FETCH_EXCEPT_GROUP);
        Criteria c = new Criteria("groupId", String.valueOf(foGroup.getId()));
        userSelectItem.setOptionCriteria(c);
        
        userSelectItem.setDisplayField("userName");
        userSelectItem.setValueField("userId");	
        
        userSelectItem.setAutoFetchData(false);
        userSelectItem.setName("userId");
        
        ButtonItem addUserToGroupButton = new ButtonItem("Add");
		addUserToGroupButton.setWidth(50);
		
		addUserToGroupButton.addClickHandler(new com.smartgwt.client.widgets.form.fields.events.ClickHandler(){

			@Override
			public void onClick(
					com.smartgwt.client.widgets.form.fields.events.ClickEvent event) {

				userToGroupForm.setAddOperation(OperationId.GROUP_ADD_USER);
				userToGroupForm.saveData();
				groupUserGrid.invalidateCache();
				groupUserGrid.fetchData(new Criteria("groupId", String.valueOf(foGroup.getId())));
				
				window.hide();
			}
		});
		
		userToGroupForm.setItems(groupIdTextItem, userSelectItem, addUserToGroupButton);
		
		window.addItem(userToGroupForm);
		
		window.show();
	}
}
