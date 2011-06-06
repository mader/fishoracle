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

import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.Cursor;
import com.smartgwt.client.types.ListGridEditEvent;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.Side;
import com.smartgwt.client.types.VerticalAlignment;
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
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.HeaderItem;
import com.smartgwt.client.widgets.form.fields.LinkItem;
import com.smartgwt.client.widgets.form.fields.PasswordItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.TextAreaItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
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

import de.unihamburg.zbh.fishoracle.client.data.CopyNumberChange;
import de.unihamburg.zbh.fishoracle.client.data.DBConfigData;
import de.unihamburg.zbh.fishoracle.client.data.FoGroup;
import de.unihamburg.zbh.fishoracle.client.data.FoMicroarraystudy;
import de.unihamburg.zbh.fishoracle.client.data.FoProject;
import de.unihamburg.zbh.fishoracle.client.data.FoProjectAccess;
import de.unihamburg.zbh.fishoracle.client.data.GWTImageInfo;
import de.unihamburg.zbh.fishoracle.client.data.Gen;
import de.unihamburg.zbh.fishoracle.client.data.MicroarrayOptions;
import de.unihamburg.zbh.fishoracle.client.data.RecMapInfo;
import de.unihamburg.zbh.fishoracle.client.data.FoUser;
import de.unihamburg.zbh.fishoracle.client.ImgCanvas;
import de.unihamburg.zbh.fishoracle.client.rpc.Admin;
import de.unihamburg.zbh.fishoracle.client.rpc.AdminAsync;
import de.unihamburg.zbh.fishoracle.client.rpc.Search;
import de.unihamburg.zbh.fishoracle.client.rpc.SearchAsync;

public class CenterPanel extends VLayout{

	private ListGrid userGrid;
	private ListGrid groupGrid;
	private ListGrid groupUserGrid;
	private SelectItem userSelectItem;
	
	private ListGrid projectGrid;
	private ListGrid projectMstudyGrid;
	private TextItem projectNameTextItem;
	private TextAreaItem projectDescriptionItem;
	private ListGrid projectAccessGrid;
	private SelectItem accessRightSelectItem;
	private SelectItem groupSelectItem;
	
	private TextItem groupNameTextItem;
	
	private TabSet centerTabSet = null;
	private TextItem chrTextItem;
	private TextItem startTextItem;
	private TextItem endTextItem;
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
				imgInfo.getQuery().setLowerTh(newLowerTh);
				imgInfo.getQuery().setUpperTh(newUpperTh);
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
		
		zoomInButton.setIcon("[APP]/icons/arrow_in.png");
		zoomInButton.setAppImgDir("/");
		presentationToolStrip.addButton(zoomInButton);
		
		Label zoomLabel = new Label("Zoom");
		zoomLabel.setWidth(20);
		presentationToolStrip.addMember(zoomLabel);
		
		ToolStripButton zoomOutButton = new ToolStripButton();
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
		zoomOutButton.setIcon("[APP]/icons/arrow_out.png");
		zoomOutButton.setAppImgDir("/");
		presentationToolStrip.addButton(zoomOutButton);
		
		presentationToolStrip.addSeparator();
		
		/*display exact chromosome range*/
		
		chrTextItem = new TextItem();
		chrTextItem.setTitle("Chromosome");
		chrTextItem.setWidth(30);
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
		startTextItem.setWidth(80);
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
		endTextItem.setWidth(80);
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
		
		lowerThTextItem = new TextItem();
		lowerThTextItem.setTitle("Less Than");
		lowerThTextItem.setWrapTitle(false);
		lowerThTextItem.setWidth(40);
		lowerThTextItem.setValue(imgInfo.getQuery().getLowerThAsString());
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
		upperThTextItem.setTitle("Greater Than");
		upperThTextItem.setWrapTitle(false);
		upperThTextItem.setWidth(40);
		upperThTextItem.setValue(imgInfo.getQuery().getUpperThAsString());
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
				GWTImageInfo imgInfo = ((ImgCanvas) cp.getCenterTabSet().getSelectedTab().getPane().getChildren()[1]).getImageInfo();
				cp.exportExcel(imgInfo);
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
	
	public void loadWindow(CopyNumberChange cncData){
		
		Window window = new Window();
		window.setTitle("Segment " + cncData.getCncStableId());
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
		lgr[0].setAttribute("key", "CNC Stable ID ");
		lgr[0].setAttribute("val", cncData.getCncStableId());
		
		lgr[1] = new ListGridRecord();
		lgr[1].setAttribute("key", "Chromosome");
		lgr[1].setAttribute("val", cncData.getChromosome());
		
		lgr[2] = new ListGridRecord();
		lgr[2].setAttribute("key", "Start");
		lgr[2].setAttribute("val", cncData.getStart());
		
		lgr[3] = new ListGridRecord();
		lgr[3].setAttribute("key", "End");
		lgr[3].setAttribute("val", cncData.getEnd());
		
		lgr[4] = new ListGridRecord();
		lgr[4].setAttribute("key", "Segment Mean");
		lgr[4].setAttribute("val", cncData.getSegmentMean());
		
		lgr[5] = new ListGridRecord();
		lgr[5].setAttribute("key", "Markers");
		lgr[5].setAttribute("val", cncData.getNumberOfMarkers());
		
		lgr[6] = new ListGridRecord();
		lgr[6].setAttribute("key", "Study");
		lgr[6].setAttribute("val", cncData.getMicroarrayStudy());
		
		lgr[7] = new ListGridRecord();
		lgr[7].setAttribute("key", "Import Date");
		lgr[7].setAttribute("val", cncData.getInsertionDate());
		
		lgr[8] = new ListGridRecord();
		lgr[8].setAttribute("key", "Chip");
		lgr[8].setAttribute("val", cncData.getChip());
		
		lgr[9] = new ListGridRecord();
		lgr[9].setAttribute("key", "Organ");
		lgr[9].setAttribute("val", cncData.getOrgan());
		
		lgr[10] = new ListGridRecord();
		lgr[10].setAttribute("key", "Pathological stage");
		lgr[10].setAttribute("val", cncData.getPstage());
		
		lgr[11] = new ListGridRecord();
		lgr[11].setAttribute("key", "Pathological Grade");
		lgr[11].setAttribute("val", cncData.getPgrade());
		
		lgr[12] = new ListGridRecord();
		lgr[12].setAttribute("key", "Meta Status");
		lgr[12].setAttribute("val", cncData.getMetaStatus());
		
		lgr[13] = new ListGridRecord();
		lgr[13].setAttribute("key", "Sample ID");
		lgr[13].setAttribute("val", cncData.getSampleId());
		
		lgr[14] = new ListGridRecord();
		lgr[14].setAttribute("key", "Description");
		lgr[14].setAttribute("val", cncData.getMicroarrayStudyDescr());
		
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
	
	public void openUserAdminTab(final FoUser[] users){
	
		Tab usersAdminTab = new Tab("Users");
		usersAdminTab.setCanClose(true);
		
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
		
		usersAdminTab.setPane(userGrid);
		
		centerTabSet.addTab(usersAdminTab);
		
		centerTabSet.selectTab(usersAdminTab);
		
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
				// TODO Auto-generated method stub
				SC.say("Not implemented yet!");
				
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
				// TODO Auto-generated method stub
				SC.say("Not implemented yet!");
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
				addProject(new FoProject(0, projectNameTextItem.getDisplayValue(), projectDescriptionItem.getDisplayValue()));
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
				
				FoProjectAccess pa = new FoProjectAccess(0, Integer.parseInt(groupSelectItem.getValueAsString()), accessRightSelectItem.getDisplayValue());
				
				addProjectAccess(pa, project.getId());
				
				window.hide();
			}
			
		});
		
		projectAccessForm.setItems(groupSelectItem, accessRightSelectItem, addProjectAccessButton);
	
		window.addItem(projectAccessForm);
		
		window.show();
	}
	
	public void openProjectAdminTab(){
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
		addProjectButton.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				loadProjectManageWindow();
			}});
		
		projectToolStrip.addButton(addProjectButton);
		
		ToolStripButton deleteProjectButton = new ToolStripButton();  
		deleteProjectButton.setTitle("delete Project");
		deleteProjectButton.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				// TODO Auto-generated method stub
				SC.say("Not implemented yet!");
				
			}});
		
		projectToolStrip.addButton(deleteProjectButton);
		
		
		ToolStripButton addProjectAccessButton = new ToolStripButton();  
		addProjectAccessButton.setTitle("add Project Access");
		addProjectAccessButton.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				ListGridRecord lgr = projectGrid.getSelectedRecord();
				
				FoProject project = new FoProject(Integer.parseInt(lgr.getAttribute("projectId")),
															lgr.getAttribute("projectName"),
															lgr.getAttribute("projectDescription"));
				loadProjectAccessManageWindow(project);
			}});
		
		projectToolStrip.addButton(addProjectAccessButton);
		
		ToolStripButton removeProjectAccessButton = new ToolStripButton();  
		removeProjectAccessButton.setTitle("remove Project Access");
		removeProjectAccessButton.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				ListGridRecord projectAccessLgr = projectAccessGrid.getSelectedRecord();

				removeProjectAccess(projectAccessLgr.getAttributeAsInt("accessId"));
				
			}});
		
		projectToolStrip.addButton(removeProjectAccessButton);
		
		ToolStripButton addMicroarraystudyProjectButton = new ToolStripButton();  
		addMicroarraystudyProjectButton.setTitle("add Microarraystudy to Project");
		addMicroarraystudyProjectButton.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				// TODO Auto-generated method stub
				SC.say("Not implemented yet!");
			}});
		
		projectToolStrip.addButton(addMicroarraystudyProjectButton);
		
		ToolStripButton removeMicroarraystudyProjectButton = new ToolStripButton();  
		removeMicroarraystudyProjectButton.setTitle("remove Microarraystudy from Project");
		removeMicroarraystudyProjectButton.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				// TODO Auto-generated method stub
				SC.say("Not implemented yet!");
			}});
		
		projectToolStrip.addButton(removeMicroarraystudyProjectButton);
		
		controlsPanel.addMember(projectToolStrip);
		
		headerContainer.addMember(controlsPanel);
		
		pane.addMember(headerContainer);
		
		HLayout gridContainer = new HLayout();
		gridContainer.setWidth100();
		gridContainer.setHeight100();
		
		projectGrid = new ListGrid();
		projectGrid.setWidth("50%");
		projectGrid.setHeight100();
		projectGrid.setShowAllRecords(true);  
		projectGrid.setAlternateRecordStyles(true);
		projectGrid.setWrapCells(true);
		projectGrid.setFixedRecordHeights(false);
		projectGrid.markForRedraw();
		
		ListGridField lgfProjectId = new ListGridField("projectId", "Project ID");
		ListGridField lgfProjectName = new ListGridField("projectName", "Project Name");
		ListGridField lgfProjectActivated = new ListGridField("projectDescription", "Description");
		
		projectGrid.setFields(lgfProjectId, lgfProjectName, lgfProjectActivated);
		
		showAllProjects();
		
		gridContainer.addMember(projectGrid);
		
		projectMstudyGrid = new ListGrid();
		projectMstudyGrid.setWidth("50%");
		projectMstudyGrid.setHeight100();
		projectMstudyGrid.setShowAllRecords(true);  
		projectMstudyGrid.setAlternateRecordStyles(true);
		projectMstudyGrid.setWrapCells(true);
		projectMstudyGrid.setFixedRecordHeights(false);
		projectMstudyGrid.markForRedraw();
		
		ListGridField lgfProjectMstudyId = new ListGridField("mstudyId", "Microarraystudy ID");
		ListGridField lgfProjectMstudyName = new ListGridField("mstudyName", "Name");
		ListGridField lgfProjectMstudyDescription = new ListGridField("mstudyDescription", "Description");
		
		projectMstudyGrid.setFields(lgfProjectMstudyId, lgfProjectMstudyName, lgfProjectMstudyDescription);
		
		gridContainer.addMember(projectMstudyGrid);
		
		pane.addMember(gridContainer);
		
		projectAccessGrid = new ListGrid();
		projectAccessGrid.setWidth100();
		projectAccessGrid.setHeight("50%");
		projectAccessGrid.setShowAllRecords(true);  
		projectAccessGrid.setAlternateRecordStyles(true);
		projectAccessGrid.setWrapCells(true);
		projectAccessGrid.setFixedRecordHeights(false);
		projectAccessGrid.markForRedraw();
		
		ListGridField lgfProjectAccessId = new ListGridField("accessId", "ID");
		ListGridField lgfProjectAccessGroup = new ListGridField("accessGroup", "Group");
		ListGridField lgfProjectAccessRight = new ListGridField("accessRight", "Access Right");
		
		projectAccessGrid.setFields(lgfProjectAccessId, lgfProjectAccessGroup, lgfProjectAccessRight);
		
		pane.addMember(projectAccessGrid);
		
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
		
		studyName = new TextItem();
		studyName.setTitle("study name");
		
		chip = new SelectItem();
		chip.setTitle("chip type");  
		
		tissue = new SelectItem();
		tissue.setTitle("tissue");
		
		project = new SelectItem();
		project.setTitle("project");
		
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
	
	public void showAllGroups(){
		
		final AdminAsync req = (AdminAsync) GWT.create(Admin.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) req;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "AdminService";
		endpoint.setServiceEntryPoint(moduleRelativeURL);
		final AsyncCallback<FoGroup[]> callback = new AsyncCallback<FoGroup[]>(){
			
			public void onSuccess(FoGroup[] result){
				
				FoGroup[] groups = result;
				
				groupGrid.addRecordClickHandler(new MyGroupRecordClickHandler(groupUserGrid,groups));
								
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
	
	public void showAllProjects(){
		
		final AdminAsync req = (AdminAsync) GWT.create(Admin.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) req;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "AdminService";
		endpoint.setServiceEntryPoint(moduleRelativeURL);
		final AsyncCallback<FoProject[]> callback = new AsyncCallback<FoProject[]>(){
			
			public void onSuccess(FoProject[] result){
				
				FoProject[] projects = result;
				
				projectGrid.addRecordClickHandler(new MyProjectRecordClickHandler(projectMstudyGrid, projectAccessGrid, projects));
								
				ListGridRecord[] lgr = new ListGridRecord[projects.length];
				
				for(int i=0; i < projects.length; i++){
					lgr[i] = new ListGridRecord();
					lgr[i].setAttribute("projectId", projects[i].getId());
					lgr[i].setAttribute("projectName", projects[i].getName());
					lgr[i].setAttribute("projectDescription", projects[i].getDescription());
				}

				projectGrid.setData(lgr);
				
			}
			public void onFailure(Throwable caught){
				SC.say(caught.getMessage());
			}

		};
		req.getAllFoProjects(callback);
	}
	
	public void addProject(FoProject foProject){
		
		final AdminAsync req = (AdminAsync) GWT.create(Admin.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) req;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "AdminService";
		endpoint.setServiceEntryPoint(moduleRelativeURL);
		final AsyncCallback<FoProject> callback = new AsyncCallback<FoProject>(){
			
			public void onSuccess(FoProject result){
				FoProject project = result;
				
				ListGridRecord lgr = new ListGridRecord();
				lgr.setAttribute("projectId", project.getId());
				lgr.setAttribute("projectName", project.getName());
				lgr.setAttribute("projectDescription", project.getDescription());
				
				projectGrid.addData(lgr);
				
			}
			public void onFailure(Throwable caught){
				SC.say(caught.getMessage());
			}

		};
		req.addFoProject(foProject, callback);
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
	
	public void addProjectAccess(FoProjectAccess foProjectAccess, int projectId){
		
		final AdminAsync req = (AdminAsync) GWT.create(Admin.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) req;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "AdminService";
		endpoint.setServiceEntryPoint(moduleRelativeURL);
		final AsyncCallback<FoProjectAccess> callback = new AsyncCallback<FoProjectAccess>(){
			
			public void onSuccess(FoProjectAccess result){
				
				ListGridRecord lgr = new ListGridRecord();
				lgr.setAttribute("accessId", result.getId());
				lgr.setAttribute("accessGroup", result.getFoGroup().getName());
				lgr.setAttribute("accessRight", result.getAccess());
				
				projectAccessGrid.addData(lgr);
			}
			public void onFailure(Throwable caught){
				SC.say(caught.getMessage());
			}
		};
		
		req.addAccessToFoProject(foProjectAccess, projectId, callback);
	}
	
	public void removeProjectAccess(int projectAccessId){
		
		final AdminAsync req = (AdminAsync) GWT.create(Admin.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) req;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "AdminService";
		endpoint.setServiceEntryPoint(moduleRelativeURL);
		final AsyncCallback<Boolean> callback = new AsyncCallback<Boolean>(){
			
			public void onSuccess(Boolean result){
				
			projectAccessGrid.removeData(projectAccessGrid.getSelectedRecord());
				
			}
			public void onFailure(Throwable caught){
				SC.say(caught.getMessage());
			}
		};
		
		req.removeAccessFromFoProject(projectAccessId, callback);
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
							propertyIds[j] = Integer.parseInt(((SelectItem) newData[i]).getValueAsString());
							j++;
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
	private FoProject[] projects;
	
	public MyProjectRecordClickHandler(ListGrid projectMstudyGrid, ListGrid projectAccessGrid, FoProject[] projects){
		this.projectMstudyGrid = projectMstudyGrid;
		this.projectAccessGrid = projectAccessGrid;
		this.projects = projects;
	}
	
	@Override
	public void onRecordClick(RecordClickEvent event) {
		ListGridRecord[] oldMstudyRecords = projectMstudyGrid.getRecords();
		
		for (int i= 0; i < oldMstudyRecords.length; i++){
			projectMstudyGrid.removeData(oldMstudyRecords[i]);
		}
		
		FoMicroarraystudy[] mstudies = projects[Integer.parseInt(event.getRecord().getAttribute("projectId")) -1].getMstudies();
		
		if(mstudies != null){
		
			ListGridRecord[] mstudyLgr = new ListGridRecord[mstudies.length];
		
			for(int i=0; i < mstudies.length; i++){
				mstudyLgr[i] = new ListGridRecord();
				mstudyLgr[i].setAttribute("mstudyId", mstudies[i].getId());
				mstudyLgr[i].setAttribute("mstudyName", mstudies[i].getName());
				mstudyLgr[i].setAttribute("mstudyDescription", mstudies[i].getDescription());
			}

			projectMstudyGrid.setData(mstudyLgr);
		
		}
		
		ListGridRecord[] oldAccessRecords = projectAccessGrid.getRecords();
		
		for (int i= 0; i < oldAccessRecords.length; i++){
			projectAccessGrid.removeData(oldAccessRecords[i]);
		}
		
		FoProjectAccess[] accesses = projects[Integer.parseInt(event.getRecord().getAttribute("projectId")) -1].getProjectAccess();

		if(accesses != null){
		
			ListGridRecord[] accessLgr = new ListGridRecord[accesses.length];
		
			for(int i=0; i < accesses.length; i++){
				accessLgr[i] = new ListGridRecord();
				accessLgr[i].setAttribute("accessId", accesses[i].getId());
				accessLgr[i].setAttribute("accessGroup", accesses[i].getFoGroup().getName());
				accessLgr[i].setAttribute("accessRight", accesses[i].getAccess());
			}

			projectAccessGrid.setData(accessLgr);
		}
	}
}

class MyGroupRecordClickHandler implements RecordClickHandler {

	private ListGrid groupUserGrid;
	private FoGroup[] groups;
	
	public MyGroupRecordClickHandler(ListGrid groupUserGrid, FoGroup[] groups){
		this.groupUserGrid = groupUserGrid;
		this.groups = groups;
	}
	
	@Override
	public void onRecordClick(RecordClickEvent event) {
		ListGridRecord[] oldRecords = groupUserGrid.getRecords();
		
		for (int i= 0; i < oldRecords.length; i++){
			groupUserGrid.removeData(oldRecords[i]);
		}
		
		FoUser[] users = groups[Integer.parseInt(event.getRecord().getAttribute("groupId")) -1].getUsers();

		ListGridRecord[] userLgr = new ListGridRecord[users.length];
		
		
		for(int i=0; i < users.length; i++){
			userLgr[i] = new ListGridRecord();
			userLgr[i].setAttribute("userid", users[i].getId());
			userLgr[i].setAttribute("userName", users[i].getUserName());
		}

		groupUserGrid.setData(userLgr);
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
		
		if(recInfo.getType().equals("cnc")){
			
			cncDetails(recInfo.getElementName());
			
		}
		if(recInfo.getType().equals("gene")){

			geneDetails(recInfo.getElementName());
			
		}
	}
	
	public void cncDetails(String query){
		
		final SearchAsync req = (SearchAsync) GWT.create(Search.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) req;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "Search";
		endpoint.setServiceEntryPoint(moduleRelativeURL);
		final AsyncCallback<CopyNumberChange> callback = new AsyncCallback<CopyNumberChange>(){
			public void onSuccess(CopyNumberChange cncData){
				
				cp.loadWindow(cncData);
				
			}
			public void onFailure(Throwable caught){
				System.out.println(caught.getMessage());
				SC.say(caught.getMessage());
			}
		};
		req.getCNCInfo(query, callback);
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