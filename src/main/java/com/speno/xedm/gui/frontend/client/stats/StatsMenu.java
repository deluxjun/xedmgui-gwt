package com.speno.xedm.gui.frontend.client.stats;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.types.VisibilityMode;
import com.smartgwt.client.widgets.layout.SectionStack;
import com.smartgwt.client.widgets.layout.SectionStackSection;
import com.smartgwt.client.widgets.layout.events.SectionHeaderClickEvent;
import com.smartgwt.client.widgets.layout.events.SectionHeaderClickHandler;
import com.speno.xedm.gui.common.client.AdminSubMenu;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.Session;
import com.speno.xedm.gui.common.client.util.AdminMenuUtil;
import com.speno.xedm.gui.common.client.util.IFAdminMenuPriv;
import com.speno.xedm.gui.common.client.util.IFHistoryObserver;
import com.speno.xedm.gui.common.client.util.ReturnHandler;
import com.speno.xedm.gui.frontend.client.panels.MainPanel;
import com.speno.xedm.gui.frontend.client.stats.statistics.StatisticsMenu;

/**
 * Statistics Tab의 left menu section statck
 * 
 * @author 박상기
 * @since 1.0
 */
public class StatsMenu extends SectionStack implements IFAdminMenuPriv, IFHistoryObserver{

	private static StatsMenu instance;
	
	private static final String STATS_MENU_STATISTICS = "statistics";
	
	private SectionStackSection statisticsSection;
	
	private ReturnHandler loadedListener;		// menu 로드 완료 리스너
	
	
	//ES_ADMINMENU 를 불러오기 위한 부분
	private long lngSelectedMenuId = (long) -1;
	
	Boolean blnIsFirstSection = true;
	
	AdminSubMenu statisticsMenu = new StatisticsMenu();

	/**
	 * 기 생성된 instance를 반환함. instance가 없을 경우 생성 후 반환함
	 * @return
	 */
	public static StatsMenu get() {
		if (instance == null)
			instance = new StatsMenu();
		return instance;
	}

	public void setLoadedListener(ReturnHandler loadedListener) {
		this.loadedListener = loadedListener;
	}

	@Override
	public void buildMenu(final String finalCallbackId, long parentMenuId, final boolean bByHistory) {
		AdminMenuUtil.get().hasPriv(finalCallbackId, parentMenuId, "statistics", new AsyncCallback<Long>() {
			@Override
			public void onSuccess(Long id) {
				if (id != null) {
//					boolean bHistory = !blnIsFirstSection;
//					statisticsSection.setExpanded(blnIsFirstSection);
//					//statisticsSection.setExpanded(false);
//	
//					blnIsFirstSection = false;
					
					//섹션추가											
					addSection(statisticsSection);
					
					statisticsMenu.buildMenu(finalCallbackId, id, true);
					statisticsMenu.setHistoryObserver(StatsMenu.this);
					if (loadedListener != null){
						loadedListener.onReturn(null);
					}
				}
			}
			@Override
			public void onFailure(Throwable caught) {
			}
		});		
	}
	
	private StatsMenu() {
		setVisibilityMode(VisibilityMode.MUTEX);
		setWidth100();

		statisticsSection = new SectionStackSection(I18N.message("statistics"));
		statisticsSection.setName(STATS_MENU_STATISTICS);
		statisticsSection.addItem(statisticsMenu);
		
		addSectionHeaderClickHandler(new SectionHeaderClickHandler() {
			@Override
			public void onSectionHeaderClick(SectionHeaderClickEvent event) {
				
				/*
				 * 선택된 버튼에 해당하는 패널을 로드함.
				 */
				
				if(STATS_MENU_STATISTICS.equals(event.getSection().getName())) {
					statisticsMenu.setContentBySelectedBtn();
				}
			}
		});


		
//		//"statictis" 탭에 대해 메뉴 ID 알아오기
//		service.findByUserIdAndParentId(Session.get().getSid(), (long) 0, new AsyncCallback<List<SAdminMenu>>() {
//			@Override
//			public void onSuccess(List<SAdminMenu> result) {
//			    
//				int iTotalCnt = result.size();
//
//				//데이터가 존재하면
//				if(iTotalCnt > 0){
//
//					//Admin 및 Stats 메뉴에 해당되는 목록 알아오기
//					for(int i=0; i< result.size(); i++){
//						//메뉴의 타이틀
//						String sMenuTitle = result.get(i).getTitle();
//						String sParentId = String.valueOf(result.get(i).getParentId());
//						
//						if ("0".equals(sParentId)){
//							if("stats".equals(sMenuTitle)){
//								lngSelectedMenuId = result.get(i).getId();
//							}
//						}
//					}
//					
//					
//					//값 알아오기에 성공한 경우 "statictis" 탭에 해당되는 섹션 리스트 가져오기
//					if(lngSelectedMenuId > -1){
//						serviceSection.findByUserIdAndParentId(Session.get().getSid(), lngSelectedMenuId, new AsyncCallback<List<SAdminMenu>>() {
//							@Override
//							public void onSuccess(List<SAdminMenu> resultSection) {
//								//데이터가 존재하면
//								if(resultSection.size() > 0){
//									
//									//해당되는 section 목록 알아오기
//									for(int i=0; i< resultSection.size(); i++){
//										//메뉴의 타이틀
//										String sSectionTitle = resultSection.get(i).getTitle();
//										
//										// "statistics" 섹션이면 
//										if("statistics".equals(sSectionTitle)){
//											
//											//statisticsSection.setExpanded(blnIsFirstSection);
//											statisticsSection.setExpanded(false);
//
//											blnIsFirstSection = false;
//											
//											//섹션추가											
//											addSection(statisticsSection);
//											
//											break;
//										}
//									}
//								}
//							}
//							@Override
//							public void onFailure(Throwable caught) {
//								Log.warn("Second.Client.SelectQueryFailure", caught.getMessage());
//							}
//						});
//						
//						
//					}
//				}
//			}		
//			
//			@Override
//			public void onFailure(Throwable caught) {
//				Log.warn("Second.Client.SelectQueryFailure", caught.getMessage());
//			}
//		});
		//------------------------------------------------------------------------------------------

	}
	
	

	@Override
	public void selectMenu(String name, String subMenu, boolean bByHistory) {
		SectionStackSection section = null;
		IFAdminMenuPriv menu = null;
		
		MainPanel.get().selectStatsTab();

		if (name == null || name.length() < 1)
			return;

		if (STATS_MENU_STATISTICS.equals(name)) {
			section = statisticsSection;
			menu = statisticsMenu;
		}
		
		if (section != null && menu != null) {
			// close all
			for (SectionStackSection s : getSections()) {
				s.setExpanded(false);
			}

			section.setExpanded(true);
			
			// sub menu
			if (subMenu == null)
				subMenu = "";
			
			if (subMenu.length() > 0) {
				menu.selectMenu(subMenu, null, bByHistory);

			}
			expandSection(name);
		}
	}

	
	/////////////////////////////
	// History

	@Override
	public void selectByHistory(String id) {
		String[] tags = id.split(";");
		if (tags != null && tags.length > 0) {
			if ("stats".equals(tags[0]) && tags.length > 2) {
				selectMenu(tags[1], tags[2], true);
			}
		}
		Session.get().setCurrentMenuId(id);
	}
	
	@Override
	public void onHistoryAdded(String refid) {
		Session.get().setCurrentMenuId(refid);
	}
}