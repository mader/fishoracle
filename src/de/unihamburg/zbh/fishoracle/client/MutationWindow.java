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
import com.smartgwt.client.types.GroupStartOpen;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.grid.ListGrid;

import de.unihamburg.zbh.fishoracle.client.data.FoConfigData;
import de.unihamburg.zbh.fishoracle.client.datasource.OperationId;
import de.unihamburg.zbh.fishoracle.client.datasource.SNPMutationDS;

public class MutationWindow extends Window {

	public MutationWindow(String geneId, int trackId, FoConfigData cd){

		Window window = new Window();
		window.setTitle("SNVs");
		window.setWidth(700);
		window.setHeight(330);
		window.setAutoCenter(true);
		window.setCanDragResize(true);
		
		ListGrid mutGrid = new ListGrid();
		mutGrid.setWidth100();
		mutGrid.setHeight100();
		mutGrid.setShowAllRecords(true);
		mutGrid.setAlternateRecordStyles(true);
		mutGrid.setShowHeader(true);
		mutGrid.setWrapCells(true);
		mutGrid.setFixedRecordHeights(false);
		
		mutGrid.setShowAllRecords(false);
		mutGrid.setAutoFetchData(false);
		
		SNPMutationDS mDS = new SNPMutationDS();
		mDS.addConfigData(cd);
		
		Criteria c = new Criteria("geneId", geneId);
		c.setAttribute("trackId", trackId);
		
		mutGrid.setDataSource(mDS);
		mutGrid.setFetchOperation(OperationId.MUTATION_FETCH_FOR_ATTRIBS);
		mutGrid.fetchData(c);
		
		mutGrid.setGroupStartOpen(GroupStartOpen.ALL);
		mutGrid.groupBy("studyName");
		
		window.addItem(mutGrid);
		
		window.show();
	}
}