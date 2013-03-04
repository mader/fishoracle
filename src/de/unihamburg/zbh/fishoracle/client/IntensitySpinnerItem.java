package de.unihamburg.zbh.fishoracle.client;

import com.smartgwt.client.widgets.form.fields.SpinnerItem;

public class IntensitySpinnerItem extends SpinnerItem{

	public IntensitySpinnerItem() {
		super();
		this.setTitle("Intensity");
		this.setDefaultValue(-0.5);
		this.setStep(0.05);
	}
}
