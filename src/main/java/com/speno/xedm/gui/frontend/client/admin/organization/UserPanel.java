package com.speno.xedm.gui.frontend.client.admin.organization;

import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.speno.xedm.gui.common.client.Constants;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.RefreshObserver;
import com.speno.xedm.gui.frontend.client.admin.TrackPanel;

/**
 * User Panel
 * 
 * @author �ڻ��
 * @since 1.0
 */
public class UserPanel extends VLayout implements RefreshObserver{	
	private static UserPanel instance;
	
	private HLayout mainHL;
	private UserGridPanel userGridPanel;
	
	/**
	 * �� ������ instance�� ��ȯ��. instance�� ���� ��� ���� �� ��ȯ��
	 * @return
	 */
	public static UserPanel get() {
		if (instance == null) {
			instance = new UserPanel();
		}
		return instance;
	}

	/**
	 * User �г� ����
	 */
	public UserPanel() {		
		setWidth100();
		setMembersMargin(10);
		setPadding(Constants.PADDING_DEFAULT);
		addMember(new TrackPanel(I18N.message("admin")+" > "+ I18N.message("organization")+" > "+ I18N.message("user"), this));
		
		createMainPanel(false); //Main�г� ����
	}
	
	/**
	 * Main�г� ����
	 */
	private void createMainPanel(boolean isRefresh) {		
		if(isRefresh) {
			removeMember(mainHL);
		}
		
		userGridPanel = createUsersVL(isRefresh);
		
		mainHL = new HLayout(10);
		mainHL.setHeight100();
		mainHL.setMembers(userGridPanel);
		addMember(mainHL);
	}
	
	/**
	 * Refresh
	 */
	private void refresh() {
		createMainPanel(true);
	}
		
	/**
	 * Users �г� ����
	 */
	private UserGridPanel createUsersVL(boolean isRefresh) {		
		return isRefresh ? 
				new UserGridPanel("admin.org.user", I18N.message("user")) :
					UserGridPanel.get("admin.org.user", I18N.message("user"));
	}
	
	/**
	 * Refresh ��ư Ŭ�� �̺�Ʈ ������
	 */
	@Override
	public void onRefreshBtnClicked(ClickEvent event) {
		refresh();
	}
}