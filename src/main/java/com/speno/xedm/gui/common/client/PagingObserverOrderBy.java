package com.speno.xedm.gui.common.client;

/**
 * Paging Observer
 * 
 * @author �ڻ��
 * @since 1.0
 */
public interface PagingObserverOrderBy extends PagingObserver {
	// 20130812 taesu, ���ļ��� �߰��� ���� �Ű����� �߰�(String orderBy)
	public void onPageDataReqeust(int pageNum, int pageSize, String orderBy);
}
