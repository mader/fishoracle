package de.unihamburg.zbh.fishoracle.client;

import com.google.gwt.user.client.ui.Image;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.core.Margins;
import com.gwtext.client.core.RegionPosition;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.MessageBox;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.TabPanel;
import com.gwtext.client.widgets.Toolbar;
import com.gwtext.client.widgets.ToolbarButton;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.form.TextField;
import com.gwtext.client.widgets.layout.BorderLayout;
import com.gwtext.client.widgets.layout.BorderLayoutData;
import com.gwtext.client.widgets.layout.FitLayout;

import de.unihamburg.zbh.fishoracle.client.data.ImageInfo;


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
	
	public void newImageTab(ImageInfo imgInfo){
		
	Toolbar toolbar = new Toolbar();
        
        ToolbarButton left = new ToolbarButton("left", new ButtonListenerAdapter(){
			public void onClick(Button button, EventObject e) {
				
				ImgPanel imagePanel = (ImgPanel) cp.getActiveTab();
    			ImageInfo imgInfo = imagePanel.getImageInfo();
				
    			
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
        
        
        ToolbarButton right = new ToolbarButton("right", new ButtonListenerAdapter(){
			public void onClick(Button button, EventObject e) {
				
				ImgPanel imagePanel = (ImgPanel) cp.getActiveTab();
    			ImageInfo imgInfo = imagePanel.getImageInfo();
				
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
        
        ToolbarButton zoomin = new ToolbarButton("zoom in", new ButtonListenerAdapter(){
			public void onClick(Button button, EventObject e) {
				
				ImgPanel imagePanel = (ImgPanel) cp.getActiveTab();
    			ImageInfo imgInfo = imagePanel.getImageInfo();
				
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
        
        ToolbarButton zoomout = new ToolbarButton("zoom out", new ButtonListenerAdapter(){
			public void onClick(Button button, EventObject e) {
				
				ImgPanel imagePanel = (ImgPanel) cp.getActiveTab();
    			ImageInfo imgInfo = imagePanel.getImageInfo();
				
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
        
        TextField chr = new TextField(); 
        chr.setWidth("25px");
        chr.setValue(Integer.toString(imgInfo.getChromosome()));
        
        TextField start = new TextField();
        start.setWidth("75px");
        start.setValue(Integer.toString(imgInfo.getStart()));
        
        TextField end = new TextField();
        end.setWidth("75px");
        end.setValue(Integer.toString(imgInfo.getEnd()));
        
        toolbar.addButton(left);
        toolbar.addButton(right);
        
        toolbar.addSpacer();
        toolbar.addSpacer();
        toolbar.addSpacer();
        toolbar.addSpacer();
        toolbar.addSpacer();
        toolbar.addSpacer();
        toolbar.addSpacer();
        toolbar.addSpacer();
        toolbar.addSpacer();
        toolbar.addSpacer();
        
        toolbar.addButton(zoomin);
        toolbar.addButton(zoomout);
        
        toolbar.addSpacer();
        toolbar.addSpacer();
        toolbar.addSpacer();
        toolbar.addSpacer();
        toolbar.addSpacer();
        toolbar.addSpacer();
        toolbar.addSpacer();
        toolbar.addSpacer();
        toolbar.addSpacer();
        toolbar.addSpacer();
        
        toolbar.addText("Chromosome: ");
        toolbar.addField(chr);
        toolbar.addText("Start: ");
        toolbar.addField(start);
        toolbar.addText("End: ");
        toolbar.addField(end);
		
		Image image = new Image(imgInfo.getImgUrl());
		ImgPanel tab = addImgTab(imgInfo.getQuery());
		tab.add(toolbar);
		
		tab.setChrBox(chr);
		tab.setStartBox(start);
		tab.setEndBox(end);
		
		tab.add(image);
		tab.setImage(image);
		tab.setImageInfo(imgInfo);
		cp.add(tab);
		cp.activate(tab.getId());
		cp.scrollToTab(tab, true);
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