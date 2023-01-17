package com.speno.xedm.gui.frontend.client.admin.documentbox;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.RecordList;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.GroupStartOpen;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.types.MultipleAppearance;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.DrawEvent;
import com.smartgwt.client.widgets.events.DrawHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.CheckboxItem;
import com.smartgwt.client.widgets.form.fields.DateItem;
import com.smartgwt.client.widgets.form.fields.HiddenItem;
import com.smartgwt.client.widgets.form.fields.PickerIcon;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ChangeEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangeHandler;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.form.fields.events.FormItemClickHandler;
import com.smartgwt.client.widgets.form.fields.events.FormItemIconClickEvent;
import com.smartgwt.client.widgets.grid.GroupNode;
import com.smartgwt.client.widgets.grid.GroupTitleRenderer;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
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
import com.speno.xedm.core.service.serials.SDocument;
import com.speno.xedm.core.service.serials.SFolder;
import com.speno.xedm.core.service.serials.SRight;
import com.speno.xedm.core.service.serials.SSecurityProfile;
import com.speno.xedm.gui.common.client.Constants;
import com.speno.xedm.gui.common.client.FolderObserver;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.Session;
import com.speno.xedm.gui.common.client.log.Log;
import com.speno.xedm.gui.common.client.util.AsyncCallbackWithStatus;
import com.speno.xedm.gui.common.client.util.ItemFactory;
import com.speno.xedm.gui.common.client.util.ReturnHandler;
import com.speno.xedm.gui.common.client.util.SCM;
import com.speno.xedm.gui.common.client.util.ServiceUtil;
import com.speno.xedm.gui.common.client.util.Util;
import com.speno.xedm.gui.frontend.client.admin.CommSearchDialog;
import com.speno.xedm.gui.frontend.client.admin.CommSearchDialog.ResultHandler;
import com.speno.xedm.gui.frontend.client.document.DocumentActionUtil;
import com.speno.xedm.gui.frontend.client.document.DocumentsPanel;
import com.speno.xedm.gui.frontend.client.folder.GroupWindow;

/**
 * Security ACL Grid Panel
 * 
 * @author 박상기
 * @since 1.0
 */
public class SecurityACLGridPanel extends VLayout {
	private static HashMap<String, SecurityACLGridPanel> instanceMap = new HashMap<String, SecurityACLGridPanel>();

	private long folderId, parentId;

	private ListGrid grid;
	private DynamicForm aclTypeForm;
	// private DynamicForm aclTypeForm, aclDtlForm;
	private HLayout actionHL;
	private DynamicForm secuComboForm;
	private SelectItem securityProfileItem;
	// private CheckboxItem allItem, viewItem, printItem, readItem, writeItem,
	// addItem, updateItem, deleteItem, checkoutItem, downloadItem, extendItem,
	// controlItem;

	private CheckboxItem retentionCheckItem;
	private DateItem startDayItem;
	private DateItem expiredDayItem;

	private FolderObserver folderObserver;

	private SFolder folderForSave;
	private LinkedHashMap<Integer, String> opts;

	// 20130508
	private Button btnSave;
	private String originProfileId;
	private Label subTitleLable;

	// goodbong 추가 : Doc 권한
	private long docId = 0;

	// 선택된 행 위치 반환용
	private int rowNum;

	private SelectItem groupTypeItem;
	private TextItem nameItem;
	
//	private IsNotChangedValidator validator = new IsNotChangedValidator();

	public void setDocId(long docId) {
		this.docId = docId;
	}

	// goodbong

	/*
	 * grid.setCanEdit(false); 모드에선 그리드의 체크박스값이 설정되지 않는다. 이를 해결하기 위해
	 * grid.setCanEdit(true);로 두고 변경가능한 핸들러를 아래와 같이 등록해두고 readOnly 기능을 구현함.
	 */
	private RecordClickHandler recordClickHandler = new RecordClickHandler() {
		@Override
		public void onRecordClick(RecordClickEvent event) {
			rowNum = event.getRecordNum();
			recordClick();
			// event.cancel();
		}
	};

	/**
	 * 기 생성된 instance를 반환함. instance가 없을 경우 생성 후 반환함
	 * 
	 * @param id
	 * @param subTitle
	 * @return
	 */
	public static SecurityACLGridPanel get(final String id,
			final String subTitle) {
		return get(id, subTitle, null, "100%");
	}

	/**
	 * 기 생성된 instance를 반환함. instance가 없을 경우 생성 후 반환함
	 * 
	 * @param id
	 * @param subTitle
	 * @param fob
	 * @param width
	 * @return
	 */
	public static SecurityACLGridPanel get(final String id,
			final String subTitle, final FolderObserver fob, final String width) {
		if (instanceMap.get(id) == null) {
			new SecurityACLGridPanel(id, subTitle, fob, width);
		}
		return instanceMap.get(id);
	}

	/**
	 * Security ACL Grid Panel 생성
	 * 
	 * @param id
	 * @param subTitle
	 */
	public SecurityACLGridPanel(final String id, final String subTitle) {
		this(id, subTitle, null, "100%");
	}

	/**
	 * Security ACL Grid Panel 생성
	 * 
	 * @param id
	 * @param subTitle
	 * @param fob
	 * @param width
	 */
	public SecurityACLGridPanel(final String id, final String subTitle,
			final FolderObserver fob, final String width) {
		instanceMap.put(id, this);

		setPadding(Constants.PADDING_DEFAULT);

		this.folderObserver = fob;

		/* Sub Title 생성 */
		subTitleLable = new Label();
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
		// grid.setCanEdit(false);

		grid.setGroupStartOpen(GroupStartOpen.ALL);
		grid.setGroupByField("groupType");
		grid.setEmptyMessage(I18N.message("notitemstoshow"));
		grid.setCanRemoveRecords(true);
		// grid의 setCanRemoveRecords()는 한 번만 실행 가능하다(grid 생성 및 설정 후 재 설정 불가)
		// 따라서 다음과 같이 remove Field의 name 을 설정하고 hide 처리하는 방식으로 control
		grid.setRemoveFieldProperties(new ListGridField("remove"));
		grid.setSelectionType(SelectionStyle.SINGLE);
		grid.invalidateCache();

		int len = 66;

		ListGridField groupTypeField = new ListGridField("groupType");
		ListGridField entityIdField = new ListGridField("entityId");
		ListGridField aclNmField = new ListGridField("name",
				I18N.message("name"), 150);
		ListGridField allField = new ListGridField("all", I18N.message("all"),
				len);
		ListGridField viewField = new ListGridField("view",
				I18N.message("view"), len);
		//20150509na GS인증용 프린트 제거
//		ListGridField printField = new ListGridField("print",
//				I18N.message("print"), len);
		ListGridField readField = new ListGridField("read",
				I18N.message("read"), len);
		ListGridField writeField = new ListGridField("write",
				I18N.message("write"), len);
		ListGridField addField = new ListGridField("add", I18N.message("add"),
				len);
		ListGridField updateField = new ListGridField("rename",
				I18N.message("update"), len);
		ListGridField deleteField = new ListGridField("pdelete",
				I18N.message("delete"), len);
		ListGridField checkoutField = new ListGridField("check",
				I18N.message("checkin") + "/" + I18N.message("checkout"), len);
		ListGridField downloadField = new ListGridField("download",
				I18N.message("download"), len);
		ListGridField extendField = new ListGridField("extend",
				I18N.message("retention"), len);
		ListGridField controlField = new ListGridField("control",
				I18N.message("control"), len);

		groupTypeField.setCanEdit(false);
		entityIdField.setCanEdit(false);
		aclNmField.setCanEdit(false);
		// 읽기는 수정 불가!
		readField.setCanEdit(false);

		groupTypeField.setHidden(true);
		entityIdField.setHidden(true);

		allField.setType(ListGridFieldType.BOOLEAN);
		viewField.setType(ListGridFieldType.BOOLEAN);
		//20150509na GS인증용 프린트 제거
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
			public String getGroupTitle(Object groupValue, GroupNode groupNode,
					ListGridField field, String fieldName, ListGrid grid) {
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

		// viewField.addRecordClickHandler(recordClickHandler);
		// printField.addRecordClickHandler(recordClickHandler);
		// readField.addRecordClickHandler(recordClickHandler);
		// writeField.addRecordClickHandler(recordClickHandler);
		// addField.addRecordClickHandler(recordClickHandler);
		// updateField.addRecordClickHandler(recordClickHandler);
		// deleteField.addRecordClickHandler(recordClickHandler);
		// checkoutField.addRecordClickHandler(recordClickHandler);
		// downloadField.addRecordClickHandler(recordClickHandler);
		// extendField.addRecordClickHandler(recordClickHandler);
		// controlField.addRecordClickHandler(recordClickHandler);

		allField.setAlign(Alignment.CENTER);
		viewField.setAlign(Alignment.CENTER);
		//20150509na GS인증용 프린트 제거
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

		//20150509na GS인증용 프린트 제거
		grid.setFields(groupTypeField, entityIdField, aclNmField, allField,
				viewField, readField, writeField, addField,
				updateField, deleteField, checkoutField, downloadField,
				extendField, controlField);
//		grid.setFields(groupTypeField, entityIdField, aclNmField, allField,
//				viewField, printField, readField, writeField, addField,
//				updateField, deleteField, checkoutField, downloadField,
//				extendField, controlField);
		// grid.setFields(groupTypeField, entityIdField, aclNmField, viewField,
		// printField, readField, writeField, addField, updateField,
		// deleteField, checkoutField, downloadField, extendField,
		// controlField);
		grid.setCanFreezeFields(true);

		// record double click event handler
		// 정의-------------------------------------------------------
		grid.addRecordDoubleClickHandler(new RecordDoubleClickHandler() {
			@Override
			public void onRecordDoubleClick(RecordDoubleClickEvent event) {
				event.cancel();
			}
		});

		// record click event handler
		grid.addRecordClickHandler(recordClickHandler);

		// record 삭제 event handler
		// 정의--------------------------------------------------------------
		grid.addRemoveRecordClickHandler(new RemoveRecordClickHandler() {
			@Override
			public void onRemoveRecordClick(final RemoveRecordClickEvent event) {
				event.cancel();
				SC.confirm(I18N.message("confirmdelete"),
						new BooleanCallback() {
							@Override
							public void execute(Boolean value) {
								if (value != null && value) {
								
									securityProfileItem
											.setValue(SSecurityProfile.PROFILE_PRIVATEACL);
									ListGridRecord record = grid
											.getRecord(event.getRowNum());
									grid.removeData(record);
									
									if (grid.getRecordList().isEmpty() ==true)
										groupTypeItem.disable();
									else
										groupTypeItem.enable();			
									// btnSave.enable();
									addNew();
								} else {
									return;
								}
							}
						});
			}
		});

		/*
		 * Selection state 보정. grid.setGroupByField(..); 가 지정되어 있을 경우 screen에 새로
		 * 그려질때 selection state 및 grouping 정보가 초기화 현상이 발생함. selection state는
		 * 보정처리함
		 */
		grid.addDrawHandler(new DrawHandler() {
			@Override
			public void onDraw(DrawEvent event) {
				correctSelectionRefGroupTypeItem();
			}
		});
		for (final ListGridField field : grid.getAllFields()) {
			field.addChangeHandler(new com.smartgwt.client.widgets.grid.events.ChangeHandler() {
				@Override
				public void onChange(
						com.smartgwt.client.widgets.grid.events.ChangeEvent event) {
					if (field.getName().equals("all")) {
						controlAllFields(event.getRowNum());

					} else
						securityProfileItem
								.setValue(SSecurityProfile.PROFILE_PRIVATEACL);
				}
			});
		}
		// allField.addChangeHandler(new
		// com.smartgwt.client.widgets.grid.events.ChangeHandler() {
		// @Override
		// public void
		// onChange(com.smartgwt.client.widgets.grid.events.ChangeEvent event) {
		// }
		// });

		HLayout gridHL = new HLayout(5);
		gridHL.setMembers(grid);
		// gridHL.setMembers( grid, createAclDtlForm());
		gridHL.setOverflow(Overflow.SCROLL);

		VLayout groupVL = new VLayout(5);
		final VLayout aclTypeVL = createAclTypeVL();
		groupVL.setMembers(createSecuComboForm(), gridHL, aclTypeVL,
				createActHL());

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

		startDayItem.addChangedHandler(changeHandler);
		expiredDayItem.addChangedHandler(changeHandler);
	}

	private ChangedHandler changeHandler = new ChangedHandler() {
		@Override
		public void onChanged(ChangedEvent event) {
			// 20131216 na 오늘 이전날짜와 비교를 해야 당일도 잠금을 선택할 수 있음.
			Date startDay = startDayItem.getValueAsDate();
			Date expiredDay = expiredDayItem.getValueAsDate();
			Date today = new Date();
			securityProfileItem.setValue(SSecurityProfile.PROFILE_PRIVATEACL);

			if (startDay.after(expiredDay)) {
				SC.warn(I18N.message("youcantchooseday"));
				startDayItem.setValue(today);
				expiredDayItem.setValue(today);
			}
		}
	};

	/**
	 * Access Control List Form Panel 생성
	 * 
	 * @return VLayout
	 */
	private VLayout createAclTypeVL() {

		opts = new LinkedHashMap<Integer, String>();
		ServiceUtil
				.getAllGroupType(new ReturnHandler<LinkedHashMap<Integer, String>>() {
					@Override
					public void onReturn(LinkedHashMap<Integer, String> param) {
						opts = param;
					}
				});

		opts.put(SRight.GROUPTYPE_DUTY, I18N.message("duty"));
		opts.put(SRight.GROUPTYPE_POSITION, I18N.message("position"));
		opts.put(SRight.GROUPTYPE_GROUP, I18N.message("group"));
		opts.put(SRight.GROUPTYPE_USERGROUP, I18N.message("user"));

		groupTypeItem = new SelectItem("groupType", I18N.message("type"));
	

		groupTypeItem.setWidth("*");
		groupTypeItem.setType("combobox");
		groupTypeItem.setValueMap(opts);
		groupTypeItem.setMultipleAppearance(MultipleAppearance.PICKLIST);
		// groupTypeItem.setRequired(true);
		groupTypeItem.setEmptyDisplayValue(I18N.message("choosetype"));
		// groupTypeItem.setCanEdit(true);
		// groupTypeItem.setCanEdit(false);
		groupTypeItem.addChangedHandler(new ChangedHandler() {
			@Override
			public void onChanged(ChangedEvent event) {

//				groupTypeItem.disable();
				ListGridRecord selectedRecord = grid.getSelectedRecord();
				if (selectedRecord == null)
					return;
				aclTypeForm.getField("entityId").clearValue();
				aclTypeForm.getField("name").clearValue();
				grid.getSelectedRecord().setAttribute("groupType",
						groupTypeItem.getValue());
				grid.getSelectedRecord().setAttribute("entityId", "");
				grid.getSelectedRecord().setAttribute("name", "");
				grid.ungroup();
				grid.groupBy("groupType");
				// grid.selectRecord(selectedRecord);
				grid.selectSingleRecord(selectedRecord);
				grid.markForRedraw();
				// grid.redraw();
				// aclDtlForm.reset();
			}
		});

		HiddenItem entityIdItem = new HiddenItem("entityId");
		entityIdItem.setVisible(false);

		PickerIcon searchPicker = new PickerIcon(PickerIcon.SEARCH,
				new FormItemClickHandler() {
					public void onFormItemClick(FormItemIconClickEvent event) {
						Integer typeVal = (Integer) groupTypeItem.getValue();
						Object name = aclTypeForm.getField("name").getValue();
						if (typeVal == null) {
							SC.say(I18N.message("choosetype"));
							groupTypeItem.focusInItem();
						} else {
							switch (typeVal) {
							case CommSearchDialog.GROUP:
								GroupWindow groupWindow = new GroupWindow(
										"single", new ReturnHandler() {
											@Override
											public void onReturn(Object param) {
												String[][] groupInfo = (String[][]) param;
												aclTypeForm.getField("entityId").setValue(groupInfo[0][0]);
												aclTypeForm.getField("name")	.setValue(groupInfo[0][1]);
												if (addNewValidation()) {
													grid.getSelectedRecord().setAttribute("entityId",aclTypeForm.getField("entityId")	.getValue());
													grid.getSelectedRecord()	.setAttribute("name",aclTypeForm.getField("name").getValue());
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
								commSearchDialog
										.addResultHandler(new ResultHandler() {
											@Override
											public void onSelected(
													HashMap<String, String> resultMap) {
												aclTypeForm.getField("entityId").setValue(resultMap.get("id"));
												aclTypeForm.getField("name")	.setValue(resultMap.get("name"));
												if (addNewValidation()) {
													// grid.getSelectedRecord().setAttribute("groupType",
													// I18N.message(aclTypeForm.getField("groupType").getValue().toString()));
													grid.getSelectedRecord()	.setAttribute("entityId",aclTypeForm.getField("entityId").getValue());
													grid.getSelectedRecord().setAttribute("name",aclTypeForm.getField("name").getValue());
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

		nameItem = new TextItem("name", I18N.message("name"));
		// 빈 그리드 일때 유형 및 이름 생성 금지
		if (grid.getRecord(0) == null)
			nameItem.disable();

		nameItem.setWidth("*");
		nameItem.setWidth(120);
		// nameItem.setRequired(true);
		nameItem.setCanEdit(false);
		// nameItem.setDisabled(true);
		nameItem.setDisableIconsOnReadOnly(false);
		nameItem.setIcons(searchPicker);

		nameItem.setStartRow(false);
		nameItem.setEndRow(false);
		nameItem.addClickHandler(new com.smartgwt.client.widgets.form.fields.events.ClickHandler() {
			@Override
			public void onClick(
					com.smartgwt.client.widgets.form.fields.events.ClickEvent event) {
//				Integer typeVal = (Integer) groupTypeItem.getValue();
//				Object name = aclTypeForm.getField("name").getValue();
//				if (typeVal == null) {
//					return;
//				}
//				if (grid.getSelectedRecord() == null) {
//					Log.warnWithPopup("checkeditemsnotexist", "");
//					return;
//				}
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

//				final CommSearchDialog commSearchDialog = new CommSearchDialog(
//						typeVal);
//				commSearchDialog.addResultHandler(new ResultHandler() {
//					@Override
//					public void onSelected(HashMap<String, String> resultMap) {
//						aclTypeForm.getField("entityId").setValue(
//								resultMap.get("id"));
//						aclTypeForm.getField("name").setValue(
//								resultMap.get("name"));
//						if (addNewValidation()) {
//							grid.getSelectedRecord()
//									.setAttribute(
//											"entityId",
//											aclTypeForm.getField("entityId")
//													.getValue());
//							grid.getSelectedRecord().setAttribute("name",
//									aclTypeForm.getField("name").getValue());
//							grid.redraw();
//						}
//					}
//				});
//				commSearchDialog.show();
			}
		});
		// nameItem.addFocusHandler(new FocusHandler() {
		// @Override
		// public void onFocus(FocusEvent event) {
		// // TODO Auto-generated method stub
		// Integer typeVal = (Integer)groupTypeItem.getValue();
		// Object name = aclTypeForm.getField("name").getValue();
		// if(typeVal == null) {
		// return;
		// }
		// final CommSearchDialog commSearchDialog = new
		// CommSearchDialog(typeVal, name);
		// commSearchDialog.addResultHandler(new ResultHandler() {
		// @Override
		// public void onSelected(HashMap<String, String> resultMap) {
		// aclTypeForm.getField("entityId").setValue(resultMap.get("id"));
		// aclTypeForm.getField("name").setValue(resultMap.get("name"));
		// if(addNewValidation()){
		// grid.getSelectedRecord().setAttribute("entityId",
		// aclTypeForm.getField("entityId").getValue());
		// grid.getSelectedRecord().setAttribute("name",
		// aclTypeForm.getField("name").getValue());
		// grid.redraw();
		// }
		// }
		// });
		// commSearchDialog.show();
		// btnSave.focus();
		// }
		// });
		// nameItem.setCanEdit(false);

		aclTypeForm = new DynamicForm();
		// aclTypeForm.setWidth("*");
		// aclTypeForm.setMinColWidth(80);
		aclTypeForm.setTitleWidth(60);
		aclTypeForm.setNumCols(6);
		// aclTypeForm.setColWidths("1","1","1","1","1","*");
		aclTypeForm.setItems(groupTypeItem, entityIdItem, nameItem);
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

		return actFormVL;
	}

	/**
	 * All field의 체크 상태에 따라서 Field 값들을 변경한다.
	 * 
	 * @param row
	 */
	private void controlAllFields(int row) {
		boolean flag = Boolean.parseBoolean(grid.getSelectedRecord()
				.getAttribute("all"));
		grid.getRecord(row).setAttribute("view", !flag);
		//20150509na GS인증용 프린트 제거
//		grid.getRecord(row).setAttribute("print", !flag);
		// 필수 입력값이므로 row 생성시 자동 true 설정
		grid.getRecord(row).setAttribute("read", true);
		grid.getRecord(row).setAttribute("write", !flag);
		grid.getRecord(row).setAttribute("add", !flag);
		grid.getRecord(row).setAttribute("rename", !flag);
		grid.getRecord(row).setAttribute("pdelete", !flag);
		grid.getRecord(row).setAttribute("check", !flag);
		grid.getRecord(row).setAttribute("download", !flag);
		grid.getRecord(row).setAttribute("extend", !flag);
		grid.getRecord(row).setAttribute("control", !flag);
		grid.refreshRow(row);

		if (Long.parseLong(securityProfileItem.getValueAsString()) != SSecurityProfile.PROFILE_PRIVATEACL) {
			securityProfileItem.setValue(SSecurityProfile.PROFILE_PRIVATEACL);
		}
	}

	/**
	 * Selection state 보정.
	 * 
	 * grid.setGroupByField(..); 가 지정되어 있을 경우 screen에 새로 그려질때 selection state 및
	 * grouping 정보가 초기화 현상이 발생함.
	 */
	private void correctSelectionRefGroupTypeItem() {
		// 20131209 na 반드시 첫번째 열 선택하게 만듬
		if (grid.getRecords().length > 0) {
			grid.selectSingleRecord(1);
			grid.markForRedraw();
		}
		// if(!aclTypeForm.getField("groupType").getCanEdit()) { //Add New 상태가
		// 아닌경우
		// RecordList recordlist = grid.getRecordList();
		// if(!recordlist.isEmpty()) {
		// final Record record = recordlist.find("entityId",
		// aclTypeForm.getValueAsString("entityId"));
		// if( record != null) {
		// // grid.selectRecord(record);
		// grid.selectSingleRecord(record);
		// grid.markForRedraw();
		// }
		// }
		// }
	}

	/**
	 * Record Click 처리
	 */
	private void recordClick() {
		// aclTypeForm.getField("groupType").setCanEdit(false);
		// aclTypeForm.getField("name").setDisabled(true);
		recordSelected();
	}

	private boolean addNewValidation() {
		ListGridRecord[] records = grid.getRecords();
		boolean returnValue = true;

		int i = 1;
		for (ListGridRecord record : records) {
			if (i != rowNum
					&& record
							.getAttribute("entityId")
							.toString()
							.equals(aclTypeForm.getField("entityId").getValue())) {
				SC.warn(I18N.message("dupmessage"));
				return false;
			}
			i++;
		}
		return returnValue;
	}

	// /**
	// * Access Control Check Button From Panel 생성
	// * @return
	// */
	// private VLayout createAclDtlForm() {
	// allItem = new CheckboxItem("all", I18N.message("all"));
	// allItem.addChangedHandler(new ChangedHandler() {
	// @Override
	// public void onChanged(ChangedEvent event) {
	// dtlChkCrtlByAll();
	// }
	// });
	//
	// viewItem = new CheckboxItem("view", I18N.message("view"));
	// viewItem.addChangedHandler(new ChangedHandler() {
	// @Override
	// public void onChanged(ChangedEvent event) {
	// allChkCrtlByEach();
	// }
	// });
	//
	// printItem = new CheckboxItem("print", I18N.message("print"));
	// printItem.addChangedHandler(new ChangedHandler() {
	// @Override
	// public void onChanged(ChangedEvent event) {
	// allChkCrtlByEach();
	// }
	// });
	//
	// readItem = new CheckboxItem("read", I18N.message("read"));
	// readItem.addChangedHandler(new ChangedHandler() {
	// @Override
	// public void onChanged(ChangedEvent event) {
	// dtlChkCrtlByRead();
	// }
	// });
	// readItem.setDefaultValue(true);
	// readItem.disable();
	// readItem.setTooltip(I18N.message("fieldisrequired",
	// readItem.getTitle()));
	//
	// writeItem = new CheckboxItem("write", I18N.message("write"));
	// writeItem.addChangedHandler(new ChangedHandler() {
	// @Override
	// public void onChanged(ChangedEvent event) {
	// allChkCrtlByEach();
	// }
	// });
	//
	// addItem = new CheckboxItem("add", I18N.message("add"));
	// addItem.addChangedHandler(new ChangedHandler() {
	// @Override
	// public void onChanged(ChangedEvent event) {
	// allChkCrtlByEach();
	// }
	// });
	//
	// updateItem = new CheckboxItem("rename", I18N.message("update"));
	// updateItem.addChangedHandler(new ChangedHandler() {
	// @Override
	// public void onChanged(ChangedEvent event) {
	// allChkCrtlByEach();
	// }
	// });
	//
	// deleteItem = new CheckboxItem("pdelete", I18N.message("delete"));
	// deleteItem.addChangedHandler(new ChangedHandler() {
	// @Override
	// public void onChanged(ChangedEvent event) {
	// allChkCrtlByEach();
	// }
	// });
	//
	// checkoutItem = new CheckboxItem("check", I18N.message("checkout"));
	// checkoutItem.addChangedHandler(new ChangedHandler() {
	// @Override
	// public void onChanged(ChangedEvent event) {
	// allChkCrtlByEach();
	// }
	// });
	//
	// downloadItem = new CheckboxItem("download", I18N.message("download"));
	// downloadItem.addChangedHandler(new ChangedHandler() {
	// @Override
	// public void onChanged(ChangedEvent event) {
	// allChkCrtlByEach();
	// }
	// });
	//
	// extendItem = new CheckboxItem("extend", I18N.message("retention"));
	// extendItem.addChangedHandler(new ChangedHandler() {
	// @Override
	// public void onChanged(ChangedEvent event) {
	// allChkCrtlByEach();
	// }
	// });
	//
	// controlItem = new CheckboxItem("control", I18N.message("control"));
	// controlItem.addChangedHandler(new ChangedHandler() {
	// @Override
	// public void onChanged(ChangedEvent event) {
	// allChkCrtlByEach();
	// }
	// });
	//
	// aclDtlForm = new DynamicForm();
	// aclDtlForm.setMinColWidth(80);
	// aclDtlForm.setAutoWidth();
	// aclDtlForm.setHeight100();
	// aclDtlForm.setCellSpacing(0);
	// aclDtlForm.setItems( allItem, viewItem, printItem, readItem, writeItem,
	// addItem, updateItem, deleteItem, checkoutItem, downloadItem, extendItem,
	// controlItem);
	// aclDtlForm.reset();
	//
	// VLayout actFormVL = new VLayout();
	// actFormVL.setAlign(Alignment.LEFT);
	// actFormVL.setBorder("1px solid gray");
	// actFormVL.setAutoWidth();
	// actFormVL.setHeight100();
	// actFormVL.addMembers(aclDtlForm);
	//
	// return actFormVL;
	// }

	/**
	 * Action Panel 생성
	 * 
	 * @return HLayout
	 */
	private HLayout createActHL() {

		Button btnAddNew = new Button(I18N.message("addnew"));
		btnAddNew
				.setIcon(ItemFactory.newImgIcon("page_white_add.png").getSrc());
		btnAddNew.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				groupTypeItem.enable();
				nameItem.enable();
				addRow();
				// addNew();
			}
		});

		// Button btnUpload = new Button(I18N.message("updaterow"));
		// btnUpload.setIcon("[SKIN]/RichTextEditor/paste.png");
		// btnUpload.addClickHandler(new ClickHandler() {
		// public void onClick(ClickEvent event) {
		// if(secuComboForm.validate() && aclTypeForm.validate()){
		// // if(secuComboForm.validate() && aclTypeForm.validate() &&
		// aclDtlForm.validate()) {
		// RecordList recordlist = grid.getRecordList();
		// if(recordlist.isEmpty()) {
		// addRow();
		// }
		// else {
		// if(aclTypeForm.getField("groupType").getCanEdit()) { //Add New 상태
		//
		// final Record record = recordlist.find("entityId",
		// aclTypeForm.getValueAsString("entityId"));
		// if( record != null) {
		// SC.confirm(I18N.message("itemoverwrite"), new BooleanCallback() {
		// @Override
		// public void execute(Boolean value) {
		// if(value != null && value) {
		// updateRow(record);
		// }
		// }
		// });
		// }
		// else {
		// addRow();
		// }
		// }
		// else {
		// updateRow(grid.getSelectedRecord());
		// }
		// }
		// }
		// }
		// });

		btnSave = new Button(I18N.message("save"));
		// btnSave.disable();
		btnSave.setIcon(ItemFactory.newImgIcon("data_into.png").getSrc());
		btnSave.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				long securityProfileItemVal = Long	.parseLong(securityProfileItem.getValueAsString());
				if (securityProfileItemVal == SSecurityProfile.PROFILE_PRIVATEACL	&& grid.getRecordList().isEmpty()) {
					SC.say(I18N.message("privateaclrequired"));
					return;
				}
				SC.confirm(I18N.message("wanttosave"), new BooleanCallback() {
					@Override
					public void execute(Boolean value) {
						if (value != null && value && checkValidation()) {
							executeSave();
						}
					}
				});
			}
		});

		actionHL = new HLayout(10);
		actionHL.setHeight(1);
		actionHL.setMembers(btnAddNew, btnSave);
		// actionHL.setMembers(btnAddNew, btnUpload, btnSave);
		return actionHL;
	}

	private boolean checkValidation() {
		// int selectedRecordSize = grid.getSelectedRecords().length;
		// grid.ungroup();
		// grid.groupBy("groupType");

		final Object groupType = aclTypeForm.getField("groupType").getValue();
		Object name = aclTypeForm.getField("name").getValue();
		RecordList recordlist = grid.getRecordList();
		if (!recordlist.isEmpty()) {
			for (int j = 0; j < recordlist.getLength(); j++) {
				Record record = recordlist.get(j);
				if (record.getAttributeAsString("entityId") != null) { // group일
																		// 경우
																		// null임
					if (!record.getAttributeAsBoolean("read")) { // 한번더 검증
						SC.say(I18N.message("readfieldrequired"));
						return false;
					}
				}
				if (groupType == null && record.getAttribute("name").equals("")) {
					SC.say(I18N.message("choosetype"));
					aclTypeForm.getField("groupType").focusInItem();
					return false;
				}
				// else if (record.getAttribute("name").equals("") && record ==
				// grid.getSelectedRecord() && !"".equals(name)){
				// final CommSearchDialog commSearchDialog = new
				// CommSearchDialog((Integer) groupType);
				// commSearchDialog.addResultHandler(new ResultHandler() {
				// @Override
				// public void onSelected(HashMap<String, String> resultMap) {
				// aclTypeForm.getField("entityId").setValue(resultMap.get("id"));
				// aclTypeForm.getField("name").setValue(resultMap.get("name"));
				// if(addNewValidation()){
				// // grid.getSelectedRecord().setAttribute("groupType",
				// I18N.message(aclTypeForm.getField("groupType").getValue().toString()));
				// grid.getSelectedRecord().setAttribute("entityId",
				// aclTypeForm.getField("entityId").getValue());
				// grid.getSelectedRecord().setAttribute("name",
				// aclTypeForm.getField("name").getValue());
				// grid.redraw();
				// executeSave();
				// }
				// }
				// });
				// commSearchDialog.show();
				// return false;
				// }

				// TODO 20140208na 자동 입력을 한 뒤 저장을 실행하는 메서드(미완성)
				// if (record == grid.getSelectedRecord() && !"".equals(name)){
				// switch (groupType) {
				// case CommSearchDialog.DUTY:
				// case CommSearchDialog.GROUP:
				// case CommSearchDialog.POSITION:
				// ServiceUtil.security().getGroup(Session.get().getSid(),
				// name.toString(), false, groupType, new
				// AsyncCallback<SGroup>() {
				// @Override
				// public void onSuccess(SGroup result) {
				// aclTypeForm.getField("entityId").setValue(result.getId());
				// aclTypeForm.getField("name").setValue(result.getName());
				// if(addNewValidation()){
				// grid.getSelectedRecord().setAttribute("entityId",
				// aclTypeForm.getField("entityId").getValue());
				// grid.getSelectedRecord().setAttribute("name",
				// aclTypeForm.getField("name").getValue());
				// executeSave();
				// }
				// }
				// @Override
				// public void onFailure(Throwable caught) {
				// SC.warn(caught.getMessage());
				// }
				// });
				// return;
				// case CommSearchDialog.USERGROUP:
				// ServiceUtil.security().getUser(Session.get().getSid(),
				// name.toString(), new AsyncCallback<SUser>() {
				// @Override
				// public void onFailure(Throwable caught) {
				// SC.warn(caught.getMessage());
				// }
				// @Override
				// public void onSuccess(SUser result) {
				// aclTypeForm.getField("entityId").setValue(result.getId());
				// aclTypeForm.getField("name").setValue(result.getName());
				// if(addNewValidation()){
				// grid.getSelectedRecord().setAttribute("entityId",
				// aclTypeForm.getField("entityId").getValue());
				// grid.getSelectedRecord().setAttribute("name",
				// aclTypeForm.getField("name").getValue());
				// executeSave();
				// }
				// }
				// });
				// return;
				// }
				// }
				else if (record.getAttribute("name").equals("")) {
					SC.warn(I18N.message("youhavetoentername"));
					return false;
				}
			}
		}
		return true;
	}

	// /**
	// * 각각의 ACL Check 버튼에 따른 All 버튼 핸들링
	// */
	// private void allChkCrtlByEach() {
	// allItem.setValue( viewItem.getValueAsBoolean() &&
	// printItem.getValueAsBoolean() &&
	// readItem.getValueAsBoolean() &&
	// writeItem.getValueAsBoolean() &&
	// addItem.getValueAsBoolean() &&
	// updateItem.getValueAsBoolean() &&
	// deleteItem.getValueAsBoolean() &&
	// checkoutItem.getValueAsBoolean() &&
	// downloadItem.getValueAsBoolean() &&
	// extendItem.getValueAsBoolean() &&
	// controlItem.getValueAsBoolean());
	// }
	//
	// /**
	// * Read Check 버튼의 Action에 의한 처리
	// */
	// private void dtlChkCrtlByRead() {
	// if( readItem.getValueAsBoolean() ) {
	// allChkCrtlByEach();
	// }
	// else {
	// allItem.setValue(false);
	// viewItem.setValue(false);
	// printItem.setValue(false);
	// writeItem.setValue(false);
	// addItem.setValue(false);
	// updateItem.setValue(false);
	// deleteItem.setValue(false);
	// checkoutItem.setValue(false);
	// downloadItem.setValue(false);
	// extendItem.setValue(false);
	// controlItem.setValue(false);
	// }
	// }
	//
	// /**
	// * All Check 버튼의 Action에 의한 처리
	// */
	// private void dtlChkCrtlByAll() {
	// viewItem.setValue(allItem.getValueAsBoolean());
	// printItem.setValue(allItem.getValueAsBoolean());
	// //readItem.setValue(allItem.getValueAsBoolean());
	// writeItem.setValue(allItem.getValueAsBoolean());
	// addItem.setValue(allItem.getValueAsBoolean());
	// updateItem.setValue(allItem.getValueAsBoolean());
	// deleteItem.setValue(allItem.getValueAsBoolean());
	// checkoutItem.setValue(allItem.getValueAsBoolean());
	// downloadItem.setValue(allItem.getValueAsBoolean());
	// extendItem.setValue(allItem.getValueAsBoolean());
	// controlItem.setValue(allItem.getValueAsBoolean());
	// }

	/**
	 * Add New 버튼의 클릭 이벤트 핸들러
	 */
	private void addNew() {
		aclTypeForm.editNewRecord();
		// aclDtlForm.editNewRecord();
		aclTypeForm.reset();
		// aclDtlForm.reset();
		// grid.deselectAllRecords();
		aclTypeForm.getField("groupType").setCanEdit(true);
		aclTypeForm.getField("name").setDisabled(false);
	}

	/**
	 * Default 초기화
	 */
	private void reset() {
		secuComboForm.reset();
		resetGridAndTypeDtlForm();
		// btnSave.disable();
	}

	/**
	 * 그리드와 상세 Form 초기화
	 */
	private void resetGridAndTypeDtlForm() {
		grid.setData(new ListGridRecord[0]);

		aclTypeForm.reset();
		// aclDtlForm.reset();

		aclTypeForm.editNewRecord();
		// aclDtlForm.editNewRecord();
	}

	/**
	 * Security Profile Form 생성
	 * 
	 * @return
	 */
	private DynamicForm createSecuComboForm() {
		securityProfileItem = new SelectItem("securityprofile",
				I18N.message("securityprofile"));
		securityProfileItem.setRequired(true);
		securityProfileItem.setWrapTitle(false);
		securityProfileItem.setEmptyDisplayValue(I18N.message("choosetype"));
		securityProfileItem.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				if (parentId == Constants.ADMIN_FOLDER_ROOT) {
					if (Long.parseLong((String) event.getValue()) == SSecurityProfile.PROFILE_INHERITEDACL) {
						SC.say(I18N.message("inheritedcannotbechanged"));
						event.cancel();
						return;
					}
				}
			}
		});

		// expiredDayCheckItem = new CheckboxItem();
		retentionCheckItem = new CheckboxItem("expirationDateCheck",
				I18N.message("validperiod"));
		retentionCheckItem.setWrapTitle(false);
		retentionCheckItem.setShowTitle(false);
		retentionCheckItem.setWidth("*");

		startDayItem = new DateItem(I18N.message("startday"));
		startDayItem.setWrapTitle(false);
		startDayItem.disable();

		expiredDayItem = new DateItem(I18N.message("expirationDate"));
		expiredDayItem.setWrapTitle(false);
		expiredDayItem.disable();

		securityProfileItem.addChangedHandler(new ChangedHandler() {
			@Override
			public void onChanged(ChangedEvent event) {
				String profileId = securityProfileItem.getValueAsString();
				onSecurityProfileItemChanged(Long.parseLong(profileId));
				// if(originProfileId.equals(profileId)) {
				// btnSave.disable();
				// }
				// else {
				// btnSave.enable();
				// }
			}
		});

		retentionCheckItem.addChangedHandler(new ChangedHandler() {
			@Override
			public void onChanged(ChangedEvent event) {
				securityProfileItem
						.setValue(SSecurityProfile.PROFILE_PRIVATEACL);

				if (!retentionCheckItem.isDisabled()
						&& retentionCheckItem.getValueAsBoolean()) {
					startDayItem.enable();
					expiredDayItem.enable();
				} else {
					startDayItem.disable();
					expiredDayItem.disable();
				}
			}
		});

		final LinkedHashMap<Long, String> opts = new LinkedHashMap<Long, String>();

		ServiceUtil
				.getAllSecurityProfile(new ReturnHandler<List<SSecurityProfile>>() {

					@Override
					public void onReturn(List<SSecurityProfile> result) {
						if (result.size() > 0) {
							for (int j = 0; j < result.size(); j++) {
								opts.put(result.get(j).getId(), result.get(j)
										.getName());
							}
						}
						securityProfileItem.setValueMap(opts);
					}
				});

		// ServiceUtil.documentcode().listSecurityProfileLikeName(Session.get().getSid(),
		// "", new AsyncCallbackWithStatus<List<SSecurityProfile>>() {
		// @Override
		// public String getSuccessMessage() {
		// return I18N.message("client.searchComplete");
		// }
		// @Override
		// public String getProcessMessage() {
		// return I18N.message("client.searchRequest");
		// }
		// @Override
		// public void onSuccessEvent(List<SSecurityProfile> result) {
		// if( result.size() > 0) {
		// for(int j=0; j<result.size(); j++) {
		// opts.put(result.get(j).getId(), result.get(j).getName());
		// }
		// }
		// securityProfileItem.setValueMap(opts);
		// }
		// @Override
		// public void onFailureEvent(Throwable caught) {
		// SCM.warn(caught);
		// }
		// });
		secuComboForm = new DynamicForm();
		secuComboForm.setAutoWidth();
		secuComboForm.setNumCols(7);
		secuComboForm.setItems(securityProfileItem, startDayItem,
				expiredDayItem, retentionCheckItem);
		return secuComboForm;
	}

	/**
	 * Security Profile Item 변경에 따른 Right 조회
	 * 
	 * @param profileId
	 */
	private void onSecurityProfileItemChanged(final long profileId) {

		securityProfileItem.setValue(profileId);
		resetGridAndTypeDtlForm();
		addNew();

		boolean isDoc = false;
		// Id 설정
		long id = 0;
		// docID가 있을경우
		if (docId != 0) {
			// Item의 값이 PROFILE_INHERITEDACL 의 경우 현재 폴더의 권한을 가져온다.
			if (SSecurityProfile.PROFILE_INHERITEDACL == profileId) {
				id = folderId;
				isDoc = false;
			} else {
				id = docId;
				isDoc = true;
			}
		}
		// docID가 없을경우 folderID or parentID 선택
		else {
			// id = folderId;
			// profileId = SSecurityProfile.PROFILE_INHERITEDACL;
			id = (SSecurityProfile.PROFILE_INHERITEDACL == profileId) ? parentId
					: folderId;
			isDoc = false;
		}

		if (SSecurityProfile.PROFILE_PRIVATEACL == profileId
				|| SSecurityProfile.PROFILE_INHERITEDACL == profileId) {
			ServiceUtil.security().listRightsByFolderId(Session.get().getSid(),
					id, isDoc, profileId,
					new AsyncCallbackWithStatus<List<SRight>>() {
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
//							validator.setMap(result, retentionCheckItem.getValueAsBoolean());
						}
					});
		} else {
			ServiceUtil.security().listRightsBySecurityProfileId(
					Session.get().getSid(), profileId,
					new AsyncCallbackWithStatus<List<SRight>>() {
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
//							validator.setMap(result, retentionCheckItem.getValueAsBoolean());
						}
					});
		}
	}

	/**
	 * 수신한 Rights 정보를 Grid에 Set
	 * 
	 * @param result
	 */
	public void setData(List<SRight> result) {
		ListGridRecord[] records = new ListGridRecord[result.size()];
		// //빈 그리드 일때 유형 생성 금지

		for (int j = 0; j < result.size(); j++) {
			records[j] = new ListGridRecord();
			records[j].setAttribute("groupType", result.get(j).getGroupType());
			records[j].setAttribute("entityId", result.get(j).getEntityId());
			records[j].setAttribute("name", result.get(j).getName());
			records[j].setAttribute("view", result.get(j).isView());
			//20150509na GS인증용 프린트 제거
//			records[j].setAttribute("print", result.get(j).isPrint());
			records[j].setAttribute("read", result.get(j).isRead());
			records[j].setAttribute("write", result.get(j).isWrite());
			records[j].setAttribute("add", result.get(j).isAdd());
			records[j].setAttribute("rename", result.get(j).isRename());
			records[j].setAttribute("pdelete", result.get(j).isDelete());
			records[j].setAttribute("check", result.get(j).isCheck());
			records[j].setAttribute("download", result.get(j).isDownload());
			records[j].setAttribute("extend", result.get(j).isExtend());
			records[j].setAttribute("control", result.get(j).isControl());
		}
		grid.setData(records);

		// 아무런 데이터가 없을 시 유형 변경 금지 yuk20140319
		if (grid.getRecordList().isEmpty() ==true){
			groupTypeItem.disable();
			nameItem.disable();
		}
		else{
			groupTypeItem.enable();
			nameItem.enable();
		}
			

		// 유효기간 설정
		if (result.size() > 0 && result.get(0).getExpiredday() != null
				&& !retentionCheckItem.isDisabled()) {
			retentionCheckItem.setValue(true);
			startDayItem.enable();
			startDayItem.setValue(result.get(0).getStartday());

			expiredDayItem.enable();
			expiredDayItem.setValue(result.get(0).getExpiredday());
		} else {
			retentionCheckItem.setValue(false);
			startDayItem.disable();
			expiredDayItem.disable();
		}

		// 20131205, junsoo, 주석처리함 (DocumentDetailPanel 에서 readonly 여부가 이미
		// 결정되므로)
		// // 상속시 Disable 설정
		// if( Long.parseLong(securityProfileItem.getValueAsString()) ==
		// SSecurityProfile.PROFILE_INHERITEDACL) {
		// expiredDayCheckItem.disable();
		// expiredDayItem.disable();
		// }else{
		// expiredDayCheckItem.enable();
		// // expiredDayItem.enable();
		// }

		if (result.size() > 0) {
			// grid.selectRecord(records[0]);
			grid.selectSingleRecord(records[0]);
			grid.markForRedraw();
			recordClick();
		}
	}

	/**
	 * 선택된 파일에 문서 보안레벨이 업을경우 폴더의 권한을 상속 받고, 보안 프로파일의 값을 '상속'으로 변경한다.
	 * 
	 * @param folderId
	 * @param profileId
	 * @param parentId
	 * @param docId
	 * @param noDocSecu
	 */
	public void executeFetch(final long folderId, final String profileId,
			final long parentId, final long docId, boolean noDocSecu) {
		executeFetch(folderId, profileId, parentId, docId);
		if (noDocSecu)
			securityProfileItem.setValue(SSecurityProfile.PROFILE_INHERITEDACL);
	}

	/**
	 * 선택된 폴더
	 * 
	 * @param folderId
	 * @param profileId
	 * @param parentId
	 */
	public void executeFetch(final long folderId, final String profileId,
			final long parentId, final long docId) {
		GWT.log("[ SecurityACLGridPanel executeFetch ] folderId[" + folderId
				+ "], profileId[" + profileId + "], parentId[" + parentId
				+ "], docId[" + docId + "]", null);
		this.folderId = folderId;
		this.parentId = parentId;
		this.originProfileId = profileId;
		this.docId = docId;

		reset();

		if (Constants.ADMIN_ROOT == folderId
				|| Constants.ADMIN_FOLDER_ROOT == folderId) {
			grid.setEmptyMessage(I18N.message("cannotassignsecurityprofile"));
			disable();
			return;
		} else {
			grid.setEmptyMessage(I18N.message("notitemstoshow"));
			enable();
			addNew();
		}
//		validator.setValue(Long.parseLong(profileId));
		onSecurityProfileItemChanged(Long.parseLong(profileId));
	}

	/**
	 * Folder 선택에 따른 Access Control List Items 설정(
	 */
	private void recordSelected() {
		CheckboxItem checkBoxItem = (CheckboxItem) aclTypeForm
				.getItem("expirationDateCheck");
		boolean checkValue = false;
		if (checkBoxItem != null)
			checkBoxItem.getValueAsBoolean();
		aclTypeForm.reset();
		// aclDtlForm.reset();
		aclTypeForm.editRecord(grid.getSelectedRecord());
		if (checkBoxItem != null)
			checkBoxItem.setValue(checkValue);
		// aclDtlForm.editRecord(grid.getSelectedRecord());
		// allChkCrtlByEach();
	}

	/**
	 * ACL Grid에 입력한 Row 추가
	 */
	private void addRow() {
		GWT.log("[ SecurityACLGridPanel addRow ]", null);

		ListGridRecord record = new ListGridRecord();
		record.setAttribute("groupType", aclTypeForm.getField("groupType")
				.getValue());
		record.setAttribute("entityId", "");
		record.setAttribute("name", "");
		// record.setAttribute("view", aclDtlForm.getField("view").getValue());
		// record.setAttribute("print",
		// aclDtlForm.getField("print").getValue());
		record.setAttribute("read", true);
		// record.setAttribute("read", aclDtlForm.getField("read").getValue());
		// record.setAttribute("write",
		// aclDtlForm.getField("write").getValue());
		// record.setAttribute("add", aclDtlForm.getField("add").getValue());
		// record.setAttribute("rename",
		// aclDtlForm.getField("rename").getValue());
		// record.setAttribute("pdelete",
		// aclDtlForm.getField("pdelete").getValue());
		// record.setAttribute("check",
		// aclDtlForm.getField("check").getValue());
		// record.setAttribute("download",
		// aclDtlForm.getField("download").getValue());
		// record.setAttribute("extend",
		// aclDtlForm.getField("extend").getValue());
		// record.setAttribute("control",
		// aclDtlForm.getField("control").getValue());
		grid.addData(record);

		if (Long.parseLong(securityProfileItem.getValueAsString()) != SSecurityProfile.PROFILE_PRIVATEACL) {
			securityProfileItem.setValue(SSecurityProfile.PROFILE_PRIVATEACL);
		}

		grid.ungroup();
		grid.groupBy("groupType");
		// grid.selectRecord(record);
		grid.selectSingleRecord(record);
		grid.markForRedraw();
		grid.scrollToRow(grid.getRecordIndex(record));
		recordClick();
		aclTypeForm.getField("entityId").clearValue();
		aclTypeForm.getField("name").clearValue();
		// btnSave.enable();
	}

	/**
	 * ACL Grid에 입력한 Row 수정
	 * 
	 * @param record
	 */
	private void updateRow(Record record) {

		grid.ungroup();
		grid.groupBy("groupType");
		// grid.selectRecord(record);
		grid.selectSingleRecord(record);
		grid.markForRedraw();
		grid.scrollToRow(grid.getRecordIndex(record));

		int rowNum = grid.getRecordIndex(record);
		GWT.log("[ SecurityACLGridPanel updateRow ] rowNum[" + rowNum + "]",
				null);

		record.setAttribute("groupType", aclTypeForm.getField("groupType")
				.getValue());
		record.setAttribute("entityId", aclTypeForm.getField("entityId")
				.getValue());
		record.setAttribute("name", aclTypeForm.getField("name").getValue());
		// record.setAttribute("view", aclDtlForm.getField("view").getValue());
		// record.setAttribute("print",
		// aclDtlForm.getField("print").getValue());
		// record.setAttribute("read", aclDtlForm.getField("read").getValue());
		// record.setAttribute("write",
		// aclDtlForm.getField("write").getValue());
		// record.setAttribute("add", aclDtlForm.getField("add").getValue());
		// record.setAttribute("rename",
		// aclDtlForm.getField("rename").getValue());
		// record.setAttribute("pdelete",
		// aclDtlForm.getField("pdelete").getValue());
		// record.setAttribute("check",
		// aclDtlForm.getField("check").getValue());
		// record.setAttribute("download",
		// aclDtlForm.getField("download").getValue());
		// record.setAttribute("extend",
		// aclDtlForm.getField("extend").getValue());
		// record.setAttribute("control",
		// aclDtlForm.getField("control").getValue());
		grid.refreshRow(rowNum);

		if (Long.parseLong(securityProfileItem.getValueAsString()) != SSecurityProfile.PROFILE_PRIVATEACL) {
			securityProfileItem.setValue(SSecurityProfile.PROFILE_PRIVATEACL);
		}
		// btnSave.enable();
	}

	/**
	 * 수정된 Access Control List(Grid) Data 저장
	 */
	private void executeSave() {
		final List<Record> sendRecords = new ArrayList<Record>();

		grid.ungroup();
		grid.groupBy("groupType");

		RecordList recordlist = grid.getRecordList();
		if (!recordlist.isEmpty()) {
			for (int j = 0; j < recordlist.getLength(); j++) {
				Record record = recordlist.get(j);
				if (record.getAttributeAsString("entityId") != null) { // group일	// 경우	// null임
					sendRecords.add(record);
				}
			}
		}
		folderForSave = new SFolder();
		folderForSave.setId(folderId);
		// if(docId != 0)
		// folderForSave.setSecurityProfileId(0L);
		// folderForSave.setSecurityProfileId(SSecurityProfile.PROFILE_PRIVATEACL);

		// else
		folderForSave.setSecurityProfileId(Long.parseLong(securityProfileItem
				.getValueAsString()));

		if (!sendRecords.isEmpty()) {
			SRight[] rights = new SRight[sendRecords.size()];
			for (int j = 0; j < sendRecords.size(); j++) {
				rights[j] = new SRight();
				rights[j].setGroupType(Integer.valueOf(sendRecords.get(j)
						.getAttributeAsString("groupType")));
				String entityId = sendRecords.get(j).getAttributeAsString(
						"entityId");
				if (entityId == null)
					entityId = "";
				rights[j].setEntityId(entityId);
				rights[j].setName(sendRecords.get(j).getAttributeAsString("name"));
				rights[j].setView(Boolean.valueOf(sendRecords.get(j).getAttributeAsString("view")));
				//20150509na GS인증용 프린트 제거
//				rights[j].setPrint(Boolean.valueOf(sendRecords.get(j).getAttributeAsString("print")));
				rights[j].setRead(Boolean.valueOf(sendRecords.get(j).getAttributeAsString("read")));
				rights[j].setWrite(Boolean.valueOf(sendRecords.get(j).getAttributeAsString("write")));
				rights[j].setAdd(Boolean.valueOf(sendRecords.get(j)	.getAttributeAsString("add")));
				rights[j].setRename(Boolean.valueOf(sendRecords.get(j).getAttributeAsString("rename")));
				rights[j].setDelete(Boolean.valueOf(sendRecords.get(j).getAttributeAsString("pdelete")));
				rights[j].setCheck(Boolean.valueOf(sendRecords.get(j)	.getAttributeAsString("check")));
				rights[j].setDownload(Boolean.valueOf(sendRecords.get(j)	.getAttributeAsString("download")));
				rights[j].setExtend(Boolean.valueOf(sendRecords.get(j).getAttributeAsString("extend")));
				rights[j].setControl(Boolean.valueOf(sendRecords.get(j).getAttributeAsString("control")));
				rights[j].setStartday(retentionCheckItem.getValueAsBoolean() ? Util.convertNoTimeDate(startDayItem.getValueAsDate()) : null);
				rights[j].setExpiredday(retentionCheckItem.getValueAsBoolean() ? Util.convertNoTimeDate(expiredDayItem.getValueAsDate()) : null);
			}
			folderForSave.setRights(rights);
			
//			if(validator.check(Arrays.asList(rights), folderForSave.getSecurityProfileId(), retentionCheckItem.getValueAsBoolean())){
//				SC.say(I18N.message("nothingchanged"));
//				return;
//			}
//			
//			validator.setMap(Arrays.asList(rights), retentionCheckItem.getValueAsBoolean());
//			validator.setValue(folderForSave.getSecurityProfileId());
		}
		else{
//			if(validator.check(new ArrayList<SRight>(), folderForSave.getSecurityProfileId(), retentionCheckItem.getValueAsBoolean())){
//				SC.say(I18N.message("nothingchanged"));
//				return;
//			}
//			
//			validator.setMap(new ArrayList<SRight>(), retentionCheckItem.getValueAsBoolean());
//			validator.setValue(folderForSave.getSecurityProfileId());
		}
		
		

		// goodbong - 수정
		ServiceUtil.folder().applyRights(Session.get().getSid(), folderForSave,docId, false, new AsyncCallbackWithStatus<Void>() {
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
						// btnSave.disable();
						if (!sendRecords.isEmpty()) {
							// grid.selectRecord(sendRecords.get(0));
							grid.selectSingleRecord(sendRecords.get(0));
							grid.markForRedraw();
							recordClick();
						}
						// 공유 폴더에서 완료 동작
						if (DocumentActionUtil.get().getActivatedMenuType() == DocumentActionUtil.TYPE_SHARED) {
							// 해당 row 값만 Refresh
							if (docId > 0)
								refreshRow();
						}
						
//						SC.say(I18N.message("operationcompleted"));
						// kimsoeun GS인증용  - 작업 완료 -> 저장 완료 문구 변경
						SC.say(I18N.message("savecompleted"));
						
						folderObserver.onFolderSaved(folderForSave);
						// 20131227na 폴더생성시 권한을 넣을 경우 정상동작이 안되어서 주석처리
						// Admin Menu에서 완료 동작
						// else{
						// if(folderObserver != null) {
						// folderObserver.onFolderSaved(folderForSave);
						// }
						// }		
					}
				});
	}

	public void disable(String reason) {
		grid.setCanEdit(false);
		grid.hideField("remove");
		aclTypeForm.setDisabled(true);
		actionHL.setDisabled(true);
		securityProfileItem.setDisabled(true);

		retentionCheckItem.setDisabled(true);
		startDayItem.disable();
		expiredDayItem.disable();

		if (reason != null) {
			String title = subTitleLable.getContents();
			title = title + "(" + reason + ")";
			subTitleLable.setContents(title);
		}
	}

	private void refreshRow() {
		ServiceUtil.document().getById(Session.get().getSid(), docId,
				new AsyncCallback<SDocument>() {
					@Override
					public void onSuccess(SDocument result) {
						DocumentsPanel.get().onDocumentSaved(result);
					}

					@Override
					public void onFailure(Throwable caught) {
						Log.serverError(caught, false);
					}
				});
	}

	public SelectItem getSecurityProfileItem() {
		return securityProfileItem;
	}

}