package com.speno.xedm.gui.frontend.client.admin.organization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.Record;
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
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.DataArrivedEvent;
import com.smartgwt.client.widgets.grid.events.DataArrivedHandler;
import com.smartgwt.client.widgets.grid.events.RecordClickEvent;
import com.smartgwt.client.widgets.grid.events.RecordClickHandler;
import com.smartgwt.client.widgets.grid.events.RecordDoubleClickEvent;
import com.smartgwt.client.widgets.grid.events.RecordDoubleClickHandler;
import com.smartgwt.client.widgets.grid.events.RemoveRecordClickEvent;
import com.smartgwt.client.widgets.grid.events.RemoveRecordClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.speno.xedm.core.service.serials.SGroup;
import com.speno.xedm.core.service.serials.SInfo;
import com.speno.xedm.gui.common.client.Constants;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.RecordObserver;
import com.speno.xedm.gui.common.client.Session;
import com.speno.xedm.gui.common.client.data.GroupsDS;
import com.speno.xedm.gui.common.client.util.AsyncCallbackWithStatus;
import com.speno.xedm.gui.common.client.util.IsNotChangedValidator;
import com.speno.xedm.gui.common.client.util.ItemFactory;
import com.speno.xedm.gui.common.client.util.LengthValidator;
import com.speno.xedm.gui.common.client.util.SCM;
import com.speno.xedm.gui.common.client.util.ServiceUtil;
import com.speno.xedm.gui.common.client.util.Util;

/**
 * GroupGrid Panel
 * 
 * @author 박상기
 * @since 1.0
 */
public class GroupGridPanel extends VLayout {	
	private static HashMap<String, GroupGridPanel> instanceMap = new HashMap<String, GroupGridPanel>();
	
	private boolean firstTime = true;
	private int groupType = SGroup.TYPE_DUTY;
	
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
	 * @param type
	 * @param subTitle
	 * @return
	 */
	public static GroupGridPanel get(final String id, final long type,  final String subTitle) {
		return get(id, type, subTitle, null, true, "100%");
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
	public static GroupGridPanel get(final String id, final long type, final String subTitle, final RecordObserver ob, final boolean isAction, final String width) {
		if (instanceMap.get(id) == null) {
			new GroupGridPanel(id, type, subTitle, ob, isAction, width);
		}
		return instanceMap.get(id);
	} 
	
	public GroupGridPanel(final String id, final long type,  final String subTitle) {
		this(id, type, subTitle, null, true, "100%");
	}
	
	public GroupGridPanel(final String id, final long type,  final String subTitle, final RecordObserver ob, final boolean isAction, final String width) {
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
    	
    	/************/
		grid.setDataSource(new GroupsDS(String.valueOf(type)));
		grid.setAutoFetchData(true);
		/***********/
		
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
		ListGridField nameField = new ListGridField("name", I18N.message("name"), 80);
		ListGridField descField = new ListGridField("description", I18N.message("description"), 255);
//		descField.setHidden(true);
		ListGridField personsCountField = new ListGridField("personsCount", I18N.message("personscount"), 40);
		descField.setWidth("*");
		if(!("admin.org.position".equals(id) ||"admin.org.duty".equals(id)))personsCountField.setHidden(true);
		
		grid.setFields(idField, nameField, descField, personsCountField);
		
		if(isAction) {
	    	//record click event handler 정의--------------------------------------------------------------
			grid.addRecordClickHandler(new RecordClickHandler() {
				@Override
	            public void onRecordClick(RecordClickEvent event) {
					form.getItem("id").setTooltip(I18N.message("fieldisreadonly", form.getItem("id").getTitle()));
	            	form.reset();
	            	form.editRecord(event.getRecord());
	            	
	            	// kimsoeun GS인증용 - 변경사항 여부 체크할 리스트 생성
	            	isNotChangedValidator.setList(form, oldForm);
	            	
	            	if(recordObserver != null) {
	            		String id = Util.getSafeString(grid.getSelectedRecord().getAttributeAsString("id"));
						String parentId = Util.getSafeString(grid.getSelectedRecord().getAttributeAsString("parentId"));					
						recordObserver.onRecordSelected(id, parentId);
	        		}
	            }   
	        });
			
			//record 삭제 event handler 정의--------------------------------------------------------------
			grid.addRemoveRecordClickHandler(new RemoveRecordClickHandler() {
				@Override
				public void onRemoveRecordClick(final RemoveRecordClickEvent event) {				
	            	
	            	//2013.05.06 Member 있으면 삭제 안되게.
	        		if(recordObserver != null) {
	        			if(recordObserver.isExistMember()) {
	        				SC.say(I18N.message("existmembercannotdeleted"));
	        				event.cancel();
	        				return;
	        			}
	        		}
	        		
					SC.confirm(I18N.message("confirmdelete"),  new BooleanCallback() {
						@Override
						public void execute(Boolean value) {
							if(value != null && value) {
								ListGridRecord record = grid.getRecord( event.getRowNum());
								form.reset();
								form.editRecord(record);
								executeRemove(record.getAttributeAsString("id"));
							}
						}
					});
					event.cancel();
				}
			});
			
			//record 수신 event handler 정의--------------------------------------------------------------
			grid.addDataArrivedHandler(new DataArrivedHandler() {
				@Override
				public void onDataArrived(DataArrivedEvent event) {
					if (firstTime) {
						firstTime = false;
						if( event.getEndRow() > 0) {
							grid.selectSingleRecord(0);						
							if(recordObserver != null) {
								String id = Util.getSafeString(grid.getSelectedRecord().getAttributeAsString("id"));
								String parentId = Util.getSafeString(grid.getSelectedRecord().getAttributeAsString("parentId"));					
								recordObserver.onRecordSelected(id, parentId);
			        		}
							
							form.reset();
			            	form.editRecord(grid.getSelectedRecord());
			            	
			            	// kimsoeun GS인증용 - 변경사항 여부 체크할 리스트 생성
			            	isNotChangedValidator.setList(form, oldForm);
			            	
			            	form.getItem("id").setTooltip(I18N.message("fieldisreadonly", form.getItem("id").getTitle()));
						}
					}
				}
			});
		}
		/* 쓰는곳 없는데 왜 해 놨지? 후반가서 삭제해라.
		else {
			idField = new ListGridField("id", I18N.message("id"), 80);
			idField.setHidden(true);
			nameField = new ListGridField("name", I18N.message("name"), 150);
			descField = new ListGridField("description", I18N.message("description"));
			descField.setHidden(true);
		}
		*/
		
		VLayout groupVL = new VLayout(5);
		groupVL.addMember(grid);
		
		if(isAction) {
			groupVL.addMember(createFormVL());
			groupVL.addMember(createActHL());
		}
		
		setMembersMargin(Constants.SUBTITLE_MARGIN);
        addMember(groupVL);
        setWidth(width);
	}
	
	/**
	 * 그룹 Type 설정
	 * @param groupType
	 */
	protected void setGroupType(int groupType) {
		this.groupType = groupType;
	}
	
	public ListGrid getGrid() {
		return grid;
	}

	/**
	 *  Left Group 폼 패널 생성
	 * @return VLayout
	 */
	private VLayout createFormVL() {
		
		//left duty 상세폼의 item 정의
		TextItem idItem = new TextItem("id", I18N.message("id"));
		idItem.setWidth("*");
		idItem.disable();
		idItem.setCanEdit(false);		
		idItem.setTooltip(I18N.message("generatedbyserver", idItem.getTitle()));
		
		TextItem dutyNmItem = new TextItem("name", I18N.message("name"));
		dutyNmItem.setWidth("*");
		dutyNmItem.setCanEdit(true);
		dutyNmItem.setWrapTitle(false);
//		dutyNmItem.setLength(Constants.MAX_LEN_NAME);
		dutyNmItem.setValidators(new LengthValidator(dutyNmItem, Constants.MAX_LEN_NAME));
		dutyNmItem.setRequired(true);
		// kimsoeun GS인증용 - 툴팁 다국어화
		dutyNmItem.setRequiredMessage(I18N.message("fieldisrequired"));
		
		TextItem descItem = new TextItem("description", I18N.message("description"));
		descItem.setWidth("*");
		descItem.setCanEdit(true);	
//		descItem.setLength(Constants.MAX_LEN_DESC);
		descItem.setValidators(new LengthValidator(descItem, Constants.MAX_LEN_DESC));
		
		form = new DynamicForm();
		form.setWidth100();
		form.setMargin(4);		
    	form.setItems(idItem, dutyNmItem, descItem);
    	form.reset();    	
    	
		System.out.println(oldForm.size());
    	
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
		SInfo info = com.speno.xedm.gui.common.client.Session.get().getInfo();
		String lock = info.getConfig("gui.lock.hrinterface");
		
		Button btnAddNew = new Button(I18N.message("addnew"));
		if(lock.equals("true"))
		btnAddNew.disable();
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
		if(lock.equals("true"))
		btnSave.disable();
		btnSave.setIcon(ItemFactory.newImgIcon("data_into.png").getSrc());
		btnSave.addClickHandler(new ClickHandler() {   
            public void onClick(ClickEvent event) {
            	if(form.getValue("id") == null) {
            		 if(form.validate() && duplicationValidation()) {
            			 executeAdd();
            		 }
            	}
            	else {
            		 if(form.validate() && duplicationValidation()) {
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
	 * 데이터 무결성 체크
	 * @return
	 */
	private boolean duplicationValidation(){
		String inputedNameValue = form.getValueAsString("name");
		
		// 선택 정보 무결성 체크
		// 데이터 수정시 수정하지 않고 저장을 눌렀을 경우 
		// 	1. 에러 메시지 방지하기 위해서 체크.
		if(grid.getSelectedRecord() != null){
			if(inputedNameValue.equals(grid.getSelectedRecord().getAttributeAsString("name"))){
				return true;
			}
		}
		
		// 전체 무결성 체크
		ListGridRecord [] records = grid.getRecords();
		String comparedNameValue = "";
		
		for (ListGridRecord record : records) {
			comparedNameValue = record.getAttributeAsString("name");
			if(inputedNameValue.equals(comparedNameValue)){
				returnBeforeValue(true);
				return false;
			}
		}
		return true;
	}
	
	private void returnBeforeValue(boolean showMessage){
		if(showMessage)	SC.warn(I18N.message("dupmessage"));
		form.setValue("name", grid.getSelectedRecord().getAttribute("name"));
		form.setValue("description", grid.getSelectedRecord().getAttribute("description"));
	}
	
	/**
	 * Group 추가
	 */
	private void executeAdd() {
		GWT.log("[ GroupGridPanel executeAdd ]", null);
		
		SGroup group = new SGroup();
		group.setType(groupType);
		group.setId("");
		group.setName(form.getValueAsString("name"));
		group.setDescription(form.getValueAsString("description"));
		
		ServiceUtil.security().saveGroup(Session.get().getSid(), group, new AsyncCallbackWithStatus<SGroup>() {
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
			public void onSuccessEvent(SGroup result) {
				GWT.log("[ GroupGridPanel executeAdd ] onSuccess. groupId["+result.getId()+"]", null);
				
				ListGridRecord addRecord = new ListGridRecord();
				addRecord.setAttribute("id", result.getId());
				addRecord.setAttribute("name", result.getName());
				addRecord.setAttribute("description", result.getDescription());
				
				grid.addData(addRecord, new DSCallback() {
					@Override
					public void execute(DSResponse response, Object rawData, DSRequest request) {
						Record addedRecord = response.getData()[0];
						final int selectedRowNum = grid.getRecordIndex(addedRecord);						
						grid.selectSingleRecord(selectedRowNum);
						grid.scrollToRow(selectedRowNum);
						if(recordObserver != null) {
							//추가된 duty의 할당된 users를 재조회 함.
							String id = Util.getSafeString(addedRecord.getAttributeAsString("id"));		
							String parentId = Util.getSafeString(addedRecord.getAttributeAsString("parentId"));							
							recordObserver.onRecordSelected(id, parentId);
		        		}
						
						form.reset();
		            	form.editRecord(addedRecord);
		            	form.getItem("id").setTooltip(I18N.message("fieldisreadonly", form.getItem("id").getTitle()));
		            	SC.say(I18N.message("operationcompleted"));
					}
				});
			}
		});
	}
	
	/**
	 * Group 수정
	 */
	private void executeUpdate() {
		GWT.log("[ GroupGridPanel executeUpdate ]", null);
		
		SGroup group = new SGroup();
		group.setType(groupType);
		group.setId(form.getValueAsString("id"));
		group.setName(form.getValueAsString("name"));
		group.setDescription(form.getValueAsString("description"));
		
		// kimsoeun GS인증용 - 변경사항 여부 확인
		List newForm = new ArrayList();
		newForm.add(group.getId());
		newForm.add(group.getName());
		newForm.add(group.getDescription());
		
		int changed = isNotChangedValidator.check2(newForm, oldForm);
		if(changed==newForm.size()) {
			SC.say(I18N.message("nothingchanged"));
			return;
		}
		
		
		ServiceUtil.security().saveGroup(Session.get().getSid(), group, new AsyncCallbackWithStatus<SGroup>() {
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
			public void onSuccessEvent(SGroup result) {
				GWT.log("[ GroupGridPanel executeUpdate ] onSuccess. groupId["+result.getId()+"]", null);
				
				final int selectedRowNum = grid.getRecordIndex(grid.getSelectedRecord());				
				final ListGridRecord selectedRecord = grid.getSelectedRecord();
				selectedRecord.setAttribute("name", result.getName());
				selectedRecord.setAttribute("description", result.getDescription());
				
				grid.updateData(selectedRecord, new DSCallback() {
					@Override
					public void execute(DSResponse response, Object rawData, DSRequest request) {
						grid.selectSingleRecord(selectedRowNum);
						grid.scrollToRow(selectedRowNum);
						//수정시엔 재조회 하지 않음.
//						SC.say(I18N.message("operationcompleted"));
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
		});
	}	
	
	/**
	 * Group 삭제
	 * @param groupId
	 */
	private void executeRemove(final String groupId)	{
		GWT.log("[ GroupGridPanel executeRemove ] groupId["+groupId+"]", null);
		if(groupId == null || groupId.length() < 1) return;
		
		ServiceUtil.security().deleteGroup(Session.get().getSid(), groupId, new AsyncCallbackWithStatus<Void>() {
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
				GWT.log("[ GroupGridPanel executeRemove ] onSuccess. groupId["+groupId+"]", null);
				
				if(recordObserver != null) {
					//그리드에서 삭제하기전 이전에 선택된 duty의 할당된 Users 재조회 함.
					//서버에서 삭제되었다면 조회되는 User는 없을 것임.
					recordObserver.onRecordSelected(groupId, Constants.INVALID_LONG);
        		}
				grid.removeSelectedData();
				
				form.editNewRecord();
            	form.reset();
            	form.getItem("id").setTooltip(I18N.message("generatedbyserver", form.getItem("id").getTitle()));
				SC.say(I18N.message("operationcompleted"));
			}
		});
	}
}
