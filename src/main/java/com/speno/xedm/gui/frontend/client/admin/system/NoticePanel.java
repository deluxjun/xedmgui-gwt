package com.speno.xedm.gui.frontend.client.admin.system;

import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.speno.xedm.gui.common.client.Constants;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.RefreshObserver;
import com.speno.xedm.gui.frontend.client.admin.TrackPanel;

/**
 * Notice Panel
 * 
 * @author na
 * @since 1.0
 */
public class NoticePanel extends VLayout implements RefreshObserver{	
	private static NoticePanel instance;	
	
	private HLayout mainHL;
	private NoticeGridPanel noticeGridPanel;
	
	/**
	 * �� ������ instance�� ��ȯ��. instance�� ���� ��� ���� �� ��ȯ��
	 * @return
	 */
	public static NoticePanel get() {
		if (instance == null) {
			instance = new NoticePanel();
		}
		return instance;
	}
	
	public NoticePanel() {            	
		setWidth100();
		setMembersMargin(10);
		setPadding(Constants.PADDING_DEFAULT);
		addMember(new TrackPanel(I18N.message("admin")+" > "+ I18N.message("system")+" > "+ I18N.message("notice"), this));
		
		createMainPanel(false); //Main�г� ����
	}
	
	/**
	 * Main�г� ����
	 */
	private void createMainPanel(boolean isRefresh) {
		if(isRefresh) {
			removeMember(mainHL);
		}
		
		noticeGridPanel = createNoticeGridVL(isRefresh);
		
		mainHL = new HLayout(10);
		mainHL.setHeight100();
		mainHL.setMembers(noticeGridPanel);
		addMember(mainHL);
	}
	
	/**
	 * Refresh
	 */
	private void refresh() {
		createMainPanel(true);
	}
	
	/**
	 * NoticeGridPanel ����
	 */
	private NoticeGridPanel createNoticeGridVL(boolean isRefresh) {		
		return isRefresh ? 
				new NoticeGridPanel("admin.system.notice", I18N.message("notice"), null, true) :
					NoticeGridPanel.get("admin.system.notice", I18N.message("notice"), null, true);
	}
	
	/**
	 * Refresh ��ư Ŭ�� �̺�Ʈ ������
	 */
	@Override
	public void onRefreshBtnClicked(ClickEvent event) {
		refresh();
	}
}