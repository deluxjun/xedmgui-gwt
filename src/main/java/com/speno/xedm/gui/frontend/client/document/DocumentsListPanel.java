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
	// xvarm ������ ���� Ȯ�� �ʵ��� ������ �ٲ�Ƿ� �׸��带 ���� �׷��� �Ѵ�.
	// ���������� ǥ�õ� xvarm ������ �̸��� ������ �ξ��ٰ� �̸��� �ٲ𶧸��� 
	// �׸��带 �ٽ� �׷��ش�.
//	private String lastXvarmFolderName;
	
//	private static DocumentsListPanel instance;
	
	private MyAsyncCallback myAsyncCallback = new MyAsyncCallback();

	// 20130910, junsoo, ������ �����Ƿ� ������
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
	
	// 20130725, junsoo, check box�� ���ּ� ���õ� ���� ����
	public Record[] getCheckedRecord(){
		return grid.getSelectedRecords();
//		RecordList rclist = grid.getDataAsRecordList();
//		Record[] rc = rclist.findAll("chk", true);
//		return rc;
	}
	
	// �ʱ� ��ȸ
	public void refresh(){
		try{
			if (	DocumentActionUtil.get().getActivatedMenuType() == DocumentActionUtil.TYPE_MYDOC ||
					DocumentActionUtil.get().getActivatedMenuType() == DocumentActionUtil.TYPE_SHARED){
				// 20130910, junsoo, ���� ���ѿ� ���� ���ɿ��� ������ �Ϸ��� ������, ������ ������ ����Ǹ� record���� drag�����ؾ� �ϹǷ�, grid�� drag������ ����Ʈ�� ��.
				grid.setCanDrag(true);
				grid.setCanDragRecordsOut(true);
				
			}
			// �⺻ �˻��� ���� ���İ� ����(total Length ���� ��� ����)
			executeFetch(1, gridPager.getPageSize()+1, Constants.ORDER_BY_MODIFIEDDATE_DESC);
//			executeFetch(1, 10, Constants.ORDER_BY_CREATEDATE_DESC);
		}catch(Exception ex){
			Log.warn(ex.getMessage());
		}
		
	}
	
	// 20130802, junsoo, ���ø� ���ڵ常 ���� ����
	public void updateSelectedRecords(){
		final ListGridRecord[] records = grid.getSelectedRecords();
		
		long[] docIds = new long[records.length];

		for (int i = 0; i < records.length; i++) {
			docIds[i] = records[i].getAttributeAsLong("id");
		}

		// id���� ���� �ٽ� ȹ��!
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
							// 20140207, junsoo, updateSelectedRecord���� �����ϹǷ� �ּ�ó��
//							grid.setRecordData(records[i], result[i]);
							updateSelectedRecord(result[i]);
						}
					}
				});
	}
	
	// 20130802, junsoo, �־��� document�� record ����
	public void updateSelectedRecord(SDocument document){
		ListGridRecord record = grid.getSelectedRecord();
//		updateSelectedRecords();
		
		grid.setRecordData(record, document);
		grid.setRecordDataEx(record, document);
		
		grid.refreshRow(grid.getRecordIndex(record));
		
		// update �޴�
		grid.onRowClick();
//		grid.updateData(record);
//		grid.redraw();
	}
	
	public void refreshSelectedRows() {
		grid.onRowClick();
	}

	// LockUserIdAndStatus ��ȸ
	private void pagingDocumentsLockUserIdAndStatus(final int pageNum, final int pageSize, final PagingConfig config){
		myAsyncCallback.setPageNum(pageNum);
		myAsyncCallback.start(I18N.message("search"));
		if(Util.getSetting("setting.waiting")) Waiting.show(I18N.message("wattingforserver"));
		ServiceUtil.document().pagingDocumentsByLockUserIdAndStatus(Session.get().getSid(), 
				Session.get().getUser().getId(), SDocument.DOC_CHECKED_OUT, config, myAsyncCallback);
	}
	
	// �Ϲݹ��� ��ȸ
	private void pagingDocuments(final int pageNum, final int pageSize, final PagingConfig config){
		SFolder folder = Session.get().getCurrentFolder();
		myAsyncCallback.setPageNum(pageNum);
		myAsyncCallback.start(I18N.message("search"));
		if(Util.getSetting("setting.waiting")) Waiting.show(I18N.message("wattingforserver"));
		ServiceUtil.document().pagingDocumentsByFolder(Session.get().getSid(), folder.getId(), config, myAsyncCallback);
	}
	
	// ������
	private void pagingDeletedDocuments(final int pageNum, final int pageSize, final PagingConfig config){
		myAsyncCallback.setPageNum(pageNum);
		myAsyncCallback.start(I18N.message("search"));
		if(Util.getSetting("setting.waiting")) Waiting.show(I18N.message("wattingforserver"));
		ServiceUtil.document().pagingDeletedDocumentsByUserId(Session.get().getSid(), Session.get().getUser().getId(),config, myAsyncCallback); 
	}
	
	// �������� ������
	private void pagingDeletedSharedDocuments(final int pageNum, final int pageSize, final PagingConfig config){
		myAsyncCallback.setPageNum(pageNum);
		myAsyncCallback.start(I18N.message("search"));
		if(Util.getSetting("setting.waiting")) Waiting.show(I18N.message("wattingforserver"));
		ServiceUtil.document().pagingDocumentsByType(Session.get().getSid(), 1, SFolder.TYPE_SHARED, Session.get().getUser().getId(), config, myAsyncCallback); 
	}
	
	// ��� ������
	private void pagingExpiredDocuments(final int pageNum, final int pageSize, final PagingConfig config){
		myAsyncCallback.setPageNum(pageNum);
		myAsyncCallback.start(I18N.message("search"));
		if(Util.getSetting("setting.waiting")) Waiting.show(I18N.message("wattingforserver"));
		ServiceUtil.document().pagingDocumentsByType(Session.get().getSid(), -1, SFolder.TYPE_SHARED, Session.get().getUser().getId(),config, myAsyncCallback); 
//		ServiceUtil.document().pagingExpiredDocumentsByUserId(Session.get().getSid(), Session.get().getUser().getId(),config, myAsyncCallback);
	}
	
	// �ϸ�ũ
	private void pagingBookmark(final int pageNum, final int pageSize, final PagingConfig config){
		if(Util.getSetting("setting.waiting")) Waiting.show(I18N.message("wattingforserver"));
		
		ServiceUtil.document().pagingBookmarksByUserId(Session.get().getSid(), 
				Session.get().getUser().getId(), 
				config, new DefaultAsyncCallbackWithStatus<PagingResult<SBookmark>>(I18N.message("bookmark")) {
					
					@Override
					public void onSuccessEvent(PagingResult<SBookmark> result) {
						Waiting.hide();
						// ��ȸ �Ǽ��� ������ ����
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
							
							// ���� Ÿ�Կ� ���� ��Ʈ ��� ����
							if(folder != null && folder.getType() == 2) root = "ecm";
							else if (folder != null && folder.getType() == 1) root = "mydoc";
							else if (folder != null && folder.getType() == 0) root = "sharedoc";
							
//							System.out.println(bookmark.getId());
							if(bookmark.getType() == 0){
								
								// �������� ����
								if(document != null){ 
									// document ����
									grid.setRecordData(record, document);
									
									// �������̵�
									record.setAttribute("docid", document.getId());
									// ���ã�� Ÿ���� ������ ���
									record.setAttribute("type", "documents");				
								}
							}else{
								// ���ã�� Ÿ���� ������ ���
								// �������̵�
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
							
							// ���̵� Ű ����
							record.setAttribute("id", bookmark.getId());
							
							// ���� ���� ǥ��
							record.setAttribute("rootnm", root );
							// ��ü ���
							record.setAttribute("path", bookmark.getPathExtended() );
							
							// ��ü ���
							record.setAttribute("targetid", bookmark.getTargetId() );

							// ���� ���� ����
							record.setAttribute("istarget", bookmark.isTargetEnabled());

							records[i]=record;
						}
						
						grid.setData(records);
						grid.setCurrentMenuType(DocumentActionUtil.get().getActivatedMenuType());		// 20140221, junsoo, menu type ����
						
						// ������ ������ ����.
						if (totalLength < 1) {
							Session.get().selectDocuments(null);
							return;
						}

//						GWT.log("offset = "+result.getOffset()+", total = "+ result.getTotalLength() + ", max = " + data.size(), null);
						gridPager.setRespPageInfo(totalLength, pageNum);
					
						//totalLength ���ġ �������
						//gridPager.setRespPageInfo((data.size() > 0), pageNum);
						
					}
				});
	}
	// TODO : ���� �޴��� ���� ��ȸ..
	public void executeFetch(final int pageNum, final int pageSize, String orderBy){		
		String order = orderBy.split("\\/")[0];
		String orderDir = orderBy.split("\\/")[1];
		PagingConfig config = null;
		
		// ���İ� ����(Paging�� ����)
		if(orderDir.equals("DESC")){
			config = PagingToolStrip.getPagingConfig(pageNum, pageSize, order, SortDir.DESC);
			gridPager.setOrderDir(SortDir.DESC);
		}else{
			config = PagingToolStrip.getPagingConfig(pageNum, pageSize, order, SortDir.ASC);
			gridPager.setOrderDir(SortDir.ASC);
		}
		gridPager.setOrderBy(order);

		// �׸��� �ʱ�ȭ
		grid.resetData();
		grid.setData(new ListGridRecord[0]);

		// field ǥ�� ���� ����
		setFieldsVisible();

		if(DocumentsPanel.get().isSearch()){
			config.setLimit(pageSize);
			DocumentsPanel.get().getActivatedSearchItems().doSearch(pageNum, config, true);
			return;
		}
		
		// ������ ���� ������ Ȯ�� �� ����
//		if(DocumentsPanel.get().haveFilter()){
//			DocumentsPanel.get().getSearchItemsDefault().doSearch(pageNum, pageSize, config, true);
//			return;
//		}
//		if(DocumentActionUtil.get().isSearching()){
//			// Offset ����
////			config.setOffset(config.getOffset());
////			// Search Tab ���ý� ����
////			if(DocumentActionUtil.get().getActivatedMenuType() == DocumentActionUtil.TYPE_SEARCH){
////				// Search Tab�� normal Search Section ���ý� ����
//////				int currentTab = DocumentsPanel.get().getTabSet().getSelectedTabNumber()+1;
////				// ���õ� Tab���� set
//////				config.setCurrentTabNum(currentTab);
////				// Data ��û
//////				DocumentsPanel.get().getSearchMenu().getSearchItems().doSearch(pageNum, pageSize, config, true);
////			}
////			// �˻� ���� ���� ��� ����
////			else
//				DocumentsPanel.get().getSearchItemsDefault().doSearch(pageNum, pageSize, config, true);
//			return;
//		}
		// Limit�� 1�� ������� maxLength�� �ʿ������.
//		gridPager.setPageSize(pageSize);
		config.setLimit(pageSize);
		switch (DocumentActionUtil.get().getActivatedMenuType()) {
			// mydoc & shared ��ȸ
		case DocumentActionUtil.TYPE_MYDOC:
		case DocumentActionUtil.TYPE_SHARED:
			pagingDocuments(pageNum, pageSize, config);
			break;
			
		case DocumentActionUtil.TYPE_FOLDER_SHARED:
			SFolder folder = Session.get().getCurrentFolder();
			// 20131120, junsoo, ������������ ����ڸ��� Ŭ������ ��, list�� ���⸸ �ϰ� ��û�� ���� ����.
			if(folder.getSharedDepth() > 0){
				pagingDocuments(pageNum, pageSize, config);
			}
			break;

			// LockUserIdAndStatus ��ȸ
		case DocumentActionUtil.TYPE_CHECKED:
			pagingDocumentsLockUserIdAndStatus(pageNum, pageSize, config);
			break;

			//20130816 taesu, �ʿ���� �������� ����
//		case DocumentActionUtil.TYPE_SEARCH:
//			pagingSearch(pageNum, pageSize);
//			break;

			// ������
		case DocumentActionUtil.TYPE_TRASH:
			// ������ �⺻ ������ MODIFIEDDATE_DESC
			config.setOrderByField(Constants.ORDER_BY_MODIFIEDDATE_DESC.split("\\/")[0]);
			pagingDeletedDocuments(pageNum, pageSize, config);
			break;

		case DocumentActionUtil.TYPE_SHARED_TRASH:
			// 20140207, junsoo, �˻��� ���ؼ��� ���������� ���� �����ϹǷ� �ּ�ó��
//			pagingDeletedSharedDocuments(pageNum, pageSize, config);
			break;

			// �ϸ�ũ
		case DocumentActionUtil.TYPE_FAVOR:
			pagingBookmark(pageNum, pageSize, config);
			break;
			
			// ��� ������
		case DocumentActionUtil.TYPE_EXPIRED:
			pagingExpiredDocuments(pageNum, pageSize, config);
			break;

			// XVARM
		case DocumentActionUtil.TYPE_XVARM:
//			listXvarmIndexFields(pageNum, pageSize, config);
			break;
			
			// ���� �����
		case DocumentActionUtil.TYPE_APPROVE_STANDBY:
			break;
			// ���� ��û��
		case DocumentActionUtil.TYPE_APPROVE_REQUEST:
			break;
			// ���� �Ϸ���
		case DocumentActionUtil.TYPE_APPROVE_COMPLETE:
			break;
			
		default:
			break;
		}
	}
	
	// �ʵ� ǥ�� ���� ����
	private void setFieldsVisible(){
		grid.hideField("type");
		grid.hideField("rootnm");
		grid.hideField("path");
		
		switch (DocumentActionUtil.get().getActivatedMenuType()) {
			// mydoc & shared ��ȸ
		case DocumentActionUtil.TYPE_MYDOC:
		case DocumentActionUtil.TYPE_SHARED:
			break;
	
			// LockUserIdAndStatus ��ȸ
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
		
		// ���� �׸��带 �����ϰ� ȭ�鿡 �׸���.
		grid = new DocumentsGrid();
		grid.setShowFilterEditor(false);
		
		gridPager = new PagingToolStrip(grid, pagingSize, true, DocumentsListPanel.this, true);
		//totalLength ���ġ �������
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
							// ������ �ִ� ������ drag ���� (����, ��� üũ).
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
	
	
	// DocumentsPanel���� �����
	@SuppressWarnings("unused")
	private SSearchOptions searchOptions;
	public void setSearchOptions(SSearchOptions searchOptions) {
		this.searchOptions = searchOptions;
	}

	/*
	 * paging Search���� �κе� paging�� ������ �ٱ� ������ searchDB���� ó��. 
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
			// waiting�� �����ְ� ������� hide!
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