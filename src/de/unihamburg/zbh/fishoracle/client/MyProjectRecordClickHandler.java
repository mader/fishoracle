package de.unihamburg.zbh.fishoracle.client;

import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.events.RecordClickEvent;
import com.smartgwt.client.widgets.grid.events.RecordClickHandler;

import de.unihamburg.zbh.fishoracle.client.data.FoUser;

public class MyProjectRecordClickHandler implements RecordClickHandler {

	private ListGrid projectStudyGrid;
	private ListGrid projectAccessGrid;
	private FoUser user;
	
	public MyProjectRecordClickHandler(ListGrid projectStudyGrid, ListGrid projectAccessGrid, FoUser user){
		this.projectStudyGrid = projectStudyGrid;
		this.projectAccessGrid = projectAccessGrid;
		this.user = user;
	}
	
	@Override
	public void onRecordClick(RecordClickEvent event) {
		
		String projectId = event.getRecord().getAttribute("projectId");
		
		projectStudyGrid.fetchData(new Criteria("projectId", projectId));
		
		if(user.getIsAdmin()){
			
			projectAccessGrid.fetchData(new Criteria("projectId", projectId));
		}
	}
}