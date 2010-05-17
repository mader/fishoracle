package de.unihamburg.zbh.fishoracle.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;

import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.CheckboxItem;
import com.smartgwt.client.widgets.form.fields.LinkItem;
import com.smartgwt.client.widgets.form.fields.RadioGroupItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ChangeEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangeHandler;
import com.smartgwt.client.widgets.form.fields.events.ClickEvent;
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

public class WestPanel extends SectionStack{


	private MainPanel mp = null;
	private WestPanel wp = this;
	
	private TextItem searchTextItem;

	private RadioGroupItem SearchRadioGroupItem;
	//private RadioGroupItem SearchPriorityRadioGroupItem;
	//private CheckboxItem ampliconFilterCheckboxItem;
	//private CheckboxItem deliconFilterCheckboxItem;
	//private CheckboxItem cncFilterCheckboxItem;
	
	private TextItem greaterTextItem;
	private TextItem lessTextItem;
	
	private SectionStackSection adminSection;
	
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
		SearchRadioGroupItem.setValueMap("CNC Stable ID", "Gene", "Karyoband"); 
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
		/*
		SearchPriorityRadioGroupItem = new RadioGroupItem();  
		SearchPriorityRadioGroupItem.setTitle("Search Priority");  
		SearchPriorityRadioGroupItem.setValueMap("Amplicon", "Delicon"); 
		SearchPriorityRadioGroupItem.setDefaultValue("Amplicon");
		*/
		
		/*display filter*/
		/*
		ampliconFilterCheckboxItem = new CheckboxItem();  
		ampliconFilterCheckboxItem.setTitle("Show Amplicons");
		ampliconFilterCheckboxItem.setDefaultValue(true);
		
		deliconFilterCheckboxItem = new CheckboxItem();  
		deliconFilterCheckboxItem.setTitle("Show Delicons");
		deliconFilterCheckboxItem.setDefaultValue(true);
		
		cncFilterCheckboxItem = new CheckboxItem();  
		cncFilterCheckboxItem.setTitle("Show Segments");
		cncFilterCheckboxItem.setDefaultValue(true);
		*/
		
		SelectItem cncDataSelectItem = new SelectItem();
		//cncDataSelectItem.setShowTitle(false);
		cncDataSelectItem.setType("Select"); 
		cncDataSelectItem.setValueMap("greater than", "less than", "between");
		cncDataSelectItem.setDefaultValue("between");
		cncDataSelectItem.addChangeHandler(new ChangeHandler(){

			@Override
			public void onChange(ChangeEvent event) {
				if(event.getValue().equals("greater than")){
					greaterTextItem.enable();
					lessTextItem.disable();
				}
				if(event.getValue().equals("less than")){
					greaterTextItem.disable();
					lessTextItem.enable();
				}
				if(event.getValue().equals("between")){
					greaterTextItem.enable();
					lessTextItem.enable();
				}
			}
		});
		
		greaterTextItem = new TextItem();
		greaterTextItem.setTitle("greater than");
		
		lessTextItem = new TextItem();
		lessTextItem.setTitle("less than");
		//lessTextItem.disable();
		
		
		/*show more information*/
		/*
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
		*/
		
		/*add content to search stack section*/
		//searchForm.setItems(searchTextItem, SearchRadioGroupItem, searchButton, SearchPriorityRadioGroupItem, ampliconFilterCheckboxItem,
		//					deliconFilterCheckboxItem, cncFilterCheckboxItem, cncDataSelectItem, greaterTextItem, lessTextItem, AmpliconLinkItem, AmpliconLinkItem);
		
		searchForm.setItems(searchTextItem, SearchRadioGroupItem, cncDataSelectItem, greaterTextItem, lessTextItem, searchButton);
		searchContent.addMember(searchForm);
		
		searchSection.setItems(searchContent);
		
		SectionStackSection settingsSection = new SectionStackSection();  
		settingsSection.setTitle("Settings");
		
		VLayout settingsContent = new VLayout();
		
		/*user settings*/
		DynamicForm userForm = new DynamicForm();
		
		Label info = new Label("user settings here");
		
		//serForm.setItems();
		
		settingsContent.addMember(info);
		
		settingsContent.addMember(userForm);
		
		settingsSection.addItem(settingsContent);
		
		adminSection = new SectionStackSection();  
		adminSection.setTitle("Admin");
		adminSection.setID("admin1");
		
		VLayout adminContent = new VLayout();
		
		/*administration settings*/
		DynamicForm adminForm = new DynamicForm();
		
		LinkItem usersLinkItem = new LinkItem();   
		usersLinkItem.setLinkTitle("Show users");
		usersLinkItem.setShowTitle(false);
		usersLinkItem.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				showAllUsers();
			}
		});
		
		LinkItem dataLinkItem = new LinkItem();  
		dataLinkItem.setLinkTitle("Add data");
		dataLinkItem.setShowTitle(false);
		dataLinkItem.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				mp.getCenterPanel().openDataAdminTab();
			}
		});
		
		adminForm.setItems(usersLinkItem, dataLinkItem);
		
		adminContent.addMember(adminForm);
		
		adminSection.addItem(adminContent);
		
		this.setSections(searchSection, settingsSection);
			
	}
	
	public TextItem getSearchTextItem() {
		return searchTextItem;
	}
	
	public SectionStackSection getAdminSection() {
		return adminSection;
	}

	public void startSearch(){
		String typeStr = null;
		
		if(searchTextItem.getDisplayValue().equals("")){
			SC.say("You have to type in a search term!");
		} else {
		
			if(SearchRadioGroupItem.getDisplayValue().equalsIgnoreCase("CNC Stable ID")){
				typeStr = "CNC Search";
			}
			if(SearchRadioGroupItem.getDisplayValue().equalsIgnoreCase("Gene")){
				typeStr = "Gene Search";
			}
			if(SearchRadioGroupItem.getDisplayValue().equalsIgnoreCase("Karyoband")){
				typeStr = "Band Search";
			}
						
			QueryInfo newQuery = new QueryInfo(searchTextItem.getDisplayValue(),
												typeStr,
												lessTextItem.getDisplayValue(),
												greaterTextItem.getDisplayValue(),
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
	
	public void showAllUsers(){
		
		final AdminAsync req = (AdminAsync) GWT.create(Admin.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) req;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "AdminService";
		endpoint.setServiceEntryPoint(moduleRelativeURL);
		final AsyncCallback<User[]> callback = new AsyncCallback<User[]>(){
			
			public void onSuccess(User[] result){
				
				mp.getCenterPanel().openUserAdminTab(result);
				
			}
			public void onFailure(Throwable caught){
				System.out.println(caught.getMessage());
				//MessageBox.hide();
				SC.say(caught.getMessage());
			}

		};
		req.getAllUsers(callback);
	}	
	
}