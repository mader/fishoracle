package de.unihamburg.zbh.fishoracle.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;

import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.CheckboxItem;
import com.smartgwt.client.widgets.form.fields.LinkItem;
import com.smartgwt.client.widgets.form.fields.PasswordItem;
import com.smartgwt.client.widgets.form.fields.RadioGroupItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ClickHandler;
import com.smartgwt.client.widgets.form.fields.events.KeyPressEvent;
import com.smartgwt.client.widgets.form.fields.events.KeyPressHandler;
import com.smartgwt.client.widgets.layout.SectionStack;
import com.smartgwt.client.widgets.layout.SectionStackSection;
import com.smartgwt.client.widgets.layout.VLayout;

import de.unihamburg.zbh.fishoracle.client.data.CopyNumberChange;
import de.unihamburg.zbh.fishoracle.client.data.GWTImageInfo;
import de.unihamburg.zbh.fishoracle.client.data.QueryInfo;
import de.unihamburg.zbh.fishoracle.client.data.User;
import de.unihamburg.zbh.fishoracle.client.rpc.Admin;
import de.unihamburg.zbh.fishoracle.client.rpc.AdminAsync;
import de.unihamburg.zbh.fishoracle.client.rpc.Search;
import de.unihamburg.zbh.fishoracle.client.rpc.SearchAsync;
import de.unihamburg.zbh.fishoracle.client.rpc.UserService;
import de.unihamburg.zbh.fishoracle.client.rpc.UserServiceAsync;

public class WestPanel extends SectionStack{


	private MainPanel mp = null;
	private WestPanel wp = this;

	private TextItem searchTextItem;
	private RadioGroupItem SearchRadioGroupItem;
	private RadioGroupItem SearchPriorityRadioGroupItem;
	private CheckboxItem AmpliconFilterCheckboxItem;
	private CheckboxItem DeliconFilterCheckboxItem;
	
	public WestPanel(MainPanel mainPanel) {
		
		mp = mainPanel;

		SectionStackSection searchSection = new SectionStackSection();  
		searchSection.setTitle("Search");  
		searchSection.setExpanded(true);
		
		VLayout searchContent = new VLayout();
		
		/*basic search options*/
		DynamicForm searchForm = new DynamicForm();
		
		searchTextItem = new TextItem();  
		searchTextItem.setTitle("Search");
		
		SearchRadioGroupItem = new RadioGroupItem();  
		SearchRadioGroupItem.setTitle("Type");  
		SearchRadioGroupItem.setValueMap("Amplicon/Delicon Stable ID", "Gene", "Karyoband"); 
		SearchRadioGroupItem.setDefaultValue("Gene");
		
		ButtonItem searchButton = new ButtonItem("Search");
		searchButton.setAlign(Alignment.CENTER);
		searchButton.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(
					com.smartgwt.client.widgets.form.fields.events.ClickEvent event) {
				startSearch();
				
			}
		});
		
		searchTextItem.addKeyPressHandler(new KeyPressHandler(){
			
			@Override
			public void onKeyPress(KeyPressEvent event) {
				if(event.getKeyName().equals("Enter")){
					startSearch();
				}
			}
		});
		
		
		/*search priority*/
		SearchPriorityRadioGroupItem = new RadioGroupItem();  
		SearchPriorityRadioGroupItem.setTitle("Search Priority");  
		SearchPriorityRadioGroupItem.setValueMap("Amplicon", "Delicon"); 
		SearchPriorityRadioGroupItem.setDefaultValue("Amplicon");
		
		/*display filter*/
		AmpliconFilterCheckboxItem = new CheckboxItem();  
		AmpliconFilterCheckboxItem.setTitle("Show Amplicons");
		AmpliconFilterCheckboxItem.setDefaultValue(true);
		
		DeliconFilterCheckboxItem = new CheckboxItem();  
		DeliconFilterCheckboxItem.setTitle("Show Delicons");
		DeliconFilterCheckboxItem.setDefaultValue(true);
		
		/*show more information*/
		LinkItem AmpliconLinkItem = new LinkItem();   
		AmpliconLinkItem.setLinkTitle("show all amplicons");
		AmpliconLinkItem.addClickHandler(new ClickHandler(){
			@Override
			public void onClick(
					com.smartgwt.client.widgets.form.fields.events.ClickEvent event) {
				wp.fetchCncData(true);
			}
		});
		AmpliconLinkItem.setShowTitle(false);
		
		LinkItem DeliconLinkItem = new LinkItem();   
		DeliconLinkItem.setLinkTitle("show all delicons");
		DeliconLinkItem.addClickHandler(new ClickHandler(){
			@Override
			public void onClick(
					com.smartgwt.client.widgets.form.fields.events.ClickEvent event) {
				wp.fetchCncData(false);
			}
		});
		DeliconLinkItem.setShowTitle(false);
		
		/*add content to search stack section*/
		searchForm.setItems(searchTextItem, SearchRadioGroupItem, searchButton, SearchPriorityRadioGroupItem, AmpliconFilterCheckboxItem,
							DeliconFilterCheckboxItem, AmpliconLinkItem, AmpliconLinkItem);
		searchContent.addMember(searchForm);
		
		searchSection.setItems(searchContent);
		
		SectionStackSection settingsSection = new SectionStackSection();  
		settingsSection.setTitle("Settings");  
		
		VLayout settingsContent = new VLayout();
		
		/*user login*/
		DynamicForm userForm = new DynamicForm();
		
		TextItem userTextItem = new TextItem();  
		userTextItem.setTitle("Username");
		userTextItem.setRequired(true);
		
		PasswordItem passwordItem = new PasswordItem();  
		passwordItem.setTitle("Password");  
		passwordItem.setRequired(true);  
		
		ButtonItem logInButton = new ButtonItem("login"); 
		
		ButtonItem logOutButton = new ButtonItem("logout");
		
		ButtonItem registerButton = new ButtonItem("register");
		
		userForm.setItems(userTextItem, passwordItem, logInButton, logOutButton, registerButton);
		
		settingsContent.addMember(userForm);
		
		settingsSection.addItem(settingsContent);
		
		SectionStackSection adminSection = new SectionStackSection();  
		adminSection.setTitle("Admin");  
		
		VLayout adminContent = new VLayout();
		
		/*administration settings*/
		DynamicForm adminForm = new DynamicForm();
		
		LinkItem usersLinkItem = new LinkItem();   
		usersLinkItem.setLinkTitle("Show users");
		usersLinkItem.setShowTitle(false);
		
		LinkItem dataLinkItem = new LinkItem();  
		dataLinkItem.setLinkTitle("Add data");
		dataLinkItem.setShowTitle(false);
		
		adminForm.setItems(usersLinkItem, dataLinkItem);
		
		adminContent.addMember(adminForm);
		
		adminSection.addItem(adminContent);
		
		this.setSections(searchSection, settingsSection, adminSection);
			
	}
	
	public void startSearch(){
		String typeStr = null;
		String cncPrio = null;
		
		if(searchTextItem.getDisplayValue().equals("")){
			SC.say("You have to type in a search term!");
		} else {
		
			if(SearchRadioGroupItem.getDisplayValue().equalsIgnoreCase("Amplicon/Delicon Stable ID")){
				typeStr = "Amplicon/Delicon Search";
			}
			if(SearchRadioGroupItem.getDisplayValue().equalsIgnoreCase("Gene")){
				typeStr = "Gene Search";
			}
			if(SearchRadioGroupItem.getDisplayValue().equalsIgnoreCase("Karyoband")){
				typeStr = "Band Search";
			}
			if(SearchPriorityRadioGroupItem.getDisplayValue().equalsIgnoreCase("Amplicon")){
				cncPrio = "Amplicon";
			}
			if(SearchPriorityRadioGroupItem.getDisplayValue().equalsIgnoreCase("Delicon")){
				cncPrio = "Delicon";
			}
						
			QueryInfo newQuery = new QueryInfo(searchTextItem.getDisplayValue(),
												typeStr, cncPrio,
												AmpliconFilterCheckboxItem.getValueAsBoolean(),
												DeliconFilterCheckboxItem.getValueAsBoolean(),
												mp.getCenterPanel().getWidth() - 30); 
			
			//MessageBox.wait("Searching for " + searchBox.getText());
			search(newQuery);
		}
	}
	
	/*=============================================================================
	 *||                              RPC Calls                                  ||
	 *=============================================================================
	 * */	
		
		
	public void search(QueryInfo q){
			
			final SearchAsync req = (SearchAsync) GWT.create(Search.class);
			ServiceDefTarget endpoint = (ServiceDefTarget) req;
			String moduleRelativeURL = GWT.getModuleBaseURL() + "Search";
			endpoint.setServiceEntryPoint(moduleRelativeURL);
			final AsyncCallback<GWTImageInfo> callback = new AsyncCallback<GWTImageInfo>(){
				public void onSuccess(GWTImageInfo result){
					
					//MessageBox.hide();
					mp.getCenterPanel().newImageTab(result);				
					
				}
				public void onFailure(Throwable caught){

					//MessageBox.hide();
					SC.say(caught.getMessage());
					
				}
			};
			req.generateImage(q, callback);
		}
	
	public void fetchCncData(final boolean isAmplicon){
		
		final SearchAsync req = (SearchAsync) GWT.create(Search.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) req;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "Search";
		endpoint.setServiceEntryPoint(moduleRelativeURL);
		final AsyncCallback<CopyNumberChange[]> callback = new AsyncCallback<CopyNumberChange[]>(){
			public void onSuccess(CopyNumberChange[] result){
				
				//MessageBox.hide();
				mp.getCenterPanel().newDataTab(result, isAmplicon);

			}
			public void onFailure(Throwable caught){
				System.out.println(caught.getMessage());
				//MessageBox.hide();
				SC.say(caught.getMessage());
			}
		};
		req.getListOfCncs(isAmplicon, callback);
	}

	public void userLogin(String userName, String password){
		
		final UserServiceAsync req = (UserServiceAsync) GWT.create(UserService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) req;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "UserService";
		endpoint.setServiceEntryPoint(moduleRelativeURL);
		final AsyncCallback<User> callback = new AsyncCallback<User>(){
			public void onSuccess(User result){
				
				//searchPanel.setDisabled(false);
				if(result.getIsAdmin()){
					//wp.add(adminPanel);
				}
				//userFormPanel.hide();
				//registerButton.hide();
				//logoutButton.show();
				//MessageBox.hide();
				
			}
			public void onFailure(Throwable caught){
				System.out.println(caught.getMessage());
				//MessageBox.hide();
				//MessageBox.alert(caught.getMessage());
			}
		};
		req.login(userName, password, callback);
	}	
	
	public void userLogout(){
		
		final UserServiceAsync req = (UserServiceAsync) GWT.create(UserService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) req;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "UserService";
		endpoint.setServiceEntryPoint(moduleRelativeURL);
		final AsyncCallback<Void> callback = new AsyncCallback<Void>(){
			public void onSuccess(Void result){
				
				//userName.reset();
				//pw.reset();
				//logoutButton.hide();
				//registerButton.show();
				//searchPanel.setDisabled(true);
				//adminPanel.setVisible(false);
				//userFormPanel.show();
				
				//wp.remove(adminPanel);
				
				/* remove all center panel tabs except for the welcome tab*/
				/*Component[] items = centerPanel.getItems();  
					for (int i = 0; i < items.length; i++) {  
						Component component = items[i];  
						if (!component.getTitle().equals("Welcome")) {  
							centerPanel.remove(component);  
						}
					}
				*/
				
				//MessageBox.hide();
				
			}
			public void onFailure(Throwable caught){
				System.out.println(caught.getMessage());
				//MessageBox.hide();
				//MessageBox.alert(caught.getMessage());
			}
		};
		req.logout(callback);
	}	
	
	public void showAllUsers(){
		
		final AdminAsync req = (AdminAsync) GWT.create(Admin.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) req;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "AdminService";
		endpoint.setServiceEntryPoint(moduleRelativeURL);
		final AsyncCallback<User[]> callback = new AsyncCallback<User[]>(){
			
			public void onSuccess(User[] result){
				
			//Panel tab = centerPanel.openUserAdminTab(result);
			//centerPanel.add(tab);
			//centerPanel.activate(tab.getId());
				
			}
			public void onFailure(Throwable caught){
				System.out.println(caught.getMessage());
				//MessageBox.hide();
				//MessageBox.alert(caught.getMessage());
			}

		};
		req.getAllUsers(callback);
	}	
	
}
	

