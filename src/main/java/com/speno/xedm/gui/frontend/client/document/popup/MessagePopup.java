package com.speno.xedm.gui.frontend.client.document.popup;


import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.RichTextEditor;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.HiddenItem;
import com.smartgwt.client.widgets.form.fields.PickerIcon;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ClickEvent;
import com.smartgwt.client.widgets.form.fields.events.ClickHandler;
import com.smartgwt.client.widgets.form.fields.events.FormItemClickHandler;
import com.smartgwt.client.widgets.form.fields.events.FormItemIconClickEvent;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.speno.xedm.core.service.serials.SDocument;
import com.speno.xedm.core.service.serials.SInfo;
import com.speno.xedm.core.service.serials.SMessage;
import com.speno.xedm.core.service.serials.SRecipient;
import com.speno.xedm.gui.common.client.Constants;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.Session;
import com.speno.xedm.gui.common.client.log.Log;
import com.speno.xedm.gui.common.client.util.AsyncCallbackWithStatus;
import com.speno.xedm.gui.common.client.util.LengthValidator;
import com.speno.xedm.gui.common.client.util.ReturnHandler;
import com.speno.xedm.gui.common.client.util.SCM;
import com.speno.xedm.gui.common.client.util.Util;
import com.speno.xedm.gui.common.client.window.Waiting;
import com.speno.xedm.gui.frontend.client.document.DocumentsGrid;
import com.speno.xedm.gui.frontend.client.document.MessageGridPanel;
import com.speno.xedm.gui.frontend.client.folder.OwnerWindow;
import com.speno.xedm.gwt.service.MessageService;
import com.speno.xedm.gwt.service.MessageServiceAsync;

public class MessagePopup extends Window{
	private MessageServiceAsync service = (MessageServiceAsync) GWT.create(MessageService.class);
	private static HashMap<String, MessagePopup> instanceMap = new HashMap<String, MessagePopup>();	
	
	// 화면 구분 레이아웃 설정
	protected VLayout main;
	public DynamicForm form;
	private HLayout actionHL;
	private int Style;
	private MessageGridPanel mgp;
	private long messageId;
	private RichTextEditor richTextEditor;
	private Canvas textView;
	private ButtonItem btnRefly;
	private ButtonItem btnEdiotrReset;
	private PickerIcon userClearPicker;
	private PickerIcon userSearchPicker;
	private TextItem toNameItem;
	private TextItem authorItem;
	private TextItem authorNameItem;
	private TextItem authorNameTempItem;
	private TextItem toIdItem;
	private TextItem selectToIdItem;
	private TextItem toNameTempItem ;
	private TextItem titleItem;
	private HLayout editorHL;
	private DynamicForm btnForm1;
	
	private DocumentsGrid grid;
	private List<SDocument> list;
	
	/**
	 * this is test
	 * @return VersionsPopup
	 */
	public static MessagePopup get(final String id){
		return instanceMap.get(id);
	}
	
	/**
	 * 기본 메시지 전송
	 */
	public MessagePopup(final String id, final MessageGridPanel mgp, final int type, final long messageId) {
		instanceMap.put(id, this);
		this.mgp = mgp;
		this.messageId = messageId;
		
		init(type);		
		createFormVL();
		createActHL();
		controlItems();
	}	

	/**
	 * 사용자 홈에서 메시지 보기
	 */
	public MessagePopup(final String id, final int type, final long messageId) {
		instanceMap.put(id, this);
		this.messageId = messageId;
		
		init(type);
		createFormVL();
		createActHL();
		controlItems();
	} 
	
	/**
	 * 파일 첨부 메시지 전송
	 * */
	public MessagePopup(final String id, final int type, final long messageId, List<SDocument> list) {
		instanceMap.put(id, this);
		this.messageId = messageId;
		this.list = list;
		
		init(type);
		createFormVL();
		if(list.size()>0)
			initGrid();
		createActHL();
		controlItems();
	}	
	
	/**
	 *	첨부 파일 그리드 초기화 
	 */
	private void initGrid(){
		grid = new DocumentsGrid(true);
		grid.setGridData(list);
		grid.setWidth(576);
		grid.setHeight(120);
		grid.setMargin(5);
		main.addMember(grid);
	}

	// 기타 인터페이스 초기화 및 생성
	public void init(int type){
		Session.get().getUser().getName();

		Style = type;
		setWidth(590);
		if(Constants.MESSAGE_NOTICE==type){
			setHeight(380);
		}else if(Constants.MESSAGE_SEND==type){
			setHeight(350);
		}else if(Constants.MESSAGE_VIEW == type){
			setHeight(370);
		}else if(Constants.MESSAGE_CONTAIN_FILES == type){
			// 메시지 전송시 첨부파일 추가로 인한 변경
			if(list!=null && list.size()>0)	setHeight(450);
			else	setHeight(328);
		}
		
		setTitle((Constants.MESSAGE_SEND==type||Constants.MESSAGE_CONTAIN_FILES==type)?I18N.message("sendmessage"):I18N.message("second.viewmessage"));
		setAutoCenter(true);
		setShowMinimizeButton(false);
		setIsModal(true);
		setShowModalMask(true);
		
		initMain();
		show();
	}
	
	public void disableReply() {
		btnRefly.disable();
	}
	
	private void initMain(){
		main = new VLayout();
		main.setHeight100();
		main.setWidth100();
		main.setAlign(Alignment.CENTER);
		
		addItem(main);
	}
	
	private void controlItems(){
		if(Constants.MESSAGE_SEND != Style && Constants.MESSAGE_CONTAIN_FILES != Style){
			executeFetch();
		}else{
			richTextEditor.setDisabled(false);
			authorItem.hide();
			authorNameItem.hide();
			authorNameTempItem.hide();
			toIdItem.hide();
			selectToIdItem.hide();
			toNameTempItem.hide();
		}
	}
	
	/**
	 * 상세 Form 생성
	 * @return
	 */
	private void createFormVL() {
		HiddenItem IdItem = new HiddenItem("id");
		
		authorItem = new TextItem("author", I18N.message("senderId"));
		authorItem.setWidth("*");
		authorItem.setCanEdit(true);
		authorItem.setWrapTitle(false);
		
		authorNameItem = new TextItem("authorName", I18N.message("second.sendor"));
		authorNameItem.setWidth("*");
		authorNameItem.setCanEdit(true);
		authorNameItem.setWrapTitle(false);
		
		
		authorNameTempItem = new TextItem("authorNameTemp", I18N.message("second.sendor"));
		authorNameTempItem.setWidth("*");
		authorNameTempItem.setCanEdit(true);
		authorNameTempItem.setWrapTitle(false);
				
		selectToIdItem = new TextItem("selecttoId", I18N.message("Id"));
		selectToIdItem.setWidth("*");
		selectToIdItem.setCanEdit(true);
		selectToIdItem.setWrapTitle(false);
		        
		toIdItem = new TextItem("toId", I18N.message("recipients"));
		toIdItem.setWidth("*");
		toIdItem.setCanEdit(true);
		toIdItem.setWrapTitle(false);
		
		toNameTempItem = new TextItem("toNameTemp", I18N.message("recipients"));
		toNameTempItem.setWidth("*");
		toNameTempItem.setCanEdit(true);
		toNameTempItem.setWrapTitle(false);
		
        //사용자(삭제) = 전체
        userClearPicker = new PickerIcon(PickerIcon.CLEAR, new FormItemClickHandler() {   
            public void onFormItemClick(FormItemIconClickEvent event) {   
            	form.getField("selecttoId").clearValue();
            	form.getField("toId").clearValue();
            	form.getField("toName").clearValue();            	
            }   
        });
		
        //사용자(검색) = 지정
		userSearchPicker = new PickerIcon(PickerIcon.SEARCH, new FormItemClickHandler() {   
            public void onFormItemClick(FormItemIconClickEvent event) {  
            	final ReturnHandler returnOwnerHandler = new ReturnHandler() {
        			@Override
        			public void onReturn(Object param) {
        				String[] ownerInfo = (String[])param;
        				form.getField("selecttoId").setValue(ownerInfo[0]);
        				form.getField("toId").setValue(ownerInfo[1]);
        				form.getField("toName").setValue(ownerInfo[2]);
        				form.getField("toNameTemp").setValue(ownerInfo[3]);
        			}
        		};
        		
            	OwnerWindow ownerWindow = new OwnerWindow("multy", returnOwnerHandler, false);
				ownerWindow.show();
            }   
        });
		
		//사용자명
		toNameItem = new TextItem("toName", I18N.message("recipients"));
		if(Style ==Constants.MESSAGE_SEND || Style == Constants.MESSAGE_CONTAIN_FILES){//sent
			toNameItem.setWrapTitle(false);
			toNameItem.setCanEdit(false);
			toNameItem.setIcons(userClearPicker, userSearchPicker);
			toNameItem.setDisableIconsOnReadOnly(false);
			toNameItem.setWidth("*");
			toNameItem.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					final ReturnHandler returnOwnerHandler = new ReturnHandler() {
	        			@Override
	        			public void onReturn(Object param) {
	        				String[] ownerInfo = (String[])param;
	        				form.getField("selecttoId").setValue(ownerInfo[0]);
	        				form.getField("toId").setValue(ownerInfo[1]);
	        				form.getField("toName").setValue(ownerInfo[2]);
	        				form.getField("toNameTemp").setValue(ownerInfo[3]);
	        			}
	        		};
	        		
	            	OwnerWindow ownerWindow = new OwnerWindow("multy", returnOwnerHandler, false);
					ownerWindow.show();
					titleItem.focusInItem();
				}
			});
		}else{
			toNameItem.setWidth("*");
			toNameItem.setCanEdit(true);
			toNameItem.setWrapTitle(false);
		}
		toNameItem.setRequired(true);		
		
		titleItem = new TextItem("subject", I18N.message("title"));
		titleItem.setWidth("*");
		titleItem.setCanEdit(true);
		titleItem.setWrapTitle(false);
		titleItem.setRequired(true);	
//		titleItem.setLength(Constants.MAX_LEN_NAME);
		titleItem.setValidators(new LengthValidator(titleItem, Constants.MAX_LEN_NAME));
		
		authorItem.setStartRow(false);			authorItem.setEndRow(false);		
		authorNameItem.setStartRow(false);		authorNameItem.setEndRow(false);
		authorNameTempItem.setStartRow(false);	authorNameTempItem.setEndRow(false);
		
		selectToIdItem.setStartRow(false);		selectToIdItem.setEndRow(false);	
		toIdItem.setStartRow(false);			toIdItem.setEndRow(false);	
		toNameItem.setStartRow(false);			toNameItem.setEndRow(false);		
		toNameTempItem.setStartRow(false);		toNameTempItem.setEndRow(false);
		titleItem.setStartRow(false);			titleItem.setEndRow(false);		
		
		if(Style != Constants.MESSAGE_NOTICE){
			form = new DynamicForm();
			form.setWidth100();
			form.setItems(authorItem, authorNameItem, authorNameTempItem, selectToIdItem, toIdItem,toNameTempItem,toNameItem, IdItem, titleItem );
			form.reset();
		}
		
    	VLayout formVL = new VLayout();
    	formVL.setWidth100();
    	formVL.setAutoHeight();  
    	formVL.setMargin(5);
    	
    	editorHL = new HLayout();
		editorHL.setHeight100();
    	
    	if(Style != Constants.MESSAGE_NOTICE){
    		editorHL.addMembers(createEditor());
    		formVL.addMembers(form,editorHL);
    	}else{
    		editorHL.addMembers(createTextView());
    		formVL.addMembers(editorHL);
    	}
    	
    	
    	if(Style ==Constants.MESSAGE_VIEW){
    		authorNameItem.setDisabled(true);
    		authorNameTempItem.hide();
    		toNameItem.setDisabled(true);
    		titleItem.setDisabled(true);
    		selectToIdItem.hide();
    		authorItem.hide();
    		toIdItem.hide();
    		toNameTempItem.hide();
    	}
    	main.addMember(formVL);
	}
	
	/**
	 * editor 초기화
	 */
	private RichTextEditor createEditor(){
		SInfo info = Session.get().getInfo();
		richTextEditor = new RichTextEditor();  
        richTextEditor.setOverflow(Overflow.HIDDEN);  
        richTextEditor.setCanDragResize(true);  
        richTextEditor.setShowEdges(true);  
        richTextEditor.setStyleName("richTextEditor");
         if(Style == Constants.MESSAGE_NOTICE){
        	 richTextEditor.setHeight(300);
        }else{
        	 richTextEditor.setHeight(200);
        }
        richTextEditor.setDisabled(true);
        richTextEditor.setWidth100();
//        richTextEditor.setWidth(560);
        richTextEditor.setAlign(Alignment.CENTER);        
		return richTextEditor;
	}
	
	private Canvas createTextView(){
		SInfo info = Session.get().getInfo();
		textView = new Canvas();
		textView.setOverflow(Overflow.SCROLL);  
		textView.setCanDragResize(true);  
		textView.setShowEdges(true);  
        textView.setHeight(300);
        textView.setWidth100();
        textView.setAlign(Alignment.CENTER);      
        
        textView.setPadding(2);  
        textView.adjustForContent(true);
		return textView;
	}
	
	
	/**
	 * Action Panel 생성
	 * @return
	 */
	private void createActHL() {	
		btnEdiotrReset = new ButtonItem();
		btnEdiotrReset.setAlign(Alignment.LEFT);
		btnEdiotrReset.setTitle(I18N.message("second.ediotorclear"));
		btnEdiotrReset.setWidth(100);
		btnEdiotrReset.setStartRow(false);
		btnEdiotrReset.setEndRow(false);
		btnEdiotrReset.addClickHandler(new com.smartgwt.client.widgets.form.fields.events.ClickHandler(){
			@Override
			public void onClick(com.smartgwt.client.widgets.form.fields.events.ClickEvent event) {
				richTextEditor.destroy();
				editorHL.addMembers(createEditor());
				richTextEditor.setDisabled(false);
			}
        });
		
        
		btnRefly = new ButtonItem();
		btnRefly.setAlign(Alignment.LEFT);
		if(Style == Constants.MESSAGE_SEND || Style == Constants.MESSAGE_CONTAIN_FILES){
			btnRefly.setTitle(I18N.message("sendmessage"));
		}else if(Style == Constants.MESSAGE_VIEW){
			btnRefly.setTitle(I18N.message("resend"));
		}else{
			btnRefly.setTitle(I18N.message("second.replay"));
		}
		btnRefly.setWidth(100);
		btnRefly.setStartRow(false);
		btnRefly.setEndRow(false);
		btnRefly.addClickHandler(new com.smartgwt.client.widgets.form.fields.events.ClickHandler(){

			@Override
			public void onClick(
					com.smartgwt.client.widgets.form.fields.events.ClickEvent event) {
				if(I18N.message("second.replay").equals(btnRefly.getTitle()) || I18N.message("resend").equals(btnRefly.getTitle())
						|I18N.message("requestrepeat").equals(btnRefly.getTitle())){
					setHeight(350);
					
					setTitle(I18N.message("sendmessage"));
					btnRefly.setTitle(I18N.message("sendmessage"));
					richTextEditor.setDisabled(false);
					toNameItem.setCanEdit(false);
					toNameItem.setIcons(userClearPicker, userSearchPicker);
					toNameItem.setDisableIconsOnReadOnly(false);
					
					if(mgp==null || "Received".equals(mgp.threadType) || "Send".equals(mgp.threadType)){//탭이 Received 이면
						form.getField("subject").setValue("Re:"+form.getField("subject").getValue());
						form.getField("toId").setValue(form.getField("author").getValue());
						form.getField("toName").setValue(form.getField("authorName").getValue());
						form.getField("toNameTemp").setValue(form.getField("authorNameTemp").getValue());
					}
					form.getField("author").setValue(Session.get().getUser().getUserName());
					form.getField("authorName").setValue(Session.get().getUser().getName());
					
					authorNameItem.hide();
					toNameItem.setDisabled(false);
					titleItem.setDisabled(false);
					toNameItem.setWidth("*");
					titleItem.setWidth("*");
					
					btnForm1.show();
				}else if(I18N.message("sendmessage").equals(btnRefly.getTitle()) ){
					if(form.validate()){
						try {
							int editLength;
							editLength = richTextEditor.getValue().getBytes("UTF-8").length;
			            	Log.debug("NoticeGridPanel editLength["+editLength+"]");
							if(editLength < 2000){
								executeSend();
							}else{
	           				 SC.say(I18N.message("second.messagecharacterslimited",Integer.toString(editLength)));
	           			 }
						} catch (UnsupportedEncodingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
        });
		
		ButtonItem btnCancel = new ButtonItem();
        btnCancel.setAlign(Alignment.LEFT);
        btnCancel.setTitle(I18N.message("second.btnclose"));
        btnCancel.setWidth(100);
        btnCancel.setStartRow(false);
        btnCancel.setEndRow(false);
		btnCancel.addClickHandler(new com.smartgwt.client.widgets.form.fields.events.ClickHandler(){

			@Override
			public void onClick(
					com.smartgwt.client.widgets.form.fields.events.ClickEvent event) {
				// TODO Auto-generated method stub
				destroy();
			}
        	
        });
		
		btnForm1 = new DynamicForm();
		DynamicForm btnForm2= new DynamicForm();
		DynamicForm btnForm3 = new DynamicForm();
		
		btnForm1.setWidth(110);
		btnForm2.setWidth(110);
		btnForm3.setWidth(110);
		
		btnForm1.setItems(btnEdiotrReset);
		btnForm2.setItems(btnRefly);
		btnForm3.setItems(btnCancel);
		
		btnForm1.setAlign(Alignment.LEFT);
		btnForm2.setAlign(Alignment.LEFT);
		btnForm3.setAlign(Alignment.LEFT);
		
		
		if(Style != Constants.MESSAGE_SEND && Style != Constants.MESSAGE_CONTAIN_FILES )	btnForm1.hide();
		
		actionHL = new HLayout();
		actionHL.setHeight(1);
		if(Style == Constants.MESSAGE_VIEW||Style == Constants.MESSAGE_SEND || Style == Constants.MESSAGE_CONTAIN_FILES){
			actionHL.setMembers(btnForm1,btnForm2,btnForm3);	
		}else if(Style == Constants.MESSAGE_NOTICE){
			actionHL.setMembers(btnForm3);
		}
		
		actionHL.setMargin(5);
		main.addMember(actionHL);
	}
	
	/**
	 * 조회
	 */
	private void executeFetch()	{				
		Log.debug("[ MessagePopup executeFetch ] messageId["+ messageId + "]");
		
		service.getMessage(Session.get().getSid(), messageId, true, new AsyncCallback<SMessage>() {
			
			@Override
			public void onFailure(Throwable caught) {
				SCM.warn(caught);
				
			}
			@Override
			public void onSuccess(SMessage result) {
				Log.info(I18N.message("operationcompleted"),null);
				
				if(Style == Constants.MESSAGE_NOTICE){
					textView.setContents(result.getMessage());
				}else{
					String tempSeleteToId = "";
					String tempToId = "";
					String tempToName = "";
					String tempToNameTemp = "";
					form.getField("id").setValue(result.getId());
					form.getField("author").setValue(result.getAuthor());
					form.getField("authorName").setValue(result.getAuthor()+"("+result.getAuthorName()+")");
					form.getField("authorNameTemp").setValue(result.getAuthorName());
				
					// 사용자 홈에서 더블 클릭시 동작 수행하기 위해 수정
					if(mgp==null || "Received".equals(mgp.threadType)){
						form.getField("toId").setValue(Session.get().getUser().getUserName());
						form.getField("toName").setValue(Session.get().getUser().getUserName()+"("+Session.get().getUser().getName()+")");
						form.getField("toNameTemp").setValue(Session.get().getUser().getName());
						form.getField("selecttoId").setValue(result.getRecipients()[0].getSenderId());
						Session.get().checkMessage();
					}else if("Send".equals(mgp.threadType)){
						SRecipient[] sRecipient = result.getRecipients();
						for(int i=0;i<sRecipient.length; i++) {
							tempSeleteToId += ","+ sRecipient[i].getAddressId();
							tempToId += ","+ sRecipient[i].getAddress();
							tempToName += ","+ sRecipient[i].getAddress()+"("+sRecipient[i].getAddressName()+")";
							tempToNameTemp += ","+ sRecipient[i].getAddressName();
						}		
						
						form.getField("selecttoId").setValue(sRecipient.length ==0?"":tempSeleteToId.substring(1));
						form.getField("toId").setValue(sRecipient.length ==0?"":tempToId.substring(1));
						form.getField("toName").setValue(sRecipient.length ==0?"":tempToName.substring(1));
						form.getField("toNameTemp").setValue(sRecipient.length ==0?"":tempToNameTemp.substring(1));
					}
					form.getField("subject").setValue(result.getSubject());
					richTextEditor.setValue(result.getMessage());
				}
				
			}
			
		});
	}

	/**
	 * grid로 부터 docIds 획득 
	 * @return
	 */
	private long[] getDocIds(){
		ListGridRecord[] records = grid.getRecords();
		long[] ids = new long[records.length];
		
		int i=0;
		for (ListGridRecord record : records) {
			ids[i] = record.getAttributeAsLong("id");
			i++;
		}
		return ids;
	}
	
	/**
	 * message 전송
	 */
	private void executeSend() {
		Log.debug("[ MessagePopup executeSend ]");
		
		SMessage smessage = new SMessage();
		smessage.setType(1);
		smessage.setId(0L);
		smessage.setSubject(form.getValueAsString("subject"));
		smessage.setMessage(richTextEditor.getValue());
		smessage.setAuthor(Session.get().getUser().getUserName());
		
		// 20130902 taesu, 파일 전송부 추가
		if(list != null && list.size()>0){
			smessage.setDocIds(getDocIds());
		}
		
		String [] recipients = form.getValueAsString("toId").split(",");
		SRecipient[] sRecipientsArr = new SRecipient[recipients.length];
		SRecipient sRecipient;
		for(int i=0; i<recipients.length; i++){
			sRecipient = new SRecipient();
			sRecipient.setAddress(recipients[i]);
			sRecipientsArr[i] = sRecipient;
		}
		smessage.setRecipients(sRecipientsArr);
		
		if(Util.getSetting("setting.waiting")) Waiting.show(I18N.message("wattingforserver"));
		service.save(Session.get().getSid(), smessage, new AsyncCallbackWithStatus<SMessage>() {
			@Override
			public String getSuccessMessage() {
				Waiting.hide();
				return I18N.message("operationcompleted");
			}
			@Override
			public String getProcessMessage() {
				Waiting.hide();
				return null;
			}
			@Override
			public void onFailureEvent(Throwable caught) {
				Waiting.hide();
				SCM.warn(caught);
			}
			@Override
			public void onSuccessEvent(SMessage result) {
				Waiting.hide();
				Log.debug("[ MessagePopup executeSend ] onSuccess. id["+result.getId()+"]");
				if(mgp!=null){
					mgp.executeFetch();
					mgp.refresh();
					mgp.selectTabs();
				}
				SC.say(I18N.message("operationcompleted"));
            	destroy();
			}
		});
	}

	public void renameReply(String str) {
		btnRefly.setTitle(str);
	}
}
