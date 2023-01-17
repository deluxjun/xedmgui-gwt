package com.speno.xedm.gui.frontend.client.document.popup;

import java.util.Date;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.speno.xedm.core.service.serials.SDocument;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.Session;
import com.speno.xedm.gui.common.client.log.Log;
import com.speno.xedm.gui.common.client.util.ServiceUtil;
import com.speno.xedm.gui.common.client.util.Util;
import com.speno.xedm.gui.common.client.window.Waiting;

/**
 * 문서 check out dialog (DocumentLockDialog 로 부터 상속)
 * 
 * @author deluxjun
 *
 */
public class DocumentCheckoutDialog extends DocumentLockDialog {

	public DocumentCheckoutDialog(SDocument document) {
		super(document);
		
		setTitle(I18N.message("checkout"));
	}

	@Override
	protected void onSave() {
		final SDocument document = updatePanel.getDocument();
		long[] docIds = new long[]{document.getId()};
		String comment = vm.getValueAsString("comment");
		Date deadLine = (Date)vm.getValue("deadline");
		
		if(Util.getSetting("setting.waiting")) Waiting.show(I18N.message("wattingforserver"));
		ServiceUtil.document().checkout(Session.get().getSid(), docIds, comment, deadLine, new AsyncCallback<Void>() {
			@Override
			public void onSuccess(Void result) {
				Waiting.hide();
				document.setStatus(SDocument.DOC_CHECKED_OUT);
				documentObserver.onDocumentSaved(document);

				Log.infoWithPopup(I18N.message("second.client.command.checkout"), I18N.message("second.client.successfully"));
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