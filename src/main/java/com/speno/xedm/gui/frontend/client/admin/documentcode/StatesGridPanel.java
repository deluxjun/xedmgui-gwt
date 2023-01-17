package com.speno.xedm.gui.frontend.client.admin.documentcode;

import java.util.HashMap;

import com.google.gwt.core.client.GWT;
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
import com.speno.xedm.core.service.serials.SState;
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
 * StatesGrid Panel
 * @author 남윤성
 * @since 1.0
 */
public class StatesGridPanel extends VLayout{
	private static HashMap<String, StatesGridPanel> instanceMap = new HashMap<String, StatesGridPanel>();
	private LifeCycleServiceAsync service = (LifeCycleServiceAsync) GWT.create(LifeCycleService.class);
	
	private ListGrid grid;
	private DynamicForm form;
	private HLayout actionHL;
	private boolean oIsAction;
	private RecordObserver recordObserver;	
	private ListGrid listGrid;
	
	/**
	 * 기 생성된 instance를 반환함. instance가 없을 경우 생성 후 반환함
	 * @param id
	 * @param subTitle
	 * @param ob
	 * @param isAction
	 * @return
	 */
	public static StatesGridPanel get(final String id, final String subTitle, final RecordObserver ob, final boolean isCanDrag, final boolean isAction, final ListGrid listGrid, final String width) {
		if (instanceMap.get(id) == null) {
			new StatesGridPanel(id, subTitle, ob, isCanDrag, isAction, listGrid, width);
		}
		return instanceMap.get(id);
	}
	
	public StatesGridPanel(final String id, final String subTitle, final RecordObserver ob, final boolean isCanDrag, final boolean isAction, final ListGrid listGrid, final String width) {
		instanceMap.put(id, this);	
		
		this.recordObserver = ob;
		this.oIsAction = isAction;
		this.listGrid = listGrid;
		
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
		grid.setCanRemoveRecords(isAction);
		grid.invalidateCache();
		
		
		grid.setSelectionType(isAction ? SelectionStyle.SINGLE : SelectionStyle.MULTIPLE);        
        grid.setCanReorderRecords(!isAction);
        grid.setCanAcceptDroppedRecords(!isAction);
        grid.setCanDragRecordsOut(!isAction);  
        if(isCanDrag) {
        	//grid의 drag 환경 설정
        	grid.setDragDataAction(DragDataAction.COPY);        
	        grid.setCanReorderRecords(false);
	        grid.setCanAcceptDroppedRecords(true);
	        grid.setCanDragRecordsOut(true);        
        }
    	
		
		ListGridField typeIdField = new ListGridField("id", I18N.message("id"));
		ListGridField typeNmField = new ListGridField("name", I18N.message("name"));
		ListGridField descField = new ListGridField("description", I18N.message("description"));
		ListGridField conditionField = new ListGridField("condition", I18N.message("second.condition"));
		ListGridField targetField = new ListGridField("target", I18N.message("target"));
		ListGridField targetPathField = new ListGridField("path", I18N.message("target"));
		
		targetPathField.setWidth("*");
		
		if(isAction) {	
			typeIdField.setHidden(true);
			descField.setHidden(true);
			targetField.setHidden(true);
			targetPathField.setHidden(true);
		}else{
			typeIdField.setHidden(true);
			targetField.setHidden(true);
		}
		
		grid.setFields(typeIdField, typeNmField,descField, conditionField,targetField, targetPathField);
		
		if(isAction) {		
	        //record dbclick event handler 정의------------------------------------------------------------
			grid.addRecordDoubleClickHandler(new RecordDoubleClickHandler() {
				@Override
				public void onRecordDoubleClick(RecordDoubleClickEvent event) {
					if(recordObserver != null) {
						recordObserver.onRecordDoubleClick(event.getRecord());
					}
				}   
	        });
	    	
			//record click event handler 정의--------------------------------------------------------------
			grid.addRecordClickHandler(new RecordClickHandler() {   
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
				public void onRemoveRecordClick(RemoveRecordClickEvent event) {
					final ListGridRecord record = grid.getRecord( event.getRowNum());
					form.reset();
	            	form.editRecord(record);
					SC.confirm(I18N.message("confirmdelete"),  new BooleanCallback() {
						@Override
						public void execute(Boolean value) {
							if(value != null && value) {
								executeRemove(Long.parseLong(record.getAttribute("id")));
							}
						}
					});
					event.cancel();
				}
			});		
			
		}
		
		VLayout docTypeVL = new VLayout(5);
		setMembersMargin(Constants.ARROW_MARGIN);
		docTypeVL.addMember(grid);
        
		if(isAction) {
			docTypeVL.addMember(createFormVL());
			docTypeVL.addMember(createActHL());
		}
        addMember(docTypeVL);
        if(isAction) {
        	setHeight100();
            setWidth(width);
        }else{
        	setHeight(width);
            setWidth100();
        }
        
        executeFetch();
	}
	
	/**
	 * 하단 상세 Form 생성
	 * @return
	 */
	private VLayout createFormVL() {
		HiddenItem typeIdItem = new HiddenItem("id");
		
		TextItem typeNmItem = new TextItem("name", I18N.message("name"));
		typeNmItem.setRequired(true);		
		typeNmItem.setCanEdit(true);
		typeNmItem.setWrapTitle(false);
//		typeNmItem.setLength(Constants.MAX_LEN_NAME);
		typeNmItem.setValidators(new LengthValidator(typeNmItem, Constants.MAX_LEN_NAME));
		
		TextItem descItem = new TextItem("description", I18N.message("description"));
		descItem.setCanEdit(true);		
		descItem.setWrapTitle(false);
//		descItem.setLength(Constants.MAX_LEN_NAME);
		descItem.setValidators(new LengthValidator(descItem, Constants.MAX_LEN_NAME));
		
		TextItem condItem = new TextItem("condition", I18N.message("second.condition"));
		condItem.setRequired(true);	
		condItem.setCanEdit(true);		
		condItem.setWrapTitle(false);
//		condItem.setLength(Constants.MAX_LEN_NAME);
		condItem.setValidators(new LengthValidator(condItem, Constants.MAX_LEN_NAME));
		
		HiddenItem targetItem = new HiddenItem("target");
		HiddenItem pathItem = new HiddenItem("path");
		
		typeNmItem.setWidth("*");        
		descItem.setWidth("*");          
		condItem.setWidth("*");   
		                                                                    
		typeIdItem.setStartRow(false);			typeIdItem.setEndRow(false);
		typeNmItem.setStartRow(false);		typeNmItem.setEndRow(false);	
		descItem.setStartRow(false);			descItem.setEndRow(false);	
		condItem.setStartRow(false);			condItem.setEndRow(false);	

		form = new DynamicForm();
		form.setWidth100();
		form.setMargin(4);
		form.setItems(typeIdItem, typeNmItem, descItem, condItem,targetItem,pathItem);
		form.reset();
    	
    	VLayout formVL = new VLayout(50);
    	formVL.setBorder("1px solid gray");
    	formVL.setWidth100();
    	formVL.setAutoHeight();    	
    	formVL.addMembers(form);
    	
    	return formVL;
	}
	
	/**
	 * Action Panel 생성
	 * @return
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
            			 executeAdd();
            		 }
            	}
            	else {
            		 if(form.validate()) {
            			 executeUpdate();
            		 }
            	}
            }   
        });
		
		actionHL = new HLayout(10);
		actionHL.setHeight(1);
		actionHL.setMembers(btnAddNew, btnSave);		
		return actionHL;
	}
	
	/**
	 * 그리드 가져오기
	 */
	public ListGrid getGrid() {
		return grid;
	}
	
	/**
	 * 폼 가져오기
	 */
	public DynamicForm getForm() {
		return form;
	}
	
	/**
	 * 조회
	 */
	public void defaultExecuteFetch(){
		executeFetch();
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
	
	/**
	 * 조회
	 */
	private void executeFetch()	{				
		Log.debug("[ StatesGridPanel executeFetch ]");
		
		service.getAllStates(Session.get().getSid(), new AsyncCallbackWithStatus<SState[]>() {
			@Override
			public String getSuccessMessage() {
				return I18N.message("client.searchComplete");
			}
			@Override
			public String getProcessMessage() {
				return I18N.message("client.searchRequest");
			}
			@Override
			public void onSuccessEvent(SState[] result) {
				for (int j = 0; j < result.length; j++) {
					ListGridRecord record=new ListGridRecord();
					record.setAttribute("id", result[j].getId());
					record.setAttribute("name", result[j].getName());
					record.setAttribute("description", result[j].getDescription());	
					record.setAttribute("condition", result[j].getCondition());	
					String targets = "";
					String paths = "";
					if(result[j].getTarget() != null){
						for(int i= 0; i<result[j].getTarget().length; i++){
							targets += "," +result[j].getTarget()[i];
							paths += "," +result[j].getPath()[i];
						}
					}
					record.setAttribute("target","".equals(targets)?targets:targets.substring(1));
					record.setAttribute("path","".equals(paths)?paths:paths.substring(1));
					grid.addData(record);
				}
				
				if (result.length > 0) {
					grid.selectSingleRecord(0);
					if(oIsAction) {
					recordClickedProcess(grid.getRecord(0));
						if(recordObserver != null) {				
							recordObserver.onRecordSelected(grid.getSelectedRecord());
		        		}
					}
				}
				Log.debug("StatesGridPanel executeFetch ] result.size()["+result.length +"]");
			}
			@Override
			public void onFailureEvent(Throwable caught) {
				Log.serverError(caught, false);
				SCM.warn(caught);
			}
		});
	}
	
	/**
	 * 신규저장
	 */
	private void executeAdd() {
		Log.debug("[ StatesGridPanel executeAdd ]");
		
		SState state = new SState();
		state.setId(0L);
		state.setName(form.getValueAsString("name"));
		state.setDescription(form.getValueAsString("description"));
		state.setCondition(form.getValueAsString("condition"));
		
		service.saveState(Session.get().getSid(), state, new AsyncCallbackWithStatus<SState>() {
			@Override
			public String getSuccessMessage() {
				return I18N.message("operationcompleted");
			}
			@Override
			public String getProcessMessage() {
				return null;
			}
			@Override
			public void onSuccessEvent(SState result) {
				Log.debug("[ StatesGridPanel executeAdd ] onSuccess. id["+result.getId()+"]");
				
				ListGridRecord addRecord = new ListGridRecord();
				addRecord.setAttribute("id", result.getId());				
				addRecord.setAttribute("name", result.getName());
				addRecord.setAttribute("condition", result.getCondition());
				addRecord.setAttribute("description", result.getDescription());
				grid.addData(addRecord);
				
				int selectedRowNum = grid.getRecordIndex(addRecord);						
				grid.selectSingleRecord(selectedRowNum);
				grid.scrollToRow(selectedRowNum);						
				form.reset();
            	form.editRecord(addRecord);
            	form.getItem("id").setTooltip(I18N.message("fieldisreadonly", form.getItem("id").getTitle()));
            	listGrid.setData(new ListGridRecord[0]); //assignedTargetFolderGridPanel 그리드 초기화	
            	SC.say(I18N.message("operationcompleted"));
			}
			@Override
			public void onFailureEvent(Throwable caught) {
				Log.serverError(caught, false);
				SCM.warn(caught);
			}
		});
	}
	
	/**
	 * 저장
	 */
	private void executeUpdate() {
		Log.debug("[ StatesGridPanel executeUpdate ]");
		
		SState state = new SState();
		state.setId(Long.parseLong(form.getValueAsString("id")));
		state.setName(form.getValueAsString("name"));
		state.setDescription(form.getValueAsString("description"));
		state.setCondition(form.getValueAsString("condition"));
		
		RecordList rclist = listGrid.getDataAsRecordList();
		
		int rccnt = rclist.getLength();
		
		if(rccnt !=0){//그리드 데이터 있을때	//기존거
			long[] targetArr = new long[rccnt];
			for(int i=0; i< rccnt; i++){
				Record rc = rclist.get(i);		
				targetArr[i] = Long.parseLong(rc.getAttribute("target"));
			}
			state.setTarget(targetArr);
		}
		
		service.saveState(Session.get().getSid(), state, new AsyncCallbackWithStatus<SState>() {
			@Override
			public String getSuccessMessage() {
				return I18N.message("operationcompleted");
			}
			@Override
			public String getProcessMessage() {
				return null;
			}
			@Override
			public void onSuccessEvent(SState result) {
				Log.debug("[ StatesGridPanel executeUpdate ] onSuccess. id["+result.getId()+"]");
				
				final int selectedRowNum = grid.getRecordIndex(grid.getSelectedRecord());				
				ListGridRecord selectedRecord = grid.getSelectedRecord();
				selectedRecord.setAttribute("name", result.getName());
				selectedRecord.setAttribute("condition", result.getCondition());
				selectedRecord.setAttribute("description", result.getDescription());
				
				grid.getDataAsRecordList().set(selectedRowNum, selectedRecord);				
				grid.selectSingleRecord(selectedRowNum);
				grid.scrollToRow(selectedRowNum);
				SC.say(I18N.message("operationcompleted"));	
			}
			@Override
			public void onFailureEvent(Throwable caught) {
				Log.serverError(caught, false);
				SCM.warn(caught);
			}
		});
	}	
	
	/**
	 * 삭제
	 */
	private void executeRemove(final long id)
	{
		Log.debug("[ StatesGridPanel executeRemove ] id["+id+"]");
		if(id < 0) return;
		
		service.deleteState(Session.get().getSid(), id, new AsyncCallbackWithStatus<Void>() {
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
				Log.debug("[ StatesGridPanel executeRemove ] onSuccess. id["+id+"]");
				grid.removeSelectedData();
				form.editNewRecord();
            	form.reset();
				form.getItem("id").setTooltip(I18N.message("generatedbyserver", form.getItem("id").getTitle()));
            	listGrid.setData(new ListGridRecord[0]); //assignedTargetFolderGridPanel 그리드 초기화	
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