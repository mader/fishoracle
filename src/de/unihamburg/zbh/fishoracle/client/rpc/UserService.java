package de.unihamburg.zbh.fishoracle.client.rpc;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import de.unihamburg.zbh.fishoracle.client.data.User;

@RemoteServiceRelativePath("user")
public interface UserService extends RemoteService {
	
	public User register(User user);
	public User login(String email, String password);
	public void logout();
	
	public static class Util {

		public static SearchAsync getInstance() {

			return GWT.create(UserService.class);
		}
	}
}
