package de.unihamburg.zbh.fishoracle.client;

import com.google.gwt.user.client.ui.Image;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.Toolbar;

import de.unihamburg.zbh.fishoracle.client.data.ImageInfo;

public class ImgPanel extends Panel{

	private ImageInfo imageInfo;
	private Image image;
	private Toolbar toolbar;
	
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

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
	}

	public Toolbar getToolbar() {
		return toolbar;
	}

	public void setToolbar(Toolbar toolbar) {
		this.toolbar = toolbar;
	}
	
	
	
}
