package com.speno.xedm.gui.common.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.smartgwt.client.types.ContentsType;
import com.smartgwt.client.widgets.HTMLPane;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.layout.HStack;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class EntryTest implements EntryPoint {
	public void onModuleLoad() {

		VLayout layout = new VLayout();  
        layout.setWidth100();  
        layout.setHeight100();  
  
        final HTMLPane htmlPane = new HTMLPane();  
        htmlPane.setShowEdges(true);  
        htmlPane.setContentsURL("http://www.shinhan.com/");  
        htmlPane.setContentsType(ContentsType.PAGE);  
  
        HStack hStack = new HStack();  
        hStack.setHeight(50);  
        hStack.setLayoutMargin(10);  
        hStack.setMembersMargin(10);  
  
        IButton wikipediaButton = new IButton("Wikipedia: Ajax");  
        wikipediaButton.addClickHandler(new ClickHandler() {  
            public void onClick(ClickEvent event) {  
                htmlPane.setContentsURL("http://www.shinhan.com/");  
            }  
        });  
        hStack.addMember(wikipediaButton);  
  
  
        layout.addMember(hStack); 
        
        layout.addMember(htmlPane);  

        RootLayoutPanel.get().add(layout);
        
	}

	

}
