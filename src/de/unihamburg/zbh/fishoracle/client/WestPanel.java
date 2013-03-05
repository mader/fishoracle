/*
  Copyright (c) 2009-2012 Malte Mader <mader@zbh.uni-hamburg.de>
  Copyright (c) 2009-2012 Center for Bioinformatics, University of Hamburg

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
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.TreeModelType;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.layout.SectionStack;
import com.smartgwt.client.widgets.layout.SectionStackSection;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;
import com.smartgwt.client.widgets.tree.Tree;
import com.smartgwt.client.widgets.tree.TreeGrid;
import com.smartgwt.client.widgets.tree.TreeNode;
import com.smartgwt.client.widgets.tree.events.NodeClickEvent;
import com.smartgwt.client.widgets.tree.events.NodeClickHandler;

import de.unihamburg.zbh.fishoracle.client.data.DBConfigData;
import de.unihamburg.zbh.fishoracle.client.rpc.Admin;
import de.unihamburg.zbh.fishoracle.client.rpc.AdminAsync;

public class WestPanel extends SectionStack{
	
	private MainPanel mp = null;
	
	ConfigLayout searchContent;
	
	private SectionStackSection adminSection;
	
	public ConfigLayout getSearchContent() {
		return searchContent;
	}

	public void setSearchContent(ConfigLayout searchContent) {
		this.searchContent = searchContent;
	}

	public WestPanel(MainPanel mainPanel) {
		
		mp = mainPanel;
		this.setOverflow(Overflow.AUTO);
		
		SectionStackSection searchSection = new SectionStackSection();
		searchSection.setTitle("Search");
		searchSection.setID("search1");
		searchSection.setExpanded(true);

		searchContent = new ConfigLayout(mp, true);
		searchContent.markForRedraw();
		
		searchSection.setItems(searchContent);
		
		/*data adminstration*/
		
		SectionStackSection dataAdminSection = new SectionStackSection();
		dataAdminSection.setTitle("Data Adminstration");
		dataAdminSection.setExpanded(true);
		
		VLayout dataAdminContent = new VLayout();
		
		TreeGrid dataAdminTreeGrid = new TreeGrid();
		dataAdminTreeGrid.setShowConnectors(true);
		dataAdminTreeGrid.setShowHeader(false);
		
		Tree dataAdminTree = new Tree();  
		dataAdminTree.setModelType(TreeModelType.CHILDREN);  
		dataAdminTree.setRoot(new TreeNode("root", 
							new TreeNode("Data Import"),
							new TreeNode("Manage Projects"),
							new TreeNode("Manage Studies")
							)); 
		
		dataAdminTreeGrid.addNodeClickHandler(new NodeClickHandler(){

			@Override
			public void onNodeClick(NodeClickEvent event) {
				
				if(event.getNode().getName().equals("Manage Projects")){
					boolean exists = false;
					int index = 0;
					
					TabSet centerTabSet = mp.getCenterPanel().getCenterTabSet();
					Tab[] tabs = mp.getCenterPanel().getCenterTabSet().getTabs();
					for(int i=0; i < tabs.length; i++){
						if(tabs[i].getTitle().equals("Manage Projects")){
							exists = true;
							index = i;
						}
					}
					
					if(exists){
						centerTabSet.selectTab(index);
					} else {
						mp.getCenterPanel().getUserObject("ProjectAdminTab");
					}
				}
				
				if(event.getNode().getName().equals("Data Import")){
					boolean exists = false;
					int index = 0;
					
					TabSet centerTabSet = mp.getCenterPanel().getCenterTabSet();
					Tab[] tabs = mp.getCenterPanel().getCenterTabSet().getTabs();
					for(int i=0; i < tabs.length; i++){
						if(tabs[i].getTitle().equals("Data Import")){
							exists = true;
							index = i;
						}
					}
					if(exists){
						centerTabSet.selectTab(index);
					} else {
						mp.getCenterPanel().openDataAdminTab();
					}
				}
				
				if(event.getNode().getName().equals("Manage Studies")){
					boolean exists = false;
					int index = 0;
					
					TabSet centerTabSet = mp.getCenterPanel().getCenterTabSet();
					Tab[] tabs = mp.getCenterPanel().getCenterTabSet().getTabs();
					for(int i=0; i < tabs.length; i++){
						if(tabs[i].getTitle().equals("Manage Studies")){
							exists = true;
							index = i;
						}
					}
					
					if(exists){
						centerTabSet.selectTab(index);
					} else {
						mp.getCenterPanel().getUserObject("StudyAdminTab");
					}
				}
			}
		});
		
		dataAdminTreeGrid.setData(dataAdminTree);
		
		dataAdminContent.addMember(dataAdminTreeGrid);
		
		dataAdminSection.setItems(dataAdminContent);
		
		this.setSections(searchSection,dataAdminSection);
			
	}
	
	public SectionStackSection newAdminSection() {
		
		adminSection = new SectionStackSection();  
		adminSection.setTitle("Admin");
		adminSection.setID("admin1");
		
		VLayout adminContent = new VLayout();
		
		/*administration settings*/
		TreeGrid adminTreeGrid = new TreeGrid();
		adminTreeGrid.setShowConnectors(true);
		adminTreeGrid.setShowHeader(false);
		adminTreeGrid.addNodeClickHandler(new NodeClickHandler(){

			@Override
			public void onNodeClick(NodeClickEvent event) {
				
				if(event.getNode().getName().equals("Show Users")){
					boolean exists = false;
					int index = 0;
					
					TabSet centerTabSet = mp.getCenterPanel().getCenterTabSet();
					Tab[] tabs = mp.getCenterPanel().getCenterTabSet().getTabs();
					for(int i=0; i < tabs.length; i++){
						if(tabs[i].getTitle().equals("Users")){
							exists = true;
							index = i;
						}
					}
					
					if(exists){
						centerTabSet.selectTab(index);
					} else {
						
						mp.getCenterPanel().openUserAdminTab();
					}
				}
				if(event.getNode().getName().equals("Manage Groups")){
					boolean exists = false;
					int index = 0;
					
					TabSet centerTabSet = mp.getCenterPanel().getCenterTabSet();
					Tab[] tabs = mp.getCenterPanel().getCenterTabSet().getTabs();
					for(int i=0; i < tabs.length; i++){
						if(tabs[i].getTitle().equals("Manage Groups")){
							exists = true;
							index = i;
						}
					}
					
					if(exists){
						centerTabSet.selectTab(index);
					} else {
						mp.getCenterPanel().openGroupAdminTab();
					}
				}
				if(event.getNode().getName().equals("Manage Organs")){
					boolean exists = false;
					int index = 0;
					
					TabSet centerTabSet = mp.getCenterPanel().getCenterTabSet();
					Tab[] tabs = mp.getCenterPanel().getCenterTabSet().getTabs();
					for(int i=0; i < tabs.length; i++){
						if(tabs[i].getTitle().equals("Manage Organs")){
							exists = true;
							index = i;
						}
					}
					
					if(exists){
						centerTabSet.selectTab(index);
					} else {
						
						mp.getCenterPanel().openOrganAdminTab();
						
					}
				}
				if(event.getNode().getName().equals("Manage Properties")){
					boolean exists = false;
					int index = 0;
					
					TabSet centerTabSet = mp.getCenterPanel().getCenterTabSet();
					Tab[] tabs = mp.getCenterPanel().getCenterTabSet().getTabs();
					for(int i=0; i < tabs.length; i++){
						if(tabs[i].getTitle().equals("Manage Properties")){
							exists = true;
							index = i;
						}
					}
					
					if(exists){
						centerTabSet.selectTab(index);
					} else {
						mp.getCenterPanel().openPropertyAdminTab();
					}
				}
				if(event.getNode().getName().equals("Manage Platforms")){
					boolean exists = false;
					int index = 0;
					
					TabSet centerTabSet = mp.getCenterPanel().getCenterTabSet();
					Tab[] tabs = mp.getCenterPanel().getCenterTabSet().getTabs();
					for(int i=0; i < tabs.length; i++){
						if(tabs[i].getTitle().equals("Management Platforms")){
							exists = true;
							index = i;
						}
					}
					
					if(exists){
						centerTabSet.selectTab(index);
					} else {
						mp.getCenterPanel().openPlatformAdminTab();
					}
				}
				if(event.getNode().getName().equals("Ensembl Databases")){
					boolean exists = false;
					int index = 0;
					
					TabSet centerTabSet = mp.getCenterPanel().getCenterTabSet();
					Tab[] tabs = mp.getCenterPanel().getCenterTabSet().getTabs();
					for(int i=0; i < tabs.length; i++){
						if(tabs[i].getTitle().equals("Ensembl Databases")){
							exists = true;
							index = i;
						}
					}
					if(exists){
						centerTabSet.selectTab(index);
					} else {
						mp.getCenterPanel().openEnsemblConfigTab();
					}
				}
				if(event.getNode().getName().equals("Database Configuration")){
					boolean exists = false;
					int index = 0;
					
					TabSet centerTabSet = mp.getCenterPanel().getCenterTabSet();
					Tab[] tabs = mp.getCenterPanel().getCenterTabSet().getTabs();
					for(int i=0; i < tabs.length; i++){
						if(tabs[i].getTitle().equals("Database Configuration")){
							exists = true;
							index = i;
						}
					}
					if(exists){
						centerTabSet.selectTab(index);
					} else {
						getDatabaseConnectionData();
					}
				}
			}
			
		});
		
		Tree adminTree = new Tree();  
		adminTree.setModelType(TreeModelType.CHILDREN);  
		adminTree.setRoot(new TreeNode("root",  
							new TreeNode("Show Users"),
							new TreeNode("Manage Groups"),
							new TreeNode("Manage Organs"),
							new TreeNode("Manage Properties"),
							new TreeNode("Manage Platforms"),
							new TreeNode("Ensembl Databases"),
							new TreeNode("Database Configuration")
							)); 
		
		adminTreeGrid.setData(adminTree);
		
		adminContent.addMember(adminTreeGrid);
		
		adminSection.addItem(adminContent);
		
		return adminSection;
	}
	
	/*=============================================================================
	 *||                              RPC Calls                                  ||
	 *=============================================================================
	 * */	
	
	
	public void getDatabaseConnectionData(){
		
		final AdminAsync req = (AdminAsync) GWT.create(Admin.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) req;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "AdminService";
		endpoint.setServiceEntryPoint(moduleRelativeURL);
		final AsyncCallback<DBConfigData> callback = new AsyncCallback<DBConfigData>(){
			@Override
			public void onSuccess(DBConfigData result){
				
				mp.getCenterPanel().openDatabaseConfigTab(result);
				
			}
			public void onFailure(Throwable caught){
				System.out.println(caught.getMessage());
				SC.say(caught.getMessage());
			}
		};
		req.fetchDBConfigData(callback);
	}
}