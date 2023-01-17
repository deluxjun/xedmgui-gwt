package com.speno.xedm.gui.frontend.client.document;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.types.TreeModelType;
import com.smartgwt.client.types.VisibilityMode;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.util.ValueCallback;
import com.smartgwt.client.widgets.ImgButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.CellClickEvent;
import com.smartgwt.client.widgets.grid.events.CellClickHandler;
import com.smartgwt.client.widgets.grid.events.CellContextClickEvent;
import com.smartgwt.client.widgets.grid.events.CellContextClickHandler;
import com.smartgwt.client.widgets.layout.SectionStack;
import com.smartgwt.client.widgets.layout.SectionStackSection;
import com.smartgwt.client.widgets.layout.events.SectionHeaderClickEvent;
import com.smartgwt.client.widgets.layout.events.SectionHeaderClickHandler;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.menu.MenuItem;
import com.smartgwt.client.widgets.menu.events.MenuItemClickEvent;
import com.smartgwt.client.widgets.tree.Tree;
import com.smartgwt.client.widgets.tree.TreeGrid;
import com.smartgwt.client.widgets.tree.TreeNode;
import com.smartgwt.client.widgets.tree.events.FolderOpenedEvent;
import com.smartgwt.client.widgets.tree.events.FolderOpenedHandler;
import com.speno.xedm.core.service.serials.SFolder;
import com.speno.xedm.core.service.serials.SSecurityProfile;
import com.speno.xedm.gui.common.client.Constants;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.Session;
import com.speno.xedm.gui.common.client.log.Log;
import com.speno.xedm.gui.common.client.util.AdminMenuUtil;
import com.speno.xedm.gui.common.client.util.AsyncCallbackWithStatus;
import com.speno.xedm.gui.common.client.util.HistoryUtil;
import com.speno.xedm.gui.common.client.util.IFAdminMenuPriv;
import com.speno.xedm.gui.common.client.util.IFHistoryObserver;
import com.speno.xedm.gui.common.client.util.ItemFactory;
import com.speno.xedm.gui.common.client.util.SCM;
import com.speno.xedm.gui.common.client.util.ServiceUtil;
import com.speno.xedm.gui.common.client.util.Useful;
import com.speno.xedm.gui.common.client.window.FolderPropertiesWindow;
import com.speno.xedm.gui.common.client.window.batchdownloder;
import com.speno.xedm.gui.frontend.client.folder.EtcMenus;
import com.speno.xedm.gui.frontend.client.folder.NavigatorForDocument;
import com.speno.xedm.gui.frontend.client.panels.MainPanel;
import com.speno.xedm.gui.frontend.client.shared.FolderSharingPanel;
import com.speno.xedm.gui.frontend.client.shared.FolderSharingWindow;
import com.speno.xedm.gwt.service.FolderService;
import com.speno.xedm.gwt.service.FolderServiceAsync;

/**
 * Modified :
 * 		20130805, junsoo, xvarm section ����. (�˻����� ������ �����̹Ƿ� ������.)
 * @author deluxjun
 *
 */
public class DocumentsMenu extends SectionStack implements IFAdminMenuPriv, IFHistoryObserver{
	// xvarm Ʈ��
//	public NavigatorForDocument XvarmTree;
	// mydoc Ʈ��
	public NavigatorForDocument MydocTree;
	// sharedoc Ʈ��
	public  NavigatorForDocument SharedocTree;
	// etc Ʈ��
	public NavigatorForDocument etcTree;
	
	
	// xvarm ����
//	protected SectionStackSection XvarmSection = null;
	// mydoc ����
	protected SectionStackSection MydocSection = null;
	// shareddoc ����
	protected SectionStackSection SharedocSection = null;
	// etc ����
	protected SectionStackSection etcSection = null;
	// ù��° ������ Ŭ���ž� ������ ������ ������ ���񽺸� ���ؼ�
	// �������� �״��� ���� ù������ ������ ��ȸ�ϱ� ���ؼ� 
	// ù��° ���� Ŭ�����θ� ����Ѵ�.
//	private boolean firstTimeXvarm = true;
	private boolean firstTimeMydoc = true;
	private boolean firstTimeShareDoc = true;
	
	// ���ã���� ����Ŭ������ ������ ã�ư��� ������ �ڵ����� �ش�������
	// �Ӽ��� ǥ�������� �̶����� ������ �ش繮���� ���õŸ鼭 ���� �Ӽ��� ��������.
	// �����Ӽ��� �������� ���� �������� ���� �Ӽ��� �ռ� �˻��Ŵ� ���� �������� ���.
	public boolean expandCommand = false;
	
	private FolderServiceAsync service = (FolderServiceAsync) GWT.create(FolderService.class);

	// 20130727, junsoo, DocumentsPanel ���� �����Ͽ� ����ϱ� ������, singletone ��� ����!!!
//	private static DocumentsMenu instance;
//	public static DocumentsMenu get() {
//		if (instance == null) {
//			instance = new DocumentsMenu();
//		}
//		return instance;
//	}
	
	public DocumentsMenu() {

		setVisibilityMode(VisibilityMode.MULTIPLE);
		setWidth100();
		setHeight100();
		// 20130903, junsoo, ������ ���ѿ� ���� �ε����� �����ڿ��� ������ ��� �ּ� ó����.
//		/*
//		 *  ������ ������ ����� �ش� ���ǿ� ����Ʈ���� �����.
//		 *  prepareSection(����, Ʈ��, ���ǳ���, ����Ÿ��)
//		 */
//		MydocSection = new SectionStackSection(I18N.message("mydoc"));
//		MydocTree = new NavigatorForDocument(Constants.FOLDER_TYPE_MYDOC);
//		prepareSection(MydocSection, MydocTree, Constants.SECTION_NAME_MYDOC);
//		
//		SharedocSection = new SectionStackSection(I18N.message("shareddoc"));
//		SharedocTree = new NavigatorForDocument(Constants.FOLDER_TYPE_SHARED);
//        prepareSection(SharedocSection, SharedocTree, Constants.SECTION_NAME_SHARED);
//
////        XvarmSection = new SectionStackSection(I18N.message("docmenuxvarm"));
////		XvarmTree = new NavigatorForDocument(Constants.FOLDER_TYPE_XVARM);
////		prepareSection(XvarmSection, XvarmTree, Constants.SECTION_NAME_XVARM);
//		
//		etcSection = new SectionStackSection(I18N.message("second.client.documentetc"));
//		etcTree = new NavigatorForDocument(Constants.FOLDER_TYPE_ETC);
//		prepareSection(etcSection, etcTree, Constants.SECTION_NAME_ETC);
//		// 20130805, junsoo, etc �� ó���� ����
//		expand(2);
	
//		final Menu contextMenu = new Menu();
//
//		MenuItem append = new MenuItem();
//		append.setTitle(I18N.message("client.appendFolder"));
//		append.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
//			@Override
//			public void onClick(MenuItemClickEvent event) {
//				onCreate(MydocTree, true);
//			}
//		});
//		contextMenu.setItems(append);
//		addRightMouseDownHandler(new RightMouseDownHandler() {
//			@Override
//			public void onRightMouseDown(RightMouseDownEvent event) {
////				if (event.getSource())
//				contextMenu.showContextMenu();
//
//				if (event != null)
//					event.cancel();
//			}
//		});
		
		
		/*
         * ������ Ŭ��������
         * �ش� ������ �������ų� ù��° �����ȿ� ������ȸ ����
         */		
		
        addSectionHeaderClickHandler(new SectionHeaderClickHandler() {
			@Override
			public void onSectionHeaderClick(SectionHeaderClickEvent event) {
//				DocumentsPanel.get().setRightDefault(false);
//				if(!sectionIsExpanded(Constants.SECTION_NAME_XVARM) && event.getSection().getName().equals(Constants.SECTION_NAME_XVARM)){
//					DocumentsPanel.get().trackPanel.setIcon(ItemFactory.newImgIcon("ecm.png").getSrc());
//					
//					if(firstTimeXvarm)	getFolderDataRpc(XvarmTree, Constants.FOLDER_TYPE_XVARM);
//					else	selectFolder(XvarmTree, Constants.FOLDER_TYPE_XVARM);	
//				}else
			
				if((getSection(Constants.MENU_DOCUMENTS_MYDOC) == null || !sectionIsExpanded(Constants.MENU_DOCUMENTS_MYDOC)) &&
						event.getSection().getName().equals(Constants.MENU_DOCUMENTS_MYDOC) ){
					
					if(Session.get().getHomeFolderId() == -1) {
						return;
					}
					
					DocumentsPanel.get().trackPanel.setIcon(ItemFactory.newImgIcon("mydoc.png").getSrc());
					
					if(firstTimeMydoc) getFolderDataRpc(MydocTree, Constants.FOLDER_TYPE_MYDOC); 
					else selectFolder(MydocTree, Constants.FOLDER_TYPE_MYDOC); 
				}else if((getSection(Constants.MENU_DOCUMENTS_SHAREDDOC) == null || !sectionIsExpanded(Constants.MENU_DOCUMENTS_SHAREDDOC)) &&
						event.getSection().getName().equals(Constants.MENU_DOCUMENTS_SHAREDDOC)){
					DocumentsPanel.get().trackPanel.setIcon(ItemFactory.newImgIcon("sharedoc.png").getSrc());
					
					if(firstTimeShareDoc) getFolderDataRpc(SharedocTree, Constants.FOLDER_TYPE_SHARED); 
					else	selectFolder(SharedocTree, Constants.FOLDER_TYPE_SHARED); 
				}else if((getSection(Constants.MENU_DOCUMENTS_ETC) == null || !sectionIsExpanded(Constants.MENU_DOCUMENTS_ETC)) && 
						event.getSection().getName().equals(Constants.MENU_DOCUMENTS_ETC)){
//					DocumentsPanel.get().trackPanel.setIcon(ItemFactory.newImgIcon("etc.png").getSrc());
	
					// 20130731, junsoo, treegrid ������ �ʱ�ȭ�ǹǷ� ����
//					getFolderDataRpc(etcTree, Constants.FOLDER_TYPE_ETC); 
				}
			}	
		});        
    }
	
	// 20130903, junsoo, ������ �����Ƿ� �ּ�ó����.
//	public void reset(){
//		collapseSection("documents_mydoc");
//		collapseSection("documents_shareddoc");
//		expandSection("documents_etc");
//	}
	
//	// �ʱ� ȭ����۰� �Բ� mydoc ����
//	public void refreshFirst(){
//		if (!firstTimeMydoc || !firstTimeShareDoc )
//			return;
//		
//		if(firstTimeMydoc) getFolderDataRpc(MydocTree, Constants.FOLDER_TYPE_MYDOC); 
//		else selectFolder(MydocTree, Constants.FOLDER_TYPE_MYDOC); 
//		
//	}
	
	
	// ������������
	public void getFolderDataRpc(final NavigatorForDocument tGrid, final int type){
		
		tGrid.getFolderDataRpc(type, 0, "folderId", "name", "parent", "type", true);
			
//		if(firstTimeXvarm && type == Constants.FOLDER_TYPE_XVARM){ firstTimeXvarm = false; }	
		if(firstTimeMydoc && type == Constants.FOLDER_TYPE_MYDOC){ firstTimeMydoc = false; }	
		if(firstTimeShareDoc && type == Constants.FOLDER_TYPE_SHARED){ firstTimeShareDoc = false; }
	}
	
	// section ����
	private void prepareSection(final SectionStackSection section, final NavigatorForDocument tree,
			final String sectionName){
		
		// 20130724, junsoo, ���� ����� refresh button �߰�
        ImgButton refreshButton = new ImgButton();  
        refreshButton.setSrc("[SKIN]/headerIcons/refresh.png");  
        refreshButton.setSize(16);  
        refreshButton.setShowFocused(false);  
        refreshButton.setShowRollOver(false);  
        refreshButton.setShowDown(false);  
        refreshButton.addClickHandler(new ClickHandler() {  
            public void onClick(ClickEvent event) {  
            	if (!sectionIsExpanded(sectionName))
            		return;

            	// refresh
            	if (Constants.MENU_DOCUMENTS_MYDOC.equals(sectionName))
            		getFolderDataRpc(tree, Constants.FOLDER_TYPE_MYDOC); 
            	else if (Constants.MENU_DOCUMENTS_SHAREDDOC.equals(sectionName))
            		getFolderDataRpc(tree, Constants.FOLDER_TYPE_SHARED); 
            	/*
            	 * ETC Section�� �ϵ��ڵ� �Ǿ�����.
            	 * 130801 taesu
            	 * */ 
//            	else if (Constants.SECTION_NAME_ETC.equals(sectionName))
//            		getFolderDataRpc(tree, Constants.FOLDER_TYPE_ETC); 
            }  
        });  
  
		
		section.setName(sectionName);
//		section.setCanCollapse(true);
		// �������� �Ұ�
		section.setResizeable(true);
		section.setItems(tree);
		
		section.setExpanded(false);
		
		/*
		 * ETC Section�� Refresh ���� ����.
		 * 130801 taesu
		 * */
		if(!Constants.MENU_DOCUMENTS_ETC.equals(sectionName))
			section.setControls(refreshButton);
		addSection(section);
        
        /* ���ؽ�Ʈ �޴� ����
         * xvarm �� ��� ���ؽ�Ʈ �޴� ����
         * getfolder�� ����Ͽ� ���������� ������ 
         * �޴��� �����.
         */
        tree.addCellContextClickHandler(new CellContextClickHandler() {
			@Override
			public void onCellContextClick(final CellContextClickEvent event) {
				// xvarm ���ؽ�Ʈ �޴� ����.
//				if(sectionIsExpanded(Constants.SECTION_NAME_XVARM)){
//				if(tree.getFolderType() == Constants.FOLDER_TYPE_XVARM || tree.getFolderType() == Constants.FOLDER_TYPE_ETC){
				// ETC �޴��� ���ؽ�Ʈ �޴� ����
				if(tree.getFolderType() == Constants.FOLDER_TYPE_ETC){
					SFolder folder = (SFolder)event.getRecord().getAttributeAsObject("folder");
					if(folder != null && folder.getSharedDepth() != 0){
						Menu contextMenu = setupSharedContextMenu(folder);
						contextMenu.showContextMenu();
					}
				}else{
					// 20130805, junsoo, ���� ������ ����ȸ�� �ʿ� ����.
					long folderId = Long.parseLong(event.getRecord().getAttributeAsString("folderId"));
					if (folderId != Session.get().getCurrentFolder().getId()) {
						// �������� �ʿ�
						service.getFolder(Session.get().getSid(),folderId, false, true,
								new AsyncCallback<SFolder>() {
							
							@Override
							public void onFailure(Throwable caught) {
								SCM.warn(caught);
							}
							
							@Override
							public void onSuccess(SFolder folder) {
								// 20130801, junsoo, ��Ŭ���ÿ��� ������ ���õǵ��� ��
								folder.setPathExtended(getPath(tree, folder.getId()));
								Session.get().setCurrentFolder(folder);
								
								// ��Ȯ�� ���� ������ �ٽ� ���� (mydoc�� ��� parentId�� ������ �ٸ��Ƿ�)
								event.getRecord().setAttribute("parent", folder.getParentId());
								
								//								Menu contextMenu = setupContextMenu(tree, folder, sectionIsExpanded(Constants.SECTION_NAME_SHARED));
								Menu contextMenu = setupContextMenu(tree, folder, tree.getFolderType() == Constants.FOLDER_TYPE_SHARED);
								contextMenu.showContextMenu();
							}
						});
					} else {
						// ��Ȯ�� ���� ������ �ٽ� ���� (mydoc�� ��� parentId�� ������ �ٸ��Ƿ�)
						event.getRecord().setAttribute("parent",  Session.get().getCurrentFolder().getParentId());
						
//								Menu contextMenu = setupContextMenu(tree, folder, sectionIsExpanded(Constants.SECTION_NAME_SHARED));
						Menu contextMenu = setupContextMenu(tree, Session.get().getCurrentFolder(), tree.getFolderType() == Constants.FOLDER_TYPE_SHARED);
						contextMenu.showContextMenu();
					}
				}
				if (event != null)
					event.cancel();
			}
		});
        
        // ������ Ŭ�������� �ش� ������ ��������Ʈ�� ��ȸ�Ѵ�.
        tree.addCellClickHandler(new CellClickHandler() {
			@Override
			public void onCellClick(CellClickEvent event) {
				//���񽺰� �ٸ��Ƿ� xvarm�� �������� �����Ͽ� �Լ�ȣ��
//				if(DocumentsPanel.get().documentsMenu.sectionIsExpanded(Constants.SECTION_NAME_XVARM)){
//					selectFolder(tree, event.getRecord().getAttribute("name").toString());
//				if (tree.getFolderType() == Constants.FOLDER_TYPE_XVARM) {
//					selectFolder(tree, event.getRecord().getAttribute("name").toString());
//				}else
				
//				if(tree.getFolderType() == Constants.FOLDER_TYPE_ETC){
//					selectEtcFolder(event.getRecord().getAttribute("id"), false);
//					getFolderFilter(Constants.FOLDER_TYPE_ETC, event.getRecord().getAttribute("id"));
//				}else{
//					selectFolder(tree, Long.parseLong( event.getRecord().getAttribute("folderId")));
//					getFolderFilter(Constants.FOLDER_TYPE_ALL, event.getRecord().getAttribute("folderId"));
//				}
				folderOpenAction(tree, event);
//				DocumentsPanel.get().setRightDefault(false);
			}
		});
        
        // 20130910, junsoo, etc�� �ƴ� tree�� drop ����
//		if(!Constants.MENU_DOCUMENTS_ETC.equals(sectionName)) {
//	    	tree.addDropHandler(new DropHandler() {
//	    		@Override
//	    		public void onDrop(DropEvent event) {
//	    			SDocument[] documents=null;
//	    			if (EventHandler.getDragTarget() instanceof DocumentsGrid) {
//	    				documents = DocumentActionUtil.get().getCurrentDocuments();
//	    			}else	return;
//	    			
//	    			long[] docIds = new long[documents.length];
//	    			for(int i=0 ; i < documents.length ; i++){
//	    				docIds[i] = documents[i].getId();
//	    			}
//	    			
//	    			/*
//	    			 *  �� ������ ����Ǿ�� ���������� ���� ������ ��� ����
//	    			 *  ��� ������ Ȯ�� �����ϰ� �����.
//	    			 * */
//	    			ListGridRecord[] records = tree.getRecords();
//	    			for (ListGridRecord record : records) {
//	    				record.setAttribute("isFolder", true);
//	    			}
//	    			
//	    			// ���� or �̵� ����
//	    			String operator = event.isCtrlKeyDown() ? Clipboard.COPY : Clipboard.CUT;
//	    			moveToFolder(tree, docIds, Long.parseLong(tree.getDropFolder().getAttribute("folderId")), operator);
//	    		}
//	    	});
//		}
	}
	
	/**
	 * D&D ����
	 * @param tree
	 * @param docId
	 * @param folderId
	 * @param operator
	 */
//	private void moveToFolder(final NavigatorForDocument tree, final long[] docId, final long folderId, final String operator){
//		ServiceUtil.folder().paste(Session.get().getSid(), docId, folderId, operator, new AsyncCallback<Void>() {
//			@Override
//			public void onSuccess(Void result) {
//				int treeIndex = Constants.FOLDER_TYPE_MYDOC;	
//				if(tree.getFolderType() == Constants.FOLDER_TYPE_MYDOC) treeIndex= Constants.FOLDER_TYPE_MYDOC; 
//			    else treeIndex= Constants.FOLDER_TYPE_SHARED; 
//				
//				// DropFolder�� �����ؾ� null Exception ������ ����.
//				TreeNode dropNode = tree.getDropFolder();
//				String parentId = "";
//				TreeNode parentNode;
//				
//				// ���� ������ �̵��� folderId�� ����ؾ���.
//				parentId = dropNode.getAttribute("parent");
//				if(parentId == null){
//					parentId = dropNode.getAttribute("folderId");
//				}
//				// ���� ���� �̵��� �ֻ��� ������ ��� ����
//				if(!parentId.equals("5"))
//					parentNode = tree.getTree().findById(String.valueOf(parentId));
//				else{	
//					if(treeIndex == Constants.FOLDER_TYPE_MYDOC)	parentId = "4";
//					else	parentId = "3";
//					
//					parentNode = tree.getTree().findById(String.valueOf(parentId));
//				}
//				// �ֻ��� ������ ��� �ֻ��� ������ refresh�Ѵ�.
//				if(parentNode.getAttribute("name").equals(parentId))	getFolderDataRpc(tree, treeIndex);
//				else	tree.getFolderDataRpcChild(treeIndex, Long.parseLong(parentId), parentNode, "folderId", "name", "parent", "type");
//			}
//			
//			@Override
//			public void onFailure(Throwable caught) {
//				Log.serverError(caught, true);
//			}
//		});
//	}
	
	/**
	 * ������ ������ Ȯ���Ų��.
	 * 
	 * 20130820 taesu
	 * */
	private void folderOpenAction(NavigatorForDocument tree, CellClickEvent event){
		// ���� ������ Ÿ��
		int folderType = tree.getFolderType();
		// Folder Filter �˻����� ��ġ ����
//		SearchItems searchItems;
		
		// Ȯ���ų ������ ID �� ����� ����
		String id;
		// ���� ������ ���� Ÿ�Կ� �ش��ϴ� Search Items�� ��ȯ
		if(folderType == Constants.FOLDER_TYPE_ETC){
//			searchItems = DocumentsPanel.get().getSearchItemsExpire();
			id = event.getRecord().getAttribute("id");
		}
		else{
			if(folderType ==DocumentActionUtil.TYPE_SHARED)
				DocumentActionUtil.get().setActivatedMenuType(DocumentActionUtil.TYPE_SHARED);
			else if(folderType ==DocumentActionUtil.TYPE_MYDOC)
				DocumentActionUtil.get().setActivatedMenuType(DocumentActionUtil.TYPE_MYDOC);
			
//			searchItems = DocumentsPanel.get().getSearchItemsDefault();
			id = event.getRecord().getAttribute("folderId");
		}
		
		selectTreeAction(folderType, id, tree, false);
//		// ���� ������ ������ ������ �˻��� ��ϵ� folderFilter�� ������� Filter ���� �˻� else �⺻ ���� �˻�
//		if(DocumentsPanel.get().getFolderFilter().containsKey(id)){
//			selectTreeAction(folderType, id, tree, true);
//			selectFolderFilterAction(id, searchItems);
//		}
//		else{
//			selectTreeAction(folderType, id, tree, false);
//			searchItems.resetItems(false);
//		}
//		DocumentActionUtil.get().resetSorter();
	}
	
	/**
	 * ���� ������ ������ ����Ǿ��ִ� Folder Filter�� �����Ű�� Filter �˻��� �Ѵ�.
	 * @param id
	 * @param searchItems
	 * */
//	private void selectFolderFilterAction(String id, SearchItems searchItems){
//		searchItems.doSearch(PagingToolStrip.getPagingConfig(1, 10, "creationDate", SortDir.DESC), false, id);
//		DocumentActionUtil.get().changeActionIcon("filter", true);
//		searchItems.setItemValues(DocumentsPanel.get().getFolderFilter().get(id));
//	}
	
	/**
	 *  filter �˻� ������ ���� �⺻ �˻�
	 * */
	private void selectTreeAction(int folderType, String id, NavigatorForDocument tree, boolean isFilter){
		// ���� �˻� ���� ����
		DocumentsPanel.get().setSearch(false);
		// ������ ������ Ȯ���ϰ� ������ �������� �����´�.
		if(folderType == Constants.FOLDER_TYPE_ETC){
			selectEtcFolder(id, false);
			int currentMenu = DocumentActionUtil.get().getActivatedMenuType();
			if(currentMenu == DocumentActionUtil.TYPE_MYDOC || currentMenu == DocumentActionUtil.TYPE_SHARED)
				DocumentsPanel.get().getSearchItemsExpire().resetItems(false);
		}else{
			selectFolder(tree, Long.parseLong(id), isFilter);
			DocumentsPanel.get().getSearchItemsDefault().resetItems(false);
		}
	}

	/**
	 * Context Menu ����
	 * */
	private Menu setupContextMenu(final NavigatorForDocument tree, final SFolder folder, final boolean bShared) {
		final TreeNode selectedNode = (TreeNode) tree.getSelectedRecord();
		final long id = Long.parseLong(selectedNode.getAttribute("folderId"));
		Menu contextMenu = new Menu();

		MenuItem create = new MenuItem();
		create.setTitle(I18N.message("addfolder"));
		create.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			@Override
			public void onClick(MenuItemClickEvent event) {
				onCreate(tree, false);
			}
		});

		MenuItem rename = new MenuItem();
		rename.setTitle(I18N.message("rename"));
		rename.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			@Override
			public void onClick(MenuItemClickEvent event) {
				onRename(tree);
			}
		});

		MenuItem delete = new MenuItem();
		delete.setTitle(I18N.message("remove"));
		delete.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			@Override
			public void onClick(MenuItemClickEvent event) {
				onRemove(tree, id);
			}
		});
		
		MenuItem addfavorite = new MenuItem();
		addfavorite.setTitle(I18N.message("addfavorites"));
		addfavorite.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			@Override
			public void onClick(MenuItemClickEvent event) {
				onAddFavorites(id);
			}
		});		
		
		MenuItem reload = new MenuItem();
		reload.setTitle(I18N.message("reloadChildFolders"));
		reload.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			@Override
			public void onClick(MenuItemClickEvent event) {
				onReload(tree);
			}
		});
		
		MenuItem properties = new MenuItem();
		properties.setTitle(I18N.message("properties"));
		properties.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			@Override
			public void onClick(MenuItemClickEvent event) {
				String folderName = "";
				
				
				if(!bShared) {
					if(Session.get().getHomeFolderId()==folder.getId()) {
						folderName = "Home";
					} else {
						if(folder.getTotalSpace() != null) {
							long uSpace = (long) (folder.getUsedSpace()*0.00000000093132);
							long tSpace = (long) (folder.getTotalSpace()*0.00000000093132);
							folderName = folder.getTotalSpace() != null ? folder.getName()+" <span class='blueItalic'>("+uSpace+"GB / "+tSpace+"GB)</span>" : folder.getName();
						} else {
							folderName = folder.getName();
						}
					}
					
					folder.setPathExtended(getPath(tree, folderName));
				} else {
					
					if(folder.getTotalSpace() != null) {
						long uSpace = (long) (folder.getUsedSpace()*0.00000000093132);
						long tSpace = (long) (folder.getTotalSpace()*0.00000000093132);
						
						folderName = folder.getName()+" <span class='blueItalic'>("+uSpace+"GB / "+tSpace+"GB)</span>";
					} else {
						folderName = folder.getName();
					}
					folder.setPathExtended(getPath(tree, folderName));	
				}
				
				FolderPropertiesWindow.get().show(folder);
			}
		});
		
		MenuItem sharing= new MenuItem();
		sharing.setTitle(I18N.message("s.sharing"));
		sharing.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			@Override
			public void onClick(MenuItemClickEvent event) {
				FolderSharingWindow folderSharingWindow = new FolderSharingWindow();
				folderSharingWindow.addItem(new FolderSharingPanel(folder, folderSharingWindow));
			}
		});	
		
		//yongsoo 20140310	��ġ �ٿ�δ�
		MenuItem batch = new MenuItem();
		batch.setTitle(I18N.message("batch"));
		batch.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			@Override
			public void onClick(MenuItemClickEvent event) {
				batchdownloder batch_window = new batchdownloder();
				batch_window.addItem(new BatchdownloderPanel(folder,batch_window));				
			}
		});
		
		
		
		
		if(bShared){
			String[] permission = folder.getPermissions();
			boolean isAdd = false;
			boolean isRename = false;
			boolean isDelete = false;
			for (int i = 0; i < permission.length; i++) {
				if(permission[i].equals("add")) isAdd = true;
				else if(permission[i].equals("rename")) isRename = true;
				else if(permission[i].equals("delete")) isDelete = true;
			}
			create.setEnabled(isAdd);
			rename.setEnabled(isRename);
			delete.setEnabled(isDelete);
			contextMenu.setItems(create, rename, delete,addfavorite, reload, properties,batch);
		}
		else {
			// soeun �� ���������� �Ӽ� ������
			if (folder.getId() != Session.get().getHomeFolderId()) { 
				
				if(folder.getParentId() == 4) {
					contextMenu.setItems(create, rename, delete, addfavorite, reload, sharing, batch, properties);
				} else {
					contextMenu.setItems(create, rename, delete, addfavorite, reload, sharing, batch);
				}
				
			}
			else {
				// home
				contextMenu.setItems(create, reload, batch, properties);
			}
		}
		
		return contextMenu;
	}
	
	/**
	 * ���� ���� ����� ContextMenu ����
	 * @param folder
	 * @return
	 */
	private Menu setupSharedContextMenu(final SFolder folder) {
		Menu contextMenu = new Menu();
		contextMenu.disable();
		String[] permis = folder.getPermissions();
		String permission = "";

		for (String str : permis) {
			permission += ", " + I18N.message(str);
		}
		permission = permission.substring(2);
		MenuItem properties = new MenuItem();
		properties.setTitle(I18N.message("rights") + " : " + permission);
		
		contextMenu.setItems(properties);
		
		return contextMenu;
	}
	
	// ���� refresh
	/**
	 * �ش������� �ٽ� ����
	 */
	private void onReload(NavigatorForDocument tree) {
		int treeIndex = Constants.FOLDER_TYPE_MYDOC;	
//		if(sectionIsExpanded(Constants.SECTION_NAME_MYDOC)) treeIndex= Constants.FOLDER_TYPE_MYDOC; 
		if(tree.getFolderType() == Constants.FOLDER_TYPE_MYDOC) treeIndex= Constants.FOLDER_TYPE_MYDOC; 
	    else treeIndex= Constants.FOLDER_TYPE_SHARED; 
		
		// �ش� ������ �������̵� ã�Ƽ� �� ���̵�� �������� �ٽ� �޾ƿͼ� �����Ѵ�.
		ListGridRecord rc = tree.getSelectedRecord();
		TreeNode refreshNode = tree.getTree().findById( rc.getAttribute("folderId") );
		tree.getFolderDataRpcChild(treeIndex, rc.getAttributeAsLong("folderId"), refreshNode, "folderId", "name", "parent", "type");
		tree.redraw();
	}
	
	// ���ã�� �߰�
	private void onAddFavorites(final long docid) {
		Useful.ask(I18N.message("question"), I18N.message("confirmfavorite"), new BooleanCallback() {
			@Override
			public void execute(Boolean value) {
				if (value) {
					long[] folderid = new long[1];
					folderid[0] = docid; 
					ServiceUtil.document().addBookmarks(Session.get().getSid(), folderid, 1, new AsyncCallback<Void>() {
						@Override
						public void onSuccess(Void result) {
							SC.say(I18N.message("addbookmarkcoplite"));
						}
						@Override
						public void onFailure(Throwable caught) {
							SCM.warn(caught);
						}
					});
				}
			}
		});
	}	
	
	/* 
	 * ���ؽ�Ʈ �޴� ���� �߰�
	 */
	private void onCreate(final NavigatorForDocument tree, final boolean append) {
		Useful.askforValue(I18N.message("newfolder"), I18N.message("newfoldername"), I18N.message("newfolder"), "200px",
				new ValueCallback() {
					@Override
					public void execute(String value) {
						if (value == null || "".equals(value.trim()))
							return;
	
						TreeNode selectedNode = (TreeNode) tree.getSelectedRecord();
						final SFolder data = new SFolder();
						data.setName(value.trim());
						if (append)	//append
							data.setParentId(Long.parseLong(selectedNode.getAttributeAsString("parent")));
						else	// add
							data.setParentId(Long.parseLong(selectedNode.getAttributeAsString("folderId")));
						data.setDescription("");
						data.setCreatorId(Session.get().getUser().getId() );
						
						// 20140204, junsoo, ���� ������ �������
						if(tree.getFolderType() == Constants.FOLDER_TYPE_MYDOC)
							data.setSecurityProfileId(SSecurityProfile.PROFILE_PRIVATEACL);
						else if( tree.getFolderType() == Constants.FOLDER_TYPE_SHARED)
							data.setSecurityProfileId(SSecurityProfile.PROFILE_INHERITEDACL);
							

						
						service.save(Session.get().getSid(), data, new AsyncCallback<SFolder>() {
	
							@Override
							public void onFailure(Throwable caught) {
								SCM.warn(caught);
							}
	
							@Override
							public void onSuccess(SFolder newFolder) {
								TreeNode selectedNode = (TreeNode) tree.getSelectedRecord();

								// add
								if (!append) {
									int type = 0;
									if(tree.getFolderType() == Constants.FOLDER_TYPE_MYDOC) type = 1;
									else if( tree.getFolderType() == Constants.FOLDER_TYPE_SHARED) type = 0;
								
									// ���� �߰��� ����ȸ
									tree.getFolderDataRpcChild(type, selectedNode.getAttributeAsLong("folderId"), 
											selectedNode, "folderId", "name", "parent", "type");
									
									if (!tree.getTree().isOpen(selectedNode)) {
										tree.getTree().openFolder(selectedNode);
									}
								}
								// append
								else {
									TreeNode parent = tree.getTree().find("folderId", selectedNode.getAttribute("parent"));
									TreeNode newNode = new TreeNode();
									newNode.setAttribute("folderId", newFolder.getId());
									newNode.setAttribute("name", newFolder.getName());
									newNode.setAttribute("parent", newFolder.getParentId());
									newNode.setAttribute("type", newFolder.getType());
									newNode.setAttribute("folder", newFolder);
									tree.getTree().add(newNode, parent);
								}
							}
						});
					}
				});
	}

	// ���ؽ�Ʈ �޴� rename
	private void onRename(final TreeGrid tree) {
		final TreeNode selectedNode = (TreeNode) tree.getSelectedRecord();
		Useful.askforValue(I18N.message("rename"), I18N.message("title"), selectedNode.getAttributeAsString("name"), "200",
				new ValueCallback() {
					@Override
					public void execute(final String value) {
						if (value == null || "".equals(value.trim()))
							return;
	
						service.rename(Session.get().getSid(),
								Long.parseLong(selectedNode.getAttributeAsString("folderId")), value.trim(),
								new AsyncCallback<Void>() {
	
									@Override
									public void onFailure(Throwable caught) {
										SCM.warn(caught);
									}
	
									@Override
									public void onSuccess(Void v) {
										selectedNode.setAttribute("name", value);
										tree.refreshRow(tree.getRecordIndex(selectedNode));
										
										// 20130730, junsoo, folder���� �̸� ����
										SFolder folder = (SFolder)selectedNode.getAttributeAsObject("folder");
										folder.setName(value);
									}
								});
					}
				});
	}	

	// ���ؽ�Ʈ �޴� ����
	private void onRemove(final TreeGrid tree, final long docid) {
		Useful.ask(I18N.message("question"), I18N.message("confirmdelete"), new BooleanCallback() {
			@Override
			public void execute(Boolean value) {
				if (value) {
					service.delete(Session.get().getSid(), docid, new AsyncCallback<Void>() {
						@Override
						public void onFailure(Throwable caught) {
							SCM.warn(caught);
						}

						@Override
						public void onSuccess(Void result) {
							/* ���� �ش� ������ �����ϸ� ������ �߻��ϱ� ������ 
							 * �θ������� ���� ������ �ش������� �����Ѵ�.
							 * ���� �ٽ� ������ �����Ѵ�.
							 */
							TreeNode node = tree.getTree().find("folderId", Long.toString(docid));
							TreeNode parent = tree.getTree().find("folderId", node.getAttribute("parent"));
							tree.getTree().remove(node);
							if (parent != null) {
								tree.getTree().closeFolder(parent);
								tree.getTree().openFolder(parent);
								// ������ ��ȸ�Ѵ�.
								selectFolder(tree, Long.parseLong(node.getAttributeAsString("parent")), false);
							} else {
								selectFolder(tree, Constants.FOLDER_TYPE_SHARED);
							}
							
						}
					});
				}
			}
		});
	}	
	
	// ���������� ���񽺸� ���� �޾ƿ��� �ش� ������ ������ ��ȸ�Ѵ�.
	// xvarm�� �̿��� ������ �ú� �Ķ���Ͱ� �ٸ��� ������ �ΰ�����
	// ó���� �и���.
	// Ʈ���� ù��° ���� �˻�
	public void selectFolder(final TreeGrid tree, int type){
		TreeNode rootNode;
		rootNode = tree.getTree().find("folderId", Long.toString(Constants.DOCUMENTS_FOLDERID));
		
		if (rootNode == null)
			return;
		
		TreeNode[] children = tree.getTree().getChildren(rootNode);
		if (children != null && children.length > 0){
			
			// 20130805, junsoo, xvarm ó�� ������.
//			if(type == 2){
//				service.getFolder(Session.get().getSid(), children[0].getAttribute("name").toString(), SFolder.TYPE_ECM,
//						false, true, new AsyncCallback<SFolder>() {
//			
//							@Override
//							public void onFailure(Throwable caught) {
//								SCM.warn(caught);
//							}
//			
//							@Override
//							public void onSuccess(SFolder folder) {
//								// ���������� ���� �˻�
//								tree.deselectAllRecords();
//								folder.setPathExtended(getPath(tree, folder.getName()));
//								Session.get().setCurrentFolder(folder);
//								tree.selectRecord(0);
//							}
//				});
//			}else{
			// 20130806 taesu, ���� ���ý� �ε� �ð����� ���� CallBack�Լ� ����
				service.getFolder(Session.get().getSid(), Long.parseLong(children[0].getAttribute("folderId").toString()), false, true, new AsyncCallbackWithStatus<SFolder>() {
					@Override
					public String getSuccessMessage() {
						return null;
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
					public void onSuccessEvent(SFolder folder) {
						tree.deselectAllRecords();
						// soeun
						if(folder.getTotalSpace() != null) {
							long uSpace = (long) (folder.getUsedSpace()*0.00000000093132);
							long tSpace = (long) (folder.getTotalSpace()*0.00000000093132);
							tree.getRecord(0).setAttribute("name", "Home <span class='blueItalic'>("+uSpace+"GB / "+tSpace+"GB)</span>");
						}
						
						folder.setPathExtended(getPath(tree, folder.getId()));
						Session.get().setCurrentFolder(folder);
						
						tree.selectRecord(0);
						
//						DocumentsPanel.get().setStyleName("globalDefaultCursor");
					}
				});
//				service.getFolder(Session.get().getSid(), Long.parseLong(children[0].getAttribute("folderId").toString()), false, true, new AsyncCallback<SFolder>() {
//			
//							@Override
//							public void onFailure(Throwable caught) {
//								SCM.warn(caught);
//							}
//			
//							@Override
//							public void onSuccess(SFolder folder) {
//								// ���������� ���� �˻�
//								tree.deselectAllRecords();
//								folder.setPathExtended(getPath(tree, folder.getId()));
//								Session.get().setCurrentFolder(folder);
//								tree.selectRecord(0);
//							}
//				});
//			}
		}
	}
	
	// Xvarm Ʈ���� �ش� ���� �˻�
//	public void selectFolder(final TreeGrid tree, String folderName) {
//		service.getFolder(Session.get().getSid(), folderName, SFolder.TYPE_ECM,
//				false, true, new AsyncCallback<SFolder>() {
//	
//					@Override
//					public void onFailure(Throwable caught) {
//						SCM.warn(caught);
//					}
//	
//					@Override
//					public void onSuccess(SFolder folder) {
//						folder.setPathExtended(getPath(tree, folder.getName()));
//						Session.get().setCurrentFolder(folder);
//					}
//		});
//	}

	/**
	 * NavigatorForDocument.java�� setEtcSectionName �� Ȯ��
	 * */
	public void selectEtcFolder(String folderId, boolean bByHistory){
		// setTrack���� �����߻���. ������� �̿ܿ� ������ �߻��� �� �־ �������Ÿ� ��������� ��.
		DocumentsPanel.get().setFolder(null);
		
		// checkOutList
		if(folderId.equals(EtcMenus.CHECKOUTLIST.getId())){
			DocumentsPanel.get().showCheckedSearch();
			DocumentActionUtil.get().updateETS(DocumentActionUtil.TYPE_CHECKED);
		}
		// favorite
		else if(folderId.equals(EtcMenus.FAVORITE.getId())){
			DocumentsPanel.get().onFavoriteBtnClick();
			DocumentActionUtil.get().updateETS(DocumentActionUtil.TYPE_FAVOR);
		}
		// trash
		else if(folderId.equals(EtcMenus.TRASH.getId())){
			DocumentsPanel.get().onTrashBtnClick();
			DocumentActionUtil.get().updateETS(DocumentActionUtil.TYPE_TRASH);
		}
		// shared trash
		else if(folderId.equals(EtcMenus.SHAREDTRASH.getId())){
			DocumentsPanel.get().onSharedTrashBtnClick();
			DocumentActionUtil.get().updateETS(DocumentActionUtil.TYPE_SHARED_TRASH);
		}
		// expireddoc
//		else if(folderId.equals(EtcMenus.EXPIREDDOC.getId())){
//			DocumentsPanel.get().onExpire();
//			DocumentActionUtil.get().updateETS(DocumentActionUtil.TYPE_EXPIRED);
////			folder.setName(EtcMenus.EXPIREDDOC.getId());
////			Session.get().setCurrentFolder(folder);
//			return;
//		}
		// 20140218, junsoo, approve ���ý� �ƹ��ϵ� �������� ����
		else if(folderId.equals(EtcMenus.APPROVE.getId())){
			return;
		}
		// approveStandby
		else if(folderId.equals(EtcMenus.APPROVESTANDBY.getId())){
			DocumentsPanel.get().onApproveStandBy();
			DocumentActionUtil.get().updateETS(DocumentActionUtil.TYPE_APPROVE_STANDBY);
			return;
		}
		// approveRequest
		else if(folderId.equals(EtcMenus.APPROVEREQUEST.getId())){
			DocumentsPanel.get().onApproveRequest();
			DocumentActionUtil.get().updateETS(DocumentActionUtil.TYPE_APPROVE_REQUEST);
			return;
		}
		// approveComplete
		else if(folderId.equals(EtcMenus.APPROVECOMPLETE.getId())){
			DocumentsPanel.get().onApproveComplete();
			DocumentActionUtil.get().updateETS(DocumentActionUtil.TYPE_APPROVE_COMPLETE);
			return;
		}
		// ���� ���� ����
		else{
			if(!folderId.equals(EtcMenus.SHAREDLIST.getId())){
				SFolder sharedFolder = (SFolder)etcTree.getTree().findById(String.valueOf(folderId)).getAttributeAsObject("folder");
				// ����ڸ��� ������ ������ �ȵǹǷ� �˻��Ǹ� �ȵȴ�.
//				if(sharedFolder.getSharedDepth() > 0){
//					if((Session.get().getCurrentFolder() == null) || (sharedFolder != null && (sharedFolder.getId() != Session.get().getCurrentFolder().getId()))){
					DocumentActionUtil.get().setActivatedMenuType(DocumentActionUtil.TYPE_FOLDER_SHARED);
					Session.get().setCurrentFolder(sharedFolder);
//				}
			}
		}
	
		// �׸� ������ �Ǿ� ���� ���� ���� �����Ƿ� ���� (��: �����丮, ���¹�)
		expandSection(Constants.MENU_DOCUMENTS_ETC);
		// 20130906, junsoo, lazy loading�Ǿ� etcTree�� null�� �� �� ����.
		if (etcTree != null) {
			TreeNode orgNode = etcTree.getTree().findById(String.valueOf(folderId) );
			if(orgNode != null)
				etcTree.selectRecord(orgNode);
		}

		// 20130731, junsoo, set history
		if (!bByHistory)
			HistoryUtil.get().newHistory(this, Constants.MENU_DOCUMENTS + ";" + Constants.MENU_DOCUMENTS_ETC + ";" + folderId);
	}

	/**
	 * ��������� ���� ���ý� ���� ������ ������.
	 * folderDepth -1 : ��Ʈ, 0 : ������ ����, �� �� : ���� ������
	 * @param folder
	 */
	public void getSharedFolderData(final SFolder folder){
		ServiceUtil.folder().listFolderByShare(Session.get().getSid(), folder.getId(), folder.getSharedUserId(), folder.getSharedFolderId(), folder.getSharedDepth(), new AsyncCallback<List<SFolder>>() {
			@Override
			public void onSuccess(List<SFolder> result) {
				for (SFolder sfolder : result) {
					if(sfolder.getParentId() != 0){
						TreeNode node = new TreeNode();
						node.setID(String.valueOf(sfolder.getId()));
						node.setAttribute("id", sfolder.getId());
						node.setAttribute("name", sfolder.getName());
						node.setAttribute("parent", sfolder.getParentId());
						node.setAttribute("folder", sfolder);
						if(sfolder.getSubfolderCount() > 0){
							node.setIsFolder(true);
						}
						
						TreeNode parentNode = etcTree.getTree().findById(String.valueOf(folder.getId()));
						if(parentNode != null && etcTree.getTree().findById(String.valueOf(sfolder.getId())) == null){
							etcTree.getTree().add(node, parentNode);
						}
					}
				}
			}
			@Override
			public void onFailure(Throwable caught) {
				Log.serverError(caught, false);
			}
		});
	}
	/**
	 *  mydoc & shareddoc Ʈ���� �ش� ���� �˻� �� ���� ������ �Ǿ����� ��� ���� �˻� 
	 *  @param tree
	 *  @param folderId
	 *  @param isFilter
	 * */
	public void selectFolder(final TreeGrid tree, long folderId, final boolean isFilter) {
		service.getFolder(Session.get().getSid(), folderId, false, true, new AsyncCallback<SFolder>() {
			@Override
			public void onFailure(Throwable caught) {
				SCM.warn(caught);
			}
			@Override
			public void onSuccess(SFolder result) {
				result.setPathExtended(getPath(tree, result.getId()));
				Session.get().setCurrentFolder(result, isFilter);
			}
		});
	}
	
	/**
	 * ���� ���̵�� Ʈ������ ��ü ��� ��������
	 * */ 
	public String getPath(final TreeGrid tree, long folderId) {
		TreeNode selectedNode = tree.getTree().find("folderId", Long.toString(folderId));
		String path = "";
		TreeNode[] parents = tree.getTree().getParents(selectedNode);
		for (int i = parents.length - 2; i >= 0; i--) {
			if (parents[i].getName() != null && !"/".equals(parents[i].getName()))
				path += "/" + parents[i].getName();
		}
		path += "/" + (selectedNode.getName().equals("/") ? "" : selectedNode.getName());
		return path;
	}
	
	/**
	 *  ���� �̸����� Ʈ������ ���� ��� ��������
	 * */
	public String getPath(final TreeGrid tree, String folderName) {

		// soeun Ʈ�� ��� id�� �ҷ����� ��
		TreeNode selectedNode;
		if("Home".equals(folderName)) {
			selectedNode = tree.getTree().findById(String.valueOf(Session.get().getHomeFolderId()));
		} else {
			selectedNode = tree.getTree().find("name", folderName);
		}
		
		String path = "";
		TreeNode[] parents = tree.getTree().getParents(selectedNode);
		for (int i = parents.length - 1; i >= 0; i--) {
			if (parents[i].getName() != null && !"/".equals(parents[i].getName())) {
				
				if(parents[i].getName().startsWith("Home")) {
					parents[i].setName("Home");
				}
				path += "/" + parents[i].getName();
			}
		}
		
		if(selectedNode.getName().contains("(")) {
			selectedNode.setName(folderName.split("\\(")[0]);
		}
		path += "/" + (selectedNode.getName().equals("/") ? "" : selectedNode.getName());
		
		selectedNode.setName(folderName);
		
		return path;
	}	
	
	/**
	 * ���� �ε����� ���� Ȯ�� �ϱ�
	 * 20130903, junsoo, ������ �������� �����ǹǷ� �� �Լ��� ����ϸ� �ȵ�. expandSection(ID) �� ����� ��!
	 * */ 
//	public void expand(int section){
//		expandSection(section);
//	}
	
	/**
	 * ���������� �̿��Ͽ� �ش� ���� ������ �ִ� ���� ��ġ�� �����ش�.
	 * foldertype ���� �ش� ������ xvarm, mydoc, shared���� Ȯ��
	 * foldertype : 2 xvarm
	 * foldertype : 1 mydoc
	 * foldertype : 0 shared
	 * originalFolderId : ������ �����ִ� ����Ÿ�� ������ ���̵��̴�.
	 */
	public void expandFolder(SFolder folder){
		expandFolder(folder, false);
	}
	
	public void expandFolder(SFolder folder, boolean bByHistory){
		final int foldertype = folder.getType();
		final long originalFolderId = folder.getId();
		expandCommand = true;
		
		/*
		 * �̹� ���� ������ Ÿ�ٰ�α��� ������� �ִ°�� 
		 * ���������� �ٽ� �޾ƿͼ� ������ �ʿ䰡 �����Ƿ� 
		 * 1. �ش� ��θ� ã�´�. getPath()
		 * 2. ���������� ��θ� �����Ѵ�. folder.setPathExtended
		 * 3. ���ǿ� �ش� ���� ������ �Ѱ��ְ� obserber�� Ȱ���Ͽ� �ش������� ��ȸ �ǵ��� �Ѵ�. Session.get().setCurrentFolder(folder)
		 * 4. �ش� ������ ���û��·� �����. selectRecord
		 * ========================================================================================================================
		 */
//		if(foldertype == 2){
//			expand(2);
//			TreeNode orgNode = XvarmTree.getTree().findById(String.valueOf(originalFolderId) );
//			
//			if(orgNode != null){
//				folder.setPathExtended(getPath(XvarmTree, originalFolderId));
//				Session.get().setCurrentFolder(folder, bByHistory);
//				XvarmTree.deselectAllRecords();
//				XvarmTree.selectRecord(orgNode);
//				return;
//			}
//		}else
		//20131206 na ��� ������ ������ ��Ȱ��ȭ
		if(MydocTree != null) MydocTree.deselectAllRecords();
		if(SharedocTree != null )SharedocTree.deselectAllRecords();
		if(etcTree != null) etcTree.deselectAllRecords();
		
//		if(foldertype == 1){
//			expandSection(Constants.MENU_DOCUMENTS_MYDOC);
//			if (MydocTree != null) {				
//
//				TreeNode orgNode = MydocTree.getTree().findById(String.valueOf(originalFolderId) );
//				
//				if(orgNode != null){
//					folder.setPathExtended(getPath(MydocTree, originalFolderId));
//					Session.get().setCurrentFolder(folder, bByHistory, false);
//					MydocTree.selectRecord(orgNode);
//					// 20130801, open folder
//					MydocTree.getTree().openAll(orgNode);
//					return;
//				}
//			}
//			
//		}else if(foldertype == 0){
//			expandSection(Constants.MENU_DOCUMENTS_SHAREDDOC);
//			if (SharedocTree != null) {				
//				TreeNode orgNode = SharedocTree.getTree().findById(String.valueOf(originalFolderId) );
//				
//				if(orgNode != null){
//					folder.setPathExtended(getPath(SharedocTree, originalFolderId));
//					Session.get().setCurrentFolder(folder, bByHistory, false);
//					SharedocTree.selectRecord(orgNode);
//					// 20130801, open folder
//					SharedocTree.getTree().openFolder(orgNode);
//					return;
//				}
//			}
//		}
		/*�̹� ���� ������ Ÿ�ٰ�α��� ������� �ִ°�� �Ϸ�
		 * ========================================================================================================================
		 */
		
		/*
		 * ���� ������ ��α��� ������� ���� ������� 
		 * ���񽺸� ȣ�� ������ ������ �޾ƿͼ� Ÿ���������� �ݺ��Ͽ� �����Ѵ�.
		 * foldertype == 2 �ΰ�� xvarm �̹Ƿ� ������ ������ ��� �̱� ������ ������ �ܰ������� �޾ƿ� 
		 * �ʿ� ���� �ѹ��� ȣ���Ѵ�.
		 * 
		 * 1. findFolderAndExpandRpcFromRoot(��������, ����Ÿ��, ����Ÿ�� ���� ���̵�)
		 * 2. findFolderAndExpandRpc(��������, ����Ÿ��, ã������� ������ �����ε���, ã�������� ������ �������̵�����, ����Ÿ�� ���� ���̵�)
		 * 
		 * getFolder ���񽺸� ���� �ش� ������ Ʈ�� ������ �޾Ƽ�
		 * arrayFolder�� �����Ѵ�.
		 * SFolder[] arrayFolder = result.getRefinedPath();
		 * 
		 * arrayFolder.length �� 0�� ��� �ٷ� ��Ʈ�� ��ġ�� ��� �̹Ƿ� 1���� ȣ���Ͽ� ��Ʈ������ �޾ƿ��� �Ϸ�ȴ�.
		 * 
		 * for(int i= 0; i< arrayFolder.length; i++)
		 * ������ ��Ʈ��κ��� ������α��� �ϳ��� �˻��Ͽ� ����Ʈ���� ���õǾ� ���� �ʴٸ� ���񽺸� ȣ���Ѵ�.
		 * orgNode = getTree().findById(String.valueOf(arrayFolder[i].getId()) 
		 * if(orgNode == null) ���� ���� Ʈ���� �������� �ʴ� ���� �̹Ƿ� �ش� �������� ���񽺸� ���Ͽ� �޾ƿ´�.
		 * 
		 * ���� i==0 ���� �������� �������� �ʴ� �ٸ� 1�� ȣ�� 1�� �Լ��ȿ��� ����Ÿ�� �������� ã�ư�.
		 *        i>0 �ٸ� �ش� ����arrayFolder[i] ���� 2���� ȣ���Ͽ� ����Ÿ�� �������� ã�ư�.
		 * ========================================================================================================================
		 */
//		if(foldertype == 2){
//			service.getFolder(Session.get().getSid(), folder.getName(), SFolder.TYPE_ECM, 
//					true, false, new AsyncCallback<SFolder>() {
//						@Override
//						public void onSuccess(SFolder result) {
//								findFolderAndExpandRpcFromRoot(result, foldertype, originalFolderId);
//								return;
//						}
//						
//						@Override
//						public void onFailure(Throwable caught) {
//							SCM.warn(caught);
//						}
//					});
//		}else{
//			GWT.log("target folder id : " + originalFolderId);
		// 20130822, junsoo, permission ���������� �߰�.
		if (originalFolderId != 0L){
			service.getFolder(Session.get().getSid(), originalFolderId, true, true, new AsyncCallback<SFolder>(){
				@Override
				public void onSuccess(SFolder result) {
					SFolder[] arrayFolder = result.getRefinedPath();
					TreeNode orgNode;
					
//					GWT.log("length : " + arrayFolder.length);
					if (arrayFolder.length == 0) {
						findFolderAndExpandRpcFromRoot(result, foldertype, originalFolderId);
						return;
					}
					
					for(int i= 0; i< arrayFolder.length; i++){
//						orgNode = XvarmTree.getTree().findById(String.valueOf(arrayFolder[i].getId()) );
//						if(orgNode == null){
							if(foldertype == 1){
								if (i == 0) findFolderAndExpandRpcFromRoot(result, foldertype, originalFolderId);
								else findFolderAndExpandRpc(result, foldertype, i, arrayFolder, originalFolderId);
							}else{
								if (i == 0) findFolderAndExpandRpcFromRoot(result, foldertype, originalFolderId);
								else findFolderAndExpandRpc(result, foldertype, i, arrayFolder, originalFolderId);
							}
							break;
//						}
					}

				}
				
				@Override
				public void onFailure(Throwable caught) {
					SCM.warn(caught);
				}
			});
		}
		else {
			// 20140519, junsoo, folderId�� 0�̸� Root ���� ��������
			findFolderAndExpandRpcFromRoot(folder, foldertype, 0);
		}
		
//		}
		
	}
	
	/*
	 * ���ã�⿡�� ���� ����Ŭ���� �ش� ������ ã�Ƽ� Ȯ���ϴ� �Լ�
	 * findSuccess : ��ġ�ϴ� ���� ���̵� �߰��ϸ� true
	 * arrayFolder : ã������ ������ ��ü �����迭
	 * ��Ʈ���� �˻�����
	 * findFolderAndExpandRpcFromRoot(��������, ����Ÿ��, ����Ÿ�� ���� ���̵�)
	 */
	private boolean findSuccess;
	public void findFolderAndExpandRpcFromRoot(final SFolder folder, final int treeType, final long originalId){
		final SFolder[] arrayFolder = folder.getRefinedPath();
		findSuccess = false;
		service.listFolderByTypeAndParentId(Session.get().getSid(), treeType, 0, new AsyncCallback<List<SFolder>>() {
			@Override
			public void onSuccess(List<SFolder> result) {
				
				SFolder[] sfolder = new SFolder[result.size()];
				
				TreeNode rootNode = new TreeNode();
				rootNode.setAttribute("folderId", Long.toString(Constants.DOCUMENTS_FOLDERID));
				rootNode.setAttribute("name", "root");
//				TreeNode[] returnNode = new TreeNode[result.size()];
//				for(int i=0; i< result.size(); i++){
//					sfolder[i] = result.get(i);
//					returnNode[i] = new TreeNode();
//					returnNode[i].setAttribute("folderId", sfolder[i].getId());
//					returnNode[i].setAttribute("name", sfolder[i].getName());
//					returnNode[i].setAttribute("parent", sfolder[i].getParentId());
//					returnNode[i].setAttribute("type", sfolder[i].getType());
//					
//					if(sfolder[i].isParentOrNot()){
//						returnNode[i].setAttribute("expand", true);
//						returnNode[i].setIsFolder(true);
//					}
//					else{
//						returnNode[i].setAttribute("expand", false);
//						returnNode[i].setIsFolder(false);
//					}
//			    
//					// ã������ �ϴ� ������ ���̵�� ��ġ�ϴ��� üũ
//					if(originalId == sfolder[i].getId()) findSuccess = true;
//				}
				
				boolean isShowVirtualRoot = true;
				if (treeType != Constants.FOLDER_TYPE_MYDOC)
					isShowVirtualRoot = false;

				int root = isShowVirtualRoot ? 1 : 0;

				TreeNode[] returnNode = new TreeNode[result.size()+root];
				boolean isParentOrNot;
				
				if(isShowVirtualRoot) {
					returnNode[0] =  new TreeNode();
					returnNode[0].setAttribute("folderId", Session.get().getHomeFolderId());
					returnNode[0].setAttribute("name", "Home");
					returnNode[0].setAttribute("parent", Constants.DOCUMENTS_FOLDERID);
					returnNode[0].setAttribute("type", Constants.FOLDER_TYPE_MYDOC);

					// 20130730, junsoo, home sfolder setting
					SFolder home = new SFolder();
					home.setId(Session.get().getHomeFolderId());
					home.setName("Home");
					home.setParentId(Constants.DOCUMENTS_FOLDERID);
					home.setType(Constants.FOLDER_TYPE_MYDOC);
					returnNode[0].setAttribute("folder", home);
					
					// ã������ �ϴ� ������ ���̵�� ��ġ�ϴ��� üũ
					if(originalId == home.getId()) findSuccess = true;
					
					rootNode.setAttribute("folderId", Session.get().getHomeFolderId());
					rootNode.setAttribute("name", "Home");
				}
				else{
					rootNode.setAttribute("folderId", Constants.SHARED_DEFAULTID);
					rootNode.setAttribute("name", I18N.message("shareddoc"));
				}
				
				for(int i=0; i< result.size(); i++){
					sfolder[i] = result.get(i);
					returnNode[i+root] = new TreeNode();
					returnNode[i+root].setAttribute("folderId", sfolder[i].getId());
					returnNode[i+root].setAttribute("name", sfolder[i].getName());
					returnNode[i+root].setAttribute("parent", sfolder[i].getParentId());
					returnNode[i+root].setAttribute("type", sfolder[i].getType());
					returnNode[i+root].setAttribute("folder", sfolder[i]);

					if(sfolder[i].isParentOrNot()){
						returnNode[i+root].setAttribute("expand", true);
//						returnNode[i+root].setIsFolder(true);
					}
					else{
						returnNode[i+root].setAttribute("expand", false);
//						returnNode[i+root].setIsFolder(false);
					}
					returnNode[i+root].setIsFolder(true);
			    
					// ã������ �ϴ� ������ ���̵�� ��ġ�ϴ��� üũ
					if(originalId == sfolder[i].getId()) findSuccess = true;

				}
				
				Tree dataTree = new Tree();
				dataTree.setModelType(TreeModelType.PARENT);
				dataTree.setIdField("folderId");
				dataTree.setParentIdField("parent");
				dataTree.setNameProperty("name");
				
				dataTree.setAutoOpenRoot(true);
				dataTree.setReportCollisions(false);
				dataTree.setShowRoot(false);
				
				dataTree.setRoot(rootNode);
				dataTree.setData(returnNode);
				// ���� ��Ī���θ� �˻��ϰ� ���� ������ �˻� ����
//				if(treeType == Constants.FOLDER_TYPE_XVARM){
//					processFolderMaching(XvarmTree, dataTree,  folder, treeType, arrayFolder, originalId);
//				}else
				if(treeType == Constants.FOLDER_TYPE_MYDOC){
					processFolderMaching(MydocTree, dataTree,  folder, treeType, arrayFolder, originalId);
				}else{
					processFolderMaching(SharedocTree, dataTree,  folder, treeType, arrayFolder, originalId);
				}
				
			}
			
			@Override
			public void onFailure(Throwable caught) {
				GWT.log(caught.getMessage());
			}
		});
		
	}
	
	/*
	 * 1�� ���� �����Ŀ� ��Ī�۾� 1���� �������� ã������ ������ �����ϸ�
	 * �ش� ������ �����ϰ� ����� ������ ��ȸ�Ѵ�.
	 * ��Ī���н� ������ �����ϰ� �������� ã�⸦ �����Ѵ�.
	 */
	private void processFolderMaching(NavigatorForDocument treeGrid, Tree dataTree, SFolder folder, int treeType, SFolder[] arrayFolder, long originalId){
		treeGrid.setData(dataTree);
		treeGrid.refreshFields();
		
		if(findSuccess){
			// ã������ ����
			TreeNode orgNode = treeGrid.getTree().findById(String.valueOf(originalId) );
			
			// ����ã�� ����
			if(orgNode != null){
				folder.setPathExtended(getPath(treeGrid, originalId));
				// �ش� ���� ��ȸ
				Session.get().setCurrentFolder(folder);
				// �ش� ���� ����
				treeGrid.selectRecord(orgNode);
				return;
			}
		// ����ã�� ����	
		}else{
			if (arrayFolder != null && arrayFolder.length > 0) {
				// ���� ���� ����
				TreeNode currNode = treeGrid.getTree().findById(String.valueOf(arrayFolder[0].getId()) );
				treeGrid.getTree().openFolder(currNode);
				// ���� ���� �˻� ����
				findFolderAndExpandRpc(folder, treeType, 1, arrayFolder, originalId);
			}
		}
	}
	
	/*
	 * ����Ʈ�� Ư���������� �ش������� ã�� ������ �˻��ϴ� �Լ�
	 * findSuccess : ��ġ�ϴ� ���� ���̵� �߰��ϸ� true
	 * findFolderAndExpandRpc(��������, ����Ÿ��, ã������� ������ �����ε���, ã�������� ������ �������̵�����, ����Ÿ�� ���� ���̵�)
	 */
	public void findFolderAndExpandRpc(final SFolder folder, final int treeType, final int startIdx, final SFolder[] arrayFolder,final long originalId){
		findSuccess = false;
		service.listFolderByTypeAndParentId(Session.get().getSid(), treeType, arrayFolder[startIdx -1].getId(), new AsyncCallback<List<SFolder>>() {
			@Override
			public void onSuccess(List<SFolder> result) {
				
				SFolder[] sfolder = new SFolder[result.size()];
				
				TreeNode[] returnNode = new TreeNode[result.size()];
				for(int i=0; i< result.size(); i++){
					sfolder[i] = result.get(i);
					returnNode[i] = new TreeNode();
					
					returnNode[i].setAttribute("folderId", sfolder[i].getId());
					
					// soeun �����뷮/�ִ��뷮 ǥ��
					if(sfolder[i].getUsedSpace() != null) {
						returnNode[i].setAttribute("name",sfolder[i].getName()+" ("+sfolder[i].getUsedSpace()+"/"+sfolder[i].getTotalSpace()+")");
					} else {
						returnNode[i].setAttribute("name",sfolder[i].getName());
					}
					returnNode[i].setAttribute("parent", sfolder[i].getParentId());
					returnNode[i].setAttribute("type", sfolder[i].getType());
					returnNode[i].setAttribute("folder", sfolder[i]);

					if(sfolder[i].isParentOrNot()){
						returnNode[i].setAttribute("expand", true);
//						returnNode[i].setIsFolder(true);
					}
					else{
						returnNode[i].setAttribute("expand", false);
//						returnNode[i].setIsFolder(false);
					}
					returnNode[i].setIsFolder(true);

					
//					if(treeType == Constants.FOLDER_TYPE_XVARM){
//						TreeNode selectNode = XvarmTree.getTree().findById(String.valueOf( arrayFolder[startIdx-1].getId() ) );
//						XvarmTree.getTree().add(returnNode[i], selectNode);
//					}else
					if(treeType == Constants.FOLDER_TYPE_MYDOC){
						TreeNode selectNode = MydocTree.getTree().findById(String.valueOf( arrayFolder[startIdx-1].getId() ) );
						MydocTree.getTree().add(returnNode[i], selectNode);
					}else{
						TreeNode selectNode = SharedocTree.getTree().findById(String.valueOf( arrayFolder[startIdx-1].getId() ) );
						SharedocTree.getTree().add(returnNode[i], selectNode);
					}
						
					// ��ġ���� �Ǵ�
					if(originalId == sfolder[i].getId()) findSuccess = true;
			    }
				
				// ��Ī���۾� ����
//				if(treeType == 2){
//					processFolderMachingChild(XvarmTree, folder, treeType, startIdx, arrayFolder, originalId);
//				}else 
				if(treeType == 1){
					processFolderMachingChild(MydocTree, folder, treeType, startIdx, arrayFolder, originalId);
				}else{
					processFolderMachingChild(SharedocTree, folder, treeType, startIdx, arrayFolder, originalId);
				}
			}
			
			@Override
			public void onFailure(Throwable caught) {
				Log.serverError(caught, true);
			}
		});
		
	}
	
	/*
	 * �ش� ������ �����ϰ� ����� ������ ��ȸ�Ѵ�.
	 * ��Ī���н� ������ �����ϰ� �ٽ� �������� ã�⸦ �����Ѵ�.
	 */
	private void processFolderMachingChild(NavigatorForDocument treeGrid, SFolder folder, int treeType, int startIdx, SFolder[] arrayFolder, long originalId){
		treeGrid.refreshFields();
		if(findSuccess){
			// ã������ ����
			TreeNode orgNode = treeGrid.getTree().findById(String.valueOf(originalId) );
			
			if(orgNode != null){
				folder.setPathExtended(getPath(treeGrid, originalId));
				// ���� ���� �˻�
				Session.get().setCurrentFolder(folder);
				// ���� ����
				treeGrid.selectRecord(orgNode);
				return;
			}
		}else{
			//���� ����
			TreeNode currNode = treeGrid.getTree().findById(String.valueOf(arrayFolder[startIdx].getId()) );
			treeGrid.getTree().openFolder(currNode);
			// ���� ������ ��˻� ����
			findFolderAndExpandRpc(folder, treeType, startIdx + 1, arrayFolder, originalId);
		}
	}
	
	@Override
	public void selectByHistory(String refid) {
		String[] tags = refid.split(";");
		if (tags != null && tags.length > 0) {
			if (Constants.MENU_DOCUMENTS.equals(tags[0]) && tags.length > 2) {
				selectMenu(tags[1], tags[2], true);
			}
		}
		Session.get().setCurrentMenuId(refid);
	}
	
	@Override
	public void onHistoryAdded(String refid) {
		Session.get().setCurrentMenuId(refid);
	}
	
	
	// 20130726,junsoo, Ư�� ��ġ�� ����
	@Override
	public void selectMenu(String name, String subMenu, boolean bByHistory) {
		Log.debug("[DocumentsMenu] selectMenu");

		// expand tab
		MainPanel.get().selectDocumentsTab();
		
		SFolder folder = new SFolder();
		if (name == null || name.length() < 1) {
			folder.setType(Constants.FOLDER_TYPE_MYDOC);
			folder.setId(Session.get().getHomeFolderId());
		}
		else {
			
			if (Constants.MENU_DOCUMENTS_MYDOC.equals(name))
				folder.setType(Constants.FOLDER_TYPE_MYDOC);
			else if (Constants.MENU_DOCUMENTS_SHAREDDOC.equals(name))
				folder.setType(Constants.FOLDER_TYPE_SHARED);
			else if (Constants.MENU_DOCUMENTS_ETC.equals(name)) {
				selectEtcFolder(subMenu, bByHistory);
				return;
			}
			
			try {
				if (subMenu != null && subMenu.length() > 0)
					folder.setId(Long.parseLong(subMenu));
				else {
					folder.setId(0L);
				}
			} catch (Exception e) {
				Log.debug(e.getMessage());
				return;
			}
		}
		
		// 20140519, �ּ� ó����. 0 �״�� �Ѱܼ� root���� ���������� �ϱ� ����.
		// 20140429, junsoo, folder id �� ���� ��� ����
//		if (folder.getId() == 0L) {
//			if (Constants.FOLDER_TYPE_MYDOC == folder.getType()) {
//				folder.setId(Session.get().getHomeFolderId());
//
//			} else if (Constants.FOLDER_TYPE_SHARED == folder.getType()) {
//				folder.setId(Constants.SHARED_DEFAULTID);
//			}
//		}
		
		//20150209na ������ �������� ���� ����
		final int foldertype = folder.getType();
		final long originalFolderId = folder.getId();
		
		if(foldertype == 1){
			expandSection(Constants.MENU_DOCUMENTS_MYDOC);
			if (MydocTree != null) {				

				TreeNode orgNode = MydocTree.getTree().findById(String.valueOf(originalFolderId) );
				
				if(orgNode != null){
					folder.setPathExtended(getPath(MydocTree, originalFolderId));
					Session.get().setCurrentFolder(folder, bByHistory, false);
					MydocTree.selectRecord(orgNode);
					// 20130801, open folder
					MydocTree.getTree().openAll(orgNode);
					return;
				}
			}
			
		}else if(foldertype == 0){
			expandSection(Constants.MENU_DOCUMENTS_SHAREDDOC);
			if (SharedocTree != null) {				
				TreeNode orgNode = SharedocTree.getTree().findById(String.valueOf(originalFolderId) );
				
				if(orgNode != null){
					folder.setPathExtended(getPath(SharedocTree, originalFolderId));
					Session.get().setCurrentFolder(folder, bByHistory, false);
					SharedocTree.selectRecord(orgNode);
					// 20130801, open folder
					SharedocTree.getTree().openFolder(orgNode);
					return;
				}
			}
		}
		
		expandFolder(folder, true);
	}
	
	// 20130903, junsoo, ������ ���ѿ� ���� ���� ������
	@Override
	public void buildMenu(final String finalCallbackId, final long parentMenuId, boolean hasHistory) {
		Log.debug("[DocumentsMenu] building menu..");
		AdminMenuUtil.get().hasPriv(finalCallbackId, parentMenuId, Constants.MENU_DOCUMENTS_MYDOC, new AsyncCallback<Long>() {
			@Override
			public void onSuccess(Long id) {
				// 20130906, junsoo, ���� üũ �߰�.
				if (!Session.get().isDelegator() || Session.get().hasDelegationPriv(Constants.MENU_DOCUMENTS_MYDOC)) {
					if (id != null) {
						/*
						 *  ������ ������ ����� �ش� ���ǿ� ����Ʈ���� �����.
						 *  prepareSection(����, Ʈ��, ���ǳ���, ����Ÿ��)
						 */
						MydocSection = new SectionStackSection(I18N.message("mydoc"));
						MydocSection.setID(Constants.MENU_DOCUMENTS_MYDOC);
						MydocTree = new NavigatorForDocument(Constants.FOLDER_TYPE_MYDOC);
						prepareSection(MydocSection, MydocTree, Constants.MENU_DOCUMENTS_MYDOC);
					}
				}
				
				// 20130906, junsoo, ���� üũ �߰�.
				if (!Session.get().isDelegator() || Session.get().hasDelegationPriv(Constants.MENU_DOCUMENTS_SHAREDDOC)) {
					AdminMenuUtil.get().hasPriv(finalCallbackId, parentMenuId, Constants.MENU_DOCUMENTS_SHAREDDOC, new AsyncCallback<Long>() {
						@Override
						public void onSuccess(Long id) {
							if (id != null) {
								SharedocSection = new SectionStackSection(I18N.message("shareddoc"));
								SharedocSection.setID(Constants.MENU_DOCUMENTS_SHAREDDOC);
								SharedocTree = new NavigatorForDocument(Constants.FOLDER_TYPE_SHARED);
						        prepareSection(SharedocSection, SharedocTree, Constants.MENU_DOCUMENTS_SHAREDDOC);
							}
						}
						@Override
						public void onFailure(Throwable caught) {}
					});
				}				

				// 20130906, junsoo, ���� üũ �߰�.
				if (!Session.get().isDelegator() || Session.get().hasDelegationPriv(EtcMenus.APPROVE.getId())) {
					AdminMenuUtil.get().hasPriv(finalCallbackId, parentMenuId, Constants.MENU_DOCUMENTS_ETC, new AsyncCallback<Long>() {
						@Override
						public void onSuccess(Long id) {
							if (id != null) {
								// sub �޴� ���� 13.7.29 taesu
								etcSection = new SectionStackSection(I18N.message("second.client.documentetc"));
								etcSection.setID(Constants.MENU_DOCUMENTS_ETC);
								etcTree = new NavigatorForDocument(Constants.FOLDER_TYPE_ETC);
								etcTree.addFolderOpenedHandler(new FolderOpenedHandler() {
									@Override
									public void onFolderOpened(FolderOpenedEvent event) {
										if((SFolder)event.getNode().getAttributeAsObject("folder") != null)
											getSharedFolderData((SFolder)event.getNode().getAttributeAsObject("folder"));
									}
								});
								prepareSection(etcSection, etcTree, Constants.MENU_DOCUMENTS_ETC);
	
								// 20130805, junsoo, etc �� ó���� ����
//								expandSection(Constants.MENU_DOCUMENTS_ETC);
							}
						}
						@Override
						public void onFailure(Throwable caught) {}
					});
				}

			}
			@Override
			public void onFailure(Throwable caught) {
				Log.debug(caught.getMessage());
			}
		});
	}

	public NavigatorForDocument getMydocTree() {
		return MydocTree;
	}

	// 20130805, junsoo, documents���� ���� ��ġ�� ���� 
	private String currentMenu = null;

	public String getCurrentMenu() {
		return currentMenu;
	}

	public void setCurrentMenu(String currentMenu) {
		this.currentMenu = currentMenu;
	}

}