/*
  Copyright (c) 2014 Malte Mader <mader@zbh.uni-hamburg.de>
  Copyright (c) 2014 Center for Bioinformatics, University of Hamburg

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

package de.unihamburg.zbh.fishoracle.client;

import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.widgets.Progressbar;
import com.smartgwt.client.widgets.Window;

public class ProgressWindow extends Window {

	private Progressbar bar;
	
	public ProgressWindow(int imp, int nofi){
		super();
		
		int per = getPercentage(imp, nofi);
		
		this.setTitle("Upload Files "+ per + "%");
		this.setWidth(400);
		this.setHeight(60);
		this.setAlign(Alignment.CENTER);
	
		this.setAutoCenter(true);
		this.setIsModal(true);
		this.setShowModalMask(true);
		
		bar = new Progressbar(); 
		bar.setVertical(false); 
		bar.setHeight(24);
		
		this.addItem(bar);
	
		bar.setPercentDone(per);
	}
	
	private int getPercentage(int x, int y){
		return (x/y)*100;
	}
	
	public void updateValues(int imp, int nofi){
		
		int per = getPercentage(imp, nofi);
		this.setTitle("Upload Files "+ per + "%");
		bar.setPercentDone(per);	
	}	
}