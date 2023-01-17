package com.speno.xedm.gui.frontend.client.admin.system.audit;

import java.io.Serializable;
import java.util.LinkedHashMap;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.RecordList;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.speno.xedm.core.service.serials.SAudit;
import com.speno.xedm.core.service.serials.SParameter;
import com.speno.xedm.gui.common.client.Constants;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.RecordObserver;
import com.speno.xedm.gui.common.client.RefreshObserver;
import com.speno.xedm.gui.common.client.Session;
import com.speno.xedm.gui.common.client.log.Log;
import com.speno.xedm.gui.common.client.util.AsyncCallbackWithStatus;
import com.speno.xedm.gui.common.client.util.SCM;
import com.speno.xedm.gui.common.client.util.ServiceUtil;
import com.speno.xedm.gui.common.client.util.Util;

/**
 * Management Panel
 * 
 * @author 남윤성
 * @since 1.0
 */
public class ManagementPanel extends VLayout implements RefreshObserver, RecordObserver{	
	private static ManagementPanel instance;
	
	private HLayout refreshHL;
	private Button refreshBtn;
	private Label lastUpdateLabel; 
	private HLayout mainHL;	
	private SelectItem auditItem = new SelectItem("audit",I18N.message("audit"));
	private DefaultGridPanel defaultGridPanel;
	private DocumentTypesGridPanel documentTypeGridPanel;
	private GroupsGridPanel groupsGridPanel;
	private ListGrid grid1;
	private CheckBox cb1;
	private CheckBox cb2;
	/**
	 * 기 생성된 instance를 반환함. instance가 없을 경우 생성 후 반환함
	 * @return
	 */
	public static ManagementPanel get() {
		if (instance == null) {
			instance = new ManagementPanel();
		}
		return instance;
	}

	/**
	 * Position 패널 생성
	 */
	public ManagementPanel() {		
		setWidth100();
		setMembersMargin(10);
		setPadding(Constants.PADDING_DEFAULT);
		
		
        LinkedHashMap<String, String> valueMap = new LinkedHashMap<String, String>();  
        valueMap.put("true", I18N.message("enabled"));  
        valueMap.put("false", I18N.message("disabled"));  
        auditItem.setWrapTitle(false);
        auditItem.setValueMap(valueMap);  
        auditItem.setDefaultValue("false");
        
        executeGetOptionsAndSet();
		
		auditItem.addChangedHandler(new ChangedHandler() {
			@Override
			public void onChanged(ChangedEvent event) {
				//이벤트 ON/OFF
				SParameter[] parameters = new SParameter[1];
				SParameter param = new SParameter("settings.history.enabled", (String)event.getValue());
				parameters[0] = param;
				ServiceUtil.system().saveSettings(Session.get().getSid(),parameters, new AsyncCallbackWithStatus<Void>() {
					@Override
					public String getSuccessMessage() {
						return I18N.message("client.searchComplete");
					}
					@Override
					public String getProcessMessage() {
						return null;
					}
					@Override
					public void onSuccessEvent(Void result) {
						executeGetOptionsAndSet();
						
					}
					@Override
					public void onFailureEvent(Throwable caught) {
						Log.serverError(caught, false);
						SCM.warn(caught);
						
					}
				});
			}
		});
		
		DynamicForm searchForm = new DynamicForm();
		searchForm.setAutoWidth();
		searchForm.setAlign(Alignment.LEFT);
		searchForm.setItems(auditItem);
		
		refreshBtn = new Button(I18N.message("refresh"));
		refreshBtn.setWidth(80);
		refreshBtn.setIcon("[SKIN]/actions/refresh.png");   
		refreshBtn.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
            	createMainPanel(true); 
            	executeFetch();
            }
        });
		
		lastUpdateLabel = new Label();
		lastUpdateLabel.setHeight(refreshBtn.getHeight());
		lastUpdateLabel.setWidth100();
		lastUpdateLabel.setAlign(Alignment.RIGHT);
		
		refreshHL = new HLayout(10);
		refreshHL.setWidth100();
		refreshHL.setAutoHeight();
		refreshHL.setAlign(Alignment.RIGHT);
		refreshHL.addMembers(searchForm, lastUpdateLabel, refreshBtn);		
		addMember(refreshHL);
		
		createMainPanel(false); //Main패널 생성
		executeFetch();
		
	}
	
	/**
	 * Main패널 생성
	 */
	private void createMainPanel(boolean isRefresh) {		
		if(isRefresh) {
			removeMember(mainHL);
		}
		
		defaultGridPanel = createDefaultPN(isRefresh);
		defaultGridPanel.setWidth("32%");
		defaultGridPanel.setHeight100();
		
		VLayout vLayout = new VLayout();  
        vLayout.setWidth("68%"); 
        vLayout.setHeight100();
        
        documentTypeGridPanel = createDocumentTypesPN(isRefresh);
        documentTypeGridPanel.setHeight("50%");
        groupsGridPanel = createGroupsPN(isRefresh);
        groupsGridPanel.setHeight("50%");
		
		vLayout.addMember(documentTypeGridPanel);
		vLayout.addMember(groupsGridPanel);
		
		mainHL = new HLayout(10);
		mainHL.setHeight100();
		mainHL.setMembers(defaultGridPanel,vLayout);
		addMember(mainHL);
	}
	
	/**
	 * Refresh
	 */
	private void refresh() {
		createMainPanel(true);
		executeFetch();
	}
	
	/**
	 * Refresh 여부에 따른 Default Panel 생성
	 * @param isRefresh
	 * @return
	 */
	private DefaultGridPanel createDefaultPN(boolean isRefresh) {
		return isRefresh ? new DefaultGridPanel(isRefresh) : DefaultGridPanel.get(isRefresh);
	}
	
	/**
	 * Refresh 여부에 따른 Document Types Panel 생성
	 * @param isRefresh
	 * @return
	 */
	private DocumentTypesGridPanel createDocumentTypesPN(boolean isRefresh) {
		return isRefresh ? new DocumentTypesGridPanel(isRefresh) : DocumentTypesGridPanel.get(isRefresh);
	}
	
	/**
	 * Refresh 여부에 따른 Groups Panel 생성
	 * @param isRefresh
	 * @return
	 */
	private GroupsGridPanel createGroupsPN(boolean isRefresh) {
		return isRefresh ? new GroupsGridPanel(isRefresh) : GroupsGridPanel.get(isRefresh);
	}
	
	/**
	 * Audit select
	 */
	private void executeGetOptionsAndSet() {				
		ServiceUtil.system().isEnabled(Session.get().getSid(), new AsyncCallbackWithStatus<Boolean>() {
			@Override
			public String getSuccessMessage() {
				return I18N.message("operationcompleted");
			}
			@Override
			public String getProcessMessage() {
				return null;
			}
			@Override
			public void onSuccessEvent(Boolean result) {
				auditItem.setDefaultValue(result?"true":"false");
				refresh();
			}
			@Override
			public void onFailureEvent(Throwable caught) {
				Log.serverError(caught, false);
				SCM.warn(caught);
			}
		});
	}
	/**
	 * Refresh 버튼 클릭 이벤트 옵져버 핸들러
	 */
	@Override
	public void onRefreshBtnClicked(ClickEvent event) {
		refresh();
	}
	
	@Override
	public void onRecordSelected(Serializable id, Serializable parentId) {
		Log.debug("[ ManagementPanel onRecordSelected ] id["+id+"], parentId["+parentId+"]");	
	}

	@Override
	public void onRecordSelected(final Record record) {
		long folderId = Util.getAslong(record.getAttributeAsString("id"));
		String profileId = record.getAttributeAsString("profileId");
		long parentId = Util.getAslong(record.getAttributeAsString("parentId"));
		
		Log.debug("[ ManagementPanel onRecordSelected ] folderId["+folderId+"], profileId["+profileId+"], parentId["+parentId+"]");
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
	
	private void executeFetch()	{				
		Log.debug("[ ManagementPanel executeFetch ]");
		ServiceUtil.system().listAllActions(Session.get().getSid(), new AsyncCallback<String[]>() {
			
			@Override
			public void onFailure(Throwable caught) {
				SCM.warn(caught);
			}
			@Override
			public void onSuccess(String[] result) {
				cb1 = defaultGridPanel.getCb1();
				cb2 = defaultGridPanel.getCb2();
				grid1 = defaultGridPanel.getGrid();
				ListGrid grid2 = documentTypeGridPanel.getGrid();
				ListGrid grid3 = groupsGridPanel.getGrid();
				
				grid1.setData(new ListGridRecord[0]); //defaultGridPanel 그리드 초기화	
				for (int j = 0; j < result.length; j++) {					
					ListGridRecord record=new ListGridRecord();
					record.setAttribute("event", result[j]);
					record.setAttribute("eventname", I18N.message(result[j]));
					System.out.println(result[j]);
					grid1.addData(record);
				}	
				
				grid2.setData(new ListGridRecord[0]); //documentTypesPanel 그리드 초기화
				
				for (int j = 0; j < result.length; j++) {					
					ListGridRecord record=new ListGridRecord();
					record.setAttribute("event", result[j]);
					record.setAttribute("eventname", I18N.message(result[j]));
					grid2.addData(record);
				}	
				
				grid3.setData(new ListGridRecord[0]); //groupsGridPanel 그리드 초기화	
				for (int j = 0; j < result.length; j++) {					
					ListGridRecord record=new ListGridRecord();
					record.setAttribute("event", result[j]);
					record.setAttribute("eventname", I18N.message(result[j]));
					grid3.addData(record);
				}	
				
				SAudit audit = new SAudit();
				audit.setType(0);
				
				//defaultGrid event 세팅
				ServiceUtil.system().listActions(Session.get().getSid(), audit, new AsyncCallback<SAudit>() {
					@Override
					public void onFailure(Throwable caught) {
						SCM.warn(caught);
					}
					@Override
					public void onSuccess(SAudit result) {
						grid1.deselectAllRecords();
				    	
						if(result != null){
							cb1.setValue(1==result.getHasFile()?true:false);
							cb2.setValue(1==result.getHasTable()?true:false);
							RecordList rclist  = grid1.getDataAsRecordList();
							for (int j = 0; j < result.getEvents().length; j++) {	
					    		Record[] rc = rclist.findAll("event", result.getEvents()[j]);
					    		grid1.selectRecords(rc);
							}
						}
					}
				});	
				
				Log.debug("ManagementPanel executeFetch ] result.size()["+result.length+"]");
			}
		});
	}

	@Override
	public boolean isIDLong() {
		return true;
	}
	
}