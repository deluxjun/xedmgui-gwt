package com.speno.xedm.gui.frontend.client.stats.statistics;

import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.layout.VLayout;
import com.speno.xedm.gui.common.client.AdminSubMenu;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.Session;
import com.speno.xedm.gui.common.client.util.IFHistoryObserver;
import com.speno.xedm.gui.common.client.util.Util;
import com.speno.xedm.gui.frontend.client.stats.StatsPanel;

/**
 * Statistics Menu
 * 
 * @author 박상기
 * @since 1.0
 */
public class StatisticsMenu extends AdminSubMenu implements IFHistoryObserver{	
	private Button repositoriesBtn;
	private Button statisticsBtn;
	private Button docStatisticsBtn;
	private Button ecmStatisticsBtn;
	private Button monitoringBtn;

	/**
	 * Statistics Menu 생성
	 */
	public StatisticsMenu() {
		setMargin(10);
		setMembersMargin(5);

		repositoriesBtn = new Button(I18N.message("repositories"));
		repositoriesBtn.setWidth100();
		repositoriesBtn.setHeight(25);
		repositoriesBtn.hide();
		
		statisticsBtn = new Button(I18N.message("edmStatistics"));
		statisticsBtn.setWidth100();
		statisticsBtn.setHeight(25);
		statisticsBtn.hide();

		docStatisticsBtn = new Button(I18N.message("docStatistics"));
		docStatisticsBtn.setWidth100();
		docStatisticsBtn.setHeight(25);
		docStatisticsBtn.hide();

		ecmStatisticsBtn = new Button(I18N.message("ecmStatistics"));
		ecmStatisticsBtn.setWidth100();
		ecmStatisticsBtn.setHeight(25);
		ecmStatisticsBtn.hide();
		
		monitoringBtn = new Button(I18N.message("monitoring"));
		monitoringBtn.setWidth100();
		monitoringBtn.setHeight(25);
		monitoringBtn.hide();
		
		boolean isAllianz = "Allianz".equalsIgnoreCase(Session.get().getInfo().getConfig("settings.product.vendor"));
		
		if(Util.getSetting("settings.connect.exNetwork")  && isAllianz){
			initMenus(new Object[]{"repositories", repositoriesBtn},
					new Object[]{"edmStatistics", statisticsBtn},
					new Object[]{"docStatistics", docStatisticsBtn},
					new Object[]{"ecmStatistics", ecmStatisticsBtn},
					new Object[]{"monitoring", monitoringBtn}
					);
		}
		else if(Util.getSetting("settings.connect.exNetwork")  && !isAllianz){
			initMenus(new Object[]{"repositories", repositoriesBtn},
					new Object[]{"edmStatistics", statisticsBtn},
					new Object[]{"docStatistics", docStatisticsBtn},
					new Object[]{"monitoring", monitoringBtn}
					);
		}
		else if(!Util.getSetting("settings.connect.exNetwork")  && isAllianz){
			initMenus(new Object[]{"repositories", repositoriesBtn},
					new Object[]{"edmStatistics", statisticsBtn},
					new Object[]{"docStatistics", docStatisticsBtn},
					new Object[]{"ecmStatistics", ecmStatisticsBtn}
					);
		}
		else{
			initMenus(new Object[]{"repositories", repositoriesBtn},
					new Object[]{"edmStatistics", statisticsBtn},
					new Object[]{"docStatistics", docStatisticsBtn}
					);
		}
	}

	@Override
	public String getMenuRef() {
		return "stats;statistics";
	}

	@Override
	public void setContent(String title) {
		
		
		VLayout content = null;
		if ("repositories".equals(title)) {
			content = RepositoriesPanel.get();
		} else if ("edmStatistics".equals(title)) {
			content = StatisticsPanel.get();
		} else if ("ecmStatistics".equals(title)) {
			content = ECMStatisticsPanel.get();
		} else if ("docStatistics".equals(title)) {
			content = DocStatisticsPanel.get();
		} else if ("monitoring".equals(title)) {
			content = MonitoringPanel.get();
		}
			
		if (content != null)
			StatsPanel.get().setContent(content);
	}

	@Override
	public void selectByHistory(String refid) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onHistoryAdded(String refid) {
		// TODO Auto-generated method stub
	}
}