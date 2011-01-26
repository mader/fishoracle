package de.unihamburg.zbh.fishoracle.client;

import java.util.LinkedHashMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;

import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.MultipleAppearance;
import com.smartgwt.client.types.TreeModelType;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
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
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;
import com.smartgwt.client.widgets.tree.Tree;
import com.smartgwt.client.widgets.tree.TreeGrid;
import com.smartgwt.client.widgets.tree.TreeNode;
import com.smartgwt.client.widgets.tree.events.NodeClickEvent;
import com.smartgwt.client.widgets.tree.events.NodeClickHandler;

import de.unihamburg.zbh.fishoracle.client.data.CopyNumberChange;
import de.unihamburg.zbh.fishoracle.client.data.GWTImageInfo;
import de.unihamburg.zbh.fishoracle.client.data.Organ;
import de.unihamburg.zbh.fishoracle.client.data.QueryInfo;
import de.unihamburg.zbh.fishoracle.client.data.User;
import de.unihamburg.zbh.fishoracle.client.rpc.Admin;
import de.unihamburg.zbh.fishoracle.client.rpc.AdminAsync;
import de.unihamburg.zbh.fishoracle.client.rpc.Search;
import de.unihamburg.zbh.fishoracle.client.rpc.SearchAsync;

public class WestPanel extends SectionStack{


	private MainPanel mp = null;
	
	private TextItem searchTextItem;

	private RadioGroupItem SearchRadioGroupItem;
	
	private TextItem greaterTextItem;
	private TextItem lessTextItem;
	
	private SelectItem selectItemTissues;
	
	private SectionStackSection adminSection;
	
	public WestPanel(MainPanel mainPanel) {
		
		mp = mainPanel;
		
		SectionStackSection searchSection = new SectionStackSection();
		searchSection.setTitle("Search");
		searchSection.setID("search1");
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
		
		SelectItem cncDataSelectItem = new SelectItem();
		cncDataSelectItem.setTitle("");
		cncDataSelectItem.setType("Select"); 
		cncDataSelectItem.setValueMap("greater than", "less than");
		cncDataSelectItem.setDefaultValue("less than");
		cncDataSelectItem.addChangeHandler(new ChangeHandler(){

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
		
		greaterTextItem.addKeyPressHandler(new KeyPressHandler(){
			
			@Override
			public void onKeyPress(KeyPressEvent event) {
				if(event.getKeyName().equals("Enter")){
					startSearch();
				}
			}
		});
		
		lessTextItem = new TextItem();
		lessTextItem.setTitle("less than");
		lessTextItem.setValue("-0.5");
		//lessTextItem.disable();
		
		lessTextItem.addKeyPressHandler(new KeyPressHandler(){
			
			@Override
			public void onKeyPress(KeyPressEvent event) {
				if(event.getKeyName().equals("Enter")){
					startSearch();
				}
			}
		});
		
		/*show more information*/
		/*
		LinkItem AllDataLinkItem = new LinkItem();   
		AllDataLinkItem.setLinkTitle("show all CNCs");
		AllDataLinkItem.addClickHandler(new ClickHandler(){
			@Override
			public void onClick(
					com.smartgwt.client.widgets.form.fields.events.ClickEvent event) {
				wp.fetchCncData(true);
			}
		});
		*/
		
		selectItemTissues = new SelectItem();
		selectItemTissues.setTitle("Tissue Filter");
		selectItemTissues.setMultiple(true);
		selectItemTissues.setMultipleAppearance(MultipleAppearance.PICKLIST);
		loadTissueFilterData();
		selectItemTissues.setDefaultToFirstOption(true);
		
		searchForm.setItems(searchTextItem,
							SearchRadioGroupItem, 
							cncDataSelectItem, 
							greaterTextItem, 
							lessTextItem, 
							selectItemTissues, 
							searchButton);
		
		searchContent.addMember(searchForm);
		
		searchSection.setItems(searchContent);
		
		SectionStackSection settingsSection = new SectionStackSection();  
		settingsSection.setTitle("Settings");
		settingsSection.setID("settings1");
		
		VLayout settingsContent = new VLayout();
		
		/*user settings*/
		DynamicForm userForm = new DynamicForm();
		
		Label info = new Label("user settings here");
		
		//serForm.setItems();
		
		settingsContent.addMember(info);
		
		settingsContent.addMember(userForm);
		
		settingsSection.addItem(settingsContent);
		
		this.setSections(searchSection, settingsSection);
			
	}
	
	public SectionStackSection newAdminSection() {
		
		adminSection = new SectionStackSection();  
		adminSection.setTitle("Admin");
		adminSection.setID("admin1");
		
		VLayout adminContent = new VLayout();
		
		/*administration settings*/
		TreeGrid adminTreeGrid = new TreeGrid();
		adminTreeGrid.setShowConnectors(true);
		adminTreeGrid.setShowHeader(false);
		adminTreeGrid.addNodeClickHandler(new NodeClickHandler(){

			@Override
			public void onNodeClick(NodeClickEvent event) {
				
				if(event.getNode().getName().equals("Show Users")){
					boolean exists = false;
					int index = 0;
					
					TabSet centerTabSet = mp.getCenterPanel().getCenterTabSet();
					Tab[] tabs = mp.getCenterPanel().getCenterTabSet().getTabs();
					for(int i=0; i < tabs.length; i++){
						if(tabs[i].getTitle().equals("Users")){
							exists = true;
							index = i;
						}
					}
					
					if(exists){
						centerTabSet.selectTab(index);
					} else {
						showAllUsers();
					}
				}
				if(event.getNode().getName().equals("Add Data")){
					boolean exists = false;
					int index = 0;
					
					TabSet centerTabSet = mp.getCenterPanel().getCenterTabSet();
					Tab[] tabs = mp.getCenterPanel().getCenterTabSet().getTabs();
					for(int i=0; i < tabs.length; i++){
						if(tabs[i].getTitle().equals("Data Import") || tabs[i].getTitle().equals("Data Import (occupied)")){
							exists = true;
							index = i;
						}
					}
					if(exists){
						centerTabSet.selectTab(index);
					} else {
						checkImportData();
					}
				}
			}
			
		});
		
		
		Tree adminTree = new Tree();  
		adminTree.setModelType(TreeModelType.CHILDREN);  
		adminTree.setRoot(new TreeNode("root",  
							new TreeNode("Show Users"),  
							new TreeNode("Add Data")
							));  
		
		adminTreeGrid.setData(adminTree);
		
		adminContent.addMember(adminTreeGrid);
		
		adminSection.addItem(adminContent);
		
		return adminSection;
	}
	
	public TextItem getSearchTextItem() {
		return searchTextItem;
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
						
			QueryInfo newQuery = null;
			try {
				
				newQuery = new QueryInfo(searchTextItem.getDisplayValue(),
											typeStr,
											lessTextItem.getDisplayValue(),
											greaterTextItem.getDisplayValue(),
											"png",
											selectItemTissues.getValues(),
											mp.getCenterPanel().getWidth() - 30);
			} catch (Exception e) {
				SC.say(e.getMessage());
			}
			
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
				//mp.getCenterPanel().newDataTab(result, isAmplicon);

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
	
	public void checkImportData(){

		final AdminAsync req = (AdminAsync) GWT.create(Admin.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) req;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "AdminService";
		endpoint.setServiceEntryPoint(moduleRelativeURL);
		final AsyncCallback<Boolean> callback = new AsyncCallback<Boolean>(){
			@Override
			public void onSuccess(Boolean result){

				mp.getCenterPanel().openDataAdminTab(result);
			}
			public void onFailure(Throwable caught){
				System.out.println(caught.getMessage());
				SC.say(caught.getMessage());				
			}
		};
		req.canAccessDataImport(callback);
	}
	
	public void loadTissueFilterData(){

		final SearchAsync req = (SearchAsync) GWT.create(Search.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) req;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "Search";
		endpoint.setServiceEntryPoint(moduleRelativeURL);
		final AsyncCallback<Organ[]> callback = new AsyncCallback<Organ[]>(){
			@Override
			public void onSuccess(Organ[] result){

				LinkedHashMap<String, String> organValueMap = new LinkedHashMap<String, String>();
				for(int i=0; i < result.length; i++){
					organValueMap.put(new Integer(i).toString(),result[i].getLabel());

				}
				
				selectItemTissues.setValueMap(organValueMap);
			}
			public void onFailure(Throwable caught){
				System.out.println(caught.getMessage());
				SC.say(caught.getMessage());
			}
		};
		req.getOrganData(callback);
	}
	
}