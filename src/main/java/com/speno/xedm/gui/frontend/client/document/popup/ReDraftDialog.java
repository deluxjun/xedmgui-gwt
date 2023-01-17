package com.speno.xedm.gui.frontend.client.document.popup;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.HTMLPane;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.CloseClickEvent;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.form.ValuesManager;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.events.TabSelectedEvent;
import com.smartgwt.client.widgets.tab.events.TabSelectedHandler;
import com.speno.xedm.core.service.serials.SDocument;
import com.speno.xedm.core.service.serials.SFolder;
import com.speno.xedm.core.service.serials.SRewrite;
import com.speno.xedm.core.service.serials.SRewriteProcess;
import com.speno.xedm.gui.common.client.Constants;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.Session;
import com.speno.xedm.gui.common.client.log.Log;
import com.speno.xedm.gui.common.client.util.DefaultAsyncCallbackWithStatus;
import com.speno.xedm.gui.common.client.util.ReturnHandler;
import com.speno.xedm.gui.common.client.util.ServiceUtil;
import com.speno.xedm.gui.common.client.util.Util;
import com.speno.xedm.gui.common.client.window.BaseWindow;
import com.speno.xedm.gui.common.client.window.Waiting;
import com.speno.xedm.gui.frontend.client.document.DocumentsPanel;

/**
 * check in dialog
 * 
 * @author deluxjun
 *
 */
public class ReDraftDialog extends BaseWindow {
	private VLayout layout = new VLayout();

	private ApproveRequestPanel approvePanel;
	private DocumentUploadPanel updatePanel;
	
	private HLayout savePanel = new HLayout();
	private ValuesManager vm;
	
	private Button saveButton;
	
	private SDocument document;
	private int command;


	public ReDraftDialog(final SDocument document, int commandl) {
		super(null);
		
		addCloseClickHandler(new CloseClickHandler() {
			@Override
			public void onCloseClick(CloseClickEvent event) {
				destroy();
			}
		});
		
		this.document = document;
		this.command = commandl;
		
		if (Constants.DRAFT_TYPE_REGISTRATION == commandl) {
			// TODO: ���� �������� ����.
			Log.warnWithPopup(I18N.message("notsupported"), "");
			destroy();
		}

		setTitle(I18N.message("redraft"));
		setHeight(460);
		
		// ��� ������ readonly�� ����
//		setReadOnly(document);
		
		updatePanel = new DocumentUploadPanel(document, DocumentsPanel.get(), true);
		updatePanel.setWidth100();
		updatePanel.setHeight("100%");
		updatePanel.setShowResizeBar(false);

		updatePanel.setCanSaveListener(new ReturnHandler<Boolean>() {
			@Override
			public void onReturn(Boolean param) {
//				if (param == Boolean.TRUE) {
//					savePanel.setVisible(true);
//				} else {
//					savePanel.setVisible(false);
//				}
			}
		});
		saveButton = new Button(I18N.message("save"));
		saveButton.setAutoFit(true);
		saveButton.setMargin(2);
		saveButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(com.smartgwt.client.widgets.events.ClickEvent event) {
				SRewrite rewrite = new SRewrite();
				
				// ��� ����� �ƴҰ�� (��� Tab�� Disable ���θ� ���� ����)
				if(command != Constants.DRAFT_TYPE_REGISTRATION){
					rewrite.setTargetId(document.getId());	//��� DocID ����
					if(approvePanel.validation()){
						makeRewrite(rewrite);
					}
				}
				// ��� ����� ���
				else{
//					if(updatePanel.validate() && approvePanel.validation()){
//						makeDocAndRewrite(rewrite);
//					}
				}
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

		initApprovePanel(document, command);
		
		setPanel(layout);
	}

	private void initApprovePanel(SDocument document, int command){
		approvePanel = new ApproveRequestPanel(document, this, command + "");
		
		approvePanel.getDraftType().setValue(command + "");

		Tab approveTab= new Tab(I18N.message("draft"));
		approveTab.setID("draftTab");
		approveTab.setPane(approvePanel);
		approveTab.addTabSelectedHandler(new TabSelectedHandler() {
			@Override
			public void onTabSelected(TabSelectedEvent event) {
			}
		});
		
		// UploadPanel�� Tab�� ù���翡 ��� Tab�� �߰��Ѵ�.
		updatePanel.tabSet.addTab(approveTab, 0);
		// Tab�� ���� �������ϱ� ���� ����
		
		updatePanel.setCanSaveListener(new ReturnHandler<Boolean>() {
			@Override
			public void onReturn(Boolean param) {
//				if (param == Boolean.TRUE) {
//					bottomLayout.setVisible(true);
//				} else {
//					bottomLayout.setVisible(false);
//				}
			}
		});
		
		// ù��° Tab����
		updatePanel.tabSet.setSelectedTab(0);
		
		if (Constants.DRAFT_TYPE_REGISTRATION == command)
			controlTabByDraftType(true);
		else
			controlTabByDraftType(false);
	}
	
	public void controlTabByDraftType(boolean isRegistration){
		if(isRegistration){
			// dialog ũ�� Ȯ��
			setWidth(800);
			// ��� Tab Ȱ��ȭ
			updatePanel.tabSet.enableTab(1);
			updatePanel.tabSet.enableTab(2);
			// ���� ���Tab���� �ڵ� �̵�.
			updatePanel.tabSet.selectTab(1);
		}
		else{
			// dialog ũ�� ���
			setWidth(555);
			// ��� Tab ��Ȱ��ȭ
			updatePanel.tabSet.disableTab(1);
			updatePanel.tabSet.disableTab(2);
			// ��ȹ�ư Layout show
//			if(bottomLayout != null)
//				bottomLayout.show();	
		}
		updatePanel.controlUploader(isRegistration);
	}
	

	private void setReadOnly(SDocument document) {
		SFolder folder = document.getFolder();
		folder.setPermissions(new String[]{"download", "view"});
	}
	
	// TODO: ����� ������ ���Ŀ� ����
	private void makeDocAndRewrite(final SRewrite rewrite) {
		if (!updatePanel.validate())
			return;
		
		final SDocument document = updatePanel.getDocument();

		String comment = vm.getValueAsString("comment");
		boolean major = "true".equals(vm.getValueAsString("majorversion"));

		if(Util.getSetting("setting.waiting")) Waiting.show(I18N.message("wattingforserver"));

		ServiceUtil.document().checkin(Session.get().getSid(), document, comment, major,

				new DefaultAsyncCallbackWithStatus<Void>(I18N.message("checkin")){
			
					@Override
					public void onSuccessEvent(Void result) {
						// üũ�� ���� �Ϸ�� ������ temp ����Ҹ� Ŭ���Ѵ�.
						ServiceUtil.document().cleanUploadedFileFolder(Session.get().getSid(), new AsyncCallback<Void>() {
							@Override
							public void onSuccess(Void result) {
								rewrite.setTargetId(document.getId());
								makeRewrite(rewrite);
								// ���õ� ���ڵ常 refresh
								DocumentsPanel.get().refreshSelectedRecords();
								Waiting.hide();
							}
							@Override
							public void onFailure(Throwable caught) {
								Log.warn(I18N.message("warning"), caught.getMessage());
								Waiting.hide();
							}
						});
						
						Log.infoWithPopup(I18N.message("second.client.command.checkin"), I18N.message("second.client.successfully"));
						destroy();		// window close
					}
					@Override
					public void onFailureEvent(Throwable caught) {
						Log.serverError(caught, true);
						Waiting.hide();
					}
				});
	}

	/**
	 * ���� ���ۿ� ������ ����
	 * @param rewrite
	 */
	private void setRewriteOptions(SRewrite rewrite){
		// ����� �ƴҰ�� ���õ� ������ docTypeId�� ������
		if(updatePanel.tabSet.getTab(1).getDisabled())
			rewrite.setTargetDocTypeId(document.getDocType());
		// ����� ��� ���â�� docTypeId�� ������
		else
			rewrite.setTargetDocTypeId(updatePanel.getDocument().getDocType());
		
		// Draft Type ����
		rewrite.setCommand(Integer.parseInt(approvePanel.getDraftType().getValueAsString()));	
//		rewrite.setCommand(Integer.valueOf(approvePanel.getDraftType().getValueAsString()));	// ���(0), ����(1), checkOut(5), checkIn(6)
		
		// ��� ����� ����
		ListGrid grid = approvePanel.getApproveUserListGrid();
		ListGridRecord[] records = grid.getRecords();
		
		// ����� ���� ����� '��'�̹Ƿ� 1�� �߰�
		SRewriteProcess[] sProcessArray = new SRewriteProcess[records.length+1];
		
		// '��'�� ������ ��� ����� ù��° ������ ����Ѵ�.
		sProcessArray[0] = new SRewriteProcess();
		sProcessArray[0].setPosition(1);
		sProcessArray[0].setRewriterId(Session.get().getUser().getId());	// ù��° ��ġ�� ����
		sProcessArray[0].setComment(approvePanel.getComment().getValue());
		
		// ��� ����� ���
		for(int i=1 ; i < records.length+1 ; i++){
			sProcessArray[i] = new SRewriteProcess();
			sProcessArray[i].setPosition(i+1);	// Position �ڵ� ��� ++1
			sProcessArray[i].setRewriterId(records[i-1].getAttributeAsString("userId"));
		}
		// ������ ��� ����� ����
		rewrite.setsProcess(sProcessArray);
	}
	
	private void makeRewrite(SRewrite rewrite){
		setRewriteOptions(rewrite);
		
		ServiceUtil.rewrite().approval(Session.get().getSid(), rewrite, new AsyncCallback<Void>() {
			@Override
			public void onSuccess(Void result) {
				Waiting.hide();
				Log.infoWithPopup(I18N.message("info"), I18N.message("rewriterequestsuccess"));
				destroy();
				// ���õ� ���ڵ常 refresh
				DocumentsPanel.get().refreshSelectedRecords();
			}
			
			@Override
			public void onFailure(Throwable caught) {
				Waiting.hide();
				Log.serverError(caught, true);
			}
		});		
	}

}