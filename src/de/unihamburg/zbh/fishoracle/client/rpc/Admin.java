package de.unihamburg.zbh.fishoracle.client.rpc;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import de.unihamburg.zbh.fishoracle.client.data.MicroarrayOptions;
import de.unihamburg.zbh.fishoracle.client.data.User;
import de.unihamburg.zbh.fishoracle.client.exceptions.UserException;

@RemoteServiceRelativePath("admin")
public interface Admin extends RemoteService  {

	public User[] getAllUsers() throws Exception;
	public String[] toggleFlag(int id, String flag, String type, int rowNum, int colNum) throws Exception;
	public MicroarrayOptions getMicroarrayOptions() throws Exception;
	boolean importData(String fileName,
						String studyName,
						String chipType,
						String tissue,
						String pstage,
						String pgrade,
						String metaStatus,
						String sampleId,
						String description) throws Exception;
	boolean canAccessDataImport() throws UserException;
	void unlockDataImport() throws UserException;
	
	public static class Util {

		public static AdminAsync getInstance() {

			return GWT.create(Admin.class);
		}
	}
}
