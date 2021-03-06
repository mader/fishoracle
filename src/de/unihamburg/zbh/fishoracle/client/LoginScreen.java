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
import com.smartgwt.client.widgets.form.validator.MatchesFieldValidator;
import com.smartgwt.client.widgets.layout.LayoutSpacer;
import com.smartgwt.client.widgets.layout.VLayout;

import de.unihamburg.zbh.fishoracle.client.data.FoUser;
import de.unihamburg.zbh.fishoracle.client.datasource.UserDS;
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
		loginForm.setAutoFocus(true);
		loginForm.setGroupTitle("FISH Oracle");
		loginForm.setIsGroup(true);
		loginForm.setWidth(300);
		
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
		
		UserDS uDS = new UserDS();
		uDS.getField("userId").setRequired(false);
		uDS.getField("userId").setHidden(true);
		
		registerForm = new DynamicForm();
		registerForm.setDataSource(uDS);
		registerForm.setUseAllDataSourceFields(true);
		registerForm.setWidth(300);
		registerForm.setGroupTitle("Register");
		registerForm.setIsGroup(true);
		registerForm.hide();
		
		userRegTextItem = new TextItem();
		userRegTextItem.setName("userName");
		
		firstNameRegTextItem = new TextItem();
		firstNameRegTextItem.setName("firstName");
		
		lastNameRegTextItem = new TextItem();
		lastNameRegTextItem.setName("lastName");
		
		emailRegTextItem = new TextItem();
		emailRegTextItem.setName("email");
		
		passwordRegItem = new PasswordItem();
		passwordRegItem.setName("pw");
		
		passwordReRegItem = new PasswordItem();
		passwordReRegItem.setTitle("Confirm Password");
		passwordReRegItem.setRequired(true);
		
		MatchesFieldValidator matchesValidator = new MatchesFieldValidator();  
		matchesValidator.setOtherField("pw");
		matchesValidator.setErrorMessage("Passwords do not match!");
		passwordReRegItem.setValidators(matchesValidator);  
		
		ButtonItem registerButton = new ButtonItem("register");
		registerButton.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				
				if(registerForm.validate()){
				
					registerForm.saveData();
				
					registerForm.animateHide(AnimationEffect.SLIDE);
					
					String msg = "Registered! Before you can login with your user name " + userRegTextItem.getDisplayValue() + " your account has to be verified." +
								" We will try to do that as fast as possible.";
			
					SC.say(msg);
					
					registerForm.clearValues();
				}
			}
		});
		
		registerForm.setFields(userRegTextItem,
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
		final AsyncCallback<FoUser> callback = new AsyncCallback<FoUser>(){
			public void onSuccess(FoUser result){
				
				passwordItem.setValue("");
				
				if(result.getIsAdmin()){
					mp.getWestPanel().addSection(mp.getWestPanel().newAdminSection());
				}
				mp.getWestPanel().expandSection(0);
				mp.getNorthPanel().getUserNameLink().setLinkTitle(result.getUserName());
				mp.getNorthPanel().getUserNameLink().redraw();
				
				boolean globalTh = (Boolean)  mp.getWestPanel().getSearchContent().getGlobalThresholdCheckbox().getValue();
				
				mp.getWestPanel().getSearchContent().addTrack(null, globalTh, true, 1);
				ls.animateFade(0, new AnimationCallback(){
					@Override
					public void execute(boolean earlyFinish) {
						ls.hide();
					}
				});
			}
			public void onFailure(Throwable caught){
				System.out.println(caught.getMessage());
				SC.say(caught.getMessage());
			}
		};
		req.login(userName, password, callback);
	}	
}