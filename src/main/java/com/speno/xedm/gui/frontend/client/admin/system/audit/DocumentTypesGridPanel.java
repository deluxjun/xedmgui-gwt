package com.speno.xedm.gui.frontend.client.admin.system.audit;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.RecordList;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.SelectionAppearance;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.CellClickEvent;
import com.smartgwt.client.widgets.grid.events.CellClickHandler;
import com.smartgwt.client.widgets.grid.events.CellDoubleClickEvent;
import com.smartgwt.client.widgets.grid.events.CellDoubleClickHandler;
import com.smartgwt.client.widgets.grid.events.HeaderClickEvent;
import com.smartgwt.client.widgets.grid.events.HeaderClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.speno.xedm.core.service.serials.SAudit;
import com.speno.xedm.core.service.serials.SDocType;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.Session;
import com.speno.xedm.gui.common.client.log.Log;
import com.speno.xedm.gui.common.client.util.AsyncCallbackWithStatus;
import com.speno.xedm.gui.common.client.util.ItemFactory;
import com.speno.xedm.gui.common.client.util.SCM;
import com.speno.xedm.gui.common.client.util.ServiceUtil;

/**
 * DocumentTypesGridPanel
 * @author 남윤성
 * @since 1.0
 */
public class DocumentTypesGridPanel extends VLayout {
	private static DocumentTypesGridPanel instance = null;
	
	private HLayout mainHL;
	private VLayout gridHL;
	private HLayout cbHL;
	private ListGrid grid1;
	private ListGrid grid2;
	private CheckBox cb1;
	private CheckBox cb2;
	private Boolean isDbData = false;
	private ButtonItem applyButton;
	private ButtonItem deleteButton;
	private boolean gridChk = false;
	/**
	 * 기 생성된 instance를 반환함. instance가 없을 경우 생성 후 반환함
	 * @return
	 */
	public static DocumentTypesGridPanel get(boolean isRefresh) {
		if (instance == null) {
			instance = new DocumentTypesGridPanel(isRefresh);
		}
		return instance;
	}

	public DocumentTypesGridPanel(boolean isRefresh) {
		if(isRefresh && mainHL != null) {
			removeMember(mainHL);
		}
		setWidth100();
		setMembersMargin(3);
		
		Label dummyLable = new Label();
		dummyLable.setHeight(1);
		addMember(dummyLable);
		
		Label subTitleLable = new Label();
		subTitleLable.setAutoHeight();   
        subTitleLable.setAlign(Alignment.LEFT);   
        subTitleLable.setValign(VerticalAlignment.CENTER);
        subTitleLable.setStyleName("subTitle");
        subTitleLable.setContents(I18N.message("documenttype"));
        addMember(subTitleLable);
		
        //grid1
        grid1 = new ListGrid();
		grid1.setWidth100();
		grid1.setHeight(240);		
		grid1.setShowAllRecords(true);
		
		grid1.setEmptyMessage(I18N.message("notitemstoshow"));		
		grid1.setCanFreezeFields(true);
		grid1.setSelectionType(SelectionStyle.SINGLE);
		grid1.invalidateCache();
		
		ListGridField typeIdField = new ListGridField("id", I18N.message("typeid"));
		typeIdField.setHidden(true);
		ListGridField typeNmField = new ListGridField("name", I18N.message("typename"));
		ListGridField descField = new ListGridField("description", I18N.message("description"));
		ListGridField cnt = new ListGridField("cnt", I18N.message("second.no"));
		cnt.setHidden(true);
		
		grid1.setFields(typeIdField, typeNmField, descField,cnt);
		
		//DOCUMENT TYPES의 이벤트 가져오기
		grid1.addCellClickHandler(new CellClickHandler() {
			@Override
			public void onCellClick(CellClickEvent event) {
				if (grid1.getSelectedRecords() == null)
					return;
		
				ListGridRecord record = event.getRecord();
				if (record != null){
					try {
						SAudit audit = new SAudit();
						audit.setType(1);
						audit.setTargetId(record.getAttributeAsString("id"));
						executeFetch3(audit);
					} catch (NumberFormatException e) {
						e.printStackTrace();
					}
				}
			}
		});
		
		//chk
        cb1 = new CheckBox(I18N.message("file"));
        cb1.setHeight("10");
        cb2 = new CheckBox(I18N.message("second.table"));
        cb2.setHeight("10");

        cb1.addClickHandler(new com.google.gwt.event.dom.client.ClickHandler()
		{
			@Override
			public void onClick(com.google.gwt.event.dom.client.ClickEvent event) {
				chkButton2();
			}
		});
        
        cb2.addClickHandler(new com.google.gwt.event.dom.client.ClickHandler()
		{
			@Override
			public void onClick(com.google.gwt.event.dom.client.ClickEvent event) {
				chkButton2();
			}
		});
        
        cbHL = new HLayout(10);
        cbHL.setWidth100();
        cbHL.setAutoHeight();
        cbHL.setAlign(Alignment.LEFT);
        cbHL.addMember(cb1);
        cbHL.addMember(cb2);
        
        //grid2
		grid2 = new ListGrid();
		grid2.setWidth100(); 
		grid2.setHeight(220);		
		grid2.setShowAllRecords(true);
		
		grid2.setEmptyMessage(I18N.message("notitemstoshow"));		
		grid2.setCanFreezeFields(true);
		grid2.setSelectionAppearance(SelectionAppearance.CHECKBOX); 
		grid2.setSelectionType(SelectionStyle.SIMPLE);
		
		grid2.addCellClickHandler(new CellClickHandler() {
			@Override
			public void onCellClick(CellClickEvent event) {				        		
				int colNum = event.getColNum();  
				if(0 != colNum){
					Record r = event.getRecord();
	            	if (!grid2.isSelected((ListGridRecord)r))
	            		grid2.selectRecord(r);
	            	else
	            		grid2.deselectRecord(r);
				}
				chkButton2();
				if(grid2.getRecords().length == grid2.getSelectedRecords().length){
					gridChk = true;
				}else{
					gridChk = false;
				}
			}
		});
		
		
		grid2.addCellDoubleClickHandler(new CellDoubleClickHandler(){

			@Override
			public void onCellDoubleClick(CellDoubleClickEvent event) {
				int colNum = event.getColNum();  
				if(0 != colNum){
					Record r = event.getRecord();
	            	if (!grid2.isSelected((ListGridRecord)r))
	            		grid2.selectRecord(r);
	            	else
	            		grid2.deselectRecord(r);
				}
				chkButton2();
				if(grid2.getRecords().length == grid2.getSelectedRecords().length){
					gridChk = true;
				}else{
					gridChk = false;
				}
			}
			
		});
		
		grid2.addHeaderClickHandler(new HeaderClickHandler(){

			@Override
			public void onHeaderClick(HeaderClickEvent event) {
				if (event.getFieldNum()!=0)
				{
					return;
				}
				
				gridChk = !gridChk;
				chkButton1();
				
			}
		});
		
		ListGridField event = new ListGridField("event", I18N.message("event"));
		event.setWidth("*");
		event.setAlign(Alignment.LEFT);
		event.setHidden(true);
		
		ListGridField eventname = new ListGridField("eventname", I18N.message("event"));
		eventname.setWidth("*");
		eventname.setAlign(Alignment.LEFT);
		
		grid2.setFields(event,eventname);
		
		gridHL = new VLayout();
		gridHL.setWidth100();
		gridHL.setAutoHeight();
		gridHL.setAlign(Alignment.LEFT);
		gridHL.addMembers(cbHL,grid2,createDBStatiActionForm());
		
		//mainHL = grid1+gridHL
		mainHL = new HLayout(10);
        mainHL.setWidth100();
        mainHL.setAutoHeight();
        mainHL.setAlign(Alignment.LEFT);
        mainHL.addMember(grid1);
        mainHL.addMember(gridHL);
        
        addMember(mainHL);
		executeFetch1();
	}
	
	/**
	 * 버튼 활성,비활성
	 */
	public void chkButton1(){
		if((cb1.getValue()|| cb2.getValue())&&!gridChk){//콤보박스 1개라도 체크 되있고 event 없으면
			applyButton.setDisabled(true);
		}else if((!cb1.getValue()&& !cb2.getValue())&&gridChk){//콤보박스가 다체크 안되있고 event 가 있으면
			applyButton.setDisabled(true);
		}else if((!cb1.getValue()&& !cb2.getValue())&&!gridChk){//콤보박스가 다 체크 안되 있을때 
			applyButton.setDisabled(true);
		}else{
			applyButton.setDisabled(false);
		}
		
		if(isDbData){
			deleteButton.setDisabled(false);
		}else{
			deleteButton.setDisabled(true);
		}		
	}
	
	/**
	 * 버튼 활성,비활성
	 */ 
	public void chkButton2(){
		ListGridRecord[] rclist  = grid2.getSelectedRecords();
		if((cb1.getValue()|| cb2.getValue())&&rclist.length ==0){//콤보박스 1개라도 체크 되있고 event 없으면
			applyButton.setDisabled(true);
		}else if((!cb1.getValue()&& !cb2.getValue())&&rclist.length >0){//콤보박스가 다체크 안되있고 event 가 있으면
			applyButton.setDisabled(true);
		}else if((!cb1.getValue()&& !cb2.getValue())&&rclist.length ==0){//콤보박스가 다 체크 안되 있을때 
			applyButton.setDisabled(true);
		}else{
			applyButton.setDisabled(false);
		}
		
		if(isDbData){
			deleteButton.setDisabled(false);
		}else{
			deleteButton.setDisabled(true);
		}	
	}
	
	/**
	 * grid2 가져오기
	 */
	public ListGrid getGrid() {
		return grid2;
	}
	
	
	public void goExecuteFetch3(){
		SAudit audit = new SAudit();
		audit.setType(1);		
		RecordList rclist  = grid1.getDataAsRecordList();
		Record[] rc = rclist.findAll("cnt", "0");
		audit.setTargetId(rc[0].getAttributeAsString("id"));
		executeFetch3(audit);
		
	}
	
	/**
	 * Document Types 가져오기
	 */
	private void executeFetch1()	{				
		Log.debug("[ DocumentTypesGridPanel executeFetch ]");
		ServiceUtil.documentcode().listDocTypeLikeName(Session.get().getSid(), "", false, new AsyncCallback<List<SDocType>>() {
			@Override
			public void onFailure(Throwable caught) {
				SCM.warn(caught);
			}
			@Override
			public void onSuccess(List<SDocType> result) {
				grid1.setData(new ListGridRecord[0]); //그리드 초기화					
				for (int j = 0; j < result.size(); j++) {					
					ListGridRecord record=new ListGridRecord();
					record.setAttribute("id", result.get(j).getId());
					record.setAttribute("name", result.get(j).getName());
					record.setAttribute("description", result.get(j).getDescription());	
					record.setAttribute("cnt", j);	
					grid1.addData(record);
				}	
				
				if (result.size() > 0) {
					grid1.selectSingleRecord(0);
					
				}
				
				goExecuteFetch3();
				
				Log.debug("DocumentTypesGridPanel executeFetch ] result.size()["+result.size()+"]");
				
			}
		});
	}
	
	/**
	 * document type 의 event 가져오기
	 */
	private void executeFetch3(SAudit bean)	{				
		Log.debug("[ DocumentTypesGridPanel executeFetch ]");
		
		ServiceUtil.system().listActions(Session.get().getSid(), bean, new AsyncCallbackWithStatus<SAudit>() {

			@Override
			public String getSuccessMessage() {
				return I18N.message("client.searchComplete");
			}

			@Override
			public String getProcessMessage() {
				return I18N.message("client.searchRequest");
			}

			@Override
			public void onSuccessEvent(SAudit result) {
				grid2.deselectAllRecords();
				if(result != null){
					cb1.setValue(1==result.getHasFile()?true:false);
					cb2.setValue(1==result.getHasTable()?true:false);
					
					RecordList rclist  = grid2.getDataAsRecordList();
					for (int j = 0; j < result.getEvents().length; j++) {	
			    		Record[] rc = rclist.findAll("event", result.getEvents()[j]);
			    		grid2.selectRecords(rc);
					}
					isDbData = true;
				}else{
					cb1.setValue(false);
					cb2.setValue(false);
					isDbData = false;
				}
				chkButton2();
			}

			@Override
			public void onFailureEvent(Throwable caught) {
				Log.serverError(caught, false);
				SCM.warn(caught);
			}
		});
	}
	
	/**
	 * APPLY 적용 버튼, DB저장
	 * @return
	 */
	private DynamicForm createDBStatiActionForm() {	
		applyButton = new ButtonItem();
		applyButton.setTitle(I18N.message("apply"));
		applyButton.setIcon("[SKIN]/actions/accept.png");
		applyButton.setWidth(80);
		applyButton.setStartRow(false);
		applyButton.setEndRow(false);
		applyButton.addClickHandler(new com.smartgwt.client.widgets.form.fields.events.ClickHandler() {
			@Override
			public void onClick(com.smartgwt.client.widgets.form.fields.events.ClickEvent event) {
				Record[] rc = grid2.getSelectedRecords();
				Log.debug("rclist.length..:"+rc.length);
				
				if((cb1.getValue()|| cb2.getValue())&&rc.length ==0){
					SC.say(I18N.message("second.checkevent"));
					chkButton2();
					return;
				}
				
				SC.confirm(I18N.message("applyondb"),  new BooleanCallback() {
					@Override
					public void execute(Boolean value) {
						if(value != null && value) {
							
							Record[] rc = grid2.getSelectedRecords();
							
							if(cb1.getValue()&& cb2.getValue()&&rc == null){
								return;
							}
							
							String[] events = null;
							if(rc!= null){
								events = new String[rc.length];
								for (int i = 0; i < rc.length; i++) {
									
									events[i] = rc[i].getAttribute("event");
								}
							}
							
							SAudit audit = new SAudit();
							audit.setType(1);
							audit.setTargetId(grid1.getSelectedRecord().getAttributeAsString("id"));
							audit.setHasFile(cb1.getValue()?1:0);
							audit.setHasTable(cb2.getValue()?1:0);
							audit.setEvents(events);							
							
							ServiceUtil.system().applyActions(Session.get().getSid(), audit, new AsyncCallbackWithStatus<Void>() {
								@Override
								public String getSuccessMessage() {
									return I18N.message("operationcompleted");
								}
								@Override
								public String getProcessMessage() {
									return null;
								}
								@Override
								public void onSuccessEvent(Void result) {
									SC.say(I18N.message("operationcompleted"));
									
								}
								@Override
								public void onFailureEvent(Throwable caught) {
									Log.serverError(caught, false);
									SCM.warn(caught);
								}
							});
						}
					}
				});
			}
        });
		
		deleteButton = new ButtonItem();
		deleteButton.setTitle(I18N.message("delete"));
		deleteButton.setIcon("[SKIN]/actions/remove.png");
		deleteButton.setWidth(80);
		deleteButton.setStartRow(false);
		deleteButton.setEndRow(false);
		deleteButton.addClickHandler(new com.smartgwt.client.widgets.form.fields.events.ClickHandler() {
			@Override
			public void onClick(com.smartgwt.client.widgets.form.fields.events.ClickEvent event) {
				SC.confirm(I18N.message("second.deleteondb"),  new BooleanCallback() {
					@Override
					public void execute(Boolean value) {
						if(value != null && value) {						
							
							SAudit audit = new SAudit();
							audit.setType(1);
							audit.setTargetId(grid1.getSelectedRecord().getAttributeAsString("id"));
							
							ServiceUtil.system().delete(Session.get().getSid(), audit, new AsyncCallbackWithStatus<Boolean>() {								
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
									SC.say(I18N.message("operationcompleted"));
								}
								@Override
								public void onFailureEvent(Throwable caught) {
									Log.serverError(caught, false);
									SCM.warn(caught);
								}
							});
						}
					}
				});
			}
        });
		
		StaticTextItem dummy = ItemFactory.newStaticTextItem("dummy", "", "");
        dummy.setShowTitle(false);
        dummy.setWidth(30);
		
		DynamicForm dfButton = new DynamicForm();   
		dfButton.setWidth(80);   
        dfButton.setAlign(Alignment.LEFT);
        dfButton.setNumCols(3);
        dfButton.setColWidths("10", "10", "10");
        		
        dfButton.setItems(applyButton, deleteButton);
				
		return dfButton;
	}
}
