package com.speno.xedm.gui.frontend.client.document;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.DragDataAction;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.util.EventHandler;
import com.smartgwt.client.widgets.events.DragMoveEvent;
import com.smartgwt.client.widgets.events.DragMoveHandler;
import com.smartgwt.client.widgets.events.DragStartEvent;
import com.smartgwt.client.widgets.events.DragStartHandler;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.VLayout;
import com.speno.xedm.core.service.serials.SBookmark;
import com.speno.xedm.core.service.serials.SDocument;
import com.speno.xedm.core.service.serials.SFolder;
import com.speno.xedm.core.service.serials.SSearchOptions;
import com.speno.xedm.gui.common.client.Constants;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.PagingObserverOrderBy;
import com.speno.xedm.gui.common.client.Session;
import com.speno.xedm.gui.common.client.log.Log;
import com.speno.xedm.gui.common.client.util.DefaultAsyncCallbackWithStatus;
import com.speno.xedm.gui.common.client.util.ServiceUtil;
import com.speno.xedm.gui.common.client.util.Util;
import com.speno.xedm.gui.common.client.window.Waiting;
import com.speno.xedm.gui.frontend.client.PagingToolStrip;
import com.speno.xedm.util.paging.PagingConfig;
import com.speno.xedm.util.paging.PagingResult;
import com.speno.xedm.util.paging.SortDir;

public class DocumentsListPanel extends VLayout implements PagingObserverOrderBy{

	private DocumentsGrid grid;	
	private PagingToolStrip gridPager;	
	
	public static int pagingSize = Session.get().getUser().getPageSize();
	// xvarm 폴더에 따라 확장 필드의 내용이 바뀌므로 그리드를 새로 그려야 한다.
	// 마지막으로 표시된 xvarm 폴더의 이름을 저장해 두었다가 이름이 바뀔때마다 
	// 그리드를 다시 그려준다.
//	private String lastXvarmFolderName;
	
//	private static DocumentsListPanel instance;
	
	private MyAsyncCallback myAsyncCallback = new MyAsyncCallback();

	// 20130910, junsoo, 사용되지 않으므로 제거함
//	public static DocumentsListPanel get() {
//		if (instance == null) {
//			instance = new DocumentsListPanel();
//		}
//		return instance;
//	}

	public DocumentsListPanel() {
		createGrid();
		
		setHeight100();
		setWidth100();
		
		myAsyncCallback.setCanvas(DocumentsPanel.get());
	}
	
	// 20130725, junsoo, check box를 없애서 선택된 것을 리턴
	public Record[] getCheckedRecord(){
		return grid.getSelectedRecords();
//		RecordList rclist = grid.getDataAsRecordList();
//		Record[] rc = rclist.findAll("chk", true);
//		return rc;
	}
	
	// 초기 조회
	public void refresh(){
		try{
			if (	DocumentActionUtil.get().getActivatedMenuType() == DocumentActionUtil.TYPE_MYDOC ||
					DocumentActionUtil.get().getActivatedMenuType() == DocumentActionUtil.TYPE_SHARED){
				// 20130910, junsoo, 폴더 권한에 따라 가능여부 설정을 하려고 했으나, 문서별 권한이 적용되면 record별로 drag가능해야 하므로, grid에 drag설정을 디폴트로 함.
				grid.setCanDrag(true);
				grid.setCanDragRecordsOut(true);
				
			}
			// 기본 검색시 최초 정렬값 수정(total Length 제거 대비 수정)
			executeFetch(1, gridPager.getPageSize()+1, Constants.ORDER_BY_MODIFIEDDATE_DESC);
//			executeFetch(1, 10, Constants.ORDER_BY_CREATEDATE_DESC);
		}catch(Exception ex){
			Log.warn(ex.getMessage());
		}
		
	}
	
	// 20130802, junsoo, 선택만 레코드만 서버 갱신
	public void updateSelectedRecords(){
		final ListGridRecord[] records = grid.getSelectedRecords();
		
		long[] docIds = new long[records.length];

		for (int i = 0; i < records.length; i++) {
			docIds[i] = records[i].getAttributeAsLong("id");
		}

		// id들의 정보 다시 획득!
		ServiceUtil.document().getByIds(Session.get().getSid(), docIds,
				new AsyncCallback<SDocument[]>() {
					@Override
					public void onFailure(Throwable caught) {
						Log.serverError(caught, false);
					}

					@Override
					public void onSuccess(final SDocument[] result) {
						// reload
						for (int i = 0; i < result.length; i++) {
							// 20140207, junsoo, updateSelectedRecord에서 수행하므로 주석처리
//							grid.setRecordData(records[i], result[i]);
							updateSelectedRecord(result[i]);
						}
					}
				});
	}
	
	// 20130802, junsoo, 주어진 document로 record 갱신
	public void updateSelectedRecord(SDocument document){
		ListGridRecord record = grid.getSelectedRecord();
//		updateSelectedRecords();
		
		grid.setRecordData(record, document);
		grid.setRecordDataEx(record, document);
		
		grid.refreshRow(grid.getRecordIndex(record));
		
		// update 메뉴
		grid.onRowClick();
//		grid.updateData(record);
//		grid.redraw();
	}
	
	public void refreshSelectedRows() {
		grid.onRowClick();
	}

	// LockUserIdAndStatus 조회
	private void pagingDocumentsLockUserIdAndStatus(final int pageNum, final int pageSize, final PagingConfig config){
		myAsyncCallback.setPageNum(pageNum);
		myAsyncCallback.start(I18N.message("search"));
		if(Util.getSetting("setting.waiting")) Waiting.show(I18N.message("wattingforserver"));
		ServiceUtil.document().pagingDocumentsByLockUserIdAndStatus(Session.get().getSid(), 
				Session.get().getUser().getId(), SDocument.DOC_CHECKED_OUT, config, myAsyncCallback);
	}
	
	// 일반문서 조회
	private void pagingDocuments(final int pageNum, final int pageSize, final PagingConfig config){
		SFolder folder = Session.get().getCurrentFolder();
		myAsyncCallback.setPageNum(pageNum);
		myAsyncCallback.start(I18N.message("search"));
		if(Util.getSetting("setting.waiting")) Waiting.show(I18N.message("wattingforserver"));
		ServiceUtil.document().pagingDocumentsByFolder(Session.get().getSid(), folder.getId(), config, myAsyncCallback);
	}
	
	// 휴지통
	private void pagingDeletedDocuments(final int pageNum, final int pageSize, final PagingConfig config){
		myAsyncCallback.setPageNum(pageNum);
		myAsyncCallback.start(I18N.message("search"));
		if(Util.getSetting("setting.waiting")) Waiting.show(I18N.message("wattingforserver"));
		ServiceUtil.document().pagingDeletedDocumentsByUserId(Session.get().getSid(), Session.get().getUser().getId(),config, myAsyncCallback); 
	}
	
	// 공유문서 휴지통
	private void pagingDeletedSharedDocuments(final int pageNum, final int pageSize, final PagingConfig config){
		myAsyncCallback.setPageNum(pageNum);
		myAsyncCallback.start(I18N.message("search"));
		if(Util.getSetting("setting.waiting")) Waiting.show(I18N.message("wattingforserver"));
		ServiceUtil.document().pagingDocumentsByType(Session.get().getSid(), 1, SFolder.TYPE_SHARED, Session.get().getUser().getId(), config, myAsyncCallback); 
	}
	
	// 폐기 문서함
	private void pagingExpiredDocuments(final int pageNum, final int pageSize, final PagingConfig config){
		myAsyncCallback.setPageNum(pageNum);
		myAsyncCallback.start(I18N.message("search"));
		if(Util.getSetting("setting.waiting")) Waiting.show(I18N.message("wattingforserver"));
		ServiceUtil.document().pagingDocumentsByType(Session.get().getSid(), -1, SFolder.TYPE_SHARED, Session.get().getUser().getId(),config, myAsyncCallback); 
//		ServiceUtil.document().pagingExpiredDocumentsByUserId(Session.get().getSid(), Session.get().getUser().getId(),config, myAsyncCallback);
	}
	
	// 북마크
	private void pagingBookmark(final int pageNum, final int pageSize, final PagingConfig config){
		if(Util.getSetting("setting.waiting")) Waiting.show(I18N.message("wattingforserver"));
		
		ServiceUtil.document().pagingBookmarksByUserId(Session.get().getSid(), 
				Session.get().getUser().getId(), 
				config, new DefaultAsyncCallbackWithStatus<PagingResult<SBookmark>>(I18N.message("bookmark")) {
					
					@Override
					public void onSuccessEvent(PagingResult<SBookmark> result) {
						Waiting.hide();
						// 조회 건수가 없으면 리턴
						if ( result == null) return;
						
						int totalLength = result.getTotalLength();
						List<SBookmark> data = result.getData();
						ListGridRecord records[] = new ListGridRecord[data.size()];
						
						String root = "";
						for (int i = 0; i < data.size(); i++) {
							SBookmark bookmark = data.get(i);
							ListGridRecord record = new ListGridRecord();
							SDocument document = bookmark.getDocument();
							SFolder folder = bookmark.getFolder();
							
							// 폴더 타입에 따른 루트 경로 설정
							if(folder != null && folder.getType() == 2) root = "ecm";
							else if (folder != null && folder.getType() == 1) root = "mydoc";
							else if (folder != null && folder.getType() == 0) root = "sharedoc";
							
//							System.out.println(bookmark.getId());
							if(bookmark.getType() == 0){
								
								// 딸린파일 갯수
								if(document != null){ 
									// document 세팅
									grid.setRecordData(record, document);
									
									// 문서아이디
									record.setAttribute("docid", document.getId());
									// 즐겨찾기 타입이 문서인 경우
									record.setAttribute("type", "documents");				
								}
							}else{
								// 즐겨찾기 타입이 폴더인 경우
								// 문서아이디
								record.setAttribute("docid", 0);
								record.setAttribute("type", "folder");
								record.setAttribute("attachs", "-");
								record.setAttribute("version", "-");
								
								if(folder != null){
									record.setAttribute("titlenm", folder.getName());
									record.setAttribute("description", folder.getDescription());
									record.setAttribute("created", folder.getCreation());
									record.setAttribute("folder", folder);
								}
							}
							
							// 아이디 키 셋팅
							record.setAttribute("id", bookmark.getId());
							
							// 폴더 섹션 표시
							record.setAttribute("rootnm", root );
							// 전체 경로
							record.setAttribute("path", bookmark.getPathExtended() );
							
							// 전체 경로
							record.setAttribute("targetid", bookmark.getTargetId() );

							// 원본 존재 여부
							record.setAttribute("istarget", bookmark.isTargetEnabled());

							records[i]=record;
						}
						
						grid.setData(records);
						grid.setCurrentMenuType(DocumentActionUtil.get().getActivatedMenuType());		// 20140221, junsoo, menu type 저장
						
						// 데이터 없으면 리턴.
						if (totalLength < 1) {
							Session.get().selectDocuments(null);
							return;
						}

//						GWT.log("offset = "+result.getOffset()+", total = "+ result.getTotalLength() + ", max = " + data.size(), null);
						gridPager.setRespPageInfo(totalLength, pageNum);
					
						//totalLength 사용치 않을경우
						//gridPager.setRespPageInfo((data.size() > 0), pageNum);
						
					}
				});
	}
	// TODO : 현재 메뉴에 따른 조회..
	public void executeFetch(final int pageNum, final int pageSize, String orderBy){		
		String order = orderBy.split("\\/")[0];
		String orderDir = orderBy.split("\\/")[1];
		PagingConfig config = null;
		
		// 정렬값 설정(Paging시 유지)
		if(orderDir.equals("DESC")){
			config = PagingToolStrip.getPagingConfig(pageNum, pageSize, order, SortDir.DESC);
			gridPager.setOrderDir(SortDir.DESC);
		}else{
			config = PagingToolStrip.getPagingConfig(pageNum, pageSize, order, SortDir.ASC);
			gridPager.setOrderDir(SortDir.ASC);
		}
		gridPager.setOrderBy(order);

		// 그리드 초기화
		grid.resetData();
		grid.setData(new ListGridRecord[0]);

		// field 표시 유무 결정
		setFieldsVisible();

		if(DocumentsPanel.get().isSearch()){
			config.setLimit(pageSize);
			DocumentsPanel.get().getActivatedSearchItems().doSearch(pageNum, config, true);
			return;
		}
		
		// 폴더별 필터 설정값 확인 후 동작
//		if(DocumentsPanel.get().haveFilter()){
//			DocumentsPanel.get().getSearchItemsDefault().doSearch(pageNum, pageSize, config, true);
//			return;
//		}
//		if(DocumentActionUtil.get().isSearching()){
//			// Offset 설정
////			config.setOffset(config.getOffset());
////			// Search Tab 선택시 동작
////			if(DocumentActionUtil.get().getActivatedMenuType() == DocumentActionUtil.TYPE_SEARCH){
////				// Search Tab의 normal Search Section 선택시 동작
//////				int currentTab = DocumentsPanel.get().getTabSet().getSelectedTabNumber()+1;
////				// 선택된 Tab정보 set
//////				config.setCurrentTabNum(currentTab);
////				// Data 요청
//////				DocumentsPanel.get().getSearchMenu().getSearchItems().doSearch(pageNum, pageSize, config, true);
////			}
////			// 검색 선택 없을 경우 동작
////			else
//				DocumentsPanel.get().getSearchItemsDefault().doSearch(pageNum, pageSize, config, true);
//			return;
//		}
		// Limit에 1을 더해줘야 maxLength가 필요없어짐.
//		gridPager.setPageSize(pageSize);
		config.setLimit(pageSize);
		switch (DocumentActionUtil.get().getActivatedMenuType()) {
			// mydoc & shared 조회
		case DocumentActionUtil.TYPE_MYDOC:
		case DocumentActionUtil.TYPE_SHARED:
			pagingDocuments(pageNum, pageSize, config);
			break;
			
		case DocumentActionUtil.TYPE_FOLDER_SHARED:
			SFolder folder = Session.get().getCurrentFolder();
			// 20131120, junsoo, 공유문서에서 사용자명을 클릭했을 때, list를 비우기만 하고 요청은 하지 않음.
			if(folder.getSharedDepth() > 0){
				pagingDocuments(pageNum, pageSize, config);
			}
			break;

			// LockUserIdAndStatus 조회
		case DocumentActionUtil.TYPE_CHECKED:
			pagingDocumentsLockUserIdAndStatus(pageNum, pageSize, config);
			break;

			//20130816 taesu, 필요없는 동작으로 제거
//		case DocumentActionUtil.TYPE_SEARCH:
//			pagingSearch(pageNum, pageSize);
//			break;

			// 휴지통
		case DocumentActionUtil.TYPE_TRASH:
			// 휴지통 기본 정렬은 MODIFIEDDATE_DESC
			config.setOrderByField(Constants.ORDER_BY_MODIFIEDDATE_DESC.split("\\/")[0]);
			pagingDeletedDocuments(pageNum, pageSize, config);
			break;

		case DocumentActionUtil.TYPE_SHARED_TRASH:
			// 20140207, junsoo, 검색을 통해서만 공유휴지통 열람 가능하므로 주석처리
//			pagingDeletedSharedDocuments(pageNum, pageSize, config);
			break;

			// 북마크
		case DocumentActionUtil.TYPE_FAVOR:
			pagingBookmark(pageNum, pageSize, config);
			break;
			
			// 폐기 문서함
		case DocumentActionUtil.TYPE_EXPIRED:
			pagingExpiredDocuments(pageNum, pageSize, config);
			break;

			// XVARM
		case DocumentActionUtil.TYPE_XVARM:
//			listXvarmIndexFields(pageNum, pageSize, config);
			break;
			
			// 승인 대기함
		case DocumentActionUtil.TYPE_APPROVE_STANDBY:
			break;
			// 승인 요청함
		case DocumentActionUtil.TYPE_APPROVE_REQUEST:
			break;
			// 승인 완료함
		case DocumentActionUtil.TYPE_APPROVE_COMPLETE:
			break;
			
		default:
			break;
		}
	}
	
	// 필드 표시 유무 설정
	private void setFieldsVisible(){
		grid.hideField("type");
		grid.hideField("rootnm");
		grid.hideField("path");
		
		switch (DocumentActionUtil.get().getActivatedMenuType()) {
			// mydoc & shared 조회
		case DocumentActionUtil.TYPE_MYDOC:
		case DocumentActionUtil.TYPE_SHARED:
			break;
	
			// LockUserIdAndStatus 조회
		case DocumentActionUtil.TYPE_CHECKED:
			grid.showField("path");
			break;
			
		case DocumentActionUtil.TYPE_SEARCH_PERSONAL:
			grid.showField("path");
			break;
		case DocumentActionUtil.TYPE_SEARCH_SHARED:
			grid.showField("path");
			break;
			
		case DocumentActionUtil.TYPE_FAVOR:
			grid.showField("type");
			grid.showField("rootnm");
			grid.showField("path");
			break;
			
		case DocumentActionUtil.TYPE_TRASH:
		case DocumentActionUtil.TYPE_SHARED_TRASH:
		case DocumentActionUtil.TYPE_EXPIRED:
			grid.showField("path");
			break;
			
			// XVARM
		case DocumentActionUtil.TYPE_XVARM:
			grid.hideField("type");
			grid.hideField("rootnm");
	//		listXvarmIndexFields(pageNum, pageSize, config);
			break;
	
		default:
			break;
		}
	}

	private void createGrid(){
		
		// 문서 그리드를 생성하고 화면에 그린다.
		grid = new DocumentsGrid();
		grid.setShowFilterEditor(false);
		
		gridPager = new PagingToolStrip(grid, pagingSize, true, DocumentsListPanel.this, true);
		//totalLength 사용치 않을경우
        //gridPager = new PagingToolStrip(grid, 20, false, this); 
		gridPager.setDeselect(false);
        gridPager.setIsAutoHeightGrid(false);
        gridPager.setMaxPageSize(200);
        
        grid.setHeight100();
        grid.setWidth100();
        grid.setBodyOverflow(Overflow.SCROLL);
        grid.draw();
        

		addDragStartHandler(new DragStartHandler() {
			@Override
			public void onDragStart(DragStartEvent ev) {
				if (EventHandler.getDragTarget() instanceof DocumentsGrid) {
					SDocument[] docs = DocumentActionUtil.get().getCurrentDocuments();
					if (docs != null) {
						for (SDocument doc : docs) {
							// 권한이 있는 문서만 drag 가능 (쓰기, 잠김 체크).
							if (!doc.getFolder().isWrite() || SDocument.DOC_UNLOCKED != doc.getStatus()) {
								ev.cancel();
								break;
							}
					}
					}
				}
			}
		});
		
		addDragMoveHandler(new DragMoveHandler() {
			@Override
			public void onDragMove(DragMoveEvent event) {
	            String tracker = "";
				SDocument[] docs = DocumentActionUtil.get().getCurrentDocuments();
				if (docs != null) {
					if (docs.length > 0)
						tracker += docs[0].getTitle();
					if (docs.length > 1)
						tracker += "(" + (docs.length-1) + ")";
				}

		        if (event.isCtrlKeyDown()) {
		            grid.setDragDataAction(DragDataAction.COPY);
		            EventHandler.setDragTracker(tracker + Util.imageHTML("copy.png", 16, 16));

		        } else {
		            grid.setDragDataAction(DragDataAction.MOVE);
		            EventHandler.setDragTracker(tracker);
		        }
			}
		});
        
        addMember(grid);
        addMember(gridPager);
	}
	
	
	// DocumentsPanel에서 사용중
	@SuppressWarnings("unused")
	private SSearchOptions searchOptions;
	public void setSearchOptions(SSearchOptions searchOptions) {
		this.searchOptions = searchOptions;
	}

	/*
	 * paging Search관련 부분도 paging시 조건이 붙기 때문에 searchDB에서 처리. 
	 * 20131816 taesu 
	*/ 
//	private void pagingSearch(final int pageNum, final int pageSize){
//		ServiceUtil.search().search(Session.get().getSid(), searchOptions, new DefaultAsyncCallbackWithStatus<SResult>(this, I18N.message("search")) {
//			@Override
//			public void onSuccessEvent(SResult result) {
////				MainPanel.get().tabSet.selectTab(1);
//
//				SDocument[] data = result.getHits();
//				
//				List<SDocument> list = Arrays.asList(data);
//
//				grid.setGridData(list, Integer.MAX_VALUE, pageNum, DocumentsPanel.get(), gridPager, true, 0);
//			}
//		});
//	}
	
	public class MyAsyncCallback extends DefaultAsyncCallbackWithStatus<PagingResult<SDocument>> {
		private int pageNum;

		@Override
		public void start(String headerMessage) {
			setCanvas(DocumentsListPanel.this);
			super.start(headerMessage);
		}
		
		public void setPageNum(int pageNum) {
			this.pageNum = pageNum;
		}
		
		@Override
		public void onSuccessEvent(PagingResult<SDocument> result) {
			// waiting을 보여주고 있을경우 hide!
			Waiting.hide();
			if (result == null) {
				Log.debug("DocumentsListPanel's MyAsyncCallback : result is null");
				return;
			}
			
			int totalLength = result.getTotalLength();
			gridPager.setHavingNextPage(result.isHavingNextPage());
			gridPager.setRespPageInfo(totalLength, pageNum);
			List<SDocument> data = result.getData();
			grid.setGridData(data, totalLength, pageNum, gridPager);
			grid.setCurrentMenuType(DocumentActionUtil.get().getActivatedMenuType());
			
			
		}
	}
	public DocumentsGrid getGrid() {
		return grid;
	}
	
	public PagingToolStrip getGridPager() {
		return gridPager;
	}

	@Override
	public void onPageDataReqeust(int pageNum, int pageSize, String orderBy) {
		executeFetch(pageNum, pageSize, orderBy);
	}

	@Override
	public void onPageDataReqeust(int pageNum, int pageSize) {
		// TODO Auto-generated method stub
		
	}
}