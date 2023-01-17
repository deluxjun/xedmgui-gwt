package com.speno.xedm.gui.frontend.client.admin.documentcode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.RecordList;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.types.SelectionAppearance;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.TransferImgButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.CheckboxItem;
import com.smartgwt.client.widgets.form.fields.HiddenItem;
import com.smartgwt.client.widgets.form.fields.PickerIcon;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.FormItemClickHandler;
import com.smartgwt.client.widgets.form.fields.events.FormItemIconClickEvent;
import com.smartgwt.client.widgets.form.fields.events.KeyUpEvent;
import com.smartgwt.client.widgets.form.fields.events.KeyUpHandler;
import com.smartgwt.client.widgets.form.validator.RegExpValidator;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.CellClickEvent;
import com.smartgwt.client.widgets.grid.events.CellClickHandler;
import com.smartgwt.client.widgets.grid.events.RecordClickEvent;
import com.smartgwt.client.widgets.grid.events.RecordClickHandler;
import com.smartgwt.client.widgets.grid.events.RecordDoubleClickEvent;
import com.smartgwt.client.widgets.grid.events.RecordDoubleClickHandler;
import com.smartgwt.client.widgets.grid.events.RemoveRecordClickEvent;
import com.smartgwt.client.widgets.grid.events.RemoveRecordClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;
import com.smartgwt.client.widgets.tab.events.TabSelectedEvent;
import com.smartgwt.client.widgets.tab.events.TabSelectedHandler;
import com.speno.xedm.core.service.serials.SCode;
import com.speno.xedm.core.service.serials.SDocType;
import com.speno.xedm.core.service.serials.SExtendedAttribute;
import com.speno.xedm.core.service.serials.SFilter;
import com.speno.xedm.core.service.serials.SGroup;
import com.speno.xedm.core.service.serials.SLifeCycle;
import com.speno.xedm.core.service.serials.SRetentionProfile;
import com.speno.xedm.core.service.serials.STemplate;
import com.speno.xedm.gui.common.client.Constants;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.RecordObserver;
import com.speno.xedm.gui.common.client.Session;
import com.speno.xedm.gui.common.client.log.Log;
import com.speno.xedm.gui.common.client.util.AsyncCallbackWithStatus;
import com.speno.xedm.gui.common.client.util.DateCellFormatter;
import com.speno.xedm.gui.common.client.util.ItemFactory;
import com.speno.xedm.gui.common.client.util.LengthValidator;
import com.speno.xedm.gui.common.client.util.ListGridMultipleItem;
import com.speno.xedm.gui.common.client.util.PositiveNumberValidator;
import com.speno.xedm.gui.common.client.util.ReturnHandler;
import com.speno.xedm.gui.common.client.util.SCM;
import com.speno.xedm.gui.common.client.util.ServiceUtil;
import com.speno.xedm.gui.frontend.client.admin.organization.DepartmentGridPanel;
import com.speno.xedm.gui.frontend.client.admin.organization.GroupTreePanel;
import com.speno.xedm.gui.frontend.client.folder.GroupWindow;
import com.speno.xedm.gwt.service.DocumentCodeService;
import com.speno.xedm.gwt.service.DocumentCodeServiceAsync;
import com.speno.xedm.gwt.service.FilterService;
import com.speno.xedm.gwt.service.FilterServiceAsync;
import com.speno.xedm.gwt.service.LifeCycleService;
import com.speno.xedm.gwt.service.LifeCycleServiceAsync;
import com.speno.xedm.gwt.service.TemplateService;
import com.speno.xedm.gwt.service.TemplateServiceAsync;

/**
 * DocumentTypeGrid Panel
 * @author 박상기
 * @since 1.0
 */
public class DocumentTypeGridPanel extends VLayout {
	private static HashMap<String, DocumentTypeGridPanel> instanceMap = new HashMap<String, DocumentTypeGridPanel>();
	
	private ListGrid grid;
	private ListGrid grid1;
	private ListGrid grid2;
	private DynamicForm form;
	private DynamicForm form1;
	private HLayout actionHL;
	private TabSet tabs = null;
	private TextItem nameItem ;
	
	private LinkedHashMap<String, String> rOpts = new LinkedHashMap<String, String>() ;		
	private LinkedHashMap<String, String> eOpts = new LinkedHashMap<String, String>() ;
	private LinkedHashMap<String, String> cOpts = new LinkedHashMap<String, String>() ;
	private LinkedHashMap<String, String> iOpts = new LinkedHashMap<String, String>() ;
	private LinkedHashMap<String, String> uOpts = new LinkedHashMap<String, String>() ;
	private LinkedHashMap<Long, String> lOpts = new LinkedHashMap<Long, String>() ;
	private LinkedHashMap<Long, String> tOpts = new LinkedHashMap<Long, String>() ;
	
	private SelectItem retentionIdItem, eclassidItem, cclassidItem, indexidItem, uclassidItem, lifecycleIdItem, filterIdItem, templateIdItem; //ComboBox
	
	private ListGridMultipleItem multiValueItem;
	
	private RecordObserver recordObserver;	
	
	private GroupTreePanel groupTreePanel;
	private DepartmentGridPanel departmentGridPanel;
	
	private HLayout tempForm1HL;
	private VLayout tempForm2VL;
	private boolean isAction;
	private String tmpFilterIds;
	private String tmpRewritecmds;
	private String tmpRewriteGroups;
	private String tmpRewriteGroupIdPath;
	private String tmpRewriteGroupPath;	
	
	/**
	 * 기 생성된 instance를 반환함. instance가 없을 경우 생성 후 반환함
	 * @param id
	 * @param subTitle
	 * @param ob
	 * @param isAction
	 * @return
	 */
	public static DocumentTypeGridPanel get(final String id, final String subTitle, final RecordObserver ob, final boolean isAction) {
		if (instanceMap.get(id) == null) {
			new DocumentTypeGridPanel(id, subTitle, ob, isAction);
		}
		return instanceMap.get(id);
	}
	
	public DocumentTypeGridPanel(final String id, final String subTitle, final RecordObserver ob, final boolean isAction) {
		instanceMap.put(id, this);	
		
		this.recordObserver = ob;
		this.isAction = isAction;
		
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
		// kimsoeun GS인증용 - 그리드 헤더 우클릭 없앰
        grid.setShowHeaderContextMenu(false);
		grid.setWidth100();
		grid.setHeight100();		
		grid.setShowAllRecords(true);
		
		grid.setEmptyMessage(I18N.message("notitemstoshow"));		
		grid.setCanFreezeFields(true);
		
		//20140205na 문서유형이 삭제될 수 있는지 여부를 context에서 설정
		grid.setCanRemoveRecords(isAction);
//		grid.setCanRemoveRecords(Util.getSetting("setting.doctype.delete"));
		grid.setSelectionType(SelectionStyle.SINGLE);
		grid.invalidateCache();
		
		ListGridField typeIdField = new ListGridField("id", I18N.message("typeid"));
		typeIdField.setHidden(true);
		ListGridField typeNmField = new ListGridField("name", I18N.message("typename"));
		ListGridField descField = new ListGridField("description", I18N.message("description"));
		descField.setWidth("*");		
		ListGridField retentionIdField = new ListGridField("retentionId");
		retentionIdField.setHidden(true);
		ListGridField retentionNmField = new ListGridField("retentionName", I18N.message("retentionname"));		
		ListGridField retentionPeriodField = new ListGridField("retentionPeriod");
		retentionPeriodField.setHidden(true);
		ListGridField eClassIdField = new ListGridField("eclassid");
		eClassIdField.setHidden(true);
		ListGridField cClassIdField = new ListGridField("cclassid");
		cClassIdField.setHidden(true);
		ListGridField indexIdField = new ListGridField("indexid");
		indexIdField.setHidden(true);
		ListGridField uClassIdField = new ListGridField("uclassid");
		uClassIdField.setHidden(true);
		
		ListGridField templateIdField = new ListGridField("templateId");
		templateIdField.setHidden(true);
		ListGridField templateNameField = new ListGridField("templateName", I18N.message("template"));
		ListGridField lifecycleIdField = new ListGridField("lifecycleId");
		lifecycleIdField.setHidden(true);
		ListGridField lifecycleNameField = new ListGridField("lifecycleName",I18N.message("lifecycle"));
		lifecycleNameField.setHidden(true);
		ListGridField filterIdField = new ListGridField("filterIds");
		filterIdField.setHidden(true);
		ListGridField versionControlField = new ListGridField("versionControl",I18N.message("second.versioncontrolabbreviation"));
		versionControlField.setType(ListGridFieldType.BOOLEAN);
		versionControlField.setCanEdit(false);
		versionControlField.setShowTitle(true);
		ListGridField deadLineField = new ListGridField("deadLine");
		deadLineField.setHidden(true);
		ListGridField filterIdsField = new ListGridField("filterIds");
		filterIdsField.setHidden(true);
		ListGridField rewriteCmdField = new ListGridField("rewriteCmd");
		rewriteCmdField.setHidden(true);
		ListGridField rewriteGroupField = new ListGridField("rewriteGroup");
		rewriteGroupField.setHidden(true);
		ListGridField rewriteExpireField = new ListGridField("rewriteExpire");
		rewriteExpireField.setHidden(true);
		ListGridField rewriteGroupPathField = new ListGridField("rewriteGroupPath");
		rewriteGroupPathField.setHidden(true);
		ListGridField rewriteGroupIdPathField = new ListGridField("rewriteGroupIdPath");
		rewriteGroupIdPathField.setHidden(true);		
		ListGridField creationDateField = new ListGridField("creationdate", I18N.message("creationdate"), 130);
		creationDateField.setAlign(Alignment.CENTER);
		creationDateField.setType(ListGridFieldType.DATE);
		creationDateField.setCellFormatter(new DateCellFormatter(I18N.message("yyyy/MM/dd HH:mm:ss")));
		
		
		grid.setFields(typeIdField, typeNmField, descField, versionControlField, retentionIdField, retentionNmField, retentionPeriodField, 
				eClassIdField, cClassIdField, indexIdField, uClassIdField,
				templateIdField, templateNameField, lifecycleIdField, lifecycleNameField, filterIdField, deadLineField, 
				rewriteCmdField, rewriteGroupField, rewriteExpireField, rewriteGroupPathField, rewriteGroupIdPathField, creationDateField);
		
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

		VLayout gridVL = new VLayout();
		gridVL.setShowResizeBar(true);
		gridVL.addMember(grid);

		docTypeVL.addMember(gridVL);
		
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
					form.reset();
	            	form.editRecord(record);
					SC.confirm(I18N.message("wanttodelete"),  new BooleanCallback() {
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
			
			Tab basic = new Tab();
			basic.setTitle(I18N.message("second.basic"));

			Tab approval = new Tab();
			approval.setTitle(I18N.message("approval"));		
			tabs = new TabSet();
			tabs.setTabs(basic,approval);
			tabs.setHeight("60%");
			tabs.setWidth100();
			
			tempForm1HL = createForm1HL();
			tempForm2VL = createForm2VL();
			
			tabs.addTabSelectedHandler(new TabSelectedHandler(){
				
				
				@Override
				public void onTabSelected(TabSelectedEvent event) {
					Tab tab = ((TabSet)event.getSource()).getSelectedTab();
					if(tab.getTitle().equals(I18N.message("second.basic"))) {
						tab.setPane(tempForm1HL);
					}
					if(tab.getTitle().equals(I18N.message("approval"))) {
						tab.setPane(tempForm2VL);
					}
					
				}
				
			});		
			
			docTypeVL.addMember(tabs);
			tabs.selectTab(1);
			docTypeVL.addMember(createActHL());
		}
        
        addMember(docTypeVL);
        
        executeFetch();
        
        
	}
	
	/**
	 * 하단 BAGIC Form 생성
	 * @return
	 */
	private HLayout createForm1HL() {
		HiddenItem typeIdItem = new HiddenItem("id");
		
		TextItem typeNmItem = new TextItem("name", I18N.message("typename"));
		typeNmItem.setRequired(true);		
		// kimsoeun GS인증용 - 툴팁 다국어화
		typeNmItem.setRequiredMessage(I18N.message("fieldisrequired"));
		typeNmItem.setCanEdit(true);
		typeNmItem.setWrapTitle(false);
//		typeNmItem.setLength(Constants.MAX_LEN_NAME);
		typeNmItem.setValidators(new LengthValidator(typeNmItem, Constants.MAX_LEN_DOCUMENTtYPE_NAME));
		
		TextItem descItem = new TextItem("description", I18N.message("description"));
		descItem.setCanEdit(true);		
		descItem.setWrapTitle(false);
//		descItem.setLength(Constants.MAX_LEN_NAME);
		descItem.setValidators(new LengthValidator(descItem, Constants.MAX_LEN_NAME));
		
		retentionIdItem = new SelectItem("retentionId", I18N.message("retention"));
		retentionIdItem.setRequired(true);	
		// kimsoeun GS인증용 - 툴팁 다국어화
		retentionIdItem.setRequiredMessage(I18N.message("fieldisrequired"));
		retentionIdItem.setType("combobox");	
		retentionIdItem.setEmptyDisplayValue(I18N.message("choosetype"));		
		executeGetRetentionAndSet();
		
		eclassidItem = new SelectItem("eclassid", I18N.message("eclassid"));
		eclassidItem.setRequired(true);
		// kimsoeun GS인증용 - 툴팁 다국어화
		eclassidItem.setRequiredMessage(I18N.message("fieldisrequired"));
		eclassidItem.setType("combobox");
		eclassidItem.setEmptyDisplayValue(I18N.message("choosetype"));
		eclassidItem.setWrapTitle(false);
		// kimsoeun GS인증용 - disable된 필드 안보이게
		eclassidItem.hide();
		executeGetElementClassIdsAndSet();
		
		cclassidItem = new SelectItem("cclassid", I18N.message("cclassid"));
		cclassidItem.setRequired(true);
		// kimsoeun GS인증용 - 툴팁 다국어화
		cclassidItem.setRequiredMessage(I18N.message("fieldisrequired"));
		cclassidItem.setType("combobox");
		cclassidItem.setEmptyDisplayValue(I18N.message("choosetype"));
		cclassidItem.setWrapTitle(false);
		executeGetContentClassIdsAndSet();
		
		indexidItem = new SelectItem("indexid", I18N.message("indexid"));
		indexidItem.setType("combobox");
		indexidItem.setEmptyDisplayValue(I18N.message("second.choosenot"));
		indexidItem.setWrapTitle(false);
		// kimsoeun GS인증용 - disable된 필드 안보이게
		indexidItem.hide();
		executeGetIndexIdsAndSet();
		
		uclassidItem = new SelectItem("uclassid", I18N.message("uclassid"));
		uclassidItem.setRequired(true);
		// kimsoeun GS인증용 - 툴팁 다국어화
		uclassidItem.setRequiredMessage(I18N.message("fieldisrequired"));
		uclassidItem.setType("combobox");
		uclassidItem.setEmptyDisplayValue(I18N.message("choosetype"));
		uclassidItem.setWrapTitle(false);
		// kimsoeun GS인증용 - disable된 필드 안보이게
		uclassidItem.hide();
		executeGetUserClassIdsAndSet();
		
		
		
		CheckboxItem versionControlCb = new CheckboxItem("versionControl",I18N.message("second.versioncontrol"));
		versionControlCb.setCanEdit(true);		
		versionControlCb.setWrapTitle(false);
		
		RegExpValidator chkVlidator = new RegExpValidator();   
		chkVlidator.setExpression("[0-9]");   
		
		TextItem deadLineItem = new TextItem("deadLine", 
				I18N.message("second.ceeckoutdeadline")
				+"("+I18N.message("CheckedOut")+"/"+I18N.message("locked")+")");
		deadLineItem.setCanEdit(true);		
		deadLineItem.setWrapTitle(false);
		deadLineItem.setHint("<nobr>"+I18N.message("days")+"</nobr>");
		deadLineItem.setValidators(chkVlidator);
//		deadLineItem.setLength(Session.get().getInfo().getIntConfig("gui.deadline.fieldsize", 4));
		deadLineItem.setValidators(new LengthValidator(deadLineItem, Session.get().getInfo().getIntConfig("gui.deadline.fieldsize", 4))
								   ,new PositiveNumberValidator(deadLineItem));
		
		
		deadLineItem.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if(null == event.getKeyName()){
					event.cancel();
					return;
				}else{
					String temp = event.getKeyName().toLowerCase();
					if(!temp.matches("[0-9]*")&&!temp.matches("backspace")&&!temp.matches("delete")&&!temp.matches("arrow_left")&&!temp.matches("arrow_right")){
						event.cancel();
						return;
					}
				}
			}
		});
		
		lifecycleIdItem = new SelectItem("lifecycleId", I18N.message("second.lifecycleid"));
		lifecycleIdItem.setType("combobox");
		lifecycleIdItem.setEmptyDisplayValue(I18N.message("choosetype"));
		lifecycleIdItem.disable();
		// kimsoeun GS인증용 - disable된 필드 안보이게
		lifecycleIdItem.hide();
		executeGetLifeCycleIdsAndSet();
		
//		filterIdItem = new SelectItem("filterId	", I18N.message("second.filterid"));
//		filterIdItem.setType("combobox");
//		filterIdItem.setEmptyDisplayValue(I18N.message("choosetype"));
//		executeGetFilterIdsAndSet();
		
		templateIdItem = new SelectItem("templateId", I18N.message("second.defaulttemplateid"));
		templateIdItem.setWrapTitle(false);
		templateIdItem.setType("combobox");
		templateIdItem.setEmptyDisplayValue(I18N.message("choosetype"));
		executeGetTemplateIdsAndSet();
		
		
//		typeIdItem.setStartRow(false);			typeIdItem.setEndRow(true);		
//		typeNmItem.setStartRow(false);		typeNmItem.setEndRow(true);		
//		descItem.setStartRow(false);			descItem.setEndRow(true);		
//		retentionIdItem.setStartRow(false);	retentionIdItem.setEndRow(true);		
//		eclassidItem.setStartRow(false);		eclassidItem.setEndRow(true);		
//		cclassidItem.setStartRow(false);		cclassidItem.setEndRow(true);		
//		indexidItem.setStartRow(false);			indexidItem.setEndRow(true);
//		uclassidItem.setStartRow(false);		uclassidItem.setEndRow(true);
//		
//		versionControlCb.setStartRow(false);		versionControlCb.setEndRow(true);
//		deadLineItem.setStartRow(false);		deadLineItem.setEndRow(true);
//		lifecycleIdItem.setStartRow(false);		lifecycleIdItem.setEndRow(true);
////		filterIdItem.setStartRow(false);		filterIdItem.setEndRow(true);
//		templateIdItem.setStartRow(false);		templateIdItem.setEndRow(true);
		
		// 확장속성
		multiValueItem = ItemFactory.newListGridMultipleItem(
				"extended", I18N.message("extentionproperties"), 
				new String[]{"name","value"}, new String[]{I18N.message("name"), I18N.message("value")}, new int[]{255,255},
				100, false);  
        multiValueItem.setRequired(false);
        multiValueItem.setHeight(150);
        
		form = new DynamicForm();
		form.setMargin(1);
		form.setNumCols(4);
		form.setTitleWidth(150);
		form.setItems(typeIdItem, typeNmItem, descItem, retentionIdItem, eclassidItem, cclassidItem, indexidItem, uclassidItem, versionControlCb, 
				deadLineItem, lifecycleIdItem, templateIdItem, multiValueItem);
		form.reset();
		
		grid1 = new ListGrid();
		grid1.setMargin(2);
		grid1.setWidth("*");
		grid1.setHeight("*");		
		grid1.setShowAllRecords(true);
		grid1.setEmptyMessage(I18N.message("notitemstoshow"));
		grid1.setSelectionAppearance(SelectionAppearance.CHECKBOX); 
	        
		grid1.setCanFreezeFields(true);
		grid1.setCanRemoveRecords(false);
		grid1.setSelectionType(SelectionStyle.SIMPLE);
		
		ListGridField filterIdField = new ListGridField("filterId", I18N.message("second.filterid"));
		ListGridField filterNameField = new ListGridField("filterName", I18N.message("second.filterid"));
		filterIdField.setHidden(true);
		grid1.setFields(filterIdField, filterNameField);
		
		grid1.addCellClickHandler(new CellClickHandler() {
			@Override
			public void onCellClick(CellClickEvent event) {				        		
				int colNum = event.getColNum();  
				if(0 != colNum){
					Record r = event.getRecord();
	            	if (!grid1.isSelected((ListGridRecord)r))
	            		grid1.selectRecord(r);
	            	else
	            		grid1.deselectRecord(r);
				}
			}
		});
		
    	HLayout formHL = new HLayout(10);
    	formHL.setWidth(1000);
    	formHL.setAutoHeight();    	
    	formHL.addMembers(form,grid1);
    	
    	return formHL;
	}
	
	/**
	 * 하단 APPROVAL Form 생성
	 * @return
	 */
	private VLayout createForm2VL() {
		Label subTitleLable1 = new Label();
		subTitleLable1.setMargin(2);
		subTitleLable1.setAutoHeight();   
		subTitleLable1.setAlign(Alignment.LEFT);   
		subTitleLable1.setValign(VerticalAlignment.CENTER);
		subTitleLable1.setStyleName("subTitle");
		subTitleLable1.setContents(I18N.message("second.commandtosetpayment"));
        
		grid2 = new ListGrid();
		grid2.setMargin(2);
		grid2.setWidth100();
		grid2.setHeight(150);		
		grid2.setShowAllRecords(true);
		grid2.setEmptyMessage(I18N.message("notitemstoshow"));
		grid2.setSelectionAppearance(SelectionAppearance.CHECKBOX); 
	        
		grid2.setCanFreezeFields(true);
		grid2.setCanRemoveRecords(false);
		grid2.setSelectionType(SelectionStyle.SIMPLE);
		
		ListGridField rewritecmdIdField = new ListGridField("rewritecmdId", I18N.message("command"));
		ListGridField rewritecmdNameField = new ListGridField("rewritecmdName", I18N.message("command"));
		rewritecmdIdField.setHidden(true);
		grid2.setFields(rewritecmdIdField, rewritecmdNameField);
		
		grid2.addCellClickHandler(new CellClickHandler() {
			@Override
			public void onCellClick(CellClickEvent event) {				        		
				int colNum = event.getColNum();  
				if(0 != colNum){
					Record r = event.getRecord();
	            	if (!grid2.isSelected((ListGridRecord)r))
	            		grid2.selectRecord(r);
	            	else
	            		grid2.deselectRecord(r);
				}
			}
		});
		
		VLayout grid1VL = new VLayout();
		grid1VL.setHeight100();
		grid1VL.setWidth("10%");
		grid1VL.setMembers(subTitleLable1,grid2);
				
		// 20140228 육용수의 코딩 검색기능 추가
		Label subTitleLable2 = new Label();
		subTitleLable2.setMargin(2);
		subTitleLable2.setAutoHeight();   
		subTitleLable2.setWidth(630);
		subTitleLable2.setAlign(Alignment.LEFT);   
		subTitleLable2.setValign(VerticalAlignment.CENTER);
		subTitleLable2.setStyleName("subTitle");
		subTitleLable2.setContents(I18N.message("second.finalsettlementgroup"));
		
		DynamicForm search_form = new DynamicForm();
    	
		nameItem = new TextItem("group", I18N.message("group"));
		nameItem.setWrapTitle(false);
		nameItem.setWidth(100);
		nameItem.setRequired(true);
		nameItem.setCanEdit(true);
		//nameItem.setDisabled(true);	
				
		nameItem.setStartRow(false);
		nameItem.setEndRow(false);
	
		PickerIcon searchPicker = new PickerIcon(PickerIcon.SEARCH, new FormItemClickHandler() {   
            public void onFormItemClick(FormItemIconClickEvent event) {      
            	GroupSearch();
            }
         });           
    	nameItem.setIcons(searchPicker);
    	
    	nameItem.addKeyUpHandler(new com.smartgwt.client.widgets.form.fields.events.KeyUpHandler() {			
			@Override
			public void onKeyUp(
					com.smartgwt.client.widgets.form.fields.events.KeyUpEvent event) {
				// TODO Auto-generated method stub
				if(event.getKeyName().equals("Enter"))
				GroupSearch();
			}
		});
    	
		search_form.setItems(nameItem);
		search_form.setAutoHeight();
		search_form.setAutoWidth();			
		
		HLayout subjectnameVL = new HLayout();
		subjectnameVL.setMembers(subTitleLable2);		
		
		HLayout searchHL = new HLayout();
		searchHL.setMembers(search_form);
		searchHL.setAlign(Alignment.RIGHT);
		subjectnameVL.setHeight("20%");
		
		VLayout TopHL = new VLayout();
		TopHL.setMembers(subjectnameVL,searchHL);
		
		VLayout groupVL = new VLayout();
		groupVL.setHeight("40%");
		groupVL.setWidth("30%");
		groupVL.setMembers(TopHL,createGroupControl());
		
		RegExpValidator regExpIdValidator = new RegExpValidator();   
		regExpIdValidator.setExpression("[0-9]");
		
		TextItem rewriteExpireItem = new TextItem("rewriteExpire", I18N.message("second.appexpirationdate"));
		rewriteExpireItem.setWidth("40");
		rewriteExpireItem.setCanEdit(true);
		rewriteExpireItem.setWrapTitle(false);
		rewriteExpireItem.setValidators(regExpIdValidator, new PositiveNumberValidator(rewriteExpireItem));
		rewriteExpireItem.setHint("<nobr>"+I18N.message("days")+"</nobr>");
		rewriteExpireItem.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if(null == event.getKeyName()){
					event.cancel();
					return;
				}else{
					String temp = event.getKeyName().toLowerCase();
					if(!temp.matches("[0-9]*")&&!temp.matches("backspace")&&!temp.matches("delete")&&!temp.matches("arrow_left")&&!temp.matches("arrow_right")){
						event.cancel();
						return;
					}
				}
			}
		});
		
			
		rewriteExpireItem.setStartRow(false);		rewriteExpireItem.setEndRow(false);	
		
		form1 = new DynamicForm();
		form1.setAutoWidth();
		form1.setMargin(1);
		form1.setNumCols(1);
		form1.setColWidths("1");
		form1.setItems(rewriteExpireItem);
		form1.reset();
		
    	HLayout gridGroupVL = new HLayout(1);
    	gridGroupVL.setHeight("70%");
    	gridGroupVL.setWidth100();    	
    	gridGroupVL.addMembers(grid1VL,groupVL);
    	
    	VLayout formVL = new VLayout(1);
    	formVL.addMembers(gridGroupVL, form1);
    	
    	executeGetOptionsFilterIdSet();
    	executeGetOptionsAppSetCommandSet();
    	
    	return formVL;
	}
	
	private HLayout createGroupControl() {
		groupTreePanel = new GroupTreePanel("admin.doctype.user", null, true, false, false);
    	departmentGridPanel = new DepartmentGridPanel("admin.doctype.user", null, groupTreePanel.getGroupTree());
    	departmentGridPanel.getGrid().setEmptyMessage((I18N.message("notitemstoshow")));    
		
    	HLayout groupHL = new HLayout(10);
    	groupHL.setWidth100();
    	groupHL.setHeight(150);
    	groupHL.setMargin(1);
    	groupHL.setMembers(departmentGridPanel, createArrowVL(), groupTreePanel);
    	return groupHL;
	}
	
	/**
	 * Action Panel 생성
	 * @return
	 */
	private void GroupSearch()
	{
		GroupWindow groupWindow = new GroupWindow("single", new ReturnHandler() {
			@Override
			public void onReturn(Object param) {
				String[][] groupInfo = (String[][]) param;	
				ListGridRecord R = new ListGridRecord();
				R.setAttribute("id", groupInfo[0][0]);
				R.setAttribute("name", groupInfo[0][1]);
				R.setAttribute("path", groupInfo[0][2]);
				R.setAttribute("IDPath",groupInfo[0][3]);
		
				
				String path = R.getAttributeAsString("path");
					
					Map<String, String> targetAllMap = new HashMap<String, String>();
					Map<String, String> targetEndMap = new HashMap<String, String>();
					List<String> srcNodesList = new ArrayList<String>();
					
					Record record;
					String pathStr;
					String[] nodeArr;
					
					//targetAllMap, targetEndMap 추출--------------------------------------
					for(int j=0; j<departmentGridPanel.grid.getRecords().length; j++) {
						record = departmentGridPanel.grid.getRecords()[j];
						//20140409 yys IDPath값이null인 경우가 존재함 path가 다르고 같은 이름의 부서라도 path자체가 같을수는 없음.
						pathStr = record.getAttributeAsString("path");
						
						nodeArr = pathStr.split(">");
						
						for(int k=0; k<nodeArr.length; k++) {
							targetAllMap.put(nodeArr[k].trim(), nodeArr[k].trim());
						}
						targetEndMap.put(nodeArr[nodeArr.length-1].trim(), nodeArr[nodeArr.length-1].trim());
					}
										
					nodeArr = path.split(">");
					for(int k=0; k<nodeArr.length; k++) {
						srcNodesList.add(nodeArr[k].trim());
					}		
					
					//targetEndMap이 srcNodesSet에 있는지 조사--------------------------
					for(int j=0; j<srcNodesList.size(); j++) {
						if(targetEndMap.get(srcNodesList.get(j)) != null) {
							SC.warn(I18N.message("dupmessage"));
							return;
						}
					}		
							
					//dragSourceGrid의 최하위 id가 targetAllMap에 있는지 조사-----------
					for(int j=0; j<targetAllMap.size(); j++) {
						if(targetAllMap.get(R.getAttributeAsString("name")) != null) {
							SC.warn(I18N.message("dupmessage"));
							return;
						}
					}					
				departmentGridPanel.grid.addData(R);
			
				}										
		}, false, nameItem.getValueAsString());
    	groupWindow.show();
    }
	
	
	private HLayout createActHL() {		
		Button btnAddNew = new Button(I18N.message("addnew"));
		btnAddNew.setIcon(ItemFactory.newImgIcon("page_white_add.png").getSrc());
		btnAddNew.addClickHandler(new ClickHandler() {   
            public void onClick(ClickEvent event) {
            	form.getItem("id").setTooltip(I18N.message("generatedbyserver", form.getItem("id").getTitle()));
            	form.editNewRecord();
            	form.reset();
            	form1.reset();
            	grid.deselectAllRecords();
            	grid1.deselectAllRecords();
            	grid2.deselectAllRecords();
		    	departmentGridPanel.resetGrid1();
				tabs.selectTab(0);
				
				// extended
				multiValueItem.setGridData(new Record[0]);
            }   
        });
		
		Button btnSave = new Button(I18N.message("save"));
		btnSave.setIcon(ItemFactory.newImgIcon("data_into.png").getSrc());
		btnSave.addClickHandler(new ClickHandler() {   
            public void onClick(ClickEvent event) {
            	//20131220na 최종 결재 그룹 지정을 안 하면 에러 메시지 출력
            	int listSize = departmentGridPanel.getRecordList().getLength();
            	if(listSize == 0 && grid2.getSelectedRecords().length != 0){
            		SC.warn(I18N.message("youhavetoselectfinalgroup"));
            		return;
            	}
            	if(multiValueItem.isNullData()){
            		SC.warn(I18N.message("havetoenternameandvalue"));
            		return;
            	}
            	
            	if(form.getValue("id") == null) {
            		 if(form.validate()){
            			 if(form1.validate()){
            				 executeAdd();
            			 }else{
            				 tabs.selectTab(1);
            			 }
            		 }else{
            			 tabs.selectTab(0);
            		 }
            	}
            	else {
            		 if(form.validate()) {
            			 if(form1.validate()){
            				 executeUpdate();
            			 }else{
            				 tabs.selectTab(1);
            			 }
            		 }else{
            			 tabs.selectTab(0);
            		 }
            	}
            }   
        });
		
		actionHL = new HLayout(10);
		actionHL.setHeight(1);
		actionHL.setMembers(btnAddNew, btnSave);		
		return actionHL;
	}
	
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
	    	
	    	if(null == record.getAttribute("lifecycleId"))lifecycleIdItem.setValue(0L);
	    	if(null == record.getAttribute("templateId"))templateIdItem.setValue(0L);
	    	if(null == record.getAttribute("indexid"))indexidItem.setValue("");
	    	
	    	// TODO: 확장속성
	    	multiValueItem.setGridData(record.getAttributeAsRecordArray("extended"));
	    	
	    	if(isAction) {
	    		grid1.deselectAllRecords();
	    		if(!"".equals(record.getAttributeAsString("filterIds"))){
		    		String [] chkFilterIds = record.getAttributeAsString("filterIds").split(",");
		        	for(int i=0; i< chkFilterIds.length; i++){
			        	RecordList rclist  = grid1.getDataAsRecordList();
			    		Record[] rc = rclist.findAll("filterId", chkFilterIds[i]);
			    		grid1.selectRecords(rc);
		        		}
		    	}
	    		
	    		grid2.deselectAllRecords();
		    	if(null != record.getAttributeAsString("rewriteCmd")){
		    		String [] chkRewriteCmd = record.getAttributeAsString("rewriteCmd").split(",");
		        	for(int i=0; i< chkRewriteCmd.length; i++){
			        	RecordList rclist  = grid2.getDataAsRecordList();
			    		Record[] rc = rclist.findAll("rewritecmdId", chkRewriteCmd[i]);
			    		grid2.selectRecords(rc);
		        		}
		    	}
		    	
		    	departmentGridPanel.resetGrid1();
		    	
		    	if(null != record.getAttributeAsString("rewriteGroup")&& !"".equals(record.getAttributeAsString("rewriteGroup"))){
		    		String [] chkRewriteGroup = record.getAttributeAsString("rewriteGroup").split(",");
		    		String [] chkRewriteGroupIdPath = record.getAttributeAsString("rewriteGroupIdPath").split(",");
		    		String [] chkRewriteGroupPath = record.getAttributeAsString("rewriteGroupPath").split(",");
		    	
		    		SGroup[] groups  = new SGroup[chkRewriteGroup.length];
		    		
		        	for(int i=0; i< chkRewriteGroup.length; i++){
		        		groups[i] = new SGroup();
		        		groups[i].setId(chkRewriteGroup[i]);
		        		groups[i].setIDPath(chkRewriteGroupIdPath[i]);
		        		groups[i].setPath(chkRewriteGroupPath[i]);
		        	}
		        	departmentGridPanel.addData(groups);
		    	}
		    	form1.getField("rewriteExpire").setValue(record.getAttributeAsString("rewriteExpire"));
	    	}
		}
	}
	
	private void executeFetch()	{				
		Log.debug("[ DocumentTypeGridPanel executeFetch ]");
		
		ServiceUtil.documentcode().listDocTypeLikeName(Session.get().getSid(), "", true, new AsyncCallbackWithStatus<List<SDocType>>() {
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
			public void onSuccessEvent(List<SDocType> result) {				
				grid.setData(new ListGridRecord[0]); //그리드 초기화					
				for (int j = 0; j < result.size(); j++) {					
					ListGridRecord record=new ListGridRecord();
					record.setAttribute("id", result.get(j).getId());
					record.setAttribute("name", result.get(j).getName());
					record.setAttribute("description", result.get(j).getDescription());					
					record.setAttribute("retentionId", result.get(j).getRetentionPeriod() + "," + result.get(j).getRetentionId());					
					record.setAttribute("retentionName", result.get(j).getRetentionName() + " (" + result.get(j).getRetentionPeriod() + ")");
					record.setAttribute("retentionPeriod", result.get(j).getRetentionPeriod());
					record.setAttribute("eclassid", result.get(j).getElementClassId());
					record.setAttribute("cclassid", result.get(j).getContentClassId());
					record.setAttribute("indexid", result.get(j).getIndexId());					
					record.setAttribute("uclassid", result.get(j).getUserClassId());
					record.setAttribute("templateId", result.get(j).getTemplateId());
					record.setAttribute("templateName", result.get(j).getTemplateName());
					record.setAttribute("lifecycleId", result.get(j).getLifecycleId());
					record.setAttribute("lifecycleName", result.get(j).getLifecycleName());
					record.setAttribute("filterIds", result.get(j).getFilterIds());
					record.setAttribute("versionControl", result.get(j).getVersionControl());
					record.setAttribute("deadLine", result.get(j).getDeadLine());
					record.setAttribute("rewriteCmd", result.get(j).getRewriteCmd());
					record.setAttribute("rewriteGroup", result.get(j).getRewriteGroup());
					record.setAttribute("rewriteGroupIdPath", result.get(j).getRewriteGroupIdPath());
					record.setAttribute("rewriteGroupPath", result.get(j).getRewriteGroupPath());	
					record.setAttribute("rewriteExpire", result.get(j).getRewriteExpire());
					record.setAttribute("creationdate", result.get(j).getLastmodifed());
					
					// TODO: extended
					Record[] records = getAttributesAsRecordArray(result.get(j));
					record.setAttribute("extended", records);
					grid.addData(record);
				}	
				
				if (result.size() > 0) {
					grid.selectSingleRecord(0);
					recordClickedProcess(grid.getRecord(0));
				}
				
				Log.debug("DocumentTypeGridPanel executeFetch ] result.size()["+result.size()+"]");
				if(isAction) {	
					tabs.selectTab(0);
				}
				
			}
		});
	}

	// 20140122, junsoo, SDocType으로 부터 제거. (서버도 사용되는 모듈이므로)
	public void setAttributes(SDocType docType, Record[] records) {
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
		docType.setAttributes(attributes);
	}

	public Record[] getAttributesAsRecordArray(SDocType docType) {
		SExtendedAttribute[] attributes = docType.getAttributes();
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
	
	private void executeAdd() {
		Log.debug("[ DocumentTypePanel executeAdd ]");
		
		SDocType docType = new SDocType();
		docType.setId(0L);
		docType.setName(form.getValueAsString("name"));
		docType.setDescription(form.getValueAsString("description"));
		String key = form.getValueAsString("retentionId");
		docType.setRetentionId(Long.parseLong(key.substring(key.indexOf(",") + 1)));
		docType.setElementClassId(form.getValueAsString("eclassid"));
		docType.setContentClassId(form.getValueAsString("cclassid"));
		
		if(I18N.message("second.choosenot").equals(form.getValueAsString("indexid"))) docType.setIndexId("");
		else docType.setIndexId(form.getValueAsString("indexid"));
		docType.setUserClassId(form.getValueAsString("uclassid"));
		if(form.getValue("templateId")!= null&& !"0".equals(form.getValue("templateId")))docType.setTemplateId(Long.parseLong(form.getValueAsString("templateId")));
		if(form.getValue("lifecycleId")!= null&& !"0".equals(form.getValue("lifecycleId")))docType.setLifecycleId(Long.parseLong(form.getValueAsString("lifecycleId")));
		docType.setVersionControl("true".equals(form.getValueAsString("versionControl"))||"1".equals(form.getValueAsString("versionControl"))?1:0);
		if(form.getValue("deadLine")!= null)docType.setDeadLine(Integer.parseInt(form.getValueAsString("deadLine")));
		
		// TODO: extended
		setAttributes(docType, multiValueItem.getData());
		
		ListGridRecord[] rclist1 = grid1.getSelectedRecords();
		int cmdCnt1 = rclist1.length;
		tmpFilterIds = "";
		if(cmdCnt1>0){
			for(int i=0; i<cmdCnt1; i++){
				tmpFilterIds += "," + rclist1[i].getAttribute("filterId");
			}
			tmpFilterIds = tmpFilterIds.substring(1);
		}	
		
		ListGridRecord[] rclist = grid2.getSelectedRecords();
		int cmdCnt = rclist.length;
		tmpRewritecmds = "";
		if(cmdCnt>0){
			for(int i=0; i<cmdCnt; i++){
				tmpRewritecmds += "," + rclist[i].getAttribute("rewritecmdId");
			}
			tmpRewritecmds = tmpRewritecmds.substring(1);
		}		
		
		RecordList recordList = departmentGridPanel.getRecordList();
		int grpCnt = recordList.getLength();
		
		tmpRewriteGroups = "";
		tmpRewriteGroupIdPath = "";
		tmpRewriteGroupPath = "";
		
		if(grpCnt>0){
			for(int j=0; j<grpCnt; j++) {
				tmpRewriteGroups += ","+ recordList.get(j).getAttributeAsString("id");
				tmpRewriteGroupIdPath += ","+ recordList.get(j).getAttributeAsString("IDPath");
				tmpRewriteGroupPath += ","+ recordList.get(j).getAttributeAsString("path");
			}
			tmpRewriteGroups = tmpRewriteGroups.substring(1);
			tmpRewriteGroupIdPath = tmpRewriteGroupIdPath.substring(1);
			tmpRewriteGroupPath = tmpRewriteGroupPath.substring(1);
		}	
		
		docType.setFilterIds(tmpFilterIds);
		docType.setRewriteCmd(tmpRewritecmds);
		docType.setRewriteGroup(tmpRewriteGroups);
		if(form1.getValue("rewriteExpire")!= null)docType.setRewriteExpire(Integer.parseInt(form1.getValueAsString("rewriteExpire")));
		
		ServiceUtil.documentcode().saveDocType(Session.get().getSid(), docType, new AsyncCallbackWithStatus<SDocType>() {
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
			public void onSuccessEvent(SDocType result) {
				Log.debug("[ DocumentTypePanel executeAdd ] onSuccess. id["+result.getId()+"]");
				
				ListGridRecord addRecord = new ListGridRecord();
				addRecord.setAttribute("id", result.getId());				
				addRecord.setAttribute("name", result.getName());
				addRecord.setAttribute("description", result.getDescription());
				addRecord.setAttribute("retentionId", result.getRetentionPeriod() + "," + result.getRetentionId());
				addRecord.setAttribute("retentionName", rOpts.get(result.getRetentionPeriod() + "," + result.getRetentionId()));
				addRecord.setAttribute("eclassid", result.getElementClassId());
				addRecord.setAttribute("cclassid", result.getContentClassId());
				addRecord.setAttribute("indexid", result.getIndexId());
				addRecord.setAttribute("uclassid", result.getUserClassId());
				addRecord.setAttribute("templateId", result.getTemplateId());
				addRecord.setAttribute("templateName",  tOpts.get(result.getTemplateId()));
				addRecord.setAttribute("lifecycleId", result.getLifecycleId());
				addRecord.setAttribute("lifecycleName", lOpts.get(result.getLifecycleId()));
				addRecord.setAttribute("versionControl", result.getVersionControl());
				addRecord.setAttribute("deadLine", result.getDeadLine());
				addRecord.setAttribute("filterIds", tmpFilterIds);
				addRecord.setAttribute("rewriteCmd", tmpRewritecmds);
				addRecord.setAttribute("rewriteGroup", tmpRewriteGroups);
				addRecord.setAttribute("rewriteGroupIdPath", tmpRewriteGroupIdPath);
				addRecord.setAttribute("rewriteGroupPath", tmpRewriteGroupPath);
				addRecord.setAttribute("rewriteExpire", result.getRewriteExpire());
				// TODO: extended
				addRecord.setAttribute("extended", getAttributesAsRecordArray(result));
				grid.addData(addRecord);
				
				int selectedRowNum = grid.getRecordIndex(addRecord);						
				grid.selectSingleRecord(selectedRowNum);
				grid.scrollToRow(selectedRowNum);						
				form.reset();
            	form.editRecord(addRecord);
            	form.getItem("id").setTooltip(I18N.message("fieldisreadonly", form.getItem("id").getTitle()));
            	SC.say(I18N.message("savecompleted"));
			}
		});
	}
	
	private void executeUpdate() {
		Log.debug("[ DocumentTypePanel executeUpdate ]");
		
		SDocType docType = new SDocType();
		docType.setId(Long.parseLong(form.getValueAsString("id")));
		docType.setName(form.getValueAsString("name"));
		docType.setDescription(form.getValueAsString("description"));

		String key = form.getValueAsString("retentionId");
		docType.setRetentionId(Long.parseLong(key.substring(key.indexOf(",") + 1)));
//		docType.setRetentionId(Long.parseLong(form.getValueAsString("retentionId")));
		
		docType.setElementClassId(form.getValueAsString("eclassid"));
		docType.setContentClassId(form.getValueAsString("cclassid"));
		docType.setIndexId(form.getValueAsString("indexid"));
		docType.setUserClassId(form.getValueAsString("uclassid"));
		if(form.getValue("templateId")!= null&& !"0".equals(form.getValue("templateId"))){
			docType.setTemplateId(Long.parseLong(form.getValueAsString("templateId")));
		}else{
			docType.setTemplateId(0L);
		}
		if(form.getValue("lifecycleId")!= null&& !"0".equals(form.getValue("lifecycleId")))docType.setLifecycleId(Long.parseLong(form.getValueAsString("lifecycleId")));
		docType.setVersionControl("true".equals(form.getValueAsString("versionControl"))||"1".equals(form.getValueAsString("versionControl"))?1:0);
		if(form.getValue("deadLine")!= null)docType.setDeadLine(Integer.parseInt(form.getValueAsString("deadLine")));
		
		ListGridRecord[] rclist1 = grid1.getSelectedRecords();
		int cmdCnt1 = rclist1.length;
		tmpFilterIds = "";
		if(cmdCnt1>0){
			for(int i=0; i<cmdCnt1; i++){
				tmpFilterIds += "," + rclist1[i].getAttribute("filterId");
			}
			tmpFilterIds = tmpFilterIds.substring(1);
		}	
		
		ListGridRecord[] rclist = grid2.getSelectedRecords();
		int cmdCnt = rclist.length;
		tmpRewritecmds = "";
		if(cmdCnt>0){
			for(int i=0; i<cmdCnt; i++){
				tmpRewritecmds += "," + rclist[i].getAttribute("rewritecmdId");
			}
			tmpRewritecmds = tmpRewritecmds.substring(1);
		}		
		
		RecordList recordList = departmentGridPanel.getRecordList();
		int grpCnt = recordList.getLength();
		
		tmpRewriteGroups = "";
		tmpRewriteGroupIdPath = "";
		tmpRewriteGroupPath = "";
		
		if(grpCnt>0){
			for(int j=0; j<grpCnt; j++) {
				tmpRewriteGroups += ","+ recordList.get(j).getAttributeAsString("id");
				tmpRewriteGroupIdPath += ","+ recordList.get(j).getAttributeAsString("IDPath");
				tmpRewriteGroupPath += ","+ recordList.get(j).getAttributeAsString("path");
			}
			tmpRewriteGroups = tmpRewriteGroups.substring(1);
			tmpRewriteGroupIdPath = tmpRewriteGroupIdPath.substring(1);
			tmpRewriteGroupPath = tmpRewriteGroupPath.substring(1);
			
			docType.setRewriteCmd(tmpRewritecmds); // 명령 순서 
			docType.setRewriteGroup(tmpRewriteGroups); // 그룹 아이디

		} else {
//			Log.error(I18N.message("second.validation"), I18N.message("invalidValueExist1", I18N.message("group")), true);
//			return;
		}
		
		docType.setFilterIds(tmpFilterIds);
		if(form1.getValue("rewriteExpire")!= null)docType.setRewriteExpire(Integer.parseInt(form1.getValueAsString("rewriteExpire")));
		
		// extended
		setAttributes(docType,multiValueItem.getData());
		
		ServiceUtil.documentcode().saveDocType(Session.get().getSid(), docType, new AsyncCallbackWithStatus<SDocType>() {
			@Override
			public String getSuccessMessage() {
//				return I18N.message("operationcompleted");
				// kimsoeun GS인증용  - 작업 완료 -> 저장 완료 문구 변경
				return I18N.message("savecompleted");
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
			public void onSuccessEvent(SDocType result) {
				Log.debug("[ DocumentTypePanel executeUpdate ] onSuccess. id["+result.getId()+"]");
				
				final int selectedRowNum = grid.getRecordIndex(grid.getSelectedRecord());				
				ListGridRecord selectedRecord = grid.getSelectedRecord();
				selectedRecord.setAttribute("name", result.getName());
				selectedRecord.setAttribute("description", result.getDescription());
				selectedRecord.setAttribute("retentionId", result.getRetentionPeriod() + "," + result.getRetentionId());
				selectedRecord.setAttribute("retentionName", rOpts.get(result.getRetentionPeriod() + "," + result.getRetentionId()));
				selectedRecord.setAttribute("eclassid", result.getElementClassId());
				selectedRecord.setAttribute("cclassid", result.getContentClassId());
				selectedRecord.setAttribute("indexid", result.getIndexId());
				selectedRecord.setAttribute("uclassid", result.getUserClassId());
				selectedRecord.setAttribute("templateId", result.getTemplateId());
				selectedRecord.setAttribute("templateName",  ("0".equals(result.getTemplateId()))?"":tOpts.get(result.getTemplateId()));
				selectedRecord.setAttribute("lifecycleId", result.getLifecycleId());
				selectedRecord.setAttribute("lifecycleName", ("0".equals(result.getLifecycleId()))?"":lOpts.get(result.getLifecycleId()));
				selectedRecord.setAttribute("versionControl", result.getVersionControl());
				selectedRecord.setAttribute("deadLine", result.getDeadLine());
				selectedRecord.setAttribute("filterIds", tmpFilterIds);
				selectedRecord.setAttribute("rewriteCmd", tmpRewritecmds);
				selectedRecord.setAttribute("rewriteGroup", tmpRewriteGroups);
				selectedRecord.setAttribute("rewriteGroupIdPath", tmpRewriteGroupIdPath);
				selectedRecord.setAttribute("rewriteGroupPath", tmpRewriteGroupPath);
				selectedRecord.setAttribute("rewriteExpire", result.getRewriteExpire());
				// TODO: extended
				selectedRecord.setAttribute("extended", getAttributesAsRecordArray(result));
				
				grid.getDataAsRecordList().set(selectedRowNum, selectedRecord);				
				grid.selectSingleRecord(selectedRowNum);
				grid.scrollToRow(selectedRowNum);
//				SC.say(I18N.message("operationcompleted"));			
				// kimsoeun GS인증용  - 작업 완료 -> 저장 완료 문구 변경
				SC.say(I18N.message("savecompleted"));	
			}
		});
	}	
	
	private void executeRemove(final long id)
	{
		Log.debug("[ DocumentTypePanel executeRemove ] id["+id+"]");
		if(id < 0) return;
		
		ServiceUtil.documentcode().deleteDocType(Session.get().getSid(), id, new AsyncCallbackWithStatus<Void>() {
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
				Log.debug("[ DocumentTypePanel executeRemove ] onSuccess. id["+id+"]");
				grid.removeSelectedData();
				form.editNewRecord();
            	form.reset();
				form.getItem("id").setTooltip(I18N.message("generatedbyserver", form.getItem("id").getTitle()));
				form1.reset();
				grid2.deselectAllRecords();		    	
		    	departmentGridPanel.resetGrid1();
				SC.say(I18N.message("operationcompleted"));
			}
		});
	}
	
	private void executeGetRetentionAndSet() {		
		DocumentCodeServiceAsync documentCodeService = (DocumentCodeServiceAsync) GWT.create(DocumentCodeService.class);
        documentCodeService.listRetentionProfilesLikeName(Session.get().getSid(), "", new AsyncCallbackWithStatus<List<SRetentionProfile>>() {
        	@Override
			public String getSuccessMessage() {
				return I18N.message("client.searchComplete");
			}
			@Override
			public String getProcessMessage() {
				return I18N.message("client.searchRequest");
			}
			@Override
			public void onSuccessEvent(List<SRetentionProfile> result) {
				if( result.size() > 0) {
					for(int j=0; j<result.size(); j++) {
						rOpts.put(result.get(j).getRetention() + "," + result.get(j).getId(), result.get(j).getName() + " (" + result.get(j).getRetention() + ")");
					}
				}
				retentionIdItem.setValueMap(rOpts);
			}			
			@Override
			public void onFailureEvent(Throwable caught) {
				SCM.warn(caught);
			}
		});
	}
	
	private void executeGetElementClassIdsAndSet() {		
		DocumentCodeServiceAsync documentCodeService = (DocumentCodeServiceAsync) GWT.create(DocumentCodeService.class);
        documentCodeService.listXvarmElementClassIds(Session.get().getSid(), new AsyncCallbackWithStatus<List<String>>() {
        	@Override
			public String getSuccessMessage() {
				return I18N.message("client.searchComplete");
			}
			@Override
			public String getProcessMessage() {
				return I18N.message("client.searchRequest");
			}
			@Override
			public void onSuccessEvent(List<String> result) {
				if( result.size() > 0) {
					for(int j=0; j<result.size(); j++) {
						eOpts.put(result.get(j), result.get(j));
					}
				}
				
				String config = Session.get().getInfo().getConfig("setting.ecm.eclassid");

				if(!config.equals("")){
					eclassidItem.setDefaultValue(config);
					eclassidItem.disable();
				}
				eclassidItem.setValueMap(eOpts);
			}			
			@Override
			public void onFailureEvent(Throwable caught) {
				SCM.warn(caught);
			}
		});
	}
	
	private void executeGetContentClassIdsAndSet() {
		DocumentCodeServiceAsync documentCodeService = (DocumentCodeServiceAsync) GWT.create(DocumentCodeService.class);
		 documentCodeService.listXvarmContentClassIds(Session.get().getSid(), new AsyncCallbackWithStatus<List<String>>() {
			 @Override
				public String getSuccessMessage() {
					return I18N.message("client.searchComplete");
				}
				@Override
				public String getProcessMessage() {
					return I18N.message("client.searchRequest");
				}
			@Override
			public void onSuccessEvent(List<String> result) {
				if( result.size() > 0) {
					for(int j=0; j<result.size(); j++) {
						cOpts.put(result.get(j), result.get(j));
					}
				}
				
				String config = Session.get().getInfo().getConfig("setting.ecm.cclassid");
				
				if(!config.equals("")){
					cclassidItem.setDefaultValue(config);
					cclassidItem.disable();
				}
				else cclassidItem.setValueMap(cOpts);
			}			
			@Override
			public void onFailureEvent(Throwable caught) {
				SCM.warn(caught);
			}
		});
	}
	
	private void executeGetUserClassIdsAndSet() {		
		DocumentCodeServiceAsync documentCodeService = (DocumentCodeServiceAsync) GWT.create(DocumentCodeService.class);
        documentCodeService.listXvarmUserClassIds(Session.get().getSid(), new AsyncCallbackWithStatus<List<String>>() {
        	@Override
			public String getSuccessMessage() {
				return I18N.message("client.searchComplete");
			}
			@Override
			public String getProcessMessage() {
				return I18N.message("client.searchRequest");
			}
			@Override
			public void onSuccessEvent(List<String> result) {
				if( result.size() > 0) {
					for(int j=0; j<result.size(); j++) {
						uOpts.put(result.get(j), result.get(j));
					}
				}

				String config = Session.get().getInfo().getConfig("setting.ecm.uclassid");
				
				if(!config.equals("")){
					uclassidItem.setDefaultValue(config);
					uclassidItem.disable();
				}
				else uclassidItem.setValueMap(uOpts);
			}			
			@Override
			public void onFailureEvent(Throwable caught) {
				SCM.warn(caught);
			}
		});
	}
	
	private void executeGetIndexIdsAndSet() {		
		DocumentCodeServiceAsync documentCodeService = (DocumentCodeServiceAsync) GWT.create(DocumentCodeService.class);
        documentCodeService.listXvarmIndexIds(Session.get().getSid(), new AsyncCallbackWithStatus<List<String>>() {
        	@Override
			public String getSuccessMessage() {
				return I18N.message("client.searchComplete");
			}
			@Override
			public String getProcessMessage() {
				return I18N.message("client.searchRequest");
			}
			@Override
			public void onSuccessEvent(List<String> result) {
				
				String config = Session.get().getInfo().getConfig("setting.ecm.indexid");
				if(!config.equals("")){
					indexidItem.setDefaultValue(I18N.message("second.choosenot"));
					indexidItem.disable();
				}
				else{
					iOpts.put("", I18N.message("second.choosenot"));
					if( result.size() > 0) {
						for(int j=0; j<result.size(); j++) {
							iOpts.put(result.get(j), result.get(j));
						}
					}
					indexidItem.setValueMap(iOpts);
				}
			}			
			@Override
			public void onFailureEvent(Throwable caught) {
				SCM.warn(caught);
			}
		});
	}
		
	
	private void executeGetLifeCycleIdsAndSet() {		
		LifeCycleServiceAsync lifeCycleservice = (LifeCycleServiceAsync) GWT.create(LifeCycleService.class);
		
		lifeCycleservice.getProfiles(Session.get().getSid(), new AsyncCallback<SLifeCycle[]>() {
			
			@Override
			public void onSuccess(SLifeCycle[] result) {
				lOpts.put(0L, I18N.message("second.choosenot"));
				if( result.length > 0) {
					for(int j=0; j<result.length; j++) {
						lOpts.put(result[j].getId(), result[j].getName());
					}
				}
				lifecycleIdItem.setValueMap(lOpts);
			}			
			@Override
			public void onFailure(Throwable caught) {
				SCM.warn(caught);
				
			}
		});
	}
		
	private void executeGetTemplateIdsAndSet() {		
		TemplateServiceAsync tesmplateService = (TemplateServiceAsync) GWT.create(TemplateService.class);
		tesmplateService.getTemplates(Session.get().getSid(), new AsyncCallback<STemplate[]>() {
			
			@Override
			public void onSuccess(STemplate[] result) {
				tOpts.put(0L, I18N.message("second.choosenot"));
				if( result.length > 0) {
					for(int j=0; j<result.length; j++) {
						tOpts.put(result[j].getId(), result[j].getName());
					}
				}
				templateIdItem.setValueMap(tOpts);
			}			
			@Override
			public void onFailure(Throwable caught) {
				SCM.warn(caught);
				
			}
		});
	}
	
	private void executeGetOptionsFilterIdSet() {		
		FilterServiceAsync filterService = (FilterServiceAsync) GWT.create(FilterService.class);
		filterService.listFilter(Session.get().getSid(), new AsyncCallbackWithStatus<List<SFilter>>() {
//		documentCodeService.listCodes(Session.get().getSid(), "APP_SET_COMMAND", new AsyncCallbackWithStatus<List<SCode>>() {
			@Override
			public String getSuccessMessage() {
				return I18N.message("client.searchComplete");
			}
			@Override
			public String getProcessMessage() {
				return I18N.message("client.searchRequest");
			}
			@Override
			public void onSuccessEvent(List<SFilter> result) {
				if( result.size() > 0) {
					for(int j=0; j<result.size(); j++) {
						ListGridRecord record =new ListGridRecord();
						record.setAttribute("filterId", result.get(j).getId());
						record.setAttribute("filterName", result.get(j).getName());
						grid1.addData(record);
					}
				}
			}
			@Override
			public void onFailureEvent(Throwable caught) {
				SCM.warn(caught);
			}
		});
	}
	
	private void executeGetOptionsAppSetCommandSet() {		
		DocumentCodeServiceAsync documentCodeService = (DocumentCodeServiceAsync) GWT.create(DocumentCodeService.class);
		documentCodeService.listCodes(Session.get().getSid(), "APP_SET_COMMAND", new AsyncCallbackWithStatus<List<SCode>>() {
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
						ListGridRecord record =new ListGridRecord();
						record.setAttribute("rewritecmdId", result.get(j).getValue());
						record.setAttribute("rewritecmdName", I18N.message("event."+result.get(j).getName()));
						grid2.addData(record);
					}
				}
			}			
			@Override
			public void onFailureEvent(Throwable caught) {
				SCM.warn(caught);
			}
		});
	}
	
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
}