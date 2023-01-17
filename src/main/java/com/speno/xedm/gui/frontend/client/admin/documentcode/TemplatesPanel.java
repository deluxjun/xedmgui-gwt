package com.speno.xedm.gui.frontend.client.admin.documentcode;

import java.io.Serializable;
import java.util.Date;

import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.RecordList;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.TransferImgButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.speno.xedm.core.service.serials.SExtendedAttribute;
import com.speno.xedm.core.service.serials.STemplate;
import com.speno.xedm.gui.common.client.Constants;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.RecordObserver;
import com.speno.xedm.gui.common.client.RefreshObserver;
import com.speno.xedm.gui.common.client.Session;
import com.speno.xedm.gui.common.client.log.Log;
import com.speno.xedm.gui.common.client.util.AsyncCallbackWithStatus;
import com.speno.xedm.gui.common.client.util.IsNotChangedValidator;
import com.speno.xedm.gui.common.client.util.ItemFactory;
import com.speno.xedm.gui.common.client.util.SCM;
import com.speno.xedm.gui.common.client.util.ServiceUtil;
import com.speno.xedm.gui.common.client.util.Util;
import com.speno.xedm.gui.frontend.client.admin.TrackPanel;

/**
 * Templates Panel
 * 
 * @author 남윤성
 * @since 1.0
 */
public class TemplatesPanel extends VLayout implements RefreshObserver, RecordObserver {	
	private static TemplatesPanel instance;	
	
	private VLayout mainVL;
	private TemplatesGridPanel templatesGridPanel;
	private TemplatesExtGridPanel templatesExtGridPanel;
	private TransferImgButton leftArrow, rightArrow;
	private HLayout actionHL;
	private ListGrid grid;
	private ListGrid extGrid;
	private DynamicForm extForm1;
	private DynamicForm extForm2;
	
	/**
	 * 기 생성된 instance를 반환함. instance가 없을 경우 생성 후 반환함
	 * @return
	 */
	public static TemplatesPanel get() {
		if (instance == null) {
			instance = new TemplatesPanel();
		}
		return instance;
	}
	
	public TemplatesPanel() {            	
		setWidth100();
		setMembersMargin(10);
		setPadding(Constants.PADDING_DEFAULT);
		addMember(new TrackPanel(I18N.message("admin")+" > "+ I18N.message("documentcode")+" > "+ I18N.message("template"), this));
		
		createMainPanel(false); //Main패널 생성
	}
	
	/**
	 * Main패널 생성
	 */
	private void createMainPanel(boolean isRefresh) {
		if(isRefresh) {
			removeMember(mainVL);
		}
		
		templatesExtGridPanel = createTemplatesExtVL(isRefresh, null);
		templatesGridPanel = createTemplatesVL(isRefresh, templatesExtGridPanel);
		
		mainVL = new VLayout(10);
		mainVL.setHeight100();
		mainVL.setWidth100();
		mainVL.setMembers(templatesGridPanel, templatesExtGridPanel,createActHL());
		addMember(mainVL);
		
		grid = templatesGridPanel.getGrid();
		extGrid = templatesExtGridPanel.getGrid();
		extForm1 = templatesExtGridPanel.getForm1();
		extForm2 = templatesExtGridPanel.getForm2();
	}
	
	/**
	 * Refresh
	 */
	private void refresh() {
		createMainPanel(true);
	}
	
	/**
	 * TemplatesGrid 패널 생성
	 */
	private TemplatesGridPanel createTemplatesVL(boolean isRefresh, TemplatesExtGridPanel extPanel) {		
		return isRefresh ? 
				new TemplatesGridPanel("admin.doccode.templates", I18N.message("template"), this, false, true, extPanel, "100%") :
					TemplatesGridPanel.get("admin.doccode.templates", I18N.message("template"), this, false, true, extPanel, "100%");
	}
	
	/**
	 * TemplatesExtGrid 패널 생성
	 */
	private TemplatesExtGridPanel createTemplatesExtVL(boolean isRefresh, ListGrid dragSourceGrid) {
		return isRefresh ? 
				new TemplatesExtGridPanel("admin.doccode.templates", I18N.message("second.assignedtargetfolder"), null,  false, "100%") : 
					TemplatesExtGridPanel.get("admin.doccode.templates", "", null,  false, "100%");
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
            	extForm1.getItem("templateid").setTooltip(I18N.message("generatedbyserver", extForm1.getItem("templateid").getTitle()));
            	extForm1.editNewRecord();
            	extForm1.reset();
            	extForm2.editNewRecord();
            	extForm2.reset();
            	extGrid.deselectAllRecords();
            	extGrid.setData(new ListGridRecord[0]); //documentTypeGridPanel 그리드 초기화
            	
            	templatesExtGridPanel.resetGridForm2("");
            	
            }   
        });
		
		Button btnSave = new Button(I18N.message("save"));
		btnSave.setIcon(ItemFactory.newImgIcon("data_into.png").getSrc());
		btnSave.addClickHandler(new ClickHandler() {   
            public void onClick(ClickEvent event) {
            	if(!checkName1((String)extForm1.getValue("name"))){
					SC.say(I18N.message("secnod.duplicatemessage", I18N.message("name")));//name 중복 체크
					extForm1.getField("name").focusInItem();
					return;
            	}else{
	            	if(0 == extGrid.getDataAsRecordList().getLength()){// 속성이 없을때
	            		if(extForm1.validate()) {
	            			if(extForm1.getValue("templateid") == null) {
	            				executeUpdate(true);
	            			}else{
	            				executeUpdate(false);
	            			}
	            			
	            		}
	            	}else{
	            		if(extForm1.validate()&&extForm2.validate()) {
	            			if(extForm1.getValue("templateid") == null) {
	            				executeUpdate(true);
	            			}else{
	            				executeUpdate(false);
	            			}
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
	
	
	/**
	 * Refresh 버튼 클릭 이벤트 옵져버
	 */
	@Override
	public void onRefreshBtnClicked(ClickEvent event) {
		refresh();
	}

	@Override
	public void onRecordSelected(Serializable id, Serializable parentId) {
//		if(Constants.ADMIN_ROOT == (Long)id || Constants.ADMIN_GROUP_ROOT == (Long)id) {
//			leftArrow.disable();
//			rightArrow.disable();
//		}
//		else {
			leftArrow.enable();
			rightArrow.enable();
//		}
	}
	
	/**
	 * 형변환
	 */
	public Object chgValue(Record rc){
		Object trn = null;
		int tmpType = Integer.parseInt(rc.getAttributeAsString("typename"));
		String tmpValue = (String)rc.getAttributeAsString("value");
		
		// multivalue
		if (tmpType == SExtendedAttribute.TYPE_MULTIVALUE) {
			if (tmpValue != null && tmpValue.length() > 0) {
				String[] values = tmpValue.split(",");
				return values;
			} else
				return null;
		}
		
		if(null != tmpValue){
			if(SExtendedAttribute.TYPE_STRING == tmpType){
				trn = tmpValue;
			}else if(SExtendedAttribute.TYPE_INT == tmpType){
				trn = Integer.parseInt(tmpValue);
			}else if(SExtendedAttribute.TYPE_DOUBLE == tmpType){
				trn = Double.parseDouble(tmpValue);
			}else if(SExtendedAttribute.TYPE_DATE == tmpType){
				trn = (Date)extForm2.getField("valueDate").getValue();
			}
		}
		return trn;
	}
	
//	/**
//	 * 신규저장
//	 */
//	private void executeAdd() {
//		Log.debug("[ TemplatesPanel executeAdd ]");
//		
//		STemplate template = new STemplate();
//		template.setId(0L);
//		template.setName(extForm1.getValueAsString("name"));
//		template.setDescription(extForm1.getValueAsString("description"));
//		
//		RecordList rclist = extGrid.getDataAsRecordList();
//		int rccnt = rclist.getLength();
//		if(rccnt>0){
//			SExtendedAttribute[] attributes = new SExtendedAttribute[rccnt];
//			for(int i=0; i< rccnt; i++){
//				Record rc = rclist.get(i);		
//				SExtendedAttribute att = new SExtendedAttribute();
//				att.setName(rc.getAttributeAsString("attributeename"));
//				att.setLabel(rc.getAttributeAsString("label"));
//				att.setMandatory("true".equals(rc.getAttributeAsString("mandatory"))?true:false);
//				int type = Integer.parseInt(rc.getAttributeAsString("typename"));
//				att.setType(type);
//				att.setEditor(Integer.parseInt(rc.getAttributeAsString("editor")));
//				att.setValue(chgValue(rc));
//				if (type == SExtendedAttribute.TYPE_STRING) {
//					try {
//						int size = Integer.parseInt(rc.getAttributeAsString("size"));
//						att.setIntValue((long)size);
//					} catch (Exception e) {
//						att.setIntValue(4000L);
//					}
//				}
//				att.setPosition(i+1);
//				
//				attributes[i] = att;
//			}
//			
//			template.setAttributes(attributes);
//		}
//		
//		ServiceUtil.template().save(Session.get().getSid(), template, new AsyncCallbackWithStatus<STemplate>() {
//			@Override
//			public String getSuccessMessage() {
//				return I18N.message("operationcompleted");
//			}
//			@Override
//			public String getProcessMessage() {
//				return null;
//			}
//			@Override
//			public void onSuccessEvent(STemplate result) {
//				SC.say(I18N.message("operationcompleted"));
//				
//				ListGridRecord addRecord = new ListGridRecord();
//				addRecord.setAttribute("id", result.getId());		
//				addRecord.setAttribute("templateid", result.getId());	
//				addRecord.setAttribute("name", result.getName());
//				addRecord.setAttribute("description", result.getDescription());
//				
//				int selectedRowNum = grid.getRecordIndex(addRecord);	
//				grid.selectRecord(addRecord);
//				grid.selectSingleRecord(selectedRowNum);
//				grid.scrollToRow(selectedRowNum);	
//				grid.addData(addRecord);
//				onRecordSelected(addRecord);
//			}
//			@Override
//			public void onFailureEvent(Throwable caught) {
//				Log.serverError(caught, false);
//				SCM.warn(caught);
//			}
//		});
//	}
	
	/**
	 * 저장
	 */
	private void executeUpdate(final boolean bAdd) {
		Log.debug("[ TemplatesPanel executeUpdate ]");
		
		STemplate template = new STemplate();
		if (bAdd)
			template.setId(0L);
		else
			template.setId(Long.parseLong(extForm1.getValueAsString("templateid")));
		template.setName(extForm1.getValueAsString("name"));
		template.setDescription(extForm1.getValueAsString("description"));
		template.setRule(extForm1.getValueAsString("ruleScript"));
		
		RecordList rclist = extGrid.getDataAsRecordList();
		int rccnt = rclist.getLength();
		SExtendedAttribute[] attributes = null;
		if(rccnt>0){
		    attributes = new SExtendedAttribute[rccnt];
			for(int i=0; i< rccnt; i++){
				Record rc = rclist.get(i);		
				SExtendedAttribute att = new SExtendedAttribute();
				att.setName(rc.getAttributeAsString("attributeename"));
				att.setLabel(rc.getAttributeAsString("label"));
				att.setMandatory("true".equals(rc.getAttributeAsString("mandatory"))?true:false);
				int type = Integer.parseInt(rc.getAttributeAsString("typename"));
				att.setType(type);
				att.setEditor(Integer.parseInt(rc.getAttributeAsString("editor")));
				att.setValue(chgValue(rc));
				att.setDescription(rc.getAttributeAsString("description"));
				if(!rc.getAttribute("priority").equals(""))
					att.setPriority(rc.getAttributeAsInt("priority"));
				if (type == SExtendedAttribute.TYPE_STRING || type == SExtendedAttribute.TYPE_MULTIVALUE){
					try {
						int size = Integer.parseInt(rc.getAttributeAsString("size"));
						att.setIntValue((long)size);
						
						// 20140325, junsoo, check size
						boolean ok = Util.isValidSize(null, att.getStringValue(), size, true, att.getName());
						if (!ok) 
							return;
					} catch (Exception e) {
						Log.debug(e.getMessage());
						att.setIntValue(4000L);
					}
				}

				if (SExtendedAttribute.TYPE_DATE == type) {		// date 일 경우 size 는 상대값 count
					try {
						int size = Integer.parseInt(rc.getAttributeAsString("size"));
						att.setIntValue((long)size);
					} catch (Exception e) {
					}
				}

				att.setPosition(i+1);
				
				attributes[i] = att;
			}
			
			template.setAttributes(attributes);
		}
		
		if(templatesExtGridPanel.checkData()){
			SC.say(I18N.message("nothingchanged"));
			return;
		}
		
		ServiceUtil.template().save(Session.get().getSid(), template, new AsyncCallbackWithStatus<STemplate>() {
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
			public void onSuccessEvent(STemplate result) {
//				SC.say(I18N.message("operationcompleted"));
				// kimsoeun GS인증용  - 작업 완료 -> 저장 완료 문구 변경
				SC.say(I18N.message("savecompleted"));
				
				if (bAdd) {
					ListGridRecord addRecord = new ListGridRecord();
					addRecord.setAttribute("id", result.getId());		
					addRecord.setAttribute("templateid", result.getId());	
					addRecord.setAttribute("name", result.getName());
					addRecord.setAttribute("description", result.getDescription());
					addRecord.setAttribute("ruleScript", result.getRule());
					
					int selectedRowNum = grid.getRecordIndex(addRecord);	
					grid.selectRecord(addRecord);
					grid.selectSingleRecord(selectedRowNum);
					grid.scrollToRow(selectedRowNum);	
					grid.addData(addRecord);
					onRecordSelected(addRecord);

				}
				else {
	            	int selectedRowNum;
	            	selectedRowNum = grid.getRecordIndex(grid.getSelectedRecord());				
					ListGridRecord selectedRecord = grid.getSelectedRecord();
					selectedRecord.setAttribute("name", result.getName());
					selectedRecord.setAttribute("description", result.getDescription());
					selectedRecord.setAttribute("ruleScript", result.getRule());
					grid.getDataAsRecordList().set(selectedRowNum, selectedRecord);				
					grid.selectSingleRecord(selectedRowNum);
					grid.scrollToRow(selectedRowNum);
					onRecordSelected(selectedRecord);
				}
			}
			@Override
			public void onFailureEvent(Throwable caught) {
				Log.serverError(caught, false);
				SCM.warn(caught);
			}
		});
	}
	
	/**
	 * name 중복체크
	 */
	private Boolean checkName1(String name){
		boolean rtn = false;
		RecordList rclist  = grid.getDataAsRecordList();
		Record[] rc = rclist.findAll("name", name);
		if(rc!= null){
			if(null == extForm1.getValueAsString("templateid")){//insert 
				rtn = false;
			}else if((extForm1.getValueAsString("templateid")).equals(rc[0].getAttributeAsString("id"))){// update
				rtn = true;
			}else{
				rtn = false;
			}
		}else{
			rtn = true;
		}
		return rtn;
	}

	@Override
	public void onRecordSelected(Record record) {
		templatesExtGridPanel.setGrid(grid,record);
	}

	@Override
	public void onRecordClick(Record record) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onRecordDoubleClick(Record record) {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean isExistMember() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isIDLong() {
		return true;
	}
}