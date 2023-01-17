package com.speno.xedm.gui.frontend.client.admin.documentcode;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.Alignment;
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
import com.smartgwt.client.widgets.form.fields.HiddenItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
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
import com.speno.xedm.core.service.serials.SCode;
import com.speno.xedm.core.service.serials.SContent;
import com.speno.xedm.core.service.serials.SDocument;
import com.speno.xedm.core.service.serials.SFileType;
import com.speno.xedm.core.service.serials.SFolder;
import com.speno.xedm.gui.common.client.Constants;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.Session;
import com.speno.xedm.gui.common.client.log.Log;
import com.speno.xedm.gui.common.client.util.AsyncCallbackWithStatus;
import com.speno.xedm.gui.common.client.util.IsNotChangedValidator;
import com.speno.xedm.gui.common.client.util.ItemFactory;
import com.speno.xedm.gui.common.client.util.LengthValidator;
import com.speno.xedm.gui.common.client.util.ServiceUtil;
import com.speno.xedm.gui.common.client.util.Util;

/**
 * FileTypeGrid Panel
 * @author 박상기
 * @since 1.0
 */
public class FileTypeGridPanel extends VLayout {	
	private static HashMap<String, FileTypeGridPanel> instanceMap = new HashMap<String, FileTypeGridPanel>();
	
	private ListGrid grid;
	private DynamicForm form;
	private HLayout actionHL;
	
	private LinkedHashMap<String, String> maxFileSizeOpts = new LinkedHashMap<String, String>() ;
	//private LinkedHashMap<String, String> viewerOpts = new LinkedHashMap<String, String>() ;
	private SelectItem maxFileSizeItem;
	private IsNotChangedValidator validator = new IsNotChangedValidator();
	
	//하단 폼 체크박스 추가
	private CheckboxItem chkOsDefalut;
	private CheckboxItem chkViewer;
	private CheckboxItem chkDownload;
	
	
	/**
	 * 기 생성된 instance를 반환함. instance가 없을 경우 생성 후 반환함
	 * @param id
	 * @param subTitle
	 * @return
	 */
	public static FileTypeGridPanel get(final String id, final String subTitle) {
		if (instanceMap.get(id) == null) {
			new FileTypeGridPanel(id, subTitle);
		}
		return instanceMap.get(id);
	}
	
	public FileTypeGridPanel(final String id, final String subTitle) {
		
		instanceMap.put(id, this);	
		
		/* Sub Title 생성 */
		Label subTitleLabel = new Label();
		subTitleLabel.setAutoHeight();   
		subTitleLabel.setAlign(Alignment.LEFT);   
		subTitleLabel.setValign(VerticalAlignment.CENTER);
		subTitleLabel.setStyleName("subTitle");
		subTitleLabel.setContents(I18N.message("filetype"));
		
		grid = new ListGrid();
		grid.setWidth100();
		grid.setHeight100();		
		grid.setShowAllRecords(true);
		
		grid.setEmptyMessage(I18N.message("notitemstoshow"));		
		grid.setCanFreezeFields(true);
		grid.setCanRemoveRecords(true);
		grid.setSelectionType(SelectionStyle.SINGLE);
		//grid.setDataSource(new DocumentCodeDS(DocumentCodeDS.TYPE_FILE));		
		//grid.setAutoFetchData(true);
		grid.invalidateCache();
		
		ListGridField typeIdField = new ListGridField("id", I18N.message("typeid"));
		typeIdField.setHidden(true);
		
		ListGridField typeNmField = new ListGridField("name", I18N.message("filetype"));
		
		ListGridField maxFileSizeField = new ListGridField("maxFileSizeField");
		maxFileSizeField.setHidden(true);
		
		ListGridField maxFileSizeCodeNameField = new ListGridField("maxFileSizeCodeName", I18N.message("availablefilesize"));
		maxFileSizeCodeNameField.setHidden(true);
		
		ListGridField descriptionField = new ListGridField("description", I18N.message("description"));
		descriptionField.setWidth("*");

		ListGridField linkViewerField = new ListGridField("linkViewer", I18N.message("linkViewerUrl"));
		linkViewerField.setWidth("*");


		//viewer 옵션의 os default 컬럼
		ListGridField fieldOsDefault = new ListGridField("fieldOsDefault", I18N.message("osdefault"));
		fieldOsDefault.setType(ListGridFieldType.BOOLEAN);
		fieldOsDefault.setCanEdit(false);
		fieldOsDefault.setShowTitle(true);
		fieldOsDefault.setAlign(Alignment.LEFT);

		
		//viewer 옵션의 viewer 컬럼
		ListGridField fieldViewer = new ListGridField("fieldViewer", I18N.message("viewer"));
		fieldViewer.setType(ListGridFieldType.BOOLEAN);
		fieldViewer.setCanEdit(false);
		fieldViewer.setShowTitle(true);
		fieldViewer.setAlign(Alignment.LEFT);
		

		//viewer 옵션의 download 컬럼
		ListGridField fieldDownload = new ListGridField("fieldDownload", I18N.message("download"));
		fieldDownload.setType(ListGridFieldType.BOOLEAN);
		fieldDownload.setCanEdit(false);
		fieldDownload.setShowTitle(true);
		fieldDownload.setAlign(Alignment.LEFT);
		
		
		ListGridField viewerField = new ListGridField("viewer");
		viewerField.setHidden(true);
		
		ListGridField viewerCodeNameField = new ListGridField("viewerCodeName");
		viewerCodeNameField.setHidden(true);
		
		//20140122 osViewer setting.contextmenu.edmos=false로 제어
		boolean isOsViewer = Util.getSetting("setting.contextmenu.edmos");
		if(!isOsViewer) fieldOsDefault.setHidden(true);
		
		// kimsoeun GS인증용 - 파일 형식 그리드에서 뷰어, 연결된 뷰어 URL 없애기
		grid.setFields(typeIdField, typeNmField, maxFileSizeField, maxFileSizeCodeNameField, descriptionField, /*linkViewerField,*/ fieldOsDefault, /*fieldViewer,*/ fieldDownload, viewerField, viewerCodeNameField);
		
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
				
				chkOsDefalut.setValue(false);
				chkViewer.setValue(false);
				chkDownload.setValue(false);
				
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
		groupVL.setMembers(grid, createFormHL(), createActHL());
		
		setMembersMargin(Constants.SUBTITLE_MARGIN);
        setMembers(subTitleLabel, groupVL);
        
        executeFetch();
	}
	
	/**
	 * 하단 상세 Form 생성
	 * @return
	 */
	private HLayout createFormHL() {
		
		HiddenItem idItem = new HiddenItem("id");
		
		TextItem nameItem = new TextItem("name", I18N.message("filetype"));
		nameItem.setWrapTitle(false);
		nameItem.setRequired(true);
		// kimsoeun GS인증용 - 툴팁 다국어화
		nameItem.setRequiredMessage(I18N.message("fieldisrequired"));
//		nameItem.setLength(Session.get().getInfo().getIntConfig("gui.filetype.fieldsize", 64));
		nameItem.setValidators(new LengthValidator(nameItem, Session.get().getInfo().getIntConfig("gui.filetype.fieldsize", 64)));
		
		maxFileSizeItem = new SelectItem("maxFileSize", I18N.message("availablefilesize"));
		maxFileSizeItem.setWrapTitle(false);
		maxFileSizeItem.setRequired(true);
		maxFileSizeItem.setVisible(false);
//		maxFileSizeItem.setEmptyDisplayValue(I18N.message("choosetype"));
		
		TextItem descriptionItem = new TextItem("description", I18N.message("description"));
		descriptionItem.setWrapTitle(false);
//		descriptionItem.setLength(Session.get().getInfo().getIntConfig("gui.description.fieldsize", 1000));
		descriptionItem.setValidators(new LengthValidator(descriptionItem, Session.get().getInfo().getIntConfig("gui.description.fieldsize", 1000)));

		TextItem linkViewerItem = new TextItem("linkViewer", I18N.message("linkViewerUrl"));
		linkViewerItem.setWrapTitle(false);
//		linkViewerItem.setLength(Session.get().getInfo().getIntConfig("gui.linkviewer.fieldsize", 255));
		linkViewerItem.setValidators(new LengthValidator(linkViewerItem, Session.get().getInfo().getIntConfig("gui.linkviewer.fieldsize", 255)));
		// kimsoeun GS인증용 - 파일 형식에서 연결된 뷰어 URL 없애기
		linkViewerItem.hide();
		
		executeGetOptionsAndSet();

		chkOsDefalut = ItemFactory.newCheckbox("osdefault", "osdefault");
		chkViewer = ItemFactory.newCheckbox("viewer", "viewer");
		chkDownload = ItemFactory.newCheckbox("download", "download");
		
		form = new DynamicForm();
		form.setAutoWidth();	
		form.setMargin(4);
		form.setNumCols(4);
		form.setColWidths("1","1","1","*");
		form.setItems(idItem, nameItem, maxFileSizeItem, descriptionItem, linkViewerItem);
		form.reset();
    	
		VLayout vlLeft = new VLayout(50);
		vlLeft.setMembers(form);
		vlLeft.setWidth("50%");

		//체크박스 부분============================================================================
//		final Label lblViewerTitle = new Label(I18N.message("Viewer"));
//		lblViewerTitle.setAlign(Alignment.CENTER);
//		lblViewerTitle.setAutoFit(true);
//		HLayout hlTitle = new HLayout();
//		hlTitle.setHeight100();
//		hlTitle.setAutoWidth();
//		hlTitle.setMargin(6);
//		hlTitle.setAlign(Alignment.CENTER);
//		hlTitle.setMembers(lblViewerTitle);
			
		DynamicForm df = new DynamicForm();
	    df.setNumCols(2);
//	    df.setColWidths(50, 50, 50);
	    
	    // kimsoeun GS인증용 - 파일 형식 추가에서 뷰어, OS Default 체크 없애기
	    String visible = Session.get().getInfo().getConfig("settings.fileType.viewerCheckbox");
	    chkViewer.setVisible(Boolean.valueOf(visible));
	    chkOsDefalut.setVisible(Boolean.valueOf(visible));
	    
	    df.setItems(chkOsDefalut, chkViewer, chkDownload);
		HLayout vl = new HLayout();
		vl.setMembers(df);
//		vl.setHeight100();
		vl.setMembersMargin(10);
		vl.setWidth("50%");
	    //=========================================================================================
		
    	HLayout fileTypeFormHL = new HLayout();
    	fileTypeFormHL.setBorder("1px solid gray");
    	fileTypeFormHL.setWidth100();
    	fileTypeFormHL.setAutoHeight();    	
    	fileTypeFormHL.addMembers(vlLeft, vl);
    	
		
		boolean isOsViewer = Util.getSetting("setting.contextmenu.edmos");
		if(!isOsViewer) chkOsDefalut.disable();
			
    	return fileTypeFormHL;
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
            	
            	chkOsDefalut.setValue(false);
            	chkViewer.setValue(false);
            	chkDownload.setValue(false);
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
		
		chkOsDefalut.setValue(false);
		chkViewer.setValue(false);
		chkDownload.setValue(false);
		
		form.getItem("id").setTooltip(I18N.message("fieldisreadonly", form.getItem("id").getTitle()));
    	form.reset();
    	form.editRecord(record);
    	setViewerOptionInfo(record.getAttribute("viewer"));
    	
    	validator.setMap(form, chkDownload.getValueAsBoolean());
	}
	
	private void executeFetch(){
		
		ServiceUtil.documentcode().listFileTypeLikeName(Session.get().getSid(), "", new AsyncCallbackWithStatus<List<SFileType>>() {
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
				Log.warn(caught.toString(), null);
			}
			@Override
			public void onSuccessEvent(List<SFileType> result) {				
				grid.setData(new ListGridRecord[0]); //그리드 초기화					
				for (int j = 0; j < result.size(); j++) {					
					ListGridRecord record=new ListGridRecord();
					record.setAttribute("id", result.get(j).getId());
					record.setAttribute("name", result.get(j).getName());
					record.setAttribute("maxFileSize", result.get(j).getMaxFileSize());					
					record.setAttribute("maxFileSizeCodeName", result.get(j).getMaxFileSizeCodeName());
					record.setAttribute("description", result.get(j).getDescription());
					record.setAttribute("linkViewer", result.get(j).getLinkViewer());
					record.setAttribute("viewer", result.get(j).getViewer());
					record.setAttribute("viewerCodeName", "");
					//체크박스 추가부분
					record.setAttribute("fieldOsDefault", getChkBoxValueByName(result.get(j).getViewer(), "fieldOsDefault"));
					// kimsoeun GS인증용 - 파일 형식 그리드에서 뷰어 없애기
					//record.setAttribute("fieldViewer", getChkBoxValueByName(result.get(j).getViewer(), "fieldViewer"));
					record.setAttribute("fieldDownload", getChkBoxValueByName(result.get(j).getViewer(), "fieldDownload"));
					
					grid.addData(record);
				}	
				
				if (result.size() > 0) {
					grid.selectSingleRecord(0);
					recordClickedProcess(grid.getRecord(0));
				}
				
				Log.info(I18N.message("operationcompleted"), null);
			}
		});
	}
	
	private void executeAdd() {
		//GWT.log("[ FileTypePanel executeAdd ]", null);
		
		String sChkOptionData = "";
		
		SFileType fileType = new SFileType();
		fileType.setId(0L);
		fileType.setName(form.getValueAsString("name"));
		fileType.setMaxFileSize(Long.parseLong(form.getValueAsString("maxFileSize")));
		fileType.setMaxFileSizeCodeName(maxFileSizeOpts.get(form.getValueAsString("maxFileSize")));		
		fileType.setDescription(form.getValueAsString("description"));		
		fileType.setLinkViewer(form.getValueAsString("linkViewer"));		

		sChkOptionData = getViewerOptionInfo();
		fileType.setViewer(sChkOptionData);
		
		fileType.setViewerCodeName("");
		
		ServiceUtil.documentcode().saveFileType(Session.get().getSid(), fileType, new AsyncCallbackWithStatus<SFileType>() {
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
				Log.warn(caught.toString(), null);
			}
			@Override
			public void onSuccessEvent(SFileType result) {
				//GWT.log("[ FileTypePanel executeAdd ] onSuccess. id["+result.getId()+"]", null);
				
				ListGridRecord addRecord = new ListGridRecord();
				addRecord.setAttribute("id", result.getId());
				addRecord.setAttribute("name", result.getName());
				addRecord.setAttribute("maxFileSize", result.getMaxFileSize());
				addRecord.setAttribute("maxFileSizeCodeName", result.getMaxFileSizeCodeName());
				addRecord.setAttribute("description", result.getDescription());
				addRecord.setAttribute("linkViewer", result.getLinkViewer());
				addRecord.setAttribute("viewer", result.getViewer());
				addRecord.setAttribute("viewerCodeName", "");
				//체크박스 추가부분
				addRecord.setAttribute("fieldOsDefault", getChkBoxValueByName(result.getViewer(), "fieldOsDefault"));
				// kimsoeun GS인증용 - 파일 형식 그리드에서 뷰어 없애기
				//addRecord.setAttribute("fieldViewer", getChkBoxValueByName(result.getViewer(), "fieldViewer"));
				addRecord.setAttribute("fieldDownload", getChkBoxValueByName(result.getViewer(), "fieldDownload"));
				
				grid.addData(addRecord);
				
				int selectedRowNum = grid.getRecordIndex(addRecord);						
				grid.selectSingleRecord(selectedRowNum);
				grid.scrollToRow(selectedRowNum);						
				form.reset();
            	form.editRecord(addRecord);
            	form.getItem("id").setTooltip(I18N.message("fieldisreadonly", form.getItem("id").getTitle()));
            	
            	// kimsoeun GS인증용  - 작업 완료 -> 저장 완료 문구 변경
				SC.say(I18N.message("savecompleted"));
            	Log.info(I18N.message("addupdate") + " " + I18N.message("operationcompleted"), null);
            	validator.setMap(form, chkDownload.getValueAsBoolean());
			}
		});
	}
	
	private void executeUpdate() {
		//GWT.log("[ FileTypePanel executeUpdate ]", null);
		
		String sChkOptionData="";
		
		SFileType fileType = new SFileType();
		fileType.setId(Long.parseLong(form.getValueAsString("id")));
		fileType.setName(form.getValueAsString("name"));
		fileType.setMaxFileSize(Long.parseLong(form.getValueAsString("maxFileSize")));
		fileType.setMaxFileSizeCodeName(maxFileSizeOpts.get(form.getValueAsString("maxFileSize")));		
		fileType.setDescription(form.getValueAsString("description"));
		fileType.setLinkViewer(form.getValueAsString("linkViewer"));
		
		sChkOptionData = getViewerOptionInfo();
		fileType.setViewer(sChkOptionData);
		
		fileType.setViewerCodeName("");
		if(validator.check(form, chkDownload.getValueAsBoolean())){
			SC.say(I18N.message("nothingchanged"));
			return;
		}
		
		ServiceUtil.documentcode().saveFileType(Session.get().getSid(), fileType, new AsyncCallbackWithStatus<SFileType>() {
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
				Log.warn(caught.toString(), null);
			}
			@Override
			public void onSuccessEvent(SFileType result) {
				//GWT.log("[ FileTypePanel executeUpdate ] onSuccess. id["+result.getId()+"]", null);
				
				final int selectedRowNum = grid.getRecordIndex(grid.getSelectedRecord());				
				ListGridRecord selectedRecord = grid.getSelectedRecord();
				selectedRecord.setAttribute("name", result.getName());
				selectedRecord.setAttribute("maxFileSize", result.getMaxFileSize());
				selectedRecord.setAttribute("maxFileSizeCodeName", result.getMaxFileSizeCodeName());
				selectedRecord.setAttribute("description", result.getDescription());
				selectedRecord.setAttribute("linkViewer", result.getLinkViewer());
				selectedRecord.setAttribute("viewer", result.getViewer());
				selectedRecord.setAttribute("viewerCodeName", "");
				selectedRecord.setAttribute("maxFileSize", result.getMaxFileSize());
				//체크박스 추가부분
				selectedRecord.setAttribute("fieldOsDefault", getChkBoxValueByName(result.getViewer(), "fieldOsDefault"));
				// kimsoeun GS인증용 - 파일 형식 그리드에서 뷰어 없애기
//				selectedRecord.setAttribute("fieldViewer", getChkBoxValueByName(result.getViewer(), "fieldViewer"));
				selectedRecord.setAttribute("fieldDownload", getChkBoxValueByName(result.getViewer(), "fieldDownload"));
				
				grid.getDataAsRecordList().set(selectedRowNum, selectedRecord);				
				grid.selectSingleRecord(selectedRowNum);
				grid.scrollToRow(selectedRowNum);
				
				// kimsoeun GS인증용  - 작업 완료 -> 저장 완료 문구 변경
				SC.say(I18N.message("savecompleted"));
				Log.info(I18N.message("addupdate") + " " + I18N.message("operationcompleted"), null);
				
				validator.setMap(form, chkDownload.getValueAsBoolean());
			}
		});
	}	
	
	private void executeRemove(final long id)	{
		//GWT.log("[ FileTypePanel executeRemove ] id["+id+"]", null);
		
		if(id < 0) return;
		
		ServiceUtil.documentcode().deleteFileType(Session.get().getSid(), id, new AsyncCallbackWithStatus<Void>() {
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
				Log.warn(caught.toString(), null);
			}
			@Override
			public void onSuccessEvent(Void result) {
				//GWT.log("[ FileTypePanel executeRemove ] onSuccess. id["+id+"]", null);
				
				grid.removeSelectedData();
				form.editNewRecord();
            	form.reset();
				form.getItem("id").setTooltip(I18N.message("generatedbyserver", form.getItem("id").getTitle()));
				
				// kimsoeun GS인증용  - 작업 완료 -> 저장 완료 문구 변경
				SC.say(I18N.message("savecompleted"));
				Log.info(I18N.message("remove") + " " + I18N.message("operationcompleted"), null);
			}
		});
	}
	
	private void executeGetOptionsAndSet() {		
		maxFileSizeOpts.clear();
		ServiceUtil.documentcode().listCodes(Session.get().getSid(), SCode.FILESIZE, new AsyncCallbackWithStatus<List<SCode>>() {
			@Override
			public String getSuccessMessage() {
				return "";
			}
			@Override
			public String getProcessMessage() {
				return "";
			}
			@Override
			public void onSuccessEvent(List<SCode> result) {
				if( result.size() > 0) {
					for(int j=0; j<result.size(); j++) {
						maxFileSizeOpts.put(result.get(j).getValue(), result.get(j).getName());
					}
				}
				maxFileSizeItem.setValueMap(maxFileSizeOpts);
				maxFileSizeItem.setDefaultToFirstOption(true);
			}			
			@Override
			public void onFailureEvent(Throwable caught) {
				Log.warn(caught.toString(), null);
			}
		});
		
//		documentCodeService.listCodes(Session.get().getSid(), SCode.VIEWER, new AsyncCallbackWithStatus<List<SCode>>() {
//			@Override
//			public String getSuccessMessage() {
//				return "";
//			}
//			@Override
//			public String getProcessMessage() {
//				return "";
//			}
//			@Override
//			public void onSuccessEvent(List<SCode> result) {
//				if( result.size() > 0) {
//					for(int j=0; j<result.size(); j++) {
//						viewerOpts.put(result.get(j).getValue(), result.get(j).getName());
//					}
//				}
//				//viewerItem.setValueMap(viewerOpts);
//			}			
//			@Override
//			public void onFailureEvent(Throwable caught) {
//				Log.warn(caught.toString(), null);
//			}
//		});
	}
	
	/**
	 * 뷰어의 옵션 정보를 스트링으로 변환하여 반환
	 * @return
	 * sRtnValue : 0,1,2 혹은 ,,2 등...(숫자가 있으면 체크표시됨, 숫자없이 콤마만 있으면 체크표시안됨)
	 */
	private String getViewerOptionInfo(){
		
		String sRtnValue = "";
		
		if("true".equals(String.valueOf(chkOsDefalut.getValue()))){
			sRtnValue = sRtnValue + "0";
		}
		sRtnValue = sRtnValue + ",";
		if("true".equals(String.valueOf(chkViewer.getValue()))){
			sRtnValue = sRtnValue + "1";
		}
		sRtnValue = sRtnValue + ",";
		if("true".equals(String.valueOf(chkDownload.getValue()))){
			sRtnValue = sRtnValue + "2";
		}

		return sRtnValue;
	}
	
	/**
	 * 스트링으로부터 뷰어의 옵션 체크 정보를 변환하여 적용
	 */
	private void setViewerOptionInfo(String sInfoData){
		
		String sData = sInfoData;
		if(null == sData){
			sData = ",,";	//기본값 세팅
		}
		
		String[] arrData = sData.split(",");
		if (arrData != null) {
			for (int i = 0; i < arrData.length; i++) {
				if (arrData[i].length() < 1)
					continue;
				
				switch (Integer.valueOf(arrData[i])){
					case 0		: chkOsDefalut.setValue(true);
							  	  break;
							  	  
					case 1		: chkViewer.setValue(true);
								  break;
							  	  
					case 2		: chkDownload.setValue(true);
								  break;
							  	  
					default		: 
								  break;
				}
			}
		}
		
		// 20130725, junsoo, StringTokenizer 는 gwt가 지원하지 않으므로 삭제함
//		StringTokenizer st = new StringTokenizer(sData, ",");
//		String[] arrData = new String[st.countTokens()];
//		int iIndex = 0;
//
//		while(st.hasMoreTokens()){
//			arrData[iIndex] = st.nextToken();
//		
//			switch (Integer.valueOf(arrData[iIndex])){
//				case 0		: chkOsDefalut.setValue(true);
//						  	  break;
//						  	  
//				case 1		: chkViewer.setValue(true);
//							  break;
//						  	  
//				case 2		: chkDownload.setValue(true);
//							  break;
//						  	  
//				default		: 
//							  break;
//			}
//
//			iIndex++;
//		}
	}
	
	/**
	 * 뷰어의 체크옵션 정보와 체크박스 이름을 이용하여 그리드상의 체크박스 제어
	 * @return
	 * rtnVal : true or false
	 */
	private Boolean getChkBoxValueByName(String sInfoData, String sChkBoxName){
		
		Boolean rtnVal = false;
		
		String sData = sInfoData;
		if(null == sData){
			sData = ",,";	//기본값 세팅
		}
		
		int iIndex = 0;
		String[] arrData = sData.split(",");
		
//		StringTokenizer st = new StringTokenizer(sData, ",");
//		String[] arrData = new String[st.countTokens()];
//		int iIndex = 0;
//
//		while(st.hasMoreTokens()){
//			arrData[iIndex] = st.nextToken();
//			iIndex++;
//		}
		
		
		
		String sFlag = "";
		
		if("fieldOsDefault".equals(sChkBoxName)){
			sFlag = "0";
		}else if("fieldViewer".equals(sChkBoxName)){
			sFlag = "1";
		}else if("fieldDownload".equals(sChkBoxName)){
			sFlag = "2";
		}
		
		
		for(iIndex=0; iIndex<arrData.length; iIndex++){
			if(sFlag.equals(arrData[iIndex])){
				rtnVal = true;
				break;
			}
		}
		
		return rtnVal;
	}
	
}