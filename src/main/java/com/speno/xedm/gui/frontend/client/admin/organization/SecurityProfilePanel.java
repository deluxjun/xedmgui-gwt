package com.speno.xedm.gui.frontend.client.admin.organization;

import java.io.Serializable;

import com.smartgwt.client.data.Record;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.speno.xedm.gui.common.client.Constants;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.RecordObserver;
import com.speno.xedm.gui.common.client.RefreshObserver;
import com.speno.xedm.gui.common.client.log.Log;
import com.speno.xedm.gui.frontend.client.admin.TrackPanel;

/**
 * SecurityProfile Panel
 * 
 * @author �ڻ��
 * @since 1.0
 */
public class SecurityProfilePanel extends VLayout implements RefreshObserver, RecordObserver{	
	private static SecurityProfilePanel instance;
	
	private HLayout mainHL;
	
	private SecurityProfileGridPanel securityProfileGridPanel;
	private SecurityACLGridPanel securityACLGridPanel;
	
	/**
	 * �� ������ instance�� ��ȯ��. instance�� ���� ��� ���� �� ��ȯ��
	 * @return
	 */
	public static SecurityProfilePanel get() {
		if (instance == null) {
			instance = new SecurityProfilePanel();
		}
		return instance;
	}
	
	/**
	 * SecurityProfile �г� ����
	 */
	public SecurityProfilePanel() {		
		setWidth100();
		setMembersMargin(10);
		setPadding(Constants.PADDING_DEFAULT);
		addMember(new TrackPanel(I18N.message("admin")+" > "+ I18N.message("organization")+" > "+ I18N.message("securityprofile"), this));
		
		createMainPanel(false); //Main�г� ����
	}

	/**
	 * Main�г� ����
	 */
	private void createMainPanel(boolean isRefresh) {		
		if(isRefresh) {
			removeMember(mainHL);
		}
		
		securityACLGridPanel = createACLVL(isRefresh);
		securityProfileGridPanel = createProfileVL(isRefresh);
		
		mainHL = new HLayout(10);
		mainHL.setHeight100();
		mainHL.setMembers(securityProfileGridPanel, securityACLGridPanel);
		addMember(mainHL);
	}
	
	/**
	 * Refresh
	 */
	private void refresh() {
		createMainPanel(true);		
	}
	
	/**
	 * 
	 */
	private SecurityProfileGridPanel createProfileVL(boolean isRefresh) {		
		return isRefresh ? 
				new SecurityProfileGridPanel("admin.org.secu", I18N.message("securityprofile"), this, "40%") : 
					SecurityProfileGridPanel.get("admin.org.secu", I18N.message("securityprofile"), this, "40%");
	}
	
	/**
	 * 
	 */
	private SecurityACLGridPanel createACLVL(boolean isRefresh) {		
		return new SecurityACLGridPanel("admin.org.secu", I18N.message("acl"), this, "100%");
//				return isRefresh ? 
//						new SecurityACLGridPanel("admin.org.secu", I18N.message("acl"), this, "100%") :
//							SecurityACLGridPanel.get("admin.org.secu", I18N.message("acl"), this, "100%");
	}
	
	/**
	 * Refresh ��ư Ŭ�� �̺�Ʈ ������ �ڵ鷯
	 */
	@Override
	public void onRefreshBtnClicked(ClickEvent event) {
		refresh();
	}

	/**
	 * Grid ���ڵ� Ŭ�� �̺�Ʈ ������ �ڵ鷯
	 */
	@Override
	public void onRecordSelected(final Serializable id, final Serializable parentId) {
		Log.debug("[ SecurityProfilePanel onRecordSelected ] id["+id+"], parentId["+parentId+"]");
		
		Long lid = -1L;
		if (id instanceof String)
			lid = Long.parseLong((String)id);
		else
			lid = (Long)id;
		
		if(lid < 0) {
			securityACLGridPanel.reset();
		}
		else {
			securityACLGridPanel.executeFetch(lid);
		}
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
	public void onRecordSelected(Record record) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isExistMember() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isIDLong() {
		return true;
	}
}