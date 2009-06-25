package de.unihamburg.zbh.fishoracle.server;

import java.util.regex.*;
import org.ensembl.datamodel.Location;
import de.unihamburg.zbh.fishoracle.client.rpc.Search;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import de.unihamburg.zbh.fishoracle.server.data.*;
import de.unihamburg.zbh.fishoracle.client.data.ImageInfo;

public class SearchImpl extends RemoteServiceServlet implements Search {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String imageUrl;
	
	
	public ImageInfo generateImage(String query, String searchType, int winWidth) {
			
			DBQuery db = new DBQuery();
			
			Amplicon[] amps = null;
			Location featuresLoc = null;
			
			
			if(searchType.equals("Amplicon Search")){
			
				double ampQuery = new Double(query).doubleValue();
			
				featuresLoc = db.getLocationForAmpliconStableId(ampQuery);
			
			} else if(searchType.equals("Gene Search")){
				
				featuresLoc = db.getLocationForGene(query);
				
			} else if(searchType.equals("Band Search")){
				
				Pattern pChr = Pattern.compile("^\\d{1,2}");
				Matcher mChr = pChr.matcher(query);
				
				Pattern pBand = Pattern.compile("[p,q]{1}\\d{1,2}\\.?\\d{1,2}?$");
				Matcher mBand = pBand.matcher(query);
				
				String chrStr = null;
				String bandStr = null;
				
				if(mChr.find()){
					
					chrStr = (String) query.subSequence(mChr.start(), mChr.end());
					//System.out.println(chrStr);
				}
				
				if(mBand.find()){
					
					bandStr = (String) query.subSequence(mBand.start(), mBand.end());
					//System.out.println(bandStr);
				}
				featuresLoc = db.getLocationForKaryoband(chrStr, bandStr);
				
			} else if(searchType.equals("range")){
				
			} 
			
			Location maxAmpRange = db.getMaxAmpliconRange(Integer.parseInt(featuresLoc.getSeqRegionName()), featuresLoc.getStart(), featuresLoc.getEnd());
			
			amps = db.getAmpliconData(Integer.parseInt(maxAmpRange.getSeqRegionName()), maxAmpRange.getStart(), maxAmpRange.getEnd());
			
			Gen[] genes = null;
			genes = db.getEnsembleGenes(Integer.parseInt(maxAmpRange.getSeqRegionName()), maxAmpRange.getStart(), maxAmpRange.getEnd());
			
			
			Karyoband[] band = null;
			band = db.getEnsemblKaryotypes(Integer.parseInt(maxAmpRange.getSeqRegionName()), maxAmpRange.getStart(), maxAmpRange.getEnd());
			
			SketchTool sketch = new SketchTool();
			
			imageUrl = sketch.generateImage(amps, genes, band, maxAmpRange, winWidth);
			
			ImageInfo imgInfo = new ImageInfo(imageUrl, 0, winWidth, Integer.parseInt(maxAmpRange.getSeqRegionName()), maxAmpRange.getStart(), maxAmpRange.getEnd(), query, searchType);
			
		return imgInfo;
	}

	
	public String resizeImage(ImageInfo imageInfo) {
		
		int chr = imageInfo.getChromosome();
		int start = imageInfo.getStart();
		int end = imageInfo.getEnd();
		
		DBQuery db = new DBQuery();
		
		Amplicon[] amps = null;
		
		Location maxAmpRange = db.getMaxAmpliconRange(chr, start, end);
		
		amps = db.getAmpliconData(chr, start, end);
		
		Gen[] genes = null;
		genes = db.getEnsembleGenes(chr, start, end);
		
		
		Karyoband[] band = null;
		band = db.getEnsemblKaryotypes(chr, start, end);
		
		SketchTool sketch = new SketchTool();
		
		String imageUrl = sketch.generateImage(amps, genes, band, maxAmpRange, imageInfo.getWidth());
		
		return imageUrl;
	}
	
	

}
