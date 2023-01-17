package com.speno.xedm.gui.frontend.client.panels;

import com.smartgwt.client.widgets.layout.HLayout;
import com.speno.xedm.gui.common.client.log.EventPanel;


public class StatusBar extends HLayout {

	public StatusBar(boolean includeIcons) {
		setHeight(20);
		setWidth100();
		setMembersMargin(2);
		setStyleName("footer");

		HLayout events = EventPanel.get();
		events.setWidth100();

		addMember(events);

		if (includeIcons)
			addMember(StatusBarIcons.get());
	}
}