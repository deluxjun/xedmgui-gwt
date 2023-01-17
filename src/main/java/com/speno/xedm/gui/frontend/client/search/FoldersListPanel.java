package com.speno.xedm.gui.frontend.client.search;

import java.util.List;

import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.speno.xedm.core.service.serials.SFolder;
import com.speno.xedm.core.service.serials.SHit;
import com.speno.xedm.gui.common.client.Constants;
import com.speno.xedm.gui.common.client.PagingObserverOrderBy;
import com.speno.xedm.gui.common.client.Session;
import com.speno.xedm.gui.frontend.client.PagingToolStrip;
import com.speno.xedm.gui.frontend.client.document.DocumentAction;
import com.speno.xedm.gui.frontend.client.document.DocumentActionUtil;
import com.speno.xedm.gui.frontend.client.document.DocumentsPanel;
import com.speno.xedm.gui.frontend.client.panels.MainPanel;
import com.speno.xedm.util.paging.PagingConfig;
import com.speno.xedm.util.paging.SortDir;

public class FoldersListPanel extends VLayout implements PagingObserverOrderBy {
	private FoldersSearchGrid grid;
	private PagingToolStrip gridPager;	
	
	private ToolStrip toolbar;

	private SFolder selectedFolder; 

	public SFolder getSelectedFolder() {
		return selectedFolder;
	}

	public void setSelectedFolder(SFolder selectedFolder) {
		this.selectedFolder = selectedFolder;
	}

	public FoldersListPanel(){
		// TODO, 두번째 파라미터 설정해야함.
		grid = new FoldersSearchGrid(this);
		grid.setShowFilterEditor(false);
		
		gridPager = new PagingToolStrip(grid, Session.get().getUser().getPageSize(), true, FoldersListPanel.this, true);
		initGridPager();

		// command 정의
		initActions();

		toolbar = DocumentActionUtil.get().getToolbar(DocumentActionUtil.TYPE_SEARCH_FOLDERS);

		addMember(toolbar);
		
		addMember(grid);
		
		addMember(gridPager);

	}
	
	public PagingToolStrip getGridPager() {
		return gridPager;
	}

	/**
	 * Search TabBar Action
	 * 20130817 taesu
	 * */
	private void initActions(){
		DocumentActionUtil.get().createAction("search_folders_goto", "client.goto", "actions_goto", true, new DocumentAction() {
			@Override
			protected void doAction(Object[] params) {
				onBtnGotoClick();
			}
		}, DocumentActionUtil.TYPE_SEARCH_FOLDERS);
	}
	
	private void onBtnGotoClick() {
		ListGridRecord selectedGrid = grid.getSelectedRecord();

		if(selectedGrid != null){
			selectedFolder = (SFolder)selectedGrid.getAttributeAsObject("folder");
			// tab 이동
			MainPanel.get().selectDocumentsTab();

			if (selectedFolder != null)
				DocumentsPanel.get().getMenu().expandFolder(selectedFolder);
		}
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
	public void setData(List<SHit> data) {
		grid.setGridData(data, gridPager);
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
		// TODO Auto-generated method stub
		
	}

}
