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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.csvreader.CsvReader;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import de.unihamburg.zbh.fishoracle.client.rpc.Admin;

import de.unihamburg.zbh.fishoracle.client.data.Chip;
import de.unihamburg.zbh.fishoracle.client.data.DBConfigData;
import de.unihamburg.zbh.fishoracle.client.data.FoGroup;
import de.unihamburg.zbh.fishoracle.client.data.MetaStatus;
import de.unihamburg.zbh.fishoracle.client.data.MicroarrayOptions;
import de.unihamburg.zbh.fishoracle.client.data.Organ;
import de.unihamburg.zbh.fishoracle.client.data.PathologicalGrade;
import de.unihamburg.zbh.fishoracle.client.data.PathologicalStage;
import de.unihamburg.zbh.fishoracle.client.data.FoUser;
import de.unihamburg.zbh.fishoracle.client.exceptions.UserException;
import de.unihamburg.zbh.fishoracle.server.data.DBConfig;
import de.unihamburg.zbh.fishoracle.server.data.DBInterface;

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
		
	    Chip[] chips = db.fetchAllChipData();
		
	    String[] chipNames = new String[chips.length];
	    
	    int i = 0;
	    
	    for(i=0; i < chips.length; i++){
	    	chipNames[i] = chips[i].getChipName();
	    }
	    
	    mo.setChipName(chipNames);
	    
	    Organ[] organs = db.fetchAllEnabledOrganData();
	    
	    String[] organNames = new String[organs.length];
	    
	    for(i=0; i < organs.length; i++){
	    	organNames[i] = organs[i].getLabel();
	    }
	    
	    mo.setTissue(organNames);
	    
	    PathologicalStage[] pstages = db.fetchAllEnabledPathologicalStageData();
	    
	    String[] pstageNames = new String[pstages.length];
	    
	    for(i=0; i < pstages.length; i++){
	    	pstageNames[i] = pstages[i].getLabel();
	    }
	    
	    mo.setPStage(pstageNames);
	    
	    PathologicalGrade[] pgrades = db.fetchAllEnabledPathologicalGradeData();
	    
	    String[] pgradeNames = new String[pgrades.length];
	    
	    for(i=0; i < pgrades.length; i++){
	    	pgradeNames[i] = pgrades[i].getLabel();
	    }
	    
	    mo.setPGrade(pgradeNames);
	    
	    MetaStatus[] mstati = db.fetchAllEnabledMetaStatusData();
	    
	    String[] mstatusNames = new String[mstati.length];
	    
	    for(i=0; i < mstati.length; i++){
	    	mstatusNames[i] = mstati[i].getLabel();
	    }
	    
	    mo.setMetaStatus(mstatusNames);
	    
		return mo;
	}

	@Override
	public boolean importData(String fileName,
								String studyName,
								String chipType,
								String tissue,
								String pstage,
								String pgrade,
								String metaStatus,
								String sampleId,
								String description) throws Exception {
		
		FoUser user = isAdmin();
		
		String servletContext = this.getServletContext().getRealPath("/");
		
		CsvReader reader = new CsvReader(servletContext + "tmp" + System.getProperty("file.separator")  + fileName);
		reader.setDelimiter('\t');
		reader.readHeaders();
		
		if(!reader.getHeader(0).equals("") || !reader.getHeader(0).equals("chr")){
			throw new Exception("Something is wrong! Check that your file is tab delimited and that " +
					"the first collumn contains either a row index  or chromosome numbering.");
		}
		
		DBInterface db = new DBInterface(servletContext);
		
		int studyId = db.createNewStudy(studyName,
										chipType,
										tissue,
										pstage,
										pgrade,
										metaStatus,
										description,
										sampleId,
										user.getId());
		
		
		db.insertCNCs(fileName, servletContext + "tmp" + System.getProperty("file.separator") , studyId);
			
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
