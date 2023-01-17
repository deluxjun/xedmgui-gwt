package com.speno.xedm.gui.frontend.client.admin.system;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.DateDisplayFormat;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.events.RightMouseDownEvent;
import com.smartgwt.client.widgets.events.RightMouseDownHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.DateItem;
import com.smartgwt.client.widgets.form.fields.PickerIcon;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.FormItemClickHandler;
import com.smartgwt.client.widgets.form.fields.events.FormItemIconClickEvent;
import com.smartgwt.client.widgets.form.fields.events.KeyUpEvent;
import com.smartgwt.client.widgets.form.fields.events.KeyUpHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.RecordClickEvent;
import com.smartgwt.client.widgets.grid.events.RecordClickHandler;
import com.smartgwt.client.widgets.grid.events.RecordDoubleClickEvent;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.tab.TabSet;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.speno.xedm.core.service.serials.SDocument;
import com.speno.xedm.core.service.serials.SFolder;
import com.speno.xedm.core.service.serials.SRecordItem;
import com.speno.xedm.core.service.serials.SRewrite;
import com.speno.xedm.core.service.serials.SRewriteProcess;
import com.speno.xedm.core.service.serials.SRewriteSearchOption;
import com.speno.xedm.core.service.serials.SUser;
import com.speno.xedm.gui.common.client.Constants;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.PagingObserver;
import com.speno.xedm.gui.common.client.Session;
import com.speno.xedm.gui.common.client.log.Log;
import com.speno.xedm.gui.common.client.util.AsyncCallbackWithStatus;
import com.speno.xedm.gui.common.client.util.ReturnHandler;
import com.speno.xedm.gui.common.client.util.SearchUtil;
import com.speno.xedm.gui.common.client.util.ServiceUtil;
import com.speno.xedm.gui.common.client.util.Util;
import com.speno.xedm.gui.frontend.client.PagingToolStrip;
import com.speno.xedm.gui.frontend.client.admin.TrackPanel;
import com.speno.xedm.gui.frontend.client.document.DocumentAction;
import com.speno.xedm.gui.frontend.client.document.DocumentActionUtil;
import com.speno.xedm.gui.frontend.client.document.DocumentObserver;
import com.speno.xedm.gui.frontend.client.document.DocumentsPanel;
import com.speno.xedm.gui.frontend.client.document.popup.ApprovalDialog;
import com.speno.xedm.gui.frontend.client.document.popup.DocumentPropertiesDialog;
import com.speno.xedm.gui.frontend.client.folder.OwnerWindow;
import com.speno.xedm.gui.frontend.client.panels.MainPanel;
import com.speno.xedm.util.paging.PagingConfig;
import com.speno.xedm.util.paging.PagingResult;

/**
 * Approval Mangement Panel
 * 
 * @author goodbong
 */
public class ApprovalManagementPanel extends VLayout implements PagingObserver, DocumentObserver {
	private static ApprovalManagementPanel instance = null;
	private VLayout topLayout;
	private VLayout toolbarLayout;
	private VLayout searchLayout;
	private DynamicForm searchTrackForm;
	private ListGrid grid;

	// 게시판 구분
	private static String threadType; // ToApproval, Request, Completed, All
	// 페이징 변수
	private PagingToolStrip gridPager;
	private int offset = 1;
	private int limit = 10;
	// 코드값
	private LinkedHashMap<Integer, String> codeMapCommand;
	private LinkedHashMap<Integer, String> codeMapStatus;
	// 검색조건
	private TextItem authorText;
	private DateItem createDateFrom;
	private DateItem createDateTo;
	private SelectItem commandSelect;
	private SelectItem statusSelect;
	private PickerIcon userClearPicker;
	private PickerIcon userSearchPicker;
	// 팝업관련
	private int selectedRewriteId = 0;
	private int selectedTargetDocId = 0;
	private String[] authorInfo = new String[2]; // Org. Popup 창에서 넘겨받는 owner 정보
													// 저장용 변수
	private ToolStrip toolbar;

	public static ApprovalManagementPanel get(String threadType) {
		if (instance == null) {
			ApprovalManagementPanel.threadType = threadType;
			instance = new ApprovalManagementPanel();
		}
		return instance;
	}

	public ApprovalManagementPanel() {
		setWidth100();
		setMembersMargin(10);
		setPadding(Constants.PADDING_DEFAULT);

		// code Map setting
		initItemOpts();

		// 1.top - title, toolbar
		topLayout = new VLayout();
		topLayout.setWidth100();
		topLayout.setAutoHeight();
		topLayout.addMember(new TrackPanel(I18N.message("admin") + " > " + I18N.message("system") + " > " + I18N.message("approvalmanagement"), null));
		toolbarLayout = new VLayout();
		toolbarLayout.setWidth100();
		toolbarLayout.setAutoHeight();
		toolbarLayout.addMember(initToolBar());

		// 2.search
		searchLayout = new VLayout();
		searchLayout.setWidth100();
		searchLayout.setAutoHeight();
		initSearchForm();

		// 3.grid
		grid = new ListGrid();
		initGrid();
		grid.draw();

		// 4.paging
		gridPager = new PagingToolStrip(grid, Session.get().getUser().getPageSize(), true, ApprovalManagementPanel.this);
		limit = gridPager.getPageSize();
		gridPager.setDeselect(false);
		gridPager.setIsAutoHeightGrid(false);
		gridPager.setMaxPageSize(200);

		// Layout 세팅
		addMember(topLayout);
		addMember(toolbarLayout);
		addMember(searchLayout);
		addMember(grid);
		addMember(gridPager);

		// 20130905, taesu, 초기조회 제거
		// 20131127, na, 초기조회 복구
		// 초기 조회
//		if ("All".equals(threadType))
//			executeSearchList();
//		else
//			executeList();
		
		// 20131128, na 레코드 선택된 것 없게 만듬
		Session.get().selectDocuments(null);
	}

	// 20130905, taesu, 유니크Id로 변경
	// toolbar 세팅
	private ToolStrip initToolBar() {
		DocumentActionUtil.get().createAction("approveManager_detail", "second.approve_detail", "view", true, new DocumentAction() {
					@Override
					protected void doAction(Object[] params) {
						onDetailViewClick();
					}
				}, DocumentActionUtil.TYPE_APPROVE_ALL);

		// property button
		DocumentActionUtil.get().createAction("approveManager_properties", "properties", "document-properties", true, new DocumentAction() {
					@Override
					protected void doAction(Object[] params) {
						onPropertiesViewClick();
					}
				}, DocumentActionUtil.TYPE_APPROVE_ALL);
		
		// goTo
		DocumentActionUtil.get().createAction("approveManager_goTo", "client.goto", "actions_goto", true, new DocumentAction() {
			@Override
			protected void doAction(Object[] params) {
				goTo();
			}
		}, DocumentActionUtil.TYPE_APPROVE_ALL);
		
		// reload
		DocumentActionUtil.get().createAction("approveManager_reload", "reload", "reload", new DocumentAction() {
					@Override
					protected void doAction(Object[] params) {
						reload();
					}
				}, DocumentActionUtil.TYPE_APPROVE_ALL);

		toolbar = DocumentActionUtil.get().getToolbar(DocumentActionUtil.TYPE_APPROVE_ALL);
		toolbar.setHeight(20);
		toolbar.setWidth100();
		return toolbar;
	}

	// serarch form 세팅
	private void initSearchForm() {
		// Item setting
		// - 기안자
		authorText = new TextItem("author", I18N.message("author"));
		authorText.setCanEdit(true);
		authorText.setCanFocus(false);
		authorText.setDisableIconsOnReadOnly(false);
		authorText.setWrapTitle(false);
		authorText.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if("Enter".equals(event.getKeyName())){
					final ReturnHandler returnOwnerHandler = new ReturnHandler() {
						@Override
						public void onReturn(Object param) {
							String[][] ownerInfo = (String[][]) param;
							authorInfo = ownerInfo[0];
							searchTrackForm.getField("author").setValue(ownerInfo[0][1]);
							authorText.setCellStyle("ownnertext");
							authorText.updateState();
						}
					};
					OwnerWindow ownerWindow = new OwnerWindow("single", returnOwnerHandler, false, authorText.getValueAsString());
					ownerWindow.show();
				}
				else{
					authorInfo[0] = null;
					authorText.setCellStyle("");
					authorText.updateState();
				}
			}
		});
		
		// 기안자 삭제 버튼
		userClearPicker = new PickerIcon(PickerIcon.CLEAR,
				new FormItemClickHandler() {
					public void onFormItemClick(FormItemIconClickEvent event) {
						searchTrackForm.getField("author").clearValue();
						authorInfo[0] = null;
					}
				});

		// 기안자 검색
		userSearchPicker = new PickerIcon(PickerIcon.SEARCH,
				new FormItemClickHandler() {
					public void onFormItemClick(FormItemIconClickEvent event) {
						final ReturnHandler returnOwnerHandler = new ReturnHandler() {
							@Override
							public void onReturn(Object param) {
								String[][] ownerInfo = (String[][]) param;
								authorInfo = ownerInfo[0];
								searchTrackForm.getField("author").setValue(ownerInfo[0][1]);
								authorText.setCellStyle("ownnertext");
								authorText.updateState();
							}
						};
						OwnerWindow ownerWindow = new OwnerWindow("single", returnOwnerHandler, false, authorText.getValueAsString());
						ownerWindow.show();
					}
				});
		authorText.setIcons(userClearPicker, userSearchPicker);

		// - 기안일
		SelectItem createDateCombo = new SelectItem("approvalDate", I18N.message("approvalDate"));
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

		// - command
		commandSelect = new SelectItem("command", I18N.message("command"));
		commandSelect.setWidth(100);
		commandSelect.setValueMap(codeMapCommand);
		commandSelect.setAlign(Alignment.LEFT);
//		commandSelect.setValue(codeMapCommand.get(-1));
//		commandSelect.setDefaultValue(codeMapCommand.get());
		commandSelect.setDefaultToFirstOption(true);
		
		// - Status
		statusSelect = new SelectItem("status", I18N.message("status"));
		statusSelect.setWidth(120);
		statusSelect.setValueMap(codeMapStatus);
		statusSelect.setAlign(Alignment.LEFT);
//		statusSelect.setValue(codeMapStatus.get(-1));
//		statusSelect.setDefaultValue(codeMapStatus.get(-1));
		statusSelect.setDefaultToFirstOption(true);
		
		// - 검색 버튼
		ButtonItem searchButton = new ButtonItem("search", I18N.message("search"));
		searchButton.setStartRow(false);
		searchButton.setEndRow(true);
		searchButton.setAlign(Alignment.RIGHT);
		searchButton.setWidth(70);

		// - reset 버튼
		ButtonItem resetButton = new ButtonItem("reset", I18N.message("reset"));
		resetButton.setStartRow(false);
		resetButton.setEndRow(true);
		resetButton.setAlign(Alignment.RIGHT);
		resetButton.setWidth(70);

		// Form setting
		searchTrackForm = new DynamicForm();
		searchTrackForm.setHeight(60);
		searchTrackForm.setWidth(400);
		searchTrackForm.setNumCols(9);
		searchTrackForm.setAlign(Alignment.LEFT);
		
		searchTrackForm.setItems(
				authorText, 	statusSelect,		 												searchButton,
				commandSelect,	createDateCombo, createDateFrom, commonDateColumn, createDateTo,	resetButton);
		statusSelect.setColSpan(4);
		
		// searchLayout에 추가
		searchLayout.addMember(searchTrackForm);

		// List 조회 이벤트 정의
		searchButton.addClickHandler(new com.smartgwt.client.widgets.form.fields.events.ClickHandler() {
					@Override
					public void onClick(com.smartgwt.client.widgets.form.fields.events.ClickEvent event) {
						offset = 1;
						executeSearchList();
					}
				});

		// Reset 조회 이벤트 정의
		resetButton.addClickHandler(new com.smartgwt.client.widgets.form.fields.events.ClickHandler() {
					@Override
					public void onClick(com.smartgwt.client.widgets.form.fields.events.ClickEvent event) {
						resetSearchForm();
					}
				});
	}

	private void resetSearchForm() {
		authorInfo[0] = null;
		searchTrackForm.reset();
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
		ListGridField idField = new ListGridField("id",	I18N.message("approvalNo"));
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
		createDateField.setType(ListGridFieldType.DATE);
		createDateField.setDateFormatter(DateDisplayFormat.TOSERIALIZEABLEDATE);
		createDateField.setAlign(Alignment.CENTER);
		// - 완료일 : timestamp(6)
		ListGridField completeDateField = new ListGridField("completeDate",	I18N.message("completedate"));
		completeDateField.setType(ListGridFieldType.DATE);
		completeDateField.setDateFormatter(DateDisplayFormat.TOSERIALIZEABLEDATE);
		completeDateField.setAlign(Alignment.CENTER);
		// - 결재 상태값 - integer
		ListGridField statusField = new ListGridField("status",	I18N.message("status"));
		statusField.setWidth(110);
		statusField.setAlign(Alignment.CENTER);
		// - 결재라인 - string
		ListGridField approvalLineField = new ListGridField("approvalLine",	I18N.message("approvalLine"));
		approvalLineField.setWidth(150);
		approvalLineField.setAlign(Alignment.CENTER);
		// - 관련 문서 ID : number(19)
		ListGridField targetIdField = new ListGridField("targetId",	I18N.message("targetid"));
		targetIdField.setHidden(true);

		List<ListGridField> fields = new ArrayList<ListGridField>();
		fields.add(idField);
		fields.add(commandField);
		fields.add(authorIdField);
		fields.add(descriptionField);
		fields.add(createDateField);
		fields.add(completeDateField);
		fields.add(statusField);
		fields.add(approvalLineField);
		fields.add(targetIdField);
		grid.setFields(fields.toArray(new ListGridField[0]));
		
		// record One Click 이벤트 정의
		grid.addRecordClickHandler(new RecordClickHandler() {
			public void onRecordClick(RecordClickEvent event) {
				selectedRewriteId = Integer.parseInt(event.getRecord().getAttribute("id"));
				selectedTargetDocId = Integer.parseInt(event.getRecord().getAttribute("targetId"));
				// 우클릭 액션
            	SRecordItem[] items = new SRecordItem[]{new SRecordItem((SRewrite)grid.getSelectedRecord().getAttributeAsObject("rewrite"))};
            	Session.get().selectDocuments(items);
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
		
		grid.addRightMouseDownHandler(new RightMouseDownHandler() {
			@Override
			public void onRightMouseDown(RightMouseDownEvent event) {
				selectedRewriteId = Integer.parseInt(grid.getSelectedRecord().getAttribute("id"));
				selectedTargetDocId = Integer.parseInt(grid.getSelectedRecord().getAttribute("targetId"));
				Menu menu = DocumentActionUtil.get().getContextMenu(DocumentActionUtil.TYPE_APPROVE_ALL);
				SRecordItem[] items = new SRecordItem[]{new SRecordItem((SRewrite)grid.getSelectedRecord().getAttributeAsObject("rewrite"))};
				Session.get().selectDocuments(items);
				grid.setContextMenu(menu);
			}
		});
	}

	// popup 실행 후 reload
	public void reload() {
		offset = 1;
		searchTrackForm.reset();
		if ("All".equals(this.threadType)) {
			executeSearchList();
		} else {
			executeList();
		}
	}

	// List 조회
	private void executeList() {
		// threadType : ToApproval, Request, Completed, All
		PagingConfig config = PagingToolStrip.getPagingConfig(offset, limit);

		ServiceUtil.rewrite().pagingRewrite(Session.get().getSid(), config, threadType,
				new AsyncCallbackWithStatus<PagingResult<SRewrite>>() {
					@Override
					public String getSuccessMessage() {
						return I18N.message("s.searchingsuccess");
					}

					@Override
					public String getProcessMessage() {
						return I18N.message("s.nowsearching");
					}

					@Override
					public void onSuccessEvent(PagingResult<SRewrite> result) {
						List<SRewrite> sRewrite = result.getData();
						// grid에 데이터 세팅
						setGridData(sRewrite, result.getTotalLength(), offset);
					}

					@Override
					public void onFailureEvent(Throwable caught) {
						Log.serverError(caught, false);
					}
				});
	}

	// 검색조건으로 List 조회
	private void executeSearchList() {
		// 검색조건 세팅
		SRewriteSearchOption opt = new SRewriteSearchOption();
		
		if (authorInfo[0] != null){
			opt.setRewriterId(authorInfo[0]);
		}
		else if(authorText.getValue() != null){
			ServiceUtil.security().getUser(Session.get().getSid(), authorText.getValueAsString(), new AsyncCallback<SUser>() {
				@Override
				public void onSuccess(SUser result) {
					authorText.setCellStyle("ownnertext");
					authorText.updateState();
					authorInfo[0] = result.getId();
					executeSearchList();
				}
				@Override
				public void onFailure(Throwable caught) {
					SC.warn(caught.getMessage());
					authorText.setCellStyle("ownnertextout");
					authorText.updateState();
				}
			});
			return;
		}
			
		if (null != createDateFrom.getValueAsDate())
			opt.setCreateDateFrom(SearchUtil.setSearchDate(createDateFrom.getValueAsDate()));
		if (null != createDateTo.getValueAsDate())
			opt.setCreateDateTo(SearchUtil.setSearchDate(createDateTo.getValueAsDate()));
		if (null != commandSelect.getValueAsString() && !commandSelect.getValueAsString().equals("0") && !commandSelect.getValue().equals(I18N.message("s.nolimits")))
			opt.setCommand(Integer.parseInt(commandSelect.getValueAsString())-1);
		if (null != statusSelect.getValueAsString() && !statusSelect.getValueAsString().equals("0") && !statusSelect.getValue().equals(I18N.message("s.nolimits")))
			opt.setStatus(Integer.parseInt(statusSelect.getValueAsString()) -1);
		
//		if(authorInfo[0] != null) opt.setRewriterId(Long.parseLong(authorInfo[0]));
//		if(null != createDateFrom.getValueAsDate()) opt.setCreateDateFrom(SearchUtil.setSearchDate(createDateFrom.getValueAsDate()));
//		if(null != createDateTo.getValueAsDate()) opt.setCreateDateTo(SearchUtil.setSearchDate(createDateTo.getValueAsDate()));
//		if(null != commandSelect.getValueAsString() && !"0".equals(commandSelect.getValueAsString())) opt.setCommand(Integer.parseInt(commandSelect.getValueAsString()));
//		if(null != statusSelect.getValueAsString() && !"0".equals(statusSelect.getValueAsString())) opt.setStatus(Integer.parseInt(statusSelect.getValueAsString()));
		

		// Paging 세팅
		PagingConfig config = PagingToolStrip.getPagingConfig(offset, limit);
		ServiceUtil.rewrite().pagingRewriteAll(Session.get().getSid(), config, opt, new AsyncCallbackWithStatus<PagingResult<SRewrite>>() {
			@Override
			public String getSuccessMessage() {
				return I18N.message("s.searchingsuccess");
			}

			@Override
			public String getProcessMessage() {
				return I18N.message("s.nowsearching");
			}

			@Override
			public void onSuccessEvent(PagingResult<SRewrite> result) {
				List<SRewrite> sRewrite = result.getData();
				// grid에 데이터 세팅
				setGridData(sRewrite, result.getTotalLength(), offset);
			}

			@Override
			public void onFailureEvent(Throwable caught) {
				Log.serverError(caught, false);
			}
		});
	}

	// grid에 데이터 세팅
	public void setGridData(List<SRewrite> data, long totalLength, int pageNum) {
		ListGridRecord records[] = new ListGridRecord[data.size()];
		String approvalLine = "";
		String currentRewriterName = "";

		if (data.size() > 0) {
			for (int i = 0; i < data.size(); i++) {
				SRewrite sRewrite = data.get(i);
				ListGridRecord record = new ListGridRecord();
				record.setAttribute("id", String.valueOf(sRewrite.getId()));
				record.setAttribute("command",codeMapCommand.get(sRewrite.getCommand()+1));
				record.setAttribute("author", sRewrite.getAuthor());
				record.setAttribute("description", Util.getStringLimitRemoveEnter(Util.removeTag(sRewrite.getDescription()), 35, "..."));
//				record.setAttribute("description", sRewrite.getDescription());
				record.setAttribute("createDate", Util.getFormattedDate(sRewrite.getCreateDate(), false));
				record.setAttribute("completeDate", Util.getFormattedDate(sRewrite.getCompleteDate(), false));
//				record.setAttribute("status",codeMapStatus.get(sRewrite.getStatus()+1));	// '상태+1'을 해야 제대로된 값이 나온다.
				record.setAttribute("status",codeMapStatus.get(sRewrite.getStatus()+1));	// '상태+1'을 해야 제대로된 값이 나온다.
				record.setAttribute("currentRewriterName", String.valueOf(sRewrite.getCurrentRewriter()));
				record.setAttribute("targetId", String.valueOf(sRewrite.getTargetId()));
				record.setAttribute("rewrite", sRewrite);
				
				// 결재라인
				for (SRewriteProcess sRewriteProcess : sRewrite.getsProcess()) {
					if (sRewrite.getCurrentRewriter() == sRewriteProcess.getRewriterId()) {
						approvalLine += "[" + sRewriteProcess.getRewriterName() + "]";
						currentRewriterName = sRewriteProcess.getRewriterName();
					} else {
						approvalLine += sRewriteProcess.getRewriterName();
					}
					if (sRewriteProcess.getPosition() == sRewrite.getsProcess().length)
						approvalLine += "";
					else
						approvalLine += " - ";
				}
				record.setAttribute("approvalLine", approvalLine);
				records[i] = record;
				approvalLine = "";

				if (i == 0) {
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
//			grid.selectRecord(0);

		} else {
			grid.setData(new ListGridRecord[] {});
		}
	}

	// Approval View PopUp 호출
	private void onDetailViewClick() {
		if (selectedRewriteId != 0 && grid.getSelectedRecord() != null) {
			ApprovalDialog approvalPopup = new ApprovalDialog(Session.get().getSid(), selectedRewriteId, ApprovalManagementPanel.this);
			approvalPopup.show();
		}
	}

	// Properties PopUp 호출	
	private void onPropertiesViewClick() {
		if (selectedTargetDocId != 0 && grid.getSelectedRecord() != null) {
			ServiceUtil.document().getByIdWithPermission(Session.get().getSid(), selectedTargetDocId, new AsyncCallback<SDocument>() {
				@Override
				public void onFailure(Throwable caught) {
					Log.serverError(caught, false);
				}
				
				@Override
				public void onSuccess(final SDocument result) {
					final DocumentPropertiesDialog dialog = new DocumentPropertiesDialog(result, true, Constants.MAIN_TAB_ADMIN);
					dialog.show();
				}
			});
		}
	}

	// goTo
	private void goTo(){
		if(grid.getSelectedRecord() != null){
			Long docId = Long.parseLong(grid.getSelectedRecord().getAttribute("targetId"));
			ServiceUtil.document().getById(Session.get().getSid(), docId, new AsyncCallback<SDocument>() {
				@Override
				public void onSuccess(SDocument result) {
					TabSet tabSet = MainPanel.get().getTabSet();
//					// Document Tab으로 이동
					tabSet.selectTab(Constants.MAIN_TAB_DOCUMENT);
//					DocumentsPanel.get().getDocumentsMenu().expandFolder(result.getFolder());
					DocumentsPanel.get().expandDocid = result.getId();
					DocumentsPanel.get().getDocumentsMenu().expandFolder(result.getFolder());
					grid.deselectAllRecords();
				}
				@Override
				public void onFailure(Throwable caught) {
					Log.serverError(caught, false);
				}
			});
		}
	}
	
	// paging 처리 - PagingObserver.onPageDataReqeust()
	@Override
	public void onPageDataReqeust(int pageNum, int pageSize) {
		offset = pageNum;
		limit = pageSize;
		if ("All".equals(threadType))
			executeSearchList();
		else
			executeList();
	}

	// code Map setting - 코드 관리를 Table에서 안하니깐 여기서..
	// 20130905, taesu, '전체' 추가, Select Item Option은 Key값을 정렬해서 출력하므로 값을 (+1)하여 재 설정함.
	private void initItemOpts() {
		codeMapCommand = new LinkedHashMap<Integer, String>();
		codeMapCommand.put(0, I18N.message("s.nolimits"));
		codeMapCommand.put(Constants.REWRITE_COMMAND_REGISTRATION+1,	I18N.message("registration"));				
		codeMapCommand.put(Constants.REWRITE_COMMAND_DELETE+1, 			I18N.message("delete"));
		codeMapCommand.put(Constants.REWRITE_COMMAND_DOWNLOAD+1,	 	I18N.message("download"));		
		codeMapCommand.put(Constants.REWRITE_COMMAND_CHECKOUT+1, 		I18N.message("checkout"));
		
		codeMapStatus = new LinkedHashMap<Integer, String>();
		codeMapStatus.put(Constants.REWRITE_STATUS_COMPLETE + 1, I18N.message("s.nolimits")); 
		codeMapStatus.put(Constants.REWRITE_STATUS_PROGRESS + 1,			I18N.message("progress"));
		codeMapStatus.put(Constants.REWRITE_STATUS_COMTLETE_RECOVERY + 1,	I18N.message("completeRecovery"));
		codeMapStatus.put(Constants.REWRITE_STATUS_COMPLETE_RETURN + 1, 	I18N.message("completeReturn"));
		codeMapStatus.put(Constants.REWRITE_STATUS_COMPLETE_APPROVAL + 1,	I18N.message("completeApproval"));
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
		// TODO Auto-generated method stub
		
	}

}