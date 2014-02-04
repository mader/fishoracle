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
import de.unihamburg.zbh.fishoracle.client.datasource.OrganDS;

public class OrganAdminTab extends Tab {

	private ListGrid organGrid;
	
	private TextItem organLabelTextItem;
	private ComboBoxItem organTypeCbItem;
	
	public OrganAdminTab(){
		
		this.setTitle("Manage Organs");
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
		
		ToolStrip organToolStrip = new ToolStrip();
		organToolStrip.setWidth100();
		
		ToolStripButton addOrganButton = new ToolStripButton();
		addOrganButton.setTitle("add Organ");
		addOrganButton.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				loadOrganManageWindow();
			}});
		
		organToolStrip.addButton(addOrganButton);
		
		controlsPanel.addMember(organToolStrip);
		
		headerContainer.addMember(controlsPanel);
		
		pane.addMember(headerContainer);
		
		HLayout gridContainer = new HLayout();
		gridContainer.setWidth100();
		gridContainer.setHeight100();
		
		organGrid = new ListGrid();
		organGrid.setWidth100();
		organGrid.setHeight100();
		organGrid.setAlternateRecordStyles(true);
		organGrid.setWrapCells(true);
		organGrid.setFixedRecordHeights(false);
		organGrid.setShowAllRecords(false);
		organGrid.setAutoFetchData(false);
		
		OrganDS oDS = new OrganDS();
		
		organGrid.setDataSource(oDS);
		organGrid.setFetchOperation(OperationId.ORGAN_FETCH_ALL);
		
		organGrid.fetchData();
		
		gridContainer.addMember(organGrid);
		
		pane.addMember(gridContainer);
		
		this.setPane(pane);
	}
	
public void loadOrganManageWindow(){
		
		final Window window = new Window();

		window.setTitle("Add Organ");
		window.setWidth(250);
		window.setHeight(120);
		window.setAlign(Alignment.CENTER);
		
		window.setAutoCenter(true);
		window.setIsModal(true);
		window.setShowModalMask(true);
		
		DynamicForm organForm = new DynamicForm();
		organLabelTextItem = new TextItem();
		organLabelTextItem.setTitle("Property Label");
		
		organTypeCbItem = new ComboBoxItem(); 
		organTypeCbItem.setTitle("Type");
		organTypeCbItem.setType("comboBox");
		
		organTypeCbItem.setAutoFetchData(false);
		
		OrganDS oDS = new OrganDS();
		
		organTypeCbItem.setOptionDataSource(oDS);
		organTypeCbItem.setOptionOperationId(OperationId.ORGAN_FETCH_TYPES);
		organTypeCbItem.setDisplayField("typeName");
		organTypeCbItem.setValueField("typeId");
		
		ButtonItem addOrganButton = new ButtonItem("Add");
		addOrganButton.setWidth(50);
		
		addOrganButton.addClickHandler(new com.smartgwt.client.widgets.form.fields.events.ClickHandler(){
			@Override
			public void onClick(
					com.smartgwt.client.widgets.form.fields.events.ClickEvent event) {
				
				ListGridRecord lgr = new ListGridRecord();
				lgr.setAttribute("organName", organLabelTextItem.getDisplayValue());
				lgr.setAttribute("organType", organTypeCbItem.getDisplayValue());
				
				organGrid.addData(lgr);
				
				window.hide();
			}
		});

		organForm.setItems(organLabelTextItem, organTypeCbItem, addOrganButton);
	
		window.addItem(organForm);
		
		window.show();
	}
}
