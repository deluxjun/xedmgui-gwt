package com.speno.xedm.gui.frontend.client.document;

import com.smartgwt.client.widgets.layout.VLayout;
import com.speno.xedm.core.service.serials.SDocument;
import com.speno.xedm.core.service.serials.SFolder;
import com.speno.xedm.gui.common.client.FolderObserver;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.log.Log;
import com.speno.xedm.gui.frontend.client.admin.documentbox.SecurityACLGridPanel;

public class DocumentSecurityPanel extends VLayout implements FolderObserver{
	// 하단 보안 Panel
	private SecurityACLGridPanel securityACLGridPanel;
	
	private SDocument document;
	
	public DocumentSecurityPanel(SDocument document) {
		this.document = document;
		initPanel();
	}
	
	private void initPanel(){
		// 하단 Panel 설정
		securityACLGridPanel = new SecurityACLGridPanel("doc.org.secu", I18N.message("acl"), DocumentSecurityPanel.this, "100%");
		securityACLGridPanel.setDocId(document.getId());
		setSelectItemInfo();
		setMembers(securityACLGridPanel);
	}
	
	public void disable(String reason) {
		securityACLGridPanel.disable(reason);
	}

	public void setProfileId(Long profileId){
		Log.debug("[DocumentSecurityPanel] setProfileId : " + profileId);

		long folderId = document.getFolder().getId();
		long parentId = folderId;
		long docId = document.getId();

		securityACLGridPanel.executeFetch(folderId, String.valueOf(profileId), parentId, docId);
	}

	
	/**
	 * Select Item 값 설정
	 */
	private void setSelectItemInfo(){
		// 20131205, junsoo, document의 최신정보를 다시 획득.
		
		
		long folderId = document.getFolder().getId();
		long docId = document.getId();
		long parentId = folderId;
//		String profileId;
//		if(document.getSecurityProfile() == null) profileId = String.valueOf(SSecurityProfile.PROFILE_INHERITEDACL);
//		else profileId = String.valueOf(document.getSecurityProfile());
//		
//		Log.debug("[DocumentSecurityPanel] profile id : " + profileId);
//		
////		if(parentId == Constants.ADMIN_FOLDER_ROOT)	parentId = folderId;
//		// 문서 자체에 보안이 걸려 있을 경우
//		if(document.getSecurityProfile() != null){
//			securityACLGridPanel.executeFetch(folderId, profileId, parentId, docId);
//		}
//		// 문서 자체에 보안이 없을 경우 Folder의 보안정책을 따른다.
//		else{
//			parentId = document.getFolder().getParentId();
//			securityACLGridPanel.executeFetch(folderId, profileId, parentId, docId, true);
////			securityACLGridPanel.setDocId(docId);
//		}
//		// 문서 보안 수정 불가 설정.
//		// 문서 제어 권한이 없을 경우
//		if(!DocumentActionUtil.get().getRights().toString().contains("control"))
//			securityACLGridPanel.disable(I18N.message("noDocumentControlRight"));
//		// 문서가 잠금 상태일 경우
//		else if(document.getStatus() == Constants.DOC_LOCKED)
//			securityACLGridPanel.disable(I18N.message("second.statusLocked"));
	}
	
	public SDocument getDocument() {
		return document;
	}

	public void setDocument(SDocument document) {
		this.document = document;
	}

	@Override
	public void onFolderSelected(SFolder folder) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onFolderSaved(SFolder folder) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onFolderReload() {
		// TODO Auto-generated method stub
		
	}
}
