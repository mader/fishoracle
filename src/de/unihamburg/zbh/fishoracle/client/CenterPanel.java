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

import java.util.LinkedHashMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;

import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.Cursor;
import com.smartgwt.client.types.ListGridEditEvent;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.Side;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Img;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;

import com.smartgwt.client.widgets.events.ResizedEvent;
import com.smartgwt.client.widgets.events.ResizedHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.ComboBoxItem;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.HeaderItem;
import com.smartgwt.client.widgets.form.fields.LinkItem;
import com.smartgwt.client.widgets.form.fields.PasswordItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.TextAreaItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.form.fields.events.KeyPressEvent;
import com.smartgwt.client.widgets.form.fields.events.KeyPressHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.CellClickEvent;
import com.smartgwt.client.widgets.grid.events.CellClickHandler;
import com.smartgwt.client.widgets.grid.events.RecordClickEvent;
import com.smartgwt.client.widgets.grid.events.RecordClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.LayoutSpacer;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.menu.MenuItem;
import com.smartgwt.client.widgets.menu.events.MenuItemClickEvent;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;
import com.smartgwt.client.widgets.tab.events.CloseClickHandler;
import com.smartgwt.client.widgets.tab.events.TabCloseClickEvent;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;
import com.smartgwt.client.widgets.toolbar.ToolStripMenuButton;

import de.unihamburg.zbh.fishoracle.client.data.DBConfigData;
import de.unihamburg.zbh.fishoracle.client.data.FoChip;
import de.unihamburg.zbh.fishoracle.client.data.FoCnSegment;
import de.unihamburg.zbh.fishoracle.client.data.FoGroup;
import de.unihamburg.zbh.fishoracle.client.data.FoProject;
import de.unihamburg.zbh.fishoracle.client.data.FoProperty;
import de.unihamburg.zbh.fishoracle.client.data.GWTImageInfo;
import de.unihamburg.zbh.fishoracle.client.data.Gen;
import de.unihamburg.zbh.fishoracle.client.data.MicroarrayOptions;
import de.unihamburg.zbh.fishoracle.client.data.RecMapInfo;
import de.unihamburg.zbh.fishoracle.client.data.FoUser;
import de.unihamburg.zbh.fishoracle.client.datasource.ChipDS;
import de.unihamburg.zbh.fishoracle.client.datasource.CnSegmentDS;
import de.unihamburg.zbh.fishoracle.client.datasource.MicroarrayStudyDS;
import de.unihamburg.zbh.fishoracle.client.datasource.OperationId;
import de.unihamburg.zbh.fishoracle.client.datasource.OrganDS;
import de.unihamburg.zbh.fishoracle.client.datasource.ProjectAccessDS;
import de.unihamburg.zbh.fishoracle.client.datasource.ProjectDS;
import de.unihamburg.zbh.fishoracle.client.ImgCanvas;
import de.unihamburg.zbh.fishoracle.client.rpc.Admin;
import de.unihamburg.zbh.fishoracle.client.rpc.AdminAsync;
import de.unihamburg.zbh.fishoracle.client.rpc.Search;
import de.unihamburg.zbh.fishoracle.client.rpc.SearchAsync;
import de.unihamburg.zbh.fishoracle.client.rpc.UserService;
import de.unihamburg.zbh.fishoracle.client.rpc.UserServiceAsync;

public class CenterPanel extends VLayout{

	private ListGrid userGrid;
	private ListGrid organGrid;
	private ListGrid propertyGrid;
	private ListGrid msGrid;
	private ListGrid chipGrid;
	private ListGrid groupGrid;
	private ListGrid groupUserGrid;
	private ListGrid segmentGrid;
	private SelectItem userSelectItem;
	private SelectItem projectSelectItem;
	
	private TextItem useIdTextItem;
	private TextItem useNameTextItem;
	private TextItem userFirstNameTextItem;
	private TextItem userLastNameTextItem;
	private TextItem userEmailTextItem;
	private PasswordItem userPwItem;
	private PasswordItem userPwConfirmItem;
	private PasswordItem newPwPasswordItem;
	
	private ListGrid projectGrid;
	private ListGrid projectMstudyGrid;
	private TextItem projectNameTextItem;
	private TextAreaItem projectDescriptionItem;
	private ListGrid projectAccessGrid;
	private SelectItem accessRightSelectItem;
	private SelectItem groupSelectItem;
	
	private TextItem groupNameTextItem;
	private TextItem organLabelTextItem;
	private ComboBoxItem organTypeCbItem;
	private TextItem propertyLabelTextItem;
	private ComboBoxItem propertyTypeCbItem;
	private TextItem chipLabelTextItem;
	private ComboBoxItem chipTypeCbItem;
	
	private TabSet centerTabSet = null;
	private TextItem chrTextItem;
	private TextItem startTextItem;
	private TextItem endTextItem;
	private SelectItem trackItem;
	private TextItem lowerThTextItem;
	private TextItem upperThTextItem;
	
	private FormPanel uploadForm;
	private FileUpload fu;
	private DynamicForm metaDataForm;
	private TextItem studyName;
	private SelectItem chip;
	private SelectItem tissue;
	private SelectItem project;
	private TextAreaItem descriptionItem;
	private ButtonItem submitNewMstudyButton;
	
	private TextItem ensemblHost;
    private TextItem ensemblPort;
    private TextItem ensemblDatabase;
    private TextItem ensemblUser;
    private TextItem ensemblPW;
    private TextItem fishoracleHost;
    private TextItem fishoracleDatabase;
    private TextItem fishoracleUser;
    private TextItem fishoraclePW;
	
	@SuppressWarnings("unused")
	private MainPanel mp = null;
	private CenterPanel cp = null;
	
	public CenterPanel(MainPanel mainPanel) {
		
		mp = mainPanel;
		cp = this;
		
		centerTabSet = new TabSet();
		centerTabSet.setTabBarPosition(Side.TOP);
		centerTabSet.setTabBarAlign(Side.LEFT);
		
		Tab welcomeTab = new Tab("Welcome"); 
		VLayout welcomeLayout = new VLayout();
		welcomeLayout.setContents("" +
        		"<center><h1>FISH Oracle</h1></center>" +
        		"<p id=\"welcome\">You can search for copy number segments by giving a CNC (copy number change) stable ID" +
        		" e.g. 'CNC100' or for a gene specified by a gene name e.g. 'kras'" +
        		" or a karyoband giving the exact karyoband identifier e.g. '8q21.3'." +
        		" By clicking on an element a window opens that shows additional information." +
        		"</p><p id=\"welcome\"><br>FISH Oracle uses:<br> " +
        		"<ul id=\"welcome\">" +
        		"<li> the Google Web Toolkit <a href=\"http://code.google.com/webtoolkit/\" target=_blank>http://code.google.com/webtoolkit/</a></li>" +
        		"<li> the Ensembl human core database <a href=\"http://www.ensembl.org\" target=_blank>http://www.ensembl.org</a></li>" +
        		"<li> AnnotationSketch of the GenomeTools <a href=\"http://www.genometools.org\" target=_blank>http://www.genometools.org</a></li>" +
        		"</ul></p>");
		
		welcomeTab.setPane(welcomeLayout);
		
		centerTabSet.addTab(welcomeTab);
		
		this.addResizedHandler(new ImageFrameResizedHandler(cp));
		
		this.addMember(centerTabSet);
	}

	public TabSet getCenterTabSet() {
		return centerTabSet;
	}

	private void refreshRange(){
		ImgCanvas imgLayer = (ImgCanvas) cp.getCenterTabSet().getSelectedTab().getPane().getChildren()[1];
		GWTImageInfo imgInfo = imgLayer.getImageInfo();
		
		String newChr;
	    int newStart;
	    int newEnd;
	    String newLowerTh;
	    String newUpperTh;
	    
		newChr = chrTextItem.getDisplayValue();
	    
	    newStart = Integer.parseInt(startTextItem.getDisplayValue());
	    
	    newEnd = Integer.parseInt(endTextItem.getDisplayValue());
	    
	    newLowerTh = lowerThTextItem.getDisplayValue();
	    
	    newUpperTh = upperThTextItem.getDisplayValue();
	    
	    if(newStart >= newEnd || newEnd - newStart <= 10){
	    	
	    	SC.say("The end value must at least be 10 base pairs greater than the start value!");
	    	
	    } else {
	    
	    	imgInfo.setChromosome(newChr);
	    	
	    	imgInfo.setStart(newStart);
	    
	    	imgInfo.setEnd(newEnd);

	    	try {
	    		if(((String) trackItem.getValue()).equals("Global threshold")){
	    			
	    			if(newLowerTh.equals("")){
	    				imgInfo.getQuery().setGlobalLowerTh(null);
	    			} else {
	    				imgInfo.getQuery().setGlobalLowerTh(newLowerTh);
	    			}
	    			if(newUpperTh.equals("")){
	    				imgInfo.getQuery().setGlobalUpperTh(null);
	    			} else {
	    				imgInfo.getQuery().setGlobalUpperTh(newUpperTh);
	    			}
	    			
	    		} else {
	    			int trackNumber = Integer.parseInt((String) trackItem.getValue());
	    			if(newLowerTh.equals("")){
	    				imgInfo.getQuery().getTracks()[trackNumber - 1].setLowerTh(null);
	    			} else {
	    				imgInfo.getQuery().getTracks()[trackNumber - 1].setLowerTh(newLowerTh);
	    			}
	    			if(newUpperTh.equals("")){
	    				imgInfo.getQuery().getTracks()[trackNumber - 1].setUpperTh(null);
	    			} else {
	    				imgInfo.getQuery().getTracks()[trackNumber - 1].setUpperTh(newUpperTh);
	    			}
	    		}
			} catch (Exception e) {
				SC.say(e.getMessage());
			}
	    	
	    	cp.imageRedraw(imgInfo);
	    }
	}
	
	public ImgCanvas createImageLayer(GWTImageInfo imgInfo){
		
		ImgCanvas image = new ImgCanvas(imgInfo);
		image.setOverflow(Overflow.HIDDEN);
        image.setWidth(imgInfo.getWidth());
        image.setHeight(imgInfo.getHeight());
        image.setAppImgDir("/");
		
		int rmc;
		
		for(rmc=0; rmc < imgInfo.getRecmapinfo().size(); rmc++){
			
			final Img spaceImg = new Img("1pximg.gif");
			
			spaceImg.addClickHandler(new RecMapClickHandler(imgInfo.getRecmapinfo().get(rmc), this));
			
			int southeast_x = (int) imgInfo.getRecmapinfo().get(rmc).getSoutheastX();
			
			int northwest_x = (int) imgInfo.getRecmapinfo().get(rmc).getNorthwestX();
			
			if(southeast_x > imgInfo.getWidth()){
				spaceImg.setWidth(imgInfo.getWidth() - northwest_x);
			} else {
				spaceImg.setWidth(southeast_x - northwest_x);
			}
			
			if(spaceImg.getWidth() <= 0){
				spaceImg.setWidth(1);
			}
			
			int southeast_y = (int) imgInfo.getRecmapinfo().get(rmc).getSoutheastY();
			
			int northwest_y = (int) imgInfo.getRecmapinfo().get(rmc).getNorthwestY();
			
			spaceImg.setHeight(southeast_y - northwest_y);
			
			spaceImg.setLeft( (int) imgInfo.getRecmapinfo().get(rmc).getNorthwestX());
			
			spaceImg.setTop( (int) imgInfo.getRecmapinfo().get(rmc).getNorthwestY());
			
			spaceImg.setCursor(Cursor.HAND);
			
			image.addChild(spaceImg);
			
		}
		
		return image;
	}
	
	public void newImageTab(final GWTImageInfo imgInfo){
		
		Tab imgTab = new Tab(imgInfo.getQuery().getQueryString()); 
		imgTab.setCanClose(true);
		
		VLayout presentationLayer = new VLayout();
		presentationLayer.setDefaultLayoutAlign(VerticalAlignment.TOP);
		
		/*Toolbar*/
		ToolStrip presentationToolStrip = new ToolStrip();
		
		presentationToolStrip.setWidth100();
		
		/*scrolling to the left and the right on the chromosome*/
		ToolStripButton scrollLeftButton = new ToolStripButton();
		scrollLeftButton.setTooltip("Scroll left");
		scrollLeftButton.addClickHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				ImgCanvas imgLayer = (ImgCanvas) cp.getCenterTabSet().getSelectedTab().getPane().getChildren()[1];
				GWTImageInfo imgInfo = imgLayer.getImageInfo();
				
				int range;
				int percRange;
				int perc = 10;
				int newStart;
				int newEnd;
		
				range = imgInfo.getEnd() - imgInfo.getStart(); 
		
				percRange = range * perc / 100;
	    
				newStart = imgInfo.getStart() - percRange;
	    
				newEnd = imgInfo.getEnd() - percRange;
	    
				if(newStart > 0){
	    
					imgInfo.setStart(newStart);
	    
					imgInfo.setEnd(newEnd);
		
					cp.imageRedraw(imgInfo);
	    	
				} else {
					SC.say("You have reached the chromsomes end ...");
				}
				
			}
			
		});
		scrollLeftButton.setIcon("[APP]/icons/arrow_left.png");
		scrollLeftButton.setAppImgDir("/");
		presentationToolStrip.addButton(scrollLeftButton);
		
		Label scrollLabel = new Label("Scroll");
		scrollLabel.setWidth(20);
		presentationToolStrip.addMember(scrollLabel);
		
		ToolStripButton scrollRightButton = new ToolStripButton();
		scrollRightButton.setTooltip("Scroll right");
		scrollRightButton.addClickHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				ImgCanvas imgLayer = (ImgCanvas) cp.getCenterTabSet().getSelectedTab().getPane().getChildren()[1];
				GWTImageInfo imgInfo = imgLayer.getImageInfo();
				
				int range;
    			int percRange;
    			int perc = 10;
    			int newStart;
    			int newEnd;
    			
    			range = imgInfo.getEnd() - imgInfo.getStart(); 
    			
    		    percRange = range * perc / 100;
    			
    		    
    		    newStart = imgInfo.getStart() + percRange;
    		    
    		    newEnd = imgInfo.getEnd() + percRange;
    		    
    		    imgInfo.setStart(newStart);
    		    
    		    imgInfo.setEnd(newEnd);
    		    
    		    cp.imageRedraw(imgInfo);
			}
		});
		
		scrollRightButton.setIcon("[APP]/icons/arrow_right.png");
		scrollRightButton.setAppImgDir("/");
		presentationToolStrip.addButton(scrollRightButton);
		presentationToolStrip.addSeparator();
		
		/*zoomin in and out*/
		ToolStripButton zoomInButton = new ToolStripButton();
		zoomInButton.setTooltip("Zoom in");
		zoomInButton.addClickHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				ImgCanvas imgLayer = (ImgCanvas) cp.getCenterTabSet().getSelectedTab().getPane().getChildren()[1];
				GWTImageInfo imgInfo = imgLayer.getImageInfo();
				
				int range;
    			int percRange;
    			int perc = 10;
    			int newStart;
    			int newEnd;
    			
    			range = imgInfo.getEnd() - imgInfo.getStart(); 
    			
    		    percRange = range * perc / 100;
    			
    		    
    		    newStart = imgInfo.getStart() + percRange;
    		    
    		    newEnd = imgInfo.getEnd() - percRange;
    		    
    		    if(newEnd - newStart > 10){
    		    
    		    	imgInfo.setStart(newStart);
    		    
    		    	imgInfo.setEnd(newEnd);
    			
    		    	cp.imageRedraw(imgInfo);
    		    	
    		    } else {
    		    	SC.say("You have reached the highest zoom level ...");
    		    }
			}
		});
		
		zoomInButton.setIcon("[APP]/icons/zoom_in.png");
		zoomInButton.setAppImgDir("/");
		presentationToolStrip.addButton(zoomInButton);
		
		Label zoomLabel = new Label("Zoom");
		zoomLabel.setWidth(20);
		presentationToolStrip.addMember(zoomLabel);
		
		ToolStripButton zoomOutButton = new ToolStripButton();
		zoomOutButton.setTooltip("Zoom out");
		zoomOutButton.addClickHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				ImgCanvas imgLayer = (ImgCanvas) cp.getCenterTabSet().getSelectedTab().getPane().getChildren()[1];
				GWTImageInfo imgInfo = imgLayer.getImageInfo();
				
				int range;
    			int percRange;
    			int perc = 10;
    			int newStart;
    			int newEnd;
    			
    			range = imgInfo.getEnd() - imgInfo.getStart(); 
    			
    		    percRange = range * perc / 100;
    			
    		    
    		    newStart = imgInfo.getStart() - percRange;
    		    
    		    newEnd = imgInfo.getEnd() + percRange;
    		    
    		    
    		    if(newStart < 0){
    		    	
    		    	newEnd = newEnd - newStart;
    		    	newStart = 0;
    		    }
    		    
    		    imgInfo.setStart(newStart);
    		    
    		    imgInfo.setEnd(newEnd);
    			
    		    cp.imageRedraw(imgInfo);
			}
		});
		zoomOutButton.setIcon("[APP]/icons/zoom_out.png");
		zoomOutButton.setAppImgDir("/");
		presentationToolStrip.addButton(zoomOutButton);
		
		presentationToolStrip.addSeparator();
		
		/*display exact chromosome range*/
		
		chrTextItem = new TextItem();
		chrTextItem.setTitle("Chr");
		chrTextItem.setTooltip("Set the chromsome");
		chrTextItem.setWidth(25);
		chrTextItem.setValue(imgInfo.getChromosome());
		chrTextItem.addKeyPressHandler(new KeyPressHandler(){

			@Override
			public void onKeyPress(KeyPressEvent event) {
				if(event.getKeyName().equals("Enter")){
					refreshRange();
				}
			}
		});
		presentationToolStrip.addFormItem(chrTextItem);
		
		startTextItem = new TextItem();
		startTextItem.setTitle("Start");
		startTextItem.setTooltip("Set start position");
		startTextItem.setWidth(70);
		startTextItem.setValue(imgInfo.getStart());
		startTextItem.addKeyPressHandler(new KeyPressHandler(){

			@Override
			public void onKeyPress(KeyPressEvent event) {
				if(event.getKeyName().equals("Enter")){
					refreshRange();
				}
			}
		});
		presentationToolStrip.addFormItem(startTextItem);
		
		endTextItem = new TextItem();
		endTextItem.setTitle("End");
		endTextItem.setTooltip("Set end position");
		endTextItem.setWidth(70);
		endTextItem.setValue(imgInfo.getEnd());
		
		endTextItem.addKeyPressHandler(new KeyPressHandler(){

			@Override
			public void onKeyPress(KeyPressEvent event) {
				if(event.getKeyName().equals("Enter")){
					refreshRange();
				}
			}
		});
		presentationToolStrip.addFormItem(endTextItem);
		
		presentationToolStrip.addSeparator();
		
		trackItem = new SelectItem();
		trackItem.setShowTitle(false);
		trackItem.setTooltip("Select the track for which the intensity" +
							" thresholds should be displayed.");
		trackItem.setWidth(80);
		
		LinkedHashMap<String, String> valueMap = new LinkedHashMap<String, String>();
		
		if(imgInfo.getQuery().isGlobalTh()){
			trackItem.setValueMap("Global threshold");
		} else {
		
			for(int i=0; i< imgInfo.getQuery().getTracks().length; i++){
				valueMap.put(new Integer(imgInfo.getQuery().getTracks()[i].getTrackNumber()).toString(),
								imgInfo.getQuery().getTracks()[i].getTrackName());
			}
			trackItem.setValueMap(valueMap);
		}
		
		trackItem.setDefaultToFirstOption(true);
		trackItem.addChangedHandler(new ChangedHandler(){

			@Override
			public void onChanged(ChangedEvent event) {
				
				ImgCanvas imgLayer = (ImgCanvas) cp.getCenterTabSet().getSelectedTab().getPane().getChildren()[1];
				GWTImageInfo imgInfo = imgLayer.getImageInfo();
				
				lowerThTextItem.setValue(imgInfo.getQuery().getTracks()[Integer.parseInt((String) event.getItem().getValue()) - 1].getLowerTh());
				lowerThTextItem.redraw();
				upperThTextItem.setValue(imgInfo.getQuery().getTracks()[Integer.parseInt((String) event.getItem().getValue()) - 1].getUpperTh());
				upperThTextItem.redraw();
			}
		});
		
		presentationToolStrip.addFormItem(trackItem);
		
		lowerThTextItem = new TextItem();
		lowerThTextItem.setTooltip("Set the lower threshold for the selcted track.");
		lowerThTextItem.setTitle("Less Than");
		lowerThTextItem.setWrapTitle(false);
		lowerThTextItem.setWidth(40);
		if(imgInfo.getQuery().isGlobalTh()){
			lowerThTextItem.setValue(imgInfo.getQuery().getGlobalLowerTh());
		} else {
			lowerThTextItem.setValue(imgInfo.getQuery().getTracks()[Integer.parseInt((String)trackItem.getValue()) - 1].getLowerTh());
		}
		lowerThTextItem.addKeyPressHandler(new KeyPressHandler(){

			@Override
			public void onKeyPress(KeyPressEvent event) {
				if(event.getKeyName().equals("Enter")){
					refreshRange();
				}
			}
		});
		presentationToolStrip.addFormItem(lowerThTextItem);
		
		upperThTextItem = new TextItem();
		upperThTextItem.setTooltip("Set the upper threshold for the selected track.");
		upperThTextItem.setTitle("Greater Than");
		upperThTextItem.setWrapTitle(false);
		upperThTextItem.setWidth(40);
		if(imgInfo.getQuery().isGlobalTh()){
			upperThTextItem.setValue(imgInfo.getQuery().getGlobalUpperTh());
		} else { 
			upperThTextItem.setValue(imgInfo.getQuery().getTracks()[Integer.parseInt((String) trackItem.getValue()) - 1].getUpperTh());
		}
		upperThTextItem.addKeyPressHandler(new KeyPressHandler(){

			@Override
			public void onKeyPress(KeyPressEvent event) {
				if(event.getKeyName().equals("Enter")){
					refreshRange();
				}
			}
		});
		presentationToolStrip.addFormItem(upperThTextItem);
		
		ToolStripButton refreshButton = new ToolStripButton();
		refreshButton.setTooltip("Refresh image");
		refreshButton.addClickHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				refreshRange();
			}
		});
		refreshButton.setIcon("[APP]/icons/arrow_refresh.png");
		refreshButton.setAppImgDir("/");
		presentationToolStrip.addButton(refreshButton);
		
		presentationToolStrip.addSeparator();
		
		/*menu for more actions*/
		Menu exportMenu = new Menu();
		
		MenuItem excelExportItem = new MenuItem("Export image as excel document");
		MenuItem pdfExportItem = new MenuItem("Export image as pdf document");
		MenuItem psExportItem = new MenuItem("Export image as ps document");
		MenuItem svgExportItem = new MenuItem("Export image as svg document");
		MenuItem pngExportItem = new MenuItem("Export image as png document");
		
		excelExportItem.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler(){

			public void onClick(MenuItemClickEvent event) {
				//GWTImageInfo imgInfo = ((ImgCanvas) cp.getCenterTabSet().getSelectedTab().getPane().getChildren()[1]).getImageInfo();
				//cp.exportExcel(imgInfo);
				SC.say("This function is currently not supported.");
			}
		});
		
		pdfExportItem.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler(){

			public void onClick(MenuItemClickEvent event) {
				GWTImageInfo imgInfo = ((ImgCanvas) cp.getCenterTabSet().getSelectedTab().getPane().getChildren()[1]).getImageInfo();
				
				GWTImageInfo newImgInfo = imgInfo.clone();
				
				newImgInfo.getQuery().setImageType("pdf");
				
				cp.exportImage(newImgInfo);
			}
		});
		
		psExportItem.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler(){

			public void onClick(MenuItemClickEvent event) {
				GWTImageInfo imgInfo = ((ImgCanvas) cp.getCenterTabSet().getSelectedTab().getPane().getChildren()[1]).getImageInfo();
				

				GWTImageInfo newImgInfo = imgInfo.clone();
				
				newImgInfo.getQuery().setImageType("ps");
				
				cp.exportImage(newImgInfo);
			}
		});
		
		svgExportItem.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler(){

			public void onClick(MenuItemClickEvent event) {
				GWTImageInfo imgInfo = ((ImgCanvas) cp.getCenterTabSet().getSelectedTab().getPane().getChildren()[1]).getImageInfo();
				
				GWTImageInfo newImgInfo = imgInfo.clone();
				
				newImgInfo.getQuery().setImageType("svg");
				
				cp.exportImage(newImgInfo);
			}
		});
		
		pngExportItem.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler(){

			public void onClick(MenuItemClickEvent event) {
				GWTImageInfo imgInfo = ((ImgCanvas) cp.getCenterTabSet().getSelectedTab().getPane().getChildren()[1]).getImageInfo();
				
				GWTImageInfo newImgInfo = imgInfo.clone();
				
				newImgInfo.getQuery().setImageType("png");
								
				cp.exportImage(newImgInfo);
			}
		});
		
		exportMenu.setItems(excelExportItem, pdfExportItem, psExportItem, svgExportItem, pngExportItem);
		
		ToolStripMenuButton exportMenuButton = new ToolStripMenuButton("Export", exportMenu);
		
		presentationToolStrip.addMenuButton(exportMenuButton);
		
		presentationLayer.addMember(presentationToolStrip);
		
		ImgCanvas image = createImageLayer(imgInfo);
		
		image.setChromosome(chrTextItem);
		image.setStart(startTextItem);
		image.setEnd(endTextItem);
		
		presentationLayer.addMember(image);
		
		imgTab.setPane(presentationLayer);
		
		centerTabSet.addTab(imgTab);
	
		centerTabSet.selectTab(imgTab);
				
	}
	
	public void loadWindow(FoCnSegment segmentData){
		
		Window window = new Window();
		window.setTitle("Segment " + segmentData.getId());
		window.setWidth(500);
		window.setHeight(330);
		window.setAutoCenter(true);
		window.setCanDragResize(true);
		
		final ListGrid cncGrid = new ListGrid();
		cncGrid.setWidth100();
		cncGrid.setHeight100();
		cncGrid.setShowAllRecords(true);
		cncGrid.setAlternateRecordStyles(true);
		cncGrid.setShowHeader(false);
		cncGrid.setWrapCells(true);
		cncGrid.setCellHeight(40);
		cncGrid.setCanEdit(true);
		cncGrid.setEditEvent(ListGridEditEvent.CLICK);
		
		
		ListGridField key = new ListGridField("key", "key");
		ListGridField val = new ListGridField("val", "val");
		
		cncGrid.setFields(key, val);
		
		ListGridRecord[] lgr = new ListGridRecord[15];
		
		lgr[0] = new ListGridRecord();
		lgr[0].setAttribute("key", "Segment ID ");
		lgr[0].setAttribute("val", segmentData.getId());
		
		lgr[1] = new ListGridRecord();
		lgr[1].setAttribute("key", "Chromosome");
		lgr[1].setAttribute("val", segmentData.getChromosome());
		
		lgr[2] = new ListGridRecord();
		lgr[2].setAttribute("key", "Start");
		lgr[2].setAttribute("val", segmentData.getStart());
		
		lgr[3] = new ListGridRecord();
		lgr[3].setAttribute("key", "End");
		lgr[3].setAttribute("val", segmentData.getEnd());
		
		lgr[4] = new ListGridRecord();
		lgr[4].setAttribute("key", "Segment Mean");
		lgr[4].setAttribute("val", segmentData.getMean());
		
		lgr[5] = new ListGridRecord();
		lgr[5].setAttribute("key", "Markers");
		lgr[5].setAttribute("val", segmentData.getNumberOfMarkers());
		
		lgr[6] = new ListGridRecord();
		lgr[6].setAttribute("key", "Study");
		lgr[6].setAttribute("val", segmentData.getMicroarraystudyName());
		
		cncGrid.setData(lgr);
		
		window.addItem(cncGrid);
		
		window.show();
	}
	
	public void loadWindow(Gen gene){
		
		Window window = new Window();

		window.setTitle("Gene " + gene.getGenName());
		window.setAutoSize(true);
		window.setAutoCenter(true);
		
		final ListGrid geneGrid = new ListGrid();
		geneGrid.setWidth(450);
		geneGrid.setHeight(270);  
		geneGrid.setShowAllRecords(true);  
		geneGrid.setAlternateRecordStyles(true);
		geneGrid.setShowHeader(false);
		geneGrid.setWrapCells(true);
		geneGrid.setFixedRecordHeights(false);
		geneGrid.setCanEdit(true);
		geneGrid.setEditEvent(ListGridEditEvent.CLICK);
		
		ListGridField key = new ListGridField("key", "key");
		ListGridField val = new ListGridField("val", "val");
		
		geneGrid.setFields(key, val);
		
		ListGridRecord[] lgr = new ListGridRecord[9];
		
		lgr[0] = new ListGridRecord();
		lgr[0].setAttribute("key", "Ensembl Stable ID");
		lgr[0].setAttribute("val", gene.getAccessionID());
		
		lgr[1] = new ListGridRecord();
		lgr[1].setAttribute("key", "Name");
		lgr[1].setAttribute("val", gene.getGenName());
		
		lgr[2] = new ListGridRecord();
		lgr[2].setAttribute("key", "Chromosome");
		lgr[2].setAttribute("val", gene.getChr());
		
		lgr[3] = new ListGridRecord();
		lgr[3].setAttribute("key", "Start");
		lgr[3].setAttribute("val", gene.getStart());
		
		lgr[4] = new ListGridRecord();
		lgr[4].setAttribute("key", "End");
		lgr[4].setAttribute("val", gene.getEnd());
		
		lgr[5] = new ListGridRecord();
		lgr[5].setAttribute("key", "Length");
		lgr[5].setAttribute("val", gene.getLength());
		
		lgr[6] = new ListGridRecord();
		lgr[6].setAttribute("key", "Strand");
		lgr[6].setAttribute("val", gene.getStrand());
		
		lgr[7] = new ListGridRecord();
		lgr[7].setAttribute("key", "Bio Type");
		lgr[7].setAttribute("val", gene.getBioType());
		
		lgr[8] = new ListGridRecord();
		lgr[8].setAttribute("key", "Description");
		lgr[8].setAttribute("val", gene.getDescription());
		
		geneGrid.setData(lgr);
		
		window.addItem(geneGrid);
		
		window.show();
	}
	
	public void loadSetPwWindow(final int userId, String username){
		
		final Window window = new Window();

		window.setTitle("Set password for " + username);
		window.setWidth(250);
		window.setHeight(100);
		window.setAlign(Alignment.CENTER);
		
		window.setAutoCenter(true);
		window.setIsModal(true);
		window.setShowModalMask(true); 
		
		DynamicForm pwForm = new DynamicForm();
		newPwPasswordItem = new PasswordItem();
		newPwPasswordItem.setTitle("New Password");
		
		ButtonItem submitPwButton = new ButtonItem("submit");
		submitPwButton.setWidth(50);
		
		submitPwButton.addClickHandler(new com.smartgwt.client.widgets.form.fields.events.ClickHandler(){

			@Override
			public void onClick(
					com.smartgwt.client.widgets.form.fields.events.ClickEvent event) {
				setPassword(userId, newPwPasswordItem.getDisplayValue());
				window.hide();
			}
			
		});

		pwForm.setItems(newPwPasswordItem, submitPwButton);
	
		window.addItem(pwForm);
		
		window.show();
		
	}
	
	public void openUserAdminTab(final FoUser[] users){
	
		Tab usersAdminTab = new Tab("Users");
		usersAdminTab.setCanClose(true);
		
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
		
		ToolStripButton changePwButton = new ToolStripButton();  
		changePwButton.setTitle("Change Password");
		changePwButton.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				
				ListGridRecord lgr = userGrid.getSelectedRecord();
				
				if (lgr != null){
				
					loadSetPwWindow(userGrid.getSelectedRecord().getAttributeAsInt("id"),
						userGrid.getSelectedRecord().getAttribute("userName"));
					
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
		userGrid.setShowAllRecords(true);  
		userGrid.setAlternateRecordStyles(true);
		userGrid.setWrapCells(true);
		userGrid.setFixedRecordHeights(false);
		userGrid.markForRedraw();
		userGrid.addCellClickHandler(new CellClickHandler() {  
			
			@Override
			public void onCellClick(CellClickEvent event) {  
			 
				ListGridRecord record =  event.getRecord();
				int id = Integer.parseInt(record.getAttribute("id"));
				
				int colNum = event.getColNum();  
				String fieldName = userGrid.getFieldName(colNum);
				
				if(fieldName.equals("isAdmin")){
					String flag = record.getAttributeAsString("isAdmin");
					toggleIsActiveOrIsAdmin(id, flag, "isAdmin", event.getRowNum(), event.getRowNum());
				} else if (fieldName.equals("isActive")){
					String flag = record.getAttributeAsString("isActive");
					toggleIsActiveOrIsAdmin(id, flag, "isActive", event.getRowNum(), event.getRowNum());
				}
			}
		}); 
		
		
		ListGridField lgfId = new ListGridField("id", "user ID");
		ListGridField lgfFirstName = new ListGridField("firstName", "First Name");
		ListGridField lgfLastName = new ListGridField("lastName", "Last Name");
		ListGridField lgfUserName = new ListGridField("userName", "Username");
		ListGridField lgfEmail = new ListGridField("email", "E-Mail");
		ListGridField lgfIsActive = new ListGridField("isActive", "Activated");
		ListGridField lgfisAdmin = new ListGridField("isAdmin", "Administator");
		
		userGrid.setFields(lgfId, lgfFirstName, lgfLastName, lgfUserName, lgfEmail, lgfIsActive, lgfisAdmin);
		
		ListGridRecord[] lgr = new ListGridRecord[users.length];
		
		for(int i=0; i < users.length; i++){
			lgr[i] = new ListGridRecord();
			lgr[i].setAttribute("id", users[i].getId());
			lgr[i].setAttribute("firstName", users[i].getFirstName());
			lgr[i].setAttribute("lastName", users[i].getLastName());
			lgr[i].setAttribute("userName", users[i].getUserName());
			lgr[i].setAttribute("email", users[i].getEmail());
			lgr[i].setAttribute("isActive", users[i].getIsActive());
			lgr[i].setAttribute("isAdmin", users[i].getIsAdmin());
		}
		
		userGrid.setData(lgr);
		
		gridContainer.addMember(userGrid);
		
		pane.addMember(gridContainer);
		
		usersAdminTab.setPane(pane);
		
		centerTabSet.addTab(usersAdminTab);
		
		centerTabSet.selectTab(usersAdminTab);
		
	}
	
	public void openUserProfileTab(FoUser user){
		
		Tab userProfileTab = new Tab("Profile");
		userProfileTab.setCanClose(true);
		
		VLayout pane = new VLayout();
		pane.setWidth100();
		pane.setHeight100();
		pane.setDefaultLayoutAlign(Alignment.CENTER);
		
		HLayout header = new HLayout();
		header.setAutoWidth();
		header.setAutoHeight();
		
		Label headerLbl = new Label("<h2>" + user.getUserName() + "s Profile</h2>");
		headerLbl.setWrap(false);
		header.addMember(headerLbl);
		
		pane.addMember(header);
		
		DynamicForm userProfileForm = new DynamicForm();
		userProfileForm.setWidth(250);
		userProfileForm.setHeight(260);
		userProfileForm.setAlign(Alignment.CENTER);
		
		useIdTextItem = new TextItem();
		useIdTextItem.setTitle("User Id");
		useIdTextItem.setValue(user.getId());
		useIdTextItem.setDisabled(true);
		
		useNameTextItem = new TextItem();
		useNameTextItem.setTitle("Username");
		useNameTextItem.setValue(user.getUserName());
		useNameTextItem.setDisabled(true);
		
		userFirstNameTextItem = new TextItem();
		userFirstNameTextItem.setTitle("First Name");
		userFirstNameTextItem.setValue(user.getFirstName());
		
		userLastNameTextItem = new TextItem();
		userLastNameTextItem.setTitle("Last Name");
		userLastNameTextItem.setValue(user.getLastName());
		
		userEmailTextItem = new TextItem();
		userEmailTextItem.setTitle("E-Mail");
		userEmailTextItem.setValue(user.getEmail());
		
		ButtonItem updateProfile = new ButtonItem();
		updateProfile.setTitle("Update Profile");
		updateProfile.addClickHandler(new com.smartgwt.client.widgets.form.fields.events.ClickHandler(){

			@Override
			public void onClick(
					com.smartgwt.client.widgets.form.fields.events.ClickEvent event) {
				updateUserProfile(new FoUser(Integer.parseInt(useIdTextItem.getDisplayValue()),
									userFirstNameTextItem.getDisplayValue(), 
									userLastNameTextItem.getDisplayValue(),
									useNameTextItem.getDisplayValue(),
									userEmailTextItem.getDisplayValue(),
									false,
									false));
			}});
		
		userPwItem = new PasswordItem();
		userPwItem.setTitle("Password");
		
		userPwConfirmItem = new PasswordItem();
		userPwConfirmItem.setTitle("Confirm Password");
		
		ButtonItem updatePassword = new ButtonItem();
		updatePassword.setTitle("Set Password");
		updatePassword.addClickHandler(new com.smartgwt.client.widgets.form.fields.events.ClickHandler(){

			@Override
			public void onClick(
					com.smartgwt.client.widgets.form.fields.events.ClickEvent event) {
				
				if(userPwItem.getDisplayValue().equals(userPwConfirmItem.getDisplayValue())){
					
					FoUser user = new FoUser(Integer.parseInt(useIdTextItem.getDisplayValue()),
												userFirstNameTextItem.getDisplayValue(), 
												userLastNameTextItem.getDisplayValue(),
												useNameTextItem.getDisplayValue(),
												userEmailTextItem.getDisplayValue(),
												false,
												false);
					user.setPw(userPwItem.getDisplayValue());
				
					updateUserPassword(user);
					
				} else {
					SC.say("Password does not match!");
				}
				
			}});
		
		userProfileForm.setItems(useIdTextItem,
								useNameTextItem,
								userFirstNameTextItem,
								userLastNameTextItem,
								userEmailTextItem,
								updateProfile, 
								userPwItem,
								userPwConfirmItem,
								updatePassword);
		
		pane.addMember(userProfileForm);
		
		userProfileTab.setPane(pane);
		
		centerTabSet.addTab(userProfileTab);
		
		centerTabSet.selectTab(userProfileTab);
		
	}
	
	public void loadUserToGroupWindow(final FoGroup foGroup){
		
		final Window window = new Window();
		
		window.setTitle("Add User to Group: " + foGroup.getName());
		window.setWidth(250);
		window.setHeight(100);
		window.setAlign(Alignment.CENTER);
		
		window.setAutoCenter(true);
		window.setIsModal(true);
		window.setShowModalMask(true);
		
		DynamicForm userToGroupForm = new DynamicForm();
		
		userSelectItem = new SelectItem();  
        userSelectItem.setTitle("User");
        
        getAllUsersExceptGroup(foGroup); 
        
        ButtonItem addUserToGroupButton = new ButtonItem("Add");
		addUserToGroupButton.setWidth(50);
		
		addUserToGroupButton.addClickHandler(new com.smartgwt.client.widgets.form.fields.events.ClickHandler(){

			@Override
			public void onClick(
					com.smartgwt.client.widgets.form.fields.events.ClickEvent event) {

				addUserToGroup(foGroup, Integer.parseInt(userSelectItem.getValueAsString()));
				window.hide();
			}
			
		});
		
		userToGroupForm.setItems(userSelectItem, addUserToGroupButton);
		
		window.addItem(userToGroupForm);
		
		window.show();
	}
	
	public void loadGroupManageWindow(){
		
		final Window window = new Window();

		window.setTitle("Add Group");
		window.setWidth(250);
		window.setHeight(100);
		window.setAlign(Alignment.CENTER);
		
		window.setAutoCenter(true);
		window.setIsModal(true);
		window.setShowModalMask(true); 
		
		DynamicForm groupForm = new DynamicForm();
		groupNameTextItem = new TextItem();
		groupNameTextItem.setTitle("Group Name");
		
		ButtonItem addGroupButton = new ButtonItem("Add");
		addGroupButton.setWidth(50);
		
		addGroupButton.addClickHandler(new com.smartgwt.client.widgets.form.fields.events.ClickHandler(){

			@Override
			public void onClick(
					com.smartgwt.client.widgets.form.fields.events.ClickEvent event) {
				addGroup(new FoGroup(0, groupNameTextItem.getDisplayValue(), true));
				window.hide();
			}
			
		});

		groupForm.setItems(groupNameTextItem, addGroupButton);
	
		window.addItem(groupForm);
		
		window.show();
	}
	
	public void openGroupAdminTab(){
		Tab groupAdminTab;
		groupAdminTab = new Tab("Group Management");
		
		groupAdminTab.setCanClose(true);
		
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
		
		ToolStrip groupToolStrip = new ToolStrip();
		groupToolStrip.setWidth100();
		
		ToolStripButton addGroupButton = new ToolStripButton();  
		addGroupButton.setTitle("add Group");
		addGroupButton.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				loadGroupManageWindow();
			}});
		
		groupToolStrip.addButton(addGroupButton);
		
		ToolStripButton deleteGroupButton = new ToolStripButton();  
		deleteGroupButton.setTitle("delete Group");
		deleteGroupButton.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				
				final ListGridRecord lgr = groupGrid.getSelectedRecord();
				
				SC.confirm("Do you really want to delete " + lgr.getAttribute("groupName") + "?", new BooleanCallback(){

					@Override
					public void execute(Boolean value) {
						if(value != null && value){
							FoGroup group = new FoGroup(Integer.parseInt(lgr.getAttribute("groupId")),
											lgr.getAttribute("groupName"),
											Boolean.parseBoolean(lgr.getAttribute("isactive")));
				
							deleteGroup(group);
						}
					}
				});
				
			}});
		
		groupToolStrip.addButton(deleteGroupButton);
		
		ToolStripButton addUserGroupButton = new ToolStripButton();  
		addUserGroupButton.setTitle("add User to Group");
		addUserGroupButton.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				ListGridRecord lgr = groupGrid.getSelectedRecord();
				
				FoGroup group = new FoGroup(Integer.parseInt(lgr.getAttribute("groupId")),
															lgr.getAttribute("groupName"),
															Boolean.parseBoolean(lgr.getAttribute("isactive")));
				
				loadUserToGroupWindow(group);
				
		}});
		
		groupToolStrip.addButton(addUserGroupButton);
		
		ToolStripButton removeUserGroupButton = new ToolStripButton();
		removeUserGroupButton.setTitle("remove User from Group");
		removeUserGroupButton.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				ListGridRecord userLgr = groupUserGrid.getSelectedRecord();
				ListGridRecord groupLgr = groupGrid.getSelectedRecord();
				
				int groupId = groupLgr.getAttributeAsInt("groupId");
				int userId = userLgr.getAttributeAsInt("userId");
				
				removeUserFromGroup(groupId, userId);
				
		}});
		
		groupToolStrip.addButton(removeUserGroupButton);
		
		controlsPanel.addMember(groupToolStrip);
		
		headerContainer.addMember(controlsPanel);
		
		pane.addMember(headerContainer);
		
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
		
		showAllGroups();
		
		gridContainer.addMember(groupGrid);
		
		groupUserGrid = new ListGrid();
		groupUserGrid.setWidth("50%");
		groupUserGrid.setHeight100();
		groupUserGrid.setShowAllRecords(true);  
		groupUserGrid.setAlternateRecordStyles(true);
		groupUserGrid.setWrapCells(true);
		groupUserGrid.setFixedRecordHeights(false);
		groupUserGrid.markForRedraw();
		
		ListGridField lgfGroupUserId = new ListGridField("userId", "User ID");
		ListGridField lgfGroupUserName = new ListGridField("userName", "Username");
		
		groupUserGrid.setFields(lgfGroupUserId, lgfGroupUserName);
		
		gridContainer.addMember(groupUserGrid);
		
		pane.addMember(gridContainer);
		
		groupAdminTab.setPane(pane);
		
		centerTabSet.addTab(groupAdminTab);
		
		centerTabSet.selectTab(groupAdminTab);
		
	}
	
	public void loadOrganManageWindow(){
		
		final Window window = new Window();

		window.setTitle("Add Organ");
		window.setWidth(250);
		window.setHeight(120);
		window.setAlign(Alignment.CENTER);
		
		window.setAutoCenter(true);
		window.setIsModal(true);
		window.setShowModalMask(true);
		
		DynamicForm organForm = new DynamicForm();
		organLabelTextItem = new TextItem();
		organLabelTextItem.setTitle("Property Label");
		
		organTypeCbItem = new ComboBoxItem(); 
		organTypeCbItem.setTitle("Type");
		organTypeCbItem.setType("comboBox");
		
		organTypeCbItem.setAutoFetchData(false);
		
		OrganDS oDS = new OrganDS();
		
		organTypeCbItem.setOptionDataSource(oDS);
		organTypeCbItem.setOptionOperationId(OperationId.ORGAN_FETCH_TYPES);
		organTypeCbItem.setDisplayField("typeName");
		organTypeCbItem.setValueField("typeId");
		
		ButtonItem addOrganButton = new ButtonItem("Add");
		addOrganButton.setWidth(50);
		
		addOrganButton.addClickHandler(new com.smartgwt.client.widgets.form.fields.events.ClickHandler(){
			@Override
			public void onClick(
					com.smartgwt.client.widgets.form.fields.events.ClickEvent event) {
				
				ListGridRecord lgr = new ListGridRecord();
				lgr.setAttribute("organName", organLabelTextItem.getDisplayValue());
				lgr.setAttribute("organType", organTypeCbItem.getDisplayValue());
				
				organGrid.addData(lgr);
				
				window.hide();
			}
		});

		organForm.setItems(organLabelTextItem, organTypeCbItem, addOrganButton);
	
		window.addItem(organForm);
		
		window.show();
	}
	
	public void openOrganAdminTab(){
		
		Tab organsAdminTab = new Tab("Organ Management");
		organsAdminTab.setCanClose(true);

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
		
		ToolStrip organToolStrip = new ToolStrip();
		organToolStrip.setWidth100();
		
		ToolStripButton addOrganButton = new ToolStripButton();
		addOrganButton.setTitle("add Organ");
		addOrganButton.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				loadOrganManageWindow();
			}});
		
		organToolStrip.addButton(addOrganButton);
		
		controlsPanel.addMember(organToolStrip);
		
		headerContainer.addMember(controlsPanel);
		
		pane.addMember(headerContainer);
		
		HLayout gridContainer = new HLayout();
		gridContainer.setWidth100();
		gridContainer.setHeight100();
		
		organGrid = new ListGrid();
		organGrid.setWidth100();
		organGrid.setHeight100();
		organGrid.setAlternateRecordStyles(true);
		organGrid.setWrapCells(true);
		organGrid.setFixedRecordHeights(false);
		organGrid.setShowAllRecords(false);
		organGrid.setAutoFetchData(false);
		
		ListGridField lgfId = new ListGridField("organId", "Organ ID");
		ListGridField lgfLabel = new ListGridField("organName", "Organ Name");
		ListGridField lgfType = new ListGridField("organType", "Organ Type");
		ListGridField lgfActivity = new ListGridField("organActivity", "enabled");
		
		organGrid.setFields(lgfId, lgfLabel, lgfType, lgfActivity);
		
		OrganDS oDS = new OrganDS();
		
		organGrid.setDataSource(oDS);
		organGrid.setFetchOperation(OperationId.ORGAN_FETCH_ALL);
		
		organGrid.fetchData();
		
		gridContainer.addMember(organGrid);
		
		pane.addMember(gridContainer);
		
		organsAdminTab.setPane(pane);
		
		centerTabSet.addTab(organsAdminTab);
		
		centerTabSet.selectTab(organsAdminTab);
	}
	
	public void loadPropertyManageWindow(){
		
		final Window window = new Window();

		window.setTitle("Add Property");
		window.setWidth(250);
		window.setHeight(120);
		window.setAlign(Alignment.CENTER);
		
		window.setAutoCenter(true);
		window.setIsModal(true);
		window.setShowModalMask(true);
		
		DynamicForm propertyForm = new DynamicForm();
		propertyLabelTextItem = new TextItem();
		propertyLabelTextItem.setTitle("Property Label");
		
		propertyTypeCbItem = new ComboBoxItem(); 
		propertyTypeCbItem.setTitle("Type");
		propertyTypeCbItem.setType("comboBox");
		
	    getAllPropertyTypes();
		
		ButtonItem addPropertyButton = new ButtonItem("Add");
		addPropertyButton.setWidth(50);
		
		addPropertyButton.addClickHandler(new com.smartgwt.client.widgets.form.fields.events.ClickHandler(){
			@Override
			public void onClick(
					com.smartgwt.client.widgets.form.fields.events.ClickEvent event) {
				addProperty(new FoProperty(0, propertyLabelTextItem.getDisplayValue(), propertyTypeCbItem.getDisplayValue(), "enabled"));
				window.hide();
			}
		});

		propertyForm.setItems(propertyLabelTextItem, propertyTypeCbItem, addPropertyButton);
	
		window.addItem(propertyForm);
		
		window.show();
	}
	
	public void openPropertyAdminTab(final FoProperty[] properties){
		
		Tab propertiesAdminTab = new Tab("Property Management");
		propertiesAdminTab.setCanClose(true);
		
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
		
		ToolStrip propertyToolStrip = new ToolStrip();
		propertyToolStrip.setWidth100();
		
		ToolStripButton addPropertyButton = new ToolStripButton();
		addPropertyButton.setTitle("add Property");
		addPropertyButton.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				loadPropertyManageWindow();
			}});
		
		propertyToolStrip.addButton(addPropertyButton);
		
		controlsPanel.addMember(propertyToolStrip);
		
		headerContainer.addMember(controlsPanel);
		
		pane.addMember(headerContainer);
		
		HLayout gridContainer = new HLayout();
		gridContainer.setWidth100();
		gridContainer.setHeight100();
				
		propertyGrid = new ListGrid();
		propertyGrid.setWidth100();
		propertyGrid.setHeight100();
		propertyGrid.setShowAllRecords(true);
		propertyGrid.setAlternateRecordStyles(true);
		propertyGrid.setWrapCells(true);
		propertyGrid.setFixedRecordHeights(false);
		
		ListGridField lgfId = new ListGridField("id", "Property ID");
		ListGridField lgfLabel = new ListGridField("propertyLabel", "Property Label");
		ListGridField lgfType = new ListGridField("propertyType", "Property Type");
		ListGridField lgfActivity = new ListGridField("propertyActivity", "enabled");
		
		propertyGrid.setFields(lgfId, lgfLabel, lgfType, lgfActivity);
		
		ListGridRecord[] lgr = new ListGridRecord[properties.length];
		
		for(int i=0; i < properties.length; i++){
			lgr[i] = new ListGridRecord();
			lgr[i].setAttribute("id", properties[i].getId());
			lgr[i].setAttribute("propertyLabel", properties[i].getLabel());
			lgr[i].setAttribute("propertyType", properties[i].getType());
			lgr[i].setAttribute("propertyActivity", properties[i].getActivty());
		}
		
		propertyGrid.setData(lgr);
		
		gridContainer.addMember(propertyGrid);
		
		pane.addMember(gridContainer);
		
		propertiesAdminTab.setPane(pane);
		
		centerTabSet.addTab(propertiesAdminTab);
		
		centerTabSet.selectTab(propertiesAdminTab);
	}
	
	public void loadChipManageWindow(){
		
		final Window window = new Window();

		window.setTitle("Add Chip");
		window.setWidth(250);
		window.setHeight(120);
		window.setAlign(Alignment.CENTER);
		
		window.setAutoCenter(true);
		window.setIsModal(true);
		window.setShowModalMask(true);
		
		DynamicForm chipForm = new DynamicForm();
		chipLabelTextItem = new TextItem();
		chipLabelTextItem.setTitle("Chip Label");
		
		chipTypeCbItem = new ComboBoxItem(); 
		chipTypeCbItem.setTitle("Type");
		chipTypeCbItem.setType("comboBox");
		
		chipTypeCbItem.setAutoFetchData(false);
		
		ChipDS cDS = new ChipDS();
		
		chipTypeCbItem.setOptionDataSource(cDS);
		chipTypeCbItem.setOptionOperationId(OperationId.CHIP_FETCH_TYPES);
		chipTypeCbItem.setDisplayField("typeName");
		chipTypeCbItem.setValueField("typeId");
		
		ButtonItem addChipButton = new ButtonItem("Add");
		addChipButton.setWidth(50);
		
		addChipButton.addClickHandler(new com.smartgwt.client.widgets.form.fields.events.ClickHandler(){
			@Override
			public void onClick(
					com.smartgwt.client.widgets.form.fields.events.ClickEvent event) {
				
				ListGridRecord lgr = new ListGridRecord();
				lgr.setAttribute("chipName", chipLabelTextItem.getDisplayValue());
				lgr.setAttribute("chipType", chipTypeCbItem.getDisplayValue());
				
				chipGrid.addData(lgr);
				
				window.hide();
			}
		});

		chipForm.setItems(chipLabelTextItem, chipTypeCbItem, addChipButton);
	
		window.addItem(chipForm);
		
		window.show();
	}
	
	public void openChipAdminTab(){
		
		Tab chipsAdminTab = new Tab("Chip Management");
		chipsAdminTab.setCanClose(true);
		
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
		
		ToolStrip chipToolStrip = new ToolStrip();
		chipToolStrip.setWidth100();
		
		ToolStripButton addChipButton = new ToolStripButton();
		addChipButton.setTitle("add Chip");
		addChipButton.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				loadChipManageWindow();
			}});
		
		chipToolStrip.addButton(addChipButton);
		
		controlsPanel.addMember(chipToolStrip);
		
		headerContainer.addMember(controlsPanel);
		
		pane.addMember(headerContainer);
		
		HLayout gridContainer = new HLayout();
		gridContainer.setWidth100();
		gridContainer.setHeight100();
		
		chipGrid = new ListGrid();
		chipGrid.setWidth100();
		chipGrid.setHeight100();
		chipGrid.setAlternateRecordStyles(true);
		chipGrid.setWrapCells(true);
		chipGrid.setFixedRecordHeights(false);
		chipGrid.setShowAllRecords(false);
		chipGrid.setAutoFetchData(false);
		
		ListGridField lgfId = new ListGridField("chipId", "Chip ID");
		ListGridField lgfLabel = new ListGridField("chipName", "Chip Name");
		ListGridField lgfType = new ListGridField("chipType", "Chip Type");
		
		chipGrid.setFields(lgfId, lgfLabel, lgfType);
		
		ChipDS cDS = new ChipDS();
		
		chipGrid.setDataSource(cDS);
		chipGrid.setFetchOperation(OperationId.CHIP_FETCH_ALL);
		chipGrid.fetchData();
		
		gridContainer.addMember(chipGrid);
		
		pane.addMember(gridContainer);
		
		chipsAdminTab.setPane(pane);
		
		centerTabSet.addTab(chipsAdminTab);
		
		centerTabSet.selectTab(chipsAdminTab);
	}
	
	public void openCnSegmentTab(String mstudyId){
		Tab segmentAdminTab = new Tab("Segments");
		segmentAdminTab.setCanClose(true);
		
		VLayout pane = new VLayout();
		pane.setWidth100();
		pane.setHeight100();
		pane.setDefaultLayoutAlign(Alignment.CENTER);
		
		HLayout gridContainer = new HLayout();
		gridContainer.setWidth100();
		gridContainer.setHeight100();
		
		segmentGrid = new ListGrid();
		segmentGrid.setWidth100();
		segmentGrid.setHeight100();
		segmentGrid.setShowAllRecords(true);
		segmentGrid.setAlternateRecordStyles(true);
		segmentGrid.setWrapCells(true);
		segmentGrid.setFixedRecordHeights(false);
		segmentGrid.setAutoFetchData(false);
		segmentGrid.setShowAllRecords(false);
		
		ListGridField lgfId = new ListGridField("segmentId", "Segment Id");
		ListGridField lgfChr = new ListGridField("chromosome", "Chromosome");
		lgfChr.setCellAlign(Alignment.CENTER);
		ListGridField lgfStart = new ListGridField("start", "Start");
		ListGridField lgfEnd = new ListGridField("end", "End");
		ListGridField lgfMean = new ListGridField("mean", "Segment Mean");
		ListGridField lgfMarkers = new ListGridField("markers", "Number of Markers");
		
		segmentGrid.setFields(lgfId, lgfChr, lgfStart, lgfEnd, lgfMean, lgfMarkers);
		
		CnSegmentDS sDS = new CnSegmentDS();
		
		segmentGrid.setDataSource(sDS);
		
		segmentGrid.fetchData(new Criteria("mstudyId", mstudyId));
		
		gridContainer.addMember(segmentGrid);
		
		pane.addMember(gridContainer);
		
		segmentAdminTab.setPane(pane);
		
		centerTabSet.addTab(segmentAdminTab);
		
		centerTabSet.selectTab(segmentAdminTab);
	}
	
	public void openMicrorraystudyAdminTab(FoUser user){
		
		Tab msAdminTab = new Tab("Microarraystudy Management");
		msAdminTab.setCanClose(true);
		
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
		
		ToolStrip msToolStrip = new ToolStrip();
		msToolStrip.setWidth100();
		
		projectSelectItem = new SelectItem();
		projectSelectItem.setTitle("Project");
		
		projectSelectItem.setDisplayField("projectName");
		projectSelectItem.setValueField("projectId");		
		
		projectSelectItem.setAutoFetchData(false);
		
		ProjectDS pDS = new ProjectDS();
		
		projectSelectItem.setOptionDataSource(pDS);
		projectSelectItem.setOptionOperationId(OperationId.PROJECT_FETCH_ALL);
		
		projectSelectItem.setDefaultToFirstOption(true);
		projectSelectItem.addChangedHandler(new ChangedHandler(){

			@Override
			public void onChanged(ChangedEvent event) {
				
				String projectId = projectSelectItem.getValueAsString();
				
				msGrid.fetchData(new Criteria("projectId", projectId));
			}
			
		});
		
		msToolStrip.addFormItem(projectSelectItem);
		
		ToolStripButton showSegmentsButton = new ToolStripButton();
		showSegmentsButton.setTitle("show segments");
		showSegmentsButton.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				
				ListGridRecord lgr = msGrid.getSelectedRecord();
				
				if (lgr != null){
				
					openCnSegmentTab(lgr.getAttributeAsString("mstudyId"));
					
				} else {
					SC.say("Select a microarray study.");
				}
				
			}});
		
		msToolStrip.addButton(showSegmentsButton);
		
		ToolStripButton removeMstudyButton = new ToolStripButton();
		removeMstudyButton.setTitle("remove microarray study");
		if(!user.getIsAdmin()){
			removeMstudyButton.setDisabled(true);
		}
		removeMstudyButton.addClickHandler(new ClickHandler(){
		
			@Override
			public void onClick(ClickEvent event) {
				
				final ListGridRecord lgr = msGrid.getSelectedRecord();
				
				if(lgr != null) {
				
					SC.confirm("Do you really want to delete " + lgr.getAttribute("name") + "?", new BooleanCallback(){

						@Override
						public void execute(Boolean value) {
							if(value != null && value){
							
								msGrid.removeData(lgr);
								
							}
						}
					});
					
				} else {
					SC.say("Select a microarray study.");
				}
				
			}});
		
		msToolStrip.addButton(removeMstudyButton);
		
		controlsPanel.addMember(msToolStrip);
		
		headerContainer.addMember(controlsPanel);
		
		pane.addMember(headerContainer);
		
		HLayout gridContainer = new HLayout();
		gridContainer.setWidth100();
		gridContainer.setHeight100();
		
		msGrid = new ListGrid();
		msGrid.setWidth100();
		msGrid.setHeight100();
		msGrid.setShowAllRecords(true);
		msGrid.setAlternateRecordStyles(true);
		msGrid.setWrapCells(true);
		msGrid.setShowAllRecords(false);
		msGrid.setAutoFetchData(false);
		msGrid.setFixedRecordHeights(false);
		
		ListGridField lgfId = new ListGridField("mstudyId", "Microarraystudy ID");
		ListGridField lgfChip = new ListGridField("chipName", "Chip");
		ListGridField lgfTissue = new ListGridField("tissueName", "Tissue");
		ListGridField lgfDate = new ListGridField("date", "Date");
		ListGridField lgfName = new ListGridField("mstudyName", "Name");
		ListGridField lgfDescription = new ListGridField("mstudyDescription", "Description");
		
		msGrid.setFields(lgfId, lgfChip, lgfTissue, lgfDate, lgfName, lgfDescription);
		
		//TODO make load the data for default option...
		MicroarrayStudyDS mDS = new MicroarrayStudyDS();
		
		msGrid.setDataSource(mDS);
		msGrid.setFetchOperation(OperationId.MSTUDY_FETCH_FOR_PROJECT);
		
		gridContainer.addMember(msGrid);
		
		pane.addMember(gridContainer);
		
		msAdminTab.setPane(pane);
		
		centerTabSet.addTab(msAdminTab);
		
		centerTabSet.selectTab(msAdminTab);
	}
	
	public void loadProjectManageWindow(){
		
		final Window window = new Window();

		window.setTitle("Add Project");
		window.setWidth(250);
		window.setHeight(200);
		window.setAlign(Alignment.CENTER);
		
		window.setAutoCenter(true);
		window.setIsModal(true);
		window.setShowModalMask(true);
		
		DynamicForm projectForm = new DynamicForm();
		projectNameTextItem = new TextItem();
		projectNameTextItem.setTitle("Project Name");
		
		projectDescriptionItem = new TextAreaItem();
		projectDescriptionItem.setTitle("Description");
		projectDescriptionItem.setLength(5000);
		
		ButtonItem addProjectButton = new ButtonItem("Add");
		addProjectButton.setWidth(50);

		addProjectButton.addClickHandler(new com.smartgwt.client.widgets.form.fields.events.ClickHandler(){

			@Override
			public void onClick(
					com.smartgwt.client.widgets.form.fields.events.ClickEvent event) {
				
				ListGridRecord lgr = new ListGridRecord();
				lgr.setAttribute("projectId", 0);
				lgr.setAttribute("projectName",projectNameTextItem.getDisplayValue());
				lgr.setAttribute("projectDescription", projectDescriptionItem.getDisplayValue());
				
				projectGrid.addData(lgr);
				
				window.hide();
			}
			
		});
		
		projectForm.setItems(projectNameTextItem, projectDescriptionItem, addProjectButton);
	
		window.addItem(projectForm);
		
		window.show();
	}
	
	public void loadProjectAccessManageWindow(final FoProject project){
		
		final Window window = new Window();

		window.setTitle("Add Project Access to Project " + project.getName());
		window.setWidth(250);
		window.setHeight(200);
		window.setAlign(Alignment.CENTER);
		
		window.setAutoCenter(true);
		window.setIsModal(true);
		window.setShowModalMask(true);
		
		final DynamicForm projectAccessForm = new DynamicForm();
		
		groupSelectItem = new SelectItem();
        groupSelectItem.setTitle("Group");
        
        getAllGroupsExceptProject(project);
		
		accessRightSelectItem = new SelectItem();
		accessRightSelectItem.setTitle("Access right");
		
		LinkedHashMap<String, String> valueMap = new LinkedHashMap<String, String>();
        valueMap.put("r", "r");
        valueMap.put("rw", "rw");
		
        accessRightSelectItem.setValueMap(valueMap);
        
		ButtonItem addProjectAccessButton = new ButtonItem("Add");
		addProjectAccessButton.setWidth(50);

		addProjectAccessButton.addClickHandler(new com.smartgwt.client.widgets.form.fields.events.ClickHandler(){

			@Override
			public void onClick(
					com.smartgwt.client.widgets.form.fields.events.ClickEvent event) {
				
				ListGridRecord lgr = new ListGridRecord();
				lgr.setAttribute("projectId", project.getId());
				lgr.setAttribute("groupId", groupSelectItem.getValueAsString());
				lgr.setAttribute("accessRight", accessRightSelectItem.getDisplayValue());
				
				projectAccessGrid.addData(lgr);
				
				window.hide();
			}
			
		});
		
		projectAccessForm.setItems(groupSelectItem, accessRightSelectItem, addProjectAccessButton);
	
		window.addItem(projectAccessForm);
		
		window.show();
	}
	
	public void openProjectAdminTab(FoUser user){
		Tab projectAdminTab;
		projectAdminTab = new Tab("Project Management");
		
		projectAdminTab.setCanClose(true);
		
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
		
		ToolStrip projectToolStrip = new ToolStrip();
		projectToolStrip.setWidth100();
		
		ToolStripButton addProjectButton = new ToolStripButton();  
		addProjectButton.setTitle("add Project");
		if(user.getIsAdmin() == false){
			addProjectButton.setDisabled(true);
		}
		addProjectButton.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				loadProjectManageWindow();
			}});
		
		projectToolStrip.addButton(addProjectButton);
		
		ToolStripButton deleteProjectButton = new ToolStripButton();  
		deleteProjectButton.setTitle("delete Project");
		if(user.getIsAdmin() == false){
			deleteProjectButton.setDisabled(true);
		}
		deleteProjectButton.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				
				final ListGridRecord lgr = projectGrid.getSelectedRecord();
				
				if (lgr != null) {
				
					SC.confirm("Do you really want to delete " + lgr.getAttribute("projectName") + "?", new BooleanCallback(){

						@Override
						public void execute(Boolean value) {
							if(value != null && value){
						
								projectGrid.removeData(lgr);
								
								projectAccessGrid.selectAllRecords();
								projectAccessGrid.removeSelectedData();
								projectMstudyGrid.selectAllRecords();
								projectMstudyGrid.removeSelectedData();
							}
						}
					});
				
				} else {
					SC.say("Select a project.");
				}
				
			}});
		
		projectToolStrip.addButton(deleteProjectButton);
		
		
		ToolStripButton addProjectAccessButton = new ToolStripButton();  
		addProjectAccessButton.setTitle("add Project Access");
		if(user.getIsAdmin() == false){
			addProjectAccessButton.setDisabled(true);
		}
		addProjectAccessButton.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				ListGridRecord lgr = projectGrid.getSelectedRecord();
				
				if (lgr != null){
				
					FoProject project = new FoProject(Integer.parseInt(lgr.getAttribute("projectId")),
																		lgr.getAttribute("projectName"),
																		lgr.getAttribute("projectDescription"));
					loadProjectAccessManageWindow(project);
					
				} else {
					SC.say("Select a project.");
				}
			}});
		
		projectToolStrip.addButton(addProjectAccessButton);
		
		ToolStripButton removeProjectAccessButton = new ToolStripButton();  
		removeProjectAccessButton.setTitle("remove Project Access");
		if(user.getIsAdmin() == false){
			removeProjectAccessButton.setDisabled(true);
		}
		removeProjectAccessButton.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				ListGridRecord projectAccessLgr = projectAccessGrid.getSelectedRecord();

				if (projectAccessLgr != null){
					
					projectAccessGrid.removeData(projectAccessLgr);
					
				} else {
					SC.say("Select a group.");
				}
				
			}});
		
		projectToolStrip.addButton(removeProjectAccessButton);
		
		controlsPanel.addMember(projectToolStrip);
		
		headerContainer.addMember(controlsPanel);
		
		pane.addMember(headerContainer);
		
		HLayout gridContainer = new HLayout();
		gridContainer.setWidth100();
		gridContainer.setHeight100();
		
		projectGrid = new ListGrid();
		projectGrid.setWidth("50%");
		projectGrid.setHeight100();  
		projectGrid.setAlternateRecordStyles(true);
		projectGrid.setWrapCells(true);
		projectGrid.setFixedRecordHeights(false);
		projectGrid.setAutoFetchData(false);
		projectGrid.setShowAllRecords(false);
		projectGrid.markForRedraw();
		
		ListGridField lgfProjectId = new ListGridField("projectId", "Project ID");
		ListGridField lgfProjectName = new ListGridField("projectName", "Project Name");
		ListGridField lgfProjectActivated = new ListGridField("projectDescription", "Description");
		
		projectGrid.setFields(lgfProjectId, lgfProjectName, lgfProjectActivated);
		
		ProjectDS pDS = new ProjectDS();
		
		projectGrid.setDataSource(pDS);
		projectGrid.setFetchOperation(OperationId.PROJECT_FETCH_ALL);
		
		projectGrid.fetchData();
		
		gridContainer.addMember(projectGrid);
		
		projectMstudyGrid = new ListGrid();
		projectMstudyGrid.setWidth("50%");
		projectMstudyGrid.setHeight100();
		projectMstudyGrid.setAlternateRecordStyles(true);
		projectMstudyGrid.setWrapCells(true);
		projectMstudyGrid.setFixedRecordHeights(false);
		projectMstudyGrid.setShowAllRecords(false);
		projectMstudyGrid.setAutoFetchData(false);
		projectMstudyGrid.markForRedraw();
		
		ListGridField lgfProjectMstudyId = new ListGridField("mstudyId", "Microarraystudy ID");
		ListGridField lgfProjectMstudyName = new ListGridField("mstudyName", "Name");
		ListGridField lgfProjectMstudyDescription = new ListGridField("mstudyDescription", "Description");
		
		projectMstudyGrid.setFields(lgfProjectMstudyId, lgfProjectMstudyName, lgfProjectMstudyDescription);
		
		MicroarrayStudyDS mDS = new MicroarrayStudyDS();
		
		projectMstudyGrid.setDataSource(mDS);
		projectMstudyGrid.setFetchOperation(OperationId.MSTUDY_FETCH_FOR_PROJECT);
		
		gridContainer.addMember(projectMstudyGrid);
		
		pane.addMember(gridContainer);
		
		if(user.getIsAdmin()){
			projectAccessGrid = new ListGrid();
			projectAccessGrid.setWidth100();
			projectAccessGrid.setHeight("50%");
			projectAccessGrid.setAlternateRecordStyles(true);
			projectAccessGrid.setWrapCells(true);
			projectAccessGrid.setFixedRecordHeights(false);
			projectAccessGrid.setAutoFetchData(false);
			projectAccessGrid.setShowAllRecords(false);
			projectAccessGrid.markForRedraw();
		
			ListGridField lgfProjectAccessId = new ListGridField("projectAccessId", "ID");
			ListGridField lgfProjectAccessGroup = new ListGridField("groupName", "Group");
			ListGridField lgfProjectAccessRight = new ListGridField("accessRight", "Access Right");
		
			projectAccessGrid.setFields(lgfProjectAccessId, lgfProjectAccessGroup, lgfProjectAccessRight);
		
			ProjectAccessDS paDS = new ProjectAccessDS();
			
			projectAccessGrid.setDataSource(paDS);
			
			pane.addMember(projectAccessGrid);
		}
		
		projectGrid.addRecordClickHandler(new MyProjectRecordClickHandler(projectMstudyGrid, projectAccessGrid, user, cp));
		
		projectAdminTab.setPane(pane);
		
		centerTabSet.addTab(projectAdminTab);
		
		centerTabSet.selectTab(projectAdminTab);
		
	}
	
	public void openDataAdminTab(boolean unlock){
		Tab dataAdminTab;
		if(unlock){
			dataAdminTab = new Tab("Data Import");
		} else {
			dataAdminTab = new Tab("Data Import (occupied)");
		}
		dataAdminTab.setCanClose(true);
		
		VLayout pane = new VLayout();
		pane.setWidth100();
		pane.setHeight100();
		pane.setDefaultLayoutAlign(Alignment.CENTER);
		
		HLayout header = new HLayout();
		header.setAutoWidth();
		header.setAutoHeight();
		
		Label headerLbl = new Label("<h2>Data Import</h2>");
		header.addMember(headerLbl);
		
		pane.addMember(header);
		
		Label step1Lbl =  new Label("<h3>Step1: upload data</h3>");
		step1Lbl.setAutoHeight();
		pane.addMember(step1Lbl);
		
		HLayout uploadPanel = new HLayout();
	    uploadPanel.setWidth100();
	    uploadPanel.setAutoHeight();
		uploadForm = new FormPanel();
		uploadForm.setWidth("100");
		uploadForm.setHeight("25");
		uploadForm.setEncoding(FormPanel.ENCODING_MULTIPART);
		uploadForm.setMethod(FormPanel.METHOD_POST);
		uploadForm.setAction(GWT.getModuleBaseURL() + "FileUpload");
		
		fu = new FileUpload();
		fu.setName("file");
		uploadForm.add(fu);
		
		Button b = new Button("upload");
		b.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				checkUploadData();
			}
		});
		
		uploadForm.addSubmitCompleteHandler(new SubmitCompleteHandler(){

			@Override
			public void onSubmitComplete(SubmitCompleteEvent event) {
				SC.say(event.getResults());
				studyName.setValue(fu.getFilename());
			}
			
		});	
		
		
		VLayout container = new VLayout();
		container.setDefaultLayoutAlign(Alignment.CENTER);
		container.setAutoHeight();
		
		container.addMember(uploadForm);
		container.addMember(b);
		
		uploadPanel.addMember(container);
		
		pane.addMember(uploadPanel);
		
		Label step2Lbl = new Label("<h3>Step2: enter meta information</h3>");
		step2Lbl.setAutoHeight();
		pane.addMember(step2Lbl);
		
		HLayout metaData = new HLayout();
		metaData.setWidth100();
		metaData.setAutoHeight();
		metaData.setDefaultLayoutAlign(Alignment.CENTER);
		
		metaData.addMember(new LayoutSpacer());
		
		metaDataForm = new DynamicForm();
		//TODO exchange microarrayoption through DS services for different data sources...
		studyName = new TextItem();
		studyName.setTitle("study name");
		
		chip = new SelectItem();
		chip.setTitle("chip type");  
		//chip.setDisplayField("chipName");
		//chip.setValueField("chipId");	
		//chip.setAutoFetchData(false);
		
		//ChipDS cDS = new ChipDS();
		
		//chip.setOptionDataSource(cDS);
		//chip.setOptionOperationId(OperationId.CHIP_FETCH_ALL);
		
		
		tissue = new SelectItem();
		tissue.setTitle("tissue");
		//tissue.setDisplayField("organName");
		//tissue.setValueField("organId");		
		//tissue.setAutoFetchData(false);
		//OrganDS oDS = new OrganDS();
		
		//tissue.setOptionDataSource(oDS);
		//tissue.setOptionOperationId(OperationId.ORGAN_FETCH_ENABLED);
		
		
		project = new SelectItem();
		project.setTitle("project");
		//project.setDisplayField("projectName");
		//project.setValueField("projectId");		
		//project.setAutoFetchData(false);
		//ProjectDS pDS = new ProjectDS();
		
		//project.setOptionDataSource(pDS);
		//project.setOptionOperationId(OperationId.PROJECT_FETCH_ALL);
		
		descriptionItem = new TextAreaItem();
		descriptionItem.setTitle("description");
		
		submitNewMstudyButton = new ButtonItem("submit");
		submitNewMstudyButton.addClickHandler(new com.smartgwt.client.widgets.form.fields.events.ClickHandler(){

			@Override
			public void onClick(
					com.smartgwt.client.widgets.form.fields.events.ClickEvent event) {
				checkImportData();
			}
			
		});
		
		getMicroarrayOptions();
		
		metaData.addMember(metaDataForm);

		metaData.addMember(new LayoutSpacer());
		
		pane.addMember(metaData);	
		
		//dataAdminTab.setPane(pane);
		
		VLayout lockPane = new VLayout();
		lockPane.setWidth100();
		lockPane.setHeight100();
		lockPane.setDefaultLayoutAlign(Alignment.CENTER);

		HLayout content = new HLayout();
		content.setHeight(50);
		content.setWidth(550);
		
		content.setContents("<h2>Page is locked due to usage of another user. Please try again later!</h2>");
		
		lockPane.addMember(content);
		
		if(unlock){
			dataAdminTab.setPane(pane);
		} else {
			dataAdminTab.setPane(lockPane);
		}
				
		centerTabSet.addTab(dataAdminTab);
		
		centerTabSet.selectTab(dataAdminTab);
		
		centerTabSet.addCloseClickHandler(new CloseClickHandler(){
			@Override
			public void onCloseClick(TabCloseClickEvent event) {
				Tab[] tabs = centerTabSet.getTabs();
				for(int i = 0; i < tabs.length; i++){
					if(tabs[i].getTitle().equals("Data Import")){
						freePage();
					}
				}
			}
		});
		
	}
	
	public void openDatabaseConfigTab(DBConfigData dbdata){
		Tab DatabaseConfigTab;
		DatabaseConfigTab = new Tab("Database Configuration");
		DatabaseConfigTab.setCanClose(true);
		
		VLayout pane = new VLayout();
		pane.setWidth100();
		pane.setHeight100();
		pane.setDefaultLayoutAlign(Alignment.CENTER);
		
		HLayout header = new HLayout();
		header.setAutoWidth();
		header.setAutoHeight();
		
		Label headerLbl = new Label("<h2>Configure Database Connections</h2>");
		headerLbl.setWidth("300");
		header.addMember(headerLbl);
		
		pane.addMember(header);
	    
	    DynamicForm dataForm = new DynamicForm();
	    dataForm.setWidth("100");
	    dataForm.setHeight("25");
	    
	    HeaderItem ensemblHeaderItem =  new HeaderItem();
	    ensemblHeaderItem.setDefaultValue("Ensembl Connection Data");
	    
	    ensemblHost = new TextItem();
	    ensemblHost.setTitle("Host");
	    ensemblHost.setValue(dbdata.getEhost());
	    
	    ensemblPort = new TextItem();
	    ensemblPort.setTitle("Port");
	    ensemblPort.setValue(dbdata.getEport());
	    
	    ensemblDatabase = new TextItem();
	    ensemblDatabase.setTitle("Database");
	    ensemblDatabase.setValue(dbdata.getEdb());
	    
	    ensemblUser = new TextItem();
	    ensemblUser.setTitle("User");
	    ensemblUser.setValue(dbdata.getEuser());
	    
	    ensemblPW = new PasswordItem();
	    ensemblPW.setTitle("Password");
	    ensemblPW.setValue(dbdata.getEpw());
		
	    HeaderItem fishoracleHeaderItem =  new HeaderItem();
	    fishoracleHeaderItem.setDefaultValue("Fish Oracle Connection Data"); 
	    
	    fishoracleHost = new TextItem();
	    fishoracleHost.setTitle("Host");
	    fishoracleHost.setValue(dbdata.getFhost());
	    
	    fishoracleDatabase = new TextItem();
	    fishoracleDatabase.setTitle("Database");
	    fishoracleDatabase.setValue(dbdata.getFdb());
	    
	    fishoracleUser = new TextItem();
	    fishoracleUser.setTitle("User");
	    fishoracleUser.setValue(dbdata.getFuser());
	    
	    fishoraclePW = new PasswordItem();
	    fishoraclePW.setTitle("Password");
	    fishoraclePW.setValue(dbdata.getFpw());
	    
	    ButtonItem submitButton = new ButtonItem("submit");
		submitButton.addClickHandler(new com.smartgwt.client.widgets.form.fields.events.ClickHandler(){

			@Override
			public void onClick(
					com.smartgwt.client.widgets.form.fields.events.ClickEvent event) {
				DBConfigData dbcd = new DBConfigData(ensemblHost.getDisplayValue(),
														Integer.parseInt(ensemblPort.getDisplayValue()),
														ensemblDatabase.getDisplayValue(),
														ensemblUser.getDisplayValue(),
														ensemblPW.getDisplayValue(),
														fishoracleHost.getDisplayValue(),
														fishoracleDatabase.getDisplayValue(),
														fishoracleUser.getDisplayValue(),
														fishoraclePW.getDisplayValue());
				storedbConfigData(dbcd);
			}
			
		});
	    
	    dataForm.setItems(ensemblHeaderItem,
	    					ensemblHost,
	    					ensemblPort,
	    					ensemblDatabase,
	    					ensemblUser,
	    					ensemblPW,
	    					fishoracleHeaderItem,
	    					fishoracleHost,
	    					fishoracleDatabase,
	    					fishoracleUser,
	    					fishoraclePW,
	    					submitButton);
	    
	    pane.addMember(dataForm);
	    
	    
	    DatabaseConfigTab.setPane(pane);
	    
	    centerTabSet.addTab(DatabaseConfigTab);
		
		centerTabSet.selectTab(DatabaseConfigTab);
	}
	
	/*=============================================================================
	 *||                              RPC Calls                                  ||
	 *=============================================================================
	 * */	
	
		
	public void imageRedraw(GWTImageInfo imgInfo){
			
		final SearchAsync req = (SearchAsync) GWT.create(Search.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) req;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "Search";
		endpoint.setServiceEntryPoint(moduleRelativeURL);
		final AsyncCallback<GWTImageInfo> callback = new AsyncCallback<GWTImageInfo>(){
			public void onSuccess(GWTImageInfo result){
				
				Canvas[] tabContents = cp.getCenterTabSet().getSelectedTab().getPane().getChildren();
				ImgCanvas imgLayer = (ImgCanvas) tabContents[1];
				
				TextItem chromosome = imgLayer.getChromosome();
				TextItem start = imgLayer.getStart();
				TextItem end = imgLayer.getEnd();
				
				imgLayer.removeFromParent();
				imgLayer.destroy();
				
				chromosome.setValue(result.getChromosome());
				start.setValue(result.getStart());
				end.setValue(result.getEnd());
				
				ImgCanvas newImgLayer = cp.createImageLayer(result);
				
				newImgLayer.setChromosome(chromosome);
				newImgLayer.setStart(start);
				newImgLayer.setEnd(end);
				
				VLayout presentationLayer = (VLayout) cp.getCenterTabSet().getSelectedTab().getPane();
				
				presentationLayer.addMember(newImgLayer);
				
				newImgLayer.draw();

			}
			public void onFailure(Throwable caught){
				System.out.println(caught.getMessage());
				SC.say(caught.getMessage());
			}
		};
		req.redrawImage(imgInfo, callback);
	}

	public void exportExcel(GWTImageInfo imgInfo){
		
		final SearchAsync req = (SearchAsync) GWT.create(Search.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) req;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "Search";
		endpoint.setServiceEntryPoint(moduleRelativeURL);
		final AsyncCallback<String> callback = new AsyncCallback<String>(){
			public void onSuccess(String result){
				
				Window window = new Window();
				window.setTitle("export image as Excel document");
				window.setAutoCenter(true);
				window.setWidth(160);
				window.setHeight(100);
				
				DynamicForm downloadForm = new DynamicForm();
				downloadForm.setPadding(25);
				
				LinkItem link = new LinkItem();
				link.setValue(result);
				link.setLinkTitle("download");
				link.setAlign(Alignment.CENTER);
				link.setShowTitle(false);
				
				downloadForm.setItems(link);
				
				window.addItem(downloadForm);
				
				window.show();
			}
			public void onFailure(Throwable caught){
				System.out.println(caught.getMessage());
				SC.say("Nothing found!");
			}
		};
		req.exportData(imgInfo, callback);
	}
	
	public void exportImage(GWTImageInfo imgInfo){
		
		final SearchAsync req = (SearchAsync) GWT.create(Search.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) req;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "Search";
		endpoint.setServiceEntryPoint(moduleRelativeURL);
		final AsyncCallback<GWTImageInfo> callback = new AsyncCallback<GWTImageInfo>(){
			public void onSuccess(GWTImageInfo result){
				
				Window window = new Window();
				window.setTitle("export image as " + result.getQuery().getImageType() +  " document");
				window.setAutoCenter(true);
				window.setWidth(160);
				window.setHeight(100);
				
				DynamicForm downloadForm = new DynamicForm();
				downloadForm.setPadding(25);
				
				LinkItem link = new LinkItem();
				link.setValue(result.getImgUrl());
				link.setLinkTitle("download");
				link.setAlign(Alignment.CENTER);
				link.setShowTitle(false);
				
				downloadForm.setItems(link);
				
				window.addItem(downloadForm);
				
				window.show();
			}
			public void onFailure(Throwable caught){
				System.out.println(caught.getMessage());
				SC.say("Nothing found!");
			}
		};
		req.redrawImage(imgInfo, callback);
	}
	
	public void storedbConfigData(DBConfigData data){
	
		final AdminAsync req = (AdminAsync) GWT.create(Admin.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) req;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "AdminService";
		endpoint.setServiceEntryPoint(moduleRelativeURL);
		final AsyncCallback<Boolean> callback = new AsyncCallback<Boolean>(){
			@Override
			public void onSuccess(Boolean result){
				
				SC.say("Database connection parameters stored.");
				
			}
			public void onFailure(Throwable caught){
				System.out.println(caught.getMessage());
				SC.say(caught.getMessage());
			}
		};
		req.writeConfigData(data, callback);
	}
	
	public void toggleIsActiveOrIsAdmin(int id, String flag, String type, int rowNum, int colNum){
		
		final AdminAsync req = (AdminAsync) GWT.create(Admin.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) req;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "AdminService";
		endpoint.setServiceEntryPoint(moduleRelativeURL);
		final AsyncCallback<String[]> callback = new AsyncCallback<String[]>(){
			@Override
			public void onSuccess(String[] result){
				
				Record userRecord = userGrid.getRecord(Integer.parseInt(result[2]));				
				
				userRecord.setAttribute(result[0], result[1]);
				
				userGrid.redraw();
				
			}
			public void onFailure(Throwable caught){
				System.out.println(caught.getMessage());
				SC.say(caught.getMessage());
			}
		};
		req.toggleFlag(id, flag, type, rowNum, colNum, callback);
	}
	
	public void getMicroarrayOptions(){
		
		final AdminAsync req = (AdminAsync) GWT.create(Admin.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) req;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "AdminService";
		endpoint.setServiceEntryPoint(moduleRelativeURL);
		final AsyncCallback<MicroarrayOptions> callback = new AsyncCallback<MicroarrayOptions>(){
			@Override
			public void onSuccess(MicroarrayOptions result){
				
				LinkedHashMap<String, String> chipValueMap = new LinkedHashMap<String, String>();
				
				for(int i=0; i < result.getChips().length; i++){
					chipValueMap.put(new Integer(result.getChips()[i].getId()).toString(), result.getChips()[i].getName());
				}
				
				chip.setValueMap(chipValueMap);
				
				LinkedHashMap<String, String> tissueValueMap = new LinkedHashMap<String, String>();
				
				for(int i=0; i < result.getOrgans().length; i++){
					tissueValueMap.put(new Integer(result.getOrgans()[i].getId()).toString(), result.getOrgans()[i].getLabel() + " (" + result.getOrgans()[i].getType() + ")");
				}
				
				tissue.setValueMap(tissueValueMap);
				
				LinkedHashMap<String, String> projectValueMap = new LinkedHashMap<String, String>();
				
				for(int i=0; i < result.getProjects().length; i++){
					projectValueMap.put(new Integer(result.getProjects()[i].getId()).toString(), result.getProjects()[i].getName());
				}
				
				project.setValueMap(projectValueMap);
				
				FormItem[] fi = new FormItem[(result.getPropertyTypes().length + 6)];
				fi[0] = studyName;
				fi[1] = chip;
				fi[2] = tissue;
				fi[3] = project;
				fi[4] = descriptionItem; 
				
				for(int i=0; i < result.getPropertyTypes().length; i++){
					
					SelectItem item = new SelectItem();
					item.setTitle(result.getPropertyTypes()[i]);
					LinkedHashMap<String, String> valueMap = new LinkedHashMap<String, String>();
					
					for(int j=0; j < result.getProperties().length; j++){
						
						if(result.getProperties()[j].getType().equals(result.getPropertyTypes()[i])){
							valueMap.put(new Integer(result.getProperties()[j].getId()).toString(), result.getProperties()[j].getLabel());
						}
						
					}
					
					item.setValueMap(valueMap);
					fi[(i+5)] = item;
				}
				
				fi[fi.length -1] = submitNewMstudyButton;

				metaDataForm.setFields(fi);
			}
			public void onFailure(Throwable caught){
				System.out.println(caught.getMessage());
				SC.say(caught.getMessage());
			}
		};
		req.getMicroarrayOptions(callback);
	}
	
	public void getAllPropertyTypes(){
		
		final AdminAsync req = (AdminAsync) GWT.create(Admin.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) req;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "AdminService";
		endpoint.setServiceEntryPoint(moduleRelativeURL);
		final AsyncCallback<String[]> callback = new AsyncCallback<String[]>(){
			
			public void onSuccess(String[] result){
				
				LinkedHashMap<String, String> propertyTypeValueMap = new LinkedHashMap<String, String>();
				
				for(int i=0; i < result.length; i++){
					propertyTypeValueMap.put(new Integer(i).toString(), result[i]);
				}
				
				propertyTypeCbItem.setValueMap(propertyTypeValueMap);
				
			}
			public void onFailure(Throwable caught){
				SC.say(caught.getMessage());
			}

		};
		req.getAllPropertyTypes(callback);
	}
	
	public void showAllProperties(){
		
		final AdminAsync req = (AdminAsync) GWT.create(Admin.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) req;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "AdminService";
		endpoint.setServiceEntryPoint(moduleRelativeURL);
		final AsyncCallback<FoProperty[]> callback = new AsyncCallback<FoProperty[]>(){
			
			public void onSuccess(FoProperty[] result){
				
				openPropertyAdminTab(result);
				
			}
			public void onFailure(Throwable caught){
				SC.say(caught.getMessage());
			}

		};
		req.getAllFoProperties(callback);
	}
	
	public void showAllGroups(){
		
		final AdminAsync req = (AdminAsync) GWT.create(Admin.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) req;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "AdminService";
		endpoint.setServiceEntryPoint(moduleRelativeURL);
		final AsyncCallback<FoGroup[]> callback = new AsyncCallback<FoGroup[]>(){
			
			public void onSuccess(FoGroup[] result){
				
				FoGroup[] groups = result;
				
				groupGrid.addRecordClickHandler(new MyGroupRecordClickHandler(groupUserGrid, cp));
								
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
		req.getAllFoGroups(callback);
	}
	
	public void deleteGroup(FoGroup foGroup){
		
		final AdminAsync req = (AdminAsync) GWT.create(Admin.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) req;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "AdminService";
		endpoint.setServiceEntryPoint(moduleRelativeURL);
		final AsyncCallback<Void> callback = new AsyncCallback<Void>(){
			
			public void onSuccess(Void result){
				
				groupGrid.removeData(groupGrid.getSelectedRecord());
				groupGrid.getRecords();
				
			}
			public void onFailure(Throwable caught){
				SC.say(caught.getMessage());
			}

		};
		req.deleteGroup(foGroup, callback);
	}
	
	public void addGroup(FoGroup foGroup){
		
		final AdminAsync req = (AdminAsync) GWT.create(Admin.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) req;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "AdminService";
		endpoint.setServiceEntryPoint(moduleRelativeURL);
		final AsyncCallback<FoGroup> callback = new AsyncCallback<FoGroup>(){
			
			public void onSuccess(FoGroup result){
				FoGroup group = result;
				
				ListGridRecord lgr = new ListGridRecord();
				lgr.setAttribute("groupId", group.getId());
				lgr.setAttribute("groupName", group.getName());
				lgr.setAttribute("isactive", group.isIsactive());
				
				groupGrid.addData(lgr);
			}
			public void onFailure(Throwable caught){
				SC.say(caught.getMessage());
			}

		};
		req.addGroup(foGroup, callback);
	}
	
	public void addProperty(FoProperty foProperty){
		
		final AdminAsync req = (AdminAsync) GWT.create(Admin.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) req;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "AdminService";
		endpoint.setServiceEntryPoint(moduleRelativeURL);
		final AsyncCallback<FoProperty> callback = new AsyncCallback<FoProperty>(){
			
			public void onSuccess(FoProperty result){
				
				ListGridRecord lgr = new ListGridRecord();
				lgr.setAttribute("id", result.getId());
				lgr.setAttribute("propertyLabel", result.getLabel());
				lgr.setAttribute("propertyType", result.getType());
				lgr.setAttribute("propertyActivity", result.getActivty());
				
				propertyGrid.addData(lgr);
			}
			public void onFailure(Throwable caught){
				SC.say(caught.getMessage());
			}

		};
		req.addProperty(foProperty, callback);
	}
	
	public void addChip(FoChip foChip){
		
		final AdminAsync req = (AdminAsync) GWT.create(Admin.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) req;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "AdminService";
		endpoint.setServiceEntryPoint(moduleRelativeURL);
		final AsyncCallback<FoChip> callback = new AsyncCallback<FoChip>(){
			
			public void onSuccess(FoChip result){
				
				ListGridRecord lgr = new ListGridRecord();
				lgr.setAttribute("id", result.getId());
				lgr.setAttribute("chipName", result.getName());
				lgr.setAttribute("chipType", result.getType());
				
				chipGrid.addData(lgr);
			}
			public void onFailure(Throwable caught){
				SC.say(caught.getMessage());
			}

		};
		req.addChip(foChip, callback);
	}
	
	public void updateUserProfile(FoUser user){
		
		final UserServiceAsync req = (UserServiceAsync) GWT.create(UserService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) req;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "UserService";
		endpoint.setServiceEntryPoint(moduleRelativeURL);
		final AsyncCallback<Void> callback = new AsyncCallback<Void>(){
			
			public void onSuccess(Void result){
				
				SC.say("Update successful!");
				
			}
			public void onFailure(Throwable caught){
				SC.say(caught.getMessage());
			}
		};
		req.updateUserProfile(user, callback);
	}
	
	public void updateUserPassword(FoUser user){
		
		final UserServiceAsync req = (UserServiceAsync) GWT.create(UserService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) req;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "UserService";
		endpoint.setServiceEntryPoint(moduleRelativeURL);
		final AsyncCallback<Void> callback = new AsyncCallback<Void>(){
			
			public void onSuccess(Void result){
				
				SC.say("Update successful!");
				
			}
			public void onFailure(Throwable caught){
				SC.say(caught.getMessage());
			}
		};
		req.updateUserPassword(user, callback);
	}
	
	public void setPassword(int userId, String pw){
		
		final AdminAsync req = (AdminAsync) GWT.create(Admin.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) req;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "AdminService";
		endpoint.setServiceEntryPoint(moduleRelativeURL);
		final AsyncCallback<Void> callback = new AsyncCallback<Void>(){
			
			public void onSuccess(Void result){
				
				SC.say("Update successful!");
				
			}
			public void onFailure(Throwable caught){
				SC.say(caught.getMessage());
			}
		};
		req.setPassword(userId, pw, callback);
	}
	
	public void getUserObject(final String forWhat){
		
		final UserServiceAsync req = (UserServiceAsync) GWT.create(UserService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) req;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "UserService";
		endpoint.setServiceEntryPoint(moduleRelativeURL);
		final AsyncCallback<FoUser> callback = new AsyncCallback<FoUser>(){
			
			public void onSuccess(FoUser result){
				
				if(forWhat.equals("ProjectAdminTab")){
					openProjectAdminTab(result);
				}
				if(forWhat.equals("MicroarraystudyAdminTab")){
					openMicrorraystudyAdminTab(result);
				}
				
			}
			public void onFailure(Throwable caught){
				SC.say(caught.getMessage());
			}
		};
		req.getSessionUserObject(callback);
	}
	
	
	public void getAllUsersExceptGroup(FoGroup foGroup){
		
		final AdminAsync req = (AdminAsync) GWT.create(Admin.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) req;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "AdminService";
		endpoint.setServiceEntryPoint(moduleRelativeURL);
		final AsyncCallback<FoUser[]> callback = new AsyncCallback<FoUser[]>(){
			
			public void onSuccess(FoUser[] result){
				
				 LinkedHashMap<String, String> valueMap = new LinkedHashMap<String, String>();
				 
				 for(int i=0; i< result.length; i++){
					 valueMap.put(new Integer(result[i].getId()).toString(), result[i].getUserName());
				 }
			     
				 userSelectItem.setValueMap(valueMap);
			}
			public void onFailure(Throwable caught){
				SC.say(caught.getMessage());
			}
		};
		req.getAllUsersExceptFoGroup(foGroup, callback);
	}
	
	public void getUsersForGroup(int groupId){
		
		final AdminAsync req = (AdminAsync) GWT.create(Admin.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) req;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "AdminService";
		endpoint.setServiceEntryPoint(moduleRelativeURL);
		final AsyncCallback<FoUser[]> callback = new AsyncCallback<FoUser[]>(){
			
			public void onSuccess(FoUser[] result){
				
				FoUser[] users = result;
				ListGridRecord[] userLgr = null;
				
				if(users != null){
					userLgr = new ListGridRecord[users.length];
				
					for(int i=0; i < users.length; i++){
						userLgr[i] = new ListGridRecord();
						userLgr[i].setAttribute("userId", users[i].getId());
						userLgr[i].setAttribute("userName", users[i].getUserName());
					}
				}
				
				groupUserGrid.setData(userLgr);
			}
			public void onFailure(Throwable caught){
				SC.say(caught.getMessage());
			}
		};
		req.getUsersForGroup(groupId, callback);
	}
	
	public void addUserToGroup(FoGroup foGroup, int userId){
		
		final AdminAsync req = (AdminAsync) GWT.create(Admin.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) req;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "AdminService";
		endpoint.setServiceEntryPoint(moduleRelativeURL);
		final AsyncCallback<FoUser> callback = new AsyncCallback<FoUser>(){
			
			public void onSuccess(FoUser result){

				ListGridRecord lgr = new ListGridRecord();
				lgr.setAttribute("userId", result.getId());
				lgr.setAttribute("userName", result.getUserName());
				
				groupUserGrid.addData(lgr);
			}
			public void onFailure(Throwable caught){
				SC.say(caught.getMessage());
			}
		};
		req.addUserToFoGroup(foGroup, userId, callback);
	}
	
	public void removeUserFromGroup(int groupId, int userId){
		
		final AdminAsync req = (AdminAsync) GWT.create(Admin.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) req;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "AdminService";
		endpoint.setServiceEntryPoint(moduleRelativeURL);
		final AsyncCallback<Boolean> callback = new AsyncCallback<Boolean>(){
			
			public void onSuccess(Boolean result){
				groupUserGrid.removeData(groupUserGrid.getSelectedRecord());
				groupUserGrid.getRecords();
			}
			public void onFailure(Throwable caught){
				SC.say(caught.getMessage());
			}
		};
		req.removeUserFromFoGroup(groupId, userId, callback);
	}
	
	public void getAllGroupsExceptProject(FoProject foProject){
		
		final AdminAsync req = (AdminAsync) GWT.create(Admin.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) req;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "AdminService";
		endpoint.setServiceEntryPoint(moduleRelativeURL);
		final AsyncCallback<FoGroup[]> callback = new AsyncCallback<FoGroup[]>(){
			
			public void onSuccess(FoGroup[] result){
				
				 LinkedHashMap<String, String> valueMap = new LinkedHashMap<String, String>();
				 
				 for(int i=0; i< result.length; i++){
					 valueMap.put(new Integer(result[i].getId()).toString(), result[i].getName());
				 }
				 
				 groupSelectItem.setValueMap(valueMap);
			}
			public void onFailure(Throwable caught){
				SC.say(caught.getMessage());
			}
		};
		req.getAllGroupsExceptFoProject(foProject, callback);
	}
	
	public void importData(String fileName,
							String studyName,
							int chipId,
							int organId,
							int projectId,
							int[] propertyIds,
							String description){
		
		final AdminAsync req = (AdminAsync) GWT.create(Admin.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) req;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "AdminService";
		endpoint.setServiceEntryPoint(moduleRelativeURL);
		final AsyncCallback<Boolean> callback = new AsyncCallback<Boolean>(){
			@Override
			public void onSuccess(Boolean result){
				
				SC.say("Data import successful!");
				
			}
			public void onFailure(Throwable caught){
				System.out.println(caught.getMessage());
				SC.say(caught.getMessage());
			}
		};
		req.importData(fileName,
						studyName,
						chipId,
						organId,
						projectId,
						propertyIds,
						description,
						callback);
	}
	
	public void freePage(){

		final AdminAsync req = (AdminAsync) GWT.create(Admin.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) req;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "AdminService";
		endpoint.setServiceEntryPoint(moduleRelativeURL);
		final AsyncCallback<Void> callback = new AsyncCallback<Void>(){
			@Override
			public void onSuccess(Void result){

			}
			public void onFailure(Throwable caught){
				System.out.println(caught.getMessage());
				SC.say(caught.getMessage());
			}

		};
		req.unlockDataImport(callback);
	}

	public void checkUploadData(){

		final AdminAsync req = (AdminAsync) GWT.create(Admin.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) req;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "AdminService";
		endpoint.setServiceEntryPoint(moduleRelativeURL);
		final AsyncCallback<Boolean> callback = new AsyncCallback<Boolean>(){
			@Override
			public void onSuccess(Boolean result){

				if(result){
					uploadForm.submit();
				} else {
					SC.say("Page currently locked by another user.");
				}
			}
			public void onFailure(Throwable caught){
				System.out.println(caught.getMessage());
				SC.say(caught.getMessage());
			}
		};
		req.canAccessDataImport(callback);
	}
	
	public void checkImportData(){

		final AdminAsync req = (AdminAsync) GWT.create(Admin.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) req;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "AdminService";
		endpoint.setServiceEntryPoint(moduleRelativeURL);
		final AsyncCallback<Boolean> callback = new AsyncCallback<Boolean>(){
			@Override
			public void onSuccess(Boolean result){

				if(result){
					
					FormItem[] newData = metaDataForm.getFields();
					
					String sName = null;
					int chipId = 0;
					int organId = 0;
					int projectId = 0;
					String description = null;
					int[] propertyIds = new int[newData.length - 5 -1];
					int j = 0;
					
					for(int i = 0; i< newData.length; i++){
						
						if(newData[i].getTitle().equals("study name")){
							sName = newData[i].getDisplayValue();
						}  else if(newData[i].getTitle().equals("chip type")){
							chipId = Integer.parseInt(((SelectItem) newData[i]).getValueAsString());
						} else if(newData[i].getTitle().equals("tissue")){
							organId = Integer.parseInt(((SelectItem) newData[i]).getValueAsString());
						} else if(newData[i].getTitle().equals("project")){
							projectId = Integer.parseInt(((SelectItem) newData[i]).getValueAsString());
						} else if(newData[i].getTitle().equals("description")){
							description = newData[i].getDisplayValue();
						} else if(newData[i].getTitle().equals("submit")){
							//do nothing
						} else {
							if(((SelectItem) newData[i]).getValueAsString() != null){
								propertyIds[j] = Integer.parseInt(((SelectItem) newData[i]).getValueAsString());
								j++;
							}
						}
					
					}
					
					importData(fu.getFilename(), sName, chipId, organId, projectId, propertyIds, description);
							
				} else {
					SC.say("Page currently locked by another user.");
				}
			}
			public void onFailure(Throwable caught){
				System.out.println(caught.getMessage());
				SC.say(caught.getMessage());				
			}
		};
		req.canAccessDataImport(callback);
	}
	
}

class MyProjectRecordClickHandler implements RecordClickHandler {

	private ListGrid projectMstudyGrid;
	private ListGrid projectAccessGrid;
	private FoUser user;
	
	public MyProjectRecordClickHandler(ListGrid projectMstudyGrid, ListGrid projectAccessGrid, FoUser user, CenterPanel cp){
		this.projectMstudyGrid = projectMstudyGrid;
		this.projectAccessGrid = projectAccessGrid;
		this.user = user;
	}
	
	@Override
	public void onRecordClick(RecordClickEvent event) {
		
		String projectId = event.getRecord().getAttribute("projectId");
		
		projectMstudyGrid.fetchData(new Criteria("projectId", projectId));
		
		if(user.getIsAdmin()){
			
			projectAccessGrid.fetchData(new Criteria("projectId", projectId));
		}
	}
}

class MyGroupRecordClickHandler implements RecordClickHandler {

	private ListGrid groupUserGrid;
	private CenterPanel cp;
	
	public MyGroupRecordClickHandler(ListGrid groupUserGrid, CenterPanel cp){
		this.groupUserGrid = groupUserGrid;
		this.cp = cp;
	}
	
	@Override
	public void onRecordClick(RecordClickEvent event) {
		ListGridRecord[] oldRecords = groupUserGrid.getRecords();
		
		for (int i= 0; i < oldRecords.length; i++){
			groupUserGrid.removeData(oldRecords[i]);
		}
		
		int groupId = Integer.parseInt(event.getRecord().getAttribute("groupId"));
		
		cp.getUsersForGroup(groupId);
	}
}

class RecMapClickHandler implements ClickHandler{
	
	private RecMapInfo recInfo;
	private CenterPanel cp;
	
	public RecMapClickHandler(RecMapInfo recmapinfo, CenterPanel centerPanel){
		this.recInfo = recmapinfo;
		this.cp = centerPanel;
	}
	
	public void onClick(ClickEvent event) {
		
		if(recInfo.getType().equals("gene")){

			geneDetails(recInfo.getElementName());
			
		}
		
		if(!recInfo.getType().equals("chromosome") && !recInfo.getType().equals("gene")){
			
			segmentDetails(recInfo.getElementName());
			
		}
	}
	
	public void segmentDetails(String query){
		
		final SearchAsync req = (SearchAsync) GWT.create(Search.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) req;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "Search";
		endpoint.setServiceEntryPoint(moduleRelativeURL);
		final AsyncCallback<FoCnSegment> callback = new AsyncCallback<FoCnSegment>(){
			public void onSuccess(FoCnSegment segmentData){
				
				cp.loadWindow(segmentData);
				
			}
			public void onFailure(Throwable caught){
				System.out.println(caught.getMessage());
				SC.say(caught.getMessage());
			}
		};
		req.getSegmentInfo(Integer.parseInt(query), callback);
	}
	
	
	public void geneDetails(String query){
		
		final SearchAsync req = (SearchAsync) GWT.create(Search.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) req;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "Search";
		endpoint.setServiceEntryPoint(moduleRelativeURL);
		final AsyncCallback<Gen> callback = new AsyncCallback<Gen>(){
			public void onSuccess(Gen gene){
				
				cp.loadWindow(gene);
				
			}
			public void onFailure(Throwable caught){
				System.out.println(caught.getMessage());
				SC.say(caught.getMessage());
			}
		};
		req.getGeneInfo(query, callback);
	}
}

class ImageFrameResizedHandler implements ResizedHandler{

	private CenterPanel cp;
	
	public ImageFrameResizedHandler(CenterPanel centerPanel){
		cp = centerPanel;
	}
	
	@Override
	public void onResized(ResizedEvent event) {
		if(cp.getCenterTabSet().getTabs().length > 1){
			Canvas[] tabContents = cp.getCenterTabSet().getSelectedTab().getPane().getChildren();
			Canvas presentationLayer = cp.getCenterTabSet().getSelectedTab().getPane();
			ImgCanvas imgLayer = (ImgCanvas) tabContents[1];
			imgLayer.getImageInfo().setWidth(presentationLayer.getInnerWidth());
			cp.imageRedraw(imgLayer.getImageInfo());
		}
	}
}