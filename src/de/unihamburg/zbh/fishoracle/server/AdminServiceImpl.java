package de.unihamburg.zbh.fishoracle.server;

import java.io.File;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import de.unihamburg.zbh.fishoracle.client.rpc.Admin;

import de.unihamburg.zbh.fishoracle.client.data.Chip;
import de.unihamburg.zbh.fishoracle.client.data.MetaStatus;
import de.unihamburg.zbh.fishoracle.client.data.MicroarrayOptions;
import de.unihamburg.zbh.fishoracle.client.data.Organ;
import de.unihamburg.zbh.fishoracle.client.data.PathologicalGrade;
import de.unihamburg.zbh.fishoracle.client.data.PathologicalStage;
import de.unihamburg.zbh.fishoracle.client.data.User;
import de.unihamburg.zbh.fishoracle.client.exceptions.UserException;
import de.unihamburg.zbh.fishoracle.server.data.DBQuery;

public class AdminServiceImpl extends RemoteServiceServlet implements Admin {

	private static final long serialVersionUID = 4434993420272783276L;

	public User[] getAllUsers() throws Exception {
		
		HttpServletRequest request=this.getThreadLocalRequest();
		HttpSession session=request.getSession();
		
		User user = (User) session.getAttribute("user");
		
		if(!user.getIsAdmin()){
			
			throw new UserException("Permission denied!");
			
		}
		
		String servletContext = this.getServletContext().getRealPath("/");
		
		DBQuery db = new DBQuery(servletContext);
		
		User[] users = db.fetchAllUsers();
		
		return users;
		
	}
	
	public String[] toggleFlag(int id, String flag, String type, int rowNum, int colNum) throws Exception {
	
		HttpServletRequest request=this.getThreadLocalRequest();
		HttpSession session=request.getSession();
		
		User user = (User) session.getAttribute("user");
		
		if(!user.getIsAdmin()){
			
			throw new UserException("Permission denied!");
			
		}
		
		String servletContext = this.getServletContext().getRealPath("/");
		
		DBQuery db = new DBQuery(servletContext);
		
		int isActivated = db.setIsActive(id, flag, type);
		
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
	
	public MicroarrayOptions getMicroarrayOptions() throws Exception{
		
		HttpServletRequest request=this.getThreadLocalRequest();
		HttpSession session=request.getSession();
		
		User user = (User) session.getAttribute("user");
		
		if(!user.getIsAdmin()){
			
			throw new UserException("Permission denied!");
			
		}
		
		String servletContext = this.getServletContext().getRealPath("/");
		
		DBQuery db = new DBQuery(servletContext);
		
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
								String description) throws Exception {
		
		HttpServletRequest request=this.getThreadLocalRequest();
		HttpSession session=request.getSession();
		
		User user = (User) session.getAttribute("user");
		
		if(!user.getIsAdmin()){
			
			throw new UserException("Permission denied!");
			
		}
		
		String servletContext = this.getServletContext().getRealPath("/");
		
		DBQuery db = new DBQuery(servletContext);
		
		int studyId = db.createNewStudy(studyName,
										chipType,
										tissue,
										pstage,
										pgrade,
										metaStatus,
										description,
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
}
