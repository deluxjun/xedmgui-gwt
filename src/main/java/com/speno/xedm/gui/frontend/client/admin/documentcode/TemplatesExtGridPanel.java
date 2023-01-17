package com.speno.xedm.gui.frontend.client.admin.documentcode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.RecordList;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.DragDataAction;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.TransferImgButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.CheckboxItem;
import com.smartgwt.client.widgets.form.fields.DateItem;
import com.smartgwt.client.widgets.form.fields.HiddenItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.RecordClickEvent;
import com.smartgwt.client.widgets.grid.events.RecordClickHandler;
import com.smartgwt.client.widgets.grid.events.RemoveRecordClickEvent;
import com.smartgwt.client.widgets.grid.events.RemoveRecordClickHandler;
import com.smartgwt.client.widgets.grid.events.SelectionChangedHandler;
import com.smartgwt.client.widgets.grid.events.SelectionEvent;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.speno.xedm.core.service.serials.SCode;
import com.speno.xedm.core.service.serials.SExtendedAttribute;
import com.speno.xedm.core.service.serials.STemplate;
import com.speno.xedm.gui.common.client.Constants;
import com.speno.xedm.gui.common.client.I18N;
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
 * TemplatesExtGrid Panel
 * 
 * @author 남윤성
 * @since 1.0
 */
public class TemplatesExtGridPanel extends VLayout {	
	private static HashMap<String, TemplatesExtGridPanel> instanceMap = new HashMap<String, TemplatesExtGridPanel>();
	
	private ListGrid grid;
	private DynamicForm form1;
	private DynamicForm form2;
	private SExtendedAttribute[] attributes ;
	private SelectItem typenameItem;
	private SelectItem editorItem;
	private TextItem valueItem, sizeItem, descriptionItem;
	private DateItem valueDateItem;
	private LinkedHashMap<String, String> typeNameOpts = new LinkedHashMap<String, String>() ;
	private LinkedHashMap<String, String> editorOpts = new LinkedHashMap<String, String>() ;
	private  boolean isAddNew = false; 
	private TransferImgButton upArrow, downArrow;
	private HLayout actHL;
	private VLayout arrowPanel;
	private String Message;
	
	CheckboxItem isRelativeDate;
	TextItem relativeCount;

	private IsNotChangedValidator validtor1 = new IsNotChangedValidator();
	private IsNotChangedValidator validtor2 = new IsNotChangedValidator();
	
	private Button btnUpload;
	
	/**
	 * 기 생성된 instance를 반환함. instance가 없을 경우 생성 후 반환함
	 * @param id
	 * @param subTitle
	 * @param dragSourceGrid
	 * @return
	 */
	public static TemplatesExtGridPanel get(
			final String id, 
			final String subTitle, 
			final ListGrid dragSourceGrid) {
		return get(id, subTitle, dragSourceGrid, false, "100%");		
	}
	
	/**
	 * 기 생성된 instance를 반환함. instance가 없을 경우 생성 후 반환함
	 * @param id
	 * @param subTitle
	 * @param dragSourceGrid
	 * @param isAction
	 * @param width
	 * @return
	 */
	public static TemplatesExtGridPanel get(
			final String id, 
			final String subTitle, 
			final ListGrid dragSourceGrid, 
			boolean isAction, 
			final String width) {
		if (instanceMap.get(id) == null) {
			new TemplatesExtGridPanel(id, subTitle, dragSourceGrid, isAction, width);
		}
		return instanceMap.get(id);
	} 
	
	public TemplatesExtGridPanel(
			final String id, 
			final String subTitle, 
			final ListGrid dragSourceGrid) {
		this(id, subTitle, dragSourceGrid, false, "100%");
	}
	
	public TemplatesExtGridPanel(
			final String id, 
			final String subTitle, 
			final ListGrid dragSourceGrid, 
			final boolean isAction, 
			final String width) {		
		instanceMap.put(id, this);
		
		/* Sub Title 생성 */
		Label subTitleLable = new Label();
        subTitleLable.setAutoHeight();   
        subTitleLable.setAlign(Alignment.LEFT);   
        subTitleLable.setValign(VerticalAlignment.CENTER);
        subTitleLable.setStyleName("subTitle");
        subTitleLable.setContents(subTitle);
                
        //grid 생성
        grid = new ListGrid();
        grid.setMargin(10);
        grid.setWidth("30%");
        grid.setHeight("210");
        grid.setShowAllRecords(true);
        
        grid.setCanReorderRecords(true);
        grid.setCanDragRecordsOut(true);
        grid.setCanAcceptDroppedRecords(true);
        grid.setDragDataAction(DragDataAction.MOVE);
        
        grid.setEmptyMessage(I18N.message("notitemstoshow"));
        grid.setCanReorderFields(true);
        grid.setCanRemoveRecords(true);        
        grid.setPreventDuplicates(true);
        
		grid.setSelectionType(SelectionStyle.SINGLE);
        grid.setDuplicateDragMessage(I18N.message("dupmessage"));
        grid.invalidateCache();

        ListGridField checkField = new ListGridField("check",I18N.message("check"));
        ListGridField priorityField = new ListGridField("priority",I18N.message("priority"));
        ListGridField itemplateIdField = new ListGridField("templateId",  I18N.message("id"));
        ListGridField nameField = new ListGridField("attributeename", I18N.message("name"));
        ListGridField labelField = new ListGridField("label", I18N.message("label"));
        ListGridField mandatoryField = new ListGridField("mandatory", I18N.message("mandatory"));
        ListGridField typenameField = new ListGridField("typename", I18N.message("typename"));
        ListGridField editorField = new ListGridField("editor", I18N.message("second.editor"));
        ListGridField valueField = new ListGridField("value", I18N.message("value"));
        ListGridField descriptionFiled = new ListGridField("decription",I18N.message("description"));
        ListGridField sizeField = new ListGridField("size", I18N.message("size"));
        
        itemplateIdField.setHidden(true);
        labelField.setHidden(false);
        mandatoryField.setHidden(true);
        typenameField.setHidden(true);
        editorField.setHidden(true);
        valueField.setHidden(true);
        descriptionFiled.setHidden(true);
        sizeField.setHidden(true);
        
        checkField.setType(ListGridFieldType.BOOLEAN);
        checkField.setCanEdit(true);
        checkField.setPrompt(I18N.message("ifyoucheckthefield"));
        priorityField.setWidth(60);
        priorityField.setAlign(Alignment.CENTER);
        priorityField.setHidden(true);

        grid.setFields(checkField, priorityField, itemplateIdField, nameField, labelField, mandatoryField, typenameField, editorField, valueField, descriptionFiled, sizeField);
        grid.setCanResizeFields(true);
        
        grid.addSelectionChangedHandler(new SelectionChangedHandler() {
			@Override
			public void onSelectionChanged(SelectionEvent event) {
				if(grid.getSelectedRecord() == null){
					controlExtSaveBtn(true);
				}else{
					controlExtSaveBtn(false);
					Record rc = event.getRecord();
					
					int type = Integer.parseInt(rc.getAttributeAsString("typename"));
					form2.getField("attributeename").setValue(rc.getAttributeAsString("attributeename"));
					form2.getField("label").setValue(rc.getAttributeAsString("label"));
					form2.getField("mandatory").setValue(rc.getAttributeAsBoolean("mandatory"));
					form2.getField("typename").setValue(rc.getAttributeAsString("typename"));
					form2.getField("editor").setValue(rc.getAttributeAsString("editor"));
//					form2.getField("value").setValue(rc.getAttributeAsString("value"));
					
					if (SExtendedAttribute.TYPE_DATE == type) {
						form2.getField("value").setValue(rc.getAttributeAsString("value"));
						form2.getField("valueDate").setValue(rc.getAttributeAsDate("valueDate"));
						form2.getField("relativeCount").setValue(rc.getAttributeAsString("relativeCount"));
					} else {
						form2.getField("value").setValue(rc.getAttributeAsString("value"));
					}
					form2.getField("description").setValue(rc.getAttributeAsString("description"));
					form2.getField("size").setValue(rc.getAttributeAsString("size"));
					

					chgingItem();
					
					validtor1.setMap(form1);
					validtor2.setMap(grid);
				}
			}
		});
        
        //20131227na 체크버튼을 누르면 우선순위를 결정
        checkField.addRecordClickHandler(new RecordClickHandler() {
			@Override
			public void onRecordClick(RecordClickEvent event) {
				ListGridRecord record = event.getRecord();
				boolean flag = record.getAttributeAsBoolean("check");
				record.setAttribute("check", !flag);
				reflash();
				event.cancel();
			}
		});
        
      //record 삭제 event handler 정의--------------------------------------------------------------
		grid.addRemoveRecordClickHandler(new RemoveRecordClickHandler() {
			@Override
			public void onRemoveRecordClick(RemoveRecordClickEvent event) {
//				final ListGridRecord record = grid.getRecord( event.getRowNum());
				System.out.println(event.getRowNum());
			}
		});		
		        
        HLayout teamGridPanel = new HLayout(0);
        
        teamGridPanel.setMembers(createFormVL());
        
        setMembersMargin(Constants.SUBTITLE_MARGIN);
        setMembers(teamGridPanel);
        setWidth("100%");  
        setAutoHeight();  
        
//        form2.getField("value").show();
//		form2.getField("valueDate").hide();
        toggleDate(false, false);
	}
	
	/**
	 * 하단 상세 Form,Grid 생성
	 * @return
	 */
	private HLayout createFormVL() {
		HiddenItem templateidItem = new HiddenItem("templateid");		
		
		TextItem nmItem = new TextItem("name", I18N.message("name"));
		nmItem.setRequired(true);		
		// kimsoeun GS인증용 - 툴팁 다국어화
		nmItem.setRequiredMessage(I18N.message("fieldisrequired"));
		nmItem.setCanEdit(true);
		nmItem.setWrapTitle(false);
//		nmItem.setLength(Constants.MAX_LEN_NAME);
		nmItem.setValidators(new LengthValidator(nmItem, Constants.MAX_LEN_NAME));
		
		TextItem descItem = new TextItem("description", I18N.message("description"));
		descItem.setCanEdit(true);		
		descItem.setWrapTitle(false);
//		descItem.setLength(Constants.MAX_LEN_DESC);
		descItem.setValidators(new LengthValidator(descItem, Constants.MAX_LEN_DESC));
		
		TextItem ruleItem = new TextItem("ruleScript", I18N.message("ruleScript"));
		ruleItem.setCanEdit(true);		
		ruleItem.setWrapTitle(false);
		ruleItem.setValidators(new LengthValidator(ruleItem, Constants.MAX_LEN_NAME));
		ruleItem.setHoverWidth(250);
		ruleItem.setTooltip(I18N.message("ruleScriptDescription"));
				
		Label subTitleLabel = new Label();
		subTitleLabel.setAutoHeight();   
		subTitleLabel.setAlign(Alignment.LEFT);   
		subTitleLabel.setValign(VerticalAlignment.CENTER);
		subTitleLabel.setStyleName("subTitle");
		subTitleLabel.setContents(I18N.message("taskmanagement"));		
		
		
		nmItem.setWidth("*");        
		descItem.setWidth("*");          
		ruleItem.setWidth("*");    
		                                                                    
		templateidItem.setStartRow(false);		templateidItem.setEndRow(false);
		nmItem.setStartRow(false);				nmItem.setEndRow(false);	
		descItem.setStartRow(false);			descItem.setEndRow(false);	
		ruleItem.setStartRow(false);			ruleItem.setEndRow(false);	

		form1 = new DynamicForm();
		form1.setWidth("40%");
		form1.setMargin(4);
		form1.setItems(templateidItem, nmItem, descItem, ruleItem);
		form1.reset();
    	
		TextItem attributeNmItem = new TextItem("attributeename", I18N.message("second.attributename"));
		attributeNmItem.setRequired(true);	
		// kimsoeun GS인증용 - 툴팁 다국어화
		attributeNmItem.setRequiredMessage(I18N.message("fieldisrequired"));
		attributeNmItem.setCanEdit(true);
		attributeNmItem.setWrapTitle(false);
//		attributeNmItem.setLength(Constants.MAX_LEN_NAME);
		attributeNmItem.setValidators(new LengthValidator(attributeNmItem, Constants.MAX_LEN_NAME));
		
		TextItem labelItem = new TextItem("label", I18N.message("label"));
		labelItem.setRequired(true);	
		// kimsoeun GS인증용 - 툴팁 다국어화
		labelItem.setRequiredMessage(I18N.message("fieldisrequired"));
		labelItem.setCanEdit(true);
		labelItem.setWrapTitle(false);
//		labelItem.setLength(Constants.MAX_LEN_NAME);
		labelItem.setValidators(new LengthValidator(labelItem, Constants.MAX_LEN_NAME));
		
		CheckboxItem mandatoryCb = new CheckboxItem("mandatory",I18N.message("mandatory"));
		
		typenameItem = new SelectItem("typename", I18N.message("typename"));
		typenameItem.setType("combobox");
		typenameItem.setWrapTitle(false);
		typenameItem.setRequired(true);
		// kimsoeun GS인증용 - 툴팁 다국어화
		typenameItem.setRequiredMessage(I18N.message("fieldisrequired"));
		typenameItem.setDefaultValues(0);
		typenameItem.addChangedHandler(new ChangedHandler(){
			@Override
			public void onChanged(ChangedEvent event) {
				chgingItem();
			}
		});
		executeGetOptionsTypeNameSet();

		editorItem = new SelectItem("editor", I18N.message("second.editor"));
		editorItem.setType("combobox");
		editorItem.setWrapTitle(false);
		editorItem.setRequired(true);
		// kimsoeun GS인증용 - 툴팁 다국어화
		editorItem.setRequiredMessage(I18N.message("fieldisrequired"));
		editorItem.setDefaultValues(0);
		
		editorItem.addChangedHandler(new ChangedHandler() {
			@Override
			public void onChanged(ChangedEvent event) {	
				chgingItem();
			}
		});
        
		executeGetOptionsEditorSet();
		
		valueItem = new TextItem("value", I18N.message("value"));
		valueItem.setRequired(false);		
		valueItem.setCanEdit(true);
		valueItem.setWrapTitle(false);
		
		descriptionItem = new TextItem("description", I18N.message("description"));
		descriptionItem.setRequired(false);		
		descriptionItem.setCanEdit(true);
		descriptionItem.setWrapTitle(false);

		sizeItem = new TextItem("size", I18N.message("size"));
		sizeItem.setRequired(false);		
		sizeItem.setCanEdit(true);
		sizeItem.setWrapTitle(false);
		
		// date 데이터 형
		valueDateItem = new DateItem("valueDate", I18N.message("value"));
		valueDateItem.setWrapTitle(false);
		valueDateItem.setRequired(false);
		
		isRelativeDate = new CheckboxItem("isRelativeDate",I18N.message("relative"));
		isRelativeDate.addChangedHandler(new ChangedHandler() {
			@Override
			public void onChanged(ChangedEvent event) {
				if(isRelativeDate.getValueAsBoolean()){
					toggleDate(true, true);
				} else {
					toggleDate(true, false);
				}
			}
		});
		relativeCount = new TextItem("relativeCount", I18N.message("value"));
		relativeCount.setRequired(false);		
		relativeCount.setCanEdit(true);
		relativeCount.setWrapTitle(false);
		relativeCount.setWidth(50);
		relativeCount.setHint(I18N.message("relativeDaysFromToday"));
		
		HiddenItem testitem = new HiddenItem("templateid");
		testitem.setShowDisabled(false);
		
		
		attributeNmItem.setWidth("*");    
		labelItem.setWidth("*");  
		mandatoryCb.setWidth("*");          
		typenameItem.setWidth("*");          
		editorItem.setWidth("*");	
		valueItem.setWidth("*");
		descriptionItem.setWidth("*");
		sizeItem.setWidth("*");
		                                                                    
		attributeNmItem.setStartRow(false);		attributeNmItem.setEndRow(false);
		labelItem.setStartRow(false);		labelItem.setEndRow(false);
		mandatoryCb.setStartRow(false);		mandatoryCb.setEndRow(false);
		typenameItem.setStartRow(false);			typenameItem.setEndRow(false);	
		editorItem.setStartRow(false);			editorItem.setEndRow(false);
		valueItem.setStartRow(false);			valueItem.setEndRow(false);
		descriptionItem.setStartRow(false);			descriptionItem.setEndRow(false);
		sizeItem.setStartRow(false);			sizeItem.setEndRow(false);

		form2 = new DynamicForm();
		form2.setWidth100();
		form2.setMargin(4);
		form2.setItems(attributeNmItem, labelItem, mandatoryCb, typenameItem,editorItem,valueItem,descriptionItem, sizeItem,valueDateItem, relativeCount, isRelativeDate);
		form2.reset();
		
		VLayout formButtonVL = new VLayout(10);
		formButtonVL.setAlign(Alignment.RIGHT);
		formButtonVL.setWidth("25%");
		formButtonVL.setHeight(270);
		
		formButtonVL.addMembers(form2,createActHL());
		
    	HLayout rightFormHL = new HLayout(10);
    	rightFormHL.setBorder("1px solid gray");
    	rightFormHL.setMargin(10);
    	rightFormHL.setWidth100();
    	rightFormHL.setHeight100();
    	rightFormHL.addMembers(grid,createArrowVL(),formButtonVL);
    	
    	HLayout allFormHL = new HLayout(10);
    	allFormHL.setBorder("1px solid gray");
    	allFormHL.setWidth100();
    	allFormHL.setHeight100();
    	
    	allFormHL.addMembers(form1,rightFormHL);
    	
    	return allFormHL;
	}
	
	/**
	 * grid 가져오기
	 */
	public ListGrid getGrid() {
		return grid;
	}
	
	/**
	 * form1 가져오기
	 */
	public DynamicForm getForm1() {
		return form1;
	}
	
	/**
	 * form2 가져오기
	 */
	public DynamicForm getForm2() {
		return form2;
	}
	
	/**
	 * actHL 가져오기
	 */
	public HLayout  getActHL(){
		return actHL;
	}
	
	/**
	 * arrowPanel 가져오기
	 */
	public VLayout getArrowPanel(){
		return arrowPanel;
	}
	
	/**
	 * Form2 초기화
	 */
	public void resetGridForm2(String param){
		if(param.equals("all")){
			grid.setData(new ListGridRecord[0]); //초기화
		}
		isAddNew = true;
    	form2.editNewRecord();
    	form2.reset();
    	typenameItem.setDisabled(false);
    	editorItem.setDisabled(false);
    	sizeItem.show();
    	
    	chgingItem();
//		toggleDate(false, false);
//    	valueItem.show();
//		valueDateItem.hide();
	}
	
	/**
	 * Item, 속성변경
	 */
	public void chgingItem(){
		// 초기화
		editorItem.setDisabled(false);
		
		if(Integer.toString(SExtendedAttribute.EDITOR_LISTBOX).equals(editorItem.getValueAsString())){//editor 가 listbox(1) 이면은 value 는 필수 이고 typename은 string(0) 으로 변경
			typenameItem.setValue(Integer.toString(SExtendedAttribute.TYPE_STRING));
			typenameItem.setDisabled(true);
			valueItem.setRequired(true);
			descriptionItem.setVisible(true);
			sizeItem.setDisabled(true);
			sizeItem.setRequired(false);
		} else{
			typenameItem.setDisabled(false);
			descriptionItem.setVisible(false);
			valueItem.setRequired(false);
			sizeItem.setRequired(false);
		}
		
		String tmpTypeName = typenameItem.getValueAsString();
		
		if(Integer.toString(SExtendedAttribute.TYPE_MULTIVALUE).equals(tmpTypeName)){//typename은 multivalue(4) 이면 editor 는 기본으로 세팅하고 disable
			editorItem.setValue("0");
			editorItem.setDisabled(true);
			valueItem.setRequired(false);
			sizeItem.setDisabled(false);
			sizeItem.setRequired(true);
		}


//		valueItem.setLength(Session.get().getInfo().getIntConfig("gui.val.fieldsize", 255));
		valueItem.setValidators(new LengthValidator(valueItem, Session.get().getInfo().getIntConfig("gui.val.fieldsize", 255)));
		if(Integer.toString(SExtendedAttribute.TYPE_DATE).equals(tmpTypeName)){//typename이 date(3)이면 value는 date 피커로 변경
			toggleDate(true);
//			valueItem.hide();
//			valueDateItem.show();
		} else{
			toggleDate(false, false);
//			valueItem.show();
//			valueDateItem.hide();
		}

		// setting size
		relativeCount.setValidators(new LengthValidator(sizeItem, 4));

		sizeItem.setValidators(new LengthValidator(sizeItem, 4));
		
		if (	Integer.toString(SExtendedAttribute.TYPE_STRING).equals(tmpTypeName) ||
				Integer.toString(SExtendedAttribute.TYPE_MULTIVALUE).equals(tmpTypeName)){//String or multivalue
			sizeItem.setDisabled(false);
		} else {
			sizeItem.setDisabled(true);
		}

		
		if(Integer.toString(SExtendedAttribute.TYPE_STRING).equals(tmpTypeName)){//String
//			valueItem.setLength(Constants.MAX_STRINGVALUE);
			valueItem.setValidators(new LengthValidator(valueItem, Constants.MAX_STRINGVALUE));
		}else if("1".equals(tmpTypeName)){//int
//			valueItem.setLength(Constants.MAX_INTVALUE);
			valueItem.setValidators(new LengthValidator(valueItem, Constants.MAX_INTVALUE));
		}else if("2".equals(tmpTypeName)){//double
//			valueItem.setLength(Constants.MAX_DOUBLEVALUE);
			valueItem.setValidators(new LengthValidator(valueItem, Constants.MAX_DOUBLEVALUE));
		}else if(Integer.toString(SExtendedAttribute.TYPE_MULTIVALUE).equals(tmpTypeName)){//double
//			valueItem.setLength(Constants.MAX_STRINGVALUE);
			valueItem.setValidators(new LengthValidator(valueItem, Constants.MAX_STRINGVALUE));
		}
		
		reflash();
	}
	
	private void toggleDate(boolean on, boolean bRelative) {
		if (on) {
			valueItem.hide();
			isRelativeDate.show();
			sizeItem.hide();
			if (!bRelative) {
				valueDateItem.show();
				relativeCount.hide();
				isRelativeDate.setValue(false);
			} else {
				valueDateItem.hide();
				if (relativeCount.getValueAsString() == null)
					relativeCount.setValue("0");
				relativeCount.show();
				isRelativeDate.setValue(true);
			}
		} else {
			valueItem.show();
			sizeItem.show();
			valueDateItem.hide();
			isRelativeDate.hide();
			relativeCount.hide();
		}
	}
	
	private void toggleDate(boolean on) {
		String str = relativeCount.getValueAsString();
		if (str == null) {
			toggleDate(on, false);
		} else {
			toggleDate(on, true);
		}
	}
			
	
	/**
	 * Arrow패널 생성
	 */
	private VLayout createArrowVL() {
		Label label = new Label();  
        label.setHeight("60");  
        label.setAlign(Alignment.CENTER);  
        
		upArrow = new TransferImgButton(TransferImgButton.UP, new ClickHandler() {   
            public void onClick(ClickEvent event) {
            	changeGridRow("up");
            }   
        });   
        
		downArrow = new TransferImgButton(TransferImgButton.DOWN, new ClickHandler() {   
            public void onClick(ClickEvent event) {   
            	changeGridRow("down");  
            }   
        });
		
		upArrow.enable();
		downArrow.enable();
		
		arrowPanel = new VLayout();
        arrowPanel.setWidth("10");
        arrowPanel.setHeight("100%");
        arrowPanel.setAlign(Alignment.CENTER);
        arrowPanel.setMembersMargin(Constants.ARROW_MARGIN);
        arrowPanel.setMembers(label, upArrow, downArrow);
        return arrowPanel;
	}
	
	/**
	 * 그리드 row 데이타 up,down 
	 */
	public void changeGridRow(String arrow){
		if(grid.getDataAsRecordList().getLength() > 0){
			ListGridRecord[] moveRecords = grid.getSelectedRecords();//선택 레코드
	    	int selectRowNum = grid.getRecordIndex(grid.getSelectedRecord());//선택 레코드 rownum	
	    	List<ListGridRecord> temp = new ArrayList<ListGridRecord>();	//new sort 레코드
	    	ListGridRecord[] orgRecords = grid.getRecords();			//original sort 레코드
	    	int totCnt = orgRecords.length;
	    	
	    	if(totCnt ==1)return;
	    	
	    	if("up".equals(arrow)){//up
	    		if(selectRowNum == 0)return;
	    	}
	    	if("down".equals(arrow)){//down
	    		if(selectRowNum == totCnt-1)return;
	    	}
	    	
	    	for(int j=0; j<orgRecords.length; j++) {
				
				if("up".equals(arrow)){//up
					if(j== selectRowNum-1){
						temp.add(moveRecords[0]);//선택 record
					}else if(j == selectRowNum){
						temp.add(orgRecords[selectRowNum-1]);
					}else{
						temp.add(orgRecords[j]);
					}
				}
				
				if("down".equals(arrow)){//down
					if(j == selectRowNum){
						temp.add(orgRecords[selectRowNum+1]);
					}else if(j== selectRowNum+1){
						temp.add(moveRecords[0]);//선택 record
					}else{
						temp.add(orgRecords[j]);
					}
				}
			}
	    	grid.setData(new ListGridRecord[0]); //초기화
	    	for(int i=0; i<temp.size(); i++){
	    		grid.addData((ListGridRecord)temp.get(i));
	    	}			
			grid.selectSingleRecord("up".equals(arrow)?selectRowNum-1:selectRowNum+1);
		}
		
//		reflash();
	}
	
	
	/**
	 * 데이타 존재유무 반환
	 * @return
	 */
	public boolean isExistMember() {
		RecordList recordList = grid.getRecordList(); 
		return (recordList != null && !recordList.isEmpty());
	}
	
	private void controlExtSaveBtn(boolean isNew){
		if(isNew){
			btnUpload.setTitle(I18N.message("apply"));
		}else{
			btnUpload.setTitle(I18N.message("modified"));
		}
	}
	/**
	 * Action Panel(FORM 내) 생성
	 * @return
	 */
	private HLayout createActHL() {		
		Button btnAddNew = new Button(I18N.message("addnew"));
		btnAddNew.setIcon(ItemFactory.newImgIcon("page_white_add.png").getSrc());
		btnAddNew.addClickHandler(new ClickHandler() {   
            public void onClick(ClickEvent event) {
            	resetGridForm2("");
            	grid.deselectAllRecords();
            	controlExtSaveBtn(true);
            }   
        });
		
//		Button btnUpload = new Button(I18N.message("updaterow"));
//		btnUpload.setIcon("[SKIN]/RichTextEditor/paste.png");
//		btnUpload.addClickHandler(new ClickHandler() {   
//            public void onClick(ClickEvent event) {
//            	if(form1.validate()&&form2.validate()) {
//            		if(!checkName3((String)form2.getValue("attributeename"))){//name 중복체크
//                		SC.say(I18N.message("secnod.duplicatemessage", I18N.message("attributeename")));
//                		form2.getField("attributeename").focusInItem();
//    	   				 return;
//    	   			 }else{
//    	   				if(!checkValue()){//value type 체크
//    						 SC.say(I18N.message("secnod.wrongtypevalue", Message));
//    						 form2.getField("value").focusInItem();
//    						 return;
//    					 }
//			            	if(isAddNew|| 0 == grid.getDataAsRecordList().getLength()) {
//			            		executeAdd();
//			            	}
//			            	else {
//			            		executeUpdate();
//			            	}
//    	   			 	}
//    	   			 }
//            	}
//        });
		btnUpload = new Button(I18N.message("modified"));
		btnUpload.setIcon("[SKIN]/RichTextEditor/paste.png");
		btnUpload.addClickHandler(new ClickHandler() {   
            public void onClick(ClickEvent event) {
            	if(grid.getSelectedRecord() != null){
            		executeUpdate();
            	}else{
            		executeAdd();
            	}
            	controlExtSaveBtn(false);
            }
        });
		
		actHL = new HLayout(10);
		actHL.setWidth100();
		actHL.setHeight100();
		actHL.setMargin(10);
		actHL.setAlign(Alignment.LEFT);
//		actHL.setMembers(btnAddNew);
		actHL.setMembers(btnAddNew, btnUpload);			
		return actHL;
	}
	
	/**
	 * 그리드 세팅
	 */
	protected void setGrid(ListGrid bgrid, Record record) {
		
		Log.debug("[ TemplatesExtGridPanel executeFetch ]");
		isAddNew = false;
		
		Long templateid = Long.parseLong(record.getAttributeAsString("id"));
		ServiceUtil.template().getTemplate(Session.get().getSid(), templateid, new AsyncCallbackWithStatus<STemplate>() {
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
			public void onSuccessEvent(STemplate result) {
				
			    attributes = result.getAttributes();
				form1.reset();
				form2.reset();
				grid.setData(new ListGridRecord[0]); //documentTypeGridPanel 그리드 초기화	
				if(null != attributes){
					for (int j = 0; j < attributes.length; j++) {
						ListGridRecord r=new ListGridRecord();
						r.setAttribute("templateId", result.getId());
						r.setAttribute("attributeename", attributes[j].getName());
						r.setAttribute("label", attributes[j].getLabel());
						r.setAttribute("mandatory", attributes[j].isMandatory());
						r.setAttribute("typename", attributes[j].getType());
						r.setAttribute("editor", attributes[j].getEditor());
						r.setAttribute("description", attributes[j].getDescription());
						r.setAttribute("priority", attributes[j].getPriority());
						if(attributes[j].getPriority() != null) r.setAttribute("check", true);
//						r.setAttribute("value", attributes[j].getValue());
						if (SExtendedAttribute.TYPE_DATE==attributes[j].getType()) {
							r.setAttribute("value", attributes[j].getValue());
							r.setAttribute("valueDate", attributes[j].getDateValue());
							r.setAttribute("relativeCount", attributes[j].getIntValue());
						}
						else if (SExtendedAttribute.TYPE_MULTIVALUE==attributes[j].getType()) {
							Object obj = attributes[j].getValue();
							String[] values = null;
							if (obj instanceof String[])
								values = (String[]) obj;
							if (values != null && values.length > 0) {
								StringBuffer sb = new StringBuffer();
								for (String string : values) {
									sb.append(string + ",");
								}
								sb.setLength(sb.length()-1);
								r.setAttribute("value", sb.toString());
							}
						} else {
							r.setAttribute("value", attributes[j].getValue());
						}
							
//						if (SExtendedAttribute.TYPE_DATE!=attributes[j].getType()) {
							r.setAttribute("size", attributes[j].getIntValue());
//						}
						
						grid.addData(r);
						
//						if(j==0){
//							form2.getField("attributeename").setValue(attributes[j].getName());
//							form2.getField("label").setValue(attributes[j].getLabel());
//							form2.getField("mandatory").setValue(attributes[j].isMandatory());
//							form2.getField("typename").setValue(attributes[j].getType());
//							form2.getField("editor").setValue(attributes[j].getEditor());
//							if(SExtendedAttribute.TYPE_DATE==attributes[j].getType()){//date 일때
//								form2.getField("value").setValue(attributes[j].getValue());
//								form2.getField("valueDate").setValue(attributes[j].getDateValue());
//							} else if(SExtendedAttribute.TYPE_MULTIVALUE==attributes[j].getType()){//multivalue 일때
//								Object obj = attributes[j].getValue();
//								String[] values = null;
//								if (obj instanceof String[])
//									values = (String[]) obj;
//								if (values != null && values.length > 0) {
//									StringBuffer sb = new StringBuffer();
//									for (String string : values) {
//										sb.append(string + ",");
//									}
//									sb.setLength(sb.length()-1);
//									form2.getField("value").setValue(sb.toString());
//								}
//								
//							}else{
//								form2.getField("value").setValue(attributes[j].getValue());
//							}
//							
//							if (	SExtendedAttribute.TYPE_STRING==attributes[j].getType() ||
//									SExtendedAttribute.TYPE_MULTIVALUE==attributes[j].getType()){//string or multivalue 일때
//								if (attributes[j].getIntValue() <= 0)
//									form2.getField("size").setValue(4000);
//								else 
//									form2.getField("size").setValue(attributes[j].getIntValue());
//							}
//
//						}
					}
					form1.getField("templateid").setValue(result.getId());
					form1.getField("name").setValue(result.getName());
					form1.getField("description").setValue(result.getDescription());
//					form1.getField("templatedoc").setValue("".equals(result.getDocPath())?"":result.getDocPath()+"/"+result.getDocTitle());
					form1.getField("ruleScript").setValue(result.getRule());
					
					if (attributes.length > 0) {
						grid.selectSingleRecord(0);
						arrowPanel.enable();
						grid.enable();
					}else{
						isAddNew = true;
						arrowPanel.disable();
						form2.editNewRecord();
						form2.reset();
					}
				}else{
					isAddNew = true;
				}
				chgingItem();
				
				validtor1.setMap(form1);
				validtor2.setMap(grid);
			}
		});
		
		
		
	}
	
	/**
	 * typenameItem Select Box 세팅
	 */
	private void executeGetOptionsTypeNameSet() {		
		ServiceUtil.documentcode().listCodes(Session.get().getSid(), "TEMPLATE_TYPE", new AsyncCallbackWithStatus<List<SCode>>() {
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
						typeNameOpts.put(result.get(j).getValue(), result.get(j).getName());
					}
				}
				typenameItem.setValueMap(typeNameOpts);
			}			
			@Override
			public void onFailureEvent(Throwable caught) {
				SCM.warn(caught);
			}
		});
	}
	
	/**
	 * editorItem Select Box 세팅
	 */
	private void executeGetOptionsEditorSet() {		
		ServiceUtil.documentcode().listCodes(Session.get().getSid(), "TEMPLATE_EDITOR", new AsyncCallbackWithStatus<List<SCode>>() {
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
						editorOpts.put(result.get(j).getValue(), result.get(j).getName());
					}
				}
				editorItem.setValueMap(editorOpts);
			}			
			@Override
			public void onFailureEvent(Throwable caught) {
				SCM.warn(caught);
			}
		});
	}
	
	/**
	 * 그리드 레코드 추가
	 */
	private void executeAdd() {
		Log.debug("[ TemplatesExtGridPanel executeAdd ]");

		String typename = typenameItem.getValueAsString();
		Object objValue = form2.getField("value").getValue();

		if(checkTypeError(typename,objValue)) return;
		
		ListGridRecord record = new ListGridRecord();
		
		String strSize = (String)form2.getField("size").getValue();
		int size = 0;
		try {
			size = Integer.parseInt(strSize);
		} catch (Exception e) {
		}
		
		if (	Integer.toString(SExtendedAttribute.TYPE_STRING).equals(typename) ||
				Integer.toString(SExtendedAttribute.TYPE_MULTIVALUE).equals(typename) ){
			// 20140325, junsoo, check size
			boolean ok = Util.isValidSize(null, (String)objValue, size, true, "");
			if (!ok) 
				return;

			record.setAttribute("size", strSize);
		}
		
		// kimsoeun GS인증용 - 템플릿 확장속성 필수값 alert
		if(form2.getField("attributeename").getValue()!=null && 
			form2.getField("label").getValue()!=null &&
			form2.getField("typename").getValue()!=null &&
			form2.getField("editor").getValue()!=null) {
			
			record.setAttribute("templateId",	form1.getField("templateid").getValue());
			record.setAttribute("attributeename",	form2.getField("attributeename").getValue());
			record.setAttribute("label", form2.getField("label").getValue());				
			record.setAttribute("mandatory", (null == form2.getField("mandatory").getValue()?false:form2.getField("mandatory").getValue()));
			record.setAttribute("typename", form2.getField("typename").getValue());
			record.setAttribute("editor", form2.getField("editor").getValue());
			if(Integer.toString(SExtendedAttribute.TYPE_DATE).equals(form2.getField("typename").getValue())){
				record.setAttribute("value", form2.getField("valueDate").getValue());
				if (isRelativeDate.getValueAsBoolean())
					record.setAttribute("size", form2.getField("relativeCount").getValue());
				else
					record.setAttribute("size", "");

			}else{
				record.setAttribute("value", form2.getField("value").getValue());
			}
			
			record.setAttribute("description", form2.getField("description").getValue());
			
			System.out.println(form2.getField("typename").getValue()); 
			
			
//			if (	(Integer.toString(SExtendedAttribute.TYPE_STRING).equals((form2.getField("typename").getValue().toString()))) ||
//					(Integer.toString(SExtendedAttribute.TYPE_MULTIVALUE).equals(form2.getField("typename").getValue())) ){
//				record.setAttribute("size", form2.getField("size").getValue());
//			}
			
			grid.addData(record);
			grid.selectRecord(record);
			isAddNew = false;
			
		} else {
			SC.say(I18N.message("fieldisrequired"));
			return;
		};	
	}
	
	/**
	 * 그리드 레코드 변경
	 */
	private void executeUpdate() {
		
		String typename = (String)form2.getField("typename").getValue();
		Object objValue = (String)form2.getField("value").getValue();
		
		if(checkTypeError(typename, objValue)) return;
		
		ListGridRecord record = grid.getSelectedRecord();
		
		String strSize = (String)form2.getField("size").getValue();
		int size = 0;
		try {
			size = Integer.parseInt(strSize);
		} catch (Exception e) {
		}
		
		if (	Integer.toString(SExtendedAttribute.TYPE_STRING).equals(typename) ||
				Integer.toString(SExtendedAttribute.TYPE_MULTIVALUE).equals(typename) ){
			// 20140325, junsoo, check size
			boolean ok = Util.isValidSize(null, (String)objValue, size, true, "");
			if (!ok) 
				return;

			record.setAttribute("size", strSize);
		}
		
		record.setAttribute("templateId",	form1.getField("templateid").getValue());
		record.setAttribute("attributeename",	form2.getField("attributeename").getValue());
		record.setAttribute("label", form2.getField("label").getValue());				
		record.setAttribute("mandatory", (null == form2.getField("mandatory").getValue()?false:form2.getField("mandatory").getValue()));
		record.setAttribute("typename", typename);
		record.setAttribute("editor", form2.getField("editor").getValue());
		if(Integer.toString(SExtendedAttribute.TYPE_DATE).equals(typename)){
			record.setAttribute("value", form2.getField("valueDate").getValue());
			if (isRelativeDate.getValueAsBoolean())
				record.setAttribute("size", form2.getField("relativeCount").getValue());
			else
				record.setAttribute("size", "");
		}else{
			record.setAttribute("value", objValue);
		}
		
		record.setAttribute("description", form2.getField("description").getValue());
		
		grid.refreshRow(grid.getRecordIndex(record));
	}
	
	/**
	 * name 중복 체크
	 */
	private Boolean checkName3(String name){
		boolean rtn = false;
		
		RecordList rclist  = grid.getDataAsRecordList();
		Record[] rc = rclist.findAll("attributeename", name);
		if(rc!= null){//같은게 있을때
			if(isAddNew){//insert 
				rtn = false;
			}else if((form2.getValueAsString("attributeename")).equals(grid.getSelectedRecord().getAttributeAsString("attributeename"))){// update 선택한 name이면 통과
				rtn = true;
			}else{
				rtn = false;
			}
		}else{
			rtn = true;
		}
		return rtn;
	}
	
	/**
	 * 타입에따른 형 체크
	 */
	public boolean checkValue(){
		boolean rtn = true;
		String tmpValue = form2.getValueAsString("value");
		int tmpType = Integer.parseInt((String)form2.getValueAsString("typename"));
		if(null != tmpValue){
			if(SExtendedAttribute.TYPE_INT == tmpType){
				try{
					Integer.parseInt(tmpValue);
				} catch (NumberFormatException e) {
					rtn = false;Message = "INT";
				}				
			}else if(SExtendedAttribute.TYPE_DOUBLE == tmpType){
				try{
					Double.parseDouble(tmpValue);
				} catch (NumberFormatException e) {
					rtn = false;Message = "DOUBLE";
				}	
			}
		}
		return rtn;
	}	
	
	public boolean checkData(){
		if(validtor1.check(form1) && validtor2.check(grid)) return true;
		else return false;
	}
	
	private boolean checkTypeError(String typename, Object value){
		// 20131209, junsoo, check null
		if (value == null)
			return false;
		
		if (Integer.toString(SExtendedAttribute.TYPE_INT).equals(typename)) {
			try {
				int intvalue = Integer.parseInt(value.toString());
				if(!Long.toString(intvalue).equals(value)) throw new NumberFormatException();
			} catch (Exception e) {
				Log.error(I18N.message("typeError"), I18N.message("numberTypeRequired"), true);
				return true;
			}
		}
		else if (Integer.toString(SExtendedAttribute.TYPE_DOUBLE).equals(typename)) {
			try {
				long longvalue = Long.parseLong(value.toString());
				if(!Long.toString(longvalue).equals(value)) throw new NumberFormatException();
			} catch (Exception e) {
				Log.error(I18N.message("typeError"), I18N.message("numberTypeRequired"), true);
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 20131227na 폴더의 우선순위를 갱신한다.
	 */
	private void reflash(){
		ListGridRecord[] grids = grid.getRecords();
		int priority = 1;
		
		//우선순위 세팅
		for (int i = 0; i < grids.length; i++){
			if(grids[i].getAttributeAsBoolean("check")){
				grids[i].setAttribute("priority", priority++);
			}
			else grids[i].setAttribute("priority", "");
		}
		
		grid.markForRedraw();
	}
	
}