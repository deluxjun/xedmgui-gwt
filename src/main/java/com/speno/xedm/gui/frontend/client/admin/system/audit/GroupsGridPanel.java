package com.speno.xedm.gui.frontend.client.admin.system.audit;

import java.io.Serializable;

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
import com.speno.xedm.core.service.serials.SGroup;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.RecordObserver;
import com.speno.xedm.gui.common.client.Session;
import com.speno.xedm.gui.common.client.log.Log;
import com.speno.xedm.gui.common.client.util.AsyncCallbackWithStatus;
import com.speno.xedm.gui.common.client.util.SCM;
import com.speno.xedm.gui.common.client.util.ServiceUtil;
import com.speno.xedm.gui.frontend.client.folder.AdminGroupTree;

/**
 * GroupsGridPanel
 * @author 남윤성
 * @since 1.0
 */
public class GroupsGridPanel extends VLayout implements RecordObserver {
	private static GroupsGridPanel instance = null;
	
	private final String ROOT_NO = "0";
	
	private HLayout mainHL;
	private VLayout gridHL;
	private HLayout cb1HL;
	private HLayout cb2HL;
	private AdminGroupTree tree;
	private ListGrid grid2;
	private CheckBox cb1;
	private CheckBox cb2;
	private CheckBox cb3;
	private Boolean hasInherited = false;
	private ButtonItem applyButton;
	private boolean gridChk = false;
	
	
	
	/**
	 * 기 생성된 instance를 반환함. instance가 없을 경우 생성 후 반환함
	 * @return
	 */
	public static GroupsGridPanel get(boolean isRefresh) {
		if (instance == null) {
			instance = new GroupsGridPanel(isRefresh);
		}
		return instance;
	}

	public GroupsGridPanel(boolean isRefresh) {
		if(isRefresh && mainHL != null) {
			removeMember(mainHL);
		}
		setWidth100();
		setMembersMargin(10);
		
		Label subTitleLable = new Label();
		subTitleLable.setAutoHeight();   
        subTitleLable.setAlign(Alignment.LEFT);   
        subTitleLable.setValign(VerticalAlignment.CENTER);
        subTitleLable.setStyleName("subTitle");
        subTitleLable.setContents(I18N.message("group"));
        addMember(subTitleLable);
		
        //tree
        tree = new AdminGroupTree(this);
        tree.setWidth100();
        tree.setBorder("1px solid gray");

        tree.setWidth100();
        tree.setHeight(258);
              
		//chk
        cb1 = new CheckBox(I18N.message("second.inherited"));
        cb1.setHeight("10");
        cb2 = new CheckBox(I18N.message("file"));
        cb2.setHeight("10");
        cb3 = new CheckBox( I18N.message("second.table"));
        cb3.setHeight("10");
        
		cb1.addClickHandler(new com.google.gwt.event.dom.client.ClickHandler()
		{

			@Override
			public void onClick(com.google.gwt.event.dom.client.ClickEvent event) {
				// TODO Auto-generated method stub
				if("true".equals(cb1.getValue().toString())){
					cb2.setEnabled(false);
					cb3.setEnabled(false);
					cb2.setValue(false);
					cb3.setValue(false);
					
					grid2.deselectAllRecords();
					grid2.setDisabled(true);

					if(!hasInherited){//db에 데이터가 있고 상속으로 변경시(+)
						applyButton.setDisabled(false);
					}else{
						chkButton2();
					}					
				}else{
					cb2.setEnabled(true);
					cb3.setEnabled(true);
					grid2.setDisabled(false);
					chkButton2();
				}
				
			}
		});
		
		cb2.addClickHandler(new com.google.gwt.event.dom.client.ClickHandler()
		{
			@Override
			public void onClick(com.google.gwt.event.dom.client.ClickEvent event) {
				chkButton2();
			}
		});
        
        cb3.addClickHandler(new com.google.gwt.event.dom.client.ClickHandler()
		{
			@Override
			public void onClick(com.google.gwt.event.dom.client.ClickEvent event) {
				chkButton2();
			}
		});
		
		cb1HL = new HLayout(10);
		cb1HL.setWidth100();
		cb1HL.setAutoHeight();
		cb1HL.setAlign(Alignment.LEFT);
		cb1HL.addMember(cb1);
        
        cb2HL = new HLayout(10);
        cb2HL.setWidth100();
        cb2HL.setAutoHeight();
        cb2HL.setAlign(Alignment.LEFT);
        cb2HL.addMember(cb2);
        cb2HL.addMember(cb3);		
		
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
		
		grid2.addCellDoubleClickHandler(new CellDoubleClickHandler() {
			
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
		
		//gridHL = chk,grid2
		gridHL = new VLayout();
		gridHL.setWidth100();
		gridHL.setAutoHeight();
		gridHL.setAlign(Alignment.LEFT);
		gridHL.addMembers(cb1HL,cb2HL,grid2,createDBStatiActionForm());
		
		//mainHL = tree+gridHL
		mainHL = new HLayout(10);
        mainHL.setWidth100();
        mainHL.setAutoHeight();
        mainHL.setAlign(Alignment.LEFT);
        mainHL.addMember(tree);
        mainHL.addMember(gridHL);
        
        addMember(mainHL);
        
		//데이타 조회
        tree.getFolderDataRpc(SGroup.ROOTID, true);
        
        chkButton2();
	}
	
	/**
	 * 버튼 활성,비활성
	 */ 
	public void chkButton1(){
		if((cb2.getValue()|| cb3.getValue())&&!gridChk){//콤보박스 1개라도 체크 되있고 event 없으면
			applyButton.setDisabled(true);
		}else if((!cb2.getValue()&& !cb3.getValue())&&gridChk){//콤보박스가 다체크 안되있고 event 가 있으면
			applyButton.setDisabled(true);
		}else if((!cb2.getValue()&& !cb3.getValue())&&!gridChk){//콤보박스가 다 체크 안되 있을때 
			applyButton.setDisabled(true);
		}else{
			applyButton.setDisabled(false);
		}
		
		if(cb1.getValue()&&hasInherited){//db에 데이터 없고 상속일시에(-)
			applyButton.setDisabled(true);
		}
		
	}
		
	/**
	 * 버튼 활성,비활성
	 */ 
	public void chkButton2(){
		
		ListGridRecord[] rclist  = grid2.getSelectedRecords();
		if((cb2.getValue()|| cb3.getValue())&&rclist.length ==0){//콤보박스 1개라도 체크 되있고 event 없으면
			applyButton.setDisabled(true);
		}else if((!cb2.getValue()&& !cb3.getValue())&&rclist.length >0){//콤보박스가 다체크 안되있고 event 가 있으면
			applyButton.setDisabled(true);
		}else if((!cb2.getValue()&& !cb3.getValue())&&rclist.length ==0){//콤보박스가 다 체크 안되 있을때 
			applyButton.setDisabled(true);
		}else{
			applyButton.setDisabled(false);
		}
		
		if(cb1.getValue()&&hasInherited){//db에 데이터 없고 상속일시에(-)
			applyButton.setDisabled(true);
		}
		
	}
		
	/**
	 * grid2 가져오기
	 */ 
	public ListGrid getGrid() {
		return grid2;
	}
	
	/**
	 * 초기화
	 */ 
	public void  defaultSet(String id, String parentId){
		hasInherited = false;
		grid2.setDisabled(true);
		
		if(ROOT_NO.equals(id)){
			cb1.setEnabled(false);
			cb1.setValue(false);
			cb2.setEnabled(false);
			cb2.setValue(false);
			cb3.setEnabled(false);
			cb3.setValue(false);
			grid2.setDisabled(true);
		}else if(ROOT_NO.equals(parentId)){//수정금지,cb2,cb3 수정가능
			cb1.setEnabled(false);
			cb1.setValue(false);
			cb2.setEnabled(true);
			cb2.setValue(false);
			cb3.setEnabled(true);
			cb3.setValue(false);
			grid2.setDisabled(false);
		}else{
			cb1.setEnabled(true);
			cb1.setValue(true);
			cb2.setEnabled(false);
			cb2.setValue(false);
			cb3.setEnabled(false);
			cb3.setValue(false);
			grid2.setDisabled(true);
		}
	}
	
	@Override
	public void onRecordSelected(Serializable id, Serializable parentId) {
		defaultSet((String)id, (String)parentId);
	}

	
	@Override
	public void onRecordSelected(Record record) {
		
//		if(!ROOT_NO.equals(treeid)){
			SAudit audit = new SAudit();
			audit.setType(2);
			audit.setTargetId(record.getAttributeAsString("id"));
			audit.setParentId(record.getAttributeAsString("parentId"));
			
			ServiceUtil.system().listActions(Session.get().getSid(), audit, new AsyncCallbackWithStatus<SAudit>() {
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
						cb1.setValue(1==result.getHasInherited()?true:false);
						
						cb2.setEnabled(true);
						cb3.setEnabled(true);
						cb2.setValue(1==result.getHasFile()?true:false);
						cb3.setValue(1==result.getHasTable()?true:false);
						
						RecordList rclist  = grid2.getDataAsRecordList();
						for (int j = 0; j < result.getEvents().length; j++) {	
				    		Record[] rc = rclist.findAll("event", result.getEvents()[j]);
				    		grid2.selectRecords(rc);
						}
						
						if(1 == result.getHasInherited()){//상속이면
							cb2.setEnabled(false);
							cb3.setEnabled(false);
							grid2.setDisabled(true);
							hasInherited = true;
						}else{
							grid2.setDisabled(false);
							hasInherited = false;
						}
					}
					chkButton2();
				}
				@Override
				public void onFailureEvent(Throwable caught) {
					Log.serverError(caught, false);
					SCM.warn(caught);
				}
			});
//		}
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
							
							Record[] rc  = grid2.getSelectedRecords();

							if(cb2.getValue()&& cb3.getValue()&&rc == null){
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
							audit.setType(2);
							audit.setTargetId(tree.getSelectedRecord().getAttributeAsString("id"));
							audit.setParentId(tree.getSelectedRecord().getAttributeAsString("parentId"));
							audit.setHasFile(cb2.getValue()?1:0);
							audit.setHasTable(cb3.getValue()?1:0);
							audit.setEvents(events);							
							
							if(!cb1.getValue()){
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
							}else{//db 데이타 존재하고 상속일때만 
							
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
										
										onRecordSelected(new Long(tree.getSelectedRecord().getAttribute("id")), new Long(tree.getSelectedRecord().getAttribute("parentId")));
										onRecordSelected(tree.getSelectedRecord());
									}
									@Override
									public void onFailureEvent(Throwable caught) {
										Log.serverError(caught, false);
										SCM.warn(caught);
									}
								});
							}
						}
					}
				});
			}
        });
		
		DynamicForm dfButton = new DynamicForm();   
		dfButton.setWidth(80);   
        dfButton.setAlign(Alignment.LEFT);
        dfButton.setNumCols(3);
        dfButton.setColWidths("10", "10", "10");
        		
        dfButton.setItems(applyButton);
				
		return dfButton;
	}

	@Override
	public boolean isIDLong() {
		return false;
	}
}
