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
 * Statistics Tab�� left menu section statck
 * 
 * @author �ڻ��
 * @since 1.0
 */
public class StatsMenu extends SectionStack implements IFAdminMenuPriv, IFHistoryObserver{

	private static StatsMenu instance;
	
	private static final String STATS_MENU_STATISTICS = "statistics";
	
	private SectionStackSection statisticsSection;
	
	private ReturnHandler loadedListener;		// menu �ε� �Ϸ� ������
	
	
	//ES_ADMINMENU �� �ҷ����� ���� �κ�
	private long lngSelectedMenuId = (long) -1;
	
	Boolean blnIsFirstSection = true;
	
	AdminSubMenu statisticsMenu = new StatisticsMenu();

	/**
	 * �� ������ instance�� ��ȯ��. instance�� ���� ��� ���� �� ��ȯ��
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
					
					//�����߰�											
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
				 * ���õ� ��ư�� �ش��ϴ� �г��� �ε���.
				 */
				
				if(STATS_MENU_STATISTICS.equals(event.getSection().getName())) {
					statisticsMenu.setContentBySelectedBtn();
				}
			}
		});


		
//		//"statictis" �ǿ� ���� �޴� ID �˾ƿ���
//		service.findByUserIdAndParentId(Session.get().getSid(), (long) 0, new AsyncCallback<List<SAdminMenu>>() {
//			@Override
//			public void onSuccess(List<SAdminMenu> result) {
//			    
//				int iTotalCnt = result.size();
//
//				//�����Ͱ� �����ϸ�
//				if(iTotalCnt > 0){
//
//					//Admin �� Stats �޴��� �ش�Ǵ� ��� �˾ƿ���
//					for(int i=0; i< result.size(); i++){
//						//�޴��� Ÿ��Ʋ
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
//					//�� �˾ƿ��⿡ ������ ��� "statictis" �ǿ� �ش�Ǵ� ���� ����Ʈ ��������
//					if(lngSelectedMenuId > -1){
//						serviceSection.findByUserIdAndParentId(Session.get().getSid(), lngSelectedMenuId, new AsyncCallback<List<SAdminMenu>>() {
//							@Override
//							public void onSuccess(List<SAdminMenu> resultSection) {
//								//�����Ͱ� �����ϸ�
//								if(resultSection.size() > 0){
//									
//									//�ش�Ǵ� section ��� �˾ƿ���
//									for(int i=0; i< resultSection.size(); i++){
//										//�޴��� Ÿ��Ʋ
//										String sSectionTitle = resultSection.get(i).getTitle();
//										
//										// "statistics" �����̸� 
//										if("statistics".equals(sSectionTitle)){
//											
//											//statisticsSection.setExpanded(blnIsFirstSection);
//											statisticsSection.setExpanded(false);
//
//											blnIsFirstSection = false;
//											
//											//�����߰�											
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