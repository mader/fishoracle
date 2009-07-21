package de.unihamburg.zbh.fishoracle.client;

import com.google.gwt.user.client.ui.HTML;
import com.gwtext.client.widgets.BoxComponent;

public class NorthPanel{

	private BoxComponent northPanel = null;
	
	public NorthPanel() {
		
		northPanel = new BoxComponent();
	 	northPanel.setHeight(65);
		
		northPanel.setEl(new HTML("<img src='images/zbh.jpg' style='float: left'><img src='images/uke.png' style='float: right'>").getElement());

	}

	public BoxComponent getNorthPanel() {
		return northPanel;
	}

	public void setNorthPanel(BoxComponent northPanel) {
		this.northPanel = northPanel;
	}

}
