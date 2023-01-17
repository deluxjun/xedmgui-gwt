package com.speno.xedm.gui.common.client.widgets;

import com.google.gwt.user.client.Window;
import com.smartgwt.client.types.Cursor;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.speno.xedm.core.service.serials.SMessage;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.util.Util;

/**
 * 
 * @author deluxjun
 *
 */
public class MessageLabel extends Label {

	public MessageLabel(final SMessage message) {
		//201509na GS인증을 위한 로그인화면에서 ....으로 표시하던 부분 제거
//		super(Util.strCut(message.getMessage(), 140, "...") 
//				+ (message.getUrl() != null ? " (<b>" + I18N.message("clickhere").toLowerCase() + "</b>)" : ""));
		super(message.getMessage() 
		+ (message.getUrl() != null ? " (<b>" + I18N.message("clickhere").toLowerCase() + "</b>)" : ""));
		
		
		setHeight(25);
		setWrap(false);
		if (message.getPriority() == SMessage.PRIO_INFO)
			setIcon("[SKIN]/Dialog/notify.png");
		else if (message.getPriority() == SMessage.PRIO_WARN)
			setIcon("[SKIN]/Dialog/warn.png");
		if (message.getUrl() != null) {
			setCursor(Cursor.HAND);
			addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					Window.open(message.getUrl(), "_self", "");
				}
			});
		}
	}
}