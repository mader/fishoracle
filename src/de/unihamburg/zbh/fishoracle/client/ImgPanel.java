package de.unihamburg.zbh.fishoracle.client;

import com.gwtext.client.widgets.Panel;

import de.unihamburg.zbh.fishoracle.client.data.ImageInfo;

public class ImgPanel extends Panel{

	private ImageInfo imageInfo;
	
	public ImgPanel() {
		
	}
	
	public ImgPanel(ImageInfo imageInfo) {
		super();
		this.imageInfo = imageInfo;
	}

	public ImageInfo getImageInfo() {
		return imageInfo;
	}

	public void setImageInfo(ImageInfo imageInfo) {
		this.imageInfo = imageInfo;
	}
	
}
