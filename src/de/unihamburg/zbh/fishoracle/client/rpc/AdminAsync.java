package de.unihamburg.zbh.fishoracle.client.rpc;


import com.google.gwt.user.client.rpc.AsyncCallback;

import de.unihamburg.zbh.fishoracle.client.data.User;

public interface AdminAsync {

	void getAllUsers(AsyncCallback<User[]> callback);

}
