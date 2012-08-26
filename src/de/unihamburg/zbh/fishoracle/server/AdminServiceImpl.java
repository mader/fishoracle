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
import de.unihamburg.zbh.fishoracle_db_api.data.CnSegment;
import de.unihamburg.zbh.fishoracle_db_api.data.Location;
import de.unihamburg.zbh.fishoracle_db_api.data.SNPMutation;
import de.unihamburg.zbh.fishoracle_db_api.data.Study;

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
	
	//TODO ignore hidden files...
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
	public int[] importData(FoStudy foStudy,
								String importType,
								boolean createStudy,
								int projectId,
								String tool,
								int importNumber,
								int nofImports) throws Exception {

		HttpServletRequest request=this.getThreadLocalRequest();
		HttpSession session=request.getSession();
		
		FoUser user = (FoUser) session.getAttribute("user");
		
		String servletContext = this.getServletContext().getRealPath("/");
		
		String fileName =  foStudy.getFiles()[0];
		
		CsvReader reader = new CsvReader(servletContext + "tmp" + System.getProperty("file.separator")  + fileName);
		reader.setDelimiter('\t');
		reader.readHeaders();
		
		Study study = DataTypeConverter.foStudyToStudy(foStudy);
		
		DBInterface db = new DBInterface(servletContext);

		if(importType.equals("Segments")){
		
			CnSegment[] segments;
			ArrayList<CnSegment> segmentContainer = new ArrayList<CnSegment>();
		
			while (reader.readRecord())
			{
				String chr = reader.get("chrom");
				String start = reader.get("loc.start");
				String end = reader.get("loc.end");
				String markers = reader.get("num.mark");
				String segmentMean = reader.get("seg.mean");
			
				Location loc = new Location(0, chr, Integer.parseInt(start), Integer.parseInt(end));
			
				CnSegment segment = new CnSegment(0,
													loc,
													Double.parseDouble(segmentMean),
													Integer.parseInt(markers));
				segmentContainer.add(segment);
		
			}

			reader.close();

			segments = new CnSegment[segmentContainer.size()];
			segmentContainer.toArray(segments);
			
			study.setSegments(segments);
		
		}
		
		if(importType.equals("Mutations")){
			
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
			
				SNPMutation mut = new SNPMutation(0, new Location(0, chr,
													Integer.parseInt(pos),
													Integer.parseInt(pos)),
													dbSnpId,
													ref,
													alt,
													Double.parseDouble(quality),
													somatic,
													confidence,
													tool);
				
				snpContainer.add(mut);
		
			}

			reader.close();

			mutations = new SNPMutation[snpContainer.size()];
			snpContainer.toArray(mutations);
			
			study.setMutations(mutations);
		
		}
		
		study.setUserId(user.getId());
	
		if(createStudy){
			db.createNewStudy(study, projectId);
		} else {
			db.importData(study, importType);
		}
			
	    File f = new File(servletContext + "tmp" + System.getProperty("file.separator") + fileName);

	    if (!f.exists())
	      throw new IllegalArgumentException(
	          "Delete: no such file or directory: " + fileName);

	    if (!f.canWrite())
	      throw new IllegalArgumentException("Delete: write protected: "
	          + fileName);

	    boolean success = f.delete();

	    if (!success){
	      throw new IllegalArgumentException("Delete: deletion failed");
		}
		
		return new int[]{importNumber, nofImports};
	}
}