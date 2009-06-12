package de.unihamburg.zbh.fishoracle.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.TextBox;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.form.FormPanel;
import com.gwtext.client.widgets.form.Radio;
import com.gwtext.client.widgets.layout.AccordionLayout;
import com.gwtext.client.core.EventObject;


import de.unihamburg.zbh.fishoracle.client.rpc.Search;
import de.unihamburg.zbh.fishoracle.client.rpc.SearchAsync;

public class WestPanel{

	private Panel westPanel = null;
	private TextBox searchBox = null;
	private Radio ampRadio = null;
	private Radio geneRadio = null;
	private Radio bandRadio = null;
	private MainPanel parentObj = null;
	

	public WestPanel(MainPanel obj) {
		
		parentObj = obj;
		
		final AccordionLayout accordion = new AccordionLayout(true);  
		
		westPanel = new Panel();  
        westPanel.setTitle("Menu");  
        westPanel.setCollapsible(true);  
        westPanel.setWidth(200);  
        westPanel.setLayout(accordion);
        
        Panel userPanel = new Panel();
        userPanel.setHtml("<p>User specific menu.</p>");  
        userPanel.setTitle("User");  
        userPanel.setBorder(false);   
        westPanel.add(userPanel);  
           
        Panel searchPanel = new Panel();  
        searchPanel.setTitle("Search");  
        searchPanel.setBorder(false);   
       

        FormPanel formPanel = new FormPanel();  
        formPanel.setBorder(false);  
        formPanel.setHideLabels(true);
        formPanel.setMargins(10);
        
        searchBox = new TextBox();
        searchBox.setText("00.03");
        
        formPanel.add(searchBox);
        
    
        ampRadio = new Radio();  
        ampRadio.setName("searchtype");
        ampRadio.setBoxLabel("Amplicon Search");  
        ampRadio.setChecked(true);
        formPanel.add(ampRadio);
          
        geneRadio = new Radio();  
        geneRadio.setName("searchtype");  
        geneRadio.setBoxLabel("Gene Search");  
        formPanel.add(geneRadio);  
               
        bandRadio = new Radio();  
        bandRadio.setName("searchtype");  
        bandRadio.setBoxLabel("Band Search");  
        formPanel.add(bandRadio);       
        
        final Button searchButton = new Button("Search", new ButtonListenerAdapter(){
			public void onClick(Button button, EventObject e) {
        		
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
				
				search(searchBox.getText() ,typeStr);
        		
        	}

       });
       
        formPanel.add(searchButton);
       
        searchPanel.add(formPanel);
        
        westPanel.add(searchPanel);
        
                
	}

	public Panel getWestPanel() {
		return westPanel;
	}

	public void setWestPanel(Panel westPanel) {
		this.westPanel = westPanel;
	}

	/*=============================================================================
	 *||                              RPC Calls                                  ||
	 *=============================================================================
	 * */	
		
		
	public void search(String query, String type){
			
			final SearchAsync req = (SearchAsync) GWT.create(Search.class);
			ServiceDefTarget endpoint = (ServiceDefTarget) req;
			String moduleRelativeURL = GWT.getModuleBaseURL() + "Search";
			endpoint.setServiceEntryPoint(moduleRelativeURL);
			final AsyncCallback<String> callback = new AsyncCallback<String>(){
				public void onSuccess(String result){
					
					
					parentObj.newImageTab(result);
					
					
				}
				public void onFailure(Throwable caught){
					System.out.println(caught.getMessage());
				}
			};
			req.generateImage(query, type, callback);
		}
		
	}
	

