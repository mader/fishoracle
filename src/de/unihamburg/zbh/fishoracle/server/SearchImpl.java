/*
  Copyright (c) 2009-2011 Malte Mader <mader@zbh.uni-hamburg.de>
  Copyright (c) 2009-2011 Center for Bioinformatics, University of Hamburg

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
import de.unihamburg.zbh.fishoracle.client.data.FoCnSegment;
import de.unihamburg.zbh.fishoracle.client.data.GWTImageInfo;
import de.unihamburg.zbh.fishoracle.client.data.Gen;
import de.unihamburg.zbh.fishoracle.client.data.QueryInfo;
import de.unihamburg.zbh.fishoracle.client.data.FoUser;
import de.unihamburg.zbh.fishoracle.client.exceptions.SearchException;
import de.unihamburg.zbh.fishoracle.client.exceptions.UserException;
import de.unihamburg.zbh.fishoracle_db_api.data.CnSegment;

public class SearchImpl extends RemoteServiceServlet implements Search {

	private static final long serialVersionUID = -6555234092930978494L;
	
	private GWTImageInfo imgInfo;
	
	public void isActiveUser() throws UserException{
		HttpServletRequest request=this.getThreadLocalRequest();
		HttpSession session=request.getSession();
		
		FoUser user = (FoUser) session.getAttribute("user");
		
		if(!user.getIsActive()){
			
			throw new UserException("Permission denied!");
			
		}
	}
	
	/**
	 * Searching for genomic data,
	 * visualizing the data and sending it to the client side 
	 * for further processing.
	 * 
	 * @param query The search query that can be a specified region, a gene name
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
			
			DBInterface db = new DBInterface(servletContext);
			
			Location maxSegmentRange = null;
			
			CnSegment[][] segments = null;
			
			Location featuresLoc = null;
			
			Date dt = new Date();
			
			System.out.println(dt + " Search: " + query.getQueryString());
			System.out.println(dt + " Search type: " + query.getSearchType());
				
			if(query.getSearchType().equals("Gene Search")){

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
				
			} else if(query.getSearchType().equals("Region")){

				try{
				featuresLoc = new Location(query.getQueryString());
				} catch (Exception e){
					e.printStackTrace();
					System.out.println("Error: " + e.getMessage());
					System.out.println(e.getCause());
					throw new Exception("Error: " + e.getMessage());
				}
				
				if(featuresLoc.getEnd() - featuresLoc.getStart() < 10 ){
					throw new Exception("The end value must at least be 10 base pairs greater than the start value!");
				}
			}
			
			if(query.getSearchType().equals("Region")){
				maxSegmentRange = featuresLoc;
			} else {
			maxSegmentRange = db.getMaxSegmentRange(featuresLoc.getSeqRegionName(), 
											featuresLoc.getStart(), 
											featuresLoc.getEnd(), 
											query);
			
			maxSegmentRange = adjustMaxSegmentRange(maxSegmentRange, featuresLoc, query.getSearchType());
			}
			
			db.getSegmentsForTracks(maxSegmentRange.getSeqRegionName(), 
												maxSegmentRange.getStart(),
												maxSegmentRange.getEnd(),
												query);
			
			Gen[] genes = null;
			genes = db.getEnsembleGenes(maxSegmentRange.getSeqRegionName(), maxSegmentRange.getStart(), maxSegmentRange.getEnd());
			
			Karyoband[] band = null;
			band = db.getEnsemblKaryotypes(maxSegmentRange.getSeqRegionName(), maxSegmentRange.getStart(), maxSegmentRange.getEnd());
			
			SketchTool sketch = new SketchTool();
			
			imgInfo = sketch.generateImage(segments,
										genes,
										band,
										maxSegmentRange,
										query,
										servletContext);
			
			imgInfo.setChromosome(maxSegmentRange.getSeqRegionName());
			imgInfo.setStart(maxSegmentRange.getStart());
			imgInfo.setEnd(maxSegmentRange.getEnd());
			imgInfo.setQuery(query);
			
		return imgInfo;
	}

	private Location adjustMaxSegmentRange(Location maxCNCRange, Location featuresLoc, String searchType){
		
		Location loc = maxCNCRange; 
			
			if(searchType.equals("Gene Search") && 
				maxCNCRange.getStart() == featuresLoc.getStart() && 
				maxCNCRange.getEnd() == featuresLoc.getEnd()){
				
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
		
		if(maxCNCRange.getEnd() - maxCNCRange.getStart() > 20000000){
			
			if(featuresLoc.getStart() - 10000000 < 0){
				loc.setStart(0);
			} else {
				loc.setStart(featuresLoc.getStart() - 10000000);
			}
			if(featuresLoc.getEnd() - 10000000 < 0){
				loc.setEnd(20000000);
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
		
		DBInterface db = new DBInterface(servletContext);
		
		CnSegment[][] segments = null;
		
		Location maxRange = null;
		
		try {
			maxRange = new Location("chromosome:" + chr + ":" + start + "-" + end);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error: " + e.getMessage());
			System.out.println(e.getCause());
			throw e;
		}
		
		db.getSegmentsForTracks(chr, 
										start, 
										end,
										imageInfo.getQuery());
		
		Gen[] genes = null;
		genes = db.getEnsembleGenes(chr, start, end);
		
		Karyoband[] band = null;
		band = db.getEnsemblKaryotypes(chr, start, end);
		
		SketchTool sketch = new SketchTool();
		
		imageInfo.getQuery().setWinWidth(imageInfo.getWidth());
		
		imgInfo = sketch.generateImage(segments,
									genes,
									band,
									maxRange,
									imageInfo.getQuery(),
									servletContext);
		
		imgInfo.setChromosome(maxRange.getSeqRegionName());
		imgInfo.setStart(maxRange.getStart());
		imgInfo.setEnd(maxRange.getEnd());
		imgInfo.setQuery(imageInfo.getQuery());
		
		return imgInfo;
	}

	/**
	 * Fetches segment data for a particular copy number change.
	 * 
	 * @param query The segment id
	 * 
	 * @return Segment
	 * @throws Exception
	 * @throws Exception
	 * */
	public FoCnSegment getSegmentInfo(
			int segmentId) throws Exception {
		
		isActiveUser();
		
		String servletContext = this.getServletContext().getRealPath("/");
		
		Date dt = new Date();
		System.out.println(dt + " Get segment data for: " + segmentId);
		
		DBInterface db = new DBInterface(servletContext);
		
		FoCnSegment  segmentData = db.getSegmentInfos(segmentId);
		
		return segmentData;
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
		
		DBInterface db = new DBInterface(servletContext);
		
		Gen  gene = db.getGeneInfos(query);
		return gene;
	}
	
	//TODO adapt to new track concept
	public String exportData(GWTImageInfo imageInfo) throws Exception {
		
		/*
		isActiveUser();
		
		String servletContext = this.getServletContext().getRealPath("/");
		
		String url = null;
		
		String chr = imageInfo.getChromosome();
		int start = imageInfo.getStart();
		int end = imageInfo.getEnd();
		
		DBInterface db = new DBInterface(servletContext);
		
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
		*/
		return "";
	}
}
