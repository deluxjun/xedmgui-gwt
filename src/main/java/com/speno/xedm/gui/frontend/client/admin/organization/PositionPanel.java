package com.speno.xedm.gui.frontend.client.admin.organization;

import java.io.Serializable;

import com.google.gwt.core.client.GWT;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.widgets.TransferImgButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.events.CellDoubleClickEvent;
import com.smartgwt.client.widgets.grid.events.CellDoubleClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.speno.xedm.core.service.serials.SGroup;
import com.speno.xedm.gui.common.client.Constants;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.RecordObserver;
import com.speno.xedm.gui.common.client.RefreshObserver;
import com.speno.xedm.gui.frontend.client.admin.TrackPanel;

/**
 * Position Panel
 * 
 * @author 박상기
 * @since 1.0
 */
public class PositionPanel extends VLayout implements RefreshObserver, RecordObserver{	
	private static PositionPanel instance;
	
	private HLayout mainHL;	
	private GroupGridPanel positionGridPanel;
	private TeamGridPanel teamGridPanel;
	private UserGridPanel userGridPanel;
	private TransferImgButton leftArrow, rightArrow;
	
	/**
	 * 기 생성된 instance를 반환함. instance가 없을 경우 생성 후 반환함
	 * @return
	 */
	public static PositionPanel get() {
		if (instance == null) {
			instance = new PositionPanel();
		}
		return instance;
	}

	/**
	 * Position 패널 생성
	 */
	public PositionPanel() {		
		setWidth100();
		setMembersMargin(10);
		setPadding(Constants.PADDING_DEFAULT);
		addMember(new TrackPanel(I18N.message("admin")+" > "+ I18N.message("organization")+" > "+ I18N.message("position"), this));
		
		createMainPanel(false); //Main패널 생성
	}
	
	/**
	 * Main패널 생성
	 */
	private void createMainPanel(boolean isRefresh) {		
		if(isRefresh) {
			removeMember(mainHL);
		}
		
		positionGridPanel = createPositionVL(isRefresh);
		positionGridPanel.setGroupType(SGroup.TYPE_POSITION);
		userGridPanel = createUsersVL(isRefresh);
		teamGridPanel = createTeamVL(isRefresh, userGridPanel.getGrid());
		
		mainHL = new HLayout(10);
		mainHL.setHeight100();
		mainHL.setMembers(positionGridPanel, teamGridPanel, createArrowVL(), userGridPanel);
		addMember(mainHL);
		
		userGridPanel.grid.addCellDoubleClickHandler(new CellDoubleClickHandler() {
			
			@Override
			public void onCellDoubleClick(CellDoubleClickEvent event) {
				// TODO Auto-generated method stub
				teamGridPanel.copyRecordsToMembers();
			}
		});
	}
	
	/**
	 * Refresh
	 */
	private void refresh() {
		createMainPanel(true);
	}
	
	/**
	 * Position 패널 생성
	 */
	private GroupGridPanel createPositionVL(boolean isRefresh) {		
		return isRefresh ? 
				new GroupGridPanel("admin.org.position", SGroup.TYPE_POSITION,  I18N.message("position"), this, true, "40%") : 
					GroupGridPanel.get("admin.org.position", SGroup.TYPE_POSITION,  I18N.message("position"), this, true, "40%");

	}
	
	/**
	 * Team패널 생성
	 */
	private TeamGridPanel createTeamVL(boolean isRefresh, ListGrid dragSourceGrid) {
		return isRefresh ? 
				new TeamGridPanel("admin.org.position", I18N.message("assignedusers"), dragSourceGrid, false, false,"30%") : 
					TeamGridPanel.get("admin.org.position", I18N.message("assignedusers"), dragSourceGrid, false, false,"30%");
	}
	
	/**
	 * Users 패널 생성
	 */
	private UserGridPanel createUsersVL(boolean isRefresh) {		
		return isRefresh ? 
				new UserGridPanel("admin.org.position", this, I18N.message("allusers"), false, true, false, "30%") :
					UserGridPanel.get("admin.org.position", this, I18N.message("allusers"), false, true, false, "30%");
	}
	
	/**
	 * Arrow패널 생성
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
	 * Refresh 버튼 클릭 이벤트 옵져버 핸들러
	 */
	@Override
	public void onRefreshBtnClicked(ClickEvent event) {
		refresh();
	}

	/**
	 * Grid 레코드 클릭 이벤트 옵져버 핸들러
	 */
	@Override
	public void onRecordSelected(final Serializable id, final Serializable parentId) {
		GWT.log("[ GroupGridPanel onRecordSelected ] id["+id+"], parentId["+parentId+"]", null);
		
		if(id.equals(Long.toString(Constants.ADMIN_ROOT)) || id.equals(Constants.ADMIN_GROUP_ROOT) ) {
			leftArrow.disable();
			rightArrow.disable();
		}
		else {
			leftArrow.enable();
			rightArrow.enable();
		}		
		teamGridPanel.executeFetch((String)id, "");
	}

	@Override
	public void onRecordClick(Record record) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRecordDoubleClick(Record record) {
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