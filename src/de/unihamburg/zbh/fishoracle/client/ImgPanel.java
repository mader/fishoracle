package de.unihamburg.zbh.fishoracle.client;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Image;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.form.TextField;

import de.unihamburg.zbh.fishoracle.client.data.GWTImageInfo;

public class ImgPanel extends Panel{

	private GWTImageInfo imageInfo;
	private Image image;
	private TextField chrBox;
	private TextField startBox;
	private TextField endBox;
	private AbsolutePanel imageLayer; 
	
	public ImgPanel() {
		
	}
	
	public ImgPanel(GWTImageInfo imageInfo) {
		super();
		this.imageInfo = imageInfo;
	}

	public GWTImageInfo getImageInfo() {
		return imageInfo;
	}

	public void setImageInfo(GWTImageInfo imageInfo) {
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

	public AbsolutePanel getImageLayer() {
		return imageLayer;
	}

	public void setImageLayer(AbsolutePanel imageLayer) {
		this.imageLayer = imageLayer;
	}
	
}
