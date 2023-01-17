package com.speno.xedm.gui.frontend.client.panels;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.Side;
import com.smartgwt.client.types.TabBarControls;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;
import com.smartgwt.client.widgets.tab.events.TabSelectedEvent;
import com.smartgwt.client.widgets.tab.events.TabSelectedHandler;
import com.speno.xedm.core.service.serials.SUser;
import com.speno.xedm.gui.common.client.Constants;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.Session;
import com.speno.xedm.gui.common.client.SessionObserver;
import com.speno.xedm.gui.common.client.log.Log;
import com.speno.xedm.gui.common.client.util.AdminMenuUtil;
import com.speno.xedm.gui.common.client.util.IFHistoryObserver;
import com.speno.xedm.gui.common.client.util.ItemFactory;
import com.speno.xedm.gui.common.client.util.ReturnHandler;
import com.speno.xedm.gui.common.client.util.Util;
import com.speno.xedm.gui.frontend.client.FrontEndEntry;
import com.speno.xedm.gui.frontend.client.admin.AdminPanel;
import com.speno.xedm.gui.frontend.client.document.DashboardPanel;
import com.speno.xedm.gui.frontend.client.document.DocumentsPanel;
import com.speno.xedm.gui.frontend.client.search.FullTextPanel;
import com.speno.xedm.gui.frontend.client.search.SearchMenu;
import com.speno.xedm.gui.frontend.client.search.SearchPanel;
import com.speno.xedm.gui.frontend.client.stats.StatsMenu;
import com.speno.xedm.gui.frontend.client.stats.StatsPanel;

public class MainPanel extends VLayout implements TabSelectedHandler, SessionObserver, IFHistoryObserver {

	public TabSet tabSet = new TabSet();

	private Tab documentsTab;	
	private Tab dashboardTab;
	private Tab searchTab;
	private Tab fullSearchTab;	// kimsoeun GS������ - ���� �˻� �� ����
	private Tab adminTab;
	private Tab statsTab;
	
//	private VLayout vlayTop = new VLayout();
	private TopPanel topPanel;
	
	private Label noRights;
	
	private StatusBar statusBar;
	
	private static MainPanel instance;
	
	//�������� �Ŵ� ������ �� ������ Ư�� �޼ҵ带 �����Ű���� �뵵
	private int menuCount = 0;
	
//	private IncomingMessage incomingMessage = null;

	public static MainPanel get() {
		if (instance == null)
			instance = new MainPanel();
		return instance;
	}

	private MainPanel() {
		setWidth100();
		setHeight100();
		executeFetch();
		Session.get().addSessionObserver(this);
	}

	private void initGUI() {
		topPanel = TopPanel.get();
//		topPanel = new TopPanel();
//		vlayTop.setWidth100();
//		vlayTop.setHeight(40);
//		vlayTop.addMember(topPanel);
		addMember(topPanel);
		
		tabSet.setTabBarPosition(Side.TOP);
		tabSet.setTabBarAlign(Side.LEFT);
//		tabSet.setWidth100();
		tabSet.setHeight("*");
//		tabSet.addClickHandler(new ClickHandler() {
//			@Override
//			public void onClick(ClickEvent event) {
//			}
//		});
		
		documentsTab = new Tab(I18N.message("documents"), ItemFactory.newImg("ico_doc.png").getSrc());
		dashboardTab = new Tab(I18N.message("dashboard"), ItemFactory.newImg("ico_dashboard.png").getSrc());
		searchTab = new Tab(I18N.message("t.search"), ItemFactory.newImg("white_view.png").getSrc());
		fullSearchTab = new Tab(I18N.message("fulltextSearch"), ItemFactory.newImg("white_view.png").getSrc()); // kimsoeun GS������ - ���� �˻� �� ����
		adminTab = new Tab(I18N.message("admin"), ItemFactory.newImg("ico_admin.png").getSrc());
		statsTab = new Tab(I18N.message("statistics"), ItemFactory.newImg("ico_statistics.png").getSrc());
		
		// set id
		documentsTab.setID(Constants.MENU_DOCUMENTS);
		dashboardTab.setID(Constants.MENU_DASHBOARD);
		searchTab.setID(Constants.MENU_SEARCHS);
		fullSearchTab.setID(Constants.MENU_FULLTEXTSEARCHS); // kimsoeun GS������ - ���� �˻� �� ����
		adminTab.setID(Constants.MENU_ADMIN);
		statsTab.setID(Constants.MENU_STATS);

		Label ipLabel = new Label();
		ipLabel.setWidth(400);
		ipLabel.setAlign(Alignment.RIGHT);
		if(Session.get().getLastIp() != null){
			//	        �������� ip�� �������ӽð� ���ϱ�
	        String ipValue =  I18N.message("lastConnectIp") +": "+ Session.get().getLastIp() +" "
	        		+ "/ "+I18N.message("lastConnectDate")+": "+ Session.get().getLastDate();
	        ipValue = "<div style=font-size:8pt;color:white;>"  +ipValue+"</div>";
	        ipLabel.setContents(ipValue);
        }
        
		// =====================================
		// debug��. refresh ��ư
		if (!GWT.isScript()) {
			ButtonItem button = new ButtonItem();
			button.setTitle("Refresh");
			button.setAlign(Alignment.RIGHT);		
			button.setStartRow(false);		
//			button.setIcon("[SKIN]/actions/refresh.png");		
			
			button.addClickHandler(new com.smartgwt.client.widgets.form.fields.events.ClickHandler() {
				@Override
				public void onClick(com.smartgwt.client.widgets.form.fields.events.ClickEvent event) {
					Tab tab = tabSet.getSelectedTab();
					if (tab.getPane() instanceof DashboardPanel) {
						DashboardPanel.get().removeFromParent();
						tab.setPane(DashboardPanel.getNew());
					}
					if (tab.getPane() instanceof DocumentsPanel) {
						DocumentsPanel.get().removeFromParent();
						tab.setPane(DocumentsPanel.getNew());
					}
					if (tab.getPane() instanceof AdminPanel) {
						AdminPanel.get().removeFromParent();
						tab.setPane(AdminPanel.getNew());
					}
	
				}
	        });
			
			ButtonItem button2 = new ButtonItem();
			button2.setTitle("Console");
			button2.setAlign(Alignment.RIGHT);		
			button2.setStartRow(false);		
			
			button2.addClickHandler(new com.smartgwt.client.widgets.form.fields.events.ClickHandler() {
				@Override
				public void onClick(com.smartgwt.client.widgets.form.fields.events.ClickEvent event) {
					SC.showConsole();
				}
	        });

	        DynamicForm form = new DynamicForm();  
	        //form.setHeight(1);  
	        form.setPadding(0);  
	        form.setWidth(20);
	        form.setMargin(0);  
	        form.setCellPadding(1);  
	        form.setNumCols(1); 
	        button2.setAlign(Alignment.RIGHT);
	        
	        
	        form.setFields(button2);  
	        
			tabSet.setTabBarControls(TabBarControls.TAB_SCROLLER, TabBarControls.TAB_PICKER, ipLabel, form);
		}
		else {
			tabSet.setTabBarControls(TabBarControls.TAB_SCROLLER, TabBarControls.TAB_PICKER, ipLabel);
		}
		// =====================================
		
		noRights = new Label(I18N.message("youDonthaveamenurights") + "<br>(" + I18N.message("pleaseasktheadministrator")+")");
		noRights.setStyleName("bigtext");
		noRights.setAlign(Alignment.CENTER);
		
		// TODO: incoming
//		addMember(incomingMessage);
		addMember(tabSet);

		// status bar
		statusBar = new StatusBar(true);
		addMember(statusBar);
	}
	
	
	@Override
	public void onUserLoggedIn(final SUser user) {
		initGUI();

//		int welcomeScreen = 1520;
//		if (user.getWelcomeScreen() != null)
//			welcomeScreen = user.getWelcomeScreen().intValue();

		// 20130821, junsoo, set user info
		// ���ʿ��� ��������, user�� observer���� �����Ͽ�. �ֽ� ���¸� ������ (��:Statusbar messages count)
		user.setUnreadMessages(user.getUnreadMessages());

		// 20130903, junsoo, ���ѿ� ���� �߰��ǹǷ� �ּ�ó����.
//		tabSet.addTab(dashboardTab);
//		tabSet.addTab(documentsTab);
//		tabSet.addTab(searchTab);

//		tabSet.setTabBarThickness(35);

		// admin�� �Ʒ� �Լ��� ���� ����Ǿ�� ��. addTab ȣ��� selectedHandler ȣ���� �Ǿ�� �ϱ� ������.
		//20140128na ie8���� ��� ������ ������ this�� ��׶���ó��
		setBackgroundImage(ItemFactory.newBrandImg(Util.getNameOfServerImage(Session.get().getInfo().getMainTabBack())).getSrc());
		tabSet.setBackgroundImage(ItemFactory.newBrandImg(Util.getNameOfServerImage(Session.get().getInfo().getMainTabBack())).getSrc());
		tabSet.addTabSelectedHandler(this);
		// 20130903, junsoo, ��� �޴��� ���� ����� �Ŀ� ȣ��Ǿ�� ��.
		Session.get().addActionObserver(Long.toString(Constants.MENUID_ROOT), new ReturnHandler() {
			@Override
			public void onReturn(Object param) {
				jump(FrontEndEntry.get().getURL());
			}
		});
		
		// parentid ���� �޴��� ĳ���ϹǷ�, onSuccess �ȿ��� ���� ������ ó���ؾ����� ���� id�� ���� ��û�� �Ͼ�� ����.
		AdminMenuUtil.get().hasPriv(Long.toString(Constants.MENUID_ROOT), 0L, Constants.MENU_DASHBOARD, new AsyncCallback<Long>() {
			@Override
			public void onSuccess(Long result) {
				// 20130906, junsoo, ������ dashboard�� �ʿ����.
				if (!Session.get().isDelegator()) {
					if (result != null) {
						menuIds.put(Constants.MENU_DASHBOARD, result);
						tabSet.addTab(dashboardTab);
					}
					instance.checkMenuRight();
				}

				AdminMenuUtil.get().hasPriv(Long.toString(Constants.MENUID_ROOT), 0L, Constants.MENU_DOCUMENTS, new AsyncCallback<Long>() {
					@Override
					public void onSuccess(Long result) {
						if (result != null) {
//							documentsTab.setPane(DocumentsPanel.get());
							menuIds.put(Constants.MENU_DOCUMENTS, result);
							tabSet.addTab(documentsTab);
						}
						instance.checkMenuRight();
					}
					@Override
					public void onFailure(Throwable caught) {
						caught.printStackTrace();
					}
				});
				
				// 20130906, junsoo, ������ ���̻� ������ �ʿ� ����.
				if (Session.get().isDelegator()) {
					return;
				}
				
				AdminMenuUtil.get().hasPriv(Long.toString(Constants.MENUID_ROOT), 0L, Constants.MENU_SEARCHS, new AsyncCallback<Long>() {
					@Override
					public void onSuccess(Long result) {
						if (result != null) {
//							searchTab.setPane(SearchPanel.get());
							menuIds.put(Constants.MENU_SEARCHS, result);
//							SearchPanel.get().getLeftMenu().buildMenu(result, true);
							tabSet.addTab(searchTab);
						}
						instance.checkMenuRight();
					}
					@Override
					public void onFailure(Throwable caught) {
						caught.printStackTrace();
					}
				});
				
				AdminMenuUtil.get().hasPriv(Long.toString(Constants.MENUID_ROOT), 0L, Constants.MENU_FULLTEXTSEARCHS, new AsyncCallback<Long>() {
					@Override
					public void onSuccess(Long result) {
						if (result != null) {
							menuIds.put(Constants.MENU_FULLTEXTSEARCHS, result);
							tabSet.addTab(fullSearchTab);
						}else{
							topPanel.getSearchText().hide();
						}
					}
					@Override
					public void onFailure(Throwable caught) {
						caught.printStackTrace();
					}
				});
				
				AdminMenuUtil.get().hasPriv(Long.toString(Constants.MENUID_ROOT), 0L, Constants.MENU_ADMIN, new AsyncCallback<Long>() {
					@Override
					public void onSuccess(Long result) {
						if (result != null) {
							menuIds.put(Constants.MENU_ADMIN, result);
//							AdminPanel.get().getAdminMenu().buildMenu(result, true);
							tabSet.addTab(adminTab);
						}
						instance.checkMenuRight();
					}
					@Override
					public void onFailure(Throwable caught) {
						caught.printStackTrace();
					}
				});

				AdminMenuUtil.get().hasPriv(Long.toString(Constants.MENUID_ROOT), 0L, Constants.MENU_STATS, new AsyncCallback<Long>() {
					@Override
					public void onSuccess(Long result) {
						if (result != null) {
//							statsTab.setPane(StatsPanel.get());
							menuIds.put(Constants.MENU_STATS, result);
//							StatsMenu.get().buildMenu(result, true);
							tabSet.addTab(statsTab);
						}
						instance.checkMenuRight();
					}
					@Override
					public void onFailure(Throwable caught) {
						caught.printStackTrace();
					}
				});

			}
			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
			}
		});
	}

	// ==============================
	// select tab
	public void selectDocumentsTab() {
		if (tabSet.getSelectedTab() != documentsTab) {
//			tabSet.selectTab(documentsTab);
			setTabPane(Constants.MENU_DOCUMENTS);
		}
	}

	public void selectDashboardTab() {
		if (tabSet.getSelectedTab() != dashboardTab){
//			tabSet.selectTab(dashboardTab);
			setTabPane(Constants.MENU_DASHBOARD);
		}
	}
	public void selectSearchTab() {
		if (tabSet.getSelectedTab() != searchTab){
//			tabSet.selectTab(searchTab);
			setTabPane(Constants.MENU_SEARCHS);
		}
	}
	
	public void selectFullTextSearchTab() {
		if (tabSet.getSelectedTab() != fullSearchTab){ // kimsoeun GS������ - ���� �˻� �� ����
//			tabSet.selectTab(searchTab);
			setTabPane(Constants.MENU_FULLTEXTSEARCHS);
		}
	}
	
	public void selectAdminTab() {
		if (tabSet.getSelectedTab() != adminTab){
//			tabSet.selectTab(adminTab);
			setTabPane(Constants.MENU_ADMIN);
		}
	}

	public void selectStatsTab() {
		if (tabSet.getSelectedTab() != statsTab){
//			tabSet.selectTab(statsTab);
			setTabPane(Constants.MENU_STATS);
		}
	}
	
	public void selectTab(String ID){
		if (tabSet.getSelectedTab().getID() != ID){
//			tabSet.selectTab(ID);
			setTabPane(ID);
		}
	}
	
	public void selectFirstTab() {
		if (tabSet.getTabs().length > 0)
			setTabPane(tabSet.getTabs()[0].getID());
	}
	// ==============================

	public void selectMenu(String refid) {
		if (refid == null || refid.length() < 1)
			return;
		
//		String section = "";
//		int semi = refid.indexOf(';');
//		if (semi >= 0)
//			section = refid.substring(0, semi).trim();
//		else
//			section = refid.trim();
//		
//		tabSet.selectTab(section);
//		setTabPane(section);

		String[] tags = refid.split(";");
		if (tags != null && tags.length > 0) {
			String tone = tags[0];
			String ttwo = null;
			String tthree = null;
			if (tags.length > 1)
				ttwo = tags[1];
			else {
				setTabPane(tone);
			}
			
			if (tags.length > 2)
				tthree = tags[2];
			
			final String one = tone;
			final String two= ttwo;
			final String three= tthree;
			
			// 20130904, junsoo, ��� �޴��� �������� �ε�� �� menu�� �����ؾ� ��.
			ReturnHandler postProcess = null;
			
			// 20130808, junsoo, admin, stat�� lazy load �ǹǷ�, observer���� ��û�Ͽ� ���� ����ǵ��� �Ѵ�.
			if (Constants.MENU_ADMIN.equals(one) || Constants.MENU_STATS.equals(one)) {
				postProcess = new ReturnHandler() {
					@Override
					public void onReturn(Object param) {
						if (Constants.MENU_ADMIN.equals(one)) {
							AdminPanel.get().getAdminMenu().selectMenu(two, three, true);
						}
						else if (Constants.MENU_STATS.equals(one)) {
							StatsMenu.get().selectMenu(two, three, true);
						}
					}
				};
			}
			
			else if (Constants.MENU_DOCUMENTS.equals(one)) {
				postProcess = new ReturnHandler() {
					@Override
					public void onReturn(Object param) {
						DocumentsPanel.get().getMenu().selectMenu(two, three, true);
					}
				};
			}
			else if (Constants.MENU_SEARCHS.equals(one)) {
				postProcess = new ReturnHandler() {
					@Override
					public void onReturn(Object param) {
						SearchMenu.get().selectMenu(two, three, true);
					}
				};
			}
//			else if (Constants.MENU_FULLTEXTSEARCHS.equals(one)) {
//				postProcess = new ReturnHandler() {
//					@Override
//					public void onReturn(Object param) {
//						FullTextPanel.get();
////						SearchMenu.get().selectMenu(two, three, true);
//					}
//				};
//			}
			else if (Constants.MENU_DASHBOARD.equals(one)) {
				postProcess = new ReturnHandler() {
					@Override
					public void onReturn(Object param) {
						DashboardPanel.get().selectMenu(two, three, true);
					}
				};
			}

			setTabPane(one, postProcess);

		} else {
//			selectFirstTab();
			return;
		}
		
		Session.get().setCurrentMenuId(refid);
	}
	
	@Override
	public void selectByHistory(String refid) {
		selectMenu(refid);
	}
	
	@Override
	public void onHistoryAdded(String refid) {
		Session.get().setCurrentMenuId(refid);
	}
	
	// TODO: �޴��� ����
	// 20130726, junsoo
	public void jump(String url) {
		Log.debug("[MainPanel] jump : " + url);
		if (url == null || url.length() < 1)
			return;
		
		int sharp = url.indexOf('#');
		if (sharp < 0) {
			// 20130904, junsoo, ���� ���ѿ� ���� dashboard�� ���� ���� �����Ƿ�
//			selectMenu(Constants.MENU_DASHBOARD);
//			selectFirstTab();
			
			//20130912 ������ �α׾ƿ��� ����� �޴��� �̵�
//			String welcome = Session.get().getUser().getWelcome();
//			if (welcome != null && welcome.length() > 0) {
//				sharp = url.length();
//				url = url + "#" + welcome;
//				//20131218na return�� �����ؾ� ����� �޴��� �̵���
////				return;
//			}
//			else{
//				url = url + "#dashboard;dash_home";
//				sharp = url.indexOf('#');
//			}
			
			String startPage = Session.get().getInfo().getConfig("settings.welcome.page");
			if (startPage.length() < 1)
				url = url + "#dashboard;dash_home";
			else
				url = url + startPage;
			sharp = url.indexOf('#');
		}

		String tokens = url.substring(sharp+1);
		
		selectMenu(tokens);

//		String[] tags = tokens.split(";");
//		if (tags != null && tags.length > 0) {
//			String one = tags[0];
//			String two = null;
//			String three = null;
//			if (tags.length > 1)
//				two = tags[1];
//			else {
//				// section ���ø� �ϱ�
//				selectMenu(one);
//			}
//			
//			if (tags.length > 2)
//				three = tags[2];
//			
//			// 20130808, junsoo, admin, stat�� lazy load �ǹǷ�, observer���� ��û�Ͽ� ���� ����ǵ��� �Ѵ�.
//			if (Constants.MENU_ADMIN.equals(one) || Constants.MENU_STATS.equals(one)) {
//				
//				// 20130820, junsoo, tab panel �ʱ�ȭ
//				setTabPane(one);
//				
//				if (Constants.MENU_ADMIN.equals(one)) {
//					AdminPanel.get().getAdminMenu().selectMenu(two, three, true);
//					MainPanel.get().selectAdminTab();
//				}
//				else if (Constants.MENU_STATS.equals(one)) {
//					statsTab.setPane(AdminPanel.get());
//					StatsMenu.get().selectMenu(two, three, true);
//				}
//			}
//			
//			else if (Constants.MENU_DOCUMENTS.equals(one)) {
//				// 20130820, junsoo, tab panel �ʱ�ȭ
//				setTabPane(one);
//				DocumentsPanel.get().getMenu().selectMenu(two, three, true);
//			}
//			else if (Constants.MENU_SEARCHS.equals(one)) {
////				SearchPanel.get();		// �ʱ�ȭ�� ���� �ʿ���.
//				SearchMenu.get().selectMenu(two, three, true);
////				AdminPanel.get().getAdminMenu().selectMenu(two, three, true);
//			}
//			else if (Constants.MENU_DASHBOARD.equals(one)) {
//				// 20130820, junsoo, dashboard ���� �޴� ����.
////				selectDashboardTab();
//				// 20130820, junsoo, tab panel �ʱ�ȭ
//				setTabPane(one);
//				DashboardPanel.get().selectMenu(two, three, true);
//			}
//		} else {
//			selectFirstTab();
//			return;
//		}
	}
	
	private Map<String, Long> menuIds = new HashMap<String,Long>();
	
	// 20130808, junsoo, lazy init�� ���� �߰�
	private void setTabPane(String id) {
		setTabPane(id, null);
	}
	private void setTabPane(final String id, final ReturnHandler post) {
		
		// 20130904, junsoo, �޴� �ʱ�ȭ
		Long menuId = menuIds.get(id);
		if(menuId != null) {
			menuIds.remove(id);		// �ٽ� ȣ����� �ʵ��� ������.
		} else {
			menuId = 0L;
		}

		String finalCallbackId = id;
		
		Tab currentTab = tabSet.getTab(id);
		
		// 20130913, junsoo, addTab �� ��� ȣ��Ǿ� menu build�ϴ��� jump���� ȣ��Ǿ� 
		// select menu�� ȣ��Ǿ� �޴��� ���� ǥ�ð� ���� �ʾ�, observer action�� ��� �߰������ν� �̸� �ذ�.  
//		if (currentTab.getPane() == null) {
			Session.get().addActionObserver(finalCallbackId, new ReturnHandler() {
				@Override
				public void onReturn(Object param) {
					tabSet.selectTab(id);
					if (post != null) post.onReturn(null);
					
					// 20130913, junsoo, ���¹� ����
					if (id.equals(Constants.MENU_DASHBOARD))
						StatusBarIcons.get().showMessage();
				}
			});
//		}
		
		if (Constants.MENU_DASHBOARD.equals(id)){
			if (dashboardTab.getPane() == null) {
				dashboardTab.setPane(DashboardPanel.get());
				DashboardPanel.get().buildMenu(finalCallbackId, menuId, true);
			} else {
				tabSet.selectTab(id);
				if (post != null) post.onReturn(null);
			}
		}
		else if (Constants.MENU_DOCUMENTS.equals(id)){
			if (documentsTab.getPane() == null){
				documentsTab.setPane(DocumentsPanel.get());
				DocumentsPanel.get().getMenu().buildMenu(finalCallbackId, menuId, true);
			} else {
				tabSet.selectTab(id);
				if (post != null) post.onReturn(null);
			}
		}
		else if (Constants.MENU_SEARCHS.equals(id)){
			if (searchTab.getPane() == null) {
				searchTab.setPane(SearchPanel.get());
				SearchPanel.get().getLeftMenu().buildMenu(finalCallbackId, menuId, true);
			} else {
				tabSet.selectTab(id);
				if (post != null) post.onReturn(null);
			}
		}
		else if (Constants.MENU_FULLTEXTSEARCHS.equals(id)){ // kimsoeun GS������ - ���� �˻� �� ����
			if (fullSearchTab.getPane() == null) {
				fullSearchTab.setPane(FullTextPanel.get());
//				SearchPanel.get().getLeftMenu().buildMenu(finalCallbackId, menuId, true);
			} else {
				tabSet.selectTab(id);
				if (post != null) post.onReturn(null);
			}
		}
		else if (Constants.MENU_ADMIN.equals(id)){
			if (adminTab.getPane() == null) {
				adminTab.setPane(AdminPanel.get());
				AdminPanel.get().getAdminMenu().buildMenu(finalCallbackId, menuId, true);
			} else {
				tabSet.selectTab(id);
				if (post != null) post.onReturn(null);
			}
		}
		else if (Constants.MENU_STATS.equals(id)){
			if (statsTab.getPane() == null){
				statsTab.setPane(StatsPanel.get());
				StatsMenu.get().buildMenu(finalCallbackId, menuId, true);
			}else {
				tabSet.selectTab(id);
				if (post != null) post.onReturn(null);
			}
		}
			
	}

	@Override
	public void onTabSelected(TabSelectedEvent event) {
		setTabPane(event.getID());
		
	}

	// 20130817 taesu, Goto�� ���ۿ� Getter
	public TabSet getTabSet() {
		return tabSet;
	}
	
	public void executeFetch() {
//		Log.debug("[ mainPanel executeFetch]");
//		
//		ServiceUtil.info().getCheckedNotice(new AsyncCallback<List<SMessage>>() {
//			@Override
//			public void onSuccess(List<SMessage> result) {
////				// TODO Auto-generated method stub
//				for (SMessage sMessage : result) {
//					System.out.println(sMessage.getSubject() +" " + sMessage.getMessage());
//				}
//			}
//			
//			@Override
//			public void onFailure(Throwable caught) {
//				// TODO Auto-generated method stub
//				Log.serverError(caught, true);
//			}
//		});
	}
	
	//�Ŵ������� �ϳ��� ������ ���� ���� ǥ��
	private void checkMenuRight() {
		instance.menuCount ++;
		if(instance.menuCount == 5 && tabSet.getTabs().length == 0){
			instance.removeMembers(tabSet, statusBar);
			instance.setBackgroundColor("transparent");
			instance.setBackgroundImage("");
			instance.addMember(noRights);
		}
	}
}