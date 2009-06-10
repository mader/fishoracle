package de.unihamburg.zbh.fishoracle.client;

import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.layout.AccordionLayout;

public class WestPanel{

	private Panel westPanel = null;
	
	public WestPanel() {
		
		final AccordionLayout accordion = new AccordionLayout(true);  
		
		westPanel = new Panel();  
        westPanel.setTitle("Menu");  
        westPanel.setCollapsible(true);  
        westPanel.setWidth(200);  
        westPanel.setLayout(accordion);
        
        Panel userPanel = new Panel();
        userPanel.setHtml("<p>User specific menu.</p>");  
        userPanel.setTitle("User");  
        userPanel.setBorder(false);   
        westPanel.add(userPanel);  
           
        Panel searchPanel = new Panel();  
        searchPanel.setHtml("<p>here we will be able to search for amplicons & co.</p>");  
        searchPanel.setTitle("Search");  
        searchPanel.setBorder(false);   
        westPanel.add(searchPanel);
        
        
	}

	public Panel getWestPanel() {
		return westPanel;
	}

	public void setWestPanel(Panel westPanel) {
		this.westPanel = westPanel;
	}

}
