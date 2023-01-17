package com.speno.xedm.gui.frontend.client.shared;


import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.CheckboxItem;
import com.smartgwt.client.widgets.form.fields.FormItemIcon;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ClickEvent;
import com.smartgwt.client.widgets.form.fields.events.ClickHandler;
import com.smartgwt.client.widgets.form.fields.events.FormItemClickHandler;
import com.smartgwt.client.widgets.form.fields.events.FormItemIconClickEvent;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.speno.xedm.core.service.serials.SFolder;
import com.speno.xedm.core.service.serials.SShare;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.Session;
import com.speno.xedm.gui.common.client.log.Log;
import com.speno.xedm.gui.common.client.util.ItemFactory;
import com.speno.xedm.gui.common.client.util.LengthValidator;
import com.speno.xedm.gui.common.client.util.ReturnHandler;
import com.speno.xedm.gui.common.client.util.SearchUtil;
import com.speno.xedm.gui.common.client.util.ServiceUtil;
import com.speno.xedm.gui.frontend.client.document.popup.FolderSelectorDialog;
import com.speno.xedm.gui.frontend.client.folder.OwnerWindow;

public class FolderSharingPanel extends VLayout{
//	public static FolderSharingWindow instance;
	
	// Form Items
	private TextItem folderText;
	private FormItemIcon folderSearch;
	private TextItem commentText;
	private TextItem ownerText;
	private FormItemIcon ownerSearch;
//	private FormItemIcon ownerDelete;
	
	private CheckboxItem readCheck;
	private CheckboxItem writeCheck;
	private CheckboxItem deleteCheck;
	private CheckboxItem includeCheck;
	
	private ButtonItem addNewButton;
	private ButtonItem saveButton;
	
	// 선택되어있는 폴더정보 저장
	private SFolder folder;
	private String[] ownerInfo = new String[2];
	
	private SShare share;
	
	private boolean isPopup = false;
	
	private Canvas parent;
	/**
	 * 	FolderSharing Window에 선택된 문서의 SFolder를 전달하고 창을 띄운다
	 * 	20130819 taesu
	 * */
	public FolderSharingPanel(SFolder folder, Canvas parent){
		this.parent = parent;
		this.folder = folder;
		this.isPopup = true;
		init();
	}

	public FolderSharingPanel(){
		init();
	}
	
	private void init(){
		setAutoWidth();
		setAutoHeight();
		
		// Window 초기화
		initTop();
		initBottom();
		initActions();
	}
	/*
	 *	설정 조건이 들어가는 Top Form init 
	 * */
	private void initTop(){
		HLayout top = new HLayout();
		
		DynamicForm topLeftForm = new DynamicForm();
		topLeftForm.setWidth(200);
		topLeftForm.setNumCols(2);
		topLeftForm.setColWidths("1","1");

		DynamicForm topRightForm = new DynamicForm();
		topRightForm.setNumCols(4);
		topRightForm.setColWidths("1","1","1","1");
		topRightForm.setGroupTitle(I18N.message("options"));
		topRightForm.setIsGroup(true);
		
		// folderText
		folderText 	= new TextItem("folder", I18N.message("folder"));
		folderText.setCanEdit(false);
		folderText.setCanFocus(false);
		folderText.setDisableIconsOnReadOnly(false);
		folderSearch = ItemFactory.newItemIcon("folder.png");
		folderSearch.setPrompt(I18N.message("s.foldersearch"));
		folderText.setIcons(folderSearch);
		SearchUtil.initItem(folderText, 130, Alignment.LEFT);
		if(folder != null){
			folderText.setValue(folder.getPathExtended().replaceAll("/root/", ""));
		}
		
		// ownerText		
		ownerText = new TextItem("owner", I18N.message("target"));
		ownerText.setCanEdit(false);
		ownerText.setCanFocus(false);
		ownerText.setDisableIconsOnReadOnly(false);
		ownerSearch = ItemFactory.newItemIcon("owner.png");
//		ownerDelete= ItemFactory.newItemIcon("delete.png");
		ownerSearch.setPrompt(I18N.message("s.ownersearch"));
//		ownerDelete.setPrompt(I18N.message("s.ownerdelete"));
		ownerText.setIcons(ownerSearch);
//		ownerText.setIcons(ownerSearch, ownerDelete);
		ownerText.setCanEdit(false);
		ownerText.setCanFocus(false);
		ownerText.setDisableIconsOnReadOnly(false);
		SearchUtil.initItem(ownerText, 130, Alignment.LEFT);
		
		// commentText
		commentText = new TextItem("comment", I18N.message("comment"));
		commentText.setStartRow(true);
		SearchUtil.initItem(commentText, 130, Alignment.LEFT);
//		commentText.setLength(Session.get().getInfo().getIntConfig("gui.comment.fieldsize", 1000));
		commentText.setValidators(new LengthValidator(commentText, Session.get().getInfo().getIntConfig("gui.comment.fieldsize", 1000)));
		
		readCheck 	= new CheckboxItem("read", I18N.message("read"));
		writeCheck 	= new CheckboxItem("write", I18N.message("write"));
		deleteCheck = new CheckboxItem("sdelete", I18N.message("delete"));
		includeCheck= new CheckboxItem("include", I18N.message("includesubdirectories"));

		readCheck.setStartRow(false);
		readCheck.setEndRow(false);
		writeCheck.setStartRow(false);
		writeCheck.setEndRow(false);
		deleteCheck.setStartRow(false);
		deleteCheck.setEndRow(false);
		includeCheck.setStartRow(false);
		includeCheck.setEndRow(false);
		
		readCheck.setValue(true);
		readCheck.setCanEdit(false);
		
		topLeftForm.setItems(
				folderText, 
				ownerText,
				commentText
				);
		
		topRightForm.setItems(
				readCheck, writeCheck, 
				deleteCheck, includeCheck
				);
		top.addMembers(topLeftForm, topRightForm);
		addMember(top);
	}
	
	/**
	 * Button Form init
	 * */
	private void initBottom(){
		DynamicForm bottomForm = new DynamicForm();
		bottomForm.setNumCols(3);
		bottomForm.setColWidths("1","1","*");
		bottomForm.setLeft(20);
		
		addNewButton = new ButtonItem("addNew", I18N.message("addnew"));
		addNewButton.setIcon(ItemFactory.newImgIcon("page_white_add.png").getSrc());
		saveButton = new ButtonItem("save", I18N.message("save"));
		saveButton.setIcon(ItemFactory.newImgIcon("data_into.png").getSrc());
		
		addNewButton.setEndRow(false);
		saveButton.setStartRow(false);
		// Form 구성 
		bottomForm.setItems(addNewButton, saveButton);
		addMember(bottomForm);
	}
	
	/**
	 * AddNew Button 선택시 아이템 리셋
	 * */
	private void reset(){
		folderText.setValue("");
		commentText.setValue("");
		ownerText.setValue("");

		folder = null;
		ownerInfo = new String[2];
		share = null;
		
		readCheck.setValue(false);
		writeCheck.setValue(false);
		deleteCheck.setValue(false);
		includeCheck.setValue(false);
		
		
		//2013-11-25 나용준 
		//대시보드 공유에서 폴더공유시 읽기 옵션은 반드시 체크되어야 함.
		if(!readCheck.getCanEdit()) readCheck.setValue(true);
	}
	
	/**
	 * 체크 상태에 따라 저장할 share 값을 자동으로 세팅한다.
	 * @param share
	 */
	private void setDataByCheckItem(SShare share){
		if(readCheck.getValueAsBoolean() == true)	share.setRead(1);
		else	share.setRead(0);

		if(writeCheck.getValueAsBoolean() == true)	share.setWrite(1);
		else	share.setWrite(0);
		
		if(deleteCheck.getValueAsBoolean() == true)	share.setDelete(1);
		else	share.setDelete(0);
		
		if(includeCheck.getValueAsBoolean() == true) share.setIncludeSubDir(1);
		else	share.setIncludeSubDir(0);
	}
	
	/**
	 * 선택된 Grid의 값을 Item들에 Set한다
	 * @param record
	 */
	public void setData(ListGridRecord record){
		// folder, owner, comment, check(4개)
		SShare share = (SShare) record.getAttributeAsObject("share");
		this.folder = new SFolder();
		this.folder.setId(share.getFolderId());
		this.ownerInfo[0] = String.valueOf(share.getGroupId());
		this.share = share;
		
		folderText.setValue(share.getFolderName());
		ownerText.setValue(share.getGroupName());
		commentText.setValue(share.getComment());
		
		if(share.getRead() == 1)	readCheck.setValue(true);
		else	readCheck.setValue(false);

		if(share.getWrite() == 1)	writeCheck.setValue(true);
		else	writeCheck.setValue(false);
		
		if(share.getDelete() == 1)	deleteCheck.setValue(true);
		else	deleteCheck.setValue(false);
		
		if(share.getIncludeSubDir() == 1)	includeCheck.setValue(true);
		else	includeCheck.setValue(false);
	}
	
	
	/*
	 * Folder Sharing Window의 모든 Action 설정
	 */
	@SuppressWarnings("rawtypes")
	private void initActions(){
		final ReturnHandler returnFolderHandler = new ReturnHandler() {
			@Override
			public void onReturn(Object param) {
				SFolder selectedFolder = (SFolder)param;
				if(selectedFolder.getName().equals(I18N.message("mydoc"))){
					SC.warn(I18N.message("rootisnotallowedto"));
				}else{
					folderText.setValue(selectedFolder.getPathExtended().replaceAll("/root/", ""));
					folder = selectedFolder;
				}
			}
		};
		final ReturnHandler returnOwnerHandler = new ReturnHandler() {
			@Override
			public void onReturn(Object param) {
 				if (param instanceof String[][]) {
     				String[][] lownerInfo = (String[][])param;
     				if (lownerInfo != null && lownerInfo.length > 0) {
         				ownerInfo = lownerInfo[0];
     				}
 				} else if (param instanceof String[]) {		// singleGroup으로 선택된 경우..
 					ownerInfo = (String[])param;
 				}
				// 자신 선택 불가 동작
				if(ownerInfo != null && ownerInfo.length > 2){
					if(String.valueOf(Session.get().getUser().getId()).equals(ownerInfo[2])){
						SC.warn(I18N.message("cantaddyourself") );
					}else{
						ownerText.setValue(ownerInfo[1]);
					}
				}else{
					ownerText.setValue(ownerInfo[1]);
				}
			}
		};
		folderText.addClickHandler(new ClickHandler() {
			@SuppressWarnings("unchecked")
			@Override
			public void onClick(ClickEvent event) {
				FolderSelectorDialog selectPath = new FolderSelectorDialog();
				selectPath.setReturnHandler(returnFolderHandler);
				selectPath.showPersonal();
			}
		});
		// 폴더 선택 Action
		folderSearch.addFormItemClickHandler(new FormItemClickHandler() {
			@SuppressWarnings("unchecked")
			@Override
			public void onFormItemClick(FormItemIconClickEvent event) {
				FolderSelectorDialog selectPath = new FolderSelectorDialog();
				selectPath.setReturnHandler(returnFolderHandler);
				selectPath.showPersonal();
			}
		});
		// 추가 Action
		addNewButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				reset();
			}
		});
		// 저장 Action
		saveButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				// 값이 있을경우 Grid에서 선택한 경우 이므로 제거 후 추가
				if(share != null){
					ServiceUtil.folder().deleteSharing(Session.get().getSid(), share.getId(), new AsyncCallback<Void>() {
						@Override
						public void onSuccess(Void result) {
							saveSharing();
						}
						@Override
						public void onFailure(Throwable caught) {
							Log.serverError(caught, false);
						}
					});
				}
				// share 값이 없을 경우 바로 추가
				else
					saveSharing();
			}
		});
		// owner 검색 조건 Action
		ownerText.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				OwnerWindow ownerWindow = new OwnerWindow("singleGroup", returnOwnerHandler, false);
				ownerWindow.show();
			}
		});
		ownerSearch.addFormItemClickHandler(new FormItemClickHandler() {
			@Override
			public void onFormItemClick(FormItemIconClickEvent event) {
				OwnerWindow ownerWindow = new OwnerWindow("singleGroup", returnOwnerHandler, false);
				ownerWindow.show();
			}
		});
//		ownerDelete.addFormItemClickHandler(new FormItemClickHandler() {
//			@Override
//			public void onFormItemClick(FormItemIconClickEvent event) {
//				ownerText.setValue("");
//				ownerInfo = new String[2];
//			}
//		});
	}
	
	/**
	 * 공유 제거
	 */
	public void stopSharing(long id){
		ServiceUtil.folder().deleteSharing(Session.get().getSid(), id, new AsyncCallback<Void>() {
			@Override
			public void onSuccess(Void result) {
				Log.info(I18N.message("successdelete"), I18N.message("successdelete"));
				if(isPopup){
					SC.say(I18N.message("successSharing"));
					parent.destroy();
				}else{
					PersonalSharedPanel.get().execute();
					reset();
				}
			}
			@Override
			public void onFailure(Throwable caught) {
				Log.serverError(caught, false);
			}
		});
	}
	
	/**
	 * 공유 추가
	 */
	private void saveSharing(){
		SShare share = new SShare();
		share.setUserId(Session.get().getUser().getId());
		share.setFolderId(folder.getId());
		share.setGroupId(ownerInfo[0]);
		share.setComment(commentText.getValueAsString());
		setDataByCheckItem(share);

		ServiceUtil.folder().saveSharing(Session.get().getSid(), share, new AsyncCallback<Void>() {
			@Override
			public void onSuccess(Void result) {
				if(isPopup){
					SC.say(I18N.message("successSharing"));
					parent.destroy();
				}else{
					PersonalSharedPanel.get().execute();
					reset();
				}
				Log.info(I18N.message("successSharing"), I18N.message("successSharing"));
			}
			@Override
			public void onFailure(Throwable caught) {
				Log.serverError(caught, false);
			}
		});
	}
//	public static FolderSharingWindow get(){
//		if(instance==null)
//			instance = new FolderSharingWindow();
//		return instance;
//	}
}
