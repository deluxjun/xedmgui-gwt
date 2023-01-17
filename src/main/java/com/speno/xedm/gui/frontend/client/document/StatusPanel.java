package com.speno.xedm.gui.frontend.client.document;

import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.Progressbar;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.toolbar.ToolStrip;

public class StatusPanel extends VLayout {
	
	private ToolStrip toolstrip;
	public StatusPanel() {            
		setWidth100();
		
		toolstrip = new ToolStrip();
		toolstrip.setWidth100();
		toolstrip.setHeight(20);
		toolstrip.setBackgroundColor("white");
		
		Label lblstatus = new Label();
		lblstatus.setWidth100();
		lblstatus.setHeight100();
		lblstatus.setShowEdges(false);
		
		final Progressbar hBar = new Progressbar();   
        hBar.setHeight(15);   
        hBar.setVertical(false);   
        hBar.setWidth(300);
        
        toolstrip.addMembers(hBar, lblstatus);   
        
		addMember(toolstrip);
		
	}
	
	
}