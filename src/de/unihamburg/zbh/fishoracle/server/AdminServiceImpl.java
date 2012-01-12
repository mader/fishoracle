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

import java.io.File;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.csvreader.CsvReader;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import de.unihamburg.zbh.fishoracle.client.rpc.Admin;

import de.unihamburg.zbh.fishoracle.client.data.FoChip;
import de.unihamburg.zbh.fishoracle.client.data.DBConfigData;
import de.unihamburg.zbh.fishoracle.client.data.FoCnSegment;
import de.unihamburg.zbh.fishoracle.client.data.FoGroup;
import de.unihamburg.zbh.fishoracle.client.data.FoMicroarraystudy;
import de.unihamburg.zbh.fishoracle.client.data.FoProject;
import de.unihamburg.zbh.fishoracle.client.data.FoProjectAccess;
import de.unihamburg.zbh.fishoracle.client.data.FoProperty;
import de.unihamburg.zbh.fishoracle.client.data.MicroarrayOptions;
import de.unihamburg.zbh.fishoracle.client.data.FoOrgan;
import de.unihamburg.zbh.fishoracle.client.data.FoUser;
import de.unihamburg.zbh.fishoracle.client.exceptions.UserException;
import de.unihamburg.zbh.fishoracle.server.data.DBConfig;
import de.unihamburg.zbh.fishoracle.server.data.DBInterface;
import de.unihamburg.zbh.fishoracle_db_api.data.CnSegment;
import de.unihamburg.zbh.fishoracle_db_api.data.Microarraystudy;
import de.unihamburg.zbh.fishoracle_db_api.data.User;

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
	
	public FoUser[] getAllUsers() throws Exception {
		
		isAdmin();
		
		String servletContext = this.getServletContext().getRealPath("/");
		
		DBInterface db = new DBInterface(servletContext);
		
		FoUser[] users = db.getAllUsers();
		
		return users;
		
	}
	
	public String[] toggleFlag(int id, String flag, String type, int rowNum, int colNum) throws Exception {
	
		isAdmin();
		
		String servletContext = this.getServletContext().getRealPath("/");
		
		DBInterface db = new DBInterface(servletContext);
		
		int isActivated = 0;
		
		if(type.equals("isAdmin")){
			isActivated = db.setAdminStatus(id, Boolean.parseBoolean(flag));
		}
		if(type.equals("isActive")){
			isActivated = db.setActiveStatus(id,Boolean.parseBoolean(flag));
		}
		
		String isActivatedStr;
		
		if(isActivated == 0){
			isActivatedStr = "false";
		} else {
			isActivatedStr = "true";
		}
		
		String rowNumStr = new Integer(rowNum).toString();
		
		String[] toggeledUser = {type, isActivatedStr, rowNumStr};
		
		return toggeledUser;
		
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
	public FoOrgan addOrgan(FoOrgan foOrgan) throws UserException {
		
		isAdmin();
		String servletContext = this.getServletContext().getRealPath("/");
		
		DBInterface db = new DBInterface(servletContext);
		
		return db.addOrgan(foOrgan);
	}
	
	@Override
	public FoCnSegment[] getCnSegmentsForMstudyId(int mstudyId) {
		
		String servletContext = this.getServletContext().getRealPath("/");
		
		DBInterface db = new DBInterface(servletContext); 
		
		return db.getCnSegmentsForMstudyId(mstudyId);
	}
	
	@Override
	public void removeMstudy(int mstudyId) {
		
		
		String servletContext = this.getServletContext().getRealPath("/");
		
		DBInterface db = new DBInterface(servletContext);
		
		db.removeMstudy(mstudyId);
	}
	
	@Override
	public FoMicroarraystudy[] getMicorarrayStudiesForProject(int projectId) {
		
		String servletContext = this.getServletContext().getRealPath("/");
		
		DBInterface db = new DBInterface(servletContext); 
		
		return db.getMicroarraystudiesForProject(projectId);
	}
	
	@Override
	public FoOrgan[] getAllFoOrgans() throws UserException {
		
		isAdmin();
		String servletContext = this.getServletContext().getRealPath("/");
		
		DBInterface db = new DBInterface(servletContext);
		
		return db.getAllOrgans();
	}
	
	@Override
	public String[] getAllOrganTypes() {
		String servletContext = this.getServletContext().getRealPath("/");
		
		DBInterface db = new DBInterface(servletContext);
		
		return db.getOrganTypes();
	}
	
	@Override
	public FoProperty addProperty(FoProperty foProperty) throws UserException {
		
		isAdmin();
		String servletContext = this.getServletContext().getRealPath("/");
		
		DBInterface db = new DBInterface(servletContext);
		
		return db.addProperty(foProperty);
	}
	
	@Override
	public FoProperty[] getAllFoProperties() throws UserException {
		
		isAdmin();
		String servletContext = this.getServletContext().getRealPath("/");
		
		DBInterface db = new DBInterface(servletContext);
		
		return db.getAllProperties();
	}
	
	@Override
	public String[] getAllPropertyTypes() {
		
		String servletContext = this.getServletContext().getRealPath("/");
		
		DBInterface db = new DBInterface(servletContext);
		
		return db.getPropertyTypes();
	}
	
	@Override
	public FoChip addChip(FoChip foChip) throws UserException {
		
		isAdmin();
		String servletContext = this.getServletContext().getRealPath("/");
		
		DBInterface db = new DBInterface(servletContext);
		
		return db.addChip(foChip);
	}
	
	@Override
	public String[] getAllChipTypes() {
		String servletContext = this.getServletContext().getRealPath("/");
		
		DBInterface db = new DBInterface(servletContext);
		
		return db.getChipTypes();
	}
	
	@Override
	public FoChip[] getAllFoChips() throws UserException {
		
		isAdmin();
		String servletContext = this.getServletContext().getRealPath("/");
		
		DBInterface db = new DBInterface(servletContext);
		
		return db.getAllChips();
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
	public FoProject[] getFoProjects() throws Exception {
		String servletContext = this.getServletContext().getRealPath("/");
		
		DBInterface db = new DBInterface(servletContext);
		
		FoUser u = getSessionUserObject();
		
		FoProject[] projects = null;
		 
		if(u.getIsAdmin()){
			
			projects = db.getAllProjects();
		} else {
			projects = db.getProjectsForUser(u);
		}
		
		return projects;
	}
	
	@Override
	public FoProject addFoProject(FoProject foProject) throws UserException {
		
		isAdmin();
		String servletContext = this.getServletContext().getRealPath("/");
		
		DBInterface db = new DBInterface(servletContext);
		
		return db.addFoProject(foProject);
	}
	
	@Override
	public boolean removeFoProject(int projectId) throws UserException {
		
		isAdmin();
		String servletContext = this.getServletContext().getRealPath("/");
		
		DBInterface db = new DBInterface(servletContext);
		
		db.removeProject(projectId);
		
		return true;
	}
	
	@Override
	public FoGroup[] getAllGroupsExceptFoProject(FoProject foProject) {
		String servletContext = this.getServletContext().getRealPath("/");
		
		DBInterface db = new DBInterface(servletContext);
		
		return db.getAllGroupsExceptProject(foProject);
	}
	
	@Override
	public FoProjectAccess addAccessToFoProject(
			FoProjectAccess foProjectAccess, int projectId) throws UserException {
		
		isAdmin();
		String servletContext = this.getServletContext().getRealPath("/");
		
		DBInterface db = new DBInterface(servletContext);
		
		return db.addAccessToProject(foProjectAccess, projectId);
	}
	
	@Override
	public boolean removeAccessFromFoProject(int projectAccessId) throws UserException {
		
		isAdmin();
		String servletContext = this.getServletContext().getRealPath("/");
		
		DBInterface db = new DBInterface(servletContext);
		
		db.removeAccessFromProject(projectAccessId);
		
		return true;
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
	
	public MicroarrayOptions getMicroarrayOptions() throws Exception{
		
		isAdmin();
		
		String servletContext = this.getServletContext().getRealPath("/");
		
		DBInterface db = new DBInterface(servletContext);
		
		MicroarrayOptions mo = new MicroarrayOptions();
		
	    FoChip[] chips = db.getAllChips();
	    
	    mo.setChips(chips);
	    
	    FoOrgan[] organs = db.getOrgans(true);
	    
	    mo.setOrgans(organs);
	    
	    FoProject[] projects = db.getAllProjects();
	    
	    mo.setProjects(projects);
	    
	    FoProperty[] properties = db.getProperties(true);
	    
	    mo.setProperties(properties);
	    
	    String[] propertyTypes = db.getPropertyTypes();
	    
	    mo.setPropertyTypes(propertyTypes);
	    
		return mo;
	}

	@Override
	public boolean importData(String fileName,
								String studyName,
								int chipId,
								int organId,
								int projectId,
								int[] propertyIds,
								String description) throws Exception {

		HttpServletRequest request=this.getThreadLocalRequest();
		HttpSession session=request.getSession();
		
		FoUser user = (FoUser) session.getAttribute("user");
		
		String servletContext = this.getServletContext().getRealPath("/");
		
		CsvReader reader = new CsvReader(servletContext + "tmp" + System.getProperty("file.separator")  + fileName);
		reader.setDelimiter('\t');
		reader.readHeaders();
		
		/*
		if(!reader.getHeader(0).equals("") || !reader.getHeader(0).equals("ID")){
			throw new Exception("Something is wrong! Check that your file is tab delimited and that " +
					"the first collumn contains either a row index or ID numbering.");
		}
		*/
		
		DBInterface db = new DBInterface(servletContext);

		CnSegment[] segments;
		ArrayList<CnSegment> segmentContainer = new ArrayList<CnSegment>();
		
		while (reader.readRecord())
		{
			String chr = reader.get("chr");
			String start = reader.get("start");
			String end = reader.get("end loc");
			String markers = reader.get("markers");
			String segmentMean = reader.get("segment mean");
			
			CnSegment segment = new CnSegment(0, chr, 
												Integer.parseInt(start), 
												Integer.parseInt(end), 
												Double.parseDouble(segmentMean), 
												Integer.parseInt(markers));
			segmentContainer.add(segment);
		
		}

		reader.close();

		segments = new CnSegment[segmentContainer.size()];
		segmentContainer.toArray(segments);
		
		Microarraystudy mstudy = new Microarraystudy(segments,
													studyName,
													description,
													chipId,
													organId,
													propertyIds,
													user.getId());
		
		int studyId = db.createNewStudy(mstudy, projectId);
			
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
		
		return true;
	}
	
	public boolean canAccessDataImport() throws UserException{
		
		FoUser user = isAdmin();
		
		String servletContext = this.getServletContext().getRealPath("/");
		
		DBInterface db = new DBInterface(servletContext);
		
		boolean access = db.isAccessable("DataImport", user);
		
		return access;
	}
	
	public void unlockDataImport() throws UserException{
		
		FoUser user = isAdmin();
		
		String servletContext = this.getServletContext().getRealPath("/");
		
		DBInterface db = new DBInterface(servletContext);
		
		db.unlockPage("DataImport", user);
	}
}
