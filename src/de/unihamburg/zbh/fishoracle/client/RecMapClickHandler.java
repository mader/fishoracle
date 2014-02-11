/*
  Copyright (c) 2014 Malte Mader <mader@zbh.uni-hamburg.de>
  Copyright (c) 2014 Center for Bioinformatics, University of Hamburg

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
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;

import de.unihamburg.zbh.fishoracle.client.data.FoConfigData;
import de.unihamburg.zbh.fishoracle.client.data.FoConstants;
import de.unihamburg.zbh.fishoracle.client.data.GWTImageInfo;
import de.unihamburg.zbh.fishoracle.client.data.QueryInfo;
import de.unihamburg.zbh.fishoracle.client.data.RecMapInfo;
import de.unihamburg.zbh.fishoracle.client.rpc.SearchService;
import de.unihamburg.zbh.fishoracle.client.rpc.SearchServiceAsync;

public class RecMapClickHandler implements ClickHandler {

	private RecMapInfo recInfo;
	private CenterPanel cp;
	private GWTImageInfo imgInfo;
	
	public RecMapClickHandler(RecMapInfo recmapinfo, GWTImageInfo imgInfo, CenterPanel centerPanel){
		this.recInfo = recmapinfo;
		this.imgInfo = imgInfo;
		this.cp = centerPanel;
	}
	
	public void onClick(ClickEvent event) {
		
		if(recInfo.getType().equals(FoConstants.GENE)){
			new FeatureDetailWindow(FoConstants.GENE, recInfo.getElementName(), imgInfo.getQuery().getConfig().getStrArray("ensemblDBName")[0]);
		}
		
		if(recInfo.getType().equals(FoConstants.SEGMENT)){
			
			new FeatureDetailWindow(FoConstants.SEGMENT, recInfo.getElementName(), null);
		}
		
		if(recInfo.getType().equals(FoConstants.TRANSLOCATION)){
			
			updateImgInfoForTranslocationId(recInfo.getElementName(), imgInfo);
		}
		if(recInfo.getType().equals("mutation_root")){
			FoConfigData cd = imgInfo.getQuery().getConfig();
			
			new MutationWindow(recInfo.getElementName(), recInfo.getTrackNumber(), cd);
		}
	}
	
	public void updateImgInfoForTranslocationId(String query, GWTImageInfo imgInfo){
		
		final SearchServiceAsync req = (SearchServiceAsync) GWT.create(SearchService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) req;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "SearchService";
		endpoint.setServiceEntryPoint(moduleRelativeURL);
		final AsyncCallback<QueryInfo> callback = new AsyncCallback<QueryInfo>(){
			public void onSuccess(QueryInfo query){
				
				cp.getMainPanel().getWestPanel().getSearchContent().search(query);
			}
			public void onFailure(Throwable caught){
				System.out.println(caught.getMessage());
				SC.say(caught.getMessage());
			}
		};
		req.updateImgInfoForTranslocationId(Integer.parseInt(query), imgInfo, callback);
	}	
}