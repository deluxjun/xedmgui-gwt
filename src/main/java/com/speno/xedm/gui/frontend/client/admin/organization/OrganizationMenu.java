package com.speno.xedm.gui.frontend.client.admin.organization;

import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.layout.VLayout;
import com.speno.xedm.gui.common.client.AdminSubMenu;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.util.IFAdminMenuPriv;
import com.speno.xedm.gui.frontend.client.admin.AdminPanel;

/**
 * Organization Menu
 * 
 * @author 박상기
 * @since 1.0
 */
public class OrganizationMenu extends AdminSubMenu implements IFAdminMenuPriv{	
	private Button group;
	private Button duty;
	private Button position;
	private Button user;
	private Button securityprofile;
	private Button menumanagement;
	
//	@Override
//	public void buildMenu(final long parentMenuId) {
//		
//		blnFirstBtn = true;
//		AdminMenuUtil.get().hasPriv(parentMenuId, "group", new AsyncCallback<Long>() {
//			@Override
//			public void onSuccess(Long id) {
//				if (id != null) {
//
//				group.show();
//				if (blnFirstBtn) {
//					group.select();
//					blnFirstBtn = false;
//				}
//				}				
//				checkPrivAndSet(parentMenuId, "duty", duty);
//				checkPrivAndSet(parentMenuId, "position", position);
//				checkPrivAndSet(parentMenuId, "user", user);
//				checkPrivAndSet(parentMenuId, "securityprofile", securityprofile);
//				checkPrivAndSet(parentMenuId, "menumanagement", menumanagement);
//
//				setContentBySelectedBtn();
//			}
//			@Override
//			public void onFailure(Throwable caught) {
//			}
//		});	}
//	
//	private void checkPrivAndSet(long parentMenuId, String menuName, final Button btn){
//		AdminMenuUtil.get().hasPriv(parentMenuId, menuName, new AsyncCallback<Long>() {
//			@Override
//			public void onSuccess(Long id) {
//				if (id != null) {
//				btn.show();
//				if (blnFirstBtn) {
//					btn.select();
//					blnFirstBtn = false;
//				}
//				}
//			}
//			@Override
//			public void onFailure(Throwable caught) {
//			}
//		});
//	}
	
	public OrganizationMenu() {
		setMargin(10);
		setMembersMargin(5);
		
		
	
		group = new Button(I18N.message("group"));
		group.setWidth100();
		group.setHeight(25);
		group.hide();
		
		duty = new Button(I18N.message("duty"));
		duty.setWidth100();
		duty.setHeight(25);
		duty.hide();
		
		position = new Button(I18N.message("position"));
		position.setWidth100();
		position.setHeight(25);
		position.hide();
		
		user = new Button(I18N.message("user"));
		user.setWidth100();
		user.setHeight(25);
		user.hide();
		
		securityprofile = new Button(I18N.message("securityprofile"));
		securityprofile.setWidth100();
		securityprofile.setHeight(25);
		securityprofile.hide();

		menumanagement = new Button(I18N.message("menumanagement"));
		menumanagement.setWidth100();
		menumanagement.setHeight(25);
		menumanagement.hide();
		
		initMenus(new Object[]{"group", group},
				new Object[]{"duty", duty},
				new Object[]{"position", position},
				new Object[]{"user", user},
				new Object[]{"securityprofile", securityprofile},
				new Object[]{"menumanagement", menumanagement});
	}


	@Override
	public String getMenuRef() {
		return "admin;organization";
	}
	@Override
	public void setContent(String title) {
		VLayout content = null;
		if ("group".equals(title)) {
			content = GroupPanel.get();
		} else if ("duty".equals(title)) {
			content = DutyPanel.get();
		} else if ("position".equals(title)) {
			content = PositionPanel.get();
		} else if ("user".equals(title)) {
			content = UserPanel.get();
		} else if ("securityprofile".equals(title)) {
			content = SecurityProfilePanel.get();
		} else if ("menumanagement".equals(title)) {
			content = MenuManagementPanel.get();
		
		}
			
		if (content != null)
			AdminPanel.get().setContent(content);
	}

//	private void onGroupContent() {
//		AdminPanel.get().setContent(GroupPanel.get());
//		duty.deselect(); position.deselect(); user.deselect(); securityprofile.deselect(); menumanagement.deselect();
//		group.select();
//	}
//	
//	private void onDutyContent() {
//		AdminPanel.get().setContent(DutyPanel.get());
//		group.deselect(); position.deselect(); user.deselect(); securityprofile.deselect(); menumanagement.deselect();
//		duty.select();
//	}
//	
//	private void onPositionContent() {
//		AdminPanel.get().setContent(PositionPanel.get());
//		group.deselect(); duty.deselect(); user.deselect(); securityprofile.deselect(); menumanagement.deselect();
//		position.select();
//	}
//	
//	private void onUserContent() {
//		AdminPanel.get().setContent(UserPanel.get());
//		group.deselect(); duty.deselect(); position.deselect(); securityprofile.deselect(); menumanagement.deselect();
//		user.select();
//	}
//	
//	private void onSecurityProfilePanelContent() {
//		AdminPanel.get().setContent(SecurityProfilePanel.get());
//		group.deselect(); duty.deselect(); position.deselect(); user.deselect(); menumanagement.deselect();
//		securityprofile.select();
//	}
//	
//	private void onMenuManagementPanelContent() {
//		AdminPanel.get().setContent(MenuManagementPanel.get());
//		group.deselect(); duty.deselect(); position.deselect(); user.deselect(); securityprofile.deselect();
//		menumanagement.select();
//	}
//
//	
//	/**
//	 * 선택된 버튼에 해당하는 패널을 로드함.
//	 */
//	public void setContentBySelectedBtn() {
//		if(group.isSelected()) {
//			AdminPanel.get().setContent(GroupPanel.get());
//		}
//		else if(duty.isSelected()) {
//			AdminPanel.get().setContent(DutyPanel.get());
//		}
//		else if(position.isSelected()) {
//			AdminPanel.get().setContent(PositionPanel.get());
//		}
//		else if(user.isSelected()) {
//			AdminPanel.get().setContent(UserPanel.get());
//		}
//		else if(securityprofile.isSelected()) {
//			AdminPanel.get().setContent(SecurityProfilePanel.get());
//		}
//		else if(menumanagement.isSelected()) {
//			AdminPanel.get().setContent(MenuManagementPanel.get());
//		}
//		else {
//			onGroupContent();
//		}
//	}
}