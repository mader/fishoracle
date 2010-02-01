package de.unihamburg.zbh.fishoracle.server.data;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.*;
import java.text.ParseException;
import java.util.ArrayList;
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

import de.unihamburg.zbh.fishoracle.client.data.Amplicon;
import de.unihamburg.zbh.fishoracle.client.data.Gen;
import de.unihamburg.zbh.fishoracle.client.data.User;
import de.unihamburg.zbh.fishoracle.client.exceptions.DBQueryException;

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
	 * @param ampliconStableId The stable id of an amplicon.
	 * @return		An ensembl API location object storing chromosome, start and end of an amplicon. 
	 * @throws Exception 
	 * 
	 * */
	public Location getLocationForAmpliconStableId(double ampliconStableId) throws Exception{
	
		Connection conn = null;
		Location loc = null;
		try{
			
			int ampStart = 0;
			int ampEnd = 0;
			int ampChr = 0;
			
			conn = FishOracleConnection.connect(fhost, fdb, fuser, fpw);
			
			Statement s = conn.createStatement();
				
				s.executeQuery("SELECT * from amplicon WHERE amplicon_stable_id = " + ampliconStableId);
				ResultSet ampRs = s.getResultSet();
				
				while(ampRs.next()){
					ampStart = ampRs.getInt(4);
					ampEnd = ampRs.getInt(5);
					ampChr = ampRs.getInt(3);
				
					String locStr = "chromosome:" + ampChr + ":" + ampStart + "-" + ampEnd;
				
					loc = new Location(locStr);
				}
				ampRs.close();
				
				if(loc == null){
					
					DBQueryException e = new DBQueryException("Couldn't find the amplicon with the stable ID " + ampliconStableId);
					throw e;
				}
				
		} catch (DBQueryException e){
			System.out.println(e.getMessage());
			throw e;
		} catch (Exception e) {
			FishOracleConnection.printErrorMessage(e);
			throw e;
			//System.exit(1);
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
	 * @throws DBQueryException 
	 * 
	 * */
	public Location getLocationForGene(String symbol) throws DBQueryException{
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
		} catch (Exception e) {
			
			if(e instanceof IndexOutOfBoundsException){
				throw new DBQueryException("Couldn't find gene with gene symbol " + symbol, e.getCause());
			}
		}
		return gene.getLocation();
	}
	
	/** 
	 * Looks location information (chromosome, start, end) for a karyoband up.
	 * 
	 * @param chr The chromosome number
	 * @param band The karyoband
	 * @return		An ensembl API location object storing chromosome, start and end of a chromosome and  karyoband. 
	 * @throws DBQueryException 
	 * 
	 * */
	public Location getLocationForKaryoband(String chr, String band) throws DBQueryException{
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
		} catch (Exception e) {
			
			if(e instanceof IndexOutOfBoundsException){
				throw new DBQueryException("Couldn't find karyoband " + chr + band, e.getCause());
			}
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
	public Location getMaxAmpliconRange(int chr, int start, int end){
		Location loc = null;
		Connection conn = null;
		int ampChr = chr;
		int ampStart = start;
		int ampEnd = end;
		try{
			
			conn = FishOracleConnection.connect(fhost, fdb, fuser, fpw);
			
			Statement s = conn.createStatement();
			
			s.executeQuery("SELECT MIN(start) as minstart, MAX(end) as maxend FROM amplicon WHERE chromosome = " + ampChr + 
					" AND ((start <= " + ampStart + " AND end >= " + ampEnd + ") OR" +
				        " (start >= " + ampStart + " AND end <= " + ampEnd + ") OR" +
				        " (start >= " + ampStart + " AND start <= " + ampEnd + ") OR" +
				        " (end >= " + ampStart + " AND end <= " + ampEnd + "))");
			
			ResultSet rangeRs = s.getResultSet();
			rangeRs.next();
			int qstart = rangeRs.getInt(1);
			int qend = rangeRs.getInt(2);
			
			String locStr = "chromosome:" + Integer.toString(ampChr) + ":" + qstart + "-" + qend;
			
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
	public Amplicon[] getAmpliconData(String chr, int start, int end){
		
		Connection conn = null;
		Amplicon[] amps = null;
		try{
			
			conn = FishOracleConnection.connect(fhost, fdb, fuser, fpw);
			
			Statement s = conn.createStatement();
				
			int ampStart = start;
			int ampEnd = end;
			String ampChr = chr;
			
			s.executeQuery("SELECT count(*) from amplicon WHERE chromosome = \"" + ampChr + "\" " +
					"AND ((start <= " + ampStart + " AND end >= " + ampEnd + ") OR" +
				        " (start >= " + ampStart + " AND end <= " + ampEnd + ") OR" +
				        " (start >= " + ampStart + " AND start <= " + ampEnd + ") OR" +
				        " (end >= " + ampStart + " AND end <= " + ampEnd + "))");
			
			ResultSet countRegRs = s.getResultSet();
			countRegRs.next();
			int ampCount = countRegRs.getInt(1);
			
			countRegRs.close();
			
			s.executeQuery("SELECT * from amplicon WHERE chromosome = \"" + ampChr + "\" " +
					"AND ((start <= " + ampStart + " AND end >= " + ampEnd + ") OR" +
					    " (start >= " + ampStart + " AND end <= " + ampEnd + ") OR" +
					    " (start >= " + ampStart + " AND start <= " + ampEnd + ") OR" +
				        " (end >= " + ampStart + " AND end <= " + ampEnd + "))");
			
			ResultSet regRs = s.getResultSet();
			
			int count = 0;

			amps = new Amplicon[ampCount];
			
			while(regRs.next()){
				double newAmpliconStableId = regRs.getDouble(2);
				String newChr = regRs.getString(3);
				int newStart = regRs.getInt(4);
				int newEnd = regRs.getInt(5);
				
				amps[count] = new Amplicon(newAmpliconStableId, newChr, newStart, newEnd);

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
		return amps;
	}
	
	/**
	 * Fetch all data for a given Amplicon stable id
	 * 
	 * @param query Amplicon Stable ID
	 * @return		Amplicon object conaiting all amplicon data.
	 * @throws Exception 
	 * 
	 * */
	public Amplicon getAmpliconInfos(String query) throws Exception{
		
		Connection conn = null;
		Amplicon amp = null;
		try{
		
			conn = FishOracleConnection.connect(fhost, fdb, fuser, fpw);
			
			Statement s = conn.createStatement();
			
			s.executeQuery("select * FROM amplicon WHERE amplicon_stable_id = " + query);
			
			ResultSet ampRs = s.getResultSet();
			
			while(ampRs.next()){
				
				double ampliconStableId = ampRs.getDouble(2);
				String chr = ampRs.getString(3);
				int start = ampRs.getInt(4);
				int end = ampRs.getInt(5);
				String caseName = ampRs.getString(6);
				String tumorType = ampRs.getString(7);
				int contin = ampRs.getInt(8);
				int amplevel = ampRs.getInt(9);
				
				amp = new Amplicon(ampliconStableId, chr, start, end, caseName, tumorType, contin, amplevel);
				
			}
		} catch (Exception e){
			FishOracleConnection.printErrorMessage(e);
			throw e;
		} finally {
			if(conn != null){
				try{
					conn.close();
				} catch(Exception e) {
					String err = FishOracleConnection.getErrorMessage(e);
					System.out.println(err);
					throw e;
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
	 * @throws Exception 
	 * 
	 * */
	public Gen getGeneInfos(String query) throws Exception {
		
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
	
	/* USER DATA */
	
	public User getUserData(String userName, String pw) throws Exception{
		Connection conn = null;
		User user = null;
		
		try{
			
			conn = FishOracleConnection.connect(fhost, fdb, fuser, fpw);
			
			Statement s = conn.createStatement();
			
			s.executeQuery("SELECT * FROM user WHERE username = '" + userName + "' AND password = '" + SimpleSHA.SHA1(pw) + "'");
			
			ResultSet userRs = s.getResultSet();
			
			int id = 0;
			String fistName = null;
			String lastName = null;
			String dbUserName = null;
			String email = null;
			Boolean isActive = null;
			Boolean isAdmin = null;
			
			while(userRs.next()){
				
				id = userRs.getInt(1);
				fistName = userRs.getString(2);
				lastName = userRs.getString(3);
				dbUserName = userRs.getString(4);
				email = userRs.getString(5);
				isActive = userRs.getBoolean(7);
				isAdmin = userRs.getBoolean(8);
			}
			
			if(id == 0){
				
				throw new DBQueryException("User name or password incorrect!");
				
			}
			if(isActive == false){
				
				throw new DBQueryException("Your account has not been activated. If you registered recently" +
						                    " this means that your acount has not been verified yet. Just try to log in later." +
						                    " If your account has been deactivated or your registration was more than 3 days ago" +
						                    " then contact the webmaster.");
				
			}
			
			user = new User(id, fistName, lastName, dbUserName, email, isActive, isAdmin);
			
		} catch (Exception e){
			FishOracleConnection.printErrorMessage(e);
			throw e;
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
		return user;	
	}
	
	public void insertUserData(User user) throws Exception{
		Connection conn = null;
		
		try{
			
			conn = FishOracleConnection.connect(fhost, fdb, fuser, fpw);
			
			Statement s = conn.createStatement();
			
			s.executeQuery("SELECT count(*) FROM user where username = '" + user.getUserName() + "'");
			
			ResultSet countRs = s.getResultSet();
			countRs.next();
			int userCount = countRs.getInt(1);
			
			if(userCount == 0){
			
			s.executeUpdate("INSERT INTO user (first_name, last_name, username, email, password, isactive, isadmin) VALUES" +
							" ('" + user.getFirstName() + "', '" + user.getLastName() + "', '" + user.getUserName() +
							"', '" + user.getEmail() + "', '" + SimpleSHA.SHA1(user.getPw()) + "', '" + user.getIsActive() + 
							"', '"+ user.getIsAdmin() + "')");
			} else {
				
				
				 throw new DBQueryException("User name is already taken! Choose another one.");
				
			}
			
		} catch (Exception e){
			FishOracleConnection.printErrorMessage(e);
			throw e;
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
	
	public ArrayList<User> fetchAllUsers() throws Exception{
		
		Connection conn = null;
		ArrayList<User> users = new ArrayList<User>();
		
		try{
			
			conn = FishOracleConnection.connect(fhost, fdb, fuser, fpw);
			
			Statement s = conn.createStatement();
			
			s.executeQuery("SELECT user_id, first_name, last_name, username, email, isActive, isadmin  FROM user");
			
			ResultSet userRs = s.getResultSet();
			
			int id = 0;
			String fistName = null;
			String lastName = null;
			String dbUserName = null;
			String email = null;
			Boolean isActive = null;
			Boolean isAdmin = null;
			
			while(userRs.next()){
				
				id = userRs.getInt(1);
				fistName = userRs.getString(2);
				lastName = userRs.getString(3);
				dbUserName = userRs.getString(4);
				email = userRs.getString(5);
				isActive = userRs.getBoolean(6);
				isAdmin = userRs.getBoolean(7);
				
				//System.out.println(id + " " + fistName + " " + lastName + " " + dbUserName + " " + email + " " + isAdmin + "\n"); 
				
				User user = new User(id, fistName, lastName, dbUserName, email, isActive, isAdmin);
				
				//System.out.println(user.getId() + " " + user.getFirstName() + " " + user.getLastName() + " " + user.getUserName() + " " + user.getEmail() + " " + user.getIsAdmin() + "\n");
				
				users.add(user);
			}
			
		} catch (Exception e){
			FishOracleConnection.printErrorMessage(e);
			System.out.println(e.getMessage());
			System.out.println(e.getStackTrace());
			throw e;
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
		
		return users;
	}
	
}
