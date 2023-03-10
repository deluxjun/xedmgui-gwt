package com.speno.xedm.gui.frontend.client.folder;

import java.io.Serializable;

import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.RecordList;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.TransferImgButton;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.CloseClickEvent;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.events.ClickEvent;
import com.smartgwt.client.widgets.form.fields.events.ClickHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.CellClickEvent;
import com.smartgwt.client.widgets.grid.events.CellClickHandler;
import com.smartgwt.client.widgets.grid.events.CellDoubleClickEvent;
import com.smartgwt.client.widgets.grid.events.CellDoubleClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.speno.xedm.core.service.serials.SGroup;
import com.speno.xedm.core.service.serials.SUser;
import com.speno.xedm.gui.common.client.Constants;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.RecordObserver;
import com.speno.xedm.gui.common.client.util.ReturnHandler;
import com.speno.xedm.gui.frontend.client.admin.organization.GroupTreePanel;
import com.speno.xedm.gui.frontend.client.admin.organization.SelectTeamGridPanel;
import com.speno.xedm.gui.frontend.client.admin.organization.TeamGridPanel;

public class OwnerWindow extends Window implements RecordObserver{
//	protected static OwnerWindow instance;
	private TransferImgButton leftArrow, rightArrow;
	
	private ReturnHandler returnHandler;		// 20130806, junsoo, ?????? ?????? handler
	private String type;
	
	private boolean getGroupInfo;
	
	SUser user = null;
	
	public void setReturnHandler(ReturnHandler returnHandler) {
		this.returnHandler = returnHandler;
	}
	// 20130829, junsoo, ?????? ????.
//	public static OwnerWindow get(final String type, ReturnHandler returnHandler, boolean getGroupInfo) {
//		if (instance == null) {
//			instance = new OwnerWindow(type, returnHandler, getGroupInfo);
//		}
//		return instance;
//	}
	private TeamGridPanel teamPanel;
	private GroupTreePanel groupPanel;
	private SelectTeamGridPanel SelectTeamPanel;
	
	private int HEIGHT = 580;
	
	public OwnerWindow(final String type, final ReturnHandler returnHandler, final boolean getGroupInfo) {
		this(type, returnHandler, getGroupInfo, null);		
	}
	public OwnerWindow(final String cur_group, final String type, final ReturnHandler returnHandler, final boolean getGroupInfo) {		
		this(type, returnHandler, getGroupInfo, null);		
		//Object layout = getLayoutData();
	}
	public OwnerWindow(final String type, final ReturnHandler returnHandler, final boolean getGroupInfo, final String userName) {
		this(null, type, returnHandler, getGroupInfo, userName);
	}
	
	public OwnerWindow(final SGroup[] topGroups, final String type, final ReturnHandler returnHandler, final boolean getGroupInfo, final String userName) {
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
        // Group ???? Panel

        groupPanel = new GroupTreePanel("admin.org.group"+type, this, false, true, false, "250");
        groupPanel.setHeight(HEIGHT-80);
        groupPanel.setWidth(180);
                
        // Group Panel ???? ?????? member ???????? panel
        teamPanel = new TeamGridPanel("admin.org.group"+type, I18N.message("teammembers"), null, false, true,"50%", userName);
//        teamPanel.setHeight100();
        
		teamPanel.getGrid().addCellDoubleClickHandler(new CellDoubleClickHandler(){

			@Override
			public void onCellDoubleClick(CellDoubleClickEvent event) {
				if(SelectTeamPanel != null){
					SelectTeamPanel.copyRecordsToMembers();
					return;
				}
				
				returnItem();
			}        	
        });
		
		// top group ????
		prepareTopGroupsGrid(topGroups);

    	VLayout groupsLayout = new VLayout();
    	if (topGroupsGrid != null) {
    		Label subTitle = new Label();
            subTitle.setAutoHeight();   
            subTitle.setAlign(Alignment.LEFT);   
            subTitle.setValign(VerticalAlignment.CENTER);
            subTitle.setStyleName("subTitle");
            subTitle.setContents(I18N.message("esRewriterInfo"));

        	groupsLayout.addMember(subTitle);
        	groupsLayout.addMember(topGroupsGrid);
            groupPanel.setHeight(HEIGHT-80-80-20);
    	}
    		
    	groupsLayout.addMember(groupPanel);
        mainLayout.addMember(groupsLayout);
        mainLayout.addMember(teamPanel);

        if("multy".equals(type)){
        	// teamPanel ???? ?????? member ???????? panel
        	SelectTeamPanel = new SelectTeamGridPanel("admin.org.group"+type, I18N.message("second.selectteammembers"), teamPanel.getGrid(),"50%");
        	SelectTeamPanel.setHeight(480);        	
        	
            mainLayout.addMember(createArrowVL());
            mainLayout.addMember(SelectTeamPanel);            
        }else{
        }
        
        VLayout 	Vlayout 	= new VLayout();
        DynamicForm ownerForm = new DynamicForm();
        ButtonItem okBtn = new ButtonItem(I18N.message("confirm"));
        okBtn.setWidth(40);
        ownerForm.setItems(okBtn);
        Vlayout.addMember(ownerForm);
        
        if("multy".equals(type)){
        	Vlayout.setLayoutLeftMargin(705);
            Vlayout.setLayoutTopMargin(10);
		}
        else{
        	Vlayout.setLayoutLeftMargin(400);
        }
        
        rootLayout.addMembers(mainLayout, Vlayout);
        
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
				// 20130829, junsoo, ?????? ?????? ???? destroy
//				hide();
				destroy();
			}
        });
	}
	
	ListGrid topGroupsGrid = null;
	private void prepareTopGroupsGrid(SGroup[] groups) {
		if (groups == null || groups.length < 1)
			return;

		topGroupsGrid = new ListGrid();
		topGroupsGrid.setMargin(2);
		topGroupsGrid.setWidth100();
		topGroupsGrid.setHeight(80);		
		topGroupsGrid.setShowAllRecords(true);
		topGroupsGrid.setEmptyMessage(I18N.message("notitemstoshow"));
	        
		topGroupsGrid.setCanFreezeFields(true);
		topGroupsGrid.setCanRemoveRecords(false);
		topGroupsGrid.setSelectionType(SelectionStyle.SINGLE);
		
		ListGridField idField = new ListGridField("groupId", I18N.message("id"));
		ListGridField nameField = new ListGridField("groupName", I18N.message("name"));
		topGroupsGrid.setFields(idField, nameField);
		
		for (SGroup group : groups) {
			ListGridRecord record = new ListGridRecord();
			record.setAttribute("groupId", group.getId());
			record.setAttribute("groupName", group.getName());
			topGroupsGrid.addData(record);
		}

		topGroupsGrid.addCellClickHandler(new CellClickHandler() {
			@Override
			public void onCellClick(CellClickEvent event) {
				ListGridRecord record = event.getRecord();
				topGroupsGrid.selectRecord(record);
				String id = record.getAttributeAsString("groupId");
				teamPanel.executeFetch(id, "");
			}
		});
	}

	
	private void returnItem(){
		if("single".equals(type)){
			ListGridRecord[] selectedRecord = teamPanel.getGrid().getSelectedRecords();
			if (returnHandler != null) {
				/*
				 *  ???? ???? ?????? ?????? ?????? ????(id, name) ???? or ???? ??????(?????? ????????) ???? ???? ????(id, name)
				 *  20130826 taesu
				 * */
				if(selectedRecord==null){
					returnHandler.onReturn(new String[]{groupPanel.getSelectedGrpId(), groupPanel.getSelectedGrpNm()});
				}
				else{
					if(getGroupInfo){
						// 20130827 taesu, ???? ???? ???? ????
						if(selectedRecord.length > 0){
							String[][] data = new String[selectedRecord.length][5];
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
								if(str[4] == null) str[4] = groupPanel.getSelectedGrpId();
								i++;
							}
							returnHandler.onReturn(data);
						}
					}
					else{
						if (selectedRecord.length < 1)
							return;
						String[][] data = new String[selectedRecord.length][5];
						int i=0;
						
						
						for (String[] str : data) {
							str[0] = selectedRecord[i].getAttribute("id");
							str[1] = selectedRecord[i].getAttribute("name");
							str[2] = selectedRecord[i].getAttribute("username");
							str[3] = selectedRecord[i].getAttribute("email");
							str[4] = selectedRecord[i].getAttribute("groupid");
							i++;
						}
						
						//20131213 na ??????->?????????? ????????
						//2???? ?????????? catch?????? ????
						try {
							returnHandler.onReturn(data);
						} catch (Exception e) {
							returnHandler.onReturn(new String[]{selectedRecord[0].getAttribute("id"), selectedRecord[0].getAttribute("name")});
						}
					}
				}
			}
			// 20130829, junsoo, ?????? ?????? ???? destroy
//			hide();
			destroy();
		}
		else if("singleGroup".equals(type)){
			String grpId= groupPanel.getSelectedGrpId();
			//20131218na Root???????? ???????? ???????? ????
//			if(!Constants.ADMIN_GROUP_ROOT.equals(grpId)){
			if (returnHandler != null) {
				ListGridRecord teamSelectedRecord = teamPanel.getGrid().getSelectedRecord();
				if(null== teamSelectedRecord){//?????? ??????
					returnHandler.onReturn(new String[]{groupPanel.getSelectedGrpId(), groupPanel.getSelectedGrpNm()});
				}else{//????????
					returnHandler.onReturn(new String[]{teamSelectedRecord.getAttribute("groupid"), teamSelectedRecord.getAttribute("name"), teamSelectedRecord.getAttribute("id")});
				}
				destroy();
//				}
			}
		}
		else if("multy".equals(type)){		
			RecordList rclist = SelectTeamPanel.getGrid().getDataAsRecordList();
			if(rclist.getLength() <= 0) return;
			String ids = "";
			String userids = "";
			String userNames = "";
			String userIdNames = "";
			String email = "";
			for(int j=0; j<rclist.getLength(); j++) {
				Record rc = rclist.get(j);
				ids += "," + rc.getAttributeAsString("id");
				userids += "," + rc.getAttributeAsString("username");
				userNames += "," + rc.getAttributeAsString("name");
				userIdNames += ","+rc.getAttributeAsString("username") +"("+ rc.getAttributeAsString("name") + ")";
				email += "," + rc.getAttributeAsString("email");
			}
			if(!"".equals(ids)){
				ids = ids.substring(1);
				userids = userids.substring(1);
				userIdNames = userIdNames.substring(1);
				userNames = userNames.substring(1);
				email = email.substring(1);
			}
			if (returnHandler != null) {
				returnHandler.onReturn(new String[]{ids,userids, userIdNames, userNames, email});
			}
			// 20130829, junsoo, ?????? ?????? ???? destroy
//			hide();
			destroy();
		}
	}
	
	protected SUser getUser(SUser id) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Arrow???? ????
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
		teamPanel.executeFetch((String)id, "");
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
				// 20130829, junsoo, ?????? ?????? ???? destroy
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
}
