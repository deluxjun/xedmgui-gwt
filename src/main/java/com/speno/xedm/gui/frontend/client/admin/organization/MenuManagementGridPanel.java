
package com.speno.xedm.gui.frontend.client.admin.organization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.RecordList;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.SelectionAppearance;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.SelectionChangedHandler;
import com.smartgwt.client.widgets.grid.events.SelectionEvent;
import com.smartgwt.client.widgets.layout.VLayout;
import com.speno.xedm.core.service.serials.SAdminMenu;
import com.speno.xedm.core.service.serials.SSecurityProfile;
import com.speno.xedm.gui.common.client.Constants;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.Session;
import com.speno.xedm.gui.common.client.log.Log;
import com.speno.xedm.gui.common.client.util.AsyncCallbackWithStatus;
import com.speno.xedm.gui.common.client.util.ServiceUtil;

/**   MenuManagementGridPanel   MenuManagementGrid
 * UserGrid Panel
 * 
 * @author
 * @since 1.0
 */
//Ŀ���ϱ� ���� �ּ�
public class MenuManagementGridPanel extends VLayout{
	private static HashMap<String, MenuManagementGridPanel> instanceMap = new HashMap<String, MenuManagementGridPanel>();
	
	private ListGrid grid;
	
	private boolean isEnableSelectedHandler = true;
	
	
	/**
	 * �� ������ instance�� ��ȯ��. instance�� ���� ��� ���� �� ��ȯ��
	 * @param id
	 * @param ob
	 * @return
	 */
	public static MenuManagementGridPanel get(
			final String id) {
			if (instanceMap.get(id) == null) {
				new MenuManagementGridPanel(id);
			}		
			return instanceMap.get(id);
	}

	public MenuManagementGridPanel(		
		final String id) {

		instanceMap.put(id, this);

		grid = new ListGrid();	
		grid.setHeight100();
		grid.setShowAllRecords(true);        
        grid.setBorder("1px solid gray");
        grid.setSelectionAppearance(SelectionAppearance.CHECKBOX);
        grid.setSelectionType(SelectionStyle.SIMPLE); 
        grid.setEmptyMessage(I18N.message("notitemstoshow"));

		ListGridField idField = new ListGridField("ES_ID", I18N.message("id"));
		ListGridField nameField = new ListGridField("ES_NAME", I18N.message("securityprofile"));
		ListGridField descField = new ListGridField("ES_DESCRIPTION", I18N.message("description"));
		
		idField.setHidden(true);	//�׸��忡�� �ش��÷��� �Ⱥ��̵���
		descField.setHidden(true);	//�׸��忡�� �ش��÷��� �Ⱥ��̵���
        
		grid.setFields(idField, nameField, descField);

        
        ServiceUtil.documentcode().listSecurityProfileLikeName(Session.get().getSid(), "", new AsyncCallbackWithStatus<List<SSecurityProfile>>() {
			@Override
			public String getSuccessMessage() {
				return I18N.message("client.searchComplete");
			}
			@Override
			public String getProcessMessage() {
				return I18N.message("client.searchRequest");
			}
			@Override
			public void onFailureEvent(Throwable caught) {
				Log.warn("Second.Client.SelectQueryFailure", caught.getMessage());
			}
			@Override
			public void onSuccessEvent(List<SSecurityProfile> result) {
				setData(result);
			}
		});
        
        
        setHeight100();
        setMembersMargin(10);
        setPadding(Constants.PADDING_DEFAULT);
                      
        
		/* Sub Title ���� */
		Label subTitleLable = new Label();
        subTitleLable.setHeight(16);   
        subTitleLable.setAlign(Alignment.LEFT);   
        subTitleLable.setValign(VerticalAlignment.CENTER);
        subTitleLable.setStyleName("subTitle");
        subTitleLable.setContents("");
        
        addMember(subTitleLable);
        addMember(grid);
        
        final IButton button = new IButton(I18N.message("apply"));
        addMember(button);

        grid.addSelectionChangedHandler(new SelectionChangedHandler() {
			@Override
			public void onSelectionChanged(SelectionEvent event) {
				// TODO Auto-generated method stub
				if(isEnableSelectedHandler){
					ListGridRecord record = event.getRecord();
					MenuManagementPanel.get().setAutoGridData(record.getAttributeAsLong("ES_ID"), event.getState());
				}
			}
		});
        

        //  Apply ��ư Ŭ�� �� ���� ���õ� �������� Ű�� ���� �۾�
        button.addClickHandler(new ClickHandler() {   
            public void onClick(ClickEvent event) {
        	   	//DB�� ������Ʈ
//        	   	doUpdateSecuProfileKeyOnMenu();
            	MenuManagementPanel.get().executeSave();
            }   
        });        
 
	}
	
	
	/**
	 * Apply ��ư Ŭ���� ���� ���õ� Admin �޴� �׸��� ES_SECURITYREF �� �׸����� SECURITY PROFILE ���� ��Ī�Ͽ� update
	 */
//	private void doUpdateSecuProfileKeyOnMenu(){	
//	//selectedMenuId, selectedProfileId ������ �̿��Ͽ� ������Ʈ
//		
//		//�׸��忡 üũ�� �����Ͱ� �ִ� ��쿡�� �����ϵ���
////		if(grid.getSelectedRecords().length >0){
//			
//		SAdminMenu adminMenu = new SAdminMenu();
//		adminMenu.setId(currentAdminMenu.getId());
//
//		if (grid.getSelectedRecords().length > 0){
//			long[] refs = new long[grid.getSelectedRecords().length];
//			for (int i = 0; i < grid.getSelectedRecords().length; i++) {
//				refs[i] = grid.getSelectedRecords()[i].getAttributeAsLong("ES_ID").longValue();
//			}
//			adminMenu.setSecurityRefs(refs);
//		}
//
//	   	ServiceUtil.security().updateSecurityRef(Session.get().getSid(), adminMenu, new AsyncCallback<SAdminMenu>() {
//			@Override
//			public void onSuccess(SAdminMenu result) {
//				SC.say(I18N.message("operationcompleted"));			
//			}	
//			@Override
//			public void onFailure(Throwable caught) {
//				Log.serverError( I18N.message("genericerror"), caught, true);
//			}
//		});
//		   	
////		}
//	}
	
	
	
	
	/**
	 * ������ SSecurityProfile ������ Grid�� Set
	 * @param result
	 */
	private void setData(List<SSecurityProfile> result) {		
		/*
		 * id�� 100������ Security Profile�� 18N���� �����Ǵ� profile�� �� �гο���
		 * �������� ����.
		 */
		
		List<ListGridRecord> reFinedRecords = new ArrayList<ListGridRecord>();
				
		for (int j = 0; j < result.size(); j++) {			
			long id = result.get(j).getId();
			if(id > 100 || id == 0) {
				ListGridRecord record = new ListGridRecord();
				record.setAttribute("ES_ID", id);
				record.setAttribute("ES_NAME", result.get(j).getName());
				record.setAttribute("ES_DESCRIPTION", result.get(j).getDescription());
				reFinedRecords.add(record);
			}
		}
		
		ListGridRecord[] records = new ListGridRecord[reFinedRecords.size()];
		records = reFinedRecords.toArray(records);
		grid.setData(records);
		
	}
//
//	//AdminMenu Ʈ������� ���� �� ���� ȣ��Ǵ� �̺�Ʈ �Լ�
//	public void SelectGridDataById(Long data_MENU_ID, Long data_ES_SECURITYREF, Long data_SecurityRefOfParent){
//		
//	   	selectedMenuId = data_MENU_ID;
//	   	secuRefOfParent = data_SecurityRefOfParent;
//	   	selectedProfileId = (long) -1;
//	   	
//	   	Boolean existInGrid = false;
//	   	
//	   	
//	   	//������ �׸������ ���õ� ���¸� �̼��� ���·� �ʱ�ȭ
//	   	grid.deselectAllRecords();
//	   	
//
//	   	//���������� ���
//	   	if(data_ES_SECURITYREF >= 0){
//	        //�Ķ���ͷ� �Ѱܹ��� key���� �̿��Ͽ� �ش��ϴ� row ����
//	        for (int index = 1; index <= grid.getTotalRows(); index++){
//
//	        	if(String.valueOf(data_ES_SECURITYREF).equals(grid.getRecord(index-1).getAttribute("ES_ID"))){
//	        	   	selectedProfileId = Long.valueOf(grid.getRecord(index-1).getAttribute("ES_ID"));
//	        	   	grid.selectRecord(index-1);
//	        	   	existInGrid = true;		//�׸��� �ȿ� ����
//	        	   	
//	        	   	break;
//	        	}
//	        }
//	   	}
//
//	   	
//        //��� : �ڽ��� ES_SECURITYREF�� �׸��� �ȿ� �������� �ʴ� ���(�߸��� �� Ȥ�� �׸���ȿ� ���� ������)
//        //�ش�޴��� �θ�޴��� �Ҵ�� ES_SECURITYREF���� ���� ���� ES_SECURITYREF������ ����
//        if(existInGrid == false || data_ES_SECURITYREF < 0){
//        	
//        	//���������� ���  //data_SecurityRefOfParent(���� ����� �θ��� ES_SECURITYREF ��)
//    	   	if(data_SecurityRefOfParent >= 0){
//    	        for (int index = 1; index <= grid.getTotalRows(); index++){
//    	        	if(String.valueOf(data_SecurityRefOfParent).equals(grid.getRecord(index-1).getAttribute("ES_ID"))){
//    	        	   	selectedProfileId = Long.valueOf(grid.getRecord(index-1).getAttribute("ES_ID"));
//    	        	   	grid.selectRecord(index-1);
//    	        	   	
//    	        	   	//�����ϰ� ���� DB���� ������Ʈ(Apply��ư Ŭ�� �ÿ� ������ �̺�Ʈ ���� �ϸ� ��)
//    	        	   	doUpdateSecuProfileKeyOnMenu();
//    	        	   	
//    	        	   	break;
//    	        	}
//    	        }
//    	   	}
//        }
//        
//	}//public void SelectGridDataById
//	
	//AdminMenu Ʈ������� ���� �� ���� ȣ��Ǵ� �̺�Ʈ �Լ�
	public void SelectGridDataById(SAdminMenu menu){
		
		//������ �׸������ ���õ� ���¸� �̼��� ���·� �ʱ�ȭ
	   	grid.deselectAllRecords();
	   	

	   	//���������� ���
	   	if(menu.getSecurityRefs() != null && menu.getSecurityRefs().length > 0){
			RecordList rclist = grid.getDataAsRecordList();
			for (long ref : menu.getSecurityRefs()) {
				Record rc = rclist.find("ES_ID", String.valueOf(ref));
					grid.selectRecord(rc);
			}
//
//	        //�Ķ���ͷ� �Ѱܹ��� key���� �̿��Ͽ� �ش��ϴ� row ����
//	        for (int index = 0; index < grid.getTotalRows(); index++){
//
//	        	if(String.valueOf(menu.getSecurityRef()).equals(grid.getRecord(index).getAttribute("ES_ID"))){
//	        	   	selectedProfileId = Long.valueOf(grid.getRecord(index).getAttribute("ES_ID"));
//	        	   	grid.selectRecord(index);
//	        	   	
//	        	   	break;
//	        	}
//	        }
	   	}

        
	}//public void SelectGridDataById

	public boolean isEnableSelectedHandler() {
		return isEnableSelectedHandler;
	}

	public void setEnableSelectedHandler(boolean isEnableSelectedHandler) {
		this.isEnableSelectedHandler = isEnableSelectedHandler;
	}


}