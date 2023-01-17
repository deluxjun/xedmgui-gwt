package com.speno.xedm.gui.common.client.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.menu.MenuItem;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;
import com.speno.xedm.gui.common.client.log.Log;
import com.speno.xedm.gui.frontend.client.document.DocumentAction;

/**
 * 
 * DocumentActionUtil의 simple version.
 * DocumentActionUtil에서 모든 것을 다 처리하지 않고, 가급적 grid별로 이 객체를 따로 가져가도록 함.
 * 
 * Created : 20140325
 * 
 * @author deluxjun
 *
 */
public class Action {

	private Map<String, DocumentAction> actions = new HashMap<String, DocumentAction>();
	
	List<MenuItem> menuItems;
	ToolStrip toolBar;

	public Action() {
		// TODO Auto-generated constructor stub
	}
	
	// create action
	public void createAction(String id, String i18n, String icon, DocumentAction action, boolean hasContext, boolean hasToolbar){
		if (actions.get(id) != null)
			Log.debug("Action duplicated !! : " + id);
		
		action.setId(id);
		action.setIcon(icon);
		action.setTitle(i18n);
		action.createMenuItem();
		actions.put(id, action);
		
		// 20130816, junsoo, contextMenu 생성 방지
		if (hasContext) {
			if (menuItems == null) {
				menuItems = new ArrayList<MenuItem>();
			}
			menuItems.add(action.getMenuItem());
		}
		

		if (hasToolbar) {
			ToolStripButton button = action.createButton(0);
			if (toolBar == null) {
				toolBar = new ToolStrip();
			}
			toolBar.addButton(button);
		}
	}
	
	
	// get toolbar
	public ToolStrip getToolBar() {
		return toolBar;
	}
	
	// get context menu
	public Menu getContextMenu() {
		
		Menu menu = new Menu();
	
		if (menuItems != null) {
			for (MenuItem menuItem : menuItems) {
				menu.addItem(menuItem);
			}
		}
		return menu;
	}
	
	public void enable(String id, boolean flag, String comment){
		DocumentAction action = actions.get(id);
		if (action != null) {
			action.addTitle(comment);
			action.setEnabled(flag);
			action.applyEnabled();
		}
	}
}
