package com.speno.xedm.gui.frontend.client.admin.documentcode;

import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.speno.xedm.gui.common.client.Constants;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.RefreshObserver;
import com.speno.xedm.gui.frontend.client.admin.TrackPanel;

/**
 * DocumentType Panel
 * 
 * @author �ڻ��
 * @since 1.0
 */
public class DocumentTypePanel extends VLayout implements RefreshObserver{	
	private static DocumentTypePanel instance;	
	
	private HLayout mainHL;
	private DocumentTypeGridPanel documentTypeGridPanel;
	
	/**
	 * �� ������ instance�� ��ȯ��. instance�� ���� ��� ���� �� ��ȯ��
	 * @return
	 */
	public static DocumentTypePanel get() {
		if (instance == null) {
			instance = new DocumentTypePanel();
		}
		return instance;
	}
	
	public DocumentTypePanel() {            	
		setWidth100();
		setMembersMargin(10);
		setPadding(Constants.PADDING_DEFAULT);
		addMember(new TrackPanel(I18N.message("admin")+" > "+ I18N.message("documentcode")+" > "+ I18N.message("documenttype"), this));
		
		createMainPanel(false); //Main�г� ����
	}
	
	/**
	 * Main�г� ����
	 */
	private void createMainPanel(boolean isRefresh) {
		if(isRefresh) {
			removeMember(mainHL);
		}
		
		documentTypeGridPanel = createDocTypeVL(isRefresh);
		
		mainHL = new HLayout(10);
		mainHL.setHeight100();
		mainHL.setMembers(documentTypeGridPanel);
		addMember(mainHL);
	}
	
	/**
	 * Refresh
	 */
	private void refresh() {
		createMainPanel(true);
	}
	
	private DocumentTypeGridPanel createDocTypeVL(boolean isRefresh) {		
		return isRefresh ? 
				new DocumentTypeGridPanel("admin.doccode.doctype", I18N.message("documenttype"), null, true) :
					DocumentTypeGridPanel.get("admin.doccode.doctype", I18N.message("documenttype"), null, true);
	}
	
	/**
	 * Refresh ��ư Ŭ�� �̺�Ʈ ������
	 */
	@Override
	public void onRefreshBtnClicked(ClickEvent event) {
		refresh();
	}
}