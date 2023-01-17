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
	// 승인 요청 패널
	private ApproveRequestPanel approvePanel;
	// 문서 등록 패널
	private DocumentUploadPanel uploadPanel;
	// 등록 버튼 패널
	private HLayout bottomLayout;
	// 등록 버튼
	private ButtonItem approveButton;
	
	private SDocument document;
	
	public ApproveRequestDialog(){
		init();
	}
	
	/**
	 * 팝업창을 초기화 한다.
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
	 * 승인 Panel, 속성 Panel, 추가 속성 Panel을 초기화한다.
	 * 새로운 등록일 경우 모든 Panel을 초기화한다.
	 * @param isNew
	 */
	private void initPanels(){
		// Panel 설정
		approvePanel = new ApproveRequestPanel(document, this, draftRight);
		uploadPanel = new DocumentUploadPanel(null, ApproveRequestDialog.this, true);
	}

	/**
	 * Tab을 초기화한다
	 * 새로운 등록의 경우 등록 Tab 추가
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
		
		// UploadPanel의 Tab의 첫번재에 기안 Tab을 추가한다.
		uploadPanel.tabSet.addTab(approveTab, 0);
		// Tab을 직접 컨르롤하기 위해 선언
		
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
		
		// 첫번째 Tab선택
		uploadPanel.tabSet.setSelectedTab(0);
//		uploadPanel.setOverflow(Overflow.SCROLL);
		// 선택된 문서가 없을경우 등록만 실행되야함.
		if(document!=null)	controlTabByDraftType(false);
		else controlTabByDraftType(true);
		
		addItem(uploadPanel);
	}
	
	/**
	 * 승인 요청 팝업창을 띄운다.
	 * Document 선택시 승인 요청 Panel만 존재하며, Document가 없을 경우 등록만 실행한다.
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
	 * Draft Type <등록>을 선택할 경우 등록 Tab을 보여준다.
	 * @param isRegistration
	 */
	public void controlTabByDraftType(boolean isRegistration){
		if(isRegistration){
			// dialog 크기 확장
			setWidth(800);
			// 등록 Tab 활성화
			uploadPanel.tabSet.enableTab(1);
			uploadPanel.tabSet.enableTab(2);
			// 문서 등록Tab으로 자동 이동.
			uploadPanel.tabSet.selectTab(1);
			// 첫번째 문서형식에 해당하는 값 읽어오기.
			if(document != null)
				uploadPanel.getPropertiesPanel().onDocTypeChanged();
			// 기안 버튼 숨김처리(문서 업로드시 나타남)
//			if(bottomLayout != null)
//				bottomLayout.hide();
		}
		else{
			// dialog 크기 축소
			setWidth(555);
			// 등록 Tab 비활성화
			uploadPanel.tabSet.disableTab(1);
			uploadPanel.tabSet.disableTab(2);
			// 기안버튼 Layout show
//			if(bottomLayout != null)
//				bottomLayout.show();	
		}
		uploadPanel.controlUploader(isRegistration);
	}
	
	/**
	 * 기안 버튼 Layout 세팅
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
		// 기본은 Hide처리
		bottomLayout.addMember(bottomForm);
		addItem(bottomLayout);
		
		approveButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				SRewrite rewrite = new SRewrite();
				
				// 등록 기안이 아닐경우 (등록 Tab의 Disable 여부를 통해 구분)
				if(uploadPanel.tabSet.getTab(1).getDisabled()){
					rewrite.setTargetId(document.getId());	//기안 DocID 지정
					if(approvePanel.validation()){
						makeRewrite(rewrite);
					}
				}
				// 등록 기안일 경우
				else{
					if(uploadPanel.validate() && approvePanel.validation()){
						makeDocAndRewrite(rewrite);
					}
				}
			}
		});
	}
	
	/**
	 * 등록(생성) 기안 요청
	 * 1. 문서 Type 2로 지정
	 * 2. 문서 등록
	 * 3. 기안 요청(Doc Id넘김)
	 * @param rewrite
	 */
	private void makeDocAndRewrite(final SRewrite rewrite){
		setRewriteOptions(rewrite);
		// Document 세팅
		final SDocument document = uploadPanel.getDocument();
		// 문서 생성시 Type을 '2'로 지정해야 생성 기안 요청이 처리된다.
		document.setType(2);
		// 문서 등록 요청
		if(Util.getSetting("setting.waiting")) Waiting.show(I18N.message("wattingforserver"));
		ServiceUtil.document().addDocuments(Session.get().getSid(), document, new DefaultAsyncCallbackWithStatus<SDocument>(I18N.message("createdocuments")){
			@Override
			public void onSuccessEvent(final SDocument doc) {
				// 기안 대상 Doc Id 설정
				rewrite.setTargetId(doc.getId());
				makeRewrite(rewrite);
				// 선택된 레코드만 refresh
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
	 * 기안 등록 요청
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
				// 선택된 레코드만 refresh
				DocumentsPanel.get().refreshSelectedRecords();
				
				// 20140219, junsoo, 한번 오류후 파일이 없다는 오류 때문에 모든 기안이 종료된 후 클리어 시킴.
				if (Constants.DRAFT_TYPE_REGISTRATION == rewrite.getCommand()) {
					// 서버 임시 저장소 클린
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
	 * 서버 전송용 데이터 생성
	 * @param rewrite
	 */
	private void setRewriteOptions(SRewrite rewrite){
		// 등록이 아닐경우 선택된 문서의 docTypeId를 가져옴
		if(uploadPanel.tabSet.getTab(1).getDisabled())
			rewrite.setTargetDocTypeId(document.getDocType());
		// 등록의 경우 등록창의 docTypeId를 가져옴
		else
			rewrite.setTargetDocTypeId(uploadPanel.getDocument().getDocType());
		
		// Draft Type 설정
		rewrite.setCommand(Integer.parseInt(approvePanel.getDraftType().getValueAsString()));	
//		rewrite.setCommand(Integer.valueOf(approvePanel.getDraftType().getValueAsString()));	// 등록(0), 삭제(1), checkOut(5), checkIn(6)
		
		// 기안 대상자 세팅
		ListGrid grid = approvePanel.getApproveUserListGrid();
		ListGridRecord[] records = grid.getRecords();
		
		// 기안의 최초 대상은 '나'이므로 1개 추가
		SRewriteProcess[] sProcessArray = new SRewriteProcess[records.length+1];
		
		// '나'의 정보를 기안 대상의 첫번째 정보에 등록한다.
		sProcessArray[0] = new SRewriteProcess();
		sProcessArray[0].setPosition(1);
		sProcessArray[0].setRewriterId(Session.get().getUser().getId());	// 첫번째 위치는 본인
		sProcessArray[0].setComment(approvePanel.getComment().getValue());
		
		// 기안 대상자 등록
		for(int i=1 ; i < records.length+1 ; i++){
			sProcessArray[i] = new SRewriteProcess();
			sProcessArray[i].setPosition(i+1);	// Position 자동 등록 ++1
			sProcessArray[i].setRewriterId(records[i-1].getAttributeAsString("userId"));
		}
		// 전송할 기안 대상자 세팅
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
