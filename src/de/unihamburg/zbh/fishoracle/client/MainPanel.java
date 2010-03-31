package de.unihamburg.zbh.fishoracle.client;

import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.VLayout;

public class MainPanel extends Layout {
	
	private VLayout mainPage = null;
	private HLayout mainFrame = null;
	private LoginScreen loginScreen  = null; 
	private NorthPanel northPanel = null;
	private WestPanel westPanel = null;
	private CenterPanel centerPanel = null;
	
	public MainPanel() {
	
		/*Create the main page in which every other component is stored*/
		mainPage = new VLayout();
		
		/*Create the north panel and add it to the main page*/
		northPanel = new NorthPanel(this);
		northPanel.setHeight("2%");
		northPanel.setWidth100();
		mainPage.addMember(northPanel);
	
		/*The main frame contains the west panel and the center panel*/
		mainFrame = new HLayout();
	
		centerPanel = new CenterPanel(this);
	
		westPanel = new WestPanel(this);
		westPanel.setWidth("20%");
		westPanel.setShowResizeBar(true);
	
		mainFrame.addMember(westPanel);
		mainFrame.addMember(centerPanel);
	
		mainPage.addMember(mainFrame);
		
		this.addMember(mainPage);
		
		loginScreen = new LoginScreen(this);
		loginScreen.setWidth100();
		loginScreen.setHeight100();
		loginScreen.bringToFront();
		loginScreen.setBackgroundColor("white");
		
		this.addChild(loginScreen);
    
	}
	
	public LoginScreen getLoginScreen() {
		return loginScreen;
	}

	public NorthPanel getNorthPanel() {
		return northPanel;
	}

	public WestPanel getWestPanel() {
		return westPanel;
	}

	public CenterPanel getCenterPanel() {
		return centerPanel;
	}
}