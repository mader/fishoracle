package de.unihamburg.zbh.fishoracle.client;

import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.viewer.DetailViewer;

import de.unihamburg.zbh.fishoracle.client.datasource.SegmentDS;

public class FeatureDetailWindow extends Window {

	public FeatureDetailWindow(String featureType){
		
		this.setWidth(500);
		this.setHeight(330);
		this.setAutoCenter(true);
		this.setCanDragResize(true);
		
		DetailViewer itemViewer = new DetailViewer();
		
		itemViewer.setAutoFetchData(false);
		
		
		if(featureType.equals("Segments")){
			
			SegmentDS ds = new SegmentDS();
			itemViewer.setDataSource(ds);
			itemViewer.setFetchOperation();
			this.setTitle("Segment " + segmentData.getId());
			
		}
		
		/*
		if(featureType.equals("SNV")){
			
			EnsemblDS ds = new EnsmeblDS();
			itemViewer.setDataSource(ds);
			itemViewer.setFetchOperation();
			this.setTitle("Gene " + gene.getGenName());
			
			
		}
		*/
		itemViewer.fetchData();
		
		this.addItem(itemViewer);
		this.show();
	}
}