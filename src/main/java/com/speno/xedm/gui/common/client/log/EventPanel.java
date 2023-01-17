package com.speno.xedm.gui.common.client.log;

import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.widgets.Img;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.speno.xedm.core.service.serials.SEvent;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.Session;
import com.speno.xedm.gui.common.client.util.ItemFactory;

/**
 * 
 * @author deluxjun
 *
 */
public class EventPanel extends HLayout {
	private static EventPanel instance;

	private Label statusLabel;
	
	private Img running;

	private Img log;

	private Img close;

//	private BlinkColor<HTMLFlow> blink = null;
//	private HTMLFlow statusBlink = new HTMLFlow();             
//	private final static String[] STATUS_COLORS = {"009900", "00ff00", "0000ff", "aa0000", "ff0000"};
//	private final static int STATUS_PROCESS = 0;
//	private final static int STATUS_INFO = 1;
//	private final static int STATUS_NEWS = 2;
//	private final static int STATUS_WARNING = 3;
//	private final static int STATUS_ERROR = 4;
//	private final static int STATUS_MAX = 5;

	private EventPanel() {
		setHeight(20);
		setWidth100();
		setAlign(Alignment.LEFT);
		setMargin(2);
		setMembersMargin(2);

//		statusBlink.setID("blinkBox");  
//		statusBlink.setContents("");  
//		statusBlink.setStyleName("statusMessageBox");  
//		statusBlink.setOverflow(Overflow.HIDDEN);
//		statusBlink.setShowEdges(false);  
//		statusBlink.setWidth(10);  
//		statusBlink.setHeight(20);
        
        // create blink class
//        if (blink == null){
//	    	blink = new BlinkColor<HTMLFlow>(statusBlink) {
//				@Override
//				protected void job(String value) {
//					element.setBackgroundColor(value);
//				}
//	
//				@Override
//				protected void lastJob(String value) {
//					element.setBackgroundColor(null);
//				}
//	    	};
//        }

		log = ItemFactory.newImgIcon("logging.png");
		log.setTooltip(I18N.message("lastevents"));
		log.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				EventsWindow.get().show();
				if (statusLabel != null)
					statusLabel.setContents("");
			}
		});

		close = ItemFactory.newImgIcon("delete.png");
		close.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				setVisible(false);
			}
		});
		// 20130801,junsoo, 닫기 버튼 표시하지 않음.
		close.setVisible(false);
		
		running = ItemFactory.newImgIcon("running_task.gif");
		running.setVisible(false);

		addMember(close);
		prepareCopyright(Session.get().getInfo().getCopyright(), "statusBarCopyRight");
		
		addMember(log);
//		addMember(statusBlink);
		addMember(running);

	}

	public static EventPanel get() {
		if (instance == null)
			instance = new EventPanel();
		return instance;
	}
	
//	private void blink(int type){
//
//		if (type == STATUS_PROCESS)
//			return;
//
//    	String sourceColor = null;
//    	if (type >= 0 && type < STATUS_MAX)
//    		sourceColor = STATUS_COLORS[type];
//    	
//    	if (sourceColor != null) {
//    		blink.animate(500, sourceColor, "525252");
//    	}
//	}

	// 20130830, junsoo copyright
	private void prepareCopyright(final String text, String style) {
		Label lblCopy = new Label(text);
		lblCopy.setAutoFit(true);
		addMember(lblCopy);

		Label dummy = new Label("");
		dummy.setWidth(5);
		addMember(dummy);

		lblCopy.setStyleName(style);
		lblCopy.setWrap(false);
		
		running.setVisible(false);
	}

	private void prepareLabel(final String text, String style) {
		if (statusLabel != null && contains(statusLabel)) {
			removeMember(statusLabel);
		}
		
		statusLabel = new Label(text);
		statusLabel.setWidth100();
		addMember(statusLabel, 5);

		statusLabel.setStyleName(style);
		statusLabel.setWrap(false);
		statusLabel.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				EventsWindow.get().show();
				statusLabel.setContents("");
			}
		});
//		statusLabel.setHeight(18);
		
		running.setVisible(false);
	}

	public void error(String message, String detail) {
		prepareLabel(message, "footerError");
		SEvent event = new SEvent();
		event.setMessage(message);
		event.setDetail(detail != null ? detail : message);
		event.setSeverity(SEvent.ERROR);
		EventsWindow.get().addEvent(event);
		setVisible(true);
	}

	public void warn(String message, String detail) {
		prepareLabel(message, "footerWarn");
		SEvent event = new SEvent();
		event.setMessage(message);
		event.setDetail(detail != null ? detail : message);
		event.setSeverity(SEvent.WARNING);
		EventsWindow.get().addEvent(event);
		setVisible(true);
	}

	public void info(String message, String detail) {
		prepareLabel(message, "footerInfo");
		SEvent event = new SEvent();
		event.setMessage(message);
		event.setDetail(detail != null ? detail : message);
		event.setSeverity(SEvent.INFO);
		EventsWindow.get().addEvent(event);
		setVisible(true);
	}
	
	public void running(String message){
		prepareLabel(message, "footerInfo");
		running.setVisible(true);
		setVisible(true);
	}

	public boolean isShowLog() {
		return log != null && log.isVisible();
	}

	public void setShowLog(boolean showLog) {
		this.log.setVisible(showLog);
	}

	public boolean isShowClose() {
		return close != null && close.isVisible();
	}

	public void setShowClose(boolean showClose) {
		this.close.setVisible(showClose);
	}

	public void cleanLabel() {
		statusLabel.setContents("");
	}
}