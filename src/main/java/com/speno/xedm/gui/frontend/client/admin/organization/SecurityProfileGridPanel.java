
package com.speno.xedm.gui.frontend.client.admin.organization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.RecordList;
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
import com.speno.xedm.core.service.serials.SSecurityProfile;
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
import com.speno.xedm.gui.common.client.util.ServiceUtil;
import com.speno.xedm.gui.common.client.util.Util;

/**
 * SecurityProfile Grid Panel
 * 
 * @author 박상기
 * @since 1.0
 */
public class SecurityProfileGridPanel extends VLayout {	
	private static HashMap<String, SecurityProfileGridPanel> instanceMap = new HashMap<String, SecurityProfileGridPanel>();
	
	private ListGrid grid;
	private DynamicForm form;
	private HLayout actionHL;
	
	// kimsoeun GS인증용 - 변경사항 있는지 유효성 검사
	private List oldForm = new ArrayList();
	private IsNotChangedValidator isNotChangedValidator = new IsNotChangedValidator();
		
	private RecordObserver recordObserver;
	
	/**
	 * 기 생성된 instance를 반환함. instance가 없을 경우 생성 후 반환함
	 * @param id
	 * @param subTitle
	 * @return
	 */
	public static SecurityProfileGridPanel get(final String id, final String subTitle) {
		return get(id, subTitle, null, "100%");
	} 
	
	/**
	 * 기 생성된 instance를 반환함. instance가 없을 경우 생성 후 반환함
	 * @param id
	 * @param subTitle
	 * @param ob
	 * @param width
	 * @return
	 */
	public static SecurityProfileGridPanel get(final String id, final String subTitle, final RecordObserver ob, final String width) {
		if (instanceMap.get(id) == null) {
			new SecurityProfileGridPanel(id, subTitle, ob, width);
		}
		return instanceMap.get(id);
	} 
	
	public SecurityProfileGridPanel(final String id, final String subTitle) {
		this(id, subTitle, null, "100%");
	}
	
	public SecurityProfileGridPanel(final String id, final String subTitle, final RecordObserver ob, final String width) {
		instanceMap.put(id, this);
		
		this.recordObserver = ob;
		
		/* Sub Title 생성 */
		Label subTitleLabel = new Label();
		subTitleLabel.setAutoHeight();   
		subTitleLabel.setAlign(Alignment.LEFT);   
		subTitleLabel.setValign(VerticalAlignment.CENTER);   
		subTitleLabel.setStyleName("subTitle");
		subTitleLabel.setContents(subTitle);
		
		grid = new ListGrid();
		grid.setWidth100();		
		grid.setHeight100();
		grid.setShowAllRecords(true);
		
		grid.setEmptyMessage(I18N.message("notitemstoshow"));
		grid.setCanFreezeFields(true);
		grid.setCanRemoveRecords(true);
		grid.setSelectionType(SelectionStyle.SINGLE);
		grid.invalidateCache();
		
		ListGridField idField = new ListGridField("id", I18N.message("id"), 80);
		idField.setHidden(true);	// 20130816, junsoo, hide id
		ListGridField nameField = new ListGridField("name", I18N.message("name"), 150);
		ListGridField descField = new ListGridField("description", I18N.message("description"));
//		ListGridField expiredField = new ListGridField("expiredDay", I18N.message("expirationDate"));
		ListGridField expiredField = new ListGridField("expired", I18N.message("termination"));
		descField.setWidth("*");
		expiredField.setHidden(false);
		
		grid.setFields(idField, nameField, descField, expiredField);		
    	
    	//record click event handler 정의--------------------------------------------------------------
		grid.addRecordClickHandler(new RecordClickHandler() {
			@Override
            public void onRecordClick(RecordClickEvent event) {
				recordClick(event.getRecord());
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
							executeRemove(Long.parseLong(record.getAttributeAsString("id")));
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
        setWidth(width);
        
        executeFetch();
	}

	/**
	 *  하단 상세 Form 생성
	 * @return VLayout
	 */
	private VLayout createFormVL() {		
		//left duty 상세폼의 item 정의
		TextItem idItem = new TextItem("id", I18N.message("id"));
		idItem.setWrapTitle(false);
		idItem.setColSpan(3);
		idItem.setWidth("*");
		idItem.disable();
		idItem.setCanEdit(false);
		idItem.setTooltip(I18N.message("generatedbyserver", idItem.getTitle()));
		idItem.setEndRow(true);
		
		TextItem dutyNmItem = new TextItem("name", I18N.message("name"));
		dutyNmItem.setColSpan(3);
		dutyNmItem.setWidth("*");
		dutyNmItem.setCanEdit(true);
		dutyNmItem.setWrapTitle(false);
//		dutyNmItem.setLength(Constants.MAX_LEN_NAME);
		dutyNmItem.setValidators(new LengthValidator(dutyNmItem, Constants.MAX_LEN_NAME));
		dutyNmItem.setRequired(true);
		dutyNmItem.setEndRow(true);
		
		TextItem descItem = new TextItem("description", I18N.message("description"));
		descItem.setWrapTitle(false);
		descItem.setColSpan(3);
		descItem.setWidth("*");
		descItem.setCanEdit(true);		
		descItem.setEndRow(true);
//		descItem.setLength(Session.get().getInfo().getIntConfig("gui.description.fieldsize", 1000));
		descItem.setValidators(new LengthValidator(descItem, Session.get().getInfo().getIntConfig("gui.description.fieldsize", 1000)));
		
		form = new DynamicForm();
		form.setWidth100();
		form.setMargin(4);	
		form.setNumCols(3);
		form.setColWidths("1","1","1");

		form.setItems(
    			idItem, 
    			dutyNmItem, 
    			descItem
    			);
    	form.reset();    	
    	
    	VLayout formVL = new VLayout(50);
    	formVL.setBorder("1px solid gray");
    	formVL.setWidth100();
    	formVL.setAutoHeight();
    	formVL.addMembers(form);
    	
    	return formVL;
	}

	/**
	 * Left  Group Action 패널 생성
	 * @return HLayout
	 */
	private HLayout createActHL() {		
		Button btnAddNew = new Button(I18N.message("addnew"));
		btnAddNew.setIcon(ItemFactory.newImgIcon("page_white_add.png").getSrc());
		btnAddNew.addClickHandler(new ClickHandler() {   
            public void onClick(ClickEvent event) {
            	addNew();
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
	
	private void setSecurityProfile(SSecurityProfile profile){
		if(form.getValueAsString("id") == null){
			profile.setId(0L);
		}else{
			profile.setId(Long.parseLong(form.getValueAsString("id")));
		}
		profile.setName(form.getValueAsString("name"));
		profile.setDescription(form.getValueAsString("description"));
	}
	
	/**
	 * Group 추가
	 */
	private void executeAdd() {
		Log.debug("[ SecurityProfileGridPanel executeAdd ]");
		
		SSecurityProfile profile = new SSecurityProfile();
		setSecurityProfile(profile);
	
		ServiceUtil.security().saveSecurityProfile(Session.get().getSid(), profile, new AsyncCallbackWithStatus<SSecurityProfile>() {
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
			public void onSuccessEvent(SSecurityProfile result) {
				Log.debug("[ SecurityProfileGridPanel executeAdd ] onSuccess. id["+result.getId()+"]");
				
				ListGridRecord addRecord = new ListGridRecord();
				addRecord.setAttribute("id", result.getId());
				addRecord.setAttribute("name", result.getName());			
				addRecord.setAttribute("description", result.getDescription());
							
				grid.addData(addRecord);
				
				int selectedRowNum = grid.getRecordIndex(addRecord);						
				grid.selectSingleRecord(selectedRowNum);
				grid.scrollToRow(selectedRowNum);						
				if(recordObserver != null) {
					String id = Util.getSafeString(addRecord.getAttributeAsString("id"));		
					String parentId = Util.getSafeString(addRecord.getAttributeAsString("parentId"));					
					recordObserver.onRecordSelected(id, parentId);
        		}
				
				form.reset();
            	form.editRecord(addRecord);
            	form.getItem("id").setTooltip(I18N.message("fieldisreadonly", form.getItem("id").getTitle()));
            	SC.say(I18N.message("operationcompleted"));
            	
            	
			}
		});
	}
	
	/**
	 * 
	 */
	private void executeUpdate() {
		Log.debug("[ SecurityProfileGridPanel executeUpdate ]");
		
		SSecurityProfile profile = new SSecurityProfile();
		setSecurityProfile(profile);
		
		// kimsoeun GS인증용 - 변경사항 여부 확인
		List newForm = new ArrayList();
		newForm.add(profile.getId());
		newForm.add(profile.getName());
		newForm.add(profile.getDescription());
		
		int changed = isNotChangedValidator.check2(newForm, oldForm);
		if(changed==newForm.size()) {
			SC.say(I18N.message("nothingchanged"));
			return;
		}
		
		ServiceUtil.security().saveSecurityProfile(Session.get().getSid(), profile, new AsyncCallbackWithStatus<SSecurityProfile>() {
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
			public void onSuccessEvent(SSecurityProfile result) {
				Log.debug("[ SecurityProfileGridPanel executeUpdate ] onSuccess. id["+result.getId()+"]");
				
				final int selectedRowNum = grid.getRecordIndex(grid.getSelectedRecord());				
				ListGridRecord selectedRecord = grid.getSelectedRecord();
				selectedRecord.setAttribute("name", result.getName());
				selectedRecord.setAttribute("description", result.getDescription());
				
				grid.getDataAsRecordList().set(selectedRowNum, selectedRecord);				
				grid.selectSingleRecord(selectedRowNum);
				grid.scrollToRow(selectedRowNum);
//				SC.say(I18N.message("operationcompleted"));		
				// kimsoeun GS인증용  - 작업 완료 -> 저장 완료 문구 변경
				SC.say(I18N.message("savecompleted"));	
				
				// kimsoeun GS인증용 - 변경사항 여부 체크할 리스트 생성
				oldForm.removeAll(oldForm);
				oldForm.add(grid.getSelectedRecord().getAttribute("id"));
				oldForm.add(grid.getSelectedRecord().getAttribute("name"));
				oldForm.add(grid.getSelectedRecord().getAttribute("description"));
			}
		});
	}	
	
	/**
	 * 
	 */
	private void executeRemove(final long id) {
		Log.debug("[ SecurityProfileGridPanel executeRemove ] id["+id+"]");
		if(id < 0) return;
		
		ServiceUtil.security().deleteSecurityProfile(Session.get().getSid(), id, new AsyncCallbackWithStatus<Void>() {
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
				Log.debug("[ SecurityProfileGridPanel executeRemove ] onSuccess. id["+id+"]");
				
				grid.removeSelectedData();
				form.editNewRecord();
            	form.reset();
				form.getItem("id").setTooltip(I18N.message("generatedbyserver", form.getItem("id").getTitle()));
				SC.say(I18N.message("operationcompleted"));
				
				if(recordObserver != null) {
					recordObserver.onRecordSelected(Constants.INVALID_LONG, Constants.INVALID_LONG);
        		}
			}
		});
	}
	
	private void resetGridAndForm() {
		grid.setData(new ListGridRecord[0]);		
		form.reset();
		form.editNewRecord();
	}
	
	/**
	 * Add New 버튼의 클릭 이벤트 핸들러
	 */
	private void addNew() {
		form.getItem("id").setTooltip(I18N.message("generatedbyserver", form.getItem("id").getTitle()));
    	form.editNewRecord();
    	form.reset();
    	grid.deselectAllRecords();    	
    	if(recordObserver != null) {
    		recordObserver.onRecordSelected(Constants.INVALID_LONG, Constants.INVALID_LONG);
		}
	}
	
	private void executeFetch()	{
		Log.debug("[ SecurityProfileGridPanel executeFetch ]");
		resetGridAndForm();
		addNew();
		
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
			public void onFailureEvent(Throwable caught) {
				SCM.warn(caught);
			}
			@Override
			public void onSuccessEvent(List<SSecurityProfile> result) {
				setData(result);
			}
		});
	}
	
	/**
	 * 수신한 SSecurityProfile 정보를 Grid에 Set
	 * @param result
	 */
	private void setData(List<SSecurityProfile> result) {		
		/*
		 * id가 100이하인 Security Profile은 18N에서 관리되는 profile로 본 패널에서
		 * 관리하지 않음.
		 */
		
		List<ListGridRecord> reFinedRecords = new ArrayList<ListGridRecord>();
	
				
		for (int j = 0; j < result.size(); j++) {			
			long id = result.get(j).getId();
			SSecurityProfile secure_temp = result.get(j);
//			Date date = null;
//			try {
//			SRight[] SR_temp = secure_temp.getRights();
//			date = SR_temp[0].getExpiredday();
//			} catch (Exception e) {
//				// TODO: handle exception
//				date = null;		
//				}

			// 20140319, junsoo, 만료여부만 표시
			
			if(id > 100) {
				ListGridRecord record = new ListGridRecord();
				record.setAttribute("id", id);
				record.setAttribute("name", result.get(j).getName());
				record.setAttribute("description", result.get(j).getDescription());			
//				record.setAttribute("expiredDay",date);
				if (secure_temp.isExpired())
					record.setAttribute("expired",I18N.message("termination"));
				reFinedRecords.add(record);
			}
		}
		
		ListGridRecord[] records = new ListGridRecord[reFinedRecords.size()];
		records = reFinedRecords.toArray(records);
		grid.setData(records);
		
		if (reFinedRecords.size() > 0) {
			grid.selectRecord(records[0]);
			recordClick(records[0]);
		}
	}
	
	private void recordClick(Record record) {
    	form.getItem("id").setTooltip(I18N.message("fieldisreadonly", form.getItem("id").getTitle()));
    	form.reset();
    	form.editRecord(record);
    	
    	// kimsoeun GS인증용 - 변경사항 여부 체크할 리스트 생성
    	isNotChangedValidator.setList(form, oldForm);
    	
    	if(recordObserver != null) {
    		//recordObserver.onRecordSelected(Long.parseLong(grid.getSelectedRecord().getAttributeAsString("id")));
    		
    		long id = Util.getAslong(record.getAttributeAsString("id"));		
			long parentId = Util.getAslong(record.getAttributeAsString("parentId"));
			recordObserver.onRecordSelected(id, parentId);
		}
	}
}