package de.unihamburg.zbh.fishoracle.server.data;

import java.io.*;
import java.util.Random;

import org.ensembl.datamodel.Location;

public class GFF3Creator {

	public GFF3Creator() {
		// TODO Auto-generated constructor stub
	}
	
	public String generateGFF3(Amplicon[] amps, Gen[] genes, Karyoband[] kband, Location loc){
		
		Random generator = new Random();
		 
		int gff3FileName = generator.nextInt( 1000000000 );
		
		String urlStr = Integer.toString(gff3FileName);
		
		try {
			FileWriter file = new FileWriter("tmp/"+ gff3FileName + ".gff3",true);
			
			// data defining the sequence region
			String seqid = loc.getSeqRegionName();
			int seqRegStart = loc.getStart();
			int seqRegEnd = loc.getEnd();
			
			// string containing the gff3 content that will be written in a gff3 file
			String gff3Str = null;
			
			String gff3Id = "ID=";
			String gff3Name = "Name=";
			String gff3FullName = null;
			
			
			//amplicon specific variables
			int ampId = 0;
			String ampIdPrefix = "amplicon"; 
			String ampliconId = null;
			
			//gene specific variables
			int geneId = 0;
			String fullgeneId = null;
			String geneIdPrefix = "gene";
			
			//chromosome band specific variables
			int kbandId = 0;
			String fullkbandId = null;
			String kbandIdPrefix = "band";
			
			// beginning of the gff3 file
			file.write("##gff-version 3\n");
			file.write("##sequence-region " + seqid + " " + seqRegStart + " " + seqRegEnd + "\n");
			
			// generate gff3 code for amplicon data
			for(int i = 0; i < amps.length;i++){
				
				ampliconId = gff3Id + ampIdPrefix + ampId; 
				gff3FullName = gff3Name + amps[i].getAmpliconStableId();
				
				
				gff3Str = seqid + "\t" + "." + "\t"+ ampIdPrefix + "\t" + amps[i].getStart() + "\t" + amps[i].getEnd() + "\t" + 
				      "." + "\t" + "." + "\t" + "." + "\t" + ampliconId + ";" + gff3FullName + "\n";
				
				System.out.println(gff3Str);
				
				file.write(gff3Str);
				
				ampId++;
			}
			
			// generate gff3 code for gene data
			for(int j = 0; j < genes.length;j++){
				
				fullgeneId = gff3Id + geneIdPrefix + geneId; 
				gff3FullName = gff3Name + genes[j].getGenName();
				
				gff3Str = seqid + "\t" + "." + "\t"+ geneIdPrefix + "\t" + genes[j].getStart() + "\t" + genes[j].getEnd() + "\t" + 
				      "." + "\t" + "." + "\t" + "." + "\t" + fullgeneId + ";" + gff3FullName + "\n";
				
				System.out.println(gff3Str);
				
				file.write(gff3Str);
				
				geneId++;
			}
			
			
			
			// generate gff3 code for chromosome data
			for(int k = 0; k < kband.length;k++){
				
				fullkbandId = gff3Id + kbandIdPrefix + kbandId; 
				gff3FullName = gff3Name + kband[k].getBand();
				
				gff3Str = seqid + "\t" + "." + "\t"+ kbandIdPrefix + "\t" + kband[k].getStart() + "\t" + kband[k].getEnd() + "\t" + 
				      "." + "\t" + "." + "\t" + "." + "\t" + fullkbandId + ";" + gff3FullName + "\n";
				
				System.out.println(gff3Str);
				
				file.write(gff3Str);
				
				kbandId++;
			}
			
			//chromosome  specific variables
			int ChrId = 0;
			String fullChrId = null;
			String ChrIdPrefix = "chromosome";
			
			fullChrId = gff3Id + ChrIdPrefix + ChrId; 
			gff3FullName = gff3Name + ChrIdPrefix + " " + kband[0].getChr();
			
			gff3Str = seqid + "\t" + "." + "\t"+ ChrIdPrefix + "\t" + seqRegStart + "\t" + seqRegEnd + "\t" + 
		      "." + "\t" + "." + "\t" + "." + "\t" + fullChrId + ";" + gff3FullName + "\n";
		
			System.out.println(gff3Str);
		
			file.write(gff3Str);
			
			file.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		return urlStr;
	}

}
