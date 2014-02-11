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
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.viewer.DetailViewer;

import de.unihamburg.zbh.fishoracle.client.data.FoConstants;
import de.unihamburg.zbh.fishoracle.client.datasource.EnsemblGeneDS;
import de.unihamburg.zbh.fishoracle.client.datasource.OperationId;
import de.unihamburg.zbh.fishoracle.client.datasource.SegmentDS;

public class FeatureDetailWindow extends Window {

	public FeatureDetailWindow(String featureType, String elementName, String ensemblId){
		
		this.setWidth(300);
		this.setHeight(250);
		this.setAutoCenter(true);
		this.setCanDragResize(true);
		
		Criteria c = new Criteria();
		
		DetailViewer itemViewer = new DetailViewer();
		itemViewer.setCanSelectText(true);
		itemViewer.setAutoFetchData(false);
		
		if(featureType.equals(FoConstants.SEGMENT)){
			
			SegmentDS ds = new SegmentDS();
			itemViewer.setDataSource(ds);
			itemViewer.setFetchOperation(OperationId.SEGMENT_FETCH_FOR_ID);
			c.addCriteria("elementName", elementName);
			this.setTitle("Segment Info");
		}
		
		if(featureType.equals(FoConstants.GENE)){
			
			EnsemblGeneDS ds = new EnsemblGeneDS();
			itemViewer.setDataSource(ds);
			itemViewer.setFetchOperation(OperationId.ENSEMBL_FETCH_GENE_FOR_ID);
			c.addCriteria("elementName", elementName);
			c.addCriteria("ensemblDB", ensemblId);
			this.setTitle("Gene Info");
		}
		
		itemViewer.fetchData(c);
		
		this.addItem(itemViewer);
		this.show();
	}
}