package com.speno.xedm.gui.frontend.client.admin.documentcode;

import java.util.HashMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.RecordList;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.DragDataAction;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.HiddenItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.RecordClickEvent;
import com.smartgwt.client.widgets.grid.events.RecordClickHandler;
import com.smartgwt.client.widgets.grid.events.RecordDoubleClickEvent;
import com.smartgwt.client.widgets.grid.events.RecordDoubleClickHandler;
import com.smartgwt.client.widgets.grid.events.RemoveRecordClickEvent;
import com.smartgwt.client.widgets.grid.events.RemoveRecordClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.speno.xedm.core.service.serials.SLifeCycle;
import com.speno.xedm.gui.common.client.Constants;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.RecordObserver;
import com.speno.xedm.gui.common.client.Session;
import com.speno.xedm.gui.common.client.log.Log;
import com.speno.xedm.gui.common.client.util.AsyncCallbackWithStatus;
import com.speno.xedm.gui.common.client.util.ItemFactory;
import com.speno.xedm.gui.common.client.util.LengthValidator;
import com.speno.xedm.gui.common.client.util.SCM;
import com.speno.xedm.gwt.service.LifeCycleService;
import com.speno.xedm.gwt.service.LifeCycleServiceAsync;

/**
 * LifeCycleGrid Panel
 * 
 * @author 남윤성
 * @since 1.0
 */
public class LifeCycleGridPanel extends VLayout {	
	private static HashMap<String, LifeCycleGridPanel> instanceMap = new HashMap<String, LifeCycleGridPanel>();
	private LifeCycleServiceAsync service = (LifeCycleServiceAsync) GWT.create(LifeCycleService.class);
	
	private ListGrid grid;
	private DynamicForm form;
	private HLayout actionHL;
	private RecordObserver recordObserver;	
 
	/**
	 * 기 생성된 instance를 반환함. instance가 없을 경우 생성 후 반환함
	 * @param id
	 * @param type
	 * @param subTitle
	 * @return
	 */
	public static LifeCycleGridPanel get(final String id,  final String subTitle) {
		return get(id, subTitle, null, true, "100%");
	} 
	
	/**
	 * 기 생성된 instance를 반환함. instance가 없을 경우 생성 후 반환함
	 * @param id
	 * @param type
	 * @param subTitle
	 * @param ob
	 * @param isAction
	 * @param width
	 * @return
	 */
	public static LifeCycleGridPanel get(final String id, final String subTitle, final RecordObserver ob, final boolean isAction, final String width) {
		if (instanceMap.get(id) == null) {
			new LifeCycleGridPanel(id, subTitle, ob, isAction, width);
		}
		return instanceMap.get(id);
	} 
	
	public LifeCycleGridPanel(final String id,  final String subTitle) {
		this(id, subTitle, null, true, "100%");
	}
	
	public LifeCycleGridPanel(final String id,  final String subTitle, final RecordObserver ob, final boolean isAction, final String width) {
		instanceMap.put(id, this);
				
		this.recordObserver = ob;
		
		if(subTitle != null) {
			/* Sub Title 생성 */
			Label subTitleLable = new Label();
			subTitleLable.setAutoHeight();   
	        subTitleLable.setAlign(Alignment.LEFT);   
	        subTitleLable.setValign(VerticalAlignment.CENTER);
	        subTitleLable.setStyleName("subTitle");
	        subTitleLable.setContents(subTitle);
	        addMember(subTitleLable);
		}
		
		grid = new ListGrid();
		grid.setWidth100();		
		grid.setHeight100();
		grid.setShowAllRecords(true);
		grid.setEmptyMessage(I18N.message("notitemstoshow"));
		grid.setCanFreezeFields(true);
		grid.setDragDataAction(DragDataAction.COPY);
		
		grid.setSelectionType(isAction ? SelectionStyle.SINGLE : SelectionStyle.MULTIPLE);        
        grid.setCanReorderRecords(!isAction);
        grid.setCanAcceptDroppedRecords(!isAction);
        grid.setCanDragRecordsOut(!isAction);        
    	grid.setCanRemoveRecords(isAction);   
    			
        //record dbclick event handler 정의------------------------------------------------------------
		grid.addRecordDoubleClickHandler(new RecordDoubleClickHandler() {
			@Override
			public void onRecordDoubleClick(RecordDoubleClickEvent event) {
				if(recordObserver != null) {
					recordObserver.onRecordDoubleClick(event.getRecord());
				}
			}   
        });
		
		ListGridField idField = new ListGridField("id", I18N.message("id"), 80);
		idField.setHidden(true);
		ListGridField nameField = new ListGridField("name", I18N.message("name"), 120);
		ListGridField descField = new ListGridField("description", I18N.message("description"));
		descField.setWidth("*");
		grid.setFields(idField, nameField, descField);
		
		if(isAction) {
	    	//record click event handler 정의--------------------------------------------------------------
			grid.addRecordClickHandler(new RecordClickHandler() {
				@Override
	            public void onRecordClick(RecordClickEvent event) {
					recordClickedProcess(event.getRecord());
					if(recordObserver != null) {				
						recordObserver.onRecordSelected(grid.getSelectedRecord());
	        		}
	            }   
	        });
			
			//record 삭제 event handler 정의--------------------------------------------------------------
			grid.addRemoveRecordClickHandler(new RemoveRecordClickHandler() {
				@Override
				public void onRemoveRecordClick(final RemoveRecordClickEvent event) {				
//					SC.confirm(I18N.message("confirmdelete"),  new BooleanCallback() {
//						@Override
//						public void execute(Boolean value) {
//							if(value != null && value) {
								final ListGridRecord record = grid.getRecord( event.getRowNum());
//								form.reset();
//								form.editRecord(record);
//								
//								//2013.05.06 Member 있으면 삭제 안되게.
//								if(recordObserver != null) {
//									if(recordObserver.isExistMember()) {
//										SC.say(I18N.message("second.existassignedstatesmembercannotdeleted"));
//										event.cancel();
//										return;
//									}
//								}
								executeRemove(Long.parseLong(record.getAttribute("id")));
//							}
//						}
//					});
					event.cancel();
				}
			});
			
		}
		
		VLayout groupVL = new VLayout(5);
		groupVL.addMember(grid);
		
		if(isAction) {
			groupVL.addMember(createFormVL());
			groupVL.addMember(createActHL());
		}
		
		setMembersMargin(Constants.SUBTITLE_MARGIN);
        addMember(groupVL);
        setWidth(width);
        
        executeFetch();
	}
	
	public ListGrid getGrid() {
		return grid;
	}

	/**
	 *  Left LifeCycle 폼 패널 생성
	 * @return VLayout
	 */
	private VLayout createFormVL() {
		HiddenItem idItem = new HiddenItem("id");
		
		TextItem NmItem = new TextItem("name", I18N.message("name"));
		NmItem.setWidth("*");
		NmItem.setCanEdit(true);
		NmItem.setWrapTitle(false);
//		NmItem.setLength(Constants.MAX_LEN_NAME);
		NmItem.setValidators(new LengthValidator(NmItem, Constants.MAX_LEN_NAME));
		NmItem.setRequired(true);
		
		TextItem descItem = new TextItem("description", I18N.message("description"));
//		descItem.setLength(Constants.MAX_LEN_NAME);
		descItem.setValidators(new LengthValidator(descItem, Constants.MAX_LEN_NAME));
		descItem.setWidth("*");
		descItem.setCanEdit(true);		
		
		form = new DynamicForm();
		form.setWidth100();
		form.setMargin(4);		
    	form.setItems(idItem, NmItem, descItem);
    	form.reset();    	
    	
    	VLayout formVL = new VLayout(50);
    	formVL.setBorder("1px solid gray");
    	formVL.setWidth100();
    	formVL.setAutoHeight();
    	formVL.addMembers(form);
		
    	return formVL;
	}

	/**
	 * Left  LifeCycle Action 패널 생성
	 * @return HLayout
	 */
	private HLayout createActHL() {
		
		Button btnAddNew = new Button(I18N.message("addnew"));
		btnAddNew.setIcon(ItemFactory.newImgIcon("page_white_add.png").getSrc());
		btnAddNew.addClickHandler(new ClickHandler() {   
            public void onClick(ClickEvent event) {
            	form.getItem("id").setTooltip(I18N.message("generatedbyserver", form.getItem("id").getTitle()));
            	form.editNewRecord();
            	form.reset();
            	grid.deselectAllRecords();
            }   
        });
		
		Button btnSave = new Button(I18N.message("save"));
		btnSave.setIcon(ItemFactory.newImgIcon("data_into.png").getSrc());
		btnSave.addClickHandler(new ClickHandler() {   
            public void onClick(ClickEvent event) {
            	if(form.getValue("id") == null) {
            		 if(form.validate()) {
            			 if(!checkName((String)form.getValue("name"))){
            				 SC.say(I18N.message("secnod.duplicatemessage", I18N.message("name")));
            				 return;
            			 }else{
            				 executeAdd();
            			 }
            		 }
            	}
            	else {
            		 if(form.validate()) {
            			 if(!checkName((String)form.getValue("name"))){
            				 SC.say(I18N.message("secnod.duplicatemessage", I18N.message("name")));
            				 return;
            			 }else{
            				 executeUpdate();
            			 }
            		 }
            	}
            }   
        });
		
		actionHL = new HLayout(10);
		actionHL.setHeight(1);
		actionHL.setMembers(btnAddNew, btnSave);		
		return actionHL;
	}
	
	private Boolean checkName(String name){
		boolean rtn = false;
		RecordList rclist  = grid.getDataAsRecordList();
		Record[] rc = rclist.findAll("name", name);
		if(rc!= null){
			if(null == form.getValueAsString("id")){//insert 
				rtn = false;
			}else if((form.getValueAsString("id")).equals(rc[0].getAttributeAsString("id"))){// update
				rtn = true;
			}else{
				rtn = false;
			}
		}else{
			rtn = true;
		}
		return rtn;
	}
	
	/**
	 * Record Click Event Handler
	 * @param record
	 */
	private void recordClickedProcess(Record record) {
		if(form != null) {
			form.getItem("id").setTooltip(I18N.message("fieldisreadonly", form.getItem("id").getTitle()));
	    	form.reset();
	    	form.editRecord(record);
		}
	}
	
	private void executeFetch()	{				
		Log.debug("[ LifeCycleGridPanel executeFetch ]");
		
		service.getProfiles(Session.get().getSid(), new AsyncCallback<SLifeCycle[]>() {

			@Override
			public void onFailure(Throwable caught) {
				SCM.warn(caught);
				
			}
			@Override
			public void onSuccess(SLifeCycle[] result) {
				for (int j = 0; j < result.length; j++) {
					ListGridRecord record=new ListGridRecord();
					record.setAttribute("id", result[j].getId());
					record.setAttribute("name", result[j].getName());
					record.setAttribute("description", result[j].getDescription());	
					grid.addData(record);
				}
				
				if (result.length > 0) {
					grid.selectRecord(0);
					recordClickedProcess(grid.getRecord(0));
					if(recordObserver != null) {				
						recordObserver.onRecordSelected(grid.getRecord(0));
	        		}
				}
				Log.debug("LifeCycleGridPanel executeFetch ] result.size()["+result.length +"]");
			}
			
		});
	}
	
	/**
	 * LifeCycle 추가
	 */
	private void executeAdd() {
		Log.debug("[ LifeCycleGridPanel executeAdd ]");
		
		SLifeCycle lifecycle = new SLifeCycle();
		lifecycle.setId(0L);
		lifecycle.setName(form.getValueAsString("name"));
		lifecycle.setDescription(form.getValueAsString("description"));
		
		service.saveProfile(Session.get().getSid(), lifecycle, new AsyncCallbackWithStatus<SLifeCycle>() {
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
			public void onSuccessEvent(SLifeCycle result) {
				Log.debug("[ LifeCycleGridPanel executeAdd ] onSuccess. id["+result.getId()+"]");
				
				ListGridRecord addRecord = new ListGridRecord();
				addRecord.setAttribute("id", result.getId());				
				addRecord.setAttribute("name", result.getName());
				addRecord.setAttribute("description", result.getDescription());
				grid.addData(addRecord);
				
				int selectedRowNum = grid.getRecordIndex(addRecord);						
				grid.selectSingleRecord(selectedRowNum);
				grid.scrollToRow(selectedRowNum);						
				form.reset();
            	form.editRecord(addRecord);
            	recordClickedProcess(addRecord);
				if(recordObserver != null) {				
					recordObserver.onRecordSelected(grid.getSelectedRecord());
        		}
            	form.getItem("id").setTooltip(I18N.message("fieldisreadonly", form.getItem("id").getTitle()));
            	SC.say(I18N.message("operationcompleted"));
			}
		});
	}
	
	/**
	 * LifeCycle 수정
	 */
	private void executeUpdate() {
		Log.debug("[ LifeCycleGridPanel executeUpdate ]");

		SLifeCycle lifecycle = new SLifeCycle();
		lifecycle.setId(Long.parseLong(form.getValueAsString("id")));
		lifecycle.setName(form.getValueAsString("name"));
		lifecycle.setDescription(form.getValueAsString("description"));
		
		service.saveProfile(Session.get().getSid(), lifecycle, new AsyncCallbackWithStatus<SLifeCycle>() {
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
			public void onSuccessEvent(SLifeCycle result) {
				Log.debug("[ LifeCycleGridPanel executeUpdate ] onSuccess. id["+result.getId()+"]");
				
				final int selectedRowNum = grid.getRecordIndex(grid.getSelectedRecord());				
				ListGridRecord selectedRecord = grid.getSelectedRecord();
				selectedRecord.setAttribute("name", result.getName());
				selectedRecord.setAttribute("description", result.getDescription());
				
				grid.getDataAsRecordList().set(selectedRowNum, selectedRecord);				
				grid.selectSingleRecord(selectedRowNum);
				grid.scrollToRow(selectedRowNum);
				SC.say(I18N.message("operationcompleted"));		
			
			}
		});
	}	
	
	/**
	 * LifeCycle 삭제
	 * @param Id
	 */
	private void executeRemove(final long id)	{
		Log.debug("[ LifeCycleGridPanel executeRemove ] id["+id+"]");
		if(id < 0) return;
		SC.confirm(I18N.message("wanttodelete"),  new BooleanCallback() {
			@Override
			public void execute(Boolean value) {
				if(value != null && value) {
					service.deleteProfile(Session.get().getSid(), id, new AsyncCallbackWithStatus<Void>() {
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
							Log.debug("[ LifeCycleGridPanel executeRemove ] onSuccess. id["+id+"]");
							grid.removeSelectedData();
							form.editNewRecord();
							form.reset();
							form.getItem("id").setTooltip(I18N.message("generatedbyserver", form.getItem("id").getTitle()));
							SC.say(I18N.message("operationcompleted"));
						}
					});
				}
			}
		});
		
	}
}
