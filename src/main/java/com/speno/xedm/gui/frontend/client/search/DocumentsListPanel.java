package com.speno.xedm.gui.frontend.client.search;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.TabSet;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.speno.xedm.core.service.serials.SDocument;
import com.speno.xedm.core.service.serials.SFolder;
import com.speno.xedm.gui.common.client.Constants;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.PagingObserverOrderBy;
import com.speno.xedm.gui.common.client.Session;
import com.speno.xedm.gui.common.client.util.SCM;
import com.speno.xedm.gui.common.client.util.ServiceUtil;
import com.speno.xedm.gui.frontend.client.PagingToolStrip;
import com.speno.xedm.gui.frontend.client.document.DocumentAction;
import com.speno.xedm.gui.frontend.client.document.DocumentActionUtil;
import com.speno.xedm.gui.frontend.client.document.DocumentsGrid;
import com.speno.xedm.gui.frontend.client.document.DocumentsPanel;
import com.speno.xedm.gui.frontend.client.document.popup.DocumentPropertiesDialog;
import com.speno.xedm.gui.frontend.client.panels.MainPanel;
import com.speno.xedm.util.paging.PagingConfig;
import com.speno.xedm.util.paging.SortDir;

public class DocumentsListPanel extends VLayout implements PagingObserverOrderBy {
	private DocumentsGrid grid;
	private PagingToolStrip gridPager;	
	
	private ToolStrip personalToolbar;

	public PagingToolStrip getGridPager() {
		return gridPager;
	}

	public DocumentsListPanel(){
		// TODO, 두번째 파라미터 설정해야함.
		grid = new DocumentsGrid(true, "");
		grid.setShowFilterEditor(false);
		
		gridPager = new PagingToolStrip(grid, Session.get().getUser().getPageSize(), true, DocumentsListPanel.this, true);
		initGridPager();
		
		// command 정의
		initActions();

		personalToolbar = DocumentActionUtil.get().getToolbar(DocumentActionUtil.TYPE_SEARCH_PERSONAL);

//		DocumentActionUtil.get().updateSearch(DocumentActionUtil.TYPE_BEFORE_SEARCH);
		addMember(personalToolbar);
		
		addMember(grid);
		
		addMember(gridPager);

	}
	
	/**
	 * Search TabBar Action
	 * 20130817 taesu
	 * */
	private void initActions(){
		DocumentActionUtil.get().createAction("goto_search", "client.goto", "actions_goto", new DocumentAction() {
			@Override
			protected void doAction(Object[] params) {
				onBtnGotoClick();
			}
		}, DocumentActionUtil.TYPE_SEARCH_PERSONAL, DocumentActionUtil.TYPE_SEARCH_SHARED);
		
		DocumentActionUtil.get().createAction("properties_search", "properties", "cog_go", new DocumentAction() {
			@Override
			protected void doAction(Object[] params) {
				onBtnPropertiesClick();
			}
		}, DocumentActionUtil.TYPE_SEARCH_PERSONAL, DocumentActionUtil.TYPE_SEARCH_SHARED);
		
		DocumentActionUtil.get().createAction("sort", "sort", "sort", false, new DocumentAction() {
			@Override
			protected void doAction(Object[] params) {
				// SearchUtil의 setSorter 동작
			}
		}, DocumentActionUtil.TYPE_SEARCH_PERSONAL, DocumentActionUtil.TYPE_SEARCH_SHARED);
	}
	
	public long expandDocid = 0;

	private void onBtnGotoClick() {
		Record[] selectedGrid;
		selectedGrid = grid.getSelectedRecords();

		if(selectedGrid.length > 0){
			// Tab 이동 위한 변수
			TabSet tabSet = MainPanel.get().getTabSet();
			// Document Tab으로 이동
			tabSet.selectTab(Constants.MAIN_TAB_DOCUMENT);
			SDocument[] docs = DocumentActionUtil.get().getCurrentDocuments();
			if (docs != null && docs.length > 0) {
				DocumentsPanel.get().expandDocid =docs[0].getId();
				DocumentsPanel.get().getMenu().expandFolder(docs[0].getFolder());
		
				return;
			}
			
			SFolder[] folders = DocumentActionUtil.get().getCurrentFolderItems();
			if (folders != null && folders.length > 0) {
				DocumentsPanel.get().expandDocid = folders[0].getId();
				DocumentsPanel.get().getMenu().expandFolder(folders[0]);
				return;
			}
		}

				

		
	}
	// 속성보기
	public void onBtnPropertiesClick() {
		Record[] selectedGrid;
		selectedGrid = grid.getSelectedRecords();
		
		if (selectedGrid == null) {
			SC.say(I18N.message("youmustselectdocument"));
			return;
		}
		if (selectedGrid.length != 1) {
			return;
		}
		
		SDocument doc = (SDocument) selectedGrid[0].getAttributeAsObject("document");
		
//		 문서정보
		ServiceUtil.document().getByIdWithPermission(Session.get().getSid(), doc.getId(), new AsyncCallback<SDocument>() {
			@Override
			public void onFailure(Throwable caught) {
				SCM.warn(caught);
			}
			
			@Override
			public void onSuccess(final SDocument result) {
				// 검색은 속성 수정 못하게 막아놨음
				final DocumentPropertiesDialog dialog = new DocumentPropertiesDialog(result, true, Constants.MAIN_TAB_SEARCH);
				dialog.show();
			}
		});

	}
	/**
	 *  GridPager 초기화
	 */
	private void initGridPager(){
		//totalLength 사용치 않을경우
		//gridPager = new PagingToolStrip(grid, 20, false, this); 
		gridPager.setDeselect(false);
		gridPager.setIsAutoHeightGrid(false);
		gridPager.setMaxPageSize(200);
		
		grid.setHeight100();
		grid.setWidth100();
		grid.setBodyOverflow(Overflow.SCROLL);
		grid.draw();
	}
	
	// set data
	public void setData(List<SDocument> list) {
//		if(list.size() > 0 && gridPager.getPageSize()== list.size())
//			grid.setGridPagerInfo(gridPager, dataLength, gridPager.getPageNum());
//		else
//		gridPager.setPageNum(config.getCurrentTabNum());
		grid.setGridPagerInfo(gridPager, list.size(), gridPager.getPageNum());
		
		// 20140220, junsoo, 카운트 없는 페이징 처리 공통화
//		if(gridPager.getPageSize() < list.size()){
//			list.remove(list.size() - 1);
//			grid.setGridData(list);
//		}
//		else
			
			grid.setGridData(list);
	}	
	
	public void executeFetch(final int pageNum, final int pageSize, String orderBy){	
		if(orderBy.split("\\/")[0] == null || orderBy.split("\\/")[0].equals("null"))
			orderBy = Constants.ORDER_BY_MODIFIEDDATE_DESC;
		String order = orderBy.split("\\/")[0];
		String orderDir = orderBy.split("\\/")[1];
		PagingConfig config = null;
		
		// 정렬값 설정(Paging시 유지)
		if(orderDir.equals("DESC")){
			config = PagingToolStrip.getPagingConfig(pageNum, pageSize, order, SortDir.DESC);
			config.setOrderDir(SortDir.DESC);
			gridPager.setOrderDir(SortDir.DESC);
		}else{
			config = PagingToolStrip.getPagingConfig(pageNum, pageSize, order, SortDir.ASC);
			config.setOrderDir(SortDir.ASC);
			gridPager.setOrderDir(SortDir.ASC);
		}
		gridPager.setOrderBy(order);

		config.setLimit(pageSize);
		// Offset 설정
		config.setOffset(config.getOffset());
		// Search Tab의 normal Search Section 선택시 동작
		config.setOrderByField(order);

		// do search!
		grid.resetGridData();
		
		// field 표시 유무 결정
		grid.hideField("type");
		grid.hideField("rootnm");

		SearchPanel.get().getLeftMenu().getSearchItems().doSearch(pageNum, config, false);
	}
	
	
	@Override
	public void onPageDataReqeust(int pageNum, int pageSize) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPageDataReqeust(int pageNum, int pageSize, String orderBy) {
		executeFetch(pageNum, pageSize, orderBy);
	}
	
	public void setPageNum(int pageNum){
		gridPager.setPageNum(pageNum);
	}
	
	public int getPageNum(){
		return gridPager.getPageNum();
	}
	
	public int getPageSize(){
		return gridPager.getPageSize();
	}

	public void setPageSize(int pageSize) {
		
	}

}
