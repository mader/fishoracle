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

import gwtupload.client.IUploadStatus.Status;
import gwtupload.client.IUploader;
import gwtupload.client.IUploader.OnFinishUploaderHandler;
import gwtupload.client.IUploader.UploadedInfo;
import gwtupload.client.MultiUploader;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.ListGridEditEvent;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.CloseClickEvent;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.CheckboxItem;
import com.smartgwt.client.widgets.form.fields.ComboBoxItem;
import com.smartgwt.client.widgets.form.fields.RadioGroupItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

import de.unihamburg.zbh.fishoracle.client.data.FoStudy;
import de.unihamburg.zbh.fishoracle.client.datasource.FeatureDS;
import de.unihamburg.zbh.fishoracle.client.datasource.FileImportDS;
import de.unihamburg.zbh.fishoracle.client.datasource.OperationId;
import de.unihamburg.zbh.fishoracle.client.datasource.OrganDS;
import de.unihamburg.zbh.fishoracle.client.datasource.PlatformDS;
import de.unihamburg.zbh.fishoracle.client.datasource.ProjectDS;
import de.unihamburg.zbh.fishoracle.client.rpc.AdminService;
import de.unihamburg.zbh.fishoracle.client.rpc.AdminServiceAsync;

public class ImportTab extends Tab {
	
	private ListGrid fileGrid;
	private RadioGroupItem createStudyItem;
	private ComboBoxItem cbItemFilterType;
	private SelectItem selectItemProjects;
	private SelectItem selectItemTissues;
	private SelectItem selectItemPlatform;
	private SelectItem selectItemGenomeAssembly;
	private CheckboxItem batchCheckbox;
	
	private Window importMsgWindow;
	
	private ImportTab self;
	
	public ImportTab(){
	
		this.self = this;
		
		this.setTitle("Data Import");
		this.setCanClose(true);
	
		VLayout pane = new VLayout();
		pane.setWidth100();
		pane.setHeight100();
		pane.setDefaultLayoutAlign(Alignment.CENTER);
	
		HLayout header = new HLayout();
		header.setWidth100();
		header.setAutoHeight();
	
		HLayout controlsPanel = new HLayout();
		controlsPanel.setWidth100();
		controlsPanel.setAutoHeight();
	
		ToolStrip importToolStrip = new ToolStrip();
		importToolStrip.setWidth100();
	
		ToolStripButton addUploadButton = new ToolStripButton();  
		addUploadButton.setTitle("Upload Files");
		addUploadButton.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				loadUploadWindow();
			}});
	
		importToolStrip.addButton(addUploadButton);
	
		ToolStripButton deleteUploadButton = new ToolStripButton();  
		deleteUploadButton.setTitle("Delete Files");
		deleteUploadButton.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
			
				if(fileGrid.getSelectedRecords().length > 0){
					fileGrid.removeSelectedData();
				} else {
					SC.say("Select at least one file for deletion.");
				}
			}});
	
		importToolStrip.addButton(deleteUploadButton);
	
		controlsPanel.addMember(importToolStrip);
	
		VLayout body = new VLayout();
		body.setWidth100();
		body.setHeight100();
		body.setDefaultLayoutAlign(Alignment.CENTER);
	
		fileGrid = new ListGrid();
		fileGrid.setWidth100();
		fileGrid.setHeight("50%");  
		fileGrid.setShowRowNumbers(true);
		fileGrid.setAlternateRecordStyles(true);
		fileGrid.setEditByCell(true);
		fileGrid.setEditEvent(ListGridEditEvent.CLICK);
		fileGrid.setWrapCells(true);
		fileGrid.setFixedRecordHeights(false);
		fileGrid.setSelectionType(SelectionStyle.MULTIPLE);
		fileGrid.setCanDragSelect(true);
		fileGrid.setShowAllRecords(false);

		FileImportDS fiDS = new FileImportDS();
	
		fileGrid.setDataSource(fiDS);
	
		fileGrid.fetchData();
	
		HLayout importOptions = new HLayout();
		importOptions.setWidth100();
		importOptions.setHeight("50%");
		importOptions.setAlign(Alignment.CENTER);
	
		DynamicForm importOptionsForm = new DynamicForm();
		importOptionsForm.setWidth("250px");
		importOptionsForm.setAlign(Alignment.CENTER);
		
		cbItemFilterType = new ComboBoxItem();
		cbItemFilterType.setTitle("Data Type");
	
		cbItemFilterType.setDisplayField("featureType");
		cbItemFilterType.setValueField("featureType");
		cbItemFilterType.setAutoFetchData(false);
		FeatureDS fDS = new FeatureDS();
	
		cbItemFilterType.setOptionDataSource(fDS);
		cbItemFilterType.setOptionOperationId(OperationId.FEATURE_FETCH_TYPES);
	
		cbItemFilterType.setDefaultToFirstOption(true);
	
		createStudyItem = new RadioGroupItem();
		createStudyItem.setTitle("");
		createStudyItem.setValueMap("Create new study", "Import to existing study");
		createStudyItem.setDefaultValue("Create new study");
		createStudyItem.addChangedHandler(new ChangedHandler(){

			@Override
			public void onChanged(ChangedEvent event) {
				String val = event.getValue().toString();
			
				if(val.equals("Create new study")) {
					selectItemProjects.show();
					selectItemTissues.show();
					selectItemPlatform.show();
					selectItemGenomeAssembly.show();
				}
				if(val.equals("Import to existing study")) {
					selectItemProjects.hide();
					selectItemTissues.hide();
					selectItemPlatform.hide();
					selectItemGenomeAssembly.hide();
				}
			}
		});
	
		selectItemProjects = new SelectItem();
		selectItemProjects.setTitle("Project");
		selectItemProjects.setDisplayField("projectName");
		selectItemProjects.setValueField("projectId");
		selectItemProjects.setAutoFetchData(false);
		ProjectDS pDS = new ProjectDS();
	
		selectItemProjects.setOptionDataSource(pDS);
		selectItemProjects.setOptionOperationId(OperationId.PROJECT_FETCH_READ_WRITE);
	
		selectItemProjects.setDefaultToFirstOption(true);
	
		selectItemTissues = new SelectItem();
		selectItemTissues.setTitle("Tissue");
		selectItemTissues.setDisplayField("organNamePlusType");
		selectItemTissues.setValueField("organId");
		selectItemTissues.setAutoFetchData(false);
		OrganDS oDS = new OrganDS();
	
		selectItemTissues.setOptionDataSource(oDS);
		selectItemTissues.setOptionOperationId(OperationId.ORGAN_FETCH_ENABLED);
	
		selectItemTissues.setDefaultToFirstOption(true);
	
		selectItemPlatform = new SelectItem();
		selectItemPlatform.setTitle("Platform");
		selectItemPlatform.setDisplayField("platformName");
		selectItemPlatform.setValueField("platformId");
		selectItemPlatform.setAutoFetchData(false);
	
		PlatformDS plDS = new PlatformDS();
	
		selectItemPlatform.setOptionDataSource(plDS);
		selectItemPlatform.setOptionOperationId(OperationId.PLATFORM_FETCH_ALL);
		selectItemPlatform.setDefaultToFirstOption(true);
		
		selectItemGenomeAssembly = new SelectItem();
		selectItemGenomeAssembly.setTitle("Genome Assembly");
		selectItemGenomeAssembly.setValueMap("GrCh37", "ncbi36");
		selectItemGenomeAssembly.setDefaultToFirstOption(true);
	
		batchCheckbox = new CheckboxItem();
		batchCheckbox.setTitle("Batch import");
		batchCheckbox.setValue(false);
	
		ButtonItem importButton = new ButtonItem("Import");
		importButton.addClickHandler(new com.smartgwt.client.widgets.form.fields.events.ClickHandler(){

			@Override
			public void onClick(
				com.smartgwt.client.widgets.form.fields.events.ClickEvent event) {
			
				ListGridRecord[] lgrs = fileGrid.getRecords();
			
				FoStudy[] studies = new FoStudy[lgrs.length];
			
				for(int i = 0; i < lgrs.length; i++){
					studies[i] = new FoStudy();
					studies[i].setName(lgrs[i].getAttributeAsString("studyName"));
					studies[i].setFiles(new String[]{lgrs[i].getAttributeAsString("fileName")});
				
					if(createStudyItem.getValueAsString().equals("Create new study")){
						studies[i].setAssembly(selectItemGenomeAssembly.getValueAsString());
						studies[i].setDescription("");
						studies[i].setOrganId(Integer.parseInt(selectItemTissues.getValue().toString()));
						studies[i].setPlatformId(Integer.parseInt(selectItemPlatform.getValue().toString()));
						studies[i].setPropertyIds(new int[]{});
					}
				}
			
				//TODO search for solution to async import with progressbar.
				if(batchCheckbox.getValueAsBoolean()) {
					// Import automatically
					//for(int i = 0; i < studies.length; i++) {
						if(createStudyItem.getValueAsString().equals("Create new study")) {
							// create study and import data
						
							importData(studies,
										cbItemFilterType.getValueAsString(),
										//cbItemFilterType.getSelectedRecord().getAttribute("type"),
										cbItemFilterType.getValueAsString(),
										true,
										Integer.parseInt(selectItemProjects.getValue().toString()),
										"",
										studies.length, 
										studies.length);
						}
						if(createStudyItem.getValueAsString().equals("Import to existing study")) {
							// import data into existing study
							importData(studies,
										cbItemFilterType.getValueAsString(), 
										cbItemFilterType.getValueAsString(),
										false,
										Integer.parseInt(selectItemProjects.getValue().toString()),
										"",
										studies.length, 
										studies.length);
						}
						//}
				} else {
					//Manual import
					if(studies.length > 0){
						ManualImportWindow miw = null;
						if(createStudyItem.getValueAsString().equals("Create new study")) {
							miw = new ManualImportWindow(studies,
															cbItemFilterType.getValueAsString(),
															cbItemFilterType.getValueAsString(),
															Integer.parseInt(selectItemProjects.getValue().toString()),
															true,
															fileGrid,
															self);
							miw.show();
						}
						if(createStudyItem.getValueAsString().equals("Import to existing study")) {
							miw = new ManualImportWindow(studies,
															cbItemFilterType.getValueAsString(),
															cbItemFilterType.getValueAsString(),
															Integer.parseInt(selectItemProjects.getValue().toString()),
															false,
															fileGrid,
															self);
							miw.show();
						}
					} else {
						SC.say("You need at least one file to import.");
					}
				}
			}
		});
	
		importOptionsForm.setFields(cbItemFilterType,
									createStudyItem,
									selectItemProjects,
									selectItemTissues,
									selectItemPlatform,
									selectItemGenomeAssembly,
									batchCheckbox,
									importButton);
	
		importOptions.addMember(importOptionsForm);
	
		body.addMember(fileGrid);
		body.addMember(importOptions);
	
		header.addMember(controlsPanel);
		
		pane.addMember(header);	
	
		pane.addMember(body);
	
		this.setPane(pane);
	}
	
	public void loadUploadWindow(){
		
		final Window window = new Window();

		window.setTitle("Upload Files");
		window.setWidth(400);
		window.setHeight(300);
		window.setAlign(Alignment.CENTER);
		
		window.setAutoCenter(true);
		window.setIsModal(true);
		window.setShowModalMask(true);
			
		window.addCloseClickHandler(new com.smartgwt.client.widgets.events.CloseClickHandler(){

			@Override
			public void onCloseClick(CloseClickEvent event) {
				fileGrid.invalidateCache();
				fileGrid.fetchData();
				Window w = (Window) event.getSource();
				w.clear();
			}
		});
		
		MultiUploader defaultUploader = new MultiUploader();
		defaultUploader.setHeight("100%");
		
		defaultUploader.addOnFinishUploadHandler(new OnFinishUploaderHandler() {
		    public void onFinish(IUploader uploader) {
		      if (uploader.getStatus() == Status.SUCCESS) {
		        UploadedInfo info = uploader.getServerInfo();
		      }
		    }
		  });
	
		window.addItem(defaultUploader);
		
		window.show();
	}
	
	public void importData(FoStudy[] foStudy,
			String importType,
			String importSubType,
			boolean createStudy,
			int projectId,
			String tool,
			int importNumber,
			int nofImports){

		final AdminServiceAsync req = (AdminServiceAsync) GWT.create(AdminService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) req;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "AdminService";
		endpoint.setServiceEntryPoint(moduleRelativeURL);
		final AsyncCallback<int[]> callback = new AsyncCallback<int[]>(){
			@Override
			public void onSuccess(int[] result){
				
				/*
				if(window != null){
					window.updateValues(result[0], result[1]);
				}
				 */
				
				importMsgWindow.hide();
				fileGrid.invalidateCache();
				fileGrid.fetchData();
				
			}
			public void onFailure(Throwable caught){
				System.out.println(caught.getMessage());
				SC.say(caught.getMessage());
			}
		};
		
		if(batchCheckbox.getValueAsBoolean()){
			/*
			window = new ProgressWindow(0, nofImports);
			window.addCloseClickHandler(new com.smartgwt.client.widgets.events.CloseClickHandler(){
	
			@Override
				public void onCloseClick(CloseClickEvent event) {
					fileGrid.invalidateCache();
					fileGrid.fetchData();
					Window w = (Window) event.getSource();
					w.clear();
				}
			});
			 */
			importMsgWindow = new Window();
			importMsgWindow.setTitle("BatchImport");
			importMsgWindow.setAlign(Alignment.CENTER);
			importMsgWindow.setWidth("100px");
			
			importMsgWindow.setAutoCenter(true);
			importMsgWindow.setIsModal(true);
			importMsgWindow.setShowModalMask(true);
			Label lbl = new Label("Import Data. This may take upto several minutes.");
			importMsgWindow.addItem(lbl);
			importMsgWindow.show();
		}
		
		req.importData(foStudy,
				importType,
				importSubType,
				createStudy,
				projectId,
				tool,
				importNumber,
				nofImports,
				callback);
	}
}
	