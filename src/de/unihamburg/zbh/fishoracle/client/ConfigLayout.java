package de.unihamburg.zbh.fishoracle.client;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.smartgwt.client.types.MultipleAppearance;
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
import com.smartgwt.client.widgets.form.fields.events.ClickEvent;
import com.smartgwt.client.widgets.form.fields.events.ClickHandler;
import com.smartgwt.client.widgets.form.fields.events.KeyPressEvent;
import com.smartgwt.client.widgets.form.fields.events.KeyPressHandler;
import com.smartgwt.client.widgets.layout.VLayout;

import de.unihamburg.zbh.fishoracle.client.data.FoConfigData;
import de.unihamburg.zbh.fishoracle.client.data.FoTrackData;
import de.unihamburg.zbh.fishoracle.client.data.GWTImageInfo;
import de.unihamburg.zbh.fishoracle.client.data.QueryInfo;
import de.unihamburg.zbh.fishoracle.client.datasource.ConfigDS;
import de.unihamburg.zbh.fishoracle.client.datasource.EnsemblDBDS;
import de.unihamburg.zbh.fishoracle.client.rpc.ConfigService;
import de.unihamburg.zbh.fishoracle.client.rpc.ConfigServiceAsync;
import de.unihamburg.zbh.fishoracle.client.rpc.Search;
import de.unihamburg.zbh.fishoracle.client.rpc.SearchAsync;
import de.unihamburg.zbh.fishoracle_db_api.data.Constants;

public class ConfigLayout extends VLayout {

	private MainPanel mp = null;
	
	private VLayout self;
	
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
	private SelectItem configSelectItem;
	
	private SelectItem statusSelectItem;
	
	private int numberOfTracks = 1;
	
	private ArrayList<Track> tracks = new ArrayList<Track>();
	
	public void removeAllTracks(){
		
		while(!tracks.isEmpty()){
			Track t = tracks.get(tracks.size() - 1);
			tracks.remove(tracks.size() - 1);
			numberOfTracks--;
			this.removeMember(t.getTrackForm());
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

	public TextItem getSearchTextItem() {
		return searchTextItem;
	}
	
	public void addTrack(FoTrackData td, boolean globalTh){
		
		Track t = new Track(numberOfTracks, globalTh, this);
		
		if(td != null){
			
			if(!globalThresholdCheckbox.getValueAsBoolean()){
				
				String dataType = td.getStrArray(Constants.DATA_TYPE)[0];
				
				if(dataType.equals("Segments (DNACopy)")){
				
					SelectItem segmentSelect = t.getSegmentThresholdSelectItem();
					TextItem greater = t.getLessTextItem();
					TextItem less = t.getGreaterTextItem();
				
					String segMean = td.getStrArray(Constants.SEGMENT_MEAN)[0];
				
					if(Double.parseDouble(segMean) > 0){
						segmentSelect.setValue("geater than");
						greater.setValue(segMean);
						greater.enable();
						less.setValue("");
						less.disable();
					} else {
						segmentSelect.setValue("less than");
						greater.setValue("");
						greater.disable();
						less.setValue(segMean);
						less.enable();
					}
					
					segmentSelect.setVisible(true);
					greater.setVisible(true);
					less.setVisible(true);
				}
				
				if(dataType.equals("Segments (PennCNV)")){
					
					t.getStatusSelectItem().setValues(td.getStrArray(Constants.CNV_STATI));
					t.getStatusSelectItem().setVisible(true);
				}
			}
			
			t.getTrackNameItem().setValue(td.getTrackName());
		 
			t.getSelectItemFilterType().setValue(td.getStrArray(Constants.DATA_TYPE)[0]);
			
			String[] pIds;
			String[] tIds;
			String[] eIds;
			double qualityScore;
			String[] somatic;
			String[] confidence;
			String[] snpTool;
		 
			if((pIds = td.getStrArray(Constants.PROJECT_IDS)) != null){
				t.getSelectItemProjects().setValues(pIds);
				t.getSelectItemProjects().setVisible(true);
			}
			
			if((tIds = td.getStrArray(Constants.TISSUE_IDS)) != null){
				t.getSelectItemTissues().setValues(tIds);
				t.getSelectItemTissues().setVisible(true);
			}
		 
			if((eIds = td.getStrArray(Constants.EXPERIMENT_IDS)) != null){
				t.getSelectItemExperiments().setValues(eIds);
				t.getSelectItemExperiments().setVisible(true);
			}
		 
			if(td.getStrArray(Constants.QUALTY_SCORE) != null){
				qualityScore = Double.parseDouble(td.getStrArray(Constants.QUALTY_SCORE)[0]);
				t.getTextItemQuality().setValue(qualityScore);
				t.getTextItemQuality().setVisible(true);
			}
			
			if((somatic = td.getStrArray(Constants.SOMATIC)) != null){
				t.getSelectItemSomatic().setValues(somatic);
				t.getSelectItemSomatic().setVisible(true);
			}
			
			if((confidence = td.getStrArray(Constants.CONFIDENCE)) != null){
				t.getSelectItemConfidence().setValues(confidence);
				t.getSelectItemConfidence().setVisible(true);
			}
			
			if((snpTool = td.getStrArray(Constants.SNP_TOOL)) != null){
				t.getSelectItemSNPTool().setValues(snpTool);
				t.getSelectItemConfidence().setVisible(true);
			}
		}
		
		tracks.add(t);
		numberOfTracks++;
		this.addMember(t.getTrackForm());
		this.redraw();
	}
	
	public ConfigLayout(MainPanel mainPanel){
		
		this.self = this;
		this.mp = mainPanel;
		
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
		
		loadConfigButton.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				
				int configId = Integer.parseInt(configSelectItem.getValueAsString());
				loadConfigData(configId);
			}
		});
		
		configSelectItem = new SelectItem();
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
		
		saveTracksButton.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				
				if(saveConfigTextItem.getDisplayValue().equals("")){
					SC.say("Configuration name needed!");
				} else {
					FoConfigData config = buildFoConfig();
					saveConfigData(config);
				}
			}
		});
		
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
				
					boolean globalTh = (Boolean)  globalThresholdCheckbox.getValue();
				
					addTrack(null, globalTh);
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
					self.removeMember(lastTrack.getTrackForm());
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
		
		this.addMember(searchForm);
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
	
	public void saveConfigData(FoConfigData foConf){
		
		final ConfigServiceAsync req = (ConfigServiceAsync) GWT.create(ConfigService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) req;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "ConfigService";
		endpoint.setServiceEntryPoint(moduleRelativeURL);
		final AsyncCallback<Void> callback = new AsyncCallback<Void>(){
			
			public void onSuccess(Void result){
			
				configSelectItem.fetchData();
				SC.say("Configuration saved.");
				
			}
			public void onFailure(Throwable caught){
				System.out.println(caught.getMessage());
				SC.say(caught.getMessage());
			}
		};
		req.add(foConf, callback);
	}
	
	public void loadConfigData(int configId){
		
		final ConfigServiceAsync req = (ConfigServiceAsync) GWT.create(ConfigService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) req;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "ConfigService";
		endpoint.setServiceEntryPoint(moduleRelativeURL);
		final AsyncCallback<FoConfigData> callback = new AsyncCallback<FoConfigData>(){
			
			public void onSuccess(FoConfigData result){
				//TODO
				
				ensemblSelectItem.setValue(result.getStrArray(Constants.ENSEMBL_ID)[0]);
				
				biotypeSelectItem.setValues(result.getStrArray(Constants.ENSEMBL_BIOTYPES));
				
				boolean globalTh =  Boolean.parseBoolean(result.getStrArray(Constants.IS_GLOBAL_SEGMENT_TH)[0]);
				
				if(globalTh){
					
					String segMean = result.getStrArray(Constants.SEGMENT_MEAN)[0];
					
					segmentDataSelectItem.show();
					greaterTextItem.show();
					lessTextItem.show();
					
					if(Double.parseDouble(segMean) > 0){
						segmentDataSelectItem.setValue("geater than");
						greaterTextItem.setValue(segMean);
						greaterTextItem.enable();
						lessTextItem.setValue("");
						lessTextItem.disable();
					} else {
						segmentDataSelectItem.setValue("less than");
						greaterTextItem.setValue("");
						greaterTextItem.disable();
						lessTextItem.setValue(segMean);
						lessTextItem.enable();
					}
				
					statusSelectItem.setValues(result.getStrArray(Constants.CNV_STATI));
					statusSelectItem.show();

				} else {
					segmentDataSelectItem.hide();
					greaterTextItem.hide();
					lessTextItem.hide();
					statusSelectItem.hide();
				}
				
				sortedCheckbox.setValue(Boolean.parseBoolean(result.getStrArray(Constants.SORTED_SEGMENTS)[0]));
				showCNVCaptionsCheckbox.setValue(Boolean.parseBoolean(result.getStrArray(Constants.SHOW_SEGMENT_CAPTION)[0]));
				globalThresholdCheckbox.setValue(Boolean.parseBoolean(result.getStrArray(Constants.IS_GLOBAL_SEGMENT_TH)[0]));
				
				removeAllTracks();
				
				FoTrackData[] tds = result.getTracks();
				
				for(int i = 0; i < tds.length; i++){
					addTrack(tds[i], globalTh);
				}
			}
			
			public void onFailure(Throwable caught){
				System.out.println(caught.getMessage());
				SC.say(caught.getMessage());
			}
		};
		
		req.fetch(configId, callback);
	}
}
