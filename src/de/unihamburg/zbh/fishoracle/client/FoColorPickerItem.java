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
