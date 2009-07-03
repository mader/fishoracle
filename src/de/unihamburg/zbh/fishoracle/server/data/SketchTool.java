package de.unihamburg.zbh.fishoracle.server.data;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import org.ensembl.datamodel.Location;

import annotationsketch.*;
import core.*;
import extended.*;

public class SketchTool {

	public SketchTool() {
		
	}
	
	public String generateImage(Amplicon[] amps, Gen[] genes, Karyoband[] kband, Location loc, int winWidth) {
		
		ArrayList<FeatureNode> features;
		
		String seqid;
		
		Range range;
		Style style;
		Diagram diagram;
		Layout layout;
		CanvasCairoFile canvas;
		long height;
		
		String imgUrl = null;
		String imgGenUrl = null;
		
		seqid = loc.getSeqRegionName();
		
		features = new ArrayList<FeatureNode>();
		try {
		
		int all;
			
		for(int i=0; i < amps.length; i++ ){
			
			features.add(new FeatureNode(seqid, "amplicon", amps[i].getStart(), amps[i].getEnd(), "."));
			features.get(i).add_attribute("ID", Double.toString(amps[i].getAmpliconStableId()));
			
		}
		
		all = amps.length;
		
		for(int j=0; j < genes.length; j++ ){
			
			features.add(new FeatureNode(seqid, "gene", genes[j].getStart(), genes[j].getEnd(), "."));
			features.get(j + all).add_attribute("ID", genes[j].getGenName());
			
		}
		
		all = all + genes.length;
		
		for(int k=0; k < kband.length; k++ ){
			
			features.add(new FeatureNode(seqid, "chromosome", kband[k].getStart(), kband[k].getEnd(), "."));
			features.get(k + all).add_attribute("ID", loc.getSeqRegionName() + kband[k].getBand());
			
		}
		
		style = new Style();
		System.out.println("Style laden ...");
		style.load_file("config/default.style");
		//style.load_file("/home/mader/Desktop/jetty-6.1.18/webapps/fishoracle/config/default.style");
		
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
		    	  
		        return typeNumber + b.get_type();
		      }};
		
		diagram = new Diagram(features, range, style);		
		diagram.set_track_selector_func(ts);
		
		layout = new Layout(diagram, winWidth, style);
		
		height = layout.get_height();
		
		canvas = new CanvasCairoFile(style, winWidth, (int) height);
		
		layout.sketch(canvas);
		
		File file;
		Random generator = new Random();
		int number = generator.nextInt( Integer.MAX_VALUE );
		
		String fileName = Integer.toString(number);
		
		imgGenUrl = "/home/mader/Desktop/jetty-6.1.18/webapps/fishoracle/tmp/" + fileName + ".png";
		
		imgUrl = "tmp/" + fileName + ".png";
		
		//file = new File(imgGenUrl);
		file = new File(imgUrl);
	    System.out.println("Datei schreiben ...");
	    //canvas.to_file(imgGenUrl);
	    canvas.to_file(imgUrl);
	    
		} catch (GTerror e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	    return imgUrl;
		
	}

}
