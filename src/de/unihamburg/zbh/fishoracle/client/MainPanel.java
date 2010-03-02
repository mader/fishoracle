package de.unihamburg.zbh.fishoracle.client;

import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.PasswordItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.VLayout;

public class MainPanel extends Layout {
	
	private VLayout mainPage = null;
	private HLayout mainFrame = null;
	private VLayout welcomePanel = new VLayout(); 
	private NorthPanel northPanel = null;
	private WestPanel westPanel = null;
	private CenterPanel centerPanel = null;
	
	public MainPanel() {
	
		/*Create the main page in which every other component is stored*/
		mainPage = new VLayout();
		
		/*Create the north panel and add it to the main page*/
		NorthPanel northPanel = new NorthPanel(this);
		northPanel.setHeight("3%");
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
		
		welcomePanel.setWidth100();
		welcomePanel.setHeight100();
		welcomePanel.bringToFront();
		welcomePanel.setBackgroundColor("white");
		welcomePanel.setAlign(Alignment.CENTER);
		
		/*user login*/
		DynamicForm loginForm = new DynamicForm();
		loginForm.setAlign(Alignment.CENTER);
		
		TextItem userTextItem = new TextItem();  
		userTextItem.setTitle("Username");
		userTextItem.setRequired(true);
		
		PasswordItem passwordItem = new PasswordItem();  
		passwordItem.setTitle("Password");  
		passwordItem.setRequired(true);  
		
		ButtonItem logInButton = new ButtonItem("login"); 
		
		ButtonItem registerButton = new ButtonItem("register");
		
		loginForm.setItems(userTextItem, passwordItem, logInButton, registerButton);
		
		welcomePanel.addMember(loginForm);
		
		this.addMember(mainPage);
	
		this.addChild(welcomePanel);
    
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