package com.speno.xedm.gui.common.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.smartgwt.client.widgets.ImgButton;
import com.smartgwt.client.widgets.layout.VLayout;
import com.speno.xedm.gui.common.client.util.ItemFactory;
import com.speno.xedm.gui.frontend.client.WaitingTest;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class EntryUtil implements EntryPoint {
	public void onModuleLoad() {

		VLayout layout = new VLayout();  
        layout.setWidth100();  
        layout.setHeight100();  

        ImgButton helpButton = ItemFactory.newImgButton("", "help", 16,16, false, false);
        helpButton.addClickHandler(new com.smartgwt.client.widgets.events.ClickHandler() {
			@Override
			public void onClick(com.smartgwt.client.widgets.events.ClickEvent event) {
				WaitingTest.show("test");
			}
		});

        layout.addMember(helpButton);
        RootLayoutPanel.get().add(layout);
        
        
	}

	

}
