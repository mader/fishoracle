package de.unihamburg.zbh.fishoracle.client.rpc;


import com.google.gwt.user.client.rpc.AsyncCallback;

import de.unihamburg.zbh.fishoracle.client.data.MicroarrayOptions;
import de.unihamburg.zbh.fishoracle.client.data.User;

public interface AdminAsync {

	void getAllUsers(AsyncCallback<User[]> callback);

	void toggleFlag(int id, String flag, String type, int rowNum, int colNum, AsyncCallback<String[]> callback);

	void getMicroarrayOptions(AsyncCallback<MicroarrayOptions> callback);

	void importData(String fileName,
					String studyName,
					String chipType,
					String tissue,
					String pstage,
					String pgrade,
					String metaStatus,
					String sampleId,
					String description, AsyncCallback<Boolean> callback);

	void canAccessDataImport(AsyncCallback<Boolean> callback);

	void unlockDataImport(AsyncCallback<Void> callback);

}
