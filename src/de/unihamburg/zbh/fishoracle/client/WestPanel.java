package de.unihamburg.zbh.fishoracle.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.Component;
import com.gwtext.client.widgets.MessageBox;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.TabPanel;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.event.KeyListener;
import com.gwtext.client.widgets.form.FormPanel;
import com.gwtext.client.widgets.form.Label;
import com.gwtext.client.widgets.form.Radio;
import com.gwtext.client.widgets.form.TextField;
import com.gwtext.client.core.EventObject;


import de.unihamburg.zbh.fishoracle.client.data.GWTImageInfo;
import de.unihamburg.zbh.fishoracle.client.data.User;
import de.unihamburg.zbh.fishoracle.client.rpc.Search;
import de.unihamburg.zbh.fishoracle.client.rpc.SearchAsync;
import de.unihamburg.zbh.fishoracle.client.rpc.UserService;
import de.unihamburg.zbh.fishoracle.client.rpc.UserServiceAsync;

public class WestPanel extends TabPanel{
	
	private Panel searchPanel = null;
	private Button logoutButton = null;
	private Button registerButton = null;
	private FormPanel userFormPanel = null;
	private TextField userName = null;
	private TextField pw = null;
	private Panel userPanel = null;
	private TextField searchBox = null;
	private Radio ampRadio = null;
	private Radio geneRadio = null;
	private Radio bandRadio = null;
	private MainPanel parentObj = null;
	private CenterPanel centerPanel = null;
	
	public WestPanel(MainPanel obj, CenterPanel cp) {
		
		parentObj = obj;
		centerPanel = cp;
		
        this.setTitle("Menu");
        this.setCollapsible(true);
        this.setWidth(200);
        
        userPanel = new Panel();  
        userPanel.setTitle("User");  
        userPanel.setBorder(false);
        
        userFormPanel = new FormPanel();
        userFormPanel.setBorder(false);
        userFormPanel.setVisible(true);
        userFormPanel.setHideLabels(true);
        
        userName = new TextField();
        pw = new TextField();
        pw.setInputType("password");
        Button loginButton = new Button("log in", new ButtonListenerAdapter(){
			public void onClick(Button button, EventObject e) {
        			
				
        			MessageBox.wait("Logging in " + userName.getText());

        			userLogin(userName.getText(), pw.getText());
        		
        	}

        });
        
        logoutButton = new Button("log out", new ButtonListenerAdapter(){
			public void onClick(Button button, EventObject e) {     
        			
        			MessageBox.wait("Logging in " + userName.getText());
        			userLogout();
        		
        	}

        });
        
        logoutButton.hide();
        
        registerButton = new Button("register", new ButtonListenerAdapter(){
			public void onClick(Button button, EventObject e) {     
        			
					Panel tab = centerPanel.openRegisterTab();
				
        			centerPanel.add(tab);
        			centerPanel.activate(tab.getId());
        			centerPanel.scrollToTab(tab, true);
        	}

        });
        
        Label emailLbl = new Label("user name: ");
        Label passwordLbl = new Label("password: ");
        
        userFormPanel.add(emailLbl);
        userFormPanel.add(userName);
        userFormPanel.add(passwordLbl);
        userFormPanel.add(pw);
        userFormPanel.add(loginButton);
        
        
        userPanel.add(userFormPanel);
        
        userPanel.add(logoutButton);
        
        userPanel.add(registerButton);
        
        searchPanel = new Panel();
        searchPanel.setTitle("Search");  
        searchPanel.setBorder(false);
        searchPanel.setVisible(true); 
        searchPanel.setDisabled(true);
        
        FormPanel formPanel = new FormPanel();  
        formPanel.setBorder(false);
        formPanel.setHideLabels(true);
        formPanel.setMargins(10);
        
        searchBox = new TextField();
        searchBox.addKeyListener(KeyCodes.KEY_ENTER, searchListener);
        
        formPanel.add(searchBox);
    
        ampRadio = new Radio();  
        ampRadio.setName("searchtype");
        ampRadio.setBoxLabel("Amplicon Search");  
        formPanel.add(ampRadio);
          
        geneRadio = new Radio();  
        geneRadio.setName("searchtype");  
        geneRadio.setBoxLabel("Gene Search");
        geneRadio.setChecked(true);
        formPanel.add(geneRadio);  
               
        bandRadio = new Radio();  
        bandRadio.setName("searchtype");  
        bandRadio.setBoxLabel("Band Search");  
        formPanel.add(bandRadio);       
        
        final Button searchButton = new Button("Search", new ButtonListenerAdapter(){
			public void onClick(Button button, EventObject e) {
        		
        		String typeStr = null;
				
        		if(searchBox.getText().equals("")){
        			MessageBox.alert("You have to type in a search term!");
        		} else {
        		
        			if(ampRadio.getValue()){
        				typeStr = ampRadio.getBoxLabel();
        			}
        			if(geneRadio.getValue()){
        				typeStr = geneRadio.getBoxLabel();
        			}	
        			if(bandRadio.getValue()){
        				typeStr = bandRadio.getBoxLabel();
        			}
        			
        			MessageBox.wait("Searching for " + searchBox.getText());
        			search(searchBox.getText() ,typeStr, centerPanel.getInnerWidth() - 20);
        		}
        	}

       });
       
        formPanel.add(searchButton);
       
        searchPanel.add(formPanel);
        
        this.add(searchPanel);
        
        this.add(userPanel);  
        
                
	}

	KeyListener searchListener = new KeyListener(){

		@Override
		public void onKey(int key, EventObject e) {
			
			String typeStr = null;
			
			if(ampRadio.getValue()){
				typeStr = ampRadio.getBoxLabel();
			}
			if(geneRadio.getValue()){
				typeStr = geneRadio.getBoxLabel();
			}	
			if(bandRadio.getValue()){
				typeStr = bandRadio.getBoxLabel();
			}
			MessageBox.wait("Searching for " + searchBox.getText());
			search(searchBox.getText() ,typeStr, centerPanel.getInnerWidth() - 20);
		}
		
		
	};
	
	/*=============================================================================
	 *||                              RPC Calls                                  ||
	 *=============================================================================
	 * */	
		
		
	public void search(String query, String type, int winWidth){
			
			final SearchAsync req = (SearchAsync) GWT.create(Search.class);
			ServiceDefTarget endpoint = (ServiceDefTarget) req;
			String moduleRelativeURL = GWT.getModuleBaseURL() + "Search";
			endpoint.setServiceEntryPoint(moduleRelativeURL);
			final AsyncCallback<GWTImageInfo> callback = new AsyncCallback<GWTImageInfo>(){
				public void onSuccess(GWTImageInfo result){
					
					MessageBox.hide();
					parentObj.newImageTab(result);
					
					
				}
				public void onFailure(Throwable caught){

					MessageBox.hide();
					MessageBox.alert(caught.getMessage());
					
				}
			};
			req.generateImage(query, type, winWidth, callback);
		}	
	
	public void userLogin(String userName, String password){
		
		final UserServiceAsync req = (UserServiceAsync) GWT.create(UserService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) req;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "UserService";
		endpoint.setServiceEntryPoint(moduleRelativeURL);
		final AsyncCallback<User> callback = new AsyncCallback<User>(){
			public void onSuccess(User result){
				
				searchPanel.setDisabled(false);
				userFormPanel.hide();
				registerButton.hide();
				logoutButton.show();
				MessageBox.hide();
				
			}
			public void onFailure(Throwable caught){
				System.out.println(caught.getMessage());
				MessageBox.hide();
				MessageBox.alert(caught.getMessage());
			}
		};
		req.login(userName, password, callback);
	}	
	
	public void userLogout(){
		
		final UserServiceAsync req = (UserServiceAsync) GWT.create(UserService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) req;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "UserService";
		endpoint.setServiceEntryPoint(moduleRelativeURL);
		final AsyncCallback<User> callback = new AsyncCallback<User>(){
			public void onSuccess(User result){
				
				userName.reset();
				pw.reset();
				logoutButton.hide();
				registerButton.show();
				searchPanel.setDisabled(true);
				userFormPanel.show();
				
				Component[] items = centerPanel.getItems();  
					for (int i = 0; i < items.length; i++) {  
						Component component = items[i];  
						if (!component.getTitle().equals("Welcome")) {  
							centerPanel.remove(component);  
						}
					}
				
				
				MessageBox.hide();
				
			}
			public void onFailure(Throwable caught){
				System.out.println(caught.getMessage());
				MessageBox.hide();
				MessageBox.alert(caught.getMessage());
			}
		};
		req.logout(callback);
	}	
}
	

