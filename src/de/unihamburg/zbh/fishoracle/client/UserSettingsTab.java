/*
  Copyright (c) 2013-2014 Malte Mader <mader@zbh.uni-hamburg.de>
  Copyright (c) 2013-2014 Center for Bioinformatics, University of Hamburg

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

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.Side;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.PasswordItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.validator.MatchesFieldValidator;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.RecordClickEvent;
import com.smartgwt.client.widgets.grid.events.RecordClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

import de.unihamburg.zbh.fishoracle.client.data.FoGroup;
import de.unihamburg.zbh.fishoracle.client.datasource.ConfigDS;
import de.unihamburg.zbh.fishoracle.client.datasource.GroupDS;
import de.unihamburg.zbh.fishoracle.client.datasource.OperationId;
import de.unihamburg.zbh.fishoracle.client.datasource.UserDS;
import de.unihamburg.zbh.fishoracle.client.rpc.Admin;
import de.unihamburg.zbh.fishoracle.client.rpc.AdminAsync;

public class UserSettingsTab extends Tab{

	private MainPanel mp;
	
	private DynamicForm userProfileForm;
	private DynamicForm userPwForm;
	private TextItem userIdTextItem;
	private TextItem userNameTextItem;
	private TextItem userFirstNameTextItem;
	private TextItem userLastNameTextItem;
	private TextItem userEmailTextItem;
	private PasswordItem userPwItem;
	private PasswordItem userPwConfirmItem;
	
	private ListGrid groupGrid;
	
	private ListGrid configGrid;
	
	public UserSettingsTab(MainPanel mp){
		
		this.mp = mp;
		
		this.setTitle("User Settings");
		this.setCanClose(true);
		
		TabSet settingsTabSet = new TabSet();  
        settingsTabSet.setTabBarPosition(Side.TOP);  
        settingsTabSet.setWidth100();  
        settingsTabSet.setHeight100(); 
        
        settingsTabSet.addTab(createUserProfileTab());
        settingsTabSet.addTab(createConfigsTab());
        settingsTabSet.addTab(createGroupsTab());
        //settingsTabSet.addTab(createStudiesTab());
        
		this.setPane(settingsTabSet);
	}
	
	private Tab createUserProfileTab() {
		
		Tab userProfileTab = new Tab("Profile");
		
		VLayout pane = new VLayout();
		pane.setWidth100();
		pane.setHeight100();
		pane.setDefaultLayoutAlign(Alignment.CENTER);
		
		HLayout header = new HLayout();
		header.setAutoWidth();
		header.setAutoHeight();
		
		Label headerLbl = new Label("<h2>Profile</h2>");
		headerLbl.setWrap(false);
		header.addMember(headerLbl);
		
		pane.addMember(header);
		
		UserDS uDS = new UserDS();
		uDS.getField("userId").setHidden(false);
		
		userProfileForm = new DynamicForm();
		userProfileForm.setWidth(250);
		userProfileForm.setHeight(260);
		userProfileForm.setAlign(Alignment.CENTER);

		userProfileForm.setDataSource(uDS);
		
		userIdTextItem = new TextItem();
		userIdTextItem.setName("userId");
		userIdTextItem.setRequired(false);
		userIdTextItem.setDisabled(true);
		
		userNameTextItem = new TextItem();
		userNameTextItem.setName("userName");
		userNameTextItem.setRequired(false);
		userNameTextItem.setDisabled(true);
		
		userFirstNameTextItem = new TextItem();
		userFirstNameTextItem.setName("firstName");
		
		userLastNameTextItem = new TextItem();
		userLastNameTextItem.setName("lastName");
		
		userEmailTextItem = new TextItem();
		userEmailTextItem.setName("email");
		
		
		userProfileForm.setFetchOperation(OperationId.USER_FETCH_PROFILE);
		userProfileForm.fetchData();
		
		ButtonItem updateProfile = new ButtonItem();
		updateProfile.setTitle("Update Profile");
		updateProfile.addClickHandler(new com.smartgwt.client.widgets.form.fields.events.ClickHandler(){

			@Override
			public void onClick(
					com.smartgwt.client.widgets.form.fields.events.ClickEvent event) {
				
				if(userFirstNameTextItem.validate() && userLastNameTextItem.validate() && userEmailTextItem.validate()){
					
					userProfileForm.setUpdateOperation(OperationId.USER_UPDATE_PROFILE);
					userProfileForm.saveData();
				}
			}});
		
		
		userProfileForm.setItems(userIdTextItem,
								userNameTextItem,
								userFirstNameTextItem,
								userLastNameTextItem,
								userEmailTextItem,
								updateProfile);
		
		userPwForm = new DynamicForm();
		userPwForm.setWidth(250);
		userPwForm.setHeight(260);
		userPwForm.setAlign(Alignment.CENTER);
		userPwForm.setUseAllDataSourceFields(true);
		userPwForm.setDataSource(uDS);
		uDS.getField("userId").setHidden(true);
		uDS.getField("userName").setHidden(true);
		uDS.getField("firstName").setHidden(true);
		uDS.getField("lastName").setHidden(true);
		uDS.getField("email").setHidden(true);
		
		userPwItem = new PasswordItem();
		userPwItem.setName("pw");
		
		userPwConfirmItem = new PasswordItem();
		userPwConfirmItem.setTitle("Confirm Password");
		userPwConfirmItem.setRequired(true);
		
		MatchesFieldValidator matchesValidator = new MatchesFieldValidator();  
		matchesValidator.setOtherField("pw");
		matchesValidator.setErrorMessage("Passwords do not match!");
		userPwConfirmItem.setValidators(matchesValidator);  
		
		userPwForm.setFetchOperation(OperationId.USER_FETCH_PROFILE);
		userPwForm.fetchData();
		
		ButtonItem updatePassword = new ButtonItem();
		updatePassword.setTitle("Set Password");
		updatePassword.addClickHandler(new com.smartgwt.client.widgets.form.fields.events.ClickHandler(){

			@Override
			public void onClick(
					com.smartgwt.client.widgets.form.fields.events.ClickEvent event) {
				
				
				if(userPwConfirmItem.validate()){
					
					userPwForm.setUpdateOperation(OperationId.USER_UPDATE_PASSWORD);
					userPwForm.saveData();
				}
			}});
		
		userPwForm.setItems(userPwItem,
							userPwConfirmItem,
							updatePassword);
		
		pane.addMember(userProfileForm);
		
		pane.addMember(userPwForm);
		
		userProfileTab.setPane(pane);
		
		return userProfileTab;
	}
	
	private Tab createConfigsTab(){
		Tab userConfigsTab = new Tab("Configurations");
		
		VLayout pane = new VLayout();
		pane.setWidth100();
		pane.setHeight100();
		pane.setDefaultLayoutAlign(Alignment.CENTER);
		
		HLayout gridContainer = new HLayout();
		gridContainer.setWidth100();
		
		ConfigLayout cl = new ConfigLayout(mp, true);
		boolean globalTh = (Boolean)  cl.getGlobalThresholdCheckbox().getValue();
		cl.addTrack(null, globalTh, true, 1);
		
		ToolStrip configToolStrip = new ToolStrip();
		configToolStrip.setWidth100();
		
		ToolStripButton updateConfigButton = new ToolStripButton();
		updateConfigButton.setTitle("Update Configuration");
		updateConfigButton.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {

				SC.say("Hello!");
			}});
		
		//configToolStrip.addButton(updateConfigButton);
		
		ToolStripButton deleteConfigButton = new ToolStripButton();
		deleteConfigButton.setTitle("Delete Configuration");
		
		configToolStrip.addButton(deleteConfigButton);
		
		configGrid = new ListGrid();
		configGrid.setWidth("50%");
		configGrid.setHeight100();
		configGrid.setShowAllRecords(true);
		configGrid.setAlternateRecordStyles(true);
		configGrid.setWrapCells(true);
		configGrid.setFixedRecordHeights(false);
		configGrid.markForRedraw();
		
        ConfigDS cDS = new ConfigDS();
		
		configGrid.setDataSource(cDS);
		configGrid.setFetchOperation(OperationId.ORGAN_FETCH_ALL);
		
		configGrid.fetchData();
		
		gridContainer.addMember(configGrid);
		
		configGrid.addRecordClickHandler(new MyConfigRecordClickHandler(cl));
		
		deleteConfigButton.addClickHandler(new MyConfigDeleteClickHandler(cl, configGrid));
		
		gridContainer.addMember(cl);
		
		pane.addMember(configToolStrip);
		
		pane.addMember(gridContainer);
		
		userConfigsTab.setPane(pane);
		
		return userConfigsTab;
	}
	
	private Tab createGroupsTab(){
		Tab userGroupsTab = new Tab("Groups");
		
		VLayout pane = new VLayout();
		pane.setWidth100();
		pane.setHeight100();
		pane.setDefaultLayoutAlign(Alignment.CENTER);
		
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
		groupGrid.setFetchOperation(OperationId.GROUP_FETCH_FOR_USER);
		groupGrid.setUseAllDataSourceFields(true);
		
		groupGrid.fetchData();
		
		gridContainer.addMember(groupGrid);
		
		pane.addMember(gridContainer);
		
		userGroupsTab.setPane(pane);
		
		return userGroupsTab;
	}
	
	private Tab createStudiesTab(){
		Tab userStudiesTab = new Tab("Studies");
		return userStudiesTab;
	}
	
	public void getAllGroupsForUser(){
		
		final AdminAsync req = (AdminAsync) GWT.create(Admin.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) req;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "AdminService";
		endpoint.setServiceEntryPoint(moduleRelativeURL);
		final AsyncCallback<FoGroup[]> callback = new AsyncCallback<FoGroup[]>(){
			
			public void onSuccess(FoGroup[] result){
				
				FoGroup[] groups = result;
								
				ListGridRecord[] lgr = new ListGridRecord[groups.length];
				
				for(int i=0; i < groups.length; i++){
					lgr[i] = new ListGridRecord();
					lgr[i].setAttribute("groupId", groups[i].getId());
					lgr[i].setAttribute("groupName", groups[i].getName());
					lgr[i].setAttribute("isactive", groups[i].isIsactive());
				}

				groupGrid.setData(lgr);
				
			}
			public void onFailure(Throwable caught){
				SC.say(caught.getMessage());
			}

		};
		req.getAllGroupsForUser(callback);
	}
}

class MyConfigRecordClickHandler implements RecordClickHandler {
	
	private ConfigLayout cl;
	
	public MyConfigRecordClickHandler(ConfigLayout cl){
		this.cl = cl;
	}

	@Override
	public void onRecordClick(RecordClickEvent event) {
		String configId = event.getRecord().getAttribute("configId");
		cl.loadConfigData(Integer.parseInt(configId));
	}	
}

class MyConfigDeleteClickHandler implements ClickHandler {
	
	private ConfigLayout cl;
	private ListGrid cg;
	
	public MyConfigDeleteClickHandler(ConfigLayout cl, ListGrid configGrid){
		this.cl = cl;
		this.cg = configGrid;
	}

	@Override
	public void onClick(ClickEvent event) {
		final ListGridRecord lgr = cg.getSelectedRecord();
		
		if(lgr != null) {
		
			SC.confirm("Do you really want to delete " + lgr.getAttribute("configName") + "?", new BooleanCallback(){

				@Override
				public void execute(Boolean value) {
					if(value != null && value){
						
						cg.removeData(lgr);
						cl.reset();
						cl.getMp().getWestPanel().getSearchContent().getConfigSelectItem().invalidateDisplayValueCache();
						cl.getMp().getWestPanel().getSearchContent().getConfigSelectItem().fetchData();
					}
				}
			});		
		} else {
			SC.say("Select a configuration.");
		}
	}	
}