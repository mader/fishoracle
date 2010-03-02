package de.unihamburg.zbh.fishoracle.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.HTML;

import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.Cursor;
import com.smartgwt.client.types.ImageStyle;
import com.smartgwt.client.types.Side;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Img;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;

import com.smartgwt.client.widgets.events.ResizedEvent;
import com.smartgwt.client.widgets.events.ResizedHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.LinkItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.KeyPressEvent;
import com.smartgwt.client.widgets.form.fields.events.KeyPressHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.menu.MenuItem;
import com.smartgwt.client.widgets.menu.events.MenuItemClickEvent;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;
import com.smartgwt.client.widgets.toolbar.ToolStripMenuButton;

import de.unihamburg.zbh.fishoracle.client.data.CopyNumberChange;
import de.unihamburg.zbh.fishoracle.client.data.GWTImageInfo;
import de.unihamburg.zbh.fishoracle.client.data.Gen;
import de.unihamburg.zbh.fishoracle.client.data.RecMapInfo;
import de.unihamburg.zbh.fishoracle.client.data.User;
import de.unihamburg.zbh.fishoracle.client.ImgCanvas;
import de.unihamburg.zbh.fishoracle.client.rpc.Admin;
import de.unihamburg.zbh.fishoracle.client.rpc.AdminAsync;
import de.unihamburg.zbh.fishoracle.client.rpc.Search;
import de.unihamburg.zbh.fishoracle.client.rpc.SearchAsync;
import de.unihamburg.zbh.fishoracle.client.rpc.UserService;
import de.unihamburg.zbh.fishoracle.client.rpc.UserServiceAsync;

public class CenterPanel extends VLayout{

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
		HTML welcomeTextHTML = new HTML("" +
        		"<br><center><h1>FISH Oracle</h1> <i>alpha</i></center><br>" +
        		"You can search for amplicons by giving an amplicon or delicon stable ID" +
        		" e.g. 'AMP60.01' or 'DEL60.01' or for a gene specified by a gene name e.g. 'kras'" +
        		" or a karyoband giving the exact karyoband identifier e.g. '8q21.3'." +
        		" By clicking on an element a window opens that shows additional information." +
        		" As the amplicon data is incompatible to the Ensembl version 55 the currently" +
        		" used Ensembl version is 54. If you want to search for a gene in the Ensembl " +
        		" browser you better also use version 54 " +
        		"<a href=\"http://may2009.archive.ensembl.org\" target=_blank>http://may09.archive.ensembl.org</a>" +
        		"<br><br>" +
        		"FISH Oracle uses:<br><br> " +
        		"<li> the Google Web Toolkit <a href=\"http://code.google.com/webtoolkit/\" target=_blank>http://code.google.com/webtoolkit/</a></li>" +
        		"<li> the Ensembl human core database <a href=\"http://www.ensembl.org\" target=_blank>http://www.ensembl.org</a></li>" +
        		"<li> AnnotationSketch of the GenomeTools <a href=\"http://www.genometools.org\" target=_blank>http://www.genometools.org</a></li>" +
        		"</ul>");
		
		welcomeLayout.addMember(welcomeTextHTML);
		
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
		
		ImgCanvas image = new ImgCanvas(imgInfo);
        image.setImageType(ImageStyle.NORMAL);
        image.setHeight(imgInfo.getHeight());
        image.setAppImgDir("/");
		
		int rmc;
		
		for(rmc=0; rmc < imgInfo.getRecmapinfo().size(); rmc++){
			
			final Img spaceImg = new Img("images/1pximg.gif");
			spaceImg.setAppImgDir("/");
			
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
		
		MenuItem exportItem = new MenuItem("Export image as excel document");
		
		exportItem.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler(){

			public void onClick(MenuItemClickEvent event) {
				GWTImageInfo imgInfo = ((ImgCanvas) cp.getCenterTabSet().getSelectedTab().getPane().getChildren()[1]).getImageInfo();
				cp.exportExcel(imgInfo);
			}
		});
		
		exportMenu.setItems(exportItem);
		
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

		String titleStr = null;
		String idStr = null;
		String levelStr = null;
		
		if (cncData.isAmplicon()){
			titleStr = "Amplicon";
			idStr = "Amplicon Stable ID";
			levelStr = "Amplevel";
		} else {
			titleStr = "Delicon";
			idStr = "Delicon Stable ID";
			levelStr = "Dellevel";
		}
		
		window.setTitle(titleStr + " " + cncData.getCncStableId());
		window.setAutoSize(true);
		window.setAutoCenter(true);
		
		final ListGrid cncGrid = new ListGrid();
		cncGrid.setWidth(450);
		cncGrid.setHeight(200);  
		cncGrid.setShowAllRecords(true);  
		cncGrid.setAlternateRecordStyles(true);
		cncGrid.setShowHeader(false);
		
		ListGridField key = new ListGridField("key", "key");
		ListGridField val = new ListGridField("val", "val");
		
		cncGrid.setFields(key, val);
		
		ListGridRecord[] lgr = new ListGridRecord[8];
		
		lgr[0] = new ListGridRecord();
		lgr[0].setAttribute("key", idStr);
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
		lgr[4].setAttribute("key", "Case Name");
		lgr[4].setAttribute("val", cncData.getCaseName());
		
		lgr[5] = new ListGridRecord();
		lgr[5].setAttribute("key", "Tumor Type");
		lgr[5].setAttribute("val", cncData.getTumorType());
		
		lgr[6] = new ListGridRecord();
		lgr[6].setAttribute("key", "Continuous");
		lgr[6].setAttribute("val", cncData.getContinuous());
		
		lgr[7] = new ListGridRecord();
		lgr[7].setAttribute("key", levelStr);
		lgr[7].setAttribute("val", cncData.getCnclevel());
		
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

	public void registerUser(User user){
		
		final UserServiceAsync req = (UserServiceAsync) GWT.create(UserService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) req;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "UserService";
		endpoint.setServiceEntryPoint(moduleRelativeURL);
		final AsyncCallback<User> callback = new AsyncCallback<User>(){
			public void onSuccess(User result){
				
				
				/*
				Component[] items = cp.getItems();
				for (int i = 0; i < items.length; i++) {  
					Component component = items[i];  
					if (component.getTitle().equals("register")) {  
						cp.remove(component);  
					}
				}
			
				String msg = "Registered! Before you can login with your user name " + result.getUserName() + " your account has to be verified." +
							" We will try to do that as fast as possible.";
				
				System.out.println(msg);
				
				MessageBox.alert(msg);
				*/
			}
			public void onFailure(Throwable caught){
				System.out.println(caught.getMessage());
				SC.say(caught.getMessage());
			}
		};
		req.register(user, callback);
	}
	
	public void toggleIsActiveOrIsAdmin(int id, String flag, int rowIndex, int colIndex){
	
		final AdminAsync req = (AdminAsync) GWT.create(Admin.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) req;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "AdminService";
		endpoint.setServiceEntryPoint(moduleRelativeURL);
		final AsyncCallback<int[]> callback = new AsyncCallback<int[]>(){
			@Override
			public void onSuccess(int[] result){
				/*
				Record record = grid.getStore().getRecordAt(result[0]);
				
				if(result[1] == 5){
				
					record.set("isactive", (result[2] == 1));
				
				}
				
				if(result[1] == 6){
					
					record.set("isadmin", (result[2] == 1));
					
				}*/
			}
			public void onFailure(Throwable caught){
				System.out.println(caught.getMessage());
				//MessageBox.alert(caught.getMessage());
			}
		};
		req.toggleFlag(id, flag, rowIndex, colIndex, callback);
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
		
		if(recInfo.getType().equals("amplicon") || recInfo.getType().equals("delicon")){
			
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

/*
class MyGridCellListener implements GridCellListener{

	private CenterPanel cp = null;
	
	public MyGridCellListener(CenterPanel centerPanel){
		
		cp = centerPanel;
		
	}
	
	@Override
	public void onCellClick(GridPanel grid, int rowIndex, int colIndex,
			EventObject e) {
		
		Record record =  grid.getStore().getAt(rowIndex);
		
		if(colIndex == 5){
			
			int id = record.getAsInteger("id");
		
			String isActive = record.getAsString("isactive");
		
			cp.toggleIsActiveOrIsAdmin(id, isActive, rowIndex, colIndex);
		
		}
		
		if(colIndex == 6){
			
			int id = record.getAsInteger("id");
			
			String isAdmin = record.getAsString("isadmin");
			
			cp.toggleIsActiveOrIsAdmin(id, isAdmin, rowIndex, colIndex);
			
		}
		
	}

	@Override
	public void onCellContextMenu(GridPanel grid, int rowIndex, int cellIndex,
			EventObject e) {
		
	}

	@Override
	public void onCellDblClick(GridPanel grid, int rowIndex, int colIndex,
			EventObject e) {
		
	}	
	
}

}*/