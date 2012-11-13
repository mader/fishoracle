/*
  Copyright (c) 2012 Malte Mader <mader@zbh.uni-hamburg.de>
  Copyright (c) 2012 Center for Bioinformatics, University of Hamburg

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

import de.unihamburg.zbh.fishoracle.client.data.FoSegment;
import de.unihamburg.zbh.fishoracle.client.data.FoEnsemblDBs;
import de.unihamburg.zbh.fishoracle.client.data.FoGroup;
import de.unihamburg.zbh.fishoracle.client.data.FoLocation;
import de.unihamburg.zbh.fishoracle.client.data.FoOrgan;
import de.unihamburg.zbh.fishoracle.client.data.FoPlatform;
import de.unihamburg.zbh.fishoracle.client.data.FoProject;
import de.unihamburg.zbh.fishoracle.client.data.FoProjectAccess;
import de.unihamburg.zbh.fishoracle.client.data.FoProperty;
import de.unihamburg.zbh.fishoracle.client.data.FoSNPMutation;
import de.unihamburg.zbh.fishoracle.client.data.FoStudy;
import de.unihamburg.zbh.fishoracle.client.data.FoTissueSample;
import de.unihamburg.zbh.fishoracle.client.data.FoUser;
import de.unihamburg.zbh.fishoracle_db_api.data.EnsemblDBs;
import de.unihamburg.zbh.fishoracle_db_api.data.Group;
import de.unihamburg.zbh.fishoracle_db_api.data.Location;
import de.unihamburg.zbh.fishoracle_db_api.data.Organ;
import de.unihamburg.zbh.fishoracle_db_api.data.Platform;
import de.unihamburg.zbh.fishoracle_db_api.data.Project;
import de.unihamburg.zbh.fishoracle_db_api.data.ProjectAccess;
import de.unihamburg.zbh.fishoracle_db_api.data.Property;
import de.unihamburg.zbh.fishoracle_db_api.data.SNPMutation;
import de.unihamburg.zbh.fishoracle_db_api.data.Segment;
import de.unihamburg.zbh.fishoracle_db_api.data.Study;
import de.unihamburg.zbh.fishoracle_db_api.data.TissueSample;
import de.unihamburg.zbh.fishoracle_db_api.data.User;

//TODO Tests.
public class DataTypeConverter {

	public DataTypeConverter() {
	}

	public static EnsemblDBs[] foEdbssToEdbss(FoEnsemblDBs[] foEdbs){
		EnsemblDBs[] edbs = new EnsemblDBs[foEdbs.length];
		
		for(int i=0; i < foEdbs.length; i++){
			edbs[i] = foEdbsToEdbs(foEdbs[i]);
		}
		return edbs;
	}
	
	public static EnsemblDBs foEdbsToEdbs(FoEnsemblDBs foEdbs){
		EnsemblDBs edbs = new EnsemblDBs(foEdbs.getId(),
									foEdbs.getDBName(),
									foEdbs.getLabel(),
									foEdbs.getVersion());
		return edbs;
	}
	
	public static FoEnsemblDBs[] edbssToFoEdbss(EnsemblDBs[] edbs){
		FoEnsemblDBs[] foEdbs = new FoEnsemblDBs[edbs.length];
		
		for(int i=0; i < edbs.length; i++){
			foEdbs[i] = edbsToFoEdbs(edbs[i]);
		}
		return foEdbs;
	}
	
	public static FoEnsemblDBs edbsToFoEdbs(EnsemblDBs edbs){
		FoEnsemblDBs foEdbs = new FoEnsemblDBs(edbs.getId(),
									edbs.getDBName(),
									edbs.getLabel(),
									edbs.getVersion());
		return foEdbs;
	}
	
	public static FoProjectAccess[] projectAccessesToFoProjectAccesses(ProjectAccess[] projectAccess, boolean withChildren){
		FoProjectAccess[] foProjectAccess = new FoProjectAccess[projectAccess.length];
		
		for(int i=0; i < projectAccess.length; i++){
			foProjectAccess[i] = projectAccessToFoProjectAccess(projectAccess[i], withChildren);
		}
		return foProjectAccess;
	}
	
	public static FoProjectAccess projectAccessToFoProjectAccess(ProjectAccess projectAccess, boolean withChildren){
		
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
	
	public static FoLocation locationToFoLocation(Location loc){
		FoLocation foLocation = new FoLocation(loc.getId(),
												loc.getChromosome(),
												loc.getStart(),
												loc.getEnd());
		return foLocation;
	}
	
	
	public static Location foLocationToLocation(FoLocation foLoc){
		Location loc = new Location(foLoc.getId(),
										foLoc.getChromosome(),
										foLoc.getStart(),
										foLoc.getEnd());
		return loc;
	}
	
	public static FoSegment[] segmentsToFoSegments(Segment[] segments){
		FoSegment[] foSegments = new FoSegment[segments.length];
		
		for(int i=0; i < segments.length; i++){
			foSegments[i] = segmentToFoSegment(segments[i]);
		}
		return foSegments;
	}
	
	public static FoSegment segmentToFoSegment(Segment segment){
		FoSegment foSegment = new FoSegment(segment.getId(),
											locationToFoLocation(segment.getLocation()),
											segment.getType());
		
		foSegment.setMean(segment.getMean());
		foSegment.setNumberOfMarkers(segment.getNumberOfMarkers());
		foSegment.setStatus(segment.getStatus());
		foSegment.setStatusScore(segment.getStatusScore());
		
		if(segment.getStudyName() != null){
			foSegment.setStudyName(segment.getStudyName());
		}
		return foSegment;
	}
	
	public static FoSNPMutation[] snpMutationToFoSNPMutation(SNPMutation[] mutations){
		FoSNPMutation[] foMutations = new FoSNPMutation[mutations.length];
		
		for(int i=0; i < mutations.length; i++){
			foMutations[i] = foSNPMutationToFoSNPMutation(mutations[i]);
		}
		return foMutations;
	}
	
	public static FoSNPMutation foSNPMutationToFoSNPMutation(SNPMutation mutation){
		FoSNPMutation foMutation = new FoSNPMutation(mutation.getId(),
											locationToFoLocation(mutation.getLocation()),
											mutation.getDbSnpId(),
											mutation.getRef(),
											mutation.getAlt(),
											mutation.getQuality(),
											mutation.getSomatic(),
											mutation.getConfidence(),
											mutation.getSnpTool());
		
		foMutation.setStudyId(mutation.getStudyId());
		return foMutation;
	}
	
	public static TissueSample[] foTissueSamplesToTissueSamples(FoTissueSample[] foTissues){
		
		TissueSample[] tissues = new TissueSample[foTissues.length];
		
		for(int i=0; i < foTissues.length; i++){
			tissues[i] = foTissueSampleToTissueSample(foTissues[i]);
		}
		
		return tissues;
	}
	
	public static TissueSample foTissueSampleToTissueSample(FoTissueSample foTissue){
		
		Organ organ = foOrganToOrgan(foTissue.getOrgan());
		Property[] properties = foPropertiesToProperties(foTissue.getProperties()); 
		
		TissueSample tissue = new TissueSample(foTissue.getId(),
												organ,
												properties);
		return tissue;
	}
	
	public static FoTissueSample[] tissueSamplesToFoTissueSamples(TissueSample[] tissues){
	
		FoTissueSample[] foTissues = new FoTissueSample[tissues.length];
		
		for(int i=0; i < tissues.length; i++){
			foTissues[i] = tissueSampleToFoTissueSample(tissues[i]);
		}
		
		return foTissues;
	}
	
	public static FoTissueSample tissueSampleToFoTissueSample(TissueSample tissue){
		
		FoOrgan foOrgan = organToFoOrgan(tissue.getOrgan());
		FoProperty[] foProperties = propertiesToFoProperties(tissue.getProperties()); 
		
		FoTissueSample foTissue = new FoTissueSample(tissue.getId(),
													foOrgan,
													foProperties);
		return foTissue;
	}
	
	public static Property[] foPropertiesToProperties(FoProperty[] foProperties){
		Property[] properties = new Property[foProperties.length];
		
		for(int i=0; i < properties.length; i++){
			properties[i] = foPropertyToProperty(foProperties[i]);
		}
		return properties;
	}
	
	public static Property foPropertyToProperty(FoProperty foProperty){
		Property property = new Property(foProperty.getId(),
											foProperty.getLabel(),
											foProperty.getType(),
											foProperty.getActivty());
		return property;
	}
	
	public static FoProperty[] propertiesToFoProperties(Property[] properties){
		FoProperty[] foProperties = new FoProperty[properties.length];
		
		for(int i=0; i < properties.length; i++){
			foProperties[i] = propertyToFoProperty(properties[i]);
		}
		return foProperties;
	}
	
	public static FoProperty propertyToFoProperty(Property property){
		FoProperty foProperty = new FoProperty(property.getId(),
											property.getLabel(),
											property.getType(),
											property.getActivty());
		return foProperty;
	}
	
	public static Organ[] foOrgansToOrgans(FoOrgan[] foOrgans){
		Organ[] organs = new Organ[foOrgans.length];
		
		for(int i=0; i < foOrgans.length; i++){
			organs[i] = foOrganToOrgan(foOrgans[i]);
		}
		return organs;
	}
	
	public static Organ foOrganToOrgan(FoOrgan foOrgan){
		Organ organ = new Organ(foOrgan.getId(),
									foOrgan.getLabel(),
									foOrgan.getType(),
									foOrgan.getActivty());
		return organ;
	}
	
	public static FoOrgan[] organsToFoOrgans(Organ[] organs){
		FoOrgan[] foOrgans = new FoOrgan[organs.length];
		
		for(int i=0; i < organs.length; i++){
			foOrgans[i] = organToFoOrgan(organs[i]);
		}
		return foOrgans;
	}
	
	public static FoOrgan organToFoOrgan(Organ organ){
		FoOrgan foOrgan = new FoOrgan(organ.getId(),
									organ.getLabel(),
									organ.getType(),
									organ.getActivty());
		return foOrgan;
	}
	
	public static Platform[] foPlatformsToPlatforms(FoPlatform[] foPlatforms){
		Platform[] platforms = new Platform[foPlatforms.length];
		
		for(int i=0; i < platforms.length; i++){
			platforms[i] = foPlatformToPlatform(foPlatforms[i]);
		}
		return platforms;
	}
	
	public static Platform foPlatformToPlatform(FoPlatform foPlatform){
		Platform platform = new Platform(foPlatform.getId(),
											foPlatform.getName(),
											foPlatform.getType());
		return platform;
	}
	
	public static FoPlatform[] platformsToFoPlatforms(Platform[] platforms){
		FoPlatform[] foPlatforms = new FoPlatform[platforms.length];
		
		for(int i=0; i < platforms.length; i++){
			foPlatforms[i] = platformToFoPlatform(platforms[i]);
		}
		return foPlatforms;
	}
	
	public static  FoPlatform platformToFoPlatform(Platform platform){
		FoPlatform foPlatform = new FoPlatform(platform.getId(),
												platform.getName(),
												platform.getType());
		return foPlatform;
	}
	
	public static Study[] foStudiesToStudies(FoStudy[] foStudies){
		Study[] studies = new Study[foStudies.length];
		
		for(int i=0; i < foStudies.length; i++){
			studies[i] = foStudyToStudy(foStudies[i]);
		}
		return studies;
		
	}
	
	public static Study foStudyToStudy(FoStudy foStudy){
		
		Study study = new Study(foStudy.getId(),
									foStudy.getDate(),
									foStudy.getName(),
									foStudy.getAssembly(),
									foStudy.getDescription(),
									foStudy.getUserId());
		
		if(foStudy.getTissue() != null){
			study.setTissue(foTissueSampleToTissueSample(foStudy.getTissue()));
		} else {
			study.setOrganId(foStudy.getOrganId());
			study.setPropertyIds(foStudy.getPropertyIds());
		}
		
		study.setHasSegment(foStudy.isHasSegment());
		study.setHasMutation(foStudy.isHasMutation());
		study.setHasTranslocation(foStudy.isHasTranslocation());
		study.setHasGeneric(foStudy.isHasGeneric());
		
		return study;
	}
	
	public static FoStudy[] studiesToFostudies(Study[] studies){
		FoStudy[] foStudies = new FoStudy[studies.length];
		
		for(int i=0; i < studies.length; i++){
			foStudies[i] = studyToFoStudy(studies[i]);
		}
		return foStudies;
		
	}
	
	public static FoStudy studyToFoStudy(Study study){
		
		FoStudy foStudy = new FoStudy(study.getId(),
										study.getDate(),
										study.getName(),
										study.getAssembly(),
										study.getDescription(),
										study.getUserId());
		
		if(study.getTissue() != null){
			foStudy.setTissue(tissueSampleToFoTissueSample(study.getTissue()));
		} else {
			foStudy.setOrganId(study.getOrganId());
			foStudy.setPropertyIds(study.getPropertyIds());
		}
		
		foStudy.setHasSegment(study.isHasSegment());
		foStudy.setHasMutation(study.isHasMutation());
		foStudy.setHasTranslocation(study.isHasTranslocation());
		foStudy.setHasGeneric(study.isHasGeneric());		
		
		return foStudy;
	}
	
	public static FoProject[] projectsToFoProjects(Project[] projects){
		FoProject[] foProjects = new FoProject[projects.length];
		
		for(int i=0; i < projects.length; i++){
			foProjects[i] = projectToFoProject(projects[i]);
		}
		return foProjects;
	}
	
	public static FoProject projectToFoProject(Project project){
		
		FoProject foProject = new FoProject(project.getId(),
											project.getName(),
											project.getDescription());
		
		if(project.getStudies() != null){
			foProject.setStudies(studiesToFostudies(project.getStudies()));
		}
		
		if(project.getProjectAccess() != null){
			foProject.setProjectAccess(projectAccessesToFoProjectAccesses(project.getProjectAccess(),true));
		}
		return foProject;
	}
	
	public static Group[] foGroupsToGroups(FoGroup[] foGroups){
		
		Group[] groups = new Group[foGroups.length];
		
		for(int i=0; i < groups.length; i++){
			groups[i] = foGroupToGroup(foGroups[i]);
		}
		return groups;
	}
	
	public static Group foGroupToGroup(FoGroup foGroup){
		
		Group group = new Group(foGroup.getId(),
								foGroup.getName(),
								foGroup.isIsactive());
		
		if(foGroup.getUsers() != null){
			group.setUsers(foUsersToUsers(foGroup.getUsers()));
		}
		return group;
	}
	
	public static FoGroup[] groupsToFoGroups(Group[] groups, boolean withChildren){
		
		FoGroup[] foGroups = new FoGroup[groups.length];
		
		for(int i=0; i < groups.length; i++){
			foGroups[i] = groupToFoGroup(groups[i], withChildren);
		}
		return foGroups;
	}
	
	public static FoGroup groupToFoGroup(Group group, boolean withChildren){
		
		FoGroup foGroup = new FoGroup(group.getId(),
								group.getName(),
								group.isIsactive());
		
		if(withChildren){
			foGroup.setUsers(usersToFoUsers(group.getUsers()));
		}
		return foGroup;
	}
	
	public static User[] foUsersToUsers(FoUser[] foUsers){
		
		User[] users = new User[foUsers.length];
		
		for(int i=0; i < users.length; i++){
			users[i] = foUserToUser(foUsers[i]);
		}
		
		return users;
	}
	
	public static User foUserToUser(FoUser foUser){
		
		User user = new User(foUser.getId(),
				foUser.getFirstName(),
				foUser.getLastName(),
				foUser.getUserName(),
				foUser.getEmail(),
				foUser.getIsActive(),
				foUser.getIsAdmin());
		
		return user;
	}
	
	public static FoUser[] usersToFoUsers(User[] users){
		
		FoUser[] foUsers = new FoUser[users.length];
		
		for(int i=0; i < users.length; i++){
			foUsers[i] = userToFoUser(users[i]);
		}
		return foUsers;
	}
	
	public static FoUser userToFoUser(User user){
		
		FoUser foUser = new FoUser(user.getId(),
				user.getFirstName(),
				user.getLastName(),
				user.getUserName(),
				user.getEmail(),
				user.getIsActive(),
				user.getIsAdmin());
		
		return foUser;
	}	
}