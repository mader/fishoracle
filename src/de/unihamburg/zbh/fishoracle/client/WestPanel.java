package de.unihamburg.zbh.fishoracle.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.MessageBox;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.TabPanel;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.event.KeyListener;
import com.gwtext.client.widgets.form.Checkbox;
import com.gwtext.client.widgets.form.FormPanel;
import com.gwtext.client.widgets.form.Radio;
import com.gwtext.client.widgets.form.TextField;
import com.gwtext.client.core.EventObject;


import de.unihamburg.zbh.fishoracle.client.data.GWTImageInfo;
import de.unihamburg.zbh.fishoracle.client.data.QueryInfo;
import de.unihamburg.zbh.fishoracle.client.rpc.Search;
import de.unihamburg.zbh.fishoracle.client.rpc.SearchAsync;

public class WestPanel extends TabPanel{

	private Radio ampPrio = null;
	private Radio delPrio = null;
	private Checkbox ampCb = null;
	private Checkbox delCb = null;
	
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

        
        Panel priorityPanel = new Panel();  
        priorityPanel.setTitle("Search Priority");  
        priorityPanel.setWidth(200);  
        priorityPanel.setCollapsible(true);  
        
        ampPrio = new Radio();  
        ampPrio.setName("cncPrio");
        ampPrio.setBoxLabel("Amplicon");
        ampPrio.setChecked(true);
        priorityPanel.add(ampPrio);
        
        delPrio = new Radio();  
        delPrio.setName("cncPrio");
        delPrio.setBoxLabel("Delicon");  
        priorityPanel.add(delPrio);
        
        
        Panel filterPanel = new Panel();  
        filterPanel.setTitle("Filter");  
        filterPanel.setWidth(200);  
        filterPanel.setCollapsible(true);
        
        ampCb = new Checkbox("show Amplicons");  
        ampCb.setChecked(true);
        filterPanel.add(ampCb);
        
        delCb = new Checkbox("show Delicons");
        delCb.setChecked(true);
        filterPanel.add(delCb);
        
        searchBox = new TextField();
        searchBox.addKeyListener(KeyCodes.KEY_ENTER, searchListener);
        
        formPanel.add(searchBox);
        
    
        ampRadio = new Radio();  
        ampRadio.setName("searchtype");
        ampRadio.setBoxLabel("Amplicon/Delicon Search");  
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
        		startSearch();
        	}

       });
       
        formPanel.add(searchButton);
       
        searchPanel.add(formPanel);
        searchPanel.add(priorityPanel);
        searchPanel.add(filterPanel);
        
        this.add(searchPanel);
        
        this.add(userPanel);  
        
                
	}

	KeyListener searchListener = new KeyListener(){

		@Override
		public void onKey(int key, EventObject e) {
			startSearch();
		}
		
		
	};
	
	public void startSearch(){
		String typeStr = null;
		String cncPrio = null;
		
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
			if(ampPrio.getValue()){
				cncPrio = ampPrio.getBoxLabel();
			}
			if(delPrio.getValue()){
				cncPrio = delPrio.getBoxLabel();
			}
			
			QueryInfo newQuery = new QueryInfo(searchBox.getText() ,typeStr, cncPrio, ampCb.getValue(), delCb.getValue(), centerPanel.getInnerWidth() - 20); 
			
			MessageBox.wait("Searching for " + searchBox.getText());
			search(newQuery);
		}
	}
	
	/*=============================================================================
	 *||                              RPC Calls                                  ||
	 *=============================================================================
	 * */	
		
		
	public void search(QueryInfo q){
			
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
			req.generateImage(q, callback);
		}	
}
	

