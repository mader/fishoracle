package de.unihamburg.zbh.fishoracle.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Image;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.core.Margins;
import com.gwtext.client.core.NameValuePair;
import com.gwtext.client.core.RegionPosition;
import com.gwtext.client.data.ArrayReader;
import com.gwtext.client.data.FieldDef;
import com.gwtext.client.data.FloatFieldDef;
import com.gwtext.client.data.IntegerFieldDef;
import com.gwtext.client.data.MemoryProxy;
import com.gwtext.client.data.RecordDef;
import com.gwtext.client.data.Store;
import com.gwtext.client.data.StringFieldDef;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.MessageBox;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.Toolbar;
import com.gwtext.client.widgets.ToolbarButton;
import com.gwtext.client.widgets.Window;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.event.KeyListener;
import com.gwtext.client.widgets.form.TextField;
import com.gwtext.client.widgets.grid.BaseColumnConfig;
import com.gwtext.client.widgets.grid.ColumnConfig;
import com.gwtext.client.widgets.grid.ColumnModel;
import com.gwtext.client.widgets.grid.GridPanel;
import com.gwtext.client.widgets.grid.GridView;
import com.gwtext.client.widgets.grid.PropertyGridPanel;
import com.gwtext.client.widgets.grid.RowNumberingColumnConfig;
import com.gwtext.client.widgets.layout.BorderLayout;
import com.gwtext.client.widgets.layout.BorderLayoutData;
import com.gwtext.client.widgets.layout.FitLayout;
import com.gwtext.client.widgets.menu.BaseItem;
import com.gwtext.client.widgets.menu.Item;
import com.gwtext.client.widgets.menu.Menu;
import com.gwtext.client.widgets.menu.event.BaseItemListenerAdapter;

import de.unihamburg.zbh.fishoracle.client.data.CopyNumberChange;
import de.unihamburg.zbh.fishoracle.client.data.GWTImageInfo;
import de.unihamburg.zbh.fishoracle.client.data.Gen;
import de.unihamburg.zbh.fishoracle.client.data.RecMapInfo;
import de.unihamburg.zbh.fishoracle.client.rpc.Search;
import de.unihamburg.zbh.fishoracle.client.rpc.SearchAsync;

public class MainPanel extends Panel{

	private Panel borderPanel = null;
	private CenterPanel centerPanel = null;

	public MainPanel() {
	
    this.setBorder(false);
    this.setPaddings(15);
    this.setLayout(new FitLayout());

    borderPanel = new Panel();
    borderPanel.setLayout(new BorderLayout());
	
    NorthPanel northPanel = new NorthPanel();
    centerPanel = new CenterPanel(this);

    WestPanel westPanel = new WestPanel(this, centerPanel);
    
    
    BorderLayoutData westData = new BorderLayoutData(RegionPosition.WEST);
    westData.setSplit(true);
    westData.setMinSize(175);
    westData.setMaxSize(400);
    westData.setMargins(new Margins(0, 5, 0, 0));
    
    
    Panel southPanel = new Panel();
    southPanel.setHeight(0);
    
    Panel eastPanel = new Panel();
    eastPanel.setWidth(0);
    BorderLayoutData eastData = new BorderLayoutData(RegionPosition.EAST);    
    eastData.setMargins(new Margins(0, 0, 5, 0));  
	

    borderPanel.add(eastPanel, eastData);  
    borderPanel.add(southPanel, new BorderLayoutData(RegionPosition.SOUTH));
    borderPanel.add(northPanel, new BorderLayoutData(RegionPosition.NORTH));
    borderPanel.add(westPanel, westData);
    borderPanel.add(centerPanel, new BorderLayoutData(RegionPosition.CENTER));
    
    this.add(borderPanel);
    
	}	
	
	public AbsolutePanel createImageLayer(GWTImageInfo imgInfo){
		
		AbsolutePanel absPanel = new AbsolutePanel();
        absPanel.setPixelSize(imgInfo.getWidth(), imgInfo.getHeight());
		
		Image image = new Image(imgInfo.getImgUrl());
        
		absPanel.add(image);
		
		int rmc;
		
		for(rmc=0; rmc < imgInfo.getRecmapinfo().size(); rmc++){
			
			final Image spaceImg = new Image();
			
			spaceImg.addClickHandler(new MyClickHandler(imgInfo.getRecmapinfo().get(rmc), this));
			
			int southeast_x = (int) imgInfo.getRecmapinfo().get(rmc).getSoutheastX();
			
			int northwest_x = (int) imgInfo.getRecmapinfo().get(rmc).getNorthwestX();
			
			spaceImg.setUrl("images/1pximg.gif");
			
			spaceImg.setWidth(Integer.toString((southeast_x - northwest_x)));
			
			spaceImg.setHeight(Integer.toString((int) (imgInfo.getRecmapinfo().get(rmc).getSoutheastY() - imgInfo.getRecmapinfo().get(rmc).getNorthwestY())));

			spaceImg.addStyleName("image_hand");
			
			absPanel.add(spaceImg, (int) imgInfo.getRecmapinfo().get(rmc).getNorthwestX(), (int) imgInfo.getRecmapinfo().get(rmc).getNorthwestY());
			
		}
		
		return absPanel;
	}
	
	public void jumpto(){
		
		ImgPanel imagePanel = (ImgPanel) centerPanel.getActiveTab();
		GWTImageInfo imgInfo = imagePanel.getImageInfo();
		
		String newChr;
	    int newStart;
	    int newEnd;
		
		newChr = imagePanel.getChrBox().getValueAsString();
	    
	    newStart = Integer.parseInt(imagePanel.getStartBox().getValueAsString());
	    
	    newEnd = Integer.parseInt(imagePanel.getEndBox().getValueAsString());
	    
	    if(newStart >= newEnd || newEnd - newStart <= 10){
	    	
	    	MessageBox.alert("The end value must at least be 10 base pairs greater than the start value!");
	    	
	    } else {
	    
	    	imgInfo.setChromosome(newChr);
	    	
	    	imgInfo.setStart(newStart);
	    
	    	imgInfo.setEnd(newEnd);

	    	centerPanel.imageRedraw(imgInfo);
	    }

	}
	
	public void newImageTab(final GWTImageInfo imgInfo){
		
	KeyListener rangeListener = new KeyListener(){
			@Override
			public void onKey(int key, EventObject e) {	
				jumpto();
			}
		};	
		
		
	Toolbar toolbar = new Toolbar();
        
        ToolbarButton left = new ToolbarButton(null, new ButtonListenerAdapter(){
			public void onClick(Button button, EventObject e) {
				
				ImgPanel imagePanel = (ImgPanel) centerPanel.getActiveTab();
				GWTImageInfo imgInfo = imagePanel.getImageInfo();
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
		
					centerPanel.imageRedraw(imgInfo);
	    	
				} else {
					MessageBox.alert("You have reached the chromsomes end ...");
				}
        	}
       });
        
        left.setIcon("icons/arrow_left.png");
        
        ToolbarButton right = new ToolbarButton(null, new ButtonListenerAdapter(){
			public void onClick(Button button, EventObject e) {
				
				ImgPanel imagePanel = (ImgPanel) centerPanel.getActiveTab();
    			GWTImageInfo imgInfo = imagePanel.getImageInfo();
				
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
    		    
    		    centerPanel.imageRedraw(imgInfo);	
        	}
       });
        
        right.setIcon("icons/arrow_right.png");
        
        ToolbarButton zoomin = new ToolbarButton(null, new ButtonListenerAdapter(){
			public void onClick(Button button, EventObject e) {
				
				ImgPanel imagePanel = (ImgPanel) centerPanel.getActiveTab();
    			GWTImageInfo imgInfo = imagePanel.getImageInfo();
				
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
    			
    		    	centerPanel.imageRedraw(imgInfo);
    		    	
    		    } else {
    		    	MessageBox.alert("You have reached the highest zoom level ...");
    		    }
        	}
       });
        
        zoomin.setIcon("icons/arrow_in.png");
        
        ToolbarButton zoomout = new ToolbarButton(null, new ButtonListenerAdapter(){
			public void onClick(Button button, EventObject e) {
				
				ImgPanel imagePanel = (ImgPanel) centerPanel.getActiveTab();
    			GWTImageInfo imgInfo = imagePanel.getImageInfo();
				
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
    			
    		    centerPanel.imageRedraw(imgInfo);	
        	}
       });
        
        zoomout.setIcon("icons/arrow_out.png");
        
        ToolbarButton jumpTo = new ToolbarButton(null, new ButtonListenerAdapter(){
			public void onClick(Button button, EventObject e) {
				
				jumpto();
        	}
       });
        
        jumpTo.setIcon("icons/arrow_refresh.png");
        
        TextField chr = new TextField(); 
        chr.setWidth("25px");
        chr.addKeyListener(KeyCodes.KEY_ENTER, rangeListener);
        chr.setValue(imgInfo.getChromosome());
        
        TextField start = new TextField();
        start.setWidth("75px");
        start.addKeyListener(KeyCodes.KEY_ENTER, rangeListener);
        start.setValue(Integer.toString(imgInfo.getStart()));
        
        TextField end = new TextField();
        end.setWidth("75px");
        end.addKeyListener(KeyCodes.KEY_ENTER, rangeListener);
        end.setValue(Integer.toString(imgInfo.getEnd()));
        
        toolbar.addButton(left);
        toolbar.addText("scroll");
        toolbar.addButton(right);
        
        toolbar.addSeparator();
        
        toolbar.addButton(zoomin);
        toolbar.addText("zoom");
        toolbar.addButton(zoomout);
        
        toolbar.addSeparator();
        
        toolbar.addText("Chromosome: ");
        toolbar.addField(chr);
        toolbar.addText("Start: ");
        toolbar.addField(start);
        toolbar.addText("End: ");
        toolbar.addField(end);
		
        toolbar.addButton(jumpTo);
		
		ImgPanel tab = addImgTab(imgInfo.getQuery().getQueryString());
		tab.add(toolbar);
		
		tab.setChrBox(chr);
		tab.setStartBox(start);
		tab.setEndBox(end);
		
		toolbar.addSeparator();
		
		ToolbarButton exportButton = new ToolbarButton("export");
		
		Menu menu = new Menu();
		
		Item excelItem = new Item("export image as Excel document");
		excelItem.addListener(new BaseItemListenerAdapter() {
            public void onClick(BaseItem item, EventObject e) {
            	ImgPanel imagePanel = (ImgPanel) centerPanel.getActiveTab();
    			GWTImageInfo imgInfo = imagePanel.getImageInfo();
				
				centerPanel.exportExcel(imgInfo);
            }
        });

		
		
		menu.addItem(excelItem); 
		
		exportButton.setMenu(menu);
		
		
		
		toolbar.addButton(exportButton);
		
		AbsolutePanel absPanel = createImageLayer(imgInfo);
		
		tab.add(absPanel);
		
		tab.setImageLayer(absPanel);
		
		tab.setImageInfo(imgInfo);
		centerPanel.add(tab);
		centerPanel.activate(tab.getId());
		centerPanel.scrollToTab(tab, true);
	}
	
	private ImgPanel addImgTab(String name) {
        ImgPanel tab = new ImgPanel();
        tab.setAutoScroll(true);
        tab.setTitle(name);
        tab.setClosable(true);
        return tab;
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
		 
		window.setClosable(true);  
		window.setPlain(true);    
		
		Panel panel = new Panel();  
		panel.setBorder(false);
		panel.setPaddings(15);  

		PropertyGridPanel grid = new PropertyGridPanel();   
		grid.setWidth(300);  
		grid.setAutoHeight(true);  
		grid.setSorted(false);   
		
		GridView view = new GridView();  
		view.setForceFit(true);  
		view.setScrollOffset(2); // the grid will never have scrollbars  
		grid.setView(view);  
		   
		NameValuePair[] source = new NameValuePair[8];  
		source[0] = new NameValuePair(idStr, cncData.getCncStableId());  
		source[1] = new NameValuePair("Chromosome", cncData.getChromosome());  
		source[2] = new NameValuePair("Start", cncData.getStart());  
		source[3] = new NameValuePair("End", cncData.getEnd());  
		source[4] = new NameValuePair("Case Name", cncData.getCaseName());  
		source[5] = new NameValuePair("Tumor Type", cncData.getTumorType());  
		source[6] = new NameValuePair("Continuous", cncData.getContinuous());  
		source[7] = new NameValuePair(levelStr, cncData.getCnclevel());   
		
		grid.setSource(source);
		
		panel.add(grid);
		
		window.add(panel);
		
		window.setCloseAction(Window.HIDE);
		//window.show(spaceImg.getElement().getId());
		
		window.show();
	}
	
	public void loadWindow(Gen gene){
		
		Window window = new Window();

		window.setTitle("Gene " + gene.getGenName());
		 
		window.setClosable(true);  
		window.setPlain(true);  
		
		Panel panel = new Panel();  
		panel.setBorder(false);
		panel.setPaddings(15);  

		PropertyGridPanel grid = new PropertyGridPanel();    
		grid.setWidth(500);
		grid.setAutoHeight(true);  
		grid.setSorted(false);   
		
		GridView view = new GridView();  
		view.setForceFit(true);  
		view.setScrollOffset(2); // the grid will never have scrollbars  
		grid.setView(view);  
		   
		NameValuePair[] source = new NameValuePair[8];  
		source[0] = new NameValuePair("Ensembl Stable ID", gene.getAccessionID()); 
		source[1] = new NameValuePair("Name", gene.getGenName());
		source[2] = new NameValuePair("Chromosome", gene.getChr());  
		source[3] = new NameValuePair("Start", gene.getStart());  
		source[4] = new NameValuePair("End", gene.getEnd());  
		source[5] = new NameValuePair("Length", gene.getLength());
		source[6] = new NameValuePair("Strand", gene.getStrand());  
		source[7] = new NameValuePair("Bio Type" ,gene.getBioType()); 
		
		Panel desc = new Panel();
		
		desc.setHtml("<center>" + gene.getDescription() + "</center>");
		desc.setPaddings(5);
		desc.setWidth(500);		
		
		grid.setSource(source);
		
		panel.add(grid);
		
		panel.add(desc);
		window.add(panel);
		
		window.setCloseAction(Window.HIDE);
		//window.show(spaceImg.getElement().getId());
		
		window.show();
	}

	public void newDataTab(CopyNumberChange[] cncs, boolean isAmplicon) {
		
		String type = null;
		
		if(isAmplicon){
			type = "amplicons";
		} else {
			type = "delicons";
		}
		
		Panel tab = new Panel();
        tab.setAutoScroll(true);
        tab.setTitle("List of all " + type);
        tab.setClosable(true);
        
        RecordDef recordDef = new RecordDef(  
        		new FieldDef[]{  
        				new StringFieldDef("stable id"),  
        				new StringFieldDef("chromosome"),  
        				new IntegerFieldDef("start"),  
        				new IntegerFieldDef("end"),  
        				new StringFieldDef("case name"),  
        				new StringFieldDef("tumor type"),  
        				new IntegerFieldDef("continuous"),
        				new IntegerFieldDef("level") 
        		}  
        );  
        
        Object[][] data = new Object[cncs.length][];
        
        for(int i=0; i<cncs.length; i++){
        	data[i] = new Object[]{cncs[i].getCncStableId(), cncs[i].getChromosome(),
    				cncs[i].getStart(), cncs[i].getEnd(),
    				cncs[i].getCaseName(), cncs[i].getTumorType(),
    				cncs[i].getContinuous(), cncs[i].getCnclevel()};
        }                                    
                                                  
        MemoryProxy proxy = new MemoryProxy(data);  

        ArrayReader reader = new ArrayReader(recordDef);  
        Store store = new Store(proxy, reader);  
        store.load();
        
        BaseColumnConfig[] columns = new BaseColumnConfig[]{  
        		new RowNumberingColumnConfig(), 
        		new ColumnConfig("stable id", "stable id", 50, true),  
        		new ColumnConfig("chromosome", "chromosome", 50, true),  
        		new ColumnConfig("start", "start", 65, true),  
        		new ColumnConfig("end", "end", 65, true),  
        		new ColumnConfig("case name", "case name", 80, true),  
        		new ColumnConfig("tumor type", "tumor type", 40, true),
        		new ColumnConfig("continuous", "continuous", 40, true),
        		new ColumnConfig("level", "level", 40, true)
        };  

        ColumnModel columnModel = new ColumnModel(columns); 
        
        GridPanel grid = new GridPanel();  
        grid.setStore(store);  
        grid.setColumnModel(columnModel);  

        //grid.setTitle("Grid with Numbered Rows and Force Fit");  
        //grid.setHeight(300);  
        //grid.setWidth(600);  
        //grid.setIconCls("grid-icon");  

        GridView view = new GridView();  
        view.setForceFit(true);  
        grid.setView(view);
        
        tab.add(grid);
        
        centerPanel.add(tab);
        centerPanel.activate(tab.getId());
	}
}

class MyClickHandler implements ClickHandler{
	
	RecMapInfo recInfo;
	MainPanel main;
	
	public MyClickHandler(RecMapInfo recmapinfo, MainPanel m){
		this.recInfo = recmapinfo;
		this.main = m;
	}
	
	@Override
	public void onClick(ClickEvent event) {
		
		if(recInfo.getType().equals("amplicon") || recInfo.getType().equals("delicon")){
			
			cncDetails(recInfo.getElementName());
			
		}
		if(recInfo.getType().equals("gene")){

			geneDetails(recInfo.getElementName());
			
		}
	}
	
	/*=============================================================================
	 *||                              RPC Calls                                  ||
	 *=============================================================================
	 * */	
		 
		
	public void cncDetails(String query){
			
		final SearchAsync req = (SearchAsync) GWT.create(Search.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) req;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "Search";
		endpoint.setServiceEntryPoint(moduleRelativeURL);
		final AsyncCallback<CopyNumberChange> callback = new AsyncCallback<CopyNumberChange>(){
			public void onSuccess(CopyNumberChange cncData){
				
				main.loadWindow(cncData);
				
			}
			public void onFailure(Throwable caught){
				System.out.println(caught.getMessage());
				MessageBox.alert(caught.getMessage());
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
				
				main.loadWindow(gene);
				
			}
			public void onFailure(Throwable caught){
				System.out.println(caught.getMessage());
				MessageBox.alert(caught.getMessage());
			}
		};
		req.getGeneInfo(query, callback);
	}
	
}