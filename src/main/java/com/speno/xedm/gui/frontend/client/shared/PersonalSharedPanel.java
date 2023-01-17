package com.speno.xedm.gui.frontend.client.shared;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.speno.xedm.core.service.serials.SDocument;
import com.speno.xedm.core.service.serials.SFolder;
import com.speno.xedm.core.service.serials.SRecordItem;
import com.speno.xedm.core.service.serials.SShare;
import com.speno.xedm.gui.common.client.Constants;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.RefreshObserver;
import com.speno.xedm.gui.common.client.Session;
import com.speno.xedm.gui.common.client.log.Log;
import com.speno.xedm.gui.common.client.util.ServiceUtil;
import com.speno.xedm.gui.frontend.client.admin.TrackPanel;
import com.speno.xedm.gui.frontend.client.document.DocumentAction;
import com.speno.xedm.gui.frontend.client.document.DocumentActionUtil;
import com.speno.xedm.gui.frontend.client.document.DocumentObserver;
import com.speno.xedm.util.paging.PagingConfig;
import com.speno.xedm.util.paging.PagingResult;

public class PersonalSharedPanel extends VLayout implements DocumentObserver, RefreshObserver{
	private static PersonalSharedPanel instance;
	protected Canvas listingPanel;
	protected Layout listing = new VLayout();
	protected Layout sharedListing = new VLayout();
	private FolderSharingPanel sharingPanel;
	// 상단 버튼 레이아웃
//	private HLayout sharedMiddle = new HLayout();
	private ToolStrip sharedToolbar;
	
	public PersonalSharedPanel(){
		init();
	}

	private void init(){
		setMembersMargin(10);
		setPadding(Constants.PADDING_DEFAULT);
		
		// 리스팅패널 생성
		listingPanel = new Label("&nbsp;" + I18N.message("selectfolder"));
		listing.setWidth100();
		listing.setHeight100();
		listing.setShowResizeBar(false);
		listing.addMember(listingPanel);
		
		// 버튼 Layout
		initActions();
//		setToolbar();
		
		sharingPanel = new FolderSharingPanel();
		addMember(new TrackPanel(I18N.message("dashboard")+" > "+ I18N.message("foldersharing"), this));
		addMembers(listing, sharingPanel);
		showListPanel();
	}
	
//	public void setToolbar(){
//		sharedToolbar = DocumentActionUtil.get().getToolbar(DocumentActionUtil.TYPE_FOLDER_SHARED);
//		sharedMiddle.setMembers(sharedToolbar);
//		sharedMiddle.setHeight(28);
//		sharedMiddle.setWidth100();
//	}
	
	public void showListPanel() {
		if (!(listingPanel instanceof PersonalSharedListPanel)) {
			listing.removeMember(listingPanel);
			listingPanel = new PersonalSharedListPanel();
			listing.addMember(listingPanel);
		}
		
		listing.redraw();
	}
	
	public long expandDocid = 0;
	/**
	 * Search TabBar Action
	 * 20130817 taesu
	 * */
	private void initActions(){
//		DocumentActionUtil.get().createAction("add_sharing", "add_sharing", "add_sharing", new DocumentAction() {
//			@Override
//			protected void doAction(Object[] params) {
//				//TODO
//			}
//		}, DocumentActionUtil.TYPE_FOLDER_SHARING);
		
		//TODO
//		DocumentActionUtil.get().createAction("goto_shared", "client.goto", "actions_goto", true, new DocumentAction() {
//			@Override
//			protected void doAction(Object[] params) {
//				SShare share = (SShare)((PersonalSharedListPanel)listingPanel).getGrid().getAttribute(attribute)
//				
//				// Tab 이동 위한 변수
//				TabSet tabSet = MainPanel.get().getTabSet();
//				// Document Tab으로 이동
//				tabSet.selectTab(Constants.MAIN_TAB_DOCUMENT);
//				
//				SFolder[] folders = DocumentActionUtil.get().getCurrentFolderItems();
//				if (folders != null && folders.length > 0) {
//					expandDocid = folders[0].getId();
//					DocumentsPanel.get().getMenu().expandFolder(folders[0]);
//					return;
//				}
//			}
//		}, DocumentActionUtil.TYPE_FOLDER_SHARING);
		
		DocumentActionUtil.get().createAction("stop_sharing", "stop_sharing", "delete", true, new DocumentAction() {
			@Override
			protected void doAction(Object[] params) {
				long id = Long.parseLong(((PersonalSharedListPanel)listingPanel).getGrid().getSelectedRecord().getAttribute("id"));
				sharingPanel.stopSharing(id);
			}
		}, DocumentActionUtil.TYPE_FOLDER_SHARING);
	}
	
	public void execute(int pageNum, int pageSize){
		PagingConfig config = new PagingConfig(0, pageNum, pageSize);
		getData(config);
	}
	
	public void execute(){
		PagingConfig config = new PagingConfig(0, 1, Session.get().getUser().getPageSize());
		getData(config);
	}

	private void getData(PagingConfig config){
		//폴더 공유 내역 뽑아오기
		ServiceUtil.folder().pagingSourceSharing(Session.get().getSid(), config, new AsyncCallback<PagingResult<SShare>>() {
			@Override
			public void onSuccess(PagingResult<SShare> result) {
				if(result != null){
					List<SShare> shared = result.getData();
					((PersonalSharedListPanel)listingPanel).getGrid().setGridData(shared, ((PersonalSharedListPanel)listingPanel).getGridPager(), result.getTotalLength());
				}
			}
			@Override
			public void onFailure(Throwable caught) {
				Log.serverError(caught, false);
			}
		});
	}
	
	public FolderSharingPanel getSharingPanel() {
		return sharingPanel;
	}

	public static PersonalSharedPanel get() {
		if (instance == null) {
			instance = new PersonalSharedPanel();
		}
		return instance;
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

	@Override
	public void onRefreshBtnClicked(ClickEvent event) {
		// TODO Auto-generated method stub
		execute();
	}

}
