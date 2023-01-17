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
 * Document Type Panel (Assigned DocTypeGridPanel + All DocTypeGridPanel �� ����)
 * 
 * @author �ڻ��
 * @since 1.0
 */
public class DocTypePanel extends VLayout implements RefreshObserver {	
	private static DocTypePanel instance = null;
	
	private HLayout mainHL;	
	private DocTypeGridPanel assignedDocTypeGridPanel, allDocTypeGridPanel;
	private TransferImgButton leftArrow, rightArrow, inherited;
	
	/**
	 * �� ������ instance�� ��ȯ��. instance�� ���� ��� ���� �� ��ȯ��
	 * @param isRefresh : mainHL�� ���� �������� ����
	 * @return
	 */
	public static DocTypePanel get(boolean isRefresh) {
		if (instance == null) {
			instance = new DocTypePanel(isRefresh);
		}
		return instance;
	}
	
	/**
	 * Doc Type Panel ����
	 */
	public DocTypePanel(boolean isRefresh) {            	
		setWidth100();
		createMainPanel(isRefresh); //Main�г� ����
	}
	
	/**
	 * Main�г� ����
	 */
	private void createMainPanel(boolean isRefresh) {		
		if(isRefresh && mainHL != null) {
			removeMember(mainHL);
		}
		
		allDocTypeGridPanel = createAllDocTypeVL(isRefresh);		
		assignedDocTypeGridPanel = createAssignedTypeVL(isRefresh, allDocTypeGridPanel.getGrid());
		
		mainHL = new HLayout(10);
		mainHL.setHeight100();
		mainHL.setMembers(assignedDocTypeGridPanel, createArrowVL(), allDocTypeGridPanel);
        addMember(mainHL);
        
        //20140220na ����Ŭ�� ���������̵�
        allDocTypeGridPanel.addDoubleClickHandler(new DoubleClickHandler() {
			@Override
			public void onDoubleClick(DoubleClickEvent event) {
				if(!leftArrow.isDisabled())
					assignedDocTypeGridPanel.copyRecordsToMembers();
			}
		});
//        assignedDocTypeGridPanel.addDoubleClickHandler(new DoubleClickHandler() {
//			@Override
//			public void onDoubleClick(DoubleClickEvent event) {
//				if(!leftArrow.isDisabled()){
//					SC.confirm(I18N.message("warning"), I18N.message("willbealldoctypesdeleted"),  new BooleanCallback() {
//    					@Override
//    					public void execute(Boolean value) {
//    						if(value != null && value) {
//    							assignedDocTypeGridPanel.removeRecordsFromMembers();
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
	 * ���õ� Record�� ���� �Ҵ�� Doc Types ��ȸ
	 * @param id
	 * @param parentId
	 */
	protected void executeFetch(long id, long parentId) {
		//Root ���ý� TransferImgButton ��Ȱ��ȭ
		if(Constants.ADMIN_ROOT == id || Constants.ADMIN_FOLDER_ROOT == id) {
			leftArrow.disable();
			rightArrow.disable();
			inherited.disable();
		}		
		//�θ� Root�� ��� inherited ��ư ��Ȱ��ȭ
		else if(Constants.ADMIN_ROOT == parentId || Constants.ADMIN_FOLDER_ROOT == parentId) {
			leftArrow.enable();
			rightArrow.enable();
			inherited.disable();
			assignedDocTypeGridPanel.setEditable(true);
		}		
		else {
			if ("TRUE".equalsIgnoreCase(Session.get().getInfo().getConfig("gui.folderproperties.inherited"))) {
				leftArrow.disable();
				rightArrow.disable();
				inherited.disable();
				assignedDocTypeGridPanel.setEditable(false);
			} else {
				leftArrow.enable();
				rightArrow.enable();
				inherited.enable();
				assignedDocTypeGridPanel.setEditable(true);
			}
		}
		assignedDocTypeGridPanel.executeFetch(id);
	}
	
	/**
	 * Refresh ���ο� ���� ��ü Doc Type Panel �ε�
	 * @param isRefresh
	 * @return
	 */
	private DocTypeGridPanel createAllDocTypeVL(boolean isRefresh) {
		return isRefresh ? 
				new DocTypeGridPanel("admin.docbox.all", I18N.message("alldoctypes"), null, true, "100%") : 
					DocTypeGridPanel.get("admin.docbox.all", I18N.message("alldoctypes"), null, true,  "100%");
	}
	
	/**
	 * Refresh ���ο� ���� �Ҵ�� Doc Type Panel �ε�
	 * @param isRefresh
	 * @param dragSourceGrid
	 * @return
	 */
	private DocTypeGridPanel createAssignedTypeVL(boolean isRefresh, ListGrid dragSourceGrid) {
		return isRefresh ? 
				new DocTypeGridPanel("admin.docbox.assigned", I18N.message("assigneddoctypes"), dragSourceGrid, false, "100%") : 
					DocTypeGridPanel.get("admin.docbox.assigned", I18N.message("assigneddoctypes"), dragSourceGrid, false, "100%");
	}
	
	/**
	 * Arrow�г� ����
	 */
	private VLayout createArrowVL() {
		leftArrow = new TransferImgButton(TransferImgButton.LEFT, new ClickHandler() {
            public void onClick(ClickEvent event) {
            	assignedDocTypeGridPanel.copyRecordsToMembers();
            }
        });
        
		rightArrow = new TransferImgButton(TransferImgButton.RIGHT, new ClickHandler() {
            public void onClick(ClickEvent event) {
            	assignedDocTypeGridPanel.removeRecordsFromMembers();
            }
        });
		
		inherited = new TransferImgButton(TransferImgButton.DOWN_LAST, new ClickHandler() {
            public void onClick(ClickEvent event) {
            	SC.confirm(I18N.message("willbealldoctypeschanged"),  new BooleanCallback() {
					@Override
					public void execute(Boolean value) {
						if(value != null && value) {
							assignedDocTypeGridPanel.inheritRecordsToMembers();
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
	 * Refresh ��ư Ŭ�� �̺�Ʈ ������ �ڵ鷯
	 */
	@Override
	public void onRefreshBtnClicked(ClickEvent event) {
		refresh();
	}
}