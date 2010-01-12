package de.unihamburg.zbh.fishoracle.client.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;

import de.unihamburg.zbh.fishoracle.client.data.User;

public interface UserServiceAsync {
	
	public void register(User user, AsyncCallback<User> callback);
	public void login(String email, String password, AsyncCallback<User> callback);
	public void logout(AsyncCallback callback);

}
