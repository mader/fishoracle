package de.unihamburg.zbh.fishoracle.server.test;


import org.ensembl.datamodel.Location;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.unihamburg.zbh.fishoracle.client.data.CopyNumberChange;
import de.unihamburg.zbh.fishoracle.client.data.Gen;
import de.unihamburg.zbh.fishoracle.server.data.*;


public class TestDBQuery {

	private CopyNumberChange[] amps, amps2;
	private Gen[] genes, genes2;
	private Karyoband[] kbands, kbands2;
	private Location maxLoc1, maxLoc2, maxLoc3, maxLoc4;
	private Location ampLoc1, ampLoc2,ampLoc3, ampLoc4, ampLoc5;
	private Location ampLoc11, ampLoc12,ampLoc13, ampLoc14, ampLoc15;
	
	@Before
	public void setUp() throws Exception {
		
		amps = new CopyNumberChange[5];
		
		amps[0] = new CopyNumberChange("AMP00.00", "2", 17000000, 18000000, true);
		amps[1] = new CopyNumberChange("AMP00.01", "2", 17300000, 17700000, true);
		amps[2] = new CopyNumberChange("AMP00.02", "2", 16500000, 18500000, true);
		amps[3] = new CopyNumberChange("AMP00.03", "2", 16500000, 17400000, true);
		amps[4] = new CopyNumberChange("AMP00.04", "2", 17500000, 19500000, true);
		
		amps2 = new CopyNumberChange[5];
		
		amps2[0] = new CopyNumberChange("AMP01.01", "3", 17000000, 18000000, true);
		amps2[1] = new CopyNumberChange("AMP01.02", "3", 17300000, 17700000, true);
		amps2[2] = new CopyNumberChange("AMP01.03", "3", 16500000, 18500000, true);
		amps2[3] = new CopyNumberChange("AMP01.04", "3", 16500000, 17400000, true);
		amps2[4] = new CopyNumberChange("AMP01.05", "3", 17500000, 19500000, true);
		
		genes = new Gen[21];
		
		genes[0] = new Gen("AC104623.4-1", "2", 16587556, 16587937, "-");
		genes[1] = new Gen("AC104623.4-2","2", 16592320, 16592644, "+");
		genes[2] = new Gen("FAM49A", "2", 16594890, 16710580, "-");
		genes[3] = new Gen("AC008069.3-2", "2", 16886780, 16886862, "-");
		genes[4] = new Gen("AC008069.2", "2", 16898568, 16900032, "-");
		genes[5] = new Gen("AC008069.3-3", "2", 16899667, 16899756, "+");
		genes[6] = new Gen("AC008069.3-1", "2", 16899673, 16899756, "+");
		genes[7] = new Gen("7SK", "2", 17167589, 17167888, "-");
		genes[8] = new Gen("AC096556.1-1", "2", 17167679, 17167888, "-");
		genes[9] = new Gen("AC068614.5", "2", 17329045, 17329651, "+");
		genes[10] = new Gen("AC092843.1", "2", 17429839, 17431165, "+");
		genes[11] = new Gen("RAD51AP2", "2", 17555331, 17575162, "-");
		genes[12] = new Gen("VSNL1", "2", 17583874,17701766 , "+");
		genes[13] = new Gen("SMC6", "2", 17708562, 17844943, "-");
		genes[14] = new Gen("GEN1", "2", 17798658, 17829460, "+");
		genes[15] = new Gen("MSGN1", "2",17861267 , 17861848, "+");
		genes[16] = new Gen("KCNS3", "2", 17922595, 17977705, "+");
		genes[17] = new Gen("SNORA40", "2", 18085353, 18085480, "+");
		genes[18] = new Gen("RDH14", "2", 18599489, 18634319, "-");
		genes[19] = new Gen("U6", "2", 18628114, 18628215, "-");
		genes[20] = new Gen("OSR1", "2", 19414728, 19421865, "-");
		
		genes2 = new Gen[9];
		
		genes2[0] = new Gen("RFTN1", "3",16332357 , 16530226, "-");
		genes2[1] = new Gen("DAZL", "3", 16603307, 16622010, "-");
		genes2[2] = new Gen("PLCL2", "3", 16901456, 17107090, "+");
		genes2[3] = new Gen("TBC1D5", "3", 17174903,17757403 , "-");
		genes2[4] = new Gen("AC104451.2", "3", 17716396, 17716468, "-");
		genes2[5] = new Gen("AC104297.1", "3", 17888229, 17889046, "-");
		genes2[6] = new Gen("SATB1", "3", 18364439, 18441828, "-");
		genes2[7] = new Gen("AC099053.2", "3", 18971440, 18971545, "-");
		genes2[8] = new Gen("KCNH8", "3", 19165021, 19552137, "+");
		
		kbands = new Karyoband[3];
		
		kbands[0] = new Karyoband("2", "p24.3", 12800001, 17000000);
		kbands[1] = new Karyoband("2", "p24.2", 17000001, 19100000);
		kbands[2] = new Karyoband("2", "p24.1", 19100001, 23900000);
		
		kbands2 = new Karyoband[1];
		
		kbands2[0] = new Karyoband("3", "p24.3", 14700001, 23800000);
		
		maxLoc1 = new Location("chromosome:2:16500000-19500000");
		maxLoc2 = new Location("chromosome:3:16500000-19500000");
		maxLoc3 = new Location("chromosome:2:16500000-18500000");
		maxLoc4 = new Location("chromosome:3:16500000-18500000");
		
		ampLoc1 = new Location("chromosome:2:17000000-18000000");
		ampLoc2 = new Location("chromosome:2:17300000-17700000");
		ampLoc3 = new Location("chromosome:2:16500000-18500000");
		ampLoc4 = new Location("chromosome:2:16500000-17400000");
		ampLoc5 = new Location("chromosome:2:17500000-19500000");
		
		ampLoc11 = new Location("chromosome:3:17000000-18000000");
		ampLoc12 = new Location("chromosome:3:17300000-17700000");
		ampLoc13 = new Location("chromosome:3:16500000-18500000");
		ampLoc14 = new Location("chromosome:3:16500000-17400000");
		ampLoc15 = new Location("chromosome:3:17500000-19500000");
		
	}
	
	@Test
	public void testGetLocationForAmpliconStableId(){
		
		DBQuery db = new DBQuery(System.getProperty("user.dir") + "/war/");
		
		Location testloc1 = db.getLocationForCNCId("AMP00.01");
		Location testloc2 = db.getLocationForCNCId("AMP00.02");
		Location testloc3 = db.getLocationForCNCId("AMP00.03");
		Location testloc4 = db.getLocationForCNCId("AMP00.04");
		Location testloc5 = db.getLocationForCNCId("AMP00.05");
		
		Assert.assertEquals(testloc1.getSeqRegionName(), ampLoc1.getSeqRegionName());
		Assert.assertEquals(testloc1.getStart(), ampLoc1.getStart());
		Assert.assertEquals(testloc1.getEnd(), ampLoc1.getEnd());
		
		Assert.assertEquals(testloc2.getSeqRegionName(), ampLoc2.getSeqRegionName());
		Assert.assertEquals(testloc2.getStart(), ampLoc2.getStart());
		Assert.assertEquals(testloc2.getEnd(), ampLoc2.getEnd());
		
		Assert.assertEquals(testloc3.getSeqRegionName(), ampLoc3.getSeqRegionName());
		Assert.assertEquals(testloc3.getStart(), ampLoc3.getStart());
		Assert.assertEquals(testloc3.getEnd(), ampLoc3.getEnd());
		
		Assert.assertEquals(testloc4.getSeqRegionName(), ampLoc4.getSeqRegionName());
		Assert.assertEquals(testloc4.getStart(), ampLoc4.getStart());
		Assert.assertEquals(testloc4.getEnd(), ampLoc4.getEnd());
		
		Assert.assertEquals(testloc5.getSeqRegionName(), ampLoc5.getSeqRegionName());
		Assert.assertEquals(testloc5.getStart(), ampLoc5.getStart());
		Assert.assertEquals(testloc5.getEnd(), ampLoc5.getEnd());
		
		
		
		Location testloc11 = db.getLocationForCNCId("AMP01.01");
		Location testloc12 = db.getLocationForCNCId("AMP01.02");
		Location testloc13 = db.getLocationForCNCId("AMP01.03");
		Location testloc14 = db.getLocationForCNCId("AMP01.04");
		Location testloc15 = db.getLocationForCNCId("AMP01.05");
		
		Assert.assertEquals(testloc11.getSeqRegionName(), ampLoc11.getSeqRegionName());
		Assert.assertEquals(testloc11.getStart(), ampLoc11.getStart());
		Assert.assertEquals(testloc11.getEnd(), ampLoc11.getEnd());
		
		Assert.assertEquals(testloc12.getSeqRegionName(), ampLoc12.getSeqRegionName());
		Assert.assertEquals(testloc12.getStart(), ampLoc12.getStart());
		Assert.assertEquals(testloc12.getEnd(), ampLoc12.getEnd());
		
		Assert.assertEquals(testloc13.getSeqRegionName(), ampLoc13.getSeqRegionName());
		Assert.assertEquals(testloc13.getStart(), ampLoc13.getStart());
		Assert.assertEquals(testloc13.getEnd(), ampLoc13.getEnd());
		
		Assert.assertEquals(testloc14.getSeqRegionName(), ampLoc14.getSeqRegionName());
		Assert.assertEquals(testloc14.getStart(), ampLoc14.getStart());
		Assert.assertEquals(testloc14.getEnd(), ampLoc14.getEnd());
		
		Assert.assertEquals(testloc15.getSeqRegionName(), ampLoc15.getSeqRegionName());
		Assert.assertEquals(testloc15.getStart(), ampLoc15.getStart());
		Assert.assertEquals(testloc15.getEnd(), ampLoc15.getEnd());
		
	}
	
	
	@Test
	public void testGetMaxAmpliconRange(){
		
		DBQuery db = new DBQuery(System.getProperty("user.dir") + "/war/");
		
		Location testloc = db.getMaxCNCRange("2", 16500000, 18500000, true);
		Location testloc2 = db.getMaxCNCRange("3", 16500000, 18500000, true);
		Location testloc3 = db.getMaxCNCRange("2", 16500000, 17400000, true);
		Location testloc4 = db.getMaxCNCRange("3", 16500000, 16900000, true);
		
		Assert.assertEquals(testloc.getSeqRegionName(), maxLoc1.getSeqRegionName());
		Assert.assertEquals(testloc.getStart(), maxLoc1.getStart());
		Assert.assertEquals(testloc.getEnd(), maxLoc1.getEnd());
		
		Assert.assertEquals(testloc2.getSeqRegionName(), maxLoc2.getSeqRegionName());
		Assert.assertEquals(testloc2.getStart(), maxLoc2.getStart());
		Assert.assertEquals(testloc2.getEnd(), maxLoc2.getEnd());
		
		Assert.assertEquals(testloc3.getSeqRegionName(), maxLoc3.getSeqRegionName());
		Assert.assertEquals(testloc3.getStart(), maxLoc3.getStart());
		Assert.assertEquals(testloc3.getEnd(), maxLoc3.getEnd());
		
		Assert.assertEquals(testloc4.getSeqRegionName(), maxLoc4.getSeqRegionName());
		Assert.assertEquals(testloc4.getStart(), maxLoc4.getStart());
		Assert.assertEquals(testloc4.getEnd(), maxLoc4.getEnd());
	}
	
	@Test
	public void testGetEsemblKaryotypes(){
		
		DBQuery db = new DBQuery(System.getProperty("user.dir") + "/war/");
		Karyoband[] testkbands = null;
		Karyoband[] testkbands2 = null;
		
		testkbands = db.getEnsemblKaryotypes("2", 16500000, 19500000);
		
		Assert.assertEquals(testkbands[0].getChr(), kbands[0].getChr());
		Assert.assertEquals(testkbands[0].getBand(), kbands[0].getBand());
		Assert.assertEquals(testkbands[0].getStart(), kbands[0].getStart());
		Assert.assertEquals(testkbands[0].getEnd(), kbands[0].getEnd());
		
		Assert.assertEquals(testkbands[1].getChr(), kbands[1].getChr());
		Assert.assertEquals(testkbands[1].getBand(), kbands[1].getBand());
		Assert.assertEquals(testkbands[1].getStart(), kbands[1].getStart());
		Assert.assertEquals(testkbands[1].getEnd(), kbands[1].getEnd());
		
		Assert.assertEquals(testkbands[2].getChr(), kbands[2].getChr());
		Assert.assertEquals(testkbands[2].getBand(), kbands[2].getBand());
		Assert.assertEquals(testkbands[2].getStart(), kbands[2].getStart());
		Assert.assertEquals(testkbands[2].getEnd(), kbands[2].getEnd());
		
		
		testkbands2 = db.getEnsemblKaryotypes("3", 16500000, 19500000);
		
		Assert.assertEquals(testkbands2[0].getChr(), kbands2[0].getChr());
		Assert.assertEquals(testkbands2[0].getBand(), kbands2[0].getBand());
		Assert.assertEquals(testkbands2[0].getStart(), kbands2[0].getStart());
		Assert.assertEquals(testkbands2[0].getEnd(), kbands2[0].getEnd());
	}
	
	@Test
	public void testGetEnsemblGenes(){
		
		DBQuery db = new DBQuery(System.getProperty("user.dir") + "/war/");
		Gen[] testgenes = null;
		Gen[] testgenes2 = null;
		
		testgenes = db.getEnsembleGenes("2",16500000, 19500000);
		
		Assert.assertEquals(testgenes[0].getGenName(), genes[0].getGenName());
		Assert.assertEquals(testgenes[0].getStart(), genes[0].getStart());
		Assert.assertEquals(testgenes[0].getEnd(), genes[0].getEnd());
		
		Assert.assertEquals(testgenes[1].getGenName(), genes[1].getGenName());
		Assert.assertEquals(testgenes[1].getStart(), genes[1].getStart());
		Assert.assertEquals(testgenes[1].getEnd(), genes[1].getEnd());

		Assert.assertEquals(testgenes[2].getGenName(), genes[2].getGenName());
		Assert.assertEquals(testgenes[2].getStart(), genes[2].getStart());
		Assert.assertEquals(testgenes[2].getEnd(), genes[2].getEnd());
		
		Assert.assertEquals(testgenes[3].getGenName(), genes[3].getGenName());
		Assert.assertEquals(testgenes[3].getStart(), genes[3].getStart());
		Assert.assertEquals(testgenes[3].getEnd(), genes[3].getEnd());
		
		Assert.assertEquals(testgenes[4].getGenName(), genes[4].getGenName());
		Assert.assertEquals(testgenes[4].getStart(), genes[4].getStart());
		Assert.assertEquals(testgenes[4].getEnd(), genes[4].getEnd());
		
		Assert.assertEquals(testgenes[5].getGenName(), genes[5].getGenName());
		Assert.assertEquals(testgenes[5].getStart(), genes[5].getStart());
		Assert.assertEquals(testgenes[5].getEnd(), genes[5].getEnd());
		
		Assert.assertEquals(testgenes[6].getGenName(), genes[6].getGenName());
		Assert.assertEquals(testgenes[6].getStart(), genes[6].getStart());
		Assert.assertEquals(testgenes[6].getEnd(), genes[6].getEnd());
		
		Assert.assertEquals(testgenes[7].getGenName(), genes[7].getGenName());
		Assert.assertEquals(testgenes[7].getStart(), genes[7].getStart());
		Assert.assertEquals(testgenes[7].getEnd(), genes[7].getEnd());
		
		Assert.assertEquals(testgenes[8].getGenName(), genes[8].getGenName());
		Assert.assertEquals(testgenes[8].getStart(), genes[8].getStart());
		Assert.assertEquals(testgenes[8].getEnd(), genes[8].getEnd());
		
		Assert.assertEquals(testgenes[9].getGenName(), genes[9].getGenName());
		Assert.assertEquals(testgenes[9].getStart(), genes[9].getStart());
		Assert.assertEquals(testgenes[9].getEnd(), genes[9].getEnd());
		
		Assert.assertEquals(testgenes[10].getGenName(), genes[10].getGenName());
		Assert.assertEquals(testgenes[10].getStart(), genes[10].getStart());
		Assert.assertEquals(testgenes[10].getEnd(), genes[10].getEnd());
		
		Assert.assertEquals(testgenes[11].getGenName(), genes[11].getGenName());
		Assert.assertEquals(testgenes[11].getStart(), genes[11].getStart());
		Assert.assertEquals(testgenes[11].getEnd(), genes[11].getEnd());
		
		Assert.assertEquals(testgenes[12].getGenName(), genes[12].getGenName());
		Assert.assertEquals(testgenes[12].getStart(), genes[12].getStart());
		Assert.assertEquals(testgenes[12].getEnd(), genes[12].getEnd());
		
		Assert.assertEquals(testgenes[13].getGenName(), genes[13].getGenName());
		Assert.assertEquals(testgenes[13].getStart(), genes[13].getStart());
		Assert.assertEquals(testgenes[13].getEnd(), genes[13].getEnd());
		
		Assert.assertEquals(testgenes[14].getGenName(), genes[14].getGenName());
		Assert.assertEquals(testgenes[14].getStart(), genes[14].getStart());
		Assert.assertEquals(testgenes[14].getEnd(), genes[14].getEnd());
		
		Assert.assertEquals(testgenes[15].getGenName(), genes[15].getGenName());
		Assert.assertEquals(testgenes[15].getStart(), genes[15].getStart());
		Assert.assertEquals(testgenes[15].getEnd(), genes[15].getEnd());
		
		Assert.assertEquals(testgenes[16].getGenName(), genes[16].getGenName());
		Assert.assertEquals(testgenes[16].getStart(), genes[16].getStart());
		Assert.assertEquals(testgenes[16].getEnd(), genes[16].getEnd());
		
		Assert.assertEquals(testgenes[17].getGenName(), genes[17].getGenName());
		Assert.assertEquals(testgenes[17].getStart(), genes[17].getStart());
		Assert.assertEquals(testgenes[17].getEnd(), genes[17].getEnd());
		
		Assert.assertEquals(testgenes[18].getGenName(), genes[18].getGenName());
		Assert.assertEquals(testgenes[18].getStart(), genes[18].getStart());
		Assert.assertEquals(testgenes[18].getEnd(), genes[18].getEnd());
		
		Assert.assertEquals(testgenes[19].getGenName(), genes[19].getGenName());
		Assert.assertEquals(testgenes[19].getStart(), genes[19].getStart());
		Assert.assertEquals(testgenes[19].getEnd(), genes[19].getEnd());
		
		Assert.assertEquals(testgenes[20].getGenName(), genes[20].getGenName());
		Assert.assertEquals(testgenes[20].getStart(), genes[20].getStart());
		Assert.assertEquals(testgenes[20].getEnd(), genes[20].getEnd());
		
		
		testgenes2 = db.getEnsembleGenes("3",16500000, 19500000);
		
		Assert.assertEquals(testgenes2[0].getGenName(), genes2[0].getGenName());
		Assert.assertEquals(testgenes2[0].getStart(), genes2[0].getStart());
		Assert.assertEquals(testgenes2[0].getEnd(), genes2[0].getEnd());
		
		Assert.assertEquals(testgenes2[1].getGenName(), genes2[1].getGenName());
		Assert.assertEquals(testgenes2[1].getStart(), genes2[1].getStart());
		Assert.assertEquals(testgenes2[1].getEnd(), genes2[1].getEnd());

		Assert.assertEquals(testgenes2[2].getGenName(), genes2[2].getGenName());
		Assert.assertEquals(testgenes2[2].getStart(), genes2[2].getStart());
		Assert.assertEquals(testgenes2[2].getEnd(), genes2[2].getEnd());
		
		Assert.assertEquals(testgenes2[3].getGenName(), genes2[3].getGenName());
		Assert.assertEquals(testgenes2[3].getStart(), genes2[3].getStart());
		Assert.assertEquals(testgenes2[3].getEnd(), genes2[3].getEnd());
		
		Assert.assertEquals(testgenes2[4].getGenName(), genes2[4].getGenName());
		Assert.assertEquals(testgenes2[4].getStart(), genes2[4].getStart());
		Assert.assertEquals(testgenes2[4].getEnd(), genes2[4].getEnd());
		
		Assert.assertEquals(testgenes2[5].getGenName(), genes2[5].getGenName());
		Assert.assertEquals(testgenes2[5].getStart(), genes2[5].getStart());
		Assert.assertEquals(testgenes2[5].getEnd(), genes2[5].getEnd());
		
		Assert.assertEquals(testgenes2[6].getGenName(), genes2[6].getGenName());
		Assert.assertEquals(testgenes2[6].getStart(), genes2[6].getStart());
		Assert.assertEquals(testgenes2[6].getEnd(), genes2[6].getEnd());
		
		Assert.assertEquals(testgenes2[7].getGenName(), genes2[7].getGenName());
		Assert.assertEquals(testgenes2[7].getStart(), genes2[7].getStart());
		Assert.assertEquals(testgenes2[7].getEnd(), genes2[7].getEnd());
		
		Assert.assertEquals(testgenes2[8].getGenName(), genes2[8].getGenName());
		Assert.assertEquals(testgenes2[8].getStart(), genes2[8].getStart());
		Assert.assertEquals(testgenes2[8].getEnd(), genes2[8].getEnd());
		
	}
	
	@Test
	public void testGetAmpliconData(){
		
		DBQuery db = new DBQuery(System.getProperty("user.dir") + "/war/");
		CopyNumberChange[] testamps = null;
		CopyNumberChange[] testamps2 = null;
		
		testamps = db.getCNCData("2", 16500000, 18500000, true);
		
		Assert.assertEquals(testamps[0].getCncStableId(), amps[0].getCncStableId());
		Assert.assertEquals(testamps[0].getChromosome(), amps[0].getChromosome());
		Assert.assertEquals(testamps[0].getStart(), amps[0].getStart());
		Assert.assertEquals(testamps[0].getEnd(), amps[0].getEnd());
		
		Assert.assertEquals(testamps[1].getCncStableId(), amps[1].getCncStableId());
		Assert.assertEquals(testamps[1].getChromosome(), amps[1].getChromosome());
		Assert.assertEquals(testamps[1].getStart(), amps[1].getStart());
		Assert.assertEquals(testamps[1].getEnd(), amps[1].getEnd());
		
		Assert.assertEquals(testamps[2].getCncStableId(), amps[2].getCncStableId());
		Assert.assertEquals(testamps[2].getChromosome(), amps[2].getChromosome());
		Assert.assertEquals(testamps[2].getStart(), amps[2].getStart());
		Assert.assertEquals(testamps[2].getEnd(), amps[2].getEnd());
	
		Assert.assertEquals(testamps[3].getCncStableId(), amps[3].getCncStableId());
		Assert.assertEquals(testamps[3].getChromosome(), amps[3].getChromosome());
		Assert.assertEquals(testamps[3].getStart(), amps[3].getStart());
		Assert.assertEquals(testamps[3].getEnd(), amps[3].getEnd());
		
		Assert.assertEquals(testamps[4].getCncStableId(), amps[4].getCncStableId());
		Assert.assertEquals(testamps[4].getChromosome(), amps[4].getChromosome());
		Assert.assertEquals(testamps[4].getStart(), amps[4].getStart());
		Assert.assertEquals(testamps[4].getEnd(), amps[4].getEnd());
		
		testamps2 = db.getCNCData("3", 16500000, 18500000, true);
		
		Assert.assertEquals(testamps2[0].getCncStableId(), amps2[0].getCncStableId());
		Assert.assertEquals(testamps2[0].getChromosome(), amps2[0].getChromosome());
		Assert.assertEquals(testamps2[0].getStart(), amps2[0].getStart());
		Assert.assertEquals(testamps2[0].getEnd(), amps2[0].getEnd());
		
		Assert.assertEquals(testamps2[1].getCncStableId(), amps2[1].getCncStableId());
		Assert.assertEquals(testamps2[1].getChromosome(), amps2[1].getChromosome());
		Assert.assertEquals(testamps2[1].getStart(), amps2[1].getStart());
		Assert.assertEquals(testamps2[1].getEnd(), amps2[1].getEnd());
		
		Assert.assertEquals(testamps2[2].getCncStableId(), amps2[2].getCncStableId());
		Assert.assertEquals(testamps2[2].getChromosome(), amps2[2].getChromosome());
		Assert.assertEquals(testamps2[2].getStart(), amps2[2].getStart());
		Assert.assertEquals(testamps2[2].getEnd(), amps2[2].getEnd());
	
		Assert.assertEquals(testamps2[3].getCncStableId(), amps2[3].getCncStableId());
		Assert.assertEquals(testamps2[3].getChromosome(), amps2[3].getChromosome());
		Assert.assertEquals(testamps2[3].getStart(), amps2[3].getStart());
		Assert.assertEquals(testamps2[3].getEnd(), amps2[3].getEnd());
		
		Assert.assertEquals(testamps2[4].getCncStableId(), amps2[4].getCncStableId());
		Assert.assertEquals(testamps2[4].getChromosome(), amps2[4].getChromosome());
		Assert.assertEquals(testamps2[4].getStart(), amps2[4].getStart());
		Assert.assertEquals(testamps2[4].getEnd(), amps2[4].getEnd());	
	}
}
