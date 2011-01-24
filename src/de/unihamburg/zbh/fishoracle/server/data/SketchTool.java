package de.unihamburg.zbh.fishoracle.server.data;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

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
	 * @throws UnsupportedEncodingException 
	 * @throws NoSuchAlgorithmException 
	 *          @see GWTImageInfo
 	 * */
	public GWTImageInfo generateImage(CopyNumberChange[] cncs,
									Gen[] genes,
									Karyoband[] kband,
									Location loc,
									int winWidth,
									String query,
									String imageType,
									String serverPath) throws NoSuchAlgorithmException, UnsupportedEncodingException {
			
		ArrayList<FeatureNode> features;
		
		String seqid;
		
		Range range;
		Style style;
		Diagram diagram;
		Layout layout;
		CanvasCairoBase canvas = null;
		long height;
		
		String imgUrl = "";

		GWTImageInfo imgInfo = null;
				
		seqid = loc.getSeqRegionName();
		
		features = new ArrayList<FeatureNode>();
		
		FeatureNode fnode;
		
		try {
		
		if(cncs != null){
			for(int l=0; l < cncs.length; l++ ){
				
				fnode = new FeatureNode(seqid, "cnc", cncs[l].getStart(), cncs[l].getEnd(), ".");
				features.add(fnode);
				fnode.add_attribute("ID", cncs[l].getMicroarrayStudy());
				fnode.add_attribute("NAME", cncs[l].getCncStableId());
				if(cncs[l].getCncStableId().equalsIgnoreCase(query)){
					fnode.mark();
				}
		}
		}
		
		for(int j=0; j < genes.length; j++ ){
			fnode = new FeatureNode(seqid, "gene", genes[j].getStart(), genes[j].getEnd(), genes[j].getStrand());
			features.add(fnode);
			fnode.add_attribute("ID", genes[j].getGenName());
			fnode.add_attribute("NAME", genes[j].getAccessionID());
			if(genes[j].getGenName().equalsIgnoreCase(query)){
				fnode.mark();
			}
		}
		
		for(int k=0; k < kband.length; k++ ){
			fnode = new FeatureNode(seqid, "chromosome", kband[k].getStart(), kband[k].getEnd(), ".");
			features.add(fnode);
			fnode.add_attribute("ID", loc.getSeqRegionName() + kband[k].getBand());
			
		}
		
		style = new Style();
		
		style.load_file(serverPath + "config" + System.getProperty("file.separator") + "default.style");
		
		range = new Range(loc.getStart(), loc.getEnd());
		
		TrackSelector ts = new TrackSelector() {
		      @Override
		      public String getTrackId(Block b)
		      {
		    	  String typeNumber = "";

		    	  if(b.get_type().equals("chromosome") ){
		    		  typeNumber = "1:";
		    	  }
		    	  if(b.get_type().equals("gene") ){
		    		  typeNumber = "2:";
		    	  }
		    	  if(b.get_type().equals("cnc") ){
		    		  typeNumber = "3:";
		    	  }
		    	  
		        return typeNumber + b.get_type();
		      }};
		
		diagram = new Diagram(features, range, style);		
		diagram.set_track_selector_func(ts);
		
		layout = new Layout(diagram, winWidth, style);
		
		height = layout.get_height();

		ImageInfo info = new ImageInfo();
		
		if(imageType.equals("png")){	
			canvas = (CanvasCairoBase) new CanvasCairoFilePNG(style, winWidth, (int) height, info);
		}	
		if(imageType.equals("pdf")){	
			canvas = new CanvasCairoFilePDF(style, winWidth, (int) height, info);
		}	
		if(imageType.equals("ps")){	
			canvas = new CanvasCairoFilePS(style, winWidth, (int) height, info);
		}	
		if(imageType.equals("svg")){	
			canvas = new CanvasCairoFileSVG(style, winWidth, (int) height, info);
		}
		
		layout.sketch(canvas);
		
		ArrayList<RecMapInfo> recmapinfoArr = getRecMapElements(info);
		
		String dateStr;
		Calendar cal = Calendar.getInstance();
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    dateStr = sdf.format(cal.getTime());
		
	    String shaStr;
	    
	    shaStr = SimpleSHA.SHA1(dateStr);
	    
		@SuppressWarnings("unused")
		File file;

		String fileName = shaStr + "_" + loc.getSeqRegionName() + ":" + loc.getStart() + "-" + loc.getEnd() + "_" + query;
		
		imgUrl = "as_output" + System.getProperty("file.separator") + fileName + "." + imageType;
		
		file = new File(serverPath + imgUrl);

	    canvas.to_file(serverPath + imgUrl);
	    
	    imgInfo = new GWTImageInfo(imgUrl, info.get_height(), winWidth, recmapinfoArr);
		
	    for(int i = 0; i < features.size(); i++){
	    	features.get(i).dispose();
	    }
	    
	    style.dispose();
	    diagram.dispose();
	    layout.dispose();
	    canvas.dispose();
	    info.dispose();
	    
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
			} else if (info.get_rec_map(i).get_genome_feature().get_type().equals("cnc")){
				
				identifier = info.get_rec_map(i).get_genome_feature().get_attribute("NAME");
				
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
