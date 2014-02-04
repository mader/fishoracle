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