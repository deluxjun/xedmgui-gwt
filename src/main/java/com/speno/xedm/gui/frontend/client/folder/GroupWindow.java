package com.speno.xedm.gui.frontend.client.folder;

import java.io.Serializable;

import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.RecordList;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.widgets.TransferImgButton;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.CloseClickEvent;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.events.ClickEvent;
import com.smartgwt.client.widgets.form.fields.events.ClickHandler;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.CellDoubleClickEvent;
import com.smartgwt.client.widgets.grid.events.CellDoubleClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.speno.xedm.core.service.serials.SUser;
import com.speno.xedm.gui.common.client.Constants;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.RecordObserver;
import com.speno.xedm.gui.common.client.util.ReturnHandler;
import com.speno.xedm.gui.frontend.client.admin.organization.GroupSearchGridPanel;
import com.speno.xedm.gui.frontend.client.admin.organization.GroupTreePanel;
import com.speno.xedm.gui.frontend.client.admin.organization.SelectTeamGridPanel;

public class GroupWindow extends Window implements RecordObserver{
//	protected static OwnerWindow instance;
	private TransferImgButton leftArrow, rightArrow;
	
	private ReturnHandler returnHandler;		// 20130806, junsoo, ����� ������ handler
	private String type;
	
	private boolean getGroupInfo;
	
	SUser user = null;
	
	public void setReturnHandler(ReturnHandler returnHandler) {
		this.returnHandler = returnHandler;
	}
	// 20130829, junsoo, �̱��� ����.
//	public static OwnerWindow get(final String type, ReturnHandler returnHandler, boolean getGroupInfo) {
//		if (instance == null) {
//			instance = new OwnerWindow(type, returnHandler, getGroupInfo);
//		}
//		return instance;
//	}
	private GroupSearchGridPanel groupSearchPanel;
	private GroupTreePanel groupPanel;
	private SelectTeamGridPanel SelectTeamPanel;
	
	private int HEIGHT = 580;
	
	public GroupWindow(final String type, final ReturnHandler returnHandler, final boolean getGroupInfo) {
		this(type, returnHandler, getGroupInfo, null);
	}
	
	public GroupWindow(final String type, final ReturnHandler returnHandler, final boolean getGroupInfo, final Object name) {
		this.getGroupInfo = getGroupInfo;
		this.type = type;
		if("single".equals(type)){
			setWidth(465);
			setHeight(HEIGHT);
		}else if("singleGroup".equals(type)){
			setWidth(465);
			setHeight(HEIGHT);
		}else if("multy".equals(type)){
			setWidth(770);
			setHeight(HEIGHT);
		}
		
		setReturnHandler(returnHandler);
		
        setTitle(I18N.message("target"));   
        setShowMinimizeButton(false);   
        setIsModal(true);   
        setShowModalMask(true);   
        setAlign(Alignment.CENTER);
        centerInPage();
        
        VLayout rootLayout = new VLayout();
        rootLayout.setAlign(Alignment.RIGHT);
        rootLayout.setMargin(5);
        HLayout mainLayout = new HLayout(10);
        mainLayout.setWidth100();
//        mainLayout.setHeight(450);
        mainLayout.setAlign(Alignment.CENTER);
        mainLayout.setAlign(VerticalAlignment.CENTER);
        // Group ���� Panel
        groupPanel = new GroupTreePanel("admin.org.group"+type, this, false, true, false, "250");
        groupPanel.setHeight(480);
        groupPanel.setWidth(180);
        // Group Panel ���� ���õ� member �����ִ� panel
        groupSearchPanel = new GroupSearchGridPanel("admin.org.group"+type, I18N.message("searchgroup"), null, false, true,"50%", name);
//        teamPanel.setHeight100();
        
		groupSearchPanel.getGrid().addCellDoubleClickHandler(new CellDoubleClickHandler(){

			@Override
			public void onCellDoubleClick(CellDoubleClickEvent event) {
				returnItem();
			}
        });
		
        if("multy".equals(type)){
        	// teamPanel ���� ���õ� member �����ִ� panel
        	SelectTeamPanel = new SelectTeamGridPanel("admin.org.group"+type, I18N.message("second.selectteammembers"), groupSearchPanel.getGrid(),"50%");
        	SelectTeamPanel.setHeight(480);
            mainLayout.addMember(groupPanel);
            mainLayout.addMember(groupSearchPanel);
            mainLayout.addMember(createArrowVL());
            mainLayout.addMember(SelectTeamPanel);
        }else{
        	 mainLayout.addMember(groupPanel);
             mainLayout.addMember(groupSearchPanel);
        }
        
        VLayout 	Volayout 	= new VLayout();
        DynamicForm ownerForm = new DynamicForm();
        ButtonItem okBtn = new ButtonItem(I18N.message("select"));
        ownerForm.setItems(okBtn);
        Volayout.addMember(ownerForm);
        Volayout.setLayoutLeftMargin(410);
        rootLayout.addMembers(mainLayout, Volayout);
        
        addItem(rootLayout);
        
        okBtn.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				returnItem();
			}
		});
        
        addCloseClickHandler(new CloseClickHandler(){
			@Override
			public void onCloseClick(CloseClickEvent event) {
				// 20130829, junsoo, �̱��� ���ſ� ���� destroy
//				hide();
				destroy();
			}
        });
	}
	
	protected SUser getUser(SUser id) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Arrow�г� ����
	 */
	private VLayout createArrowVL() {
		
		leftArrow = new TransferImgButton(TransferImgButton.LEFT, new com.smartgwt.client.widgets.events.ClickHandler(){

			@Override
			public void onClick(
					com.smartgwt.client.widgets.events.ClickEvent event) {
				SelectTeamPanel.removeRecordsFromMembers();  
			}   
        });   
        
		rightArrow = new TransferImgButton(TransferImgButton.RIGHT, new com.smartgwt.client.widgets.events.ClickHandler(){
			@Override
			public void onClick(
					com.smartgwt.client.widgets.events.ClickEvent event) {
				SelectTeamPanel.copyRecordsToMembers();
			}   
        });
		        
        VLayout arrowPanel = new VLayout();
        arrowPanel.setWidth(30);
        arrowPanel.setHeight100();
        arrowPanel.setAlign(VerticalAlignment.CENTER);
        arrowPanel.setMembersMargin(Constants.ARROW_MARGIN);
        arrowPanel.setMembers(leftArrow, rightArrow);
        return arrowPanel;
	}
	
	@Override
	public void onRecordSelected(Serializable id, Serializable parentId) {
		groupSearchPanel.executeFetch((String)id, "");
	}


	@Override
	public void onRecordSelected(Record record) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onRecordClick(Record record) {
	}

	@Override
	public void onRecordDoubleClick(Record record) {
		if("singleGroup".equals(type)){
			String tempId = record.getAttributeAsString("id");
			
			if(!tempId.equals(Long.toString(Constants.ADMIN_ROOT)) && Constants.ADMIN_GROUP_ROOT != tempId) {
				if (returnHandler != null) {
					returnHandler.onReturn(new String[]{record.getAttribute("id"), record.getAttribute("name")});
				}
				// 20130829, junsoo, �̱��� ���ſ� ���� destroy
//				hide();
				destroy();

			}
		}
		
	}

	@Override
	public boolean isExistMember() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isIDLong() {
		return true;
	}
	
	private void returnItem(){
		if("single".equals(type)){
			ListGridRecord[] selectedRecord = groupSearchPanel.getGrid().getSelectedRecords();
			if (returnHandler != null) {
				/*
				 *  ��� �߰� ����� ���ý� ����� ����(id, name) ��ȯ or �׷� ���ý�(����� �̼��õ�) �׷� ���� ��ȯ(id, name)
				 *  20130826 taesu
				 * */
				if(selectedRecord==null){
					returnHandler.onReturn(new String[]{groupPanel.getSelectedGrpId(), groupPanel.getSelectedGrpNm()});
				}
				else{
					if(getGroupInfo){
						// 20130827 taesu, ���� ���� ���� �߰�
						if(selectedRecord.length > 0){
							String[][] data = new String[selectedRecord.length][7];
							SUser user;
							
							int i=0;
							for (String[] str : data) {
//								str[0] = selectedRecord[i].getAttribute("id");
//								str[1] = selectedRecord[i].getAttribute("name");
//								str[2] = groupPanel.getSelectedGrpNm();
//								str[3] = selectedRecord[i].getAttribute("username");
//								str[4] = groupPanel.getSelectedGrpId();
								
								str[0] = selectedRecord[i].getAttribute("id");
								str[1] = selectedRecord[i].getAttribute("name");
								str[2] = selectedRecord[i].getAttribute("department");
								if(str[2] == null) str[2] = groupPanel.getSelectedGrpNm();  
								str[3] = selectedRecord[i].getAttribute("username");
								str[4] = selectedRecord[i].getAttribute("departmentid");
								str[5] = selectedRecord[i].getAttribute("path");		
								str[6]	=	selectedRecord[i].getAttribute("IDPath");
								if(str[4] == null) str[4] = groupPanel.getSelectedGrpId();
								i++;
							}
							returnHandler.onReturn(data);
						}
					}
					else{
						if (selectedRecord.length < 1)
						return;
						String[][] data = new String[selectedRecord.length][4];
						int i=0;
						
						
						for (String[] str : data) {
							str[0] = selectedRecord[i].getAttribute("id");
							str[1] = selectedRecord[i].getAttribute("name");
							str[2] = selectedRecord[i].getAttribute("path");		
							str[3]	=	selectedRecord[i].getAttribute("IDPath");
//							str[2] = selectedRecord[i].getAttribute("username");
//							str[3] = selectedRecord[i].getAttribute("email");
//							str[4] = selectedRecord[i].getAttribute("groupid");
							i++;
						}
						
						//20131213 na �ý���->���ӿ����� ��������
						//2���� �Ķ���ʹ� catch������ ����
						try {
							returnHandler.onReturn(data);
						} catch (Exception e) {
							returnHandler.onReturn(new String[]{selectedRecord[0].getAttribute("id"), selectedRecord[0].getAttribute("name")});
						}
					}
				}
			}
			// 20130829, junsoo, �̱��� ���ſ� ���� destroy
//			hide();
			destroy();
		}
		else if("singleGroup".equals(type)){
			String grpId= groupPanel.getSelectedGrpId();
			//20131218na Root�������� �˻��ϴ� ��쿡�� �ݿ�
//			if(!Constants.ADMIN_GROUP_ROOT.equals(grpId)){
			if (returnHandler != null) {
				ListGridRecord teamSelectedRecord = groupSearchPanel.getGrid().getSelectedRecord();
				if(null== teamSelectedRecord){//�׷츸 ���ý�
					returnHandler.onReturn(new String[]{groupPanel.getSelectedGrpId(), groupPanel.getSelectedGrpNm()});
				}else{//�����ý�
					returnHandler.onReturn(new String[]{teamSelectedRecord.getAttribute("groupid"), teamSelectedRecord.getAttribute("name"), teamSelectedRecord.getAttribute("id")});
				}
				destroy();
//				}
			}
		}
	}
}
