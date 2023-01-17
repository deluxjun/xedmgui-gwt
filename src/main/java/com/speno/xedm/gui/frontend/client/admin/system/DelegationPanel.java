package com.speno.xedm.gui.frontend.client.admin.system;

import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.speno.xedm.gui.common.client.Constants;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.RefreshObserver;
import com.speno.xedm.gui.frontend.client.admin.TrackPanel;
import com.speno.xedm.gui.frontend.client.document.DelegationGridPanel;

/**
 * Mandate Panel
 * 
 * @author ������
 * @since 1.0
 */
public class DelegationPanel extends VLayout implements RefreshObserver{	
	private static DelegationPanel instance;	
	
	private HLayout mainHL;
	private DelegationGridPanel delegatioinGridPanel;
	
	/**
	 * �� ������ instance�� ��ȯ��. instance�� ���� ��� ���� �� ��ȯ��
	 * @return
	 */
	public static DelegationPanel get() {
		if (instance == null) {
			instance = new DelegationPanel();
		}
		return instance;
	}
	
	public DelegationPanel() {            	
		setWidth100();
		setMembersMargin(10);
		setPadding(Constants.PADDING_DEFAULT);
		addMember(new TrackPanel(I18N.message("admin")+" > "+ I18N.message("system")+" > "+ I18N.message("delegation"), this));
		
		createMainPanel(false); //Main�г� ����
	}
	
	/**
	 * Main�г� ����
	 */
	private void createMainPanel(boolean isRefresh) {
		if(isRefresh) {
			removeMember(mainHL);
		}
		
		delegatioinGridPanel = createDelegationVL(isRefresh);
		
		mainHL = new HLayout(10);
		mainHL.setHeight100();
		mainHL.setMembers(delegatioinGridPanel);
		addMember(mainHL);
	}
	
	/**
	 * Refresh
	 */
	private void refresh() {
		createMainPanel(true);
	}
	
	private DelegationGridPanel createDelegationVL(boolean isRefresh) {		
		return isRefresh ? 
				new DelegationGridPanel("admin.system.delegation", I18N.message("delegation"), null, true) :
					DelegationGridPanel.get("admin.system.delegation", I18N.message("delegation"), null, true);
	}
	
	/**
	 * Refresh ��ư Ŭ�� �̺�Ʈ ������
	 */
	@Override
	public void onRefreshBtnClicked(ClickEvent event) {
		refresh();
	}
}