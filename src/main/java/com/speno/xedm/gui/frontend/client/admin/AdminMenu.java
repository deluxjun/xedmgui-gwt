package com.speno.xedm.gui.frontend.client.admin;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.types.VisibilityMode;
import com.smartgwt.client.widgets.layout.SectionStack;
import com.smartgwt.client.widgets.layout.SectionStackSection;
import com.smartgwt.client.widgets.layout.events.SectionHeaderClickEvent;
import com.smartgwt.client.widgets.layout.events.SectionHeaderClickHandler;
import com.speno.xedm.gui.common.client.AdminSubMenu;
import com.speno.xedm.gui.common.client.Constants;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.Session;
import com.speno.xedm.gui.common.client.util.AdminMenuUtil;
import com.speno.xedm.gui.common.client.util.IFAdminMenuPriv;
import com.speno.xedm.gui.common.client.util.IFHistoryObserver;
import com.speno.xedm.gui.frontend.client.admin.documentbox.DocumentBoxMenu;
import com.speno.xedm.gui.frontend.client.admin.documentcode.DocumentCodeMenu;
import com.speno.xedm.gui.frontend.client.admin.organization.OrganizationMenu;
import com.speno.xedm.gui.frontend.client.admin.system.SystemMenu;

/**
 * Admin Tab의 left menu section statck
 * 
 * @author 스팬오컴 - 박상기
 * @since 1.0
 */
public class AdminMenu extends SectionStack implements IFAdminMenuPriv, IFHistoryObserver {
	Boolean blnIsFirstSection = true;
	
	private SectionStackSection systemSection;
	private SectionStackSection organizationSection;
	private SectionStackSection documentCodeSection;
	private SectionStackSection documentBoxSection;
	
	private static AdminMenu instance;
	
	private static final String ADMIN_MENU_SYSTEM = "system";
	private static final String ADMIN_MENU_SECURITY = "organization";
	private static final String ADMIN_MENU_DOCCODE = "documentcode";
	private static final String ADMIN_MENU_DOCBOX = "documentbox";
	
	AdminSubMenu systemMenu = new SystemMenu();
	AdminSubMenu organizationMenu = new OrganizationMenu();
	AdminSubMenu documentCodeMenu = new DocumentCodeMenu();
	AdminSubMenu documentBoxMenu = new DocumentBoxMenu();

	/**
	 * 기 생성된 instance를 반환함. instance가 없을 경우 생성 후 반환함
	 * @return
	 */
	public static AdminMenu get() {
		if (instance == null)
			instance = new AdminMenu();
		return instance;
	}

	private AdminMenu() {
		setVisibilityMode(VisibilityMode.MUTEX);
		setWidth100();
		
		systemSection = new SectionStackSection(I18N.message("system"));
		systemSection.setName(ADMIN_MENU_SYSTEM);
		systemSection.addItem(systemMenu);
		
		organizationSection = new SectionStackSection(I18N.message("organizationsecurityprofile"));
		organizationSection.setName(ADMIN_MENU_SECURITY);
		organizationSection.addItem(organizationMenu);
		
		documentCodeSection = new SectionStackSection(I18N.message("documentcode"));
		documentCodeSection.setName(ADMIN_MENU_DOCCODE);
		documentCodeSection.addItem(documentCodeMenu);
		
		documentBoxSection = new SectionStackSection(I18N.message("sharedfolder"));
		documentBoxSection.setName(ADMIN_MENU_DOCBOX);
		documentBoxSection.addItem(documentBoxMenu);


		addSectionHeaderClickHandler(new SectionHeaderClickHandler() {
			@Override
			public void onSectionHeaderClick(SectionHeaderClickEvent event) {
				
				/*
				 * 선택된 버튼에 해당하는 패널을 로드함.
				 */
				
				if(ADMIN_MENU_SYSTEM.equals(event.getSection().getName())) {
					systemMenu.setContentBySelectedBtn();
				}
				else if(ADMIN_MENU_SECURITY.equals(event.getSection().getName())) {
					organizationMenu.setContentBySelectedBtn();
				}
				else if(ADMIN_MENU_DOCCODE.equals(event.getSection().getName())) {
					documentCodeMenu.setContentBySelectedBtn();
				}
				else if(ADMIN_MENU_DOCBOX.equals(event.getSection().getName())) {
					documentBoxMenu.setContentBySelectedBtn();
				}
			}
		});
	}
	
	@Override
	public void buildMenu(final String finalCallbackId, final long parentMenuId, final boolean bByHistory){
		blnIsFirstSection = true;
		
		AdminMenuUtil.get().hasPriv(finalCallbackId, parentMenuId, ADMIN_MENU_SYSTEM, new AsyncCallback<Long>() {
			@Override
			public void onSuccess(Long id) {
				if (id != null) {
//					boolean bHistory = !blnIsFirstSection;
//					systemSection.setExpanded(blnIsFirstSection);
//					
//					blnIsFirstSection = false;
					
					//섹션추가											
					addSection(systemSection);
					systemMenu.buildMenu(finalCallbackId, id, true);
					systemMenu.setHistoryObserver(AdminMenu.this);
				}
				
				checkPrivAndSet(finalCallbackId, parentMenuId, ADMIN_MENU_SECURITY, organizationSection, organizationMenu);
				checkPrivAndSet(finalCallbackId, parentMenuId, ADMIN_MENU_DOCCODE, documentCodeSection, documentCodeMenu);
				checkPrivAndSet(finalCallbackId, parentMenuId, ADMIN_MENU_DOCBOX, documentBoxSection, documentBoxMenu);

			}
			@Override
			public void onFailure(Throwable caught) {
			}
		});
	}
	
	private void checkPrivAndSet(final String finalCallbackId, long parentMenuId, String menuName, final SectionStackSection section, final AdminSubMenu menu) {
		AdminMenuUtil.get().hasPriv(finalCallbackId, parentMenuId, menuName, new AsyncCallback<Long>() {
			@Override
			public void onSuccess(Long id) {
				if (id != null) {
//					boolean bHistory = !blnIsFirstSection;
//					section.setExpanded(blnIsFirstSection);
//					blnIsFirstSection = false;
					
					//섹션추가											
					addSection(section);
					menu.buildMenu(finalCallbackId, id, true);
					menu.setHistoryObserver(AdminMenu.this);
				}
			}
			@Override
			public void onFailure(Throwable caught) {
			}
		});
	}

	@Override
	public void selectMenu(String name, String subMenu, boolean bByHistory) {
		SectionStackSection section = null;
		IFAdminMenuPriv menu = null;
		
		if (name == null || name.length() < 1)
			return;
		
		if (ADMIN_MENU_SYSTEM.equals(name)) {
			section = systemSection;
			menu = systemMenu;
		}
		else if (ADMIN_MENU_SECURITY.equals(name)) {
			section = organizationSection;
			menu = organizationMenu;
		}
		else if (ADMIN_MENU_DOCCODE.equals(name)) {
			section = documentCodeSection;
			menu = documentCodeMenu;
		}
		else if (ADMIN_MENU_DOCBOX.equals(name)) {
			section = documentBoxSection;
			menu = documentBoxMenu;
		}
		
		// 20130808,junsoo, section 추가되어 있는지 판단함. 권한이 없을 수 도 있으므로.
		if (getSection(name) == null)
			return;
		
		if (section != null && menu != null) {
			// sub menu
			if (subMenu == null)
				subMenu = "";
			
			if (subMenu.length() > 0) {
				menu.selectMenu(subMenu, null, bByHistory);

//				하위 메뉴에서 history 남기므로..
//				if (!bByHistory)
//					HistoryUtil.get().newHistory(this, "admin;" + name + ";" + subMenu);
			}
			
//			section.setExpanded(true);

			// close all
//			for (SectionStackSection s : getSections()) {
//				if (s != section)
//					s.setExpanded(false);
//			}
			expandSection(name);
		}
	}

	
	/////////////////////////////
	// History

	@Override
	public void selectByHistory(String id) {
		String[] tags = id.split(";");
		if (tags != null && tags.length > 0) {
			if (Constants.MENU_ADMIN.equals(tags[0]) && tags.length > 2) {
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