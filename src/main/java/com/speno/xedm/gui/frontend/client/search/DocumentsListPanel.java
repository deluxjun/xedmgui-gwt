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
		// TODO, �ι�° �Ķ���� �����ؾ���.
		grid = new DocumentsGrid(true, "");
		grid.setShowFilterEditor(false);
		
		gridPager = new PagingToolStrip(grid, Session.get().getUser().getPageSize(), true, DocumentsListPanel.this, true);
		initGridPager();
		
		// command ����
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
				// SearchUtil�� setSorter ����
			}
		}, DocumentActionUtil.TYPE_SEARCH_PERSONAL, DocumentActionUtil.TYPE_SEARCH_SHARED);
	}
	
	public long expandDocid = 0;

	private void onBtnGotoClick() {
		Record[] selectedGrid;
		selectedGrid = grid.getSelectedRecords();

		if(selectedGrid.length > 0){
			// Tab �̵� ���� ����
			TabSet tabSet = MainPanel.get().getTabSet();
			// Document Tab���� �̵�
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
	// �Ӽ�����
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
		
//		 ��������
		ServiceUtil.document().getByIdWithPermission(Session.get().getSid(), doc.getId(), new AsyncCallback<SDocument>() {
			@Override
			public void onFailure(Throwable caught) {
				SCM.warn(caught);
			}
			
			@Override
			public void onSuccess(final SDocument result) {
				// �˻��� �Ӽ� ���� ���ϰ� ���Ƴ���
				final DocumentPropertiesDialog dialog = new DocumentPropertiesDialog(result, true, Constants.MAIN_TAB_SEARCH);
				dialog.show();
			}
		});

	}
	/**
	 *  GridPager �ʱ�ȭ
	 */
	private void initGridPager(){
		//totalLength ���ġ �������
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
		
		// 20140220, junsoo, ī��Ʈ ���� ����¡ ó�� ����ȭ
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
		
		// field ǥ�� ���� ����
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
