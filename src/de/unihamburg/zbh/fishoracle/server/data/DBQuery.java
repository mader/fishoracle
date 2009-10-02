package de.unihamburg.zbh.fishoracle.server.data;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.*;
import java.text.ParseException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ensembl.datamodel.CoordinateSystem;
import org.ensembl.datamodel.Gene;
import org.ensembl.datamodel.KaryotypeBand;
import org.ensembl.datamodel.Location;

import org.ensembl.driver.AdaptorException;
import org.ensembl.driver.CoreDriver;
import org.ensembl.driver.CoreDriverFactory;
import org.ensembl.driver.KaryotypeBandAdaptor;

import de.unihamburg.zbh.fishoracle.client.data.CopyNumberChange;
import de.unihamburg.zbh.fishoracle.client.data.Gen;

/**
 * Fetches various information from the fish oracle database an gene
 * information from the ensembl database using the ensembl Java API.
 * 
 * */
public class DBQuery {

	//ensembl connection parameters
	private String ehost = null;
	private int eport;
	private String edb = null;
	private String euser = null;
	private String epw = null;
	
	//fish oracle connection parameters
	private String fhost = null;
	private String fdb = null;
	private String fuser = null;
	private String fpw = null;
	
	/**
	 * Initializes the database object by fetching the database connection 
	 * parameters from the database.conf file.
	 * 
	 * 
	 * 
	 * @param serverPath should contain the realPath of a servlet context to the 
	 *         database.conf file. e.g.:
	 *         <p> 
	 *         <code>new DBQuery(getServletContext().getRealPath("/"));<code>
	 * 
	 * */
	public DBQuery(String serverPath) {
		
		try{

	    FileInputStream fStream = new FileInputStream(serverPath + "config/database.conf");
	    DataInputStream inStream = new DataInputStream(fStream);
	    BufferedReader br = new BufferedReader(new InputStreamReader(inStream));
	    
	    String strLine;
	    String[] dataStr;
	    
	    Boolean ensmbl = false;
	    Boolean fishoracle = false;   
	    
	    while ((strLine = br.readLine()) != null)   {
		  
	      Pattern pensmbl = Pattern.compile("^\\[ensembl\\]$");
		  Matcher mensmbl = pensmbl.matcher(strLine);
	      
		  if(mensmbl.find()){
			  ensmbl = true;  
			  fishoracle = false; 
		  }
		  
		  Pattern pforacle = Pattern.compile("^\\[fishoracle\\]$");
		  Matcher mforacle = pforacle.matcher(strLine);
		  
		  if(mforacle.find()){
			  fishoracle = true; 
			  ensmbl = false; 
		  }
		  
		  Pattern phost = Pattern.compile("^host");
		  Matcher mhost = phost.matcher(strLine);
	      
		  Pattern pport = Pattern.compile("^port");
		  Matcher mport = pport.matcher(strLine);
		  
		  Pattern pdb = Pattern.compile("^db");
		  Matcher mdb = pdb.matcher(strLine);
		  
		  Pattern puser = Pattern.compile("^user");
		  Matcher muser = puser.matcher(strLine);
		  
		  Pattern ppw = Pattern.compile("^pw");
		  Matcher mpw = ppw.matcher(strLine);
		  
		  if(ensmbl){
			  
			  if(mhost.find()){
				  dataStr = strLine.split("=");
				  ehost = dataStr[1].trim();
			  }
			  if(mport.find()){
				  dataStr = strLine.split("=");
				  eport = Integer.parseInt(dataStr[1].trim());
			  }
			  if(mdb.find()){
				  dataStr = strLine.split("=");
				  edb = dataStr[1].trim();
			  }
			  if(muser.find()){
				  dataStr = strLine.split("=");
				  euser = dataStr[1].trim();
			  }
			  if(mpw.find()){
				  dataStr = strLine.split("=");
				  epw = dataStr[1].trim();
			  }
		  }
		  if(fishoracle){
			  
			  if(mhost.find()){
				  dataStr = strLine.split("=");
				  fhost = dataStr[1].trim();
			  }
			  if(mdb.find()){
				  dataStr = strLine.split("=");
				  fdb = dataStr[1].trim();
			  }
			  if(muser.find()){
				  dataStr = strLine.split("=");
				  fuser = dataStr[1].trim();
			  }
			  if(mpw.find()){
				  dataStr = strLine.split("=");
				  fpw = dataStr[1].trim();
			  }
		  }
	    }

	    inStream.close();
	    } catch (Exception e){
	    	e.printStackTrace();
	    	System.err.println("Error: " + e.getMessage());
	    }
	}
	
	/**
	 * Looks location information (chromosome, start, end) for an amplicon stable id up.
	 * 
	 * @param copyNamberChangeId The stable id of an amplicon.
	 * @return		An ensembl API location object storing chromosome, start and end of an amplicon. 
	 * 
	 * */
	public Location getLocationForCNCId(String copyNamberChangeId){
	
		String qrystr = null;
		Pattern pampid = Pattern.compile("AMP");
		Matcher mampid = pampid.matcher(copyNamberChangeId);
		
		if(mampid.find()){
			qrystr = "SELECT * from amplicon WHERE amplicon_stable_id = " + copyNamberChangeId;
		}
		
		Pattern pdelid = Pattern.compile("DEL");
		Matcher mdelid = pdelid.matcher(copyNamberChangeId);
		
		if(mdelid.find()){
			qrystr = "SELECT * from delicon WHERE delicon_stable_id = " + copyNamberChangeId;
		}
		
		Connection conn = null;
		Location loc = null;
		try{
			
			int copyNumberChangeStart = 0;
			int copyNumberChangeEnd = 0;
			String copyNumberChangeChr = null;
			
			conn = FishOracleConnection.connect(fhost, fdb, fuser, fpw);
			
			Statement s = conn.createStatement();
				
				s.executeQuery(qrystr);
				ResultSet copyNumberChangeRs = s.getResultSet();
				
				while(copyNumberChangeRs.next()){
					copyNumberChangeStart = copyNumberChangeRs.getInt(4);
					copyNumberChangeEnd = copyNumberChangeRs.getInt(5);
					copyNumberChangeChr = copyNumberChangeRs.getString(3);
				
					String locStr = "chromosome:" + copyNumberChangeChr + ":" + copyNumberChangeStart + "-" + copyNumberChangeEnd;
				
					loc = new Location(locStr);
				}
				copyNumberChangeRs.close();
				
		} catch (Exception e){
			FishOracleConnection.printErrorMessage(e);
			System.exit(1);
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
			
		return loc;
	}
	
	/**
	 * Looks location information (chromosome, start, end) for a gene symbol up.
	 * 
	 * @param symbol The gene symbol, that was specified in the search query.
	 * @return		An ensembl API location object storing chromosome, start and end of a gene. 
	 * 
	 * */
	public Location getLocationForGene(String symbol){
		Gene gene = null;
		CoreDriver coreDriver;
		try {
			coreDriver = CoreDriverFactory.createCoreDriver(ehost, eport, edb, euser, epw);
			coreDriver.getConnection();
		
			gene = (Gene) coreDriver.getGeneAdaptor().fetchBySynonym(symbol).get(0);
			
			coreDriver.closeAllConnections();
		
		} catch (AdaptorException e) {
			e.printStackTrace();
			System.out.println("Error: " + e.getMessage());
			System.out.println(e.getCause());
		}	
		return gene.getLocation();
	}
	
	/** 
	 * Looks location information (chromosome, start, end) for a karyoband up.
	 * 
	 * @param chr The chromosome number
	 * @param band The karyoband
	 * @return		An ensembl API location object storing chromosome, start and end of a chromosome and  karyoband. 
	 * 
	 * */
	public Location getLocationForKaryoband(String chr, String band){
		CoordinateSystem coordSys = null;
		KaryotypeBand k = null;
		CoreDriver coreDriver;
		try {
			coreDriver = CoreDriverFactory.createCoreDriver(ehost, eport, edb, euser, epw);
			coreDriver.getConnection();
			
			KaryotypeBandAdaptor kband = coreDriver.getKaryotypeBandAdaptor();
			
			coordSys = coreDriver.getCoordinateSystemAdaptor().fetch("chromosome", null);
			
			k = (KaryotypeBand) kband.fetch(coordSys, chr, band).get(0);
			
			coreDriver.closeAllConnections();
		
		} catch (AdaptorException e) {
			e.printStackTrace();
			System.out.println("Error: " + e.getMessage());
			System.out.println(e.getCause());
		}	
		return k.getLocation();
	}
	
	
	/**
	 * Finds all amplicons that overlap with a given range on a chromosome and returns the 
	 * maximum range over all overlapping amplicons as an ensembl location object.
	 * 
	 * @param chr chromosome number
	 * @param start Starting position on the chromosome.
	 * @param end Ending postion on the chromosome.
	 * @return 		An ensembl API location object storing chromosome, start and end
	 * 
	 * */
	public Location getMaxCNCRange(String chr, int start, int end, boolean isAmplicon){
		Location loc = null;
		Connection conn = null;
		String copyNumberChangeChr = chr;
		int copyNumberChangeStart = start;
		int copyNumberChangeEnd = end;
		String qrystr = null;
		
		if(isAmplicon){
			qrystr = "SELECT MIN(start) as minstart, MAX(end) as maxend FROM amplicon WHERE chromosome = \"" + copyNumberChangeChr + 
			"\" AND ((start <= " + copyNumberChangeStart + " AND end >= " + copyNumberChangeEnd + ") OR" +
	        " (start >= " + copyNumberChangeStart + " AND end <= " + copyNumberChangeEnd + ") OR" +
	        " (start >= " + copyNumberChangeStart + " AND start <= " + copyNumberChangeEnd + ") OR" +
	        " (end >= " + copyNumberChangeStart + " AND end <= " + copyNumberChangeEnd + "))";
		} else {
			qrystr = "SELECT MIN(start) as minstart, MAX(end) as maxend FROM delicon WHERE chromosome = \"" + copyNumberChangeChr + 
			"\" AND ((start <= " + copyNumberChangeStart + " AND end >= " + copyNumberChangeEnd + ") OR" +
	        " (start >= " + copyNumberChangeStart + " AND end <= " + copyNumberChangeEnd + ") OR" +
	        " (start >= " + copyNumberChangeStart + " AND start <= " + copyNumberChangeEnd + ") OR" +
	        " (end >= " + copyNumberChangeStart + " AND end <= " + copyNumberChangeEnd + "))";
		}
		
		try{
			
			conn = FishOracleConnection.connect(fhost, fdb, fuser, fpw);
			
			Statement s = conn.createStatement();
			
			s.executeQuery(qrystr);
			
			ResultSet rangeRs = s.getResultSet();
			rangeRs.next();
			int qstart = rangeRs.getInt(1);
			int qend = rangeRs.getInt(2);
			
			String locStr = "chromosome:" + copyNumberChangeChr + ":" + qstart + "-" + qend;
			
			loc = new Location(locStr);
			
			rangeRs.close();
			s.close();
			
		} catch (Exception e){
			FishOracleConnection.printErrorMessage(e);
			System.exit(1);
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
		
		return loc;
	}
	
	/**
	 * For a range on a chromosome an array with all overlapping amplicons is returned.
	 * 
	 * @param chr chromosome
	 * @param start Starting position on the chromosome.
	 * @param end Ending postion on the chromosome.
	 * @return		Array containing amplicon objects
	 * 
	 * */
	public CopyNumberChange[] getCNCData(String chr, int start, int end, boolean isAmplicon){
		
		String qrystrc = null;
		String qrystr = null;
		int copyNumberChangeStart = start;
		int copyNumberChangeEnd = end;
		String copyNumberChangeChr = chr;
		String copyNumberChangeType = null;	
		
		if(isAmplicon){
			copyNumberChangeType = "amplicon";
			
		} else {
			copyNumberChangeType = "delicon";
		}
		
		qrystrc = "SELECT count(*) from " + copyNumberChangeType + " WHERE chromosome = \"" + copyNumberChangeChr + "\" " +
		"AND ((start <= " + copyNumberChangeStart + " AND end >= " + copyNumberChangeEnd + ") OR" +
        " (start >= " + copyNumberChangeStart + " AND end <= " + copyNumberChangeEnd + ") OR" +
        " (start >= " + copyNumberChangeStart + " AND start <= " + copyNumberChangeEnd + ") OR" +
        " (end >= " + copyNumberChangeStart + " AND end <= " + copyNumberChangeEnd + "))";
		
		qrystr = "SELECT * from " + copyNumberChangeType + " WHERE chromosome = \"" + copyNumberChangeChr + "\" " +
		"AND ((start <= " + copyNumberChangeStart + " AND end >= " + copyNumberChangeEnd + ") OR" +
        " (start >= " + copyNumberChangeStart + " AND end <= " + copyNumberChangeEnd + ") OR" +
        " (start >= " + copyNumberChangeStart + " AND start <= " + copyNumberChangeEnd + ") OR" +
        " (end >= " + copyNumberChangeStart + " AND end <= " + copyNumberChangeEnd + "))";
		
		Connection conn = null;
		CopyNumberChange[] cnc = null;
		try{
			
			conn = FishOracleConnection.connect(fhost, fdb, fuser, fpw);
			
			Statement s = conn.createStatement();
			
			s.executeQuery(qrystrc);
			
			ResultSet countRegRs = s.getResultSet();
			countRegRs.next();
			int cncCount = countRegRs.getInt(1);
			
			countRegRs.close();
			
			s.executeQuery(qrystr);
			
			ResultSet regRs = s.getResultSet();
			
			int count = 0;

			cnc = new CopyNumberChange[cncCount];
			
			while(regRs.next()){
				String newCNCStableId = regRs.getString(2);
				String newChr = regRs.getString(3);
				int newStart = regRs.getInt(4);
				int newEnd = regRs.getInt(5);
				
				cnc[count] = new CopyNumberChange(newCNCStableId, newChr, newStart, newEnd, isAmplicon);

				count++;
			}
			
			regRs.close();
			s.close();
			
		} catch (Exception e){
			FishOracleConnection.printErrorMessage(e);
			System.exit(1);
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
		return cnc;
	}
	
	/**
	 * Fetch all data for a given Amplicon stable id
	 * 
	 * @param query Amplicon Stable ID
	 * @return		Amplicon object conaiting all amplicon data.
	 * 
	 * */
	public CopyNumberChange getCNCInfos(String query){
		
		String qrystr = null;
		Pattern pampid = Pattern.compile("AMP");
		Matcher mampid = pampid.matcher(query);
		boolean isAmplicon = false;
		
		if(mampid.find()){
			qrystr = "SELECT * from amplicon WHERE amplicon_stable_id = " + "'" + query + "'";
			isAmplicon = true;
		}
		
		Pattern pdelid = Pattern.compile("DEL");
		Matcher mdelid = pdelid.matcher(query);
		
		if(mdelid.find()){
			qrystr = "SELECT * from delicon WHERE delicon_stable_id = " + "'" + query + "'";
			isAmplicon = false;
		}
		
		Connection conn = null;
		CopyNumberChange amp = null;
		try{
		
			conn = FishOracleConnection.connect(fhost, fdb, fuser, fpw);
			
			Statement s = conn.createStatement();
			
			s.executeQuery(qrystr);
			
			ResultSet cncRs = s.getResultSet();
			
			while(cncRs.next()){
				
				String cncStableId = cncRs.getString(2);
				String chr = cncRs.getString(3);
				int start = cncRs.getInt(4);
				int end = cncRs.getInt(5);
				String caseName = cncRs.getString(6);
				String tumorType = cncRs.getString(7);
				int contin = cncRs.getInt(8);
				int cnclevel = cncRs.getInt(9);
				
				amp = new CopyNumberChange(cncStableId, chr, start, end, caseName, tumorType, contin, cnclevel, isAmplicon);
				
			}
		} catch (Exception e){
			FishOracleConnection.printErrorMessage(e);
			System.exit(1);
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
		return amp;
	}
	
	/**
	 * Fetch all data for a gene given by an ensembl stable id.
	 * 
	 * @param query Ensembl Stable ID
	 * @return		Gen object conaiting all gene data.
	 * 
	 * */
	public Gen getGeneInfos(String query) {
		
		Gen gene = null;
		
		CoreDriver coreDriver;
		try {
			coreDriver = CoreDriverFactory.createCoreDriver(ehost, eport, edb, euser, epw);
		
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
				CoreDriverFactory.createCoreDriver(ehost, eport, edb, euser, epw);

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
				coreDriver = CoreDriverFactory.createCoreDriver(ehost, eport, edb, euser, epw);

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
}
