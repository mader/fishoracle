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
import com.smartgwt.client.types.MultipleAppearance;
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

import de.unihamburg.zbh.fishoracle.client.data.DBConfigData;
import de.unihamburg.zbh.fishoracle.client.data.FoConfigData;
import de.unihamburg.zbh.fishoracle.client.data.FoTrackData;
import de.unihamburg.zbh.fishoracle.client.data.GWTImageInfo;
import de.unihamburg.zbh.fishoracle.client.data.QueryInfo;
import de.unihamburg.zbh.fishoracle.client.datasource.ConfigDS;
import de.unihamburg.zbh.fishoracle.client.datasource.EnsemblDBDS;
import de.unihamburg.zbh.fishoracle.client.rpc.Admin;
import de.unihamburg.zbh.fishoracle.client.rpc.AdminAsync;
import de.unihamburg.zbh.fishoracle.client.rpc.Search;
import de.unihamburg.zbh.fishoracle.client.rpc.SearchAsync;
import de.unihamburg.zbh.fishoracle_db_api.data.Constants;

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
	private CheckboxItem showCNVCaptionsCheckbox;
	private SelectItem ensemblSelectItem;
	private SelectItem biotypeSelectItem;
	
	private SelectItem segmentDataSelectItem;
	private TextItem greaterTextItem;
	private TextItem lessTextItem;
	
	private TextItem saveConfigTextItem;
	
	private SelectItem statusSelectItem;
	
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
	
	public SelectItem getSegmentDataSelectItem() {
		return segmentDataSelectItem;
	}
	
	public TextItem getGreaterTextItem() {
		return greaterTextItem;
	}

	public TextItem getLessTextItem() {
		return lessTextItem;
	}	
	
	public SelectItem getStatusTextItem() {
		return statusSelectItem;
	}
	
	public CheckboxItem getGlobalThresholdCheckbox() {
		return globalThresholdCheckbox;
	}

	public void addTrack(){
		
		Track t = new Track(numberOfTracks, globalThresholdCheckbox, mp);
		
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
		searchTextItem.addKeyPressHandler(new KeyPressHandler(){
			
			@Override
			public void onKeyPress(KeyPressEvent event) {
				if(event.getKeyName().equals("Enter")){
					startSearch();
				}
			}
		});
		
		chrTextItem = new TextItem();
		chrTextItem.setTitle("Chromosome");
		chrTextItem.setVisible(false);
		chrTextItem.addKeyPressHandler(new KeyPressHandler(){
			
			@Override
			public void onKeyPress(KeyPressEvent event) {
				if(event.getKeyName().equals("Enter")){
					startSearch();
				}
			}
		});
		
		startTextItem = new TextItem();  
		startTextItem.setTitle("Start");
		startTextItem.setVisible(false);
		startTextItem.addKeyPressHandler(new KeyPressHandler(){
			
			@Override
			public void onKeyPress(KeyPressEvent event) {
				if(event.getKeyName().equals("Enter")){
					startSearch();
				}
			}
		});

		endTextItem = new TextItem();  
		endTextItem.setTitle("End");
		endTextItem.setVisible(false);
		endTextItem.addKeyPressHandler(new KeyPressHandler(){
			
			@Override
			public void onKeyPress(KeyPressEvent event) {
				if(event.getKeyName().equals("Enter")){
					startSearch();
				}
			}
		});
		
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
		
		ensemblSelectItem = new SelectItem();
		ensemblSelectItem.setTitle("Ensembl Database");
		
		ensemblSelectItem.setDisplayField("ensemblDBLabel");
		ensemblSelectItem.setValueField("ensemblDBName");
		ensemblSelectItem.setAutoFetchData(false);
		
		EnsemblDBDS edbDS = new EnsemblDBDS();
		
		ensemblSelectItem.setOptionDataSource(edbDS);
		
		ensemblSelectItem.setDefaultToFirstOption(true);
		
		biotypeSelectItem = new SelectItem();
		biotypeSelectItem.setTitle("Gene Biotype Filter");
		biotypeSelectItem.setMultiple(true);
		biotypeSelectItem.setMultipleAppearance(MultipleAppearance.PICKLIST);
		biotypeSelectItem.setValueMap("protein_coding",
										"pseudogene",
										"processed_transcript",
										"polymorphic_pseudogene",
										"lincRNA",
										"antisense",
										"sense_intronic",
										"non_coding",
										"sense_overlapping",
										"3prime_overlapping_ncrna",
										"ncrna_host",
										"TR_gene",
										"IG_gene",
										"other RNA",
										"LRG_gene");
		biotypeSelectItem.setDefaultValues("protein_coding",
				"pseudogene",
				"processed_transcript",
				"polymorphic_pseudogene");
		
		
		segmentDataSelectItem = new SelectItem();
		segmentDataSelectItem.setTitle("");
		segmentDataSelectItem.setType("Select"); 
		segmentDataSelectItem.setValueMap("greater than", "less than");
		segmentDataSelectItem.setDefaultValue("less than");
		segmentDataSelectItem.addChangeHandler(new ChangeHandler(){

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
		lessTextItem.addKeyPressHandler(new KeyPressHandler(){
			
			@Override
			public void onKeyPress(KeyPressEvent event) {
				if(event.getKeyName().equals("Enter")){
					startSearch();
				}
			}
		});
		
		statusSelectItem = new SelectItem();
		statusSelectItem.setTitle("CNV Status");
		statusSelectItem.setMultiple(true);
		statusSelectItem.setMultipleAppearance(MultipleAppearance.PICKLIST);
		statusSelectItem.setValueMap("0",
										"1",
										"2",
										"3",
										"4");
		statusSelectItem.setDefaultValues("0","1");
		statusSelectItem.addKeyPressHandler(new KeyPressHandler(){
			
			@Override
			public void onKeyPress(KeyPressEvent event) {
				if(event.getKeyName().equals("Enter")){
					startSearch();
				}
			}
		});
		
		sortedCheckbox = new CheckboxItem();
		sortedCheckbox.setTitle("sort segments by experiment");
		sortedCheckbox.setValue(true);
		sortedCheckbox.addKeyPressHandler(new KeyPressHandler(){
			
			@Override
			public void onKeyPress(KeyPressEvent event) {
				if(event.getKeyName().equals("Enter")){
					startSearch();
				}
			}
		});
		
		showCNVCaptionsCheckbox = new CheckboxItem();
		showCNVCaptionsCheckbox.setTitle("show segment captions");
		showCNVCaptionsCheckbox.setValue(false);
		showCNVCaptionsCheckbox.addKeyPressHandler(new KeyPressHandler(){
			
			@Override
			public void onKeyPress(KeyPressEvent event) {
				if(event.getKeyName().equals("Enter")){
					startSearch();
				}
			}
		});
		
		globalThresholdCheckbox = new CheckboxItem();
		globalThresholdCheckbox.setTitle("Global Segment Threshold");
		globalThresholdCheckbox.setValue(true);
		globalThresholdCheckbox.addChangedHandler(new ChangedHandler(){

			@Override
			public void onChanged(ChangedEvent event) {
				
				if(!((Boolean) event.getValue())){
					segmentDataSelectItem.hide();
					greaterTextItem.hide();
					lessTextItem.hide();
					statusSelectItem.hide();
					
					for(int i = 0; i < tracks.size(); i++){
						
						Track t = tracks.get(i);
						if(t.getSelectItemFilterType().getValueAsString().equals("Segments (DNACopy)")){
							t.getSegmentThresholdSelectItem().show();
							t.getGreaterTextItem().show();
							t.getLessTextItem().show();
						}
						if(t.getSelectItemFilterType().getValueAsString().equals("Segments (PennCNV)")){
							t.getStatusSelectItem().show();
						}
						
					}
				}
				
				if((Boolean) event.getValue()){
					segmentDataSelectItem.show();
					greaterTextItem.show();
					lessTextItem.show();
					statusSelectItem.show();
					
					for(int i = 0; i < tracks.size(); i++){
						
						Track t = tracks.get(i);
						t.getSegmentThresholdSelectItem().hide();
						t.getGreaterTextItem().hide();
						t.getLessTextItem().hide();
						t.getStatusSelectItem().hide();
					}
				}
			}
		});
		globalThresholdCheckbox.addKeyPressHandler(new KeyPressHandler(){
			
			@Override
			public void onKeyPress(KeyPressEvent event) {
				if(event.getKeyName().equals("Enter")){
					startSearch();
				}
			}
		});
		
		ButtonItem loadConfigButton = new ButtonItem();
		loadConfigButton.setTitle("load config");
		loadConfigButton.setStartRow(true);
		loadConfigButton.setEndRow(false);
		
		SelectItem configSelectItem = new SelectItem();
		configSelectItem.setEndRow(true);
		configSelectItem.setStartRow(false);
		configSelectItem.setShowTitle(false);
		configSelectItem.setDisplayField("configName");
		configSelectItem.setValueField("configId");		
		configSelectItem.setAutoFetchData(false);
		ConfigDS cDS = new ConfigDS();
		configSelectItem.setOptionDataSource(cDS);
		
		ButtonItem saveTracksButton = new ButtonItem();
		saveTracksButton.setTitle("save config");
		saveTracksButton.setStartRow(true);
		saveTracksButton.setEndRow(false);
		
		saveConfigTextItem = new TextItem();
		saveConfigTextItem.setShowTitle(false);
		saveConfigTextItem.setStartRow(false);
		saveConfigTextItem.setEndRow(true);
		
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
		removeTrackButton.setTitle("remove last Track");
		removeTrackButton.setStartRow(false);
		removeTrackButton.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(
					com.smartgwt.client.widgets.form.fields.events.ClickEvent event) {
				
				if(tracks.size() > 1){
					Track lastTrack = tracks.get(tracks.size() - 1);
					tracks.remove(tracks.size() - 1);
					numberOfTracks--;
					searchContent.removeMember(lastTrack.getTrackForm());
				} else {
					SC.say("You need at least one track to visualize your data!");
				}
			}
		});
		
		searchForm.setItems(searchTextItem,
							chrTextItem,
							startTextItem,
							endTextItem,
							SearchRadioGroupItem,
							ensemblSelectItem,
							biotypeSelectItem,
							sortedCheckbox,
							showCNVCaptionsCheckbox,
							globalThresholdCheckbox,
							segmentDataSelectItem,
							greaterTextItem,
							lessTextItem,
							statusSelectItem,
							loadConfigButton,
							configSelectItem,
							saveTracksButton,
							saveConfigTextItem,
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
							new TreeNode("Manage Studies")
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
						if(tabs[i].getTitle().equals("Data Import")){
							exists = true;
							index = i;
						}
					}
					if(exists){
						centerTabSet.selectTab(index);
					} else {
						mp.getCenterPanel().openDataAdminTab();
					}
				}
				
				if(event.getNode().getName().equals("Manage Studies")){
					boolean exists = false;
					int index = 0;
					
					TabSet centerTabSet = mp.getCenterPanel().getCenterTabSet();
					Tab[] tabs = mp.getCenterPanel().getCenterTabSet().getTabs();
					for(int i=0; i < tabs.length; i++){
						if(tabs[i].getTitle().equals("Manage Studies")){
							exists = true;
							index = i;
						}
					}
					
					if(exists){
						centerTabSet.selectTab(index);
					} else {
						mp.getCenterPanel().getUserObject("StudyAdminTab");
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
						if(tabs[i].getTitle().equals("Manage Groups")){
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
						if(tabs[i].getTitle().equals("Manage Organs")){
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
						if(tabs[i].getTitle().equals("Manage Properties")){
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
				if(event.getNode().getName().equals("Manage Platforms")){
					boolean exists = false;
					int index = 0;
					
					TabSet centerTabSet = mp.getCenterPanel().getCenterTabSet();
					Tab[] tabs = mp.getCenterPanel().getCenterTabSet().getTabs();
					for(int i=0; i < tabs.length; i++){
						if(tabs[i].getTitle().equals("Management Platforms")){
							exists = true;
							index = i;
						}
					}
					
					if(exists){
						centerTabSet.selectTab(index);
					} else {
						mp.getCenterPanel().openPlatformAdminTab();
					}
				}
				if(event.getNode().getName().equals("Ensembl Databases")){
					boolean exists = false;
					int index = 0;
					
					TabSet centerTabSet = mp.getCenterPanel().getCenterTabSet();
					Tab[] tabs = mp.getCenterPanel().getCenterTabSet().getTabs();
					for(int i=0; i < tabs.length; i++){
						if(tabs[i].getTitle().equals("Ensembl Databases")){
							exists = true;
							index = i;
						}
					}
					if(exists){
						centerTabSet.selectTab(index);
					} else {
						mp.getCenterPanel().openEnsemblConfigTab();
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
							new TreeNode("Manage Platforms"),
							new TreeNode("Ensembl Databases"),
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
	
	private String[] intArrToStrArr(int[] intArr){
		
		String[] strArr = new String[intArr.length];
		
		for(int j = 0; j < intArr.length; j++){
			strArr[j] = String.valueOf(intArr[j]);
		}
		
		return strArr;
	}
	
	public FoConfigData buildFoConfig(){
			
				String globalLessThenThr;
				String globalGreaterThenThr;
				int[] globalCnvStati;
				
				/* Get global options. */
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
					
					if(statusSelectItem.getValues().length == 0){
						globalCnvStati = new int[0];
					} else {
						globalCnvStati = strArrToIntArr(statusSelectItem.getValues());
					}
				} else {
					globalLessThenThr = null;
					globalGreaterThenThr = null;
					globalCnvStati = new int[0];
				}
				
				FoTrackData[] trackData = new FoTrackData[tracks.size()];
				
				/* Get all track Options */
				for(int i = 0; i < tracks.size(); i++){
					trackData[i] = new FoTrackData();
					
					trackData[i].setTrackNumber(tracks.get(i).getTrackNumber());
					trackData[i].setTrackName(tracks.get(i).getTrackNameItem().getDisplayValue());
					
					trackData[i].addStrArray(Constants.DATA_TYPE, new String[]{tracks.get(i).getSelectItemFilterType().getValueAsString()});
					
					if(!globalThresholdCheckbox.getValueAsBoolean()){
						if(!tracks.get(i).getLessTextItem().getDisplayValue().equals("")){
							trackData[i].addStrArray(Constants.SEGMENT_MEAN, new String[]{tracks.get(i).getLessTextItem().getDisplayValue()});
						}
						if(!tracks.get(i).getGreaterTextItem().getDisplayValue().equals("")){
							trackData[i].addStrArray(Constants.SEGMENT_MEAN, new String[]{tracks.get(i).getGreaterTextItem().getDisplayValue()});
						}
						
						if(tracks.get(i).getStatusSelectItem().getValues().length != 0){
							trackData[i].addStrArray(Constants.CNV_STATI, tracks.get(i).getStatusSelectItem().getValues());
						}
					}
					if(tracks.get(i).getSelectItemProjects().getVisible()){
						
						String[] strArr = tracks.get(i).getSelectItemProjects().getValues();
						trackData[i].addStrArray(Constants.PROJECT_IDS, strArr);
					}
					
					if(tracks.get(i).getSelectItemTissues().getVisible()){
						
						String[] strArr = tracks.get(i).getSelectItemTissues().getValues();
						trackData[i].addStrArray(Constants.TISSUE_IDS, strArr);
					}
					
					if(tracks.get(i).getSelectItemExperiments().getVisible()){
						
						String[] strArr = tracks.get(i).getSelectItemExperiments().getValues();
						trackData[i].addStrArray(Constants.EXPERIMENT_IDS, strArr);
					}
					if(tracks.get(i).getSelectItemFilterType().getValueAsString().equals("Mutations")){
						if(tracks.get(i).getTextItemQuality().getVisible()){
							
							trackData[i].addStrArray(Constants.QUALTY_SCORE, new String[]{tracks.get(i).getTextItemQuality().getValueAsString()});
						}
						if(tracks.get(i).getSelectItemSomatic().getVisible()){
							
							String[] strArr = tracks.get(i).getSelectItemSomatic().getValues();
							trackData[i].addStrArray(Constants.SOMATIC, strArr);
						}
						if(tracks.get(i).getSelectItemConfidence().getVisible()){
							
							String[] strArr = tracks.get(i).getSelectItemConfidence().getValues();							
							trackData[i].addStrArray(Constants.CONFIDENCE, strArr);
						}
						if(tracks.get(i).getSelectItemSNPTool().getVisible()){
							
							String[] strArr = tracks.get(i).getSelectItemSNPTool().getValues();							
							trackData[i].addStrArray(Constants.SNP_TOOL, strArr);
						}
					}
				}
			
				/*create and set config object*/
				FoConfigData config = new FoConfigData();
				
				config.setTracks(trackData);
				
				if(globalLessThenThr != null){
					config.addStrArray(Constants.SEGMENT_MEAN, new String[]{globalLessThenThr});
				}
				if(globalGreaterThenThr != null){
					config.addStrArray(Constants.SEGMENT_MEAN, new String[]{globalGreaterThenThr});
				}
				if(globalCnvStati.length != 0){
					config.addStrArray(Constants.CNV_STATI, intArrToStrArr(globalCnvStati));
				}
				
				config.setName(saveConfigTextItem.getDisplayValue());
				
				config.addStrArray(Constants.ENSEMBL_ID, new String[]{ensemblSelectItem.getValueAsString()});
				config.addStrArray(Constants.ENSEMBL_BIOTYPES, biotypeSelectItem.getValues());
				config.addStrArray(Constants.SORTED_SEGMENTS, new String[]{String.valueOf(sortedCheckbox.getValueAsBoolean())});
				config.addStrArray(Constants.SHOW_SEGMENT_CAPTION, new String[]{String.valueOf(showCNVCaptionsCheckbox.getValueAsBoolean())});
				config.addStrArray(Constants.IS_GLOBAL_SEGMENT_TH, new String[]{String.valueOf(globalThresholdCheckbox.getValueAsBoolean())});
				
		return config;
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

			FoConfigData config = buildFoConfig();
			
			config.addStrArray("ensemblDBName", new String[]{ensemblSelectItem.getValueAsString()});
			config.addStrArray("ensemblDBLabel", new String[]{ensemblSelectItem.getDisplayValue()});
			
			try {
			
				newQuery = new QueryInfo(qryStr,
											typeStr,
											"png",
											mp.getCenterPanel().getWidth() - 30,
											config);
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