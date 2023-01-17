package com.speno.xedm.gui.frontend.client.admin.organization;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.RecordList;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.GroupStartOpen;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.CheckboxItem;
import com.smartgwt.client.widgets.form.fields.DateItem;
import com.smartgwt.client.widgets.form.fields.HiddenItem;
import com.smartgwt.client.widgets.form.fields.PickerIcon;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.SpacerItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.form.fields.events.FormItemClickHandler;
import com.smartgwt.client.widgets.form.fields.events.FormItemIconClickEvent;
import com.smartgwt.client.widgets.grid.GroupNode;
import com.smartgwt.client.widgets.grid.GroupTitleRenderer;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.ChangeEvent;
import com.smartgwt.client.widgets.grid.events.ChangeHandler;
import com.smartgwt.client.widgets.grid.events.RecordClickEvent;
import com.smartgwt.client.widgets.grid.events.RecordClickHandler;
import com.smartgwt.client.widgets.grid.events.RecordDoubleClickEvent;
import com.smartgwt.client.widgets.grid.events.RecordDoubleClickHandler;
import com.smartgwt.client.widgets.grid.events.RemoveRecordClickEvent;
import com.smartgwt.client.widgets.grid.events.RemoveRecordClickHandler;
import com.smartgwt.client.widgets.grid.events.SelectionUpdatedEvent;
import com.smartgwt.client.widgets.grid.events.SelectionUpdatedHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.speno.xedm.core.service.serials.SRight;
import com.speno.xedm.core.service.serials.SSecurityProfile;
import com.speno.xedm.gui.common.client.Constants;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.RecordObserver;
import com.speno.xedm.gui.common.client.Session;
import com.speno.xedm.gui.common.client.log.Log;
import com.speno.xedm.gui.common.client.util.AsyncCallbackWithStatus;
//import com.speno.xedm.gui.common.client.services.FolderService;
import com.speno.xedm.gui.common.client.util.ItemFactory;
import com.speno.xedm.gui.common.client.util.ReturnHandler;
import com.speno.xedm.gui.common.client.util.SCM;
import com.speno.xedm.gui.common.client.util.ServiceUtil;
import com.speno.xedm.gui.common.client.util.Util;
import com.speno.xedm.gui.frontend.client.admin.CommSearchDialog;
import com.speno.xedm.gui.frontend.client.admin.CommSearchDialog.ResultHandler;
import com.speno.xedm.gui.frontend.client.folder.GroupWindow;

/**
 * SecurityACL Grid Panel
 * 
 * @author �ڻ��
 * @since 1.0
 */
public class SecurityACLGridPanel extends VLayout {	
	
	private static HashMap<String, SecurityACLGridPanel> instanceMap = new HashMap<String, SecurityACLGridPanel>();
	
	private long profileId;
	
	private ListGrid grid;
	private DynamicForm aclTypeForm, aclDtlForm;
	private HLayout actionHL;
//	private CheckboxItem allItem, viewItem, printItem, readItem, writeItem, addItem, updateItem, deleteItem, checkoutItem, downloadItem, extendItem, controlItem;
		
	private LinkedHashMap<Integer, String> opts;
	private final RecordObserver recordObserver;
	
	//20130508
	private Button btnSave;
	
	//20130923 taesu ���� ���� ���� ��ư
//	private Button btnApply;
	
	// ���õ� �� ��ġ ��ȯ��
	private int rowNum;
	
//	private IsNotChangedValidator validator = new IsNotChangedValidator();
	
	private ChangedHandler changeHandler = new ChangedHandler() {
		@Override
		public void onChanged(ChangedEvent event) {
			// TODO Auto-generated method stub
			DateItem startDayItem = (DateItem) aclTypeForm.getItem("startday");
			DateItem expiredDayItem = (DateItem) aclTypeForm.getItem("expirationDate");
			
			//20131216 na ���� ������¥�� �񱳸� �ؾ� ���ϵ� ����� ������ �� ����.
			Date startDay = startDayItem.getValueAsDate();
			Date expiredDay = expiredDayItem.getValueAsDate();
			Date today = new Date();
			
			if(startDay.after(expiredDay)){
				SC.warn(I18N.message("youcantchooseday"));
				startDayItem.setValue(today);
				expiredDayItem.setValue(today);
			}
		}
	};
	/*
	 * grid.setCanEdit(false); ��忡�� �׸����� üũ�ڽ����� �������� �ʴ´�.
	 * �̸� �ذ��ϱ� ���� grid.setCanEdit(true);�� �ΰ� ���氡���� �ڵ鷯�� 
	 * �Ʒ��� ���� ����صΰ� readOnly ����� ������. 
	 */
	private RecordClickHandler recordClickHandler = new RecordClickHandler() {
		@Override
		public void onRecordClick(RecordClickEvent event) {
			rowNum = event.getRecordNum();
			recordClick();
//			event.cancel();
		}
	};
 
	/**
	 * �� ������ instance�� ��ȯ��. instance�� ���� ��� ���� �� ��ȯ��
	 * @param id
	 * @param subTitle
	 * @param profileId
	 * @return
	 */
	public static SecurityACLGridPanel get(
			final String id, 
			final String subTitle, 
			final long profileId) {
		return get(id, subTitle, null, "100%");
	} 
	
	/**
	 * �� ������ instance�� ��ȯ��. instance�� ���� ��� ���� �� ��ȯ��
	 * @param id
	 * @param subTitle
	 * @param ob
	 * @param width
	 * @return
	 */
	public static SecurityACLGridPanel get(
			final String id, 
			final String subTitle, 
			final RecordObserver ob, 
			final String width) {
		if (instanceMap.get(id) == null) {
			new SecurityACLGridPanel(id, subTitle, ob, width);
		}
		return instanceMap.get(id);
	} 
	
	public SecurityACLGridPanel(
			final String id, 
			final String subTitle) {
		this(id, subTitle, null, "100%");
	}
	
	public SecurityACLGridPanel(
			final String id, 
			final String subTitle,
			final RecordObserver ob, 
			final String width) {
		instanceMap.put(id, this);
		
		this.recordObserver = ob;
		
		/* Sub Title ���� */
		Label subTitleLable = new Label();
		subTitleLable.setAutoHeight();   
		subTitleLable.setAlign(Alignment.LEFT);   
		subTitleLable.setValign(VerticalAlignment.CENTER);   
		subTitleLable.setStyleName("subTitle");
		subTitleLable.setContents(subTitle);
		
		grid = new ListGrid();
		grid.setWidth100();
		grid.setHeight100();
		grid.setShowAllRecords(true);
		
		
		grid.setCanEdit(true);
		
		grid.setAutoFitFieldWidths(true);
		
		grid.setGroupStartOpen(GroupStartOpen.ALL);
		grid.setGroupByField("groupType");
		
		grid.setEmptyMessage(I18N.message("notitemstoshow"));
		grid.setCanRemoveRecords(true);
		grid.setSelectionType(SelectionStyle.SINGLE);
		grid.invalidateCache();
		
		int len = 66;
		
		ListGridField groupTypeField = new ListGridField("groupType");
		ListGridField entityIdField = new ListGridField("entityId");		
		ListGridField aclNmField = new ListGridField("name", I18N.message("name"), 150);
		ListGridField allField = new ListGridField("all", I18N.message("all"), len);
		ListGridField viewField = new ListGridField("view", I18N.message("view"), len);
		//20150509na GS������ ����Ʈ ����
//		ListGridField printField = new ListGridField("print", I18N.message("print"), len);
		ListGridField readField = new ListGridField("read", I18N.message("read"), len);
		ListGridField writeField = new ListGridField("write", I18N.message("write"), len);
		ListGridField addField = new ListGridField("add", I18N.message("add"), len);
		ListGridField updateField = new ListGridField("rename", I18N.message("update"), len);
		ListGridField deleteField = new ListGridField("pdelete", I18N.message("delete"), len);
		ListGridField checkoutField = new ListGridField("check", I18N.message("checkout"), len);
		ListGridField downloadField = new ListGridField("download", I18N.message("download"), len);
		ListGridField extendField = new ListGridField("extend", I18N.message("retention"), len);
		ListGridField controlField = new ListGridField("control", I18N.message("control"), len);
		
		groupTypeField.setCanEdit(false);
		entityIdField.setCanEdit(false);
		aclNmField.setCanEdit(false);
		// �б�� ���� �Ұ�!
		readField.setCanEdit(false);
		
		groupTypeField.setHidden(true);
		entityIdField.setHidden(true);
		
		allField.setType(ListGridFieldType.BOOLEAN);
		viewField.setType(ListGridFieldType.BOOLEAN);
		//20150509na GS������ ����Ʈ ����
//		printField.setType(ListGridFieldType.BOOLEAN);
		readField.setType(ListGridFieldType.BOOLEAN);
		writeField.setType(ListGridFieldType.BOOLEAN);
		addField.setType(ListGridFieldType.BOOLEAN);
		updateField.setType(ListGridFieldType.BOOLEAN);
		deleteField.setType(ListGridFieldType.BOOLEAN);
		checkoutField.setType(ListGridFieldType.BOOLEAN);
		viewField.setType(ListGridFieldType.BOOLEAN);
		downloadField.setType(ListGridFieldType.BOOLEAN);
		extendField.setType(ListGridFieldType.BOOLEAN);
		controlField.setType(ListGridFieldType.BOOLEAN);
		
		groupTypeField.setGroupTitleRenderer(new GroupTitleRenderer() {
			@Override
			public String getGroupTitle(Object groupValue, GroupNode groupNode, ListGridField field, String fieldName, ListGrid grid) {
				try {
					final int groupType = (Integer) groupValue;
					String baseTitle = String.valueOf(groupType);
					
					switch (groupType) {
					case SRight.GROUPTYPE_DUTY:
						baseTitle = I18N.message("duty");
						break;
					case SRight.GROUPTYPE_POSITION:
						baseTitle = I18N.message("position");
						break;
					case SRight.GROUPTYPE_GROUP:
						baseTitle = I18N.message("group");
						break;
					case SRight.GROUPTYPE_USERGROUP:
						baseTitle = I18N.message("user");
						break;
					}
					return baseTitle;
				} catch (Exception e) {
					return "";
				}
			}
	    });
		
//		viewField.addRecordClickHandler(recordClickHandler);
//		printField.addRecordClickHandler(recordClickHandler);
//		readField.addRecordClickHandler(recordClickHandler);
//		writeField.addRecordClickHandler(recordClickHandler);
//		addField.addRecordClickHandler(recordClickHandler);
//		updateField.addRecordClickHandler(recordClickHandler);
//		deleteField.addRecordClickHandler(recordClickHandler);
//		checkoutField.addRecordClickHandler(recordClickHandler);
//		downloadField.addRecordClickHandler(recordClickHandler);
//		extendField.addRecordClickHandler(recordClickHandler);
//		controlField.addRecordClickHandler(recordClickHandler);
		
		allField.setAlign(Alignment.CENTER);
		viewField.setAlign(Alignment.CENTER);
		//20150509na GS������ ����Ʈ ����
//		printField.setAlign(Alignment.CENTER);
		readField.setAlign(Alignment.CENTER);
		writeField.setAlign(Alignment.CENTER);
		addField.setAlign(Alignment.CENTER);
		updateField.setAlign(Alignment.CENTER);
		deleteField.setAlign(Alignment.CENTER);
		checkoutField.setAlign(Alignment.CENTER);
		downloadField.setAlign(Alignment.CENTER);
		extendField.setAlign(Alignment.CENTER);
		controlField.setAlign(Alignment.CENTER);
		
		//20150509na GS������ ����Ʈ ����
		grid.setFields(groupTypeField, entityIdField, aclNmField, allField, viewField, readField, writeField, addField, updateField, deleteField, checkoutField, downloadField, extendField, controlField);
//		grid.setFields(groupTypeField, entityIdField, aclNmField, allField, viewField, printField, readField, writeField, addField, updateField, deleteField, checkoutField, downloadField, extendField, controlField);
//		grid.setCanFreezeFields(true);
		allField.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				control(event.getRowNum());
			}
		});

		//record double click event handler ����-------------------------------------------------------
		grid.addRecordDoubleClickHandler(new RecordDoubleClickHandler() {
			@Override
			public void onRecordDoubleClick(RecordDoubleClickEvent event) {
				event.cancel();
			}
		});
		
    	//record click event handler
		grid.addRecordClickHandler(recordClickHandler);
		
		//record ���� event handler ����--------------------------------------------------------------
		grid.addRemoveRecordClickHandler(new RemoveRecordClickHandler() {
			@Override
			public void onRemoveRecordClick(final RemoveRecordClickEvent event) {
				event.cancel();
				SC.confirm(I18N.message("confirmdelete"),  new BooleanCallback() {
					@Override
					public void execute(Boolean value) {
						if(value != null && value) {
							ListGridRecord record = grid.getRecord(event.getRowNum());
							grid.removeData(record);
//				btnSave.enable();
							addNew();
						}
					}
				});
			}
		});
		
		/*
		 * Selection state ����.
		 * grid.setGroupByField(..); �� �����Ǿ� ���� ��� screen�� ���� �׷����� 
		 * selection state �� grouping ������ �ʱ�ȭ ������ �߻���.
		 * selection state�� ����ó����  
		 */		
//		grid.addDrawHandler(new DrawHandler() {
//			@Override
//			public void onDraw(DrawEvent event) {
//				correctSelectionRefGroupTypeItem();
//			}			
//		});
		
//		HLayout gridHL = new HLayout(5);
//		gridHL.setMembers( grid, createAclDtlForm());
		
		VLayout groupVL = new VLayout(5);
		final VLayout aclTypeVL = createAclTypeVL();
		groupVL.setMembers(grid, aclTypeVL, createActHL());
//		groupVL.setMembers(gridHL, createAclTypeVL(), createActHL());
		
		grid.addSelectionUpdatedHandler(new SelectionUpdatedHandler() {
			@Override
			public void onSelectionUpdated(SelectionUpdatedEvent event) {
				ListGridRecord sr = grid.getSelectedRecord();
				if (sr != null) {
					aclTypeVL.enable();
				} else {
					aclTypeVL.disable();
				}
			}
		});
		
		
		setMembersMargin(Constants.SUBTITLE_MARGIN);
        setMembers(subTitleLable, groupVL);
        setWidth(width);
        
        disable();
	}
	
	/**
	 *  Access Control List Form Panel ����
	 * @return VLayout
	 */
	private VLayout createAclTypeVL() {		
		opts = new LinkedHashMap<Integer, String>();
		opts.put(SRight.GROUPTYPE_DUTY, I18N.message("duty"));
		opts.put(SRight.GROUPTYPE_POSITION,I18N.message( "position"));
		opts.put(SRight.GROUPTYPE_GROUP, I18N.message("group"));
		opts.put(SRight.GROUPTYPE_USERGROUP, I18N.message("user"));
		
		final SelectItem groupTypeItem = new SelectItem("groupType", I18N.message("type"));
		groupTypeItem.setWrapTitle(false);
		groupTypeItem.setWidth(150);
		groupTypeItem.setType("combobox");
		groupTypeItem.setValueMap(opts);		
		groupTypeItem.setRequired(true);
		groupTypeItem.setEmptyDisplayValue(I18N.message("choosetype"));
		groupTypeItem.setCanEdit(false);
		groupTypeItem.addChangedHandler(new ChangedHandler() {
			@Override
			public void onChanged(ChangedEvent event) {
				aclTypeForm.getField("entityId").clearValue();
				aclTypeForm.getField("name").clearValue();
				ListGridRecord selectedRecord = grid.getSelectedRecord();
				//20140107na GWT ���� ����
				if(groupTypeItem.getValue() == null) return;
				grid.getSelectedRecord().setAttribute("groupType",	groupTypeItem.getValue());
				grid.getSelectedRecord().setAttribute("entityId",	"");
				grid.getSelectedRecord().setAttribute("name",	"");
				grid.ungroup();
				grid.groupBy("groupType");
				grid.selectRecord(selectedRecord);
//            	aclDtlForm.reset();
			}
		});
		
		HiddenItem entityIdItem = new HiddenItem("entityId");
		entityIdItem.setVisible(false);
		
		PickerIcon searchPicker = new PickerIcon(PickerIcon.SEARCH, new FormItemClickHandler() {   
            public void onFormItemClick(FormItemIconClickEvent event) {   
				Integer typeVal = (Integer) groupTypeItem.getValue();
				Object name = aclTypeForm.getField("name").getValue();
				if (typeVal == null) {
					SC.say(I18N.message("choosetype"));
					groupTypeItem.focusInItem();
				} else {
					switch (typeVal) {
					case CommSearchDialog.GROUP:
						GroupWindow groupWindow = new GroupWindow("single", new ReturnHandler() {
									@Override
									public void onReturn(Object param) {
										String[][] groupInfo = (String[][]) param;
										aclTypeForm.getField("entityId").setValue(groupInfo[0][0]);
										aclTypeForm.getField("name").setValue(groupInfo[0][1]);
										if (addNewValidation()) {
											grid.getSelectedRecord().setAttribute("entityId",aclTypeForm.getField("entityId").getValue());
											grid.getSelectedRecord().setAttribute("name",aclTypeForm.getField("name").getValue());
											grid.redraw();
										}
									}
								}, false, name);
						groupWindow.show();
						break;

					default:
						String typeName = "";
						switch (typeVal) {
						case -1:
							typeName = I18N.message("usersgroup");
							break;
						default:
							typeName = I18N.message("searchgroup");
						}
						
						final CommSearchDialog commSearchDialog = new CommSearchDialog(typeVal, typeName);
						
						commSearchDialog.addResultHandler(new ResultHandler() {
									@Override
									public void onSelected(HashMap<String, String> resultMap) {
										aclTypeForm.getField("entityId").setValue(resultMap.get("id"));
										aclTypeForm.getField("name")	.setValue(resultMap.get("name"));
										if (addNewValidation()) {
											// grid.getSelectedRecord().setAttribute("groupType",
											// I18N.message(aclTypeForm.getField("groupType").getValue().toString()));
											grid.getSelectedRecord()	.setAttribute("entityId", aclTypeForm.getField("entityId").getValue());
											grid.getSelectedRecord().setAttribute("name", aclTypeForm.getField("name").getValue());
											grid.redraw();
											grid.setSelectionType(SelectionStyle.SINGLE);
										}
									}
								});
							commSearchDialog.show();
							break;
						}
					}
				
				
				
				
				
				
            }   
        }); 
		
		TextItem nameItem = new TextItem("name", I18N.message("name"));
		nameItem.setWrapTitle(false);
		nameItem.setWidth("*");
		nameItem.setRequired(true);
		nameItem.setCanEdit(false);
//		nameItem.setDisabled(true);
		nameItem.setDisableIconsOnReadOnly(false);
		nameItem.setIcons(searchPicker);
		
		nameItem.setStartRow(false);
		nameItem.setEndRow(false);
		
		nameItem.addClickHandler(new com.smartgwt.client.widgets.form.fields.events.ClickHandler() {
		@Override
		public void onClick(
				com.smartgwt.client.widgets.form.fields.events.ClickEvent event) {
			Integer typeVal = (Integer) groupTypeItem.getValue();
			Object name = aclTypeForm.getField("name").getValue();
			if (typeVal == null) {
				SC.say(I18N.message("choosetype"));
				groupTypeItem.focusInItem();
			} else {
				switch (typeVal) {
				case CommSearchDialog.GROUP:
					GroupWindow groupWindow = new GroupWindow("single", new ReturnHandler() {
								@Override
								public void onReturn(Object param) {
									String[][] groupInfo = (String[][]) param;
									aclTypeForm.getField("entityId").setValue(groupInfo[0][0]);
									aclTypeForm.getField("name").setValue(groupInfo[0][1]);
									if (addNewValidation()) {
										grid.getSelectedRecord().setAttribute("entityId",aclTypeForm.getField("entityId").getValue());
										grid.getSelectedRecord().setAttribute("name",aclTypeForm.getField("name").getValue());
										grid.redraw();
									}
								}
							}, false, name);
					groupWindow.show();
					break;

				default:
					String typeName = "";
					switch (typeVal) {
					case -1:
						typeName = I18N.message("usersgroup");
						break;
					default:
						typeName = I18N.message("searchgroup");
					}
					
					final CommSearchDialog commSearchDialog = new CommSearchDialog(
							typeVal, typeName);
						commSearchDialog.addResultHandler(new ResultHandler() {
									@Override
									public void onSelected(HashMap<String, String> resultMap) {
										aclTypeForm.getField("entityId").setValue(resultMap.get("id"));
										aclTypeForm.getField("name")	.setValue(resultMap.get("name"));
										if (addNewValidation()) {
											// grid.getSelectedRecord().setAttribute("groupType",
											// I18N.message(aclTypeForm.getField("groupType").getValue().toString()));
											grid.getSelectedRecord()	.setAttribute("entityId", aclTypeForm.getField("entityId").getValue());
											grid.getSelectedRecord().setAttribute("name", aclTypeForm.getField("name").getValue());
											grid.redraw();
										}
									}
								});
						commSearchDialog.show();
						break;
					}
				}
		}
	});
		
		SpacerItem dummyItem = new SpacerItem();
		
		DateItem startDayItem = new DateItem("startday",I18N.message("startday"));
		startDayItem.setWrapTitle(false);
		startDayItem.setWidth(150);
		startDayItem.disable();
		
		DateItem expiredDayItem = new DateItem("expirationDate", I18N.message("expirationDate"));
		expiredDayItem.setWrapTitle(false);
		expiredDayItem.disable();
		
		CheckboxItem retentionCheckItem = new CheckboxItem("expirationDateCheck", I18N.message("validperiod"));
		retentionCheckItem.setWrapTitle(false);
		retentionCheckItem.setShowTitle(false);
		retentionCheckItem.setWidth("30");
	
		aclTypeForm = new DynamicForm();
		aclTypeForm.setWidth("*");
		aclTypeForm.setNumCols(6);
		aclTypeForm.setColWidths("1","1","1","1","1","*");
		aclTypeForm.setItems(groupTypeItem, entityIdItem, nameItem, dummyItem,
				startDayItem, expiredDayItem, retentionCheckItem);
		aclTypeForm.reset();
		
		VLayout actFormMarginVL = new VLayout();
		actFormMarginVL.setWidth100();
		actFormMarginVL.setAutoHeight();
		actFormMarginVL.setMargin(4);
		actFormMarginVL.addMembers(aclTypeForm);
		
		VLayout actFormVL = new VLayout();
		actFormVL.setBorder("1px solid gray");
		actFormVL.setWidth100();
		actFormVL.setAutoHeight();
		actFormVL.addMembers(actFormMarginVL);
		
		retentionCheckItem.addChangedHandler(new ChangedHandler() {
			@Override
			public void onChanged(ChangedEvent event) {
				// TODO Auto-generated method stub
				CheckboxItem checkboxItem = (CheckboxItem) event.getItem();
				DateItem startDayItem = (DateItem) aclTypeForm.getItem("startday");
				DateItem expiredDayItem = (DateItem) aclTypeForm.getItem("expirationDate");
				
				if(checkboxItem.getValueAsBoolean()){
					startDayItem.enable();
					expiredDayItem.enable();
				}
				else{
					startDayItem.disable();
					expiredDayItem.disable();
				}
			}
		});
		
		startDayItem.addChangedHandler(changeHandler);
		expiredDayItem.addChangedHandler(changeHandler);
		
    	return actFormVL;
	}
	
	/**
	 * All field�� üũ ���¿� ���� Field ������ �����Ѵ�. 
	 * @param row
	 */
	private void control(int row){
		boolean flag = Boolean.parseBoolean(grid.getSelectedRecord().getAttribute("all"));
		grid.getRecord(row).setAttribute("view", 	!flag);
		//20150509na GS������ ����Ʈ ����
//		grid.getRecord(row).setAttribute("print", 	!flag);
		//�ʼ� �Է°��̹Ƿ� row ������ �ڵ� true ����
		grid.getRecord(row).setAttribute("read", 	true);
		grid.getRecord(row).setAttribute("write", 	!flag);
		grid.getRecord(row).setAttribute("add", 	!flag);
		grid.getRecord(row).setAttribute("rename", 	!flag);
		grid.getRecord(row).setAttribute("pdelete", !flag);
		grid.getRecord(row).setAttribute("check", 	!flag);
		grid.getRecord(row).setAttribute("download",!flag);
		grid.getRecord(row).setAttribute("extend",	!flag);
		grid.getRecord(row).setAttribute("control", !flag);
		grid.refreshRow(row);
	}
	
	/**
	 * Selection state ����.
	 * 
	 * grid.setGroupByField(..); �� �����Ǿ� ���� ��� screen�� ���� �׷�����
	 * selection state �� grouping ������ �ʱ�ȭ ������ �߻���.
	 */
	private void correctSelectionRefGroupTypeItem() {
		if(!aclTypeForm.getField("groupType").getCanEdit()) { //Add New ���°� �ƴѰ��
			RecordList recordlist = grid.getRecordList();
    		if(!recordlist.isEmpty()) {
    			final Record record = recordlist.find("entityId", aclTypeForm.getValueAsString("entityId"));
    			if( record != null) {
    				grid.selectRecord(record);
    			}
    		}
		}
	}
	
	/**
	 * Record Click ó��
	 */
	public void recordClick() {
//		aclTypeForm.getField("groupType").setCanEdit(false);			
//		aclTypeForm.getField("name").setDisabled(true);
		recordSelected();
	}
	

	
	
	private boolean addNewValidation(){
		ListGridRecord[] records = grid.getRecords();
		boolean returnValue = true;
		
		int i=1;
		for (ListGridRecord record : records) {
			if(i!=rowNum && record.getAttribute("entityId").toString().equals(aclTypeForm.getField("entityId").getValue())){
				SC.warn(I18N.message("dupmessage"));
				return false;
			}
			i++;
		}
		return returnValue;
	}
	
	/**
	 * Access Control Check Button From Panel ����
	 * @return
	 */
//	private VLayout createAclDtlForm() {
//		allItem = new CheckboxItem("all", I18N.message("all"));
//		allItem.addChangedHandler(new ChangedHandler() {
//			@Override
//			public void onChanged(ChangedEvent event) {
//				dtlChkCrtlByAll();
//			}
//		});		
//		
//		viewItem = new CheckboxItem("view", I18N.message("view"));
//		viewItem.addChangedHandler(new ChangedHandler() {
//			@Override
//			public void onChanged(ChangedEvent event) {
//				allChkCrtlByEach();
//			}
//		});
//		
//		printItem = new CheckboxItem("print", I18N.message("print"));
//		printItem.addChangedHandler(new ChangedHandler() {
//			@Override
//			public void onChanged(ChangedEvent event) {
//				allChkCrtlByEach();
//			}
//		});
//		
//		readItem = new CheckboxItem("read", I18N.message("read"));
//		readItem.addChangedHandler(new ChangedHandler() {
//			@Override
//			public void onChanged(ChangedEvent event) {
//				dtlChkCrtlByRead();
//			}
//		});
//		readItem.setDefaultValue(true);
//		readItem.disable();
//		readItem.setTooltip(I18N.message("fieldisrequired", readItem.getTitle()));
//		
//		writeItem = new CheckboxItem("write", I18N.message("write"));
//		writeItem.addChangedHandler(new ChangedHandler() {
//			@Override
//			public void onChanged(ChangedEvent event) {
//				allChkCrtlByEach();
//			}
//		});
//		
//		addItem = new CheckboxItem("add", I18N.message("add"));
//		addItem.addChangedHandler(new ChangedHandler() {
//			@Override
//			public void onChanged(ChangedEvent event) {
//				allChkCrtlByEach();
//			}
//		});
//		
//		updateItem = new CheckboxItem("rename", I18N.message("update"));
//		updateItem.addChangedHandler(new ChangedHandler() {
//			@Override
//			public void onChanged(ChangedEvent event) {
//				allChkCrtlByEach();
//			}
//		});
//		
//		deleteItem = new CheckboxItem("pdelete",  I18N.message("delete"));
//		deleteItem.addChangedHandler(new ChangedHandler() {
//			@Override
//			public void onChanged(ChangedEvent event) {
//				allChkCrtlByEach();
//			}
//		});
//		
//		checkoutItem = new CheckboxItem("check", I18N.message("checkout"));
//		checkoutItem.addChangedHandler(new ChangedHandler() {
//			@Override
//			public void onChanged(ChangedEvent event) {
//				allChkCrtlByEach();
//			}
//		});
//		
//		downloadItem = new CheckboxItem("download", I18N.message("download"));
//		downloadItem.addChangedHandler(new ChangedHandler() {
//			@Override
//			public void onChanged(ChangedEvent event) {
//				allChkCrtlByEach();
//			}
//		});    	
//		extendItem = new CheckboxItem("extend", I18N.message("retention"));
//		extendItem.addChangedHandler(new ChangedHandler() {
//			@Override
//			public void onChanged(ChangedEvent event) {
//				allChkCrtlByEach();
//			}
//		});    	
//		controlItem = new CheckboxItem("control", I18N.message("control"));
//		controlItem.addChangedHandler(new ChangedHandler() {
//			@Override
//			public void onChanged(ChangedEvent event) {
//				allChkCrtlByEach();
//			}
//		});    	
//		
//		aclDtlForm = new DynamicForm();
//		aclDtlForm.setMinColWidth(80);
//		aclDtlForm.setAutoWidth();
//		aclDtlForm.setHeight100();
//		aclDtlForm.setItems(allItem, viewItem, printItem, readItem, writeItem, addItem, updateItem, deleteItem, checkoutItem, downloadItem, extendItem, controlItem);
//		aclDtlForm.reset();
//		
//		VLayout actFormVL = new VLayout();
//		actFormVL.setAlign(Alignment.LEFT);
//		actFormVL.setBorder("1px solid gray");
//		actFormVL.setAutoWidth();
//		actFormVL.setHeight100();
//		actFormVL.addMembers(aclDtlForm);
//		
//    	return actFormVL;
//	}
	
	/**
	 * Action Panel ����
	 * @return
	 */
	private HLayout createActHL() {		
		Button btnAddNew = new Button(I18N.message("addnew"));
		btnAddNew.setIcon(ItemFactory.newImgIcon("page_white_add.png").getSrc());
		btnAddNew.addClickHandler(new ClickHandler() {   
            public void onClick(ClickEvent event) {            	
            	addRow();
            }   
        });
		
//		Button btnUpload = new Button(I18N.message("updaterow"));
//		btnUpload.setIcon("[SKIN]/RichTextEditor/paste.png");
//		btnUpload.addClickHandler(new ClickHandler() {   
//            public void onClick(ClickEvent event) {           	
////            	if(aclTypeForm.validate() && aclDtlForm.validate()) {
//            	if(aclTypeForm.validate()) {
//            		RecordList recordlist = grid.getRecordList();
//            		if(recordlist.isEmpty()) {
//            			addRow();
//            		}
//            		else {
//            			if(aclTypeForm.getField("groupType").getCanEdit()) { //Add New ����
//            				
//            				final Record record = recordlist.find("entityId", aclTypeForm.getValueAsString("entityId"));
//                			if( record != null) {
//            					SC.confirm(I18N.message("itemoverwrite"),  new BooleanCallback() {
//            						@Override
//            						public void execute(Boolean value) {
//            							if(value != null && value) {
//            								updateRow(record);
//            							}
//            						}
//            					});
//                			}
//                			else {
//                				addRow();
//                			}
//            			}
//            			else {
//            				updateRow(grid.getSelectedRecord());
//            			}
//            		}
//            	}
//            }   
//        });
		
		btnSave = new Button(I18N.message("save"));
//		btnSave.disable();
		btnSave.setIcon(ItemFactory.newImgIcon("data_into.png").getSrc());
		btnSave.addClickHandler(new ClickHandler() {   
            public void onClick(ClickEvent event) {
            	SC.confirm(I18N.message("wanttosave"),  new BooleanCallback() {
					@Override
					public void execute(Boolean value) {
						if(value != null && value) {
							executeSave();
						}
					}
				});
			}
        });
		
//		btnApply = new Button(I18N.message("applyrights"));
//		btnApply.setIcon(ItemFactory.newImgIcon("data_into.png").getSrc());
//		btnApply.hide();

		actionHL = new HLayout(10);
		actionHL.setHeight(1);
//		actionHL.setMembers(btnAddNew, btnUpload, btnSave, btnApply);
//		actionHL.setMembers(btnAddNew, btnSave, btnApply);
		actionHL.setMembers(btnAddNew, btnSave);
		return actionHL;
	}
	
	/**
	 * ������ ACL Check ��ư�� ���� All ��ư �ڵ鸵
	 */
//	private void allChkCrtlByEach() {
//		allItem.setValue( viewItem.getValueAsBoolean() &&
//				printItem.getValueAsBoolean() &&
//				readItem.getValueAsBoolean() &&
//				writeItem.getValueAsBoolean()  &&
//				addItem.getValueAsBoolean()  &&
//				updateItem.getValueAsBoolean() &&
//				deleteItem.getValueAsBoolean() &&
//				checkoutItem.getValueAsBoolean() &&
//				extendItem.getValueAsBoolean() &&
//				controlItem.getValueAsBoolean() &&
//				downloadItem.getValueAsBoolean());
//	}
	
	/**
	 * Read Check ��ư�� Action�� ���� ó��
	 */
//	private void dtlChkCrtlByRead() {
//		if( readItem.getValueAsBoolean() ) {
//			allChkCrtlByEach();
//		}
//		else {
//			allItem.setValue(false);
//			viewItem.setValue(false);
//			printItem.setValue(false);
//			writeItem.setValue(false);
//			addItem.setValue(false);
//			updateItem.setValue(false);
//			deleteItem.setValue(false);
//			checkoutItem.setValue(false);
//			downloadItem.setValue(false);
//			extendItem.setValue(false);
//			controlItem.setValue(false);
//		}
//	}
//	
//	/**
//	 * All Check ��ư�� Action�� ���� ó��
//	 */
//	private void dtlChkCrtlByAll() {
//		viewItem.setValue(allItem.getValueAsBoolean());
//		printItem.setValue(allItem.getValueAsBoolean());
//		//readItem.setValue(allItem.getValueAsBoolean());
//		writeItem.setValue(allItem.getValueAsBoolean());
//		addItem.setValue(allItem.getValueAsBoolean());
//		updateItem.setValue(allItem.getValueAsBoolean());
//		deleteItem.setValue(allItem.getValueAsBoolean());
//		checkoutItem.setValue(allItem.getValueAsBoolean());
//		downloadItem.setValue(allItem.getValueAsBoolean());
//		extendItem.setValue(allItem.getValueAsBoolean());
//		controlItem.setValue(allItem.getValueAsBoolean());
//	}
	
	/**
	 * Add New ��ư�� Ŭ�� �̺�Ʈ �ڵ鷯
	 */
	private void addNew() {
		aclTypeForm.editNewRecord();
//		aclDtlForm.editNewRecord();
		aclTypeForm.reset();
//		aclDtlForm.reset();
		grid.deselectAllRecords();          
		aclTypeForm.getField("groupType").setCanEdit(true);
		aclTypeForm.getField("name").setDisabled(false);
	}
	
	/**
	 * Default �ʱ�ȭ
	 */
	public void reset() {
		this.profileId = Constants.INVALID_LONG;
		resetGridAndTypeDtlForm();
		setDisabled(true);
//		btnSave.disable();
	}
	
	/**
	 * �׸���� �� Form �ʱ�ȭ
	 */
	private void resetGridAndTypeDtlForm() {
		grid.setEmptyMessage(I18N.message("notitemstoshow"));
		grid.setData(new ListGridRecord[0]);
		addNew();
	}
	
	public void executeFetch(final long profileId)	{
		Log.debug("[ SecurityACLGridPanel executeFetch ] profileId["+profileId+"]");
		this.profileId = profileId;
		resetGridAndTypeDtlForm();
		setDisabled(false);
//		btnSave.disable();
		
		ServiceUtil.security().listRightsBySecurityProfileId(Session.get().getSid(), profileId, new AsyncCallbackWithStatus<List<SRight>>() {
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
			public void onSuccessEvent(List<SRight> result) {
				setData(result);
				
				CheckboxItem checkBoxItem = (CheckboxItem) aclTypeForm.getItem("expirationDateCheck");
				boolean checkValue = checkBoxItem.getValueAsBoolean();
//				validator.setMap(result, checkValue);
			}
		});
	}
	
	/**
	 * ������ Rights ������ Grid�� Set
	 * @param result
	 */
	public void setData(List<SRight> result) {
		if(result == null){
			grid.setData(new ListGridRecord[]{});
			return;
		}
		ListGridRecord[] records = new ListGridRecord[result.size()];
		
		for (int j = 0; j < result.size(); j++) {
			records[j] =new ListGridRecord();
			records[j].setAttribute("groupType",	result.get(j).getGroupType());
			records[j].setAttribute("entityId",		result.get(j).getEntityId());			
			records[j].setAttribute("name",			result.get(j).getName());
			records[j].setAttribute("view",			result.get(j).isView());
			//20150509na GS������ ����Ʈ ����
//			records[j].setAttribute("print",		result.get(j).isPrint());
			records[j].setAttribute("read",			result.get(j).isRead());
			records[j].setAttribute("write",		result.get(j).isWrite());
			records[j].setAttribute("add",			result.get(j).isAdd());
			records[j].setAttribute("rename",		result.get(j).isRename());
			records[j].setAttribute("pdelete",		result.get(j).isDelete());					
			records[j].setAttribute("check",		result.get(j).isCheck());			
			records[j].setAttribute("download",		result.get(j).isDownload());
			records[j].setAttribute("extend",		result.get(j).isExtend());
			records[j].setAttribute("control",		result.get(j).isControl());
		}
		grid.setData(records);
		
		if (result.size() > 0) {
			grid.selectRecord(records[0]);
			recordClick();
		}	
		
		// ��ȿ�Ⱓ ����
		CheckboxItem checkBoxItem = (CheckboxItem) aclTypeForm.getItem("expirationDateCheck");
		DateItem startDayItem = (DateItem) aclTypeForm.getItem("startday");
		DateItem expiredDayItem = (DateItem) aclTypeForm.getItem("expirationDate");
		
		if(result.size() > 0 && result.get(0).getExpiredday() != null){
			checkBoxItem.setValue(true);
			startDayItem.enable();
			startDayItem.setValue(result.get(0).getStartday());
			
			expiredDayItem.enable();
			expiredDayItem.setValue(result.get(0).getExpiredday());
		}else{
			checkBoxItem.setValue(false);
			startDayItem.disable();
			expiredDayItem.disable();
		}
	}
	
	private void recordSelected() {
		CheckboxItem checkBoxItem = (CheckboxItem) aclTypeForm.getItem("expirationDateCheck");
		boolean checkValue = checkBoxItem.getValueAsBoolean();
    	aclTypeForm.reset();
//    	aclDtlForm.reset();
    	aclTypeForm.editRecord(grid.getSelectedRecord());
    	checkBoxItem.setValue(checkValue);
//    	aclDtlForm.editRecord(grid.getSelectedRecord());
    	
//    	allChkCrtlByEach();
	}
	
	/**
	 * ACL Grid�� �Է��� Row �߰�
	 */
	private void addRow() {
		Log.debug("[ SecurityACLGridPanel addRow ]");
		
		ListGridRecord record = new ListGridRecord();
		record.setAttribute("groupType",	aclTypeForm.getField("groupType").getValue());
		record.setAttribute("entityId", "");				
		record.setAttribute("name", "");
//		record.setAttribute("view", aclDtlForm.getField("view").getValue());
//		record.setAttribute("print", aclDtlForm.getField("print").getValue());
		//�ʼ� �Է°��̹Ƿ� row ������ �ڵ� true ����
		record.setAttribute("read", true);
//		record.setAttribute("write", aclDtlForm.getField("write").getValue());
//		record.setAttribute("add", aclDtlForm.getField("add").getValue());
//		record.setAttribute("rename", aclDtlForm.getField("rename").getValue());
//		record.setAttribute("pdelete", aclDtlForm.getField("pdelete").getValue());
//		record.setAttribute("check", aclDtlForm.getField("check").getValue());
//		record.setAttribute("download", aclDtlForm.getField("download").getValue());
//		record.setAttribute("extend", aclDtlForm.getField("extend").getValue());
//		record.setAttribute("control", aclDtlForm.getField("control").getValue());
		grid.addData(record);
		
		grid.ungroup();
		grid.groupBy("groupType");
		grid.selectRecord(record);
		grid.scrollToRow(grid.getRecordIndex(record));
		recordClick();
		aclTypeForm.getField("entityId").clearValue();
		aclTypeForm.getField("name").clearValue();
//		btnSave.enable();
	}
	
	/**
	 * ACL Grid�� �Է��� Row ����
	 * @param record
	 */
	private void updateRow(Record record) {
		grid.ungroup();
		grid.groupBy("groupType");
		grid.selectRecord(record);
		grid.scrollToRow(grid.getRecordIndex(record));		
		
		int rowNum = grid.getRecordIndex(record);
		Log.debug("[ SecurityACLGridPanel updateRow ] rowNum["+rowNum+"]");
		
		record.setAttribute("groupType",	aclTypeForm.getField("groupType").getValue());
		record.setAttribute("entityId", aclTypeForm.getField("entityId").getValue());		
		record.setAttribute("name", aclTypeForm.getField("name").getValue());
		record.setAttribute("view", aclDtlForm.getField("view").getValue());
		//20150509na GS������ ����Ʈ ����
//		record.setAttribute("print", aclDtlForm.getField("print").getValue());
		record.setAttribute("read", aclDtlForm.getField("read").getValue());
		record.setAttribute("write", aclDtlForm.getField("write").getValue());
		record.setAttribute("add", aclDtlForm.getField("add").getValue());
		record.setAttribute("rename", aclDtlForm.getField("rename").getValue());
		record.setAttribute("pdelete", aclDtlForm.getField("pdelete").getValue());
		record.setAttribute("check", aclDtlForm.getField("check").getValue());
		record.setAttribute("download", aclDtlForm.getField("download").getValue());		
		record.setAttribute("extend", aclDtlForm.getField("extend").getValue());		
		record.setAttribute("control", aclDtlForm.getField("control").getValue());
		grid.refreshRow(rowNum);
//		btnSave.enable();
	}
	
	/**
	 * ������ Access Control List(Grid) Data ����
	 */
	private void executeSave() {
		
		
		final List<Record> sendRecords = new ArrayList<Record>();
		
		SSecurityProfile profile = new SSecurityProfile();
		profile.setId(profileId);
		
		grid.ungroup();
		grid.groupBy("groupType");
		
		RecordList recordlist = grid.getRecordList();		
		if(!recordlist.isEmpty()) {
			for(int j=0; j<recordlist.getLength(); j++) {
				Record record = recordlist.get(j);
				if(record.getAttributeAsString("entityId") != null) { //group�� ��� null��
					if(!record.getAttributeAsBoolean("read")) { //�ѹ��� ����
						SC.say(I18N.message("readfieldrequired"));
						return;
					}
					sendRecords.add(record);
				}
				
				if(record.getAttribute("name").equals("")){
					SC.warn(I18N.message("youhavetoentername"));
					return;
				}
			}
		}
		
		if(!sendRecords.isEmpty()) {
			SRight[] rights = new SRight[sendRecords.size()];
			for(int j=0; j<sendRecords.size(); j++) {
				rights[j] = new SRight();				
				rights[j].setGroupType(Integer.valueOf(sendRecords.get(j).getAttributeAsString("groupType")));
				String entityId = sendRecords.get(j).getAttributeAsString("entityId");
				if (entityId == null) entityId = "";
				CheckboxItem checkBoxItem = (CheckboxItem) aclTypeForm.getItem("expirationDateCheck");
				DateItem startDayItem = (DateItem) aclTypeForm.getItem("startday");
				DateItem expiredDayItem = (DateItem) aclTypeForm.getItem("expirationDate");
				
				rights[j].setEntityId(entityId);								
				rights[j].setName(sendRecords.get(j).getAttributeAsString("name"));				
				rights[j].setView(Boolean.valueOf(sendRecords.get(j).getAttributeAsString("view")));
				//20150509na GS������ ����Ʈ ����
//				rights[j].setPrint(Boolean.valueOf(sendRecords.get(j).getAttributeAsString("print")));
				rights[j].setRead(Boolean.valueOf(sendRecords.get(j).getAttributeAsString("read")));
				rights[j].setWrite(Boolean.valueOf(sendRecords.get(j).getAttributeAsString("write")));
				rights[j].setAdd(Boolean.valueOf(sendRecords.get(j).getAttributeAsString("add")));
				rights[j].setRename(Boolean.valueOf(sendRecords.get(j).getAttributeAsString("rename")));
				rights[j].setDelete(Boolean.valueOf(sendRecords.get(j).getAttributeAsString("pdelete")));
				rights[j].setCheck(Boolean.valueOf(sendRecords.get(j).getAttributeAsString("check")));
				rights[j].setDownload(Boolean.valueOf(sendRecords.get(j).getAttributeAsString("download")));
				rights[j].setExtend(Boolean.valueOf(sendRecords.get(j).getAttributeAsString("extend")));
				rights[j].setControl(Boolean.valueOf(sendRecords.get(j).getAttributeAsString("control")));
				rights[j].setStartday(checkBoxItem.getValueAsBoolean() ? Util.convertNoTimeDate(startDayItem.getValueAsDate()) : null);
				rights[j].setExpiredday(checkBoxItem.getValueAsBoolean() ? Util.convertNoTimeDate(expiredDayItem.getValueAsDate()) : null);
			}
			profile.setRights(rights);
			
			CheckboxItem checkBoxItem = (CheckboxItem) aclTypeForm.getItem("expirationDateCheck");
//			if(validator.check(Arrays.asList(rights), checkBoxItem.getValueAsBoolean())){
//				SC.say(I18N.message("nothingchanged"));
//				return;
//			}
			
//			validator.setMap(Arrays.asList(rights), checkBoxItem.getValueAsBoolean());
		}
		
		ServiceUtil.security().applySecurityProfileRights(Session.get().getSid(), profile, new AsyncCallbackWithStatus<Void>() {
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
				correctSelectionRefGroupTypeItem();
			}
			@Override
			public void onSuccessEvent(Void result) {			
//				btnSave.disable();
				if(!sendRecords.isEmpty()) {
					grid.selectRecord(sendRecords.get(0));
					recordClick();
				}
				
				if(recordObserver != null) {
					recordObserver.onRecordSelected(profileId, Constants.INVALID_LONG);
        		}
//				SC.say(I18N.message("operationcompleted"));
				// kimsoeun GS������ - ���� �ٱ���ȭ
				SC.say(I18N.message("savecompleted"));
			}
		});
	}
//	public Button getBtnApply() {
//		return btnApply;
//	}
}