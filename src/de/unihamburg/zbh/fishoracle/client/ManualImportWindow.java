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
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.TextAreaItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.grid.ListGrid;

import de.unihamburg.zbh.fishoracle.client.data.FoConstants;
import de.unihamburg.zbh.fishoracle.client.data.FoStudy;
import de.unihamburg.zbh.fishoracle.client.datasource.OperationId;
import de.unihamburg.zbh.fishoracle.client.datasource.OrganDS;
import de.unihamburg.zbh.fishoracle.client.datasource.PlatformDS;
import de.unihamburg.zbh.fishoracle.client.datasource.ProjectDS;
import de.unihamburg.zbh.fishoracle.client.datasource.PropertyDS;

public class ManualImportWindow extends Window {

	private TextItem textItemStudyName;
	private SelectItem selectItemProjects;
	private SelectItem selectItemTissues;
	private SelectItem selectItemPlatform;
	private SelectItem selectItemGenomeAssembly;
	private TextAreaItem textItemDescription;
	private SelectItem selectItemSNPTool;
	private SelectItem selectItemProperty;
	private ButtonItem submitButton;
	private int fileNumber;
	private int nofFiles;
	private ManualImportWindow self;
	private ListGrid listGrid;
	private ImportTab it;
	
	public ManualImportWindow(final FoStudy[] studies,
							final String importType,
							final String dataSubType,
							final int projectId,
							final boolean createStudy,
							ListGrid lg,
							ImportTab lit){
		super();
		self = this;
		this.listGrid = lg;
		this.it = lit;
		
		fileNumber = 0;
		nofFiles = studies.length;

		this.setTitle("Import file " + (fileNumber + 1) + " of " + studies.length);
		this.setWidth(300);
		this.setHeight(400);
		this.setAlign(Alignment.CENTER);
		
		this.setAutoCenter(true);
		this.setIsModal(true);
		this.setShowModalMask(true);
		
		DynamicForm importOptionsForm = new DynamicForm();
		importOptionsForm.setWidth100();
		importOptionsForm.setHeight100();
		importOptionsForm.setAlign(Alignment.CENTER);
		
		textItemStudyName = new TextItem();
		textItemStudyName.setTitle("Study Name");
		textItemStudyName.setValue(studies[0].getName());
		
		
		selectItemProjects = new SelectItem();
		selectItemProjects.setTitle("Project");
		selectItemProjects.setDisplayField("projectName");
		selectItemProjects.setValueField("projectId");
		selectItemProjects.setAutoFetchData(false);
		ProjectDS pDS = new ProjectDS();
		
		selectItemProjects.setOptionDataSource(pDS);
		selectItemProjects.setOptionOperationId(OperationId.PROJECT_FETCH_ALL);
		
		selectItemProjects.setDefaultValue(projectId);
		
		if(!createStudy){
			selectItemProjects.setVisible(false);
		}
		
		selectItemTissues = new SelectItem();
		selectItemTissues.setTitle("Tissue");
		selectItemTissues.setDisplayField("organNamePlusType");
		selectItemTissues.setValueField("organId");
		selectItemTissues.setAutoFetchData(false);
		OrganDS oDS = new OrganDS();
		
		selectItemTissues.setOptionDataSource(oDS);
		selectItemTissues.setOptionOperationId(OperationId.ORGAN_FETCH_ENABLED);
		
		selectItemTissues.setDefaultValue(studies[0].getOrganId());
		
		if(!createStudy){
			selectItemTissues.setVisible(false);
		}
		
		selectItemPlatform = new SelectItem();
		selectItemPlatform.setTitle("Platform");
		selectItemPlatform.setDisplayField("platformName");
		selectItemPlatform.setValueField("platformId");
		selectItemPlatform.setAutoFetchData(false);
		
		PlatformDS plDS = new PlatformDS();
		
		selectItemPlatform.setOptionDataSource(plDS);
		selectItemPlatform.setOptionOperationId(OperationId.PLATFORM_FETCH_ALL);
		selectItemPlatform.setDefaultValue(studies[0].getPlatformId());
		
		if(!createStudy){
			selectItemPlatform.setVisible(false);
		}
		
		selectItemGenomeAssembly = new SelectItem();
		selectItemGenomeAssembly.setTitle("Genome Assembly");
		selectItemGenomeAssembly.setValueMap("GrCh37", "ncbi36");
		selectItemGenomeAssembly.setDefaultValue(studies[0].getAssembly());
		
		if(!createStudy){
			selectItemGenomeAssembly.setVisible(false);
		}
		
		textItemDescription = new TextAreaItem();
		textItemDescription.setTitle("Description");
		textItemDescription.setDefaultValue("");
		
		if(!createStudy){
			textItemDescription.setVisible(false);
		}
		
		selectItemSNPTool = new SelectItem();
		selectItemSNPTool.setTitle("SNP Tool");
		selectItemSNPTool.setValueMap("gatk", "varscan", "SNVMix", "samtools");
		if(!importType.equals(FoConstants.SNV)){
			selectItemSNPTool.setVisible(false);
		}
		
		selectItemProperty = new SelectItem();
		selectItemProperty.setTitle("Property");
		selectItemProperty.setDisplayField("propertyNamePlusType");
		selectItemProperty.setValueField("propertyId");
		selectItemProperty.setAutoFetchData(false);
		
		PropertyDS prlDS = new PropertyDS();
		
		selectItemProperty.setOptionDataSource(prlDS);
		selectItemProperty.setOptionOperationId(OperationId.PROPERTY_FETCH_ENABLED);
		
		ButtonItem cancelButton = new ButtonItem("cancel");
		cancelButton.setEndRow(false);
		cancelButton.setAlign(Alignment.RIGHT);
		cancelButton.addClickHandler(new com.smartgwt.client.widgets.form.fields.events.ClickHandler(){
			
			@Override
			public void onClick(
					com.smartgwt.client.widgets.form.fields.events.ClickEvent event) {
				
				if(fileNumber + 1 < nofFiles){
					
					fileNumber++;
					
					self.setTitle("Import file " + (fileNumber + 1) + " of " + studies.length);
					
					textItemStudyName.setValue(studies[fileNumber].getName());
					selectItemProjects.setValue(projectId);
					selectItemTissues.setValue(studies[fileNumber].getOrganId());
					selectItemPlatform.setValue(studies[fileNumber].getPlatformId());
					selectItemGenomeAssembly.setValue(studies[fileNumber].getAssembly());
					textItemDescription.setValue("");
					selectItemSNPTool.setValue("");
					selectItemProperty.clearValue();
					
					if(fileNumber + 1 == nofFiles){
						submitButton.setTitle("finish");
					}
					
				} else {
					listGrid.invalidateCache();
					listGrid.fetchData();
					self.clear();
				}	
			}
		});
		
		submitButton = new ButtonItem("next");
		submitButton.setStartRow(false);
		
		submitButton.addClickHandler(new com.smartgwt.client.widgets.form.fields.events.ClickHandler(){
			
			@Override
			public void onClick(
					com.smartgwt.client.widgets.form.fields.events.ClickEvent event) {
				
				if(fileNumber < nofFiles){
					
					FoStudy s = new FoStudy();
					s.setName(textItemStudyName.getValueAsString());
					s.setFiles(studies[fileNumber].getFiles());
					
					if(createStudy){
						s.setAssembly(selectItemGenomeAssembly.getValueAsString());
						s.setDescription(textItemDescription.getValueAsString());
						s.setOrganId(Integer.parseInt(selectItemTissues.getValue().toString()));
						s.setPlatformId(Integer.parseInt(selectItemPlatform.getValue().toString()));
						String[] strPIds = selectItemProperty.getValues();
						int[] intPIds = new int[strPIds.length];
						for(int k=0; k < strPIds.length; k++){
							intPIds[k] = Integer.parseInt(strPIds[k]);
						}
						s.setPropertyIds(intPIds);
					}
					
					it.importData(new FoStudy[]{s},
									importType,
									dataSubType,
									createStudy,
									Integer.parseInt(selectItemProjects.getValue().toString()),
									selectItemSNPTool.getValueAsString(),
									fileNumber + 1,
									nofFiles);
					
					if(fileNumber + 1 == nofFiles){
						listGrid.invalidateCache();
						listGrid.fetchData();
						self.clear();
					}
					
					if(fileNumber + 1 < nofFiles) {
						
						fileNumber++;
					
						self.setTitle("Import file " + (fileNumber + 1) + " of " + studies.length);
					
						textItemStudyName.setValue(studies[fileNumber].getName());
						selectItemProjects.setValue(projectId);
						selectItemTissues.setValue(studies[fileNumber].getOrganId());
						selectItemPlatform.setValue(studies[fileNumber].getPlatformId());
						selectItemGenomeAssembly.setValue(studies[fileNumber].getAssembly());
						textItemDescription.setValue("");
						selectItemSNPTool.setValue("");
						selectItemProperty.clearValue();
					
					}
					
					if(fileNumber + 1 == nofFiles){
						submitButton.setTitle("finish");
					}
				}
			}
		});
		
		importOptionsForm.setFields(
				textItemStudyName,
				selectItemProjects,
				selectItemTissues,
				selectItemPlatform,
				selectItemGenomeAssembly,
				selectItemSNPTool,
				textItemDescription,
				selectItemProperty,
				cancelButton,
				submitButton);
		
		this.addItem(importOptionsForm);
	}
}