package com.speno.xedm.gui.frontend.client.document.popup;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.speno.xedm.core.service.serials.SDocument;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.Session;
import com.speno.xedm.gui.common.client.log.Log;
import com.speno.xedm.gui.common.client.util.ServiceUtil;
import com.speno.xedm.gui.common.client.util.Util;
import com.speno.xedm.gui.common.client.window.Waiting;
import com.speno.xedm.gui.frontend.client.document.DocumentActionUtil;
import com.speno.xedm.gui.frontend.client.document.DocumentsPanel;

public class DocumentCheckoutCancelDialog extends DocumentUnlockDialog{

	public DocumentCheckoutCancelDialog(SDocument document) {
		super(document);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected void onSave() {
		final SDocument document = updatePanel.getDocument();
		long[] docIds = new long[]{document.getId()};

		if(Util.getSetting("setting.waiting")) Waiting.show(I18N.message("wattingforserver"));
		ServiceUtil.document().checkOutCancel(Session.get().getSid(), docIds, new AsyncCallback<Void>() {
			@Override
			public void onSuccess(Void result) {
				document.setStatus(SDocument.DOC_UNLOCKED);
				documentObserver.onDocumentSaved(document);
				Waiting.hide();
				
				// 20140306, junsoo, 만약 체크아웃리스트를 보고 있으면 refresh
				if (DocumentActionUtil.TYPE_CHECKED == DocumentActionUtil.get().getActivatedMenuType())
					DocumentsPanel.get().showListPanel();

				Log.infoWithPopup(I18N.message("cancelcheckout"), I18N.message("second.client.successfully"));
				destroy();
			}
			@Override
			public void onFailure(Throwable caught) {
				Waiting.hide();
				Log.serverError(caught, true);
			}
		});
	}

}
