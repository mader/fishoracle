package de.unihamburg.zbh.fishoracle.server;

import java.io.IOException;
import java.text.ParseException;
import java.util.regex.*;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import org.ensembl.datamodel.Location;
import de.unihamburg.zbh.fishoracle.client.rpc.Search;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import de.unihamburg.zbh.fishoracle.server.data.*;
import de.unihamburg.zbh.fishoracle.client.data.CopyNumberChange;
import de.unihamburg.zbh.fishoracle.client.data.GWTImageInfo;
import de.unihamburg.zbh.fishoracle.client.data.Gen;
import de.unihamburg.zbh.fishoracle.client.data.Organ;
import de.unihamburg.zbh.fishoracle.client.data.QueryInfo;
import de.unihamburg.zbh.fishoracle.client.data.User;
import de.unihamburg.zbh.fishoracle.client.exceptions.SearchException;
import de.unihamburg.zbh.fishoracle.client.exceptions.UserException;

public class SearchImpl extends RemoteServiceServlet implements Search {

	private static final long serialVersionUID = -6555234092930978494L;
	
	private GWTImageInfo imgInfo;
	
	public void isActiveUser() throws UserException{
		HttpServletRequest request=this.getThreadLocalRequest();
		HttpSession session=request.getSession();
		
		User user = (User) session.getAttribute("user");
		
		if(!user.getIsActive()){
			
			throw new UserException("Permission denied!");
			
		}
	}
	
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
	 *          @see GWTImageInfo
	 * */
	public GWTImageInfo generateImage(QueryInfo query) throws Exception {
		
			isActiveUser();
		
			String servletContext = this.getServletContext().getRealPath("/");
			
			DBQuery db = new DBQuery(servletContext);
			
			Location maxCNCRange = null;
			
			CopyNumberChange[] cncs = null;
			
			Location featuresLoc = null;
			
			Date dt = new Date();
			
			System.out.println(dt + " Search: " + query.getQueryString());
			System.out.println(dt + " Search type: " + query.getSearchType());
			
			if(query.getSearchType().equals("CNC Search")){

				Pattern pid = Pattern.compile("^(CNC)[0-9]+$", Pattern.CASE_INSENSITIVE);
				Matcher mid = pid.matcher(query.getQueryString());
				
				if(mid.find()){
					featuresLoc = db.getLocationForCNCId(query.getQueryString());
				} else {
					
					throw new SearchException("The stable id has to begin with \"CNC\"  followed by a number" +
							"e.g.: CNC100");
					
				}
				
			} else if(query.getSearchType().equals("Gene Search")){

				featuresLoc = db.getLocationForGene(query.getQueryString());
				
			} else if(query.getSearchType().equals("Band Search")){
				
				Pattern pChr = Pattern.compile("^\\d{1,2}");
				Matcher mChr = pChr.matcher(query.getQueryString());
				
				Pattern pBand = Pattern.compile("[p,q]{1}\\d{1,2}\\.?\\d{1,2}?$");
				Matcher mBand = pBand.matcher(query.getQueryString());
				
				String chrStr = null;
				String bandStr = null;
				
				if(mChr.find()){
					
					chrStr = (String) query.getQueryString().subSequence(mChr.start(), mChr.end());

				}
				
				if(mBand.find()){
					
					bandStr = (String) query.getQueryString().subSequence(mBand.start(), mBand.end());

				}
				if(chrStr == null || bandStr == null){
					throw new SearchException("The input for a karyoband has to look like 4q13.3!");
				} else {
					featuresLoc = db.getLocationForKaryoband(chrStr, bandStr);
				}
				
			} else if(query.getSearchType().equals("range")){
				
			}
			
			maxCNCRange = db.getMaxCNCRange(featuresLoc.getSeqRegionName(), 
											featuresLoc.getStart(), 
											featuresLoc.getEnd(), 
											query.getLowerTh(), 
											query.getUpperTh());
			
			maxCNCRange = adjustMaxCNCRange(maxCNCRange, featuresLoc, query.getSearchType());
			
			cncs = db.getCNCData(maxCNCRange.getSeqRegionName(), 
									maxCNCRange.getStart(), 
									maxCNCRange.getEnd(), 
									query.getLowerTh(), 
									query.getUpperTh(),
									query.getOrganFilter());
			
			Gen[] genes = null;
			genes = db.getEnsembleGenes(maxCNCRange.getSeqRegionName(), maxCNCRange.getStart(), maxCNCRange.getEnd());
			
			Karyoband[] band = null;
			band = db.getEnsemblKaryotypes(maxCNCRange.getSeqRegionName(), maxCNCRange.getStart(), maxCNCRange.getEnd());
			
			SketchTool sketch = new SketchTool();
			
			imgInfo = sketch.generateImage(cncs,
										genes,
										band,
										maxCNCRange,
										query.getWinWidth(),
										query.getQueryString(),
										query.getImageType(),
										servletContext);
			
			imgInfo.setChromosome(maxCNCRange.getSeqRegionName());
			imgInfo.setStart(maxCNCRange.getStart());
			imgInfo.setEnd(maxCNCRange.getEnd());
			imgInfo.setQuery(query);
			
		return imgInfo;
	}

	private Location adjustMaxCNCRange(Location maxCNCRange, Location featuresLoc, String searchType){
		
		Location loc = maxCNCRange; 
		
		if(maxCNCRange.getEnd() - maxCNCRange.getStart() == 0){
			
			if(searchType.equals("Band Search")){
			
				loc = featuresLoc;
			
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
				
					loc.setSeqRegionName(featuresLoc.getSeqRegionName());
					loc.setStart(newStart);
					loc.setEnd(newEnd);
			}
		}
		
		if(maxCNCRange.getEnd() - maxCNCRange.getStart() > 20000000){
			
			if(featuresLoc.getStart() - 10000000 < 0){
				loc.setStart(0);
			} else {
				loc.setStart(featuresLoc.getStart() - 10000000);
			}
			if(featuresLoc.getStart() - 10000000 < 0){
				loc.setEnd(featuresLoc.getEnd() + 10000000 - (featuresLoc.getEnd() + 10000000));
			} else {
				loc.setEnd(featuresLoc.getEnd() + 10000000);
			}
			
		}
		
		return loc;
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
		
		isActiveUser();
		
		String servletContext = this.getServletContext().getRealPath("/");
		
		String chr = imageInfo.getChromosome();
		int start = imageInfo.getStart();
		int end = imageInfo.getEnd();
		
		Date dt = new Date();
		
		System.out.println(dt + " Redraw range: " + chr + ":" +  start + "-" + end);
		
		DBQuery db = new DBQuery(servletContext);
		
		CopyNumberChange[] cncs = null;
		
		Location maxRange = null;
		
		try {
			maxRange = new Location("chromosome:" + chr + ":" + start + "-" + end);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error: " + e.getMessage());
			System.out.println(e.getCause());
			throw e;
		}
		
		cncs = db.getCNCData(chr, 
							start, 
							end, 
							imageInfo.getQuery().getLowerTh(), 
							imageInfo.getQuery().getUpperTh(), 
							imageInfo.getQuery().getOrganFilter());
		
		Gen[] genes = null;
		genes = db.getEnsembleGenes(chr, start, end);
		
		
		Karyoband[] band = null;
		band = db.getEnsemblKaryotypes(chr, start, end);
		
		SketchTool sketch = new SketchTool();
		
		imgInfo = sketch.generateImage(cncs,
									genes,
									band,
									maxRange,
									imageInfo.getWidth(),
									imageInfo.getQuery().getQueryString(),
									imageInfo.getQuery().getImageType(),
									servletContext);
		
		imgInfo.setChromosome(maxRange.getSeqRegionName());
		imgInfo.setStart(maxRange.getStart());
		imgInfo.setEnd(maxRange.getEnd());
		imgInfo.setQuery(imageInfo.getQuery());
		
		return imgInfo;
	}

	/**
	 * Fetches amplicon data for a particular amplicon.
	 * 
	 * @param query The amplicon stable id
	 * 
	 * @return Amplicon
	 * @throws Exception 
	 * @throws Exception
	 * */
	public CopyNumberChange getCNCInfo(
			String query) throws Exception {
		
		isActiveUser();
		
		String servletContext = this.getServletContext().getRealPath("/");
		
		Date dt = new Date();
		System.out.println(dt + " Get amplicon data for: " + query);
		
		DBQuery db = new DBQuery(servletContext);
		
		CopyNumberChange  cncData = db.getCNCInfos(query);
		
		return cncData;
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
		
		isActiveUser();
		
		String servletContext = this.getServletContext().getRealPath("/");
		
		Date dt = new Date();
		System.out.println(dt + " Get gene data for: " + query);
		
		DBQuery db = new DBQuery(servletContext);
		
		Gen  gene = db.getGeneInfos(query);
		return gene;
	}
	
	public CopyNumberChange[] getListOfCncs(boolean isAmplicon) throws Exception{
		
		isActiveUser();
		
		String servletContext = this.getServletContext().getRealPath("/");
		
		CopyNumberChange[] cncs = null;
		
		DBQuery db = new DBQuery(servletContext);
		
		//cncs = db.getAllCNCData();
		
		return cncs;
	}
	
	
	public String exportData(GWTImageInfo imageInfo) throws Exception {
		
		isActiveUser();
		
		String servletContext = this.getServletContext().getRealPath("/");
		
		String url = null;
		
		String chr = imageInfo.getChromosome();
		int start = imageInfo.getStart();
		int end = imageInfo.getEnd();
		
		DBQuery db = new DBQuery(servletContext);
		
		CopyNumberChange[] cncs = null;
		
		Location maxRange = null;
		
		try {
			maxRange = new Location("chromosome:" + chr + ":" + start + "-" + end);
		} catch (ParseException e) {
			e.printStackTrace();
			System.out.println("Error: " + e.getMessage());
			System.out.println(e.getCause());
		}
		
		cncs = db.getCNCData(chr, 
							start, 
							end, 
							imageInfo.getQuery().getLowerTh(), 
							imageInfo.getQuery().getUpperTh(), 
							imageInfo.getQuery().getOrganFilter());
		
		Gen[] genes = null;
		genes = db.getEnsembleGenes(chr, start, end);
		
		Export exp = new Export();
		
		try {
			url = exp.exportImageAsExcelDocument(cncs, genes, maxRange, servletContext);
		} catch (RowsExceededException e) {
			e.printStackTrace();
		} catch (WriteException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return url;
	}
	
	public Organ[] getOrganData() throws Exception{
				
		String servletContext = this.getServletContext().getRealPath("/");
		
		DBQuery db = new DBQuery(servletContext);
		
		Organ[] organs = db.fetchAllEnabledOrganData();
		
		return organs;
	}	
}
