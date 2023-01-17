package com.speno.xedm.gui.common.client.window;

import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Label;
import com.speno.xedm.core.service.serials.SDocument;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.frontend.client.document.DocumentActionUtil;
import com.speno.xedm.gui.frontend.client.document.DocumentsPanel;
import com.speno.xedm.gui.frontend.client.document.prop.DocumentDetailsPanel;

public class DocumentPropertiesWindow extends BaseWindow {
	private static DocumentPropertiesWindow instance = null;

//	private DocumentDetailsPanel panel = null;
	private Label dummy = null;
	
	public static DocumentPropertiesWindow get() {
		if (instance == null)
			instance = new DocumentPropertiesWindow(null);
		return instance;
	}

	public DocumentPropertiesWindow(Canvas item) {
		super(item);
		dummy = new Label("&nbsp;" + I18N.message("selectfolderordoc"));
	} 

	@Override
	public void refresh() {
		// 보여지고 있으며, 최소화상태인 경우에만 갱신
		SDocument[] docs = DocumentActionUtil.get().getCurrentDocuments();
		if (docs != null && docs.length > 0){
			refresh(docs[0]);
		}
	}
	
	public void refresh(SDocument doc) {
		if (panel == null)
			return;
		
		// 복원
		if (!contains(panel)) {
			removeItem(dummy);
			addItem(panel);
		}
		// 보여지고 있으며, 최소화상태인 경우에만 갱신
		if (getActivated()) {
//			((DocumentDetailsPanel)panel).refreshDocument(doc);
			((DocumentDetailsPanel)panel).setDocument(doc);
		}
	}

	@Override
	public void show() {
		if (panel == null) {
			panel = new DocumentDetailsPanel(DocumentsPanel.get());
			addItem(panel);
		}

		super.show();
	}
	
	// 문서가 아무것도 선택되지 않았을 경우 호출됨.
	public void empty() {
		if (panel != null && contains(panel))
			removeItem(panel);
		else
			return;
		
		addItem(dummy);
	}


}