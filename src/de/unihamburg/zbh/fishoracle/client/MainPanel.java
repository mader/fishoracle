package de.unihamburg.zbh.fishoracle.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.core.Margins;
import com.gwtext.client.core.RegionPosition;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.Component;
import com.gwtext.client.widgets.MessageBox;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.TabPanel;
import com.gwtext.client.widgets.Toolbar;
import com.gwtext.client.widgets.ToolbarButton;
import com.gwtext.client.widgets.Window;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.event.ComponentListenerAdapter;
import com.gwtext.client.widgets.event.KeyListener;
//import com.gwtext.client.widgets.form.Label;
import com.gwtext.client.widgets.form.TextField;
import com.gwtext.client.widgets.layout.BorderLayout;
import com.gwtext.client.widgets.layout.BorderLayoutData;
import com.gwtext.client.widgets.layout.FitLayout;

import de.unihamburg.zbh.fishoracle.client.data.GWTImageInfo;


public class MainPanel{

	private Panel mainPanel = null;
	private Panel borderPanel = null;
	private TabPanel cp = null;
	private CenterPanel cpContainer = null;
	
	
	public MainPanel() {
	
	mainPanel = new Panel();
	
    mainPanel.setBorder(false);
    mainPanel.setPaddings(15);
    mainPanel.setLayout(new FitLayout());

    borderPanel = new Panel();
    borderPanel.setLayout(new BorderLayout());
	
    NorthPanel northPanel = new NorthPanel();
    CenterPanel centerPanel = new CenterPanel();
    cpContainer = centerPanel;
    cp = centerPanel.getCenterPanel();
    WestPanel westPanel = new WestPanel(this, cp);
    
    
    BorderLayoutData westData = new BorderLayoutData(RegionPosition.WEST);
    westData.setSplit(true);
    westData.setMinSize(175);
    westData.setMaxSize(400);
    westData.setMargins(new Margins(0, 5, 0, 0));
    
    borderPanel.add(northPanel.getNorthPanel(), new BorderLayoutData(RegionPosition.NORTH));
    borderPanel.add(westPanel.getWestPanel(), westData);
    borderPanel.add(centerPanel.getCenterPanel(), new BorderLayoutData(RegionPosition.CENTER));
    
    mainPanel.add(borderPanel);

	}

	public Panel getMainPanel() {
		return mainPanel;
	}

	public void setMainPanel(Panel mainPanel) {
		this.mainPanel = mainPanel;
	}
	
	
	public void jumpto(){
		
		ImgPanel imagePanel = (ImgPanel) cp.getActiveTab();
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
		
	    	cpContainer.imageRedraw(imgInfo);
	    }

	}
	
	public void newImageTab(GWTImageInfo imgInfo){
		
	KeyListener rangeListener = new KeyListener(){
			@Override
			public void onKey(int key, EventObject e) {	
				jumpto();
			}
		};	
		
		
	Toolbar toolbar = new Toolbar();
        
        ToolbarButton left = new ToolbarButton(null, new ButtonListenerAdapter(){
			public void onClick(Button button, EventObject e) {
				
				ImgPanel imagePanel = (ImgPanel) cp.getActiveTab();
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
		
					cpContainer.imageRedraw(imgInfo);
	    	
				} else {
					MessageBox.alert("You have reached the chromsomes end ...");
				}
        	}
       });
        
        left.setIcon("icons/arrow_left.png");
        
        ToolbarButton right = new ToolbarButton(null, new ButtonListenerAdapter(){
			public void onClick(Button button, EventObject e) {
				
				ImgPanel imagePanel = (ImgPanel) cp.getActiveTab();
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
    		    
				cpContainer.imageRedraw(imgInfo);	
        	}
       });
        
        right.setIcon("icons/arrow_right.png");
        
        ToolbarButton zoomin = new ToolbarButton(null, new ButtonListenerAdapter(){
			public void onClick(Button button, EventObject e) {
				
				ImgPanel imagePanel = (ImgPanel) cp.getActiveTab();
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
    			
    		    	cpContainer.imageRedraw(imgInfo);
    		    	
    		    } else {
    		    	MessageBox.alert("You have reached the highest zoom level ...");
    		    }
        	}
       });
        
        zoomin.setIcon("icons/arrow_in.png");
        
        ToolbarButton zoomout = new ToolbarButton(null, new ButtonListenerAdapter(){
			public void onClick(Button button, EventObject e) {
				
				ImgPanel imagePanel = (ImgPanel) cp.getActiveTab();
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
    		    
    		    imgInfo.setStart(newStart);
    		    
    		    imgInfo.setEnd(newEnd);
    			
				cpContainer.imageRedraw(imgInfo);	
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
        
        AbsolutePanel absPanel = new AbsolutePanel();
        absPanel.setPixelSize(imgInfo.getWidth(), imgInfo.getHeight());
        
		Image image = new Image(imgInfo.getImgUrl());
		ImgPanel tab = addImgTab(imgInfo.getQuery());
		tab.add(toolbar);
		
		tab.setChrBox(chr);
		tab.setStartBox(start);
		tab.setEndBox(end);
		
		absPanel.add(image);
		
		for(int i=0; i < imgInfo.getRecmapinfo().size(); i++){
			
			
			final Image spaceImg = new Image();
			
			spaceImg.addClickHandler(new ClickHandler(){
				@Override
				public void onClick(ClickEvent event) {
					loadWindow(spaceImg);
					
				}
			});
			
			int southeast_x = (int) imgInfo.getRecmapinfo().get(i).getSoutheastX();
			
			int northwest_x = (int) imgInfo.getRecmapinfo().get(i).getNorthwestX();
			
			spaceImg.setUrl("images/1pximg.gif");
			
			spaceImg.setWidth(Integer.toString((southeast_x - northwest_x)));
			
			spaceImg.setHeight(Integer.toString((int) (imgInfo.getRecmapinfo().get(i).getSoutheastY() - imgInfo.getRecmapinfo().get(i).getNorthwestY())));

			absPanel.add(spaceImg, (int) imgInfo.getRecmapinfo().get(i).getNorthwestX(), (int) imgInfo.getRecmapinfo().get(i).getNorthwestY());
			
		}
		
		tab.add(absPanel);
		
		
		tab.setImage(image);
		tab.setImageInfo(imgInfo);
		cp.add(tab);
		cp.activate(tab.getId());
		cp.scrollToTab(tab, true);
	}
	
	private void loadWindow(Image spaceImg){
		
		Window window = new Window();
		window.setTitle("Layout Window");  
		window.setClosable(true);  
		window.setWidth(600);  
		window.setHeight(350);  
		window.setPlain(true);  
		//window.setLayout(new BorderLayout());  
		window.setCloseAction(Window.HIDE);
		window.show(spaceImg.getElement().getId());
	}
	
	private Panel addTab(String name) {
        Panel tab = new Panel();
        tab.setAutoScroll(true);
        tab.setTitle(name);
        tab.setClosable(true);
        return tab;
    }
	
	private ImgPanel addImgTab(String name) {
        ImgPanel tab = new ImgPanel();
        tab.setAutoScroll(true);
        tab.setTitle(name);
        tab.setClosable(true);
        return tab;
    }
	
}