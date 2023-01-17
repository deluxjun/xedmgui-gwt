package com.speno.xedm.gui.frontend.client.search;

import java.util.List;
import java.util.Map;

import com.smartgwt.client.widgets.layout.HLayout;
import com.speno.xedm.core.service.serials.SDocument;
import com.speno.xedm.core.service.serials.SFolder;
import com.speno.xedm.core.service.serials.SHit;
import com.speno.xedm.core.service.serials.SRecordItem;
import com.speno.xedm.gui.common.client.Session;
import com.speno.xedm.gui.frontend.client.document.DocumentActionUtil;
import com.speno.xedm.gui.frontend.client.document.DocumentObserver;

public class SearchPanel extends HLayout implements DocumentObserver {
	public static final int RESULT_DOCUMENTS = 0;
	public static final int RESULT_CONTENTS = 1;
	public static final int RESULT_FOLDERS = 2;
	
	private static SearchPanel instance;
	
	private int currentListType = RESULT_DOCUMENTS;

	// 검색 메뉴
	private SearchMenu leftMenu;

	// right 패널들 정의
	private DocumentsListPanel	rightPanelDocuments;
	
	private ContentsListPanel	rightPanelContents;

	private FoldersListPanel	rightPanelFolders;
	
	public SearchPanel(){
		Session.get().getUser();
		init();
	}
	
	private void init(){
		this.setHeight100();
		this.setWidth100();

		// SearchMenu 생성
		leftMenu = SearchMenu.get();
//				leftMenu.setWidth("17%");
		leftMenu.setWidth(230);
		leftMenu.setMinWidth(leftMenu.getWidth());
		leftMenu.setHeight100();
		leftMenu.setShowResizeBar(true);
	
		addMembers(leftMenu);
	}
	
	public void showListPanel() {
		// TODO : remove and add
	}
	
	public void showDummy () {
		clearPanel();
	}
	
	// 20140220, junsoo, set response paging
	public void responsePaging(boolean hasNext, int totalCount, int pageNum) {
		switch (currentListType) {
		case RESULT_DOCUMENTS:
			rightPanelDocuments.getGridPager().updatePageStatus(hasNext, totalCount, pageNum);
			break;
		case RESULT_FOLDERS:
			rightPanelFolders.getGridPager().updatePageStatus(hasNext, totalCount, pageNum);
			break;
		case RESULT_CONTENTS:
			rightPanelContents.getGridPager().updatePageStatus(hasNext, totalCount, pageNum);
			break;

		default:
			break;
		}
	}
	
	public void show(int resultType) {
		switch (resultType) {
		case RESULT_DOCUMENTS:
			showDocuments();
			break;
		case RESULT_CONTENTS:
			showContents();
			break;
		case RESULT_FOLDERS:
			showFolders();
			break;

		default:
			showDummy();
			break;
		}
		
		DocumentActionUtil.get().setActivatedMenuType(DocumentActionUtil.TYPE_SEARCH_PERSONAL);
	}

	private void showDocuments () {
		if (rightPanelDocuments == null)
			rightPanelDocuments = new DocumentsListPanel();
		
		clearPanel();
		
		addMember(rightPanelDocuments);
		currentListType = RESULT_DOCUMENTS;
	}
	
	private void showContents () {
		if (rightPanelContents == null)
			rightPanelContents = new ContentsListPanel();
		
		clearPanel();

		addMember(rightPanelContents);
		currentListType = RESULT_CONTENTS;
	}

	// folders list 관련
	private void showFolders() {
		if (rightPanelFolders == null)
			rightPanelFolders = new FoldersListPanel();
		
		clearPanel();

		addMember(rightPanelFolders);
		currentListType = RESULT_FOLDERS;
	}
	
	public void executeFetch(){
		switch (currentListType) {
		case RESULT_DOCUMENTS:
			rightPanelDocuments.executeFetch(getPageNum(), getPageSize(), DocumentActionUtil.get().getCurrentSorter());
			break;
		case RESULT_FOLDERS:
			rightPanelFolders.executeFetch(getPageNum(), getPageSize(), DocumentActionUtil.get().getCurrentSorter());
			break;
		case RESULT_CONTENTS:
			rightPanelContents.executeFetch(getPageNum(), getPageSize(), DocumentActionUtil.get().getCurrentSorter());
			break;

		default:
			break;
		}
	}

	// set data
	public void setDocumentsData(List<SDocument> data, boolean isNew) {
		showDocuments();
		rightPanelDocuments.setData(data);
	}
	public void setContentsFields(String[] fields) {
		rightPanelContents.setFields(fields);
	}
	public void setContentsData(List<Map<String, String>> data) {
		showContents();
		rightPanelContents.setData(data);
	}

	public void setFoldersData(List<SHit> data) {
		showFolders();
		rightPanelFolders.setData(data);
	}
	
	public void setPageNum(int pageNum) {
		if (rightPanelContents != null && hasMember(rightPanelContents))
			rightPanelContents.setPageNum(pageNum);
		if (rightPanelDocuments != null && hasMember(rightPanelDocuments))
			rightPanelDocuments.setPageNum(pageNum);
		if (rightPanelFolders != null && hasMember(rightPanelFolders))
			rightPanelFolders.setPageNum(pageNum);
	}
	
	public int getPageNum() {
		if (rightPanelContents != null && hasMember(rightPanelContents))
			return rightPanelContents.getPageNum();
		if (rightPanelDocuments != null && hasMember(rightPanelDocuments))
			return rightPanelDocuments.getPageNum();
		if (rightPanelFolders != null && hasMember(rightPanelFolders))
			return rightPanelFolders.getPageNum();
		return 1;
	}
	
	public int getPageSize() {
		if (rightPanelContents != null && hasMember(rightPanelContents))
			return rightPanelContents.getPageSize();
		if (rightPanelDocuments != null && hasMember(rightPanelDocuments))
			return rightPanelDocuments.getPageSize();
		if (rightPanelFolders != null && hasMember(rightPanelFolders))
			return rightPanelFolders.getPageSize();
		return Session.get().getUser().getPageSize();
	}
	
	public void setPageSize(int pageSize) {
		if (rightPanelContents != null && hasMember(rightPanelContents))
			rightPanelContents.setPageSize(pageSize);
		if (rightPanelDocuments != null && hasMember(rightPanelDocuments))
			rightPanelDocuments.setPageSize(pageSize);
		if (rightPanelFolders != null && hasMember(rightPanelFolders))
			rightPanelFolders.setPageSize(pageSize);
	}

	// clear panels
	private void clearPanel() {
		if (rightPanelContents != null && hasMember(rightPanelContents))
			removeMember(rightPanelContents);

		if (rightPanelDocuments != null && hasMember(rightPanelDocuments))
			removeMember(rightPanelDocuments);

		if (rightPanelFolders != null && hasMember(rightPanelFolders))
			removeMember(rightPanelFolders);

	}

	// 즐겨찾기나 대쉬보드에서 선택할 문서 아이디
	public long expandDocid = 0;
	
		/**
	 * 기 생성된 instance를 반환함. instance가 없을 경우 생성 후 반환함
	 * @return instance
	 */
	public static SearchPanel get() {
		if (instance == null)
			instance = new SearchPanel();
		return instance;
	}

	public SearchMenu getLeftMenu() {
		return leftMenu;
	}

	@Override
	public void onDocumentSaved(SDocument document) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onServiceComplite(String message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDocumentSelected(SRecordItem[] items) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onReloadRequest(SFolder folder) {
		// TODO Auto-generated method stub
		
	}
	
	public void resetGridForContents(){
		if (rightPanelContents != null && hasMember(rightPanelContents))
			rightPanelContents.resetGrid();
	}
}
