package com.speno.xedm.gui.frontend.client.admin.organization;

import java.io.Serializable;

import com.google.gwt.core.client.GWT;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.widgets.TransferImgButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.speno.xedm.gui.common.client.Constants;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.RecordObserver;
import com.speno.xedm.gui.common.client.RefreshObserver;
import com.speno.xedm.gui.frontend.client.admin.TrackPanel;

/**
 * Group Panel
 * 
 * @author �ڻ��
 * @since 1.0
 */
public class GroupPanel extends VLayout implements RefreshObserver, RecordObserver {	
	private static GroupPanel instance;
	
	private HLayout mainHL;
	
	private GroupTreePanel groupTreePanel;
	private TeamGridPanel teamGridPanel;
	private UserGridPanel userGridPanel;
	private TransferImgButton leftArrow, rightArrow;
	
	/**
	 * �� ������ instance�� ��ȯ��. instance�� ���� ��� ���� �� ��ȯ��
	 * @return
	 */
	public static GroupPanel get() {
		if (instance == null) {
			instance = new GroupPanel();
		}
		return instance;
	}
	
	/**
	 * Group �г� ����
	 */
	public GroupPanel() {            	
		setWidth100();
		setMembersMargin(10);		
		setPadding(Constants.PADDING_DEFAULT);
		addMember(new TrackPanel(I18N.message("admin")+" > "+ I18N.message("organization")+" > "+ I18N.message("group"), this));
		
		createMainPanel(false); //Main�г� ����
	}
	
	/**
	 * Main�г� ����
	 */
	private void createMainPanel(boolean isRefresh) {		
		if(isRefresh) {
			removeMember(mainHL);
		}
		
		groupTreePanel = createGrpVL(isRefresh);
		groupTreePanel.setShowResizeBar(true);
		
		userGridPanel = createUsersVL(isRefresh);		
		teamGridPanel = createTeamVL(isRefresh, userGridPanel.getGrid());
		
		HLayout rightHL = new HLayout(10);
		rightHL.setWidth100();
		rightHL.setPadding(Constants.PADDING_DEFAULT);
		rightHL.addMembers(teamGridPanel, createArrowVL(), userGridPanel);
		
		mainHL = new HLayout();
		mainHL.setHeight100();
		//mainHL.setMembers(groupTreePanel, dumyVL, teamGridPanel, createArrowVL(), userGridPanel);
		mainHL.setMembers(groupTreePanel, rightHL);
        addMember(mainHL);
	}
	
	/**
	 * Refresh
	 */
	private void refresh() {
		createMainPanel(true);
	}
	
	/**
	 * Group�г� ����
	 */
	public GroupTreePanel createGrpVL(boolean isRefresh) {		
		return isRefresh ? 
				new GroupTreePanel("admin.org.group", this, false, true, true, "250") : 
					GroupTreePanel.get("admin.org.group", this, false, true, true, "250");
	}
	
	/**
	 * Team�г� ����
	 */
	public TeamGridPanel createTeamVL(boolean isRefresh, ListGrid dragSourceGrid) {
		return isRefresh ? 
				new TeamGridPanel("admin.org.group", I18N.message("teammembers"), dragSourceGrid,  false, false, "100%") : 
					TeamGridPanel.get("admin.org.group", I18N.message("teammembers"), dragSourceGrid,  false, false, "100%");
	}
	
	/**
	 * Users �г� ����
	 */
	private UserGridPanel createUsersVL(boolean isRefresh) {		
		return isRefresh ? 
				new UserGridPanel("admin.org.group", this, I18N.message("allusers"), false, true, false, "100%") :
					UserGridPanel.get("admin.org.group", this, I18N.message("allusers"), false, true, false, "100%");
	}
	
	/**
	 * Arrow�г� ����
	 */
	private VLayout createArrowVL() {
		leftArrow = new TransferImgButton(TransferImgButton.LEFT, new ClickHandler() {   
            public void onClick(ClickEvent event) {
            	teamGridPanel.copyRecordsToMembers();
            }   
        });   
        
		rightArrow = new TransferImgButton(TransferImgButton.RIGHT, new ClickHandler() {   
            public void onClick(ClickEvent event) {   
            	teamGridPanel.removeRecordsFromMembers();   
            }   
        });
		
		leftArrow.disable();
		rightArrow.disable();
        
        VLayout arrowPanel = new VLayout();
        arrowPanel.setWidth(30);
        arrowPanel.setHeight100();
        arrowPanel.setAlign(VerticalAlignment.CENTER);
        arrowPanel.setMembersMargin(Constants.ARROW_MARGIN);
        arrowPanel.setMembers(leftArrow, rightArrow);
        return arrowPanel;
	}	
	
	/**
	 * Refresh ��ư Ŭ�� �̺�Ʈ ������ �ڵ鷯
	 */
	@Override
	public void onRefreshBtnClicked(ClickEvent event) {
		refresh();
	}

	/**
	 *  ���ڵ� Selected �̺�Ʈ ������ �ڵ鷯
	 */
	@Override
	public void onRecordSelected(Serializable id, Serializable parentId) {
		GWT.log("[ GroupPanel onRecordSelected ] id["+id+"], parentId["+parentId+"]", null);
		if(Constants.ADMIN_GROUP_ROOT.equals(id)) {
			leftArrow.disable();
			rightArrow.disable();
		}
		else {
			leftArrow.enable();
			rightArrow.enable();
		}
//		if(Constants.ADMIN_ROOT == id || Constants.ADMIN_GROUP_ROOT == id) {
//			leftArrow.disable();
//			rightArrow.disable();
//		}
//		else {
//			leftArrow.enable();
//			rightArrow.enable();
//		}
		String sid = "";
		if (id instanceof Long) {
			sid = Long.toString((Long)id);
			if ((Long)id == 0L)
				sid = "";
		}
		else
			sid = (String)id;
		teamGridPanel.executeFetch(sid, "");
	}

	@Override
	public void onRecordClick(Record record) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRecordDoubleClick(Record record) {
		if(!leftArrow.isDisabled())
			teamGridPanel.copyRecordsToMembers();
	}

	@Override
	public void onRecordSelected(Record record) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isExistMember() {
		return teamGridPanel.isExistMember();
	}

	@Override
	public boolean isIDLong() {
		return false;
	}
}