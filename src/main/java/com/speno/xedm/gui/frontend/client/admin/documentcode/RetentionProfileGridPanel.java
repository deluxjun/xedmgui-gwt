package com.speno.xedm.gui.frontend.client.admin.documentcode;

import java.util.HashMap;
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
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.RecordClickEvent;
import com.smartgwt.client.widgets.grid.events.RecordClickHandler;
import com.smartgwt.client.widgets.grid.events.RemoveRecordClickEvent;
import com.smartgwt.client.widgets.grid.events.RemoveRecordClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.speno.xedm.core.service.serials.SRetentionProfile;
import com.speno.xedm.gui.common.client.Constants;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.Session;
import com.speno.xedm.gui.common.client.util.AsyncCallbackWithStatus;
import com.speno.xedm.gui.common.client.util.IsNotChangedValidator;
import com.speno.xedm.gui.common.client.util.ItemFactory;
import com.speno.xedm.gui.common.client.util.LengthValidator;
import com.speno.xedm.gui.common.client.util.PositiveNumberValidator;
import com.speno.xedm.gui.common.client.util.SCM;
import com.speno.xedm.gwt.service.DocumentCodeService;
import com.speno.xedm.gwt.service.DocumentCodeServiceAsync;

/**
 * RetentionProfileGrid Panel
 * @author 박상기
 * @since 1.0
 */
public class RetentionProfileGridPanel extends VLayout {	
	private static HashMap<String, RetentionProfileGridPanel> instanceMap = new HashMap<String, RetentionProfileGridPanel>();	
	private DocumentCodeServiceAsync service = (DocumentCodeServiceAsync) GWT.create(DocumentCodeService.class);
	
	private ListGrid grid;
	private DynamicForm form;
	private HLayout actionHL;
	private IsNotChangedValidator validator = new IsNotChangedValidator();
	
	/**
	 * 기 생성된 instance를 반환함. instance가 없을 경우 생성 후 반환함
	 * @param id
	 * @param subTitle
	 * @return
	 */
	public static RetentionProfileGridPanel get(final String id, final String subTitle) {
		if (instanceMap.get(id) == null) {
			new RetentionProfileGridPanel(id, subTitle);
		}
		return instanceMap.get(id);
	}
	
	public RetentionProfileGridPanel(final String id, final String subTitle) {
		instanceMap.put(id, this);	
		
		/* Sub Title 생성 */
		Label subTitleLabel = new Label();
		subTitleLabel.setAutoHeight();   
		subTitleLabel.setAlign(Alignment.LEFT);   
		subTitleLabel.setValign(VerticalAlignment.CENTER);
		subTitleLabel.setStyleName("subTitle");
		subTitleLabel.setContents(I18N.message("retentionprofile"));
		
		grid = new ListGrid();
		grid.setWidth100();
		grid.setHeight100();		
		grid.setShowAllRecords(true);
		
		grid.setEmptyMessage(I18N.message("notitemstoshow"));		
		grid.setCanFreezeFields(true);
		grid.setCanRemoveRecords(true);
		grid.setSelectionType(SelectionStyle.SINGLE);
		//grid.setDataSource(new DocumentCodeDS(DocumentCodeDS.TYPE_RETENTION));
		//grid.setAutoFetchData(true);
		grid.invalidateCache();
		
		ListGridField typeIdField = new ListGridField("id", I18N.message("typeid"));
		typeIdField.setHidden(true);
		ListGridField typeNmField = new ListGridField("name", I18N.message("filetype"));
		ListGridField descriptionField = new ListGridField("description", I18N.message("description"));
		descriptionField.setWidth("*");
		ListGridField retentionField = new ListGridField("retention", I18N.message("retention"));
		retentionField.setHidden(true);
		
		grid.setFields(typeIdField, typeNmField, descriptionField, retentionField);
    	
		//record click event handler 정의--------------------------------------------------------------
		grid.addRecordClickHandler(new RecordClickHandler() {   
            public void onRecordClick(RecordClickEvent event) {
            	recordClickedProcess(event.getRecord());
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
		
		VLayout groupVL = new VLayout(5);
		groupVL.setMembers(grid, createFormVL(), createActHL());
		
		setMembersMargin(Constants.SUBTITLE_MARGIN);
        setMembers(subTitleLabel, groupVL);
        
        executeFetch();
	}
	
	/**
	 * 하단 상세 Form 생성
	 * @return
	 */
	private VLayout createFormVL() {		
		HiddenItem idItem = new HiddenItem("id");
		/*
		TextItem idItem = new TextItem("id", I18N.message("typeid"));		
		idItem.disable();
		idItem.setCanEdit(false);
		idItem.setWrapTitle(false);
		idItem.setWidth("*");
		idItem.setTooltip(I18N.message("generatedbyserver", idItem.getTitle()));
		*/
		
		TextItem nameItem = new TextItem("name", I18N.message("id"));
		nameItem.setWrapTitle(false);
		nameItem.setWidth("*");
		nameItem.setRequired(true);	
		// kimsoeun GS인증용 - 툴팁 다국어화
		nameItem.setRequiredMessage(I18N.message("fieldisrequired"));
//		nameItem.setLength(Session.get().getInfo().getIntConfig("gui.retention.name.fieldsize", 255));
		nameItem.setValidators(new LengthValidator(nameItem, Session.get().getInfo().getIntConfig("gui.retention.name.fieldsize", 255)));
		
		TextItem retentionItem = new TextItem("retention", I18N.message("retention"));
		retentionItem.setWrapTitle(false);
		retentionItem.setWidth("*");
		retentionItem.setRequired(true);
		// kimsoeun GS인증용 - 툴팁 다국어화
		retentionItem.setRequiredMessage(I18N.message("fieldisrequired"));
		retentionItem.setHint(I18N.message("days"));
//		retentionItem.setLength(Session.get().getInfo().getIntConfig("gui.retention.retention.filedsize", 4));
//		retentionItem.setValidators(new LengthValidator(retentionItem, Session.get().getInfo().getIntConfig("gui.retention.retention.filedsize", 6)));
		retentionItem.setKeyPressFilter("[0-9.]");
		// kimsoeun GS인증용 - 보존기간 프로파일 음수 입력 체크
		retentionItem.setValidators(
			new LengthValidator(retentionItem, Session.get().getInfo().getIntConfig("gui.retention.retention.filedsize", 6)), 
			new PositiveNumberValidator(retentionItem)
		);
		
		
		TextItem descriptionItem = new TextItem("description", I18N.message("description"));
		descriptionItem.setWrapTitle(false);
		descriptionItem.setWidth("*");
//		descriptionItem.setLength(Session.get().getInfo().getIntConfig("gui.description.fieldsize", 1000));
		descriptionItem.setValidators(new LengthValidator(descriptionItem, Session.get().getInfo().getIntConfig("gui.description.fieldsize", 1000)));
		
		form = new DynamicForm();
		form.setAutoWidth();	
		form.setMargin(4);
		form.setNumCols(4);
		form.setColWidths("1","1","1","*");
		form.setItems(idItem, nameItem, retentionItem, descriptionItem);
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
	 * Record Click Event Handler
	 * @param record
	 */
	private void recordClickedProcess(Record record) {
		form.getItem("id").setTooltip(I18N.message("fieldisreadonly", form.getItem("id").getTitle()));
    	form.reset();
    	form.editRecord(record);
    	validator.setMap(form);
	}
	
	private void executeFetch()	{				
		GWT.log("[ RetentionProfileGridPanel executeFetch ]", null);
		
		service.listRetentionProfilesLikeName(Session.get().getSid(), "", new AsyncCallbackWithStatus<List<SRetentionProfile>>() {
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
			public void onSuccessEvent(List<SRetentionProfile> result) {				
				grid.setData(new ListGridRecord[0]); //그리드 초기화					
				for (int j = 0; j < result.size(); j++) {					
					ListGridRecord record=new ListGridRecord();
					record.setAttribute("id", result.get(j).getId());
					record.setAttribute("name", result.get(j).getName());
					record.setAttribute("description", result.get(j).getDescription());
					record.setAttribute("retention", result.get(j).getRetention());
					grid.addData(record);
				}	
				
				if (result.size() > 0) {
					grid.selectSingleRecord(0);
					recordClickedProcess(grid.getRecord(0));
				}
				
				GWT.log("RetentionProfileGridPanel executeFetch ] result.size()["+result.size()+"]", null);
			}
		});
	}
	
	private void executeAdd() {
		GWT.log("[ RetentionProfileGridPanel executeAdd ]", null);
		
		SRetentionProfile retentionProfile = new SRetentionProfile();
		retentionProfile.setId(0L);
		retentionProfile.setName(form.getValueAsString("name"));
		retentionProfile.setDescription(form.getValueAsString("description"));
		retentionProfile.setRetention(Long.parseLong(form.getValueAsString("retention")));
		
		service.saveRetentionProfile(Session.get().getSid(), retentionProfile, new AsyncCallbackWithStatus<SRetentionProfile>() {
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
			public void onSuccessEvent(SRetentionProfile result) {
				GWT.log("[ RetentionProfileGridPanel executeAdd ] onSuccess. id["+result.getId()+"]", null);
				
				ListGridRecord addRecord = new ListGridRecord();
				addRecord.setAttribute("id", result.getId());
				addRecord.setAttribute("name", result.getName());
				addRecord.setAttribute("description", result.getDescription());
				addRecord.setAttribute("retention", result.getRetention());
				grid.addData(addRecord);
				
				int selectedRowNum = grid.getRecordIndex(addRecord);						
				grid.selectSingleRecord(selectedRowNum);
				grid.scrollToRow(selectedRowNum);						
				form.reset();
            	form.editRecord(addRecord);
            	form.getItem("id").setTooltip(I18N.message("fieldisreadonly", form.getItem("id").getTitle()));
            	SC.say(I18N.message("operationcompleted"));
            	validator.setMap(form);
			}
		});
	}
	
	private void executeUpdate() {
		GWT.log("[ RetentionProfileGridPanel executeUpdate ]", null);
		
		SRetentionProfile retentionProfile = new SRetentionProfile();
		retentionProfile.setId(Long.parseLong(form.getValueAsString("id")));
		retentionProfile.setName(form.getValueAsString("name"));
		retentionProfile.setDescription(form.getValueAsString("description"));
		retentionProfile.setRetention(Long.parseLong(form.getValueAsString("retention")));
		
		if(validator.check(form)){
			SC.say(I18N.message("nothingchanged"));
			return;
		}
		
		service.saveRetentionProfile(Session.get().getSid(), retentionProfile, new AsyncCallbackWithStatus<SRetentionProfile>() {
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
			public void onSuccessEvent(SRetentionProfile result) {
				GWT.log("[ RetentionProfileGridPanel executeUpdate ] onSuccess. id["+result.getId()+"]", null);
				
				final int selectedRowNum = grid.getRecordIndex(grid.getSelectedRecord());				
				ListGridRecord selectedRecord = grid.getSelectedRecord();
				selectedRecord.setAttribute("name", result.getName());
				selectedRecord.setAttribute("description", result.getDescription());
				selectedRecord.setAttribute("retention", result.getRetention());
				
				grid.getDataAsRecordList().set(selectedRowNum, selectedRecord);				
				grid.selectSingleRecord(selectedRowNum);
				grid.scrollToRow(selectedRowNum);
//				SC.say(I18N.message("operationcompleted"));
				// kimsoeun GS인증용  - 작업 완료 -> 저장 완료 문구 변경
				SC.say(I18N.message("savecompleted"));
				validator.setMap(form);
			}
		});
	}	
	
	private void executeRemove(final long id)
	{
		GWT.log("[ RetentionProfileGridPanel executeRemove ] id["+id+"]", null);
		if(id < 0) return;
		
		service.deleteRetentionProfile(Session.get().getSid(), id, new AsyncCallbackWithStatus<Void>() {
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
				GWT.log("[ RetentionProfileGridPanel executeRemove ] onSuccess. id["+id+"]", null);
				
				grid.removeSelectedData();
				form.editNewRecord();
            	form.reset();
            	form.getItem("id").setTooltip(I18N.message("generatedbyserver", form.getItem("id").getTitle()));
				SC.say(I18N.message("operationcompleted"));
			}
		});
	}
}