package com.speno.xedm.gui.common.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.speno.xedm.gui.frontend.client.panels.FileDropArea;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class EntryUpload implements EntryPoint {

	public void onModuleLoad() {

		FileDropArea area = new FileDropArea(200, 24);
//		area.setWidth(300);
//		area.setHeight(30);
		RootLayoutPanel.get().add(area);
//		RootLayoutPanel.get().setWidth("250px");
//		RootLayoutPanel.get().setHeight("35px");
	}

}