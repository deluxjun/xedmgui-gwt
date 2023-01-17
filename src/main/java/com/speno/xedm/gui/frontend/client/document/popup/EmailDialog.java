package com.speno.xedm.gui.frontend.client.document.popup;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.HeaderControls;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.RichTextEditor;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.CloseClickEvent;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.ValuesManager;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.FormItemIcon;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ClickEvent;
import com.smartgwt.client.widgets.form.fields.events.ClickHandler;
import com.smartgwt.client.widgets.form.fields.events.FormItemClickHandler;
import com.smartgwt.client.widgets.form.fields.events.FormItemIconClickEvent;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.speno.xedm.core.service.serials.SDocument;
import com.speno.xedm.core.service.serials.SEmail;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.Session;
import com.speno.xedm.gui.common.client.log.Log;
import com.speno.xedm.gui.common.client.util.ItemFactory;
import com.speno.xedm.gui.common.client.util.ReturnHandler;
import com.speno.xedm.gui.common.client.util.ServiceUtil;
import com.speno.xedm.gui.common.client.window.Waiting;
import com.speno.xedm.gui.frontend.client.document.DocumentsGrid;
import com.speno.xedm.gui.frontend.client.folder.OwnerWindow;


/**
 * 
 * @author deluxjun
 *
 */
public class EmailDialog extends Window {
//	private long[] docIds;
	private DocumentsGrid grid;
	private String docTitle;
	private List<SDocument> list;
	
	private String reUser="";
	private String ccUser="";
	private RichTextEditor richTextEditor;

	private ValuesManager vm = new ValuesManager();

	public EmailDialog(List<SDocument> list, String docTitle){
		super();
		this.docTitle = docTitle;
		this.list = list;
		init();
	}
	
	public EmailDialog(long[] docIds, String docTitle) {
		super();
//		this.docIds = docIds;
		this.docTitle = docTitle;
		init();
	}
	
	private void init(){
		setHeaderControls(HeaderControls.HEADER_LABEL, HeaderControls.CLOSE_BUTTON);
		setTitle(I18N.message("sendmail"));
		setWidth(550);
		setCanDragResize(true);
		setIsModal(true);
		setShowModalMask(true);
		setPadding(5);
		setAutoSize(true);
		
		initTopForm();
		if(list.size() > 0){
			setHeight(500);
			initGrid();
		}else{
			setHeight(380);
		}
		initBottomForm();
		centerInPage();
	}
	
	private void initTopForm(){
		addCloseClickHandler(new CloseClickHandler() {
			@Override
			public void onCloseClick(CloseClickEvent event) {
				destroy();
			}
		});
		
		final DynamicForm form = new DynamicForm();
		form.setID("emailform");
		form.setValuesManager(vm);
		form.setWidth100();
		form.setMargin(5);
		form.setTitleOrientation(TitleOrientation.TOP);
		form.setNumCols(1);
		
		final TextItem recipients = ItemFactory.newEmailItem("recipients", "recipients", true);
		recipients.setWidth("*");
		recipients.setRequired(true);
		
		FormItemIcon recipientsSearch = ItemFactory.newItemIcon("owner.png");
		FormItemIcon recipientsDelete= ItemFactory.newItemIcon("delete.png");
		recipientsSearch.setPrompt(I18N.message("s.ownersearch"));
		recipientsDelete.setPrompt(I18N.message("s.ownerdelete"));
		
		recipients.setIcons(recipientsSearch, recipientsDelete);

//		PickerIcon userClearPicker;
//		PickerIcon userSearchPicker;	
//		
//		// TODO: 수신자 버튼에 따른 액션 구현. ; 으로 구분
//		// 버튼 들
//		userClearPicker = new PickerIcon(PickerIcon.CLEAR, new FormItemClickHandler() {   
//			public void onFormItemClick(FormItemIconClickEvent event) {   
//				form.getField("recipients").clearValue();
//			}   
//		});
//		
//		// 기안자 검색
//		userSearchPicker = new PickerIcon(PickerIcon.SEARCH, new FormItemClickHandler() {   
//			public void onFormItemClick(FormItemIconClickEvent event) {  
//				final ReturnHandler returnOwnerHandler = new ReturnHandler() {
//					@Override
//					public void onReturn(Object param) {
//						String[][] ownerInfos = (String[][])param;
//						String owners = "";
//						for (String[] str : ownerInfos) {
//							owners += "; "+str[1];
//						}
//						owners = owners.substring(1);
//						form.getField("recipients").setValue(owners);
////						String[] ownerInfo = (String[])param;
////						form.getField("recipients").setValue(ownerInfo[1]);
//					}
//				};
//				OwnerWindow ownerWindow = new OwnerWindow("single", returnOwnerHandler, false);
//				ownerWindow.show();
//			}   
//		});
//		recipients.setIcons(userClearPicker, userSearchPicker);
		
		final TextItem cc = ItemFactory.newEmailItem("cc", "cc", true);
		cc.setWidth("*");
		
		FormItemIcon ccSearch = ItemFactory.newItemIcon("owner.png");
		FormItemIcon ccDelete= ItemFactory.newItemIcon("delete.png");
		ccSearch.setPrompt(I18N.message("s.ownersearch"));
		ccDelete.setPrompt(I18N.message("s.ownerdelete"));
		
		cc.setIcons(ccSearch, ccDelete);
		
		TextItem subject = ItemFactory.newTextItem("subject", "subject", docTitle);
		subject.setRequired(true);
		subject.setWidth("*");
		
		richTextEditor = createEditor();
		richTextEditor.setTitle(I18N.message("message"));
		richTextEditor.setWidth(550);
		richTextEditor.setHeight(200);
		richTextEditor.setStyleName("richTextEditor");
//			final CheckboxItem ticket = new CheckboxItem();
//			ticket.setName("sendticket");
//			ticket.setTitle(I18N.message("sendticket"));
		
//			final CheckboxItem zip = new CheckboxItem();
//			zip.setName("zip");
//			zip.setTitle(I18N.message("zipattachments"));
		
		form.setFields(recipients, cc, subject);
		
		addItem(form);
		addItem(richTextEditor);
		
		recipientsSearch.addFormItemClickHandler(new FormItemClickHandler() {
			@Override
			public void onFormItemClick(FormItemIconClickEvent event) {
				final ReturnHandler returnOwnerHandler = new ReturnHandler() {
					@Override
					public void onReturn(Object param) {
						form.getField("recipients").clearValue();
						
						String[] infos = (String[])param;
						String[] name = infos[3].split(",");
						String[] email = infos[4].split(",");
						
						String owners = "";
						for(int i=0 ; i < name.length ; i ++){
							owners +="; "+name[i]+"("+email[i]+")";
						}
						if(reUser.equals("") || reUser == null)		owners = owners.substring(2);
						reUser = recipients.getValueAsString() + owners;
						reUser = reUser.replaceAll("null", "");
						form.getField("recipients").setValue(reUser);
					}
				};
				OwnerWindow ownerWindow = new OwnerWindow("multy", returnOwnerHandler, false);
				ownerWindow.show();
			}
		});
		
		recipientsDelete.addFormItemClickHandler(new FormItemClickHandler() {
			@Override
			public void onFormItemClick(FormItemIconClickEvent event) {
				reUser = "";
				form.getField("recipients").clearValue();
			}
		});
		
		ccSearch.addFormItemClickHandler(new FormItemClickHandler() {
			@Override
			public void onFormItemClick(FormItemIconClickEvent event) {
				final ReturnHandler returnOwnerHandler = new ReturnHandler() {
					@Override
					public void onReturn(Object param) {
						form.getField("cc").clearValue();
						
						String[] infos = (String[])param;
						String[] name = infos[3].split(",");
						String[] email = infos[4].split(",");
						
						String owners = "";
						for(int i=0 ; i < name.length ; i ++){
							owners +="; "+name[i]+"("+email[i]+")";
						}
						if(ccUser.equals("") || ccUser == null)		owners = owners.substring(2);
						ccUser = cc.getValueAsString() + owners;
						ccUser = ccUser.replaceAll("null", "");
						form.getField("cc").setValue(ccUser);
					}
				};
				OwnerWindow ownerWindow = new OwnerWindow("multy", returnOwnerHandler, false);
				ownerWindow.show();
			}
		});
		
		ccDelete.addFormItemClickHandler(new FormItemClickHandler() {
			@Override
			public void onFormItemClick(FormItemIconClickEvent event) {
				ccUser = "";
				form.getField("cc").clearValue();
			}
		});
	}
	
	private void initGrid(){
		grid = new DocumentsGrid(true);
		grid.setGridData(list);
		grid.setWidth(535);
		grid.setHeight(120);
		grid.setMargin(5);
		addItem(grid);
	}
	
	private SDocument[] getDocsInfo(){
		ListGridRecord[] records = grid.getRecords();
		SDocument[] documents = new SDocument[records.length];
		int i=0;
		for (ListGridRecord record : records) {
			documents[i] = (SDocument)record.getAttributeAsObject("document");
			i++;
		}	
		return documents;
	}

	/**
	 * Doc Grid에 있는 Doc들의 Id를 구한다.
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
	
	private void initBottomForm(){
		DynamicForm form = new DynamicForm();
		form.setMargin(5);
		ButtonItem sendItem = new ButtonItem();
		sendItem.setTitle(I18N.message("send"));
		sendItem.setAutoFit(true);
		sendItem.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				vm.validate();
				if (!vm.hasErrors()) {
					SEmail mail = new SEmail();
					mail.setRecipients(parsing(vm.getValueAsString("recipients")));
					mail.setCc(parsing(vm.getValueAsString("cc")));
					mail.setSubject(vm.getValueAsString("subject"));
					mail.setMessage(richTextEditor.getValue());
					// 현재는, 무조건 ticket 으로 전송
//						mail.setSendAsTicket("true".equals(vm.getValueAsString("sendticket")));
					mail.setSendAsTicket(true);
//						mail.setZipCompression("true".equals(vm.getValueAsString("zip")));
					if(list.size() > 0){
						mail.setDocIds(getDocIds());
						mail.setDocuments(getDocsInfo());
					}
					Waiting.show(I18N.message("nowsendingMail"));
					ServiceUtil.document().sendAsEmail(Session.get().getSid(), mail, new AsyncCallback<Void>() {
						@Override
						public void onFailure(Throwable caught) {
							Log.serverError(caught, true);
							Waiting.hide();
							destroy();
						}
						
						@Override
						public void onSuccess(Void result) {
							Log.info(I18N.message("messagesent"), I18N.message("documentcopysent"));
							SC.say(I18N.message("operationcompleted"));
							Waiting.hide();
							destroy();
						}
					});
				}
			}
		});
		form.setFields(sendItem);
		addItem(form);
	}
	
	/**
	 * Email Parsing Owner를 통해 입력한 값과, 직접 입력한 값을 Parsing한다.
	 * @param str
	 * @return
	 */
	private String parsing(String str){
		if(str == null)	return "";
		String[] temp = str.split(";");
		String sendData = "";
		
		for (String tem : temp) {
			String data = "";
			if(tem.contains("(")){
				data = tem.split("[(]")[1];
				data = data.split("[)]")[0];
			}else
				data = tem;
			sendData += ";" + data;
		}
		sendData = sendData.substring(1);
		return sendData;
	}
	
	private RichTextEditor createEditor(){
		richTextEditor = new RichTextEditor();  
        richTextEditor.setOverflow(Overflow.HIDDEN);  
        richTextEditor.setCanDragResize(true);  
        richTextEditor.setShowEdges(true);  
        richTextEditor.setHeight(200);
        richTextEditor.setDisabled(true);
        richTextEditor.setWidth100();
        richTextEditor.setAlign(Alignment.CENTER);
        richTextEditor.setDisabled(false);
		return richTextEditor;
	}
}