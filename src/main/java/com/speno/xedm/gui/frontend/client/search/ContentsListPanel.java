package com.speno.xedm.gui.frontend.client.search;

import java.util.List;
import java.util.Map;

import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.widgets.layout.VLayout;
import com.speno.xedm.gui.common.client.Constants;
import com.speno.xedm.gui.common.client.PagingObserverOrderBy;
import com.speno.xedm.gui.common.client.Session;
import com.speno.xedm.gui.common.client.util.Action;
import com.speno.xedm.gui.common.client.util.Util;
import com.speno.xedm.gui.frontend.client.PagingToolStrip;
import com.speno.xedm.gui.frontend.client.document.DocumentAction;
import com.speno.xedm.util.paging.PagingConfig;
import com.speno.xedm.util.paging.SortDir;

public class ContentsListPanel extends VLayout implements PagingObserverOrderBy {
	private ContentsSearchGrid grid;
	private PagingToolStrip gridPager;	
	
	private Action action;


	public ContentsListPanel(){
		grid = new ContentsSearchGrid(this);
		grid.setShowFilterEditor(false);

		// 20140325, junsoo, 2.0������ ���� �ο� ���ø� ����
		grid.setSelectionType(SelectionStyle.SINGLE);
		
		gridPager = new PagingToolStrip(grid, Session.get().getUser().getPageSize(), true, ContentsListPanel.this, true);
		initGridPager();

		// command ����
		initActions();

//		ecmToolbar = DocumentActionUtil.get().getToolbar(DocumentActionUtil.TYPE_SEARCH_ECM);

//		DocumentActionUtil.get().updateSearch(DocumentActionUtil.TYPE_SEARCH_ECM);
//		addMember(ecmToolbar);
		
		grid.setAction(action);
		
		if (action.getToolBar() != null)
			addMember(action.getToolBar());
		
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
		// 20140325, junsoo, DocumentActionUtil �� �ʹ� �����ϴ�, ���� ������ �ܵ����� ����!
		action = new Action();
		
		// ������ ecm.viewer.url ���ÿ� ���� ����޴� ǥ��
		String urlInfo = Session.get().getInfo().getConfig("ecm.viewer.url");
		if (urlInfo.trim().length() > 0) {
			action.createAction("show", "show", "view", new DocumentAction() {
				@Override
				protected void doAction(Object[] params) {
					showEcmDoc();
				}
			}, true, true);
		}
		
		action.createAction("download", "download", "actions_download", new DocumentAction() {
			@Override
			protected void doAction(Object[] params) {
				downloadEcmDoc();
			}
		}, true, true);
		
	}

	private String elementId;
	
	public void setElementId(String elementId) {
		this.elementId = elementId;
	}

	/**
	 * TODO ECM Doc Show 
	 */
	private void showEcmDoc(){
		if(elementId != null)
			Util.previewECMWindow(0, elementId, false);
	}
	
	/**
	 * TODO ECM Doc Download
	 */
	private void downloadEcmDoc(){
		if(elementId != null)
			Util.downloadAsFrame(0, elementId);
	}
	
	
	public long expandDocid = 0;


	/**
	 *  GridPager �ʱ�ȭ
	 */
	private void initGridPager(){
		//totalLength ���ġ �������
		//gridPager = new PagingToolStrip(grid, 20, false, this); 
		/*gridPager.setDeselect(false);
		gridPager.setIsAutoHeightGrid(false);
		gridPager.setMaxPageSize(200);*/
		
		grid.setHeight100();
		grid.setWidth100();
		grid.setBodyOverflow(Overflow.SCROLL);
		grid.draw();
	}
	
	// set data
	public void setData(List<Map<String, String>> data) {
		grid.setGridData(data, gridPager);
	}
	
	
	public void executeFetch(final int pageNum, final int pageSize, String orderBy){	
		if(orderBy.split("\\/")[0] == null || orderBy.split("\\/")[0].equals("null"))
			orderBy = Constants.ORDER_BY_MODIFIEDDATE_DESC;
		String order = orderBy.split("\\/")[0];
		String orderDir = orderBy.split("\\/")[1];
		PagingConfig config = null;
		
		// ���İ� ����(Paging�� ����)
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
		// Offset ����
		config.setOffset(config.getOffset());
		// Search Tab�� normal Search Section ���ý� ����
		config.setOrderByField(order);

		// do search!
		grid.resetGridData();
		SearchPanel.get().getLeftMenu().getEcmSearchItems().searchAction(config);
	}
	
	public void setFields(String[] fields) {
		grid.setField(fields);
	}
	
	@Override
	public void onPageDataReqeust(int pageNum, int pageSize) {
		executeFetch(pageNum, pageSize, null);
	}

	@Override
	public void onPageDataReqeust(int pageNum, int pageSize, String orderBy) {
		executeFetch(pageNum, pageSize, orderBy);
	}
	
	public void setPageNum(int pageNum){
		gridPager.setPageNum(pageNum);
	}
	
	public void setPageSize(int pageSize) {
		gridPager.setPageSize(pageSize);
	}
	
	public int getPageNum(){
		return gridPager.getPageNum();
	}
	
	public int getPageSize(){
		return gridPager.getPageSize();
	}
	
	public void resetGrid(){
		grid.resetGridData();
	}
}
