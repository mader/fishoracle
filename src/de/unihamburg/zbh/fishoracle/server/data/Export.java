package de.unihamburg.zbh.fishoracle.server.data;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.ensembl.datamodel.Location;

import de.unihamburg.zbh.fishoracle.client.data.CopyNumberChange;
import de.unihamburg.zbh.fishoracle.client.data.Gen;

import jxl.*;
import jxl.write.*; 
import jxl.write.biff.RowsExceededException;


public class Export {

	public Export() {

	}

	@SuppressWarnings({"deprecation"})
	public String exportImageAsExcelDocument(CopyNumberChange[] cncs, 
			                                  Gen[] genes, 
			                                  Location maxRange, 
			                                  String servletPath) throws IOException, 
			                                                              RowsExceededException, 
			                                                              WriteException, 
			                                                              NoSuchAlgorithmException{
		
		int i, j, k;
		int geneCol;
		String fileName = null;
		
		String dateStr;
		Calendar cal = Calendar.getInstance();
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    dateStr = sdf.format(cal.getTime());
		
	    String shaStr;
	    
	    shaStr = SimpleSHA.SHA1(dateStr);
		
		fileName = shaStr + "_" + maxRange.getSeqRegionName() + ":" + maxRange.getStart() + "-" + maxRange.getEnd() + ".xls";
		
		String url = "excel_output" + System.getProperty("file.separator") + fileName;
		
		WritableWorkbook workbook = Workbook.createWorkbook(new File(servletPath + url));
		WritableSheet sheet = workbook.createSheet(maxRange.getSeqRegionName() + "," + maxRange.getStart() + "-" + maxRange.getEnd(), 0); 
		
		WritableFont textwidth = new WritableFont(WritableFont.ARIAL, 8);
		WritableCellFormat text = new WritableCellFormat(textwidth);

		if (cncs != null){
			geneCol = cncs.length;
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

		if (cncs!=null){
			for(j = 0; j < cncs.length; j++){

				WritableCellFormat background = new WritableCellFormat(textwidth);
				background.setWrap(true);
				background.setBackground(Colour.getInternalColour(j+5*3));

				for(k = 0; k < genes.length; k++){

					if((cncs[j].getStart() < genes[k].getEnd() && cncs[j].getEnd() > genes[k].getStart() ||
							(cncs[j].getEnd() > genes[k].getStart() && cncs[j].getStart() < genes[k].getEnd())) ||
							(cncs[j].getStart() > genes[k].getStart() && cncs[j].getEnd() < genes[k].getEnd()))
					{
						Label label = new Label(j, k, cncs[j].getCncStableId(), background);
						sheet.setColumnView(j, 10);
						sheet.addCell(label);
					}

				}

			}
		}
		
		workbook.write();
		workbook.close();
		
		return url;
		
	}
	
}
