
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
//커밋하기 위한 주석
public class MenuManagementGridPanel extends VLayout{
	private static HashMap<String, MenuManagementGridPanel> instanceMap = new HashMap<String, MenuManagementGridPanel>();
	
	private ListGrid grid;
	
	private boolean isEnableSelectedHandler = true;
	
	
	/**
	 * 기 생성된 instance를 반환함. instance가 없을 경우 생성 후 반환함
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
		
		idField.setHidden(true);	//그리드에서 해당컬럼이 안보이도록
		descField.setHidden(true);	//그리드에서 해당컬럼이 안보이도록
        
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
                      
        
		/* Sub Title 생성 */
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
        

        //  Apply 버튼 클릭 시 현재 선택된 데이터의 키값 으로 작업
        button.addClickHandler(new ClickHandler() {   
            public void onClick(ClickEvent event) {
        	   	//DB에 업데이트
//        	   	doUpdateSecuProfileKeyOnMenu();
            	MenuManagementPanel.get().executeSave();
            }   
        });        
 
	}
	
	
	/**
	 * Apply 버튼 클릭시 현재 선택된 Admin 메뉴 항목의 ES_SECURITYREF 에 그리드의 SECURITY PROFILE 값을 매칭하여 update
	 */
//	private void doUpdateSecuProfileKeyOnMenu(){	
//	//selectedMenuId, selectedProfileId 변수를 이용하여 업데이트
//		
//		//그리드에 체크된 데이터가 있는 경우에만 수행하도록
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
	 * 수신한 SSecurityProfile 정보를 Grid에 Set
	 * @param result
	 */
	private void setData(List<SSecurityProfile> result) {		
		/*
		 * id가 100이하인 Security Profile은 18N에서 관리되는 profile로 본 패널에서
		 * 관리하지 않음.
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
//	//AdminMenu 트리노드의 선택 시 마다 호출되는 이벤트 함수
//	public void SelectGridDataById(Long data_MENU_ID, Long data_ES_SECURITYREF, Long data_SecurityRefOfParent){
//		
//	   	selectedMenuId = data_MENU_ID;
//	   	secuRefOfParent = data_SecurityRefOfParent;
//	   	selectedProfileId = (long) -1;
//	   	
//	   	Boolean existInGrid = false;
//	   	
//	   	
//	   	//기존의 그리드상의 선택된 상태를 미선택 상태로 초기화
//	   	grid.deselectAllRecords();
//	   	
//
//	   	//정상데이터인 경우
//	   	if(data_ES_SECURITYREF >= 0){
//	        //파라미터로 넘겨받은 key값을 이용하여 해당하는 row 선택
//	        for (int index = 1; index <= grid.getTotalRows(); index++){
//
//	        	if(String.valueOf(data_ES_SECURITYREF).equals(grid.getRecord(index-1).getAttribute("ES_ID"))){
//	        	   	selectedProfileId = Long.valueOf(grid.getRecord(index-1).getAttribute("ES_ID"));
//	        	   	grid.selectRecord(index-1);
//	        	   	existInGrid = true;		//그리드 안에 존재
//	        	   	
//	        	   	break;
//	        	}
//	        }
//	   	}
//
//	   	
//        //요건 : 자신의 ES_SECURITYREF가 그리드 안에 존재하지 않는 경우(잘못된 값 혹은 그리드안에 없는 데이터)
//        //해당메뉴의 부모메뉴에 할당된 ES_SECURITYREF값을 현재 나의 ES_SECURITYREF값으로 갱신
//        if(existInGrid == false || data_ES_SECURITYREF < 0){
//        	
//        	//정상데이터인 경우  //data_SecurityRefOfParent(현재 노드의 부모노드 ES_SECURITYREF 값)
//    	   	if(data_SecurityRefOfParent >= 0){
//    	        for (int index = 1; index <= grid.getTotalRows(); index++){
//    	        	if(String.valueOf(data_SecurityRefOfParent).equals(grid.getRecord(index-1).getAttribute("ES_ID"))){
//    	        	   	selectedProfileId = Long.valueOf(grid.getRecord(index-1).getAttribute("ES_ID"));
//    	        	   	grid.selectRecord(index-1);
//    	        	   	
//    	        	   	//갱신하고 나서 DB에도 업데이트(Apply버튼 클릭 시와 동일한 이벤트 구현 하면 됨)
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
	//AdminMenu 트리노드의 선택 시 마다 호출되는 이벤트 함수
	public void SelectGridDataById(SAdminMenu menu){
		
		//기존의 그리드상의 선택된 상태를 미선택 상태로 초기화
	   	grid.deselectAllRecords();
	   	

	   	//정상데이터인 경우
	   	if(menu.getSecurityRefs() != null && menu.getSecurityRefs().length > 0){
			RecordList rclist = grid.getDataAsRecordList();
			for (long ref : menu.getSecurityRefs()) {
				Record rc = rclist.find("ES_ID", String.valueOf(ref));
					grid.selectRecord(rc);
			}
//
//	        //파라미터로 넘겨받은 key값을 이용하여 해당하는 row 선택
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