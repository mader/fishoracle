package de.unihamburg.zbh.fishoracle.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.LinkItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ClickEvent;
import com.smartgwt.client.widgets.form.fields.events.ClickHandler;

import de.unihamburg.zbh.fishoracle.client.data.GWTImageInfo;
import de.unihamburg.zbh.fishoracle.client.rpc.SearchService;
import de.unihamburg.zbh.fishoracle.client.rpc.SearchServiceAsync;

public class ExportWindow extends Window {

	private GWTImageInfo imgInfo;
	private TextItem width;
	private ExportWindow self;
	
	public ExportWindow(GWTImageInfo newImgInfo){
		
		this.imgInfo = newImgInfo;
		this.self = this;
		
		this.setTitle("export image as " + newImgInfo.getQuery().getImageType() +  " document");
		this.setAutoCenter(true);
		this.setWidth(220);
		this.setHeight(150);
		
		DynamicForm submitForm = new DynamicForm();
		submitForm.setPadding(25);
		
		width = new TextItem("Image Width");
		width.setWidth(50);
		width.setValue(newImgInfo.getWidth());
		
		ButtonItem submitButton = new ButtonItem("submit");
		
		submitButton.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				
				imgInfo.setWidth(Integer.parseInt(width.getDisplayValue()));
				exportImage(imgInfo);
				self.destroy();
			}});
		
		submitForm.setItems(width, submitButton);
		
		this.addItem(submitForm);
		
		this.show();
		
	}
	
	public void exportImage(GWTImageInfo imgInfo){
		
		final SearchServiceAsync req = (SearchServiceAsync) GWT.create(SearchService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) req;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "SearchService";
		endpoint.setServiceEntryPoint(moduleRelativeURL);
		final AsyncCallback<GWTImageInfo> callback = new AsyncCallback<GWTImageInfo>(){
			public void onSuccess(GWTImageInfo result){
				
				Window window = new Window();
				window.setTitle("export image as " + result.getQuery().getImageType() +  " document");
				window.setAutoCenter(true);
				window.setWidth(180);
				window.setHeight(120);
				
				DynamicForm downloadForm = new DynamicForm();
				downloadForm.setPadding(25);
				
				LinkItem link = new LinkItem();
				link.setValue(result.getImgUrl());
				link.setLinkTitle("download");
				link.setAlign(Alignment.CENTER);
				link.setShowTitle(false);
				
				downloadForm.setItems(link);
				
				window.addItem(downloadForm);
				
				window.show();
			}
			public void onFailure(Throwable caught){
				System.out.println(caught.getMessage());
				SC.say(caught.getMessage());
			}
		};
		req.redrawImage(imgInfo, callback);
	}
}