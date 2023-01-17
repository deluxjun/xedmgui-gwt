package com.speno.xedm.gui.frontend.client.document;

import com.smartgwt.client.widgets.layout.VLayout;
import com.speno.xedm.core.service.serials.SDocument;
import com.speno.xedm.core.service.serials.SFolder;
import com.speno.xedm.gui.common.client.FolderObserver;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.log.Log;
import com.speno.xedm.gui.frontend.client.admin.documentbox.SecurityACLGridPanel;

public class DocumentSecurityPanel extends VLayout implements FolderObserver{
	// �ϴ� ���� Panel
	private SecurityACLGridPanel securityACLGridPanel;
	
	private SDocument document;
	
	public DocumentSecurityPanel(SDocument document) {
		this.document = document;
		initPanel();
	}
	
	private void initPanel(){
		// �ϴ� Panel ����
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
	 * Select Item �� ����
	 */
	private void setSelectItemInfo(){
		// 20131205, junsoo, document�� �ֽ������� �ٽ� ȹ��.
		
		
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
//		// ���� ��ü�� ������ �ɷ� ���� ���
//		if(document.getSecurityProfile() != null){
//			securityACLGridPanel.executeFetch(folderId, profileId, parentId, docId);
//		}
//		// ���� ��ü�� ������ ���� ��� Folder�� ������å�� ������.
//		else{
//			parentId = document.getFolder().getParentId();
//			securityACLGridPanel.executeFetch(folderId, profileId, parentId, docId, true);
////			securityACLGridPanel.setDocId(docId);
//		}
//		// ���� ���� ���� �Ұ� ����.
//		// ���� ���� ������ ���� ���
//		if(!DocumentActionUtil.get().getRights().toString().contains("control"))
//			securityACLGridPanel.disable(I18N.message("noDocumentControlRight"));
//		// ������ ��� ������ ���
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
