package de.unihamburg.zbh.fishoracle.server.data;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import org.ensembl.datamodel.Location;

import de.unihamburg.zbh.fishoracle.client.data.CopyNumberChange;
import de.unihamburg.zbh.fishoracle.client.data.Gen;

import jxl.*;
import jxl.write.*; 
import jxl.write.biff.RowsExceededException;


public class Export {

	public Export() {

	}

	public String exportImageAsExcelDocument(CopyNumberChange[] amps, CopyNumberChange[] dels, Gen[] genes, Location maxAmpRange, String servletPath) throws IOException, RowsExceededException, WriteException{
		
		int i, j, k;
		int geneCol;
		int delBeginCol = 0;
		String fileName = null;
		
		Random generator = new Random();
		int number = generator.nextInt( Integer.MAX_VALUE );
		
		fileName = number + ".xls";

		WritableWorkbook workbook = Workbook.createWorkbook(new File(servletPath + "tmp/" + fileName)); 
		WritableSheet sheet = workbook.createSheet(maxAmpRange.getSeqRegionName() + "," + maxAmpRange.getStart() + "-" + maxAmpRange.getEnd(), 0); 
		
		WritableFont textwidth = new WritableFont(WritableFont.ARIAL, 8);
		WritableCellFormat text = new WritableCellFormat(textwidth);
		
		if(amps == null && dels != null){
			geneCol = dels.length;
			delBeginCol = 0;
		} else if (amps != null && dels == null){
			geneCol = amps.length;
		} else if (amps != null && dels != null){
			geneCol = amps.length + dels.length;
			delBeginCol = amps.length;
		} else {
			geneCol = 0;
		}
		
		for(i = 0; i < genes.length; i++){
		
			Label label = new Label(geneCol, i, genes[i].getGenName(), text);
			Label startlabel = new Label(geneCol+1, i, Integer.toString(genes[i].getStart()), text);
			Label endlabel = new Label(geneCol+2, i, Integer.toString(genes[i].getEnd()), text);
			sheet.setColumnView(geneCol, 15);
			sheet.addCell(label);
			sheet.addCell(startlabel);
			sheet.addCell(endlabel);
		}

		if (amps!=null){
			for(j = 0; j < amps.length; j++){

				WritableCellFormat background = new WritableCellFormat(textwidth);
				background.setWrap(true);
				background.setBackground(Colour.getInternalColour(j+5*3));

				for(k = 0; k < genes.length; k++){

					if((amps[j].getStart() < genes[k].getEnd() && amps[j].getEnd() > genes[k].getStart() ||
							(amps[j].getEnd() > genes[k].getStart() && amps[j].getStart() < genes[k].getEnd())) ||
							(amps[j].getStart() > genes[k].getStart() && amps[j].getEnd() < genes[k].getEnd()))
					{
						Label label = new Label(j, k, amps[j].getCncStableId(), background);
						sheet.setColumnView(j, 10);
						sheet.addCell(label);
					}

				}

			}
		}
		
		if(dels!=null){
			for(j = 0; j < dels.length; j++){

				WritableCellFormat background = new WritableCellFormat(textwidth);
				background.setWrap(true);
				background.setBackground(Colour.getInternalColour(j+5*3));

				for(k = 0; k < genes.length; k++){

					if((dels[j].getStart() < genes[k].getEnd() && dels[j].getEnd() > genes[k].getStart() ||
							(dels[j].getEnd() > genes[k].getStart() && dels[j].getStart() < genes[k].getEnd())) ||
							(dels[j].getStart() > genes[k].getStart() && dels[j].getEnd() < genes[k].getEnd()))
					{
						Label label = new Label(j + delBeginCol, k, dels[j].getCncStableId(), background);
						sheet.setColumnView(j, 10);
						sheet.addCell(label);
					}

				}

			}
		}
		
		workbook.write();
		workbook.close();
		
		return fileName;
		
	}
	
}
