
package com.speno.xedm.gui.frontend.client.admin.system.audit;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.datepicker.client.CalendarUtil;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.DateItem;
import com.smartgwt.client.widgets.form.fields.HiddenItem;
import com.smartgwt.client.widgets.form.fields.PickerIcon;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.SpacerItem;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.form.fields.events.FormItemClickHandler;
import com.smartgwt.client.widgets.form.fields.events.FormItemIconClickEvent;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.VLayout;
import com.speno.xedm.core.service.serials.SHistory;
import com.speno.xedm.core.service.serials.SHistorySearchOptions;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.PagingObserver;
import com.speno.xedm.gui.common.client.Session;
import com.speno.xedm.gui.common.client.log.Log;
import com.speno.xedm.gui.common.client.util.AsyncCallbackWithStatus;
import com.speno.xedm.gui.common.client.util.DateCellFormatter;
import com.speno.xedm.gui.common.client.util.SCM;
import com.speno.xedm.gui.common.client.util.ServiceUtil;
import com.speno.xedm.gui.frontend.client.PagingToolStrip;
import com.speno.xedm.gui.frontend.client.admin.CommSearchDialog;
import com.speno.xedm.gui.frontend.client.admin.CommSearchDialog.ResultHandler;
import com.speno.xedm.util.paging.PagingConfig;
import com.speno.xedm.util.paging.PagingResult;

/**
 * Audit Grid Panel
 * 
 * @author 박상기
 * @since 1.0
 */
public class AuditGridPanel extends VLayout implements PagingObserver {	
	private static HashMap<String, AuditGridPanel> instanceMap = new HashMap<String, AuditGridPanel>();
	
	private ListGrid grid;
	private PagingToolStrip gridPager;
	private DynamicForm searchForm;	
	private SelectItem actionItem;
	
	/**
	 * 기 생성된 instance를 반환함. instance가 없을 경우 생성 후 반환함
	 * @param id
	 * @param subTitle
	 * @return
	 */
	public static AuditGridPanel get(final String id, final String subTitle) {
		if (instanceMap.get(id) == null) {
			new AuditGridPanel(id, subTitle);
		}
		return instanceMap.get(id);
	}
	
	public AuditGridPanel(final String id, final String subTitle) {
		instanceMap.put(id, this);	
		
		setMembersMargin(10);
		
		if(subTitle != null) {
			/* Sub Title 생성 */
			Label subTitleLabel = new Label();
			subTitleLabel.setAutoHeight();   
			subTitleLabel.setAlign(Alignment.LEFT);   
			subTitleLabel.setValign(VerticalAlignment.CENTER);
			subTitleLabel.setStyleName("subTitle");
			subTitleLabel.setContents(I18N.message("audit"));
			addMember(subTitleLabel);
		}
		
		grid = new ListGrid();		
		grid.setShowAllRecords(true);		
		grid.setEmptyMessage(I18N.message("notitemstoshow"));		
		grid.setCanFreezeFields(true);
		grid.setCanRemoveRecords(false);
		grid.setSelectionType(SelectionStyle.SINGLE);
		grid.invalidateCache();
				
		ListGridField idField = new ListGridField("userId", I18N.message("userid"));
		ListGridField titleField = new ListGridField("title", I18N.message("title"));
		ListGridField pathField = new ListGridField("path", I18N.message("path"));
		ListGridField dateField = new ListGridField("date", I18N.message("date"), 130);
		ListGridField actionField = new ListGridField("event", I18N.message("event"));
		ListGridField descriptionField = new ListGridField("description", I18N.message("description"));
		
		dateField.setAlign(Alignment.CENTER);
		dateField.setType(ListGridFieldType.DATE);
		dateField.setCellFormatter(new DateCellFormatter(I18N.message("yyyy/MM/dd HH:mm:ss")));
		
		descriptionField.setWidth("*");
		
		grid.setFields(idField, titleField, pathField, dateField, actionField, descriptionField);
		
		VLayout gridPanel = new VLayout();
        gridPanel.setHeight100();
        gridPanel.setMembersMargin(0);
        gridPanel.setMembers(createSearchForm(), grid);
		
		VLayout pagerPanel = new VLayout();
		pagerPanel.setHeight100();
		pagerPanel.setMembersMargin(1);
		pagerPanel.addMember(gridPanel);
        
        gridPager = new PagingToolStrip(grid, 20, false, this);
        gridPager.setDeselect(false);
        gridPager.setIsAutoHeightGrid(false);
        
        grid.setHeight100();
        grid.setWidth100();
        grid.setBodyOverflow(Overflow.SCROLL);
        
        pagerPanel.addMember(gridPager);
        addMember(pagerPanel);
        
        executeFetch();
	}
	
	/**
	 * 상단 검색 Form 생성
	 * @return
	 */
	private DynamicForm createSearchForm() {		
		SpacerItem dummyItem = new SpacerItem();
		dummyItem.setWidth(5);
		
		//사용자 id
		HiddenItem userIdItem = new HiddenItem("userId");
        
        //사용자(삭제) = 전체
        PickerIcon userClearPicker = new PickerIcon(PickerIcon.CLEAR, new FormItemClickHandler() {   
            public void onFormItemClick(FormItemIconClickEvent event) {   
            	searchForm.getField("userId").clearValue();
				searchForm.getField("userName").clearValue();
            }   
        });
		
        //사용자(검색) = 지정
		PickerIcon userSearchPicker = new PickerIcon(PickerIcon.SEARCH, new FormItemClickHandler() {   
            public void onFormItemClick(FormItemIconClickEvent event) {   
            	final CommSearchDialog commSearchDialog = new CommSearchDialog(CommSearchDialog.USERGROUP, "",I18N.message("users"));
				commSearchDialog.addResultHandler(new ResultHandler() {
					@Override
					public void onSelected(HashMap<String, String> resultMap) {						
						searchForm.getField("userId").setValue(resultMap.get("id"));
						searchForm.getField("userName").setValue(resultMap.get("name"));
					}
				});
				commSearchDialog.show();
            }   
        });
		
		final DateItem fromItem = new DateItem("from", I18N.message("from"));
		fromItem.setWrapTitle(false);
		fromItem.setRequired(true);
		fromItem.setShowTitle(false);
		
		StaticTextItem commonDateColumn = new StaticTextItem();
		commonDateColumn.setValue("~");
		commonDateColumn.setWidth(7);
		commonDateColumn.setAlign(Alignment.CENTER);
		commonDateColumn.setShowTitle(false);
		commonDateColumn.setStartRow(false);
		commonDateColumn.setEndRow(false);
		
		//종료일자
		final DateItem toItem = new DateItem("to", I18N.message("to"));
		toItem.setWrapTitle(false);
		toItem.setRequired(true);
		toItem.setShowTitle(false);
		
		ChangedHandler ch = new ChangedHandler() {
			@Override
			public void onChanged(ChangedEvent event) {
				Date endDate = toItem.getValueAsDate();
					if(endDate.before(fromItem.getValueAsDate())){
						SC.warn(I18N.message("youcantchoiceafterday"));
						toItem.setValue(endDate);
					}
				}
		};
		
		//20131204na 이전일자 클릭시 팝업
		fromItem.addChangedHandler(ch);
		toItem.addChangedHandler(ch);
		
		//사용자명
		TextItem userNameItem = new TextItem("userName", I18N.message("user"));
		userNameItem.setWrapTitle(false);
		userNameItem.setCanEdit(false);
		userNameItem.setEmptyDisplayValue(I18N.message("all"));
		userNameItem.setIcons(userClearPicker, userSearchPicker);
		userNameItem.setDisableIconsOnReadOnly(false);
		userNameItem.setWidth(100);
		
		actionItem = new SelectItem("event",I18N.message("event"));
		actionItem.setWrapTitle(false);
		actionItem.setWidth("*");
		
		executeGetOptionsAndSet();
		
		ButtonItem searchButton = new ButtonItem();
		searchButton.setTitle(I18N.message("search"));	
		searchButton.setStartRow(false);
		searchButton.setIcon("[SKIN]/actions/search.png");		
		searchButton.addClickHandler(new com.smartgwt.client.widgets.form.fields.events.ClickHandler() {
			@Override
			public void onClick(com.smartgwt.client.widgets.form.fields.events.ClickEvent event) {
				executeFetch();
			}
        });
		
		searchForm = new DynamicForm();
		searchForm.setWidth100();
		searchForm.setAlign(Alignment.RIGHT);			
		searchForm.setMargin(4);
		searchForm.setNumCols(15);
		searchForm.setColWidths("*","1","1","1","1","1","1","1","1","1","1","1","1","1","1");
		
		searchForm.setItems(dummyItem, fromItem, dummyItem, commonDateColumn, dummyItem, toItem, dummyItem, actionItem, dummyItem, userIdItem, userNameItem, dummyItem, searchButton);
		return searchForm;
	}
	
	/**
	 * 1(Default)페이지 조회
	 */
	public void executeFetch() {
		executeFetch(1, gridPager.getPageSize());
	}
	
	
	private void executeFetch(final int pageNum, final int pageSize)	{				
		Log.debug("[ AuditGridPanel executeFetch ] pageNum["+pageNum+"], pageSize["+pageSize+"]");
		
		SHistorySearchOptions searchOptions = new SHistorySearchOptions();
		
		String userId = (String)searchForm.getField("userId").getValue();
		if(userId != null) {
			searchOptions.setUserId(userId);
		}
		
		searchOptions.setFrom((Date)searchForm.getField("from").getValue());
		searchOptions.setTo((Date)searchForm.getField("to").getValue());
		
		String action = (String)searchForm.getField("event").getValue();		
		if(action != null) {
			searchOptions.setEvent(action);
		}
		
		PagingConfig config = PagingToolStrip.getPagingConfig(pageNum, pageSize);
		searchOptions.setConfig(config);
		
		ServiceUtil.system().pagingHistory(Session.get().getSid(), searchOptions, new AsyncCallbackWithStatus<PagingResult<SHistory>>() {
			@Override
			public String getSuccessMessage() {
				return I18N.message("client.searchComplete");
			}
			@Override
			public String getProcessMessage() {
				return I18N.message("client.searchRequest");
			}
			@Override
			public void onFailureEvent(Throwable caught) {
				SCM.warn(caught);
			}
			@Override
			public void onSuccessEvent(PagingResult<SHistory> result) {
				grid.setData(new ListGridRecord[0]); //그리드 초기화
				
				List<SHistory> data = result.getData();
				SHistory history;	
				
				for (int j = 0; j < data.size(); j++) {
					history = data.get(j);
					
					ListGridRecord record=new ListGridRecord();
					record.setAttribute("userId", history.getUserName());
					record.setAttribute("title", history.getTitle());
					record.setAttribute("path", history.getPath());
					record.setAttribute("date", history.getDate());
					record.setAttribute("event", I18N.message(history.getEvent()));
					record.setAttribute("description", history.getComment());
					
					grid.addData(record);
				}	
				
				if (data.size() > 0) {
					grid.selectSingleRecord(0);
				}
				
				Log.debug("isExistData["+(data.size() > 0)+"], pageNum["+ pageNum + "]");
				gridPager.setRespPageInfo((data.size() > 0), pageNum); 
			}
		});
	}
	
	private void executeGetOptionsAndSet() {
		//ACTIONS
//		documentCodeService.listCodes(Session.get().getSid(), "ACTIONS", new AsyncCallbackWithStatus<List<SCode>>() {
//			@Override
//			public String getSuccessMessage() {
//				return I18N.message("client.searchComplete");
//			}
//			@Override
//			public String getProcessMessage() {
//				return I18N.message("client.searchRequest");
//			}
//			@Override
//			public void onSuccessEvent(List<SCode> result) {
//				final LinkedHashMap<String, String> opts = new LinkedHashMap<String, String>() ;
//				opts.put("", I18N.message("all"));
//				if( result.size() > 0) {
//					for(int j=0; j<result.size(); j++) {
//						opts.put(result.get(j).getValue(), I18N.message(result.get(j).getName()));
//					}
//				}
//				actionItem.setValueMap(opts);
//				actionItem.setDefaultValue("");
//				
//				setDefaultStatVal();
//			}			
//			@Override
//			public void onFailureEvent(Throwable caught) {
//				SCM.warn(caught);
//			}
//		});
		
		ServiceUtil.system().listAllActions(Session.get().getSid(), new AsyncCallback<String[]>() {
			
			@Override
			public void onFailure(Throwable caught) {
				SCM.warn(caught);
			}
			@Override
			public void onSuccess(String[] result) {
				final LinkedHashMap<String, String> opts = new LinkedHashMap<String, String>() ;
				opts.put("", I18N.message("all"));
				if( result.length > 0) {
					for(int j=0; j<result.length; j++) {
						opts.put(result[j], I18N.message(result[j]));
					}
				}
				actionItem.setValueMap(opts);
				actionItem.setDefaultValue("");
				
				setDefaultStatVal();
			}
		});
	}
	
	private void setDefaultStatVal() {		
		Date date = (Date)searchForm.getField("from").getValue();
		CalendarUtil.addMonthsToDate(date, -1);
		searchForm.getField("from").setValue(date);
	}
	

	@Override
	public void onPageDataReqeust(int pageNum, int pageSize) {
		executeFetch(pageNum, pageSize);
	}
}