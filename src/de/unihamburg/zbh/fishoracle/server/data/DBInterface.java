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

import core.GTerrorJava;
import core.Range;

import annotationsketch.FeatureCollection;
import annotationsketch.FeatureIndex;
import annotationsketch.FeatureIndexFo;

import de.unihamburg.zbh.fishoracle.client.data.DBConfigData;
import de.unihamburg.zbh.fishoracle.client.data.FoCnSegment;
import de.unihamburg.zbh.fishoracle.client.data.FoEnsemblDBs;
import de.unihamburg.zbh.fishoracle.client.data.FoGroup;
import de.unihamburg.zbh.fishoracle.client.data.FoPlatform;
import de.unihamburg.zbh.fishoracle.client.data.FoStudy;
import de.unihamburg.zbh.fishoracle.client.data.FoOrgan;
import de.unihamburg.zbh.fishoracle.client.data.FoProject;
import de.unihamburg.zbh.fishoracle.client.data.FoProjectAccess;
import de.unihamburg.zbh.fishoracle.client.data.FoProperty;
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
import de.unihamburg.zbh.fishoracle_db_api.driver.SNPMutationAdaptor;
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
		
		gene = new EnsemblGene(fn.get_attribute(GFF3Constants.ID),
								fn.get_seqid(),
								rng.get_start(),
								rng.get_end(),
								Character.toString(fn.get_strand()));
		
		gene.setBioType(fn.get_attribute(GFF3Constants.BIOTYPE));
		gene.setDescription(fn.get_attribute(GFF3Constants.DESCRIPTION));
		gene.setAccessionID(fn.get_attribute(GFF3Constants.NAME));
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
		
		Location maxLoc = new Location(0, chr, start, end);
		
		for(int i = 0; i < query.getTracks().length; i++){
			
			Location l;
			if(query.getTracks()[i].getDataType().equals("Segments")){
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
		}
		
		return maxLoc;
	}
	
	public void getFeaturesForTracks(RDBMysql rdbFo,
										RDBMysql rdbe,
										String chr,
										int start,
										int end,
										QueryInfo query,
										FeatureCollection features) throws GTerrorJava{
		
		Range r = new Range(start, end);
		core.Array feats;
		
		AnnoDBFo adb = new AnnoDBFo();
		FeatureIndex fi = adb.gt_anno_db_schema_get_feature_index((RDB) rdbFo);
		FeatureIndexFo fifo = new FeatureIndexFo(fi.to_ptr());
		
		int[] pIds;
		int[] tIds;
		int[] eIds;
		double qualityScore;
		String[] somatic;
		String[] confidence;
		String[] snpTool;
		
		Double lth = null;
		Double uth = null;
		
		for(int i = 0; i < query.getTracks().length; i++){
			
			if(i > 0){
				adb.unsetAllFilters(fifo);
			}
			
			adb.setTrackId(fifo, query.getTracks()[i].getTrackName());
			
			if(query.getTracks()[i].getProjectIds() != null){
				pIds = query.getTracks()[i].getProjectIds();
				adb.addProjectFilter(fifo, pIds);
			}
				
			if(query.getTracks()[i].getTissueIds() != null){
				tIds = query.getTracks()[i].getTissueIds();
				adb.addTissueFilter(fifo, tIds);
			}
				
			if(query.getTracks()[i].getExperimentIds() != null){
				eIds = query.getTracks()[i].getExperimentIds();
				adb.setAdditionalExperimentFilter(fifo, eIds);
			}
			
			if(query.getTracks()[i].getDataType().equals("Segments")){
				
				adb.segmentOnly(fifo);
				
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
				adb.setSegmentsSorted(fifo, query.isSorted());
				adb.setSegmentsLowerTh(fifo, lth);
				adb.setSegmentsUpperTh(fifo, uth);
				
			}
			
			if(query.getTracks()[i].getDataType().equals("Mutations")){
				
				adb.mutationsOnly(fifo);
				
				if(query.getTracks()[i].getQualityScore() != 99999.0){
					qualityScore = query.getTracks()[i].getQualityScore();
					adb.setScore(fifo, qualityScore, true);
				}
				
				if(query.getTracks()[i].getSomatic() != null){
					somatic = query.getTracks()[i].getSomatic();
					adb.addSomaticFilter(fifo, somatic);
				}
				
				if(query.getTracks()[i].getConfidence() != null){
					confidence = query.getTracks()[i].getConfidence();
					adb.addConfidenceFilter(fifo, confidence);
				}
				
				if(query.getTracks()[i].getSnpTool() != null){
					snpTool = query.getTracks()[i].getSnpTool();
					adb.addSnptoolFilter(fifo, snpTool);
				}
			}
			
			if(query.getTracks()[i].getDataType().equals("Translocations")){
				
				adb.translocationsOnly(fifo);
				
			}
			
			adb.setLocation(fifo, chr, r);
			
			feats = adb.getFeatures(fifo);
			
			if(query.getTracks()[i].getDataType().equals("Mutations")){
				
				core.Array procFeats;
				procFeats = adb.processMutations(feats, rdbe, query.getTracks()[i].getTrackName(), query.getBiotypeFilter());
				features.addArray(procFeats);
				procFeats.dispose();
			}
			if(query.getTracks()[i].getDataType().equals("Translocations")){
				
				core.Array procFeats;
				procFeats = adb.processTranslocations(fifo, feats, rdbe, query.getTracks()[i].getTrackName(), query.getBiotypeFilter());
				features.addArray(procFeats);
				
				//procFeats.dispose();
			}
			
			if(query.getTracks()[i].getDataType().equals("Segments")){
				features.addArray(feats);
			}
			feats.dispose();
		}
		
		fifo.delete();
		adb.delete();
		
	}
	
	public FoCnSegment getSegmentInfos(int segmentId) {
		
		FODriver driver = getFoDriver();
		CnSegmentAdaptor sa = driver.getCnSegmentAdaptor();
		
		CnSegment s = sa.fetchCnSegmentById(segmentId);
		
		return DataTypeConverter.cnSegmentToFoCnSegment(s);
	}
	
	public FoEnsemblDBs addEDB(FoEnsemblDBs foEdbs) {
		
		FODriver driver = getFoDriver();
		EnsemblDBsAdaptor ea = driver.getEnsemblDBsAdaptor();
		
		int id = ea.storeDB(DataTypeConverter.foEdbsToEdbs(foEdbs));
		
		EnsemblDBs newEdbs = ea.fetchDBById(id);
		
		return DataTypeConverter.edbsToFoEdbs(newEdbs);
	}
	
	public FoEnsemblDBs[] fetchEDBs() {
		
		FODriver driver = getFoDriver();
		EnsemblDBsAdaptor ea = driver.getEnsemblDBsAdaptor();
		
		EnsemblDBs[] edbss = ea.fetchAllDBs();
		
		return DataTypeConverter.edbssToFoEdbss(edbss);
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
	
	public void importData(Study study, String importType) {
		FODriver driver = getFoDriver();
		
		StudyAdaptor sa = driver.getStudyAdaptor();
		
		Study s = sa.fetchStudyForName(study.getName(), false);
		
		if(importType.equals("Segments")){
			CnSegmentAdaptor ca = driver.getCnSegmentAdaptor();
		
			ca.storeCnSegments(study.getSegments(), s.getId());
		}
		if(importType.equals("Mutations")){
			SNPMutationAdaptor ma = driver.getSNPMutationAdaptor();
			
			ma.storeSNPMutations(study.getMutations(), s.getId());
		}
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
		
		return DataTypeConverter.userToFoUser(newUser);
	}
	
	public FoUser getUser(String userName, String password) throws Exception {
		FODriver driver = getFoDriver();
		UserAdaptor ua = driver.getUserAdaptor();
		
		User user = ua.fetchUserForLogin(userName, password);

		return DataTypeConverter.userToFoUser(user);
	}
	
	public FoUser[] getAllUsers(){
		FODriver driver = getFoDriver();
		UserAdaptor ua = driver.getUserAdaptor();
		
		User[] users = ua.fetchAllUsers();
		
		return DataTypeConverter.usersToFoUsers(users);
	}
	
	public FoUser[] getUsersForGroup(int groupId){
		
		FODriver driver = getFoDriver();
		UserAdaptor ua = driver.getUserAdaptor();
			
		return DataTypeConverter.usersToFoUsers(ua.fetchUsersForGroup(groupId));
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
		
		return DataTypeConverter.groupsToFoGroups(groups, false);
	}
	
	public FoGroup addGroup(FoGroup foGroup){
		FODriver driver = getFoDriver();
		GroupAdaptor ga = driver.getGroupAdaptor();
		
		int newGroupId = ga.storeGroup(foGroup.getName(), foGroup.getIsactiveAsInt());
		
		Group newGroup = ga.fetchGroupById(newGroupId, false);
		
		return DataTypeConverter.groupToFoGroup(newGroup, false);
		
	}
	
	public void deleteGroup(FoGroup foGroup){
		FODriver driver = getFoDriver();
		GroupAdaptor ga = driver.getGroupAdaptor();
		
		Group g = DataTypeConverter.foGroupToGroup(foGroup);
		
		ga.deleteGroup(g);
	}
	
	public FoUser[] getAllUserExceptGroup(FoGroup foGroup){
		FODriver driver = getFoDriver();
		UserAdaptor ua = driver.getUserAdaptor();
		
		User[] users = ua.fetchAllUsersNotInGroup(foGroup.getId());
		
		return DataTypeConverter.usersToFoUsers(users);
	}
	
	public FoUser addUserGroup(FoGroup foGroup, int userId){
		FODriver driver = getFoDriver();
		GroupAdaptor ga = driver.getGroupAdaptor();
		UserAdaptor ua = driver.getUserAdaptor();
		
		ga.addUserToGroup(foGroup.getId(), userId);
		
		User user = ua.fetchUserByID(userId);
		
		return DataTypeConverter.userToFoUser(user);
		
	}
	
	public FoCnSegment[] getCnSegmentsForStudyId(int studyId){
		
		FODriver driver = getFoDriver();
		CnSegmentAdaptor ca = driver.getCnSegmentAdaptor();
		
		CnSegment[] segments = ca.fetchCnSegmentsForStudyId(studyId);
		
		return DataTypeConverter.cnSegmentsToFoCnSegments(segments);
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
		
		return DataTypeConverter.studiesToFostudies(s);
	}
	
	public FoProject[] getProjectsForUser(FoUser user,boolean withChildren, boolean writeOnly) throws Exception {
		FODriver driver = getFoDriver();
		
		GroupAdaptor ga = driver.getGroupAdaptor();
		ProjectAdaptor pa = driver.getProjectAdaptor();
		
		Group[] g = ga.fetchGroupsForUser(user.getId(), false);
		ProjectAccess[] projectAccess = pa.fetchProjectAccessForGroups(g, withChildren, writeOnly);
		
		Project[] p = pa.fetchProjectsForProjectAccess(projectAccess, false);
		
		return DataTypeConverter.projectsToFoProjects(p);
	}
	
	public FoProject[] getAllProjects() throws Exception {
		FODriver driver = getFoDriver();
		ProjectAdaptor pa = driver.getProjectAdaptor();
		
		Project[] projects = pa.fetchAllProjects(false);
		
		return DataTypeConverter.projectsToFoProjects(projects);
	}
	
	public FoProject addFoProject(FoProject foProject){
		FODriver driver = getFoDriver();
		ProjectAdaptor pa = driver.getProjectAdaptor();
		
		int newProjectId = pa.storeProject(foProject.getName(), foProject.getDescription());
		
		Project newProject = pa.fetchProjectById(newProjectId, false);
		
		return DataTypeConverter.projectToFoProject(newProject);
		
	}
	
	public FoGroup[] getAllGroupsExceptProject(FoProject foProject){
		FODriver driver = getFoDriver();
		GroupAdaptor ga = driver.getGroupAdaptor();
		
		Group[] groups = ga.fetchGroupsNotInProject(foProject.getId(), false);
		
		return DataTypeConverter.groupsToFoGroups(groups, false);
	}
	
	public FoProjectAccess[] getProjectAccessForProject(int projectId){
		
		FODriver driver = getFoDriver();
		ProjectAdaptor pa = driver.getProjectAdaptor();
		
		return DataTypeConverter.projectAccessesToFoProjectAccesses(pa.fetchProjectAccessForProject(projectId, true), true);
	}
	
	public FoProjectAccess addAccessToProject(FoProjectAccess foProjectAccess){
		FODriver driver = getFoDriver();
		ProjectAdaptor pa = driver.getProjectAdaptor();
		
		ProjectAccess projectAccess = pa.addGroupAccessToProject(foProjectAccess.getGroupId(), foProjectAccess.getFoProjectId(), foProjectAccess.getAccess());
		
		return DataTypeConverter.projectAccessToFoProjectAccess(projectAccess, true);
	}
	
	public FoPlatform[] getAllPlatforms(){
		FODriver driver = getFoDriver();
		PlatformAdaptor pa = driver.getPlatformAdaptor();
		
		Platform[] platforms = pa.fetchAllPlatforms();
		
		return DataTypeConverter.platformsToFoPlatforms(platforms);
	}
	
	public FoOrgan addOrgan(FoOrgan foOrgan){
		FODriver driver = getFoDriver();
		OrganAdaptor oa = driver.getOrganAdaptor();
		
		int id = oa.storeOrgan(DataTypeConverter.foOrganToOrgan(foOrgan));
		
		Organ o = oa.fetchOrganById(id);
		
		return DataTypeConverter.organToFoOrgan(o);
	}
	
	public FoOrgan[] getOrgans(boolean enabled){
		FODriver driver = getFoDriver();
		OrganAdaptor oa = driver.getOrganAdaptor();
		
		Organ[] organs = oa.fetchOrgans(enabled);
		
		return DataTypeConverter.organsToFoOrgans(organs);
	}
	
	public FoOrgan[] getAllOrgans(){
		
		FODriver driver = getFoDriver();
		OrganAdaptor oa = driver.getOrganAdaptor();
		
		Organ[] organs = oa.fetchAllOrgans();
		
		return DataTypeConverter.organsToFoOrgans(organs);
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
		
		int id = pa.storeProperty(DataTypeConverter.foPropertyToProperty(foProperty));
		
		Property p = pa.fetchPropertyById(id);
		
		return DataTypeConverter.propertyToFoProperty(p);
	}
	
	public FoProperty[] getProperties(boolean enabled){
		FODriver driver = getFoDriver();
		PropertyAdaptor pa = driver.getPropertyAdaptor();
		
		Property[] properties = pa.fetchProperties(true);
		
		return DataTypeConverter.propertiesToFoProperties(properties);
	}
	
	public FoProperty[] getAllProperties(){
		FODriver driver = getFoDriver();
		PropertyAdaptor pa = driver.getPropertyAdaptor();
		
		Property[] properties = pa.fetchAllProperties();
		
		return DataTypeConverter.propertiesToFoProperties(properties);
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
	
		int id = pa.storePlatform(DataTypeConverter.foPlatformToPlatform(foPlatform));
		
		Platform p = pa.fetchPlatformById(id);
		
		return DataTypeConverter.platformToFoPlatform(p);
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