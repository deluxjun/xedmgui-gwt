package com.speno.xedm.gui.frontend.client.admin.documentcode;

import java.io.Serializable;

import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.TransferImgButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.speno.xedm.gui.common.client.Constants;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.RecordObserver;
import com.speno.xedm.gui.common.client.RefreshObserver;
import com.speno.xedm.gui.frontend.client.admin.TrackPanel;
import com.speno.xedm.gui.frontend.client.admin.documentbox.FolderTreePanel;

/**
 * States Panel
 * 
 * @author 남윤성
 * @since 1.0
 */
public class StatesPanel extends VLayout implements RefreshObserver, RecordObserver {	
	private static StatesPanel instance;	
	
	private HLayout mainHL;
	private StatesGridPanel statesGridPanel;
	private AssignedTargetFolderGridPanel assignedTargetFolderGridPanel;
	private FolderTreePanel folderTreePanel;
	private TransferImgButton leftArrow, rightArrow;
	
	/**
	 * 기 생성된 instance를 반환함. instance가 없을 경우 생성 후 반환함
	 * @return
	 */
	public static StatesPanel get() {
		if (instance == null) {
			instance = new StatesPanel();
		}
		return instance;
	}
	
	public StatesPanel() {            	
		setWidth100();
		setMembersMargin(10);
		setPadding(Constants.PADDING_DEFAULT);
		addMember(new TrackPanel(I18N.message("admin")+" > "+ I18N.message("documentcode")+" > "+ I18N.message("states"), this));
		
		createMainPanel(false); //Main패널 생성
	}
	
	/**
	 * Main패널 생성
	 */
	private void createMainPanel(boolean isRefresh) {
		if(isRefresh) {
			removeMember(mainHL);
		}
		
		folderTreePanel = createFolderVL(isRefresh);	
		assignedTargetFolderGridPanel = createAssignedTargetFolderVL(isRefresh, folderTreePanel.getGroupTree());
		statesGridPanel = createStatesVL(isRefresh, assignedTargetFolderGridPanel.getGrid());
		
		HLayout rightHL = new HLayout(10);
		
		VLayout folderVL = new VLayout(10);
		folderVL.setWidth100();
		Label subTitleLable = new Label();
		subTitleLable.setAutoHeight();   
        subTitleLable.setAlign(Alignment.LEFT);   
        subTitleLable.setValign(VerticalAlignment.CENTER);
        subTitleLable.setStyleName("subTitle");
        subTitleLable.setContents(I18N.message("folder"));
	    folderVL.addMembers(subTitleLable,folderTreePanel);
	    
		rightHL.setWidth100();
		rightHL.setPadding(Constants.PADDING_DEFAULT);
		rightHL.addMembers(assignedTargetFolderGridPanel, createArrowVL(), folderVL);
		
		mainHL = new HLayout(10);
		mainHL.setHeight100();
		mainHL.setMembers(statesGridPanel, rightHL);
		addMember(mainHL);
	}
	
	/**
	 * Refresh
	 */
	private void refresh() {
		createMainPanel(true);
	}
	
	/**
	 * States 패널 생성
	 */
	private StatesGridPanel createStatesVL(boolean isRefresh, ListGrid listGrid) {		
		return isRefresh ? 
				new StatesGridPanel("admin.doccode.states", I18N.message("states"), this, false, true, listGrid, "40%") :
					StatesGridPanel.get("admin.doccode.states", I18N.message("states"), this, false, true,listGrid,  "40%");
	}
	
	/**
	 * AssignedTargetFolderGridPanel 패널 생성
	 */
	private AssignedTargetFolderGridPanel createAssignedTargetFolderVL(boolean isRefresh, ListGrid dragSourceGrid) {
		return isRefresh ? 
				new AssignedTargetFolderGridPanel("admin.doccode.states", I18N.message("second.assignedtargetfolder"), dragSourceGrid,  true, "100%") : 
					AssignedTargetFolderGridPanel.get("admin.doccode.states", I18N.message("second.assignedtargetfolder"), dragSourceGrid,  true, "100%");
	}
	
	/**
	 * Folders 패널 생성
	 * @param isRefresh
	 * @return
	 */
	private FolderTreePanel createFolderVL(boolean isRefresh) {		
		return isRefresh ? 
				new FolderTreePanel("admin.doccode.states", null, true, false, false, "100%") : 
					FolderTreePanel.get("admin.doccode.states", null, true, false, false, "100%");
	}
	
	/**
	 * Arrow패널 생성
	 */
	private VLayout createArrowVL() {
		leftArrow = new TransferImgButton(TransferImgButton.LEFT, new ClickHandler() {   
            public void onClick(ClickEvent event) {
            	assignedTargetFolderGridPanel.copyRecordsToMembers();
            }   
        });   
        
		rightArrow = new TransferImgButton(TransferImgButton.RIGHT, new ClickHandler() {   
            public void onClick(ClickEvent event) {   
            	assignedTargetFolderGridPanel.removeRecordsFromMembers();   
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
	 * Refresh 버튼 클릭 이벤트 옵져버
	 */
	@Override
	public void onRefreshBtnClicked(ClickEvent event) {
		refresh();
	}
	
	@Override
	public void onRecordSelected(Serializable id, Serializable parentId) {
//		if(Constants.ADMIN_ROOT == (Long)id || Constants.ADMIN_GROUP_ROOT == (Long)id) {
//			leftArrow.disable();
//			rightArrow.disable();
//		}
//		else {
			leftArrow.enable();
			rightArrow.enable();
//		}
	}

	@Override
	public void onRecordSelected(Record record) {
		ListGrid grid = statesGridPanel.getGrid();
		DynamicForm form = statesGridPanel.getForm();
		assignedTargetFolderGridPanel.setGrid(record, grid, form);
		leftArrow.enable();
		rightArrow.enable();
	}

	@Override
	public void onRecordClick(Record record) {		
	}

	@Override
	public void onRecordDoubleClick(Record record) {
	}

	@Override
	public boolean isExistMember() {
		return false;
	}

	@Override
	public boolean isIDLong() {
		return true;
	}
}