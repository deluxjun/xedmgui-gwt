package com.speno.xedm.gui.frontend.client.document;

import com.google.gwt.core.client.GWT;
import com.smartgwt.client.types.Visibility;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.speno.xedm.gui.common.client.AdminSubMenu;
import com.speno.xedm.gui.common.client.Constants;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.Session;
import com.speno.xedm.gui.common.client.log.Log;
import com.speno.xedm.gui.common.client.util.IFHistoryObserver;
import com.speno.xedm.gui.common.client.util.Util;
import com.speno.xedm.gui.frontend.client.panels.MainPanel;
import com.speno.xedm.gui.frontend.client.shared.PersonalSharedPanel;
import com.speno.xedm.gwt.service.MessageService;
import com.speno.xedm.gwt.service.MessageServiceAsync;

public class DashboardPanel extends HLayout implements IFHistoryObserver{
    
	private	VLayout vlaylist = new VLayout(); 
//	private	VLayout vlaybtn = new VLayout(); 
//	private HomePanel homePanel;
	
	// �뽬����
    protected Button homeButton;
    // �޼���
	protected Button messageButton;
	// ���� ����
	protected Button sharingButton;
    // ����
	protected Button settingsButton;
	// ����
	protected Button delegationButton;
	
	private static DashboardPanel instance;
	
	protected MessageServiceAsync messageService = (MessageServiceAsync) GWT.create(MessageService.class);
	
	AdminSubMenu menu = new AdminSubMenu() {
		// 20130903, junsoo, ��ư�� ���� ���ѿ� ���� ǥ���ؾ� �ϹǷ� �ּ�ó����.
//		@Override
//		public void buildMenu(long parentMenuId, boolean bByHistory) {
//			// ��ư�� ��� ǥ��.
//			for (Object obj : subMenus.values()) {
//				Button btn = (Button)obj;
//				btn.show();
//			}
//		}
		
		@Override
		public void setContent(String title) {
			// TODO: �޴��߰��� ������ ��.
			deselectButton();
			
			if ("dash_messages".equals(title)) {
				refreshMessage();
			} else if ("dash_home".equals(title)) {
				refreshDash();
			} 
			else if ("dash_sharing".equals(title)) {
				refreshSharing();
			}
			else if ("dash_delegation".equals(title)) {
				refreshDelegation();
			} else if ("dash_settings".equals(title)) {
				refreshSettings();
			}
		}
		
		@Override
		public String getMenuRef() {
			return Constants.MENU_DASHBOARD;
		}
		
		// 20130903, junsoo, ��� �ε��Ǹ� visible �� ���� ��ư ����!
		@Override
		public void onFinished() {
			for (String key : menuList) {
				Button btn = (Button)subMenus.get(key);
				
				// 20130808, junsoo, ������ ������ üũ
				if (!btn.isVisible())
					continue;

				selectButton(btn, key, false);
				break;
			}
		}
	};
	
	// 20130903, junsoo, ���� �޴��� ���ѿ� �°� ǥ��
	public void buildMenu(String finalCallbackId, long parentMenuId, boolean bByHistory) {
		menu.buildMenu(finalCallbackId, parentMenuId, bByHistory);
	}

			
	public static DashboardPanel get() {
		if (instance == null)
			instance = new DashboardPanel();
		return instance;
	}

	public static DashboardPanel getNew() {
		instance = null;
		instance = new DashboardPanel();
		return instance;
	}
	
	
	private DashboardPanel() {
		setWidth100();
		setHeight100();
		
		// home
		homeButton = new Button(I18N.message("home"));
		setButton(homeButton, 236, 0);
		homeButton.setWidth100();
		homeButton.hide();
		
		// �޼���
		messageButton = new Button(I18N.message("message"));
		setButton(messageButton, 236, 0);
		messageButton.setWidth100();
		messageButton.hide();

		// ���� ����
		sharingButton = new Button(I18N.message("s.sharing"));
		setButton(sharingButton, 236, 0);
		sharingButton.setWidth100();
		sharingButton.hide();

		// settings
		settingsButton = new Button(I18N.message("settings"));
		setButton(settingsButton, 236, 0);
		settingsButton.setWidth100();
		settingsButton.hide();
		
		delegationButton = new Button(I18N.message("delegation"));
		setButton(delegationButton, 236, 0);
		delegationButton.setWidth100();
		delegationButton.hide();

		menu.setWidth("15%");
		menu.setHeight100();
		menu.setMembersMargin(5);
//		vlaybtn.setMembers(homeButton, messageButton, sharingButton, settingsButton);
		menu.setShowResizeBar(true);
		
		boolean isFoldeShared = Util.getSetting("setting.foldershared");
		if(isFoldeShared){
			menu.initMenus(
					new Object[]{"dash_home", homeButton},
					new Object[]{"dash_messages", messageButton},
					new Object[]{"dash_sharing", sharingButton},
					new Object[]{"dash_settings", settingsButton},
					new Object[]{"dash_delegation", delegationButton});
		}
		else{
			sharingButton.setVisibility(Visibility.HIDDEN);
			menu.initMenus(
					new Object[]{"dash_home", homeButton},
					new Object[]{"dash_messages", messageButton},
					new Object[]{"dash_settings", settingsButton},
					new Object[]{"dash_delegation", delegationButton});
		}
		
		// �޴� �ʱ�ȭ
//		menu.buildMenu(0, true);
		menu.setHistoryObserver(this);
//		homeButton.select();

		addMember(menu);
		
		boolean isRefresh = true;
		
//		homePanel = createHomeVL(isRefresh);
		
		vlaylist.setWidth100();
		vlaylist.setHeight100();
		vlaylist.setShowEdges(false);
//		vlaylist.addMember(homePanel);
		addMember(vlaylist);
	}
	
	/*
	 * 
	 * ��ư ���ۺ�
	 * 
	 */
	public void refreshMessage(){
//		vlaylist.removeMember(homePanel);
		messageButton.select();
		vlaylist.removeMembers(vlaylist.getMembers());
		vlaylist.addMember(MessagePanel.get());
		MessagePanel.get().refresh();
	}
	public void refreshDash(){
		homeButton.select();
		vlaylist.removeMembers(vlaylist.getMembers());
		vlaylist.addMember(createHomeVL(false));
	}
//	���� ���� 
	public void refreshSharing(){
		sharingButton.select();
		vlaylist.removeMembers(vlaylist.getMembers());
		vlaylist.addMember(PersonalSharedPanel.get());
		PersonalSharedPanel.get().execute();
	}
	public void refreshDelegation(){
		delegationButton.select();
		vlaylist.removeMembers(vlaylist.getMembers());
		vlaylist.addMember(DelegationPanel.get());
	}
	
	public void refreshSettings(){
		settingsButton.select();
		vlaylist.removeMembers(vlaylist.getMembers());
		vlaylist.addMember(SettingsPanel.get());
	}
	
	private HomePanel createHomeVL(boolean isRefresh) {		
		return isRefresh ? 
				new HomePanel("dashboard.home", I18N.message("dashboard")) :
					HomePanel.get("dashboard.home", I18N.message("dashboard"));
	}
	
	// ��ư ����
	protected void setButton(Button btn, int width, int height){
		if(width == 0){ btn.setWidth(50); }
		else btn.setWidth(width);
		btn.setShowRollOver(true);   
		btn.setShowDisabled(true);   
		btn.setShowDown(true);
	}
	
	@Override
	public void selectByHistory(String refid) {
		String[] tags = refid.split(";");
		if (tags != null && tags.length > 0) {
			if ("dashboard".equals(tags[0]) && tags.length > 1) {
				selectMenu(tags[1], "", true);
			}
		}
		Session.get().setCurrentMenuId(refid);
	}
	
	// �޴� ����.
	public void selectMenu(String name, String subMenu, boolean bByHistory) {
		Log.debug("dashboard selectMenu");
		/*
		 *  20130823 taesu ���� ����
		 *  1. Dashboard Tab ����(�̵�)
		 *  2. �޴� ����
		 * */
		
		// expand tab
		MainPanel.get().selectDashboardTab();
		
//		// submenu�� �̵�
//		MessagePanel.get().selectTab(subMenu);
		
		// ��ư ���� �� cotent ǥ��. submenu�� ���̻� �������� �����Ƿ� "" ����
		menu.selectMenu(name, "", bByHistory);
	}
	
	@Override
	public void onHistoryAdded(String refid) {
		Session.get().setCurrentMenuId(refid);
	}
	
	public void deselectButton(){
		homeButton.deselect();
		messageButton.deselect();
		sharingButton.deselect();
		settingsButton.deselect();
		delegationButton.deselect();
	}
	
}