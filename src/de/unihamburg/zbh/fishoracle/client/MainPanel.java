package de.unihamburg.zbh.fishoracle.client;

import com.gwtext.client.core.Margins;
import com.gwtext.client.core.RegionPosition;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.layout.BorderLayout;
import com.gwtext.client.widgets.layout.BorderLayoutData;
import com.gwtext.client.widgets.layout.FitLayout;

public class MainPanel{

	private Panel mainPanel = null;
	
	public MainPanel() {
	
	mainPanel = new Panel();
	
    mainPanel.setBorder(false);
    mainPanel.setPaddings(15);
    mainPanel.setLayout(new FitLayout());

    Panel borderPanel = new Panel();
    borderPanel.setLayout(new BorderLayout());
	
    NorthPanel northPanel = new NorthPanel();
    WestPanel westPanel = new WestPanel();
    CenterPanel centerPanel = new CenterPanel();
    
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
	  
}
