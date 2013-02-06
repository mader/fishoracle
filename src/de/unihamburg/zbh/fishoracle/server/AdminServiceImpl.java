/*
  Copyright (c) 2009-2012 Malte Mader <mader@zbh.uni-hamburg.de>
  Copyright (c) 2009-2012 Center for Bioinformatics, University of Hamburg

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

import java.io.File;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.csvreader.CsvReader;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import de.unihamburg.zbh.fishoracle.client.rpc.Admin;

import de.unihamburg.zbh.fishoracle.client.data.DBConfigData;
import de.unihamburg.zbh.fishoracle.client.data.FoGroup;
import de.unihamburg.zbh.fishoracle.client.data.FoStudy;
import de.unihamburg.zbh.fishoracle.client.data.FoProject;
import de.unihamburg.zbh.fishoracle.client.data.FoUser;
import de.unihamburg.zbh.fishoracle.client.exceptions.UserException;
import de.unihamburg.zbh.fishoracle.server.data.DBConfig;
import de.unihamburg.zbh.fishoracle.server.data.DBInterface;
import de.unihamburg.zbh.fishoracle.server.data.DataTypeConverter;
import de.unihamburg.zbh.fishoracle_db_api.data.GenericFeature;
import de.unihamburg.zbh.fishoracle_db_api.data.Location;
import de.unihamburg.zbh.fishoracle_db_api.data.SNPMutation;
import de.unihamburg.zbh.fishoracle_db_api.data.Segment;
import de.unihamburg.zbh.fishoracle_db_api.data.Study;
import de.unihamburg.zbh.fishoracle_db_api.data.Translocation;

public class AdminServiceImpl extends RemoteServiceServlet implements Admin {

	private static final long serialVersionUID = 4434993420272783276L;

	public FoUser isAdmin() throws UserException{
		HttpServletRequest request=this.getThreadLocalRequest();
		HttpSession session=request.getSession();
		
		FoUser user = (FoUser) session.getAttribute("user");
		
		if(!user.getIsAdmin()){
			
			throw new UserException("Permission denied!");
			
		}
		return user;
	}
	
	public FoUser getSessionUserObject(){
		
		HttpServletRequest request=this.getThreadLocalRequest();
		HttpSession session=request.getSession();
		
		FoUser user = (FoUser) session.getAttribute("user");
		
		return user;
	}
	
	public String[] getUploadedFiles() throws Exception {
		
		String servletContext = this.getServletContext().getRealPath("/");
		
		File dir = new File(servletContext + System.getProperty("file.separator") + "tmp");

		String[] children = dir.list();
		String[] fileNames;
		
		if (children == null) {
		    throw new Exception("Directory does not exist.");
		} else {
			
			Pattern pHidden = Pattern.compile("^\\.");
			
			ArrayList<String> fileNamesContainer = new ArrayList<String>();
			
		    for (int i=0; i < children.length; i++) {
		    	Matcher mHidden = pHidden.matcher(children[i]);
		    	
		    	if(!mHidden.find()){
		    		fileNamesContainer.add(children[i]);
		    	}
		    }
		    
		    fileNames = new String[fileNamesContainer.size()];
			
		    fileNamesContainer.toArray(fileNames);
		    
		}
		
		return fileNames;
	}
	
	public void deleteFiles(String[] files) {
		
		String servletContext = this.getServletContext().getRealPath("/");
		
		File dir = new File(servletContext + System.getProperty("file.separator") + "tmp");
		
		for (int i=0; i < files.length; i++) {
	    	
			File f = new File(dir + System.getProperty("file.separator") + files[i]);

	    	if (!f.exists())
	    	throw new IllegalArgumentException("Delete: no such file or directory: " + files[i]);

	    	if (!f.canWrite())
	    	throw new IllegalArgumentException("Delete: write protected: " + files[i]);

	    	boolean success = f.delete();

	    	if (!success){
	    	throw new IllegalArgumentException("Delete: deletion failed");
			}
	    }
	}
	
	@Override
	public FoGroup[] getAllFoGroups() throws Exception {
		isAdmin();
		String servletContext = this.getServletContext().getRealPath("/");
		
		DBInterface db = new DBInterface(servletContext);
		
		return db.getAllGroups();
	}
	
	@Override
	public FoGroup addGroup(FoGroup foGroup) throws UserException {
		isAdmin();
		String servletContext = this.getServletContext().getRealPath("/");
		
		DBInterface db = new DBInterface(servletContext);
		
		return db.addGroup(foGroup);
	}
	
	@Override
	public void deleteGroup(FoGroup foGroup) throws UserException {
		
		isAdmin();
		String servletContext = this.getServletContext().getRealPath("/");
		
		DBInterface db = new DBInterface(servletContext);
		
		db.deleteGroup(foGroup);
		
	}
	
	@Override
	public FoUser[] getAllUsersExceptFoGroup(FoGroup foGroup) throws UserException {
		isAdmin();
		String servletContext = this.getServletContext().getRealPath("/");
		
		DBInterface db = new DBInterface(servletContext);
		
		return db.getAllUserExceptGroup(foGroup);
	}
	
	@Override
	public FoUser addUserToFoGroup(FoGroup foGroup, int userId) throws UserException {
		
		isAdmin();
		String servletContext = this.getServletContext().getRealPath("/");
		
		DBInterface db = new DBInterface(servletContext);
		
		return db.addUserGroup(foGroup, userId);
	}
	
	@Override
	public boolean removeUserFromFoGroup(int groupId, int userId) throws UserException {
		
		isAdmin();
		String servletContext = this.getServletContext().getRealPath("/");
		
		DBInterface db = new DBInterface(servletContext);
		
		db.removeUserFromGroup(groupId, userId);
		
		return true;
	}
	
	@Override
	public FoGroup[] getAllGroupsExceptFoProject(FoProject foProject) {
		String servletContext = this.getServletContext().getRealPath("/");
		
		DBInterface db = new DBInterface(servletContext);
		
		return db.getAllGroupsExceptProject(foProject);
	}
	
	@Override
	public FoUser[] getUsersForGroup(int groupId) throws UserException {
		
		isAdmin();
		String servletContext = this.getServletContext().getRealPath("/");
		
		DBInterface db = new DBInterface(servletContext);
		
		return db.getUsersForGroup(groupId);
	}
	
	public DBConfigData fetchDBConfigData() throws Exception{
		isAdmin();
		String servletContext = this.getServletContext().getRealPath("/");
		DBConfig dbconfig = new DBConfig(servletContext);
		return dbconfig.getConnectionData();
	}
	
	public boolean writeConfigData(DBConfigData dbcdata) throws Exception{
		isAdmin();
		String servletContext = this.getServletContext().getRealPath("/");
		DBConfig dbconfig = new DBConfig();
		dbconfig.setConnectionData(dbcdata);
		dbconfig.setServerPath(servletContext);
		dbconfig.writeConfigDataToFile();
		return true;
	}

	@Override
	public int[] importData(FoStudy[] foStudy,
								String importType,
								String dataSubType,
								boolean createStudy,
								int projectId,
								String tool,
								int importNumber,
								int nofImports) throws Exception {

		HttpServletRequest request=this.getThreadLocalRequest();
		HttpSession session=request.getSession();
		
		FoUser user = (FoUser) session.getAttribute("user");
		
		String servletContext = this.getServletContext().getRealPath("/");
		
		for(int i = 0; i < foStudy.length; i++){
		
			String fileName =  foStudy[i].getFiles()[0];
		
			CsvReader reader = new CsvReader(servletContext + "tmp" + System.getProperty("file.separator")  + fileName);
			reader.setDelimiter('\t');
			reader.readHeaders();
		
			Study study = DataTypeConverter.foStudyToStudy(foStudy[i]);
			int plId = foStudy[i].getPlatformId();
		
			DBInterface db = new DBInterface(servletContext);

			if(importType.equals("Segments (DNACopy)") || 
					importType.equals("Segments (PennCNV)")){
		
				Segment[] segments;
				ArrayList<Segment> segmentContainer = new ArrayList<Segment>();
		
				while (reader.readRecord())
				{
					String chr = reader.get("chrom");
					String start = reader.get("loc.start");
					String end = reader.get("loc.end");
					String mean = "0";
					String markers = "0";
					String status = "-1";
					String statusScore = "-1.0";
							
					if(dataSubType.equals("dnacopy")){
						
						mean = reader.get("seg.mean");
						markers = reader.get("num.mark");
						status = "-1";
						statusScore = "-1.0";
					}
					if(dataSubType.equals("penncnv")){
						
						status = reader.get("cnv.status");
						statusScore = reader.get("status.score");
						mean = "0";
						markers = "0";
					}
			
					Location loc = new Location(chr, Integer.parseInt(start), Integer.parseInt(end));
			
					Segment segment = new Segment(0,
													loc,
													dataSubType);
					
					segment.setMean(Double.parseDouble(mean));
					segment.setNumberOfMarkers(Integer.parseInt(markers));
					
					segment.setStatus(Integer.parseInt(status));
					segment.setStatusScore(Double.parseDouble(statusScore));
					
					segment.setPlatformId(plId);
					
					segmentContainer.add(segment);
		
				}

				reader.close();

				segments = new Segment[segmentContainer.size()];
				segmentContainer.toArray(segments);
			
				study.setSegments(segments);
		
			} else if(importType.equals("Mutations")){
			
				SNPMutation[] mutations;
				ArrayList<SNPMutation> snpContainer = new ArrayList<SNPMutation>();
		
				while (reader.readRecord())
				{
					String chr = reader.get("#CHROM");
					String pos = reader.get("POS");
					String dbSnpId = reader.get("DBSNP_ID");
					String ref = reader.get("REF");
					String alt = reader.get("ALT");
					String quality = reader.get("QUAL");
					String somatic = reader.get("SOMATIC_GERMLINE_CLASSIFICATION");
					String confidence = reader.get("CONFIDENCE");
					
					if(quality.equals("")){
						quality = "100.0";
					}
					
					SNPMutation mut = new SNPMutation(0, new Location(chr,
														Integer.parseInt(pos),
														Integer.parseInt(pos)),
														dbSnpId,
														ref,
														alt,
														Double.parseDouble(quality),
														somatic,
														confidence,
														tool);
					
					mut.setPlatformId(plId);
				
					snpContainer.add(mut);
		
				}

				reader.close();

				mutations = new SNPMutation[snpContainer.size()];
				snpContainer.toArray(mutations);
			
				study.setMutations(mutations);
		
			} else if(importType.equals("Translocations")){
				
				Translocation[][] translocs;
				ArrayList<Translocation[]> translocContainer = new ArrayList<Translocation[]>();
		
				while (reader.readRecord())
				{
					
					Translocation[] transloc = new Translocation[2];
					
					String chr1 = reader.get("CHR1");
					String pos1 = reader.get("POS1");
					String chr2 = reader.get("CHR2");
					String pos2 = reader.get("POS2");
					
					transloc[0] = new Translocation(0, new Location(chr1,
													Integer.parseInt(pos1),
													Integer.parseInt(pos1)),
													0);
					transloc[0].setPlatformId(plId);
					transloc[1] = new Translocation(0, new Location(chr2,
													Integer.parseInt(pos2),
													Integer.parseInt(pos2)),
													0);
					transloc[1].setPlatformId(plId);
					
					translocContainer.add(transloc);
		
				}

				reader.close();

				translocs = new Translocation[translocContainer.size()][];
				translocContainer.toArray(translocs);
			
				study.setTranslocs(translocs);
		
			} else {
				
				GenericFeature[] features;
				ArrayList<GenericFeature> featureContainer = new ArrayList<GenericFeature>();
		
				while (reader.readRecord())
				{
					
					String chr = reader.get("#CHROM");
					String start = reader.get("START");
					String end = reader.get("END");
					
					GenericFeature f = new GenericFeature(0, new Location(chr,
														Integer.parseInt(start),
														Integer.parseInt(end)),
														importType);
					f.setPlatformId(plId);
				
					featureContainer.add(f);
		
				}

				reader.close();

				features = new GenericFeature[featureContainer.size()];
				featureContainer.toArray(features);
			
				study.setFeatures(features);
		
			}
			
			study.setUserId(user.getId());
	
			if(createStudy){
				db.createNewStudy(study, projectId);
			} else {
				db.importData(study, importType);
			}
			
	    	File f = new File(servletContext + "tmp" + System.getProperty("file.separator") + fileName);

	    	if (!f.exists())
	    	throw new IllegalArgumentException("Delete: no such file or directory: " + fileName);

	    	if (!f.canWrite())
	    	throw new IllegalArgumentException("Delete: write protected: " + fileName);

	    	boolean success = f.delete();

	    	if (!success){
	    	throw new IllegalArgumentException("Delete: deletion failed");
			}
	    	
	    	System.out.println(" Imported: " + foStudy[i].getFiles()[0]);
		}
	    
		return new int[]{importNumber, nofImports};
	}
}