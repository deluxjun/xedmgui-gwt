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
import com.smartgwt.client.widgets.form.fields.BooleanItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.speno.xedm.core.service.serials.SDocument;
import com.speno.xedm.core.service.serials.SFolder;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.Session;
import com.speno.xedm.gui.common.client.log.Log;
import com.speno.xedm.gui.common.client.util.DefaultAsyncCallbackWithStatus;
import com.speno.xedm.gui.common.client.util.ItemFactory;
import com.speno.xedm.gui.common.client.util.ReturnHandler;
import com.speno.xedm.gui.common.client.util.ServiceUtil;
import com.speno.xedm.gui.common.client.window.BaseWindow;
import com.speno.xedm.gui.frontend.client.document.DocumentActionUtil;
import com.speno.xedm.gui.frontend.client.document.DocumentsPanel;

/**
 * check in dialog
 * 
 * @author deluxjun
 *
 */
public class DocumentCheckinDialog extends BaseWindow {
	private VLayout layout = new VLayout();

	private DocumentUploadPanel updatePanel;
	
	private HLayout savePanel = new HLayout();
	private ValuesManager vm;
	
	private Button saveButton;


	public DocumentCheckinDialog(SDocument document) {
		super(null);
		
		addCloseClickHandler(new CloseClickHandler() {
			@Override
			public void onCloseClick(CloseClickEvent event) {
				destroy();
			}
		});

		setTitle(I18N.message("checkin"));
		setHeight(460);
		
		// 모든 동작을 readonly로 설정
		setReadOnly(document);
		
		updatePanel = new DocumentUploadPanel(document, DocumentsPanel.get(), true);
		updatePanel.setWidth100();
		updatePanel.setHeight("100%");
		updatePanel.setShowResizeBar(false);

		updatePanel.setCanSaveListener(new ReturnHandler<Boolean>() {
			@Override
			public void onReturn(Boolean param) {
				if (param == Boolean.TRUE) {
					savePanel.setVisible(true);
				} else {
					savePanel.setVisible(false);
				}
			}
		});
		saveButton = new Button(I18N.message("save"));
		saveButton.setAutoFit(true);
		saveButton.setMargin(2);
		saveButton.setDisabled(true);
		saveButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(com.smartgwt.client.widgets.events.ClickEvent event) {
				onSave();
			}
		});
		
		HTMLPane spacer = new HTMLPane();
		spacer.setContents("<div>&nbsp;</div>");
		spacer.setWidth("10%");
		spacer.setOverflow(Overflow.HIDDEN);

		HTMLPane spacer2 = new HTMLPane();
		spacer2.setContents("<div>&nbsp;</div>");
		spacer2.setWidth("2%");
		spacer2.setOverflow(Overflow.HIDDEN);
		
		DynamicForm saveForm = new DynamicForm();
		saveForm.setNumCols(4);
		vm = new ValuesManager();
		saveForm.setValuesManager(vm);

		BooleanItem versionItem = new BooleanItem();
		versionItem.setName("majorversion");
		versionItem.setTitle(I18N.message("majorversion"));

		final TextItem commentItem = ItemFactory.newTextItem("comment", "comment", null);
		commentItem.setRequired(true);
		commentItem.setWidth(300);
		commentItem.addChangedHandler(new ChangedHandler() {
			@Override
			public void onChanged(ChangedEvent event) {
				if (commentItem.getValueAsString().length() > 0)
					saveButton.setDisabled(false);
				else
					saveButton.setDisabled(true);
			}
		});

		saveForm.setItems(commentItem, versionItem);
		
		savePanel.addMember(saveForm);
		savePanel.addMember(spacer);
		savePanel.addMember(saveButton);
		savePanel.addMember(spacer2);
		savePanel.setHeight(25);
		savePanel.setMembersMargin(10);
		savePanel.setVisible(false);
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
	
	private void onSave() {
		if (!updatePanel.validate())
			return;
		
		final SDocument document = updatePanel.getDocument();

		String comment = vm.getValueAsString("comment");
		boolean major = "true".equals(vm.getValueAsString("majorversion"));

		ServiceUtil.document().checkin(Session.get().getSid(), document, comment, major,

				new DefaultAsyncCallbackWithStatus<Void>(I18N.message("checkin")){
			
					@Override
					public void onSuccessEvent(Void result) {
//						ServiceUtil.document().getById(Session.get().getSid(), document.getId(), new AsyncCallback<SDocument>() {
//							@Override
//							public void onFailure(Throwable caught) {
//								Log.serverError(caught, false);
//							}
//
//							@Override
//							public void onSuccess(SDocument document) {
//								DocumentsPanel.get().onDocumentSaved(document);
//								Session.get().getUser().setCheckedOutDocs(Session.get().getUser().getCheckedOutDocs() - 1);
//								Log.infoWithPopup(I18N.message("second.client.command.checkin"), I18N.message("second.client.successfully"));
//								destroy();		// window close
//							}
//						});
						
						DocumentsPanel.get().onDocumentSaved(document);
						Session.get().getUser().setCheckedOutDocs(Session.get().getUser().getCheckedOutDocs() - 1);

						// 체크인 서비스 완료시 서버의 temp 저장소를 클린한다.
						ServiceUtil.document().cleanUploadedFileFolder(Session.get().getSid(), new AsyncCallback<Void>() {
							@Override
							public void onSuccess(Void result) {
							}
							@Override
							public void onFailure(Throwable caught) {
								Log.warn(I18N.message("warning"), caught.getMessage());
							}
						});
						
						// 20140306, junsoo, 만약 체크아웃리스트를 보고 있으면 refresh
						if (DocumentActionUtil.TYPE_CHECKED == DocumentActionUtil.get().getActivatedMenuType())
							DocumentsPanel.get().showListPanel();
						
						Log.infoWithPopup(I18N.message("second.client.command.checkin"), I18N.message("second.client.successfully"));
						destroy();		// window close
					}
					@Override
					public void onFailureEvent(Throwable caught) {
						Log.serverError(caught, true);
					}
				});
	}


}