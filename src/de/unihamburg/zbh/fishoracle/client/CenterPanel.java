/*
  Copyright (c) 2009-2014 Malte Mader <mader@zbh.uni-hamburg.de>
  Copyright (c) 2009-2014 Center for Bioinformatics, University of Hamburg

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

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;

import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.Cursor;
import com.smartgwt.client.types.GroupStartOpen;
import com.smartgwt.client.types.ListGridEditEvent;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.SelectionType;
import com.smartgwt.client.types.Side;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Img;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.Progressbar;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.MouseOverEvent;
import com.smartgwt.client.widgets.events.MouseOverHandler;

import com.smartgwt.client.widgets.events.ResizedEvent;
import com.smartgwt.client.widgets.events.ResizedHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.LinkItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.form.fields.events.KeyPressEvent;
import com.smartgwt.client.widgets.form.fields.events.KeyPressHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.RecordClickEvent;
import com.smartgwt.client.widgets.grid.events.RecordClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.menu.MenuItem;
import com.smartgwt.client.widgets.menu.events.MenuItemClickEvent;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;
import com.smartgwt.client.widgets.toolbar.ToolStripMenuButton;

import de.unihamburg.zbh.fishoracle.client.data.DBConfigData;
import de.unihamburg.zbh.fishoracle.client.data.FoConfigData;
import de.unihamburg.zbh.fishoracle.client.data.FoConstants;
import de.unihamburg.zbh.fishoracle.client.data.FoSegment;
import de.unihamburg.zbh.fishoracle.client.data.GWTImageInfo;
import de.unihamburg.zbh.fishoracle.client.data.EnsemblGene;
import de.unihamburg.zbh.fishoracle.client.data.QueryInfo;
import de.unihamburg.zbh.fishoracle.client.data.RecMapInfo;
import de.unihamburg.zbh.fishoracle.client.data.FoUser;
import de.unihamburg.zbh.fishoracle.client.datasource.FeatureDS;
import de.unihamburg.zbh.fishoracle.client.datasource.SNPMutationDS;
import de.unihamburg.zbh.fishoracle.client.datasource.SegmentDS;
import de.unihamburg.zbh.fishoracle.client.datasource.StudyDS;
import de.unihamburg.zbh.fishoracle.client.datasource.OperationId;
import de.unihamburg.zbh.fishoracle.client.datasource.ProjectDS;
import de.unihamburg.zbh.fishoracle.client.datasource.TranslocationDS;
import de.unihamburg.zbh.fishoracle.client.ImgCanvas;
import de.unihamburg.zbh.fishoracle.client.rpc.SearchService;
import de.unihamburg.zbh.fishoracle.client.rpc.SearchServiceAsync;
import de.unihamburg.zbh.fishoracle.client.rpc.UserService;
import de.unihamburg.zbh.fishoracle.client.rpc.UserServiceAsync;

public class CenterPanel extends VLayout {

	private ToolStripButton selectButton;
	private ListGrid studyGrid;
	private ListGrid dataTypeGrid;
	private SelectItem projectSelectItem;
	
	private TabSet centerTabSet = null;
	private TextItem chrTextItem;
	private TextItem startTextItem;
	private TextItem endTextItem;
    
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
        		"<p id=\"welcome\">You can search for genomic regions" +
        		" or for a gene specified by a gene name e.g. 'kras'" +
        		" or a karyoband." +
        		" By clicking on an element a window opens that shows additional information." +
        		"</p><p id=\"welcome\"><br>FISH Oracle uses:<br> " +
        		"<ul id=\"welcome\">" +
        		"<li> the Google Web Toolkit <a href=\"http://code.google.com/webtoolkit/\" target=_blank>http://code.google.com/webtoolkit/</a></li>" +
        		"<li> the SmartGWT <a href=\"http://code.google.com/p/smartgwt/\" target=_blank>http://code.google.com/p/smartgwt/</a></li>" +
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

	public MainPanel getMainPanel() {
		return mp;
	}
	
	public ToolStripButton getSelectButtion() {
		return selectButton;
	}
	
	public void refreshRange(){
		
		GWTImageInfo imgInfo;
		
		ImgCanvas imgLayer = (ImgCanvas) cp.getCenterTabSet().getSelectedTab().getPane().getChildren()[1];
		imgInfo = imgLayer.getImageInfo();
		
		String newChr;
	    int newStart;
	    int newEnd;
	    
		newChr = chrTextItem.getDisplayValue();
	    
	    newStart = Integer.parseInt(startTextItem.getDisplayValue());
	    
	    newEnd = Integer.parseInt(endTextItem.getDisplayValue());
	    
	    if(newStart >= newEnd || newEnd - newStart <= 10){
	    	
	    	SC.say("The end value must at least be 10 base pairs greater than the start value!");
	    	
	    } else {
	    
	    	imgInfo.setChromosome(newChr);
	    	
	    	imgInfo.setStart(newStart);
	    
	    	imgInfo.setEnd(newEnd);
	    	
	    	cp.imageRedraw(imgInfo);
	    }
	}
	
	public ImgCanvas createImageLayer(GWTImageInfo imgInfo){
		
		ImgCanvas image = new ImgCanvas(imgInfo, this);
		image.setOverflow(Overflow.HIDDEN);
        image.setWidth(imgInfo.getWidth());
        image.setHeight(imgInfo.getHeight());
        image.setAppImgDir("/");
		
        image.addMouseOverHandler(new MouseOverHandler(){

        	@Override
        	public void onMouseOver(MouseOverEvent event) {
        		ImgCanvas image = (ImgCanvas) event.getSource();

        		if(!image.isSetChildren()){

        			int rmc;

        			GWTImageInfo imgInfo = image.getImageInfo();

        			for(rmc=0; rmc < imgInfo.getRecmapinfo().size(); rmc++){
        				
        				if((imgInfo.getRecmapinfo().get(rmc).getSoutheastX() - imgInfo.getRecmapinfo().get(rmc).getNorthwestX()) < 3 
        						&& !imgInfo.getRecmapinfo().get(rmc).getType().equals("gene")
        						&& !imgInfo.getRecmapinfo().get(rmc).getType().equals("translocation")
        						&& !imgInfo.getRecmapinfo().get(rmc).getType().equals("mutation")){
        					continue;
        				}
        				
        				final Img spaceImg = new Img("1pximg.gif");

        				spaceImg.addClickHandler(new RecMapClickHandler(imgInfo.getRecmapinfo().get(rmc), imgInfo, cp));

        				int southeast_x = (int) imgInfo.getRecmapinfo().get(rmc).getSoutheastX();

        				int northwest_x = (int) imgInfo.getRecmapinfo().get(rmc).getNorthwestX();

        				if(southeast_x > imgInfo.getWidth()){
        					spaceImg.setWidth(imgInfo.getWidth() - northwest_x);
        				} else {
        					spaceImg.setWidth(southeast_x - northwest_x);
        				}

        				if(spaceImg.getWidth() <= 0){
        					if(!imgInfo.getRecmapinfo().get(rmc).getType().equals("translocation")  ){
        						spaceImg.setWidth(1);
            				} else {
            					spaceImg.setWidth(10);
            				}
        				}
        				if(imgInfo.getRecmapinfo().get(rmc).getType().equals("translocation")  ){
        					
        				}

        				int southeast_y = (int) imgInfo.getRecmapinfo().get(rmc).getSoutheastY();

        				int northwest_y = (int) imgInfo.getRecmapinfo().get(rmc).getNorthwestY();

        				spaceImg.setHeight(southeast_y - northwest_y);

        				spaceImg.setLeft( (int) imgInfo.getRecmapinfo().get(rmc).getNorthwestX());

        				spaceImg.setTop( (int) imgInfo.getRecmapinfo().get(rmc).getNorthwestY());

        				spaceImg.setCursor(Cursor.HAND);

        				image.addChild(spaceImg);

        			}
        			
        			image.setSetChildren(true);
        		}

        	}

        });
        
		return image;
	}
	
	public void newImageTab(final GWTImageInfo imgInfo){
		
		Tab imgTab = new Tab(imgInfo.getQuery().getQueryString() + " (" + imgInfo.getQuery().getConfig().getStrArray("ensemblDBLabel")[0] + ")"); 
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
		
		ToolStripButton configButton = new ToolStripButton();
		configButton.setTitle("Configure");
		configButton.addClickHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				
				ImgCanvas imgLayer = (ImgCanvas) cp.getCenterTabSet().getSelectedTab().getPane().getChildren()[1];
				GWTImageInfo imgInfo = imgLayer.getImageInfo();
				
				Window w = new Window();
				w.setTitle("Configuration");
				//w.setAutoCenter(true);
				w.setLeft(500);
				w.setWidth(300);
				w.setHeight(cp.getInnerHeight() - 300);
				w.setIsModal(true);
				w.setShowModalMask(true);
				ConfigLayout cl = new ConfigLayout(mp, false);
				cl.setImgInfo(imgInfo);
				cl.setWin(w);
				cl.loadConfig(imgInfo.getQuery().getConfig(), false);
				cl.getSearchTextItem().setValue(imgInfo.getQuery().getQueryString());
				cl.getSearchRadioGroupItem().setValue(imgInfo.getQuery().getSearchType());
				if(imgInfo.getQuery().getSearchType().equals("Region")){
					cl.getChrTextItem().setValue(chrTextItem.getDisplayValue());
					cl.getStartTextItem().setValue(startTextItem.getDisplayValue());
					cl.getEndTextItem().setValue(endTextItem.getDisplayValue());
					cl.getSearchTextItem().hide();
					cl.getChrTextItem().show();
					cl.getStartTextItem().show();
					cl.getEndTextItem().show();
				}
				w.addItem(cl);
				w.show();
			}
		});
		
		presentationToolStrip.addButton(configButton);
		
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
		
		MenuItem pdfExportItem = new MenuItem("Export image as pdf document");
		MenuItem psExportItem = new MenuItem("Export image as ps document");
		MenuItem svgExportItem = new MenuItem("Export image as svg document");
		MenuItem pngExportItem = new MenuItem("Export image as png document");
		
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
		
		exportMenu.setItems(pdfExportItem, psExportItem, svgExportItem, pngExportItem);
		
		final ImgCanvas image = createImageLayer(imgInfo);
		
		ToolStripMenuButton exportMenuButton = new ToolStripMenuButton("Export", exportMenu);
		
		selectButton = new ToolStripButton();
		selectButton.setTitle("Select");
		selectButton.setActionType(SelectionType.CHECKBOX);
		selectButton.setSelected(false);
		selectButton.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				ToolStripButton b = (ToolStripButton) event.getSource();
				
				if(!b.isSelected()){
					ImgCanvas imgLayer = (ImgCanvas) cp.getCenterTabSet().getSelectedTab().getPane().getChildren()[1];
					imgLayer.hideRec();
				}
			}
			
		});
		
		ToolStripButton ensemblButton = new ToolStripButton();
		ensemblButton = new ToolStripButton();
		ensemblButton.setTitle("Ensembl");
		ensemblButton.setTooltip("Show region in Ensembl");
		ensemblButton.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				
				String chr = imgInfo.getChromosome();
				String start = String.valueOf(imgInfo.getStart());
				String end = String.valueOf(imgInfo.getEnd());
				
				String url = "http://www.ensembl.org/Homo_sapiens/Location/View?r=" + chr + ":" + start + "-" + end;
				
				com.google.gwt.user.client.Window.open(url,"_blank",null);
			}
			
		});
		
		presentationToolStrip.addButton(selectButton);
		presentationToolStrip.addButton(ensemblButton);
		
		presentationToolStrip.addMenuButton(exportMenuButton);
		
		presentationLayer.addMember(presentationToolStrip);
		
		image.setChromosome(chrTextItem);
		image.setStart(startTextItem);
		image.setEnd(endTextItem);
		
		presentationLayer.addMember(image);
		
		imgTab.setPane(presentationLayer);
		
		centerTabSet.addTab(imgTab);
	
		centerTabSet.selectTab(imgTab);
		
	}
	
	public void loadMutationWindow(String geneId, int trackId, FoConfigData cd){
		Window window = new Window();
		window.setTitle("SNVs");
		window.setWidth(700);
		window.setHeight(330);
		window.setAutoCenter(true);
		window.setCanDragResize(true);
		
		ListGrid mutGrid = new ListGrid();
		mutGrid.setWidth100();
		mutGrid.setHeight100();
		mutGrid.setShowAllRecords(true);
		mutGrid.setAlternateRecordStyles(true);
		mutGrid.setShowHeader(true);
		mutGrid.setWrapCells(true);
		mutGrid.setFixedRecordHeights(false);
		
		mutGrid.setShowAllRecords(false);
		mutGrid.setAutoFetchData(false);
		
		SNPMutationDS mDS = new SNPMutationDS();
		mDS.addConfigData(cd);
		
		Criteria c = new Criteria("geneId", geneId);
		c.setAttribute("trackId", trackId);
		
		mutGrid.setDataSource(mDS);
		mutGrid.setFetchOperation(OperationId.MUTATION_FETCH_FOR_ATTRIBS);
		mutGrid.fetchData(c);
		
		mutGrid.setGroupStartOpen(GroupStartOpen.ALL);
		mutGrid.groupBy("studyName");
		
		window.addItem(mutGrid);
		
		window.show();
	}
	
	public void loadWindow(FoSegment segmentData){
		
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
		lgr[1].setAttribute("val", segmentData.getLocation().getChromosome());
		
		lgr[2] = new ListGridRecord();
		lgr[2].setAttribute("key", "Start");
		lgr[2].setAttribute("val", segmentData.getLocation().getStart());
		
		lgr[3] = new ListGridRecord();
		lgr[3].setAttribute("key", "End");
		lgr[3].setAttribute("val", segmentData.getLocation().getEnd());
		
		lgr[4] = new ListGridRecord();
		lgr[4].setAttribute("key", "Segment Mean");
		lgr[4].setAttribute("val", segmentData.getMean());
		
		lgr[5] = new ListGridRecord();
		lgr[5].setAttribute("key", "Markers");
		lgr[5].setAttribute("val", segmentData.getNumberOfMarkers());
		
		lgr[6] = new ListGridRecord();
		lgr[6].setAttribute("key", "Status");
		lgr[6].setAttribute("val", segmentData.getStatus());
		
		lgr[7] = new ListGridRecord();
		lgr[7].setAttribute("key", "Status score");
		lgr[7].setAttribute("val", segmentData.getStatusScore());
		
		lgr[8] = new ListGridRecord();
		lgr[8].setAttribute("key", "Study");
		lgr[8].setAttribute("val", segmentData.getStudyName());
		
		cncGrid.setData(lgr);
		
		window.addItem(cncGrid);
		
		window.show();
	}
	
	public void loadWindow(EnsemblGene gene){
		
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
	
	public void openDataTab(String type, String studyId){
		Tab segmentAdminTab = new Tab(type);
		segmentAdminTab.setCanClose(true);
		
		VLayout pane = new VLayout();
		pane.setWidth100();
		pane.setHeight100();
		pane.setDefaultLayoutAlign(Alignment.CENTER);
		
		HLayout gridContainer = new HLayout();
		gridContainer.setWidth100();
		gridContainer.setHeight100();
		
		dataTypeGrid = new ListGrid();
		dataTypeGrid.setWidth100();
		dataTypeGrid.setHeight100();
		dataTypeGrid.setShowAllRecords(true);
		dataTypeGrid.setAlternateRecordStyles(true);
		dataTypeGrid.setWrapCells(true);
		dataTypeGrid.setFixedRecordHeights(false);
		dataTypeGrid.setAutoFetchData(false);
		dataTypeGrid.setShowAllRecords(false);
		
		
		if(type.equals("Segments")){
			SegmentDS sDS = new SegmentDS();
		
			dataTypeGrid.setDataSource(sDS);
		}
		
		if(type.equals(FoConstants.SNV)){
			SNPMutationDS mDS = new SNPMutationDS();
		
			dataTypeGrid.setDataSource(mDS);
			dataTypeGrid.setFetchOperation(OperationId.MUTATION_FETCH_FOR_STUDY_ID);
		}
		
		if(type.equals("Translocations")){
			TranslocationDS tDS = new TranslocationDS();
		
			dataTypeGrid.setDataSource(tDS);
		}
		
		if(type.equals("Features")){
			FeatureDS fDS = new FeatureDS();
			
			dataTypeGrid.setDataSource(fDS);
			
			dataTypeGrid.setFetchOperation(OperationId.FEATURE_FETCH_FOR_STUDY_ID);
		}
		
		dataTypeGrid.fetchData(new Criteria("studyId", studyId));
		
		gridContainer.addMember(dataTypeGrid);
		
		pane.addMember(gridContainer);
		
		segmentAdminTab.setPane(pane);
		
		centerTabSet.addTab(segmentAdminTab);
		
		centerTabSet.selectTab(segmentAdminTab);
	}
	
	public void openStudyAdminTab(FoUser user){
		
		Tab studyAdminTab = new Tab("Manage Studies");
		studyAdminTab.setCanClose(true);
		
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
		
		ToolStrip sToolStrip = new ToolStrip();
		sToolStrip.setWidth100();
		
		projectSelectItem = new SelectItem();
		projectSelectItem.setTitle("Project");
		
		projectSelectItem.setDisplayField("projectName");
		projectSelectItem.setValueField("projectId");		
		
		projectSelectItem.setAutoFetchData(false);
		
		ProjectDS pDS = new ProjectDS();
		
		projectSelectItem.setOptionDataSource(pDS);
		projectSelectItem.setOptionOperationId(OperationId.PROJECT_FETCH_ALL);
		
		projectSelectItem.setDefaultToFirstOption(true);
		projectSelectItem.addChangedHandler(new ChangedHandler() {

			@Override
			public void onChanged(ChangedEvent event) {
				
				String projectId = projectSelectItem.getValueAsString();
				
				studyGrid.fetchData(new Criteria("projectId", projectId));
			}
		});
		
		sToolStrip.addFormItem(projectSelectItem);
		
		ToolStripButton showSegmentsButton = new ToolStripButton();
		showSegmentsButton.setTitle("show segments");
		showSegmentsButton.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				
				ListGridRecord lgr = studyGrid.getSelectedRecord();
				
				if (lgr != null){
				
					openDataTab("Segments", lgr.getAttributeAsString("studyId"));
					
				} else {
					SC.say("Select a study.");
				}
				
			}});
		
		sToolStrip.addButton(showSegmentsButton);
		
		ToolStripButton showMutationsButton = new ToolStripButton();
		showMutationsButton.setTitle("show SNVs");
		showMutationsButton.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				
				ListGridRecord lgr = studyGrid.getSelectedRecord();
				
				if (lgr != null){
				
					openDataTab(FoConstants.SNV, lgr.getAttributeAsString("studyId"));
					
				} else {
					SC.say("Select a study.");
				}
				
			}});
		
		sToolStrip.addButton(showMutationsButton);
		
		ToolStripButton showTranslocationsButton = new ToolStripButton();
		showTranslocationsButton.setTitle("show translocations");
		showTranslocationsButton.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				
				ListGridRecord lgr = studyGrid.getSelectedRecord();
				
				if (lgr != null){
				
					openDataTab("Translocations", lgr.getAttributeAsString("studyId"));
					
				} else {
					SC.say("Select a study.");
				}
				
			}});
		
		sToolStrip.addButton(showTranslocationsButton);
		
		ToolStripButton showFeaturesButton = new ToolStripButton();
		showFeaturesButton.setTitle("show features");
		showFeaturesButton.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				
				ListGridRecord lgr = studyGrid.getSelectedRecord();
				
				if (lgr != null){
				
					openDataTab("Features", lgr.getAttributeAsString("studyId"));
					
				} else {
					SC.say("Select a study.");
				}
				
			}});
		
		sToolStrip.addButton(showFeaturesButton);
		
		ToolStripButton removeStudyButton = new ToolStripButton();
		removeStudyButton.setTitle("remove study");
		if(!user.getIsAdmin()){
			removeStudyButton.setDisabled(true);
		}
		removeStudyButton.addClickHandler(new ClickHandler(){
		
			@Override
			public void onClick(ClickEvent event) {
				
				final ListGridRecord lgr = studyGrid.getSelectedRecord();
				
				if(lgr != null) {
				
					SC.confirm("Do you really want to delete " + lgr.getAttribute("studyName") + "?", new BooleanCallback(){

						@Override
						public void execute(Boolean value) {
							if(value != null && value){
								
								lgr.setAttribute("projectId", projectSelectItem.getValueAsString());
								
								studyGrid.removeData(lgr);
								
							}
						}
					});
					
				} else {
					SC.say("Select a study.");
				}
				
			}});
		
		sToolStrip.addButton(removeStudyButton);
		
		controlsPanel.addMember(sToolStrip);
		
		headerContainer.addMember(controlsPanel);
		
		pane.addMember(headerContainer);
		
		HLayout gridContainer = new HLayout();
		gridContainer.setWidth100();
		gridContainer.setHeight100();
		
		studyGrid = new ListGrid();
		studyGrid.setWidth100();
		studyGrid.setHeight100();
		studyGrid.setShowAllRecords(true);
		studyGrid.setAlternateRecordStyles(true);
		studyGrid.setWrapCells(true);
		studyGrid.setShowAllRecords(false);
		studyGrid.setAutoFetchData(true);
		studyGrid.setFixedRecordHeights(false);
		
		StudyDS mDS = new StudyDS();
		
		studyGrid.setDataSource(mDS);
		studyGrid.setFetchOperation(OperationId.STUDY_FETCH_FOR_PROJECT);
		
		gridContainer.addMember(studyGrid);
		
		pane.addMember(gridContainer);
		
		studyAdminTab.setPane(pane);
		
		centerTabSet.addTab(studyAdminTab);
		
		centerTabSet.selectTab(studyAdminTab);
	}
	
	public void openProjectConfigTab(FoUser user){
		
		ProjectConfigTab pct = new ProjectConfigTab(user);
		centerTabSet.addTab(pct);
		centerTabSet.selectTab(pct);
	}
	
	public void openDataImportTab(){
		
		ImportTab it = new ImportTab();
		centerTabSet.addTab(it);
		centerTabSet.selectTab(it);
	}
	
	/** 
	 * Administration Tabs.
	 */
	public void openUserAdminTab(){
		
		UserAdminTab uat = new UserAdminTab();
		centerTabSet.addTab(uat);
		centerTabSet.selectTab(uat);
	}
	
	public void openUserProfileTab(){
		
		UserSettingsTab us = new UserSettingsTab(mp);
		centerTabSet.addTab(us);
		centerTabSet.selectTab(us);
		
	}
	
	public void openGroupAdminTab(){
		
		GroupAdminTab gat = new GroupAdminTab();
		centerTabSet.addTab(gat);
		centerTabSet.selectTab(gat);
	}
	
	public void openOrganAdminTab(){
		
		OrganAdminTab oat = new OrganAdminTab();
		centerTabSet.addTab(oat);
		centerTabSet.selectTab(oat);
	}
	
	public void openPropertyAdminTab(){
		
		PropertyAdminTab pat = new PropertyAdminTab();
		centerTabSet.addTab(pat);
		centerTabSet.selectTab(pat);
	}
	
	public void openPlatformAdminTab(){
		
		PlatformAdminTab plat = new PlatformAdminTab();
		centerTabSet.addTab(plat);
		centerTabSet.selectTab(plat);
	}
	
	public void openEnsemblDBAdminTab(){	
		
		EnsemblDBAdminTab edbat = new EnsemblDBAdminTab();		
		centerTabSet.addTab(edbat);
		centerTabSet.selectTab(edbat);
	}
	
	public void openDatabaseConfigTab(DBConfigData dbdata){
		
		DatabaseAdminTab dat = new DatabaseAdminTab(dbdata);	
	    centerTabSet.addTab(dat);
		centerTabSet.selectTab(dat);
	}
	
	/*=============================================================================
	 *||                              RPC Calls                                  ||
	 *=============================================================================
	 * */
		
	public void imageRedraw(GWTImageInfo imgInfo){
			
		final SearchServiceAsync req = (SearchServiceAsync) GWT.create(SearchService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) req;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "SearchService";
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

			}
			public void onFailure(Throwable caught){
				System.out.println(caught.getMessage());
				SC.say(caught.getMessage());
			}
		};
		req.redrawImage(imgInfo, callback);
	}
	
	public void exportImage(GWTImageInfo imgInfo){
		
		final SearchServiceAsync req = (SearchServiceAsync) GWT.create(SearchService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) req;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "SearchService";
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
	
	public void getUserObject(final String forWhat){
		
		final UserServiceAsync req = (UserServiceAsync) GWT.create(UserService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) req;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "UserService";
		endpoint.setServiceEntryPoint(moduleRelativeURL);
		final AsyncCallback<FoUser[]> callback = new AsyncCallback<FoUser[]>(){
			
			public void onSuccess(FoUser[] result){
				
				if(forWhat.equals("ProjectAdminTab")){
					openProjectConfigTab(result[0]);
				}
				if(forWhat.equals("StudyAdminTab")){
					openStudyAdminTab(result[0]);
				}
				
			}
			public void onFailure(Throwable caught){
				SC.say(caught.getMessage());
			}
		};
		req.getSessionUserObject(callback);
	}
}

class ProgressWindow extends Window {
	
	private Progressbar bar;
	
	public ProgressWindow(int imp, int nofi){
		super();
		
		int per = getPercentage(imp, nofi);
		
		this.setTitle("Upload Files "+ per + "%");
		this.setWidth(400);
		this.setHeight(60);
		this.setAlign(Alignment.CENTER);
	
		this.setAutoCenter(true);
		this.setIsModal(true);
		this.setShowModalMask(true);
		
		bar = new Progressbar(); 
		bar.setVertical(false); 
		bar.setHeight(24);
		
		this.addItem(bar);
	
		bar.setPercentDone(per);
	}
	
	private int getPercentage(int x, int y){
		return (x/y)*100;
	}
	
	public void updateValues(int imp, int nofi){
		
		int per = getPercentage(imp, nofi);
		this.setTitle("Upload Files "+ per + "%");
		bar.setPercentDone(per);	
	}
}

class MyGroupRecordClickHandler implements RecordClickHandler {

	private ListGrid groupUserGrid;
	
	public MyGroupRecordClickHandler(ListGrid groupUserGrid){
		this.groupUserGrid = groupUserGrid;
	}
	
	@Override
	public void onRecordClick(RecordClickEvent event) {
		
		String groupId = event.getRecord().getAttribute("groupId");
		
		groupUserGrid.fetchData(new Criteria("groupId", groupId));
	}
}

class RecMapClickHandler implements ClickHandler{
	
	private RecMapInfo recInfo;
	private CenterPanel cp;
	private GWTImageInfo imgInfo;
	
	
	public RecMapClickHandler(RecMapInfo recmapinfo, GWTImageInfo imgInfo, CenterPanel centerPanel){
		this.recInfo = recmapinfo;
		this.imgInfo = imgInfo;
		this.cp = centerPanel;
	}
	
	public void onClick(ClickEvent event) {
		
		if(recInfo.getType().equals("gene")){
			
			geneDetails(recInfo.getElementName(), imgInfo.getQuery().getConfig().getStrArray("ensemblDBName")[0]);
		}
		
		if(recInfo.getType().equals("segment")){
			
			segmentDetails(recInfo.getElementName());
		}
		
		if(recInfo.getType().equals("translocation")){
			
			updateImgInfoForTranslocationId(recInfo.getElementName(), imgInfo);
		}
		if(recInfo.getType().equals("mutation_root")){
			FoConfigData cd = imgInfo.getQuery().getConfig();
			
			cp.loadMutationWindow(recInfo.getElementName(), recInfo.getTrackNumber(), cd);
		}
	}
	
	public void segmentDetails(String query){
		
		final SearchServiceAsync req = (SearchServiceAsync) GWT.create(SearchService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) req;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "SearchService";
		endpoint.setServiceEntryPoint(moduleRelativeURL);
		final AsyncCallback<FoSegment> callback = new AsyncCallback<FoSegment>(){
			public void onSuccess(FoSegment segmentData){
				
				cp.loadWindow(segmentData);
				
			}
			public void onFailure(Throwable caught){
				System.out.println(caught.getMessage());
				SC.say(caught.getMessage());
			}
		};
		req.getSegmentInfo(Integer.parseInt(query), callback);
	}
	
	public void geneDetails(String query, String ensemblDB){
		
		final SearchServiceAsync req = (SearchServiceAsync) GWT.create(SearchService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) req;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "SearchService";
		endpoint.setServiceEntryPoint(moduleRelativeURL);
		final AsyncCallback<EnsemblGene> callback = new AsyncCallback<EnsemblGene>(){
			public void onSuccess(EnsemblGene gene){
				
				cp.loadWindow(gene);
				
			}
			public void onFailure(Throwable caught){
				System.out.println(caught.getMessage());
				SC.say(caught.getMessage());
			}
		};
		req.getGeneInfo(query, ensemblDB, callback);
	}
	
	public void updateImgInfoForTranslocationId(String query, GWTImageInfo imgInfo){
		
		final SearchServiceAsync req = (SearchServiceAsync) GWT.create(SearchService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) req;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "SearchService";
		endpoint.setServiceEntryPoint(moduleRelativeURL);
		final AsyncCallback<QueryInfo> callback = new AsyncCallback<QueryInfo>(){
			public void onSuccess(QueryInfo query){
				
				cp.getMainPanel().getWestPanel().getSearchContent().search(query);
				
			}
			public void onFailure(Throwable caught){
				System.out.println(caught.getMessage());
				SC.say(caught.getMessage());
			}
		};
		req.updateImgInfoForTranslocationId(Integer.parseInt(query), imgInfo, callback);
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