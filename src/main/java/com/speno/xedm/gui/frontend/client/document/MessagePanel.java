package com.speno.xedm.gui.frontend.client.document;

import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;
import com.smartgwt.client.widgets.tab.events.TabSelectedEvent;
import com.smartgwt.client.widgets.tab.events.TabSelectedHandler;
import com.speno.xedm.gui.common.client.Constants;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.RefreshObserver;
import com.speno.xedm.gui.frontend.client.admin.TrackPanel;

/**
 * Message Panel
 * 
 * @author 남윤성
 * @since 1.0
 */
public class MessagePanel extends VLayout implements RefreshObserver{	
	private static MessagePanel instance;	
	
	private VLayout mainHL;
	private TabSet tabs ;
	private MessageGridPanel messageGridPanelNotice;
	private MessageGridPanel messageGridPanelReceive;
	private MessageGridPanel messageGridPanelSend;
	private Tab notice;
	private Tab received;
	private Tab sent;
	
	/**
	 * 기 생성된 instance를 반환함. instance가 없을 경우 생성 후 반환함
	 * @return
	 */
	public static MessagePanel get() {
		if (instance == null) {
			instance = new MessagePanel();
		}
		return instance;
	}
	
	public MessagePanel() {            	
		setWidth100();
		setMembersMargin(10);
		setPadding(Constants.PADDING_DEFAULT);
		addMember(new TrackPanel(I18N.message("dashboard")+" > "+ I18N.message("message"), this));
		
		createMainPanel(false); //Main패널 생성
	}
	
	/**
	 * Main패널 생성
	 */
	private void createMainPanel(final boolean isRefresh) {
		if(isRefresh) {
			selectTab(Constants.DASHBOARD_MESSAGE_NOTICE);
			tabs.updateTab(2, null);
			tabs.updateTab(1, null); 
			tabs.updateTab(0, null); 
			tabs.removeTab(Constants.DASHBOARD_MESSAGE_SENT);
			tabs.removeTab(Constants.DASHBOARD_MESSAGE_RECEIVED);
			tabs.removeTab(Constants.DASHBOARD_MESSAGE_NOTICE);
			mainHL.removeMember(tabs);
			removeMember(mainHL);
		}
		
		notice = new Tab();
		notice.setTitle(I18N.message("second.notice"));
		notice.setID(Constants.DASHBOARD_MESSAGE_NOTICE);

		received = new Tab();
		received.setTitle(I18N.message("second.received"));
		received.setID(Constants.DASHBOARD_MESSAGE_RECEIVED);
		
		sent = new Tab();
		sent.setTitle(I18N.message("sent"));
		sent.setID(Constants.DASHBOARD_MESSAGE_SENT);
		
		tabs = new TabSet();
		tabs.setTabs(notice, received, sent);
		tabs.addTabSelectedHandler(new TabSelectedHandler() {
			@Override
			public void onTabSelected(TabSelectedEvent event) {				
				Tab tab = ((TabSet)event.getSource()).getSelectedTab();
				if(tab.getTitle().equals(I18N.message("notice"))) {
					messageGridPanelNotice = createMessageVL(isRefresh,"Notice");
					tab.setPane(messageGridPanelNotice);
				}
				if(tab.getTitle().equals(I18N.message("sent"))) {
					messageGridPanelSend = createMessageVL(isRefresh,"Send");
					tab.setPane(createMessageVL(isRefresh,"Send"));
				}
				if(tab.getTitle().equals(I18N.message("second.received"))) {
					messageGridPanelReceive = createMessageVL(isRefresh,"Received");
					tab.setPane(createMessageVL(isRefresh,"Received"));
				}
			}
		});
		mainHL = new VLayout();
		mainHL.setHeight100();        
		mainHL.setMembers(tabs);
        addMembers(mainHL);
		}
	/**
	 * Refresh
	 */
	public void refresh() {
		createMainPanel(true);
	}
	
	/**
	 * Received 탭 선택
	 */
	public void selectTabs() {
		tabs.selectTab(2);
	}

	/**
	 * 20130821, junsoo, 탭을 선택
	 * @param index
	 */
	public void selectTab(String ID) {
		tabs.selectTab(ID);
	}

	/**
	 * MessageGridPanel 생서
	 */
	private MessageGridPanel createMessageVL(boolean isRefresh, String tab) {		
		return isRefresh ? 
				new MessageGridPanel("admin.system.notice"+tab, I18N.message("notice")+tab, tab, null, true, this) :
					MessageGridPanel.get("admin.system.notice"+tab, I18N.message("notice")+tab, tab,  null, true, this);
	}
	
	/**
	 * Refresh 버튼 클릭 이벤트 옵져버
	 */
	@Override
	public void onRefreshBtnClicked(ClickEvent event) {
		refresh();
	}
}