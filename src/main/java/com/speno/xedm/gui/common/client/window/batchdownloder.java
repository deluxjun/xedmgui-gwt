package com.speno.xedm.gui.common.client.window;

import com.smartgwt.client.widgets.Window;
import com.speno.xedm.gui.common.client.I18N;

public class batchdownloder extends Window{

	private static batchdownloder instance = null;
	
	public static batchdownloder get() {
		if (instance == null)
			instance = new batchdownloder();
		return instance;
	}
	public batchdownloder(){
		setTitle(I18N.message("batch"));

		setWidth(300);
		setHeight(120);
		setMargin(5);	
		centerInPage();		
		show();
	}
	

	
	
}
