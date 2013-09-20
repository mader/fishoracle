package de.unihamburg.zbh.fishoracle.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.Side;
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
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

import de.unihamburg.zbh.fishoracle.client.data.FoGroup;
import de.unihamburg.zbh.fishoracle.client.datasource.ConfigDS;
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
		
		HLayout toolbarContainer = new HLayout();
		toolbarContainer.setWidth100();
		toolbarContainer.setHeight("2%");
		
		HLayout gridContainer = new HLayout();
		gridContainer.setWidth100();
		
		ToolStrip configToolStrip = new ToolStrip();
		configToolStrip.setWidth100();
		
		ToolStripButton updateConfigButton = new ToolStripButton();
		updateConfigButton.setTitle("Update Configuration");
		updateConfigButton.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {

				SC.say("Hello!");
			}});
		
		configToolStrip.addButton(updateConfigButton);
		
		ToolStripButton deleteConfigButton = new ToolStripButton();
		deleteConfigButton.setTitle("Delete Configuration");
		deleteConfigButton.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {

				SC.say("Hello!");
			}});
		
		configToolStrip.addButton(updateConfigButton);
		configToolStrip.addButton(deleteConfigButton);
		
		toolbarContainer.setMembers(configToolStrip);
		
		//grid showing saved configs
		
		ListGrid configGrid;
		
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
		
		
		//config layout
		
		ConfigLayout cl = new ConfigLayout(mp, true);
		
		gridContainer.addMember(cl);
		
		pane.addMember(toolbarContainer);
		
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
		
		ListGridField lgfGroupId = new ListGridField("groupId", "group ID");
		ListGridField lgfGroupName = new ListGridField("groupName", "Group Name");
		ListGridField lgfGroupActivated = new ListGridField("isactive", "Activated");
		
		groupGrid.setFields(lgfGroupId, lgfGroupName, lgfGroupActivated);
		
		getAllGroupsForUser();
		
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
