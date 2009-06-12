package de.unihamburg.zbh.fishoracle.client;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.gwtext.client.widgets.form.Label;

public class NorthPanel{

	private HorizontalPanel northPanel = null;
	
	public NorthPanel() {
		
		northPanel = new HorizontalPanel();
		northPanel.setHeight("45px");
		
		Label lbl = new Label();
		lbl.setText("Logos or anything else?");
		northPanel.add(lbl);
	}

	public HorizontalPanel getNorthPanel() {
		return northPanel;
	}

	public void setNorthPanel(HorizontalPanel northPanel) {
		this.northPanel = northPanel;
	}

}
