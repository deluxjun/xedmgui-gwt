package com.speno.xedm.gui.frontend.client.admin.documentcode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.Alignment;
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
import com.smartgwt.client.widgets.form.fields.SelectItem;
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
import com.speno.xedm.core.service.serials.SFilter;
import com.speno.xedm.gui.common.client.Constants;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.RecordObserver;
import com.speno.xedm.gui.common.client.Session;
import com.speno.xedm.gui.common.client.log.Log;
import com.speno.xedm.gui.common.client.util.AsyncCallbackWithStatus;
import com.speno.xedm.gui.common.client.util.IsNotChangedValidator;
import com.speno.xedm.gui.common.client.util.ItemFactory;
import com.speno.xedm.gui.common.client.util.LengthValidator;
import com.speno.xedm.gui.common.client.util.SCM;
import com.speno.xedm.gwt.service.FilterService;
import com.speno.xedm.gwt.service.FilterServiceAsync;

/**
 * FilterProfileGrid Panel
 * @author 남윤성
 * @since 1.0
 */
public class FilterProfileGridPanel extends VLayout {	
	private static HashMap<String, FilterProfileGridPanel> instanceMap = new HashMap<String, FilterProfileGridPanel>();	
	private FilterServiceAsync filterService = (FilterServiceAsync) GWT.create(FilterService.class);
	private ListGrid grid;
	private DynamicForm form;
	private HLayout actionHL;
	private LinkedHashMap<Integer, String> valueMap = new LinkedHashMap<Integer, String>();
	private RecordObserver recordObserver;	

	// kimsoeun GS인증용 - 변경사항 있는지 유효성 검사
	private List oldForm = new ArrayList();
	private IsNotChangedValidator isNotChangedValidator = new IsNotChangedValidator();
		
	
	/**
	 * 기 생성된 instance를 반환함. instance가 없을 경우 생성 후 반환함
	 * @param id
	 * @param subTitle
	 * @param ob
	 * @param isAction
	 * @return
	 */
	public static FilterProfileGridPanel get(final String id, final String subTitle, final RecordObserver ob, final boolean isAction) {
		if (instanceMap.get(id) == null) {
			new FilterProfileGridPanel(id, subTitle, ob, isAction);
		}
		return instanceMap.get(id);
	}
	
	public FilterProfileGridPanel(final String id, final String subTitle, final RecordObserver ob, final boolean isAction) {
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
		grid.setShowAllRecords(true);		
		grid.setEmptyMessage(I18N.message("notitemstoshow"));		
		grid.setCanFreezeFields(true);
		grid.setCanRemoveRecords(true);
		grid.setSelectionType(SelectionStyle.SINGLE);
		grid.setHeight100();
		grid.invalidateCache();
		
		ListGridField idField = new ListGridField("id", I18N.message("id"));
		idField.setHidden(true);
		ListGridField nameField = new ListGridField("name", I18N.message("name"));
		ListGridField descriptionField = new ListGridField("description", I18N.message("description"));
		ListGridField moduleField = new ListGridField("module", I18N.message("second.module"));
		ListGridField typeField = new ListGridField("type", I18N.message("type"));
		typeField.setHidden(true);
		ListGridField typeNameField = new ListGridField("typeName", I18N.message("type"));
		
		grid.setFields(idField, nameField, descriptionField, moduleField, typeField, typeNameField);
				
        //record dbclick event handler 정의------------------------------------------------------------
		grid.addRecordDoubleClickHandler(new RecordDoubleClickHandler() {
			@Override
			public void onRecordDoubleClick(RecordDoubleClickEvent event) {
				if(recordObserver != null) {
					recordObserver.onRecordDoubleClick(event.getRecord());
				}
			}   
        });
    	
		VLayout noticeVL = new VLayout(5);
		setMembersMargin(Constants.SUBTITLE_MARGIN);
		noticeVL.addMember(grid);
		
		if(isAction) {			
			//record click event handler 정의--------------------------------------------------------------
			grid.addRecordClickHandler(new RecordClickHandler() {   
	            public void onRecordClick(RecordClickEvent event) {
	            	recordClickedProcess(event.getRecord());
	            }   
	        });
			
			//record 삭제 event handler 정의--------------------------------------------------------------
			grid.addRemoveRecordClickHandler(new RemoveRecordClickHandler() {
				@Override
				public void onRemoveRecordClick(final RemoveRecordClickEvent event) {
					SC.confirm(I18N.message("confirmdelete"),  new BooleanCallback() {
						@Override
						public void execute(Boolean value) {
							if(value != null && value) {
								final ListGridRecord record = grid.getRecord( event.getRowNum());
								form.reset();
								form.editRecord(record);
								executeRemove(Long.parseLong(record.getAttribute("id")));
							}
						}
					});
					event.cancel();
				}
			});
			
			noticeVL.addMember(createFormVL());
			noticeVL.addMember(createActHL());
		}
        
        addMember(noticeVL);
        
        executeFetch();
	}
	
	/**
	 * 하단 상세 Form 생성
	 * @return
	 */
	private VLayout createFormVL() {
		HiddenItem idItem = new HiddenItem("id");
		
		TextItem nameItem = new TextItem("name", I18N.message("name"));
		nameItem.setWidth("300");
		nameItem.setCanEdit(true);
		nameItem.setWrapTitle(false);
		nameItem.setRequired(true);
		// kimsoeun GS인증용 - 툴팁 다국어화
		nameItem.setRequiredMessage(I18N.message("fieldisrequired"));
//		nameItem.setLength(Constants.MAX_LEN_NAME);
		nameItem.setValidators(new LengthValidator(nameItem, Constants.MAX_LEN_NAME));
        
		nameItem.setStartRow(false);			nameItem.setEndRow(false);	
		
		TextItem descriptionItem = new TextItem("description", I18N.message("description"));
		descriptionItem.setWidth("300");
		descriptionItem.setCanEdit(true);
		descriptionItem.setWrapTitle(false);
//		descriptionItem.setLength(Constants.MAX_LEN_NAME);
		descriptionItem.setValidators(new LengthValidator(descriptionItem, Constants.MAX_LEN_NAME));
        
		descriptionItem.setStartRow(false);			descriptionItem.setEndRow(true);	
		
		TextItem moduleItem = new TextItem("module", I18N.message("second.module"));
		moduleItem.setWidth("300");
		moduleItem.setCanEdit(true);
		moduleItem.setWrapTitle(false);
		moduleItem.setRequired(true);
		// kimsoeun GS인증용 - 툴팁 다국어화
		moduleItem.setRequiredMessage(I18N.message("fieldisrequired"));
//		moduleItem.setLength(Constants.MAX_LEN_MODULE);
		moduleItem.setValidators(new LengthValidator(moduleItem, Constants.MAX_LEN_MODULE));
        
		moduleItem.setStartRow(true);			moduleItem.setEndRow(false);	
		
	    SelectItem typeItem = new SelectItem("type",I18N.message("type"));
        typeItem.setWrapTitle(false);
        typeItem.setRequired(true);
        // kimsoeun GS인증용 - 툴팁 다국어화
        typeItem.setRequiredMessage(I18N.message("fieldisrequired"));
        typeItem.setEmptyDisplayValue(I18N.message("choosetype"));
        valueMap.put(1, I18N.message("input"));  
        valueMap.put(2, I18N.message("second.retrieve"));  
        typeItem.setValueMap(valueMap); 
        
        typeItem.setStartRow(false);			typeItem.setEndRow(false);	
        
		form = new DynamicForm();
		form.setMargin(4);
		form.setNumCols(4);
		form.setTitleWidth(80);
		form.setItems(idItem, nameItem, descriptionItem, moduleItem, typeItem);
		form.reset();
		
    	VLayout formVL = new VLayout();
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
	 * grid 가져오기
	 */
	public ListGrid getGrid() {
		return grid;
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
	    	
	    	// kimsoeun GS인증용 - 변경사항 여부 체크할 리스트 생성
        	isNotChangedValidator.setList(form, oldForm);
		}
	}
	
	private void executeFetch()	{		
		Log.debug("[ FilterProfileGridPanel executeFetch ]");
		
//		service.pagingMessages(Session.get().getSid(), config, "Notice", new AsyncCallbackWithStatus<PagingResult<SMessage>>() {
		filterService.listFilter(Session.get().getSid(), new AsyncCallbackWithStatus<List<SFilter>>() {
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
			public void onSuccessEvent(List<SFilter> result) {
				grid.setData(new ListGridRecord[0]); //그리드 초기화					
				for (int j = 0; j < result.size(); j++) {					
					ListGridRecord record=new ListGridRecord();
					record.setAttribute("id", result.get(j).getId());
					record.setAttribute("name", result.get(j).getName());
					record.setAttribute("description", result.get(j).getDescription());
					record.setAttribute("module", result.get(j).getModule());
					record.setAttribute("type", result.get(j).getType());
					record.setAttribute("typeName", valueMap.get(result.get(j).getType()));
					
					grid.addData(record);
				}	
				
				if (result.size() > 0) {
					grid.selectSingleRecord(0);
					recordClickedProcess(grid.getRecord(0));
				}
			}
		});
	}
	
	private void executeAdd() {
		Log.debug("[ FilterProfileGridPanel executeAdd ]");
		
		SFilter sfilter = new SFilter();
		sfilter.setId(0L);
		sfilter.setName(form.getValueAsString("name"));
		sfilter.setDescription(form.getValueAsString("description"));
		sfilter.setModule(form.getValueAsString("module"));
		sfilter.setType(Integer.parseInt(form.getValueAsString("type")));
		
		filterService.saveFilter(Session.get().getSid(), sfilter, new AsyncCallbackWithStatus<SFilter>() {
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
			public void onSuccessEvent(SFilter result) {
				Log.debug("[ FilterProfileGridPanel executeAdd ] onSuccess. id["+result.getId()+"]");
				
				ListGridRecord addRecord = new ListGridRecord();
				addRecord.setAttribute("id", result.getId());		
				addRecord.setAttribute("name", result.getName());
				addRecord.setAttribute("description", result.getDescription());
				addRecord.setAttribute("module", result.getModule());
				addRecord.setAttribute("type", result.getType());
				addRecord.setAttribute("typeName", valueMap.get(result.getType()));
				grid.addData(addRecord);
				
				int selectedRowNum = grid.getRecordIndex(addRecord);						
				grid.selectSingleRecord(selectedRowNum);
				grid.scrollToRow(selectedRowNum);						
				form.reset();
            	form.editRecord(addRecord);
            	form.getItem("id").setTooltip(I18N.message("fieldisreadonly", form.getItem("id").getTitle()));
            	SC.say(I18N.message("operationcompleted"));
			}
		});
	}
	
	private void executeUpdate() {
		Log.debug("[ FilterProfileGridPanel executeUpdate ]");
		
		SFilter sfilter = new SFilter();
		sfilter.setId(Long.parseLong(form.getValueAsString("id")));
		sfilter.setName(form.getValueAsString("name"));
		sfilter.setDescription(form.getValueAsString("description"));
		sfilter.setModule(form.getValueAsString("module"));
		sfilter.setType(Integer.parseInt(form.getValueAsString("type")));
		
		// kimsoeun GS인증용 - 변경사항 여부 확인
		List newForm = new ArrayList();
		newForm.add(sfilter.getId());
		newForm.add(sfilter.getName());
		newForm.add(sfilter.getDescription());
		newForm.add(sfilter.getModule());
		newForm.add(sfilter.getType());
		
		int changed = isNotChangedValidator.check2(newForm, oldForm);
		if(changed==newForm.size()) {
			SC.say(I18N.message("nothingchanged"));
			return;
		}
				
		filterService.saveFilter(Session.get().getSid(), sfilter, new AsyncCallbackWithStatus<SFilter>() {
			@Override
			public String getSuccessMessage() {
//				return I18N.message("operationcompleted");
				// kimsoeun GS인증용 - 툴팁 다국어화
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
			public void onSuccessEvent(SFilter result) {
				Log.debug("[ FilterProfileGridPanel executeUpdate ] onSuccess. id["+result.getId()+"]");
				
				final int selectedRowNum = grid.getRecordIndex(grid.getSelectedRecord());				
				ListGridRecord selectedRecord = grid.getSelectedRecord();
				selectedRecord.setAttribute("id", result.getId());		
				selectedRecord.setAttribute("name", result.getName());
				selectedRecord.setAttribute("description", result.getDescription());
				selectedRecord.setAttribute("module", result.getModule());
				selectedRecord.setAttribute("type", result.getType());
				selectedRecord.setAttribute("typeName", valueMap.get(result.getType()));
				
				grid.getDataAsRecordList().set(selectedRowNum, selectedRecord);				
				grid.selectSingleRecord(selectedRowNum);
				grid.scrollToRow(selectedRowNum);
//				SC.say(I18N.message("operationcompleted"));			
				// kimsoeun GS인증용 - 툴팁 다국어화
				SC.say(I18N.message("savecompleted"));			
				
				// kimsoeun GS인증용 - 변경사항 여부 체크할 리스트 생성
				oldForm.removeAll(oldForm);
				oldForm.add(grid.getSelectedRecord().getAttribute("id"));
				oldForm.add(grid.getSelectedRecord().getAttribute("name"));
				oldForm.add(grid.getSelectedRecord().getAttribute("description"));
				oldForm.add(grid.getSelectedRecord().getAttribute("module"));
				oldForm.add(grid.getSelectedRecord().getAttribute("type"));
			}
		});
	}	
	
	private void executeRemove(final long id)
	{
		Log.debug("[ FilterProfileGridPanel executeRemove ] id["+id+"]");

		filterService.deleteFilter(Session.get().getSid(), id, new AsyncCallbackWithStatus<Void>() {
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
				Log.debug("[ FilterProfileGridPanel executeRemove ] onSuccess. id["+id+"]");
				grid.removeSelectedData();
				form.editNewRecord();
				form.reset();
				form.getItem("id").setTooltip(I18N.message("generatedbyserver", form.getItem("id").getTitle()));
				SC.say(I18N.message("operationcompleted"));
			}
		});
		}
}