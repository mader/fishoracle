/*
  Copyright (c) 2009-2012 Malte Mader <mader@zbh.uni-hamburg.de>
  Copyright (c) 2009-2012 Center for Bioinformatics, University of Hamburg

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

import com.smartgwt.client.types.Visibility;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Img;
import com.smartgwt.client.widgets.events.MouseDownEvent;
import com.smartgwt.client.widgets.events.MouseDownHandler;
import com.smartgwt.client.widgets.events.MouseMoveEvent;
import com.smartgwt.client.widgets.events.MouseMoveHandler;
import com.smartgwt.client.widgets.events.MouseOutEvent;
import com.smartgwt.client.widgets.events.MouseOutHandler;
import com.smartgwt.client.widgets.events.MouseOverEvent;
import com.smartgwt.client.widgets.events.MouseOverHandler;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

import de.unihamburg.zbh.fishoracle.client.data.GWTImageInfo;

public class ImgCanvas extends Img {
	
	/*image info object for the image that is a member of this canvas*/
	private GWTImageInfo imageInfo;
	/* references to the text items in the toolbar that is a member of the same canvas as this canvas layer*/
	private TextItem chromosome;
	private TextItem start;
	private TextItem end;
	private Canvas line;
	private Canvas rec;
	private boolean mouseDown;
	private ImgCanvas imgc;
	private CenterPanel cp;
	
	public ImgCanvas() {
		
	}
	
	public ImgCanvas(GWTImageInfo imageInfo, CenterPanel cp) {
		this.imageInfo = imageInfo;
		this.setSrc("[APP]/" + imageInfo.getImgUrl());
		imgc = this;
		this.cp = cp;
		mouseDown = false;
	}

	@Override   
	protected void onInit() {  
	line = createLine();
	rec = createRec();
	addChild(line);
	addChild(rec);
	
	this.addMouseDownHandler(new MouseDownHandler(){

		@Override
		public void onMouseDown(MouseDownEvent event) {
			
			ToolStripButton s = cp.getSelectButtion();
			
				if(s.isSelected()){
			
					if(!mouseDown){
						
						rec.show();
						
						rec.setLeft(imgc.getOffsetX());
						rec.setTop(imgc.getOffsetY());
						rec.setWidth(1);
						rec.setHeight(1);
						
						mouseDown = true;
					} else {
						mouseDown = false;
					}
				}
		}
	});
	
	this.addMouseOverHandler(new MouseOverHandler() {
		@Override
		public void onMouseOver(MouseOverEvent event) {  
			getLine().show();
			updateLine();  
		}
	});
	
	this.addMouseMoveHandler(new MouseMoveHandler() {
		@Override
		public void onMouseMove(MouseMoveEvent event) {
			updateLine();  
			
			if(mouseDown){
				
				int w = imgc.getOffsetX() - rec.getLeft();
				int h = imgc.getOffsetY() - rec.getTop();
				if(w > 0){
					rec.setWidth(w);
				}
				if(h > 0){
					rec.setHeight(h);
				}
			}
		}
	});
	
	this.addMouseOutHandler(new MouseOutHandler() {
		@Override
		public void onMouseOut(MouseOutEvent event) {  
			getLine().hide();  
		}
	});
	}  
	
	public void hideRec(){
		rec.setVisibility(Visibility.HIDDEN);
	}
	
	public GWTImageInfo getImageInfo() {
		return imageInfo;
	}

	public void setImageInfo(GWTImageInfo imageInfo) {
		this.imageInfo = imageInfo;
	}

	public TextItem getChromosome() {
		return chromosome;
	}

	public void setChromosome(TextItem chromosome) {
		this.chromosome = chromosome;
	}

	public TextItem getStart() {
		return start;
	}

	public void setStart(TextItem start) {
		this.start = start;
	}

	public TextItem getEnd() {
		return end;
	}

	public void setEnd(TextItem end) {
		this.end = end;
	}

	private Canvas createRec() {
		
		Canvas canvas = new Canvas();
		canvas.setBorder("2px solid #FF0000");
		canvas.setVisibility(Visibility.HIDDEN);
		return canvas;
	}
	
	private Canvas createLine() {
		Canvas canvas = new Canvas();
		canvas.setWidth(this.getWidth() + 10);
		canvas.setHeight(this.getHeight() * 2 + 10);
		canvas.setBorder("2px dotted #000000");
		canvas.setVisibility(Visibility.HIDDEN);
		return canvas;
	}

	public Canvas getLine() {  
		return line;
	}
	
	public void updateLine() {
		int x = getOffsetX();
		int y = getOffsetY();
		line.setLeft(x);
		line.setTop(y - getLine().getHeight() / 2);
	}
}
