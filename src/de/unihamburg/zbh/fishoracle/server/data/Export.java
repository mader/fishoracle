/*
  Copyright (c) 2009-2011 Malte Mader <mader@zbh.uni-hamburg.de>
  Copyright (c) 2009-2011 Center for Bioinformatics, University of Hamburg

  Permission to use, copy, modify, and distribute this software for any
  purpose with or without fee is hereby granted, provided that the above
  copyright notice and this permission notice appear in all copies.

  THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
  WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
  MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
  ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
  WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
  ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
  OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
*/

package de.unihamburg.zbh.fishoracle.server.data;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.unihamburg.zbh.fishoracle.client.data.EnsemblGene;
import de.unihamburg.zbh.fishoracle_db_api.data.CnSegment;
import de.unihamburg.zbh.fishoracle_db_api.data.Location;

import jxl.*;
import jxl.write.*; 
import jxl.write.biff.RowsExceededException;


public class Export {

	public Export() {

	}

	@SuppressWarnings({"deprecation"})
	public String exportImageAsExcelDocument(CnSegment[] segments, 
			                                  EnsemblGene[] genes, 
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
		
		fileName = shaStr + "_" + maxRange.getChrosmome() + ":" + maxRange.getStart() + "-" + maxRange.getEnd() + ".xls";
		
		String url = "excel_output" + System.getProperty("file.separator") + fileName;
		
		WritableWorkbook workbook = Workbook.createWorkbook(new File(servletPath + url));
		WritableSheet sheet = workbook.createSheet(maxRange.getChrosmome() + "," + maxRange.getStart() + "-" + maxRange.getEnd(), 0); 
		
		WritableFont textwidth = new WritableFont(WritableFont.ARIAL, 8);
		WritableCellFormat text = new WritableCellFormat(textwidth);

		if (segments != null){
			geneCol = segments.length;
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

		if (segments!=null){
			for(j = 0; j < segments.length; j++){

				WritableCellFormat background = new WritableCellFormat(textwidth);
				background.setWrap(true);
				background.setBackground(Colour.getInternalColour(j+5*3));

				for(k = 0; k < genes.length; k++){

					if((segments[j].getStart() < genes[k].getEnd() && segments[j].getEnd() > genes[k].getStart() ||
							(segments[j].getEnd() > genes[k].getStart() && segments[j].getStart() < genes[k].getEnd())) ||
							(segments[j].getStart() > genes[k].getStart() && segments[j].getEnd() < genes[k].getEnd()))
					{
						Label label = new Label(j, k, segments[j].getMicroarraystudyName(), background);
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