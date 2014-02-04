/*
  Copyright (c) 2014 Malte Mader <mader@zbh.uni-hamburg.de>
  Copyright (c) 2014 Center for Bioinformatics, University of Hamburg

  Permission to use, copy, modify, and distribute this software for any
  purpose with or without fee is hereby granted, provided that the above
  copyright notice and this permission notice appear in all copies.

  THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
  WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
  MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
  ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
  WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
  ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
  OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
*/

package de.unihamburg.zbh.fishoracle.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.HeaderItem;
import com.smartgwt.client.widgets.form.fields.PasswordItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;

import de.unihamburg.zbh.fishoracle.client.data.DBConfigData;
import de.unihamburg.zbh.fishoracle.client.rpc.AdminService;
import de.unihamburg.zbh.fishoracle.client.rpc.AdminServiceAsync;

public class DatabaseAdminTab extends Tab {

	private TextItem ensemblHost;
    private TextItem ensemblPort;
    private TextItem ensemblDatabase;
    private TextItem ensemblUser;
    private TextItem ensemblPW;
    private TextItem fishoracleHost;
    private TextItem fishoraclePort;
    private TextItem fishoracleDatabase;
    private TextItem fishoracleUser;
    private TextItem fishoraclePW;
	
	public DatabaseAdminTab(DBConfigData dbdata){
		
		this.setTitle("Database Configuration");
		this.setCanClose(true);
		
		VLayout pane = new VLayout();
		pane.setWidth100();
		pane.setHeight100();
		pane.setDefaultLayoutAlign(Alignment.CENTER);
		
		HLayout header = new HLayout();
		header.setAutoWidth();
		header.setAutoHeight();
		
		Label headerLbl = new Label("<h2>Configure Database Connections</h2>");
		headerLbl.setWidth("300");
		header.addMember(headerLbl);
		
		pane.addMember(header);
	    
	    DynamicForm dataForm = new DynamicForm();
	    dataForm.setWidth("100");
	    dataForm.setHeight("25");
	    
	    HeaderItem ensemblHeaderItem =  new HeaderItem();
	    ensemblHeaderItem.setDefaultValue("Ensembl Connection Data");
	    
	    ensemblHost = new TextItem();
	    ensemblHost.setTitle("Host");
	    ensemblHost.setValue(dbdata.getEhost());
	    
	    ensemblPort = new TextItem();
	    ensemblPort.setTitle("Port");
	    ensemblPort.setValue(dbdata.getEport());
	    
	    ensemblDatabase = new TextItem();
	    ensemblDatabase.setTitle("Database");
	    ensemblDatabase.setValue(dbdata.getEdb());
	    
	    ensemblUser = new TextItem();
	    ensemblUser.setTitle("User");
	    ensemblUser.setValue(dbdata.getEuser());
	    
	    ensemblPW = new PasswordItem();
	    ensemblPW.setTitle("Password");
	    ensemblPW.setValue(dbdata.getEpw());
		
	    HeaderItem fishoracleHeaderItem =  new HeaderItem();
	    fishoracleHeaderItem.setDefaultValue("Fish Oracle Connection Data"); 
	    
	    fishoracleHost = new TextItem();
	    fishoracleHost.setTitle("Host");
	    fishoracleHost.setValue(dbdata.getFhost());
	    
	    fishoraclePort = new TextItem();
	    fishoraclePort.setTitle("Port");
	    fishoraclePort.setValue(dbdata.getEport());
	    
	    fishoracleDatabase = new TextItem();
	    fishoracleDatabase.setTitle("Database");
	    fishoracleDatabase.setValue(dbdata.getFdb());
	    
	    fishoracleUser = new TextItem();
	    fishoracleUser.setTitle("User");
	    fishoracleUser.setValue(dbdata.getFuser());
	    
	    fishoraclePW = new PasswordItem();
	    fishoraclePW.setTitle("Password");
	    fishoraclePW.setValue(dbdata.getFpw());
	    
	    ButtonItem submitButton = new ButtonItem("submit");
		submitButton.addClickHandler(new com.smartgwt.client.widgets.form.fields.events.ClickHandler(){

			@Override
			public void onClick(
					com.smartgwt.client.widgets.form.fields.events.ClickEvent event) {
				DBConfigData dbcd = new DBConfigData(ensemblHost.getDisplayValue(),
														Integer.parseInt(ensemblPort.getDisplayValue()),
														ensemblDatabase.getDisplayValue(),
														ensemblUser.getDisplayValue(),
														ensemblPW.getDisplayValue(),
														fishoracleHost.getDisplayValue(),
														Integer.parseInt(fishoraclePort.getDisplayValue()),
														fishoracleDatabase.getDisplayValue(),
														fishoracleUser.getDisplayValue(),
														fishoraclePW.getDisplayValue());
				storedbConfigData(dbcd);
			}
			
		});
	    
	    dataForm.setItems(ensemblHeaderItem,
	    					ensemblHost,
	    					ensemblPort,
	    					ensemblDatabase,
	    					ensemblUser,
	    					ensemblPW,
	    					fishoracleHeaderItem,
	    					fishoracleHost,
	    					fishoraclePort,
	    					fishoracleDatabase,
	    					fishoracleUser,
	    					fishoraclePW,
	    					submitButton);
	    
	    pane.addMember(dataForm);
	    
	    this.setPane(pane);
	}
	
	public void storedbConfigData(DBConfigData data){
		
		final AdminServiceAsync req = (AdminServiceAsync) GWT.create(AdminService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) req;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "AdminService";
		endpoint.setServiceEntryPoint(moduleRelativeURL);
		final AsyncCallback<Boolean> callback = new AsyncCallback<Boolean>(){
			@Override
			public void onSuccess(Boolean result){
				
				SC.say("Database connection parameters stored.");
				
			}
			public void onFailure(Throwable caught){
				System.out.println(caught.getMessage());
				SC.say(caught.getMessage());
			}
		};
		req.writeConfigData(data, callback);
	}
}