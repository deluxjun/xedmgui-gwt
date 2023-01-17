package com.speno.xedm.gui.frontend.client.document;

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
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.RecordDoubleClickEvent;
import com.smartgwt.client.widgets.grid.events.RecordDoubleClickHandler;
import com.smartgwt.client.widgets.grid.events.RemoveRecordClickEvent;
import com.smartgwt.client.widgets.grid.events.RemoveRecordClickHandler;
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
import com.speno.xedm.gui.common.client.util.SCM;
import com.speno.xedm.gui.frontend.client.PagingToolStrip;
import com.speno.xedm.gui.frontend.client.document.popup.MessagePopup;
import com.speno.xedm.gwt.service.MessageService;
import com.speno.xedm.gwt.service.MessageServiceAsync;
import com.speno.xedm.util.GeneralException;
import com.speno.xedm.util.paging.PagingConfig;
import com.speno.xedm.util.paging.PagingResult;

/**
 * MessageGrid Panel
 * @author 남윤성
 * @since 1.0
 */
public class MessageGridPanel extends VLayout implements PagingObserver{	
	private static HashMap<String, MessageGridPanel> instanceMap = new HashMap<String, MessageGridPanel>();	
	private MessageServiceAsync service = (MessageServiceAsync) GWT.create(MessageService.class);
	private ListGrid grid;
	private PagingToolStrip gridPager;

	public String threadType;//Notice, Received, Send
	private VLayout btnrPanel;
	private MessagePanel msgPan;
			
	/**
	 * 기 생성된 instance를 반환함. instance가 없을 경우 생성 후 반환함
	 * @param id
	 * @param subTitle
	 * @param ob
	 * @param isAction
	 * @return
	 */
	public static MessageGridPanel get(final String id, final String subTitle, final String tab, final RecordObserver ob, final boolean isAction, final MessagePanel msgPan) {
		if (instanceMap.get(id) == null) {
			new MessageGridPanel(id, subTitle, tab, ob, isAction, msgPan);
		}
		return instanceMap.get(id);
	}
	
	public MessageGridPanel(final String id, final String subTitle, final String tab, final RecordObserver ob, final boolean isAction, final MessagePanel msgPan) {
		instanceMap.put(id, this);	
		
		this.threadType = tab;
		this.msgPan = msgPan;
		
		if("Received".equals(threadType)){
			final ButtonItem sendMessageBtnItem = new ButtonItem("addwindow", I18N.message("second.sendmessage"));  
			sendMessageBtnItem.setIcon(ItemFactory.newImgIcon("add.png").getSrc());  
			sendMessageBtnItem.setAutoFit(true);  
		    sendMessageBtnItem.setStartRow(false);  
			sendMessageBtnItem.setEndRow(false);  
			sendMessageBtnItem.addClickHandler(new com.smartgwt.client.widgets.form.fields.events.ClickHandler() {  
		        public void onClick(com.smartgwt.client.widgets.form.fields.events.ClickEvent event) {  
		        	MessagePopup msgPopup = new MessagePopup(Session.get().getSid(),MessageGridPanel.this, Constants.MESSAGE_SEND, 0);
		    		msgPopup.show();
		        }  
			});  
			
			DynamicForm dumyForm = new DynamicForm();
			dumyForm.setItems(sendMessageBtnItem);
			btnrPanel = new VLayout();
			btnrPanel.setHeight(1);
			btnrPanel.addMember(dumyForm);
			addMember(btnrPanel);
		}
		
		grid = new ListGrid() {
			@Override
			protected String getCellCSSText(ListGridRecord record, int rowNum, int colNum) {
				if (getFieldName(colNum).equals("authorName")||getFieldName(colNum).equals("subject")||getFieldName(colNum).equals("modified")) {
					if ("0".equals(record.getAttributeAsString("read"))) {
						return "font-weight:bold;";
					} else {
						return super.getCellCSSText(record, rowNum, colNum);
					}
				} else {
					return super.getCellCSSText(record, rowNum, colNum);
				}
			}
		};
		grid.setShowAllRecords(true);		
		grid.setEmptyMessage(I18N.message("notitemstoshow"));		
		grid.setCanFreezeFields(true);
		if("Send".equals(threadType)||"Received".equals(threadType)){
			grid.setCanRemoveRecords(true);
		}
		grid.setSelectionType(SelectionStyle.SINGLE);
		grid.invalidateCache();
		
		ListGridField idField = new ListGridField("id", I18N.message("id"));
		
		
		String author = "";
		if("Notice".equals(threadType))
			author = I18N.message("second.publisher2");
		else if("Received".equals(threadType))
			author = I18N.message("second.sendor");
		
		ListGridField authorField = new ListGridField("author", author);
		ListGridField authorNameField = new ListGridField("authorName", author);
		ListGridField toIdField = new ListGridField("toId", I18N.message("recipientsId"));
		ListGridField toNameField = new ListGridField("toName", I18N.message("recipients"));
		ListGridField titleField = new ListGridField("subject", I18N.message("title"));
		ListGridField messageField = new ListGridField("message", I18N.message("message"));
		messageField.setHidden(true);
		messageField.setWidth("*");
		ListGridField readField = new ListGridField("read", I18N.message("read"));
		readField.setHidden(true);
		
		ListGridField dateField = new ListGridField("modified", I18N.message("second.date"), 200);
		dateField.setType(ListGridFieldType.DATE);
		dateField.setAlign(Alignment.LEFT);
		dateField.setCellFormatter(new DateCellFormatter(I18N.message("yyyy/MM/dd HH:mm:ss")));
		
		
		if("Notice".equals(threadType)){
			idField.setHidden(true);
			authorField.setHidden(true);
			toIdField.setHidden(true);
			toNameField.setHidden(true);
		}else if("Received".equals(threadType)){
			idField.setHidden(true);
			authorField.setHidden(true);
			toIdField.setHidden(true);
			toNameField.setHidden(true);
		}else if("Send".equals(threadType)){
			idField.setHidden(true);
			authorField.setHidden(true);
			authorNameField.setHidden(true);
			toIdField.setHidden(true);
		}
		
		grid.setFields(idField, authorField, authorNameField, toIdField, toNameField, titleField, dateField, messageField, readField);
		
		VLayout gridPanel = new VLayout();
        gridPanel.setHeight100();
        gridPanel.setMembersMargin(0);
        gridPanel.setMembers(grid);
		
		VLayout pagerPanel = new VLayout();
		pagerPanel.setHeight100();
		pagerPanel.setMembersMargin(1);
		pagerPanel.addMember(gridPanel);
        
        gridPager = new PagingToolStrip(grid, Session.get().getUser().getPageSize(), true, this);
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
            	if("Received".equals(threadType)){
            		Record updateRecord = event.getRecord();
    				updateRecord.setAttribute("read", "1");
    				grid.getDataAsRecordList().set(event.getRecordNum(), updateRecord);		
            	}
				onViewClick(Integer.parseInt(event.getRecord().getAttribute("id")));
			}   
        });
    	
		VLayout noticeVL = new VLayout(5);
		setMembersMargin(Constants.SUBTITLE_MARGIN);
		noticeVL.addMember(pagerPanel);
		
		//record 삭제 event handler 정의--------------------------------------------------------------
		grid.addRemoveRecordClickHandler(new RemoveRecordClickHandler() {
			@Override
			public void onRemoveRecordClick(RemoveRecordClickEvent event) {
				final ListGridRecord record = grid.getRecord( event.getRowNum());
				SC.confirm(I18N.message("wanttodelete"),  new BooleanCallback() {
					@Override
					public void execute(Boolean value) {
						if(value != null && value) {
							executeRemove(Long.parseLong(record.getAttribute("id")));
						}
					}
				});
				event.cancel();
			}
		});
		
		grid.sort("modified", SortDirection.DESCENDING);
			
        addMember(noticeVL);
        executeFetch();
	}
	
	/**
	 * refresh
	 */
	public void refresh () {
		msgPan.refresh();
	}
	
	/**
	 * 탭선택
	 */
	public void selectTabs() {
		msgPan.selectTabs(); 
	}
	
	/**
	 * message popup 뛰우기
	 */
	private void onViewClick(int messageId){
		MessagePopup msgPopup = new MessagePopup(Session.get().getSid(),MessageGridPanel.this,"Notice".equals(threadType)?Constants.MESSAGE_NOTICE:Constants.MESSAGE_VIEW, messageId);
		if ("Send".equals(threadType))
			msgPopup.renameReply(I18N.message("requestrepeat"));
//			msgPopup.disableReply();
		msgPopup.show();
	}
	
	/**
	 * 1(Default)페이지 조회
	 */
	public void executeFetch() {
		executeFetch(1, gridPager.getPageSize());
	}
	
	private void executeFetch(final int pageNum, final int pageSize)	{				
		Log.debug("[ MessageGridPanel executeFetch ] pageNum["+pageNum+"], pageSize["+pageSize+"]"+"threadType["+threadType+"]");
		
		SHistorySearchOptions searchOptions = new SHistorySearchOptions();
		
		PagingConfig config = PagingToolStrip.getPagingConfig(pageNum, pageSize);
		searchOptions.setConfig(config);
		try {
			service.pagingMessages(Session.get().getSid(), config, threadType, new AsyncCallbackWithStatus<PagingResult<SMessage>>() {
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
					int unread = 0;
					for (int j = 0; j < data.size(); j++) {
						message = data.get(j);
						String tempToId = "";
						String tempToName = "";
						ListGridRecord record=new ListGridRecord();
						record.setAttribute("id", message.getId());
						record.setAttribute("author", message.getAuthor());
						record.setAttribute("authorName", message.getAuthorName());
						
						if("Received".equals(threadType)){
							record.setAttribute("authorName", message.getAuthor()+"("+message.getAuthorName()+")");
							SRecipient[]  sRecipient = message.getRecipients();
							String sessionUser = Session.get().getUser().getUserName();

							
							for(int i=0;i<sRecipient.length; i++) {
								if(sessionUser.equals(sRecipient[i].getAddress())){
									record.setAttribute("read", sRecipient[i].getRead());
									if (sRecipient[i].getRead() == 0)
										unread ++;
								}
							}

						}else if("Send".equals(threadType)){
							SRecipient[]  sRecipient = message.getRecipients();
							for(int i=0;i<sRecipient.length; i++) {
								tempToId += ","+ sRecipient[i].getAddress();
								tempToName += ","+ sRecipient[i].getAddress()+"("+sRecipient[i].getAddressName()+")";
							}
							record.setAttribute("toId", sRecipient.length ==0?"":tempToId.substring(1));
							record.setAttribute("toName", sRecipient.length ==0?"":tempToName.substring(1));
						}
						record.setAttribute("subject", message.getSubject());
						record.setAttribute("message", message.getMessage());
						record.setAttribute("modified", message.getLastNotified());
						
						grid.addData(record);
						
					}	
					
					// 20130821, junsoo, set unread count
					if("Received".equals(threadType))
						Session.get().getUser().setUnreadMessages(unread);
					
					if (data.size() > 0) {
						grid.selectSingleRecord(0);
					}
					
					Log.debug("isExistData["+(data.size() > 0)+"], pageNum["+ pageNum + "]"+"threadType["+threadType+"]");
					gridPager.setRespPageInfo(result.getTotalLength(), pageNum); 
				}
			});
		} catch (GeneralException e) {
//			e.printStackTrace();
		}
	}
	
	private void executeRemove(final long id)
	{
		Log.debug("[ MessageGridPanel executeRemove ] id["+id+"]");
		if(id < 0) return;
		
		long[] ids = new long[1];
		ids[0] = id;
				
		if("Received".equals(threadType)){
			service.deleteRecipient(Session.get().getSid(), ids, Session.get().getUser().getUserName(), new AsyncCallbackWithStatus<Void>() {
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
					Log.debug("[ MessageGridPanel executeRemove ] onSuccess. id["+id+"]");
					grid.removeSelectedData();
					SC.say(I18N.message("operationcompleted"));
				}
			});
		}else{
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
					Log.debug("[ MessageGridPanel executeRemove ] onSuccess. id["+id+"]");
					grid.removeSelectedData();
					SC.say(I18N.message("operationcompleted"));
				}
			});
		}
		
	}

	@Override
	public void onPageDataReqeust(int pageNum, int pageSize) {
		executeFetch(pageNum, pageSize);
	}
}