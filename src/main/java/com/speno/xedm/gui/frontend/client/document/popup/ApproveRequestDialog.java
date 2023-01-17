package com.speno.xedm.gui.frontend.client.document.popup;


import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.events.ClickEvent;
import com.smartgwt.client.widgets.form.fields.events.ClickHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.events.TabSelectedEvent;
import com.smartgwt.client.widgets.tab.events.TabSelectedHandler;
import com.speno.xedm.core.service.serials.SDocument;
import com.speno.xedm.core.service.serials.SFolder;
import com.speno.xedm.core.service.serials.SRecordItem;
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
import com.speno.xedm.gui.common.client.window.Waiting;
import com.speno.xedm.gui.frontend.client.document.DocumentObserver;
import com.speno.xedm.gui.frontend.client.document.DocumentsPanel;
import com.speno.xedm.util.GeneralException;

public class ApproveRequestDialog extends Window implements DocumentObserver{
	// ���� ��û �г�
	private ApproveRequestPanel approvePanel;
	// ���� ��� �г�
	private DocumentUploadPanel uploadPanel;
	// ��� ��ư �г�
	private HLayout bottomLayout;
	// ��� ��ư
	private ButtonItem approveButton;
	
	private SDocument document;
	
	public ApproveRequestDialog(){
		init();
	}
	
	/**
	 * �˾�â�� �ʱ�ȭ �Ѵ�.
	 */
	private void init(){
		setWidth(555);
		setHeight(550);
		setTitle(I18N.message("draft"));			 
		setAutoCenter(true);
		setShowMinimizeButton(false);
		setIsModal(true);
		setShowResizeBar(true);
		setShowModalMask(true);
		setAlign(Alignment.CENTER);
	}
	
	/**
	 * ���� Panel, �Ӽ� Panel, �߰� �Ӽ� Panel�� �ʱ�ȭ�Ѵ�.
	 * ���ο� ����� ��� ��� Panel�� �ʱ�ȭ�Ѵ�.
	 * @param isNew
	 */
	private void initPanels(){
		// Panel ����
		approvePanel = new ApproveRequestPanel(document, this, draftRight);
		uploadPanel = new DocumentUploadPanel(null, ApproveRequestDialog.this, true);
	}

	/**
	 * Tab�� �ʱ�ȭ�Ѵ�
	 * ���ο� ����� ��� ��� Tab �߰�
	 * @param isNew
	 */
	private void initTab(){
		Tab approveTab= new Tab(I18N.message("draft"));
		approveTab.setID("draftTab");
		approveTab.setPane(approvePanel);
		approveTab.addTabSelectedHandler(new TabSelectedHandler() {
			@Override
			public void onTabSelected(TabSelectedEvent event) {
			}
		});
		
		// UploadPanel�� Tab�� ù���翡 ��� Tab�� �߰��Ѵ�.
		uploadPanel.tabSet.addTab(approveTab, 0);
		// Tab�� ���� �������ϱ� ���� ����
		
		uploadPanel.setCanSaveListener(new ReturnHandler<Boolean>() {
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
		uploadPanel.tabSet.setSelectedTab(0);
//		uploadPanel.setOverflow(Overflow.SCROLL);
		// ���õ� ������ ������� ��ϸ� ����Ǿ���.
		if(document!=null)	controlTabByDraftType(false);
		else controlTabByDraftType(true);
		
		addItem(uploadPanel);
	}
	
	/**
	 * ���� ��û �˾�â�� ����.
	 * Document ���ý� ���� ��û Panel�� �����ϸ�, Document�� ���� ��� ��ϸ� �����Ѵ�.
	 * @param document
	 */
	private String draftRight;
	public void show(SDocument document, String draftRight){
		this.draftRight = draftRight;
		if(document!=null){
			this.document = document;
			initPanels();
			initTab();
			initBottom(true);
		}
		else{
			initPanels();
			initTab();
			initBottom(false);
		}
		show();
	}
	
	/**
	 * Draft Type <���>�� ������ ��� ��� Tab�� �����ش�.
	 * @param isRegistration
	 */
	public void controlTabByDraftType(boolean isRegistration){
		if(isRegistration){
			// dialog ũ�� Ȯ��
			setWidth(800);
			// ��� Tab Ȱ��ȭ
			uploadPanel.tabSet.enableTab(1);
			uploadPanel.tabSet.enableTab(2);
			// ���� ���Tab���� �ڵ� �̵�.
			uploadPanel.tabSet.selectTab(1);
			// ù��° �������Ŀ� �ش��ϴ� �� �о����.
			if(document != null)
				uploadPanel.getPropertiesPanel().onDocTypeChanged();
			// ��� ��ư ����ó��(���� ���ε�� ��Ÿ��)
//			if(bottomLayout != null)
//				bottomLayout.hide();
		}
		else{
			// dialog ũ�� ���
			setWidth(555);
			// ��� Tab ��Ȱ��ȭ
			uploadPanel.tabSet.disableTab(1);
			uploadPanel.tabSet.disableTab(2);
			// ��ȹ�ư Layout show
//			if(bottomLayout != null)
//				bottomLayout.show();	
		}
		uploadPanel.controlUploader(isRegistration);
	}
	
	/**
	 * ��� ��ư Layout ����
	 * */
	private void initBottom(boolean showBottom){
		bottomLayout = new HLayout();
		bottomLayout.setHeight(20);
		bottomLayout.setWidth100();
		bottomLayout.setAlign(Alignment.RIGHT);
		
		DynamicForm bottomForm = new DynamicForm();
		bottomForm.setWidth(50);
		bottomForm.setAlign(Alignment.RIGHT);
		
		approveButton = new ButtonItem("draftApprove", I18N.message("draft"));
		bottomForm.setItems(approveButton);

		bottomLayout.setStyleName("infoPanel");
		// �⺻�� Hideó��
		bottomLayout.addMember(bottomForm);
		addItem(bottomLayout);
		
		approveButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				SRewrite rewrite = new SRewrite();
				
				// ��� ����� �ƴҰ�� (��� Tab�� Disable ���θ� ���� ����)
				if(uploadPanel.tabSet.getTab(1).getDisabled()){
					rewrite.setTargetId(document.getId());	//��� DocID ����
					if(approvePanel.validation()){
						makeRewrite(rewrite);
					}
				}
				// ��� ����� ���
				else{
					if(uploadPanel.validate() && approvePanel.validation()){
						makeDocAndRewrite(rewrite);
					}
				}
			}
		});
	}
	
	/**
	 * ���(����) ��� ��û
	 * 1. ���� Type 2�� ����
	 * 2. ���� ���
	 * 3. ��� ��û(Doc Id�ѱ�)
	 * @param rewrite
	 */
	private void makeDocAndRewrite(final SRewrite rewrite){
		setRewriteOptions(rewrite);
		// Document ����
		final SDocument document = uploadPanel.getDocument();
		// ���� ������ Type�� '2'�� �����ؾ� ���� ��� ��û�� ó���ȴ�.
		document.setType(2);
		// ���� ��� ��û
		if(Util.getSetting("setting.waiting")) Waiting.show(I18N.message("wattingforserver"));
		ServiceUtil.document().addDocuments(Session.get().getSid(), document, new DefaultAsyncCallbackWithStatus<SDocument>(I18N.message("createdocuments")){
			@Override
			public void onSuccessEvent(final SDocument doc) {
				// ��� ��� Doc Id ����
				rewrite.setTargetId(doc.getId());
				makeRewrite(rewrite);
				// ���õ� ���ڵ常 refresh
				DocumentsPanel.get().refreshSelectedRecords();
				Waiting.hide();
//				DocumentsPanel.get().onReloadRequest(document.getFolder());

				
			}
			@Override
			public void onFailureEvent(Throwable caught) {
				Log.serverError(caught, true);
			}
		});
	}
	
	/**
	 * ��� ��� ��û
	 * @param rewrite
	 */
	private void makeRewrite(final SRewrite rewrite){
		setRewriteOptions(rewrite);
		
		if(Util.getSetting("setting.waiting")) Waiting.show(I18N.message("wattingforserver"));
		ServiceUtil.rewrite().approval(Session.get().getSid(), rewrite, new AsyncCallback<Void>() {
			@Override
			public void onSuccess(Void result) {
				Waiting.hide();
				Log.infoWithPopup(I18N.message("info"), I18N.message("rewriterequestsuccess"));
				destroy();
				// ���õ� ���ڵ常 refresh
				DocumentsPanel.get().refreshSelectedRecords();
				
				// 20140219, junsoo, �ѹ� ������ ������ ���ٴ� ���� ������ ��� ����� ����� �� Ŭ���� ��Ŵ.
				if (Constants.DRAFT_TYPE_REGISTRATION == rewrite.getCommand()) {
					// ���� �ӽ� ����� Ŭ��
					ServiceUtil.document().cleanUploadedFileFolder(Session.get().getSid(), new AsyncCallback<Void>() {
						@Override
						public void onSuccess(Void result) {
							Waiting.hide();
						}
						@Override
						public void onFailure(Throwable caught) {
							Waiting.hide();
						}
					});
				}
			}
			
			@Override
			public void onFailure(Throwable caught) {
				Waiting.hide();
				GeneralException e = (GeneralException)caught;
				Log.serverError(e.getDetailMessage(), caught, true);
			}
		});		

	}
	
	/**
	 * ���� ���ۿ� ������ ����
	 * @param rewrite
	 */
	private void setRewriteOptions(SRewrite rewrite){
		// ����� �ƴҰ�� ���õ� ������ docTypeId�� ������
		if(uploadPanel.tabSet.getTab(1).getDisabled())
			rewrite.setTargetDocTypeId(document.getDocType());
		// ����� ��� ���â�� docTypeId�� ������
		else
			rewrite.setTargetDocTypeId(uploadPanel.getDocument().getDocType());
		
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

	public ApproveRequestPanel getApprovePanel() {
		return approvePanel;
	}

	public DocumentUploadPanel getUploadPanel() {
		return uploadPanel;
	}

	public void setUploadPanel(DocumentUploadPanel uploadPanel) {
		this.uploadPanel = uploadPanel;
	}

	@Override
	public void onDocumentSaved(SDocument document) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onServiceComplite(String message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDocumentSelected(SRecordItem[] items) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onReloadRequest(SFolder folder) {
		approveButton.disable();
	}
}
