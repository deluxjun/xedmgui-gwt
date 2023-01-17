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
	// 상단
	private SelectItem draftType;
	private StaticTextItem reasonItem;
	
	// 기안자 정보
	private StaticTextItem es_rewriterInfoItem; 
	private ListGrid approveUserListGrid;
	private TransferImgButton upArrow, downArrow;
	// 문서 정보
	private ListGrid approveDocListGrid;
	// comment
	private RichTextEditor comment;
	
	private String rewriterGroup;
	// 현재 선택되어있는 문서정보 저장
	private SDocument document;
	// 부모 Canvas 저장(부모 Destroy용)
	private Canvas parent;
	// Draft Type 저장
	private LinkedHashMap<Integer, String> map = new LinkedHashMap<Integer, String>();
	
	/*
	 * user가 문서 선택 후 기안 등록시 기안 type을 생성으로 변경 후 생성 Tap에서 Dotype을 변경하고 기안 type을 변경했을 경우
	 * 선택되어있던 문서의 DocType정보를 재 요청하지 않기 위해서 사용한 데이터 임시 저장용 변수
	 */
	private String save_rewriterGroup;
	private String save_rewriterInfo;
	
	private SGroup[] rewriterGroups;
	
//	private ButtonItem owner;
	
	// 기안 권한들
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
	 * Main Layout 초기화
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
		// 기안 종류
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
	 * String형의 DraftType을 Map으로 변환하여 SelectItem에 저장한다.
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
		// 선택된 문서가 없고 등록 권한도 없을경우 등록 기안이 가능해야한다.
		else{
			map.put(Constants.DRAFT_TYPE_REGISTRATION, I18N.message("event.stored"));
			draftType.setDefaultToFirstOption(true);
		}
		draftType.setValueMap(map);
	}

	private String reason ="";
	private void makeCannotDraftReason(){
		HTML html = new HTML();
		// 선택된 문서가 없을경우 생성만 가능
		reason += "<font color='gray'>";
		reason +="test";
		if(document == null){
			reason += "선택된 문서가 없음";
		}
		reasonItem.setValue(reason);
		reason += "</font>";
		
		html.setHTML(reason);
		
	}
	
	/**
	 *	결재자 지정 
	 */
	private void initApproveUserList(){
		// 결재자 지정
		HLayout gridLayout = new HLayout();
		gridLayout.setHeight(100);
		gridLayout.setWidth100();
		gridLayout.setMargin(5);
		gridLayout.setAlign(Alignment.CENTER);
		// 결재자 Grid
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
	 * 기안 문서
	 */
	private void initApproveDocList(){
		if(document != null){
			//기안 문서
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
		// 내용
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
					//20150509na GS인증을 위한 제약
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
	 * 결재자 Grid 우측 아이콘(생성, 위치 변경
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
	 * 그리드 row 데이타 up,down 동작
	 */
	private void changeGridRow(String arrow){
		ListGridRecord[] moveRecords = approveUserListGrid.getSelectedRecords();//선택 레코드
    	int selectRowNum = approveUserListGrid.getRecordIndex(approveUserListGrid.getSelectedRecord());//선택 레코드 rownum	
    	List<ListGridRecord> temp = new ArrayList<ListGridRecord>();	//new sort 레코드
    	ListGridRecord[] orgRecords = approveUserListGrid.getRecords();			//original sort 레코드
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
					temp.add(moveRecords[0]);//선택 record
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
					temp.add(moveRecords[0]);//선택 record
				}else{
					temp.add(orgRecords[j]);
				}
			}
		}
    	approveUserListGrid.setData(new ListGridRecord[0]); //초기화
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
	 * Doc List Grid의 Record를 Set한다.
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
	 * 승인자 지정 대상 Grid에 Records를 Set한다.
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
		
		//20131220na 결재순를 넣기 위해 i를 넣음
		for (String[] str : data) {
			// 자기 자신 선택 validation
			if(Session.get().getUser().getId().equals(str[0])){
				SC.warn(I18N.message("draftrequestmember") + I18N.message("cantaddyourself", str[0]));
				return;
			}
			// 동일유저가 존재 validation
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
	 * 유저 그리드 데이터 초기화
	 * */
	public void resetGridRecords(){
		approveUserListGrid.setRecords(new ListGridRecord[]{});
	}
	
	/**
	 * Item 초기화
	 */
	public void resetItems(){
		// 대상 초기화는 버튼 선택으로됨
//		approveUserListGrid.setRecords(new ListGridRecord[]{});
//		approveDocListGrid.setRecords(new ListGridRecord[]{});
		comment.setValue("");
	}
	
	// 20140319, junsoo, 사용자 선택 팝업 -> 함수로 분리
	private void showOwnerWindow(){
		// 대상 지정 Actions
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
	 *	액션 지정 
	 */
	private void initActions(){
		// 20140319, junsoo, 팀명 클릭해도 선택창 띄우기
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
		
		// Draft Type이 등록의 경우 등록 Tab을 보여주며, 모든 아이템을 초기화한다.
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
	 * Draft Type에 따라 Tab과 해당하는 Item의 값들을 변화시킨다.
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
	 * DocType의 정보와, Group정보를 가져와 세팅한다.
	 * 문서 생성 기안시 DocType 변경시 호출하기 위해 public 처리
	 * @param docTypeId
	 */
	public void getDocTypeInfo(long docTypeId){
		ServiceUtil.documentcode().getSDocType(Session.get().getSid(), docTypeId, new AsyncCallback<SDocType>() {
			@Override
			public void onSuccess(SDocType result) {
				rewriterGroup = result.getRewriteGroup();
				// 최초 호출시에만 저장. (선택되어있는 문서에 대한 정보를 저장한다)
				//20140109 na 문서형식이 변경될때마다 바뀌어야 함.
//				if(save_rewriterGroup == null){
					save_rewriterGroup = rewriterGroup;
//				}
				// 필수 기안자 그룹 목록이 없을경우 동작
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
				
				// 기안 필수자 명단을 보여준다.(id 값들을 이름으로 보여주기 위함)
				ServiceUtil.security().findByIds(Session.get().getSid(), ids, new AsyncCallback<SGroup[]>() {
					@Override
					public void onSuccess(SGroup[] result) {
						if(result.length >0){
							// 20140319, junsoo, rewriter 그룹 backup
							rewriterGroups = result;
							
							String rewriterInfo = "";
							for (SGroup group : result) {
								rewriterInfo += ", ";
								rewriterInfo += group.getName();
							}
							rewriterInfo = rewriterInfo.substring(2);
							es_rewriterInfoItem.setValue(rewriterInfo);
							// 최초 호출시에만 저장.
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
	 * 기안 등록 validation
	 * @return
	 */
	public boolean validation(){
		ListGridRecord[] records = approveUserListGrid.getRecords();
		String lastGroupId;
		// Draft Type 선택 검사 
		if(draftType.getValueAsString() == null){
			SC.warn(I18N.message("selectDraftType"));
			return false;
		}

		// 기안 가능 부서 검사
		if(es_rewriterInfoItem.getValue().equals(I18N.message("noRewritersInfo"))){
			SC.warn(I18N.message("noRewritersInfo"));
			return false;
		}

		// 기안 대상자 개수 검사
		if(records.length == 0){
			SC.warn(I18N.message("youMustSelectDraftTargetAtLeastOne"));
			return false;
		}else
			lastGroupId = records[records.length-1].getAttribute("groupId");
		
		// 20140218, junsoo, 서버에서 체크하므로 모두 주석처리함. 클라이언트에서는 두 부서에 모두 귀속되는 유저를 체크할 수 없으므로
//		// 기안 필수자 포함 검사
//		// 20140116na 오류 수정
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

		//20150509na GS인증을 위한 커멘트 제약
		try {
			// Comment
			byte[] strByte = comment.getValue().getBytes("UTF-8");
			String str = comment.getValue();
			if(comment.getValue().replaceAll("<br>", "").trim().length() == 0){	// 값 입력없이 값 출력할 경우 "<br>"이 붙어있음.
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
