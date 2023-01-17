package com.speno.xedm.gui.frontend.client.document.popup;

import java.util.Date;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.DateDisplayFormat;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.HTMLPane;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.CloseClickEvent;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.ValuesManager;
import com.smartgwt.client.widgets.form.fields.DateItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.speno.xedm.core.service.serials.SDocType;
import com.speno.xedm.core.service.serials.SDocument;
import com.speno.xedm.core.service.serials.SFolder;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.Session;
import com.speno.xedm.gui.common.client.log.Log;
import com.speno.xedm.gui.common.client.util.ItemFactory;
import com.speno.xedm.gui.common.client.util.ServiceUtil;
import com.speno.xedm.gui.common.client.util.Util;
import com.speno.xedm.gui.common.client.window.BaseWindow;
import com.speno.xedm.gui.frontend.client.document.DocumentObserver;
import com.speno.xedm.gui.frontend.client.document.DocumentsPanel;

/**
 * 문서 잠금 dialog
 * 
 * @author deluxjun
 *
 */
public class DocumentLockDialog extends BaseWindow {
	private VLayout layout = new VLayout();

	protected DocumentUploadPanel updatePanel;
	
	protected HLayout savePanel = new HLayout();
	protected ValuesManager vm;
	
	protected Button saveButton;
	
	protected DocumentObserver documentObserver;

	private DateItem dateText;
	
	private int deadLine = 7;

	public DocumentLockDialog(SDocument document) {
		super(null);
		
		addCloseClickHandler(new CloseClickHandler() {
			@Override
			public void onCloseClick(CloseClickEvent event) {
				destroy();
			}
		});
		
		documentObserver = DocumentsPanel.get();

		setTitle(I18N.message("lock"));
		setHeight(380);
		
		// 모든 동작을 readonly로 설정
		setReadOnly(document);
		
		updatePanel = new DocumentUploadPanel(document, documentObserver, false);
		updatePanel.setWidth100();
		updatePanel.setHeight("100%");
		updatePanel.setShowResizeBar(false);

		saveButton = new Button(I18N.message("ok"));
//		saveButton.setAutoFit(true);
		saveButton.setWidth(60);
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
		spacer.setWidth("100%");
		spacer.setOverflow(Overflow.HIDDEN);

		HTMLPane spacer2 = new HTMLPane();
		spacer2.setContents("<div>&nbsp;</div>");
		spacer2.setWidth("2%");
		spacer2.setOverflow(Overflow.HIDDEN);
		
		DynamicForm saveForm = new DynamicForm();
		saveForm.setNumCols(2);
		vm = new ValuesManager();
		saveForm.setValuesManager(vm);
		
		dateText = new DateItem("deadline", I18N.message("second.ceeckoutdeadline"));
		dateText.setHint("<nobr>" + I18N.message("second.client.timeToCanceled") + "</nobr>");
		dateText.setUseTextField(false);
		dateText.setWrapTitle(false);
		dateText.setDateFormatter(DateDisplayFormat.TOJAPANSHORTDATE);
		dateText.setAlign(Alignment.LEFT);
		dateText.setPickerIconPrompt(null);
		dateText.addChangedHandler(new ChangedHandler() {
			@Override
			public void onChanged(ChangedEvent event) {
				final Date deadLineDate = Util.getExpireDateFromRetention(null, deadLine);
				//20131216 na 오늘 이전날짜와 비교를 해야 당일도 잠금을 선택할 수 있음.
				Date today = Util.getExpireDateFromRetention(null, -1);
				Date compare = dateText.getValueAsDate();
				
				if(today.after(compare)){
					SC.warn(I18N.message("youcantchoiceafterday"));
					dateText.setValue(deadLineDate);
				}
			}
		});
		// 20131209, junsoo, set default deadline
		setDeadline(deadLine);

		ServiceUtil.documentcode().getSDocType(Session.get().getSid(), document.getDocType(), new AsyncCallback<SDocType>() {
			@Override
			public void onSuccess(SDocType result) {
				int deadLineDays = 7;
				if (result.getDeadLine() != null)
					deadLineDays = result.getDeadLine();
				else
					try {
						deadLineDays = Session.get().getInfo().getIntConfig("gui.defaultCheckoutDeadLine", 3);
					} catch (Exception e) {}
				
				deadLine = deadLineDays;
				setDeadline(deadLine);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				Log.serverError(caught, false);
			}
		});



		final TextItem commentItem = ItemFactory.newTextItem("comment", "comment", null);
		commentItem.setRequired(true);
		commentItem.setWidth(300);
		commentItem.setWrapTitle(false);
		commentItem.addChangedHandler(new ChangedHandler() {
			@Override
			public void onChanged(ChangedEvent event) {
				if (commentItem.getValueAsString().length() > 0)
					saveButton.setDisabled(false);
				else
					saveButton.setDisabled(true);
			}
		});

		saveForm.setItems(commentItem, dateText);
		
		savePanel.addMember(saveForm);
		savePanel.addMember(spacer);
		savePanel.addMember(saveButton);
		savePanel.addMember(spacer2);
//		savePanel.setHeight(25);
//		savePanel.setAlign(VerticalAlignment.BOTTOM);
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
	
	// 20131209, junsoo, set deadline
	private void setDeadline(int deadLineDays) {
		final Date deadLineDate = Util.getExpireDateFromRetention(null, deadLineDays);
		dateText.setDefaultValue(deadLineDate);
	}


	private void setReadOnly(SDocument document) {
		SFolder folder = document.getFolder();
		folder.setPermissions(new String[]{"download", "view"});
	}
	
	protected void onSave() {
		final SDocument document = updatePanel.getDocument();
		long[] docIds = new long[]{document.getId()};
		String comment = vm.getValueAsString("comment");
		Date deadLine = (Date)vm.getValue("deadline");
		
		ServiceUtil.document().lock(Session.get().getSid(), docIds, comment, deadLine, new AsyncCallback<Void>() {
			@Override
			public void onSuccess(Void result) {
				document.setStatus(SDocument.DOC_LOCKED);
				documentObserver.onDocumentSaved(document);
				Session.get().getUser().setLockedDocs(Session.get().getUser().getLockedDocs() - 1);
				Log.infoWithPopup(I18N.message("lock"), I18N.message("lockSuccess"));
				System.out.println("onSuccess");
				destroy();
			}
			@Override
			public void onFailure(Throwable caught) {
				Log.serverError(caught, true);
			}
		});

	}


}