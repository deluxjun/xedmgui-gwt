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
			// TODO: 아직 지원하지 않음.
			Log.warnWithPopup(I18N.message("notsupported"), "");
			destroy();
		}

		setTitle(I18N.message("redraft"));
		setHeight(460);
		
		// 모든 동작을 readonly로 설정
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
				
				// 등록 기안이 아닐경우 (등록 Tab의 Disable 여부를 통해 구분)
				if(command != Constants.DRAFT_TYPE_REGISTRATION){
					rewrite.setTargetId(document.getId());	//기안 DocID 지정
					if(approvePanel.validation()){
						makeRewrite(rewrite);
					}
				}
				// 등록 기안일 경우
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
		
		// UploadPanel의 Tab의 첫번재에 기안 Tab을 추가한다.
		updatePanel.tabSet.addTab(approveTab, 0);
		// Tab을 직접 컨르롤하기 위해 선언
		
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
		
		// 첫번째 Tab선택
		updatePanel.tabSet.setSelectedTab(0);
		
		if (Constants.DRAFT_TYPE_REGISTRATION == command)
			controlTabByDraftType(true);
		else
			controlTabByDraftType(false);
	}
	
	public void controlTabByDraftType(boolean isRegistration){
		if(isRegistration){
			// dialog 크기 확장
			setWidth(800);
			// 등록 Tab 활성화
			updatePanel.tabSet.enableTab(1);
			updatePanel.tabSet.enableTab(2);
			// 문서 등록Tab으로 자동 이동.
			updatePanel.tabSet.selectTab(1);
		}
		else{
			// dialog 크기 축소
			setWidth(555);
			// 등록 Tab 비활성화
			updatePanel.tabSet.disableTab(1);
			updatePanel.tabSet.disableTab(2);
			// 기안버튼 Layout show
//			if(bottomLayout != null)
//				bottomLayout.show();	
		}
		updatePanel.controlUploader(isRegistration);
	}
	

	private void setReadOnly(SDocument document) {
		SFolder folder = document.getFolder();
		folder.setPermissions(new String[]{"download", "view"});
	}
	
	// TODO: 등록의 재기안은 추후에 구현
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
						// 체크인 서비스 완료시 서버의 temp 저장소를 클린한다.
						ServiceUtil.document().cleanUploadedFileFolder(Session.get().getSid(), new AsyncCallback<Void>() {
							@Override
							public void onSuccess(Void result) {
								rewrite.setTargetId(document.getId());
								makeRewrite(rewrite);
								// 선택된 레코드만 refresh
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
	 * 서버 전송용 데이터 생성
	 * @param rewrite
	 */
	private void setRewriteOptions(SRewrite rewrite){
		// 등록이 아닐경우 선택된 문서의 docTypeId를 가져옴
		if(updatePanel.tabSet.getTab(1).getDisabled())
			rewrite.setTargetDocTypeId(document.getDocType());
		// 등록의 경우 등록창의 docTypeId를 가져옴
		else
			rewrite.setTargetDocTypeId(updatePanel.getDocument().getDocType());
		
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
	
	private void makeRewrite(SRewrite rewrite){
		setRewriteOptions(rewrite);
		
		ServiceUtil.rewrite().approval(Session.get().getSid(), rewrite, new AsyncCallback<Void>() {
			@Override
			public void onSuccess(Void result) {
				Waiting.hide();
				Log.infoWithPopup(I18N.message("info"), I18N.message("rewriterequestsuccess"));
				destroy();
				// 선택된 레코드만 refresh
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