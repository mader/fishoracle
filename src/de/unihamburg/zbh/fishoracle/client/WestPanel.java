/*
  Copyright (c) 2009-2011 Malte Mader <mader@zbh.uni-hamburg.de>
  Copyright (c) 2009-2011 Center for Bioinformatics, University of Hamburg

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

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.TreeModelType;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.CheckboxItem;
import com.smartgwt.client.widgets.form.fields.RadioGroupItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ChangeEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangeHandler;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.form.fields.events.ClickHandler;
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

import de.unihamburg.zbh.fishoracle.client.data.DBConfigData;
import de.unihamburg.zbh.fishoracle.client.data.GWTImageInfo;
import de.unihamburg.zbh.fishoracle.client.data.QueryInfo;
import de.unihamburg.zbh.fishoracle.client.data.TrackData;
import de.unihamburg.zbh.fishoracle.client.rpc.Admin;
import de.unihamburg.zbh.fishoracle.client.rpc.AdminAsync;
import de.unihamburg.zbh.fishoracle.client.rpc.Search;
import de.unihamburg.zbh.fishoracle.client.rpc.SearchAsync;

public class WestPanel extends SectionStack{


	private MainPanel mp = null;
	
	VLayout searchContent;
	
	private TextItem searchTextItem;
	private TextItem chrTextItem;
	private TextItem startTextItem;
	private TextItem endTextItem;
	
	private RadioGroupItem SearchRadioGroupItem;
	private CheckboxItem sortedCheckbox;
	private CheckboxItem globalThresholdCheckbox;
	
	private TextItem greaterTextItem;
	private TextItem lessTextItem;
	
	private SectionStackSection adminSection;
	
	private int numberOfTracks = 1;
	
	private List<Track> tracks = new ArrayList<Track>();
	
	public void removeAllTracks(){
		
		while(!tracks.isEmpty()){
			Track t = tracks.get(tracks.size() - 1);
			tracks.remove(tracks.size() - 1);
			numberOfTracks--;
			searchContent.removeMember(t.getTrackForm());
		}
	}
	
	public void addTrack(){
		
		Track t = new Track(numberOfTracks, globalThresholdCheckbox);
		
		tracks.add(t);
		
		numberOfTracks++;
		searchContent.addMember(t.getTrackForm());
		searchContent.redraw();
	}
	
	public WestPanel(MainPanel mainPanel) {
		
		mp = mainPanel;
		this.setOverflow(Overflow.AUTO);
		
		SectionStackSection searchSection = new SectionStackSection();
		searchSection.setTitle("Search");
		searchSection.setID("search1");
		searchSection.setExpanded(true);
		
		searchContent = new VLayout();
		searchContent.markForRedraw();
		
		DynamicForm searchForm = new DynamicForm();
		
		searchTextItem = new TextItem();
		searchTextItem.setTitle("Search");
		
		chrTextItem = new TextItem();
		chrTextItem.setTitle("Chromosome");
		chrTextItem.setVisible(false);
		
		startTextItem = new TextItem();  
		startTextItem.setTitle("Start");
		startTextItem.setVisible(false);

		endTextItem = new TextItem();  
		endTextItem.setTitle("End");
		endTextItem.setVisible(false);
		
		SearchRadioGroupItem = new RadioGroupItem();  
		SearchRadioGroupItem.setTitle("Type");  
		SearchRadioGroupItem.setValueMap("Region", "Gene", "Karyoband"); 
		SearchRadioGroupItem.setDefaultValue("Gene");
		SearchRadioGroupItem.addChangedHandler(new ChangedHandler(){
			
			@Override
			public void onChanged(ChangedEvent event) {
				if(event.getItem().getDisplayValue().equals("Region")){
					searchTextItem.hide();
					chrTextItem.show();
					startTextItem.show();
					endTextItem.show();
				} else {
					chrTextItem.hide();
					startTextItem.hide();
					endTextItem.hide();
					searchTextItem.show();
				}
			}
			
		});
		
		final SelectItem cncDataSelectItem = new SelectItem();
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
		
		lessTextItem = new TextItem();
		lessTextItem.setTitle("less than");
		lessTextItem.setValue("-0.5");
		
		sortedCheckbox = new CheckboxItem();
		sortedCheckbox.setTitle("sort segments by experiment");
		sortedCheckbox.setValue(true);
		
		globalThresholdCheckbox = new CheckboxItem();
		globalThresholdCheckbox.setTitle("Global Intensity Threshold");
		globalThresholdCheckbox.setValue(true);
		globalThresholdCheckbox.addChangedHandler(new ChangedHandler(){

			@Override
			public void onChanged(ChangedEvent event) {
				
				if(!((Boolean) event.getValue())){
					cncDataSelectItem.hide();
					greaterTextItem.hide();
					lessTextItem.hide();
					
					for(int i = 0; i < tracks.size(); i++){
						
						Track t = tracks.get(i);
						t.getSegmentThresholdSelectItem().show();
						t.getGreaterTextItem().show();
						t.getLessTextItem().show();
					}
				}
				
				if((Boolean) event.getValue()){
					cncDataSelectItem.show();
					greaterTextItem.show();
					lessTextItem.show();
					
					for(int i = 0; i < tracks.size(); i++){
						
						Track t = tracks.get(i);
						t.getSegmentThresholdSelectItem().hide();
						t.getGreaterTextItem().hide();
						t.getLessTextItem().hide();
					}
				}
			}
		});
		
		ButtonItem searchButton = new ButtonItem("Search");
		searchButton.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(
					com.smartgwt.client.widgets.form.fields.events.ClickEvent event) {
				startSearch();
			}
		});
		
		ButtonItem addTrackButton = new ButtonItem();
		addTrackButton.setTitle("add Track");
		addTrackButton.setEndRow(false);
		addTrackButton.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(
					com.smartgwt.client.widgets.form.fields.events.ClickEvent event) {
				
					addTrack();
			}
		});
		
		ButtonItem removeTrackButton = new ButtonItem();
		removeTrackButton.setTitle("remove Track");
		removeTrackButton.setStartRow(false);
		removeTrackButton.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(
					com.smartgwt.client.widgets.form.fields.events.ClickEvent event) {
				
					Track lastTrack = tracks.get(tracks.size() - 1);
					tracks.remove(tracks.size() - 1);
					numberOfTracks--;
					searchContent.removeMember(lastTrack.getTrackForm());
			}
		});
		
		searchForm.setItems(searchTextItem,
							chrTextItem,
							startTextItem,
							endTextItem,
							SearchRadioGroupItem,
							sortedCheckbox,
							globalThresholdCheckbox,
							cncDataSelectItem,
							greaterTextItem,
							lessTextItem,
							addTrackButton,
							removeTrackButton,
							searchButton);
		
		searchContent.addMember(searchForm);
		
		searchSection.setItems(searchContent);
		
		/*data adminstration*/
		
		SectionStackSection dataAdminSection = new SectionStackSection();
		dataAdminSection.setTitle("Data Adminstration");
		dataAdminSection.setExpanded(true);
		
		VLayout dataAdminContent = new VLayout();
		
		TreeGrid dataAdminTreeGrid = new TreeGrid();
		dataAdminTreeGrid.setShowConnectors(true);
		dataAdminTreeGrid.setShowHeader(false);
		
		Tree dataAdminTree = new Tree();  
		dataAdminTree.setModelType(TreeModelType.CHILDREN);  
		dataAdminTree.setRoot(new TreeNode("root", 
							new TreeNode("Data Import"),
							new TreeNode("Manage Projects"),
							new TreeNode("Manage Microarraystudies")
							)); 
		
		dataAdminTreeGrid.addNodeClickHandler(new NodeClickHandler(){

			@Override
			public void onNodeClick(NodeClickEvent event) {
				
				if(event.getNode().getName().equals("Manage Projects")){
					boolean exists = false;
					int index = 0;
					
					TabSet centerTabSet = mp.getCenterPanel().getCenterTabSet();
					Tab[] tabs = mp.getCenterPanel().getCenterTabSet().getTabs();
					for(int i=0; i < tabs.length; i++){
						if(tabs[i].getTitle().equals("Manage Projects")){
							exists = true;
							index = i;
						}
					}
					
					if(exists){
						centerTabSet.selectTab(index);
					} else {
						mp.getCenterPanel().getUserObject("ProjectAdminTab");
					}
				}
				
				if(event.getNode().getName().equals("Data Import")){
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
				
				if(event.getNode().getName().equals("Manage Microarraystudies")){
					boolean exists = false;
					int index = 0;
					
					TabSet centerTabSet = mp.getCenterPanel().getCenterTabSet();
					Tab[] tabs = mp.getCenterPanel().getCenterTabSet().getTabs();
					for(int i=0; i < tabs.length; i++){
						if(tabs[i].getTitle().equals("Microarraystudy Management")){
							exists = true;
							index = i;
						}
					}
					
					if(exists){
						centerTabSet.selectTab(index);
					} else {
						mp.getCenterPanel().getUserObject("MicroarraystudyAdminTab");
					}
				}
			}
		});
		
		dataAdminTreeGrid.setData(dataAdminTree);
		
		dataAdminContent.addMember(dataAdminTreeGrid);
		
		dataAdminSection.setItems(dataAdminContent);
		
		this.setSections(searchSection,dataAdminSection);
			
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
						
						mp.getCenterPanel().openUserAdminTab();
					}
				}
				if(event.getNode().getName().equals("Manage Groups")){
					boolean exists = false;
					int index = 0;
					
					TabSet centerTabSet = mp.getCenterPanel().getCenterTabSet();
					Tab[] tabs = mp.getCenterPanel().getCenterTabSet().getTabs();
					for(int i=0; i < tabs.length; i++){
						if(tabs[i].getTitle().equals("Group Management")){
							exists = true;
							index = i;
						}
					}
					
					if(exists){
						centerTabSet.selectTab(index);
					} else {
						mp.getCenterPanel().openGroupAdminTab();
					}
				}
				if(event.getNode().getName().equals("Manage Organs")){
					boolean exists = false;
					int index = 0;
					
					TabSet centerTabSet = mp.getCenterPanel().getCenterTabSet();
					Tab[] tabs = mp.getCenterPanel().getCenterTabSet().getTabs();
					for(int i=0; i < tabs.length; i++){
						if(tabs[i].getTitle().equals("Organ Management")){
							exists = true;
							index = i;
						}
					}
					
					if(exists){
						centerTabSet.selectTab(index);
					} else {
						
						mp.getCenterPanel().openOrganAdminTab();
						
					}
				}
				if(event.getNode().getName().equals("Manage Properties")){
					boolean exists = false;
					int index = 0;
					
					TabSet centerTabSet = mp.getCenterPanel().getCenterTabSet();
					Tab[] tabs = mp.getCenterPanel().getCenterTabSet().getTabs();
					for(int i=0; i < tabs.length; i++){
						if(tabs[i].getTitle().equals("Property Management")){
							exists = true;
							index = i;
						}
					}
					
					if(exists){
						centerTabSet.selectTab(index);
					} else {
						mp.getCenterPanel().openPropertyAdminTab();
					}
				}
				if(event.getNode().getName().equals("Manage Chips")){
					boolean exists = false;
					int index = 0;
					
					TabSet centerTabSet = mp.getCenterPanel().getCenterTabSet();
					Tab[] tabs = mp.getCenterPanel().getCenterTabSet().getTabs();
					for(int i=0; i < tabs.length; i++){
						if(tabs[i].getTitle().equals("Chip Management")){
							exists = true;
							index = i;
						}
					}
					
					if(exists){
						centerTabSet.selectTab(index);
					} else {
						mp.getCenterPanel().openChipAdminTab();
					}
				}
				if(event.getNode().getName().equals("Database Configuration")){
					boolean exists = false;
					int index = 0;
					
					TabSet centerTabSet = mp.getCenterPanel().getCenterTabSet();
					Tab[] tabs = mp.getCenterPanel().getCenterTabSet().getTabs();
					for(int i=0; i < tabs.length; i++){
						if(tabs[i].getTitle().equals("Database Configuration")){
							exists = true;
							index = i;
						}
					}
					if(exists){
						centerTabSet.selectTab(index);
					} else {
						getDatabaseConnectionData();
					}
				}
			}
			
		});
		
		Tree adminTree = new Tree();  
		adminTree.setModelType(TreeModelType.CHILDREN);  
		adminTree.setRoot(new TreeNode("root",  
							new TreeNode("Show Users"),
							new TreeNode("Manage Groups"),
							new TreeNode("Manage Organs"),
							new TreeNode("Manage Properties"),
							new TreeNode("Manage Chips"),
							new TreeNode("Database Configuration")
							)); 
		
		adminTreeGrid.setData(adminTree);
		
		adminContent.addMember(adminTreeGrid);
		
		adminSection.addItem(adminContent);
		
		return adminSection;
	}
	
	public TextItem getSearchTextItem() {
		return searchTextItem;
	}

	private int[] strArrToIntArr(String[] strArr){
		
		int[] intArr = new int[strArr.length];
		
		for(int j = 0; j < strArr.length; j++){
			intArr[j] = Integer.parseInt(strArr[j]);
		}
		
		return intArr;
	}
	
	public void startSearch(){
		String typeStr = null;
		String qryStr = null;
		
		if(searchTextItem.getDisplayValue().equals("") &&
			chrTextItem.getDisplayValue().equals("") &&
			startTextItem.getDisplayValue().equals("") &&
			endTextItem.getDisplayValue().equals("") ){
			SC.say("You have to type in a search term or provide a chromosome, start position and end position!");
		} else {
		
			if(SearchRadioGroupItem.getDisplayValue().equalsIgnoreCase("Region")){
				typeStr = "Region";
				qryStr = chrTextItem.getDisplayValue() + ":" 
							+ startTextItem.getDisplayValue() + ":" + endTextItem.getDisplayValue();
			}
			if(SearchRadioGroupItem.getDisplayValue().equalsIgnoreCase("Gene")){
				typeStr = "Gene Search";
				qryStr = searchTextItem.getDisplayValue();
			}
			if(SearchRadioGroupItem.getDisplayValue().equalsIgnoreCase("Karyoband")){
				typeStr = "Band Search";
				qryStr = searchTextItem.getDisplayValue();
			}
						
			QueryInfo newQuery = null;
			try {
				
				String globalLessThenThr;
				String globalGreaterThenThr;
				
				if(globalThresholdCheckbox.getValueAsBoolean()){
					if(lessTextItem.getDisplayValue().equals("")){
						globalLessThenThr = null;
					} else {
						globalLessThenThr = lessTextItem.getDisplayValue();
					}
					if(greaterTextItem.getDisplayValue().equals("")){
						globalGreaterThenThr = null;
					} else {
						globalGreaterThenThr = greaterTextItem.getDisplayValue();
					}
				} else {
					globalLessThenThr = null;
					globalGreaterThenThr = null;
				}
				
				TrackData[] trackData = new TrackData[tracks.size()];
				
				for(int i = 0; i < tracks.size(); i++){
					trackData[i] = new TrackData();
					trackData[i].setTrackNumber(tracks.get(i).getTrackNumber());
					trackData[i].setTrackName(tracks.get(i).getTrackNameItem().getDisplayValue());
					if(globalThresholdCheckbox.getValueAsBoolean()){
						trackData[i].setLowerTh(null);
						trackData[i].setUpperTh(null);
					} else {
						if(tracks.get(i).getLessTextItem().getDisplayValue().equals("")){
							trackData[i].setLowerTh(null);
						} else {
							trackData[i].setLowerTh(tracks.get(i).getLessTextItem().getDisplayValue());
						}
						if(tracks.get(i).getGreaterTextItem().getDisplayValue().equals("")){
							trackData[i].setUpperTh(null);
						} else {
							trackData[i].setUpperTh(tracks.get(i).getGreaterTextItem().getDisplayValue());
						}
					}
					if(tracks.get(i).getSelectItemProjects().getVisible()){
						
						String[] strArr = tracks.get(i).getSelectItemProjects().getValues();
						
						trackData[i].setProjectIds(strArrToIntArr(strArr));
						
					} else {
						trackData[i].setProjectIds(null);
					}
					
					if(tracks.get(i).getSelectItemTissues().getVisible()){
						
						String[] strArr = tracks.get(i).getSelectItemTissues().getValues();
						
						trackData[i].setTissueIds(strArrToIntArr(strArr));
						
					} else {
						trackData[i].setTissueIds(null);
					}
					
					if(tracks.get(i).getSelectItemExperiments().getVisible()){
						
						String[] strArr = tracks.get(i).getSelectItemExperiments().getValues();
						
						trackData[i].setExperimentIds(strArrToIntArr(strArr));
						
					} else {
						trackData[i].setExperimentIds(null);
					}
					
				}
				
				newQuery = new QueryInfo(qryStr,
											typeStr,
											globalLessThenThr,
											globalGreaterThenThr,
											sortedCheckbox.getValueAsBoolean(),
											globalThresholdCheckbox.getValueAsBoolean(),
											"png",
											trackData,
											mp.getCenterPanel().getWidth() - 30);
			} catch (Exception e) {
				SC.say(e.getMessage());
			}
			
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
					
					mp.getCenterPanel().newImageTab(result);				
					
				}
				public void onFailure(Throwable caught){

					SC.say(caught.getMessage());
					
				}
			};
			req.generateImage(q, callback);
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
	
	public void getDatabaseConnectionData(){
		
		final AdminAsync req = (AdminAsync) GWT.create(Admin.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) req;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "AdminService";
		endpoint.setServiceEntryPoint(moduleRelativeURL);
		final AsyncCallback<DBConfigData> callback = new AsyncCallback<DBConfigData>(){
			@Override
			public void onSuccess(DBConfigData result){
				
				mp.getCenterPanel().openDatabaseConfigTab(result);
				
			}
			public void onFailure(Throwable caught){
				System.out.println(caught.getMessage());
				SC.say(caught.getMessage());
			}
		};
		req.fetchDBConfigData(callback);
	}
}