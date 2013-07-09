package de.unihamburg.zbh.fishoracle.client;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.smartgwt.client.types.MultipleAppearance;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.CheckboxItem;
import com.smartgwt.client.widgets.form.fields.RadioGroupItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.SpinnerItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.form.fields.events.ClickEvent;
import com.smartgwt.client.widgets.form.fields.events.ClickHandler;
import com.smartgwt.client.widgets.form.fields.events.KeyPressEvent;
import com.smartgwt.client.widgets.form.fields.events.KeyPressHandler;
import com.smartgwt.client.widgets.layout.VLayout;

import de.unihamburg.zbh.fishoracle.client.data.FoConfigData;
import de.unihamburg.zbh.fishoracle.client.data.FoConstants;
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
	
	private IntensitySpinnerItem thrItem;
	
	private TextItem saveConfigTextItem;
	private SelectItem configSelectItem;
	
	private SelectItem statusSelectItem;
	
	private TextItem trackPosTextItem;
	
	private int numberOfTracks = 1;
	
	private GWTImageInfo imgInfo;
	private Window win;
	
	private ArrayList<Track> tracks = new ArrayList<Track>();
	
	public void removeAllTracks(){
		
		while(!tracks.isEmpty()){
			Track t = tracks.get(tracks.size() - 1);
			tracks.remove(tracks.size() - 1);
			numberOfTracks--;
			this.removeMember(t.getTrackForm());
		}
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
	
	public TextItem getChrTextItem() {
		return chrTextItem;
	}

	public void setChrTextItem(TextItem chrTextItem) {
		this.chrTextItem = chrTextItem;
	}

	public TextItem getStartTextItem() {
		return startTextItem;
	}

	public void setStartTextItem(TextItem startTextItem) {
		this.startTextItem = startTextItem;
	}

	public TextItem getEndTextItem() {
		return endTextItem;
	}

	public void setEndTextItem(TextItem endTextItem) {
		this.endTextItem = endTextItem;
	}

	public RadioGroupItem getSearchRadioGroupItem() {
		return SearchRadioGroupItem;
	}

	public void setSearchRadioGroupItem(RadioGroupItem searchRadioGroupItem) {
		SearchRadioGroupItem = searchRadioGroupItem;
	}

	public GWTImageInfo getImgInfo() {
		return imgInfo;
	}

	public void setImgInfo(GWTImageInfo imgInfo) {
		this.imgInfo = imgInfo;
	}
	
	public Window getWin() {
		return win;
	}

	public void setWin(Window win) {
		this.win = win;
	}

	public void addTrack(FoTrackData td, boolean globalTh, final boolean newSearch, int trackPos){
		
		Track t = new Track(trackPos, globalTh, newSearch, this);
		
		if(td != null){
			
			if(!globalThresholdCheckbox.getValueAsBoolean()){
				
				String dataType = td.getStrArray(Constants.DATA_TYPE)[0];
				
				if(dataType.equals(FoConstants.CNV_INTENSITY)){
				
					SpinnerItem thrItem = t.getThrItem();
					
					String segMean = td.getStrArray(Constants.SEGMENT_MEAN)[0];
					
					thrItem.setValue(segMean);
					thrItem.setVisible(true);
				} else {
					t.getThrItem().setVisible(false);
				}
				
				if(dataType.equals(FoConstants.CNV_STATUS)){
					
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
				t.getRemoveProjectButtonItem().setVisible(true);
			}
			
			if((tIds = td.getStrArray(Constants.TISSUE_IDS)) != null){
				t.getSelectItemTissues().setValues(tIds);
				t.getSelectItemTissues().setVisible(true);
				t.getRemoveTissueButtonItem().setVisible(true);
			}
		 
			if((eIds = td.getStrArray(Constants.EXPERIMENT_IDS)) != null){
				t.getSelectItemExperiments().setValues(eIds);
				t.getSelectItemExperiments().setVisible(true);
				t.getRemoveExperimentsButtonItem().setVisible(true);
			}
		 
			if(td.getStrArray(Constants.QUALTY_SCORE) != null){
				qualityScore = Double.parseDouble(td.getStrArray(Constants.QUALTY_SCORE)[0]);
				t.getTextItemQuality().setValue(qualityScore);
				t.getTextItemQuality().setVisible(true);
				t.getRemoveQualityButtonItem().setVisible(true);
			}
			
			if((somatic = td.getStrArray(Constants.SOMATIC)) != null){
				t.getSelectItemSomatic().setValues(somatic);
				t.getSelectItemSomatic().setVisible(true);
				t.getRemoveSomaticButtonItem().setVisible(true);
			}
			
			if((confidence = td.getStrArray(Constants.CONFIDENCE)) != null){
				t.getSelectItemConfidence().setValues(confidence);
				t.getSelectItemConfidence().setVisible(true);
				t.getRemoveConfidenceButtonItem().setVisible(true);
			}
			
			if((snpTool = td.getStrArray(Constants.SNP_TOOL)) != null){
				t.getSelectItemSNPTool().setValues(snpTool);
				t.getSelectItemConfidence().setVisible(true);
				t.getRemoveSNPToolButtonItem().setVisible(true);
			}
			
			int red;
			int green;
			int blue;
			int alpha;
			
			red = Integer.parseInt(td.getStrArray(Constants.COLOR_RED)[0]);
			green = Integer.parseInt(td.getStrArray(Constants.COLOR_GREEN)[0]);
			blue = Integer.parseInt(td.getStrArray(Constants.COLOR_BLUE)[0]);
			alpha = Integer.parseInt(td.getStrArray(Constants.COLOR_ALPHA)[0]);
			
			t.getColorPicker().setRed(red);
			t.getColorPicker().setGreen(green);
			t.getColorPicker().setBlue(blue);
			t.getColorPicker().setAlpha(alpha);
			
			t.getColorPicker().updateButtonColor();
			t.getColorPicker().setCustom(Boolean.parseBoolean(td.getStrArray(Constants.COLOR_CUSTOM)[0]));
			
		}
		
		tracks.add(trackPos - 1, t);
		this.addMember(t.getTrackForm(), trackPos);
		
		if(trackPos < numberOfTracks){
			updateTrackNumbers(trackPos + 1, true);
		}
		
		numberOfTracks++;
		trackPosTextItem.setValue(numberOfTracks);
		this.redraw();
	}
	
	public ConfigLayout(MainPanel mainPanel, final boolean newSearch){
		
		this.self = this;
		this.mp = mainPanel;
		
		DynamicForm searchForm = new DynamicForm();
		
		searchTextItem = new TextItem();
		searchTextItem.setTitle("Search");
		searchTextItem.addKeyPressHandler(new KeyPressHandler(){
			
			@Override
			public void onKeyPress(KeyPressEvent event) {
				if(event.getKeyName().equals("Enter")){
					startSearch(newSearch);
				}
			}
		});
		if(!newSearch){
			searchTextItem.setDisabled(true);
		}
		
		chrTextItem = new TextItem();
		chrTextItem.setTitle("Chromosome");
		chrTextItem.setVisible(false);
		chrTextItem.addKeyPressHandler(new KeyPressHandler(){
			
			@Override
			public void onKeyPress(KeyPressEvent event) {
				if(event.getKeyName().equals("Enter")){
					startSearch(newSearch);
				}
			}
		});
		if(!newSearch){
			chrTextItem.setDisabled(true);
		}
		
		startTextItem = new TextItem();  
		startTextItem.setTitle("Start");
		startTextItem.setVisible(false);
		startTextItem.addKeyPressHandler(new KeyPressHandler(){
			
			@Override
			public void onKeyPress(KeyPressEvent event) {
				if(event.getKeyName().equals("Enter")){
					startSearch(newSearch);
				}
			}
		});
		if(!newSearch){
			startTextItem.setDisabled(true);
		}

		endTextItem = new TextItem();  
		endTextItem.setTitle("End");
		endTextItem.setVisible(false);
		endTextItem.addKeyPressHandler(new KeyPressHandler(){
			
			@Override
			public void onKeyPress(KeyPressEvent event) {
				if(event.getKeyName().equals("Enter")){
					startSearch(newSearch);
				}
			}
		});
		if(!newSearch){
			endTextItem.setDisabled(true);
		}
		
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
		if(!newSearch){
			SearchRadioGroupItem.setDisabled(true);
		}
		
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
		
		thrItem = new IntensitySpinnerItem();
		thrItem.addKeyPressHandler(new KeyPressHandler(){
			
			@Override
			public void onKeyPress(KeyPressEvent event) {
				if(event.getKeyName().equals("Enter")){
					startSearch(newSearch);
				}
			}
		});
		
		statusSelectItem = new SelectItem();
		statusSelectItem.setTitle("Status");
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
					startSearch(newSearch);
				}
			}
		});
		
		sortedCheckbox = new CheckboxItem();
		sortedCheckbox.setTitle("group by sample");
		sortedCheckbox.setValue(false);
		sortedCheckbox.addKeyPressHandler(new KeyPressHandler(){
			
			@Override
			public void onKeyPress(KeyPressEvent event) {
				if(event.getKeyName().equals("Enter")){
					startSearch(newSearch);
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
					startSearch(newSearch);
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
					thrItem.hide();
					statusSelectItem.hide();
					
					for(int i = 0; i < tracks.size(); i++){
						
						Track t = tracks.get(i);
						if(t.getSelectItemFilterType().getValueAsString().equals(FoConstants.CNV_INTENSITY)){
							t.getThrItem().show();
						}
						if(t.getSelectItemFilterType().getValueAsString().equals(FoConstants.CNV_STATUS)){
							t.getStatusSelectItem().show();
						}
						
					}
				}
				
				if((Boolean) event.getValue()){
					thrItem.show();
					statusSelectItem.show();
					
					for(int i = 0; i < tracks.size(); i++){
						
						Track t = tracks.get(i);
						t.getThrItem().hide();
						t.getStatusSelectItem().hide();
					}
				}
			}
		});
		globalThresholdCheckbox.addKeyPressHandler(new KeyPressHandler(){
			
			@Override
			public void onKeyPress(KeyPressEvent event) {
				if(event.getKeyName().equals("Enter")){
					startSearch(newSearch);
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
		if(newSearch){
			searchButton.setTitle("Search");
		} else {
			searchButton.setTitle("Refresh");
		}
		searchButton.setStartRow(false);
		searchButton.setEndRow(false);
		searchButton.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(
					com.smartgwt.client.widgets.form.fields.events.ClickEvent event) {
				startSearch(newSearch);
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
				
					addTrack(null, globalTh, newSearch, Integer.parseInt(trackPosTextItem.getValue().toString()));
			}
		});
		
		trackPosTextItem = new TextItem();
		trackPosTextItem.setTooltip("At Position");
		trackPosTextItem.setWidth(50);
		trackPosTextItem.setShowTitle(false);
		trackPosTextItem.setStartRow(false);
		trackPosTextItem.setEndRow(true);
		trackPosTextItem.setValue(numberOfTracks);
		
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
							thrItem,
							statusSelectItem,
							loadConfigButton,
							configSelectItem,
							saveTracksButton,
							saveConfigTextItem,
							addTrackButton,
							trackPosTextItem,
							searchButton);
		
		this.addMember(searchForm);
		
	}

	public void updateTrackNumbers(int pos, boolean add){
		for(int i = pos - 1; i < tracks.size(); i++){
			Track t = tracks.get(i);
			if(add){
				t.setTrackNumber(t.getTrackNumber() + 1);
			} else {
				t.setTrackNumber(t.getTrackNumber() - 1);
			}
			t.getTrackForm().setGroupTitle("Track" + t.getTrackNumber());
		}
	}
	
	public void removeTrack(int pos){
		if(tracks.size() > 1){
			Track track = tracks.get(pos - 1);
			tracks.remove(pos - 1);
			numberOfTracks--;
			self.removeMember(track.getTrackForm());
			updateTrackNumbers(pos, false);
			trackPosTextItem.setValue(numberOfTracks);
			
		} else {
			SC.say("You need at least one track to visualize your data!");
		}
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
	
	public void loadConfig(FoConfigData cd, boolean newSearch){
		
		ensemblSelectItem.setValue(cd.getStrArray(Constants.ENSEMBL_ID)[0]);
		
		biotypeSelectItem.setValues(cd.getStrArray(Constants.ENSEMBL_BIOTYPES));
		
		boolean globalTh =  Boolean.parseBoolean(cd.getStrArray(Constants.IS_GLOBAL_SEGMENT_TH)[0]);
		
		if(globalTh){
			
			String segMean = cd.getStrArray(Constants.SEGMENT_MEAN)[0];
			
			thrItem.show();
			thrItem.setValue(segMean);
		
			statusSelectItem.setValues(cd.getStrArray(Constants.CNV_STATI));
			statusSelectItem.show();

		} else {
			thrItem.hide();
			statusSelectItem.hide();
		}
		
		sortedCheckbox.setValue(Boolean.parseBoolean(cd.getStrArray(Constants.SORTED_SEGMENTS)[0]));
		showCNVCaptionsCheckbox.setValue(Boolean.parseBoolean(cd.getStrArray(Constants.SHOW_SEGMENT_CAPTION)[0]));
		globalThresholdCheckbox.setValue(Boolean.parseBoolean(cd.getStrArray(Constants.IS_GLOBAL_SEGMENT_TH)[0]));
		
		removeAllTracks();
		
		FoTrackData[] tds = cd.getTracks();
		
		for(int i = 0; i < tds.length; i++){
			addTrack(tds[i], globalTh, newSearch, tds[i].getTrackNumber());
		}
	}
	
	public FoConfigData buildFoConfig(){
			
				String globalThr = null;
				int[] globalCnvStati;
				
				/* Get global options. */
				if(globalThresholdCheckbox.getValueAsBoolean()){
					
					if(thrItem.isVisible()){
						globalThr = thrItem.getDisplayValue();
					}
					
					if(statusSelectItem.getValues().length == 0){
						globalCnvStati = new int[0];
					} else {
						globalCnvStati = strArrToIntArr(statusSelectItem.getValues());
					}
				} else {
					globalThr = null;
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
						if(tracks.get(i).getThrItem().isVisible()){
							trackData[i].addStrArray(Constants.SEGMENT_MEAN, new String[]{tracks.get(i).getThrItem().getDisplayValue()});
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
					if(tracks.get(i).getSelectItemFilterType().getValueAsString().equals(FoConstants.SNV)){
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
					
					FoColorPickerItem cp  = tracks.get(i).getColorPicker();
					
					trackData[i].addStrArray(Constants.COLOR_CUSTOM, new String[]{new Boolean(cp.isCustom()).toString()});
					
					if(cp.isCustom()){
						String[] red = new String[]{new Integer(cp.getRed()).toString()};
						trackData[i].addStrArray(Constants.COLOR_RED, red);
						
						String[] green = new String[]{new Integer(cp.getGreen()).toString()};
						trackData[i].addStrArray(Constants.COLOR_GREEN, green);
					
						String[] blue = new String[]{new Integer(cp.getBlue()).toString()};
						trackData[i].addStrArray(Constants.COLOR_BLUE, blue);
						
						String[] alpha = new String[]{new Integer(cp.getAlpha()).toString()};
						trackData[i].addStrArray(Constants.COLOR_ALPHA, alpha);
						
					} else {
						
						if(trackData[i].getStrArray(Constants.DATA_TYPE)[0].equals(FoConstants.CNV_INTENSITY)){
							
							double thr;
							
							if(!globalThresholdCheckbox.getValueAsBoolean()){
								thr = Double.parseDouble(trackData[i].getStrArray(Constants.SEGMENT_MEAN)[0]);
							} else {
								thr = Double.parseDouble(globalThr);
							}
							
							if(thr <= 0){
								trackData[i].addStrArray(Constants.COLOR_RED, new String[]{"0"});
								trackData[i].addStrArray(Constants.COLOR_GREEN, new String[]{"0"});
								trackData[i].addStrArray(Constants.COLOR_BLUE, new String[]{"255"});
								trackData[i].addStrArray(Constants.COLOR_ALPHA, new String[]{"70"});
							} else {
								trackData[i].addStrArray(Constants.COLOR_RED, new String[]{"255"});
								trackData[i].addStrArray(Constants.COLOR_GREEN, new String[]{"0"});
								trackData[i].addStrArray(Constants.COLOR_BLUE, new String[]{"0"});
								trackData[i].addStrArray(Constants.COLOR_ALPHA, new String[]{"70"});
							}
						}
						else if(trackData[i].getStrArray(Constants.DATA_TYPE)[0].equals(FoConstants.CNV_STATUS)){
							trackData[i].addStrArray(Constants.COLOR_RED, new String[]{"0"});
							trackData[i].addStrArray(Constants.COLOR_GREEN, new String[]{"0"});
							trackData[i].addStrArray(Constants.COLOR_BLUE, new String[]{"128"});
							trackData[i].addStrArray(Constants.COLOR_ALPHA, new String[]{"70"});
						}
						else if(trackData[i].getStrArray(Constants.DATA_TYPE)[0].equals(FoConstants.SNV)){
							trackData[i].addStrArray(Constants.COLOR_RED, new String[]{"0"});
							trackData[i].addStrArray(Constants.COLOR_GREEN, new String[]{"0"});
							trackData[i].addStrArray(Constants.COLOR_BLUE, new String[]{"0"});
							trackData[i].addStrArray(Constants.COLOR_ALPHA, new String[]{"0"});
						}
						else {
							trackData[i].addStrArray(Constants.COLOR_RED, new String[]{"128"});
							trackData[i].addStrArray(Constants.COLOR_GREEN, new String[]{"128"});
							trackData[i].addStrArray(Constants.COLOR_BLUE, new String[]{"128"});
							trackData[i].addStrArray(Constants.COLOR_ALPHA, new String[]{"100"});
						}
					}
				}
			
				/*create and set config object*/
				FoConfigData config = new FoConfigData();
				
				config.setTracks(trackData);
				
				if(globalThr != null){
					config.addStrArray(Constants.SEGMENT_MEAN, new String[]{globalThr});
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
	
	public void startSearch(boolean newSearch){
		
		searchTextItem.focusInItem(); //Workaround to force an update of the spinner items display value.
		
		String typeStr = null;
		String qryStr = null;
		
		String name;
		boolean nondup = true;
		
		HashMap<String, String> names = new HashMap<String, String>();
		
		for(int i = 0; i < tracks.size(); i++){
			
			name = tracks.get(i).getTrackNameItem().getDisplayValue();
			
			if(names.get(name) == null){
				names.put(name, "1");
			} else {
				nondup = false;
				SC.say("Track names cannot be duplicate! " + name);
				break;
			}
		}
		
		if(nondup){
		
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
			
				if(newSearch){
					search(newQuery);
				} else {
					imgInfo.setQuery(newQuery);
					win.destroy();
					mp.getCenterPanel().refreshRange();
				}
			}
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
				
				loadConfig(result, true);
			}
			
			public void onFailure(Throwable caught){
				System.out.println(caught.getMessage());
				SC.say(caught.getMessage());
			}
		};
		
		req.fetch(configId, callback);
	}
}
