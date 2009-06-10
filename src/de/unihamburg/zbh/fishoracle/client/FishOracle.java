package de.unihamburg.zbh.fishoracle.client;

import com.google.gwt.core.client.EntryPoint;
import com.gwtext.client.widgets.Viewport;

public class FishOracle implements EntryPoint {

	public void onModuleLoad() {
		
		MainPanel mainPanel = new MainPanel();
		
		new Viewport(mainPanel.getMainPanel());
	}
}
