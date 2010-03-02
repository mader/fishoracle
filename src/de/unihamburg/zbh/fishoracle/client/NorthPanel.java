package de.unihamburg.zbh.fishoracle.client;

import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.LinkItem;
import com.smartgwt.client.widgets.layout.HLayout;


public class NorthPanel extends HLayout{
	
	private MainPanel mp = null;
	
	public NorthPanel(MainPanel mainPanel) {
		
		mp = mainPanel;
		
		DynamicForm left = new DynamicForm();
		
		LinkItem info = new LinkItem();
		info.setLinkTitle("Info");
		info.setShowTitle(false);
		
		left.setItems(info);
		
		DynamicForm right = new DynamicForm();
		
		LinkItem logout = new LinkItem();
		logout.setTitle("UserName");
		logout.setLinkTitle("Logout");
		
		right.setItems(logout);
		
		this.addMember(left);
		this.addMember(right);
	}
}
