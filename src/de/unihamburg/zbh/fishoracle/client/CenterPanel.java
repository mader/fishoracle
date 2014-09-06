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
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.Cursor;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.SelectionType;
import com.smartgwt.client.types.Side;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Img;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.MouseOverEvent;
import com.smartgwt.client.widgets.events.MouseOverHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.LinkItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.KeyPressEvent;
import com.smartgwt.client.widgets.form.fields.events.KeyPressHandler;
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
import de.unihamburg.zbh.fishoracle.client.data.FoConstants;
import de.unihamburg.zbh.fishoracle.client.data.GWTImageInfo;
import de.unihamburg.zbh.fishoracle.client.data.FoUser;
import de.unihamburg.zbh.fishoracle.client.ImgCanvas;
import de.unihamburg.zbh.fishoracle.client.rpc.SearchService;
import de.unihamburg.zbh.fishoracle.client.rpc.SearchServiceAsync;
import de.unihamburg.zbh.fishoracle.client.rpc.UserService;
import de.unihamburg.zbh.fishoracle.client.rpc.UserServiceAsync;

public class CenterPanel extends VLayout {

	private ToolStripButton selectButton;
	
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
		
		WelcomeTab wt = new WelcomeTab();
		
		centerTabSet.addTab(wt);
		
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
        						&& !imgInfo.getRecmapinfo().get(rmc).getType().equals(FoConstants.GENE)
        						&& !imgInfo.getRecmapinfo().get(rmc).getType().equals(FoConstants.TRANSLOCATION)
        						&& !imgInfo.getRecmapinfo().get(rmc).getType().equals("mutation")){
        					//TODO replace mutation string with SNV constants.
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
        					if(!imgInfo.getRecmapinfo().get(rmc).getType().equals(FoConstants.TRANSLOCATION)  ){
        						spaceImg.setWidth(1);
            				} else {
            					spaceImg.setWidth(10);
            				}
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
		
		MenuItem excelExportItem = new MenuItem("Export image as excel document");
		MenuItem pdfExportItem = new MenuItem("Export image as pdf document");
		MenuItem psExportItem = new MenuItem("Export image as ps document");
		MenuItem svgExportItem = new MenuItem("Export image as svg document");
		MenuItem pngExportItem = new MenuItem("Export image as png document");
		
		excelExportItem.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler(){
			
			public void onClick(MenuItemClickEvent event) {
			GWTImageInfo imgInfo = ((ImgCanvas) cp.getCenterTabSet().getSelectedTab().getPane().getChildren()[1]).getImageInfo();
			cp.exportExcel(imgInfo);
			//SC.say("This function is currently not supported.");
			}
		});
		
		pdfExportItem.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler(){

			public void onClick(MenuItemClickEvent event) {
				GWTImageInfo imgInfo = ((ImgCanvas) cp.getCenterTabSet().getSelectedTab().getPane().getChildren()[1]).getImageInfo();
				
				GWTImageInfo newImgInfo = imgInfo.clone();
				
				newImgInfo.getQuery().setImageType("pdf");
				
				new ExportWindow(newImgInfo);
			}
		});
		
		psExportItem.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler(){

			public void onClick(MenuItemClickEvent event) {
				GWTImageInfo imgInfo = ((ImgCanvas) cp.getCenterTabSet().getSelectedTab().getPane().getChildren()[1]).getImageInfo();
				

				GWTImageInfo newImgInfo = imgInfo.clone();
				
				newImgInfo.getQuery().setImageType("ps");
				
				new ExportWindow(newImgInfo);
			}
		});
		
		svgExportItem.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler(){

			public void onClick(MenuItemClickEvent event) {
				GWTImageInfo imgInfo = ((ImgCanvas) cp.getCenterTabSet().getSelectedTab().getPane().getChildren()[1]).getImageInfo();
				
				GWTImageInfo newImgInfo = imgInfo.clone();
				
				newImgInfo.getQuery().setImageType("svg");
				
				new ExportWindow(newImgInfo);
			}
		});
		
		pngExportItem.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler(){

			public void onClick(MenuItemClickEvent event) {
				GWTImageInfo imgInfo = ((ImgCanvas) cp.getCenterTabSet().getSelectedTab().getPane().getChildren()[1]).getImageInfo();
				
				GWTImageInfo newImgInfo = imgInfo.clone();
				
				newImgInfo.getQuery().setImageType("png");
						
				new ExportWindow(newImgInfo);
			}
		});
		
		exportMenu.setItems(excelExportItem, pdfExportItem, psExportItem, svgExportItem, pngExportItem);
		
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
		
		Menu externMenu = new Menu();
		
		MenuItem EnsemblItem = new MenuItem("in Ensembl");
		MenuItem UCSCItem = new MenuItem("in UCSC");
		
		EnsemblItem.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler(){

			public void onClick(MenuItemClickEvent event) {
				String chr = imgInfo.getChromosome();
				String start = String.valueOf(imgInfo.getStart());
				String end = String.valueOf(imgInfo.getEnd());
				
				String url = "http://www.ensembl.org/Homo_sapiens/Location/View?r=" + chr + ":" + start + "-" + end;
				
				com.google.gwt.user.client.Window.open(url,"_blank","");
			}
		});
		
		UCSCItem.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler(){

			public void onClick(MenuItemClickEvent event) {
				String chr = imgInfo.getChromosome();
				String start = String.valueOf(imgInfo.getStart());
				String end = String.valueOf(imgInfo.getEnd());
				
				String url = "http://genome-euro.ucsc.edu/cgi-bin/hgTracks?db=hg19&position=chr" + chr + "%3A" + start + "-" + end;
				
				com.google.gwt.user.client.Window.open(url,"_blank","");
			}
		});
		
		externMenu.setItems(EnsemblItem, UCSCItem);
		
		ToolStripMenuButton externMenuButton = new ToolStripMenuButton("View", externMenu);
		
		presentationToolStrip.addButton(selectButton);
		
		presentationToolStrip.addMenuButton(externMenuButton);
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
	
	/** 
	 * Data administration Tabs.
	 */
	public void openDataImportTab(){
		
		ImportTab it = new ImportTab();
		centerTabSet.addTab(it);
		centerTabSet.selectTab(it);
	}
	
	public void openProjectConfigTab(FoUser user){
		
		ProjectConfigTab pct = new ProjectConfigTab(user);
		centerTabSet.addTab(pct);
		centerTabSet.selectTab(pct);
	}
	
	public void openStudyConfigTab(FoUser user){
		
		StudyConfigTab sct = new StudyConfigTab(user, cp);
		centerTabSet.addTab(sct);
		centerTabSet.selectTab(sct);
	}
	
	/** 
	 * Application administration Tabs.
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
	
	public void exportExcel(GWTImageInfo imgInfo){
				
				final SearchServiceAsync req = (SearchServiceAsync) GWT.create(SearchService.class);
				ServiceDefTarget endpoint = (ServiceDefTarget) req;
				String moduleRelativeURL = GWT.getModuleBaseURL() + "SearchService";
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
						SC.say(caught.getMessage());
					}
				};
				req.exportData(imgInfo, callback);
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
					openStudyConfigTab(result[0]);
				}
			}
			public void onFailure(Throwable caught){
				SC.say(caught.getMessage());
			}
		};
		req.getSessionUserObject(callback);
	}
}