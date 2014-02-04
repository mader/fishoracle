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

import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

import de.unihamburg.zbh.fishoracle.client.datasource.EnsemblDBDS;

public class EnsemblDBAdminTab extends Tab {

	private ListGrid ensemblGrid;
	
	private TextItem dbNameTextItem;
	private TextItem dbLabelTextItem;
	private TextItem dbVersionTextItem;
	
	public EnsemblDBAdminTab(){
		
		this.setTitle("Ensembl Databases");
		this.setCanClose(true);
		
		VLayout pane = new VLayout();
		pane.setWidth100();
		pane.setHeight100();
		pane.setDefaultLayoutAlign(Alignment.CENTER);
		
		VLayout headerContainer = new VLayout();
		headerContainer.setDefaultLayoutAlign(Alignment.CENTER);
		headerContainer.setWidth100();
		headerContainer.setAutoHeight();
		
		HLayout controlsPanel = new HLayout();
		controlsPanel.setWidth100();
		controlsPanel.setAutoHeight();
		
		ToolStrip ensemblToolStrip = new ToolStrip();
		ensemblToolStrip.setWidth100();
		
		ToolStripButton addEDBButton = new ToolStripButton();
		addEDBButton.setTitle("add Database");
		addEDBButton.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				loadEnsemblManageWindow();
			}});
		
		ensemblToolStrip.addButton(addEDBButton);
		
		ToolStripButton deleteEDBButton = new ToolStripButton();  
		deleteEDBButton.setTitle("delete Database");
		deleteEDBButton.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				
				final ListGridRecord lgr = ensemblGrid.getSelectedRecord();
				
				if (lgr != null) {
				
					SC.confirm("Do you really want to delete " + lgr.getAttribute("ensemblDBLabel") + "?", new BooleanCallback(){

						@Override
						public void execute(Boolean value) {
							if(value != null && value){
						
								ensemblGrid.removeData(lgr);
							}
						}
					});
				
				} else {
					SC.say("Select a database.");
				}
			}});
		
		ensemblToolStrip.addButton(deleteEDBButton);
		
		
		controlsPanel.addMember(ensemblToolStrip);
		
		headerContainer.addMember(controlsPanel);
		
		pane.addMember(headerContainer);
		
		HLayout gridContainer = new HLayout();
		gridContainer.setWidth100();
		gridContainer.setHeight100();
		
		ensemblGrid = new ListGrid();
		ensemblGrid.setWidth100();
		ensemblGrid.setHeight100();
		ensemblGrid.setAlternateRecordStyles(true);
		ensemblGrid.setWrapCells(true);
		ensemblGrid.setFixedRecordHeights(false);
		ensemblGrid.setShowAllRecords(false);
		ensemblGrid.setAutoFetchData(false);
		
		EnsemblDBDS edbDS = new EnsemblDBDS();
		
		ensemblGrid.setDataSource(edbDS);
		ensemblGrid.fetchData();
		
		gridContainer.addMember(ensemblGrid);
		
		pane.addMember(gridContainer);
		
		this.setPane(pane);
	}
	
	public void loadEnsemblManageWindow(){
		
		final Window window = new Window();

		window.setTitle("Add Ensembl Database");
		window.setWidth(250);
		window.setHeight(150);
		window.setAlign(Alignment.CENTER);
		
		window.setAutoCenter(true);
		window.setIsModal(true);
		window.setShowModalMask(true);
		
		DynamicForm ensemblForm = new DynamicForm();
		
		dbNameTextItem = new TextItem();
		dbNameTextItem.setTitle("Database Name");
		
		dbLabelTextItem = new TextItem();
		dbLabelTextItem.setTitle("Database Label");
		
		dbVersionTextItem = new TextItem();
		dbVersionTextItem.setTitle("Database Version");
		
		ButtonItem addDBButton = new ButtonItem("Add");
		addDBButton.setWidth(50);
		
		addDBButton.addClickHandler(new com.smartgwt.client.widgets.form.fields.events.ClickHandler(){
			@Override
			public void onClick(
					com.smartgwt.client.widgets.form.fields.events.ClickEvent event) {
				
				ListGridRecord lgr = new ListGridRecord();
				lgr.setAttribute("ensemblDBName", dbNameTextItem.getDisplayValue());
				lgr.setAttribute("ensemblDBLabel", dbLabelTextItem.getDisplayValue());
				lgr.setAttribute("ensemblDBVersion", dbVersionTextItem.getDisplayValue());
				
				ensemblGrid.addData(lgr);
				
				window.hide();
			}
		});

		ensemblForm.setItems(dbNameTextItem, dbLabelTextItem, dbVersionTextItem, addDBButton);
	
		window.addItem(ensemblForm);
		
		window.show();
	}
}