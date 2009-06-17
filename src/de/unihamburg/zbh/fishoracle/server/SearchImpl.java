package de.unihamburg.zbh.fishoracle.server;

import java.util.regex.*;
import org.ensembl.datamodel.Location;
import de.unihamburg.zbh.fishoracle.client.rpc.Search;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import de.unihamburg.zbh.fishoracle.server.data.*;

public class SearchImpl extends RemoteServiceServlet implements Search {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String imageUrl;
	
	
	public String generateImage(String query, String searchType, int winWidth) {
			
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
			
			
			amps = db.getAmpliconData(Integer.parseInt(featuresLoc.getSeqRegionName()), featuresLoc.getStart(), featuresLoc.getEnd());
			
			
			Location maxAmpRange = db.getMaxAmpliconRange(Integer.parseInt(featuresLoc.getSeqRegionName()), featuresLoc.getStart(), featuresLoc.getEnd());
			
			
			Gen[] genes = null;
			genes = db.getEnsembleGenes(Integer.parseInt(maxAmpRange.getSeqRegionName()), maxAmpRange.getStart(), maxAmpRange.getEnd());
			
			
			Karyoband[] band = null;
			band = db.getEnsemblKaryotypes(Integer.parseInt(maxAmpRange.getSeqRegionName()), maxAmpRange.getStart(), maxAmpRange.getEnd());
			
			
			int i;
			int minstart= Integer.MAX_VALUE;
			int maxend = 0;
			for(i=0;i<band.length;i++){
				
				if(minstart > band[i].getStart()){
					
					minstart = band[i].getStart();
				}  
				if(maxend < band[i].getEnd()){
					maxend = band[i].getEnd();
				}
			}
			
			SketchTool sketch = new SketchTool();
			
			imageUrl = sketch.generateImage(amps, genes, band, maxAmpRange, winWidth);
			
		return imageUrl;
	}

}
