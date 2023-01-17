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
	private String title;		// 20130820, junsoo, menu title을 id가 아닌 title 을 사용하기 위해 추가
	private String icon;
	private boolean enabled;
	private MenuItem menuItem;
	
	// ToolStripButton 은 여러군데에서 사용이 안되므로 type별로 생성하기 위함.
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
	
	// 20130819, title을 자세히 세팅하기 위해 사용됨. (예: locked by ..)
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
	 *  버튼 아이콘을 실행중인 상태로 바꾼다.
	 *  @param id : 버튼 아이콘의 Name 값 ex) delete, filter ...
	 * 	@param isWorking : 동작 유.무 
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