package de.unihamburg.zbh.fishoracle.client.rpc;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import de.unihamburg.zbh.fishoracle.client.data.User;

@RemoteServiceRelativePath("admin")
public interface Admin extends RemoteService  {

	public User[] getAllUsers() throws Exception;
	public int[] toggleFlag(int id, String flag, int rowIndex, int colIndex) throws Exception;
	
	
	public static class Util {

		public static AdminAsync getInstance() {

			return GWT.create(Admin.class);
		}
	}
}
