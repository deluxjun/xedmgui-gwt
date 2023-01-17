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
		// 문서 그리드를 생성하고 화면에 그린다.
		grid = new PersonalSharedGrid();
		grid.setShowFilterEditor(false);
		gridPager = new PagingToolStrip(grid, DocumentsListPanel.pagingSize, true, PersonalSharedListPanel.this, true);
		//totalLength 사용치 않을경우
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
//      2013.12.02 정승범 수정 위치가 addMember뒤에 와야 화면에 쓸때없는 여백이 생기지 않는다. 
//      2013.12.02 정승범 추가 ↓↓↓ 이 줄이 추가 되어야 페이징처리 버튼이 내용이 없을 경우 디스에이블을 걸어준다. 
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
//      2013.12.02 정승범 추가 config를 새로 설정해 준다. 
		PagingConfig config = PagingToolStrip.getPagingConfig(pageNum, pageSize);
		
		// 정렬값 설정(Paging시 유지)
		if(orderDir.equals("DESC")){
			config = PagingToolStrip.getPagingConfig(pageNum, pageSize, order, SortDir.DESC);
			gridPager.setOrderDir(SortDir.DESC);
		}else{
			config = PagingToolStrip.getPagingConfig(pageNum, pageSize, order, SortDir.ASC);
			gridPager.setOrderDir(SortDir.ASC);
		}
		gridPager.setOrderBy(order);

		// Offset 설정
		config.setOffset(config.getOffset());

		config.setOrderByField(order);
		if(orderDir.equals("DESC"))
			config.setOrderDir(SortDir.DESC);
		else
			config.setOrderDir(SortDir.ASC);
		
		PersonalSharedPanel.get().execute(pageNum, pageSize);
//      2013.12.02 정승범 추가 ↓↓↓ 이 줄이 추가 되어야 페이징처리 버튼이 내용이 없을 경우 디스에이블을 걸어준다. 
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
