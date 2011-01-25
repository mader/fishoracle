package de.unihamburg.zbh.fishoracle.client;

import com.smartgwt.client.types.Visibility;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Img;
import com.smartgwt.client.widgets.events.MouseMoveEvent;
import com.smartgwt.client.widgets.events.MouseMoveHandler;
import com.smartgwt.client.widgets.events.MouseOutEvent;
import com.smartgwt.client.widgets.events.MouseOutHandler;
import com.smartgwt.client.widgets.events.MouseOverEvent;
import com.smartgwt.client.widgets.events.MouseOverHandler;
import com.smartgwt.client.widgets.form.fields.TextItem;

import de.unihamburg.zbh.fishoracle.client.data.GWTImageInfo;

public class ImgCanvas extends Img {
	
	/*image info object for the image that is a member of this canvas*/
	private GWTImageInfo imageInfo;
	/* references to the text items in the toolbar that is a member of the same canvas as this canvas layer*/
	private TextItem chromosome;
	private TextItem start;
	private TextItem end;
	private Canvas line;
	
	
	public ImgCanvas() {
		
	}
	
	public ImgCanvas(GWTImageInfo imageInfo) {
		this.imageInfo = imageInfo;
		this.setSrc("[APP]/" + imageInfo.getImgUrl());
	}

	@Override   
	protected void onInit() {  
	line = createLine();
	addChild(line);
	
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
		}
	});
	
	this.addMouseOutHandler(new MouseOutHandler() {
		@Override
		public void onMouseOut(MouseOutEvent event) {  
			getLine().hide();  
		}
	});
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
