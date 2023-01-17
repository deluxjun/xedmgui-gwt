package com.speno.xedm.gui.frontend.client.admin.system;

import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;
import com.smartgwt.client.widgets.tab.events.TabSelectedEvent;
import com.smartgwt.client.widgets.tab.events.TabSelectedHandler;
import com.speno.xedm.gui.common.client.Constants;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.frontend.client.admin.TrackPanel;

/**
 * General Panel
 * 
 * @author 박상기
 * @since 1.0
 */
public class GeneralPanel extends VLayout {	
	private static GeneralPanel instance = null;
	
	private VLayout mainPanel;	
	private TabSet tabs = new TabSet();
	
	/**
	 * 기 생성된 instance를 반환함. instance가 없을 경우 생성 후 반환함
	 * @return
	 */
	public static GeneralPanel get() {
		if (instance == null) {
			instance = new GeneralPanel();
		}
		return instance;
	}

	public GeneralPanel() {
		setWidth100();
		setMembersMargin(10);
		setPadding(Constants.PADDING_DEFAULT);
		addMember(new TrackPanel(I18N.message("admin")+" > "+ I18N.message("system")+" > "+ I18N.message("general"), null));
		
		createMainPanel(); //Main패널 생성
	}
	
	/**
	 * Main패널 생성
	 */
	private void createMainPanel() {
		/*
		Tab repositories = new Tab();
		repositories.setTitle(I18N.message("repositories"));
		
		Tab stats = new Tab();
		stats.setTitle(I18N.message("statistics"));
		
		Tab monitoring = new Tab();
		monitoring.setTitle(I18N.message("monitoring"));
		*/

		Tab sessions = new Tab();
		sessions.setTitle(I18N.message("sessions"));

		Tab log = new Tab();
		log.setTitle(I18N.message("log"));
		
		//tabs.setTabs(repositories, stats, monitoring, sessions, log);		
		tabs.setTabs(sessions, log);
		tabs.addTabSelectedHandler(new TabSelectedHandler() {
			@Override
			public void onTabSelected(TabSelectedEvent event) {				
				Tab tab = ((TabSet)event.getSource()).getSelectedTab();
				/*
				if(tab.getTitle().equals(I18N.message("repositories"))) {
					tab.setPane(RepositoriesPanel.get());
				}
				if(tab.getTitle().equals(I18N.message("statistics"))) {
					tab.setPane(StatisticsPanel.get());
				}
				if(tab.getTitle().equals(I18N.message("monitoring"))) {
					tab.setPane(MonitoringPanel.get());
				}
				*/
				if(tab.getTitle().equals(I18N.message("sessions"))) {
					tab.setPane(SessionsPanel.get());
				}
				if(tab.getTitle().equals(I18N.message("log"))) {
					tab.setPane(LogsPanel.get());
				}
			}
		});
		
		mainPanel = new VLayout();
        mainPanel.setHeight100();        
        mainPanel.setMembers(tabs);
        addMembers(mainPanel);
	}
}