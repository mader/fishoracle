package de.unihamburg.zbh.fishoracle.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.gwtext.client.widgets.BoxComponent;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.Component;
import com.gwtext.client.widgets.HTMLPanel;
import com.gwtext.client.widgets.MessageBox;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.TabPanel;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.event.PanelListenerAdapter;
import com.gwtext.client.widgets.form.FormPanel;
import com.gwtext.client.widgets.form.TextField;
import com.gwtext.client.widgets.grid.ColumnConfig;
import com.gwtext.client.widgets.grid.ColumnModel;
import com.gwtext.client.widgets.grid.GridPanel;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.data.ArrayReader;
import com.gwtext.client.data.FieldDef;
import com.gwtext.client.data.MemoryProxy;
import com.gwtext.client.data.RecordDef;
import com.gwtext.client.data.Store;
import com.gwtext.client.data.StringFieldDef;

import de.unihamburg.zbh.fishoracle.client.data.GWTImageInfo;
import de.unihamburg.zbh.fishoracle.client.data.User;
import de.unihamburg.zbh.fishoracle.client.ImgPanel;
import de.unihamburg.zbh.fishoracle.client.rpc.Search;
import de.unihamburg.zbh.fishoracle.client.rpc.SearchAsync;
import de.unihamburg.zbh.fishoracle.client.rpc.UserService;
import de.unihamburg.zbh.fishoracle.client.rpc.UserServiceAsync;

public class CenterPanel extends TabPanel{

	private TabPanel centerPanel = null;
	private MainPanel mp = null;;
	private CenterPanel cp = null;
	
	private TextField userName = null;
	private TextField email = null;
	private TextField firstName = null;
	private TextField lastName = null;
	private TextField pw = null;
	
	public CenterPanel(MainPanel mainPanel) {
		
		mp = mainPanel;
		cp = this;
		
        this.setDeferredRender(false);  
        this.setEnableTabScroll(true);
        this.setActiveTab(0);
        
        Panel startingPanel = new HTMLPanel();
        
        startingPanel.setHtml("" +
        		"<br><center><h1>FISH Oracle</h1> <i>alpha</i></center><br>" +
        		"You can serach for Amplicons by giving an Amplicon Stable ID" +
        		" e.g. '60.01' or for a gene specified by a gene name e.g. 'kras'" +
        		" or a Karyoband giving the exact Karyoband identifier e.g. '8q21.3'." +
        		" By clicking on an element a window opens that shows additional information." +
        		" As the amplicon data is incompatible to the Ensembl version 55 the currently" +
        		" used Ensembl version is 54. If you want to search for a gene in the Ensembl " +
        		" browser you better also use version 54 " +
        		"<a href=\"http://may2009.archive.ensembl.org\" target=_blank>http://may09.archive.ensembl.org</a>" +
        		"<br><br>" +
        		"FISH Oracle uses:<br><br> " +
        		"<li> the Google Web Toolkit <a href=\"http://code.google.com/webtoolkit/\" target=_blank>http://code.google.com/webtoolkit/</a></li>" +
        		"<li> the Ensembl human core database <a href=\"http://www.ensembl.org\" target=_blank>http://www.ensembl.org</a></li>" +
        		"<li> AnnotationSketch of the GenomeTools <a href=\"http://www.genometools.org\" target=_blank>http://www.genometools.org</a></li>" +
        		"</ul>");
        
        startingPanel.setTitle("Welcome");
        startingPanel.setAutoScroll(true);
        startingPanel.setClosable(false);
        
        this.add(startingPanel);
        
        this.addListener(new MyPanelListenerAdapter(this, mp));
        
	}

	public TabPanel getCenterPanel() {
		return centerPanel;
	}

	public void setCenterPanel(TabPanel centerPanel) {
		this.centerPanel = centerPanel;
	}	
	
	private Panel addTab(String name) {
        Panel tab = new Panel();
        tab.setAutoScroll(true);
        tab.setTitle(name);
        tab.setClosable(true);
        return tab;
    }
	
	public Panel openRegisterTab(){
		
		Panel tab = addTab("register");
		
		FormPanel reg = new FormPanel();
		
		reg.setBorder(false);
		reg.setVisible(true);
		
		userName = new TextField();
		userName.setLabel("user name");
		email = new TextField();
		email.setLabel("e-mail");
		firstName = new TextField();
		firstName.setLabel("first name");
		lastName = new TextField();
		lastName.setLabel("lastName");
		pw = new TextField();
		pw.setInputType("password");
		pw.setLabel("password");
		
		Button submit = new Button("submit", new ButtonListenerAdapter(){
			public void onClick(Button button, EventObject e) {     
    			
				User user = new User(firstName.getText(), lastName.getText(), userName.getText(), email.getText(), pw.getText(), false);
				
				registerUser(user);
				
    		}
		});
		
		reg.add(userName);
		reg.add(email);
		reg.add(firstName);
		reg.add(lastName);
		reg.add(pw);
		reg.add(submit);
		
		tab.add(reg);
		
		return tab;
		
		
	}
	
	public Panel openUserAdminTab(User[] users){
		
		Panel tab = addTab("Show all users");
		
			RecordDef recordDef = new RecordDef(  
					new FieldDef[]{  
							new StringFieldDef("id"),  
							new StringFieldDef("firstname"),  
							new StringFieldDef("lastname"),  
							new StringFieldDef("username"),  
							new StringFieldDef("email"),  
							new StringFieldDef("isadmin"),   
					}  
			);  
		
		final GridPanel grid = new GridPanel();  
		
		Object[][] userData = new Object[users.length][];
		int i = 0;
		
		for(i = 0; i < users.length; i++){
			
			userData[i] = new Object[]{users[i].getId(),
										users[i].getFirstName(),
										users[i].getLastName(),
										users[i].getUserName(),
										users[i].getEmail(),
										users[i].getIsAdmin()};
		}
			
		MemoryProxy proxy = new MemoryProxy(userData);  
		
		ArrayReader reader = new ArrayReader(recordDef);  
		Store store = new Store(proxy, reader);  
		store.load();
		grid.setStore(store);  
		
		
		ColumnConfig[] columns = new ColumnConfig[]{  
				new ColumnConfig("id", "id", 30, true, null, "id"),  
				new ColumnConfig("first name", "firstname", 100, true, null, "firstname"),  
				new ColumnConfig("last name", "lastname", 100, true, null, "lastname"),  
				new ColumnConfig("user name", "username", 100, true, null, "username"),  
				new ColumnConfig("e-mail", "email", 100, true, null, "email"),  
				new ColumnConfig("is admin", "isadmin", 60, true)  
		};  
		
		ColumnModel columnModel = new ColumnModel(columns);  
		grid.setColumnModel(columnModel);  
		
		grid.setFrame(true);  
		grid.setStripeRows(true);  
		grid.setAutoExpandColumn("email");  

		grid.setTitle("All users");  
		
		tab.add(grid);
		
		return tab;
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
				
				if(cp.getActiveTab() instanceof ImgPanel){	
				
				ImgPanel imagePanel = (ImgPanel) cp.getActiveTab();
				
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
				
			}
			public void onFailure(Throwable caught){
				System.out.println(caught.getMessage());
				MessageBox.alert(caught.getMessage());
			}
		};
		req.redrawImage(imgInfo, callback);
	}

	public void registerUser(User user){
		
		final UserServiceAsync req = (UserServiceAsync) GWT.create(UserService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) req;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "UserService";
		endpoint.setServiceEntryPoint(moduleRelativeURL);
		final AsyncCallback<User> callback = new AsyncCallback<User>(){
			public void onSuccess(User result){
				
				
				
				Component[] items = cp.getItems();
				for (int i = 0; i < items.length; i++) {  
					Component component = items[i];  
					if (component.getTitle().equals("register")) {  
						cp.remove(component);  
					}
				}
			
				String msg = "Registered! You can now login with your User Name " + result.getUserName();
				
				System.out.println(msg);
				
				MessageBox.alert(msg);
				
			}
			public void onFailure(Throwable caught){
				System.out.println(caught.getMessage());
				MessageBox.alert(caught.getMessage());
			}
		};
		req.register(user, callback);
	}
}

class MyPanelListenerAdapter extends PanelListenerAdapter {
	
	CenterPanel cp=null;
	MainPanel mp=null;
	
	public MyPanelListenerAdapter(CenterPanel centerPanel, MainPanel mainPanel){
		cp = centerPanel;
		mp = mainPanel;
	}
	
	public void onResize(BoxComponent component, int adjWidth, int adjHeight, int rawWidth, int rawHeight){
		if(component.getWidth() >= 150){
			if(cp.getActiveTab() instanceof ImgPanel){	
			ImgPanel imagePanel = (ImgPanel) cp.getActiveTab();
			imagePanel.getImageInfo().setWidth(component.getWidth() - 20);
			cp.imageRedraw(imagePanel.getImageInfo());
			}
		}
	}

}