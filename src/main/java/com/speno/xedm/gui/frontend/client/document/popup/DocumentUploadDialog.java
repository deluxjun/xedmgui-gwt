package com.speno.xedm.gui.frontend.client.document.popup;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.HTMLPane;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.CloseClickEvent;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.speno.xedm.core.service.serials.SDocument;
import com.speno.xedm.core.service.serials.SExtendedAttribute;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.Session;
import com.speno.xedm.gui.common.client.log.Log;
import com.speno.xedm.gui.common.client.util.DefaultAsyncCallbackWithStatus;
import com.speno.xedm.gui.common.client.util.ReturnHandler;
import com.speno.xedm.gui.common.client.util.ServiceUtil;
import com.speno.xedm.gui.common.client.util.Util;
import com.speno.xedm.gui.common.client.window.BaseWindow;
import com.speno.xedm.gui.common.client.window.Waiting;
import com.speno.xedm.gui.frontend.client.document.DocumentObserver;

/**
 * 문서 등록 dialog
 * 
 * @author deluxjun
 *
 */
public class DocumentUploadDialog extends BaseWindow {
	private VLayout layout = new VLayout();

	private DocumentUploadPanel updatePanel;
	
	private HLayout savePanel = new HLayout();

	private DocumentObserver documentObserver;
	
	public DocumentUploadDialog(DocumentObserver observer) {
		super(null);
		
		addCloseClickHandler(new CloseClickHandler() {
			@Override
			public void onCloseClick(CloseClickEvent event) {
				destroy();
			}
		});

		setTitle(I18N.message("createdocuments"));
		setHeight(460);
		
		this.documentObserver = observer;
		updatePanel = new DocumentUploadPanel(null, observer);
		updatePanel.setWidth100();
		updatePanel.setHeight("100%");
		updatePanel.setShowResizeBar(false);

//		updatePanel.setOkListener(new ReturnHandler<SDocument>() {
//			public void onReturn(final SDocument document) {
//				ServiceUtil.document().addDocuments(Session.get().getSid(), document, new DefaultAsyncCallbackWithStatus<Void>(DocumentUploadDialog.this, I18N.message("createdocuments")){
//					@Override
//					public void onSuccessEvent(Void result) {
//						DocumentsPanel.get().onReloadRequest(document.getFolder());
//						Log.infoWithPopup(I18N.message("info"), I18N.message("docuploaded"));
//						close();		// window close
//						
//						// 서버 임시 저장소 클린
//						ServiceUtil.document().cleanUploadedFileFolder(Session.get().getSid(), new AsyncCallback<Void>() {
//							@Override
//							public void onSuccess(Void result) {
//							}
//							
//							@Override
//							public void onFailure(Throwable caught) {
//								Log.warn(I18N.message("warning"), caught.getMessage());
//							}
//						});
//					}
//					@Override
//					public void onFailureEvent(Throwable caught) {
//						Log.serverError(caught, true);
//					}
//				});	
//			}
//		});
		
		
		updatePanel.setCanSaveListener(new ReturnHandler<Boolean>() {
			@Override
			public void onReturn(Boolean param) {
				if (param == Boolean.TRUE) {
					savePanel.setDisabled(false);
					savePanel.setVisible(true);
				} else {
					savePanel.setDisabled(true);
					savePanel.setVisible(true);
				}
			}
		});

		
		HTMLPane spacer = new HTMLPane();
		spacer.setContents("<div>&nbsp;</div>");
		spacer.setWidth("60%");
		spacer.setOverflow(Overflow.HIDDEN);

		Button saveButton = new Button(I18N.message("save"));
		saveButton.setAutoFit(true);
		saveButton.setMargin(2);
		saveButton.addClickHandler(new com.smartgwt.client.widgets.events.ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onSave();
			}
		});
		
		HTMLPane spacer2 = new HTMLPane();
		spacer2.setContents("<div>&nbsp;</div>");
		spacer2.setWidth("2%");
		spacer2.setOverflow(Overflow.HIDDEN);

		DynamicForm saveForm = new DynamicForm();
		savePanel.addMember(saveForm);
		savePanel.addMember(spacer);
		savePanel.addMember(saveButton);
		savePanel.addMember(spacer2);
		savePanel.setHeight(25);
		savePanel.setMembersMargin(10);
		savePanel.setDisabled(true);
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

	private void onSave() {
		if (!updatePanel.validate())
			return;
		
		final SDocument document = updatePanel.getDocument();
		if(Util.getSetting("setting.waiting")) Waiting.show(I18N.message("wattingforserver"));

		ServiceUtil.document().addDocuments(Session.get().getSid(), document, new DefaultAsyncCallbackWithStatus<SDocument>(I18N.message("createdocuments")){
			@Override
			public void onSuccessEvent(SDocument result) {
				if (documentObserver != null)
					documentObserver.onReloadRequest(document.getFolder());
				
				// 서버 임시 저장소 클린
				ServiceUtil.document().cleanUploadedFileFolder(Session.get().getSid(), new AsyncCallback<Void>() {
					@Override
					public void onSuccess(Void result) {
						Waiting.hide();
					}
					
					@Override
					public void onFailure(Throwable caught) {
						Log.warn(I18N.message("warning"), caught.getMessage());
						Waiting.hide();
					}
				});

				Log.infoWithPopup(I18N.message("info"), I18N.message("docuploaded"));
				destroy();		// window close

			}
			@Override
			public void onFailureEvent(Throwable caught) {
				Log.serverError(caught, true);
				document.setAttributes(new SExtendedAttribute[0]);
				Waiting.hide();
			}
		});	
	}

}