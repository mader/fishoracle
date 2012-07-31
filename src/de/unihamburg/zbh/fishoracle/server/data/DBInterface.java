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

package de.unihamburg.zbh.fishoracle.server.data;

import java.sql.*;

import com.sun.jna.Pointer;

import core.GTerrorJava;
import core.Range;

import annotationsketch.FeatureCollection;
import annotationsketch.FeatureIndex;
import annotationsketch.FeatureIndexFo;

import de.unihamburg.zbh.fishoracle.client.data.DBConfigData;
import de.unihamburg.zbh.fishoracle.client.data.FoCnSegment;
import de.unihamburg.zbh.fishoracle.client.data.FoEnsemblDBs;
import de.unihamburg.zbh.fishoracle.client.data.FoGroup;
import de.unihamburg.zbh.fishoracle.client.data.FoLocation;
import de.unihamburg.zbh.fishoracle.client.data.FoPlatform;
import de.unihamburg.zbh.fishoracle.client.data.FoStudy;
import de.unihamburg.zbh.fishoracle.client.data.FoOrgan;
import de.unihamburg.zbh.fishoracle.client.data.FoProject;
import de.unihamburg.zbh.fishoracle.client.data.FoProjectAccess;
import de.unihamburg.zbh.fishoracle.client.data.FoProperty;
import de.unihamburg.zbh.fishoracle.client.data.FoTissueSample;
import de.unihamburg.zbh.fishoracle.client.data.FoUser;
import de.unihamburg.zbh.fishoracle.client.data.EnsemblGene;
import de.unihamburg.zbh.fishoracle.client.data.QueryInfo;
import de.unihamburg.zbh.fishoracle.client.exceptions.DBQueryException;

import de.unihamburg.zbh.fishoracle_db_api.data.CnSegment;
import de.unihamburg.zbh.fishoracle_db_api.data.EnsemblDBs;
import de.unihamburg.zbh.fishoracle_db_api.data.Group;
import de.unihamburg.zbh.fishoracle_db_api.data.Location;
import de.unihamburg.zbh.fishoracle_db_api.data.Organ;
import de.unihamburg.zbh.fishoracle_db_api.data.Platform;
import de.unihamburg.zbh.fishoracle_db_api.data.Project;
import de.unihamburg.zbh.fishoracle_db_api.data.ProjectAccess;
import de.unihamburg.zbh.fishoracle_db_api.data.Property;
import de.unihamburg.zbh.fishoracle_db_api.data.Study;
import de.unihamburg.zbh.fishoracle_db_api.data.TissueSample;
import de.unihamburg.zbh.fishoracle_db_api.data.User;
import de.unihamburg.zbh.fishoracle_db_api.driver.CnSegmentAdaptor;
import de.unihamburg.zbh.fishoracle_db_api.driver.EnsemblDBsAdaptor;
import de.unihamburg.zbh.fishoracle_db_api.driver.FODriver;
import de.unihamburg.zbh.fishoracle_db_api.driver.FODriverImpl;
import de.unihamburg.zbh.fishoracle_db_api.driver.GroupAdaptor;
import de.unihamburg.zbh.fishoracle_db_api.driver.OrganAdaptor;
import de.unihamburg.zbh.fishoracle_db_api.driver.PlatformAdaptor;
import de.unihamburg.zbh.fishoracle_db_api.driver.ProjectAdaptor;
import de.unihamburg.zbh.fishoracle_db_api.driver.PropertyAdaptor;
import de.unihamburg.zbh.fishoracle_db_api.driver.StudyAdaptor;
import de.unihamburg.zbh.fishoracle_db_api.driver.UserAdaptor;
import extended.AnnoDBEnsembl;
import extended.AnnoDBFo;
import extended.EnsemblGeneAdaptor;
import extended.EnsemblKaryoAdaptor;
import extended.FeatureNode;
import extended.RDB;
import extended.RDBMysql;

/**
 * Fetches various information from the fish oracle database and gene
 * information from the ensembl database using the ensembl GenomeTools API.
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
	
	public RDBMysql getEnsemblRDB(String ensemblDB) throws GTerrorJava {
		
		if(ensemblDB != null){
			
			connectionData.setEdb(ensemblDB);
		}
		
		RDBMysql rdb = new RDBMysql(connectionData.getEhost(), connectionData.getEport(), connectionData.getEdb(), connectionData.getEuser(), connectionData.getEpw());
		return rdb;
	}
	
	public RDBMysql getFishoracleRDB() throws GTerrorJava {
		RDBMysql rdb = new RDBMysql(connectionData.getFhost(), 3306, connectionData.getFdb(), connectionData.getFuser(), connectionData.getFpw());
		return rdb;
	}
	
	/**
	 * Looks up location information (chromosome, start, end) for a gene symbol.
	 * 
	 * @param symbol The gene symbol, that was specified in the search query.
	 * @return		An ensembl API location object storing chromosome, start and end of a gene. 
	 * @throws Exception 
	 * 
	 * */
	public Location getLocationForGene(RDBMysql rdb, String symbol) throws Exception{
		
		AnnoDBEnsembl adb = new AnnoDBEnsembl();
		FeatureIndex fi = adb.gt_anno_db_schema_get_feature_index((RDB) rdb);
		
		int ensembl_version;
		EnsemblGeneAdaptor ga = null;
		
		ensembl_version = adb.getFeatureIndexEnsemblVersion(fi);
			
		ga = new EnsemblGeneAdaptor(ensembl_version);
		
		FeatureNode fn;
		try {
			fn = ga.fetchGeneForSymbol(fi, symbol);
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
		
		Location l = new Location(0, fn.get_seqid(), fn.get_range().get_start(), fn.get_range().get_end());
		
		fn.dispose();
		fi.delete();
		adb.delete();
		
		return l;
	}
	
	/** 
	 * Looks up location information (chromosome, start, end) for a karyoband.
	 * 
	 * @param chr The chromosome number
	 * @param band The karyoband
	 * @return		An ensembl API location object storing chromosome, start and end of a chromosome and karyoband. 
	 * @throws DBQueryException 
	 * @throws GTerrorJava 
	 * 
	 * */
	public Location getLocationForKaryoband(RDBMysql rdb, String chr, String band) throws DBQueryException, GTerrorJava {
		
		AnnoDBEnsembl adb = new AnnoDBEnsembl();
		FeatureIndex fi = adb.gt_anno_db_schema_get_feature_index((RDB) rdb);
		
		int ensembl_version;
		EnsemblKaryoAdaptor ka = null;
		
		ensembl_version = adb.getFeatureIndexEnsemblVersion(fi);
			
		ka = new EnsemblKaryoAdaptor(ensembl_version);
		
		Range r = ka.fetchRangeForKaryoband(fi, chr, band);
		
		Location l = new Location(0, chr, r.get_start(), r.get_end());
		
		fi.delete();
		adb.delete();
		
		return l;
	}
	
	/**
	 * Fetch all data for a gene given by an ensembl stable id.
	 * 
	 * @param query Ensembl Stable ID
	 * @return		Gen object containing all gene data.
	 * @throws Exception 
	 * 
	 * */
	public EnsemblGene getGeneInfos(RDBMysql rdb, String query) throws Exception {
		
		EnsemblGene gene = null;
		
		AnnoDBEnsembl adb = new AnnoDBEnsembl();
		FeatureIndex fi = adb.gt_anno_db_schema_get_feature_index((RDB) rdb);
		
		int ensembl_version;
		EnsemblGeneAdaptor ga = null;
		
		ensembl_version = adb.getFeatureIndexEnsemblVersion(fi);
			
		ga = new EnsemblGeneAdaptor(ensembl_version);
		
		FeatureNode fn = ga.fetchGeneForStableId(fi, query);
		
		Range rng = fn.get_range();
		
		gene = new EnsemblGene(fn.get_attribute("ID"),
								fn.get_seqid(),
								rng.get_start(),
								rng.get_end(),
								Character.toString(fn.get_strand()));
		
		gene.setBioType(fn.get_attribute("BIOTYPE"));
		gene.setDescription(fn.get_attribute("DESCRIPTION"));
		gene.setAccessionID(fn.get_attribute("NAME"));
		gene.setLength(rng.get_end() - rng.get_start());
		
		fn.dispose();
		fi.delete();
		adb.delete();
		
		return gene;
	}
	
	/**
	 * For a range on a chromosome an array with all overlapping genes is returned.
	 * 
	 * @param chr chromosome
	 * @param start Starting position on the chromosome.
	 * @param end ending postion on the chromosome.
	 * @return 		Array containing gen objects
	 * @throws GTerrorJava 
	 * 
	 * */
	public void getEnsembleGenes(RDBMysql rdb,
									String chr,
									int start,
									int end,
									String[] biotype,
									FeatureCollection features) throws GTerrorJava {
		
		AnnoDBEnsembl adb = new AnnoDBEnsembl();
		FeatureIndex fi = adb.gt_anno_db_schema_get_feature_index((RDB) rdb);
		
		int ensembl_version;
		EnsemblGeneAdaptor ga = null;
		
		ensembl_version = adb.getFeatureIndexEnsemblVersion(fi);
			
		ga = new EnsemblGeneAdaptor(ensembl_version);
		
		Range r = new Range(start, end);
		
		core.Array arr = ga.fetchGenesForRange(fi, chr, r, biotype);
		
		features.addArray(arr);
		
		arr.dispose();
		fi.delete();
		adb.delete();
		
	}
	
	/**
	 * For a range on a chromosome an array with all overlapping karyobands is returned.
	 * 
	 * @param chr Chromosome
	 * @param start Starting position on the chromosome.
	 * @param end Ending postion on the chromosome.
	 * @return 		Array containing karyoband objects.
	 * @throws GTerrorJava 
	 * 
	 * */
	public synchronized void getEnsemblKaryotypes(RDBMysql rdb,
													String chr,
													int start,
													int end,
													FeatureCollection features) throws GTerrorJava {

		AnnoDBEnsembl adb = new AnnoDBEnsembl();
		FeatureIndex fi = adb.gt_anno_db_schema_get_feature_index((RDB) rdb);
		
		int ensembl_version;
		EnsemblKaryoAdaptor ka = null;
		
		ensembl_version = adb.getFeatureIndexEnsemblVersion(fi);
			
		ka = new EnsemblKaryoAdaptor(ensembl_version);
		
		Range r = new Range(start, end);
		
		core.Array arr = ka.fetchKaryobandsForRange(fi, chr, r);
		
		features.addArray(arr);
		
		arr.dispose();
		fi.delete();
		adb.delete();
	}
	
	/* FISH ORACLE INTERFACE */
	
	private FODriver getFoDriver(){
		return new FODriverImpl(connectionData.getFhost(),
									connectionData.getFdb(),
									connectionData.getFuser(),
									connectionData.getFpw(),
									"3306");
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
		
		Location maxLoc = null;
		
		for(int i = 0; i < query.getTracks().length; i++){
			
			Location l;
			
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
		
		return maxLoc;
	}
	
	public void getSegmentsForTracks(RDBMysql rdb,
										String chr,
										int start,
										int end,
										QueryInfo query,
										FeatureCollection features) throws GTerrorJava{
		
		Range r = new Range(start, end);
		core.Array segments;
		
		AnnoDBFo adb = new AnnoDBFo();
		FeatureIndex fi = adb.gt_anno_db_schema_get_feature_index((RDB) rdb);
		FeatureIndexFo fifo = new FeatureIndexFo(fi.to_ptr());
		adb.segmentOnly(fifo);
		
		int[] pIds;
		int[] tIds;
		int[] eIds;
			
		Double lth;
		Double uth;
		
		for(int i = 0; i < query.getTracks().length; i++){
			
			if(query.getTracks()[i].getProjectIds() != null){
				pIds = query.getTracks()[i].getProjectIds();
			} else {
				pIds = new int[0];
			}
				
			if(query.getTracks()[i].getTissueIds() != null){
				tIds = query.getTracks()[i].getTissueIds();
			} else {
				tIds = new int[0];
			}
				
			if(query.getTracks()[i].getExperimentIds() != null){
				eIds = query.getTracks()[i].getExperimentIds();
			} else {
				eIds = new int[0];
			}
			
			if(query.isGlobalTh()){
				
				if(query.getGlobalLowerThAsDouble() != null){
					lth = query.getGlobalLowerThAsDouble();
				} else {
					lth = 99999.0;
				}
				
				if(query.getGlobalUpperThAsDouble() != null){
					uth = query.getGlobalUpperThAsDouble();
				} else {
					uth = 99999.0;
				}
			} else {
				
				if(query.getTracks()[i].getLowerThAsDouble() != null){
					lth = query.getTracks()[i].getLowerThAsDouble();
				} else {
					lth = 99999.0;
				}
				
				if(query.getTracks()[i].getUpperThasDouble() != null){
					uth = query.getTracks()[i].getUpperThasDouble();
				} else {
					uth = 99999.0;
				}
			}
			
			adb.setTrackId(fifo, query.getTracks()[i].getTrackName());
			adb.setSegmentsLowerTh(fifo, lth);
			adb.setSegmentsUpperTh(fifo, uth);
			adb.setSegmentsSorted(fifo, query.isSorted());
			adb.addProjectFilter(fifo, pIds, pIds.length);
			adb.addTissueFilter(fifo, tIds, tIds.length);
			adb.setAdditionalExperimentFilter(fifo, eIds, eIds.length);
			
			segments = adb.getFeatures(fifo,
										chr,
										r);
			
			features.addArray(segments);
			segments.dispose();
		}
		
		fifo.delete();
		adb.delete();
		
	}
	
	public FoCnSegment getSegmentInfos(int segmentId) {
		
		FODriver driver = getFoDriver();
		CnSegmentAdaptor sa = driver.getCnSegmentAdaptor();
		
		CnSegment s = sa.fetchCnSegmentById(segmentId);
		
		return cnSegmentToFoCnSegment(s);
	}
	
	public FoEnsemblDBs addEDB(FoEnsemblDBs foEdbs) {
		
		FODriver driver = getFoDriver();
		EnsemblDBsAdaptor ea = driver.getEnsemblDBsAdaptor();
		
		int id = ea.storeDB(foEdbsToEdbs(foEdbs));
		
		EnsemblDBs newEdbs = ea.fetchDBById(id);
		
		return edbsToFoEdbs(newEdbs);
	}
	
	public FoEnsemblDBs[] fetchEDBs() {
		
		FODriver driver = getFoDriver();
		EnsemblDBsAdaptor ea = driver.getEnsemblDBsAdaptor();
		
		EnsemblDBs[] edbss = ea.fetchAllDBs();
		
		return edbssToFoEdbss(edbss);
	}
	
	public void removeEDB(int edbsId) {
		
		FODriver driver = getFoDriver();
		EnsemblDBsAdaptor ea = driver.getEnsemblDBsAdaptor();
		
		ea.deleteDB(edbsId);
	}
	
	public int createNewStudy(Study study, int projectId) {
		FODriver driver = getFoDriver();
		StudyAdaptor sa = driver.getStudyAdaptor();
	
		return sa.storeStudy(study, projectId);
	}
	
	public FoUser insertUser(FoUser user) throws Exception {
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
	
	public FoUser getUser(String userName, String password) throws Exception {
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
	
	public FoUser[] getUsersForGroup(int groupId){
		
		FODriver driver = getFoDriver();
		UserAdaptor ua = driver.getUserAdaptor();
			
		return usersToFoUsers(ua.fetchUsersForGroup(groupId));
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
		
		Group[] groups = ga.fetchAllGroups(false);
		
		return groupsToFoGroups(groups, false);
	}
	
	public FoGroup addGroup(FoGroup foGroup){
		FODriver driver = getFoDriver();
		GroupAdaptor ga = driver.getGroupAdaptor();
		
		int newGroupId = ga.storeGroup(foGroup.getName(), foGroup.getIsactiveAsInt());
		
		Group newGroup = ga.fetchGroupById(newGroupId, false);
		
		return groupToFoGroup(newGroup, false);
		
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
		
		ga.addUserToGroup(foGroup.getId(), userId);
		
		User user = ua.fetchUserByID(userId);
		
		return userToFoUser(user);
		
	}
	
	public FoCnSegment[] getCnSegmentsForStudyId(int studyId){
		
		FODriver driver = getFoDriver();
		CnSegmentAdaptor ca = driver.getCnSegmentAdaptor();
		
		CnSegment[] segments = ca.fetchCnSegmentsForStudyId(studyId);
		
		return cnSegmentsToFoCnSegments(segments);
	}
	
	public void removeStudy(int studyId){
		
		FODriver driver = getFoDriver();
		StudyAdaptor sa = driver.getStudyAdaptor();
		
		sa.deleteStudy(studyId);
		
	}
	
	public FoStudy[] getStudiesForProject(int[] projectId, boolean withChildren){
		
		FODriver driver = getFoDriver();
		
		StudyAdaptor sa = driver.getStudyAdaptor();
		
		Study[] s = sa.fetchStudiesForProject(projectId, withChildren);
		
		return studiesToFostudies(s, withChildren);
	}
	
	public FoProject[] getProjectsForUser(FoUser user,boolean withChildren, boolean writeOnly) throws Exception {
		FODriver driver = getFoDriver();
		
		GroupAdaptor ga = driver.getGroupAdaptor();
		ProjectAdaptor pa = driver.getProjectAdaptor();
		
		Group[] g = ga.fetchGroupsForUser(user.getId(), false);
		ProjectAccess[] projectAccess = pa.fetchProjectAccessForGroups(g, withChildren, writeOnly);
		
		Project[] p = pa.fetchProjectsForProjectAccess(projectAccess, false);
		
		return projectsToFoProjects(p);
	}
	
	public FoProject[] getAllProjects() throws Exception {
		FODriver driver = getFoDriver();
		ProjectAdaptor pa = driver.getProjectAdaptor();
		
		Project[] projects = pa.fetchAllProjects(false);
		
		return projectsToFoProjects(projects);
	}
	
	public FoProject addFoProject(FoProject foProject){
		FODriver driver = getFoDriver();
		ProjectAdaptor pa = driver.getProjectAdaptor();
		
		int newProjectId = pa.storeProject(foProject.getName(), foProject.getDescription());
		
		Project newProject = pa.fetchProjectById(newProjectId, false);
		
		return projectToFoProject(newProject);
		
	}
	
	public FoGroup[] getAllGroupsExceptProject(FoProject foProject){
		FODriver driver = getFoDriver();
		GroupAdaptor ga = driver.getGroupAdaptor();
		
		Group[] groups = ga.fetchGroupsNotInProject(foProject.getId(), false);
		
		return groupsToFoGroups(groups, false);
	}
	
	public FoProjectAccess[] getProjectAccessForProject(int projectId){
		
		FODriver driver = getFoDriver();
		ProjectAdaptor pa = driver.getProjectAdaptor();
		
		return projectAccessesToFoProjectAccesses(pa.fetchProjectAccessForProject(projectId, true), true);
	}
	
	public FoProjectAccess addAccessToProject(FoProjectAccess foProjectAccess){
		FODriver driver = getFoDriver();
		ProjectAdaptor pa = driver.getProjectAdaptor();
		
		ProjectAccess projectAccess = pa.addGroupAccessToProject(foProjectAccess.getGroupId(), foProjectAccess.getFoProjectId(), foProjectAccess.getAccess());
		
		return projectAccessToFoProjectAccess(projectAccess, true);
	}
	
	public FoPlatform[] getAllPlatforms(){
		FODriver driver = getFoDriver();
		PlatformAdaptor pa = driver.getPlatformAdaptor();
		
		Platform[] platforms = pa.fetchAllPlatforms();
		
		return platformsToFoPlatforms(platforms);
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
	
	public FoPlatform addPlatform(FoPlatform foPlatform){
		FODriver driver = getFoDriver();
		PlatformAdaptor pa = driver.getPlatformAdaptor();
	
		int id = pa.storePlatform(foPlatformToPlatform(foPlatform));
		
		Platform p = pa.fetchPlatformById(id);
		
		return platformToFoPlatform(p);
	}
	
	public String[] getPlatformTypes(){
		FODriver driver = getFoDriver();
		PlatformAdaptor pa = driver.getPlatformAdaptor();
		
		String[] platformTypes = pa.fetchAllTypes();
		
		return platformTypes;
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
	
	private EnsemblDBs[] foEdbssToEdbss(FoEnsemblDBs[] foEdbs){
		EnsemblDBs[] edbs = new EnsemblDBs[foEdbs.length];
		
		for(int i=0; i < foEdbs.length; i++){
			edbs[i] = foEdbsToEdbs(foEdbs[i]);
		}
		return edbs;
	}
	
	private EnsemblDBs foEdbsToEdbs(FoEnsemblDBs foEdbs){
		EnsemblDBs edbs = new EnsemblDBs(foEdbs.getId(),
									foEdbs.getDBName(),
									foEdbs.getLabel(),
									foEdbs.getVersion());
		return edbs;
	}
	
	private FoEnsemblDBs[] edbssToFoEdbss(EnsemblDBs[] edbs){
		FoEnsemblDBs[] foEdbs = new FoEnsemblDBs[edbs.length];
		
		for(int i=0; i < edbs.length; i++){
			foEdbs[i] = edbsToFoEdbs(edbs[i]);
		}
		return foEdbs;
	}
	
	private FoEnsemblDBs edbsToFoEdbs(EnsemblDBs edbs){
		FoEnsemblDBs foEdbs = new FoEnsemblDBs(edbs.getId(),
									edbs.getDBName(),
									edbs.getLabel(),
									edbs.getVersion());
		return foEdbs;
	}
	
	private FoProjectAccess[] projectAccessesToFoProjectAccesses(ProjectAccess[] projectAccess, boolean withChildren){
		FoProjectAccess[] foProjectAccess = new FoProjectAccess[projectAccess.length];
		
		for(int i=0; i < projectAccess.length; i++){
			foProjectAccess[i] = projectAccessToFoProjectAccess(projectAccess[i], withChildren);
		}
		return foProjectAccess;
	}
	
	private FoProjectAccess projectAccessToFoProjectAccess(ProjectAccess projectAccess, boolean withChildren){
		
		FoProjectAccess foProjectAccess;
		
		if(!withChildren){
		
			foProjectAccess = new FoProjectAccess(projectAccess.getId(),
													projectAccess.getGroupId(),
													projectAccess.getAccess());
			
		} else {
			
			FoGroup foGroup = groupToFoGroup(projectAccess.getGroup(), false);
		
			foProjectAccess = new FoProjectAccess(projectAccess.getId(),
													foGroup,
													projectAccess.getAccess());
		}
		
		return foProjectAccess;
	}
	
	private FoLocation locationToFoLocation(Location loc){
		FoLocation foLocation = new FoLocation(loc.getId(),
												loc.getChromosome(),
												loc.getStart(),
												loc.getEnd());
		return foLocation;
	}
	
	
	private Location foLocationToLocation(FoLocation foLoc){
		Location loc = new Location(foLoc.getId(),
										foLoc.getChromosome(),
										foLoc.getStart(),
										foLoc.getEnd());
		return loc;
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
											locationToFoLocation(segment.getLocation()),
											segment.getMean(),
											segment.getNumberOfMarkers());
		if(segment.getStudyName() != null){
			foSegment.setStudyName(segment.getStudyName());
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
	
	private Platform[] foPlatformsToPlatforms(FoPlatform[] foPlatforms){
		Platform[] platforms = new Platform[foPlatforms.length];
		
		for(int i=0; i < platforms.length; i++){
			platforms[i] = foPlatformToPlatform(foPlatforms[i]);
		}
		return platforms;
	}
	
	private Platform foPlatformToPlatform(FoPlatform foPlatform){
		Platform platform = new Platform(foPlatform.getId(),
											foPlatform.getName(),
											foPlatform.getType());
		return platform;
	}
	
	private FoPlatform[] platformsToFoPlatforms(Platform[] platforms){
		FoPlatform[] foPlatforms = new FoPlatform[platforms.length];
		
		for(int i=0; i < platforms.length; i++){
			foPlatforms[i] = platformToFoPlatform(platforms[i]);
		}
		return foPlatforms;
	}
	
	private FoPlatform platformToFoPlatform(Platform platform){
		FoPlatform foPlatform = new FoPlatform(platform.getId(),
												platform.getName(),
												platform.getType());
		return foPlatform;
	}
	
	private FoStudy[] studiesToFostudies(Study[] studies, boolean withChildren){
		FoStudy[] foStudies = new FoStudy[studies.length];
		
		for(int i=0; i < studies.length; i++){
			foStudies[i] = studyToFoStudy(studies[i], withChildren);
		}
		return foStudies;
		
	}
	
	private FoStudy studyToFoStudy(Study study, boolean withChildren){
		
		FoStudy foStudy = new FoStudy(study.getId(),
										study.getDate(),
										study.getName(),
										study.getType(),
										study.getAssembly(),
										study.getDescription(),
										study.getUserId());
		
		if(withChildren){
			foStudy.setPlatform(platformToFoPlatform(study.getPlatform()));
			foStudy.setTissue(tissueSampleToFoTissueSample(study.getTissue()));
		} else {
			foStudy.setPlatformId(study.getPlatformId());
			foStudy.setOrganId(study.getOrganId());
			foStudy.setPropertyIds(study.getPropertyIds());
		}
		return foStudy;
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
		
		if(project.getStudies() != null){
			foProject.setStudies(studiesToFostudies(project.getStudies(), false));
		}
		
		if(project.getProjectAccess() != null){
			foProject.setProjectAccess(projectAccessesToFoProjectAccesses(project.getProjectAccess(),true));
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
	
	private FoGroup[] groupsToFoGroups(Group[] groups, boolean withChildren){
		
		FoGroup[] foGroups = new FoGroup[groups.length];
		
		for(int i=0; i < groups.length; i++){
			foGroups[i] = groupToFoGroup(groups[i], withChildren);
		}
		
		return foGroups;
	}
	
	private FoGroup groupToFoGroup(Group group, boolean withChildren){
		
		FoGroup foGroup = new FoGroup(group.getId(),
								group.getName(),
								group.isIsactive());
		
		if(withChildren){
			foGroup.setUsers(usersToFoUsers(group.getUsers()));
		}
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