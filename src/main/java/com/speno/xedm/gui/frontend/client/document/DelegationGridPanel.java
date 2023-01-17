package com.speno.xedm.gui.frontend.client.document;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.datepicker.client.CalendarUtil;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.types.SortDirection;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.CheckboxItem;
import com.smartgwt.client.widgets.form.fields.DateItem;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.HiddenItem;
import com.smartgwt.client.widgets.form.fields.PickerIcon;
import com.smartgwt.client.widgets.form.fields.SpacerItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.FormItemClickHandler;
import com.smartgwt.client.widgets.form.fields.events.FormItemIconClickEvent;
import com.smartgwt.client.widgets.form.fields.events.KeyDownEvent;
import com.smartgwt.client.widgets.form.fields.events.KeyDownHandler;
import com.smartgwt.client.widgets.form.fields.events.KeyUpEvent;
import com.smartgwt.client.widgets.form.fields.events.KeyUpHandler;
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
import com.speno.xedm.core.service.serials.SCode;
import com.speno.xedm.core.service.serials.SDelegation;
import com.speno.xedm.core.service.serials.SHistorySearchOptions;
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
import com.speno.xedm.gui.common.client.util.ReturnHandler;
import com.speno.xedm.gui.common.client.util.ServiceUtil;
import com.speno.xedm.gui.common.client.util.Util;
import com.speno.xedm.gui.frontend.client.PagingToolStrip;
import com.speno.xedm.gui.frontend.client.folder.OwnerWindow;
import com.speno.xedm.gwt.service.DocumentCodeService;
import com.speno.xedm.gwt.service.DocumentCodeServiceAsync;
import com.speno.xedm.util.paging.PagingConfig;
import com.speno.xedm.util.paging.PagingResult;

/**
 * DelegationGrid Panel
 * @author 남윤성
 * @since 1.0
 */
public class DelegationGridPanel extends VLayout implements PagingObserver {	
	private static HashMap<String, DelegationGridPanel> instanceMap = new HashMap<String, DelegationGridPanel>();
	private LinkedHashMap<String, String> privilege = new LinkedHashMap<String, String>() ;
	private ListGrid grid;
	private DynamicForm form1;
	private DynamicForm form2;
	private HLayout actionHL;
	private DateItem expireDateItem;
	
	private PagingToolStrip gridPager;
	private RecordObserver recordObserver;	
	private DynamicForm searchForm;
	private String sessionId;
	private String sessionName;
	private String isMenu = "";
	private boolean isAction;
	private List<FormItem> formList;
	private List<ListGridField> listGridFieldList;
	private String[] srchMandatorInfo = new String[2];		    // Org. Popup 창에서 넘겨받는 owner 정보 저장용 변수
	private String[] srchDelegatorInfo = new String[2];		// Org. Popup 창에서 넘겨받는 owner 정보 저장용 변수
	private String[] mandatorInfo = new String[2];				// Org. Popup 창에서 넘겨받는 owner 정보 저장용 변수
	private String[] delegatorInfo = new String[2];			// Org. Popup 창에서 넘겨받는 owner 정보 저장용 변수
	private boolean isAdmin = false;
	
	private String delegatorrUserId;
	private String mandatorUserId;
	
	/**
	 * 기 생성된 instance를 반환함. instance가 없을 경우 생성 후 반환함
	 * @param id
	 * @param subTitle
	 * @param ob
	 * @param isAction
	 * @return
	 */
	public static DelegationGridPanel get(final String id, final String subTitle, final RecordObserver ob, final boolean isAction) {
		if (instanceMap.get(id) == null) {
			new DelegationGridPanel(id, subTitle, ob, isAction);
		}
		return instanceMap.get(id);
	}
	
	public DelegationGridPanel(final String id, final String subTitle, final RecordObserver ob, final boolean isAction) {
		instanceMap.put(id, this);	
		this.isMenu = id;
		this.recordObserver = ob;
		this.isAction = isAction;
		
		executeGetDelegationPrivilege();
		
	}
	
	public void CreateDelegationGridPanel(){
		//관리자 메뉴 판단
		if(!"dashboard.dashboard.delegation".equals(isMenu)){
			isAdmin = true;
		}
		
//		SGroup[] group = Session.get().getUser().getGroups();
//		for(int i=0; i< group.length; i++){
//			if(group[i].getId() == 1){
//				isAdmin = true;
//				break;
//			}
//		}
		
		sessionId = Session.get().getUser().getId();
		sessionName = Session.get().getUser().getName();
						
		if(isAdmin)addMember(createSearchForm());
		
		grid = new ListGrid();
		grid.setWidth100();
		grid.setHeight100();		
		grid.setShowAllRecords(true);
		
		grid.setEmptyMessage(I18N.message("notitemstoshow"));		
		grid.setCanFreezeFields(true);
		grid.setCanRemoveRecords(isAction);
		grid.setSelectionType(SelectionStyle.SINGLE);
		grid.invalidateCache();
		
		ListGridField idField = new ListGridField("id", I18N.message("typeid"));
		idField.setHidden(true);
		
		ListGridField MandatorIdField = new ListGridField("mandatorId", I18N.message("second.mandator"));	// 위임자
		MandatorIdField.setHidden(true);
		ListGridField MandatorField = new ListGridField("mandator", I18N.message("second.mandator"));	// 위임자
		ListGridField DelagatorIdField = new ListGridField("delegatorId", I18N.message("second.delegator"));	// 수임자
		DelagatorIdField.setHidden(true);
		ListGridField DelagatorField = new ListGridField("delegator", I18N.message("second.delegator"));	// 수임자
		
		ListGridField dateField = new ListGridField("modified", I18N.message("date"),200);//date
		dateField.setType(ListGridFieldType.DATE);
		dateField.setAlign(Alignment.LEFT);
		dateField.setCellFormatter(new DateCellFormatter(I18N.message("yyyy/MM/dd")));
		ListGridField exfireField = new ListGridField("expire", I18N.message("expire"),200);//expire
		exfireField.setType(ListGridFieldType.DATE);
		exfireField.setAlign(Alignment.LEFT);
		exfireField.setCellFormatter(new DateCellFormatter(I18N.message("yyyy/MM/dd")));
		
		ListGridField myDocField = new ListGridField("mydoc",I18N.message("mydoc"));
		myDocField.setType(ListGridFieldType.BOOLEAN);
		myDocField.setCanEdit(false);
		myDocField.setShowTitle(true);
		
		ListGridField sharedDocField = new ListGridField("shareddoc",I18N.message("shareddoc"));
		sharedDocField.setType(ListGridFieldType.BOOLEAN);
		sharedDocField.setCanEdit(false);
		sharedDocField.setShowTitle(true);
		
		ListGridField approvalField = new ListGridField("approval",I18N.message("approval"));
		approvalField.setType(ListGridFieldType.BOOLEAN);
		approvalField.setCanEdit(false);
		approvalField.setShowTitle(true);
		
		
		listGridFieldList = new ArrayList<ListGridField>();
		listGridFieldList.add(idField);
		listGridFieldList.add(MandatorIdField);
		listGridFieldList.add(MandatorField);
		listGridFieldList.add(DelagatorIdField);
		listGridFieldList.add(DelagatorField);
		listGridFieldList.add(dateField);
		listGridFieldList.add(exfireField);
		
		createPrivilegtGridForm();//그리드와 폼 체크박스 privilege 만큼 추가
	
		VLayout gridPanel = new VLayout();
        gridPanel.setHeight100();
        gridPanel.setMembersMargin(0);
        gridPanel.setMembers(grid);
		
		VLayout pagerPanel = new VLayout();
		pagerPanel.setHeight100();
		pagerPanel.setMembersMargin(1);
		pagerPanel.addMember(gridPanel);
        
        gridPager = new PagingToolStrip(grid, Session.get().getUser().getPageSize(), true, this);
        gridPager.setDeselect(false);
        gridPager.setIsAutoHeightGrid(false);
        
        grid.setHeight100();
        grid.setWidth100();
        grid.setBodyOverflow(Overflow.SCROLL);
        grid.sort("modified", SortDirection.DESCENDING);
        
        pagerPanel.addMember(gridPager);
        
        //record dbclick event handler 정의------------------------------------------------------------
		grid.addRecordDoubleClickHandler(new RecordDoubleClickHandler() {
			@Override
			public void onRecordDoubleClick(RecordDoubleClickEvent event) {
				if(recordObserver != null) {
					recordObserver.onRecordDoubleClick(event.getRecord());
				}
			}   
        });
    	
		VLayout docTypeVL = new VLayout(5);
		setMembersMargin(Constants.SUBTITLE_MARGIN);
		docTypeVL.addMember(pagerPanel);
		
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
				public void onRemoveRecordClick(RemoveRecordClickEvent event) {
					final ListGridRecord record = grid.getRecord( event.getRowNum());
					form1.reset();
					form1.editRecord(record);
					form2.reset();
					form2.editRecord(record);
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
			
			docTypeVL.addMember(createFormVL());
			docTypeVL.addMember(createActHL());
		}
        
        addMember(docTypeVL);
        
        executeFetch();
	}
	
	/**
	 * 상단 검색 Form 생성
	 * @return
	 */
	private DynamicForm createSearchForm() {		
		SpacerItem dummyItem = new SpacerItem();
		dummyItem.setWidth(100);
		
		final TextItem srchMandatorIdText = new TextItem("srchMandatorId", I18N.message("second.mandator"));
		srchMandatorIdText.setVisible(false);
		
     		// - 위임자
        final TextItem srchMandatorText = new TextItem("srchMandator", I18N.message("second.mandator"));
        srchMandatorText.setWrapTitle(false);
//        srchMandatorText.setCanEdit(false);
//        srchMandatorText.setCanFocus(false);
        srchMandatorText.setDisableIconsOnReadOnly(false);

         // 위임자 삭제 버튼
        PickerIcon mandatorClearPicker = new PickerIcon(PickerIcon.CLEAR, new FormItemClickHandler() {   
             public void onFormItemClick(FormItemIconClickEvent event) { 
            	 searchForm.getField("srchMandatorId").clearValue();
            	 searchForm.getField("srchMandator").clearValue();
            	 srchMandatorInfo[0] = null;
             }   
         });
             
         // 위임자 검색
         PickerIcon mandatorSearchPicker = new PickerIcon(PickerIcon.SEARCH, new FormItemClickHandler() {   
             public void onFormItemClick(FormItemIconClickEvent event) {  
             	final ReturnHandler returnOwnerHandler = new ReturnHandler() {
         			@Override
         			public void onReturn(Object param) {
         				String[][] ownerInfo = (String[][])param;
         				searchForm.getField("srchMandatorId").setValue(ownerInfo[0][0]);
         				searchForm.getField("srchMandator").setValue(ownerInfo[0][1]);
         				searchForm.getField("srchMandator").setCellStyle("ownnertext");
         				searchForm.getField("srchMandator").updateState();
         			}
         		};
             	OwnerWindow ownerWindow = new OwnerWindow("single", returnOwnerHandler, false, srchMandatorText.getValueAsString());
 				ownerWindow.show();
             }   
         });
         srchMandatorText.setIcons(mandatorClearPicker, mandatorSearchPicker);
         
       //20140218na 키인
//         srchMandatorText.addBlurHandler(new BlurHandler() {
// 			@Override
// 			public void onBlur(BlurEvent event) {
// 				mandatorTop = srchMandatorText.getValueAsString();
// 				if(srchMandatorIdText.getValue() == null) srchMandatorText.clearValue();
// 			}
// 		});
// 		
         srchMandatorText.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				srchMandatorIdText.clearValue();
				srchMandatorText.setCellStyle("");
				srchMandatorText.updateState();
			}
		});
         
         final TextItem srchDelegatorIdText = new TextItem("srchDelegatorId", I18N.message("second.delegator"));
         srchDelegatorIdText.setVisible(false);
         
         //수임자
         final TextItem srchDelegatorText = new TextItem("srchDelegator", I18N.message("second.delegator"));
         srchDelegatorText.setWrapTitle(false);
//         srchDelegatorText.setCanEdit(false);
//         srchDelegatorText.setCanFocus(false);
         srchDelegatorText.setDisableIconsOnReadOnly(false);

          // 수임자 삭제 버튼
         PickerIcon delegatorClearPicker = new PickerIcon(PickerIcon.CLEAR, new FormItemClickHandler() {   
              public void onFormItemClick(FormItemIconClickEvent event) { 
            	  searchForm.getField("srchDelegatorId").clearValue();
             	 searchForm.getField("srchDelegator").clearValue();
             	srchDelegatorInfo[0] = null;
              }   
          });
              
          // 위임자 검색
          PickerIcon delegatorSearchPicker = new PickerIcon(PickerIcon.SEARCH, new FormItemClickHandler() {   
              public void onFormItemClick(FormItemIconClickEvent event) {  
              	final ReturnHandler returnOwnerHandler = new ReturnHandler() {
          			@Override
          			public void onReturn(Object param) {
          				String[] ownerInfo = (String[])param;
          				srchDelegatorInfo = ownerInfo;
          				searchForm.getField("srchDelegatorId").setValue(ownerInfo[0]);
          				searchForm.getField("srchDelegator").setValue(ownerInfo[1]);
          				searchForm.getField("srchDelegator").setCellStyle("ownnertext");
         				searchForm.getField("srchDelegator").updateState();
          			}
          		};
              	OwnerWindow ownerWindow = new OwnerWindow("single", returnOwnerHandler, false, srchDelegatorText.getValueAsString());
  				ownerWindow.show();
              }   
          });
          srchDelegatorText.setIcons(delegatorClearPicker, delegatorSearchPicker);
          
//          srchDelegatorText.addBlurHandler(new BlurHandler() {
//   			@Override
//   			public void onBlur(BlurEvent event) {
//   				delagatorTop = srchDelegatorText.getValueAsString();
//   				if(srchDelegatorIdText.getValue() == null) srchDelegatorText.clearValue();
//   			}
//   		});
   		
          srchDelegatorText.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				srchDelegatorIdText.clearValue();
				srchDelegatorText.setCellStyle("");
				srchDelegatorText.updateState();
			}
		});
          
          ButtonItem searchButton = new ButtonItem();
		searchButton.setTitle(I18N.message("search"));	
		searchButton.setStartRow(false);
		searchButton.setIcon("[SKIN]/actions/search.png");		
		searchButton.addClickHandler(new com.smartgwt.client.widgets.form.fields.events.ClickHandler() {
			@Override
			public void onClick(com.smartgwt.client.widgets.form.fields.events.ClickEvent event) {
				executeFetch();
			}
        });
		
		
		searchForm = new DynamicForm();
		searchForm.setWidth100();
		searchForm.setAlign(Alignment.RIGHT);			
		searchForm.setMargin(4);
		searchForm.setNumCols(11);
		searchForm.setColWidths("*","1","1","1","1","1","1","1","1","1","1");
		
		searchForm.setItems(dummyItem, srchMandatorIdText, srchMandatorText, srchDelegatorIdText, srchDelegatorText, searchButton);
		return searchForm;
	}
	
	/**
	 * 하단 상세 Form 생성
	 * @return
	 */
	private HLayout createFormVL() {
		HiddenItem IdItem = new HiddenItem("id");
		/*
		TextItem typeIdItem = new TextItem("id", I18N.message("typeid"));		
		typeIdItem.setWidth("*");
		typeIdItem.disable();
		typeIdItem.setCanEdit(false);
		typeIdItem.setWrapTitle(false);
		typeIdItem.setTooltip(I18N.message("generatedbyserver", typeIdItem.getTitle()));
		*/
				
		final TextItem mandatorIdItem = new TextItem("mandatorId", I18N.message("second.mandator"));
		mandatorIdItem.setVisible(false);
		
		final TextItem mandatorItem = new TextItem("mandator", I18N.message("second.mandator"));
		mandatorItem.setRequired(true);	
		// kimsoeun GS인증용 - 툴팁 다국어화
		mandatorItem.setRequiredMessage(I18N.message("fieldisrequired"));
//		mandatorItem.setCanEdit(false);
		mandatorItem.setWrapTitle(false);
		if(isAdmin){
			mandatorItem.setDisableIconsOnReadOnly(false);
		}else{
			mandatorIdItem.setValue(sessionId);
			mandatorItem.setValue(sessionName);
			mandatorItem.setDisableIconsOnReadOnly(true);
			mandatorItem.setDisabled(true);
		}
		
		//20140218na 키인
//		mandatorItem.addBlurHandler(new BlurHandler() {
//			@Override
//			public void onBlur(BlurEvent event) {
//				mandator = mandatorItem.getValueAsString();
//				if(mandatorIdItem.getValue() == null) mandatorItem.clearValue();
//			}
//		});
		
		mandatorItem.addKeyDownHandler(new KeyDownHandler() {
			@Override
			public void onKeyDown(KeyDownEvent event) {
				mandatorIdItem.clearValue();
				mandatorItem.setCellStyle("");
				mandatorItem.updateState();
			}
		});
		
        // 위임자 삭제 버튼
        PickerIcon mandatorClearPicker = new PickerIcon(PickerIcon.CLEAR, new FormItemClickHandler() {   
             public void onFormItemClick(FormItemIconClickEvent event) {   
            	 form1.getField("mandatorId").clearValue();
            	 form1.getField("mandator").clearValue();
            	 mandatorInfo[0] = null;
             }   
         });
             
         // 위임자
         PickerIcon mandatorSearchPicker = new PickerIcon(PickerIcon.SEARCH, new FormItemClickHandler() {   
             public void onFormItemClick(FormItemIconClickEvent event) {  
             	final ReturnHandler returnOwnerHandler = new ReturnHandler() {
         			@Override
         			public void onReturn(Object param) {
         				String[][] ownerInfo = (String[][])param;
         				mandatorUserId = ownerInfo[0][0];
         				form1.getField("mandatorId").setValue(ownerInfo[0][0]);
         				form1.getField("mandator").setValue(ownerInfo[0][1]);
         				form1.getField("mandator").setCellStyle("ownnertext");
         				form1.getField("mandator").updateState();
         			}
         		};
         		String mandator = "";
         		try {
         			mandator = form1.getField("mandator").getValue().toString();
				} catch (Exception e) {}
             	OwnerWindow ownerWindow = new OwnerWindow("single", returnOwnerHandler, false, mandator);
 				ownerWindow.show();
             }   
         });
         
         if(isAdmin){
        	 mandatorItem.setIcons(mandatorClearPicker, mandatorSearchPicker);
         }
		
		CheckboxItem mydocCb = new CheckboxItem("mydoc",I18N.message("mydoc"));
		mydocCb.setType("combobox");
		mydocCb.setWrapTitle(false);
		mydocCb.setRequired(true);
		mydocCb.setDefaultValue(0);
		
		final TextItem delagatorIdItem = new TextItem("delegatorId", I18N.message("second.delegator"));
		delagatorIdItem.setVisible(false);
		
		final TextItem delagatorItem = new TextItem("delegator", I18N.message("second.delegator"));
		delagatorItem.setRequired(true);		
		// kimsoeun GS인증용 - 툴팁 다국어화
		delagatorItem.setRequiredMessage(I18N.message("fieldisrequired"));		
//		delagatorItem.setCanEdit(false);
		delagatorItem.setWrapTitle(false);
		delagatorItem.setDisableIconsOnReadOnly(false);
		
		//20140218na 키인
//		delagatorItem.addBlurHandler(new BlurHandler() {
//			@Override
//			public void onBlur(BlurEvent event) {
//				delagator = delagatorItem.getValueAsString();
//				if(delagatorIdItem.getValue() == null) delagatorItem.clearValue();
//			}
//		});
//		
		delagatorItem.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				delagatorIdItem.clearValue();
				delagatorItem.setCellStyle("");
				delagatorItem.updateState();
			}
		});

         // 수임자 삭제 버튼
        PickerIcon delegatorClearPicker = new PickerIcon(PickerIcon.CLEAR, new FormItemClickHandler() {   
             public void onFormItemClick(FormItemIconClickEvent event) {  
            	 form1.getField("delegatorId").clearValue();
            	 form1.getField("delegator").clearValue();
            	 delegatorInfo[0] = null;
             }   
         });
             
         // 위임자 검색
         PickerIcon delegatorSearchPicker = new PickerIcon(PickerIcon.SEARCH, new FormItemClickHandler() {   
             public void onFormItemClick(FormItemIconClickEvent event) {  
             	final ReturnHandler returnOwnerHandler = new ReturnHandler() {
         			@Override
         			public void onReturn(Object param) {
         				if (param instanceof String[][]) {
	         				String[][] ownerInfo = (String[][])param;
	         				if (ownerInfo != null && ownerInfo.length > 0) {
	         					delegatorrUserId =ownerInfo[0][0];
		         				delegatorInfo[0] = ownerInfo[0][4];
		         				delegatorInfo[1] = ownerInfo[0][1];
		         				form1.getField("delegatorId").setValue(delegatorInfo[0]);
		         				form1.getField("delegator").setValue(delegatorInfo[1]);
		         				form1.getField("delegator").setCellStyle("ownnertext");
		         				form1.getField("delegator").updateState();
	         				}
         				}
         			}
         		};
         		// TODO : 그룹은 선택이 안되도록 함. 추후에는 그룹에게 위임가능하도록 하자.
         		String delegator = "";
         		try {
         			delegator = form1.getField("delegator").getValue().toString();
				} catch (Exception e) {}
             	OwnerWindow ownerWindow = new OwnerWindow("single", returnOwnerHandler, false, delegator);
 				ownerWindow.show();
             }   
         });
         delagatorItem.setIcons(delegatorClearPicker, delegatorSearchPicker);
         
		
		CheckboxItem sharedDocCb = new CheckboxItem("shareddoc",I18N.message("shareddoc"));
		sharedDocCb.setType("combobox");
		sharedDocCb.setWrapTitle(false);
		sharedDocCb.setRequired(true);
		sharedDocCb.setDefaultValue(0);
		
		expireDateItem = new DateItem("expire", I18N.message("expire"));
		expireDateItem.setRequired(true);
		expireDateItem.setWrapTitle(false);
		expireDateItem.setRequired(false);
		
		CheckboxItem approvalCb = new CheckboxItem("approval",I18N.message("approval"));
		approvalCb.setType("combobox");
		approvalCb.setWrapTitle(false);
		approvalCb.setRequired(true);
		approvalCb.setDefaultValue(0);
		
		TextItem commentItem = new TextItem("comment", I18N.message("comment"));
		commentItem.setCanEdit(true);
		commentItem.setWrapTitle(false);
//		commentItem.setLength(Session.get().getInfo().getIntConfig("gui.comment.fieldsize", 1000));
		commentItem.setValidators(new LengthValidator(commentItem, Session.get().getInfo().getIntConfig("gui.comment.fieldsize", 1000)));
		mandatorItem.setStartRow(false);			mandatorItem.setEndRow(false);		
		delagatorItem.setStartRow(true);			delagatorItem.setEndRow(false);		
		expireDateItem.setStartRow(true);			expireDateItem.setEndRow(false);		
		commentItem.setStartRow(true);		commentItem.setEndRow(false);	
		
		setExpireDateItem();
		
		form1 = new DynamicForm();
		form1.setAutoWidth();
		form1.setMargin(4);
		form1.setNumCols(4);
		form1.setColWidths("1","1","1","1");
		form1.setItems(IdItem,mandatorIdItem,  mandatorItem, delagatorIdItem, delagatorItem, expireDateItem, commentItem);
		form1.reset();
    	
		
		
    	HLayout formVL = new HLayout(50);
    	formVL.setBorder("1px solid gray");
    	formVL.setWidth100();
    	formVL.setAutoHeight();    	
    	formVL.addMembers(form1, form2);
    	
    	return formVL;
	}
	
	/**
	 * DELEGATION_PRIVILEGE 코드 수만큼 그리드(필드), 폼(체크박스) 추가
	 */
	private void createPrivilegtGridForm(){
		Collection bindVar = privilege.keySet();
		Iterator iter = bindVar.iterator();
		String varStr = "";
		
		form2 = new DynamicForm();
		formList = new ArrayList<FormItem>();
		while(iter.hasNext()) {
			varStr = (String)iter.next();
			privilege.get(varStr);
			ListGridField privilegeField = new ListGridField(privilege.get(varStr),I18N.message(privilege.get(varStr)));
			privilegeField.setType(ListGridFieldType.BOOLEAN);
			privilegeField.setCanEdit(false);
			privilegeField.setShowTitle(true);
			listGridFieldList.add(privilegeField);
			
			CheckboxItem privilegeCb = new CheckboxItem(privilege.get(varStr),I18N.message(privilege.get(varStr)));
			formList.add(privilegeCb);
		}
		grid.setFields(listGridFieldList.toArray(new ListGridField[0]));
		
		form2.setAutoWidth();
		form2.setMargin(4);
		form2.setItems(formList.toArray(new FormItem[0]));
		form2.reset();
		
	}
	
	/**
	 * DELEGATION_PRIVILEGE 코드 가져오기
	 */
	private void executeGetDelegationPrivilege() {		
		DocumentCodeServiceAsync documentCodeService = (DocumentCodeServiceAsync) GWT.create(DocumentCodeService.class);
		documentCodeService.listCodes(Session.get().getSid(), "DELEGATION_PRIVILEGE", new AsyncCallbackWithStatus<List<SCode>>() {
			@Override
			public String getSuccessMessage() {
				return I18N.message("client.searchComplete");
			}
			@Override
			public String getProcessMessage() {
				return I18N.message("client.searchRequest");
			}
			@Override
			public void onSuccessEvent(List<SCode> result) {
				if( result.size() > 0) {
					for(int j=0; j<result.size(); j++) {
						try {
							boolean isFoldeShared = Util.getSetting("setting.foldershared");
							if(!isFoldeShared && result.get(j).getName().equals("mydoc")) continue;
						} catch (Exception e) {}
						privilege.put(result.get(j).getValue(), result.get(j).getName());
					}
				}
				
				CreateDelegationGridPanel();
			}			
			@Override
			public void onFailureEvent(Throwable caught) {
				Log.serverError(caught, true);
			}
		});
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
            	form1.getItem("id").setTooltip(I18N.message("generatedbyserver", form1.getItem("id").getTitle()));
            	form1.editNewRecord();
            	form1.reset();
            	form2.editNewRecord();
            	form2.reset();
            	grid.deselectAllRecords();
            	
				form1.getField("mandator").setCellStyle("");
				form1.getField("mandator").updateState();
            	
				form1.getField("delegator").setCellStyle("");
				form1.getField("delegator").updateState();
				
            	if(!isAdmin){
            		form1.getField("mandatorId").setValue(sessionId);
            		form1.getField("mandator").setValue(sessionName);
            	}
            	Date date = (Date)form1.getField("expire").getValue();
        		CalendarUtil.addDaysToDate(date, Session.get().getInfo().getIntConfig("gui.delegation.expireDay", 3));
        		form1.getField("expire").setValue(date);
            	
            }   
        });
		
		Button btnSave = new Button(I18N.message("save"));
		btnSave.setIcon(ItemFactory.newImgIcon("data_into.png").getSrc());
		btnSave.addClickHandler(new ClickHandler() {   
            public void onClick(ClickEvent event) {
            	if(form1.getValue("id") == null) {
            		 if(form1.validate()) {
            			 if(checkBox() && expireValidation()){
        					 executeAdd();
            			 }
            		 }
            	}
            	else {
            		 if(form1.validate()) {
            			 if(checkBox() && expireValidation()){
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
	
	private boolean expireValidation(){
		Date selectedDate = (Date)expireDateItem.getValue();
		selectedDate.setHours(23);
		selectedDate.setMinutes(59);
		selectedDate.setSeconds(59);
		
		Date nowDate = new Date();
		if(selectedDate.compareTo(nowDate) <= 0){
			SC.warn(I18N.message("youcantchoiceafterday"));
			return false;
		}
		else
			return true;
	}
	
	/**
	 * CHECKBOX VALIDATION Message
	 */
	public String getMessageCheckBox(){
		String I18NMessage = "";
		Collection bindVar = privilege.keySet();
		Iterator iter = bindVar.iterator();
		String varStr = "";
		while(iter.hasNext()) {
			varStr = (String)iter.next();
			I18NMessage += ", " + I18N.message(privilege.get(varStr));
		}
		I18NMessage = "".equals(I18NMessage)?"":I18NMessage.substring(1);
		return I18NMessage;
	}
	
	/**
	 * CHECKBOX VALIDATION
	 */
	public Boolean checkBox(){
		Collection bindVar = privilege.keySet();
		Iterator iter = bindVar.iterator();
		String varStr = "";
		boolean rtn = false;
		while(iter.hasNext()) {
			varStr = (String)iter.next();
			privilege.get(varStr);
			if(("1".equals(form2.getValueAsString(privilege.get(varStr))) || "true".equals(form2.getValueAsString(privilege.get(varStr))))){
				rtn = true;
			}
		}
		
		if(rtn == false)
			SC.say(I18N.message("second.checkatleastoneof", getMessageCheckBox()));
		return rtn;
	}
		
	/**
	 * Record Click Event Handler
	 * @param record
	 */
	private void recordClickedProcess(Record record) {
		if(form1 != null) {
			form1.getItem("id").setTooltip(I18N.message("fieldisreadonly", form1.getItem("id").getTitle()));
			form1.reset();
			form1.editRecord(record);
		}
		if(form2 != null) {
			form2.reset();
			form2.editRecord(record);
		}
	}

	/**
	 * 1(Default)페이지 조회
	 */
	public void executeFetch() {
		executeFetch(1, gridPager.getPageSize());
	}
	
	private void executeFetch(final int pageNum, final int pageSize)	{				
		Log.debug("[ DelegationGridPanel executeFetch ] pageNum["+pageNum+"], pageSize["+pageSize+"]");
		
		SHistorySearchOptions searchOptions = new SHistorySearchOptions();
		
		PagingConfig config = PagingToolStrip.getPagingConfig(pageNum, pageSize);
		searchOptions.setConfig(config);
		
		String srchMandateId = "";
		String srchDelegatorId = "";
		
		if(isAdmin){
			
			//20140317na 위임자 검색 방식 변경
			if(null != searchForm.getField("srchMandatorId").getValue()){
				srchMandateId = (String)searchForm.getField("srchMandatorId").getValue();
			}
			else if(null !=searchForm.getField("srchMandator").getValue()){
				String srchMandator = (String)searchForm.getField("srchMandator").getValue();
				ServiceUtil.security().getUser(Session.get().getSid(), srchMandator, new AsyncCallback<SUser>() {
					@Override
					public void onSuccess(SUser result) {
						mandatorUserId = result.getId();
						searchForm.getField("srchMandatorId").setValue(result.getId());
						searchForm.getField("srchMandator").setCellStyle("ownnertext");
						searchForm.getField("srchMandator").updateState();
						executeFetch(pageNum, pageSize);
					}
					@Override
					public void onFailure(Throwable caught) {
						SC.warn(caught.getMessage());
						searchForm.getField("srchMandator").setCellStyle("ownnertextout");
						searchForm.getField("srchMandator").updateState();
					}
				});
				return;
			}

			if(null != searchForm.getField("srchDelegatorId").getValue()){
				srchDelegatorId = (String)searchForm.getField("srchDelegatorId").getValue();
			}
			else if(null !=searchForm.getField("srchDelegator").getValue()){
				String srchDelegator = (String)searchForm.getField("srchDelegator").getValue();
				ServiceUtil.security().getUser(Session.get().getSid(), srchDelegator, new AsyncCallback<SUser>() {
					@Override
					public void onSuccess(SUser result) {
						delegatorrUserId = result.getId();
						searchForm.getField("srchDelegatorId").setValue(result.getId());
						searchForm.getField("srchDelegator").setCellStyle("ownnertext");
						searchForm.getField("srchDelegator").updateState();
						executeFetch(pageNum, pageSize);
					}
					@Override
					public void onFailure(Throwable caught) {
						SC.warn(caught.getMessage());
						searchForm.getField("srchDelegator").setCellStyle("ownnertextout");
						searchForm.getField("srchDelegator").updateState();
					}
				});
				return;
			}
			
		}else{
			srchMandateId = sessionId;
			form1.getField("mandatorId").setValue(sessionId);
			form1.getField("mandator").setValue(sessionName);
		}
		
		Log.debug("srchMandateId"+srchMandateId);
		Log.debug("srchDelegatorId"+srchDelegatorId);
						
		ServiceUtil.security().pagingDelegation(Session.get().getSid(), config, srchMandateId, srchDelegatorId, new AsyncCallbackWithStatus<PagingResult<SDelegation>>() {
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
				Log.serverError(caught, true);
			}
			@Override
			public void onSuccessEvent(PagingResult<SDelegation> result) {
				grid.setData(new ListGridRecord[0]); //그리드 초기화
				
				List<SDelegation> data = result.getData();
				SDelegation delegation;	
				
				for (int j = 0; j < data.size(); j++) {
					delegation = data.get(j);
					
					ListGridRecord record=new ListGridRecord();
					record.setAttribute("id", delegation.getId());
					record.setAttribute("mandatorId", delegation.getMandatorId());
					record.setAttribute("mandator", delegation.getMandatorName());
					record.setAttribute("delegatorId", delegation.getDelegatorId());
					record.setAttribute("delegator", delegation.getDelegatorName());
					record.setAttribute("modified", delegation.getLastModified());
					record.setAttribute("expire", delegation.getExpireDate());
					record.setAttribute("comment", delegation.getComment());
										
					if(!"".equals(delegation.getAllowed()) && null !=delegation.getAllowed()){
							String [] allowedArr = delegation.getAllowed();
							String tmpPrivilege = "";
							for(int i=0; i< allowedArr.length; i++){
								tmpPrivilege = privilege.get(allowedArr[i]);
								if(!"".equals(tmpPrivilege)){//privilege에 같은code 값이 있으면
									record.setAttribute(tmpPrivilege, 1);
								}
							}
					}
					
					grid.addData(record);
					
					if(j==0){
						recordClickedProcess(record);
					}
				}	
				
				if (data.size() > 0) {
					grid.selectSingleRecord(0);
				}
				
				Log.debug("isExistData["+(data.size() > 0)+"], pageNum["+ pageNum + "]");
				gridPager.setRespPageInfo(result.getTotalLength(), pageNum); 
			}
		});
	}
	
	/**
	 * 신규 저장
	 */
	private void executeAdd() {
		Log.debug("[ DelegationGridPanel executeAdd ]");	
		
		//20140123na 에러방지를 위한 try catch
    	try {
    		String delegator = form1.getValueAsString("delegator");
        	String delegatorId = form1.getValueAsString("delegatorId");
        	String mandator = form1.getValueAsString("mandator");
        	String mandatorId = form1.getValueAsString("mandatorId");
        	
        	if(delegatorId == null || delegatorrUserId == null){
    			ServiceUtil.security().getUser(Session.get().getSid(), delegator, new AsyncCallback<SUser>() {
    				@Override
    				public void onSuccess(SUser result) {
    					delegatorrUserId = result.getId();
    					form1.getField("delegatorId").setValue(result.getUserGroup().getId());
    					form1.getField("delegator").setCellStyle("ownnertext");
    					form1.getField("delegator").updateState();
    					executeAdd();
    				}
    				@Override
    				public void onFailure(Throwable caught) {
    					SC.warn(caught.getMessage());
    					form1.getField("delegator").setCellStyle("ownnertextout");
    					form1.getField("delegator").updateState();
    				}
    			});
    			return;
    		}
        	
        	if(mandatorId == null || mandatorUserId == null){
    			ServiceUtil.security().getUser(Session.get().getSid(), mandator, new AsyncCallback<SUser>() {
    				@Override
    				public void onSuccess(SUser result) {
    					mandatorUserId = result.getId();
    					form1.getField("mandatorId").setValue(result.getId());
    					form1.getField("mandator").setCellStyle("ownnertext");
    					form1.getField("mandator").updateState();
    					executeAdd();
    				}
    				@Override
    				public void onFailure(Throwable caught) {
    					SC.warn(caught.getMessage());
    					form1.getField("mandator").setCellStyle("ownnertextout");
    					form1.getField("mandator").updateState();
    				}
    			});
    			return;
    		}
        	
        	
        	Log.debug(delegator + " " + mandator);
        	
    		if(delegatorrUserId.equals(mandatorUserId)){
    			SC.warn(I18N.message("cantsamedelegator"));
    			return;
    		}
		} catch (Exception e) {} 
		
		SDelegation delegation = new SDelegation();
		delegation.setId(0L);
		delegation.setMandatorId(form1.getValueAsString("mandatorId"));
		delegation.setMandatorName(form1.getValueAsString("mandator"));
		delegation.setDelegatorId(form1.getValueAsString("delegatorId"));
		delegation.setDelegatorName(form1.getValueAsString("delegator"));
		delegation.setComment(form1.getValueAsString("comment"));
		delegation.setExpireDate((Date)form1.getField("expire").getValue());
		
		delegation.setAllowed(getAllowed());
		
		ServiceUtil.security().saveDelegation(Session.get().getSid(), delegation, new AsyncCallbackWithStatus<SDelegation>() {
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
//				SCM.warn(caught);
				Log.serverError(caught, true);
			}
			@Override
			public void onSuccessEvent(SDelegation result) {
				Log.debug("[ DelegationGridPanel executeAdd ] onSuccess. id["+result.getId()+"]");
				
				ListGridRecord addRecord = new ListGridRecord();
				addRecord.setAttribute("id", result.getId());		
				addRecord.setAttribute("mandatorId", result.getMandatorId());	
				addRecord.setAttribute("mandator", result.getMandatorName());
				addRecord.setAttribute("delegatorId", result.getDelegatorId());
				addRecord.setAttribute("delegator", result.getDelegatorName());		
				addRecord.setAttribute("comment", result.getComment());	
				addRecord.setAttribute("expire", result.getExpireDate());	
				addRecord.setAttribute("modified", new Date());	
				
				gridPrivilegeCheck(addRecord, result.getAllowed());
								
				grid.addData(addRecord);
				
				int selectedRowNum = grid.getRecordIndex(addRecord);						
				grid.selectSingleRecord(selectedRowNum);
				grid.scrollToRow(selectedRowNum);						
				form1.reset();
				form1.editRecord(addRecord);
				form1.getItem("id").setTooltip(I18N.message("fieldisreadonly", form1.getItem("id").getTitle()));
				form2.reset();
				form2.editRecord(addRecord);
            	SC.say(I18N.message("operationcompleted"));
			}
		});
	}
	
	/**
	 * 저장
	 */
	private void executeUpdate() {
		Log.debug("[ DelegationGridPanel executeUpdate ]");
		
		//20140123na 에러방지를 위한 try catch
    	try {
    		String delegator = form1.getValueAsString("delegator");
        	String delegatorId = form1.getValueAsString("delegatorId");
        	String mandator = form1.getValueAsString("mandator");
        	String mandatorId = form1.getValueAsString("mandatorId");
        	
        	if(delegatorId == null || delegatorrUserId == null){
    			ServiceUtil.security().getUser(Session.get().getSid(), delegator, new AsyncCallback<SUser>() {
    				@Override
    				public void onSuccess(SUser result) {
    					delegatorrUserId = result.getId();
    					form1.getField("delegatorId").setValue(result.getUserGroup().getId());
    					form1.getField("delegator").setCellStyle("ownnertext");
    					form1.getField("delegator").updateState();
    					executeUpdate();
    				}
    				@Override
    				public void onFailure(Throwable caught) {
    					SC.warn(caught.getMessage());
    					form1.getField("delegator").setCellStyle("ownnertextout");
    					form1.getField("delegator").updateState();
    				}
    			});
    			return;
    		}
        	
        	if(mandatorId == null || mandatorUserId == null){
    			ServiceUtil.security().getUser(Session.get().getSid(), mandator, new AsyncCallback<SUser>() {
    				@Override
    				public void onSuccess(SUser result) {
    					mandatorUserId = result.getId();
    					form1.getField("mandatorId").setValue(result.getId());
    					form1.getField("mandator").setCellStyle("ownnertext");
    					form1.getField("mandator").updateState();
    					executeUpdate();
    				}
    				@Override
    				public void onFailure(Throwable caught) {
    					SC.warn(caught.getMessage());
    					form1.getField("mandator").setCellStyle("ownnertextout");
    					form1.getField("mandator").updateState();
    				}
    			});
    			return;
    		}
        	
        	
        	Log.debug(delegator + " " + mandator);
        	
    		if(delegatorrUserId.equals(mandatorUserId)){
    			SC.warn(I18N.message("cantsamedelegator"));
    			return;
    		}
		} catch (Exception e) {} 

		SDelegation delegation = new SDelegation();
		delegation.setId(Long.parseLong(form1.getValueAsString("id")));
		delegation.setMandatorId(form1.getValueAsString("mandatorId"));
		delegation.setMandatorName(form1.getValueAsString("mandator"));
		delegation.setDelegatorId(form1.getValueAsString("delegatorId"));
		delegation.setDelegatorName(form1.getValueAsString("delegator"));
		delegation.setComment(form1.getValueAsString("comment"));
		delegation.setExpireDate((Date)form1.getField("expire").getValue());
		
		delegation.setAllowed(getAllowed());
		
		ServiceUtil.security().saveDelegation(Session.get().getSid(), delegation, new AsyncCallbackWithStatus<SDelegation>() {
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
//				SCM.warn(caught);
				Log.serverError(caught, true);
			}
			@Override
			public void onSuccessEvent(SDelegation result) {
				Log.debug("[ DelegationGridPanel executeUpdate ] onSuccess. id["+result.getId()+"]");
				
				final int selectedRowNum = grid.getRecordIndex(grid.getSelectedRecord());				
				ListGridRecord selectedRecord = grid.getSelectedRecord();		
				selectedRecord.setAttribute("mandatorId", result.getMandatorId());	
				selectedRecord.setAttribute("mandator", result.getMandatorName());
				selectedRecord.setAttribute("delegatorId", result.getDelegatorId());
				selectedRecord.setAttribute("delegator", result.getDelegatorName());			
				selectedRecord.setAttribute("comment", result.getComment());	
				selectedRecord.setAttribute("expire", result.getExpireDate());	
				selectedRecord.setAttribute("modified", new Date());	
				
				gridPrivilegeCheck(selectedRecord, result.getAllowed());
				
				grid.getDataAsRecordList().set(selectedRowNum, selectedRecord);				
				grid.selectSingleRecord(selectedRowNum);
				grid.scrollToRow(selectedRowNum);
				SC.say(I18N.message("operationcompleted"));			
			}
		});
	}	
	
	/**
	 * 삭제
	 */
	private void executeRemove(final long id)
	{
		Log.debug("[ DelegationGridPanel executeRemove ] id["+id+"]");
		if(id < 0) return;
		
		ServiceUtil.security().deleteDelegation(Session.get().getSid(), id, new AsyncCallbackWithStatus<Void>() {
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
				Log.serverError(caught, true);
			}
			@Override
			public void onSuccessEvent(Void result) {
				Log.debug("[ DelegationGridPanel executeRemove ] onSuccess. id["+id+"]");
				grid.removeSelectedData();
				form1.editNewRecord();
				form1.reset();
				form1.getItem("id").setTooltip(I18N.message("generatedbyserver", form1.getItem("id").getTitle()));
				form2.editNewRecord();
				form2.reset();
				setExpireDateItem();
				
				if(!isAdmin){
					form1.getField("mandatorId").setValue(sessionId);
					form1.getField("mandator").setValue(sessionName);
				}
				SC.say(I18N.message("operationcompleted"));
			}
		});
	}
	
	/**
	 * 저장시 DELEGATION_PRIVILEGE CHECK VALUE 가져오기
	 */
	private String[] getAllowed(){
		Collection bindVar = privilege.keySet();
		Iterator iter = bindVar.iterator();
		String varStr = "";
		String tmpAllowedArr = "";
		while(iter.hasNext()) {
			varStr = (String)iter.next();
			privilege.get(varStr);
			if("1".equals(form2.getValueAsString(privilege.get(varStr))) || "true".equals(form2.getValueAsString(privilege.get(varStr)))){
				tmpAllowedArr += ","+varStr;
			}
		}
		
		if(!"".equals(tmpAllowedArr)){
			tmpAllowedArr = tmpAllowedArr.substring(1);
		}
		
		String[] addAllowed = {tmpAllowedArr};
		return addAllowed;
	}
	
	/**
	 * 저장후 GRID PRIVILEGE CHEECK
	 */
	public void gridPrivilegeCheck(ListGridRecord record, String[] allowed){
		Collection bindVar = privilege.keySet();
		Iterator iter = bindVar.iterator();
		String varStr = "";
		boolean rtn = false;
		while(iter.hasNext()) {
			varStr = (String)iter.next();
			String tmpPrivilege = "";
			tmpPrivilege = privilege.get(varStr);
			record.setAttribute(tmpPrivilege, 0);//초기화
			String [] allowedArr = allowed[0].split(",");
			for(int i=0; i<allowedArr.length; i++ ){
				if((varStr).equals(allowedArr[i])){
					record.setAttribute(tmpPrivilege, 1);
					break;
				}
			}
		}
	}

	@Override
	public void onPageDataReqeust(int pageNum, int pageSize) {
		executeFetch(pageNum, pageSize);
		
	}
	
	private void setExpireDateItem(){
		//2013.12.09 나용준
		//폐기일 초기값 7일후로 설정
		Date date = new Date();
		long newTime = date.getTime() + (60*60*24*1000*7);
		date.setTime(newTime);
		expireDateItem.setValue(date);
	}
}