package de.unihamburg.zbh.fishoracle.server;

import java.text.ParseException;
import java.util.regex.*;
import java.util.*;

import org.ensembl.datamodel.Location;
import de.unihamburg.zbh.fishoracle.client.rpc.Search;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import de.unihamburg.zbh.fishoracle.server.data.*;
import de.unihamburg.zbh.fishoracle.client.data.Amplicon;
import de.unihamburg.zbh.fishoracle.client.data.GWTImageInfo;
import de.unihamburg.zbh.fishoracle.client.data.Gen;
import de.unihamburg.zbh.fishoracle.client.exceptions.DBQueryException;
import de.unihamburg.zbh.fishoracle.client.exceptions.SearchException;

public class SearchImpl extends RemoteServiceServlet implements Search {

	private static final long serialVersionUID = -6555234092930978494L;
	
	private GWTImageInfo imgInfo;
	
	/**
	 * Searching for genomic data,
	 * visualizing the data and sending it to the client side 
	 * for further processing.
	 * 
	 * @param query The search query that can be  an amplicon stable id, a gene name
	 *         or a karyoband identifier.
	 * @param searchType Determines what data the query represents.
	 * @param intWidth The inner width of the center panel.
	 * 
	 * @return imgInfo image info object that stores additional information of
	 *          the generated image that need to be displayed or further processed
	 *          at the client side.
	 * @throws Exception 
	 * @see GWTImageInfo
	 * */
	public GWTImageInfo generateImage(String query, String searchType, int winWidth) throws Exception {
		
			String servletContext = this.getServletContext().getRealPath("/");
			
			DBQuery db = new DBQuery(servletContext);
			
			Amplicon[] amps = null;
			Location featuresLoc = null;
			
			Date dt = new Date();
			
			System.out.println(dt + " Search: " + query);
			System.out.println(dt + " Search type: " + searchType);
			
			if(searchType.equals("Amplicon Search")){

				
			
				try {
					
					double ampQuery = new Double(query).doubleValue();
					
					featuresLoc = db.getLocationForAmpliconStableId(ampQuery);
					
					
				} catch (DBQueryException e) {
					throw e;
				} catch (Exception e) {
					
					if(e instanceof NumberFormatException){	
						throw new SearchException("The input has to be a number! e.g. 1.01", e.getCause());
					}
				}
				
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

				}
				
				if(mBand.find()){
					
					bandStr = (String) query.subSequence(mBand.start(), mBand.end());

				}
				if(chrStr == null || bandStr == null){
					throw new SearchException("The input for a karyoband has to follow the rule ^\\d{1,2}[p,q]{1}\\d{1,2}\\.?\\d{1,2}?$");
				} else {
					featuresLoc = db.getLocationForKaryoband(chrStr, bandStr);
				}
				
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

	/**
	 * Redraws an image for a given image info object. This is necessary 
	 * when the browser is resized or the user wants to scroll over the chromosome.
	 * 
	 * @param imgInfo image info object that stores additional information of
	 *         the generated image that need to be displayed or further processed
	 *         at the client side.
	 *         
	 * @return imgInfo image info object
	 * @throws Exception 
	 * @throws Exception 
	 * */
	public GWTImageInfo redrawImage(GWTImageInfo imageInfo) throws Exception {
		
		String servletContext = this.getServletContext().getRealPath("/");
		
		String chr = imageInfo.getChromosome();
		int start = imageInfo.getStart();
		int end = imageInfo.getEnd();
		
		Date dt = new Date();
		
		System.out.println(dt + " Redraw range: " + chr + ":" +  start + "-" + end);
		
		DBQuery db = new DBQuery(servletContext);
		
		Amplicon[] amps = null;
		
		Location maxRange = null;
		
		try {
			maxRange = new Location("chromosome:" + chr + ":" + start + "-" + end);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error: " + e.getMessage());
			System.out.println(e.getCause());
			throw e;
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

	/**
	 * Fetches amplicon data for a particular amplicon.
	 * 
	 * @param query The amplicon stable id
	 * 
	 * @return Amplicon
	 * @throws Exception 
	 * */
	public Amplicon getAmpliconInfo(
			String query) throws Exception {
		
		String servletContext = this.getServletContext().getRealPath("/");
		
		Date dt = new Date();
		System.out.println(dt + " Get amplicon data for: " + query);
		
		DBQuery db = new DBQuery(servletContext);
		
		Amplicon  amp = db.getAmpliconInfos(query);
		
		return amp;
	}

	/**
	 * Fetches gene data for a particular gene.
	 * 
	 * @param query The ensembl stable id
	 * 
	 * @return gene
	 * @throws Exception 
	 * */
	public Gen getGeneInfo(String query) throws Exception {
		
		String servletContext = this.getServletContext().getRealPath("/");
		
		Date dt = new Date();
		System.out.println(dt + " Get gene data for: " + query);
		
		DBQuery db = new DBQuery(servletContext);
		
		Gen  gene = db.getGeneInfos(query);
		return gene;
	}

}
