package de.unihamburg.zbh.fishoracle.client;

import com.google.gwt.user.client.ui.Image;
import com.gwtext.client.core.Margins;
import com.gwtext.client.core.RegionPosition;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.TabPanel;
import com.gwtext.client.widgets.layout.BorderLayout;
import com.gwtext.client.widgets.layout.BorderLayoutData;
import com.gwtext.client.widgets.layout.FitLayout;

import de.unihamburg.zbh.fishoracle.client.data.ImageInfo;


public class MainPanel{

	private Panel mainPanel = null;
	private Panel borderPanel = null;
	private TabPanel cp = null;
	
	public MainPanel() {
	
	mainPanel = new Panel();
	
    mainPanel.setBorder(false);
    mainPanel.setPaddings(15);
    mainPanel.setLayout(new FitLayout());

    borderPanel = new Panel();
    borderPanel.setLayout(new BorderLayout());
	
    NorthPanel northPanel = new NorthPanel();
    CenterPanel centerPanel = new CenterPanel();
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
		
		Image image = new Image(imgInfo.getImgUrl());
		ImgPanel tab = addImgTab(imgInfo.getQuery());
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