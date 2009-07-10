package de.unihamburg.zbh.fishoracle.client;

import com.google.gwt.user.client.ui.Image;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.Toolbar;
import com.gwtext.client.widgets.form.TextField;

import de.unihamburg.zbh.fishoracle.client.data.ImageInfo;

public class ImgPanel extends Panel{

	private ImageInfo imageInfo;
	private Image image;
	private TextField chrBox;
	private TextField startBox;
	private TextField endBox;
	
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

	public TextField getChrBox() {
		return chrBox;
	}

	public void setChrBox(TextField chrBox) {
		this.chrBox = chrBox;
	}

	public TextField getStartBox() {
		return startBox;
	}

	public void setStartBox(TextField startBox) {
		this.startBox = startBox;
	}

	public TextField getEndBox() {
		return endBox;
	}

	public void setEndBox(TextField endBox) {
		this.endBox = endBox;
	}
	
}
