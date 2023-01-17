package com.speno.xedm.gui.frontend.client.document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.widgets.form.fields.CanvasItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.menu.MenuItem;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;
import com.speno.xedm.core.service.serials.SCode;
import com.speno.xedm.core.service.serials.SContent;
import com.speno.xedm.core.service.serials.SDocument;
import com.speno.xedm.core.service.serials.SFileType;
import com.speno.xedm.core.service.serials.SFolder;
import com.speno.xedm.core.service.serials.SRecordItem;
import com.speno.xedm.core.service.serials.SRewrite;
import com.speno.xedm.gui.common.client.Constants;
import com.speno.xedm.gui.common.client.FolderObserver;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.Session;
import com.speno.xedm.gui.common.client.log.Log;
import com.speno.xedm.gui.common.client.util.InstanceHandler;
import com.speno.xedm.gui.common.client.util.ReturnHandler;
import com.speno.xedm.gui.common.client.util.SearchUtil;
import com.speno.xedm.gui.common.client.util.ServiceUtil;
import com.speno.xedm.gui.common.client.util.Util;
import com.speno.xedm.gui.frontend.client.clipboard.Clipboard;
import com.speno.xedm.gwt.service.DocumentCodeService;
import com.speno.xedm.gwt.service.DocumentCodeServiceAsync;

/**
 * documents 의 context 메뉴 및 toolbar 컨트롤 클래스
 * type에 따라 전체 툴바 및 컨텍스트메뉴는 이 클래스를 사용할 것.
 * 
 * Menu 추가 요령
 * 1. TYPE 상수 추가
 * 2. createAction 으로 항목 추가
 * 3. setActivatedMenuType 으로 메뉴 이동시 마다 현재 메뉴를 알려주기
 * 4. get().getToolbar(), get().getContextMenu() 로 현재 메뉴의 툴바 및 컨텍스트 획득
 * 
 * Modified:
 * 		20130816, junsoo, extend 권한이 있어야만 패기 및 복구 가능
 * 
 * @author deluxjun
 *
 */
public class DocumentActionUtil implements FolderObserver, DocumentObserver {
	public final static int TYPE_SHARED = 0;
	public final static int TYPE_MYDOC = 1;
	public final static int TYPE_XVARM = 2;
	public final static int TYPE_TRASH = 3;
	public final static int TYPE_FAVOR = 4;
	public final static int TYPE_EXPIRED = 6;
	public final static int TYPE_CHECKED = 7;
	public final static int TYPE_FILE = 8;
	public final static int TYPE_SHARED_TRASH = 9;
	public final static int TYPE_MESSAGES = 10;
	
	public final static int TYPE_APPROVE_STANDBY = 11;
	public final static int TYPE_APPROVE_REQUEST = 12;
	public final static int TYPE_APPROVE_COMPLETE = 13;
	public final static int TYPE_APPROVE_ALL = 14;			// goodbong
	
	public final static int TYPE_SEARCH_PERSONAL = 5;
	public final static int TYPE_SEARCH_SHARED = 15;
	public final static int TYPE_SEARCH_SAEVD = 16;
	public final static int TYPE_SEARCH_ECM = 17;
	public final static int TYPE_SEARCH_FOLDERS= 20;

	public final static int TYPE_FOLDER_SHARING = 18;
	public final static int TYPE_FOLDER_SHARED = 19;
	public final static int TYPE_BEFORE_SEARCH=20;
	
	private static DocumentActionUtil instance;

	private List<String> actionList = new ArrayList<String>();
	private Map<String, DocumentAction> actions = new HashMap<String, DocumentAction>();

	// 20140107, junsoo, canvasitems
	private Map<String, List<CanvasItem>> canvasItems = new HashMap<String, List<CanvasItem>>();
	
	private Map<Integer, List<MenuItem>> contextMenus = new HashMap<Integer, List<MenuItem>>();
	private Map<Integer, ToolStrip> toolbars = new HashMap<Integer, ToolStrip>();

	// 현재 상태
	private SFolder currentFolder;
	private SDocument[] currentDocuments;
	private SFolder[] currentFolderItems;
	
	private SRewrite[] currentReriteItems;
	private SRecordItem[] currentItems;	
	private int activatedMenuType = TYPE_MYDOC;
	
	private ToolStrip toolbar;
	// 썸네일 아이콘 선택상태 유무
	private boolean isThumbnail = false;
	
	// 20130903 taesu, 권한 저장
	private List<String> rights = new ArrayList<String>();
	// 20130904, taesu, Draft권한 저장 추가
	private LinkedHashMap<Integer, String> DraftMap = new LinkedHashMap<Integer, String>();
	
	// 20130814 taesu, 검색 상태 추가
//	private boolean isSearching = false;
	// Sorter SelectItem 저장용
	private Map<Integer, SelectItem> sorterMap = new HashMap<Integer, SelectItem>();
	// Draft Icon Control용
//	private boolean enableDraftIcon = false;
	
	private Object[] actionParameters;
	
	private static DocumentCodeServiceAsync documentCodeService = (DocumentCodeServiceAsync) GWT.create(DocumentCodeService.class);
	List<SFileType> fileTypes;
	
	public SDocument[] getCurrentDocuments() {
		return currentDocuments;
	}
	
	public SFolder[] getCurrentFolderItems() {
		return currentFolderItems;
	}

	public SRecordItem[] getCurrentItems() {
		return currentItems;
	}

	public void setCurrentItems(SRecordItem[] items) {
		this.currentItems = items;

		currentDocuments = null;
		currentFolderItems = null;
		currentReriteItems = null;
		
		// save to documents
		if (items != null && items.length > 0) {
			List<SDocument> docList = new ArrayList<SDocument>();
			for (SRecordItem item : items) {
				if (item.getDocument() != null)
					docList.add(item.getDocument());
			}
			if (docList.size() > 0)
				currentDocuments = docList.toArray(new SDocument[0]);
			
			List<SFolder> folderList = new ArrayList<SFolder>();
			for (SRecordItem item : items) {
				if (item.getFolder() != null)
					folderList.add(item.getFolder());
			}
			if (folderList.size() > 0)
				currentFolderItems = folderList.toArray(new SFolder[0]);
			
			List<SRewrite> rewriteList = new ArrayList<SRewrite>();
			for (SRecordItem item : items) {
				if (item.getRewrite() != null)
					rewriteList.add(item.getRewrite());
			}
			if (rewriteList.size() > 0)
				currentReriteItems = rewriteList.toArray(new SRewrite[0]);
		}
	}

	public DocumentActionUtil() {
		Session.get().addFolderObserver(this);
		Session.get().addDocumentObserver(this);
		
		documentCodeService.getFileTypes(Session.get().getSid(), new AsyncCallback<List<SFileType>>() {
			@Override
			public void onSuccess(List<SFileType> result) {
				DocumentActionUtil.get().fileTypes = result;
			}
			
			@Override
			public void onFailure(Throwable caught) {
			}
		});
		
		
	}
	
	public static DocumentActionUtil get() {
		if (instance == null) {
			instance = new DocumentActionUtil();
		}
		return instance;
	}
	
	public int getActivatedMenuType() {
		return activatedMenuType;
	}

	public void setActivatedMenuType(int activatedMenuType) {
//		// menu가 바뀌면 속성 창 닫기
//		if (this.activatedMenuType != activatedMenuType)
//			DocumentPropertiesWindow.get().close();
		
		this.activatedMenuType = activatedMenuType;

		// 20140211, junsoo, 소트콤보박스 갱신
		if(sorterMap != null && sorterMap.get(getActivatedMenuType()) != null)
			sorterMap.get(getActivatedMenuType()).setValue(Constants.ORDER_BY_MODIFIEDDATE_DESC);

	}
	
	// 20140211, junsoo, sorter 획득
	public String getCurrentSorter() {
		if(sorterMap != null && sorterMap.get(getActivatedMenuType()) != null)
			return sorterMap.get(getActivatedMenuType()).getValueAsString();
		
		return Constants.ORDER_BY_MODIFIEDDATE_DESC;
	}

	public Object[] getActionParameters() {
		return actionParameters;
	}

	public void setActionParameters(Object[] actionParameters) {
		this.actionParameters = actionParameters;
	}

	// disable action
	public void disable(String id) {
		disable(id, "");
	}
	public void disable(String id, String comment) {
		enable(id, false, comment);
	}
	
	// enable action
	public void enable(String id) {
		enable(id, true);
	}
	
	public void disableAll(){
		for (String id : actionList) {
			enable(id, false);
		}
	}
	
	// apply all enable flag
	private void applyAll(){
		for (String id : actionList) {
			actions.get(id).applyEnabled();
		}
	}

	private void enableAll(){
		for (String id : actionList) {
			enable(id, true);
		}
	}

	private void enable(String id, boolean flag){
		enable(id, flag, "");
	}
	private void enable(String id, boolean flag, String comment){
		DocumentAction action = actions.get(id);
		if (action != null) {
			action.addTitle(comment);
			action.setEnabled(flag);
		}
		
		// 20140107, junsoo, canvasItems
		List<CanvasItem> items = canvasItems.get(id);
		if (items != null) {
			for (CanvasItem i : items) {
				if (flag)
					i.show();
				else
					i.hide();
			}
		}
	}

	/**
	 *	Filter Icon을 활성화/비활성화 상태로 변환한다.
	 *	@param id
	 *	@param isWorking : 활성/비활성화 유무
	 * */
//	public void changeActionIcon(String id, boolean isWorking){
//		DocumentAction action = actions.get(id);
//		action.changeButtonIcon(id, isWorking);
//	}
	public void chageIcon(String id,String iconName)
	{
		DocumentAction action = actions.get(id);
		action.setIcon(iconName);
	}
	
	/**
	 *  get context menu
	 * */
	public Menu getContextMenuAddSecurity(int type, boolean isSecurity) {
		
		Menu menu = new Menu();
		List<MenuItem> items = contextMenus.get(type);
		
		// 20131128 na 대시보드의 공유는 제목을 추가 안함. 
		if(TYPE_FOLDER_SHARING  == type) ;
		else if (TYPE_FILE != type && TYPE_APPROVE_STANDBY != type && TYPE_APPROVE_REQUEST != type && TYPE_APPROVE_COMPLETE != type && TYPE_APPROVE_ALL != type) {
			if (currentDocuments != null) {
				MenuItem titleItem = new MenuItem();
				titleItem.setEnabled(false);
				String title = "";
				try {
					title = Util.strCut(currentDocuments[0].getTitle(), 16, "...");
				} catch (Exception e) {
					Log.debug("Util.getStringLimit :" + e.getMessage());
					title = currentDocuments[0].getTitle();
				}

				if (currentDocuments.length == 1 && currentDocuments[0].getLockUserId() != null){
					if(currentDocuments[0].getStatus() == SDocument.DOC_LOCKED){
						title += "-" + I18N.message("second.statusLocked") + ":" + currentDocuments[0].getLockUserName();
					}else if(currentDocuments[0].getStatus() == SDocument.DOC_CHECKED_OUT){
						title += "-" + I18N.message("second.statusCheckedout") + ":" + currentDocuments[0].getLockUserName();
					}
					titleItem.setTitle(title);
				} else if (currentDocuments.length > 1) {
					title +=  "(" + I18N.message("second.client.and") + " " + Integer.toString(currentDocuments.length) + ")";
					titleItem.setTitle(title);
				}
				titleItem.setTitle(title);
				menu.addItem(titleItem);
			}
		}
		
		
		
		
		if (items != null)
			for (MenuItem menuItem : items) {
//				if(isSecurity && menuItem.getAttribute("id").equals("security"))
//					menuItem.setChecked(isSecurity);
				menu.addItem(menuItem);
			}
		return menu;
	}
	/**
	 *  get context menu
	 * */
	public Menu getContextMenu(int type) {
		return getContextMenuAddSecurity(type, false);
	}
	
	/**
	 *  컨텍스트 메뉴 리턴.
	 * */
	public Menu getContextMenu() {
		return getContextMenu(getActivatedMenuType());
	}
	
	/**
	 * 문서 level 보안이 적용되어있는 contextMenu 리턴
	 * @param isSecurity
	 * @return
	 */
	public Menu getContextMenu(boolean isSecurity){
		return getContextMenuAddSecurity(getActivatedMenuType(), isSecurity);
	}
	
	/**
	 * 	특정 ContextMenu를 생성함.
	 * 	20130821 taesu
	 * */
	public Menu getPersonalSearchContextMenu(){
		return getContextMenu(TYPE_SEARCH_PERSONAL);
	}
	public Menu getSharedSearchContextMenu(){
		return getContextMenu(TYPE_SEARCH_SHARED);
	}
	public Menu getSharingContextMenu(){
		return getContextMenu(TYPE_FOLDER_SHARING);
	}
	public Menu getECMContextMenu(){
		return getContextMenu(TYPE_SEARCH_ECM);
	}
	
	/**
	 *  get toolbar
	 * */
	public ToolStrip getToolbar(int type) {
		return toolbars.get(type);
	}
	
	//EDM,ECM색인검색-검색이전에 toolbar활성화 막음 hyewon
	public void updateSearch(int type){
		enableAll();
		switch (type) {
		case TYPE_BEFORE_SEARCH:
		disable	("goto_search");
		disable	("properties_search");
		disable	("download_search");
		break;
		case TYPE_SEARCH_ECM:
    	disable	("download_search");
			break;
		}
		applyAll();
	}
	

	/**
	 * 20131210 na 툴바 아이콘 비활성화 ETS용
	 * 현재는 checkout만 사용
	 * 액션 활성/비활성화 갱신 
	 * */
	public void updateETS(int type) {
		// 0. enable all buttons
		enableAll();
		
		switch (type) {
		case TYPE_CHECKED:
			disable	("download");
			disable	("checkin");
			disable	("checkout");
			disable	("lock");
			disable	("unlock");
			disable	("properties");
			disable	("favorites");
			disable	("cancelcheckout");
			break;
		case TYPE_TRASH:
		case TYPE_SHARED_TRASH:
			disable	("restore");
			disable	("expire");
			disable	("properties");
			break;
		case TYPE_FAVOR:
			disable	("delete_bookmark");
			disable	("goto");
			disable	("properties");
			break;
		case TYPE_EXPIRED:
			disable	("expire_restore");
			disable	("properties");
			break;
		case TYPE_APPROVE_STANDBY:
		case TYPE_APPROVE_REQUEST:
		case TYPE_APPROVE_COMPLETE:
			disable	("approve_request");
			disable ("approve_detail");
			disable ("approve_properties");
			disable("approve_goTo");
			disable("approve_download");
			disable("approve_redraft");
			break;
		}
		
		// apply enabled
		applyAll();
	}
	
	
	
	/**
	 * 액션 활성/비활성화 갱신
	 * */
	public void update(SFolder folder, SRecordItem[] items) {
		Log.debug("Actions update is called : " + folder + "," + items);
		
		if (items != null) {
			for (SRecordItem s : items) {
				Log.debug("" + s.getType());
			}
		}
		setCurrentItems(items);
		// 0. enable all buttons
		enableAll();
		
		// 승인 문서함 동작
		if(currentReriteItems != null){
			for (SRewrite rewrite : currentReriteItems) {
				if(rewrite.getCommand() != Constants.REWRITE_COMMAND_DOWNLOAD){
					disable("approve_download");
					
				}
				
				// 승인 완료
				if(rewrite.getStatus() == Constants.REWRITE_STATUS_COMPLETE_APPROVAL){
					if(rewrite.getCommand() == Constants.REWRITE_COMMAND_DELETE){
						disable("approve_goTo", 		" (" + I18N.message("second.nofile") + ")");
						disable("approveManager_goTo", 	" (" + I18N.message("second.nofile") + ")");
						
					}
					disable("approve_redraft");
				}
				// 회수 완료
				else if(rewrite.getStatus() == Constants.REWRITE_STATUS_COMPLETE_RETURN){
					if(rewrite.getCommand() == Constants.REWRITE_COMMAND_DOWNLOAD){
						disable("approve_download");
					}
					if(rewrite.getCommand() == Constants.REWRITE_COMMAND_REGISTRATION){
						disable("approve_goTo", 		" (" + I18N.message("second.nofile") + ")");	// 20140218, junsoo, 회수일 경우 goto할 수 없음
						disable("approve_download");
						disable("approve_redraft");
					}
				}
				// 반려 완료
				else if(rewrite.getStatus() == Constants.REWRITE_STATUS_COMTLETE_RECOVERY){
					if(rewrite.getCommand() == Constants.REWRITE_COMMAND_DOWNLOAD){
						disable("approve_download");
					}
					if(rewrite.getCommand() == Constants.REWRITE_COMMAND_REGISTRATION){
						disable("approve_goTo", 		" (" + I18N.message("second.nofile") + ")");	// 20140218, junsoo, 반려일 경우 goto할 수 없음
						disable("approve_download");
						disable("approve_redraft");
					}
				}
				// 진행 중
				else if(rewrite.getStatus() == Constants.REWRITE_STATUS_PROGRESS){
					if(rewrite.getCommand() == Constants.REWRITE_COMMAND_DOWNLOAD){
						disable("approve_download");
					}
					if(rewrite.getCommand() == Constants.REWRITE_COMMAND_REGISTRATION){
						disable("approve_goTo", 		" (" + I18N.message("second.nofile") + ")");
						disable("approveManager_goTo", 	" (" + I18N.message("second.nofile") + ")");
						disable("approve_download");
					}
					disable("approve_redraft");
				} 
			}
		}

		// 썸네일 버튼 상태 변경.(현재 폴더 이동시 기본 보기로 자동 변경되게 해놨음)
//		changeThumbnailStatus(isThumbnail);
//		disable	("add");
//		disable	("delete");
//		disable	("download");
//		disable	("copy");
//		disable	("checkin");
//		disable	("checkout");
//		disable	("favorites");
//		disable	("paste");
//		disable	("move");
//		disable	("reload");
//		disable	("props");
//		disable	("list_detail");
//		disable	("list_icon");
//		disable	("list_thumbnail");
//		disable	("lock");
//		disable	("unlock");
		
		// 20130726, junsoo, 이벤트는 받았으나. 문서가 한개도 선택이 안되는 경우가 있음. 예) 컨트롤을 누르면서 선택해제를 했을 경우
		// 이때는, 모든 권한을 없애야 함.
		// 20130805, junsoo, 문서 선택 없을 경우 null, null 이 입력되므로 주석 처리함.
//		if (folder == null && (items == null || items.length < 1)) {
//			disableAll();
//			applyAll();
//			return;
//		}
		
		// item 이 folder 를 포함할 경우 처리
//		if(!enableDraftIcon) disable("approve_request");	
		
		// document 처리!
		// 1. 폴더 체크
		List<SFolder> folderList = new ArrayList<SFolder>();
		
		if (currentFolder == null || (folder != null && currentFolder.getId() != folder.getId()))
			currentFolder = folder;
		
		if (folder == null) {
			// 한꺼번에 검색되는 경우는, currentFolder를 사용하지 않음.
			if ((activatedMenuType == TYPE_MYDOC || activatedMenuType == TYPE_SHARED) && currentFolder != null)
				folderList.add(currentFolder);
			else
				if (currentDocuments != null) {
					for (int i = 0; i < currentDocuments.length; i++) {
						if (currentDocuments[i].getFolder() != null && !folderList.contains(currentDocuments[i].getFolder()))
							folderList.add(currentDocuments[i].getFolder());
					}
				}
		}
		else {
			folderList.add(folder);
		}
		Log.debug("menuType = " + getActivatedMenuType());

		// 기안문서 Grid 에러 방지용 이거 안쓰면 에러 발생해요~!
		if(getActivatedMenuType() != TYPE_APPROVE_STANDBY && getActivatedMenuType() != TYPE_APPROVE_REQUEST && getActivatedMenuType() != TYPE_APPROVE_COMPLETE ){
			// 공유문서함, 공유폴더일 경우 권한 설정
			if (	getActivatedMenuType() == TYPE_SHARED || getActivatedMenuType() == TYPE_FOLDER_SHARED ||
					getActivatedMenuType() == TYPE_EXPIRED || getActivatedMenuType() == TYPE_SHARED_TRASH){
				String[] permis = null;
				
				// 문서 보안레벨이 있을경우 문서의 보안 permis 설정
				if(currentDocuments != null && currentDocuments[0].getPermissions()!=null && getActivatedMenuType() != TYPE_FOLDER_SHARED){
					permis = currentDocuments[0].getPermissions();
				}
				// 공유된 폴더 목록 보기 permis 설정
				else if(getActivatedMenuType() == TYPE_FOLDER_SHARED){
					permis = Session.get().getCurrentFolder().getPermissions();
				}
				// 문서 보안 레벨이 없을 경우 폴더의 보안 permis 설정
				else{
					for (SFolder sFolder : folderList) {
						permis = sFolder.getPermissions();
					}
				}

				rights.clear();
				if(permis != null){
					for (String string : permis) {
						rights.add(string);
					}
					if (!rights.toString().contains("delete")) {
						disable	("move");
						disable	("delete");
					}
					if (!rights.toString().contains("download")) {
						disable	("download");
						if(items != null && items.length > 0){
							disable	("sendAsEmail");
							disable	("message");
						}
					}
					if (!rights.toString().contains("check")) {
						
						//20140213na 기안을 요청한 문서는 체크인 체크아웃 취소가 활성화
						if(currentDocuments != null){
							if(!Session.get().getUser().getId().equalsIgnoreCase(currentDocuments[0].getLockUserId())){
								disable	("checkin");
								disable	("cancelcheckout");
							}
						}
						else{
							disable	("checkin");
							disable	("cancelcheckout");
						}
						
						disable	("checkout");
						disable	("lock");
						disable	("unlock");
						
					}
					if (!rights.toString().contains("write")) {
						disable	("copy");
						disable	("add");
						// 20140107, junsoo, D&D
						disable ("DragAndDrop");
						disable	("move");
						disable	("paste");
						disable	("pasteTo");
						disable	("restore");		// 쓰기 권한이 없으면 복원도 불가
					}else{
						if(items == null || items.length == 0)	controlDraftIcon(false);
					}
					if (!rights.toString().contains("view")) {
					}
					if (!rights.toString().contains("extend")) {
						// 20130816, junsoo, extend 권한이 있어야만 패기 및 복구 가능
						disable	("expire");
						disable	("expire_expire");
						disable	("expire_restore");
					}
				}
			}
		}
		
		// 2. 문서 선택이 아무것도 안되어 있을 경우!
		if (items == null || items.length < 0) {
			// 문서
			
			disable("viewScan");
			disable("delete");
			disable("download");
			disable("copy");
			disable("checkin");
			disable("checkout");
			disable("cancelcheckout");
			disable("favorites");
			disable("lock");
			disable("unlock");
			disable("move");
			disable("downloadtemplate");
			disable("settemplate");
			disable("goto");
			disable("properties");

			// 문서 Level 보안
//			disable	("security");
			
			// 승인
//			disable ("approve_detail");
//			disable ("approve_properties");
//			disable ("approval_detail");
//			disable ("approval_properties");
			
			// 승인함
			disable("approve_goTo");
			disable("approveManager_goTo");
			disable("approve_detail");
			disable("approve_properties");
			
			
			
			// 이메일
//			disable ("sendAsEmail");	// 이메일 전송
//			disable ("message");	// 메시지 전송
			// 휴지통.
			disable	("restore");
			disable	("expire");
			
			// 즐겨찾기
			disable	("delete_bookmark");

			// 폐기문서함.
			disable	("expire_restore");
			disable	("expire_expire");
			
			// 검색
			disable	("goto_search");
			disable	("properties_search");
			disable	("show_search");
//			disable	("download_search");
			SFolder currentFolder = Session.get().getCurrentFolder();
			if(currentFolder != null && currentFolder.getType() == SFolder.TYPE_WORKSPACE)
				disable("approve_request");
		}
		if(items != null && items.length == 1)
		{
			disable("doScan");
		}
		// 2. 멀티 선택 체크
		if (items != null && items.length >1) {
			// 문서	
			disable("doScan");
		    disable("viewScan");
			disable("download");
			disable("checkin");
			disable("checkout");
			disable("cancelcheckout");
			disable("lock");
			disable("unlock");
			disable("downloadtemplate");
			disable("settemplate");
			disable("goto");
			disable("properties");
//			disable("delete_bookmark");	// 20140207, junsoo, 북마크 멀티 삭제 가능
			disable("approve_request");
			
			//육용수 선택된 레코드 아이템이 폴더+파일인경우 속성아이콘 해제 20140304
			boolean doc_flag=false,folder_flag = false;
			for(SRecordItem item : items)
			{				
				if(item.getDocument() != null)
				doc_flag  = true;
				else if(item.getFolder() != null)
				folder_flag = true;
				if(doc_flag && folder_flag)
				{
					disable	("properties");
					disable	("properties_search");
					break;
				}
			}
			// 다중 선택된 문서중 권한이 없는 문서가 포함되어 있는 경우 아이콘 disable
			for (SRecordItem item : items) {
					String[] permis;
					try {
						permis = item.getDocument().getPermissions();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						permis = item.getFolder().getPermissions();
					}			
					List<String> permises = new ArrayList<String>();
					for (String str : permis) {
						permises.add(str);
					}
					if(!permises.toString().contains("download")){
						disable ("sendAsEmail",	" (" + I18N.message("sendAsEmail") + 	I18N.message("impossibledocumentisselected") + ")");
						disable ("message",		" (" + I18N.message("message") + 		I18N.message("impossibledocumentisselected") + ")");
						break;
		
					
				}
			}
			
			// 승인
//			disable	("approve_request");
//			disable ("approve_detail");
//			disable ("approve_properties");
//			disable ("approval_detail");
//			disable ("approval_properties");
			
			// 검색
			disable	("goto_search");
			disable	("properties_search");
			disable	("show_search");
//			disable	("download_search");
//			Log.warn("DocumentActionUtil : not multi");
		}
		
		
		// 3. 문서 상태 체크
		if (currentDocuments != null && currentFolder != null) {
//			if(!(SDocument.DOC_LOCKED == currentDocuments[0].getStatus() && 
//					(currentDocuments[0].getLockUserId() != Session.get().getUser().getId()) || !isAdminGroupUser)){
//				disable	("checkin", 		" (" + I18N.message("second.lockedByAnotherUser") + ")");
//				disable	("cancelcheckout", 	" (" + I18N.message("second.lockedByAnotherUser") + ")");
//			}
			
			for (int i = 0; i < currentDocuments.length; i++) {
				// 잠김 상태
				if (SDocument.DOC_UNLOCKED == currentDocuments[i].getStatus()) {
					disable	("unlock");
					disable	("checkin");
					disable	("cancelcheckout");
					
					if(currentFolder.getType() == SFolder.TYPE_WORKSPACE)
						disable	("approve_request");	
				}
				else if (SDocument.DOC_LOCKED == currentDocuments[i].getStatus()){
					disable	("lock", 			" (" + I18N.message("second.statusLocked") + ")");
					disable	("checkout", 		" (" + I18N.message("second.statusLocked") + ")");
					disable	("checkin", 		" (" + I18N.message("second.statusLocked") + ")");
					disable	("cancelcheckout", 	" (" + I18N.message("second.statusLocked") + ")");
					disable	("copy", 			" (" + I18N.message("second.statusLocked") + ")");
					disable	("move", 			" (" + I18N.message("second.statusLocked") + ")");
					disable	("delete", 			" (" + I18N.message("second.statusLocked") + ")");
					disable ("sendAsEmail", 	" (" + I18N.message("second.statusLocked") + ")");	// 이메일 전송
					disable ("message", 		" (" + I18N.message("second.statusLocked") + ")");
					disable ("approve_request", " (" + I18N.message("second.statusLocked") + ")");
					disable	("download", 		" (" + I18N.message("second.statusLocked") + ")");	// 2013.12.5 정승범 다운로드 막기
				}
				else if (SDocument.DOC_CHECKED_OUT == currentDocuments[i].getStatus()) {
					disable	("lock", 		" (" + I18N.message("second.statusCheckedout") + ")");
					disable	("unlock", 		" (" + I18N.message("second.statusCheckedout") + ")");
					disable	("checkout", 	" (" + I18N.message("second.statusCheckedout") + ")");
					disable	("copy", 		" (" + I18N.message("second.statusCheckedout") + ")");
					disable	("move", 		" (" + I18N.message("second.statusCheckedout") + ")");
					disable	("delete", 		" (" + I18N.message("second.statusCheckedout") + ")");
					disable ("sendAsEmail", " (" + I18N.message("second.statusCheckedout") + ")");	// 이메일 전송
					disable ("message", 	" (" + I18N.message("second.statusCheckedout") + ")");
					disable ("approve_request", " (" + I18N.message("second.statusCheckedout") + ")");	// 20140218, junsoo, 체크아웃중 기안 막음
				}

				// 20130816, junsoo, 잠겨있지만, lock user가 아닌경우 비활성화
				// 20130819, junsoo, admin은 모두 가능!
				if (!Session.get().getUser().isMemberOf(Constants.GROUP_ADMIN)) {
					if (SDocument.DOC_LOCKED == currentDocuments[i].getStatus() || SDocument.DOC_CHECKED_OUT == currentDocuments[i].getStatus()) {
						if (currentDocuments[i].getLockUserId() != null && currentDocuments[i].getLockUserId() != Session.get().getUser().getId()) {
							disable	("unlock", 			" (" + I18N.message("second.lockedByAnotherUser") + ")");
							disable	("checkin", 		" (" + I18N.message("second.lockedByAnotherUser") + ")");
							disable	("cancelcheckout", 	" (" + I18N.message("second.lockedByAnotherUser") + ")");
						}
					}
				}
				
				// 파일 존재 여부 체크
				if (currentDocuments[i].getContents().length < 1) {
					disable("download", 	" (" + I18N.message("second.nofile") + ")");
					disable ("sendAsEmail", " (" + I18N.message("second.nofile") + ")");	// 이메일 전송
					disable ("message", 	" (" + I18N.message("second.nofile") + ")");
				}
				
				// template 체크
				if (currentDocuments[i].getTemplateId() == null) {
					disable	("downloadtemplate");
				}
				
				// 20131206, junsoo, content의 process 상태일 경우 download 비활성화
				SContent[] contents = currentDocuments[i].getContents();
				if (contents != null) {
					for (int j = 0; j < contents.length; j++) {
						if (contents[j] != null && SContent.PROCESS_PROCESSED != contents[j].getProcessed()) {
							String message = "";
							if (SContent.PROCESS_TO_PROCESS == contents[j].getProcessed())
								message = I18N.message("PostJobProcessing");
							else
								message = I18N.message("PostJobError");
							disable	("download", " (" + message + ")");
							disable ("sendAsEmail", " (" + message + ")");	// 이메일 전송
							disable ("message", 	" (" + message + ")");
							disable ("approve_request", " (" + message + ")");	// 20140221, junsoo, 후처리중 기안 막음
							disable ("checkout", " (" + message + ")");	// 20140221, junsoo, 후처리중 체크아웃 막음
							disable ("copy", " (" + message + ")");	// 20140306, junsoo, 후처리중 복사 막음
						}
						
						//20150505na 다운로드가 불가능한 타입은 다운로드를 활성화하지 않아야 함.
						if(fileTypes != null){
							for (SFileType fileType : fileTypes) {
								String ext = Util.getExtByFileName(contents[j].getFileName());
								
								if(ext.equalsIgnoreCase(fileType.getName()) && !fileType.isDownload()){
									disable("download");
									break;
								}
							}
						}
					}
				}
			}
		}
		if(currentDocuments != null && currentFolderItems != null)
		{
			
		}
		// 4. 선택이 폴더일 경우 상태 체크
		if (currentFolderItems != null) {
			disable	("properties");
			disable	("properties_search");
//			Log.warn("DocumentActionUtil : folder selected : " + currentFolderItems[0]);
		}
		
		// 5. clipboard 상태 체크
		if (Clipboard.getInstance().isEmpty()) {
			disable	("paste");
			disable	("pasteTo");
		}
//			disable	("move");
		
		// TODO: 4. 현재 지원되지 않는 기능들
//		disable("list_detail");
//		disable("list_icon");
//		disable("list_thumbnail");
		
		// apply enabled
		applyAll();
		// change toolbar
//		DocumentsPanel.get().changeToolbar(toolbars.get(getActivatedMenuType()));
	}

	// add menuitem
	public void createAction(String id, String icon, DocumentAction action, int... types){
		createAction(id, id, icon, true, action, types);
	}
	
	public void createAction(String id, String i18n, String icon, DocumentAction action, int... types){
		createAction(id, i18n, icon, true, action, types);
	}
	// 20130816, junsoo, context menu 포함 여부 추가.
	/**
	 * 	툴바 및 Context Menu를 생성한다.
	 * 	기능별로 하나씩 createAction 해야함.
	 * @param id : 유니크 해야함.
	 * @param i18n
	 * @param icon
	 * @param hasContext : context menu 추가 여부
	 * @param action : 선택 동작 Action 구현
	 * @param types : (DocumentActionUtil.* , ....) - 기능 동작 지역 설정(이 외에는 나타나지 않음)
	 * */
	public void createAction(String id, String i18n, String icon, boolean hasContext, DocumentAction action, int... types){
		if (actions.get(id) != null)
			Log.debug("Action duplicated !! : " + id);
		
		actionList.add(id);
		
		action.setId(id);
		action.setIcon(icon);
		action.setTitle(i18n);
		action.createMenuItem();
		actions.put(id, action);
	
		for (int i = 0; i < types.length; i++) {
			List<MenuItem> items = contextMenus.get(types[i]);
			if (items == null) {
				items = new ArrayList<MenuItem>();
				contextMenus.put(types[i], items);
			}
			// 20130816, junsoo, contextMenu 생성 방지
			if (hasContext) {
//			if(!id.equals("sort"))
				items.add(action.getMenuItem());
			}
			ToolStripButton button = action.createButton(types[i]);
			toolbar = toolbars.get(types[i]);
			if (toolbar == null) {
				toolbar = new ToolStrip();
				toolbars.put(types[i], toolbar);
			}
			/*
			 *  정렬SelectItem 추가위한 변경, sort는 id값을 중복해서 사용!!
			 *  20130812 taesu
			 * */
			if(!id.equals("sort")){
//				if(!id.equals("security"))
					toolbar.addButton(button);
			}
			else {
				SelectItem selectItem = new SelectItem();
				SearchUtil.setSorter(selectItem);
				sorterMap.put(types[i], selectItem);
				toolbar.addFormItem(selectItem);
//				selectItem = action.createSelectItem(types[i]);
			}
		}
	}
	
	// 20140106, junsoo, toolbar에 custom form item 을 추가
	public void createToolItem(String id, InstanceHandler instance, int... types){

		for (int i = 0; i < types.length; i++) {
			toolbar = toolbars.get(types[i]);
			if (toolbar == null) {
				toolbar = new ToolStrip();
				toolbars.put(types[i], toolbar);
			}
			List<CanvasItem> items = canvasItems.get(id);
			if (items == null) {
				items = new ArrayList<CanvasItem>();
				canvasItems.put(id, items);
			}
			CanvasItem item = (CanvasItem)instance.getInstance();
			items.add(item);

			toolbar.addFormItem(item);
		}
	}

	private boolean hasfolderControlRight;
	@Override
	public void onFolderSelected(SFolder folder) {
		update(folder, null);
		hasfolderControlRight = rights.toString().contains("control");
	}
	
	@Override
	public void onFolderSaved(SFolder folder) {
		
	}

	@Override
	public void onFolderReload() {
		
	}

	@Override
	public void onDocumentSaved(SDocument document) {
		
	}

	@Override
	public void onServiceComplite(String message) {
		
	}

	@Override
	public void onDocumentSelected(SRecordItem[] items) {
		// 20130805, 문서가 전혀 선택되지 않은 경우 기본 액션 가능케 하기 위해 주석처리함.
//		if (items == null || items.length < 1)
//			return;
		getAllDraftRights(items);
		
		// 20140325, junsoo, items가 null인 것도 의미가 있는 코드이므로.. items == null 체크 하지 말것!!
		update(null, items);
	}

	@Override
	public void onReloadRequest(SFolder folder) {
		
	}

	
	/**
	 * Get All Draft Rights(cashed)
	 * @param items
	 */
	private void getAllDraftRights(final SRecordItem[] items){
		if(getActivatedMenuType() != DocumentActionUtil.TYPE_SHARED) return;
		
		final LinkedHashMap<Integer, String> map = new LinkedHashMap<Integer, String>();
		// 선택되어 있는 문서가 없을경우 기본적으로 등록만 가능
		if(items == null){
			map.put(Constants.DRAFT_TYPE_REGISTRATION, I18N.message("event.stored"));
			DraftMap = map;
		}else{
			/*
			 * 1. 모든 권한 획득
			 * */
			ServiceUtil.getDocumentCodes("APP_SET_COMMAND", new ReturnHandler<List<SCode>>() {
				@Override
				public void onReturn(List<SCode> result) {
					for (SCode code : result) {
						map.put(Integer.parseInt(code.getValue()), I18N.message("event."+code.getName()));
						DraftMap = map;
					}
				}
			});
		}
	}
	
	/**
	 * 썸네일 아이콘 선택 상태 변경
	 * @param isThumbnail
	 */
	public void changeThumbnailStatus(){
//		this.isThumbnail = isThumbnail;
		this.isThumbnail = !this.isThumbnail;	// toggle
//		DocumentAction list_detail_action = actions.get("list_detail");
		DocumentAction list_thumbnail_action = actions.get("list_thumbnail");
		
		if (list_thumbnail_action != null) {
			list_thumbnail_action.changeButtonWorking(this.isThumbnail);
		}
	}
	
//	/**
//	 * 최초 폴더 선택 후(문서 미 선택시) 사용자의 폴더 권한에 따라 기안버튼 컨트롤  
//	 */
//	private void controlDraftButton(){
//		if(!rights.toString().contains("write")){
//			controlDraftIcon(true);
//		}else{
//			controlDraftIcon(false);
//		}
//	}
	/**
	 * Draft Icon control
	 * @param enable
	 */
	public void controlDraftIcon(boolean enable){
		if(!enable) disable("approve_request");	
		applyAll();
	}
	
	/*
	 * Getter, Setter
	 */
	public ToolStrip getToolbar() {
		return toolbar;
	}

	public boolean isHasfolderControlRight() {
		return hasfolderControlRight;
	}

	public void setHasfolderControlRight(boolean hasfolderControlRight) {
		this.hasfolderControlRight = hasfolderControlRight;
	}

	public List<String> getRights() {
		return rights;
	}

	public LinkedHashMap<Integer, String> getDraftMap() {
		return DraftMap;
	}

	public void setDraftMap(LinkedHashMap<Integer, String> draftMap) {
		this.DraftMap = draftMap;
	}

	public boolean isThumbnail() {
		return isThumbnail;
	}
}
