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
import com.smartgwt.client.widgets.form.fields.LinkItem;
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
import de.unihamburg.zbh.fishoracle.client.data.GWTImageInfo;
import de.unihamburg.zbh.fishoracle.client.data.Gen;
import de.unihamburg.zbh.fishoracle.client.data.MicroarrayOptions;
import de.unihamburg.zbh.fishoracle.client.data.RecMapInfo;
import de.unihamburg.zbh.fishoracle.client.data.User;
import de.unihamburg.zbh.fishoracle.client.ImgCanvas;
import de.unihamburg.zbh.fishoracle.client.rpc.Admin;
import de.unihamburg.zbh.fishoracle.client.rpc.AdminAsync;
import de.unihamburg.zbh.fishoracle.client.rpc.Search;
import de.unihamburg.zbh.fishoracle.client.rpc.SearchAsync;

public class CenterPanel extends VLayout{

	private ListGrid userGrid;
	
	private TabSet centerTabSet = null;
	private TextItem chrTextItem;
	private TextItem startTextItem;
	private TextItem endTextItem;
	private TextItem lowerThTextItem;
	private TextItem upperThTextItem;
	
	private FormPanel uploadForm;
	private FileUpload fu;
	private TextItem studyName;
	private SelectItem chip;
	private SelectItem tissue;
	private SelectItem pstage;
	private SelectItem pgrade;
	private SelectItem metaStatus;
	private TextItem sampleId;
	private TextAreaItem descriptionItem;
	
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
        		" As the CNC data is incompatible to the Ensembl version 57 the currently" +
        		" used Ensembl version is 54. If you want to search for a gene in the Ensembl " +
        		" browser you better also use version 54 " +
        		"<a href=\"http://may2009.archive.ensembl.org\" target=_blank>http://may09.archive.ensembl.org</a>" +
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
	
	/*
	public void newDataTab(CopyNumberChange[] cncs, boolean isAmplicon) {
		
		String type = null;
		
		if(isAmplicon){
			type = "amplicons";
		} else {
			type = "delicons";
		}
		
		Tab tab = new Tab("List of all " + type);
		tab.setCanClose(true);
		
		final ListGrid summaryGrid = new ListGrid();
		summaryGrid.setWidth100();
		summaryGrid.setHeight100();
		summaryGrid.setShowAllRecords(true);  
		summaryGrid.setAlternateRecordStyles(true);
		summaryGrid.setWrapCells(true);
		summaryGrid.setFixedRecordHeights(false);
		
		ListGridField lgfId = new ListGridField("stableId", "Stable ID");
		ListGridField lgfChromosome = new ListGridField("chromosome", "Chromosome");
		ListGridField lgfStart = new ListGridField("start", "Start");
		ListGridField lgfEnd = new ListGridField("end", "End");
		ListGridField lgfCaseName = new ListGridField("caseName", "Case name");
		ListGridField lgfTumorType = new ListGridField("tumorType", "Tumor Type");
		ListGridField lgfContinuous = new ListGridField("continuous", "Continuous");
		ListGridField lgfLevel = new ListGridField("level", "Level");
		
		summaryGrid.setFields(lgfId, lgfChromosome, lgfStart, lgfEnd, lgfCaseName, lgfTumorType, lgfContinuous, lgfLevel);
		
		ListGridRecord[] lgr = new ListGridRecord[cncs.length];
		
		for(int i=0; i < cncs.length; i++){
			lgr[i] = new ListGridRecord();
			lgr[i].setAttribute("stableId", cncs[i].getCncStableId());
			lgr[i].setAttribute("chromosome", cncs[i].getChromosome());
			lgr[i].setAttribute("start", cncs[i].getStart());
			lgr[i].setAttribute("end", cncs[i].getEnd());
			lgr[i].setAttribute("caseName", cncs[i].getCaseName());
			lgr[i].setAttribute("tumorType", cncs[i].getTumorType());
			lgr[i].setAttribute("continuous", cncs[i].getContinuous());
			lgr[i].setAttribute("level", cncs[i].getCnclevel());
		}
		
		summaryGrid.setData(lgr);
		
		tab.setPane(summaryGrid);
		
		centerTabSet.addTab(tab);
		
		centerTabSet.selectTab(tab);
	}
	*/
	public void openUserAdminTab(final User[] users){
	
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
		
		/* 
		 * normalized with
		 * sample id
		 * */
		
		HLayout metaData = new HLayout();
		metaData.setWidth100();
		metaData.setAutoHeight();
		metaData.setDefaultLayoutAlign(Alignment.CENTER);
		
		metaData.addMember(new LayoutSpacer());
		
		DynamicForm metaDataForm = new DynamicForm();
		
		studyName = new TextItem();
		studyName.setTitle("study name");
		
		chip = new SelectItem();
		chip.setTitle("chip type");  
		
		tissue = new SelectItem();
		tissue.setTitle("tissue");
		
		pstage = new SelectItem();
		pstage.setTitle("pathological stage");
		
		pgrade = new SelectItem();
		pgrade.setTitle("pathological grade");
		
		metaStatus = new SelectItem();
		metaStatus.setTitle("meta status");
		
		sampleId = new TextItem();
		sampleId.setTitle("sample Id");
		
		descriptionItem = new TextAreaItem();
		descriptionItem.setTitle("description");  
		
		ButtonItem submitButton = new ButtonItem("submit");
		submitButton.addClickHandler(new com.smartgwt.client.widgets.form.fields.events.ClickHandler(){

			@Override
			public void onClick(
					com.smartgwt.client.widgets.form.fields.events.ClickEvent event) {
				checkImportData();
			}
			
			
		});
		
		metaDataForm.setItems(studyName, chip, tissue, pstage, pgrade, metaStatus, sampleId, descriptionItem, submitButton);
		
		metaData.addMember(metaDataForm);

		metaData.addMember(new LayoutSpacer());
		
		pane.addMember(metaData);
		
		getMicroarrayOptions();
		
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
				
				for(int i=0; i < result.getChipName().length; i++){
					chipValueMap.put(new Integer(i).toString(),result.getChipName()[i]);
				}
				
				chip.setValueMap(chipValueMap);
				
				LinkedHashMap<String, String> tissueValueMap = new LinkedHashMap<String, String>();
				
				for(int i=0; i < result.getTissue().length; i++){
					tissueValueMap.put(new Integer(i).toString(),result.getTissue()[i]);
				}
				
				tissue.setValueMap(tissueValueMap);
				
				LinkedHashMap<String, String> pstageValueMap = new LinkedHashMap<String, String>();
				
				for(int i=0; i < result.getPStage().length; i++){
					pstageValueMap.put(new Integer(i).toString(),result.getPStage()[i]);
				}
				
				pstage.setValueMap(pstageValueMap);
				
				LinkedHashMap<String, String> pgradeValueMap = new LinkedHashMap<String, String>();
				
				for(int i=0; i < result.getPGrade().length; i++){
					pgradeValueMap.put(new Integer(i).toString(),result.getPGrade()[i]);
				}
				
				pgrade.setValueMap(pgradeValueMap);
				
				LinkedHashMap<String, String> mstatusValueMap = new LinkedHashMap<String, String>();
				
				for(int i=0; i < result.getMetaStatus().length; i++){
					mstatusValueMap.put(new Integer(i).toString(),result.getMetaStatus()[i]);
				}
				
				metaStatus.setValueMap(mstatusValueMap);
				
			}
			public void onFailure(Throwable caught){
				System.out.println(caught.getMessage());
				SC.say(caught.getMessage());
			}
		};
		req.getMicroarrayOptions(callback);
	}
	
	public void importData(String fileName,
							String studyName,
							String chipType,
							String tissue,
							String pstage,
							String pgrade,
							String metaStatus,
							String sampleId,
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
						chipType,
						tissue,
						pstage,
						pgrade,
						metaStatus,
						description,
						sampleId,
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
					importData(fu.getFilename(), studyName.getDisplayValue(), chip.getDisplayValue(), tissue.getDisplayValue(), 
							pstage.getDisplayValue(), pgrade.getDisplayValue(), metaStatus.getDisplayValue(),
							sampleId.getDisplayValue(), descriptionItem.getDisplayValue());
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