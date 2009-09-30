package de.unihamburg.zbh.fishoracle.server.data;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import org.ensembl.datamodel.Location;

import de.unihamburg.zbh.fishoracle.client.data.Amplicon;
import de.unihamburg.zbh.fishoracle.client.data.Gen;

import jxl.*;
import jxl.write.*; 
import jxl.write.biff.RowsExceededException;


public class Export {

	public Export() {

	}

	public String exportImageAsExcelDocument(Amplicon[] amps, Gen[] genes, Location maxAmpRange, String servletPath) throws IOException, RowsExceededException, WriteException{
		
		int i, j, k;
		String fileName = null;
		
		Random generator = new Random();
		int number = generator.nextInt( Integer.MAX_VALUE );
		
		fileName = number + ".xls";

		WritableWorkbook workbook = Workbook.createWorkbook(new File(servletPath + "tmp/" + fileName)); 
		WritableSheet sheet = workbook.createSheet(maxAmpRange.getSeqRegionName() + "," + maxAmpRange.getStart() + "-" + maxAmpRange.getEnd(), 0); 
		
		WritableFont textwidth = new WritableFont(WritableFont.ARIAL, 8);
		WritableCellFormat text = new WritableCellFormat(textwidth);
		
		for(i = 0; i < genes.length; i++){
		
			Label label = new Label(amps.length, i, genes[i].getGenName(), text);
			sheet.setColumnView(amps.length, 15);
			sheet.addCell(label);
		
		}
		
		for(j = 0; j < amps.length; j++){
			
			WritableCellFormat background = new WritableCellFormat(textwidth);
		    background.setWrap(true);
		    background.setBackground(Colour.getInternalColour(j+5*3));
		    
			for(k = 0; k < genes.length; k++){
				
				if((amps[j].getStart() < genes[k].getEnd() && amps[j].getEnd() > genes[k].getStart() ||
					(amps[j].getEnd() > genes[k].getStart() && amps[j].getStart() < genes[k].getEnd())) ||
					(amps[j].getStart() > genes[k].getStart() && amps[j].getEnd() < genes[k].getEnd()))
				{
					Label label = new Label(j, k, Double.toString(amps[j].getAmpliconStableId()), background);
					sheet.setColumnView(j, 6);
					sheet.addCell(label);
				}
				
			}
			
		}
		
		workbook.write();
		workbook.close();
		
		return fileName;
		
	}
	
}
