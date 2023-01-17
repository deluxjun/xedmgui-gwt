package com.speno.xedm.gui.common.client.window;

import com.smartgwt.client.types.HeaderControls;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Window;
import com.speno.xedm.gui.common.client.I18N;

public class BaseWindow extends Window {
	protected Canvas panel = null;
	
	public BaseWindow(Canvas item) {
		super();

		setHeaderControls(HeaderControls.HEADER_LABEL, HeaderControls.CLOSE_BUTTON);
		setTitle(I18N.message("properties"));
		setWidth(800);
		setHeight(400);
		setCanDragResize(true);
		setCanDragReposition(true);
		setIsModal(true);		// 20130805, junsoo, modal 로 수정.
		setShowModalMask(true);
		centerInPage();
		
		setDismissOnEscape(true);

		if (item != null)
			addItem(item);

		refresh();
	}
	
	protected boolean getActivated() {
		return isVisible() && panel != null && !getMinimized();
	}
	
	public void setPanel(Canvas panel){
		if (this.panel != null && contains(this.panel)) {
			removeItem(this.panel);
			this.panel.destroy();
		}

		if (panel != null){
			this.panel = panel;
			addItem(panel);
			refresh();
		}
	}
	
	public void refresh(){
		
	}

	public void show() {
		// 최소화 되어 있으면 최대화 시킴
		if (getMinimized() || !isVisible())
			restore();
			
		super.show();

		refresh();
	}
	
	public void close() {
		hide();
	}
	
}