package de.unihamburg.zbh.fishoracle.client;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.form.Label;

public class NorthPanel{

	private HorizontalPanel northPanel = null;
	
	public NorthPanel() {
		
		northPanel = new HorizontalPanel();
		northPanel.setHeight("45px");
		//northPanel.setSpacing(50);
		
		//Label lbl = new Label();
		//lbl.setText("Logos or anything else?");
		//northPanel.add(lbl);

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
