package com.speno.xedm.gui.frontend.client.admin.organization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.RecordList;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.DragDataAction;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.types.SortDirection;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.TransferImgButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.CheckboxItem;
import com.smartgwt.client.widgets.form.fields.HiddenItem;
import com.smartgwt.client.widgets.form.fields.PasswordItem;
import com.smartgwt.client.widgets.form.fields.PickerIcon;
import com.smartgwt.client.widgets.form.fields.TextAreaItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.FormItemClickHandler;
import com.smartgwt.client.widgets.form.fields.events.FormItemIconClickEvent;
import com.smartgwt.client.widgets.form.fields.events.KeyUpEvent;
import com.smartgwt.client.widgets.form.fields.events.KeyUpHandler;
import com.smartgwt.client.widgets.form.validator.MatchesFieldValidator;
import com.smartgwt.client.widgets.form.validator.RegExpValidator;
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
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.speno.xedm.core.service.serials.SExtendedAttribute;
import com.speno.xedm.core.service.serials.SGroup;
import com.speno.xedm.core.service.serials.SUser;
import com.speno.xedm.gui.common.client.Constants;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.PagingObserver;
import com.speno.xedm.gui.common.client.RecordObserver;
import com.speno.xedm.gui.common.client.Session;
import com.speno.xedm.gui.common.client.log.Log;
import com.speno.xedm.gui.common.client.util.AsyncCallbackWithStatus;
import com.speno.xedm.gui.common.client.util.DateCellFormatter;
import com.speno.xedm.gui.common.client.util.ItemFactory;
import com.speno.xedm.gui.common.client.util.LengthValidator;
import com.speno.xedm.gui.common.client.util.ListGridMultipleItem;
import com.speno.xedm.gui.common.client.util.SCM;
import com.speno.xedm.gui.common.client.util.ServiceUtil;
import com.speno.xedm.gui.common.client.util.regex;
import com.speno.xedm.gui.frontend.client.PagingToolStrip;
import com.speno.xedm.gui.frontend.client.admin.CommSearchDialog;
import com.speno.xedm.gui.frontend.client.admin.CommSearchDialog.ResultHandler;
import com.speno.xedm.util.paging.PagingConfig;
import com.speno.xedm.util.paging.PagingResult;

/**
 * UserGrid Panel
 * 
 * @author 박상기
 * @since 1.0
 */
public class UserGridPanel extends VLayout implements PagingObserver {	
	private static HashMap<String, UserGridPanel> instanceMap = new HashMap<String, UserGridPanel>();
		
	public ListGrid grid;
	private PagingToolStrip gridPager;
	private Layout indexPanel;
	
	private DynamicForm form;
	private DynamicForm form2;
	private HLayout actHL;
	
	private String name = "";
	private boolean isShowAct = true;
	
	private Map<String, SGroup[]> groupsMap;
	private GroupTreePanel groupTreePanel;
	private DepartmentGridPanel departmentGridPanel;
	private ListGridMultipleItem multiValueItem;
	private CheckboxItem homeUsableCb;
	
	private final RecordObserver recordObserver;
	
	private String searchText = "";
	private long homeFolderId;
	
	UserGridPanel userGridPanel = this;
		
	/**
	 * 기 생성된 instance를 반환함. instance가 없을 경우 생성 후 반환함
	 * @param id
	 * @param subTitle
	 * @return
	 */
	public static UserGridPanel get(
			final String id, 
			final String subTitle) {
		return get(id, null, subTitle, true, false, true, "100%");
	}
	
	/**
	 * 기 생성된 instance를 반환함. instance가 없을 경우 생성 후 반환함
	 * @param id
	 * @param ob
	 * @param subTitle
	 * @param isIndexPosTop
	 * @param isCanDrag
	 * @param isShowAct
	 * @param width
	 * @return
	 */
	public static UserGridPanel get(
			final String id, 
			final RecordObserver ob,
			final String subTitle, 
			final boolean isIndexPosTop, 
			final boolean isCanDrag, 
			final boolean isShowAct, 
			final String width) {
		if (instanceMap.get(id) == null) {
			new UserGridPanel(id, ob, subTitle, isIndexPosTop, isCanDrag, isShowAct, width);
		}
		return instanceMap.get(id);
	}
	
	public UserGridPanel(
			final String id, 
			final String subTitle) {
		this(id, null, subTitle, true, false, true, "100%");
	}
	public UserGridPanel(
			final String id, 
			final RecordObserver ob,
			final String subTitle, 
			final boolean isIndexPosTop,
			final boolean isCanDrag, 
			final boolean isShowAct, 
			final String width) {		
		instanceMap.put(id, this);
		
		setMembersMargin(Constants.SUBTITLE_MARGIN);
		
		this.recordObserver = ob;
		this.isShowAct = isShowAct;
		
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
        grid.setSelectionType(isShowAct ? SelectionStyle.SINGLE : SelectionStyle.MULTIPLE);
        grid.setCanResizeFields(true);
        //grid.setShowFilterEditor(true); No DataSource or invalid DataSource specified, can't create data model 
        //grid.setFilterOnKeypress(true);        
        if(isCanDrag) {
        	//grid의 drag 환경 설정
        	grid.setDragDataAction(DragDataAction.COPY);        
	        grid.setCanReorderRecords(false);
	        grid.setCanAcceptDroppedRecords(true);
	        grid.setCanDragRecordsOut(true);        
        }
        
        ListGridField idField = new ListGridField("id",  I18N.message("id"));
        ListGridField userIdField = new ListGridField("username",  I18N.message("userid"));
        ListGridField userNameField = new ListGridField("name", I18N.message("uusername"));
        ListGridField departmentField = new ListGridField("department", I18N.message("department"));
        
        idField.setHidden(true);
        
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
			@Override
            public void onRecordClick(RecordClickEvent event) {
				recordClickedProcess(event.getRecord());
				if(recordObserver != null) {
					recordObserver.onRecordClick(event.getRecord());
				}
            }   
        });
		
        if(isShowAct) {
        	
            /*
             * isShowAct 모드에서 선택할 수 있는 Check Type
             * 1) grid.setSelectionAppearance(SelectionAppearance.CHECKBOX);
             * 2) checkField.setType(ListGridFieldType.BOOLEAN);
             */
        	
        	grid.setCanRemoveRecords(true);
        	
        	ListGridField checkField = new ListGridField("check", I18N.message("delete"));
        	checkField.setWidth(40);
        	checkField.setType(ListGridFieldType.BOOLEAN);
        	checkField.setCanEdit(true);
        	
    		ListGridField descriptionField = new ListGridField("description", I18N.message("description"));
    		ListGridField creationDateField = new ListGridField("creationdate", I18N.message("creationdate"), 130);
    		ListGridField dutyIdField = new ListGridField("dutyid", I18N.message("dutyid"));
    		ListGridField dutyNameField = new ListGridField("dutyname", I18N.message("dutyname"));
    		ListGridField positionIdField = new ListGridField("positionid");
    		ListGridField positionNameField = new ListGridField("positionname");
    		ListGridField exAttributeField = new ListGridField("exattribute");
    		ListGridField homeFolderIdField = new ListGridField("homeFolderId");
    		ListGridField homeUsableField = new ListGridField("homeUsable");
    		ListGridField usedSpaceField = new ListGridField("usedSpace");
    		ListGridField totalSpaceField = new ListGridField("totalSpace");
    

    		creationDateField.setAlign(Alignment.CENTER);
    		creationDateField.setType(ListGridFieldType.DATE);
    		creationDateField.setCellFormatter(new DateCellFormatter(I18N.message("yyyy/MM/dd HH:mm:ss")));
    		
    		dutyIdField.setHidden(true);
    		dutyNameField.setHidden(true);
    		positionIdField.setHidden(true);
    		positionNameField.setHidden(true);
    		exAttributeField.setHidden(true);
    		homeFolderIdField.setHidden(true);
    		homeUsableField.setHidden(true);
    		usedSpaceField.setHidden(true);
    		totalSpaceField.setHidden(true);
    		
    		//record 삭제 event handler 정의--------------------------------------------------------------
    		grid.addRemoveRecordClickHandler(new RemoveRecordClickHandler() {
    			@Override
    			public void onRemoveRecordClick(final RemoveRecordClickEvent event) {
    				event.cancel();
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
    			}
    		});    		
    		
    		grid.setFields(checkField, idField, userIdField, userNameField,departmentField, descriptionField, 
    				creationDateField, dutyIdField, dutyNameField, 
    				positionIdField, positionNameField, exAttributeField, homeFolderIdField, homeUsableField, usedSpaceField, totalSpaceField);
    		grid.sort("creationdate", SortDirection.DESCENDING);
        }
        else {
        	grid.setFields(idField, userIdField, userNameField, departmentField);
        }            
                
        Layout gridPanel = isIndexPosTop ? new VLayout() : new HLayout();
        gridPanel.setHeight100();
        gridPanel.setMembersMargin(0);

        
        addMember(createSearch());
        
        if(isIndexPosTop) {
        	gridPanel.setMembers(createUserIndexBtnsVL(isIndexPosTop), grid);
        }
        else {
        	gridPanel.setMembers(grid, createUserIndexBtnsVL(isIndexPosTop));
        }
        
        gridPager = new PagingToolStrip(grid, 20, true, this);
        gridPager.setDeselect(false);
        gridPager.setIsAutoHeightGrid(false);
        
        grid.setBodyOverflow(Overflow.SCROLL);
        
        VLayout userGridPagerPanel = new VLayout();
        userGridPagerPanel.setHeight100();
        userGridPagerPanel.setShowResizeBar(isShowAct);
        userGridPagerPanel.setMembersMargin(1);
        userGridPagerPanel.addMember(gridPanel); 
        userGridPagerPanel.addMember(gridPager);
        
        if(isShowAct) {
        	VLayout userVL = new VLayout(5);
        	userVL.setMembers(userGridPagerPanel, createFormHL(), createActHL());
        	addMember(userVL);
        }
        else {
        	addMember(userGridPagerPanel);
        }
        setWidth(width);
        
        executeFetch(1, gridPager.getPageSize());
	}
	
	private DynamicForm createSearch() {
		// TODO Auto-generated method stub
		 final DynamicForm dfTop = new DynamicForm();   
//			dfTop.setWidth(250);
	     dfTop.setNumCols(2);
	     dfTop.setAlign(Alignment.RIGHT);
	     dfTop.setShowEdges(false);
	     // =================================================
	     // 검색바
	     final TextItem searchText = new TextItem("userSearch", I18N.message("UserIdORUserName"));
	     searchText.setWidth(230);
	     searchText.setShowTitle(false);
	     searchText.setWrapTitle(false);
	     searchText.setCanEdit(true);
	     searchText.setDisableIconsOnReadOnly(false);
	//	        searchText.setLength(Session.get().getInfo().getIntConfig("gui.title.fieldsize", 255));
	     searchText.setValidators(new LengthValidator(searchText, Session.get().getInfo().getIntConfig("gui.title.fieldsize", 255)));
	     searchText.addKeyUpHandler(new KeyUpHandler() {
				@Override
				public void onKeyUp(KeyUpEvent event) {
					if(event.getKeyName().equals("Enter")){
						search(searchText.getValueAsString(), 1, gridPager.getPageSize());
					}
				}
			});
	
	     // 검색
	     PickerIcon searchPicker = new PickerIcon(PickerIcon.SEARCH, new FormItemClickHandler() {   
	         public void onFormItemClick(FormItemIconClickEvent event) {  
					search(searchText.getValueAsString(), gridPager.getPageNum(), gridPager.getPageSize());
	         }   
	     });
	
	     // 삭제 버튼
//	     PickerIcon clearPicker = new PickerIcon(PickerIcon.REFRESH, new FormItemClickHandler() {   
//	     	public void onFormItemClick(FormItemIconClickEvent event) { 
//	         	 dfTop.getField("userSearch").clearValue();
//	         }   
//	     });
	     
//	     searchText.setIcons(searchPicker, clearPicker);
	     searchText.setIcons(searchPicker);
	     dfTop.setItems(searchText);
	     return dfTop;
	}

	/**
	 * Record Click Event Handler
	 * @param record
	 */
	private void recordClickedProcess(Record record) {
		if(isShowAct) {
			form.reset();
	    	form.editRecord(record);
	    	
	    	multiValueItem.setGridData(record.getAttributeAsRecordArray("extended"));
	    	homeUsableCb.setValue("true".equals(record.getAttribute("homeUsable").toString()) ? true : false);
	    	
	    	if(record.getAttribute("id").contains("@@@")){
	    		Canvas[] canvass =  actHL.getMembers();
	    		for (Canvas canvas : canvass) {
	    			Button button = (Button) canvas;
//	    			if(!button.getTitle().equals(I18N.message("addnew")))
//	    			button.disable();
				}
	    	}
	    	else{
	    		Canvas[] canvass =  actHL.getMembers();
	    		for (Canvas canvas : canvass) {
	    			Button button = (Button) canvas;
	    			button.enable();
				}
	    	}
			departmentGridPanel.resetGrid();
			SGroup[] groups = groupsMap.get(record.getAttributeAsString("id"));
			departmentGridPanel.addData(groups);
		}
	}
	
	protected String getName() {
		return name;
	}
	
	private void setName(final String name) {
		this.name = name;
	} 
	
	/**
	 * 
	 * @return
	 */
	public ListGrid getGrid() {
		return grid;
	}
	
	/**
	 * 색인버튼 패널 생성
	 */
	private Layout createUserIndexBtnsVL(final boolean isIndexPosTop) {
		
		final IndexBtn[] btns = IndexBtn.getBtns();
		
		ClickHandler indexBtnsClickHandler = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				grid.setData(new ListGridRecord[0]); //그리드 초기화
				
				IndexBtn btn = (IndexBtn)event.getSource();				
				for(int i=0; i<btns.length; i++) {
		    		btns[i].setISelected(btn.getID().equals(btns[i].getID()));
		    	}
				setName(btn.getIValue());
				gridPager.goToPage(1);
			}
		};
		
        indexPanel = isIndexPosTop ? new HLayout(1) : new VLayout(1);
        indexPanel.setAutoHeight();
    	indexPanel.setAutoWidth();    	
    	for(int i=0; i<btns.length; i++) {
    		btns[i].setAddClickHandler(indexBtnsClickHandler);
    		indexPanel.addMember(btns[i]);
    	}
        return indexPanel;
	}
	
	private HLayout createFormHL() {		
		HiddenItem idItem = new HiddenItem("id");
		idItem.setVisible(false);
		
		HiddenItem dutyIdItem = new HiddenItem("dutyid");
		dutyIdItem.setVisible(false);
		
		HiddenItem positionIdItem = new HiddenItem("positionid");
		positionIdItem.setVisible(false);
		
		RegExpValidator regExpIdValidator = new RegExpValidator();   
		regExpIdValidator.setExpression("[0-9a-zA-Z]");   
        
		TextItem userIdItem = new TextItem("username", I18N.message("userid"));
//		userIdItem.setLength(Constants.MAX_LEN_ID);
		userIdItem.setValidators(regExpIdValidator, new LengthValidator(userIdItem, Constants.MAX_LEN_ID));
		userIdItem.setWrapTitle(false);
		userIdItem.setRequired(true);   
		// kimsoeun GS인증용 - 툴팁 다국어화
		userIdItem.setRequiredMessage(I18N.message("fieldisrequired"));
		
		TextItem nmItem = new TextItem("name", I18N.message("uusername"));
		nmItem.setWrapTitle(false);
//		nmItem.setLength(Constants.MAX_LEN_NAME);
		nmItem.setValidators(new LengthValidator(nmItem, Constants.MAX_LEN_NAME));
		nmItem.setRequired(true);
		// kimsoeun GS인증용 - 툴팁 다국어화
		nmItem.setRequiredMessage(I18N.message("fieldisrequired"));
		
		MatchesFieldValidator validator = new MatchesFieldValidator();   
        validator.setOtherField("oldPassword");   
        validator.setErrorMessage(I18N.message("passwordsdonotmatch"));
		
		PasswordItem password = new PasswordItem("password", I18N.message("password"));
        // kimsoeun GS인증용 - 비밀번호 관련 필드 필수 지정
		// password.setRequired(true);   
		// kimsoeun GS인증용 - 툴팁 다국어화
		password.setRequiredMessage(I18N.message("fieldisrequired"));
        password.setValidators(validator, new LengthValidator(password, Session.get().getInfo().getIntConfig("gui.password.fieldsize", 255)));
        PasswordItem oldPassword = new PasswordItem("oldPassword", I18N.message("passwordagain"));   
        // kimsoeun GS인증용 - 비밀번호 관련 필드 필수 지정
        // oldPassword.setRequired(true);	
        // kimsoeun GS인증용 - 툴팁 다국어화
        oldPassword.setRequiredMessage(I18N.message("fieldisrequired"));
        oldPassword.setValidators(new LengthValidator(oldPassword, Session.get().getInfo().getIntConfig("gui.password.fieldsize", 255)));
		
		PickerIcon dutyClearPicker = new PickerIcon(PickerIcon.CLEAR, new FormItemClickHandler() {   
            public void onFormItemClick(FormItemIconClickEvent event) {   
            	form.getField("dutyid").clearValue();
				form.getField("dutyname").clearValue();
            }   
        });
		
		PickerIcon dutySearchPicker = new PickerIcon(PickerIcon.SEARCH, new FormItemClickHandler() {   
            public void onFormItemClick(FormItemIconClickEvent event) {   
            	final CommSearchDialog commSearchDialog = new CommSearchDialog(CommSearchDialog.DUTY);
				commSearchDialog.addResultHandler(new ResultHandler() {
					@Override
					public void onSelected(HashMap<String, String> resultMap) {						
						form.getField("dutyid").setValue(resultMap.get("id"));
						form.getField("dutyname").setValue(resultMap.get("name"));
					}
				});
				commSearchDialog.show();
            }   
        });
		
		TextItem dutyNameItem = new TextItem("dutyname", I18N.message("duty"));
		dutyNameItem.setWrapTitle(false);
		dutyNameItem.setCanEdit(false);
		dutyNameItem.setIcons(dutyClearPicker, dutySearchPicker);
		dutyNameItem.setDisableIconsOnReadOnly(false);
//		dutyNameItem.setLength(Session.get().getInfo().getIntConfig("gui.title.fieldsize", 255));
		dutyNameItem.setValidators(new LengthValidator(dutyNameItem, Session.get().getInfo().getIntConfig("gui.title.fieldsize", 255)));
		
		PickerIcon positionClearPicker = new PickerIcon(PickerIcon.CLEAR, new FormItemClickHandler() {   
            public void onFormItemClick(FormItemIconClickEvent event) {   
            	form.getField("positionid").clearValue();
				form.getField("positionname").clearValue();
            }   
        });
		
		PickerIcon positionSearchPicker = new PickerIcon(PickerIcon.SEARCH, new FormItemClickHandler() {   
            public void onFormItemClick(FormItemIconClickEvent event) {   
            	final CommSearchDialog commSearchDialog = new CommSearchDialog(CommSearchDialog.POSITION);
				commSearchDialog.addResultHandler(new ResultHandler() {
					@Override
					public void onSelected(HashMap<String, String> resultMap) {
						form.getField("positionid").setValue(resultMap.get("id"));
						form.getField("positionname").setValue(resultMap.get("name"));
					}
				});
				commSearchDialog.show();
            }   
        });
		
		TextItem positionNameItem = new TextItem("positionname", I18N.message("position"));
		positionNameItem.setWrapTitle(false);
		positionNameItem.setCanEdit(false);
		positionNameItem.setIcons(positionClearPicker, positionSearchPicker);
		positionNameItem.setDisableIconsOnReadOnly(false);
		
		TextAreaItem descriptionItem = new TextAreaItem("description", I18N.message("description"));
		descriptionItem.setWrapTitle(false);
//		descriptionItem.setLength(Constants.MAX_LEN_DESC);
		descriptionItem.setValidators(new LengthValidator(descriptionItem, Constants.MAX_LEN_DESC));
		descriptionItem.setHeight("40");
		
		// soeun TODO validator
		TextItem totalSpaceItem = new TextItem("totalSpace", I18N.message("totalSpace"));
		totalSpaceItem.setWrapTitle(false);
//		totalSpaceItem.setValidators(new LengthValidator(totalSpaceItem, 1099500000),
//									new PositiveNumberValidator(totalSpaceItem));
		totalSpaceItem.setRequired(false);
		
		TextItem usedSpaceItem = new TextItem("usedSpace", I18N.message("usedSpace"));
		usedSpaceItem.setWrapTitle(false);
		usedSpaceItem.setCanEdit(false);
		
		homeUsableCb = new CheckboxItem("homeUsable",I18N.message("homeUsable"));
		homeUsableCb.setType("boolean");		
		homeUsableCb.setCanEdit(true);		
		homeUsableCb.setWrapTitle(false);
		
		TextItem homeFolderId = new TextItem("homeFolderId", I18N.message("homeFolderId"));
		homeFolderId.hide();
		
		nmItem.setStartRow(true);	
		nmItem.setEndRow(true);
		
		// 확장속성
		multiValueItem = ItemFactory.newListGridMultipleItem(
				"extended", I18N.message("extentionproperties"), 
				new String[]{"name","value"}, new String[]{I18N.message("name"), I18N.message("value")}, new int[]{255,255},
				100, false);
		multiValueItem.setTitleOrientation(TitleOrientation.TOP);
		multiValueItem.setTitleAlign(Alignment.CENTER);
	    multiValueItem.setRequired(false);
	    multiValueItem.setHeight(200);
	


	    form = new DynamicForm();
		form.setAutoWidth();
//		form.setMargin(4);
		form.setItems(idItem, dutyIdItem, positionIdItem, userIdItem, nmItem, password, oldPassword, dutyNameItem, positionNameItem, descriptionItem, usedSpaceItem, totalSpaceItem, homeUsableCb, homeFolderId);
    	form.reset();
    	
    	form2 = new DynamicForm();
    	form2.setAutoWidth();
    	form2.setItems(multiValueItem);
    	form2.reset();
    	
    	HLayout formHL = new HLayout(10);
    	formHL.setBorder("1px solid gray");
    	formHL.setWidth100();
    	formHL.setHeight100();
    	formHL.setOverflow(Overflow.SCROLL);
    	formHL.addMember(form);
    	formHL.addMember(form2);
    	formHL.addMember(createGroupControl());
    	return formHL;
	}
	
	/**
	 * Arrow패널 생성
	 */
	private VLayout createArrowVL() {
		TransferImgButton leftArrow = new TransferImgButton(TransferImgButton.LEFT, new ClickHandler() {   
            public void onClick(ClickEvent event) {
            	departmentGridPanel.copyRecordsToMembers();
            }   
        });   
        
		TransferImgButton rightArrow = new TransferImgButton(TransferImgButton.RIGHT, new ClickHandler() {   
            public void onClick(ClickEvent event) {   
            	departmentGridPanel.removeRecordsFromMembers();   
            }   
        });
        
        VLayout arrowPanel = new VLayout();
        arrowPanel.setWidth(30);
        arrowPanel.setHeight100();
        arrowPanel.setAlign(VerticalAlignment.CENTER);
        arrowPanel.setMembersMargin(Constants.ARROW_MARGIN);
        arrowPanel.setMembers(leftArrow, rightArrow);
        return arrowPanel;
	}

	/**
	 * Action Panel 생성
	 * @return
	 */
	private HLayout createActHL() {		
		Button btnRowsRemove = new Button(I18N.message("checkeddel"));
		btnRowsRemove.setWidth(120);
		btnRowsRemove.setIcon("[SKIN]/MultiUploadItem/icon_remove_files.png");
		btnRowsRemove.addClickHandler(new ClickHandler() {   
            public void onClick(ClickEvent event) {
            	executeRemove();
            }   
        });
		
		Button btnAddNew = new Button(I18N.message("addnew"));
		btnAddNew.setIcon(ItemFactory.newImgIcon("page_white_add.png").getSrc());
		btnAddNew.addClickHandler(new ClickHandler() {   
            public void onClick(ClickEvent event) {
            	Canvas[] canvass =  actHL.getMembers();
	    		for (Canvas canvas : canvass) {
	    			Button button = (Button) canvas;
	    			button.enable();
				}
            	
            	form.reset();
            	form.editNewRecord();
            	multiValueItem.setGridData(new Record[0]);
            	
            	grid.deselectAllRecords();
            	departmentGridPanel.resetGrid();
            	
            	homeUsableCb.setValue(true);
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
		
		actHL = new HLayout(10);
		actHL.setHeight(1);
		actHL.setMembers(btnRowsRemove, btnAddNew, btnSave);		
		return actHL;
	}
	
	private HLayout createGroupControl() {
		groupTreePanel = new GroupTreePanel("admin.org.user", null, true, false, false);
    	departmentGridPanel = new DepartmentGridPanel("admin.org.user", null, groupTreePanel.getGroupTree());
    	
    	HLayout groupHL = new HLayout(10);
    	groupHL.setMargin(4);
    	groupHL.setMembers(departmentGridPanel, createArrowVL(), groupTreePanel);
    	return groupHL;
	}
	
	private void executeFetch(final int pageNum, final int pageSize)	{				
		GWT.log("[ UserGridPanel executeFetch ] name["+name+"]", null);		
		PagingConfig config = PagingToolStrip.getPagingConfig(pageNum, pageSize);
		
		ServiceUtil.security().pagingUsersByName(name, config, new AsyncCallbackWithStatus<PagingResult<SUser>>() {
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
			public void onSuccessEvent(PagingResult<SUser> result) {
				int totalLength = result.getTotalLength();
				
				grid.setData(new ListGridRecord[0]); //그리드 초기화
				groupsMap = new HashMap<String, SGroup[]>(); //해당 유저의 그룹정보 초기화
				
				SUser user;			
				SGroup duty;
				SGroup position;
				
				List<SUser> data = result.getData();					
				for (int j = 0; j < data.size(); j++) {
					
					user = data.get(j);			
					duty = user.getDuty();
					position = user.getPosition();
					
					ListGridRecord record=new ListGridRecord();
					record.setAttribute("id", user.getId());
					record.setAttribute("name", user.getName());
					
					if (user.getUserGroup() != null && user.getGroupLength() != 0) {
						record.setAttribute("groupid", user.getUserGroup().getId());
						record.setAttribute("departmentid", user.getDepartmentId());
//						record.setAttribute("department", user.getDepartment());
						SGroup[] groups =  user.getGroups();
						if(groups.length > 1){
						//20140221na 그룹 여러개 보이기
							String departments = groups[0].getName() ;
							for (int i = 1; i < groups.length; i++) {
								departments = departments + ", " + groups[i].getName() ; 
							}
							record.setAttribute("department", departments);
						}
						else record.setAttribute("department", user.getDepartment());
					}
					record.setAttribute("username", user.getUserName());
					record.setAttribute("description", user.getDescription());
					record.setAttribute("creationdate", user.getCreationDate());
					record.setAttribute("dutyid", (duty != null) ? duty.getId() : null);
					record.setAttribute("dutyname", (duty != null) ? duty.getName() : null);
					record.setAttribute("positionid", (position != null) ? position.getId() : null);
					record.setAttribute("positionname", (position != null) ? position.getName() : null);
					record.setAttribute("extended", getAttributesAsRecordArray(user));
					// soeun 사용량, 사용 제한 추가
					if(user.getTotalSpace() != null) {
						record.setAttribute("totalSpace", user.getTotalSpace());
						record.setAttribute("usedSpace", user.getUsedSpace());						
					} else {
						record.setAttribute("totalSpace", 0);
						record.setAttribute("usedSpace", 0);
					}
					record.setAttribute("homeFolderId", user.getHomeFolderId());						
					record.setAttribute("homeUsable", user.isHomeUsable());		
					
					grid.addData(record);					
					groupsMap.put(user.getId(), user.getGroups());
				}	
				
				if (data.size() > 0) {
					grid.selectSingleRecord(0);
					recordClickedProcess(grid.getRecord(0));
				}
				GWT.log("totalLength["+totalLength+"], pageNum["+ pageNum + "]", null);					
				gridPager.setRespPageInfo(totalLength, pageNum);
			}
		});
	}
	
	/**
	 * 선택된 Rows 제거
	 */
	private void executeRemove() {
		GWT.log("[ UserGridPanel executeRemove ]", null);
				
		RecordList recordList = grid.getRecordList();
		if( recordList.isEmpty() ) {
			SC.say(I18N.message("noitemstodelete"));
			return;
		}		
		
		final List<String> usersList = new ArrayList<String>();
		Record record;
		for(int j=0; j<recordList.getLength(); j++) {
			record = recordList.get(j);
			if( record.getAttributeAsBoolean("check") ) {
				usersList.add(record.getAttributeAsString("id"));
			}
		}
		
		if(usersList.isEmpty()) {
			SC.say(I18N.message("checkeditemsnotexist"));
			return;
		}
		
		SC.confirm(I18N.message("checkeddelete", String.valueOf(usersList.size())), new BooleanCallback() {
			@Override
			public void execute(Boolean value) {
				if(value != null && value) {
					String[] removeUsers = new String[usersList.size()];
					for(int j=0; j<usersList.size(); j++) {
						removeUsers[j] = usersList.get(j);
					}
					
					ServiceUtil.security().deleteUser(Session.get().getSid(), removeUsers, new AsyncCallbackWithStatus<Void>() {
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
							GWT.log("[ UserGridPanel executeRemove ]");
							SC.say(I18N.message("operationcompleted"));				
							gridPager.goToPage(gridPager.getPageNum()); //재조회
						}
					});
				}
			}
		});
	}
	
	private boolean userValidation(){
		String userName = form.getValueAsString("username");
		
		// 수정시 Validation.
		ListGridRecord selectedRecord = grid.getSelectedRecord();
		if(selectedRecord != null){
			if(selectedRecord.getAttributeAsString("username").equals(form.getValueAsString("username"))){
				return false;
			}
		}
		
		// 새로 생성시 Validation.
		ListGridRecord[] records = grid.getRecords();
		for (ListGridRecord record : records) {
			if (userName.equals(record.getAttributeAsString("username"))) {
				SC.warn(I18N.message("dupmessage"));
				return true;
			}
		}
		
		return false;
	}
	
	// 20140129, junsoo, SUser에서 이전
	public void setAttributes(SUser suser, Record[] records) {
		if (records == null)
			return;
		SExtendedAttribute[] attributes = new SExtendedAttribute[records.length];
		int i = 0;
		for (Record a : records) {
			attributes[i] = new SExtendedAttribute();
			attributes[i].setName(a.getAttribute("name"));
			attributes[i].setStringValue(a.getAttribute("value"));
			i++;
		}
		
		suser.setAttributes(attributes);
	}
	
	public Record[] getAttributesAsRecordArray(SUser suser) {
		SExtendedAttribute[] attributes = suser.getAttributes();
		Record[] ra = new Record[attributes.length];
		for (int i = 0; i < ra.length; i++) {
			String name = attributes[i].getName();
			String value = attributes[i].getStringValue();
			ra[i] = new Record();
			ra[i].setAttribute("name", name);
			ra[i].setAttribute("value", value);
		}
		return ra;
	}

	/**
	 * User 추가
	 */
	private void executeAdd() {
		GWT.log("[ UserGridPanel executeAdd ]", null);
		
		if(userValidation()) return;
		
		RecordList recordList = departmentGridPanel.getRecordList();
		if( recordList.isEmpty() ) {
			SC.say(I18N.message("grouprequired"));
			return;
		}
		
		SGroup[] groups = new SGroup[recordList.getLength()];
		for(int j=0; j<recordList.getLength(); j++) {
			groups[j] = new SGroup();
			groups[j].setId(recordList.get(j).getAttributeAsString("id"));
		}
		
		SGroup duty = null;
		String dutyId = form.getValueAsString("dutyid");
		if( dutyId != null ) {
			duty = new SGroup();
			duty.setId(dutyId);
		}
		
		SGroup position = null;
		String positionId = form.getValueAsString("positionid");
		if( positionId != null ) {
			position = new SGroup();
			position.setId(positionId);
		}		
		
		SUser sUser = new SUser();
		sUser.setId("");
		String temp = form.getValueAsString("username");	
		boolean check = regex.patten_korean(temp);
		boolean check2 = regex.patten_Schar(temp);
		if(check || check2)
		SC.warn(I18N.message("error.createkoreanid"));			
		else
		{	
		sUser.setUserName(form.getValueAsString("username"));
		sUser.setName(form.getValueAsString("name"));		
		sUser.setPassword(form.getValueAsString("password"));
		sUser.setOldPassword(form.getValueAsString("oldPassword"));
		sUser.setDescription(form.getValueAsString("description"));
		sUser.setGroups(groups);
		sUser.setDuty(duty);
		sUser.setPosition(position);
		// soeun 사용량, 사용 제한 저장
		sUser.setUsedSpace(form.getValue("usedSpace")!=null ? Long.parseLong((form.getValue("usedSpace")).toString()) : 0);
		if(form.getValue("totalSpace") != null && !"".equals(form.getValue("totalSpace"))) 
			sUser.setTotalSpace(Long.parseLong(((String)form.getValue("totalSpace"))));
		sUser.setHomeFolderId(0);
		
		
		
		if(!multiValueItem.isNullData()) setAttributes(sUser, multiValueItem.getData());
		else{
			SC.warn(I18N.message("havetoenternameandvalue"));
			return;
		}
		sUser.setHomeUsable(form.getValueAsString("homeUsable")!=null && "true".equals(form.getValueAsString("homeUsable")) ? true : false);
		ServiceUtil.security().saveUser(Session.get().getSid(), sUser, new AsyncCallbackWithStatus<SUser>() {
			
			String session = Session.get().getSid();
			
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
			public void onSuccessEvent(SUser result) {	
				GWT.log("[ UserGridPanel executeAdd ] onSuccess. id["+result.getId()+"]", null);
				SC.say(I18N.message("operationcompleted"));
				
				gridPager.goToPage(gridPager.getPageNum()); //재조회
				
				//다시 열음 레코드를 세팅하기 위해 20140319 yuk
				groupsMap.put(result.getId(), result.getGroups());
				
				final ListGridRecord addRecord = new ListGridRecord();
				addRecord.setAttribute("id",					form.getValueAsString("id"));
				addRecord.setAttribute("username",			form.getValueAsString("username"));
				addRecord.setAttribute("name",				form.getValueAsString("name"));				
				addRecord.setAttribute("description",		form.getValueAsString("description"));
				addRecord.setAttribute("dutyid",				form.getValueAsString("dutyid"));
				addRecord.setAttribute("dutyname",		form.getValueAsString("dutyname"));
				addRecord.setAttribute("positionid",		form.getValueAsString("positionid"));
				addRecord.setAttribute("positionname",	form.getValueAsString("positionname"));
				addRecord.setAttribute("extended", getAttributesAsRecordArray(result));
				grid.addData(addRecord);
				
				int selectedRowNum = grid.getRecordIndex(addRecord);
				grid.selectSingleRecord(selectedRowNum);
				grid.scrollToRow(selectedRowNum);
				
				ServiceUtil.folder().updateSpace(session, result.getHomeFolderId(), result.getUsedSpace(), result.getTotalSpace(), new AsyncCallback<Void>() {
					
					@Override
					public void onFailure(Throwable caught) {
						SCM.warn(caught);
					}

					@Override
					public void onSuccess(Void result) {
						
						//addRecord.setAttribute("usedSpace",		form.getValueAsString("usedSpace"));
						addRecord.setAttribute("totalSpace",		form.getValueAsString("totalSpace"));
						addRecord.setAttribute("homeUsable",		form.getValueAsString("homeUsable"));
						
						SC.say(I18N.message("savecompleted"));
					}
				});
				
				form.reset();
            	form.editRecord(addRecord);
            	form.getItem("id").setTooltip(I18N.message("fieldisreadonly", form.getItem("id").getTitle()));
            	
			}
		});
		}
	}
	
	/**
	 * User 수정
	 */
	private void executeUpdate() {
		GWT.log("[ UserGridPanel executeUpdate ]", null);
		
		if(userValidation()) return;
		
		RecordList recordList = departmentGridPanel.getRecordList();
		if( recordList.isEmpty() ) {
			SC.say(I18N.message("grouprequired"));
			return;
		}
		
		SGroup[] groups = new SGroup[recordList.getLength()];
		for(int j=0; j<recordList.getLength(); j++) {
			groups[j] = new SGroup();
			groups[j].setId(recordList.get(j).getAttributeAsString("id"));
		}
		
		SGroup duty = null;
		String dutyId = form.getValueAsString("dutyid");
		if( dutyId != null ) {
			duty = new SGroup();
			duty.setId(dutyId);
		}
		
		SGroup position = null;
		String positionId = form.getValueAsString("positionid");
		if( positionId != null ) {
			position = new SGroup();
			position.setId(positionId);
		}		
		
		String temp = form.getValueAsString("username");	
		boolean check = regex.patten_korean(temp);
 		boolean check2 = regex.patten_Schar(temp);
		if(check || check2)
		SC.warn(I18N.message("error.createkoreanid"));	
		else
		{		
		final SUser sUser = new SUser();
		sUser.setId(form.getValueAsString("id"));
		sUser.setUserName(form.getValueAsString("username"));
		sUser.setName(form.getValueAsString("name"));
		sUser.setPassword(form.getValueAsString("password"));
		sUser.setOldPassword(form.getValueAsString("oldPassword"));
		sUser.setDescription(form.getValueAsString("description"));
		sUser.setGroups(groups);
		sUser.setDuty(duty);
		sUser.setPosition(position);
		// soeun 사용량, 사용 제한 저장
		sUser.setUsedSpace(Long.parseLong((form.getValue("usedSpace")).toString()));
		sUser.setTotalSpace(Long.parseLong((form.getValue("totalSpace")).toString()));
		sUser.setHomeFolderId(Long.parseLong((form.getValue("homeFolderId")).toString()));
		
		sUser.setHomeUsable("true".equals(form.getValueAsString("homeUsable")) ? true : false);
		
		if(!multiValueItem.isNullData()) setAttributes(sUser, multiValueItem.getData());
		else{
			SC.warn(I18N.message("havetoenternameandvalue"));
			return;
		}
		
			ServiceUtil.security().saveUser(Session.get().getSid(), sUser, new AsyncCallbackWithStatus<SUser>() {
				
				String session = Session.get().getSid();
				
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
				public void onSuccessEvent(SUser result) {	
					GWT.log("[ UserGridPanel executeUpdate ] onSuccess. id["+result.getId()+"]", null);
					
					groupsMap.put(result.getId(), departmentGridPanel.getData());
					
					final int targetRowNum = grid.getRecordList().findIndex("id", form.getValueAsString("id"));				
					//final int selectedRowNum = grid.getRecordIndex(grid.getSelectedRecord());
					final ListGridRecord updateRecord = grid.getSelectedRecord();
					updateRecord.setAttribute("id",				form.getValueAsString("id"));
					updateRecord.setAttribute("username",		form.getValueAsString("username"));
					updateRecord.setAttribute("name",			form.getValueAsString("name"));				
					updateRecord.setAttribute("description",		form.getValueAsString("description"));
					updateRecord.setAttribute("dutyid",			form.getValueAsString("dutyid"));
					updateRecord.setAttribute("dutyname",		form.getValueAsString("dutyname"));
					updateRecord.setAttribute("positionid",		form.getValueAsString("positionid"));
					updateRecord.setAttribute("positionname",	form.getValueAsString("positionname"));
					
					updateRecord.setAttribute("homeUsable",	form.getValueAsString("homeUsable"));
					
					updateRecord.setAttribute("extended", getAttributesAsRecordArray(result));
					grid.getDataAsRecordList().set(targetRowNum, updateRecord);				
					grid.selectSingleRecord(targetRowNum);
					grid.scrollToRow(targetRowNum);
	//				SC.say(I18N.message("operationcompleted"));
					// kimsoeun GS인증용  - 작업 완료 -> 저장 완료 문구 변경
					
					ServiceUtil.folder().updateSpace(session, sUser.getHomeFolderId(), sUser.getUsedSpace(), sUser.getTotalSpace(), new AsyncCallback<Void>() {
	
						@Override
						public void onFailure(Throwable caught) {
							SCM.warn(caught);
						}
	
						@Override
						public void onSuccess(Void result) {
							
							updateRecord.setAttribute("usedSpace",		form.getValueAsString("usedSpace"));
							updateRecord.setAttribute("totalSpace",		form.getValueAsString("totalSpace"));
							updateRecord.setAttribute("homeUsable",		form.getValueAsString("homeUsable"));
							
							SC.say(I18N.message("savecompleted"));
						}
					});
					
									
				}
			});		
		}
	}
	
	/**
	 * User 삭제
	 * @param id
	 */
	private void executeRemove(final String id)	{
		GWT.log("[ UserGridPanel executeRemove ] id["+id+"]", null);
		
		String[] deleteUser = new String[1];
		deleteUser[0] = id;
		
		ServiceUtil.security().deleteUser(Session.get().getSid(), deleteUser, new AsyncCallbackWithStatus<Void>() {
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
				GWT.log("[ UserGridPanel executeRemove ] onSuccess.", null);
				SC.say(I18N.message("operationcompleted"));
				
				gridPager.goToPage(gridPager.getPageNum()); //재조회
				
				/* 재조회 하지 않을시 사용.
				grid.removeSelectedData();				
				form.editNewRecord();
            	form.reset();
				form.getItem("id").setTooltip(I18N.message("generatedbyserver", form.getItem("id").getTitle()));
				departmentGridPanel.resetGrid();
				*/
			}
		});
	}
	
	// 20131128, junsoo, 유저 검색
	private void search(String idOrName, final int pageNum, final int pageSize)	{
		if (idOrName == null)
			idOrName = "";
		Log.debug("[ UserGridPanel search ] idOrName ["+idOrName+"], " + pageNum + "," + pageSize);
		searchText = idOrName;
		PagingConfig config = PagingToolStrip.getPagingConfig(pageNum, pageSize);
		ServiceUtil.security().pagingUsersByIdOrName(idOrName, config, new AsyncCallbackWithStatus<PagingResult<SUser>>() {
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
			public void onSuccessEvent(PagingResult<SUser> result) {
//					reset();

				if(result == null){
					grid.setEmptyMessage(I18N.message("notitemstoshow"));
					return;	
				}
				int totalLength = result.getTotalLength();
				
				SUser user;			
				SGroup duty;
				SGroup position;
				
				List<SUser> data = result.getData();

				grid.selectAllRecords();
				grid.removeSelectedData();
				
				grid.setData(new ListGridRecord[0]); //그리드 초기화
				groupsMap = new HashMap<String, SGroup[]>(); //해당 유저의 그룹정보 초기화
				
				for (int j = 0; j < data.size(); j++) {
					
					user = data.get(j);			
					duty = user.getDuty();
					position = user.getPosition();
					
					ListGridRecord record=new ListGridRecord();
					record.setAttribute("id", user.getId());
					record.setAttribute("name", user.getName());
					record.setAttribute("username", user.getUserName());
					
					if (user.getUserGroup() != null && user.getGroupLength() != 0) {
						record.setAttribute("groupid", user.getUserGroup().getId());
						record.setAttribute("departmentid", user.getDepartmentId());
						SGroup[] groups =  user.getGroups();
						if(groups.length > 1){
						//20140221na 그룹 여러개 보이기
							String departments = groups[0].getName() ;
							for (int i = 1; i < groups.length; i++) {
								departments = departments + ", " + groups[i].getName() ; 
							}
							record.setAttribute("department", departments);
						}
						else record.setAttribute("department", user.getDepartment());
					}
					record.setAttribute("groupname", user.getUserGroupName());
					record.setAttribute("email", user.getEmail());
					record.setAttribute("description", user.getDescription());
					record.setAttribute("creationdate", user.getCreationDate());
					record.setAttribute("dutyid", (duty != null) ? duty.getId() : null);
					record.setAttribute("dutyname", (duty != null) ? duty.getName() : null);
					record.setAttribute("positionid", (position != null) ? position.getId() : null);
					record.setAttribute("positionname", (position != null) ? position.getName() : null);			
					// soeun 사용량, 사용 제한 추가
					if(user.getTotalSpace() != null) {
						record.setAttribute("totalSpace", user.getTotalSpace());
						record.setAttribute("usedSpace", user.getUsedSpace());						
					} else {
						record.setAttribute("totalSpace", 0);
						record.setAttribute("usedSpace", 0);
					}
					record.setAttribute("homeFolderId", user.getHomeFolderId());						
					record.setAttribute("homeUsable", user.isHomeUsable());		
							
					grid.addData(record);					
					groupsMap.put(user.getId(), user.getGroups());
				}	
				GWT.log("totalLength["+totalLength+"], pageNum["+ pageNum + "]", null);				
				gridPager.setRespPageInfo(totalLength, pageNum);
			}
		});
	}

	@Override
	public void onPageDataReqeust(int pageNum, int pageSize) {
		if(isShowAct) {
			form.editNewRecord();
	    	form.reset();
	    	form.getItem("id").setTooltip(I18N.message("generatedbyserver", form.getItem("id").getTitle()));
		}
		
		if (searchText != null && searchText.length() > 0)
			search(searchText, pageNum, pageSize);
		else executeFetch(pageNum, pageSize);
	}
}