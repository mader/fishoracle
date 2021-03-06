/*
  Copyright (c) 2009-2014 Malte Mader <mader@zbh.uni-hamburg.de>
  Copyright (c) 2009-2014 Center for Bioinformatics, University of Hamburg

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
import java.util.regex.*;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
import de.unihamburg.zbh.fishoracle.client.rpc.SearchService;
import annotationsketch.FeatureCollection;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import de.unihamburg.zbh.fishoracle.server.data.*;
import de.unihamburg.zbh.fishoracle.client.data.FoConstants;
import de.unihamburg.zbh.fishoracle.client.data.GWTImageInfo;
import de.unihamburg.zbh.fishoracle.client.data.QueryInfo;
import de.unihamburg.zbh.fishoracle.client.data.FoUser;
import de.unihamburg.zbh.fishoracle.client.exceptions.SearchException;
import de.unihamburg.zbh.fishoracle.client.exceptions.UserException;
import de.unihamburg.zbh.fishoracle_db_api.data.Constants;
import de.unihamburg.zbh.fishoracle_db_api.data.Location;
import de.unihamburg.zbh.fishoracle_db_api.data.Translocation;
import extended.RDBMysql;

public class SearchServiceImpl extends RemoteServiceServlet implements SearchService {

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
			
			Location featuresLoc = null;
			
			Date dt = new Date();
			
			System.out.println(dt + " Search: " + query.getQueryString());
			System.out.println(dt + " Search type: " + query.getSearchType());
			
			RDBMysql rdbEnsembl = db.getEnsemblRDB(query.getConfig().getStrArray("ensemblDBName")[0]);
			RDBMysql rdbFishoracle = db.getFishoracleRDB();
			
			if(query.getSearchType().equals("Gene Search")){

				featuresLoc = db.getLocationForGene(rdbEnsembl, query.getQueryString());
				
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
					throw new SearchException("The input for a karyoband has to look e.g. like 4q13.3 or 4q13!");
				} else {
					featuresLoc  = db.getLocationForKaryoband(rdbEnsembl, chrStr, bandStr);
				}
				
			} else if(query.getSearchType().equals("Region")){

				try{
				
					String[] region = query.getQueryString().split(":");
					
					featuresLoc = new Location(region[0],
												Integer.parseInt(region[1]),
												Integer.parseInt(region[2]));
				
				} catch (Exception e){
					e.printStackTrace();
					System.out.println("Error: " + e.getMessage());
					System.out.println(e.getCause());
					throw new Exception("Error: " + e.getMessage());
				}
				
				if(featuresLoc.getEnd() - featuresLoc.getStart() < 10 ){
					
					int addBases = 100000;
					
					if((featuresLoc.getStart() - addBases) < 0 ){
						featuresLoc.setStart(0);
						featuresLoc.setEnd(featuresLoc.getEnd() + 2 * addBases);
					} else {
						featuresLoc.setStart(featuresLoc.getStart() - addBases);
						featuresLoc.setEnd(featuresLoc.getEnd() + addBases);
					}
				}
			}
			
			if(query.getSearchType().equals("Region")){
				maxSegmentRange = featuresLoc;
			} else {
			
			maxSegmentRange = db.getMaxSegmentRange(featuresLoc.getChromosome(), 
											featuresLoc.getStart(), 
											featuresLoc.getEnd(), 
											query);
			
			maxSegmentRange = adjustMaxSegmentRange(maxSegmentRange, featuresLoc, query.getSearchType());
			}
			
			FeatureCollection features = new FeatureCollection();
			
			db.getFeaturesForTracks(rdbFishoracle,
									rdbEnsembl,
									maxSegmentRange.getChromosome(),
									maxSegmentRange.getStart(),
									maxSegmentRange.getEnd(),
									query, features);
			
			if(maxSegmentRange.getEnd() - maxSegmentRange.getStart() < 10000000){
				db.getEnsembleGenes(rdbEnsembl,
									maxSegmentRange.getChromosome(),
									maxSegmentRange.getStart(),
									maxSegmentRange.getEnd(),
									query.getConfig().getStrArray(Constants.ENSEMBL_BIOTYPES),
									features);
			}

			db.getEnsemblKaryotypes(rdbEnsembl, maxSegmentRange.getChromosome(), maxSegmentRange.getStart(), maxSegmentRange.getEnd(), features);
			
			SketchTool sketch = new SketchTool();
			
			imgInfo = sketch.generateImage(features,
										maxSegmentRange,
										query,
										servletContext);
			
			imgInfo.setChromosome(maxSegmentRange.getChromosome());
			imgInfo.setStart(maxSegmentRange.getStart());
			imgInfo.setEnd(maxSegmentRange.getEnd());
			imgInfo.setQuery(query);
			
			features.delete();
			rdbEnsembl.delete();
			rdbFishoracle.delete();
			
		return imgInfo;
	}

	private Location adjustMaxSegmentRange(Location maxCNCRange,
			Location featuresLoc, String searchType){
		
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
				
					loc.setChrosmome(featuresLoc.getChromosome());
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
		
		if(start < 0 || end < 0 || start > end){
			throw new Exception("0 < Start < End required!");
		}
	
		Date dt = new Date();			
		System.out.println(dt + " Redraw range: " + chr + ":" +  start + "-" + end);
		
		DBInterface db = new DBInterface(servletContext);
		
		RDBMysql rdbEnsembl = db.getEnsemblRDB(imageInfo.getQuery().getConfig().getStrArray("ensemblDBName")[0]);
		RDBMysql rdbFishoracle = db.getFishoracleRDB();
		
		FeatureCollection features =  new FeatureCollection();
		
		Location maxRange = null;
		
		try {
			maxRange = new Location(chr, start, end);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error: " + e.getMessage());
			System.out.println(e.getCause());
			throw e;
		}
			
		db.getFeaturesForTracks(rdbFishoracle, rdbEnsembl, chr, start, end, imageInfo.getQuery(), features);
		
		if(end - start < 10000000){
			db.getEnsembleGenes(rdbEnsembl,
								chr,
								start,
								end,
								imageInfo.getQuery().getConfig().getStrArray(Constants.ENSEMBL_BIOTYPES),
								features);
		}
		
		db.getEnsemblKaryotypes(rdbEnsembl, chr, start, end, features);
		
		SketchTool sketch = new SketchTool();
		
		imageInfo.getQuery().setWinWidth(imageInfo.getWidth());
		
		imgInfo = sketch.generateImage(features,
									maxRange,
									imageInfo.getQuery(),
									servletContext);
		
		imgInfo.setChromosome(maxRange.getChromosome());
		imgInfo.setStart(maxRange.getStart());
		imgInfo.setEnd(maxRange.getEnd());
		imgInfo.setQuery(imageInfo.getQuery());
		
		features.delete();
		rdbEnsembl.delete();
		rdbFishoracle.delete();
		
	return imgInfo;
	}
	
	//TODO adapt to new track concept
	public String exportData(GWTImageInfo imageInfo) throws Exception {
			
		isActiveUser();
			
		String servletContext = this.getServletContext().getRealPath("/");
			
		String url = null;
		
		if(imageInfo.getQuery().getConfig().getTracks().length == 1){
			
			if(imageInfo.getQuery().getConfig().getTracks()[0].getStrArray(Constants.DATA_TYPE)[0].equals(FoConstants.CNV_INTENSITY)){
		
				if(!imageInfo.getQuery().getConfig().getStrArray(Constants.SORTED_SEGMENTS)[0].equals("true")){
				
					String chr = imageInfo.getChromosome();
					int start = imageInfo.getStart();
					int end = imageInfo.getEnd();
		
					if(start < 0 || end < 0 || start > end){
						throw new Exception("0 < Start < End required!");
					}
		
					Date dt = new Date();
		
					System.out.println(dt + " Exprot: " + chr + ":" +  start + "-" + end);
		
					DBInterface db = new DBInterface(servletContext);
		
					RDBMysql rdbEnsembl = db.getEnsemblRDB(imageInfo.getQuery().getConfig().getStrArray("ensemblDBName")[0]);
					RDBMysql rdbFishoracle = db.getFishoracleRDB();
		
					FeatureCollection segments =  new FeatureCollection();
					FeatureCollection genes =  new FeatureCollection();
		
					Location maxRange = null;
		
					try {
						maxRange = new Location(chr, start, end);
					} catch (Exception e) {
						e.printStackTrace();
						System.out.println("Error: " + e.getMessage());
						System.out.println(e.getCause());
						throw e;
					}
		
					db.getFeaturesForTracks(rdbFishoracle, rdbEnsembl, chr, start, end, imageInfo.getQuery(), segments);
		
					db.getEnsembleGenes(rdbEnsembl,
									chr,
									start,
									end,
									imageInfo.getQuery().getConfig().getStrArray(Constants.ENSEMBL_BIOTYPES),
									genes);
			
					Export exp = new Export();
			
					try {
						url = exp.exportImageAsExcelDocument(segments, genes, maxRange, servletContext);
					} catch (RowsExceededException e) {
						e.printStackTrace();
					} catch (WriteException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
		
					segments.delete();
					genes.delete();
					rdbEnsembl.delete();
					rdbFishoracle.delete();
		
				} else {
					throw new Exception("Only non sorted segments allowed.");
				}
			} else {
				throw new Exception("Only CNV intensity data allowed.");		
			}
		} else {
			throw new Exception("Only one Track allowed.");
		}
		return url;
	}
	
	@Override
	public QueryInfo updateImgInfoForTranslocationId(int translocId,
			GWTImageInfo imgInfo) throws UserException {
		
		isActiveUser();
		
		String servletContext = this.getServletContext().getRealPath("/");
		
		DBInterface db = new DBInterface(servletContext);
		
		Translocation t = db.getTranslocationForId(translocId);
		
		String qryStr = t.getLocation().getChromosome() + ":" 
				+ t.getLocation().getStart() + ":" + t.getLocation().getEnd();
		
		imgInfo.getQuery().setQueryString(qryStr);
		imgInfo.getQuery().setSearchType("Region");
		
		return imgInfo.getQuery();
	}
}
