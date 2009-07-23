package de.unihamburg.zbh.fishoracle.server;

import java.text.ParseException;
import java.util.regex.*;
import javax.servlet.*;

import org.eclipse.jdt.internal.compiler.ast.ThisReference;
import org.ensembl.datamodel.Location;
import de.unihamburg.zbh.fishoracle.client.rpc.Search;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import de.unihamburg.zbh.fishoracle.server.data.*;
import de.unihamburg.zbh.fishoracle.client.data.Amplicon;
import de.unihamburg.zbh.fishoracle.client.data.GWTImageInfo;
import de.unihamburg.zbh.fishoracle.client.data.Gen;

public class SearchImpl extends RemoteServiceServlet implements Search {


	/**
	 * 
	 */
	private static final long serialVersionUID = -6555234092930978494L;
	
	private GWTImageInfo imgInfo;
	
	
	

	
	public GWTImageInfo generateImage(String query, String searchType, int winWidth) {
		
			String servletContext = this.getServletContext().getRealPath("/");
		
			System.out.println(servletContext);
			
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
			
			if(maxAmpRange.getEnd() - maxAmpRange.getStart() == 0){
				
				if(searchType.equals("Band Search")){
				
					maxAmpRange = featuresLoc;
				
				}
				if(searchType.equals("Gene Search")){
					
					int perc = 200;
					int percRange;
					int newStart;
					int newEnd;
					int start = featuresLoc.getStart();
					int end = featuresLoc.getEnd();
					
					
					int range = end - start;
					
					percRange = range * perc / 100;
					
					if((start - percRange/2) < 0){
						newStart = 0;
					} else {
						newStart = start - percRange/2; 
					}
					
					if((start - percRange/2) < 0){
						newEnd = end + percRange/2 - (start - percRange/2);
					} else{
						newEnd = end + percRange/2;
					}
					
						maxAmpRange.setSeqRegionName(featuresLoc.getSeqRegionName());
						maxAmpRange.setStart(newStart);
						maxAmpRange.setEnd(newEnd);
				
				}
				
			}
			
			amps = db.getAmpliconData(maxAmpRange.getSeqRegionName(), maxAmpRange.getStart(), maxAmpRange.getEnd());
			
			Gen[] genes = null;
			genes = db.getEnsembleGenes(maxAmpRange.getSeqRegionName(), maxAmpRange.getStart(), maxAmpRange.getEnd());
			
			
			Karyoband[] band = null;
			band = db.getEnsemblKaryotypes(maxAmpRange.getSeqRegionName(), maxAmpRange.getStart(), maxAmpRange.getEnd());
			
			SketchTool sketch = new SketchTool();
			
			imgInfo = sketch.generateImage(amps, genes, band, maxAmpRange, winWidth, query, servletContext);
			
			imgInfo.setChromosome(maxAmpRange.getSeqRegionName());
			imgInfo.setStart(maxAmpRange.getStart());
			imgInfo.setEnd(maxAmpRange.getEnd());
			imgInfo.setQuery(query);
			imgInfo.setSearchType(searchType);
			
		return imgInfo;
	}

	
	public GWTImageInfo redrawImage(GWTImageInfo imageInfo) {
		
		String servletContext = this.getServletContext().getRealPath("/");
		
		String chr = imageInfo.getChromosome();
		int start = imageInfo.getStart();
		int end = imageInfo.getEnd();
		
		DBQuery db = new DBQuery();
		
		Amplicon[] amps = null;
		
		Location maxRange = null;
		
		try {
			maxRange = new Location("chromosome:" + chr + ":" + start + "-" + end);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		amps = db.getAmpliconData(chr, start, end);
		
		Gen[] genes = null;
		genes = db.getEnsembleGenes(chr, start, end);
		
		
		Karyoband[] band = null;
		band = db.getEnsemblKaryotypes(chr, start, end);
		
		SketchTool sketch = new SketchTool();
		
		imgInfo = sketch.generateImage(amps, genes, band, maxRange, imageInfo.getWidth(), imageInfo.getQuery(), servletContext);
		
		imgInfo.setChromosome(maxRange.getSeqRegionName());
		imgInfo.setStart(maxRange.getStart());
		imgInfo.setEnd(maxRange.getEnd());
		imgInfo.setQuery(imageInfo.getQuery());
		imgInfo.setSearchType(imageInfo.getSearchType());
		
		return imgInfo;
	}

	@Override
	public Amplicon getAmpliconInfo(
			String query) {
		
		DBQuery db = new DBQuery();
		
		Amplicon  amp = db.getAmpliconInfos(query);
		
		
		return amp;
	}

	@Override
	public Gen getGeneInfo(String query) {
		
		DBQuery db = new DBQuery();
		
		Gen  gene = db.getGeneInfos(query);
		return gene;
	}

}
