package com.speno.xedm.gui.frontend.client.admin.documentbox;

import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.TransferImgButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.DoubleClickEvent;
import com.smartgwt.client.widgets.events.DoubleClickHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.speno.xedm.gui.common.client.Constants;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.RefreshObserver;
import com.speno.xedm.gui.common.client.Session;

/**
 * File Type Panel (Assigned FileTypeGridPanel + All FileTypeGridPanel 로 구성)
 * 
 * @author 박상기
 * @since 1.0
 */
public class FileTypePanel extends VLayout implements RefreshObserver {	
	private static FileTypePanel instance = null;
	
	private HLayout mainHL;	
	private FileTypeGridPanel assignedFileTypeGridPanel, allFileTypeGridPanel;
	private TransferImgButton leftArrow, rightArrow, inherited;
	
	/**
	 * 기 생성된 instance를 반환함. instance가 없을 경우 생성 후 반환함
	 * @param isRefresh : mainHL을 새로 생성할지 여부
	 * @return
	 */
	public static FileTypePanel get(boolean isRefresh) {
		if (instance == null) {
			instance = new FileTypePanel(isRefresh);
		}
		return instance;
	}
	
	/**
	 * File Type Panel 생성
	 */
	public FileTypePanel(boolean isRefresh) {            	
		setWidth100();
		createMainPanel(isRefresh); //Main패널 생성
	}
	
	/**
	 * Main패널 생성
	 */
	private void createMainPanel(boolean isRefresh) {		
		if(isRefresh && mainHL != null) {
			removeMember(mainHL);
		}
		
		allFileTypeGridPanel = createAllFileTypeVL(isRefresh);		
		assignedFileTypeGridPanel = createAssignedTypeVL(isRefresh, allFileTypeGridPanel.getGrid());
		
		mainHL = new HLayout(10);
		mainHL.setHeight100();
		mainHL.setMembers(assignedFileTypeGridPanel, createArrowVL(), allFileTypeGridPanel);
        addMember(mainHL);
        
        //20140220na 더블클릭 형식 이동
        allFileTypeGridPanel.addDoubleClickHandler(new DoubleClickHandler() {
			@Override
			public void onDoubleClick(DoubleClickEvent event) {
				if(!leftArrow.isDisabled())
					assignedFileTypeGridPanel.copyRecordsToMembers();
			}
		});
//        assignedFileTypeGridPanel.addDoubleClickHandler(new DoubleClickHandler() {
//			@Override
//			public void onDoubleClick(DoubleClickEvent event) {
//				if(!leftArrow.isDisabled()){
//					SC.confirm(I18N.message("warning"), I18N.message("willbeallfiletypesdeleted"),  new BooleanCallback() {
//    					@Override
//    					public void execute(Boolean value) {
//    						if(value != null && value) {
//    							assignedFileTypeGridPanel.removeRecordsFromMembers();
//    						}
//    					}
//    				});
//    				event.cancel();
//				}
//			}
//		});
	}
	
	/**
	 * Refresh
	 */
	private void refresh() {
		createMainPanel(true);
	}
	
	/**
	 * 선택된 Record에 따른 할당된 File Types 조회
	 * @param id
	 * @param parentId
	 */
	protected void executeFetch(long id, long parentId) {
		//Root 선택시 TransferImgButton 비활성화
		if(Constants.ADMIN_ROOT == id || Constants.ADMIN_FOLDER_ROOT == id) {
			leftArrow.disable();
			rightArrow.disable();
			inherited.disable();
		}
		//부모가 Root일 경우 inherited 버튼 비활성화
		else if(Constants.ADMIN_ROOT == parentId || Constants.ADMIN_FOLDER_ROOT == parentId) {
			leftArrow.enable();
			rightArrow.enable();
			inherited.disable();
			assignedFileTypeGridPanel.setEditable(true);
		}
		else {
			if ("TRUE".equalsIgnoreCase(Session.get().getInfo().getConfig("gui.folderproperties.inherited"))) {
				leftArrow.disable();
				rightArrow.disable();
				inherited.disable();
				assignedFileTypeGridPanel.setEditable(false);
			} else {
				leftArrow.enable();
				rightArrow.enable();
				inherited.enable();
				assignedFileTypeGridPanel.setEditable(true);
			}
		}
		assignedFileTypeGridPanel.executeFetch(id);
	}
	
	/**
	 * Refresh 여부에 따른 전체 File Type Panel 로드
	 * @param isRefresh
	 * @return
	 */
	private FileTypeGridPanel createAllFileTypeVL(boolean isRefresh) {
		return isRefresh ? 
				new FileTypeGridPanel("admin.docbox.all", I18N.message("allfiletypes"), null, true, "100%") : 
					FileTypeGridPanel.get("admin.docbox.all", I18N.message("allfiletypes"), null, true,  "100%");
	}
	
	/**
	 * Refresh 여부에 따른 할당된 File Type Panel 로드
	 * @param isRefresh
	 * @param dragSourceGrid
	 * @return
	 */
	private FileTypeGridPanel createAssignedTypeVL(boolean isRefresh, ListGrid dragSourceGrid) {
		return isRefresh ? 
				new FileTypeGridPanel("admin.docbox.assigned", I18N.message("assignedfiletypes"), dragSourceGrid, false, "100%") : 
					FileTypeGridPanel.get("admin.docbox.assigned", I18N.message("assignedfiletypes"), dragSourceGrid, false, "100%");
	}
	
	/**
	 * Arrow패널 생성
	 */
	private VLayout createArrowVL() {
		leftArrow = new TransferImgButton(TransferImgButton.LEFT, new ClickHandler() {
            public void onClick(ClickEvent event) {
            	assignedFileTypeGridPanel.copyRecordsToMembers();
            }
        });
        
		rightArrow = new TransferImgButton(TransferImgButton.RIGHT, new ClickHandler() {
            public void onClick(ClickEvent event) {
            	assignedFileTypeGridPanel.removeRecordsFromMembers();
            }
        });
		
		inherited = new TransferImgButton(TransferImgButton.DOWN_LAST, new ClickHandler() {
            public void onClick(ClickEvent event) {
            	SC.confirm(I18N.message("willbeallfiletypeschanged"),  new BooleanCallback() {
					@Override
					public void execute(Boolean value) {
						if(value != null && value) {
							assignedFileTypeGridPanel.inheritRecordsToMembers();
						}
					}
				});
            }
        });
		
		leftArrow.setTooltip(I18N.message("assigntypes"));
		rightArrow.setTooltip(I18N.message("typesremov"));
		inherited.setTooltip(I18N.message("inherittypes"));
		
		leftArrow.disable();
		rightArrow.disable();
		inherited.disable();
        
        VLayout arrowPanel = new VLayout();
        arrowPanel.setWidth(30);
        arrowPanel.setHeight100();
        arrowPanel.setAlign(VerticalAlignment.CENTER);
        arrowPanel.setMembersMargin(Constants.ARROW_MARGIN);
        arrowPanel.setMembers(leftArrow, rightArrow, inherited);
        return arrowPanel;
	}	
	
	/**
	 * Refresh 버튼 클릭 이벤트 옵져버 핸들러
	 */
	@Override
	public void onRefreshBtnClicked(ClickEvent event) {
		refresh();
	}
}