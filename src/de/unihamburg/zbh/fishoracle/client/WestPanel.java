package de.unihamburg.zbh.fishoracle.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.MessageBox;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.event.KeyListener;
import com.gwtext.client.widgets.form.FormPanel;
import com.gwtext.client.widgets.form.Radio;
import com.gwtext.client.widgets.form.TextField;
import com.gwtext.client.widgets.layout.AccordionLayout;
import com.gwtext.client.core.EventObject;


import de.unihamburg.zbh.fishoracle.client.data.GWTImageInfo;
import de.unihamburg.zbh.fishoracle.client.rpc.Search;
import de.unihamburg.zbh.fishoracle.client.rpc.SearchAsync;

public final class WestPanel extends Panel{

	private TextField searchBox = null;
	private Radio ampRadio = null;
	private Radio geneRadio = null;
	private Radio bandRadio = null;
	private MainPanel parentObj = null;
	private CenterPanel centerPanel = null;

	static WestPanel west = new WestPanel();
	
	public WestPanel() {
		
	}
	
	public WestPanel(MainPanel obj, CenterPanel cp) {
		
		parentObj = obj;
		centerPanel = cp;
		
		final AccordionLayout accordion = new AccordionLayout(true);  
		
		
        this.setTitle("Menu");
        this.setCollapsible(true);
        this.setWidth(200);
        this.setLayout(accordion);
        
        Panel userPanel = new Panel();
        userPanel.setHtml("<p>User specific menu. Not implemented yet.</p>");  
        userPanel.setTitle("User");  
        userPanel.setBorder(false);   
        
        Panel searchPanel = new Panel();
        searchPanel.setTitle("Search");  
        searchPanel.setBorder(false);
        searchPanel.setVisible(true); 
        
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
					System.out.println(caught.getMessage());
					MessageBox.hide();
					MessageBox.alert("Nothing found!");
				}
			};
			req.generateImage(query, type, winWidth, callback);
		}
	
	public static WestPanel getInstance(){
		if(west == null){
			west = new WestPanel();
		}
		return west;
	}
	
}
	

