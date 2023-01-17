package com.speno.xedm.gui.frontend.client.admin.system;

import java.util.Date;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.KeyPressEvent;
import com.smartgwt.client.widgets.form.fields.events.KeyPressHandler;
import com.smartgwt.client.widgets.form.validator.RegExpValidator;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.RemoveRecordClickEvent;
import com.smartgwt.client.widgets.grid.events.RemoveRecordClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.speno.xedm.core.service.serials.SSessionSimple;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.Session;
import com.speno.xedm.gui.common.client.util.AsyncCallbackWithStatus;
import com.speno.xedm.gui.common.client.util.DateCellFormatter;
import com.speno.xedm.gui.common.client.util.LengthValidator;
import com.speno.xedm.gui.common.client.util.SCM;
import com.speno.xedm.gui.common.client.util.ServiceUtil;

/**
 * Sessions Panel
 * @author 박상기
 * @since 1.0
 */
public class SessionsPanel extends VLayout {
	private static SessionsPanel instance = null;
	
	private HLayout refreshHL;
	private DynamicForm searchForm;
	private TextItem maxItem;
	private Button refreshBtn;
	private Label lastUpdateLabel; 
	private ListGrid grid;
	
	/**
	 * 기 생성된 instance를 반환함. instance가 없을 경우 생성 후 반환함
	 * @return
	 */
	public static SessionsPanel get() {
		if (instance == null) {
			instance = new SessionsPanel();
		}
		return instance;
	}

	public SessionsPanel() {
		setWidth100();
		setMembersMargin(10);
		
		RegExpValidator regExpIdValidator = new RegExpValidator();   
		regExpIdValidator.setExpression("[0-9]");
		
		maxItem = new TextItem("max", I18N.message("max"));
		maxItem.setWrapTitle(false);
		maxItem.setRequired(true);   
//		maxItem.setValidators(regExpIdValidator );
		maxItem.setDefaultValue(20);
		maxItem.setValue(20);
//		maxItem.setLength(9);
		maxItem.setValidators(regExpIdValidator, new LengthValidator(maxItem, 9));
		maxItem.setKeyPressFilter("[0-9.]");
		maxItem.addKeyPressHandler(new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent event) {				
				if("Enter".equals(event.getKeyName())) {
					if(searchForm.validate()) {
						executeFetch();
					}
				}	
			}
		});
		
		searchForm = new DynamicForm();
		searchForm.setAutoWidth();
		searchForm.setAlign(Alignment.LEFT);
		searchForm.setItems(maxItem);
		
		refreshBtn = new Button(I18N.message("refresh"));
		refreshBtn.setWidth(80);
		refreshBtn.setIcon("[SKIN]/actions/refresh.png");   
		refreshBtn.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
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
		
		grid = new ListGrid();
		grid.setWidth100();
		grid.setHeight100();		
		grid.setShowAllRecords(true);
		
		grid.setEmptyMessage(I18N.message("notitemstoshow"));		
		grid.setCanFreezeFields(true);
		grid.setCanRemoveRecords(true);
		grid.setSelectionType(SelectionStyle.SINGLE);
		grid.invalidateCache();
		
		ListGridField sidField = new ListGridField("sid");		
		ListGridField systemIdField = new ListGridField("systemId", I18N.message("id"));
		
		ListGridField userIdField = new ListGridField("userId", I18N.message("userid"));
		ListGridField userNameField = new ListGridField("userName", I18N.message("uusername"));
		ListGridField ipField = new ListGridField("ip", I18N.message("address"));
		ListGridField loggedInDateField = new ListGridField("loggedInDate", I18N.message("connectiontime"), 130);
		
		sidField.setHidden(true);
		systemIdField.setHidden(true);
		
		loggedInDateField.setAlign(Alignment.CENTER);
		loggedInDateField.setType(ListGridFieldType.DATE);
		loggedInDateField.setCellFormatter(new DateCellFormatter(I18N.message("yyyy/MM/dd HH:mm:ss")));
		
		grid.setFields(sidField, systemIdField, userIdField, userNameField, ipField, loggedInDateField);
		
		//record 삭제 event handler 정의--------------------------------------------------------------
		grid.addRemoveRecordClickHandler(new RemoveRecordClickHandler() {
			@Override
			public void onRemoveRecordClick(RemoveRecordClickEvent event) {
				final ListGridRecord record = grid.getRecord( event.getRowNum());
				SC.confirm(I18N.message("confirmkill"),  new BooleanCallback() {
					@Override
					public void execute(Boolean value) {
						if(value != null && value) {
							executeRemove(record.getAttributeAsString("sid"));
						}
					}
				});
				event.cancel();
			}
		});
		addMember(grid);
        
        executeFetch();
	}
	
	private void executeFetch()	{				
		GWT.log("[ SessionsPanel executeFetch ]", null);
		ServiceUtil.system().listSession(Session.get().getSid(), Integer.parseInt(maxItem.getValueAsString()), new AsyncCallbackWithStatus<List<SSessionSimple>>() {
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
				SCM.warn(caught);
			}
			@Override
			public void onSuccessEvent(List<SSessionSimple> result) {				
				grid.setData(new ListGridRecord[0]); //그리드 초기화					
				for (int j = 0; j < result.size(); j++) {					
					ListGridRecord record=new ListGridRecord();
					record.setAttribute("sid", result.get(j).getSid());
					record.setAttribute("systemId", result.get(j).getUserId());
					
					record.setAttribute("userId", result.get(j).getUserName());
					record.setAttribute("userName", result.get(j).getName());
					record.setAttribute("ip", result.get(j).getIp());										
					record.setAttribute("loggedInDate", result.get(j).getLoggedInDate());
					grid.addData(record);
				}	
				
				if (result.size() > 0) {
					grid.selectSingleRecord(0);
				}
				
				lastUpdateLabel.setContents("<b>" + I18N.message("lastupdate") + ": " + new Date() + "</b>");
				
				GWT.log("SessionsPanel executeFetch ] result.size()["+result.size()+"]", null);
			}
		});
	}
	
	private void executeRemove(final String targetSid)	{
		GWT.log("[ SessionsPanel executeRemove ] targetSid["+targetSid+"]", null);
		
		ServiceUtil.system().killSession(Session.get().getSid(), targetSid, new AsyncCallbackWithStatus<Void>() {
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
				SCM.warn(caught);
			}
			@Override
			public void onSuccessEvent(Void result) {
				GWT.log("[ SessionsPanel executeRemove ] onSuccess. targetSid["+targetSid+"]", null);
								
				grid.removeSelectedData();
				//SC.say(I18N.message("operationcompleted"));
			}
		});
	}
}
