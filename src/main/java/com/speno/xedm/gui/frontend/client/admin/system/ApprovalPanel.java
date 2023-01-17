package com.speno.xedm.gui.frontend.client.admin.system;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.DateDisplayFormat;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.types.SortDirection;
import com.smartgwt.client.widgets.events.RightMouseDownEvent;
import com.smartgwt.client.widgets.events.RightMouseDownHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.DateItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ClickEvent;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.RecordClickEvent;
import com.smartgwt.client.widgets.grid.events.RecordClickHandler;
import com.smartgwt.client.widgets.grid.events.RecordDoubleClickEvent;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.tab.TabSet;
import com.speno.xedm.core.service.serials.SDocument;
import com.speno.xedm.core.service.serials.SRecordItem;
import com.speno.xedm.core.service.serials.SRewrite;
import com.speno.xedm.core.service.serials.SRewriteProcess;
import com.speno.xedm.core.service.serials.SRewriteSearchOption;
import com.speno.xedm.gui.common.client.Constants;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.PagingObserver;
import com.speno.xedm.gui.common.client.Session;
import com.speno.xedm.gui.common.client.log.Log;
import com.speno.xedm.gui.common.client.util.AsyncCallbackWithStatus;
import com.speno.xedm.gui.common.client.util.SCM;
import com.speno.xedm.gui.common.client.util.SearchUtil;
import com.speno.xedm.gui.common.client.util.ServiceUtil;
import com.speno.xedm.gui.common.client.util.Util;
import com.speno.xedm.gui.common.client.window.Waiting;
import com.speno.xedm.gui.frontend.client.PagingToolStrip;
import com.speno.xedm.gui.frontend.client.document.DocumentAction;
import com.speno.xedm.gui.frontend.client.document.DocumentActionUtil;
import com.speno.xedm.gui.frontend.client.document.DocumentsPanel;
import com.speno.xedm.gui.frontend.client.document.popup.ApprovalDialog;
import com.speno.xedm.gui.frontend.client.document.popup.DocumentPropertiesDialog;
import com.speno.xedm.gui.frontend.client.document.popup.ReDraftDialog;
import com.speno.xedm.gui.frontend.client.panels.MainPanel;
import com.speno.xedm.gwt.service.RewriteService;
import com.speno.xedm.gwt.service.RewriteServiceAsync;
import com.speno.xedm.util.paging.PagingConfig;
import com.speno.xedm.util.paging.PagingResult;

/**
 *  Approval Panel 
 *  
 * @author goodbong
 */
public class ApprovalPanel extends VLayout implements PagingObserver{
	
	protected RewriteServiceAsync rewriteService = (RewriteServiceAsync) GWT.create(RewriteService.class);	
	private static ApprovalPanel instance = null;
//	private VLayout toolbarLayout;
	private ListGrid grid;
	
	// 게시판 구분
	private static String threadType;		// ToApproval, Request, Completed, All
	// 페이징 변수
	private PagingToolStrip gridPager;
	private int offset = 1; 
	private int limit;
	// 코드값
	private LinkedHashMap<Integer, String> codeMapCommand;
	private LinkedHashMap<Integer, String> codeMapStatus;
	// 팝업관련
	private int selectedRewriteId = 0;
	private int selectedTargetDocId = 0;
	
	private VLayout searchLayout;
	// 검색
	private TextItem descriptionText;
	private SelectItem commandSelect;
	private DateItem createDateFrom;
	private DateItem createDateTo;
	private ButtonItem searchButton;
	private SelectItem statusSelect;
	
	private boolean isSearch= false;
	
	
	public static ApprovalPanel get() {
		if (instance == null) {
			instance = new ApprovalPanel();
		}
		return instance;
	}
	
	public ApprovalPanel() { 
		this.limit = Session.get().getUser().getPageSize();
		setWidth100();
		setMembersMargin(10);
		setPadding(Constants.PADDING_DEFAULT);
		
		// code Map setting
		initItemOpts();
		
		// 1.top - title, toolbar
//		toolbarLayout = new VLayout();
//		toolbarLayout.setWidth100();
//		toolbarLayout.setAutoHeight();
		initToolBar();
//		toolbarLayout.addMember(initToolBar());
		
		// 2. search
		searchLayout = new VLayout();
		searchLayout.setWidth100();
		searchLayout.setAutoHeight();
		initSearchForm();
		
		// 3.grid
		grid = new ListGrid();
		initGrid();
        grid.draw();
        
        // 4.paging
        gridPager = new PagingToolStrip(grid, limit, true, ApprovalPanel.this);
        limit = gridPager.getPageSize();
        gridPager.setDeselect(false);
        gridPager.setIsAutoHeightGrid(false);
        gridPager.setMaxPageSize(200);
        
        // Layout 세팅
//        addMember(toolbarLayout);
        addMember(searchLayout);
        addMember(grid);
        addMember(gridPager);
        // 20130828 taesu, 초기 조회 제거( menu 선택시 데이터 가져옴 )
        // 초기 조회
//        executeList();
	}

	private void initSearchForm(){
		// 설명
		descriptionText = new TextItem("description", I18N.message("description"));
		descriptionText.setCanEdit(true);
		descriptionText.setDisableIconsOnReadOnly(false);
		descriptionText.setWrapTitle(false);
		descriptionText.addKeyUpHandler(new com.smartgwt.client.widgets.form.fields.events.KeyUpHandler() {
			@Override
			public void onKeyUp(com.smartgwt.client.widgets.form.fields.events.KeyUpEvent event) {
				if(event.getKeyName().equals("Enter")){
					offset = 1;
					executeSearch();
				}
			}
		});
		// 명령
		commandSelect = new SelectItem("command", I18N.message("command"));
		commandSelect.setWidth(100);
		commandSelect.setValueMap(codeMapCommand);
		commandSelect.setAlign(Alignment.LEFT);
		commandSelect.setDefaultToFirstOption(true);
		
		// 기안일자
		final SelectItem createDateCombo = new SelectItem("approvalDate", I18N.message("approvalDate"));
		createDateFrom = new DateItem();
		createDateTo = new DateItem();
		SearchUtil.dateItemSetting(createDateFrom);
		SearchUtil.dateItemSetting(createDateTo);
		createDateFrom.setAlign(Alignment.LEFT);
		createDateTo.setAlign(Alignment.LEFT);
		SearchUtil.setDateComboData(createDateCombo, createDateFrom, createDateTo, false);

		// - 날짜 구분(From - To)선 20130905, taesu
		StaticTextItem commonDateColumn = new StaticTextItem();
		commonDateColumn.setValue("-");
		commonDateColumn.setWidth(10);
		commonDateColumn.setAlign(Alignment.CENTER);
		commonDateColumn.setShowTitle(false);
		commonDateColumn.setStartRow(false);
		commonDateColumn.setEndRow(false);
		
		// 상태
		statusSelect = new SelectItem("status", I18N.message("status"));
		statusSelect.setWidth(120);
		statusSelect.setValueMap(codeMapStatus);
		statusSelect.setAlign(Alignment.LEFT);
		statusSelect.setDefaultToFirstOption(true);
		
		
		// 검색 버튼
		searchButton = new ButtonItem("search", I18N.message("search"));
		searchButton.setStartRow(false);
		searchButton.setEndRow(true);
		searchButton.setAlign(Alignment.RIGHT);
		searchButton.setWidth(70);
		
		searchButton.addClickHandler(new com.smartgwt.client.widgets.form.fields.events.ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				// 페이지 초기화
				offset = 1;
				executeSearch();
			}
		});
		
		// reset 버튼
		ButtonItem resetButton = new ButtonItem("reset", I18N.message("reset"));
		resetButton.setStartRow(false);
		resetButton.setEndRow(true);
		resetButton.setAlign(Alignment.RIGHT);
		resetButton.setWidth(70);
		
		resetButton.addClickHandler(new com.smartgwt.client.widgets.form.fields.events.ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				descriptionText.setValue("");
				createDateCombo.clearValue();
				statusSelect.clearValue();
				commandSelect.clearValue();
				createDateFrom.clearValue();
				createDateTo.clearValue();
			}
		});
		
		// 폼 설정
		DynamicForm searchForm = new DynamicForm();
		searchForm.setHeight(60);
		searchForm.setWidth(400);
		searchForm.setNumCols(9);
		searchForm.setAlign(Alignment.LEFT);
		
		searchForm.setItems(
				descriptionText,commandSelect,				 										searchButton,
				statusSelect,	createDateCombo, createDateFrom, commonDateColumn, createDateTo,	resetButton
				 );
		commandSelect.setColSpan(4);

		searchLayout.addMember(searchForm);
		searchLayout.hide();
	}
	
	// toolbar 세팅
	private void initToolBar() {
		// detail
		DocumentActionUtil.get().createAction("approve_detail", "second.approve_detail", "approval_act", true, new DocumentAction() {
			@Override
			protected void doAction(Object[] params) {
				onDetailViewClick();
			}
		}, DocumentActionUtil.TYPE_APPROVE_STANDBY, DocumentActionUtil.TYPE_APPROVE_REQUEST, DocumentActionUtil.TYPE_APPROVE_COMPLETE);
	
		// property
		DocumentActionUtil.get().createAction("approve_properties", "properties", "cog_go", true, new DocumentAction() {
			@Override
			protected void doAction(Object[] params) {
				onPropertiesViewClick();
			}
		}, DocumentActionUtil.TYPE_APPROVE_STANDBY, DocumentActionUtil.TYPE_APPROVE_REQUEST, DocumentActionUtil.TYPE_APPROVE_COMPLETE);		

//		if (Session.get().isDevel()) {
			// redraft
			DocumentActionUtil.get().createAction("approve_redraft", "redraft", "approval_act", true, new DocumentAction() {
				@Override
				protected void doAction(Object[] params) {
					onReDraft();
				}
			}, DocumentActionUtil.TYPE_APPROVE_COMPLETE);
//		}

		// goTo
		DocumentActionUtil.get().createAction("approve_goTo", "client.goto", "actions_goto", true, new DocumentAction() {
			@Override
			protected void doAction(Object[] params) {
				goTo();
			}
		}, DocumentActionUtil.TYPE_APPROVE_STANDBY, DocumentActionUtil.TYPE_APPROVE_REQUEST, DocumentActionUtil.TYPE_APPROVE_COMPLETE);		
		
		// Download
		DocumentActionUtil.get().createAction("approve_download", "download", "actions_download", true, new DocumentAction() {
			@Override
			protected void doAction(Object[] params) {
				download();
			}
		}, DocumentActionUtil.TYPE_APPROVE_STANDBY, DocumentActionUtil.TYPE_APPROVE_REQUEST, DocumentActionUtil.TYPE_APPROVE_COMPLETE);		
		
		// search
		DocumentActionUtil.get().createAction("filter", "filter", "filter", false, new DocumentAction() {
			@Override
			protected void doAction(Object[] params) {
				onFilterClick();
			}
		}, DocumentActionUtil.TYPE_APPROVE_STANDBY, DocumentActionUtil.TYPE_APPROVE_REQUEST, DocumentActionUtil.TYPE_APPROVE_COMPLETE);
		
		// reload
		DocumentActionUtil.get().createAction("approve_reload", "reload", "reload", true, new DocumentAction() {
			@Override
			protected void doAction(Object[] params) {
				refresh();
			}
		}, DocumentActionUtil.TYPE_APPROVE_STANDBY, DocumentActionUtil.TYPE_APPROVE_REQUEST, DocumentActionUtil.TYPE_APPROVE_COMPLETE);
		
		// retore
//		DocumentActionUtil.get().createAction("restore", "data_into", new DocumentAction() {
//			@Override
//			protected void doAction(Object[] params) {
//			}
//		}, DocumentActionUtil.TYPE_APPROVE_COMPLETE );
	}
	
	// grid 세팅
	private void initGrid() {
		// grid 속성 정의
		grid.setWidth100();
		grid.setHeight100();		
		grid.setShowAllRecords(true);
		grid.setBodyOverflow(Overflow.SCROLL);
		grid.setEmptyMessage(I18N.message("notitemstoshow"));		
		grid.setCanFreezeFields(true);
		grid.setCanRemoveRecords(false);
		grid.setSelectionType(SelectionStyle.SINGLE);
		grid.invalidateCache();		
		grid.setShowFilterEditor(false);
		
		// - 결재번호 : number(19)
		ListGridField idField = new ListGridField("id", I18N.message("approvalno"));
		idField.setWidth(70);
		idField.setAlign(Alignment.CENTER);
		// - 명령
		ListGridField commandField = new ListGridField("command", I18N.message("command"));
		commandField.setWidth(100);
		commandField.setAlign(Alignment.CENTER);
		// - 기안자 : number(19)
		ListGridField authorIdField = new ListGridField("author", I18N.message("author"));
		authorIdField.setWidth(70);
		authorIdField.setAlign(Alignment.CENTER);
		// - 내용 : varchar2(1000)
		ListGridField descriptionField = new ListGridField("description", I18N.message("description"));
		descriptionField.setAlign(Alignment.LEFT);
		// - 기안일 : timestamp(6)
		ListGridField createDateField = new ListGridField("createDate", I18N.message("approvalDate"));
		createDateField.setWidth(110);
		createDateField.setType(ListGridFieldType.DATE);
		createDateField.setDateFormatter(DateDisplayFormat.TOSERIALIZEABLEDATE);
		createDateField.setAlign(Alignment.CENTER);
		// - 완료일 : timestamp(6)
		ListGridField completeDateField = new ListGridField("completeDate", I18N.message("completedate"));
		completeDateField.setWidth(110);
		completeDateField.setType(ListGridFieldType.DATE);
		completeDateField.setDateFormatter(DateDisplayFormat.TOSERIALIZEABLEDATE);
		completeDateField.setAlign(Alignment.CENTER);
		
		// - 만료일 : timestamp(6)
		ListGridField expiredDateField = new ListGridField("expiredDate", I18N.message("s.expiredate"));					
		expiredDateField.setType(ListGridFieldType.DATE);
		expiredDateField.setDateFormatter(DateDisplayFormat.TOSERIALIZEABLEDATE);
		expiredDateField.setAlign(Alignment.CENTER);
		
		// - 결재 상태값 - integer
		ListGridField statusField = new ListGridField("status", I18N.message("status"));
		statusField.setWidth(110);
		statusField.setAlign(Alignment.CENTER);		
		// - 결재라인 - string
		ListGridField approvalLineField = new ListGridField("approvalLine", I18N.message("approvalLine"));
		approvalLineField.setWidth(150);
		approvalLineField.setAlign(Alignment.CENTER);		
		// - 관련 문서 ID : number(19)
		ListGridField targetIdField = new ListGridField("targetId", I18N.message("targetid"));
		targetIdField.setHidden(true);
		
		List<ListGridField> fields = new ArrayList<ListGridField>();
		fields.add(idField);
		fields.add(commandField);
		fields.add(authorIdField);
		fields.add(descriptionField);
		fields.add(createDateField);
		fields.add(completeDateField);
		fields.add(expiredDateField);
		fields.add(statusField);
		fields.add(approvalLineField);
		fields.add(targetIdField);
		grid.setFields(fields.toArray(new ListGridField[0]));
		grid.sort("createDate", SortDirection.DESCENDING);
		
		// record One Click 이벤트 정의
		grid.addRecordClickHandler(new RecordClickHandler() {   
            public void onRecordClick(RecordClickEvent event) {
            	selectedRewriteId = Integer.parseInt(event.getRecord().getAttribute("id"));
            	selectedTargetDocId = Integer.parseInt(event.getRecord().getAttribute("targetId"));
            	SRecordItem[] items = new SRecordItem[]{new SRecordItem((SRewrite)grid.getSelectedRecord().getAttributeAsObject("rewrite"))};
            	Session.get().selectDocuments(items);
            }   
        });		

		grid.addRightMouseDownHandler(new RightMouseDownHandler() {
			@Override
			public void onRightMouseDown(RightMouseDownEvent event) {
				selectedRewriteId = Integer.parseInt(grid.getSelectedRecord().getAttribute("id"));
				selectedTargetDocId = Integer.parseInt(grid.getSelectedRecord().getAttribute("targetId"));
				SRecordItem[] items = new SRecordItem[]{new SRecordItem((SRewrite)grid.getSelectedRecord().getAttributeAsObject("rewrite"))};
            	Session.get().selectDocuments(items);
				setupContextMenu();
			}
		});
		
		// record Double Click 이벤트 정의
		grid.addRecordDoubleClickHandler(new com.smartgwt.client.widgets.grid.events.RecordDoubleClickHandler() {   
			public void onRecordDoubleClick(RecordDoubleClickEvent event) {
            	selectedRewriteId = Integer.parseInt(event.getRecord().getAttribute("id"));
            	selectedTargetDocId = Integer.parseInt(event.getRecord().getAttribute("targetId"));
            	onDetailViewClick();
            }   
        });		
	}
	
	private void setupContextMenu(){
		Menu menu = DocumentActionUtil.get().getContextMenu();
		setContextMenu(menu);
	}
	
	// popup 실행 후 reload
	public void reload() {
		offset = 1;
		executeList();
	}
	
	// UI refresh
	public void refresh(){
		refresh(null);
	}
	public void refresh(String threadType) {
		offset = 1;
		if(threadType!=null) this.threadType = threadType;
		isSearch = false;
		clearItem();
		executeList();
	}
	
	/**
	 * 보여지는 위치에따라서 보여지는 필드를 조정한다.
	 */
	private void controlFields(){
		if(threadType.equals("ToApproval") || threadType.equals("Request")){
			grid.showField("createDate");
			grid.hideField("completeDate");
			grid.showField("expiredDate");
		}else if(threadType.equals("Completed")){
			grid.showField("createDate");
			grid.showField("completeDate");
			grid.hideField("expiredDate");
		} 
	}
	
	/**
	 * 보여지는 위치에따라서 Search Items의 값을 조정한다.
	 */
	private void controlSearchItems(){
		if(threadType.equals("ToApproval") || threadType.equals("Request")){
			statusSelect.setValue(I18N.message("progress"));
			statusSelect.disable();
		}else if(threadType.equals("Completed")){
			statusSelect.clearValue();
			statusSelect.enable();
		} 
	}
	
	// List 조회
	private void executeList() {
		controlFields();
		controlSearchItems();
		 // threadType : ToApproval, Request, Completed, All
		PagingConfig config = PagingToolStrip.getPagingConfig(offset, limit);
		
		if(Util.getSetting("setting.waiting")) Waiting.show(I18N.message("wattingforserver"));
    	
    	rewriteService.pagingRewrite(Session.get().getSid(), config, threadType, new AsyncCallbackWithStatus<PagingResult<SRewrite>>() {
			@Override
			public String getSuccessMessage() {
				Waiting.hide();
				return I18N.message("s.searchingsuccess");
			}
			@Override
			public String getProcessMessage() {
				Waiting.hide();
				return I18N.message("s.nowsearching");
			}
			@Override
			public void onSuccessEvent(PagingResult<SRewrite> result) {
				List<SRewrite> sRewrite = result.getData();
				// grid에 데이터 세팅
				setGridData(sRewrite, result.getTotalLength(), offset);
				Waiting.hide();
			}
			@Override
			public void onFailureEvent(Throwable caught) {
				Waiting.hide();
				Log.serverError(caught, false);
			}
		});
	}	
	
	// 검색 List 조회
	private void executeSearch(){
		isSearch = true;
		PagingConfig config = PagingToolStrip.getPagingConfig(offset, limit);
		SRewriteSearchOption opt = new SRewriteSearchOption();
		
		opt.setDescription(descriptionText.getValueAsString());
		try {
			opt.setStatus(Integer.parseInt(statusSelect.getValueAsString())-1);
		} catch (Exception e) {
			opt.setStatus(Constants.REWRITE_STATUS_PROGRESS);
		}
		opt.setCommand(Integer.parseInt(commandSelect.getValueAsString())-1);
		opt.setCreateDateFrom(SearchUtil.setSearchDate(createDateFrom.getValueAsDate()));
		opt.setCreateDateTo(SearchUtil.setSearchDate(createDateTo.getValueAsDate()));
		
		if(Util.getSetting("setting.waiting")) Waiting.show(I18N.message("wattingforserver"));

		ServiceUtil.rewrite().pagingRewriteByOptions(Session.get().getSid(), opt, config, threadType, new AsyncCallbackWithStatus<PagingResult<SRewrite>>() {
			@Override
			public String getSuccessMessage() {
				Waiting.hide();
				return I18N.message("s.searchingsuccess");
			}
			@Override
			public String getProcessMessage() {
				Waiting.hide();
				return I18N.message("s.nowsearching");
			}
			@Override
			public void onSuccessEvent(PagingResult<SRewrite> result) {
				List<SRewrite> sRewrite = result.getData();
				// grid에 데이터 세팅
				setGridData(sRewrite, result.getTotalLength(), offset);
				Waiting.hide();
			}
			@Override
			public void onFailureEvent(Throwable caught) {
				Waiting.hide();
				Log.serverError(caught, false);
			}
		});
	}
	// grid에 데이터 세팅
	public void setGridData(List<SRewrite> data, long totalLength, int pageNum){
		ListGridRecord records[] = new ListGridRecord[data.size()];
		String approvalLine = "";
		
		if(data.size()>0) {
			for (int i = 0; i < data.size(); i++) {
				SRewrite sRewrite = data.get(i);
				ListGridRecord record = new ListGridRecord();
				record.setAttribute("id", String.valueOf(sRewrite.getId()));
				record.setAttribute("command", codeMapCommand.get(sRewrite.getCommand()+1));
				record.setAttribute("author", sRewrite.getAuthor());
				record.setAttribute("description", Util.getStringLimitRemoveEnter(Util.removeTag(sRewrite.getDescription()), 35, "..."));
//				record.setAttribute("description", Util.removeTag(Util.getStringLimitRemoveEnter(sRewrite.getDescription(), 35, "...")));
				record.setAttribute("createDate", Util.getFormattedDate(sRewrite.getCreateDate(), true));
				record.setAttribute("completeDate", Util.getFormattedDate(sRewrite.getCompleteDate(), true));
				record.setAttribute("expiredDate", Util.getFormattedDate(sRewrite.getExpireDate(), true));	//
				record.setAttribute("status", codeMapStatus.get(sRewrite.getStatus()+1));
				record.setAttribute("statusAsInt", sRewrite.getStatus());
				record.setAttribute("currentRewriterName", String.valueOf(sRewrite.getCurrentRewriter()));
				record.setAttribute("targetId", String.valueOf(sRewrite.getTargetId()));
				record.setAttribute("downUrl", String.valueOf(sRewrite.getData()));
				record.setAttribute("rewrite", sRewrite);
				
				// 결재라인
				for(SRewriteProcess sRewriteProcess : sRewrite.getsProcess()) {
					if(sRewrite.getCurrentRewriter() == sRewriteProcess.getRewriterId()) {
						approvalLine += "["+sRewriteProcess.getRewriterName()+"]";
					} else {
						approvalLine += sRewriteProcess.getRewriterName();
					}
					if(sRewriteProcess.getPosition() == sRewrite.getsProcess().length) approvalLine += "";
					else approvalLine += " - ";
				}
				record.setAttribute("approvalLine", approvalLine);
				records[i]=record;
				approvalLine = "";
				
				if(i==0) {
					selectedRewriteId = Integer.parseInt(record.getAttribute("id"));
					selectedTargetDocId = Integer.parseInt(record.getAttribute("targetId"));
				}
			}
			// 데이터 셋팅
			grid.setData(records);
	
			// 데이터 없으면 리턴.
			if (totalLength < 1) {
				Session.get().selectDocuments(null);
				return;
			}
			gridPager.setRespPageInfo(totalLength, pageNum);
			grid.selectRecord(0);
			SRecordItem[] items = new SRecordItem[]{new SRecordItem((SRewrite)grid.getRecord(0).getAttributeAsObject("rewrite"))};
        	Session.get().selectDocuments(items);
		} else {
			grid.setData(new ListGridRecord[] {});
			// 값이 없을 경우 하단 페이징 처리 버튼 디스에이블 걸리게끔
			gridPager.setRespPageInfo(totalLength, pageNum);
		}
	}
	private ApprovalDialog approvalPopup;
	// Approval View PopUp 호출
	//20131204서버에서 권한 관련 제어
	private void onDetailViewClick() {
		if(selectedRewriteId != 0 && grid.getSelectedRecord() != null) {
			// 20131209, junsoo, 결제 상세 내용을 볼 때는 권한이 필요없으므로 제거함.
//			ServiceUtil.document().getByIdWithPermission(Session.get().getSid(), selectedTargetDocId, new AsyncCallback<SDocument>() {
//				@Override
//				public void onFailure(Throwable caught) {
//					SCM.warn(caught);
//				}
//				
//				@Override
//				public void onSuccess(final SDocument result) {
//					approvalPopup = new ApprovalDialog(Session.get().getSid(), selectedRewriteId, ApprovalPanel.this);
//					approvalPopup.show();
//				}
//			});
			approvalPopup = new ApprovalDialog(Session.get().getSid(), selectedRewriteId, ApprovalPanel.this);
			approvalPopup.show();
		}
	}
	
	// Properties PopUp 호출
	public void onPropertiesViewClick() {
		if(selectedTargetDocId != 0 && grid.getSelectedRecord() != null) {
			ServiceUtil.document().getByIdWithPermission(Session.get().getSid(), selectedTargetDocId, new AsyncCallback<SDocument>() {
				@Override
				public void onFailure(Throwable caught) {
					SCM.warn(caught);
				}
				
				@Override
				public void onSuccess(final SDocument result) {
					boolean readonly = false;
					if(		DocumentActionUtil.TYPE_APPROVE_STANDBY == DocumentActionUtil.get().getActivatedMenuType()	||
							DocumentActionUtil.TYPE_APPROVE_COMPLETE == DocumentActionUtil.get().getActivatedMenuType()	||
							DocumentActionUtil.TYPE_APPROVE_REQUEST == DocumentActionUtil.get().getActivatedMenuType()	)
						readonly = true;
					
					// properties
					final DocumentPropertiesDialog dialog = new DocumentPropertiesDialog(result, readonly);
					dialog.show();
				}
			});
		}
	}	
	
	private void onFilterClick(){
		if(searchLayout.isVisible())
			searchLayout.hide();
		else	searchLayout.show();
	}
	
	/**
	 * 해당 문서의 폴더로 이동
	 * @param docId
	 */
	private void goTo(){
		long docId = Long.parseLong(grid.getSelectedRecord().getAttribute("targetId"));
		goTo(docId);
	}
	
	private void onReDraft() {
		// TODO:
		if(selectedTargetDocId != 0 && grid.getSelectedRecord() != null) {
			ServiceUtil.document().getByIdWithPermission(Session.get().getSid(), selectedTargetDocId, new AsyncCallback<SDocument>() {
				@Override
				public void onFailure(Throwable caught) {
					SCM.warn(caught);
				}
				
				@Override
				public void onSuccess(final SDocument result) {
					SRewrite rewrite = (SRewrite)grid.getSelectedRecord().getAttributeAsObject("rewrite");
					ReDraftDialog dialog = new ReDraftDialog(result, rewrite.getCommand());
					dialog.show();
				}
			});
		}

	}
	
	public void goTo(final long docId){
		ServiceUtil.document().getById(Session.get().getSid(), docId, new AsyncCallback<SDocument>() {
			@Override
			public void onSuccess(SDocument result) {
				TabSet tabSet = MainPanel.get().getTabSet();
				// Document Tab으로 이동
				tabSet.selectTab(Constants.MAIN_TAB_DOCUMENT);
				DocumentsPanel.get().expandDocid = result.getId();
				DocumentsPanel.get().getDocumentsMenu().expandFolder(result.getFolder());
				if(approvalPopup != null)	approvalPopup.destroy();
			}
			@Override
			public void onFailure(Throwable caught) {
				Log.serverError(caught, true);
			}
		});
	}
	
	/**
	 * 해당 문서 다운로드
	 * @param url
	 */
	public void download(){
		if(grid.getSelectedRecords() != null && grid.getSelectedRecords().length >0){
			String url = ((SRewrite)grid.getSelectedRecord().getAttributeAsObject("rewrite")).getData();
			if(url != null)
				download(url);
		}
	}
	public void download(String url){
		// 20140210, junsoo, frame으로 다운로드하도록 변경
//		WindowUtils.openUrl(url);
		Util.downloadAsFrame(url);
	}
	
	// paging 처리 - PagingObserver.onPageDataReqeust()
	@Override
	public void onPageDataReqeust(int pageNum, int pageSize) {
		offset = pageNum;
		limit = pageSize;
		if(isSearch)
			executeSearch();
		else
			executeList();
	}

	//	code Map setting - 코드 관리를 Table에서 안하니깐 여기서..
	private void initItemOpts() {
		// +1이유 : select Item 값 정렬 유지를 위해 사용함.
		codeMapCommand = new LinkedHashMap<Integer, String>();
		codeMapCommand.put(0, I18N.message("s.nolimits"));
		codeMapCommand.put(Constants.REWRITE_COMMAND_REGISTRATION+1,	I18N.message("registration"));				
		codeMapCommand.put(Constants.REWRITE_COMMAND_DELETE+1, 			I18N.message("delete"));
		codeMapCommand.put(Constants.REWRITE_COMMAND_DOWNLOAD+1,	 	I18N.message("download"));		
		codeMapCommand.put(Constants.REWRITE_COMMAND_CHECKOUT+1, 		I18N.message("checkout"));
		
		codeMapStatus = new LinkedHashMap<Integer, String>();
		codeMapStatus.put(0, I18N.message("s.nolimits"));
//		codeMapStatus.put(1, I18N.message("progress"));
		codeMapStatus.put(Constants.REWRITE_STATUS_COMPLETE_APPROVAL+1,	I18N.message("completeApproval"));	
		codeMapStatus.put(Constants.REWRITE_STATUS_COMPLETE_RETURN+1, 	I18N.message("completeReturn"));
		codeMapStatus.put(Constants.REWRITE_STATUS_COMTLETE_RECOVERY+1,	I18N.message("completeRecovery"));
	}

	//필터 검색 텍스트들을 클리어한다.
	private void clearItem(){
		descriptionText.setValue("");
		searchLayout.hide();
		commandSelect.clearValue();
		createDateFrom.clearValue();
		createDateTo.clearValue();
	}
}
