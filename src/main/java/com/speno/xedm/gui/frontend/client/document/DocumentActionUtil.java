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
 * documents �� context �޴� �� toolbar ��Ʈ�� Ŭ����
 * type�� ���� ��ü ���� �� ���ؽ�Ʈ�޴��� �� Ŭ������ ����� ��.
 * 
 * Menu �߰� ���
 * 1. TYPE ��� �߰�
 * 2. createAction ���� �׸� �߰�
 * 3. setActivatedMenuType ���� �޴� �̵��� ���� ���� �޴��� �˷��ֱ�
 * 4. get().getToolbar(), get().getContextMenu() �� ���� �޴��� ���� �� ���ؽ�Ʈ ȹ��
 * 
 * Modified:
 * 		20130816, junsoo, extend ������ �־�߸� �б� �� ���� ����
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

	// ���� ����
	private SFolder currentFolder;
	private SDocument[] currentDocuments;
	private SFolder[] currentFolderItems;
	
	private SRewrite[] currentReriteItems;
	private SRecordItem[] currentItems;	
	private int activatedMenuType = TYPE_MYDOC;
	
	private ToolStrip toolbar;
	// ����� ������ ���û��� ����
	private boolean isThumbnail = false;
	
	// 20130903 taesu, ���� ����
	private List<String> rights = new ArrayList<String>();
	// 20130904, taesu, Draft���� ���� �߰�
	private LinkedHashMap<Integer, String> DraftMap = new LinkedHashMap<Integer, String>();
	
	// 20130814 taesu, �˻� ���� �߰�
//	private boolean isSearching = false;
	// Sorter SelectItem �����
	private Map<Integer, SelectItem> sorterMap = new HashMap<Integer, SelectItem>();
	// Draft Icon Control��
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
//		// menu�� �ٲ�� �Ӽ� â �ݱ�
//		if (this.activatedMenuType != activatedMenuType)
//			DocumentPropertiesWindow.get().close();
		
		this.activatedMenuType = activatedMenuType;

		// 20140211, junsoo, ��Ʈ�޺��ڽ� ����
		if(sorterMap != null && sorterMap.get(getActivatedMenuType()) != null)
			sorterMap.get(getActivatedMenuType()).setValue(Constants.ORDER_BY_MODIFIEDDATE_DESC);

	}
	
	// 20140211, junsoo, sorter ȹ��
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
	 *	Filter Icon�� Ȱ��ȭ/��Ȱ��ȭ ���·� ��ȯ�Ѵ�.
	 *	@param id
	 *	@param isWorking : Ȱ��/��Ȱ��ȭ ����
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
		
		// 20131128 na ��ú����� ������ ������ �߰� ����. 
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
	 *  ���ؽ�Ʈ �޴� ����.
	 * */
	public Menu getContextMenu() {
		return getContextMenu(getActivatedMenuType());
	}
	
	/**
	 * ���� level ������ ����Ǿ��ִ� contextMenu ����
	 * @param isSecurity
	 * @return
	 */
	public Menu getContextMenu(boolean isSecurity){
		return getContextMenuAddSecurity(getActivatedMenuType(), isSecurity);
	}
	
	/**
	 * 	Ư�� ContextMenu�� ������.
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
	
	//EDM,ECM���ΰ˻�-�˻������� toolbarȰ��ȭ ���� hyewon
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
	 * 20131210 na ���� ������ ��Ȱ��ȭ ETS��
	 * ����� checkout�� ���
	 * �׼� Ȱ��/��Ȱ��ȭ ���� 
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
	 * �׼� Ȱ��/��Ȱ��ȭ ����
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
		
		// ���� ������ ����
		if(currentReriteItems != null){
			for (SRewrite rewrite : currentReriteItems) {
				if(rewrite.getCommand() != Constants.REWRITE_COMMAND_DOWNLOAD){
					disable("approve_download");
					
				}
				
				// ���� �Ϸ�
				if(rewrite.getStatus() == Constants.REWRITE_STATUS_COMPLETE_APPROVAL){
					if(rewrite.getCommand() == Constants.REWRITE_COMMAND_DELETE){
						disable("approve_goTo", 		" (" + I18N.message("second.nofile") + ")");
						disable("approveManager_goTo", 	" (" + I18N.message("second.nofile") + ")");
						
					}
					disable("approve_redraft");
				}
				// ȸ�� �Ϸ�
				else if(rewrite.getStatus() == Constants.REWRITE_STATUS_COMPLETE_RETURN){
					if(rewrite.getCommand() == Constants.REWRITE_COMMAND_DOWNLOAD){
						disable("approve_download");
					}
					if(rewrite.getCommand() == Constants.REWRITE_COMMAND_REGISTRATION){
						disable("approve_goTo", 		" (" + I18N.message("second.nofile") + ")");	// 20140218, junsoo, ȸ���� ��� goto�� �� ����
						disable("approve_download");
						disable("approve_redraft");
					}
				}
				// �ݷ� �Ϸ�
				else if(rewrite.getStatus() == Constants.REWRITE_STATUS_COMTLETE_RECOVERY){
					if(rewrite.getCommand() == Constants.REWRITE_COMMAND_DOWNLOAD){
						disable("approve_download");
					}
					if(rewrite.getCommand() == Constants.REWRITE_COMMAND_REGISTRATION){
						disable("approve_goTo", 		" (" + I18N.message("second.nofile") + ")");	// 20140218, junsoo, �ݷ��� ��� goto�� �� ����
						disable("approve_download");
						disable("approve_redraft");
					}
				}
				// ���� ��
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

		// ����� ��ư ���� ����.(���� ���� �̵��� �⺻ ����� �ڵ� ����ǰ� �س���)
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
		
		// 20130726, junsoo, �̺�Ʈ�� �޾�����. ������ �Ѱ��� ������ �ȵǴ� ��찡 ����. ��) ��Ʈ���� �����鼭 ���������� ���� ���
		// �̶���, ��� ������ ���־� ��.
		// 20130805, junsoo, ���� ���� ���� ��� null, null �� �ԷµǹǷ� �ּ� ó����.
//		if (folder == null && (items == null || items.length < 1)) {
//			disableAll();
//			applyAll();
//			return;
//		}
		
		// item �� folder �� ������ ��� ó��
//		if(!enableDraftIcon) disable("approve_request");	
		
		// document ó��!
		// 1. ���� üũ
		List<SFolder> folderList = new ArrayList<SFolder>();
		
		if (currentFolder == null || (folder != null && currentFolder.getId() != folder.getId()))
			currentFolder = folder;
		
		if (folder == null) {
			// �Ѳ����� �˻��Ǵ� ����, currentFolder�� ������� ����.
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

		// ��ȹ��� Grid ���� ������ �̰� �Ⱦ��� ���� �߻��ؿ�~!
		if(getActivatedMenuType() != TYPE_APPROVE_STANDBY && getActivatedMenuType() != TYPE_APPROVE_REQUEST && getActivatedMenuType() != TYPE_APPROVE_COMPLETE ){
			// ����������, ���������� ��� ���� ����
			if (	getActivatedMenuType() == TYPE_SHARED || getActivatedMenuType() == TYPE_FOLDER_SHARED ||
					getActivatedMenuType() == TYPE_EXPIRED || getActivatedMenuType() == TYPE_SHARED_TRASH){
				String[] permis = null;
				
				// ���� ���ȷ����� ������� ������ ���� permis ����
				if(currentDocuments != null && currentDocuments[0].getPermissions()!=null && getActivatedMenuType() != TYPE_FOLDER_SHARED){
					permis = currentDocuments[0].getPermissions();
				}
				// ������ ���� ��� ���� permis ����
				else if(getActivatedMenuType() == TYPE_FOLDER_SHARED){
					permis = Session.get().getCurrentFolder().getPermissions();
				}
				// ���� ���� ������ ���� ��� ������ ���� permis ����
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
						
						//20140213na ����� ��û�� ������ üũ�� üũ�ƿ� ��Ұ� Ȱ��ȭ
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
						disable	("restore");		// ���� ������ ������ ������ �Ұ�
					}else{
						if(items == null || items.length == 0)	controlDraftIcon(false);
					}
					if (!rights.toString().contains("view")) {
					}
					if (!rights.toString().contains("extend")) {
						// 20130816, junsoo, extend ������ �־�߸� �б� �� ���� ����
						disable	("expire");
						disable	("expire_expire");
						disable	("expire_restore");
					}
				}
			}
		}
		
		// 2. ���� ������ �ƹ��͵� �ȵǾ� ���� ���!
		if (items == null || items.length < 0) {
			// ����
			
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

			// ���� Level ����
//			disable	("security");
			
			// ����
//			disable ("approve_detail");
//			disable ("approve_properties");
//			disable ("approval_detail");
//			disable ("approval_properties");
			
			// ������
			disable("approve_goTo");
			disable("approveManager_goTo");
			disable("approve_detail");
			disable("approve_properties");
			
			
			
			// �̸���
//			disable ("sendAsEmail");	// �̸��� ����
//			disable ("message");	// �޽��� ����
			// ������.
			disable	("restore");
			disable	("expire");
			
			// ���ã��
			disable	("delete_bookmark");

			// ��⹮����.
			disable	("expire_restore");
			disable	("expire_expire");
			
			// �˻�
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
		// 2. ��Ƽ ���� üũ
		if (items != null && items.length >1) {
			// ����	
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
//			disable("delete_bookmark");	// 20140207, junsoo, �ϸ�ũ ��Ƽ ���� ����
			disable("approve_request");
			
			//����� ���õ� ���ڵ� �������� ����+�����ΰ�� �Ӽ������� ���� 20140304
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
			// ���� ���õ� ������ ������ ���� ������ ���ԵǾ� �ִ� ��� ������ disable
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
			
			// ����
//			disable	("approve_request");
//			disable ("approve_detail");
//			disable ("approve_properties");
//			disable ("approval_detail");
//			disable ("approval_properties");
			
			// �˻�
			disable	("goto_search");
			disable	("properties_search");
			disable	("show_search");
//			disable	("download_search");
//			Log.warn("DocumentActionUtil : not multi");
		}
		
		
		// 3. ���� ���� üũ
		if (currentDocuments != null && currentFolder != null) {
//			if(!(SDocument.DOC_LOCKED == currentDocuments[0].getStatus() && 
//					(currentDocuments[0].getLockUserId() != Session.get().getUser().getId()) || !isAdminGroupUser)){
//				disable	("checkin", 		" (" + I18N.message("second.lockedByAnotherUser") + ")");
//				disable	("cancelcheckout", 	" (" + I18N.message("second.lockedByAnotherUser") + ")");
//			}
			
			for (int i = 0; i < currentDocuments.length; i++) {
				// ��� ����
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
					disable ("sendAsEmail", 	" (" + I18N.message("second.statusLocked") + ")");	// �̸��� ����
					disable ("message", 		" (" + I18N.message("second.statusLocked") + ")");
					disable ("approve_request", " (" + I18N.message("second.statusLocked") + ")");
					disable	("download", 		" (" + I18N.message("second.statusLocked") + ")");	// 2013.12.5 ���¹� �ٿ�ε� ����
				}
				else if (SDocument.DOC_CHECKED_OUT == currentDocuments[i].getStatus()) {
					disable	("lock", 		" (" + I18N.message("second.statusCheckedout") + ")");
					disable	("unlock", 		" (" + I18N.message("second.statusCheckedout") + ")");
					disable	("checkout", 	" (" + I18N.message("second.statusCheckedout") + ")");
					disable	("copy", 		" (" + I18N.message("second.statusCheckedout") + ")");
					disable	("move", 		" (" + I18N.message("second.statusCheckedout") + ")");
					disable	("delete", 		" (" + I18N.message("second.statusCheckedout") + ")");
					disable ("sendAsEmail", " (" + I18N.message("second.statusCheckedout") + ")");	// �̸��� ����
					disable ("message", 	" (" + I18N.message("second.statusCheckedout") + ")");
					disable ("approve_request", " (" + I18N.message("second.statusCheckedout") + ")");	// 20140218, junsoo, üũ�ƿ��� ��� ����
				}

				// 20130816, junsoo, ���������, lock user�� �ƴѰ�� ��Ȱ��ȭ
				// 20130819, junsoo, admin�� ��� ����!
				if (!Session.get().getUser().isMemberOf(Constants.GROUP_ADMIN)) {
					if (SDocument.DOC_LOCKED == currentDocuments[i].getStatus() || SDocument.DOC_CHECKED_OUT == currentDocuments[i].getStatus()) {
						if (currentDocuments[i].getLockUserId() != null && currentDocuments[i].getLockUserId() != Session.get().getUser().getId()) {
							disable	("unlock", 			" (" + I18N.message("second.lockedByAnotherUser") + ")");
							disable	("checkin", 		" (" + I18N.message("second.lockedByAnotherUser") + ")");
							disable	("cancelcheckout", 	" (" + I18N.message("second.lockedByAnotherUser") + ")");
						}
					}
				}
				
				// ���� ���� ���� üũ
				if (currentDocuments[i].getContents().length < 1) {
					disable("download", 	" (" + I18N.message("second.nofile") + ")");
					disable ("sendAsEmail", " (" + I18N.message("second.nofile") + ")");	// �̸��� ����
					disable ("message", 	" (" + I18N.message("second.nofile") + ")");
				}
				
				// template üũ
				if (currentDocuments[i].getTemplateId() == null) {
					disable	("downloadtemplate");
				}
				
				// 20131206, junsoo, content�� process ������ ��� download ��Ȱ��ȭ
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
							disable ("sendAsEmail", " (" + message + ")");	// �̸��� ����
							disable ("message", 	" (" + message + ")");
							disable ("approve_request", " (" + message + ")");	// 20140221, junsoo, ��ó���� ��� ����
							disable ("checkout", " (" + message + ")");	// 20140221, junsoo, ��ó���� üũ�ƿ� ����
							disable ("copy", " (" + message + ")");	// 20140306, junsoo, ��ó���� ���� ����
						}
						
						//20150505na �ٿ�ε尡 �Ұ����� Ÿ���� �ٿ�ε带 Ȱ��ȭ���� �ʾƾ� ��.
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
		// 4. ������ ������ ��� ���� üũ
		if (currentFolderItems != null) {
			disable	("properties");
			disable	("properties_search");
//			Log.warn("DocumentActionUtil : folder selected : " + currentFolderItems[0]);
		}
		
		// 5. clipboard ���� üũ
		if (Clipboard.getInstance().isEmpty()) {
			disable	("paste");
			disable	("pasteTo");
		}
//			disable	("move");
		
		// TODO: 4. ���� �������� �ʴ� ��ɵ�
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
	// 20130816, junsoo, context menu ���� ���� �߰�.
	/**
	 * 	���� �� Context Menu�� �����Ѵ�.
	 * 	��ɺ��� �ϳ��� createAction �ؾ���.
	 * @param id : ����ũ �ؾ���.
	 * @param i18n
	 * @param icon
	 * @param hasContext : context menu �߰� ����
	 * @param action : ���� ���� Action ����
	 * @param types : (DocumentActionUtil.* , ....) - ��� ���� ���� ����(�� �ܿ��� ��Ÿ���� ����)
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
			// 20130816, junsoo, contextMenu ���� ����
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
			 *  ����SelectItem �߰����� ����, sort�� id���� �ߺ��ؼ� ���!!
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
	
	// 20140106, junsoo, toolbar�� custom form item �� �߰�
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
		// 20130805, ������ ���� ���õ��� ���� ��� �⺻ �׼� ������ �ϱ� ���� �ּ�ó����.
//		if (items == null || items.length < 1)
//			return;
		getAllDraftRights(items);
		
		// 20140325, junsoo, items�� null�� �͵� �ǹ̰� �ִ� �ڵ��̹Ƿ�.. items == null üũ ���� ����!!
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
		// ���õǾ� �ִ� ������ ������� �⺻������ ��ϸ� ����
		if(items == null){
			map.put(Constants.DRAFT_TYPE_REGISTRATION, I18N.message("event.stored"));
			DraftMap = map;
		}else{
			/*
			 * 1. ��� ���� ȹ��
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
	 * ����� ������ ���� ���� ����
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
//	 * ���� ���� ���� ��(���� �� ���ý�) ������� ���� ���ѿ� ���� ��ȹ�ư ��Ʈ��  
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
