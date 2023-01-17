package com.speno.xedm.gui.common.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.layout.VLayout;
import com.speno.xedm.gui.common.client.util.AdminMenuUtil;
import com.speno.xedm.gui.common.client.util.HistoryUtil;
import com.speno.xedm.gui.common.client.util.IFAdminMenuPriv;
import com.speno.xedm.gui.common.client.util.IFHistoryObserver;
import com.speno.xedm.gui.common.client.util.Util;

public abstract class AdminSubMenu extends VLayout implements IFAdminMenuPriv{
	protected List<String> menuList = new ArrayList<String>();				// menu list
	protected Map<String, Object> subMenus = new HashMap<String, Object>();	// submenu name, button
	private IFHistoryObserver historyObserver = null;
	
	protected Boolean blnFirstBtn;
	
	public AdminSubMenu() {
		super();
	}
	
	public void setHistoryObserver(IFHistoryObserver historyObserver) {
		this.historyObserver = historyObserver;
	}
	
	// menu �ʱ�ȭ
    public void initMenus(Object[]... members) {
    	
    	// clear all
		Canvas[] membersToRemove = getMembers();
		for(Canvas member : membersToRemove) {
			removeMember(member);
		}
		menuList.clear();
		subMenus.clear();
		
		// reset
		for (final Object[] member : members) {
			if(member[0].equals("lifecycle") && !Util.getSetting("setting.lifecycle")) continue;
			else if(member[0].equals("states") && !Util.getSetting("setting.lifecycle")) continue;
			else if(member[0].equals("settings") && !Util.getSetting("settings.ecmsettings")) continue;
			// kimsoeun GS������ - ������-�ڵ���� �����			
			else if(member[0].equals("codemanagement") && !Util.getSetting("settings.codeManagement")) continue;
			
			Button btn = (Button)member[1];
			addMember(btn);
			
			menuList.add((String)member[0]);
			subMenus.put((String)member[0], btn);
			
			btn.addClickHandler(new ClickHandler() {
				private String title = (String)member[0];
				
				@Override
				public void onClick(ClickEvent event) {
					onSubMenuClicked(title, false);
				}
			});
		}
		
    }
    
    // ��ư ���õ� �� �׼�
	private void onSubMenuClicked(String title, boolean bByHistory) {

		for (String key : menuList) {
			Button btn = (Button)subMenus.get(key);
			
			// 20130808, junsoo, ������ ������ üũ
			if (!btn.isVisible())
				continue;
			
//			Button btn = (Button)objs[0];
//			VLayout content = (VLayout)objs[1];
			if (title.equals(key)){
				selectButton(btn, title, bByHistory);
//				break;	// ���� ��ư select���¿��� ���� ��ư select�� ���� ��ư�� deselect���� �ʾ� �ּ�ó���� 20130926 taesu
			}
			else {
				btn.deselect();
			}
		}
	}


	// ��ư ����. �����丮 ����
	protected void selectButton(Button btn, String title, boolean bByHistory) {
		btn.select();
//		setContent((VLayout)subMenus.get(title)[1]);
		setContent(title);

		if (historyObserver != null && !bByHistory) {
			HistoryUtil.get().newHistory(historyObserver, getMenuRef() + ";" + title);
		}
	}
	
	// ���õ� ��ư�� ���� ǥ��
	public void setContentBySelectedBtn() {
		for (String key : menuList) {
			Button btn = (Button)subMenus.get(key);
//			Button btn = (Button)objs[0];
//			VLayout content = (VLayout)objs[1];
			if(btn.isSelected()) {
				selectButton(btn, key, false);		// notify history
//				setContent(key);
				break;
			}
		}
	}
	
	// ===============================================
	// build menus
	
	@Override
	public void buildMenu(final String finalCallbackId, final long parentMenuId, final boolean bByHistory) {
		// admin menu
		blnFirstBtn = true;
		final String[] keysArray = menuList.toArray(new String[0]);
		final Button button = (Button)subMenus.get(keysArray[0]);

		AdminMenuUtil.get().hasPriv(finalCallbackId, parentMenuId, keysArray[0], new AsyncCallback<Long>() {
			@Override
			public void onSuccess(Long id) {
				if (id != null) {
					button.show();
					// 20130808, junsoo, �ڵ������ϰ� �ϴ� �ε� �ӵ��� �������Ƿ�, �ּ�ó����
//					if (blnFirstBtn) {
//						// �ʱ�ȭ �̹Ƿ� history ������ ����.
////						selectButton(button, keysArray[0], bByHistory);
//						button.select();
//						setContentBySelectedBtn();
//						blnFirstBtn = false;
//					}
				}
				
				for (int i = 1; i < keysArray.length; i++) {
					checkPrivAndSet(finalCallbackId, parentMenuId, keysArray[i], (Button)subMenus.get(keysArray[i]), bByHistory);
				}

				// 20130723, �ʱ� �޴����鶧 �������� �ʱ�. ���� ������ ����.
//				setContentBySelectedBtn();
				
			}
			@Override
			public void onFailure(Throwable caught) {
			}
		});

	}
	
	private void checkPrivAndSet(final String finalCallbackId, long parentMenuId, final String menuName, final Button btn, final boolean bByHistory){
		AdminMenuUtil.get().hasPriv(finalCallbackId, parentMenuId, menuName, new AsyncCallback<Long>() {
			@Override
			public void onSuccess(Long id) {
				if (id != null) {

					btn.show();
					
					// 20130808, junsoo, �ڵ������ϰ� �ϴ� �ε� �ӵ��� �������Ƿ�, �ּ�ó����
//					if (blnFirstBtn) {
//						// �ʱ�ȭ �̹Ƿ� history ������ ����.
////						selectButton(btn, menuName, bByHistory);
//						btn.select();
//						setContentBySelectedBtn();
//						blnFirstBtn = false;
//					}
				}
			}
			@Override
			public void onFailure(Throwable caught) {
			}
		});
	}

	@Override
	public void selectMenu(String menu, String subMenu, boolean bByHistory) {
		if (menu == null || menu.length() < 1)
			return;
		onSubMenuClicked(menu, bByHistory);
	}
	
	// adminsubmenu �� ��ӹ��� sub menu �� title
	public abstract String getMenuRef();
	public abstract void setContent(String title);

	public void onFinished() {
		
	}
}
