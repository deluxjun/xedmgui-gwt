package com.speno.xedm.gui.frontend.client.admin.system;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.RecordList;
import com.smartgwt.client.types.SelectionAppearance;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.CellClickEvent;
import com.smartgwt.client.widgets.grid.events.CellClickHandler;
import com.smartgwt.client.widgets.grid.events.RecordClickEvent;
import com.smartgwt.client.widgets.grid.events.RecordClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.speno.xedm.core.service.serials.SAdminMenu;
import com.speno.xedm.core.service.serials.SParameter;
import com.speno.xedm.core.service.serials.SSecurityProfile;
import com.speno.xedm.gui.common.client.Constants;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.RefreshObserver;
import com.speno.xedm.gui.common.client.Session;
import com.speno.xedm.gui.common.client.log.Log;
import com.speno.xedm.gui.common.client.util.AsyncCallbackWithStatus;
import com.speno.xedm.gui.common.client.util.ItemFactory;
import com.speno.xedm.gui.common.client.util.SCM;
import com.speno.xedm.gui.common.client.util.ServiceUtil;
import com.speno.xedm.gui.frontend.client.admin.TrackPanel;

/**
 * EcmSettings Panel
 * 
 * @author 남윤성
 * @since 1.0
 */
public class EcmSettingsPanel extends VLayout implements RefreshObserver{	
	private static EcmSettingsPanel instance;	
	
	private VLayout mainHL;
	private VLayout actionHL;
	private ListGrid grid1;
	private ListGrid grid2;
	private ListGrid grid3;;	
	
	/**
	 * 기 생성된 instance를 반환함. instance가 없을 경우 생성 후 반환함
	 * @return
	 */
	public static EcmSettingsPanel get() {
		if (instance == null) {
			instance = new EcmSettingsPanel();
		}
		return instance;
	}
	
	public EcmSettingsPanel() {            	
		setWidth100();
		setMembersMargin(10);
		setPadding(Constants.PADDING_DEFAULT);
		addMember(new TrackPanel(I18N.message("admin") + " > " + I18N.message("system") + " > " + I18N.message("second.ecmsetting"), this));
		
		createMainPanel(false); //Main패널 생성
	}
	
	/**
	 * Main패널 생성
	 */
	private void createMainPanel(boolean isRefresh) {
		if(isRefresh) {
			removeMember(mainHL);
		}
		
		mainHL = new VLayout(10);
		mainHL.setHeight100();
		mainHL.setMembers(createXvarmGridVL(),createActHL(), createSPGridVL());
		addMember(mainHL);
		
		setDataOnGeneralSettings();
		executeFetchSecurityProfile();
		executeFetchIndexId();
		
	}
	
	/**
	 * Refresh
	 */
	private void refresh() {
		createMainPanel(true);
	}
	
	/**
	 * Xvarm 그리드 생성
	 */
	private VLayout createXvarmGridVL() {
		grid1 = new ListGrid();
		grid1.setWidth100();
		grid1.setHeight100();
		grid1.setAlwaysShowEditors(true);
		grid1.setCellHeight(10); 
		grid1.setShowAllRecords(true);
		grid1.setEmptyMessage(I18N.message("notitemstoshow"));
		grid1.setSelectionType(SelectionStyle.SINGLE);
		grid1.setCanFreezeFields(true);
		grid1.setCanRemoveRecords(false);
		
		ListGridField nameViewField = new ListGridField("nameView", I18N.message("name"));
		nameViewField.setCanEdit(false);
		nameViewField.setWidth("200");
		ListGridField nameField = new ListGridField("name", I18N.message("name"));
		nameField.setHidden(true);
		ListGridField valueField = new ListGridField("value", I18N.message("value"));
		valueField.setWidth("*");
		grid1.setFields(nameViewField, nameField, valueField);
		
    	VLayout formVL = new VLayout(10);
    	formVL.setBorder("1px solid gray");
    	formVL.setWidth100();
    	formVL.setHeight100();    	
    	formVL.addMembers(grid1);
    	
    	return formVL;
	}
	
	/**
	 * Action Panel 생성
	 * @return
	 */
	private VLayout createActHL() {				
		Button btnSave = new Button(I18N.message("save"));
		btnSave.setIcon(ItemFactory.newImgIcon("data_into.png").getSrc());
		btnSave.addClickHandler(new ClickHandler() {   
            public void onClick(ClickEvent event) {
            	executeUpdateEcmContext();
            }   
        });
		
		actionHL = new VLayout(10);
		actionHL.setHeight(10);
		actionHL.setAutoWidth();
		actionHL.setMembers(btnSave);		
		return actionHL;
	}
	
	/**
	 * IndexId, Security Profile 그리드 생성
	 */
	private HLayout createSPGridVL() {
		grid2 = new ListGrid();
		grid2.setWidth100();
		grid2.setHeight100();		
		grid2.setShowAllRecords(true);
		grid2.setEmptyMessage(I18N.message("notitemstoshow"));
		grid2.setSelectionType(SelectionStyle.SINGLE);
		grid2.setCanFreezeFields(true);
		grid2.setCanRemoveRecords(false);
		
		ListGridField indexIdField = new ListGridField("indexId", I18N.message("indexid"));
		ListGridField securityRefField = new ListGridField("securityRef", I18N.message("indexid"));
		securityRefField.setHidden(true);
		ListGridField indexNameField = new ListGridField("indexName", I18N.message("indexid"));
		indexIdField.setHidden(true);
		grid2.setFields(indexIdField,securityRefField, indexNameField);
		
		grid2.addRecordClickHandler(new RecordClickHandler() {   
            public void onRecordClick(RecordClickEvent event) {
            	recordClickedProcess(event.getRecord());
            }   
        });
		
		grid3 = new ListGrid();
		grid3.setWidth100();
		grid3.setHeight100();		
		grid3.setShowAllRecords(true);
		grid3.setEmptyMessage(I18N.message("notitemstoshow"));
		grid3.setSelectionAppearance(SelectionAppearance.CHECKBOX); 
		grid3.setCanFreezeFields(true);
		grid3.setCanRemoveRecords(false);
		grid3.setSelectionType(SelectionStyle.SIMPLE);
		
		ListGridField idField = new ListGridField("id", I18N.message("securityprofile"));
		ListGridField nameField = new ListGridField("name", I18N.message("securityprofile"));
		idField.setHidden(true);
		grid3.setFields(idField, nameField);
		
		grid3.addCellClickHandler(new CellClickHandler() {
			@Override
			public void onCellClick(CellClickEvent event) {
				int colNum = event.getColNum();  
				if(0 != colNum){
					Record r = event.getRecord();
	            	if (!grid3.isSelected((ListGridRecord)r))
	            		grid3.selectRecord(r);
	            	else
	            		grid3.deselectRecord(r);
				}
			}
		});
		
		Button btnSave = new Button(I18N.message("save"));
		btnSave.setIcon(ItemFactory.newImgIcon("data_into.png").getSrc());
		btnSave.addClickHandler(new ClickHandler() {   
            public void onClick(ClickEvent event) {
	    		executeUpdateSecurityProfile();
            }   
        });	
		
		// EDM 테이블의 값과 adminMenu의 값을 맞춘다.
		Button btnRefresh = new Button(I18N.message("refresh"));
		btnRefresh.setIcon(ItemFactory.newImgIcon("refresh.png").getSrc());
		btnRefresh.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				ServiceUtil.security().refreshEcmMenu(Session.get().getSid(), new AsyncCallback<Void>() {
					@Override
					public void onSuccess(Void result) {
						grid2.setData(new ListGridRecord[]{});
						executeFetchIndexId();
					}
					@Override
					public void onFailure(Throwable caught) {
						Log.serverError(caught, false);
					}
				});
			}
		});
		
		VLayout formVL = new VLayout(10);
		formVL.setHeight100();    	
    	formVL.addMembers(grid3, btnSave);
    	
    	VLayout gridFormVL = new VLayout(10);
    	gridFormVL.setHeight100();  
    	gridFormVL.setWidth("50%");
    	gridFormVL.addMembers(grid2, btnRefresh);
    	
		HLayout gridHL = new HLayout(10);
		gridHL.setWidth100();    	
		gridHL.setHeight100();  
		gridHL.addMembers(gridFormVL, formVL);
				
		return gridHL;
	}
	
	/**
	 * Xvarm 데이터 가져오기
	 */
	private void setDataOnGeneralSettings(){
		ServiceUtil.system().loadECMSettings(Session.get().getSid(), new AsyncCallbackWithStatus<SParameter[]>() {
			@Override
			public String getSuccessMessage() {
				return I18N.message("client.searchComplete");
			}
			@Override
			public String getProcessMessage() {
				return I18N.message("client.searchRequest");
			}
			@Override
			public void onSuccessEvent(SParameter[] result) {
				for(int i=0; i<result.length; i++){
					ListGridRecord record=new ListGridRecord();
					record.setAttribute("nameView",  I18N.message(result[i].getName()));
					record.setAttribute("name", result[i].getName());
					record.setAttribute("value",result[i].getValue());
					grid1.addData(record);
				}
			}
			@Override
			public void onFailureEvent(Throwable caught) {
				Log.serverError(caught, false);
				SCM.warn(caught);
			}
		});
	}
	
	/**
	 * Index Id 데이터 가져오기
	 */
	private void executeFetchIndexId()	{				
		Log.debug("[ EcmSettingsPanel executeFetchIndexId ]");
		
		ServiceUtil.security().listEcmMenu(Session.get().getSid(), new AsyncCallbackWithStatus<List<SAdminMenu>>() {
			@Override
			public String getSuccessMessage() {
				return I18N.message("client.searchComplete");
			}
			@Override
			public String getProcessMessage() {
				return I18N.message("client.searchRequest");
			}
			@Override
			public void onSuccessEvent(List<SAdminMenu> result) {
				for (int j = 0; j < result.size(); j++) {
					ListGridRecord record=new ListGridRecord();
					record.setAttribute("indexId", result.get(j).getId());
					record.setAttribute("securityRef", changeArrayToString(result.get(j).getSecurityRefs(),","));
					record.setAttribute("indexName", result.get(j).getTitle());
					grid2.addData(record);
				}
				
				if (result.size() > 0) {
					grid2.selectSingleRecord(0);
					recordClickedProcess(grid2.getRecord(0));
				}
				Log.debug("EcmSettingsPanel executeFetchIndexId ] result.size()["+result.size() +"]");
			}
			@Override
			public void onFailureEvent(Throwable caught) {
				Log.serverError(caught, false);
				SCM.warn(caught);
			}
		});
	}
	
	/**
	 * Record Click Event Handler
	 * @param record
	 */
	private void recordClickedProcess(Record record) {    		
    		grid3.deselectAllRecords();
	    	if(!"".equals(record.getAttributeAsString("securityRef"))){
	    		String [] chkFilterIds = record.getAttributeAsString("securityRef").split(",");
	        	for(int i=0; i< chkFilterIds.length; i++){
		        	RecordList rclist  = grid3.getDataAsRecordList();
		    		Record[] rc = rclist.findAll("id", chkFilterIds[i]);
		    		grid3.selectRecords(rc);
	        		}
	    	}
	}
	
	/**
	 * Security Profile 데이터 가져오기
	 */
	private void executeFetchSecurityProfile()	{				
		Log.debug("[ EcmSettingsPanel executeFetchSecurityProfile ]");
		
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
			public void onSuccessEvent(List<SSecurityProfile> result) {
				for (int j = 0; j < result.size(); j++) {
					if(result.get(j).getId() > 100 || result.get(j).getId() == 0){
						ListGridRecord record=new ListGridRecord();
						record.setAttribute("id", result.get(j).getId());
						record.setAttribute("name", result.get(j).getName());
						grid3.addData(record);
					}
				}
				
				Log.debug("EcmSettingsPanel executeFetchSecurityProfile ] result.size()["+result.size() +"]");
			}
			@Override
			public void onFailureEvent(Throwable caught) {
				Log.serverError(caught, false);
				SCM.warn(caught);
			}
		});
	}
		
	/**
	 * ecm 데이터 저장
	 */
	private void executeUpdateEcmContext(){
//    	ecm.ip					:	10.1.61.6,10.1.61.6
//    	ecm.port				:	2102,2102
//    	ecm.username		:	SUPER,SUPER
//    	ecm.password		:	SUPER,SUPER
//    	ecm.gateway		:	XVARM_MAIN
//    	ecm.db					:	oracle
//    	ecm.viewer.ur		:	/frontend/viewer/viewersat.jsp?url=param_url&filename=param_filename
		grid1.saveAllEdits(); 
		RecordList rclist = grid1.getDataAsRecordList();
		
		int rccnt = rclist.getLength();
		
		SParameter[] parameters = new SParameter[rccnt];
		
		if(rccnt !=0){//그리드 데이터 있을때	//기존거
			for(int i=0; i< rccnt; i++){
				Record rc = rclist.get(i);	
				parameters[i] = new SParameter(rc.getAttribute("name"), rc.getAttribute("value"));
			}
		}
		
		ServiceUtil.system().saveSettings(Session.get().getSid(), parameters, new AsyncCallbackWithStatus<Void>() {
			@Override
			public String getSuccessMessage() {
				return I18N.message("operationcompleted");
			}
			@Override
			public String getProcessMessage() {
				return null;
			}
			@Override
			public void onFailureEvent(Throwable caught) {
				SCM.warn(caught);
			}
			@Override
			public void onSuccessEvent(Void result) {
				Log.debug("[ EcmSettingsPanel executeUpdateEcmContext ] onSuccess.");
				SC.say(I18N.message("operationcompleted"));			
			}
		});
		
	}
	
	/**
	 * index Id 와 Security Profile 매칭 저장
	 */
	private void executeUpdateSecurityProfile() {
		Log.debug("[ EcmSettingsPanel executeUpdateSecurityProfile ]");
		
		SAdminMenu sAdminMenu = new SAdminMenu();
		Record[] rc1 = grid2.getSelectedRecords();		
		sAdminMenu.setId(Long.parseLong(rc1[0].getAttribute("indexId")));
		
		ListGridRecord[] rclist = grid3.getSelectedRecords();
		int refCnt = rclist.length;
		if(refCnt>0){
			long[] securityRefs = new long[refCnt];
			for(int i=0; i<refCnt; i++){
				securityRefs[i] = Long.parseLong(rclist[i].getAttribute("id"));
			}
			sAdminMenu.setSecurityRefs(securityRefs);
		}		
		
		ServiceUtil.security().updateSecurityRef(Session.get().getSid(), sAdminMenu, new AsyncCallbackWithStatus<SAdminMenu>() {
			@Override
			public String getSuccessMessage() {
				return I18N.message("operationcompleted");
			}
			@Override
			public String getProcessMessage() {
				return null;
			}
			@Override
			public void onFailureEvent(Throwable caught) {
				SCM.warn(caught);
			}
			@Override
			public void onSuccessEvent(SAdminMenu result) {		
				ListGridRecord selectedRecord = grid2.getSelectedRecord();
				selectedRecord.setAttribute("securityRef", changeArrayToString(result.getSecurityRefs(),","));
				recordClickedProcess(selectedRecord);
				Log.debug("[ EcmSettingsPanel executeUpdateSecurityProfile ] onSuccess. id["+result.getId()+"]");
				SC.say(I18N.message("operationcompleted"));			
			}
		});
	}	
	
	/**
	 * Refresh 버튼 클릭 이벤트 옵져버
	 */
	@Override
	public void onRefreshBtnClicked(ClickEvent event) {
		refresh();
	}
	
	/**
	 * long[] 타입을 delimiter로 구분한 String 으로 반환
	 */
	private String changeArrayToString(long[] target, String delimiter) {
		String targetStr = "";
		if(target.length>0) {
			for(int t=0; t<target.length; t++) {
				if(t>0) targetStr += delimiter;	 
				targetStr += target[t];
			}
		} 
		return targetStr;
	}
	
}

