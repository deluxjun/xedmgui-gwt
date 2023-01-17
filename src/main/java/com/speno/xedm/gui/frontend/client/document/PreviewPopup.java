package com.speno.xedm.gui.frontend.client.document;

import com.smartgwt.client.types.ContentsType;
import com.smartgwt.client.types.HeaderControls;
import com.smartgwt.client.widgets.HTMLFlow;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.CloseClickEvent;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.layout.VLayout;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.Session;

public class PreviewPopup extends Window {
	private HTMLFlow html = null;

	private VLayout layout = null;

	private long id;

	private String elementId;

	private boolean printEnabled = false;

	public PreviewPopup(long docId, String elementId, boolean printEnabled) {
		setHeaderControls(HeaderControls.HEADER_LABEL, HeaderControls.CLOSE_BUTTON);
		setTitle(I18N.message("preview"));

		int size = 100;
		try {
			size = Integer.parseInt(Session.get().getInfo().getConfig("gui.preview.size"));
			if (size <= 0 || size > 100)
				size = 100;
		} catch (Throwable t) {

		}

		setWidth(Math.round((float) com.google.gwt.user.client.Window.getClientWidth() * (float) size / 100F - 100));
		setHeight(Math.round((float) com.google.gwt.user.client.Window.getClientHeight() * (float) size / 100F - 100));
		setCanDragResize(true);
		setIsModal(true);
		setShowModalMask(true);
		centerInPage();
		setMargin(2);

		this.id = docId;
		this.elementId = elementId;
		this.printEnabled = printEnabled;

		layout = new VLayout(5);
		layout.setTop(20);
		layout.setMargin(0);
		// kimsoeun 20141229
		layout.setWidth("100%");
		layout.setHeight("100%");
		

		reloadPreview();

		addCloseClickHandler(new CloseClickHandler() {
			@Override
			public void onCloseClick(CloseClickEvent event) {
				close();
			}
		});
		
		addResizedHandler(new com.smartgwt.client.widgets.events.ResizedHandler() {
			
			@Override
			public void onResized(com.smartgwt.client.widgets.events.ResizedEvent event) {
				if (html != null) {
					layout.removeMember(html);
					reloadPreview();
				}
			}
		});

		addChild(layout);
	}

	/**
	 * Reloads a preview.
	 */
	private void reloadPreview() {

		html = new HTMLFlow();
		
		String urlEid = (elementId != null && elementId.length() > 0)? "&elementId=" + elementId : "";
		
		String url = "";
		if (id > 0L)
			// kimsoeun 20141229
			url += "http://localhost:8082/NIPA/ImageViewer_Sample.jsp?sid=" + Session.get().getSid() + "&docId=" + id + urlEid;
		else
			url += "http://localhost:8082/NIPA/ImageViewer_Sample.jsp?sid=" + Session.get().getSid() + urlEid;

		url += "&viewerType=1";		// inline ºä¾î´Â 1
		url += "&print=" + printEnabled;
		
		html.setShowEdges(true);  
		html.setContentsURL(url);
		html.setContentsType(ContentsType.PAGE);  
//		html.setContents(url);
		
		layout.addMember(html);
		
	}

	public static int getZoom() {
		try {
			return Integer.parseInt(Session.get().getInfo().getConfig("gui.preview.zoom"));
		} catch (Throwable t) {
			return 100;
		}
	}
	
}