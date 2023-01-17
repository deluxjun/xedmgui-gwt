package com.speno.xedm.gui.frontend.client.document.popup;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.RichTextEditor;
import com.smartgwt.client.widgets.TransferImgButton;
import com.smartgwt.client.widgets.events.KeyPressEvent;
import com.smartgwt.client.widgets.events.KeyPressHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.FormItemIcon;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.form.fields.events.ClickEvent;
import com.smartgwt.client.widgets.form.fields.events.ClickHandler;
import com.smartgwt.client.widgets.form.fields.events.IconClickEvent;
import com.smartgwt.client.widgets.form.fields.events.IconClickHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.RemoveRecordClickEvent;
import com.smartgwt.client.widgets.grid.events.RemoveRecordClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.speno.xedm.core.service.serials.SDocType;
import com.speno.xedm.core.service.serials.SDocument;
import com.speno.xedm.core.service.serials.SGroup;
import com.speno.xedm.gui.common.client.Constants;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.Session;
import com.speno.xedm.gui.common.client.log.Log;
import com.speno.xedm.gui.common.client.util.ItemFactory;
import com.speno.xedm.gui.common.client.util.ReturnHandler;
import com.speno.xedm.gui.common.client.util.ServiceUtil;
import com.speno.xedm.gui.common.client.util.Util;
import com.speno.xedm.gui.frontend.client.document.DocumentsPanel;
import com.speno.xedm.gui.frontend.client.folder.OwnerWindow;

public class ApproveRequestPanel extends VLayout{
	// ���
	private SelectItem draftType;
	private StaticTextItem reasonItem;
	
	// ����� ����
	private StaticTextItem es_rewriterInfoItem; 
	private ListGrid approveUserListGrid;
	private TransferImgButton upArrow, downArrow;
	// ���� ����
	private ListGrid approveDocListGrid;
	// comment
	private RichTextEditor comment;
	
	private String rewriterGroup;
	// ���� ���õǾ��ִ� �������� ����
	private SDocument document;
	// �θ� Canvas ����(�θ� Destroy��)
	private Canvas parent;
	// Draft Type ����
	private LinkedHashMap<Integer, String> map = new LinkedHashMap<Integer, String>();
	
	/*
	 * user�� ���� ���� �� ��� ��Ͻ� ��� type�� �������� ���� �� ���� Tap���� Dotype�� �����ϰ� ��� type�� �������� ���
	 * ���õǾ��ִ� ������ DocType������ �� ��û���� �ʱ� ���ؼ� ����� ������ �ӽ� ����� ����
	 */
	private String save_rewriterGroup;
	private String save_rewriterInfo;
	
	private SGroup[] rewriterGroups;
	
//	private ButtonItem owner;
	
	// ��� ���ѵ�
	private String rights;
	
	public ApproveRequestPanel(SDocument document, Canvas parent, String rights){
		this.parent = parent;
		this.document = document;
		this.rights = rights;
		setWidth100();
		setHeight100();
		setAlign(Alignment.CENTER);
		setPadding(10);		
		initRequestLayout();
		initActions();
	}
	
	/**
	 * Main Layout �ʱ�ȭ
	 * */
	private void initRequestLayout(){
		if(document != null)
			getDocTypeInfo(document.getDocType());
		initTopForm();
		initApproveUserList();
		initCommentArea();
		initApproveDocList();
	}
	
	/**
	 * Draft Type
	 */
	private void initTopForm(){
		// ��� ����
		DynamicForm form = new DynamicForm();
		draftType = new SelectItem("draftType", I18N.message("draftType"));
		draftType.setWrapTitle(false);
		reasonItem = new StaticTextItem("reason");
		reasonItem.setShowTitle(false);
		
		FormItemIcon icon = new FormItemIcon();  
        icon.setSrc(ItemFactory.newImgIcon("owner.png").getSrc());
		es_rewriterInfoItem = new StaticTextItem("es_rewriterInfo", I18N.message("esRewriterInfo"));
		es_rewriterInfoItem.setIcons(icon);
		es_rewriterInfoItem.setIconPrompt(I18N.message("selecttarget"));
		
//		owner = new ButtonItem("selecttarget", I18N.message("approvalchoice"));
//		owner.setIcon(ItemFactory.newImgIcon("owner.png").getSrc());
//		owner.setShowFocused(false);  
//		owner.setWidth("100");
//		owner.setStartRow(false);
		
//		ListGridField field = new ListGridField("list");
//		field.setShowTitle(false);
//		draftType.setPickListFields(field);
		makeDraftStringToMap();
		
		form.setItems(
				draftType, reasonItem,
				es_rewriterInfoItem
				);
		form.setNumCols(4);
		draftType.setEndRow(false);				draftType.setEndRow(false);
		reasonItem.setStartRow(false);			reasonItem.setEndRow(true);
		es_rewriterInfoItem.setStartRow(false);	es_rewriterInfoItem.setEndRow(true);
		
		addMember(form);
	}
	
	/**
	 * String���� DraftType�� Map���� ��ȯ�Ͽ� SelectItem�� �����Ѵ�.
	 */
	private void makeDraftStringToMap(){
		if(document != null){
			if(rights.contains(String.valueOf(Constants.DRAFT_TYPE_REGISTRATION))){
				map.put(Constants.DRAFT_TYPE_REGISTRATION, I18N.message("event.stored"));
			}
			if(rights.contains(String.valueOf(Constants.DRAFT_TYPE_DELETE))){
				map.put(Constants.DRAFT_TYPE_DELETE, I18N.message("event.deleted"));
			}
			if(rights.contains(String.valueOf(Constants.DRAFT_TYPE_DOWNLOAD))){
				map.put(Constants.DRAFT_TYPE_DOWNLOAD, I18N.message("download"));
			}
			if(rights.contains(String.valueOf(Constants.DRAFT_TYPE_CHECKOUT))){
				map.put(Constants.DRAFT_TYPE_CHECKOUT, I18N.message("checkout"));
			}
			if(rights.contains(String.valueOf(Constants.DRAFT_TYPE_CHECKIN))){
				map.put(Constants.DRAFT_TYPE_CHECKIN, I18N.message("checkin"));
			}
		}
		// ���õ� ������ ���� ��� ���ѵ� ������� ��� ����� �����ؾ��Ѵ�.
		else{
			map.put(Constants.DRAFT_TYPE_REGISTRATION, I18N.message("event.stored"));
			draftType.setDefaultToFirstOption(true);
		}
		draftType.setValueMap(map);
	}

	private String reason ="";
	private void makeCannotDraftReason(){
		HTML html = new HTML();
		// ���õ� ������ ������� ������ ����
		reason += "<font color='gray'>";
		reason +="test";
		if(document == null){
			reason += "���õ� ������ ����";
		}
		reasonItem.setValue(reason);
		reason += "</font>";
		
		html.setHTML(reason);
		
	}
	
	/**
	 *	������ ���� 
	 */
	private void initApproveUserList(){
		// ������ ����
		HLayout gridLayout = new HLayout();
		gridLayout.setHeight(100);
		gridLayout.setWidth100();
		gridLayout.setMargin(5);
		gridLayout.setAlign(Alignment.CENTER);
		// ������ Grid
		approveUserListGrid = new ListGrid();
		approveUserListGrid.setHeight(100);
		approveUserListGrid.setWidth("95%");
		approveUserListGrid.setCanRemoveRecords(true);
		ListGridField approvalOrder = new ListGridField("approvalOrder", I18N.message("approvalOrder"));
		ListGridField userDepart = new ListGridField("userDepart", I18N.message("department"));
		ListGridField userName = new ListGridField("userName", I18N.message("name"));
		ListGridField userId = new ListGridField("userId");
		ListGridField user = new ListGridField("user", I18N.message("id"));
		userId.setHidden(true);
		approvalOrder.setWidth(70);
		approvalOrder.setAlign(Alignment.CENTER);
		makeFieldCenter(userDepart, userName, userId, user);
		
		approveUserListGrid.setFields(approvalOrder, userDepart, userName, userId, user);
		approveUserListGrid.addRemoveRecordClickHandler(new RemoveRecordClickHandler() {
			@Override
			public void onRemoveRecordClick(RemoveRecordClickEvent event) {
				// TODO Auto-generated method stub
				changeGridRow(event.getRowNum());
			}
		});
		
		gridLayout.addMember(approveUserListGrid);
		gridLayout.addMember(arrowPanel());
		
		addMember(gridLayout);
	}
	
	/**
	 * ��� ����
	 */
	private void initApproveDocList(){
		if(document != null){
			//��� ����
			approveDocListGrid = new ListGrid();
			approveDocListGrid.setAlign(Alignment.CENTER);
			approveDocListGrid.setHeight(60);
			approveDocListGrid.setWidth("99%");
			approveDocListGrid.setMargin(5);
			
			ListGridField docName = new ListGridField("docName", I18N.message("title"));
			docName.setAlign(Alignment.CENTER);
			ListGridField createdDate = new ListGridField("createdDate", I18N.message("createddate"));
			ListGridField docType = new ListGridField("docType", I18N.message("doctype"));
			ListGridField expiredDate = new ListGridField("expiredDate", I18N.message("s.expiredate"));
			makeFieldCenter(docName, createdDate, docType, expiredDate);
			
			approveDocListGrid.setFields(docName, createdDate, docType, expiredDate);
			
			setDocListGridRecord(document);
			
			addMember(approveDocListGrid);
		}
	}
	
	private void makeFieldCenter(ListGridField... field){
		for (ListGridField listGridField : field) {
			listGridField.setAlign(Alignment.CENTER);
		}
	}
	
	/**
	 * Comment
	 */
	private void initCommentArea(){
		// ����
		comment = new RichTextEditor();
		comment.setOverflow(Overflow.HIDDEN);  
		comment.setCanDragResize(true);  
		comment.setShowEdges(true); 
		comment.setHeight(200);
		comment.setWidth100();
		comment.setAlign(Alignment.CENTER);
		comment.setMargin(5);
		comment.setStyleName("richTextEditor");
		
		comment.addKeyPressHandler(new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent event) {
				try {
					//20150509na GS������ ���� ����
					// Comment
					byte[] strByte = comment.getValue().getBytes("UTF-8");
					String str = comment.getValue();
					if(strByte.length > Constants.MAX_LEN_DESC){
						String message = "";
						message += I18N.message("exceedMessage");
						message += "<br>(" +I18N.message("current")+": "+ strByte.length+ 
											"bytes / "+I18N.message("max")+": " + Constants.MAX_LEN_DESC + "bytes)";
						SC.warn(message);
						comment.setValue(str = Util.strCut(str, Constants.MAX_LEN_DESC));
					}
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
		});
		
		addMember(comment); 
	}
	
	/**
	 * ������ Grid ���� ������(����, ��ġ ����
	 */
	private VLayout arrowPanel(){

		upArrow = new TransferImgButton(TransferImgButton.UP, new com.smartgwt.client.widgets.events.ClickHandler() {
			@Override
			public void onClick(com.smartgwt.client.widgets.events.ClickEvent event) {
				changeGridRow("up");
			}
		});
        
		downArrow = new TransferImgButton(TransferImgButton.DOWN, new com.smartgwt.client.widgets.events.ClickHandler() {
			@Override
			public void onClick(com.smartgwt.client.widgets.events.ClickEvent event) {
				changeGridRow("down");  
			}
		});
		
		upArrow.enable();
		downArrow.enable();

		VLayout arrowPanel = new VLayout();
		
        arrowPanel.setWidth("10");
        arrowPanel.setHeight100();
        arrowPanel.setAlign(Alignment.CENTER);
        arrowPanel.setMembersMargin(Constants.ARROW_MARGIN);
        arrowPanel.setMembers( upArrow, downArrow);
        return arrowPanel;
	}
	
	/**
	 * �׸��� row ����Ÿ up,down ����
	 */
	private void changeGridRow(String arrow){
		ListGridRecord[] moveRecords = approveUserListGrid.getSelectedRecords();//���� ���ڵ�
    	int selectRowNum = approveUserListGrid.getRecordIndex(approveUserListGrid.getSelectedRecord());//���� ���ڵ� rownum	
    	List<ListGridRecord> temp = new ArrayList<ListGridRecord>();	//new sort ���ڵ�
    	ListGridRecord[] orgRecords = approveUserListGrid.getRecords();			//original sort ���ڵ�
    	int approvalOrder;
    	
    	int totCnt = orgRecords.length;
    	
    	if(totCnt ==1)return;
    	
    	if("up".equals(arrow)){//up
    		if(selectRowNum == 0)return;
    	}
    	if("down".equals(arrow)){//down
    		if(selectRowNum == totCnt-1)return;
    	}
    	
    	for(int j=0; j<orgRecords.length; j++) {
			if("up".equals(arrow)){//up
				if(j== selectRowNum-1){
					approvalOrder = moveRecords[0].getAttributeAsInt("approvalOrder");
					moveRecords[0].setAttribute("approvalOrder", approvalOrder-1);
					temp.add(moveRecords[0]);//���� record
				}else if(j == selectRowNum){
					approvalOrder = orgRecords[selectRowNum-1].getAttributeAsInt("approvalOrder");
					orgRecords[selectRowNum-1].setAttribute("approvalOrder", approvalOrder+1);
					temp.add(orgRecords[selectRowNum-1]);
				}else{
					temp.add(orgRecords[j]);
				}
			}
			
			if("down".equals(arrow)){//down
				if(j == selectRowNum){
					approvalOrder = orgRecords[selectRowNum+1].getAttributeAsInt("approvalOrder");
					orgRecords[selectRowNum+1].setAttribute("approvalOrder", approvalOrder-1);
					temp.add(orgRecords[selectRowNum+1]);
				}else if(j== selectRowNum+1){
					approvalOrder = moveRecords[0].getAttributeAsInt("approvalOrder");
					moveRecords[0].setAttribute("approvalOrder", approvalOrder+1);
					temp.add(moveRecords[0]);//���� record
				}else{
					temp.add(orgRecords[j]);
				}
			}
		}
    	approveUserListGrid.setData(new ListGridRecord[0]); //�ʱ�ȭ
    	for(int i=0; i<temp.size(); i++){
    		approveUserListGrid.addData((ListGridRecord)temp.get(i));
    	}			
    	approveUserListGrid.selectSingleRecord("up".equals(arrow)?selectRowNum-1:selectRowNum+1);
	}
	
	private void changeGridRow(int rowNum){
		ListGridRecord[] orgRecords = approveUserListGrid.getRecords();
		for (int i = rowNum; i < orgRecords.length; i++) {
			orgRecords[i].setAttribute("approvalOrder", i);
		}
	}
	
	/**
	 * Doc List Grid�� Record�� Set�Ѵ�.
	 * @param document
	 */
	private void setDocListGridRecord(SDocument document){
		ListGridRecord record = new ListGridRecord();
		record.setAttribute("docName", document.getTitle());
		record.setAttribute("createdDate", Util.getFormattedDate(document.getCreationDate(), false));
		record.setAttribute("docType", document.getDocTypeName());
		record.setAttribute("expiredDate",Util.getFormattedExpireDate(document.getExpireDate(), false));
		record.setAttribute("docTypeId", document.getDocType());
		approveDocListGrid.setData(new ListGridRecord[]{record});
	}
	
	/**
	 * ������ ���� ��� Grid�� Records�� Set�Ѵ�.
	 * @param data
	 */
	private void setUserListGridRecords(String[][] data){
		String ids = null;
		ListGridRecord[] records = approveUserListGrid.getRecords();
		if(records.length > 0){
			for (ListGridRecord record : records) {
				ids += record.getAttribute("userId") + ","; 
			}
		}
		
		//20131220na ������� �ֱ� ���� i�� ����
		for (String[] str : data) {
			// �ڱ� �ڽ� ���� validation
			if(Session.get().getUser().getId().equals(str[0])){
				SC.warn(I18N.message("draftrequestmember") + I18N.message("cantaddyourself", str[0]));
				return;
			}
			// ���������� ���� validation
			if(ids != null && ids.contains(str[0])){
				SC.warn(I18N.message("sameuseriscontained"));
				return;
			}
			
			ListGridRecord record = new ListGridRecord();
			record.setAttribute("approvalOrder", approveUserListGrid.getRecords().length + 1);
			record.setAttribute("user", str[3]);
			record.setAttribute("userDepart", str[2]);
			record.setAttribute("userName", str[1]);
			record.setAttribute("userId", str[0]);
			record.setAttribute("groupId", str[4]);
			approveUserListGrid.addData(record);
			
			
		}
	}
	
	/**
	 * ���� �׸��� ������ �ʱ�ȭ
	 * */
	public void resetGridRecords(){
		approveUserListGrid.setRecords(new ListGridRecord[]{});
	}
	
	/**
	 * Item �ʱ�ȭ
	 */
	public void resetItems(){
		// ��� �ʱ�ȭ�� ��ư �������ε�
//		approveUserListGrid.setRecords(new ListGridRecord[]{});
//		approveDocListGrid.setRecords(new ListGridRecord[]{});
		comment.setValue("");
	}
	
	// 20140319, junsoo, ����� ���� �˾� -> �Լ��� �и�
	private void showOwnerWindow(){
		// ��� ���� Actions
		final ReturnHandler returnOwnerHandler = new ReturnHandler() {
			@Override
			public void onReturn(Object param) {
				String[][] data = (String[][])param;
				setUserListGridRecords(data);
			}
		};

		OwnerWindow ownerWindow = new OwnerWindow(rewriterGroups,"single", returnOwnerHandler, true, null);
		ownerWindow.show();
	}
	
	/**
	 *	�׼� ���� 
	 */
	private void initActions(){
		// 20140319, junsoo, ���� Ŭ���ص� ����â ����
		es_rewriterInfoItem.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				showOwnerWindow();
			}
		});
		es_rewriterInfoItem.addIconClickHandler(new IconClickHandler() {
			@Override
			public void onIconClick(IconClickEvent event) {
				showOwnerWindow();
			}
		});
		
		// Draft Type�� ����� ��� ��� Tab�� �����ָ�, ��� �������� �ʱ�ȭ�Ѵ�.
		draftType.addChangedHandler(new ChangedHandler() {
			@Override
			public void onChanged(ChangedEvent event) {
				int selectedValue = Integer.parseInt(draftType.getValueAsString());
				if(selectedValue == Constants.DRAFT_TYPE_REGISTRATION){
					resetItems();
					controlTabByDraftType(true);
				}else{
//					rewriterGroup = save_rewriterInfo;
					if(save_rewriterInfo == null){
						es_rewriterInfoItem.setValue(I18N.message("noRewritersInfo"));
					}else{
						es_rewriterInfoItem.setValue(save_rewriterInfo);
					}
					controlTabByDraftType(false);
				}
			}
		});
	}
	
	/**
	 * Draft Type�� ���� Tab�� �ش��ϴ� Item�� ������ ��ȭ��Ų��.
	 * @param isRegistration
	 */
	private void controlTabByDraftType(boolean isRegistration){
		if(isRegistration){
			if(approveDocListGrid!=null)
				approveDocListGrid.hide();
		}
		else approveDocListGrid.show();
		
		if(DocumentsPanel.get()!=null){
			DocumentsPanel.get().getApprovePopup().controlTabByDraftType(isRegistration);
		}
	}

	/**
	 * DocType�� ������, Group������ ������ �����Ѵ�.
	 * ���� ���� ��Ƚ� DocType ����� ȣ���ϱ� ���� public ó��
	 * @param docTypeId
	 */
	public void getDocTypeInfo(long docTypeId){
		ServiceUtil.documentcode().getSDocType(Session.get().getSid(), docTypeId, new AsyncCallback<SDocType>() {
			@Override
			public void onSuccess(SDocType result) {
				rewriterGroup = result.getRewriteGroup();
				// ���� ȣ��ÿ��� ����. (���õǾ��ִ� ������ ���� ������ �����Ѵ�)
				//20140109 na ���������� ����ɶ����� �ٲ��� ��.
//				if(save_rewriterGroup == null){
					save_rewriterGroup = rewriterGroup;
//				}
				// �ʼ� ����� �׷� ����� ������� ����
				if(rewriterGroup == null){
					es_rewriterInfoItem.setValue(I18N.message("noRewritersInfo"));
//					save_rewriterInfo = I18N.message("noRewritersInfo");
					return;
				}
				String[] groups = rewriterGroup.split("\\,");
				String[] ids = new String[groups.length];
				int i=0;
				for (String group : groups) {
					ids[i] = group;
					i++;
				}
				
				// ��� �ʼ��� ����� �����ش�.(id ������ �̸����� �����ֱ� ����)
				ServiceUtil.security().findByIds(Session.get().getSid(), ids, new AsyncCallback<SGroup[]>() {
					@Override
					public void onSuccess(SGroup[] result) {
						if(result.length >0){
							// 20140319, junsoo, rewriter �׷� backup
							rewriterGroups = result;
							
							String rewriterInfo = "";
							for (SGroup group : result) {
								rewriterInfo += ", ";
								rewriterInfo += group.getName();
							}
							rewriterInfo = rewriterInfo.substring(2);
							es_rewriterInfoItem.setValue(rewriterInfo);
							// ���� ȣ��ÿ��� ����.
							if(save_rewriterInfo == null)
								save_rewriterInfo = rewriterInfo;
						}
					}
					@Override
					public void onFailure(Throwable caught) {
						Log.serverError(caught, false);
					}
				});
			}
			@Override
			public void onFailure(Throwable caught) {
				Log.serverError(caught, false);
			}
		});
	}
	
	/**
	 * ��� ��� validation
	 * @return
	 */
	public boolean validation(){
		ListGridRecord[] records = approveUserListGrid.getRecords();
		String lastGroupId;
		// Draft Type ���� �˻� 
		if(draftType.getValueAsString() == null){
			SC.warn(I18N.message("selectDraftType"));
			return false;
		}

		// ��� ���� �μ� �˻�
		if(es_rewriterInfoItem.getValue().equals(I18N.message("noRewritersInfo"))){
			SC.warn(I18N.message("noRewritersInfo"));
			return false;
		}

		// ��� ����� ���� �˻�
		if(records.length == 0){
			SC.warn(I18N.message("youMustSelectDraftTargetAtLeastOne"));
			return false;
		}else
			lastGroupId = records[records.length-1].getAttribute("groupId");
		
		// 20140218, junsoo, �������� üũ�ϹǷ� ��� �ּ�ó����. Ŭ���̾�Ʈ������ �� �μ��� ��� �ͼӵǴ� ������ üũ�� �� �����Ƿ�
//		// ��� �ʼ��� ���� �˻�
//		// 20140116na ���� ����
//		StringTokenizer token = new StringTokenizer(save_rewriterGroup, ",");
//		boolean areGroup[] = new boolean[token.getLength()];
//		for (int i = 0; i < areGroup.length; i++) {
//			areGroup[i] = false;
//		}
//		
//		while (token.hasMoreElements()) {
//			String str = token.nextToken();
//			for (int i = 0; i < records.length; i++) {
//				String strRecord = records[i].getAttribute("groupId");
//				if(str.equals(strRecord)){
//					areGroup[token.getIndex() -1] = true;
//					break;
//				}
//			}
//		}
//		
//		for (int i = 0; i < areGroup.length; i++) {
//			if(areGroup[i] == false){
//				SC.warn(I18N.message("error.IncorrectFinalApprover"));
//				return false;
//			}
//		}

		//20150509na GS������ ���� Ŀ��Ʈ ����
		try {
			// Comment
			byte[] strByte = comment.getValue().getBytes("UTF-8");
			String str = comment.getValue();
			if(comment.getValue().replaceAll("<br>", "").trim().length() == 0){	// �� �Է¾��� �� ����� ��� "<br>"�� �پ�����.
				SC.warn(I18N.message("youMustWriteDraftReason"));
				return false;
			}
			else if(strByte.length > Constants.MAX_LEN_DESC){
				String message = "";
				message += I18N.message("exceedMessage");
				message += "<br>(" +I18N.message("current")+": "+ strByte.length+ 
									"bytes / "+I18N.message("max")+": " + Constants.MAX_LEN_DESC + "bytes)";
				SC.warn(message);
				comment.setValue(str = Util.strCut(str, Constants.MAX_LEN_DESC));
				return false;
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return true;
	}
	
	/*
	 * Getter
	 * */
	public SelectItem getDraftType() {
		return draftType;
	}

	public ListGrid getApproveUserListGrid() {
		return approveUserListGrid;
	}

	public ListGrid getApproveDocListGrid() {
		return approveDocListGrid;
	}

	public RichTextEditor getComment() {
		return comment;
	}
}
