
package com.speno.xedm.gui.frontend.client.admin.organization;

import java.util.ArrayList;
import java.util.List;

import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.VLayout;
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
public class ACLMenuManagementGridPanel extends VLayout{
	private static ACLMenuManagementGridPanel instance;
	
	private MenuManagementPanel parentPanel;

		
	public ListGrid grid;
	

	public ACLMenuManagementGridPanel(MenuManagementPanel parentPanel) {

		this.parentPanel = parentPanel;
		
		grid = new ListGrid();	
		grid.setHeight100();
		grid.setSelectionType(SelectionStyle.SINGLE);
		grid.setShowAllRecords(true);        
        grid.setBorder("1px solid gray");             
        grid.setEmptyMessage(I18N.message("notitemstoshow"));

		ListGridField idField = new ListGridField("ES_ID", I18N.message("id"));
		ListGridField nameField = new ListGridField("ES_NAME", I18N.message("securityprofile"));
		ListGridField descField = new ListGridField("ES_DESCRIPTION", I18N.message("description"));
		
		idField.setHidden(true);	//그리드에서 해당컬럼이 안보이도록
		descField.setHidden(true);	//그리드에서 해당컬럼이 안보이도록
        
		grid.setFields(idField, nameField, descField);
		
		setData();
		
		
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
        
        //  Apply 버튼 클릭 시 현재 선택된 데이터의 키값 으로 작업
        button.addClickHandler(new ClickHandler() {   
            public void onClick(ClickEvent event) {
            	ACLMenuManagementGridPanel.this.parentPanel.executeSaveACL();  	
            }   
        });         
	}

	/**
	 * 수신한 SSecurityProfile 정보를 Grid에 Set
	 * @param result
	 */
	public void setData() {		
		/*
		 * 
		 * id가 100이하인 Security Profile은 18N에서 관리되는 profile로 본 패널에서
		 * 관리하지 않음.
		 */
		String SessionID = Session.get().getSid();
        ServiceUtil.documentcode().listSecurityProfileLikeName(SessionID, "", new AsyncCallbackWithStatus<List<SSecurityProfile>>() {
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
				List<ListGridRecord> reFinedRecords = new ArrayList<ListGridRecord>();
				
				for (int j = 0; j < result.size(); j++) {			
					long id = result.get(j).getId();
					if(id > 100 || id == 0) {
						ListGridRecord record = new ListGridRecord();		
						record.setAttribute("ES_ID", id);
						record.setAttribute("ES_NAME", result.get(j).getName());
						record.setAttribute("ES_DESCRIPTION", result.get(j).getDescription());
						//if(!result.get(j).getName().equals("None"))
						reFinedRecords.add(record);			
					}
				}				
				ListGridRecord[] records = new ListGridRecord[reFinedRecords.size()];
				records = reFinedRecords.toArray(records);				
				grid.setData(records);
				grid.selectSingleRecord(0); 
				MenuManagementPanel.seleted_recordid = grid.getSelectedRecord().getAttributeAsLong("ES_ID");		
			}
		});
        
	}	
	


}