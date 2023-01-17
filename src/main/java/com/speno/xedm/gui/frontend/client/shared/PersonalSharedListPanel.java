package com.speno.xedm.gui.frontend.client.shared;

import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.widgets.layout.VLayout;
import com.speno.xedm.gui.common.client.Constants;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.PagingObserverOrderBy;
import com.speno.xedm.gui.frontend.client.PagingToolStrip;
import com.speno.xedm.gui.frontend.client.document.DocumentsListPanel;
import com.speno.xedm.util.paging.PagingConfig;
import com.speno.xedm.util.paging.SortDir;

public class PersonalSharedListPanel extends VLayout implements PagingObserverOrderBy{
	private PersonalSharedGrid grid;
	private PagingToolStrip gridPager;	
	private static PersonalSharedListPanel instance;
	
	
	public PersonalSharedListPanel(){
		createGrid();
	}
	
	private void createGrid() {
		// ���� �׸��带 �����ϰ� ȭ�鿡 �׸���.
		grid = new PersonalSharedGrid();
		grid.setShowFilterEditor(false);
		gridPager = new PagingToolStrip(grid, DocumentsListPanel.pagingSize, true, PersonalSharedListPanel.this, true);
		//totalLength ���ġ �������
        //gridPager = new PagingToolStrip(grid, 20, false, this);

		gridPager.setDeselect(false);
        gridPager.setIsAutoHeightGrid(false);
        gridPager.setMaxPageSize(200);

       
        grid.setHeight100();
        grid.setWidth100();
        grid.setBodyOverflow(Overflow.SCROLL);
        grid.draw();
        
        grid.setEmptyMessage(I18N.message("notitemstoshow"));

        addMember(grid);
        addMember(gridPager);
//      2013.12.02 ���¹� ���� ��ġ�� addMember�ڿ� �;� ȭ�鿡 �������� ������ ������ �ʴ´�. 
//      2013.12.02 ���¹� �߰� ���� �� ���� �߰� �Ǿ�� ����¡ó�� ��ư�� ������ ���� ��� �𽺿��̺��� �ɾ��ش�. 
        gridPager.setRespPageInfo(grid.getTotalLengh(), gridPager.getPageNum());
	}
	
	public static PersonalSharedListPanel get() {
		if (instance == null) {
			instance = new PersonalSharedListPanel();
		}
		return instance;
	}
	
	public void executeFetch(final int pageNum, final int pageSize, String orderBy){	
		if(orderBy.split("\\/")[0] == null || orderBy.split("\\/")[0].equals("null"))
			orderBy = Constants.ORDER_BY_MODIFIEDDATE_DESC;
		String order = orderBy.split("\\/")[0];
		String orderDir = orderBy.split("\\/")[1];
		//PagingConfig config = null;
//      2013.12.02 ���¹� �߰� config�� ���� ������ �ش�. 
		PagingConfig config = PagingToolStrip.getPagingConfig(pageNum, pageSize);
		
		// ���İ� ����(Paging�� ����)
		if(orderDir.equals("DESC")){
			config = PagingToolStrip.getPagingConfig(pageNum, pageSize, order, SortDir.DESC);
			gridPager.setOrderDir(SortDir.DESC);
		}else{
			config = PagingToolStrip.getPagingConfig(pageNum, pageSize, order, SortDir.ASC);
			gridPager.setOrderDir(SortDir.ASC);
		}
		gridPager.setOrderBy(order);

		// Offset ����
		config.setOffset(config.getOffset());

		config.setOrderByField(order);
		if(orderDir.equals("DESC"))
			config.setOrderDir(SortDir.DESC);
		else
			config.setOrderDir(SortDir.ASC);
		
		PersonalSharedPanel.get().execute(pageNum, pageSize);
//      2013.12.02 ���¹� �߰� ���� �� ���� �߰� �Ǿ�� ����¡ó�� ��ư�� ������ ���� ��� �𽺿��̺��� �ɾ��ش�. 
		gridPager.setRespPageInfo(grid.getTotalLengh(), pageNum);
	}
	
	public PersonalSharedGrid getGrid() {
		return grid;
	}

	public PagingToolStrip getGridPager() {
		return gridPager;
	}

	@Override
	public void onPageDataReqeust(int pageNum, int pageSize) {
		
	}

	@Override
	public void onPageDataReqeust(int pageNum, int pageSize, String orderBy) {
		executeFetch(pageNum, pageSize, orderBy);
	}
}
