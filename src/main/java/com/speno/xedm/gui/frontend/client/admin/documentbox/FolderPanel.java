package com.speno.xedm.gui.frontend.client.admin.documentbox;

import java.io.Serializable;

import com.google.gwt.core.client.GWT;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.speno.xedm.core.service.serials.SFolder;
import com.speno.xedm.gui.common.client.Constants;
import com.speno.xedm.gui.common.client.FolderObserver;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.RecordObserver;
import com.speno.xedm.gui.common.client.RefreshObserver;
import com.speno.xedm.gui.common.client.util.Util;
import com.speno.xedm.gui.frontend.client.admin.TrackPanel;

/**
 * Shared Folder Panel
 * 
 * @author 박상기
 * @since 1.0
 */
public class FolderPanel extends VLayout implements RefreshObserver, RecordObserver, FolderObserver {	
	private static FolderPanel instance = null;
	
	private HLayout mainHL;	
	private VLayout typeAndSecuVL;	
	private FolderTreePanel folderTreePanel;
	private DocTypePanel docTypePanel;
	private FileTypePanel fileTypePanel;
	private SecurityACLGridPanel securityACLGridPanel;
	
	/**
	 * 기 생성된 instance를 반환함. instance가 없을 경우 생성 후 반환함
	 * @return
	 */
	public static FolderPanel get() {
		if (instance == null) {
			instance = new FolderPanel();
		}
		return instance;
	}
	
	/**
	 * Shared Folder Panel 생성
	 */
	public FolderPanel() {            	
		setWidth100();
		setMembersMargin(10);		
		setPadding(Constants.PADDING_DEFAULT);
		addMember(new TrackPanel(I18N.message("admin")+" > "+ I18N.message("sharedfolder")+" > "+ I18N.message("sharedfolder"), this));
		
		createMainPanel(false); //Main패널 생성
	}
	
	/**
	 * Main패널 생성
	 */
	private void createMainPanel(boolean isRefresh) {		
		if(isRefresh) {
			removeMember(mainHL);
		}
		
		folderTreePanel = createFolderVL(isRefresh);
		folderTreePanel.setShowResizeBar(true);
		
		docTypePanel = createDocTypeVL(isRefresh);
		fileTypePanel = createFileTypeVL(isRefresh);
		securityACLGridPanel = createACLVL(isRefresh);
		
        VLayout typesVL = new VLayout(10);
		typesVL.setWidth100();
		typesVL.setShowResizeBar(true);
		//typesVL.setExtraSpace(5);
		typesVL.setPadding(Constants.PADDING_DEFAULT);
		typesVL.addMembers(docTypePanel, fileTypePanel);
		
		typeAndSecuVL = new VLayout(10);
		typeAndSecuVL.setWidth100();
		typeAndSecuVL.addMembers(typesVL, securityACLGridPanel);
		
		mainHL = new HLayout(10);
		mainHL.setHeight100();
		mainHL.setMembers(folderTreePanel, typeAndSecuVL);
        addMember(mainHL);
	}
	
	/**
	 * Refresh
	 */
	private void refresh() {
		createMainPanel(true);
	}
	
	/**
	 * Folder Tree Panel 생성
	 * @param isRefresh
	 * @return
	 */
	private FolderTreePanel createFolderVL(boolean isRefresh) {		
		return isRefresh ? 
				new FolderTreePanel("admin.docbox.docbox", this, true, true, true, "250", false) : 
					FolderTreePanel.get("admin.docbox.docbox", this, true, true, true, "250", false);
	}
	
	/**
	 * Refresh 여부에 따른 Doc Type Panel 생성
	 * @param isRefresh
	 * @return
	 */
	private DocTypePanel createDocTypeVL(boolean isRefresh) {
		return isRefresh ? new DocTypePanel(isRefresh) : DocTypePanel.get(isRefresh);
	}
	
	/**
	 * Refresh 여부에 따른 File type Panel 생성
	 * @param isRefresh
	 * @return
	 */
	private FileTypePanel createFileTypeVL(boolean isRefresh) {
		return isRefresh ? new FileTypePanel(isRefresh) : FileTypePanel.get(isRefresh);
	}
	
	/**
	 * Refresh 여부에 따른 Access Control List Panel 생성
	 * @param isRefresh
	 * @return
	 */
	private SecurityACLGridPanel createACLVL(boolean isRefresh) {		
		return isRefresh ? 
				new SecurityACLGridPanel("admin.docbox.docbox", I18N.message("acl"), this, "100%") :
					SecurityACLGridPanel.get("admin.docbox.docbox", I18N.message("acl"), this, "100%");
	}
	
	/**
	 * Refresh 버튼 클릭 이벤트 옵져버 핸들러
	 */
	@Override
	public void onRefreshBtnClicked(ClickEvent event) {
		refresh();
	}

	/**
	 *  레코드 Selected 이벤트 옵져버 핸들러
	 */
	@Override
	public void onRecordSelected(Serializable id, Serializable parentId) {
		GWT.log("[ FolderPanel onRecordSelected ] id["+id+"], parentId["+parentId+"]", null);		
		docTypePanel.executeFetch((Long)id, (Long)parentId);
		fileTypePanel.executeFetch((Long)id, (Long)parentId);
	}
	

	@Override
	public void onRecordSelected(final Record record) {
		long folderId = Util.getAslong(record.getAttributeAsString("id"));
		String profileId = record.getAttributeAsString("profileId");
		long parentId = Util.getAslong(record.getAttributeAsString("parentId"));
		
		GWT.log("[ FolderPanel onRecordSelected ] folderId["+folderId+"], profileId["+profileId+"], parentId["+parentId+"]", null);
		securityACLGridPanel.executeFetch(folderId, profileId, parentId, 0);

		// ########################################################
		// Doc ACL Test - goodbong
//		long docId = 298;
//		
//		parentId = docId;
//		folderId = docId;		
//		if(profileId == null) profileId = String.valueOf(SSecurityProfile.PROFILE_INHERITEDACL); 
//		
//		System.out.println("[ FolderPanel onRecordSelected ] folderId["+folderId+"], profileId["+profileId+"], parentId["+parentId+"], docId["+docId+"]");
//		securityACLGridPanel.executeFetch(folderId, profileId, parentId, docId);
		// ########################################################
	}

	@Override
	public void onFolderSelected(SFolder folder) {
		// TODO Auto-generated method stub		
	}

	@Override
	public void onFolderSaved(SFolder folder) {
		folderTreePanel.setSecurityProfileId(folder);
	}

	@Override
	public void onFolderReload() {
		// TODO Auto-generated method stub		
	}

	@Override
	public boolean isExistMember() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public void onRecordClick(Record record) {
		// TODO Auto-generated method stub		
	}

	@Override
	public void onRecordDoubleClick(Record record) {
		// TODO Auto-generated method stub		
	}

	@Override
	public boolean isIDLong() {
		return true;
	}
}