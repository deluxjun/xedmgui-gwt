package com.speno.xedm.gui.frontend.client.document.prop;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.RecordList;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.ListGridEditEvent;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.events.ShowContextMenuEvent;
import com.smartgwt.client.widgets.events.ShowContextMenuHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.ValuesManager;
import com.smartgwt.client.widgets.form.fields.CheckboxItem;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.form.fields.TextAreaItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.EditCompleteEvent;
import com.smartgwt.client.widgets.grid.events.EditCompleteHandler;
import com.smartgwt.client.widgets.grid.events.RemoveRecordClickEvent;
import com.smartgwt.client.widgets.grid.events.RemoveRecordClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.menu.Menu;
import com.speno.xedm.core.service.serials.SContent;
import com.speno.xedm.core.service.serials.SDocType;
import com.speno.xedm.core.service.serials.SDocument;
import com.speno.xedm.core.service.serials.SFileType;
import com.speno.xedm.core.service.serials.SFolder;
import com.speno.xedm.core.service.serials.SRetentionProfile;
import com.speno.xedm.core.service.serials.SVersion;
import com.speno.xedm.gui.common.client.Constants;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.Session;
import com.speno.xedm.gui.common.client.util.ItemFactory;
import com.speno.xedm.gui.common.client.util.LengthValidator;
import com.speno.xedm.gui.common.client.util.ReturnHandler;
import com.speno.xedm.gui.common.client.util.ServiceUtil;
import com.speno.xedm.gui.common.client.util.Util;
import com.speno.xedm.gui.frontend.client.document.DocumentActionUtil;
import com.speno.xedm.gui.frontend.client.document.DocumentObserver;
import com.speno.xedm.gui.frontend.client.document.DocumentsPanel;

/**
 * Modified :
 * 		20130816, junsoo, doctype 변경은 update권한만 있으면 가능하도록 변경.
 * @author deluxjun
 *
 */
public class StandardPropertiesPanel extends DocumentDetailTab {

	private DynamicForm form1;

	private DynamicForm form2;

	private VLayout container = new VLayout();

	private HLayout formsContainer = new HLayout();
	
	private VLayout rightContainer = new VLayout();


	private ValuesManager vm = new ValuesManager();

	private DynamicForm pathForm = new DynamicForm();

	private StaticTextItem id;
	private StaticTextItem creation;
	private StaticTextItem lockDeadline;
	private StaticTextItem creator;
	private StaticTextItem lockUser;
	private TextItem title;
	private StaticTextItem version;
	private CheckboxItem versionControl;
	
	
	private TextAreaItem description;// = new TextAreaItem();

	// 속성창 제목
//	private StaticTextItem titledoc; 
	// 생성일자
	private Date create;
	// form1_1 과 form1_2 에 각각 담을 생성일자
	private StaticTextItem createdText;
	
	// doctype
	private StaticTextItem doctype;
	private SelectItem doctypeSelect;

	// 오너
	private StaticTextItem owner;
	// 수정일자
	private Date modifi;
	// form1_2 에 담을 수정일자
	private StaticTextItem modified;
	// retention
	private StaticTextItem retention;
	private SelectItem retentionSelect;
	// 딸린파일
	private StaticTextItem attachfiles;
	private Label lblProperties;
	private Label lblExtentionProperties;

	// 키워드
	private TextItem searchKeyWord;
	
	// 딸린파일 표시
	ListGrid attach;
	// 확장 속성 표시 grid
	ListGrid extentionGrid;
	// 왼쪽 layout에 들어가는 form
	DynamicForm leftForm;
	
	private boolean beModifiedRetention = false;
	private boolean beModifiedDocType = false;
	
	protected DocumentObserver observer;
	
	protected ChangedHandler docTypeChangedHandler;

	private Map<String,SDocType> currentDocTypes = new LinkedHashMap<String,SDocType>();
	private Map<String,SFileType> currentFileTypes = new LinkedHashMap<String,SFileType>();
	private LinkedHashMap<String, SRetentionProfile> allRetentions = new LinkedHashMap<String, SRetentionProfile>();

	private boolean recordEditable = false;
	
	// 이 속성창에서 상태가 바뀌었을 경우, 이벤트를 받아갈 곳
	private ChangedHandler statusChangedListener;
	public void setStatusChangedListener(ChangedHandler listener){
		statusChangedListener = listener;
	}
	

	public StandardPropertiesPanel(final SDocument document, ChangedHandler changedHandler, DocumentObserver observer, boolean recordEditable) {
		super(document, changedHandler);
		this.observer = observer;
		this.recordEditable = recordEditable;
		
		setWidth100();
		setHeight100();
		container.setWidth100();
//		container.setMembersMargin(5);
		addMember(container);

		formsContainer.setWidth100();
//		formsContainer.setMembersMargin(10);

		
		prepareLeftForm();
		refreshAttachGrid(recordEditable);

//		refresh();
	}
	
	public void setDocTypeChangedHandler(ChangedHandler docTypeChangedHandler) {
		this.docTypeChangedHandler = docTypeChangedHandler;
	}


	@Override
	public void refresh() {
		vm.clearErrors(false);

		// path
		if (pathForm != null && container.contains(pathForm)) {
			container.removeMember(pathForm);
			pathForm.destroy();
		}

		if (!isNew()) {
			String path = "";
			if (document.getPathExtended() != null)
				path = document.getPathExtended();
			
			StaticTextItem pathItem = ItemFactory.newStaticTextItem("path", "path", path);

//			LinkItem pathItem = ItemFactory.newLinkItem("path", path);
//			pathItem.setTitle(I18N.message("path"));
//			pathItem.addChangedHandler(changedHandler);
//			String pathUrl = Util.contextPath() + "download?docId=" + document.getId();
//			pathItem.setValue(pathUrl);
//			pathItem.setWidth(400);
			
	
	//		String downloadUrl = Util.contextPath() + "download?docId=" + document.getId();
	//		LinkItem download = ItemFactory.newLinkItem("download", downloadUrl);
	//		download.setTitle(I18N.message("download"));
	//		download.setValue(downloadUrl);
	//		download.addChangedHandler(changedHandler);
	//		download.setWidth(400);
	
	//		pathForm.setItems(pathItem, download);
			
			pathForm.setItems(pathItem);
		} else {
			// 신규 문서일 경우
			setLayoutTopMargin(-12);
			beModifiedRetention = true;
		}

		// 속성 업데이트!
		id.setValue(Long.toString(document.getId()));

		if (document.getCreationDate() != null)
			creation.setValue(Util.getFormattedDate(document.getCreationDate(), true));
		
		creator.setValue(document.getCreateUserName());
		
		if (SDocument.DOC_CHECKED_OUT == document.getStatus() || SDocument.DOC_LOCKED == document.getStatus()) {
			lockUser.setValue(document.getLockUserName());
			if (document.getDeadLine() != null) {
				lockDeadline.setValue(Util.getFormattedDate(document.getDeadLine(), true));
			}
		}
		
		title.setValue(document.getTitle());
		description.setValue(document.getComment());
		versionControl.setValue((document.getVersionControl() != null && document.getVersionControl() == 1)? true: false);
		version.setValue(document.getVersion());
		searchKeyWord.setValue(document.getKeyword());
		
//		title.setLength(Session.get().getInfo().getIntConfig("gui.title.fieldsize", 255));
		title.setValidators(new LengthValidator(title, Session.get().getInfo().getIntConfig("gui.title.fieldsize", 255)));
//		description.setLength(Session.get().getInfo().getIntConfig("gui.comment.fieldsize", 1000));
		description.setValidators(new LengthValidator(description, Session.get().getInfo().getIntConfig("gui.comment.fieldsize", 1000)));
//		searchKeyWord.setLength(Session.get().getInfo().getIntConfig("gui.keyword.fieldsize", 255));
		searchKeyWord.setValidators(new LengthValidator(searchKeyWord, Session.get().getInfo().getIntConfig("gui.keyword.fieldsize", 255)));

		// retention Date 설정
		if (document.getCreationDate() != null && document.getExpireDate() != null)
			retention.setValue(Util.getFormattedExpireDate(document.getCreationDate(), document.getExpireDate()));
		// 최종수정일자
		if (document.getLastModified() != null)
			modified.setValue(Util.getFormattedDate(document.getLastModified(), true));
		
		// doctype
		doctype.setValue(document.getDocTypeName());
		
		// get retentions (보존기한이 더 큰 retention만 설정)
		ServiceUtil.getAllRetentions(new ReturnHandler<List<SRetentionProfile>>() {
			@Override
			public void onReturn(List<SRetentionProfile> param) {
				long oldRetention = Util.getRetentionFromExpireDate(document.getCreationDate(), document.getExpireDate());
				
				allRetentions.clear();
				LinkedHashMap<String, String> list = new LinkedHashMap<String, String>();
				for (SRetentionProfile retention : param) {
					String key = retention.getRetention() + "," + Long.toString(retention.getId());
					
					// retention 이 더 큰 profile 만 추가. (즉, 연장만 가능)
					// 20130819, junsoo, 보존기간 축소도 가능하게 함.
//					if (retention.getRetention() > oldRetention)
					list.put(key, retention.getName() + " (" + retention.getRetention() + ")" );
					
					allRetentions.put(Long.toString(retention.getId()), retention );
				}

				retentionSelect.setValueMap(list);
//				retentionSelect.setSortField("");
				
				
				// 비동기 방식이므로, 다음 액션을 여기서 수행.
				// doctype 설정
				if (update) {
					refreshAvailableDocTypes();
					if (document.getDocType() != null)
						doctypeSelect.setValue(document.getDocType().toString());
				}

				// 첨부파일 그리드 편집가능 할 때만 로드
//				if (recordEditable)
					refreshAvailableFileTypes();
			}
		});
		

		// 표시 여부 설정
		setVisible();
		
		// right area
		// set grid data
		List<ListGridRecord> extRecords = new ArrayList<ListGridRecord>();
//		
		SContent[] contents = document.getContents();
		String[] ecmFields = null;
		
		// set field
		if (contents != null && contents.length > 0) {
			SContent firstContent = contents[0];
			ecmFields = firstContent.getFieldNames();
			
			for (SContent content : contents) {
				ListGridRecord contentRecord = new ListGridRecord();
				contentRecord.setAttribute("elementId", content.getElementId());
				contentRecord.setAttribute("type", content.getIcon());
				contentRecord.setAttribute("filename", content.getFileName() + " (" + Util.setFileSize(content.getFileSize(), true) + ")");
				contentRecord.setAttribute("realfilename", content.getFileName());
//				contentRecord.setAttribute("size", Util.setFileSize(content.getFileSize(), true));
				for (int i = 0; i < content.getFieldNames().length; i++) {
					contentRecord.setAttribute(content.getFieldNames()[i], content.getFieldValues()[i]);
				}
				extRecords.add(contentRecord);
			}
		}

		refreshGridFields(ecmFields);
//		attach.setFields(extFields.toArray(new ListGridField[0]));
		attach.setRecords(extRecords.toArray(new ListGridRecord[0]));
		
		// 권한!!
		title.setDisabled(!update || !rename);
		description.setDisabled(!update);
		searchKeyWord.setDisabled(!update);
		doctypeSelect.setDisabled(!update);
		doctypeSelect.setVisible(!doctypeSelect.getDisabled());
		retentionSelect.setDisabled(!update || !extend);
		retentionSelect.setVisible(!retentionSelect.getDisabled());
		attach.setCanEdit(update);
		// 최종
		formsContainer.setMembers(form1, rightContainer);
		
		container.setMembers(pathForm, formsContainer);
	}
	
	/*
	 *  갱신! 현재 폴더에서 가능한 문서형식들..
	 *  1. 개인 문서 폴더의 경우 사용 가능한 모든 문서 형식으로 등록이 가능해야한다.
	 *  2. 공유 문서 폴더의 경우 
	 *  	1) 기본 등록
	 *  		- 사용 가능한 문서 형식에 대해서 등록이 가능.
	 *  	2) 기안 등록
	 *  		- 사용 가능한 문서 형식에 등록 결재 권한이 없을 경우 문서형식 목록에 나타나지 않아야 한다.
	 *  20131205, junsoo, firstDocTypeId 추가하여 디폴트값 세팅되도록함.
	 */
	public void refreshAvailableDocTypes(){
		currentDocTypes.clear();
		ServiceUtil.getAvailableSDocTypes(document.getFolder(), new ReturnHandler<SDocType[]>() {
			@Override
			public void onReturn(SDocType[] result) {
				String firstDocTypeId = "";
				LinkedHashMap<String, String> doctypeMap = new LinkedHashMap<String, String>();
				for (int i = 0; i < result.length; i++){
					String key = result[i].getId().toString();

					// 현재 위치가 공유 문서일 경우
					if(DocumentActionUtil.get().getActivatedMenuType() == DocumentActionUtil.TYPE_SHARED){
						if(DocumentsPanel.get().getApprovePopup() != null){
							// 기안창의 경우 문서 형식에 등록 결재 권한이 없을경우 문서형식 목록에 나타나지 않아야 한다.
							if(result[i].getRewriteCmd()!=null && result[i].getRewriteCmd().contains(String.valueOf(Constants.DRAFT_TYPE_REGISTRATION))){
								currentDocTypes.put(key, result[i]);
								if (firstDocTypeId.length() < 1)
									firstDocTypeId = result[i].getId().toString();
								doctypeMap.put(key, result[i].getName());
							}
						}
						// 기안 창이 아닐경우 가능한 문서 형식에 대해서 등록이 가능하다.
						else{
							currentDocTypes.put(key, result[i]);
							if (firstDocTypeId.length() < 1)
								firstDocTypeId = result[i].getId().toString();
							doctypeMap.put(key, result[i].getName());
						}
					}
					// 현재 위치가 개인 문서일 경우 사용 가능한 문서 형식에 대해서 모두 사용 가능해야 한다.
					else{
						currentDocTypes.put(key, result[i]);
						if (firstDocTypeId.length() < 1)
							firstDocTypeId = result[i].getId().toString();
						doctypeMap.put(key, result[i].getName());
					}
				}
				
				doctypeSelect.setValueMap(doctypeMap);
				
				// 첫번째 doctype선택!
				// 20130819, junsoo, 기존 doctype이 설정되어 있지 않을 때에만. 첫번째 doctype선택
				if (currentDocTypes.size() > 0 && document.getDocType() == null) {
					doctypeSelect.setValue(firstDocTypeId);
						onDocTypeChanged();
//					doctype.setValue(result[0].getName());
				}
				
				if(currentDocTypes.size() == 0) observer.onReloadRequest(null);
			}
		});
	}
	
	// 갱신! 현재 폴더에서 가능한 문서형식들..
	public void refreshAvailableFileTypes(){
		currentFileTypes.clear();
		ServiceUtil.getAvailableSFileTypes(document.getFolder(), new ReturnHandler<SFileType[]>() {
			@Override
			public void onReturn(SFileType[] result) {
				for (int i = 0; i < result.length; i++){
					String key = result[i].getName();
					currentFileTypes.put(key, result[i]);
				}
				
				if (statusChangedListener != null)
					statusChangedListener.onChanged(null);
			}
		});
	}
	
	public void changeFolder(SFolder folder){
		refreshAvailableDocTypes();
		refreshAvailableFileTypes();
		
//		doctype.setValue("");
	}
	
	private String[] indexFieldNames;
	private void refreshGridFields(String[] fields) {
		List<ListGridField> extFields = new ArrayList<ListGridField>();

		ListGridField elementId = new ListGridField("elementId", I18N.message("elementId"));
		elementId.setHidden(true);
		
		ListGridField type = new ListGridField("type", "type", 20);
		type.setShowTitle(false);
		type.setType(ListGridFieldType.IMAGE);
		type.setImageURLPrefix(Util.imagePrefix());
		type.setAlign(Alignment.CENTER);
		type.setAutoFitWidth(true);
		type.setCanFilter(false);
		type.setCanEdit(false);
		
		ListGridField filename = new ListGridField("filename", I18N.message("filename"));
		filename.setType(ListGridFieldType.TEXT);
		filename.setAlign(Alignment.CENTER);
		filename.setCanFilter(false);
		filename.setCanEdit(false);
		filename.setAutoFitWidth(true);
		filename.setAlign(Alignment.LEFT);

		extFields.add(elementId);
		extFields.add(type);
		extFields.add(filename);

		if (fields != null) {
			// 20140107, junsoo, save ecm index names
			indexFieldNames = fields;
			for(int j = 0 ; j < fields.length ; j++){
				ListGridField contentField = new ListGridField(fields[j]);
				extFields.add(contentField);
			}
		}
		
		attach.setFields(extFields.toArray(new ListGridField[0]));
	}
	
	/**
	 * add content to attach list. 신규문서 등록
	 * @param content
	 */
	public void addContent(SContent content){
		ListGridRecord contentRecord = new ListGridRecord();
//		contentRecord.setAttribute("elementId", content.getElementId());
		contentRecord.setAttribute("type", Util.getIconByExt(Util.getExtByFileName(content.getFileName())));
		contentRecord.setAttribute("filename", content.getFileName() + " (" + Util.setFileSize(content.getFileSize(), true) + ")");
		contentRecord.setAttribute("realfilename", content.getFileName());
		
		// 20140107, junsoo, 초기화 ecm index field
//		if (indexFieldNames != null && indexFieldNames.length > 0) {
//			content.setFieldNames(indexFieldNames);
//		}
		for (int i = 0; i < content.getFieldNames().length; i++) {
			contentRecord.setAttribute(content.getFieldNames()[i], content.getFieldValues()[i]);
		}
		attach.addData(contentRecord);
		document.addContent(content);
		
		// 변경되었으므로 통보!
		changedHandler.onChanged(null);
	}
	
	/**
	 * remove content from attach list
	 * @param content
	 */
	public void removeContent(String fileName){
		// 그리드에서 해당건 빼기
		RecordList rclist = attach.getDataAsRecordList();
		Record rc = rclist.find("realfilename", fileName);
		if(rc != null){
			attach.removeData(rc);
			document.removeContent(fileName);

			// 변경되었으므로 통보!
			changedHandler.onChanged(null);

		}
	}
	
	/**
	 * 파일 validation 체크. 현재는 파일 확장자만 체크
	 * @param fileName
	 * @return
	 */
	public boolean validateFileName(String fileName) {
		boolean ok = false;
		// 파일 확장자 체크
		for (SFileType fileType : currentFileTypes.values()) {
			if (fileType.getName().equalsIgnoreCase(Util.getExtByFileName(fileName))) {
				ok = true;
				break;
			}
		}
		
		if (ok) {
			// 파일명 체크 (이미 존재하는지)
			RecordList rclist = attach.getDataAsRecordList();
			Record rc = rclist.find("realfilename", fileName);
			if (rc != null)
				ok = false;
		}
		
		return ok;
	}

	/**
	 * 허용되는 파일 타입들을 표시
	 * @return
	 */
	public String getAvailableFileTypes(){
		StringBuffer buffer = new StringBuffer();
		for (SFileType fileType : currentFileTypes.values()) {
			buffer.append(fileType.getName() + ", ");
		}
		
		// , 제거
//		if (buffer.length() > 2);
//			buffer.setLength(buffer.length()-2);
		
		return buffer.toString();
	}
	
	/**
	 * 허용되는 파일 타입 리턴.
	 * @return
	 */
	public String[] getAvailableFileTypesArray(){
		String[] types = new String[currentFileTypes.size()];
		int i = 0;
		for (SFileType fileType : currentFileTypes.values()) {
			types[i++] = fileType.getName().toLowerCase().trim();
		}
		return types;
	}
	
	@Override
	protected void updatePermission() {
		super.updatePermission();
		
		// 신규등록이면 가능해야 할 권한들.
		if (isNew()) {
			update = true;
			rename = true;
			control = true;
		}
	}	
	
	// item의 표시 여부 설정
	private void setVisible() {
		if (isNew())
			id.setVisible(false);
		if (document.getCreationDate() == null)
			creation.setVisible(false);
		if (document.getCreateUserName() == null || document.getCreateUserName().length() < 1)
			creator.setVisible(false);
		if (document.getLockUserName() == null || document.getLockUserName().length() < 1) {
			lockUser.setVisible(false);
			lockDeadline.setVisible(false);
		}
		if (document.getVersion() == null || document.getVersion().length() < 1)
			version.setVisible(false);
		if (document.getLastModified() == null)
			modified.setVisible(false);
	}
	
	// 속성 셋팅
	private void prepareLeftForm(){
		
		form1 = new DynamicForm();
		form1.setNumCols(2);
		form1.setValuesManager(vm);
		form1.setTitleOrientation(TitleOrientation.LEFT);
		form1.setWidth(300);
		form1.setTitleWidth(100);
		form1.setAutoFocus(true);

		id = ItemFactory.newStaticTextItem("id", "id", Long.toString(document.getId()));

		DateTimeFormat formatter = DateTimeFormat.getFormat(I18N.message("format_date"));
//		creation = ItemFactory.newStaticTextItem("creation", "createdon", formatter.format((Date) document.getCreationDate()));
		creation = ItemFactory.newStaticTextItem("creation", "createdon", null);
		
		lockDeadline = ItemFactory.newStaticTextItem("lockDeadline", "lockDeadline", null);
		lockDeadline.setTitleStyle("boldblack");

		creator = ItemFactory.newStaticTextItem("creator", "creator", document.getCreateUserName());

		lockUser = ItemFactory.newStaticTextItem("lockUser", "second.lockUser", null);

		title = ItemFactory.newTextItem("title", "title", document.getTitle());
		setTextitem(title, true);
		
		title.addChangedHandler(new ChangedHandler() {
			@Override
			public void onChanged(ChangedEvent event) {
				if (isNew())
					document.setTitle((String)title.getValue());
				if(title.getValueAsString() == null || title.getValueAsString().length() > 0) 
					changedHandler.onChanged(event);
			}
		});
		title.setRequired(true);

//		leftForm = new DynamicForm();
//		leftForm.setNumCols(4);
//		leftForm.setColWidths("25","25","25","*");

		StaticTextItem dummy = ItemFactory.newStaticTextItem("dummy", "dummy", "");
		setTextitem(dummy, true);
		dummy.setVisible(false);
		
//		titledoc = ItemFactory.newStaticTextItem("titledoc", "titledoc", document.getTitle());
//		setTextitem(titledoc, true);
//		titledoc.setShowTitle(false);
//		titledoc.setCellStyle("propertyTitle");
//		titledoc.setVisible(false);
		
		doctype = ItemFactory.newStaticTextItem("doctype", "doctype", document.getDocTypeName());
		setTextitem(doctype, true);
		doctypeSelect = new SelectItem("doctypeCombo", I18N.message("second.changeDoctype"));
		doctypeSelect.setShowTitle(true);
		doctypeSelect.setWidth(120);
		doctypeSelect.setAddUnknownValues(false);
		doctypeSelect.addChangedHandler(new ChangedHandler() {
			@Override
			public void onChanged(ChangedEvent event) {
				//20130819, junsoo, 기존 doctype과 같은 것을 선택하면 이벤트 호출하지 않음.
				if (onDocTypeChanged())
					changedHandler.onChanged(null);
			}
		});


		versionControl = ItemFactory.newCheckbox("versionControl", "second.versioncontrol");
		versionControl.disable();
		
		version = ItemFactory.newStaticTextItem("version", "version", document.getVersion());
		setTextitem(version, true);
		
		owner = ItemFactory.newStaticTextItem("owner", "owner", document.getCreateUserName());
		setTextitem(owner, true);
		
		create = document.getCreationDate();
//		createdText = ItemFactory.newStaticTextItem("create", "crate", document.getCreationDate().toString());
		createdText = ItemFactory.newStaticTextItem("create", "crate", null);
		setTextitem(createdText, true);
		
		modifi = document.getLastModified();
//		if(modifi != null) modified = ItemFactory.newStaticTextItem("modifiedon", "modifiedon", modifi.toString());
//		else modified = ItemFactory.newStaticTextItem("modifiedon", "modifiedon", create.toString());
		if(modifi != null) modified = ItemFactory.newStaticTextItem("modifiedon", "modifieddate", null);
		else modified = ItemFactory.newStaticTextItem("modifiedon", "modifiedon", null);
		setTextitem(modified, true);
		
		description = ItemFactory.newTextAreaItemWithInputBox("description", "description",document.getComment(), 240, 30,
				Session.get().getInfo().getIntConfig("gui.comment.fieldsize", 1000));
		setTextitem(description, true);
		description.addChangedHandler(changedHandler);

		retention = ItemFactory.newStaticTextItem("retention", "retention", "");
		setTextitem(retention, true);
		retentionSelect = new SelectItem("retentionCombo", I18N.message("second.changeRetention"));
		retentionSelect.setShowTitle(true);
		retentionSelect.setWidth(120);
//		retentionSelect.setAddUnknownValues(false);
		retentionSelect.addChangedHandler(new ChangedHandler() {
			@Override
			public void onChanged(ChangedEvent event) {
				// set changed
				beModifiedRetention = true;
				
				String key = retentionSelect.getValue().toString();
				SRetentionProfile profile = allRetentions.get(key.substring(key.indexOf(",") +1));
				document.setExpireDate(Util.getExpireDateFromRetention(null, profile.getRetention()));
				
				changedHandler.onChanged(event);
			}
		});

		searchKeyWord = ItemFactory.newTextItem("searchKeyWord", I18N.message("second.searchKeyWord"), "");
		setTextitem(searchKeyWord, true);
		searchKeyWord.addChangedHandler(changedHandler);
//		searchKeyWord.setHint("<nobr>" + I18N.message("second.client.commaSeperated") + "</nobr>");
//		FormItem searchHint = new FormItem();
//		
//		searchHint.setHint("<nobr>" + I18N.message("second.client.commaSeperated") + "</nobr>");
		StaticTextItem searchHint = ItemFactory.newStaticTextItem("", "", "");
		setTextitem(searchHint, true);
		searchHint.setShowTitle(false);
		searchHint.setWidth(30);
		searchHint.setHint("<nobr>" + I18N.message("second.client.commaSeperated") + "</nobr>");
		
		
//		retention.setColSpan(2);
		
		form1.setItems(id, title, versionControl, version, creation, creator, lockUser, lockDeadline,
				doctype, doctypeSelect,
				modified,
				retention, retentionSelect,
				description, 
				searchKeyWord, searchHint					
				);

	}
	
	// 문서 기안시 호출하기 위해서 public 선언
	public boolean onDocTypeChanged(){
		//20130819, junsoo, 기존 doctype과 같은 것을 선택하면 변경이 없는 것으로 처리.
		Long docTypeId = Long.parseLong(doctypeSelect.getValueAsString());
		
		// set changed
		beModifiedDocType = true;

		SDocType docType = currentDocTypes.get(docTypeId.toString());

		// 20130912 taesu, 생성 기안이 아닐경우 호출하지 않게 변경
		if(DocumentsPanel.get().getApprovePopup() != null
				&& DocumentsPanel.get().getApprovePopup().getUploadPanel() != null 
				&& (!DocumentsPanel.get().getApprovePopup().getUploadPanel().getPropertiesTab().getDisabled()))
			DocumentsPanel.get().getApprovePopup().getApprovePanel().getDocTypeInfo(docType.getId());

		// 변경되지 않았으면 종료
		if (document != null && document.getDocType() == docTypeId){
			beModifiedDocType = false;
			return false;
		}

		// 20130819, junsoo, doctype 에 따라 버전관리 여부도 화면 갱신.
		versionControl.setValue((docType.getVersionControl() != null && docType.getVersionControl() == 1)? true: false);

		// 신규등록일때만 실행.
		if (isNew()) {
			ServiceUtil.getECMIndexFields(docType.getIndexId(), new ReturnHandler<List<String>>() {
				@Override
				public void onReturn(List<String> param) {
					refreshGridFields(param.toArray(new String[0]));
				}
			});
			
			// 등록일 때, 문서유형이 변경됨에따라 확장 속성탭의 template id도 변경되도록 하기 위함.
			document.setTemplateId(docType.getTemplateId());
			if (docTypeChangedHandler != null)
				docTypeChangedHandler.onChanged(null);
			
			// 화면의 retention 변경.
			if (docType.getRetentionId() != 0L) {
//				retentionSelect.setValue(Long.toString(docType.getRetentionId()));
				try {
					long retentionPeriod = allRetentions.get(Long.toString(docType.getRetentionId())).getRetention();
					retention.setValue(Util.getFormattedExpireDate(new Date(), Util.getExpireDateFromRetention(null, retentionPeriod)));
					retentionSelect.setValue(retentionPeriod + "," + Long.toString(docType.getRetentionId()));
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				retention.setValue("");
			}
			
			// 화면의 doctype 변경
			doctype.setValue(docType.getName());

			// uploader 활성화를 위해 세팅.
			document.setDocType(docTypeId);
			
//			document.setExpireDate(Util.getExpireDateFromRetention(null, docType.getRetentionPeriod()));
		}
		
		return true;

	}
	
	private void setTextitem(FormItem txtItem, boolean isEnd){
		txtItem.setAlign(Alignment.LEFT);
		txtItem.setWidth(150);
		txtItem.setStartRow(isEnd);
		txtItem.setEndRow(isEnd);
		if(isEnd)
			txtItem.setColSpan(4);
//		txtItem.setWrap(false);
	}


	private void refreshAttachGrid(boolean recordEditable) {
		
		// 그리드 생성
		if (attach != null && rightContainer.contains(attach)){
			rightContainer.removeMember(attach);
			attach.destroy();
		}
		
		attach = new ListGrid();
		attach.setWidth100(); 
		attach.setHeight100();
		attach.setEmptyMessage(I18N.message("notitemstoshow"));
		attach.setShowRecordComponents(true);
		attach.setShowRecordComponentsByCell(true);
		attach.setCanFreezeFields(false);
		attach.setFilterOnKeypress(true);
		attach.setWrapCells(false);
		attach.setBodyOverflow(Overflow.SCROLL);
		attach.setEditEvent(ListGridEditEvent.DOUBLECLICK);
		
		// TODO: 마우스 우클릭 동작
//		attach.addRightMouseDownHandler(new RightMouseDownHandler() {
//			@Override
//			public void onRightMouseDown(final RightMouseDownEvent event) {
//				ListGridRecord record = attach.getSelectedRecord();
//				if(attach.getEventRow()>=0)  {
//					String elementId = record.getAttributeAsString("elementId");
//					String fileName = record.getAttributeAsString("realfilename");
//					ItemFactory.setFileMenuByExt(document, elementId, Util.getExtByFileName(fileName), new ReturnHandler() {
//						@Override
//						public void onReturn(Object param) {
////							attach.setContextMenu((Menu)param);
//							Menu menu = (Menu)param;
//							menu.setTop(event.getY());
//							menu.setLeft(event.getX());
//							menu.show();
//
//						}
//					});
//				}
//				setContextMenu(null);
//				event.cancel();
//			}
//		});
		attach.addShowContextMenuHandler(new ShowContextMenuHandler() {
			@Override
			public void onShowContextMenu(final ShowContextMenuEvent event) {
				ListGridRecord record = attach.getSelectedRecord();
				if(attach.getEventRow()>=0)  {
					String elementId = record.getAttributeAsString("elementId");
					String fileName = record.getAttributeAsString("realfilename");
					if (document instanceof SVersion)
						GWT.log("is version");
					ItemFactory.setFileMenuByExt(document, document.getContent(fileName), elementId, Util.getExtByFileName(fileName), new ReturnHandler() {
						@Override
						public void onReturn(Object param) {
							Menu menu = (Menu)param;
							menu.setTop(event.getY());
							menu.setLeft(event.getX());
							menu.show();

						}
					});
				}
				setContextMenu(null);
				event.cancel();

			}
		});
		
		attach.addEditCompleteHandler(new EditCompleteHandler() {
			@Override
			public void onEditComplete(EditCompleteEvent event) {
				// call super's modified function
				changedHandler.onChanged(null);
			}
		});
		
		attach.setCanRemoveRecords(recordEditable);
		if (recordEditable) {
	        // 레코드 제거
	        attach.addRemoveRecordClickHandler(new RemoveRecordClickHandler() {
				@Override
				public void onRemoveRecordClick(RemoveRecordClickEvent event) {
					ListGridRecord record = attach.getRecord(event.getRowNum());
					String fileName = record.getAttribute("realfilename");
					document.removeContent(fileName);
					changedHandler.onChanged(null);
				}
			});
		}
		
		rightContainer.setMembers(attach);
//		form2.setItems(attach);

	}

//	// TODO: ecm fields
//	private List<String> indexFields = null;
//	private void setECMIndexFields(final SDocument document) {
//		indexFields = (List<String>) DataCache.get(DataCache.INDEXFIELDS.getId() + document.getDocTypeName());
//		documentCodeService.listXvarmIndexFieldsByDocTypeId(Session.get().getSid(), document.getDocType(), new AsyncCallback<List<String>>() {
//			@Override
//			public void onSuccess(List<String> result) {
//
//				// save to cache
//				DataCache.put(DataCache.INDEXFIELDS.getId() + document.getDocTypeName(), result);
//			}
//			@Override
//			public void onFailure(Throwable caught) {
//				Log.error("Server Error", caught.toString(), false);
//			}
//		});
//	}

	// validate 및 저장을 하기 위해 document에 세팅
	@SuppressWarnings("unchecked")
	public boolean validate() {
		Map<String, Object> values = (Map<String, Object>) vm.getValues();
		vm.validate();
		if (!vm.hasErrors()) {
			document.setTitle((String) values.get("title"));
			document.setComment((String) values.get("description"));
			document.setKeyword((String) values.get("searchKeyWord"));
			
			// set doctype
			if (beModifiedDocType && doctypeSelect.getValue() != null && !"".equals(doctypeSelect.getValue().toString())){
				SDocType docType = currentDocTypes.get(doctypeSelect.getValue().toString());
				document.setDocType(docType.getId());
				// TODO: set default life cycle id. 추후 직접 수정가능하도록 변경될 수 있음
				document.setLifecycleId(docType.getLifecycleId());
			}
			
			if (beModifiedRetention && retentionSelect.getValue() != null && !"".equals(retentionSelect.getValue().toString())) {
				String key = retentionSelect.getValue().toString();
				SRetentionProfile profile = allRetentions.get(key.substring(key.indexOf(",") +1));
				document.setExpireDate(Util.getExpireDateFromRetention(null, profile.getRetention()));
			}
			
			// save ecm fields
			int upCnt = attach.getRecords().length;
			SContent[] con = document.getContents();
			for(int i = 0; i< upCnt; i++){
				ListGridRecord rc = attach.getRecord(i);

				// 20140107, junsoo, 그리드에 실제 세팅된 값을 기준으로 세팅
				if (indexFieldNames != null && indexFieldNames.length > 0) {
					List<String> nameList = new ArrayList<String>();
					List<String> valueList = new ArrayList<String>();
					for (int j = 0; j < indexFieldNames.length; j++) {
						String value = rc.getAttribute(indexFieldNames[j]);
						if (value != null && value.length() > 0) {
							nameList.add(indexFieldNames[j]);
							valueList.add(value);
						}
					}
					
					con[i].setFieldNames(nameList.toArray(new String[0]));
					con[i].setFieldValues(valueList.toArray(new String[0]));
				}
				
//				String[] fieldvalue = new String[con[i].getFieldNames().length];
//				for(int j =0; j< fieldvalue.length; j++){
//					fieldvalue[j] = rc.getAttribute(con[i].getFieldNames()[j]);
//				}
//				con[i].setFieldValues(fieldvalue);
			}

		}
		return !vm.hasErrors();
	}
	

	/**
	 * DISABLED : 현재 화면에 표시되어 있는 값을 리턴
	 * @param id
	 * @return
	 */
	public String getDisplayedValue(String id) {
		return (String)((Map<String, Object>) vm.getValues()).get(id);
	}
	
	public void setTitle(String str){
		title.setValue(str);
	}
}