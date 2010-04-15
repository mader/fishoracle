package de.unihamburg.zbh.fishoracle.client;

import com.smartgwt.client.widgets.Img;
import com.smartgwt.client.widgets.form.fields.TextItem;

import de.unihamburg.zbh.fishoracle.client.data.GWTImageInfo;

public class ImgCanvas extends Img {
	
	/*image info object for the image that is a member of this canvas*/
	private GWTImageInfo imageInfo;
	/* references to the text items in the toolbar that is a member of the same canvas as this canvas layer*/
	private TextItem chromosome;
	private TextItem start;
	private TextItem end;
	
	public ImgCanvas() {
		
	}
	
	public ImgCanvas(GWTImageInfo imageInfo) {
		this.imageInfo = imageInfo;
		this.setSrc("[APP]/" + imageInfo.getImgUrl());
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
}
