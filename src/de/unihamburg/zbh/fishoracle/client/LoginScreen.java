package de.unihamburg.zbh.fishoracle.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.AnimationEffect;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.AnimationCallback;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.LinkItem;
import com.smartgwt.client.widgets.form.fields.PasswordItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ClickEvent;
import com.smartgwt.client.widgets.form.fields.events.ClickHandler;
import com.smartgwt.client.widgets.form.fields.events.KeyPressEvent;
import com.smartgwt.client.widgets.form.fields.events.KeyPressHandler;
import com.smartgwt.client.widgets.layout.LayoutSpacer;
import com.smartgwt.client.widgets.layout.VLayout;

import de.unihamburg.zbh.fishoracle.client.data.User;
import de.unihamburg.zbh.fishoracle.client.rpc.UserService;
import de.unihamburg.zbh.fishoracle.client.rpc.UserServiceAsync;

public class LoginScreen extends VLayout{

	private LoginScreen ls;
	private MainPanel mp;
	
	private DynamicForm registerForm;
	private TextItem userRegTextItem;
	private TextItem emailRegTextItem;
	private TextItem firstNameRegTextItem;
	private TextItem lastNameRegTextItem;
	private PasswordItem passwordRegItem;
	private PasswordItem passwordReRegItem;
	
	private TextItem userTextItem;
	private PasswordItem passwordItem;
	
	public LoginScreen(MainPanel mainPanel){
		ls = this;
		mp = mainPanel;
		
		VLayout formContainer = new VLayout();
		formContainer.setDefaultLayoutAlign(Alignment.CENTER);
		formContainer.setHeight(100);
		
		/*user login*/
		DynamicForm loginForm = new DynamicForm();
		loginForm.setGroupTitle("FISH Oracle");
		loginForm.setIsGroup(true);
		loginForm.setWidth(300);
		//loginForm.setBorder("1px dotted");
		
		userTextItem = new TextItem();
		userTextItem.setTitle("Username");
		userTextItem.setRequired(true);
		userTextItem.addKeyPressHandler(new KeyPressHandler(){

			@Override
			public void onKeyPress(KeyPressEvent event) {
				if(event.getKeyName().equals("Enter")){
					userLogin(userTextItem.getDisplayValue(), passwordItem.getDisplayValue());
				}
			}
		});
		
		passwordItem = new PasswordItem();
		passwordItem.setTitle("Password");
		passwordItem.setRequired(true);
		passwordItem.addKeyPressHandler(new KeyPressHandler(){

			@Override
			public void onKeyPress(KeyPressEvent event) {
				if(event.getKeyName().equals("Enter")){
					userLogin(userTextItem.getDisplayValue(), passwordItem.getDisplayValue());
				}
			}
		});
		
		ButtonItem logInButton = new ButtonItem("login");
		logInButton.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				userLogin(userTextItem.getDisplayValue(), passwordItem.getDisplayValue());
			}
			
		});
		
		LinkItem registerLink = new LinkItem();
		registerLink.setLinkTitle("register");
		registerLink.setShowTitle(false);
		registerLink.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				if(!registerForm.isVisible()){
					registerForm.animateShow(AnimationEffect.SLIDE);
				} else {
					registerForm.animateHide(AnimationEffect.SLIDE);
				}
			}
		});
		
		
		loginForm.setItems(userTextItem, passwordItem, logInButton, registerLink);
		
		registerForm = new DynamicForm();
		registerForm.setWidth(300);
		registerForm.setGroupTitle("Register");
		registerForm.setIsGroup(true);
		registerForm.hide();
		
		userRegTextItem = new TextItem();
		userRegTextItem.setTitle("Username");
		userRegTextItem.setRequired(true);
		
		emailRegTextItem = new TextItem();
		emailRegTextItem.setTitle("E-Mail");
		emailRegTextItem.setRequired(true);
		
		firstNameRegTextItem = new TextItem();
		firstNameRegTextItem.setTitle("First Name");
		firstNameRegTextItem.setRequired(true);
		
		lastNameRegTextItem = new TextItem();
		lastNameRegTextItem.setTitle("Last Name");
		lastNameRegTextItem.setRequired(true);
		
		passwordRegItem = new PasswordItem();
		passwordRegItem.setTitle("Password");
		passwordRegItem.setRequired(true);
		
		passwordReRegItem = new PasswordItem();
		passwordReRegItem.setTitle("Retype Password");
		passwordReRegItem.setRequired(true);
		
		ButtonItem registerButton = new ButtonItem("register");
		registerButton.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				if(passwordRegItem.getDisplayValue().equals(passwordReRegItem.getDisplayValue())){
					User user = new User(firstNameRegTextItem.getDisplayValue(),
											lastNameRegTextItem.getDisplayValue(),
											userRegTextItem.getDisplayValue(),
											emailRegTextItem.getDisplayValue(),
											passwordRegItem.getDisplayValue(),
											false,
											false);
					registerUser(user);
				} else {
					passwordRegItem.setValue("");
					passwordReRegItem.setValue("");
					SC.say("Your password confirmation did not match with the given password!");
				}
			}
		});
		
		
		registerForm.setItems(userRegTextItem,
								emailRegTextItem,
								firstNameRegTextItem,
								lastNameRegTextItem,
								passwordRegItem,
								passwordReRegItem,
								registerButton);
		
		formContainer.addMember(loginForm);
		formContainer.addMember(registerForm);
		
		this.addMember(new LayoutSpacer());
		this.addMember(formContainer);
		this.addMember(new LayoutSpacer());
	}
	
	/*=============================================================================
	 *||                              RPC Calls                                  ||
	 *=============================================================================
	 * */	
	
	public void userLogin(String userName, String password){
		
		final UserServiceAsync req = (UserServiceAsync) GWT.create(UserService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) req;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "UserService";
		endpoint.setServiceEntryPoint(moduleRelativeURL);
		final AsyncCallback<User> callback = new AsyncCallback<User>(){
			public void onSuccess(User result){
				
				passwordItem.setValue("");
				
				if(result.getIsAdmin()){
					mp.getWestPanel().addSection(mp.getWestPanel().getAdminSection());
				}
				mp.getNorthPanel().getLogout().setTitle(result.getUserName());
				mp.getNorthPanel().getLogout().redraw();
				ls.animateFade(0, new AnimationCallback(){
					@Override
					public void execute(boolean earlyFinish) {
						ls.hide();	
					}
				});
				//MessageBox.hide();
				
			}
			public void onFailure(Throwable caught){
				System.out.println(caught.getMessage());
				//MessageBox.hide();
				SC.say(caught.getMessage());
			}
		};
		req.login(userName, password, callback);
	}	

	public void registerUser(User user){
	
		final UserServiceAsync req = (UserServiceAsync) GWT.create(UserService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) req;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "UserService";
		endpoint.setServiceEntryPoint(moduleRelativeURL);
		final AsyncCallback<User> callback = new AsyncCallback<User>(){
			public void onSuccess(User result){
		
				userRegTextItem.setValue("");
				emailRegTextItem.setValue("");
				firstNameRegTextItem.setValue("");
				lastNameRegTextItem.setValue("");
				passwordRegItem.setValue("");
				passwordReRegItem.setValue("");
				
				registerForm.animateHide(AnimationEffect.SLIDE);
				
				String msg = "Registered! Before you can login with your user name " + result.getUserName() + " your account has to be verified." +
							" We will try to do that as fast as possible.";
			
				SC.say(msg);

			}
			public void onFailure(Throwable caught){
				System.out.println(caught.getMessage());
				SC.say(caught.getMessage());
			}
		};
		req.register(user, callback);
	}
	
}
