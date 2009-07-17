package de.unihamburg.zbh.fishoracle.client;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.gwtext.client.widgets.Panel;

public class NorthPanel{

	private HorizontalPanel northPanel = null;
	
	public NorthPanel() {
		
		northPanel = new HorizontalPanel();
		northPanel.setHeight("45px");

		Panel content = new Panel();
		
		content.setHtml("<img src='images/zbh.jpg' style='float: left'><img src='images/uke.png' style='float: right'>");
		
		northPanel.add(content);
	}

	public HorizontalPanel getNorthPanel() {
		return northPanel;
	}

	public void setNorthPanel(HorizontalPanel northPanel) {
		this.northPanel = northPanel;
	}

}
