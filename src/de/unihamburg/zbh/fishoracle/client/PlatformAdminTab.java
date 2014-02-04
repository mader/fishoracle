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
import de.unihamburg.zbh.fishoracle.client.datasource.PlatformDS;

public class PlatformAdminTab extends Tab {

	private ListGrid platformGrid;
	
	private TextItem platformLabelTextItem;
	private ComboBoxItem platformTypeCbItem;
	
	public PlatformAdminTab(){
		
		this.setTitle("Manage Platforms");
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
		
		ToolStrip platformToolStrip = new ToolStrip();
		platformToolStrip.setWidth100();
		
		ToolStripButton addPlatformButton = new ToolStripButton();
		addPlatformButton.setTitle("add Platform");
		addPlatformButton.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				loadPlatformManageWindow();
			}});
		
		platformToolStrip.addButton(addPlatformButton);
		
		controlsPanel.addMember(platformToolStrip);
		
		headerContainer.addMember(controlsPanel);
		
		pane.addMember(headerContainer);
		
		HLayout gridContainer = new HLayout();
		gridContainer.setWidth100();
		gridContainer.setHeight100();
		
		platformGrid = new ListGrid();
		platformGrid.setWidth100();
		platformGrid.setHeight100();
		platformGrid.setAlternateRecordStyles(true);
		platformGrid.setWrapCells(true);
		platformGrid.setFixedRecordHeights(false);
		platformGrid.setShowAllRecords(false);
		platformGrid.setAutoFetchData(false);
		
		PlatformDS pDS = new PlatformDS();
		
		platformGrid.setDataSource(pDS);
		platformGrid.setFetchOperation(OperationId.PLATFORM_FETCH_ALL);
		platformGrid.fetchData();
		
		gridContainer.addMember(platformGrid);
		
		pane.addMember(gridContainer);
		
		this.setPane(pane);
	}
	
public void loadPlatformManageWindow(){
		
		final Window window = new Window();

		window.setTitle("Add Platform");
		window.setWidth(250);
		window.setHeight(120);
		window.setAlign(Alignment.CENTER);
		
		window.setAutoCenter(true);
		window.setIsModal(true);
		window.setShowModalMask(true);
		
		DynamicForm chipForm = new DynamicForm();
		platformLabelTextItem = new TextItem();
		platformLabelTextItem.setTitle("Platform Label");
		
		platformTypeCbItem = new ComboBoxItem(); 
		platformTypeCbItem.setTitle("Type");
		platformTypeCbItem.setType("comboBox");
		
		platformTypeCbItem.setAutoFetchData(false);
		
		PlatformDS pDS = new PlatformDS();
		
		platformTypeCbItem.setOptionDataSource(pDS);
		platformTypeCbItem.setOptionOperationId(OperationId.PLATFORM_FETCH_TYPES);
		platformTypeCbItem.setDisplayField("typeName");
		platformTypeCbItem.setValueField("typeId");
		
		ButtonItem addPlatformButton = new ButtonItem("Add");
		addPlatformButton.setWidth(50);
		
		addPlatformButton.addClickHandler(new com.smartgwt.client.widgets.form.fields.events.ClickHandler(){
			@Override
			public void onClick(
					com.smartgwt.client.widgets.form.fields.events.ClickEvent event) {
				
				ListGridRecord lgr = new ListGridRecord();
				lgr.setAttribute("platformName", platformLabelTextItem.getDisplayValue());
				lgr.setAttribute("platformType", platformTypeCbItem.getDisplayValue());
				
				platformGrid.addData(lgr);
				
				window.hide();
			}
		});

		chipForm.setItems(platformLabelTextItem, platformTypeCbItem, addPlatformButton);
	
		window.addItem(chipForm);
		
		window.show();
	}
}