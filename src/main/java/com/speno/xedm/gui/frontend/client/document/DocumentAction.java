package com.speno.xedm.gui.frontend.client.document;

import java.util.HashMap;
import java.util.Map;

import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.menu.MenuItem;
import com.smartgwt.client.widgets.menu.events.MenuItemClickEvent;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.util.ItemFactory;

// action
public abstract class DocumentAction implements com.smartgwt.client.widgets.menu.events.ClickHandler, com.smartgwt.client.widgets.events.ClickHandler{
	private String id;
	private String title;		// 20130820, junsoo, menu title�� id�� �ƴ� title �� ����ϱ� ���� �߰�
	private String icon;
	private boolean enabled;
	private MenuItem menuItem;
	
	// ToolStripButton �� ������������ ����� �ȵǹǷ� type���� �����ϱ� ����.
	private Map<Integer, ToolStripButton> buttons = new HashMap<Integer, ToolStripButton>();
//	public Action(String id){
//		this.id = id;
//		createMenuItem();
//	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public void setTitle(String title){
		this.title = title;
	}
	
	public void setIcon(String icon) {
		this.icon = icon;
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
//		menuItem.setEnabled(enabled);
//		for (ToolStripButton button : buttons.values()) {
//			button.setDisabled(!enabled);
//		}
	}
	
	public void applyEnabled(){
		menuItem.setEnabled(enabled);
		for (ToolStripButton button : buttons.values()) {
			button.setDisabled(!enabled);
		}
	}
	
	public MenuItem getMenuItem() {
		return menuItem;
	}

	public ToolStripButton getButton(int type) {
		return buttons.get(type);
	}
	// create new menu item
	public MenuItem createMenuItem(){
		menuItem = new MenuItem();
		menuItem.setAttribute("id", title);
		menuItem.setTitle(I18N.message((title != null)? title : id));
		menuItem.setIcon(ItemFactory.newImgIcon(icon + ".png").getSrc());
		menuItem.setEnabled(false);
		menuItem.addClickHandler(this);
		return menuItem;
	}
	
	// 20130819, title�� �ڼ��� �����ϱ� ���� ����. (��: locked by ..)
	public void addTitle(String title){
		menuItem.setTitle(I18N.message((this.title != null)? this.title : id) + title);
	}

	public ToolStripButton createButton(int type){
		ToolStripButton button = new ToolStripButton();
    	button.setTooltip(I18N.message((title != null)? title : id));
    	button.setIcon(ItemFactory.newImgIcon(icon + ".png").getSrc());
    	button.addClickHandler(this);
    	buttons.put(type, button);
		return button;
	}
	
	/**
	 *  ��ư �������� �������� ���·� �ٲ۴�.
	 *  @param id : ��ư �������� Name �� ex) delete, filter ...
	 * 	@param isWorking : ���� ��.�� 
	 * */
	public void changeButtonIcon(String id, boolean isWorking){
		ToolStripButton button = buttons.get(DocumentActionUtil.get().getActivatedMenuType());
		if(button!=null){
			if(isWorking)
				button.setIcon(ItemFactory.newImgIcon(id + "_ing.png").getSrc());
			else
				button.setIcon(ItemFactory.newImgIcon(id + ".png").getSrc());
		}
	}
	
	public void changeButtonWorking(boolean isWorking){
		ToolStripButton button = buttons.get(DocumentActionUtil.get().getActivatedMenuType());
		if(button!=null){
			button.setSelected(isWorking);
		}
	}
	
	// menuitem event
	@Override
	public void onClick(MenuItemClickEvent event) {
		Object[] params = DocumentActionUtil.get().getActionParameters();
		doAction(params);
	}

	// button event
	@Override
	public void onClick(ClickEvent event) {
		Object[] params = DocumentActionUtil.get().getActionParameters();
		doAction(params);
	}

	protected abstract void doAction(Object[] params);
	
}