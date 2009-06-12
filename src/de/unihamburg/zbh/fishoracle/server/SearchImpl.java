package de.unihamburg.zbh.fishoracle.server;

import java.io.IOException;
import java.text.ParseException;
import java.util.regex.*;

import org.ensembl.datamodel.Location;

import de.unihamburg.zbh.fishoracle.client.rpc.Search;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import de.unihamburg.zbh.fishoracle.server.data.*;

public class SearchImpl extends RemoteServiceServlet implements Search {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String gff3File;
	
	
	public String generateImage(String query, String searchType) {
		try {
			DBQuery db = new DBQuery();
			
			Amplicon[] amps = null;
			Location featuresLoc = null;
			
			
			if(searchType.equals("Amplicon Search")){
			
				double ampQuery = new Double(query).doubleValue();
			
				featuresLoc = db.getLocationForAmpliconStableId(ampQuery);
			
			} else if(searchType.equals("Gene Search")){
				
				featuresLoc = db.getLocationForGene(query);
				
			} else if(searchType.equals("Band Search")){
				
				Pattern pChr = Pattern.compile("^\\d{1,2}");
				Matcher mChr = pChr.matcher(query);
				
				Pattern pBand = Pattern.compile("[p,q]{1}\\d{1,2}\\.?\\d{1,2}?$");
				Matcher mBand = pBand.matcher(query);
				
				String chrStr = null;
				String bandStr = null;
				
				if(mChr.find()){
					
					chrStr = (String) query.subSequence(mChr.start(), mChr.end());
					//System.out.println(chrStr);
				}
				
				if(mBand.find()){
					
					bandStr = (String) query.subSequence(mBand.start(), mBand.end());
					//System.out.println(bandStr);
				}
				featuresLoc = db.getLocationForKaryoband(chrStr, bandStr);
				
			} else if(searchType.equals("range")){
				
			} 
			
			
			amps = db.getAmpliconData(Integer.parseInt(featuresLoc.getSeqRegionName()), featuresLoc.getStart(), featuresLoc.getEnd());
			
			
			Location maxAmpRange = db.getMaxAmpliconRange(Integer.parseInt(featuresLoc.getSeqRegionName()), featuresLoc.getStart(), featuresLoc.getEnd());
			
			
			Gen[] genes = null;
			genes = db.getEnsembleGenes(Integer.parseInt(maxAmpRange.getSeqRegionName()), maxAmpRange.getStart(), maxAmpRange.getEnd());
			
			
			Karyoband[] band = null;
			band = db.getEnsemblKaryotypes(Integer.parseInt(maxAmpRange.getSeqRegionName()), maxAmpRange.getStart(), maxAmpRange.getEnd());
			
			
			int i;
			int minstart= Integer.MAX_VALUE;
			int maxend = 0;
			for(i=0;i<band.length;i++){
				
				if(minstart > band[i].getStart()){
					
					minstart = band[i].getStart();
				}  
				if(maxend < band[i].getEnd()){
					maxend = band[i].getEnd();
				}
			}
			
			String maxRangeLoc = "chromosome:" + band[0].getChr() + ":" + Integer.toString(minstart) + "-" + Integer.toString(maxend);
			
			Location maxRange = null;
			
			try {
				maxRange = new Location(maxRangeLoc);
			} catch (ParseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			GFF3Creator gff = new GFF3Creator();
			
			gff3File = gff.generateGFF3(amps, genes, band, maxRange);
			
			//Process rmproc = Runtime.getRuntime().exec("rm tmp/samp.png tmp/samp.gff3");
			
			System.out.println("gt gff3 -sort -o tmp/s" + gff3File + ".gff3 " + "tmp/" + gff3File + ".gff3");
			
			Process sortproc = Runtime.getRuntime().exec("gt gff3 -sort -o tmp/s" + gff3File + ".gff3 " + "tmp/" + gff3File + ".gff3");
			
			System.out.println("gt sketch -start " + maxAmpRange.getStart() + " -end " + maxAmpRange.getEnd() + " -input gff -format png tmp/s" + gff3File + ".png " + "tmp/s" + gff3File + ".gff3");
			
			Process genproc = Runtime.getRuntime().exec("gt sketch -start " + maxAmpRange.getStart() + " -end " + maxAmpRange.getEnd() + " -input gff -format png tmp/s" + gff3File + ".png " + "tmp/s" + gff3File + ".gff3");
			
			//Process rmproc2 = Runtime.getRuntime().exec("rm tmp/amp.gff3");
			
			//db.connectEnsemble();
 			try {
				//proc.waitFor();
				sortproc.waitFor();
				genproc.waitFor();
				//rmproc.waitFor();
				//rmproc2.waitFor();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            //System.out.println(proc.exitValue());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println(e.getMessage());
		}
		return "tmp/s" + gff3File + ".png";
	}

}
