/*
  Copyright (c) 2013 Malte Mader <mader@zbh.uni-hamburg.de>
  Copyright (c) 2013 Center for Bioinformatics, University of Hamburg

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

import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.ImgButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.ColorPicker;
import com.smartgwt.client.widgets.form.events.ColorSelectedEvent;
import com.smartgwt.client.widgets.form.events.ColorSelectedHandler;
import com.smartgwt.client.widgets.form.fields.CanvasItem;

public class FoColorPickerItem extends CanvasItem {
	
	private ImgButton b;
	private ColorPicker cp; 
	private boolean custom;
	
	public FoColorPickerItem() {
		super();
		
		this.setWidth(18);
		this.setHeight(18);
		
		custom = false;
		
		cp = new ColorPicker();
		Canvas c = new Canvas();
		b = new ImgButton();
		b.setWidth(18);
		b.setHeight(18);
		b.setShowTitle(false);
		b.setShowRollOver(false);
		b.setShowDown(false);
		b.setTooltip("Select a custom color.");
		b.setSrc("[SKIN]/DynamicForm/ColorPicker_icon.png");
		
		b.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				custom = true;
				cp.addColorSelectedHandler(new ColorSelectedHandler(){

					@Override
					public void onColorSelected(ColorSelectedEvent event) {
						b.setBackgroundColor(cp.getHtmlColor());
					}
				});
				cp.show();
			}});
		
		c.addChild(b);
		this.setCanvas(c);
	}
	
	public int getRed(){
		return cp.getRed();
	}
	
	public void setRed(int red){
		cp.setRed(red);
	}
	
	public int getGreen(){
		return cp.getGreen();
	}
	
	public void setGreen(int green){
		cp.setGreen(green);
	}
	
	public int getBlue(){
		return cp.getBlue();
	}
	
	public void setBlue(int blue){
		cp.setBlue(blue);
	}
	
	public int getAlpha(){
		int ret;
		if(cp.getOpacity() == null){
			ret = 70;
		} else {
			ret = cp.getOpacity();
		}
		return ret;
	}

	public void setAlpha(int alpha){
		cp.setOpacity(alpha);
	}
	
	public boolean isCustom() {
		return custom;
	}

	public void setCustom(boolean set) {
		this.custom = set;
	}

	public void updateButtonColor() {
		b.setBackgroundColor(cp.getHtmlColor());
	}
}
