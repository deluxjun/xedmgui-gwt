package com.speno.xedm.gui.frontend.client.document.popup;

import java.util.HashMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.RichTextEditor;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.TabSet;
import com.speno.xedm.core.service.serials.SDocument;
import com.speno.xedm.core.service.serials.SGroup;
import com.speno.xedm.core.service.serials.SRewrite;
import com.speno.xedm.core.service.serials.SRewriteProcess;
import com.speno.xedm.gui.common.client.Constants;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.Session;
import com.speno.xedm.gui.common.client.util.AsyncCallbackWithStatus;
import com.speno.xedm.gui.common.client.util.SCM;
import com.speno.xedm.gui.common.client.util.Util;
import com.speno.xedm.gui.common.client.window.Waiting;
import com.speno.xedm.gui.frontend.client.admin.system.ApprovalManagementPanel;
import com.speno.xedm.gui.frontend.client.admin.system.ApprovalPanel;
import com.speno.xedm.gui.frontend.client.document.DocumentsPanel;
import com.speno.xedm.gui.frontend.client.panels.MainPanel;
import com.speno.xedm.gwt.service.RewriteService;
import com.speno.xedm.gwt.service.RewriteServiceAsync;

/**
 * Approval Detail PopUp
 * 
 * @author goodbong
 */
public class ApprovalDialog extends Window{
	private RewriteServiceAsync service = (RewriteServiceAsync) GWT.create(RewriteService.class);
	private static HashMap<String, ApprovalDialog> instanceMap = new HashMap<String, ApprovalDialog>();	
	
	protected VLayout mainLayer;

	private long approvalId;
	private SRewrite sRewrite;
	private RichTextEditor currentDescTextEditor;
	private ApprovalPanel approvalPanel;
	private ApprovalManagementPanel approvalManagementPanel;
	private ApprovalDialog approvalDialog = this;
	
	private String threadType;
	// Admin 결재 관리창에서는 버튼이 활성화 되어야 하므로 추가됨.
	boolean isAdmin = false;
	
	public ApprovalDialog(final String id, final long approvalId, final ApprovalPanel approvalPanel) {
		instanceMap.put(id, this);
		this.approvalId = approvalId;
		this.approvalPanel = approvalPanel;
		initPopup();
	}	
	
	public ApprovalDialog(final String id, final long approvalId, final ApprovalManagementPanel approvalManagementPanel) {
		instanceMap.put(id, this);
		this.approvalId = approvalId;
		this.approvalManagementPanel = approvalManagementPanel;
		initPopup();
	}	
	
	// 팝업 윈도우 초기화
	private void initPopup() {
		
		setWidth(670);
		setHeight(410);		
		setTitle(I18N.message("viewApproval"));			 
		setAutoCenter(true);
		setShowMinimizeButton(false);
		setIsModal(true);
		setShowResizeBar(true);
		setShowModalMask(true);
		
		mainLayer = new VLayout();
		mainLayer.setHeight100();
		mainLayer.setWidth100();
		mainLayer.setMembersMargin(5);  
		executeFetch();
//		initLayer();
		addItem(mainLayer);
		show();		
	}
	
	// 결재건 조회
	private void executeFetch()	{				
		service.getRewrite(Session.get().getSid(), approvalId, new AsyncCallback<SRewrite>() {
			@Override
			public void onFailure(Throwable caught) {
				SCM.warn(caught);
			}
			@Override
			public void onSuccess(SRewrite result) {
				sRewrite = result;
				// 결재건 조회 이후 UI를 정의한다(결재라인에 따라 UI가 동적으로 구성되기 때문)
				initLayer();
			}
		});
	}
	
	// Layer 세팅
	private void initLayer() {
		// A.title Layout 선언
		HLayout titleLayout = new HLayout();
		titleLayout.setWidth100();
		titleLayout.setHeight(20);    			
		titleLayout.setAlign(Alignment.LEFT);
		// B.버튼 Layout 선언
		HLayout btnLayout = new HLayout();
		btnLayout.setWidth(630);
		btnLayout.setAutoHeight();    			
		btnLayout.setMembersMargin(5);  
		btnLayout.setAlign(Alignment.RIGHT);
		// C.결재라인별 상세정보 Layout 선언
    	VLayout rowLayout = new VLayout();
    	rowLayout.setWidth100();
    	rowLayout.setAutoHeight();
    	rowLayout.setAlign(Alignment.CENTER);
    	rowLayout.setMembersMargin(5);
    	
    	for(SGroup sGroup : Session.get().getUser().getGroups()) {
    		if(sGroup.getId().equals("1")) isAdmin = true;
    	}
    	
		int arraySeq = 0;
		boolean isDisableAfter = false;
		String approvalLine = I18N.message("approvalLine")+" : ";
		String currentRewriterName = "";
		String approver = "";
		
		if(sRewrite.getsProcess().length > 0) {
			for(SRewriteProcess sRewriteProcess : sRewrite.getsProcess()) {
				// 결재라인 Title
				if(sRewrite.getCurrentRewriter() == sRewriteProcess.getRewriterId()) {
					approvalLine += "<b>["+sRewriteProcess.getRewriterName()+"]</b>";
					currentRewriterName = sRewriteProcess.getRewriterName();
				} else {
					approvalLine += sRewriteProcess.getRewriterName();
				}
				if(sRewriteProcess.getPosition() == sRewrite.getsProcess().length) approvalLine += "";
				else approvalLine += " - ";
				
				// 승인 날짜 설정
				String date = "";
				if(sRewriteProcess.getCompleteDate() != null){
					date = sRewriteProcess.getCompleteDate().toString().split("\\.")[0];	// ms 제거
					date = date.replaceAll(" ", "<br>");									// 공백 한줄 띄기
				}
				
				// 부서 이름 설정
				if(sRewriteProcess.getRewriterDeptName() == null)
					approver = sRewriteProcess.getRewriterName() + "<br>(" + I18N.message("notspecified") + ")";
				else
					approver = sRewriteProcess.getRewriterName() + "<br>(" + sRewriteProcess.getRewriterDeptName() + ")" + "<br>" + date;
				
				// 결재라인 row
		    	HLayout colLayout = new HLayout();
		    	colLayout.setWidth100();
		    	colLayout.setAutoHeight();   
		    	colLayout.setMembersMargin(5); 
		    	
				// 결재자
		    	Label approvalLabel = new Label(approver);
		    	approvalLabel.setAlign(Alignment.CENTER);
		    	approvalLabel.setWidth(100);
		    	approvalLabel.setAutoHeight();
		    	approvalLabel.setWrap(true);
		    	approvalLabel.setShowEdges(false);
		    	
				if(isDisableAfter) approvalLabel.hide();
				colLayout.addMember(approvalLabel);  
				// C.결재라인별 상세정보  Layout 정의
				rowLayout.addMember(colLayout);

				// 현재 승인해야 할 Id가 자신일 경우 Edit가능한 Text를 보여준다.
				if((sRewrite.getStatus() == 0) 
						&& (sRewrite.getCurrentRewriter() == sRewriteProcess.getRewriterId()) 
						&& (isAdmin || (sRewriteProcess.getRewriterId() == Session.get().getUser().getId())) ){
					approvalLabel.setContents("<strong>" + approver + "</strong>");
					
					RichTextEditor descTextEditor = new RichTextEditor();  
					descTextEditor.setOverflow(Overflow.HIDDEN);  
					descTextEditor.setCanDragResize(true);  
					descTextEditor.setShowEdges(true);  
					descTextEditor.setWidth(525);
					descTextEditor.setHeight(100);
					descTextEditor.setAlign(Alignment.CENTER);
					descTextEditor.setStyleName("richTextEditor");
//					descTextEditor.setDisabled(true);
//				descTextEditor.setValue(Util.getStringLimitRemoveEnter(toStringNull(sRewriteProcess.getComment()), 30, "..."));
					descTextEditor.setValue(toStringNull(sRewriteProcess.getComment()));
					
					if(!isDisableAfter) colLayout.addMember(descTextEditor);
//					if(sRewrite.getStatus() == 0) {
//						descTextEditor.setDisabled(false);
						currentDescTextEditor = descTextEditor;
//					}
				}else{
					Canvas htmlCanvas = new Canvas();  
					htmlCanvas.setHeight(100);
					htmlCanvas.setWidth(525);
					htmlCanvas.setPadding(1);  
					htmlCanvas.setOverflow(Overflow.SCROLL);  
					htmlCanvas.setCanDragResize(false);
					htmlCanvas.setEdgeSize(3);
					htmlCanvas.setShowEdges(true);  
					htmlCanvas.setContents(toStringNull(sRewriteProcess.getComment()));
					colLayout.addMember(htmlCanvas);
				}
				arraySeq++;
			}
		}
			
		// A.title Layout 세팅
    	Label approvalLineLabel = new Label();
    	approvalLineLabel.setAlign(Alignment.CENTER);
    	approvalLineLabel.setWidth100();
    	approvalLineLabel.setHeight(18);
    	approvalLineLabel.setShowEdges(false);
    	approvalLineLabel.setContents(approvalLine);
    	approvalLineLabel.setBorder("1px solid #6a6a6a");  
    	approvalLineLabel.setPadding(10);
    	titleLayout.addMember(approvalLineLabel);
		
    	
		// B.버튼 Layout 정의
    	// 진행중
    	if(sRewrite.getStatus() == Constants.REWRITE_STATUS_PROGRESS){
    		if(isAdmin || sRewrite.getCurrentRewriter() == Session.get().getUser().getId()) {
    			IButton approvalBtnItem = new IButton();
    			approvalBtnItem.setWidth(100);
    			approvalBtnItem.setAlign(Alignment.CENTER);
    			approvalBtnItem.setTitle(I18N.message("approvalAccept"));		 
    			approvalBtnItem.addClickHandler(new com.smartgwt.client.widgets.events.ClickHandler(){
    				@Override
    				public void onClick(com.smartgwt.client.widgets.events.ClickEvent event) {
    					if(isValidate()) execute("approvalItem");
    				}
    			});
    			btnLayout.addMember(approvalBtnItem);
    			
    			// 반려
    			IButton returnBtnItem = new IButton();
    			returnBtnItem.setAlign(Alignment.CENTER);
    			returnBtnItem.setWidth(100);
    			returnBtnItem.setTitle(I18N.message("approvalReturn"));		 
    			returnBtnItem.addClickHandler(new com.smartgwt.client.widgets.events.ClickHandler(){
    				@Override
    				public void onClick(com.smartgwt.client.widgets.events.ClickEvent event) {
    					if(isValidate()) execute("returnItem");
    				}
    			});
    			btnLayout.addMember(returnBtnItem);
    		}
    		// 회수
    		if(isAdmin || sRewrite.getAuthorId() == Session.get().getUser().getId()) {
    			IButton recoveryBtnItem = new IButton();
    			recoveryBtnItem.setAlign(Alignment.CENTER);
    			recoveryBtnItem.setWidth(100);
    			recoveryBtnItem.setTitle(I18N.message("approvalRecovery"));	
    			recoveryBtnItem.addClickHandler(new com.smartgwt.client.widgets.events.ClickHandler(){
    				@Override
    				public void onClick(com.smartgwt.client.widgets.events.ClickEvent event) {
    					execute("recoveryItem");
    				}
    			});
    			btnLayout.addMember(recoveryBtnItem);
    		}
    	}
//    	// 결재 완료건
//    	else if(sRewrite.getStatus() == Constants.REWRITE_STATUS_COMPLETE_APPROVAL)
//		{
//			// 삭제가 아닐경우
//			if(!(sRewrite.getCommand() == Constants.REWRITE_COMMAND_DELETE)){
//				if(sRewrite.getData() != null){
//					makeDownBtn(btnLayout);
//				}
//				makeMoveBtn(btnLayout);
//			}
//		}
//		// 반려 완료
//    	else if(sRewrite.getStatus() == Constants.REWRITE_STATUS_COMTLETE_RECOVERY)
//    	{
//			makeDownBtn(btnLayout);
//			makeMoveBtn(btnLayout);
//    	}
//    	// 회수 완료
//    	else if(sRewrite.getStatus() == Constants.REWRITE_STATUS_COMPLETE_RETURN)
//    	{
//			makeDownBtn(btnLayout);
//			makeMoveBtn(btnLayout);
//    	}
    	
    	// 다운로드 및 Move 버튼 추가
    	//20131129 결재가 완료 되면 다운로드 되게 설정
    	// 20131209, junsoo, 결재창에서의 다운로드 및 이동 기능 제거함.
//    	if (sRewrite.getCommand() == Constants.REWRITE_COMMAND_DOWNLOAD && sRewrite.getData() != null){
//			makeDownBtn(btnLayout);
//    	}
//    	if (!(sRewrite.getStatus() == Constants.REWRITE_STATUS_COMPLETE_APPROVAL &&
//    		sRewrite.getCommand() == Constants.REWRITE_COMMAND_DELETE)){
//    		makeMoveBtn(btnLayout);
//    	}
    	
		// 파일 다운로드 동작 추가
		// 20130912, taesu, 종료버튼 제거
//		IButton closeBtnItem = new IButton();
//		closeBtnItem.setAlign(Alignment.CENTER);
//		closeBtnItem.setTitle(I18N.message("close"));
//		closeBtnItem.setWidth(100);
//		closeBtnItem.addClickHandler(new com.smartgwt.client.widgets.events.ClickHandler(){
//			@Override
//			public void onClick(com.smartgwt.client.widgets.events.ClickEvent event) {
//				destroy();
//			}
//        });
//		btnLayout.addMember(closeBtnItem);
		
		// Layout 세팅
		mainLayer.addMember(titleLayout);
		mainLayer.addMember(btnLayout);	
		mainLayer.addMember(rowLayout);			
	}
	
//	private void makeDownBtn(HLayout layout){
//		// 다운로드
//		IButton downloadBtnItem = new IButton();
//		downloadBtnItem.setWidth(100);
//		downloadBtnItem.setAlign(Alignment.CENTER);
//		downloadBtnItem.setTitle(I18N.message("download"));		 
//		downloadBtnItem.addClickHandler(new com.smartgwt.client.widgets.events.ClickHandler(){
//			@Override
//			public void onClick(com.smartgwt.client.widgets.events.ClickEvent event) {
////				Util.downloadAsFrame(sRewrite.getTargetId(), "");
//				Util.downloadAsFrame(sRewrite.getData());
//			}
//		});
//		layout.addMember(downloadBtnItem);
//	}
//	
//	private void makeMoveBtn(HLayout layout){
//		// 이동
//		IButton moveBtnItem = new IButton();
//		moveBtnItem.setWidth(100);
//		moveBtnItem.setAlign(Alignment.CENTER);
//		moveBtnItem.setTitle(I18N.message("move"));		 
//		moveBtnItem.addClickHandler(new com.smartgwt.client.widgets.events.ClickHandler(){
//			@Override
//			public void onClick(com.smartgwt.client.widgets.events.ClickEvent event) {
//				ServiceUtil.document().getById(Session.get().getSid(), sRewrite.getTargetId(), new AsyncCallback<SDocument>() {
//					@Override
//					public void onSuccess(SDocument result) {
//						goTo(result);
//					}
//					@Override
//					public void onFailure(Throwable caught) {
//						Log.serverError(caught, false);
//					}
//				});
//			}
//		});
//		layout.addMember(moveBtnItem);
//	}
	// 결재 처리
	private void execute(String actionType) {
		if("approvalItem".equals(actionType)) {	// 승인
			if(Util.getSetting("setting.waiting")) Waiting.show(I18N.message("wattingforserver"));
			service.approvalItem(Session.get().getSid(), approvalId, currentDescTextEditor.getValue(), new AsyncCallbackWithStatus<Void>() {
				@Override
				public String getSuccessMessage() {
					Waiting.hide();
					return I18N.message("client.searchComplete");
				}
				@Override
				public String getProcessMessage() {
					Waiting.hide();
					return I18N.message("client.searchRequest");
				}
				@Override
				public void onFailureEvent(Throwable caught) {
					Waiting.hide();
					SCM.warn(caught);
				}
				@Override
				public void onSuccessEvent(Void result) {
					Waiting.hide();
					if(approvalPanel != null) approvalPanel.reload();
					if(approvalManagementPanel != null) approvalManagementPanel.reload();
	            	destroy();
				}
			});
		  
		} else if("returnItem".equals(actionType)) {	// 반려
			if(Util.getSetting("setting.waiting")) Waiting.show(I18N.message("wattingforserver"));
			service.returnItem(Session.get().getSid(), approvalId, currentDescTextEditor.getValue(), new AsyncCallbackWithStatus<Void>() {
				@Override
				public String getSuccessMessage() {
					Waiting.hide();
					return I18N.message("client.searchComplete");
				}
				@Override
				public String getProcessMessage() {
					Waiting.hide();
					return I18N.message("client.searchRequest");
				}
				@Override
				public void onFailureEvent(Throwable caught) {
					Waiting.hide();
					SCM.warn(caught);
				}
				@Override
				public void onSuccessEvent(Void result) {
					Waiting.hide();
					if(approvalPanel != null) approvalPanel.reload();
					if(approvalManagementPanel != null) approvalManagementPanel.reload();
	            	destroy();
				}
			});			
		
		} else if("recoveryItem".equals(actionType)) { // 회수
			if(Util.getSetting("setting.waiting")) Waiting.show(I18N.message("wattingforserver"));
			service.recoveryItem(Session.get().getSid(), approvalId, "", new AsyncCallbackWithStatus<Void>() {
				@Override
				public String getSuccessMessage() {
					Waiting.hide();
					return I18N.message("client.searchComplete");
				}
				@Override
				public String getProcessMessage() {
					Waiting.hide();
					return I18N.message("client.searchRequest");
				}
				@Override
				public void onFailureEvent(Throwable caught) {
					Waiting.hide();
					SCM.warn(caught);
				}
				@Override
				public void onSuccessEvent(Void result) {
					Waiting.hide();
					if(approvalPanel != null) approvalPanel.reload();
					if(approvalManagementPanel != null) approvalManagementPanel.reload();
	            	destroy();
				}
			});			
		}
	}
	
	// RichTextEditor값 필수 체크
	private boolean isValidate() {
		if(currentDescTextEditor.getValue().trim().length() == 0) {
			SC.warn(I18N.message("commentrequired"));
			currentDescTextEditor.focus();
			return false;
		} else {
			return true;
		}
	}	
	
	// RichTextEditor null값 보정처리
	private String toStringNull(String param) {
		if(param == null || param.equals("") || param.equals("null")) return " ";
		else return param;
	}	
	
	public void show(String threadType){
		this.threadType = threadType;
		this.show();
	}
	
	// 20131128문서탭으로 이동하는 메서드
	private void goTo(SDocument result){
		TabSet tabSet = MainPanel.get().getTabSet();
		// Document Tab으로 이동
		tabSet.selectTab(Constants.MAIN_TAB_DOCUMENT);
		DocumentsPanel.get().expandDocid = result.getId();
		DocumentsPanel.get().getDocumentsMenu().expandFolder(result.getFolder());
		approvalDialog.destroy();
	}
}
