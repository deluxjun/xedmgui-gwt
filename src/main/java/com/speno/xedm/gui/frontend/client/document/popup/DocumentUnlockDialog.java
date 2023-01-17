package com.speno.xedm.gui.frontend.client.document.popup;


import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.HTMLPane;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.CloseClickEvent;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.ValuesManager;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.speno.xedm.core.service.serials.SDocument;
import com.speno.xedm.core.service.serials.SFolder;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.Session;
import com.speno.xedm.gui.common.client.log.Log;
import com.speno.xedm.gui.common.client.util.ServiceUtil;
import com.speno.xedm.gui.common.client.window.BaseWindow;
import com.speno.xedm.gui.frontend.client.document.DocumentObserver;
import com.speno.xedm.gui.frontend.client.document.DocumentsPanel;

/**
 * 문서 잠금 dialog
 * 
 * @author deluxjun
 *
 */
public class DocumentUnlockDialog extends BaseWindow {
	private VLayout layout = new VLayout();

	protected DocumentUploadPanel updatePanel;
	
	protected HLayout savePanel = new HLayout();
	protected ValuesManager vm;
	
	protected Button saveButton;
	
	protected DocumentObserver documentObserver;


	public DocumentUnlockDialog(SDocument document) {
		super(null);
		
		addCloseClickHandler(new CloseClickHandler() {
			@Override
			public void onCloseClick(CloseClickEvent event) {
				destroy();
			}
		});
		
		documentObserver = DocumentsPanel.get();

		setTitle(I18N.message("unlock"));
		setHeight(380);
		
		// 모든 동작을 readonly로 설정
		setReadOnly(document);
		
		updatePanel = new DocumentUploadPanel(document, documentObserver, false);
		updatePanel.setWidth100();
		updatePanel.setHeight("100%");
		updatePanel.setShowResizeBar(false);

		saveButton = new Button(I18N.message("ok"));
		saveButton.setWidth(60);
		saveButton.setMargin(2);
		saveButton.setDisabled(false);
		saveButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(com.smartgwt.client.widgets.events.ClickEvent event) {
				onSave();
			}
		});
		
		HTMLPane spacer = new HTMLPane();
		spacer.setContents("<div>&nbsp;</div>");
		spacer.setWidth("100%");
		spacer.setOverflow(Overflow.HIDDEN);

		HTMLPane spacer2 = new HTMLPane();
		spacer2.setContents("<div>&nbsp;</div>");
		spacer2.setWidth("2%");
		spacer2.setOverflow(Overflow.HIDDEN);
		
		DynamicForm saveForm = new DynamicForm();
		saveForm.setNumCols(6);
		vm = new ValuesManager();
		saveForm.setValuesManager(vm);
		
		savePanel.addMember(saveForm);
		savePanel.addMember(spacer);
		savePanel.addMember(saveButton);
		savePanel.addMember(spacer2);
		savePanel.setHeight(25);
		savePanel.setMembersMargin(10);
		savePanel.setVisible(true);
		savePanel.setWidth100();
		savePanel.setStyleName("infoPanel");

		layout.setMembersMargin(10);
		layout.setTop(25);
		layout.setMargin(3);
		layout.setWidth100();
		layout.setHeight("99%");
		layout.setMembers(updatePanel, savePanel);

		setPanel(layout);
	}


	private void setReadOnly(SDocument document) {
		SFolder folder = document.getFolder();
		folder.setPermissions(new String[]{"download", "view"});
	}
	
	protected void onSave() {
		final SDocument document = updatePanel.getDocument();
		long[] docIds = new long[]{document.getId()};

		ServiceUtil.document().unlock(Session.get().getSid(), docIds, new AsyncCallback<Void>() {
			@Override
			public void onSuccess(Void result) {
				document.setStatus(SDocument.DOC_UNLOCKED);
				documentObserver.onDocumentSaved(document);
				Log.infoWithPopup(I18N.message("second.client.command.unlock"), I18N.message("second.client.successfully"));
				destroy();
			}
			@Override
			public void onFailure(Throwable caught) {
				Log.serverError(caught, true);
			}
		});
	}


}