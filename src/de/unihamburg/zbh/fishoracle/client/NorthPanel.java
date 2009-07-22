package de.unihamburg.zbh.fishoracle.client;

import com.google.gwt.user.client.ui.HTML;
import com.gwtext.client.widgets.BoxComponent;

public class NorthPanel extends BoxComponent{
	
	public NorthPanel() {
		this.setHeight(65);
		this.setEl(new HTML("<img src='images/zbh.jpg' style='float: left'><img src='images/uke.png' style='float: right'>").getElement());
	}
}
