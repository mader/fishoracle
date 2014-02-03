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

import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.util.SC;

import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.PasswordItem;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

import de.unihamburg.zbh.fishoracle.client.datasource.OperationId;
import de.unihamburg.zbh.fishoracle.client.datasource.UserDS;

public class UserAdminTab extends Tab {

	private ListGrid userGrid;
	private DynamicForm pwForm;
	private PasswordItem newPwPasswordItem;
	
	public UserAdminTab(){
      	
		this.setTitle("Users");
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
		
		ToolStrip userToolStrip = new ToolStrip();
		userToolStrip.setWidth100();
		
		UserDS uDS = new UserDS();
		
		ToolStripButton changePwButton = new ToolStripButton();  
		changePwButton.setTitle("Change Password");
		changePwButton.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				
				ListGridRecord lgr = userGrid.getSelectedRecord();
				
				UserDS uDS = (UserDS) userGrid.getDataSource();
				
				if (lgr != null){
				
					loadSetPwWindow(Integer.parseInt(userGrid.getSelectedRecord().getAttribute("userId")),
						userGrid.getSelectedRecord().getAttribute("userName"), uDS);
					
				} else {
					SC.say("Select a user.");
				}
			}});
		
		userToolStrip.addButton(changePwButton);
		
		controlsPanel.addMember(userToolStrip);
		
		headerContainer.addMember(controlsPanel);
		
		pane.addMember(headerContainer);
		
		HLayout gridContainer = new HLayout();
		gridContainer.setWidth100();
		gridContainer.setHeight100();
		
		userGrid = new ListGrid();
		userGrid.setWidth100();
		userGrid.setHeight100();
		userGrid.setAlternateRecordStyles(true);
		userGrid.setWrapCells(true);
		userGrid.setFixedRecordHeights(false);
		userGrid.setEditByCell(true);
		userGrid.setAutoSaveEdits(false);
		
		userGrid.setShowAllRecords(false);
		userGrid.setAutoFetchData(false);
		
		userGrid.setDataSource(uDS);
		userGrid.setFetchOperation(OperationId.USER_FETCH_ALL);
		userGrid.fetchData();
		
		ListGridField lgfId = new ListGridField("userId", "User ID");
		lgfId.setCanEdit(false);
		ListGridField lgfFirstName = new ListGridField("firstName", "First Name");
		lgfFirstName.setCanEdit(false);
		ListGridField lgfLastName = new ListGridField("lastName", "Last Name");
		lgfFirstName.setCanEdit(false);
		ListGridField lgfUserName = new ListGridField("userName", "Username");
		lgfUserName.setCanEdit(false);
		ListGridField lgfEmail = new ListGridField("email", "E-Mail");
		lgfEmail.setCanEdit(false);
		ListGridField lgfIsActive = new ListGridField("isActive", "Activated");
		lgfIsActive.setCanEdit(true);
		
		lgfIsActive.addChangedHandler(new com.smartgwt.client.widgets.grid.events.ChangedHandler(){

			@Override
			public void onChanged(
					com.smartgwt.client.widgets.grid.events.ChangedEvent event) {
				
				ListGridRecord lgr = userGrid.getSelectedRecord();
				userGrid.setUpdateOperation(OperationId.USER_UPDATE_ISACTIVE);
				userGrid.updateData(lgr);
			}
		});
		
		ListGridField lgfisAdmin = new ListGridField("isAdmin", "Administator");
		lgfisAdmin.setCanEdit(true);
		lgfisAdmin.addChangedHandler(new com.smartgwt.client.widgets.grid.events.ChangedHandler(){

			@Override
			public void onChanged(
					com.smartgwt.client.widgets.grid.events.ChangedEvent event) {
				
				ListGridRecord lgr = userGrid.getSelectedRecord();
				userGrid.setUpdateOperation(OperationId.USER_UPDATE_ISADMIN);
				userGrid.updateData(lgr);
			}
		});
		
		userGrid.setFields(lgfId, lgfFirstName, lgfLastName, lgfUserName, lgfEmail, lgfIsActive, lgfisAdmin);
		
		gridContainer.addMember(userGrid);
		
		pane.addMember(gridContainer);
		
		this.setPane(pane);
	}
	
	public void loadSetPwWindow(final int userId, String username, UserDS uDS){
		
		final Window window = new Window();

		window.setTitle("Set password for " + username);
		window.setWidth(250);
		window.setHeight(100);
		window.setAlign(Alignment.CENTER);
		
		window.setAutoCenter(true);
		window.setIsModal(true);
		window.setShowModalMask(true);
		
		pwForm = new DynamicForm();
		pwForm.setUseAllDataSourceFields(true);
		pwForm.setDataSource(uDS);
		
		uDS.getField("userId").setHidden(true);
		uDS.getField("userName").setHidden(true);
		uDS.getField("firstName").setHidden(true);
		uDS.getField("lastName").setHidden(true);
		uDS.getField("email").setHidden(true);
		
		
		pwForm.editSelectedData(userGrid);
		
		newPwPasswordItem = new PasswordItem();
		newPwPasswordItem.setName("pw");
		
		ButtonItem submitPwButton = new ButtonItem("submit");
		submitPwButton.setWidth(50);
		
		submitPwButton.addClickHandler(new com.smartgwt.client.widgets.form.fields.events.ClickHandler(){

			@Override
			public void onClick(
					com.smartgwt.client.widgets.form.fields.events.ClickEvent event) {
				
				pwForm.setUpdateOperation(OperationId.USER_UPDATE_PASSWORD_ADMIN);
				pwForm.saveData();
				
				window.hide();
			}
			
		});

		pwForm.setItems(newPwPasswordItem, submitPwButton);
	
		window.addItem(pwForm);
		
		window.show();
	}
}
