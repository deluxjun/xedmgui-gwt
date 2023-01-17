package com.speno.xedm.gui.common.client;

/**
 * Paging Observer
 * 
 * @author 박상기
 * @since 1.0
 */
public interface PagingObserverOrderBy extends PagingObserver {
	// 20130812 taesu, 정렬순서 추가로 인한 매개변수 추가(String orderBy)
	public void onPageDataReqeust(int pageNum, int pageSize, String orderBy);
}
