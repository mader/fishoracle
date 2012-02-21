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

package de.unihamburg.zbh.fishoracle.server.data;

import java.sql.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.ensembl.datamodel.CoordinateSystem;
import org.ensembl.datamodel.Gene;
import org.ensembl.datamodel.KaryotypeBand;
import org.ensembl.datamodel.Location;

import org.ensembl.driver.AdaptorException;
import org.ensembl.driver.CoreDriver;
import org.ensembl.driver.CoreDriverFactory;
import org.ensembl.driver.KaryotypeBandAdaptor;

import de.unihamburg.zbh.fishoracle.client.data.CopyNumberChange;
import de.unihamburg.zbh.fishoracle.client.data.DBConfigData;
import de.unihamburg.zbh.fishoracle.client.data.FoChip;
import de.unihamburg.zbh.fishoracle.client.data.FoCnSegment;
import de.unihamburg.zbh.fishoracle.client.data.FoGroup;
import de.unihamburg.zbh.fishoracle.client.data.FoMicroarraystudy;
import de.unihamburg.zbh.fishoracle.client.data.FoOrgan;
import de.unihamburg.zbh.fishoracle.client.data.FoProject;
import de.unihamburg.zbh.fishoracle.client.data.FoProjectAccess;
import de.unihamburg.zbh.fishoracle.client.data.FoProperty;
import de.unihamburg.zbh.fishoracle.client.data.FoTissueSample;
import de.unihamburg.zbh.fishoracle.client.data.FoUser;
import de.unihamburg.zbh.fishoracle.client.data.Gen;
import de.unihamburg.zbh.fishoracle.client.data.QueryInfo;
import de.unihamburg.zbh.fishoracle.client.data.TrackData;
import de.unihamburg.zbh.fishoracle.client.exceptions.DBQueryException;

import de.unihamburg.zbh.fishoracle_db_api.data.Chip;
import de.unihamburg.zbh.fishoracle_db_api.data.CnSegment;
import de.unihamburg.zbh.fishoracle_db_api.data.Group;
import de.unihamburg.zbh.fishoracle_db_api.data.Microarraystudy;
import de.unihamburg.zbh.fishoracle_db_api.data.Organ;
import de.unihamburg.zbh.fishoracle_db_api.data.Project;
import de.unihamburg.zbh.fishoracle_db_api.data.ProjectAccess;
import de.unihamburg.zbh.fishoracle_db_api.data.Property;
import de.unihamburg.zbh.fishoracle_db_api.data.TissueSample;
import de.unihamburg.zbh.fishoracle_db_api.data.User;
import de.unihamburg.zbh.fishoracle_db_api.driver.ChipAdaptor;
import de.unihamburg.zbh.fishoracle_db_api.driver.CnSegmentAdaptor;
import de.unihamburg.zbh.fishoracle_db_api.driver.FODriver;
import de.unihamburg.zbh.fishoracle_db_api.driver.FODriverImpl;
import de.unihamburg.zbh.fishoracle_db_api.driver.GroupAdaptor;
import de.unihamburg.zbh.fishoracle_db_api.driver.MicroarraystudyAdaptor;
import de.unihamburg.zbh.fishoracle_db_api.driver.OrganAdaptor;
import de.unihamburg.zbh.fishoracle_db_api.driver.ProjectAdaptor;
import de.unihamburg.zbh.fishoracle_db_api.driver.PropertyAdaptor;
import de.unihamburg.zbh.fishoracle_db_api.driver.UserAdaptor;

/**
 * Fetches various information from the fish oracle database and gene
 * information from the ensembl database using the ensembl Java API.
 * 
 * */
public class DBInterface {
	
	private DBConfig dbConfig;
	private DBConfigData connectionData;
	
	/**
	 * Initializes the database object by fetching the database connection 
	 * parameters from the database.conf file.
	 * 
	 * @param serverPath should contain the realPath of a servlet context to the 
	 *         database.conf file. e.g.:
	 *         <p> 
	 *         <code>new DBInterface(getServletContext().getRealPath("/"));<code>
	 * 
	 * */
	public DBInterface(String serverPath) {
		dbConfig = new DBConfig(serverPath);
		connectionData = dbConfig.getConnectionData();
	}
	
	/* ENSEMBL INTERFACE*/
	
	/**
	 * Looks up location information (chromosome, start, end) for a gene symbol.
	 * 
	 * @param symbol The gene symbol, that was specified in the search query.
	 * @return		An ensembl API location object storing chromosome, start and end of a gene. 
	 * @throws DBQueryException 
	 * 
	 * */
	public Location getLocationForGene(String symbol) throws DBQueryException{
		Gene gene = null;
		CoreDriver coreDriver;
		try {
			coreDriver = CoreDriverFactory.createCoreDriver(connectionData.getEhost(), connectionData.getEport(), connectionData.getEdb(), connectionData.getEuser(), connectionData.getEpw());
			coreDriver.getConnection();
		
			gene = (Gene) coreDriver.getGeneAdaptor().fetchBySynonym(symbol).get(0);
			
			coreDriver.closeAllConnections();
		
		} catch (AdaptorException e) {
			e.printStackTrace();
			System.out.println("Error: " + e.getMessage());
			System.out.println(e.getCause());
		} catch (Exception e) {
			
			if(e instanceof IndexOutOfBoundsException){
				throw new DBQueryException("Couldn't find gene with gene symbol " + symbol, e.getCause());
			}
		}
		return gene.getLocation();
	}
	
	/** 
	 * Looks up location information (chromosome, start, end) for a karyoband.
	 * 
	 * @param chr The chromosome number
	 * @param band The karyoband
	 * @return		An ensembl API location object storing chromosome, start and end of a chromosome and karyoband. 
	 * @throws DBQueryException 
	 * 
	 * */
	public Location getLocationForKaryoband(String chr, String band) throws DBQueryException{
		CoordinateSystem coordSys = null;
		KaryotypeBand k = null;
		CoreDriver coreDriver;
		try {
			coreDriver = CoreDriverFactory.createCoreDriver(connectionData.getEhost(), connectionData.getEport(), connectionData.getEdb(), connectionData.getEuser(), connectionData.getEpw());
			coreDriver.getConnection();
			
			KaryotypeBandAdaptor kband = coreDriver.getKaryotypeBandAdaptor();
			
			coordSys = coreDriver.getCoordinateSystemAdaptor().fetch("chromosome", null);
			
			k = (KaryotypeBand) kband.fetch(coordSys, chr, band).get(0);
			
			coreDriver.closeAllConnections();
		
		} catch (AdaptorException e) {
			e.printStackTrace();
			System.out.println("Error: " + e.getMessage());
			System.out.println(e.getCause());
		} catch (Exception e) {
			
			if(e instanceof IndexOutOfBoundsException){
				throw new DBQueryException("Couldn't find karyoband " + chr + band, e.getCause());
			}
		}
		return k.getLocation();
	}
	
	/**
	 * Fetch all data for a gene given by an ensembl stable id.
	 * 
	 * @param query Ensembl Stable ID
	 * @return		Gen object containing all gene data.
	 * @throws Exception 
	 * 
	 * */
	public Gen getGeneInfos(String query) throws Exception {
		
		Gen gene = null;
		
		CoreDriver coreDriver;
		try {
			coreDriver = CoreDriverFactory.createCoreDriver(connectionData.getEhost(), connectionData.getEport(), connectionData.getEdb(), connectionData.getEuser(), connectionData.getEpw());
		
			coreDriver.getConnection();
			
			Gene ensGene =  coreDriver.getGeneAdaptor().fetch(query);

			gene = new Gen();
				
				gene.setGenName(ensGene.getDisplayName());
				gene.setChr(ensGene.getLocation().getSeqRegionName());
				gene.setStart(ensGene.getLocation().getStart());
				gene.setEnd(ensGene.getLocation().getEnd());
				gene.setStrand(Integer.toString(ensGene.getLocation().getStrand()));
				gene.setAccessionID(ensGene.getAccessionID());
				gene.setBioType(ensGene.getBioType());
				
				if(ensGene.getDescription() == null){
					gene.setDescription("not available");
				} else {
					gene.setDescription(ensGene.getDescription());
				}
				
				gene.setLength(ensGene.getLocation().getLength());
				
	    coreDriver.closeAllConnections();
		
		} catch (AdaptorException e) {
			e.printStackTrace();			
			System.out.println("Error: " + e.getMessage());
			System.out.println(e.getCause());
			throw e;
		}

		return gene;
	}
	
	/**
	 * For a range on a chromosome an array with all overlapping genes is returned.
	 * 
	 * @param chr chromosome
	 * @param start Starting position on the chromosome.
	 * @param end ending postion on the chromosome.
	 * @return 		Array containing gen objects
	 * 
	 * */
	public Gen[] getEnsembleGenes(String chr, int start, int end){
		
		Gen[] genes = null;
		
		String loc = "chromosome:" + chr + ":" + Integer.toString(start) + "-" + Integer.toString(end);
		
		try {
			
			CoreDriver coreDriver =
				CoreDriverFactory.createCoreDriver(connectionData.getEhost(), connectionData.getEport(), connectionData.getEdb(), connectionData.getEuser(), connectionData.getEpw());

			coreDriver.getConnection();
			
			List<?> ensGenes;
			try {
				ensGenes = coreDriver.getGeneAdaptor().fetch(new Location(loc));
				
				genes = new Gen[ensGenes.size()];
				for (int j = 0; j < ensGenes.size(); j++) {
					Gene g = (Gene) ensGenes.get(j);
					
					genes[j] = new Gen(g.getDisplayName(), 
							           g.getLocation().getSeqRegionName(),
							           g.getLocation().getStart(),
							           g.getLocation().getEnd(),
							           Integer.toString(g.getLocation().getStrand()));
					
					genes[j].setAccessionID(g.getAccessionID());
					
				}
			} catch (ParseException e) {
				e.printStackTrace();
				System.out.println("Error: " + e.getMessage());
				System.out.println(e.getCause());
			}
			
			coreDriver.closeAllConnections();

		} catch (AdaptorException e) {
			e.printStackTrace();
			System.out.println("Error: " + e.getMessage());
			System.out.println(e.getCause());
		}
		return genes;
	}
	
	/**
	 * For a range on a chromosome an array with all overlapping karyobands is returned.
	 * 
	 * @param chr Chromosome
	 * @param start Starting position on the chromosome.
	 * @param end Ending postion on the chromosome.
	 * @return 		Array containing karyoband objects.
	 * 
	 * */
	public Karyoband[] getEnsemblKaryotypes(String chr, int start, int end){

	        Karyoband[] karyoband = null;
		
			CoreDriver coreDriver;
			try {
				coreDriver = CoreDriverFactory.createCoreDriver(connectionData.getEhost(), connectionData.getEport(), connectionData.getEdb(), connectionData.getEuser(), connectionData.getEpw());

				coreDriver.getConnection();
			
				String loc = "chromosome:" + chr + ":" + Long.toString(start) + "-" + Long.toString(end);
				
				KaryotypeBandAdaptor ktba = coreDriver.getKaryotypeBandAdaptor();
				
				List<?> ensChrs; 
				
				ensChrs = ktba.fetch(loc);
			
				karyoband = new Karyoband[ensChrs.size()];
				for (int i = 0; i < ensChrs.size(); i++) {
				
					KaryotypeBand k = (KaryotypeBand) ensChrs.get(i);
								
					karyoband[i] = new Karyoband(k.getLocation().getSeqRegionName(),
												k.getBand(), 
												k.getLocation().getStart(), 
												k.getLocation().getEnd());
				}
				
				coreDriver.closeAllConnections();
			
			} catch (AdaptorException e) {
				e.printStackTrace();
				System.out.println("Error: " + e.getMessage());
				System.out.println(e.getCause());
			}			
			return karyoband;
	}
	
	/* FISH ORACLE INTERFACE */
	
	private FODriver getFoDriver(){
		return new FODriverImpl(connectionData.getFhost(), connectionData.getFdb(), connectionData.getFuser(), connectionData.getFpw(), "3306");
	}
	
	/**
	 * Finds all segments that overlap with a given range on a chromosome and returns the 
	 * maximum range over all overlapping segments as an ensembl location object.
	 * 
	 * @param chr chromosome number
	 * @param start Starting position on the chromosome.
	 * @param end Ending postion on the chromosome.
	 * @param tracks Track data
	 **/
	public Location getMaxSegmentRange(String chr, int start, int end, QueryInfo query){
		
		FODriver driver = getFoDriver();
		CnSegmentAdaptor sa = driver.getCnSegmentAdaptor();
		
		de.unihamburg.zbh.fishoracle_db_api.data.Location maxLoc = null;
		
		for(int i = 0; i < query.getTracks().length; i++){
			
			de.unihamburg.zbh.fishoracle_db_api.data. Location l;
			
			if(query.isGlobalTh()){
				
				l = sa.fetchMaximalOverlappingCnSegmentRange(chr,
															start,
															end,
															query.getGlobalLowerThAsDouble(),
															query.getGlobalUpperThAsDouble(),
															query.getTracks()[i].getProjectIds(),
															query.getTracks()[i].getTissueIds(),
															query.getTracks()[i].getExperimentIds());
			} else {
				l = sa.fetchMaximalOverlappingCnSegmentRange(chr,
															start,
															end,
															query.getTracks()[i].getLowerThAsDouble(),
															query.getTracks()[i].getUpperThasDouble(),
															query.getTracks()[i].getProjectIds(),
															query.getTracks()[i].getTissueIds(),
															query.getTracks()[i].getExperimentIds());
			}
			
			try {
				if(maxLoc != null){
					maxLoc = maxLoc.maximize(l);
				} else {
					maxLoc = l;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		Location loc = null;
		try {
			loc = new Location("chromosome:" + maxLoc.getChrosmome() + ":" + maxLoc.getStart() + "-" + maxLoc.getEnd());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return loc;
	}
	
	public CnSegment[][] getSegmentsForTracks(String chr, int start, int end, QueryInfo query){
		
		FODriver driver = getFoDriver();
		CnSegmentAdaptor sa = driver.getCnSegmentAdaptor();
		
		CnSegment[][] tracks = new CnSegment[query.getTracks().length][];
		
		CnSegment[] segments;
		
		
		for(int i = 0; i < query.getTracks().length; i++){
			
			if(query.isGlobalTh()){
				
				segments = sa.fetchCnSegments(chr,
										start,
										end,
										query.getGlobalLowerThAsDouble(),
										query.getGlobalUpperThAsDouble(),
										query.getTracks()[i].getProjectIds(),
										query.getTracks()[i].getTissueIds(),
										query.getTracks()[i].getExperimentIds());
				
				tracks[i] = segments;
				
			} else {
				segments = sa.fetchCnSegments(chr,
										start,
										end,
										query.getTracks()[i].getLowerThAsDouble(),
										query.getTracks()[i].getUpperThasDouble(),
										query.getTracks()[i].getProjectIds(),
										query.getTracks()[i].getTissueIds(),
										query.getTracks()[i].getExperimentIds());
				tracks[i] = segments;
			}
		}
		return tracks;
	}
	
	public FoCnSegment getSegmentInfos(int segmentId) {
		
		FODriver driver = getFoDriver();
		CnSegmentAdaptor sa = driver.getCnSegmentAdaptor();
		
		CnSegment s = sa.fetchCnSegmentById(segmentId);
		
		return cnSegmentToFoCnSegment(s);
	}
	
	
	public int createNewStudy(Microarraystudy mstudy, int projectId){
		FODriver driver = getFoDriver();
		MicroarraystudyAdaptor ma = driver.getMicroarraystudyAdaptor();
	
		return ma.storeMicroarraystudy(mstudy, projectId);
	}
	
	public FoUser insertUser(FoUser user) throws Exception{
		FODriver driver = getFoDriver();
		UserAdaptor ua = driver.getUserAdaptor();
		
		int userId = ua.storeUser(user.getFirstName(),
								user.getLastName(),
								user.getUserName(),
								user.getEmail(),
								user.getPw(),
								user.getIsActiveAsInt(),
								user.getIsAdminAsInt());
		
		User newUser = ua.fetchUserByID(userId);
		
		return userToFoUser(newUser);
	}
	
	public FoUser getUser(String userName, String password) throws Exception{
		FODriver driver = getFoDriver();
		UserAdaptor ua = driver.getUserAdaptor();
		
		User user = ua.fetchUserForLogin(userName, password);

		return userToFoUser(user);
	}
	
	public FoUser[] getAllUsers(){
		FODriver driver = getFoDriver();
		UserAdaptor ua = driver.getUserAdaptor();
		
		User[] users = ua.fetchAllUsers();
		
		return usersToFoUsers(users);
	}
	
	public int setActiveStatus(int userId, boolean activeFlag){
		FODriver driver = getFoDriver();
		UserAdaptor ua = driver.getUserAdaptor();
		
		return ua.toggleUserActiveStatus(userId, activeFlag);
	}
	
	public int setAdminStatus(int userId, boolean adminFlag){
		FODriver driver = getFoDriver();
		UserAdaptor ua = driver.getUserAdaptor();
		
		return ua.toggleUserAdminStatus(userId, adminFlag);
	}
	
	public FoGroup[] getAllGroups() throws Exception{
		FODriver driver = getFoDriver();
		GroupAdaptor ga = driver.getGroupAdaptor();
		
		Group[] groups = ga.fetchAllGroups();
		
		return groupsToFoGroups(groups);
	}
	
	public FoGroup addGroup(FoGroup foGroup){
		FODriver driver = getFoDriver();
		GroupAdaptor ga = driver.getGroupAdaptor();
		
		int newGroupId = ga.storeGroup(foGroup.getName(), foGroup.getIsactiveAsInt());
		
		Group newGroup = ga.fetchGroupById(newGroupId);
		
		return groupToFoGroup(newGroup);
		
	}
	
	public void deleteGroup(FoGroup foGroup){
		FODriver driver = getFoDriver();
		GroupAdaptor ga = driver.getGroupAdaptor();
		
		Group g = foGroupToGroup(foGroup);
		
		ga.deleteGroup(g);
	}
	
	
	public FoUser[] getAllUserExceptGroup(FoGroup foGroup){
		FODriver driver = getFoDriver();
		UserAdaptor ua = driver.getUserAdaptor();
		
		User[] users = ua.fetchAllUsersNotInGroup(foGroup.getId());
		
		return usersToFoUsers(users);
	}
	
	public FoUser addUserGroup(FoGroup foGroup, int userId){
		FODriver driver = getFoDriver();
		GroupAdaptor ga = driver.getGroupAdaptor();
		UserAdaptor ua = driver.getUserAdaptor();
		
		ga.addUserToGroup(userId, foGroup.getId());
		
		User user = ua.fetchUserByID(userId);
		
		return userToFoUser(user);
		
	}
	
	public FoCnSegment[] getCnSegmentsForMstudyId(int mstudyId){
		
		FODriver driver = getFoDriver();
		CnSegmentAdaptor ca = driver.getCnSegmentAdaptor();
		
		CnSegment[] segments = ca.fetchCnSegmentsForMicroarraystudyId(mstudyId);
		
		return cnSegmentsToFoCnSegments(segments);
	}
	
	public void removeMstudy(int mstudyId){
		
		FODriver driver = getFoDriver();
		MicroarraystudyAdaptor ma = driver.getMicroarraystudyAdaptor();
		
		ma.deleteMicroarraystudy(mstudyId);
		
	}
	
	public FoMicroarraystudy[] getMicroarraystudiesForProject(int[] projectId){
		
		FODriver driver = getFoDriver();
		
		MicroarraystudyAdaptor ma = driver.getMicroarraystudyAdaptor();
		
		Microarraystudy[] m = ma.fetchMicroarraystudiesForProject(projectId, true);
		
		return mstudiesToFoMstudies(m, true);
	}
	
	public FoProject[] getProjectsForUser(FoUser user, boolean writeOnly) throws Exception {
		FODriver driver = getFoDriver();
		
		GroupAdaptor ga = driver.getGroupAdaptor();
		ProjectAdaptor pa = driver.getProjectAdaptor();
		
		Group[] g = ga.fetchGroupsForUser(user.getId());
		ProjectAccess[] projectAccess = pa.fetchProjectAccessForGroups(g, writeOnly);
		
		Project[] p = pa.fetchProjectsForProjectAccess(projectAccess);
		
		return projectsToFoProjects(p);
	}
	
	public FoProject[] getAllProjects() throws Exception {
		FODriver driver = getFoDriver();
		ProjectAdaptor pa = driver.getProjectAdaptor();
		
		Project[] projects = pa.fetchAllProjects();
		
		return projectsToFoProjects(projects);
	}
	
	public FoProject addFoProject(FoProject foProject){
		FODriver driver = getFoDriver();
		ProjectAdaptor pa = driver.getProjectAdaptor();
		
		int newProjectId = pa.storeProject(foProject.getName(), foProject.getDescription());
		
		Project newProject = pa.fetchProjectById(newProjectId);
		
		return projectToFoProject(newProject);
		
	}
	
	public FoGroup[] getAllGroupsExceptProject(FoProject foProject){
		FODriver driver = getFoDriver();
		GroupAdaptor ga = driver.getGroupAdaptor();
		
		Group[] groups = ga.fetchGroupsNotInProject(foProject.getId());
		
		return groupsToFoGroups(groups);
	}
	
	public FoProjectAccess addAccessToProject(FoProjectAccess foProjectAccess, int projectId){
		FODriver driver = getFoDriver();
		ProjectAdaptor pa = driver.getProjectAdaptor();
		
		ProjectAccess projectAccess = pa.addGroupAccessToProject(foProjectAccess.getGroupId(), projectId, foProjectAccess.getAccess());
		
		return projectAccessToFoProjectAccess(projectAccess);
	}
	
	public FoChip[] getAllChips(){
		FODriver driver = getFoDriver();
		ChipAdaptor ca = driver.getChipAdaptor();
		
		Chip[] chips = ca.fetchAllChips();
		
		return chipsToFoChips(chips);
	}
	
	public FoOrgan addOrgan(FoOrgan foOrgan){
		FODriver driver = getFoDriver();
		OrganAdaptor oa = driver.getOrganAdaptor();
		
		int id = oa.storeOrgan(foOrganToOrgan(foOrgan));
		
		Organ o = oa.fetchOrganById(id);
		
		return organToFoOrgan(o);
	}
	
	public FoOrgan[] getOrgans(boolean enabled){
		FODriver driver = getFoDriver();
		OrganAdaptor oa = driver.getOrganAdaptor();
		
		Organ[] organs = oa.fetchOrgans(enabled);
		
		return organsToFoOrgans(organs);
	}
	
	public FoOrgan[] getAllOrgans(){
		
		FODriver driver = getFoDriver();
		OrganAdaptor oa = driver.getOrganAdaptor();
		
		Organ[] organs = oa.fetchAllOrgans();
		
		return organsToFoOrgans(organs);
	}
	
	public String[] getOrganTypes(){
		FODriver driver = getFoDriver();
		OrganAdaptor oa = driver.getOrganAdaptor();
		
		String[] organTypes = oa.fetchAllTypes();
		
		return organTypes;
	}
	
	public FoProperty addProperty(FoProperty foProperty){
		FODriver driver = getFoDriver();
		PropertyAdaptor pa = driver.getPropertyAdaptor();
		
		int id = pa.storeProperty(foPropertyToProperty(foProperty));
		
		Property p = pa.fetchPropertyById(id);
		
		return propertyToFoProperty(p);
	}
	
	public FoProperty[] getProperties(boolean enabled){
		FODriver driver = getFoDriver();
		PropertyAdaptor pa = driver.getPropertyAdaptor();
		
		Property[] properties = pa.fetchProperties(true);
		
		return propertiesToFoProperties(properties);
	}
	
	public FoProperty[] getAllProperties(){
		FODriver driver = getFoDriver();
		PropertyAdaptor pa = driver.getPropertyAdaptor();
		
		Property[] properties = pa.fetchAllProperties();
		
		return propertiesToFoProperties(properties);
	}
	
	public String[] getPropertyTypes(){
		FODriver driver = getFoDriver();
		PropertyAdaptor pa = driver.getPropertyAdaptor();
		
		String[] propertyTypes = pa.fetchAllTypes();
		
		return propertyTypes;
	}
	
	public FoChip addChip(FoChip foChip){
		FODriver driver = getFoDriver();
		ChipAdaptor ca = driver.getChipAdaptor();
		
		int id = ca.storeChip(foChipToChip(foChip));
		
		Chip c = ca.fetchChipById(id);
		
		return chipToFoChip(c);
	}
	
	public String[] getChipTypes(){
		FODriver driver = getFoDriver();
		ChipAdaptor ca = driver.getChipAdaptor();
		
		String[] chipTypes = ca.fetchAllTypes();
		
		return chipTypes;
	}
	
	public void removeAccessFromProject(int projectAccessId){
		FODriver driver = getFoDriver();
		ProjectAdaptor pa = driver.getProjectAdaptor();
		
		pa.removeGroupAccessFromProject(projectAccessId);

	}
	
	public void removeProject(int projectId){
		FODriver driver = getFoDriver();
		ProjectAdaptor pa = driver.getProjectAdaptor();
		
		pa.deleteProject(projectId);
	}
	
	public void removeUserFromGroup(int groupId, int userId){
		FODriver driver = getFoDriver();
		GroupAdaptor ga = driver.getGroupAdaptor();
		
		ga.removeUserFromGroup(userId, groupId);
		
	}
	
	public void updateProfile(FoUser updateUser, FoUser sessionUser) throws Exception{
		FODriver driver = getFoDriver();
		UserAdaptor ua = driver.getUserAdaptor();
		
		if(updateUser.getId() == sessionUser.getId() &&
				updateUser.getUserName().equals(sessionUser.getUserName())){
			
			ua.updateUserFistName(updateUser.getId(), updateUser.getFirstName());
			ua.updateUserLastName(updateUser.getId(), updateUser.getLastName());
			ua.updateUserEmail(updateUser.getId(), updateUser.getEmail());
			
		} else {
			
			throw new Exception("ID or Username not correct");
			
		}
		
	}
	
	public void updatePassword(FoUser updateUser, FoUser sessionUser) throws Exception{
		FODriver driver = getFoDriver();
		UserAdaptor ua = driver.getUserAdaptor();
		
		if(updateUser.getId() == sessionUser.getId() &&
				updateUser.getUserName().equals(sessionUser.getUserName())){
			
			ua.updateUserPassword(updateUser.getId(), updateUser.getPw());
			
		} else {
			
			throw new Exception("ID or Username not correct");
			
		}
		
	}
	
	public void setPassword(int userId, String pw){
		FODriver driver = getFoDriver();
		UserAdaptor ua = driver.getUserAdaptor();
		
		ua.updateUserPassword(userId, pw);
		
	}
	
	/* private methods */
	
	private FoProjectAccess[] projectAccessesToFoProjectAccesses(ProjectAccess[] projectAccess){
		FoProjectAccess[] foProjectAccess = new FoProjectAccess[projectAccess.length];
		
		for(int i=0; i < projectAccess.length; i++){
			foProjectAccess[i] = projectAccessToFoProjectAccess(projectAccess[i]);
		}
		return foProjectAccess;
	}
	
	private FoProjectAccess projectAccessToFoProjectAccess(ProjectAccess projectAccess){
		
		FoGroup foGroup = groupToFoGroup(projectAccess.getGroup());
		
		FoProjectAccess foProjectAccess = new FoProjectAccess(projectAccess.getId(),
															foGroup,
															projectAccess.getAccess());
		return foProjectAccess;
	}
	
	private FoCnSegment[] cnSegmentsToFoCnSegments(CnSegment[] segments){
		FoCnSegment[] foSegments = new FoCnSegment[segments.length];
		
		for(int i=0; i < segments.length; i++){
			foSegments[i] = cnSegmentToFoCnSegment(segments[i]);
		}
		return foSegments;
	}
	
	private FoCnSegment cnSegmentToFoCnSegment(CnSegment segment){
		FoCnSegment foSegment = new FoCnSegment(segment.getId(),
											segment.getChromosome(),
											segment.getStart(),
											segment.getEnd(),
											segment.getMean(),
											segment.getNumberOfMarkers());
		if(segment.getMicroarraystudyName() != null){
			foSegment.setMicroarraystudyName(segment.getMicroarraystudyName());
		}
		return foSegment;
	}
	
	private FoTissueSample[] tissueSamplesToFoTissueSamples(TissueSample[] tissues){
	
		FoTissueSample[] foTissues = new FoTissueSample[tissues.length];
		
		for(int i=0; i < tissues.length; i++){
			foTissues[i] = tissueSampleToFoTissueSample(tissues[i]);
		}
		
		return foTissues;
	}
	
	private FoTissueSample tissueSampleToFoTissueSample(TissueSample tissue){
		
		FoOrgan foOrgan = organToFoOrgan(tissue.getOrgan());
		FoProperty[] foProperties = propertiesToFoProperties(tissue.getProperties()); 
		
		FoTissueSample foTissue = new FoTissueSample(tissue.getId(),
													foOrgan,
													foProperties);
		return foTissue;
	}
	
	private Property[] foPropertiesToProperties(FoProperty[] foProperties){
		Property[] properties = new Property[foProperties.length];
		
		for(int i=0; i < properties.length; i++){
			properties[i] = foPropertyToProperty(foProperties[i]);
		}
		return properties;
	}
	
	private Property foPropertyToProperty(FoProperty foProperty){
		Property property = new Property(foProperty.getId(),
											foProperty.getLabel(),
											foProperty.getType(),
											foProperty.getActivty());
		return property;
	}
	
	private FoProperty[] propertiesToFoProperties(Property[] properties){
		FoProperty[] foProperties = new FoProperty[properties.length];
		
		for(int i=0; i < properties.length; i++){
			foProperties[i] = propertyToFoProperty(properties[i]);
		}
		return foProperties;
	}
	
	private FoProperty propertyToFoProperty(Property property){
		FoProperty foProperty = new FoProperty(property.getId(),
											property.getLabel(),
											property.getType(),
											property.getActivty());
		return foProperty;
	}
	
	private Organ[] foOrgansToOrgans(FoOrgan[] foOrgans){
		Organ[] organs = new Organ[foOrgans.length];
		
		for(int i=0; i < foOrgans.length; i++){
			organs[i] = foOrganToOrgan(foOrgans[i]);
		}
		return organs;
	}
	
	private Organ foOrganToOrgan(FoOrgan foOrgan){
		Organ organ = new Organ(foOrgan.getId(),
									foOrgan.getLabel(),
									foOrgan.getType(),
									foOrgan.getActivty());
		return organ;
	}
	
	private FoOrgan[] organsToFoOrgans(Organ[] organs){
		FoOrgan[] foOrgans = new FoOrgan[organs.length];
		
		for(int i=0; i < organs.length; i++){
			foOrgans[i] = organToFoOrgan(organs[i]);
		}
		return foOrgans;
	}
	
	private FoOrgan organToFoOrgan(Organ organ){
		FoOrgan foOrgan = new FoOrgan(organ.getId(),
									organ.getLabel(),
									organ.getType(),
									organ.getActivty());
		return foOrgan;
	}
	
	private Chip[] foChipsToChips(FoChip[] foChips){
		Chip[] chips = new Chip[foChips.length];
		
		for(int i=0; i < chips.length; i++){
			chips[i] = foChipToChip(foChips[i]);
		}
		return chips;
	}
	
	private Chip foChipToChip(FoChip foChip){
		Chip chip = new Chip(foChip.getId(),
								foChip.getName(),
								foChip.getType());
		return chip;
	}
	
	private FoChip[] chipsToFoChips(Chip[] chips){
		FoChip[] foChips = new FoChip[chips.length];
		
		for(int i=0; i < chips.length; i++){
			foChips[i] = chipToFoChip(chips[i]);
		}
		return foChips;
	}
	
	private FoChip chipToFoChip(Chip chip){
		FoChip foChip = new FoChip(chip.getId(),
								chip.getName(),
								chip.getType());
		return foChip;
	}
	
	private FoMicroarraystudy[] mstudiesToFoMstudies(Microarraystudy[] mstudies, boolean withChildren){
		FoMicroarraystudy[] foMstudies = new FoMicroarraystudy[mstudies.length];
		
		for(int i=0; i < mstudies.length; i++){
			foMstudies[i] = mstudyToFoMstudy(mstudies[i], withChildren);
		}
		return foMstudies;
		
	}
	
	private FoMicroarraystudy mstudyToFoMstudy(Microarraystudy mstudy, boolean withChildren){
		
		FoMicroarraystudy foMstudy = new FoMicroarraystudy(mstudy.getId(),
														mstudy.getDate(),
														mstudy.getName(),
														mstudy.getDescription(),
														mstudy.getUserId());
		
		if(withChildren){
			foMstudy.setChip(chipToFoChip(mstudy.getChip()));
			//foMstudy.setSegments(cnSegmentsToFoCnSegments(mstudy.getSegments()));
			foMstudy.setTissue(tissueSampleToFoTissueSample(mstudy.getTissue()));
		} else {
			foMstudy.setChipId(mstudy.getChipId());
			foMstudy.setOrgan_id(mstudy.getOrgan_id());
			foMstudy.setPropertyIds(mstudy.getPropertyIds());
		}
		return foMstudy;
	}
	
	private FoProject[] projectsToFoProjects(Project[] projects){
		FoProject[] foProjects = new FoProject[projects.length];
		
		for(int i=0; i < projects.length; i++){
			foProjects[i] = projectToFoProject(projects[i]);
		}
		return foProjects;
	}
	
	private FoProject projectToFoProject(Project project){
		
		FoProject foProject = new FoProject(project.getId(),
											project.getName(),
											project.getDescription());
		
		if(project.getMstudies() != null){
			foProject.setMstudies(mstudiesToFoMstudies(project.getMstudies(), false));
		}
		
		if(project.getProjectAccess() != null){
			foProject.setProjectAccess(projectAccessesToFoProjectAccesses(project.getProjectAccess()));
		}
		return foProject;
	}
	
	private Group[] foGroupsToGroups(FoGroup[] foGroups){
		
		Group[] groups = new Group[foGroups.length];
		
		for(int i=0; i < groups.length; i++){
			groups[i] = foGroupToGroup(foGroups[i]);
		}
		
		return groups;
	}
	
	private Group foGroupToGroup(FoGroup foGroup){
		
		Group group = new Group(foGroup.getId(),
								foGroup.getName(),
								foGroup.isIsactive());
		
		if(foGroup.getUsers() != null){
			group.setUsers(foUsersToUsers(foGroup.getUsers()));
		}
		
		return group;
	}
	
	private FoGroup[] groupsToFoGroups(Group[] groups){
		
		FoGroup[] foGroups = new FoGroup[groups.length];
		
		for(int i=0; i < groups.length; i++){
			foGroups[i] = groupToFoGroup(groups[i]);
		}
		
		return foGroups;
	}
	
	private FoGroup groupToFoGroup(Group group){
		
		FoGroup foGroup = new FoGroup(group.getId(),
								group.getName(),
								group.isIsactive());
		foGroup.setUsers(usersToFoUsers(group.getUsers()));
		
		return foGroup;
	}
	
	private User[] foUsersToUsers(FoUser[] foUsers){
		
		User[] users = new User[foUsers.length];
		
		for(int i=0; i < users.length; i++){
			users[i] = foUserToUser(foUsers[i]);
		}
		
		return users;
	}
	
	private User foUserToUser(FoUser foUser){
		
		User user = new User(foUser.getId(),
				foUser.getFirstName(),
				foUser.getLastName(),
				foUser.getUserName(),
				foUser.getEmail(),
				foUser.getIsActive(),
				foUser.getIsAdmin());
		
		return user;
	}
	
	private FoUser[] usersToFoUsers(User[] users){
		
		FoUser[] foUsers = new FoUser[users.length];
		
		
		for(int i=0; i < users.length; i++){
			foUsers[i] = userToFoUser(users[i]);
		}
		
		return foUsers;
	}
	
	private FoUser userToFoUser(User user){
		
		FoUser foUser = new FoUser(user.getId(),
				user.getFirstName(),
				user.getLastName(),
				user.getUserName(),
				user.getEmail(),
				user.getIsActive(),
				user.getIsAdmin());
		
		return foUser;
	}
	
	//TODO move to fishoracle_db_api
	public boolean isAccessable(String page, FoUser user){
		Connection conn = null;
		
		boolean access = false;
		
		try {
			conn = FishOracleConnection.connect(connectionData.getFhost(), connectionData.getFdb(), connectionData.getFuser(), connectionData.getFpw());
			
			Statement pageAccessStatement = conn.createStatement();
			pageAccessStatement.executeQuery("SELECT area_access_user_id, (NOW() - area_access_table_time) / 60 AS timeleft FROM area_access WHERE area_access_area_name = '" + page + "'");
		
			ResultSet pageAccessRs = pageAccessStatement.getResultSet();
			//do we get a result? if yes, the page is occupied, else we can use it.
			if(pageAccessRs.next()){
				int paUserId = pageAccessRs.getInt(1);
				
				double minutesLeft = pageAccessRs.getDouble(2);
				
				// check if the timestamp is still valid. if not, delete the lock and use the page
				if(minutesLeft > 1){
					access = true;
					pageAccessStatement.executeUpdate("DELETE FROM area_access WHERE area_access_area_name = '" + page + "' AND " +
							"'" + user.getId() + "'");
					
					pageAccessStatement.executeUpdate("INSERT INTO area_access (area_access_area_name, area_access_user_id) " +
							"VALUES ('" + page + "', '" + user.getId() + "')");
				}
				// check if the user has permission
				if(user.getId() == paUserId){
					access = true;
				}
				
			} else {
				access = true;
				pageAccessStatement.executeUpdate("INSERT INTO area_access (area_access_area_name, area_access_user_id) " +
												"VALUES ('" + page + "', '" + user.getId() + "')");
			}
			
		} catch (Exception e) {
			FishOracleConnection.printErrorMessage(e);
			System.out.println(e.getMessage());
			e.printStackTrace();
		} finally {
			if(conn != null){
				try{
					conn.close();
				} catch(Exception e) {
					String err = FishOracleConnection.getErrorMessage(e);
					System.out.println(err);
				}
			}
		}
		
		return access;
	}
	
	public void unlockPage(String page, FoUser user){
		Connection conn = null;
		
		try {
			conn = FishOracleConnection.connect(connectionData.getFhost(), connectionData.getFdb(), connectionData.getFuser(), connectionData.getFpw());
			
			Statement pageAccessStatement = conn.createStatement();
			pageAccessStatement.executeUpdate("DELETE FROM area_access WHERE area_access_area_name = '" + page + "' AND " +
											"area_access_user_id = '" + user.getId() + "'");
			
		} catch (Exception e) {
			FishOracleConnection.printErrorMessage(e);
			System.out.println(e.getMessage());
			e.printStackTrace();
		} finally {
			if(conn != null){
				try{
					conn.close();
				} catch(Exception e) {
					String err = FishOracleConnection.getErrorMessage(e);
					System.out.println(err);
				}
			}
		}
	}
}
