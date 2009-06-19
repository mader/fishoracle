package de.unihamburg.zbh.fishoracle.client;

import com.gwtext.client.widgets.HTMLPanel;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.TabPanel;

public class CenterPanel{

	TabPanel centerPanel = null;
	
	public CenterPanel() {
		
		centerPanel = new TabPanel();  
        centerPanel.setDeferredRender(false);  
        centerPanel.setActiveTab(0);
        
        Panel startingPanel = new HTMLPanel();  
        
        startingPanel.setHtml("<br>Greeting text and information how to use Fish Oracle, loaded from a properties file or a database ...");
        
        startingPanel.setTitle("Welcome");  
        startingPanel.setAutoScroll(true);  
        startingPanel.setClosable(false);  
        
        centerPanel.add(startingPanel);
  		
	}

	public TabPanel getCenterPanel() {
		return centerPanel;
	}

	public void setCenterPanel(TabPanel centerPanel) {
		this.centerPanel = centerPanel;
	}

}
