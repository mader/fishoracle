package de.unihamburg.zbh.fishoracle.client.rpc;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;

import de.unihamburg.zbh.fishoracle.client.data.FoTranslocation;

public interface TranslocationService extends RemoteService {

	FoTranslocation add(FoTranslocation foTranslocation);
	FoTranslocation[][] fetch(int studyId) throws Exception;
	public void update(FoTranslocation transloc);
	public void delete(int translocId);
	
	public static class Util {

		public static TranslocationServiceAsync getInstance() {

			return GWT.create(TranslocationService.class);
		}
	}
	
}
