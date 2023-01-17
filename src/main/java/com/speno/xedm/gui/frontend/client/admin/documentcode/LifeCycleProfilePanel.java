package com.speno.xedm.gui.frontend.client.admin.documentcode;

import java.io.Serializable;

import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.Alignment;
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
import com.speno.xedm.gui.common.client.log.Log;
import com.speno.xedm.gui.frontend.client.admin.TrackPanel;

/**
 * LifeCycleProfile Panel
 * 
 * @author 남윤성
 * @since 1.0
 */
public class LifeCycleProfilePanel extends VLayout implements RefreshObserver, RecordObserver{	
	private static LifeCycleProfilePanel instance;
	
	private HLayout mainHL;	
	private VLayout statesVL;
	private LifeCycleGridPanel lifeCycleGridPanel;
	private AssignedStatesGridPanel assignedStatesGridPanel;
	private StatesGridPanel statesGridPanel;
	private TransferImgButton upArrow, downArrow;
	
	/**
	 * 기 생성된 instance를 반환함. instance가 없을 경우 생성 후 반환함
	 * @return
	 */
	public static LifeCycleProfilePanel get() {
		if (instance == null) {
			instance = new LifeCycleProfilePanel();
		}
		return instance;
	}

	/**
	 * Life Cycle Profile 패널 생성
	 */
	public LifeCycleProfilePanel() {		
		setWidth100();
		setMembersMargin(10);
		setPadding(Constants.PADDING_DEFAULT);
		addMember(new TrackPanel(I18N.message("admin")+" > "+ I18N.message("documentcode")+" > "+ I18N.message("second.lifecycleprofile"), this));
		
		createMainPanel(false); //Main패널 생성
	}
	
	/**
	 * Main패널 생성
	 */
	private void createMainPanel(boolean isRefresh) {		
		if(isRefresh) {
			removeMember(mainHL);
		}
		
		lifeCycleGridPanel = createDutyVL(isRefresh);
		statesGridPanel = createStatesVL(isRefresh);
		assignedStatesGridPanel = createTeamVL(isRefresh, statesGridPanel.getGrid());
		
		mainHL = new HLayout(10);
		mainHL.setHeight100();
		
		statesVL = new VLayout(10);
		statesVL.setHeight100();
		statesVL.setMembers(assignedStatesGridPanel, createArrowVL(), statesGridPanel);
		mainHL.setMembers(lifeCycleGridPanel, statesVL);
		addMember(mainHL);
	}
	
	/**
	 * Refresh
	 */
	private void refresh() {
		createMainPanel(true);
	}
	
	/**
	 * LifeCycle패널 생성
	 */
	private LifeCycleGridPanel createDutyVL(boolean isRefresh) {		
		return isRefresh ? 
				new LifeCycleGridPanel("admin.doccode.lifecycleprofile",  I18N.message("lifecycle"), this, true, "28%") : 
					LifeCycleGridPanel.get("admin.doccode.lifecycleprofile", I18N.message("lifecycle"), this, true, "28%");
	}
	
	/**
	 * AssignedStates패널 생성
	 */
	private AssignedStatesGridPanel createTeamVL(boolean isRefresh, ListGrid dragSourceGrid) {
		return isRefresh ? 
				new AssignedStatesGridPanel("admin.doccode.lifecycleprofile", I18N.message("second.assignedstates"), dragSourceGrid, true, "100%") : 
					AssignedStatesGridPanel.get("admin.doccode.lifecycleprofile", I18N.message("second.assignedstates"), dragSourceGrid, true, "100%");
	}
	
	/**
	 * States패널 생성
	 */
	private StatesGridPanel createStatesVL(boolean isRefresh) {		
		return isRefresh ? 
				new StatesGridPanel("admin.doccode.lifecycleprofile", I18N.message("states"), this , true, false, null, "100%") :
					StatesGridPanel.get("admin.doccode.lifecycleprofile", I18N.message("states"), this, true, false, null, "100%");
	}
	
	/**
	 * Arrow패널 생성
	 */
	private HLayout createArrowVL() {
		upArrow = new TransferImgButton(TransferImgButton.UP, new ClickHandler() {   
            public void onClick(ClickEvent event) {
            	assignedStatesGridPanel.copyRecordsToMembers();
            }   
        });   
        
		downArrow = new TransferImgButton(TransferImgButton.DOWN, new ClickHandler() {   
            public void onClick(ClickEvent event) {   
            	assignedStatesGridPanel.removeRecordsFromMembers();   
            }   
        });
		
		upArrow.enable();
		downArrow.enable();
        
        HLayout arrowPanel = new HLayout();
        arrowPanel.setWidth100();
        arrowPanel.setHeight(30);
        arrowPanel.setAlign(Alignment.CENTER);
        arrowPanel.setMembersMargin(Constants.ARROW_MARGIN);
        arrowPanel.setMembers(upArrow, downArrow);
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
	public void onRecordSelected(Serializable id, Serializable parentId) {
		Log.debug("[ DutyPanel onRecordSelected ] id["+id+"], parentId["+parentId+"]");
		
//		if(Constants.ADMIN_ROOT == (Long)id || Constants.ADMIN_GROUP_ROOT == (Long)id) {
//			upArrow.disable();
//			downArrow.disable();
//		}
//		else {
			upArrow.enable();
			downArrow.enable();
//		}
	}

	@Override
	public void onRecordClick(Record record) {
	}

	@Override
	public void onRecordDoubleClick(Record record) {
	}

	@Override
	public void onRecordSelected(Record record) {
		assignedStatesGridPanel.setGrid(record);
	}

	@Override
	public boolean isExistMember() {
		return assignedStatesGridPanel.isExistMember();
	}

	@Override
	public boolean isIDLong() {
		return true;
	}
}