package de.unihamburg.zbh.fishoracle.client;

import com.google.gwt.core.client.EntryPoint;


public class FishOracle implements EntryPoint {

	public void onModuleLoad() {
		
		MainPanel mainPanel = new MainPanel();
		mainPanel.setWidth100();
		mainPanel.setHeight100();
		mainPanel.draw();
	}
}
