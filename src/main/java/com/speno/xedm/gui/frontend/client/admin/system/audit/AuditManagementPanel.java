package com.speno.xedm.gui.frontend.client.admin.system.audit;

import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;
import com.smartgwt.client.widgets.tab.events.TabSelectedEvent;
import com.smartgwt.client.widgets.tab.events.TabSelectedHandler;
import com.speno.xedm.gui.common.client.Constants;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.frontend.client.admin.TrackPanel;

/**
 * AuditManagement Panel
 * 
 * @author 남윤성
 * @since 1.0
 */
public class AuditManagementPanel extends VLayout {	
	private static AuditManagementPanel instance = null;
	
	private VLayout mainPanel;	
	private TabSet tabs = new TabSet();
	
	/**
	 * 기 생성된 instance를 반환함. instance가 없을 경우 생성 후 반환함
	 * @return
	 */
	public static AuditManagementPanel get() {
		if (instance == null) {
			instance = new AuditManagementPanel();
		}
		return instance;
	}

	public AuditManagementPanel() {
		setWidth100();
		setMembersMargin(10);
		setPadding(Constants.PADDING_DEFAULT);
		addMember(new TrackPanel(I18N.message("admin")+" > "+ I18N.message("system")+" > "+ I18N.message("audit"), null));
		
		createMainPanel(); //Main패널 생성
	}
	
	/**
	 * Main패널 생성
	 */
	private void createMainPanel() {
		
		Tab audit = new Tab();
		audit.setTitle(I18N.message("audit"));

		Tab management = new Tab();
		management.setTitle(I18N.message("second.management"));
		
		tabs.setTabs(audit, management);
		tabs.addTabSelectedHandler(new TabSelectedHandler() {
			@Override
			public void onTabSelected(TabSelectedEvent event) {				
				Tab tab = ((TabSet)event.getSource()).getSelectedTab();
				
				if(tab.getTitle().equals(I18N.message("audit"))) {
					tab.setPane(AuditPanel.get());
				}
				if(tab.getTitle().equals(I18N.message("second.management"))) {
					tab.setPane(ManagementPanel.get());
				}
			}
		});
		
		mainPanel = new VLayout();
        mainPanel.setHeight100();        
        mainPanel.setMembers(tabs);
        addMembers(mainPanel);
	}
}