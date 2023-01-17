package com.speno.xedm.gui.frontend.client.admin.organization;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.SelectionAppearance;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.CellClickEvent;
import com.smartgwt.client.widgets.grid.events.CellClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.TabSet;
import com.smartgwt.client.widgets.tab.events.TabSelectedEvent;
import com.smartgwt.client.widgets.tab.events.TabSelectedHandler;
import com.smartgwt.client.widgets.tree.TreeNode;
import com.speno.xedm.core.service.serials.SAdminMenu;
import com.speno.xedm.gui.common.client.Constants;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.RecordObserver;
import com.speno.xedm.gui.common.client.RefreshObserver;
import com.speno.xedm.gui.common.client.Session;
import com.speno.xedm.gui.common.client.log.Log;
import com.speno.xedm.gui.common.client.util.ServiceUtil;
import com.speno.xedm.gui.common.client.window.Waiting;
import com.speno.xedm.gui.frontend.client.admin.TrackPanel;

/**
 * MenuManagement Panel
 * 
 * @author 
 * @since 1.0
 */
public class MenuManagementPanel extends VLayout implements RefreshObserver, RecordObserver {	
	private static MenuManagementPanel instance;
	
	private HLayout mainHL;
	
	private MenuManagementAMPGridPanel menuManagementAMPGridPanel;
	private MenuManagementGridPanel menuManagementGridPanel;
		
	private ACLMenuManagementGridPanel aclMenuManagementGridPanel;
	private ACLMenuManagementAMPGridPanel aclMenuManagementAMPGridPanel;
	
	private com.smartgwt.client.widgets.tab.Tab tab;
	public static long seleted_recordid;
	
	// kimsoeun GS인증용 - 변경사항 있는지 유효성 검사
	private List oldList = new ArrayList();
		
	/**
	 * 기 생성된 instance를 반환함. instance가 없을 경우 생성 후 반환함
	 * @return
	 */
	//커밋하기 위한 주석2
	public static MenuManagementPanel get() {
		instance = new MenuManagementPanel();		
		return instance;
	}
	
	/**
	 * Group 패널 생성
	 */
	public MenuManagementPanel() {            	
//		setWidth100();
		setMembersMargin(10);		
		setPadding(Constants.PADDING_DEFAULT);
		addMember(new TrackPanel(I18N.message("admin")+" > "+ I18N.message("organization")+" > "+ I18N.message("menumanagement"), this));
		createMainPanel(false); //Main패널 생성			
	}
	
	/**
	 * Main패널 생성
	 */
	private void createMainPanel(final boolean isRefresh) {		
		TabSet tabs = new TabSet();
		
		if(isRefresh) {
			removeChild(mainHL);
			mainHL.destroy();
		}
		com.smartgwt.client.widgets.tab.Tab menu_manage = new com.smartgwt.client.widgets.tab.Tab();
		menu_manage.setTitle(I18N.message("menu.menumange"));
		com.smartgwt.client.widgets.tab.Tab ACL_menu_manage = new com.smartgwt.client.widgets.tab.Tab();
		ACL_menu_manage.setTitle(I18N.message("menu.aclmenumange"));
		
		if(!GWT.isScript())
		tabs.setTabs(menu_manage,ACL_menu_manage);
		else
		tabs.setTabs(ACL_menu_manage);
		
		menuManagementAMPGridPanel = createGrpVL(isRefresh);
		menuManagementAMPGridPanel.setShowResizeBar(false);		
		
		menuManagementGridPanel = createGridVL(isRefresh);
		menuManagementGridPanel.setShowResizeBar(false);	
		
		aclMenuManagementGridPanel = createAclGridVL(isRefresh);
		aclMenuManagementGridPanel.setShowResizeBar(false);		
		
		aclMenuManagementAMPGridPanel = createAclGrpVL(isRefresh);
		aclMenuManagementAMPGridPanel.setShowResizeBar(false);
		aclMenuManagementAMPGridPanel.treeGrid.setSelectionAppearance(SelectionAppearance.CHECKBOX);
		aclMenuManagementAMPGridPanel.treeGrid.setShowSelectedStyle(false);
		aclMenuManagementAMPGridPanel.treeGrid.setShowPartialSelection(true);
		
		aclMenuManagementGridPanel.grid.addCellClickHandler(new CellClickHandler() {
			@Override
			public void onCellClick(CellClickEvent event) {
				// TODO Auto-generated method stub
					Record profile = event.getRecord();		
					seleted_recordid = profile.getAttributeAsLong("ES_ID");
					aclMenuManagementAMPGridPanel.SetMenuData(seleted_recordid);
			}
		});	
		tabs.addTabSelectedHandler(new TabSelectedHandler() {
			@Override
			public void onTabSelected(TabSelectedEvent event) {
				// TODO Auto-generated method stub
				tab = ((TabSet)event.getSource()).getSelectedTab();				
				
				if(tab.getTitle().equals(I18N.message("menu.menumange"))) {					
					HLayout menu = new HLayout();							
					menu.setMembers(menuManagementAMPGridPanel,menuManagementGridPanel);			
					tab.setPane(menu);					
				}
				if(tab.getTitle().equals(I18N.message("menu.aclmenumange")))	{					
					HLayout menu = new HLayout();						
					aclMenuManagementAMPGridPanel.GetMenuData();
					menu.setMembers(aclMenuManagementGridPanel,aclMenuManagementAMPGridPanel);		
					tab.setPane(menu);		
					
				}
			}
		});	
				
		mainHL = new HLayout();
		mainHL.setHeight100();
		mainHL.setPadding(Constants.PADDING_DEFAULT);		
		mainHL.setMembers(tabs);
	    addMember(mainHL);	    
	}

	/**
	 * Refresh
	 */
	private void refresh() {
		createMainPanel(true);
	}
	
	/**
	 * MenuManagement패널 생성
	 */
	
	private ACLMenuManagementGridPanel createAclGridVL(boolean isRefresh) {		
		return new ACLMenuManagementGridPanel(this);
	}
	
	private ACLMenuManagementAMPGridPanel createAclGrpVL(boolean isRefresh) {		
		return isRefresh ? 
				new ACLMenuManagementAMPGridPanel("admin.org.menuTree", this, true) : 
					ACLMenuManagementAMPGridPanel.get("admin.org.menuTree", this, true);
	}

	
	private MenuManagementAMPGridPanel createGrpVL(boolean isRefresh) {		
		return isRefresh ? 
				new MenuManagementAMPGridPanel("admin.org.menuTree", this, true) : 
					MenuManagementAMPGridPanel.get("admin.org.menuTree", this, true);
	}
	
	private MenuManagementGridPanel createGridVL(boolean isRefresh) {		
		return isRefresh ? 
				new MenuManagementGridPanel("admin.org.menuGrid") : 
					MenuManagementGridPanel.get("admin.org.menuGrid");
	}
	
	/**
	 * Refresh 버튼 클릭 이벤트 옵져버 핸들러
	 */
	@Override
	public void onRefreshBtnClicked(ClickEvent event) {
		refresh();
	}
	
	/**
	 *  레코드 Selected 이벤트 옵져버 핸들러
	 */
	@Override
	public void onRecordSelected(Serializable id, Serializable parentId) {
		menuManagementGridPanel.setEnableSelectedHandler(false);
		if (menuManagementAMPGridPanel.treeGrid.getSelectedRecords().length > 0 ){
			TreeNode tree = menuManagementAMPGridPanel.treeGrid.getSelectedRecord();
			
			SAdminMenu adminMenu = new SAdminMenu();
			adminMenu.setId(tree.getAttributeAsLong("ID"));
			adminMenu.setTitle(tree.getAttribute("TITLE"));
			adminMenu.setParentId(Long.parseLong(tree.getAttribute("PARENTID")));
			if(!tree.getAttribute("SECURITYREF").equals(""))
				adminMenu.setSecurityRefs(tree.getAttributeAsIntArray("SECURITYREF"));
			menuManagementGridPanel.SelectGridDataById(adminMenu);
		}
		menuManagementGridPanel.setEnableSelectedHandler(true);
	}

	
	@Override
	public void onRecordClick(Record record) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onRecordDoubleClick(Record record) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRecordSelected(Record record) {
		
	}

	@Override
	public boolean isExistMember() {
		return true;
	}

	@Override
	public boolean isIDLong() {
		return true;
	}

	/**
	 * setAutoGridData
	 * 상위 트리의 데이터를 하위에 전부 검색되게 만든다.
	 * @param menuid: 보안 프로파일 아이디
	 * @param check 보안 프로파일 체크여부
	 */
	public void setAutoGridData(long menuId, boolean check) {
		TreeNode tree = menuManagementAMPGridPanel.treeGrid.getSelectedRecord();
		ListGridRecord[] grid = menuManagementAMPGridPanel.treeGrid.getRecords();
		long id = tree.getAttributeAsLong("ID");
		searchTreeChildGridData(menuId, check, id, grid, 0, "ID");
		
		if(tree.getAttribute("PARENTID").contains("-")) return;
		
		long parent = tree.getAttributeAsLong("PARENTID");
		searchTreeParentGridData(menuId, check, parent, grid, 0, "ID");
	}
	private void searchTreeChildGridData(long menuId, boolean check, long id, ListGridRecord[] grid, int index, String mode){
		for (int i = index; i < grid.length; i++) {
			if(grid[i].getAttribute(mode).contains("-")) continue;
			long treeId = grid[i].getAttributeAsLong(mode);
			if(id == treeId){
//				System.out.println(grid[i].getAttribute("ID") +" " + grid[i].getAttribute("TITLE") + " " +grid[i].getAttribute("SECURITYREF"));
//				System.out.println(grid[i].getAttribute("SECURITYREF"));
				boolean isMenu = false;
				int securityRef[] = grid[i].getAttributeAsIntArray("SECURITYREF");
				if(securityRef.length == 0 && !check) continue;
				
				ArrayList<Long> refs = new ArrayList<Long>();
				for (int j = 0; j < securityRef.length; j++) {
					refs.add((long)securityRef[j]);
					if((long)securityRef[j] == menuId) isMenu = true;
				}
				
				if(check && !isMenu){
					refs.add(menuId);
					grid[i].setAttribute("CHAGED", true);
				}
				else if(check && isMenu) ;
				else if(isMenu){
					refs.remove(menuId);
					grid[i].setAttribute("CHAGED", true);
				}
				
				grid[i].setAttribute("SECURITYREF", refs.toArray());
				searchTreeChildGridData(menuId, check, grid[i].getAttributeAsLong("ID"), grid,  i+1, "PARENTID");
			}
		}
	}
	
	private void searchTreeParentGridData(long menuId, boolean check, long parentId, ListGridRecord[] grid, int index, String mode){
		for (int i = grid.length -1; i >= 0; i--) {
			long treeId = grid[i].getAttributeAsLong(mode);
			if(parentId == treeId){
//				System.out.println(grid[i].getAttribute("ID") +" " + grid[i].getAttribute("TITLE") + " " +grid[i].getAttribute("SECURITYREF"));
//				System.out.println(grid[i].getAttribute("SECURITYREF"));
				boolean isMenu = false;
				int securityRef[] = grid[i].getAttributeAsIntArray("SECURITYREF");
				if(securityRef.length == 0 && !check) continue;
				
				ArrayList<Long> refs = new ArrayList<Long>();
				for (int j = 0; j < securityRef.length; j++) {
					refs.add((long)securityRef[j]);
					if((long)securityRef[j] == menuId) isMenu = true;
				}
				
				if(check && !isMenu){
					refs.add(menuId);
					grid[i].setAttribute("CHAGED", true);
				}
				else if(check && isMenu) ;
				else if(isMenu){
//					refs.remove(menuId);
//					grid[i].setAttribute("CHAGED", true);
				}
				
				grid[i].setAttribute("SECURITYREF", refs.toArray());
				if(grid[i].getAttribute("PARENTID").contains("-")) continue;
				parentId =  grid[i].getAttributeAsLong("PARENTID");
			}
		}
	}
	
	public void executeSave(){
		SC.confirm(I18N.message("wanttosave"), new BooleanCallback() {
			@Override
			public void execute(Boolean value) {
				if(value != null && value) {
					Waiting.show("");
					ListGridRecord[] grid = menuManagementAMPGridPanel.treeGrid.getRecords();
					final ArrayList<SAdminMenu> adminMenuList = new ArrayList<SAdminMenu>();
					
					for (int i = 0; i < grid.length; i++) {
						if(grid[i].getAttributeAsBoolean("CHAGED") == false){
							continue;
						}
//						System.out.println(grid[i].getAttribute("ID") +" " + grid[i].getAttribute("TITLE") + " " +grid[i].getAttribute("SECURITYREF"));
						SAdminMenu adminMenu = new SAdminMenu();
						adminMenu.setId(grid[i].getAttributeAsLong("ID"));
						adminMenu.setSecurityRefs(grid[i].getAttributeAsIntArray("SECURITYREF"));
						adminMenuList.add(adminMenu);
						grid[i].setAttribute("CHAGED", "");
					}
					ServiceUtil.security().updateSecurityRef(Session.get().getSid(), adminMenuList, new AsyncCallback<Void>() {
						@Override
						public void onFailure(Throwable caught) {
							Waiting.hide();
							Log.serverError( I18N.message("genericerror"), caught, true);
						}
						@Override
						public void onSuccess(Void result) {
							Waiting.hide();
//							SC.say(I18N.message("operationcompleted"));
							// kimsoeun GS인증용  - 작업 완료 -> 저장 완료 문구 변경
							SC.say(I18N.message("savecompleted"));
						}
					});
				}
			}
		});
	   	
	}
	
	public void executeSaveACL(){

		ListGridRecord[] LR = aclMenuManagementAMPGridPanel.treeGrid.getSelectedRecords();		
		
		// kimsoeun GS인증용 - 변경여부 확인
		oldList = aclMenuManagementAMPGridPanel.getOldCheckedList();
		if(LR.length == oldList.size()) {
			int count = 0;
			
			for (int i = 0; i < LR.length; i++) {
				Record re = LR[i];
				
				for (int j = 0; j < oldList.size(); j++) {
					Record re2 = (Record) oldList.get(j);
					
					if(re.getAttribute("ID").equals(re2.getAttribute("ID"))) {
						count++;
					}
				}
			}
			
			if(count == LR.length) {
				SC.say(I18N.message("nothingchanged"));
				return;
			}
		}
		
		long[] menuIds = new long[LR.length];
		for(int i =0; i < LR.length ; i++)
		{
		
		menuIds[i] = LR[i].getAttributeAsLong("ID");		
		}
		String SessionID = Session.get().getSid();
		
		ServiceUtil.security().updateMenuIdsBySecurityProfileId(SessionID, seleted_recordid, menuIds, new AsyncCallback<Void>() {
			
			@Override
			public void onSuccess(Void result) {
				// TODO Auto-generated method stub
				Waiting.hide();
				SC.say(I18N.message("savecompleted"));
				
				// kimsoeun GS인증용 - 변경여부 확인
				aclMenuManagementAMPGridPanel.SetMenuData(seleted_recordid);
				
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				Waiting.hide();
				Log.serverError( I18N.message("genericerror"), caught, true);
			}
		});
	}
	
}