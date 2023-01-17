package com.speno.xedm.gui.frontend.client.admin.system;

import java.util.HashMap;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.types.SortDirection;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.RichTextEditor;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.CheckboxItem;
import com.smartgwt.client.widgets.form.fields.HiddenItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.RecordClickEvent;
import com.smartgwt.client.widgets.grid.events.RecordClickHandler;
import com.smartgwt.client.widgets.grid.events.RecordDoubleClickEvent;
import com.smartgwt.client.widgets.grid.events.RecordDoubleClickHandler;
import com.smartgwt.client.widgets.grid.events.RemoveRecordClickEvent;
import com.smartgwt.client.widgets.grid.events.RemoveRecordClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.speno.xedm.core.service.serials.SHistorySearchOptions;
import com.speno.xedm.core.service.serials.SMessage;
import com.speno.xedm.core.service.serials.SRecipient;
import com.speno.xedm.gui.common.client.Constants;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.PagingObserver;
import com.speno.xedm.gui.common.client.RecordObserver;
import com.speno.xedm.gui.common.client.Session;
import com.speno.xedm.gui.common.client.log.Log;
import com.speno.xedm.gui.common.client.util.AsyncCallbackWithStatus;
import com.speno.xedm.gui.common.client.util.DateCellFormatter;
import com.speno.xedm.gui.common.client.util.ItemFactory;
import com.speno.xedm.gui.common.client.util.LengthValidator;
import com.speno.xedm.gui.common.client.util.SCM;
import com.speno.xedm.gui.frontend.client.PagingToolStrip;
import com.speno.xedm.gwt.service.MessageService;
import com.speno.xedm.gwt.service.MessageServiceAsync;
import com.speno.xedm.util.GeneralException;
import com.speno.xedm.util.paging.PagingConfig;
import com.speno.xedm.util.paging.PagingResult;

/**
 * NoticeGrid Panel
 * @author na
 * @since 1.0
 */
public class NoticeGridPanel extends VLayout implements PagingObserver {	
	private static HashMap<String, NoticeGridPanel> instanceMap = new HashMap<String, NoticeGridPanel>();	
	private MessageServiceAsync service = (MessageServiceAsync) GWT.create(MessageService.class);
	private ListGrid grid;
	private PagingToolStrip gridPager;
	private DynamicForm form;
	private DynamicForm formCheck;
	private HLayout actionHL;
	private HLayout editorHL;
	private RichTextEditor richTextEditor;
	private final static int TYPE_SYSTEM = 0;
	private CheckboxItem checkItem;
	
	private RecordObserver recordObserver;	
	
	/**
	 * 기 생성된 instance를 반환함. instance가 없을 경우 생성 후 반환함
	 * @param id
	 * @param subTitle
	 * @param ob
	 * @param isAction
	 * @return
	 */
	public static NoticeGridPanel get(final String id, final String subTitle, final RecordObserver ob, final boolean isAction) {
		if (instanceMap.get(id) == null) {
			new NoticeGridPanel(id, subTitle, ob, isAction);
		}
		return instanceMap.get(id);
	}
	
	public NoticeGridPanel(final String id, final String subTitle, final RecordObserver ob, final boolean isAction) {
		instanceMap.put(id, this);	
		
		this.recordObserver = ob;
				
		grid = new ListGrid();
		grid.setShowAllRecords(true);		
		grid.setEmptyMessage(I18N.message("notitemstoshow"));		
		grid.setCanFreezeFields(true);
		grid.setCanRemoveRecords(true);
		grid.setSelectionType(SelectionStyle.SINGLE);
		grid.invalidateCache();
		
		//20131217na 머릿글 선택 추가
		ListGridField headerField = new ListGridField("header", I18N.message("header"));
		ListGridField idField = new ListGridField("id", I18N.message("id"));
		ListGridField publisherField = new ListGridField("author", I18N.message("second.publisher2"));
		ListGridField titleField = new ListGridField("subject", I18N.message("title"));
		ListGridField messageField = new ListGridField("message", I18N.message("message"));
		ListGridField dateField = new ListGridField("modified", I18N.message("second.date"),200);
		
		headerField.setWidth(40);
		headerField.setAlign(Alignment.CENTER);
		messageField.setWidth("*");
		messageField.setHidden(true);
		dateField.setType(ListGridFieldType.DATE);
		dateField.setAlign(Alignment.LEFT);
		dateField.setCellFormatter(new DateCellFormatter(I18N.message("yyyy/MM/dd HH:mm:ss")));
		
		
		grid.setFields(headerField, idField, publisherField, titleField, messageField, dateField);
		grid.sort("modified", SortDirection.DESCENDING);
		
		VLayout gridPanel = new VLayout();
        gridPanel.setHeight100();
        gridPanel.setMembersMargin(0);
        gridPanel.setMembers(grid);
		
		VLayout pagerPanel = new VLayout();
		pagerPanel.setHeight100();
		pagerPanel.setMembersMargin(1);
		pagerPanel.addMember(gridPanel);
        
        gridPager = new PagingToolStrip(grid, 20, true, this);
        gridPager.setDeselect(false);
        gridPager.setIsAutoHeightGrid(false);
        
        grid.setHeight100();
        grid.setWidth100();
        grid.setBodyOverflow(Overflow.SCROLL);
        
        pagerPanel.addMember(gridPager);
		
        //record dbclick event handler 정의------------------------------------------------------------
		grid.addRecordDoubleClickHandler(new RecordDoubleClickHandler() {
			@Override
			public void onRecordDoubleClick(RecordDoubleClickEvent event) {
				if(recordObserver != null) {
					recordObserver.onRecordDoubleClick(event.getRecord());
				}
			}   
        });
    	
		VLayout noticeVL = new VLayout(5);
		setMembersMargin(Constants.SUBTITLE_MARGIN);
		noticeVL.addMember(pagerPanel);
		
		if(isAction) {			
			//record click event handler 정의--------------------------------------------------------------
			grid.addRecordClickHandler(new RecordClickHandler() {   
	            public void onRecordClick(RecordClickEvent event) {
	            	recordClickedProcess(event.getRecord());
	            }   
	        });
			
			//record 삭제 event handler 정의--------------------------------------------------------------
			grid.addRemoveRecordClickHandler(new RemoveRecordClickHandler() {
				@Override
				public void onRemoveRecordClick(final RemoveRecordClickEvent event) {
					SC.confirm(I18N.message("confirmdelete"),  new BooleanCallback() {
						@Override
						public void execute(Boolean value) {
							if(value != null && value) {
								ListGridRecord record = grid.getRecord( event.getRowNum());
								form.reset();
								form.editRecord(record);
								richTextEditor.setValue("");
								richTextEditor.setValue(record.getAttribute("message"));
								executeRemove(Long.parseLong(record.getAttribute("id")));
							}
						}
					});
					event.cancel();
				}
			});
			
			noticeVL.addMember(createFormVL());
			noticeVL.addMember(createActHL());
		}
        
        addMember(noticeVL);
        
        executeFetch();
	}
	
	/**
	 * 하단 상세 Form 생성
	 * @return
	 */
	private VLayout createFormVL() {
		HiddenItem typeIdItem = new HiddenItem("id");
		
		TextItem titleItem = new TextItem("subject", I18N.message("title"));
		titleItem.setWidth(200);
		titleItem.setCanEdit(true);
		titleItem.setWrapTitle(false);
		titleItem.setRequired(true);
//		titleItem.setLength(Constants.MAX_LEN_NAME);
		titleItem.setValidators(new LengthValidator(titleItem, Constants.MAX_LEN_NAME));
        
		titleItem.setStartRow(false);			titleItem.setEndRow(false);		
		
		checkItem = new CheckboxItem("headerCheck",I18N.message("header"));
		
		
		form = new DynamicForm();
		formCheck = new DynamicForm();
		
		form.setAutoWidth();
		form.setColWidths("1","1");
		form.setItems(typeIdItem, titleItem);
		form.reset();
		
		formCheck.setAutoWidth();
		formCheck.setColWidths("1","1");
		formCheck.setItems(checkItem);
		formCheck.reset();
    	
		editorHL = new HLayout();
		editorHL.setHeight100();
		editorHL.addMembers(createEditor());
		
		HLayout formHL = new HLayout();
		formHL.setWidth100();
		formHL.setHeight("20%");
		formHL.addMembers(form, formCheck);
		
    	VLayout formVL = new VLayout();
    	formVL.setBorder("1px solid gray");
    	formVL.setWidth100();
    	formVL.setHeight("50%");    	
    	formVL.addMembers(formHL,editorHL);
    	
    	return formVL;
	}
	
	private RichTextEditor createEditor(){
		
		richTextEditor = new RichTextEditor(); 
        richTextEditor.setOverflow(Overflow.HIDDEN);  
        richTextEditor.setCanDragResize(true);  
        richTextEditor.setShowEdges(true);  
        richTextEditor.setHeight100();
        richTextEditor.setWidth100();
        richTextEditor.setStyleName("richTextEditor");
		return richTextEditor;
	}

//	private VerticalPanel createTextArea(){
//
//		VerticalPanel layout = new VerticalPanel();
//		richTextArea = new RichTextArea();
//		richTextToolbar = new RichTextToolbar(richTextArea);
//		layout.add(richTextToolbar);
//		layout.add(richTextArea);
//		layout.setHeight("100%");
//		layout.setWidth("100%");
//		return layout;
//	}

	/**
	 * Action Panel 생성
	 * @return
	 */
	private HLayout createActHL() {		
		Button btnAddNew = new Button(I18N.message("addnew"));
		btnAddNew.setIcon(ItemFactory.newImgIcon("page_white_add.png").getSrc());
		btnAddNew.addClickHandler(new ClickHandler() {   
            public void onClick(ClickEvent event) {
            	form.getItem("id").setTooltip(I18N.message("generatedbyserver", form.getItem("id").getTitle()));
            	form.editNewRecord();
            	form.reset();
            	checkItem.setValue(false);            	
            	grid.deselectAllRecords();
            	richTextEditor.destroy();
            	editorHL.addMembers(createEditor());
            }   
        });
		
		Button btnSave = new Button(I18N.message("save"));
		btnSave.setIcon(ItemFactory.newImgIcon("data_into.png").getSrc());
		btnSave.addClickHandler(new ClickHandler() {   
            public void onClick(ClickEvent event) {
            	try{
	            	int editLength = richTextEditor.getValue().getBytes("UTF-8").length;
	            	Log.debug("NoticeGridPanel editLength["+editLength+"]");
	            	if(form.getValue("id") == null) {
	            		 if(form.validate()) {
	            			 if(editLength < 2000){
	            				 executeAdd();
	            			 }else{
	            				 SC.say(I18N.message("second.messagecharacterslimited",Integer.toString(editLength)));
	            			 }
	            		 }
	            	}
	            	else {
	            		 if(form.validate()) {
	            			 if(editLength < 2000){
	            				 executeUpdate();
	            			 }else{
	            				 SC.say(I18N.message("second.messagecharacterslimited",Integer.toString(editLength)));
	            			 }
	            		 }
	            	}
            	}catch(Exception e){
                }
	           	}   
            
        });
		
		actionHL = new HLayout(10);
		actionHL.setHeight(1);
		actionHL.setMembers(btnAddNew, btnSave);		
		return actionHL;
	}
	
	/**
	 * grid 가져오기
	 */
	public ListGrid getGrid() {
		return grid;
	}
	
	/**
	 * Record Click Event Handler
	 * @param record
	 */
	private void recordClickedProcess(Record record) {
		if(form != null) {
			form.getItem("id").setTooltip(I18N.message("fieldisreadonly", form.getItem("id").getTitle()));
	    	form.reset();
	    	form.editRecord(record);
	    	
			//20140813na 머릿글 체크박스 수정
	    	CheckboxItem checkboxItem = (CheckboxItem) formCheck.getItem("headerCheck");
	    	boolean isCheck = Boolean.parseBoolean(record.getAttribute("headerCheck"));
	    	checkboxItem.setValue(isCheck);
	    	
	    	richTextEditor.setValue("");
	    	richTextEditor.setValue(record.getAttribute("message"));
		}
	}
	
	/**
	 * 1(Default)페이지 조회
	 */
	public void executeFetch() {
		executeFetch(1, gridPager.getPageSize());
	}
	
	private void executeFetch(final int pageNum, final int pageSize)	{				
		Log.debug("[ NoticeGridPanel executeFetch ] pageNum["+pageNum+"], pageSize["+pageSize+"]");
		
		SHistorySearchOptions searchOptions = new SHistorySearchOptions();
		
		PagingConfig config = PagingToolStrip.getPagingConfig(pageNum, pageSize);
		searchOptions.setConfig(config);
		
		try {
			service.pagingMessages(Session.get().getSid(), config, "Notice", new AsyncCallbackWithStatus<PagingResult<SMessage>>() {
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
				public void onSuccessEvent(PagingResult<SMessage> result) {
					grid.setData(new ListGridRecord[0]); //그리드 초기화
					
					List<SMessage> data = result.getData();
					SMessage message;	
					
					for (int j = 0; j < data.size(); j++) {
						message = data.get(j);
						
						ListGridRecord record=new ListGridRecord();
						record.setAttribute("header", message.getStatusAsString());
						record.setAttribute("id", message.getId());
						record.setAttribute("author", message.getAuthor());
						record.setAttribute("subject", message.getSubject());
						record.setAttribute("message", message.getMessage());
						record.setAttribute("modified", message.getLastNotified());
						record.setAttribute("headerCheck", (message.getStatus()==1)? "true" : "false");
						
						grid.addData(record);
						
						if(j==0){
							recordClickedProcess(record);
						}
					}	
					
					if (data.size() > 0) {
						grid.selectSingleRecord(0);
					}
					
					Log.debug("isExistData["+(data.size() > 0)+"], pageNum["+ pageNum + "]");
					gridPager.setRespPageInfo(result.getTotalLength(), pageNum); 
				}
			});
		} catch (GeneralException e) {
//			e.printStackTrace();
		}
	}
	
	private void executeAdd() {
		Log.debug("[ NoticeGridPanel executeAdd ]");
		
		SMessage smessage = new SMessage();
		smessage.setId(0L);
		smessage.setSubject(form.getValueAsString("subject"));
		smessage.setMessage(richTextEditor.getValue());
		smessage.setType(TYPE_SYSTEM);
		smessage.setAuthor("admin");
		if ("true".equalsIgnoreCase(formCheck.getValueAsString("headerCheck")))
			smessage.setStatus(1);
		else
			smessage.setStatus(0);

		SRecipient sRecipient1 = new SRecipient();
		sRecipient1.setAddress("admin");
		SRecipient[] sRecipients = {sRecipient1};	
		smessage.setRecipients(sRecipients);
		
		service.save(Session.get().getSid(), smessage, new AsyncCallbackWithStatus<SMessage>() {
			@Override
			public String getSuccessMessage() {
				return I18N.message("operationcompleted");
			}
			@Override
			public String getProcessMessage() {
				return null;
			}
			@Override
			public void onFailureEvent(Throwable caught) {
				SCM.warn(caught);
			}
			@Override
			public void onSuccessEvent(SMessage result) {
				Log.debug("[ NoticeGridPanel executeAdd ] onSuccess. id["+result.getId()+"]");
				
				ListGridRecord addRecord = new ListGridRecord();
				addRecord.setAttribute("header", result.getStatusAsString());
				addRecord.setAttribute("id", result.getId());		
				addRecord.setAttribute("author", result.getAuthor());
				addRecord.setAttribute("subject", result.getSubject());
				addRecord.setAttribute("message", result.getMessage());
				addRecord.setAttribute("modified", result.getLastNotified());
				addRecord.setAttribute("headerCheck", (result.getStatus()==1)? "true" : "false");
				grid.addData(addRecord);
				
				int selectedRowNum = grid.getRecordIndex(addRecord);						
				grid.selectSingleRecord(selectedRowNum);
				grid.scrollToRow(selectedRowNum);						
				form.reset();
            	form.editRecord(addRecord);
            	richTextEditor.setValue("");
            	richTextEditor.setValue(addRecord.getAttribute("message"));
            	form.getItem("id").setTooltip(I18N.message("fieldisreadonly", form.getItem("id").getTitle()));
            	SC.say(I18N.message("operationcompleted"));
			}
		});
	}
	
	private void executeUpdate() {
		Log.debug("[ NoticeGridPanel executeUpdate ]");
		
		SMessage smessage = new SMessage();
		smessage.setId(Long.parseLong(form.getValueAsString("id")));
		smessage.setSubject(form.getValueAsString("subject"));
		smessage.setMessage(richTextEditor.getValue());
		Log.debug("testsize..::"+ richTextEditor.getValue().getBytes().length);
		smessage.setType(TYPE_SYSTEM);
		smessage.setAuthor("admin");
		if ("true".equalsIgnoreCase(formCheck.getValueAsString("headerCheck")))
			smessage.setStatus(1);
		else
			smessage.setStatus(0);
		SRecipient sRecipient1 = new SRecipient();
		sRecipient1.setAddress("admin");
		SRecipient[] sRecipients = {sRecipient1};	
		smessage.setRecipients(sRecipients);
		
		service.save(Session.get().getSid(), smessage, new AsyncCallbackWithStatus<SMessage>() {
			@Override
			public String getSuccessMessage() {
//				return I18N.message("operationcompleted");
				// kimsoeun GS인증용  - 작업 완료 -> 저장 완료 문구 변경
				return I18N.message("savecompleted");
			}
			@Override
			public String getProcessMessage() {
				return null;
			}
			@Override
			public void onFailureEvent(Throwable caught) {
				SCM.warn(caught);
			}
			@Override
			public void onSuccessEvent(SMessage result) {
				Log.debug("[ NoticeGridPanel executeUpdate ] onSuccess. id["+result.getId()+"]");
				
				final int selectedRowNum = grid.getRecordIndex(grid.getSelectedRecord());				
				ListGridRecord selectedRecord = grid.getSelectedRecord();
				selectedRecord.setAttribute("subject", result.getSubject());
				selectedRecord.setAttribute("message", result.getMessage());
				selectedRecord.setAttribute("header", result.getStatusAsString());
				selectedRecord.setAttribute("headerCheck", (result.getStatus()==1)? "true" : "false");

				grid.getDataAsRecordList().set(selectedRowNum, selectedRecord);				
				grid.selectSingleRecord(selectedRowNum);
				grid.scrollToRow(selectedRowNum);
//				SC.say(I18N.message("operationcompleted"));			
				// kimsoeun GS인증용  - 작업 완료 -> 저장 완료 문구 변경
				SC.say(I18N.message("savecompleted"));	
				
			}
		});
	}	
	
	private void executeRemove(final long id)
	{
		Log.debug("[ NoticeGridPanel executeRemove ] id["+id+"]");
		if(id < 0) return;
		
		long[] ids = new long[1];
		ids[0] = id;
				
		service.delete(Session.get().getSid(), ids, new AsyncCallbackWithStatus<Void>() {
			@Override
			public String getSuccessMessage() {
				return I18N.message("operationcompleted");
			}
			@Override
			public String getProcessMessage() {
				return null;
			}
			@Override
			public void onFailureEvent(Throwable caught) {
				SCM.warn(caught);
			}
			@Override
			public void onSuccessEvent(Void result) {
				Log.debug("[ NoticeGridPanel executeRemove ] onSuccess. id["+id+"]");
				grid.removeSelectedData();
				form.editNewRecord();
            	form.reset();
            	richTextEditor.setValue("");
				form.getItem("id").setTooltip(I18N.message("generatedbyserver", form.getItem("id").getTitle()));
				SC.say(I18N.message("operationcompleted"));
			}
		});
	}

	@Override
	public void onPageDataReqeust(int pageNum, int pageSize) {
		executeFetch(pageNum, pageSize);
		
	}
}