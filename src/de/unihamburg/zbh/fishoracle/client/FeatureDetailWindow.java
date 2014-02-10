package de.unihamburg.zbh.fishoracle.client;

import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.viewer.DetailViewer;
import com.smartgwt.client.widgets.viewer.DetailViewerField;

import de.unihamburg.zbh.fishoracle.client.datasource.OperationId;
import de.unihamburg.zbh.fishoracle.client.datasource.SegmentDS;

public class FeatureDetailWindow extends Window {

	public FeatureDetailWindow(String featureType, String elementName, String ensemblId){
		
		this.setWidth(500);
		this.setHeight(330);
		this.setAutoCenter(true);
		this.setCanDragResize(true);
		
		DetailViewer itemViewer = new DetailViewer();
		
		itemViewer.setAutoFetchData(false);
		
		
		if(featureType.equals("segments")){
			
			SegmentDS ds = new SegmentDS();
			itemViewer.setDataSource(ds);
			itemViewer.setFetchOperation(OperationId.SEGMENT_FETCH_FOR_ID);
			itemViewer.setCanSelectText(true);
			
			//DetailViewerField ivf = new DetailViewerField("segmentId", "SegmentId");
			
			//itemViewer.setFields(ivf);
			Criteria c = new Criteria();
			c.addCriteria("elementName", elementName);
			c.addCriteria("elementName", elementName);
			//c.addCriteria(field, value);
			itemViewer.fetchData(c);
			this.setTitle("Segment " + itemViewer.getAttribute("segmentId"));
		}
		
		/*
		if(featureType.equals("SNV")){
			
			EnsemblDS ds = new EnsmeblDS();
			itemViewer.setDataSource(ds);
			itemViewer.setFetchOperation();
			this.setTitle("Gene " + gene.getGenName());
			
			
		}
		*/
		//itemViewer.fetchData();
		
		this.addItem(itemViewer);
		this.show();
	}
}