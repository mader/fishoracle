package de.unihamburg.zbh.fishoracle.server.data;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import de.unihamburg.zbh.fishoracle.client.data.CopyNumberChange;
import de.unihamburg.zbh.fishoracle.client.data.GWTImageInfo;
import de.unihamburg.zbh.fishoracle.client.data.Gen;

import org.ensembl.datamodel.Location;

import annotationsketch.*;
import core.*;
import de.unihamburg.zbh.fishoracle.client.data.RecMapInfo;
import extended.*;

/**
 *  Draws images visualizing the genomic data especially chromosome,
 *   gene and amplicon data. The drawing is done by AnnotaionsSketch
 *   of the GenomeTools which is a bioinformatics library written in C.
 *   The connection to C library is established via the java bindings
 *   genometools-java that are based on JNA (Java Native Access).
 * 
 * */
public class SketchTool {

	public SketchTool() {
		
	}
	
	/**
	 * Draws an image and creates an image info object that stores the URL
	 * to the image and addional image information liek a ReqMap of all
	 * features in the image.
	 * 
	 * @param amps Array of amplicons normally retrieved from the database.
	 * @param genes Array of genes normally retrieved from the database.
	 * @param kband Array of karyobands normally retrieved from the database.
	 * @param loc ensembl API location object
	 * @param winWidth The inner width of the center panel.
	 * @param query The original search query. Needed to mark the searched feature.
	 * @param serverPath the realPath of a servlet context retrieved from 
	 *         <code>getServletContext().getRealPath("/");<code>.
	 * 
	 * @return imgInfo image info object that stores additional information of
	 *          the generated image that need to be displayed or further processed
	 *          at the client side.
	 *          @see GWTImageInfo
 	 * */
	public GWTImageInfo generateImage(CopyNumberChange[] amps, CopyNumberChange[] dels, Gen[] genes, Karyoband[] kband, Location loc, int winWidth, String query, String serverPath) {
			
		ArrayList<FeatureNode> features;
		
		String seqid;
		
		Range range;
		Style style;
		Diagram diagram;
		Layout layout;
		CanvasCairoFile canvas;
		long height;
		
		String imgUrl = null;

		GWTImageInfo imgInfo = null;
		
		seqid = loc.getSeqRegionName();
		
		features = new ArrayList<FeatureNode>();
		
		FeatureNode fnode;
		
		try {
			
		for(int i=0; i < amps.length; i++ ){
			
			fnode = new FeatureNode(seqid, "amplicon", amps[i].getStart(), amps[i].getEnd(), ".");
			features.add(fnode);
			fnode.add_attribute("ID", amps[i].getCncStableId());
			if(amps[i].getCncStableId().equals(query)){
				fnode.gt_genome_node_mark();
			}
		}
		
		for(int i=0; i < dels.length; i++ ){
			
			fnode = new FeatureNode(seqid, "delicon", dels[i].getStart(), dels[i].getEnd(), ".");
			features.add(fnode);
			fnode.add_attribute("ID", dels[i].getCncStableId());
			if(dels[i].getCncStableId().equals(query)){
				fnode.gt_genome_node_mark();
			}
		}
		
		for(int j=0; j < genes.length; j++ ){
			fnode = new FeatureNode(seqid, "gene", genes[j].getStart(), genes[j].getEnd(), genes[j].getStrand());
			features.add(fnode);
			fnode.add_attribute("ID", genes[j].getGenName());
			fnode.add_attribute("NAME", genes[j].getAccessionID());
			if(genes[j].getGenName().equalsIgnoreCase(query)){
				fnode.gt_genome_node_mark();
			}
		}
		
		for(int k=0; k < kband.length; k++ ){
			fnode = new FeatureNode(seqid, "chromosome", kband[k].getStart(), kband[k].getEnd(), ".");
			features.add(fnode);
			fnode.add_attribute("ID", loc.getSeqRegionName() + kband[k].getBand());
			
		}
		
		style = new Style();
		
		style.load_file(serverPath + "config/default.style");
		
		range = new Range(loc.getStart(), loc.getEnd());
		System.out.println(range.get_start() + " " + range.get_end());
		
		TrackSelector ts = new TrackSelector() {
		      @Override
		      public String getTrackId(Block b)
		      {
		    	  String typeNumber = null;

		    	  if(b.get_type().equals("chromosome") ){
		    		  typeNumber = "1:";
		    	  }
		    	  if(b.get_type().equals("gene") ){
		    		  typeNumber = "2:";
		    	  }
		    	  if(b.get_type().equals("amplicon") ){
		    		  typeNumber = "3:";
		    	  }
		    	  if(b.get_type().equals("delicon") ){
		    		  typeNumber = "4:";
		    	  }
		    	  
		        return typeNumber + b.get_type();
		      }};
		
		diagram = new Diagram(features, range, style);		
		diagram.set_track_selector_func(ts);
		
		layout = new Layout(diagram, winWidth, style);
		
		height = layout.get_height();

		ImageInfo info = new ImageInfo();
		
		canvas = new CanvasCairoFile(style, winWidth, (int) height, info);
				
		layout.sketch(canvas);
		
		ArrayList<RecMapInfo> recmapinfoArr = getRecMapElements(info);
		
		@SuppressWarnings("unused")
		File file;
		Random generator = new Random();
		int number = generator.nextInt( Integer.MAX_VALUE );
		
		String fileName = Integer.toString(number);
		
		imgUrl = "tmp/" + fileName + ".png";
		
		file = new File(serverPath + imgUrl);

	    canvas.to_file(serverPath + imgUrl);
	    
	    imgInfo = new GWTImageInfo(imgUrl, info.get_height(), winWidth, recmapinfoArr);
		
		} catch (GTerrorJava e) {
			e.printStackTrace();
			System.out.println("Error: " + e.getMessage());
			System.out.println(e.getCause());
		}
	    return imgInfo;
	}

	
	/*
	 * Copies the GTReqMap object into the custom ReqMapInfo object
	 * and stores additional information in it.
	 *  
	 * */
	private ArrayList<RecMapInfo> getRecMapElements(ImageInfo info){
		
		ArrayList<RecMapInfo> recmapinfoArray = new ArrayList<RecMapInfo>();
		int countGenes = 0;
		String identifier = null;
		
		for(int i=0; i < info.num_of_rec_maps(); i++){
		
			// for genes we need to set an unique identifier like the ensembl stable id
			if(info.get_rec_map(i).get_genome_feature().get_type().equals("gene")){
				
				identifier = info.get_rec_map(i).get_genome_feature().get_attribute("NAME");
				
				countGenes++;
			// the same applies to the amplicons but here the caption equals the amplicon stable id	
			} else if (info.get_rec_map(i).get_genome_feature().get_type().equals("amplicon") ||
					info.get_rec_map(i).get_genome_feature().get_type().equals("delicon")){
				
				identifier = info.get_rec_map(i).get_genome_feature().get_attribute("ID");
				
			}
			
			// we don't need reqmap information for the karyoband
			if(!info.get_rec_map(i).get_genome_feature().get_type().equals("chromosome")){
				RecMapInfo recmapinfo = new RecMapInfo(info.get_rec_map(i).get_northwest_x(),
														info.get_rec_map(i).get_northwest_y(),
														info.get_rec_map(i).get_southeast_x(),
														info.get_rec_map(i).get_southeast_y(),
														info.get_rec_map(i).get_genome_feature().get_type(),
														identifier);
			
				recmapinfoArray.add(recmapinfo);
			}
		}		
		return recmapinfoArray;
	}
}
