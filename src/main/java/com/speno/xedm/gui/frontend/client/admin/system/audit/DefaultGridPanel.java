package com.speno.xedm.gui.frontend.client.admin.system.audit;

import com.google.gwt.user.client.ui.CheckBox;
import com.smartgwt.client.data.Record;
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
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.Session;
import com.speno.xedm.gui.common.client.log.Log;
import com.speno.xedm.gui.common.client.util.AsyncCallbackWithStatus;
import com.speno.xedm.gui.common.client.util.ItemFactory;
import com.speno.xedm.gui.common.client.util.SCM;
import com.speno.xedm.gui.common.client.util.ServiceUtil;

/**
 * DefaultGridPanel
 * @author 남윤성
 * @since 1.0
 */
public class DefaultGridPanel extends VLayout {
	private static DefaultGridPanel instance = null;
	
	private VLayout mainVL;
	private HLayout cbHL;
	private ListGrid grid;
	private CheckBox cb1;
	private CheckBox cb2;
	private ButtonItem applyButton;
	private boolean gridChk = false;
	
	/**
	 * 기 생성된 instance를 반환함. instance가 없을 경우 생성 후 반환함
	 * @return
	 */
	public static DefaultGridPanel get(boolean isRefresh) {
		if (instance == null) {
			instance = new DefaultGridPanel(isRefresh);
		}
		return instance;
	}
	
	public DefaultGridPanel(boolean isRefresh) {
		if(isRefresh && mainVL != null) {
			removeMember(mainVL);
		}
		setWidth100();
		setMembersMargin(10);
		
		Label subTitleLable = new Label();
		subTitleLable.setAutoHeight();   
        subTitleLable.setAlign(Alignment.LEFT);   
        subTitleLable.setValign(VerticalAlignment.CENTER);
        subTitleLable.setStyleName("subTitle");
        subTitleLable.setContents(I18N.message("defalut"));
		
		
        cb1 = new CheckBox(I18N.message("second.file"));
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
        
		grid = new ListGrid();
		grid.setWidth100();
		grid.setHeight(553);		
		grid.setShowAllRecords(true);
		
		grid.setEmptyMessage(I18N.message("notitemstoshow"));		
		grid.setCanFreezeFields(true);
		grid.setSelectionAppearance(SelectionAppearance.CHECKBOX); 
		grid.setSelectionType(SelectionStyle.SIMPLE);
		
		
		grid.addCellClickHandler(new CellClickHandler() {
			@Override
			public void onCellClick(CellClickEvent event) {				        		
				int colNum = event.getColNum();  
				if(0 != colNum){
					Record r = event.getRecord();
	            	if (!grid.isSelected((ListGridRecord)r))
	            		grid.selectRecord(r);
	            	else
	            		grid.deselectRecord(r);
				}
				chkButton2();
				if(grid.getRecords().length == grid.getSelectedRecords().length){
					gridChk = true;
				}else{
					gridChk = false;
				}
			}
		});

		grid.addCellDoubleClickHandler(new CellDoubleClickHandler() {
			@Override
			public void onCellDoubleClick(CellDoubleClickEvent event) {				        		
				int colNum = event.getColNum();  
				if(0 != colNum){
					Record r = event.getRecord();
	            	if (!grid.isSelected((ListGridRecord)r))
	            		grid.selectRecord(r);
	            	else
	            		grid.deselectRecord(r);
				}
				chkButton2();
				if(grid.getRecords().length == grid.getSelectedRecords().length){
					gridChk = true;
				}else{
					gridChk = false;
				}
			}
		});

		grid.addHeaderClickHandler(new HeaderClickHandler(){

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
		
		grid.setFields(event,eventname);
		
		mainVL = new VLayout();
		mainVL.setHeight100();
		mainVL.setWidth100();
		mainVL.setMembers(subTitleLable,cbHL,grid,createDBStatiActionForm());
		
		addMember(mainVL);
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
	}	
	
	/**
	 * 버튼 활성,비활성
	 */
	public void chkButton2(){
		ListGridRecord[] rclist  = grid.getSelectedRecords();
		if((cb1.getValue()|| cb2.getValue())&&rclist.length ==0){//콤보박스 1개라도 체크 되있고 event 없으면
			applyButton.setDisabled(true);
		}else if((!cb1.getValue()&& !cb2.getValue())&&rclist.length >0){//콤보박스가 다체크 안되있고 event 가 있으면
			applyButton.setDisabled(true);
		}else if((!cb1.getValue()&& !cb2.getValue())&&rclist.length ==0){//콤보박스가 다 체크 안되 있을때 
			applyButton.setDisabled(true);
		}else{
			applyButton.setDisabled(false);
		}
	}
	
	/**
	 * grid 가져오기
	 */
	public ListGrid getGrid() {
		return grid;
	}

	/**
	 * cb1 가져오기
	 */
	public CheckBox getCb1() {
		return cb1;
	}
	
	/**
	 * cb2 가져오기
	 */
	public CheckBox getCb2() {
		return cb2;
	}
	
	/**
	 * APPLY 적용 버튼, xml저장
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
				Record[] rc = grid.getSelectedRecords();
				if((cb1.getValue()|| cb2.getValue())&&rc.length ==0){
					SC.say(I18N.message("second.checkevent"));
					chkButton2();
					return;
				}
				SC.confirm(I18N.message("applyondb"),  new BooleanCallback() {
					@Override
					public void execute(Boolean value) {
						if(value != null && value) {
							
							Record[] rc = grid.getSelectedRecords();
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
							audit.setType(0);
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
									// TODO Auto-generated method stub
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
		
		StaticTextItem dummy = ItemFactory.newStaticTextItem("dummy", "", "");
        dummy.setShowTitle(false);
        dummy.setWidth(30);
        
		DynamicForm searchForm = new DynamicForm();
		searchForm.setAlign(Alignment.LEFT);
		searchForm.setNumCols(2);
		searchForm.setColWidths("10","10");
		searchForm.setItems(applyButton);
		
		return searchForm;
	}
}
