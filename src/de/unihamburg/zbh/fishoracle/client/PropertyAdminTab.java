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
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.ComboBoxItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

import de.unihamburg.zbh.fishoracle.client.datasource.OperationId;
import de.unihamburg.zbh.fishoracle.client.datasource.PropertyDS;

public class PropertyAdminTab extends Tab {

	private ListGrid propertyGrid;
	
	private TextItem propertyLabelTextItem;
	private ComboBoxItem propertyTypeCbItem;
	
	public PropertyAdminTab(){
		
		this.setTitle("Manage Properties");
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
		
		ToolStrip propertyToolStrip = new ToolStrip();
		propertyToolStrip.setWidth100();
		
		ToolStripButton addPropertyButton = new ToolStripButton();
		addPropertyButton.setTitle("add Property");
		addPropertyButton.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				loadPropertyManageWindow();
			}});
		
		propertyToolStrip.addButton(addPropertyButton);
		
		controlsPanel.addMember(propertyToolStrip);
		
		headerContainer.addMember(controlsPanel);
		
		pane.addMember(headerContainer);
		
		HLayout gridContainer = new HLayout();
		gridContainer.setWidth100();
		gridContainer.setHeight100();
				
		propertyGrid = new ListGrid();
		propertyGrid.setWidth100();
		propertyGrid.setHeight100();
		propertyGrid.setAlternateRecordStyles(true);
		propertyGrid.setWrapCells(true);
		propertyGrid.setFixedRecordHeights(false);
		propertyGrid.setShowAllRecords(false);
		propertyGrid.setAutoFetchData(false);
		
		PropertyDS pDS = new PropertyDS();
		
		propertyGrid.setDataSource(pDS);
		propertyGrid.setFetchOperation(OperationId.PROPERTY_FETCH_ALL);
		
		propertyGrid.fetchData();
		
		gridContainer.addMember(propertyGrid);
		
		pane.addMember(gridContainer);
		
		this.setPane(pane);
	}
	
	public void loadPropertyManageWindow(){
		
		final Window window = new Window();

		window.setTitle("Add Property");
		window.setWidth(250);
		window.setHeight(120);
		window.setAlign(Alignment.CENTER);
		
		window.setAutoCenter(true);
		window.setIsModal(true);
		window.setShowModalMask(true);
		
		DynamicForm propertyForm = new DynamicForm();
		propertyLabelTextItem = new TextItem();
		propertyLabelTextItem.setTitle("Property Label");
		
		propertyTypeCbItem = new ComboBoxItem(); 
		propertyTypeCbItem.setTitle("Type");
		propertyTypeCbItem.setType("comboBox");
		
		propertyTypeCbItem.setDisplayField("typeName");
		propertyTypeCbItem.setValueField("typeId");
		
		propertyTypeCbItem.setAutoFetchData(false);
		
		PropertyDS pDS = new PropertyDS();
		
		propertyTypeCbItem.setOptionDataSource(pDS);
		propertyTypeCbItem.setOptionOperationId(OperationId.PROPERTY_FETCH_TYPES);
		
		ButtonItem addPropertyButton = new ButtonItem("Add");
		addPropertyButton.setWidth(50);
		
		addPropertyButton.addClickHandler(new com.smartgwt.client.widgets.form.fields.events.ClickHandler(){
			@Override
			public void onClick(
					com.smartgwt.client.widgets.form.fields.events.ClickEvent event) {
				
				ListGridRecord lgr = new ListGridRecord();
				lgr.setAttribute("propertyName", propertyLabelTextItem.getDisplayValue());
				lgr.setAttribute("propertyType", propertyTypeCbItem.getDisplayValue());
				
				propertyGrid.addData(lgr);
				
				window.hide();
				
				window.hide();
			}
		});

		propertyForm.setItems(propertyLabelTextItem, propertyTypeCbItem, addPropertyButton);
	
		window.addItem(propertyForm);
		
		window.show();
	}
}