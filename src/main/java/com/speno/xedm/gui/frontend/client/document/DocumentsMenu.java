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
 * 		20130805, junsoo, xvarm section 삭제. (검색에서 구현할 예정이므로 삭제함.)
 * @author deluxjun
 *
 */
public class DocumentsMenu extends SectionStack implements IFAdminMenuPriv, IFHistoryObserver{
	// xvarm 트리
//	public NavigatorForDocument XvarmTree;
	// mydoc 트리
	public NavigatorForDocument MydocTree;
	// sharedoc 트리
	public  NavigatorForDocument SharedocTree;
	// etc 트리
	public NavigatorForDocument etcTree;
	
	
	// xvarm 섹션
//	protected SectionStackSection XvarmSection = null;
	// mydoc 섹션
	protected SectionStackSection MydocSection = null;
	// shareddoc 섹션
	protected SectionStackSection SharedocSection = null;
	// etc 섹션
	protected SectionStackSection etcSection = null;
	// 첫번째 섹션이 클릭돼어 열리면 폴더를 정보를 서비스를 통해서
	// 가져오고 그다음 부턴 첫폴더의 문서를 조회하기 위해서 
	// 첫번째 섹션 클릭여부를 기록한다.
//	private boolean firstTimeXvarm = true;
	private boolean firstTimeMydoc = true;
	private boolean firstTimeShareDoc = true;
	
	// 즐겨찾기등에서 더블클릭으로 폴더를 찾아가면 원래는 자동으로 해당폴더의
	// 속성을 표시하지만 이때에는 폴더의 해당문서가 선택돼면서 문서 속성이 보여진다.
	// 문서속성이 보여지기 전에 쓸데없이 폴더 속성이 먼서 검색돼는 것을 막기위해 사용.
	public boolean expandCommand = false;
	
	private FolderServiceAsync service = (FolderServiceAsync) GWT.create(FolderService.class);

	// 20130727, junsoo, DocumentsPanel 에서 생성하여 사용하기 때문에, singletone 사용 금지!!!
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
		// 20130903, junsoo, 섹션의 권한에 따른 로딩으로 생성자에서 생성은 모두 주석 처리함.
//		/*
//		 *  각각의 섹션을 만들고 해당 섹션에 폴더트리를 만든다.
//		 *  prepareSection(섹션, 트리, 섹션네임, 서비스타입)
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
//		// 20130805, junsoo, etc 는 처음에 오픈
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
         * 섹션을 클릭했을때
         * 해당 폴더를 가져오거나 첫번째 폴더안에 문서조회 시작
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
	
					// 20130731, junsoo, treegrid 생성시 초기화되므로 삭제
//					getFolderDataRpc(etcTree, Constants.FOLDER_TYPE_ETC); 
				}
			}	
		});        
    }
	
	// 20130903, junsoo, 사용되지 않으므로 주석처리함.
//	public void reset(){
//		collapseSection("documents_mydoc");
//		collapseSection("documents_shareddoc");
//		expandSection("documents_etc");
//	}
	
//	// 초기 화면시작과 함께 mydoc 시작
//	public void refreshFirst(){
//		if (!firstTimeMydoc || !firstTimeShareDoc )
//			return;
//		
//		if(firstTimeMydoc) getFolderDataRpc(MydocTree, Constants.FOLDER_TYPE_MYDOC); 
//		else selectFolder(MydocTree, Constants.FOLDER_TYPE_MYDOC); 
//		
//	}
	
	
	// 폴더가져오기
	public void getFolderDataRpc(final NavigatorForDocument tGrid, final int type){
		
		tGrid.getFolderDataRpc(type, 0, "folderId", "name", "parent", "type", true);
			
//		if(firstTimeXvarm && type == Constants.FOLDER_TYPE_XVARM){ firstTimeXvarm = false; }	
		if(firstTimeMydoc && type == Constants.FOLDER_TYPE_MYDOC){ firstTimeMydoc = false; }	
		if(firstTimeShareDoc && type == Constants.FOLDER_TYPE_SHARED){ firstTimeShareDoc = false; }
	}
	
	// section 설정
	private void prepareSection(final SectionStackSection section, final NavigatorForDocument tree,
			final String sectionName){
		
		// 20130724, junsoo, 섹션 헤더에 refresh button 추가
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
            	 * ETC Section은 하드코딩 되어있음.
            	 * 130801 taesu
            	 * */ 
//            	else if (Constants.SECTION_NAME_ETC.equals(sectionName))
//            		getFolderDataRpc(tree, Constants.FOLDER_TYPE_ETC); 
            }  
        });  
  
		
		section.setName(sectionName);
//		section.setCanCollapse(true);
		// 리사이즈 불가
		section.setResizeable(true);
		section.setItems(tree);
		
		section.setExpanded(false);
		
		/*
		 * ETC Section은 Refresh 하지 않음.
		 * 130801 taesu
		 * */
		if(!Constants.MENU_DOCUMENTS_ETC.equals(sectionName))
			section.setControls(refreshButton);
		addSection(section);
        
        /* 컨텍스트 메뉴 설정
         * xvarm 일 경우 컨텍스트 메뉴 없음
         * getfolder를 사용하여 폴더정보를 얻어온후 
         * 메뉴를 만든다.
         */
        tree.addCellContextClickHandler(new CellContextClickHandler() {
			@Override
			public void onCellContextClick(final CellContextClickEvent event) {
				// xvarm 컨텍스트 메뉴 없음.
//				if(sectionIsExpanded(Constants.SECTION_NAME_XVARM)){
//				if(tree.getFolderType() == Constants.FOLDER_TYPE_XVARM || tree.getFolderType() == Constants.FOLDER_TYPE_ETC){
				// ETC 메뉴도 컨텍스트 메뉴 생성
				if(tree.getFolderType() == Constants.FOLDER_TYPE_ETC){
					SFolder folder = (SFolder)event.getRecord().getAttributeAsObject("folder");
					if(folder != null && folder.getSharedDepth() != 0){
						Menu contextMenu = setupSharedContextMenu(folder);
						contextMenu.showContextMenu();
					}
				}else{
					// 20130805, junsoo, 같은 폴더는 재조회할 필요 없음.
					long folderId = Long.parseLong(event.getRecord().getAttributeAsString("folderId"));
					if (folderId != Session.get().getCurrentFolder().getId()) {
						// 폴더정보 필요
						service.getFolder(Session.get().getSid(),folderId, false, true,
								new AsyncCallback<SFolder>() {
							
							@Override
							public void onFailure(Throwable caught) {
								SCM.warn(caught);
							}
							
							@Override
							public void onSuccess(SFolder folder) {
								// 20130801, junsoo, 우클릭시에도 폴더가 선택되도록 함
								folder.setPathExtended(getPath(tree, folder.getId()));
								Session.get().setCurrentFolder(folder);
								
								// 정확한 서버 정보를 다시 세팅 (mydoc의 경우 parentId가 실제와 다르므로)
								event.getRecord().setAttribute("parent", folder.getParentId());
								
								//								Menu contextMenu = setupContextMenu(tree, folder, sectionIsExpanded(Constants.SECTION_NAME_SHARED));
								Menu contextMenu = setupContextMenu(tree, folder, tree.getFolderType() == Constants.FOLDER_TYPE_SHARED);
								contextMenu.showContextMenu();
							}
						});
					} else {
						// 정확한 서버 정보를 다시 세팅 (mydoc의 경우 parentId가 실제와 다르므로)
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
        
        // 폴더를 클릭했을때 해당 폴더의 문서리스트를 조회한다.
        tree.addCellClickHandler(new CellClickHandler() {
			@Override
			public void onCellClick(CellClickEvent event) {
				//서비스가 다르므로 xvarm과 나머지를 구분하여 함수호출
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
        
        // 20130910, junsoo, etc가 아닌 tree만 drop 가능
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
//	    			 *  이 동작이 실행되어야 하위폴더가 없는 폴더에 드랍 가능
//	    			 *  모든 폴더를 확장 가능하게 만든다.
//	    			 * */
//	    			ListGridRecord[] records = tree.getRecords();
//	    			for (ListGridRecord record : records) {
//	    				record.setAttribute("isFolder", true);
//	    			}
//	    			
//	    			// 복사 or 이동 결정
//	    			String operator = event.isCtrlKeyDown() ? Clipboard.COPY : Clipboard.CUT;
//	    			moveToFolder(tree, docIds, Long.parseLong(tree.getDropFolder().getAttribute("folderId")), operator);
//	    		}
//	    	});
//		}
	}
	
	/**
	 * D&D 동작
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
//				// DropFolder로 접근해야 null Exception 생기지 않음.
//				TreeNode dropNode = tree.getDropFolder();
//				String parentId = "";
//				TreeNode parentNode;
//				
//				// 상위 폴더로 이동시 folderId를 사용해야함.
//				parentId = dropNode.getAttribute("parent");
//				if(parentId == null){
//					parentId = dropNode.getAttribute("folderId");
//				}
//				// 상위 폴더 이동시 최상위 폴더일 경우 동작
//				if(!parentId.equals("5"))
//					parentNode = tree.getTree().findById(String.valueOf(parentId));
//				else{	
//					if(treeIndex == Constants.FOLDER_TYPE_MYDOC)	parentId = "4";
//					else	parentId = "3";
//					
//					parentNode = tree.getTree().findById(String.valueOf(parentId));
//				}
//				// 최상위 폴더일 경우 최상위 폴더를 refresh한다.
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
	 * 선택한 폴더를 확장시킨다.
	 * 
	 * 20130820 taesu
	 * */
	private void folderOpenAction(NavigatorForDocument tree, CellClickEvent event){
		// 현재 폴더의 타입
		int folderType = tree.getFolderType();
		// Folder Filter 검색조건 위치 저장
//		SearchItems searchItems;
		
		// 확장시킬 폴더의 ID 값 저장용 변수
		String id;
		// 현재 선택한 폴더 타입에 해당하는 Search Items을 반환
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
//		// 현재 선택한 폴더에 폴더내 검색시 등록된 folderFilter가 있을경우 Filter 적용 검색 else 기본 폴더 검색
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
	 * 현재 선택한 폴더에 저장되어있는 Folder Filter를 적용시키고 Filter 검색을 한다.
	 * @param id
	 * @param searchItems
	 * */
//	private void selectFolderFilterAction(String id, SearchItems searchItems){
//		searchItems.doSearch(PagingToolStrip.getPagingConfig(1, 10, "creationDate", SortDir.DESC), false, id);
//		DocumentActionUtil.get().changeActionIcon("filter", true);
//		searchItems.setItemValues(DocumentsPanel.get().getFolderFilter().get(id));
//	}
	
	/**
	 *  filter 검색 조건이 없는 기본 검색
	 * */
	private void selectTreeAction(int folderType, String id, NavigatorForDocument tree, boolean isFilter){
		// 필터 검색 조건 제거
		DocumentsPanel.get().setSearch(false);
		// 선택한 폴더를 확장하고 폴더의 문서들을 가져온다.
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
	 * Context Menu 설정
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
		
		//yongsoo 20140310	배치 다운로더
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
			// soeun 내 문서에서도 속성 나오게
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
	 * 공유 폴더 목록의 ContextMenu 설정
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
	
	// 폴더 refresh
	/**
	 * 해당폴더를 다시 셋팅
	 */
	private void onReload(NavigatorForDocument tree) {
		int treeIndex = Constants.FOLDER_TYPE_MYDOC;	
//		if(sectionIsExpanded(Constants.SECTION_NAME_MYDOC)) treeIndex= Constants.FOLDER_TYPE_MYDOC; 
		if(tree.getFolderType() == Constants.FOLDER_TYPE_MYDOC) treeIndex= Constants.FOLDER_TYPE_MYDOC; 
	    else treeIndex= Constants.FOLDER_TYPE_SHARED; 
		
		// 해당 폴더의 폴더아이디를 찾아서 그 아이디로 서버에서 다시 받아와서 셋팅한다.
		ListGridRecord rc = tree.getSelectedRecord();
		TreeNode refreshNode = tree.getTree().findById( rc.getAttribute("folderId") );
		tree.getFolderDataRpcChild(treeIndex, rc.getAttributeAsLong("folderId"), refreshNode, "folderId", "name", "parent", "type");
		tree.redraw();
	}
	
	// 즐겨찾기 추가
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
	 * 컨텍스트 메뉴 폴더 추가
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
						
						// 20140204, junsoo, 폴더 생성은 상속으로
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
								
									// 폴더 추가후 재조회
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

	// 컨텍스트 메뉴 rename
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
										
										// 20130730, junsoo, folder에도 이름 변경
										SFolder folder = (SFolder)selectedNode.getAttributeAsObject("folder");
										folder.setName(value);
									}
								});
					}
				});
	}	

	// 컨텍스트 메뉴 삭제
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
							/* 직접 해당 폴더를 삭제하면 문제가 발생하기 때문에 
							 * 부모폴더를 닫은 다음에 해당폴더를 삭제한다.
							 * 그후 다시 폴더를 오픈한다.
							 */
							TreeNode node = tree.getTree().find("folderId", Long.toString(docid));
							TreeNode parent = tree.getTree().find("folderId", node.getAttribute("parent"));
							tree.getTree().remove(node);
							if (parent != null) {
								tree.getTree().closeFolder(parent);
								tree.getTree().openFolder(parent);
								// 문서를 조회한다.
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
	
	// 폴더정보를 서비스를 통해 받아온후 해당 폴더의 문서를 조회한다.
	// xvarm과 이외의 폴더가 시비스 파라메터가 다르기 때문에 두가지의
	// 처리로 분리함.
	// 트리의 첫번째 폴더 검색
	public void selectFolder(final TreeGrid tree, int type){
		TreeNode rootNode;
		rootNode = tree.getTree().find("folderId", Long.toString(Constants.DOCUMENTS_FOLDERID));
		
		if (rootNode == null)
			return;
		
		TreeNode[] children = tree.getTree().getChildren(rootNode);
		if (children != null && children.length > 0){
			
			// 20130805, junsoo, xvarm 처리 제거함.
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
//								// 현재폴더내 문서 검색
//								tree.deselectAllRecords();
//								folder.setPathExtended(getPath(tree, folder.getName()));
//								Session.get().setCurrentFolder(folder);
//								tree.selectRecord(0);
//							}
//				});
//			}else{
			// 20130806 taesu, 폴더 선택시 로딩 시간으로 인한 CallBack함수 변경
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
//								// 현재폴더내 문서 검색
//								tree.deselectAllRecords();
//								folder.setPathExtended(getPath(tree, folder.getId()));
//								Session.get().setCurrentFolder(folder);
//								tree.selectRecord(0);
//							}
//				});
//			}
		}
	}
	
	// Xvarm 트리에 해당 폴더 검색
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
	 * NavigatorForDocument.java의 setEtcSectionName 값 확인
	 * */
	public void selectEtcFolder(String folderId, boolean bByHistory){
		// setTrack에서 에러발생함. 공유목록 이외에 문제가 발생할 수 있어서 폴더제거를 설정해줘야 함.
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
		// 20140218, junsoo, approve 선택시 아무일도 수행하지 않음
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
		// 공유 문서 선택
		else{
			if(!folderId.equals(EtcMenus.SHAREDLIST.getId())){
				SFolder sharedFolder = (SFolder)etcTree.getTree().findById(String.valueOf(folderId)).getAttributeAsObject("folder");
				// 사용자명의 폴더는 공유가 안되므로 검색되면 안된다.
//				if(sharedFolder.getSharedDepth() > 0){
//					if((Session.get().getCurrentFolder() == null) || (sharedFolder != null && (sharedFolder.getId() != Session.get().getCurrentFolder().getId()))){
					DocumentActionUtil.get().setActivatedMenuType(DocumentActionUtil.TYPE_FOLDER_SHARED);
					Session.get().setCurrentFolder(sharedFolder);
//				}
			}
		}
	
		// 항목 선택이 되어 있지 않을 수도 있으므로 선택 (예: 히스토리, 상태바)
		expandSection(Constants.MENU_DOCUMENTS_ETC);
		// 20130906, junsoo, lazy loading되어 etcTree가 null이 될 수 있음.
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
	 * 공유목록의 폴더 선택시 하위 폴더를 가져옴.
	 * folderDepth -1 : 루트, 0 : 유저명 폴더, 그 외 : 하위 폴더들
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
	 *  mydoc & shareddoc 트리에 해당 폴더 검색 및 필터 적용이 되어있을 경우 필터 검색 
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
	 * 폴더 아이디로 트리에서 전체 경로 가져오기
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
	 *  폴더 이름으로 트리에서 전제 경로 가져오기
	 * */
	public String getPath(final TreeGrid tree, String folderName) {

		// soeun 트리 노드 id로 불러오게 함
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
	 * 섹션 인덱스로 섹션 확장 하기
	 * 20130903, junsoo, 섹션이 동적으로 구성되므로 이 함수를 사용하면 안됨. expandSection(ID) 를 사용할 것!
	 * */ 
//	public void expand(int section){
//		expandSection(section);
//	}
	
	/**
	 * 폴더정보를 이용하여 해당 원본 문서가 있는 폴더 위치를 보여준다.
	 * foldertype 으로 해당 폴더가 xvarm, mydoc, shared인지 확인
	 * foldertype : 2 xvarm
	 * foldertype : 1 mydoc
	 * foldertype : 0 shared
	 * originalFolderId : 원본이 속해있는 최종타켓 폴더의 아이디이다.
	 */
	public void expandFolder(SFolder folder){
		expandFolder(folder, false);
	}
	
	public void expandFolder(SFolder folder, boolean bByHistory){
		final int foldertype = folder.getType();
		final long originalFolderId = folder.getId();
		expandCommand = true;
		
		/*
		 * 이미 왼쪽 폴더의 타겟경로까지 만들어져 있는경우 
		 * 폴더정보를 다시 받아와서 셋팅할 필요가 없으므로 
		 * 1. 해당 경로를 찾는다. getPath()
		 * 2. 폴더정보에 경로를 셋팅한다. folder.setPathExtended
		 * 3. 세션에 해당 폴더 정보를 넘겨주고 obserber를 활용하여 해당폴더가 조회 되도록 한다. Session.get().setCurrentFolder(folder)
		 * 4. 해당 폴더를 선택상태로 만든다. selectRecord
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
		//20131206 na 모든 폴더의 선택을 비활성화
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
		/*이미 왼쪽 폴더의 타겟경로까지 만들어져 있는경우 완료
		 * ========================================================================================================================
		 */
		
		/*
		 * 왼쪽 폴더의 경로까지 만들어져 있지 않은경우 
		 * 서비스를 호출 폴더의 정보를 받아와서 타겟폴더까지 반복하여 셋팅한다.
		 * foldertype == 2 인경우 xvarm 이므로 폴더의 구조가 평면 이기 때문에 폴더를 단계적으로 받아올 
		 * 필요 없이 한번만 호출한다.
		 * 
		 * 1. findFolderAndExpandRpcFromRoot(폴더정보, 폴더타입, 최종타겟 폴더 아이디)
		 * 2. findFolderAndExpandRpc(폴더정보, 폴더타입, 찾기시작할 폴더의 시작인덱스, 찾을폴더의 순차적 폴더아이디정보, 최종타겟 폴더 아이디)
		 * 
		 * getFolder 서비스를 통해 해당 폴더의 트리 구조를 받아서
		 * arrayFolder에 셋팅한다.
		 * SFolder[] arrayFolder = result.getRefinedPath();
		 * 
		 * arrayFolder.length 가 0일 경우 바로 루트에 위치한 경우 이므로 1번을 호출하여 루트정보만 받아오고 완료된다.
		 * 
		 * for(int i= 0; i< arrayFolder.length; i++)
		 * 폴더의 루트경로부터 최종경로까지 하나씩 검사하여 폴더트리에 셋팅되어 있지 않다면 서비스를 호출한다.
		 * orgNode = getTree().findById(String.valueOf(arrayFolder[i].getId()) 
		 * if(orgNode == null) 왼쪽 폴더 트리에 존재하지 않는 폴더 이므로 해당 폴더부터 서비스를 통하여 받아온다.
		 * 
		 * 만약 i==0 최초 폴더부터 존재하지 않는 다면 1번 호출 1번 함수안에서 최종타겟 폴더까지 찾아감.
		 *        i>0 다면 해당 폴더arrayFolder[i] 부터 2번을 호출하여 최종타켓 폴더까지 찾아감.
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
		// 20130822, junsoo, permission 가져오도록 추가.
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
			// 20140519, junsoo, folderId가 0이면 Root 부터 가져오기
			findFolderAndExpandRpcFromRoot(folder, foldertype, 0);
		}
		
//		}
		
	}
	
	/*
	 * 즐겨찾기에서 문서 더블클릭시 해당 폴더를 찾아서 확장하는 함수
	 * findSuccess : 일치하는 폴더 아이디를 발견하면 true
	 * arrayFolder : 찾으려는 폴더의 전체 폴더배열
	 * 루트부터 검색시작
	 * findFolderAndExpandRpcFromRoot(폴더정보, 폴더타입, 최종타겟 폴더 아이디)
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
//					// 찾으려고 하는 폴더의 아이디와 일치하는지 체크
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
					
					// 찾으려고 하는 폴더의 아이디와 일치하는지 체크
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
			    
					// 찾으려고 하는 폴더의 아이디와 일치하는지 체크
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
				// 폴더 매칭여부를 검사하고 하위 폴더로 검색 시작
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
	 * 1차 폴더 생성후에 매칭작업 1차원 폴더에서 찾으려는 폴더가 존재하면
	 * 해당 폴더를 선택하고 연결된 문서를 조회한다.
	 * 매칭실패시 폴더를 오픈하고 하위폴더 찾기를 시작한다.
	 */
	private void processFolderMaching(NavigatorForDocument treeGrid, Tree dataTree, SFolder folder, int treeType, SFolder[] arrayFolder, long originalId){
		treeGrid.setData(dataTree);
		treeGrid.refreshFields();
		
		if(findSuccess){
			// 찾으려는 폴더
			TreeNode orgNode = treeGrid.getTree().findById(String.valueOf(originalId) );
			
			// 폴더찾기 성공
			if(orgNode != null){
				folder.setPathExtended(getPath(treeGrid, originalId));
				// 해당 문서 조회
				Session.get().setCurrentFolder(folder);
				// 해당 폴더 선택
				treeGrid.selectRecord(orgNode);
				return;
			}
		// 폴더찾기 실패	
		}else{
			if (arrayFolder != null && arrayFolder.length > 0) {
				// 현재 폴더 오픈
				TreeNode currNode = treeGrid.getTree().findById(String.valueOf(arrayFolder[0].getId()) );
				treeGrid.getTree().openFolder(currNode);
				// 하위 폴더 검색 시작
				findFolderAndExpandRpc(folder, treeType, 1, arrayFolder, originalId);
			}
		}
	}
	
	/*
	 * 폴더트리 특정시점부터 해당폴더를 찾아 하위로 검색하는 함수
	 * findSuccess : 일치하는 폴더 아이디를 발견하면 true
	 * findFolderAndExpandRpc(폴더정보, 폴더타입, 찾기시작할 폴더의 시작인덱스, 찾을폴더의 순차적 폴더아이디정보, 최종타겟 폴더 아이디)
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
					
					// soeun 현재사용량/최대사용량 표시
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
						
					// 일치여부 판단
					if(originalId == sfolder[i].getId()) findSuccess = true;
			    }
				
				// 매칭후작업 시작
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
	 * 해당 폴더를 선택하고 연결된 문서를 조회한다.
	 * 매칭실패시 폴더를 오픈하고 다시 하위폴더 찾기를 시작한다.
	 */
	private void processFolderMachingChild(NavigatorForDocument treeGrid, SFolder folder, int treeType, int startIdx, SFolder[] arrayFolder, long originalId){
		treeGrid.refreshFields();
		if(findSuccess){
			// 찾으려는 폴더
			TreeNode orgNode = treeGrid.getTree().findById(String.valueOf(originalId) );
			
			if(orgNode != null){
				folder.setPathExtended(getPath(treeGrid, originalId));
				// 폴더 문서 검색
				Session.get().setCurrentFolder(folder);
				// 폴더 선택
				treeGrid.selectRecord(orgNode);
				return;
			}
		}else{
			//폴더 오픈
			TreeNode currNode = treeGrid.getTree().findById(String.valueOf(arrayFolder[startIdx].getId()) );
			treeGrid.getTree().openFolder(currNode);
			// 폴더 하위로 재검색 시작
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
	
	
	// 20130726,junsoo, 특정 위치로 점프
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
		
		// 20140519, 주석 처리함. 0 그대로 넘겨서 root부터 가져오도록 하기 위함.
		// 20140429, junsoo, folder id 가 없을 경우 세팅
//		if (folder.getId() == 0L) {
//			if (Constants.FOLDER_TYPE_MYDOC == folder.getType()) {
//				folder.setId(Session.get().getHomeFolderId());
//
//			} else if (Constants.FOLDER_TYPE_SHARED == folder.getType()) {
//				folder.setId(Constants.SHARED_DEFAULTID);
//			}
//		}
		
		//20150209na 폴더가 펼쳐지는 순서 변경
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
	
	// 20130903, junsoo, 섹션을 권한에 따른 동적 구성함
	@Override
	public void buildMenu(final String finalCallbackId, final long parentMenuId, boolean hasHistory) {
		Log.debug("[DocumentsMenu] building menu..");
		AdminMenuUtil.get().hasPriv(finalCallbackId, parentMenuId, Constants.MENU_DOCUMENTS_MYDOC, new AsyncCallback<Long>() {
			@Override
			public void onSuccess(Long id) {
				// 20130906, junsoo, 위임 체크 추가.
				if (!Session.get().isDelegator() || Session.get().hasDelegationPriv(Constants.MENU_DOCUMENTS_MYDOC)) {
					if (id != null) {
						/*
						 *  각각의 섹션을 만들고 해당 섹션에 폴더트리를 만든다.
						 *  prepareSection(섹션, 트리, 섹션네임, 서비스타입)
						 */
						MydocSection = new SectionStackSection(I18N.message("mydoc"));
						MydocSection.setID(Constants.MENU_DOCUMENTS_MYDOC);
						MydocTree = new NavigatorForDocument(Constants.FOLDER_TYPE_MYDOC);
						prepareSection(MydocSection, MydocTree, Constants.MENU_DOCUMENTS_MYDOC);
					}
				}
				
				// 20130906, junsoo, 위임 체크 추가.
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

				// 20130906, junsoo, 위임 체크 추가.
				if (!Session.get().isDelegator() || Session.get().hasDelegationPriv(EtcMenus.APPROVE.getId())) {
					AdminMenuUtil.get().hasPriv(finalCallbackId, parentMenuId, Constants.MENU_DOCUMENTS_ETC, new AsyncCallback<Long>() {
						@Override
						public void onSuccess(Long id) {
							if (id != null) {
								// sub 메뉴 통합 13.7.29 taesu
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
	
								// 20130805, junsoo, etc 는 처음에 오픈
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

	// 20130805, junsoo, documents에서 현재 위치를 저장 
	private String currentMenu = null;

	public String getCurrentMenu() {
		return currentMenu;
	}

	public void setCurrentMenu(String currentMenu) {
		this.currentMenu = currentMenu;
	}

}