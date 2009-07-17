package de.unihamburg.zbh.fishoracle.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.gwtext.client.widgets.BoxComponent;
import com.gwtext.client.widgets.HTMLPanel;
import com.gwtext.client.widgets.MessageBox;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.TabPanel;
import com.gwtext.client.widgets.event.PanelListenerAdapter;

import de.unihamburg.zbh.fishoracle.client.data.GWTImageInfo;
import de.unihamburg.zbh.fishoracle.client.ImgPanel;
import de.unihamburg.zbh.fishoracle.client.rpc.Search;
import de.unihamburg.zbh.fishoracle.client.rpc.SearchAsync;

public class CenterPanel{

	private TabPanel centerPanel = null;
	private MainPanel mp = null;;
	
	public CenterPanel(MainPanel mainPanel) {
		
		mp = mainPanel;
		
		centerPanel = new TabPanel();  
        centerPanel.setDeferredRender(false);  
        centerPanel.setEnableTabScroll(true);
        centerPanel.setActiveTab(0);
        
        Panel startingPanel = new HTMLPanel();
        
        startingPanel.setHtml("<br>Greeting text and information how to use Fish Oracle, loaded from a properties file or a database ...");
        
        startingPanel.setTitle("Welcome");
        startingPanel.setAutoScroll(true);
        startingPanel.setClosable(false);
        
        centerPanel.add(startingPanel);
        
        centerPanel.addListener(new PanelListenerAdapter(){
        	public void onResize(BoxComponent component, int adjWidth, int adjHeight, int rawWidth, int rawHeight){
        		if(component.getWidth() >= 150){
        			ImgPanel imagePanel = (ImgPanel) centerPanel.getActiveTab();
        			imagePanel.getImageInfo().setWidth(component.getWidth() - 20);
        			imageRedraw(imagePanel.getImageInfo());
        		}
        	}
        });
        
	}

	public TabPanel getCenterPanel() {
		return centerPanel;
	}

	public void setCenterPanel(TabPanel centerPanel) {
		this.centerPanel = centerPanel;
	}	
	
	
	/*=============================================================================
	 *||                              RPC Calls                                  ||
	 *=============================================================================
	 * */	
		
		
	public void imageRedraw(GWTImageInfo imgInfo){
			
		final SearchAsync req = (SearchAsync) GWT.create(Search.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) req;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "Search";
		endpoint.setServiceEntryPoint(moduleRelativeURL);
		final AsyncCallback<GWTImageInfo> callback = new AsyncCallback<GWTImageInfo>(){
			public void onSuccess(GWTImageInfo result){
				
				ImgPanel imagePanel = (ImgPanel) centerPanel.getActiveTab();
				
				imagePanel.remove(imagePanel.getImageLayer().getElement().getId(), true);
				
				imagePanel.setImageInfo(result);
				
				imagePanel.getChrBox().setValue(imagePanel.getImageInfo().getChromosome());
				imagePanel.getStartBox().setValue(Integer.toString(imagePanel.getImageInfo().getStart()));
				imagePanel.getEndBox().setValue(Integer.toString(imagePanel.getImageInfo().getEnd()));
				
				AbsolutePanel absp =  mp.createImageLayer(result);
				
				imagePanel.add(absp);
				
				imagePanel.setImageLayer(absp);
				
				imagePanel.doLayout();
				
			}
			public void onFailure(Throwable caught){
				System.out.println(caught.getMessage());
				MessageBox.alert("Nothing found!");
			}
		};
		req.redrawImage(imgInfo, callback);
	}
	
}
