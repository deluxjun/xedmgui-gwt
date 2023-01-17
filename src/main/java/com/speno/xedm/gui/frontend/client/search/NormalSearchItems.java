package com.speno.xedm.gui.frontend.client.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.CheckboxItem;
import com.smartgwt.client.widgets.form.fields.ComboBoxItem;
import com.smartgwt.client.widgets.form.fields.DateItem;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.FormItemIcon;
import com.smartgwt.client.widgets.form.fields.PickerIcon;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.form.fields.events.ClickEvent;
import com.smartgwt.client.widgets.form.fields.events.ClickHandler;
import com.smartgwt.client.widgets.form.fields.events.FormItemClickHandler;
import com.smartgwt.client.widgets.form.fields.events.FormItemIconClickEvent;
import com.smartgwt.client.widgets.form.fields.events.KeyDownEvent;
import com.smartgwt.client.widgets.form.fields.events.KeyDownHandler;
import com.smartgwt.client.widgets.form.fields.events.KeyUpEvent;
import com.smartgwt.client.widgets.form.fields.events.KeyUpHandler;
import com.speno.xedm.core.service.serials.SDocument;
import com.speno.xedm.core.service.serials.SExtendedAttribute;
import com.speno.xedm.core.service.serials.SFolder;
import com.speno.xedm.core.service.serials.SHit;
import com.speno.xedm.core.service.serials.SSearchOptions;
import com.speno.xedm.core.service.serials.SSearches;
import com.speno.xedm.core.service.serials.STemplate;
import com.speno.xedm.core.service.serials.SUser;
import com.speno.xedm.gui.common.client.Constants;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.Session;
import com.speno.xedm.gui.common.client.log.Log;
import com.speno.xedm.gui.common.client.util.AsyncCallbackWithStatus;
import com.speno.xedm.gui.common.client.util.FixedTextItem;
import com.speno.xedm.gui.common.client.util.HistoryUtil;
import com.speno.xedm.gui.common.client.util.ItemFactory;
import com.speno.xedm.gui.common.client.util.LengthValidator;
import com.speno.xedm.gui.common.client.util.ReturnHandler;
import com.speno.xedm.gui.common.client.util.SearchUtil;
import com.speno.xedm.gui.common.client.util.ServiceUtil;
import com.speno.xedm.gui.common.client.util.Stack;
import com.speno.xedm.gui.common.client.util.Util;
import com.speno.xedm.gui.common.client.window.Waiting;
import com.speno.xedm.gui.frontend.client.PagingToolStrip;
import com.speno.xedm.gui.frontend.client.document.DocumentActionUtil;
import com.speno.xedm.gui.frontend.client.document.DocumentsGrid;
import com.speno.xedm.gui.frontend.client.document.DocumentsPanel;
import com.speno.xedm.util.paging.PagingConfig;
import com.speno.xedm.util.paging.PagingResult;
import com.speno.xedm.util.paging.SortDir;

public class NormalSearchItems{
	//	Form Item 선언
	private FixedTextItem titleText;
	
	private StaticTextItem commonDateColumn;
	
	private FixedTextItem fileNameText;
	private FixedTextItem keywordText;
	private FixedTextItem folderText;
	private FixedTextItem ownerText;
	
	private FormItemIcon templateAdd;
//	private FormItemIcon templateDelete;
	
	// Picker Icon
	private PickerIcon deleteSavedPicker;
	private PickerIcon searchPicker;
	private PickerIcon clearPicker;
	
	//folder, owner Search 아이콘 제거 요청에 의한 삭제
//	private PickerIcon folderSearchPicker;
	private PickerIcon folderCleanPicker;
	private PickerIcon ownerSearchPicker;
//	private PickerIcon ownerCleanPicker;
	private PickerIcon templateCleanPicker;

	private DateItem fcreateDate;
	private DateItem bcreateDate;
	private DateItem fmodifyDate;
	private DateItem bmodifyDate;
	private DateItem fexpireDate;
	private DateItem bexpireDate;
	
	private SelectItem savedSelect;
	private SelectItem sizeSelect;
	private SelectItem createDateSelect;
	private SelectItem modifyDateSelect;
	private SelectItem expireDateSelect;
	private SelectItem docTypeSelect;
	private SelectItem templateIdSelect;
	
	private ComboBoxItem fieldCombo;
	
	private CheckboxItem searchFolderCheck;
	private CheckboxItem searchContentCheck;
	
	private ButtonItem searchButton;
	private ButtonItem resetButton;
	
//	private SelectItem selectRange;
	
	// 템플릿 추가 속성용 아이템
	private SelectItem	exTemplateNameSelect; 	
	private SelectItem	exTemplateOperatorSelect;
	private FormItem	exTemplateFormItem;
	private SelectItem	exTemplateAndOrSelect;
	private PickerIcon exTemplateDeletePicker;


	private STemplate sTemplate;
	// DB에서 데이터를 읽을 Offset지정
	private int currentOffset = 0;
	// Paging 설정 정보 저장
	private PagingConfig config; 
	// Search Items 저장용 변수(다른 객체에서 get()을 통해 접근)
	private HashMap<String, FormItem> searchItemsMap;
	// 서버 전송용 변수
	private SFolder folder = null;
	// Search Option 변수(Document Tab과 Search Tab 구분용)
	private int searchOption = 0;
	// Filter 검색시 검색 조건 저장용 변수
	private SSearchOptions opt;
	/* Owner Popup 창에서 넘겨받는 owner 정보 저장용 변수
	 * 0: id, 1: name
	*/ 
	private String[][] ownerInfo = new String[1][2];
	// 검색 완료 후 데이터를 보여줄 Grid 설정 변수
	private	DocumentsGrid grid = null;
	// 선택된 Template의 과거값과 현재 값을 비교하여 Template 초기화 여부를 결정하기 위한 변수
	private STemplate beforeSTemplate;
	// 현재 템플릿에 해당하는 Type과 Name(Label)값들을 저장한다.
	private int[] exTypeValue;
	private String[] exNameValue;
	// 추가 템플릿 행 위치 이름(삭제동작을 위해 사용)
	int count = 0;
	// 추가 템플릿 Items 자료를 저장한다( 추가, 삭제, 데이터 전송용)
	private Map<Integer, FormItem[]> itemsMap = new HashMap<Integer, FormItem[]>();
	// 20130822, junsoo, 검색 조건을 stack에 저장하여 history에 활용함.
	private Stack<SSearchOptions> searchStack = new Stack<SSearchOptions>(30);

	private LinkedHashMap<Long, String> savedItemMap = new LinkedHashMap<Long, String>();
	
	public final static int documentt= 0;
	public final static int searchMenu = 1;
	
	private int type = 0;
	/**
	 * 	SearchItems 생성자
	 * */
	public NormalSearchItems() {
		init();
	}
	
	public NormalSearchItems(int type){
		init();
		this.type = type;
	}
	
	private void init(){
		initItems();
		initSearchFormItemsMap();
//		initTemplateGrid();
		initSearchConditionActions();
		initCheckActions();
		initSearchButtonAction();
		setEnterAction();
	}
	
	/**
	 * 저장된 검색 옵션을 불러온다. 
	 */
	public void loadSavedSelectItemValue(){
		savedItemMap.clear();
		savedItemMap.put(0L, I18N.message("notspecified"));
		ServiceUtil.search().listSearchesByUser(Session.get().getSid(), Session.get().getUser().getId(), new AsyncCallback<List<SSearches>>() {
			@Override
			public void onSuccess(List<SSearches> result) {
				if(result.size() == 0){
					savedSelect.clearValue();
					savedSelect.setValueMap(savedItemMap);
					savedSelect.setValue(0L);
					return;
				}
				
				String showValue = "";
				for (SSearches opt : result) {
					if(opt.getDescription() != null){
						showValue = opt.getName() + "(" + opt.getDescription() + ")";
					}else{
						showValue = opt.getName();
					}
					savedItemMap.put(opt.getId(), showValue);
					savedSelect.setValueMap(savedItemMap);
				}
				savedSelect.setValue(0L);
			}
			@Override
			public void onFailure(Throwable caught) {
				Log.serverError(caught, false);
			}
		});
	}
	
	/**
	 *	Search Items Init
	 * */
	private void initItems(){
		// Saved info
		savedSelect = new SelectItem("saved", I18N.message("s.savedSearch"));
		savedSelect.setWidth(130);
		loadSavedSelectItemValue();
		deleteSavedPicker = new PickerIcon(PickerIcon.CLEAR);
		savedSelect.setIcons(deleteSavedPicker);
		
		// Title
		titleText = new FixedTextItem("title", I18N.message("title"));
		SearchUtil.initItem(titleText, 130, Alignment.LEFT);
		searchPicker = new PickerIcon(PickerIcon.SEARCH);
		clearPicker = new PickerIcon(PickerIcon.REFRESH);
		
		searchPicker.setPrompt(I18N.message("search"));
		clearPicker.setPrompt(I18N.message("s.searchreset"));
		
//		titleText.setIcons(searchPicker, clearPicker);
//		titleText.setLength(Session.get().getInfo().getIntConfig("gui.title.fieldsize", 255));
		titleText.setValidators(new LengthValidator(titleText, Session.get().getInfo().getIntConfig("gui.title.fieldsize", 255)));
		titleText.setShowHintInField(true);
		
		// File Name
		fileNameText = new FixedTextItem("filename", I18N.message("filename"));
//		fileNameText.setLength(Session.get().getInfo().getIntConfig("gui.file.fieldsize", 255));
		fileNameText.setValidators(new LengthValidator(fileNameText, Session.get().getInfo().getIntConfig("gui.file.fieldsize", 255)));
		SearchUtil.initItem(fileNameText, 130, Alignment.LEFT);
		
		// keyword
		keywordText = new FixedTextItem("keyword", I18N.message("second.keyword"));
//		keywordText.setLength(Session.get().getInfo().getIntConfig("gui.keyword.fieldsize", 255));
		keywordText.setValidators(new LengthValidator(keywordText, Session.get().getInfo().getIntConfig("gui.keyword.fieldsize", 255)));
		SearchUtil.initItem(keywordText, 130, Alignment.LEFT);
		
		// docType 
		docTypeSelect = new SelectItem("doctype", I18N.message("doctype"));
		SearchUtil.initItem(docTypeSelect, 130, Alignment.LEFT);
		SearchUtil.getDefaultDocType(docTypeSelect);
		
		// Template
		templateIdSelect = new SelectItem("template", I18N.message("template"));
		templateAdd		= ItemFactory.newItemIcon("add.png");
		templateAdd.setPrompt(I18N.message("s.templateadd"));
		templateCleanPicker = new PickerIcon(PickerIcon.REFRESH);
		
		templateIdSelect.setIcons(templateAdd, templateCleanPicker);
		SearchUtil.getTemplateSelectItem(templateIdSelect);
		
		// folder
		folderText = new FixedTextItem("folder", I18N.message("folder"));
		folderText.setDisableIconsOnReadOnly(false);
		folderText.setCanEdit(false);
		folderText.setCanFocus(false);
		SearchUtil.initItem(folderText, 130, Alignment.LEFT);
		
//		folderSearchPicker = new PickerIcon(PickerIcon.SEARCH);
        folderCleanPicker = new PickerIcon(PickerIcon.REFRESH);
        
//        folderSearchPicker.setPrompt(I18N.message("search"));
        folderCleanPicker.setPrompt(I18N.message("reset"));
        
        folderText.setIcons(folderCleanPicker);
		
		// document creator
		ownerText = new FixedTextItem("owner", I18N.message("owner"));
		//20140205na 키로 입력할 수 있게 도입
		ownerText.setCanEdit(true);
//		ownerText.setCanFocus(false);
//		ownerText.setDisableIconsOnReadOnly(false);
		
//		ownerText.addBlurHandler(new BlurHandler() {
//			@Override
//			public void onBlur(BlurEvent event) {
//				if(ownerInfo[0][0] == null){
//					ownerText.setCellStyle("ownnertextout");
//					ownerText.updateState();
////					ownerText.clearValue();
//				}
//			}
//		});
		
		ownerText.addChangedHandler(new ChangedHandler() {
			
			@Override
			public void onChanged(ChangedEvent event) {
				ownerInfo[0][0] = null;
				ownerText.setCellStyle("");
				ownerText.updateState();
			}
		});
		ownerText.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				try {
//					System.out.println("onKeyUp");
					if("Enter".equals(event.getKeyName())){
//						setSearchOptionAndSearch(true);
						searchName = ownerText.getValueAsString();
						SearchUtil.doFindAction(Constants.OWNER, returnOwnerHandler, false, searchName);
						searchName = "";
					}
//					else{
//						ownerInfo[0][0] = null;
//						ownerText.setCellStyle("");
//						ownerText.updateState();
//					}
				} catch (Exception e) {}
				
				event.cancel();
			}
		});
		
		
		SearchUtil.initItem(ownerText, 130, Alignment.LEFT);
		ownerSearchPicker = new PickerIcon(PickerIcon.SEARCH);
//        ownerCleanPicker = new PickerIcon(PickerIcon.REFRESH);
        
        ownerSearchPicker.setPrompt(I18N.message("search"));
//        ownerCleanPicker.setPrompt(I18N.message("reset"));
        
        ownerText.setIcons(ownerSearchPicker);
		
		// fileSize
		sizeSelect = new SelectItem("size", I18N.message("filesize"));
		SearchUtil.setFileSizeItem(sizeSelect);
		
		// expireDate
		expireDateSelect= new SelectItem("expiredateafter", I18N.message("s.expiredateafter"));
		fexpireDate = new DateItem();	
		bexpireDate = new DateItem();
		SearchUtil.dateItemSetting(fexpireDate);
		SearchUtil.dateItemSetting(bexpireDate);
		SearchUtil.setDateComboData(expireDateSelect, fexpireDate, bexpireDate, true);

		// createDate
		createDateSelect= new SelectItem("createddate", I18N.message("createddate"));
		fcreateDate = new DateItem();
		bcreateDate = new DateItem();
		SearchUtil.dateItemSetting(fcreateDate);
		SearchUtil.dateItemSetting(bcreateDate);
		SearchUtil.setDateComboData(createDateSelect, fcreateDate, bcreateDate, false);
		
		// modifyDate
		modifyDateSelect= new SelectItem("modified", I18N.message("modifieddate"));
		fmodifyDate = new DateItem();
		bmodifyDate = new DateItem();
		SearchUtil.dateItemSetting(fmodifyDate);
		SearchUtil.dateItemSetting(bmodifyDate);
		SearchUtil.setDateComboData(modifyDateSelect, fmodifyDate, bmodifyDate, false);
		
		// 공통 Date 컬럼
		commonDateColumn = new StaticTextItem();
		commonDateColumn.setValue("-");
		commonDateColumn.setWidth(10);
		commonDateColumn.setAlign(Alignment.CENTER);
		commonDateColumn.setShowTitle(false);
		commonDateColumn.setStartRow(false);
		commonDateColumn.setEndRow(false);

		// 20131223, junsoo, 범위 지정.
//		selectRange = new SelectItem("range", I18N.message("searchRange"));
//		SearchUtil.initItem(selectRange, 130, Alignment.LEFT);
//		LinkedHashMap<String, String> selectRangeValues = new LinkedHashMap<String, String>();
//		selectRangeValues.put(String.valueOf(SSearchOptions.RANGE_WORKSPACE), I18N.message("s.personaldoc"));
//		selectRangeValues.put(String.valueOf(SSearchOptions.RANGE_SHARED), I18N.message("shareddocs"));
//		selectRange.setValueMap(selectRangeValues);
//		selectRange.setValue(String.valueOf(SSearchOptions.RANGE_SHARED));
		
		// folder check
		searchFolderCheck = new CheckboxItem("folder", I18N.message("folderSearch"));
		searchFolderCheck.setWidth(10);
		// content check
		searchContentCheck = new CheckboxItem("content", I18N.message("content"));
		searchContentCheck.setWidth(10);
		
		// search
		searchButton = new ButtonItem();
		searchButton.setTitle(I18N.message("search"));
		searchButton.setAlign(Alignment.RIGHT);	
    	searchButton.setWidth(60); 
    	
    	//reset
    	resetButton = new ButtonItem();
		resetButton.setTitle(I18N.message("initialize"));
		resetButton.setAlign(Alignment.RIGHT);
		resetButton.setWidth(65);
    	
//    	if(getUserAgent().contains("msie 8")){
//    		searchButton.setTitleStyle("searchButton");
//    		resetButton.setTitleStyle("initializeButton");
//    	}
//    	else{
    		searchButton.setBaseStyle("searchButton");
    		resetButton.setBaseStyle("initializeButton");
//    	}
    	
//        searchButton.setTitleStyle("searchButton");
//    	searchButton.setBaseStyle("searchButton");		
				
//		resetButton.setTitleStyle("initializeButton");
//		resetButton.setBaseStyle("initializeButton");
		
		
		initFolder();
	}
	
	private void initFolder() {
		// 20131226, junsoo, 폴더선택은 필수로 수정
		folder = new SFolder();
		folder.setId(Constants.SHARED_DEFAULTID);
		folderText.setValue("/" + I18N.message(Constants.MENU_DOCUMENTS_SHAREDDOC));
	}
	
	/**
//	 *  Search Form Item 생성 및 초기화
	 * */
	private void initSearchFormItemsMap(){
		HashMap<String, FormItem> itemMap = new HashMap<String, FormItem>();
		itemMap.put("saved", savedSelect);
		
//		itemMap.put("range", selectRange);		// 20131223

		itemMap.put("title", titleText);
		itemMap.put("fileName", fileNameText);
		itemMap.put("keyword", keywordText);
		itemMap.put("docType", docTypeSelect);
		itemMap.put("templateId", templateIdSelect);
		itemMap.put("fieldCombo", fieldCombo);
		itemMap.put("folder", folderText);
		itemMap.put("owner", ownerText);
		itemMap.put("size", sizeSelect);
		
		itemMap.put("createDate", createDateSelect);
		itemMap.put("fcreateDate", fcreateDate);
		itemMap.put("bcreateDate", bcreateDate);
		
		itemMap.put("modifyDate", modifyDateSelect);
		itemMap.put("fmodifyDate", fmodifyDate);
		itemMap.put("bmodifyDate", bmodifyDate);
		
		itemMap.put("expireDate", expireDateSelect);
		itemMap.put("fexpireDate", fexpireDate);
		itemMap.put("bexpireDate", bexpireDate);
		
		itemMap.put("commonDateItem", commonDateColumn);

		itemMap.put("folderCheck", searchFolderCheck);
		itemMap.put("contentCheck", searchContentCheck);
		
		itemMap.put("search", searchButton);
		itemMap.put("initialize", resetButton);
		
		searchItemsMap = itemMap;
	}
	
	/**
	 *	Search Items에 Enter 액션을 추가 
	 * */
	private void setEnterAction(){
		Collection<FormItem> items = searchItemsMap.values();
		for (final FormItem item : items) {
			if(item!=null){
				// 20140325, junsoo, 생성자에서의 enter는 사용자 검색창만 표시해야하므로
				if (item == ownerText)
					continue;
				
				item.addKeyUpHandler(new KeyUpHandler() {
					
					@Override
					public void onKeyUp(KeyUpEvent event) {
						try {
							if("Enter".equals(event.getKeyName())){
								setSearchOptionAndSearch(true);
							}
						} catch (Exception e) {}
					}
				});
			}
		}
	}
	
	/**
	 *	Search Items 및 Value 초기화
	 * 1. folder, Template, Owner 정보 초기화
	 * 2. Item Value 초기화
	 * 3. Template 정보 및 Form 초기화
	 * 
	 *	@param containCheckItem
	 * */ 
	public void resetSearchItems(boolean containCheckItem){
		// 검색 Tab에서만 Template 초기화 
		if(searchOption == Constants.SEARCH_PLACE_SEARCH){
			folder = null;
			resetTemplateData(true);
		}
		ownerInfo = new String[1][2];
		resetItems(containCheckItem);
//		resetTemplateGrid();
	}
	
	/**
	 * 	Search Items Value Reset
	 *  20130809 taesu
	 * */
	public void resetItems(boolean containCheckItem){
//		Collection<FormItem> allItems = searchItemsMap.values();
//		for (FormItem formItem : allItems) {
//			formItem.clearValue();
//		}
		titleText.setValue("");
		fileNameText.setValue("");
		keywordText.setValue("");
		
		switch (type) {
		case NormalSearchItems.searchMenu:
			initFolder();
			break;

		}
		
		// docType 초기화
//		docTypeSelect.setValue(SearchUtil.all);
		// Template 초기화
//		templateIdSelect.setValue(SearchUtil.all);
		// Size 초기화
//		if(searchOption != Constants.SEARCH_PLACE_EXPIRE)
		
		//저장된 검색도 미지정으로 바꿈
		// 20131126, junsoo, javascript 오류 발생하여 try/catch 처리. 원인 불명.
//		try {
//			savedSelect.setValue(0L);
//		} catch (Exception e) {
//			Log.debug(e.getMessage());
//		}
//		loadSavedSelectItemValue();
		
		ownerText.setValue("");
		// Date Items 초기화
		createDateSelect.setValue(SearchUtil.all);
		modifyDateSelect.setValue(SearchUtil.all);
		expireDateSelect.setValue(SearchUtil.all);
		fcreateDate.setValue("");
		bcreateDate.setValue("");
		fmodifyDate.setValue("");
		bmodifyDate.setValue("");
		fexpireDate.setValue("");
		bexpireDate.setValue("");
		// 체크 박스 초기화
		if(containCheckItem){
			searchContentCheck.setValue(false);
//			setEnableByCheck(false, true);
//			setEnableByCheck(false, false);
//			searchFolderCheck.setValue(false);
//			setEnableByContentCheck(false);
		}
//		if(searchOption == Constants.SEARCH_PLACE_SEARCH){
//			SearchPanel.get().getLeftMenu().redrawForm();
//		}
		if(searchOption != Constants.SEARCH_PLACE_SHAREDTRASH){
			sizeSelect.clearValue();
			docTypeSelect.clearValue();
		}
	}
	
	/**
	 *	Folder Filter 검색 조건에 따라 Search Item들의 값을 초기화 한다.
	 * @param SSearchOptions
	 * */ 
//	public void setItemValues(SSearchOptions opt){
//		// 공통 Item 초기화
//		titleText.setValue(opt.getTitle());
//		keywordText.setValue(opt.getKeyword());
//		
//		createDateSelect.setValue(opt.getCreateDateValue());
//		expireDateSelect.setValue(opt.getExDateValue());
//		fcreateDate.setValue(SearchUtil.setDatebyString(opt.getCreateDateFrom()));
//		bcreateDate.setValue(SearchUtil.setDatebyString(opt.getCreateDateTo()));
//		fexpireDate.setValue(SearchUtil.setDatebyString(opt.getExpiredDateFrom()));
//		bexpireDate.setValue(SearchUtil.setDatebyString(opt.getExpiredDateTo()));
//		
//		// 특정 Item 초기화
//		int currentMenuType = DocumentActionUtil.get().getActivatedMenuType(); 
//		if((currentMenuType == DocumentActionUtil.TYPE_MYDOC) || (currentMenuType == DocumentActionUtil.TYPE_SHARED)){
//			sizeSelect.setValue(opt.getSizeValue());
//			docTypeSelect.setValue(opt.getDocTypeValue());
//		}
//		else if(currentMenuType == DocumentActionUtil.TYPE_EXPIRED){
//			ownerInfo = new String[2];
//			ownerText.setValue("");
//		}
//	}

	/**
	 * 	Folder Check Item의 check 상태에 따라 Item Enable/Disable 설정
	 * @param doEnable
	 * @param isFolderCheck
	 */
	private void setEnableByCheck(boolean doEnable, boolean isFolderCheck){
		fileNameText.setDisabled(doEnable);
		keywordText.setDisabled(doEnable);
		docTypeSelect.setDisabled(doEnable);
		sizeSelect.setDisabled(doEnable);
//		folderText.setDisabled(doEnable);
		ownerText.setDisabled(doEnable);
		
		modifyDateSelect.setDisabled(doEnable);
		fmodifyDate.setDisabled(doEnable);
		bmodifyDate.setDisabled(doEnable);
		expireDateSelect.setDisabled(doEnable);
		fexpireDate.setDisabled(doEnable);
		bexpireDate.setDisabled(doEnable);
		
		templateIdSelect.setDisabled(doEnable);

		if(!isFolderCheck){
			createDateSelect.setDisabled(doEnable);
			fcreateDate.setDisabled(doEnable);
			bcreateDate.setDisabled(doEnable);
		}
		
		if(doEnable){
			if(isFolderCheck){
				searchContentCheck.setValue(false);
				titleText.setTitle(I18N.message("foldername"));
			}else{
				searchFolderCheck.setValue(false);
				titleText.setTitle(I18N.message("title"));
//				titleText.setTitle(I18N.message("expression"));
			}
		}
		else
			titleText.setTitle(I18N.message("title"));
	}
	
//	/**
//	 *	Content Check Item의 check 상태에 따라 Item Enable/Disable 설정
//	 * */
//	private void setEnableByContentCheck(boolean doEnable){
//		fileNameText.setDisabled(doEnable);
//		keywordText.setDisabled(doEnable);
//		docTypeSelect.setDisabled(doEnable);
//		sizeSelect.setDisabled(doEnable);
//		folderText.setDisabled(doEnable);
//		ownerText.setDisabled(doEnable);
//		templateIdSelect.setDisabled(doEnable);
//		
//		createDateSelect.setDisabled(doEnable);
//		fcreateDate.setDisabled(doEnable);
//		bcreateDate.setDisabled(doEnable);
//		modifyDateSelect.setDisabled(doEnable);
//		fmodifyDate.setDisabled(doEnable);
//		bmodifyDate.setDisabled(doEnable);
//		expireDateSelect.setDisabled(doEnable);
//		fexpireDate.setDisabled(doEnable);
//		bexpireDate.setDisabled(doEnable);
//		
////		searchFolderCheck.setCanEdit(!doEnable);
//		
//		if(doEnable){
//			searchFolderCheck.setValue(false);
//			titleText.setTitle(I18N.message("expression"));
//		}
//		else
//			titleText.setTitle(I18N.message("title"));
//	}
	
	/**
	 * Check Item 선택시 동작
	 * */ 
	private void initCheckActions(){
		searchFolderCheck.addChangedHandler(new ChangedHandler() {
			@Override
			public void onChanged(ChangedEvent event) {
				resetSearchItems(false);
				if(searchFolderCheck.getValueAsBoolean()){
					searchContentCheck.setValue(false);
					setEnableByCheck(true, true);
				}else{
					setEnableByCheck(false, true);
				}
				SearchPanel.get().getLeftMenu().redrawForm();
			}
		});
		// content search comboItem select operation		
		searchContentCheck.addChangedHandler(new ChangedHandler() {
			@Override
			public void onChanged(ChangedEvent event) {
				resetSearchItems(false);
				if(searchContentCheck.getValueAsBoolean()){
					searchFolderCheck.setValue(false);
					setEnableByCheck(true, false);
				}else{
					setEnableByCheck(false, false);
				}
				SearchPanel.get().getLeftMenu().redrawForm();
			}
		});
	}
	
	private ReturnHandler returnOwnerHandler = new ReturnHandler() {
		@Override
		public void onReturn(Object param) {
			ownerInfo = (String[][])param;
			ownerText.setValue(ownerInfo[0][1]);
			ownerText.setCellStyle("ownnertext");
			ownerText.updateState();
		}
	};
	
	/**
	 * Search button 제외 Action
	 * */
	@SuppressWarnings("rawtypes")
	private void initSearchConditionActions(){
		// 20130806, junsoo, 폴더 선택후 결과를 리턴 받을 handler생성
		final ReturnHandler returnFolderHandler = new ReturnHandler() {
			@Override
			public void onReturn(Object param) {
				SFolder selectedFolder = (SFolder)param;
				folderText.setValue(selectedFolder.getPathExtended().replaceAll("/root", ""));
				folder = selectedFolder;
			}
		};
		
		
		// 저장된 검색 selectItem 값 변경시 동작
		savedSelect.addChangedHandler(new ChangedHandler() {
			@Override
			public void onChanged(ChangedEvent event) {
				long id = 0;
				
				//20131211 na 미지정일 경우의 NumberFormatExcpetion 제거
				try {
					 id = Long.parseLong(savedSelect.getValueAsString());
				} catch (Exception e) {}
				//20131211 na id가 0이면 아무런 선택이 안된 것
				if(id == 0) return;
				
				ServiceUtil.search().load(Session.get().getSid(), id, new AsyncCallback<SSearchOptions>() {
					@Override
					public void onSuccess(SSearchOptions result) {
						setItemValues(result);
					}
					@Override
					public void onFailure(Throwable caught) {
						Log.serverError(caught, false);
					}
				});
			}
		});
		
		// 저장된 검색 삭제 선택 동작		
		deleteSavedPicker.addFormItemClickHandler(new FormItemClickHandler() {
			@Override
			public void onFormItemClick(FormItemIconClickEvent event) {
				final long id;
				try {
					id = Long.parseLong(savedSelect.getValueAsString());
				} catch (Exception e) {
					return;
				}
				//20131211na id가 0이면 미 지정
				if(id == 0){
					SC.warn(I18N.message("error.system.cannotDelete"));
					return;
				}
				SC.confirm(I18N.message("wanttodelete"), new BooleanCallback() {
					@Override
					public void execute(Boolean value) {
						if (value != null && value) {
							ServiceUtil.search().delete(Session.get().getSid(), id, new AsyncCallback<Void>() {
								@Override
								public void onSuccess(Void v) {
									SC.say(I18N.message("successdelete"));
									// 20140217, junsoo, reload list
									loadSavedSelectItemValue();
									resetItems(false);
								}
								@Override
								public void onFailure(Throwable caught) {
									Log.serverError(caught, false);
								}
							});
						}else {
							return;
						}
					}
				});
			}
		});
		
		folderText.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
//				if(!searchFolderCheck.getValueAsBoolean()){
					SearchUtil.doFindAction(Constants.FOLDER_PATH, returnFolderHandler, false);
//				}				
			}
		});
//		folderSearchPicker.addFormItemClickHandler(new FormItemClickHandler() {
//			@Override
//			public void onFormItemClick(FormItemIconClickEvent event) {
//				if(!searchFolderCheck.getValueAsBoolean()){
//					// 20130806, junsoo, return handler 추가하여 처리 결과는 이 클래스에서 수행하도록 함. (소스 리빌딩)
//					FolderSelectorDialog selectPath = FolderSelectorDialog.get();
//					selectPath.setReturnHandler(returnFolderHandler);
//					selectPath.show(true);
//				}
//			}
//		});
		folderCleanPicker.addFormItemClickHandler(new FormItemClickHandler() {
			@Override
			public void onFormItemClick(FormItemIconClickEvent event) {
//				folderText.setValue("");
//				folder = null;
				initFolder();
			}
		});
		// owner 검색 조건 Action
//		ownerText.addClickHandler(new ClickHandler() {
//			@Override
//			public void onClick(ClickEvent event) {
//				if(!searchFolderCheck.getValueAsBoolean()){
//					SearchUtil.doFindAction(Constants.OWNER, returnOwnerHandler, false);
//				}				
//			}
//		});
		ownerSearchPicker.addFormItemClickHandler(new FormItemClickHandler() {
			@Override
			public void onFormItemClick(FormItemIconClickEvent event) {
				if(!searchFolderCheck.getValueAsBoolean()){
					searchName = ownerText.getValueAsString();
					SearchUtil.doFindAction(Constants.OWNER, returnOwnerHandler, false, searchName);
					searchName = "";
				}				
			}
		});
//		ownerCleanPicker.addFormItemClickHandler(new FormItemClickHandler() {
//			@Override
//			public void onFormItemClick(FormItemIconClickEvent event) {
//				ownerText.setValue("");
//				ownerInfo = new String[1][2];
//			}
//		});
		// 템플릿 Combo 선택 변경시 동작
		templateIdSelect.addChangedHandler(new ChangedHandler() {
			@Override
			public void onChanged(ChangedEvent event) {
				resetTemplateData(false);
				sTemplate = (STemplate) templateIdSelect.getValue();
			}
		});
		// 템플릿 그리드 데이터 추가
		templateAdd.addFormItemClickHandler(new FormItemClickHandler() {
			@Override
			public void onFormItemClick(FormItemIconClickEvent event) {
				sTemplate = (STemplate) templateIdSelect.getValue();
				if(sTemplate.getId() != 0){
					setExTemplateItems(null);
					SearchPanel.get().getLeftMenu().refreshFormByFormItems(itemsMap, "template");
				}
			}
		});
		// 템플릿 그리드 데이터 초기화
		templateCleanPicker.addFormItemClickHandler(new FormItemClickHandler() {
			@Override
			public void onFormItemClick(FormItemIconClickEvent event) {
				resetTemplateData(true);
			}
		});
		// 검색 조건 초기화resetSearchItems(true);
		resetButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				resetSearchItems(true);
			}
		});
		clearPicker.addFormItemClickHandler(new FormItemClickHandler() {
			@Override
			public void onFormItemClick(FormItemIconClickEvent event) {
				resetSearchItems(true);
			}
		});
	}
	
	/**
	 * 템플릿 관련 데이터를 모두 초기화 한다.
	 * */ 
	private void resetTemplateData(boolean containSelectItem){
//		templateIdSelect.setValue(SearchUtil.all);
		if(containSelectItem)
			templateIdSelect.clearValue();
		sTemplate = null;
		beforeSTemplate = null;
		itemsMap.clear();
		// 검색 폼 아이템 초기화
		SearchPanel.get().getLeftMenu().refreshFormByFormItems(itemsMap, "template");
	}
	
	String searchName = "";
	
	/**
	 * 	Search Action
	 * */
	public void initSearchButtonAction(){
		searchPicker.addFormItemClickHandler(new FormItemClickHandler() {
			@Override
			public void onFormItemClick(FormItemIconClickEvent event) {
				setSearchOptionAndSearch(true);
			}
		});
		// 검색 버튼 동작
		searchButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				setSearchOptionAndSearch(true);
			}
		});
	}
	
	/**
	 *	템플릿 추가 속성 값들을 초기화 한다. 
	 * */
//	private void setExTemplateItemsValue(){
//		if(exTemplateNameSelect!=null)
//			exTemplateNameSelect.setValue("");
//		if(exTemplateOperatorSelect!=null)
//			exTemplateOperatorSelect.setValue("");
//		if(exTemplateFormItem!=null)
//			exTemplateFormItem.setValue("");
//		if(exTemplateAndOrSelect!=null)
//			exTemplateAndOrSelect.setValue("");	
//	}

	/**
	 *	추가 Template Items를 설정한다. 
	 * */
	private void initExTemplateItems(){
		exTemplateNameSelect 	= new SelectItem();
		exTemplateOperatorSelect= new SelectItem();
		exTemplateFormItem 		= new FormItem();
		exTemplateAndOrSelect	= new SelectItem();
		exTemplateDeletePicker	= new PickerIcon(PickerIcon.CLEAR);
		exTemplateDeletePicker.setPrompt(I18N.message("delete"));

		exTemplateNameSelect.setStartRow(true);
		exTemplateNameSelect.setShowTitle(false);
		exTemplateNameSelect.setWidth(80);
		exTemplateNameSelect.setDefaultToFirstOption(true);
		exTemplateFormItem.setWidth(80);
		exTemplateFormItem.setShowTitle(false);
		SearchUtil.setOperatorItem(exTemplateOperatorSelect);
		SearchUtil.setAndOrItem(exTemplateAndOrSelect);
		exTemplateAndOrSelect.setEndRow(true);
		exTemplateAndOrSelect.setIcons(exTemplateDeletePicker);
	}

	/**
	 *	템플릿 Select Item 변경 유무 반환 
	 * */
	private boolean isNewTemplate(){
		if(beforeSTemplate == null){
			return true;
		}else{
			// Select Item 값 선택시 값을 변경하지 않을 경우 동작
			if(beforeSTemplate == sTemplate)
				return false;
			else
				return true;
		}
	}
	
	/**
	 * 	검색시 Template의 값이 선택되어 Template의 Attribute를 설정하기 위한 Form Item들을 설정하고 데이터를 검색 Form 에 전달한다.
	 * 	추가/제거 및 Attribute의 Type별 Form Item 재생성
	 * 	@param exData : 히스토리값. 히스토리 사용 안할경우 null
	 * 	20130809 taesu 
	 * */
	private void setExTemplateItems(String[] exData){
		if(sTemplate == null)	return;
		initExTemplateItems();
		
		// Template 추가 속성 획득 및 저장
		SExtendedAttribute[] exAttr = sTemplate.getAttributes();
		// Template의 값을 변경할 경우 해당 Template의 Attribute을 저장
		if(isNewTemplate()){
			exNameValue = new String[exAttr.length];
			exTypeValue = new int[exAttr.length];
			int i=0;
			for (SExtendedAttribute attr : exAttr) {
				exNameValue[i] = attr.getName()+"("+attr.getLabel()+")";
				exTypeValue[i] = attr.getType();
				i++;
			}
		}
		// template select Item 값 변경시 validation 을 위해 템플릿 저장
		beforeSTemplate = sTemplate;
		exTemplateNameSelect.setValueMap(exNameValue);
		exTemplateNameSelect.setAttribute("type", exTypeValue[0]);
		
		// 행 구분 값 저장(Item 삭제시 사용됨)
		exTemplateDeletePicker.setAttribute("key", count);
		// history를 통한 데이터 세팅
		if(exData !=null && exData.length > 0){
//			opt.setTemplateCondition(new String[]{relationalOperator, fieldName, String.valueOf(type), comparisonOperator, data});
			exTemplateAndOrSelect.setValue(exData[0]);

			// Type에 따라 Item 형 변환
			if(Integer.parseInt(exData[2]) == Constants.TYPE_DATE){
				DateItem newDate = new DateItem();
				SearchUtil.dateItemSetting(newDate);
				exTemplateFormItem = newDate;
			}else{
				TextItem textItem = new TextItem();
				textItem.setShowTitle(false);
				textItem.setWidth(80);
				exTemplateFormItem = textItem;
			}
			
			for (int i = 0; i < exNameValue.length; i++) {
				if(exNameValue[i].contains(exData[1])){
					exTemplateNameSelect.setValue(exNameValue[i]);
					break;
				}
			}
			exTemplateOperatorSelect.setValue(exData[3]);
			exTemplateFormItem.setValue(exData[4]);
		}
		// 기본 추가 속성 세팅
		else{
			// 최초 선택되어 있는 값의 Type에 따라 Form Item 구성
			// Date Item 설정
			if(exTypeValue[0] == Constants.TYPE_DATE){
				DateItem newDate = new DateItem();
				SearchUtil.dateItemSetting(newDate);
				exTemplateFormItem = newDate;
			}
			// Text Item 설정
			else{
				TextItem textItem = new TextItem();
				textItem.setShowTitle(false);
				textItem.setWidth(80);
				exTemplateFormItem = textItem;
			}
		}
		// 템플릿 구성할 Item 저장
		itemsMap.put(count, new FormItem[]{exTemplateNameSelect, exTemplateOperatorSelect, exTemplateFormItem, exTemplateAndOrSelect});
		count++;
		
		// Name값 선택시 FormItem 값 변경 Action
		exTemplateNameSelect.addChangedHandler(new ChangedHandler() {
			@Override
			public void onChanged(ChangedEvent event) {
				int rowNum = 0;
				// 선택한 Name의 value와 배열에 저장되어있는 value를 비교하여 선택한 Name에 mapping 되는 type의 위치를 구한다
				for (int i = 0; i < exNameValue.length; i++) {
					if(exNameValue[i].equals(exTemplateNameSelect.getValueAsString())){
						rowNum = i;
						break;
					}
				}
				
				// 선택한 Name의 타입값에 따라 Form Item 변경
//				setFormItemDateOrText(exTypeValue[rowNum], itemsMap.get(exTemplateDeleteIcon.getAttributeAsInt("temp"))[2]);
				if(exTypeValue[rowNum] == Constants.TYPE_DATE){
					DateItem newDate = new DateItem();
					SearchUtil.dateItemSetting(newDate);
					itemsMap.get(exTemplateDeletePicker.getAttributeAsInt("key"))[2] = newDate;
				}else{
					TextItem textItem = new TextItem();
					textItem.setShowTitle(false);
					textItem.setWidth(80);
					itemsMap.get(exTemplateDeletePicker.getAttributeAsInt("key"))[2] = textItem;
				}
				itemsMap.get(exTemplateDeletePicker.getAttributeAsInt("key"))[0].setAttribute("type", exTypeValue[rowNum]);
				SearchPanel.get().getLeftMenu().refreshFormByFormItems(itemsMap, "template");
			}
		});
		
		exTemplateFormItem.addKeyDownHandler(new KeyDownHandler() {
			
			@Override
			public void onKeyDown(KeyDownEvent event) {
				// TODO Auto-generated method stub
				try {
					if(event.getKeyName().equals("Enter")){
						setSearchOptionAndSearch(true);
					}
				} catch (Exception e) {}
			}
		});
		
		// ExTemplate 정보 삭제 Action
		exTemplateDeletePicker.addFormItemClickHandler(new FormItemClickHandler() {
			@Override
			public void onFormItemClick(FormItemIconClickEvent event) {
				// 삭제하려는 행의 Key값을 가져온다.
				int row = Integer.parseInt(event.getIcon().getAttribute("key"));
				// 삭제하는 행의 데이터 및 Form Item 제거
				itemsMap.remove(row);

				// 모두 제거하였을 경우 Template 관련 데이터 모두 초기화
				if(itemsMap.size() == 0){
//					templateIdSelect.setValue(SearchUtil.all);
					resetTemplateData(true);
				}
				// refresh Template Form
				SearchPanel.get().getLeftMenu().refreshFormByFormItems(itemsMap, "template");
			}
		});
	}

	/**
	 * 검색 조건을 저장하고 검색까지 실행한다.
	 * @param isNew
	 */
	public void setSearchOptionAndSearch(final boolean isNew){
		setSearchOptions(isNew);
		switch (type) {
			case NormalSearchItems.searchMenu:
				String userName = ownerText.getDisplayValue();
				if(userName == null || "".equals(userName)) SearchPanel.get().executeFetch();
				else{
					ServiceUtil.security().getUser(Session.get().getSid(), userName, new AsyncCallback<SUser>() {
						@Override
						public void onSuccess(SUser result) {
							ownerInfo[0][0] = result.getId();
							opt.setOwnerId(ownerInfo[0][0]);
							ownerText.setCellStyle("ownnertext");
							ownerText.updateState();
							SearchPanel.get().executeFetch();
							
						}
						@Override
						public void onFailure(Throwable caught) {
							SC.warn(caught.getMessage());
							ownerText.setCellStyle("ownnertextout");
							ownerText.updateState();
						}
					});
				}
				break;
			default:
				doSearch(1, isNew);
				break;
		}
	}
	
	/**
	 *	검색 조건을 저장한다.
	 *	@param isNew 새로운 검색 유무
	 * */ 
	public SSearchOptions setSearchOptions(final boolean isNew){
		
		// 20140211, junsoo, set current orderby
		String currentOrderBy = DocumentActionUtil.get().getCurrentSorter();
		String order = currentOrderBy.split("\\/")[0];
		String orderDir = currentOrderBy.split("\\/")[1];
		SortDir sortDir = SortDir.DESC;
		
		// 정렬값 설정(Paging시 유지)
		if(!orderDir.equals("DESC")){
			sortDir = SortDir.ASC;
		}
		
		
		SearchPanel.get().setPageNum(1);
		switch (type) {
		case NormalSearchItems.searchMenu:
//			config = PagingToolStrip.getPagingConfig(SearchPanel.get().getPageNum(), SearchPanel.get().getPageSize(), order, sortDir);
			config = PagingToolStrip.getPagingConfig(1, SearchPanel.get().getPageSize(), order, sortDir);
			break;
		default:
			DocumentsPanel.get().getListingPanel().getGridPager().setOrderDir(sortDir);
			DocumentsPanel.get().getListingPanel().getGridPager().setOrderBy(order);

			config = PagingToolStrip.getPagingConfig(1, Session.get().getUser().getPageSize(), order, sortDir);
			break;
		}
		
//		config = PagingToolStrip.getPagingConfig(1, 10, "creationDate", SortDir.DESC);
		// 새로운 검색의 경우 Offset 초기화

		if(isNew){
			opt = new SSearchOptions();
			config.setOffset(0);
			currentOffset = 0;
		} else {
			if(opt == null)
				opt = new SSearchOptions();
		}
//		config = new PagingConfig(0, currentOffset, 10);
//		config.setOrderByField(orderBy);
//		config.setOrderDir(sortDir);
//		opt.setLanguage("ko");
//		opt.setMaxHits(0);			
		opt.setCaseSensitive(1);	// 0(True), 1(False)
		opt.setUserId(Session.get().getUser().getId());
		// Title 설정
		if(searchFolderCheck.getValueAsBoolean()){
			opt.setFolderName(titleText.getValueAsString());
		}
		else
			opt.setTitle(titleText.getValueAsString());
		
		// fileName 설정
		opt.setFileName(fileNameText.getValueAsString());
		
		// Keyword 설정
		opt.setKeyword(keywordText.getValueAsString());
		
		// 20131223, junsoo, 범위 지정
//		if (selectRange.getValueAsString() != null)
//			opt.setRange(Integer.parseInt(selectRange.getValueAsString()));
		
		// DocTypeId 설정
		if(docTypeSelect.getValueAsString()!=null && docTypeSelect.getValueAsString().split("/").length > 1 ){
			if(docTypeSelect.getValueAsString() != null && !docTypeSelect.getValueAsString().split("/")[1].equals(SearchUtil.all)){
				opt.setDocTypeId(Long.parseLong(docTypeSelect.getValueAsString().split("/")[1]));
			}
		}else{
			if(docTypeSelect.getValueAsString() != null){
				opt.setDocTypeId(docTypeId);
			}
		}

		// Template Condition 설정
		if(sTemplate != null && !templateIdSelect.getValue().equals(SearchUtil.all)){
			// Template Id 설정
			opt.setTemplateId(sTemplate.getId());
			// 추가 Template 속성의 값들을 가져와 검색 조건에 반영한다
			for (FormItem[] item : itemsMap.values()) {
				String fieldName 			=	((String)item[0].getValue()).split("\\(")[0];
				String comparisonOperator 	= 	(String)item[1].getValue();
				String relationalOperator 	= 	(String)item[3].getValue();
				// Type
				int type = item[0].getAttributeAsInt("type");
				String data = null;
				// Type에 따라(크게 String, date) 전달할 value값을 세팅
				if(type == Constants.TYPE_DATE){
					Date date = (Date)item[2].getValue();
					data = SearchUtil.setSearchDate(date);
				}else
					data = (String)item[2].getValue();
				opt.setTemplateCondition(new String[]{relationalOperator, fieldName, String.valueOf(type), comparisonOperator, data});
			}
		}

		// FolderId 설정
		if(folder != null)
			opt.setFolder(folder.getId());
		
		// OwnerInfo 설정
		if(ownerInfo[0][0] != null)
			opt.setOwnerId(ownerInfo[0][0]);
		else opt.setOwnerId("");
		
		// FileSize 설정
		if((sizeSelect.getValueAsString() != null && !sizeSelect.getValueAsString().equals(SearchUtil.all))){
			opt.setSizeValue(sizeSelect.getValueAsString());
			try{
				Long[] fileSize = (Long[]) sizeSelect.getValue();
				opt.setSizeMin(fileSize[0]);
				opt.setSizeMax(fileSize[1]);
			}catch(Exception e){
				if(sizeMax != 0){
					opt.setSizeMin(sizeMin);
					opt.setSizeMax(sizeMax);
				}else{
					opt.setSizeMin(0L);
					opt.setSizeMax(0L);
				}
			}
		}else{
			opt.setSizeMin(0L);
			opt.setSizeMax(0L);
		}
		
		// Create Date 설정
		opt.setCreateDateFrom(SearchUtil.setSearchDate(fcreateDate.getValueAsDate()));
		opt.setCreateDateTo(SearchUtil.setSearchDate(bcreateDate.getValueAsDate()));
		
		opt.setCreationFrom(fcreateDate.getValueAsDate());
		opt.setCreationTo(bcreateDate.getValueAsDate());
		
		opt.setCreateDateValue(createDateSelect.getValueAsString());
		// Modify Date 설정
		if(searchOption == Constants.SEARCH_PLACE_SEARCH){
			opt.setModifyDateFrom(SearchUtil.setSearchDate(fmodifyDate.getValueAsDate()));
			opt.setModifyDateTo(SearchUtil.setSearchDate(bmodifyDate.getValueAsDate()));
		}
		// Expired Date 설정
		opt.setExpiredDateFrom(SearchUtil.setSearchDate(fexpireDate.getValueAsDate()));
		opt.setExpiredDateTo(SearchUtil.setSearchDate(bexpireDate.getValueAsDate()));
		opt.setExDateValue(expireDateSelect.getValueAsString());
		
		/*
		 *  Documents Tab 및 Search Tab 에서 선택한 섹션에 해당하는 검색조건 설정
		 * */
		opt.setType(SSearchOptions.TYPE_DB);
		switch(searchOption){
		case Constants.SEARCH_PLACE_SHAREDTRASH:
			opt.setType(SSearchOptions.TYPE_SHAREDTRASH);
			break;
		case Constants.SEARCH_PLACE_DEFAULT:
			opt.setType(SSearchOptions.TYPE_INFOLDER);
			break;
		case Constants.SEARCH_PLACE_SEARCH:
			opt.setType(SSearchOptions.TYPE_DB);
			break;	
		}

		// Folder 검색 선택 동작 : IN FOLDER 검색
		if(searchFolderCheck.getValueAsBoolean()){
			// 20131219, junsoo, 폴더만 검색 (TYPE_INFOLDERS -> TYPE_FOLDERS 수정)
			opt.setType(SSearchOptions.TYPE_FOLDERS);
		}
		// Content 검색 선택 동작 : FULL TEXT 검색
		else if(searchContentCheck.getValueAsBoolean()){
//			opt = new SSearchOptions();
			opt.setType(SSearchOptions.TYPE_FULLTEXT);
			opt.setExpression(titleText.getValueAsString().trim());
			opt.setFields(new String[] { "content", "title" });
			config.setSearchFullText(true);
		}
		
		return opt;
//		doSearch(1, config.getLimit(), config, true);
	}
	
	/**
	 *  Folder Filter 설정되어있는 폴더 선택시 최초 검색용 Search
	 * */
//	public void doSearch(PagingConfig config, final boolean isNew, String id){
//		if(!isNew)
//			opt = DocumentsPanel.get().getFolderFilter().get(id);
//		DocumentsPanel.get().getFolderFilter().put(id, opt);
//		
//		config.setSearchTab(false);
//		panel = DocumentsPanel.get();
//		grid = DocumentsPanel.get().getListingPanel().getGrid();
//		
//		// Filter 설정되어 있는 경우 최초 검색 페이지 1로 적용
//		searchAction(1, isNew, 0);
//		DocumentActionUtil.get().changeActionIcon("filter", true);
//	}
	
	/**
	 * 기본 Search 동작
	 * */ 
	public void doSearch(final int pageNum, PagingConfig config, final boolean isNew){
		this.config = config;
		doSearch(pageNum, isNew);
	}
	
	public void doSearch(final int pageNum, final boolean  isNew){
		if(opt == null)
			opt = new SSearchOptions();

		//20140115na 검색시 페이지 변경 기능 추가
//		switch (type) {
//		case NormalSearchItems.searchMenu:
//			SearchPanel.get().setPageNum(pageNum);
//			break;
//		}
		
		if(searchOption == Constants.SEARCH_PLACE_SEARCH){
//			config.setSearchTab(true);
			
			// 20130822, junsoo, set history
			HistoryUtil.get().newHistory(SearchMenu.get(), "search;" + SearchMenu.SEARCH_MENU_NORMAL + ";" + searchStack.put(opt));
		}
		else{
			//Expired Doc은 필터검색 제외
//			if(DocumentActionUtil.get().getActivatedMenuType() != DocumentActionUtil.TYPE_EXPIRED){
//				// 폴더 필터검색 적용
//				// 현재 선택한 폴더의 ID값을 가져온다.
//				SFolder folder = Session.get().getCurrentFolder();
//				String folderId;
//				if(folder.getId() !=0 && folder.getId()>0)
//					folderId =String.valueOf(folder.getId()); 
//				else
//					// ETC 폴더의 경우 ID값이 Name 값으로 지정되어있음.
//					folderId = folder.getName();
//				if(!isNew)
//					opt = DocumentsPanel.get().getFolderFilter().get(folderId);
//				DocumentsPanel.get().getFolderFilter().put(folderId, opt);
			// 검색 위치에 따라서 서버의 Search Option 값을 다르게 주기 위함.
			if(DocumentActionUtil.get().getActivatedMenuType() == DocumentActionUtil.TYPE_MYDOC) {
				opt.setSearchLocation("private");
//				opt.setRange(SSearchOptions.RANGE_WORKSPACE);
			}
			else if(DocumentActionUtil.get().getActivatedMenuType() == DocumentActionUtil.TYPE_SHARED) {
				opt.setSearchLocation("shared");
//				opt.setRange(SSearchOptions.RANGE_SHARED);
			}
			else if(DocumentActionUtil.get().getActivatedMenuType() == DocumentActionUtil.TYPE_FOLDER_SHARED) {
				opt.setSearchLocation("folder_shared");
//				opt.setRange(SSearchOptions.RANGE_SHARED);
			}
			DocumentsPanel.get().setSearch(true);
//			config.setSearchTab(false);
		}

		// 데이터의 개수 +1을 하여 Total Length를 사용하지 않는다.
		// 20140220, junsoo, 페이징 서버에서 알아서함.
		config.setPageSize(config.getLimit());
		searchAction(pageNum, isNew);
	}
	
	/**
	 * 서버 검색 요청
	 * */
	private void searchAction(final int pageNum, final boolean isNew){
		// 20131227, junsoo, 필수 입력값 (제목, 파일명, 키워드, 템플릿 중 한가지 이상) 검증
		if(!searchFolderCheck.getValueAsBoolean()){
			if (	Util.isEmpty(opt.getTitle()) &&
					Util.isEmpty(opt.getKeyword()) &&
					Util.isEmpty(opt.getFileName()) &&
					Util.isEmpty(opt.getFileName()) &&
					opt.getTemplateCondition().size() < 1) {
				
				switch (type) {
				case documentt:
					Log.warnWithPopup(I18N.message("inputParameterError"), I18N.message("titleOrFilenameOrKeyword"));
					break;
				case searchMenu:
					Log.warnWithPopup(I18N.message("inputParameterError"), I18N.message("titleOrFilenameOrKeywordOrTemplate"));
					break;

				}
				
				return;
			}
		}
		
		// 20130822, junsoo, 검색부하를 줄이기 위해 검색시 아무동작이 안되게 함.
		Waiting.show(I18N.message("s.nowsearching"));
		
		if (SSearchOptions.TYPE_FOLDERS == opt.getType()) {
			ServiceUtil.search().searchFolder(Session.get().getSid(), opt, config, new AsyncCallbackWithStatus<PagingResult<SHit>>() {
				@Override
				public String getSuccessMessage() {
					return I18N.message("s.searchingsuccess");
				}
				@Override
				public String getProcessMessage() {
					return I18N.message("s.nowsearching");
				}
				@Override
				public void onSuccessEvent(PagingResult<SHit> result) {
					// TODO:
					SearchPanel.get().setFoldersData(result.getData());

					Waiting.hide();
				}
				@Override
				public void onFailureEvent(Throwable caught) {
					Log.serverError(caught, false);
					Waiting.hide();
				}
			});
		}
		else {
			ServiceUtil.search().searchDB(Session.get().getSid(), opt, config, new AsyncCallbackWithStatus<PagingResult<SDocument>>() {
				@Override
				public String getSuccessMessage() {
					return I18N.message("s.searchingsuccess");
				}
				@Override
				public String getProcessMessage() {
					return I18N.message("s.nowsearching");
				}
				@Override
				public void onSuccessEvent(PagingResult<SDocument> result) {
					List<SDocument> sDocuments = null;
					// 검색 결과값이 없을경우
					if(result == null){
						SearchPanel.get().setDocumentsData(new ArrayList<SDocument>(), isNew);
						Waiting.hide();
						return;
					}
					else
						sDocuments = result.getData();
					
					// Search Tab에서 검색 결과 출력
					if(searchOption == Constants.SEARCH_PLACE_SEARCH){
						// 데이터 분할
						SearchPanel.get().responsePaging(result.isHavingNextPage(), result.getTotalLength(), pageNum);
						SearchPanel.get().setDocumentsData(result.getData(), isNew);
					}
					// Document Tab에서 검색 결과 출력
					else {
						DocumentsPanel.get().getListingPanel().getGridPager().updatePageStatus(result.isHavingNextPage(),result.getTotalLength(), pageNum);
						DocumentsPanel.get().getListingPanel().getGrid().setGridData(sDocuments, result.getTotalLength(), pageNum, DocumentsPanel.get().getListingPanel().getGridPager());
					}
					
					Waiting.hide();
				}
				@Override
				public void onFailureEvent(Throwable caught) {
					Log.serverError(caught, false);
					Waiting.hide();
				}
			});
		}
	}
	
	/**
	 * 현재 입력되어있는 검색 조건들의 값들을 가져온다. 
	 * @return SSearchOptions
	 */
	public SSearchOptions getSavedSearchCondition(){
		return setSearchOptions(true);
	}
	
	/**
	 * 20130822, junsoo,  저장된 search option으로 검색하기
	 * @param guid
	 */
	public void searchBySaved(String guid){
		SSearchOptions option = searchStack.get(guid);
		if (option == null)
			return;
		opt = option;
		
		setItemValues(opt);
		
		config = PagingToolStrip.getPagingConfig(1, Session.get().getUser().getPageSize(), "lastModified", SortDir.DESC);
		config.setOffset(0);
		// Total Length를 사용하지 않기 때문에 +1
		config.setPageSize(config.getLimit()+1);
		currentOffset = 0;

		searchAction(1, true);
	}
	
	private long docTypeId = 0;
	private long sizeMax = 0;
	private long sizeMin = 0;
	
	/**
	 * 검색 Option 값 채우기.
	 * */
	public void setItemValues(final SSearchOptions opt){
		if(opt.getType() == SSearchOptions.TYPE_INFOLDER){
			setEnableByCheck(true, true);
			searchFolderCheck.setValue(true);
			searchContentCheck.setValue(false);
			titleText.setTitle(I18N.message("foldername"));
			titleText.setValue(opt.getFolderName());
			fcreateDate.setValue(SearchUtil.setDatebyString(opt.getCreateDateFrom()));
			bcreateDate.setValue(SearchUtil.setDatebyString(opt.getCreateDateTo()));
			
			if(opt.getTemplateId() != null && opt.getTemplateId() != 0){
				beforeSTemplate = null;
				// Template ID를 통해 STemplate 획득
				LinkedHashMap<STemplate, String> templateMap = SearchUtil.getTemplatemap();
				Set<STemplate> templateSet =  templateMap.keySet();
				for (STemplate sTemp : templateSet) {
					if(sTemp.getId() == opt.getTemplateId()){
						templateIdSelect.setValue(sTemp);
						sTemplate = sTemp;
						itemsMap.clear();
						// 추가 Template 속성값 적용
						if(opt.getTemplateCondition().size() > 0){
							for (int i = 0 ; i < opt.getTemplateCondition().size() ; i++) {
								setExTemplateItems(opt.getTemplateCondition().get(i));
							}
							// 추가 속성 Items 값 보여주기
							SearchPanel.get().getLeftMenu().refreshFormByFormItems(itemsMap, "template");
						}
						break;
					}
				}
			}else{
				itemsMap.clear();
				templateIdSelect.setValue(SearchUtil.all);
				SearchPanel.get().getLeftMenu().refreshFormByFormItems(itemsMap, "template");
			}
		}else if(opt.getType() == SSearchOptions.TYPE_FULLTEXT){
			setEnableByCheck(true, false);
			searchFolderCheck.setValue(false);
			searchContentCheck.setValue(true);
			titleText.setValue(opt.getTitle());
			titleText.setTitle(I18N.message("expression"));
			itemsMap.clear();
			SearchPanel.get().getLeftMenu().refreshFormByFormItems(itemsMap, "template");
		}else if(opt.getType() == SSearchOptions.TYPE_FOLDERS){
			setEnableByCheck(true, true);
			searchFolderCheck.setValue(true);
			searchContentCheck.setValue(false);
			titleText.setValue(opt.getFolderName());

			titleText.setTitle(I18N.message("foldername"));
			fcreateDate.setValue(opt.getCreationFrom());
			bcreateDate.setValue(opt.getCreationTo());

//			titleText.setTitle(I18N.message("expression"));
			itemsMap.clear();
			SearchPanel.get().getLeftMenu().refreshFormByFormItems(itemsMap, "template");
		}else{
			titleText.setTitle(I18N.message("title"));
			setEnableByCheck(false, true);
			setEnableByCheck(false, false);
			searchFolderCheck.setValue(false);
			searchContentCheck.setValue(false);
			// Title Set
			titleText.setValue(opt.getTitle());
			// FileName Set
			fileNameText.setValue(opt.getFileName());
			// keyword Set
			keywordText.setValue(opt.getKeyword());
			// 템플릿 Set
			if(opt.getTemplateId() != null && opt.getTemplateId() != 0){
				beforeSTemplate = null;
				// Template ID를 통해 STemplate 획득
				LinkedHashMap<STemplate, String> templateMap = SearchUtil.getTemplatemap();
				Set<STemplate> templateSet =  templateMap.keySet();
				for (STemplate sTemp : templateSet) {
					if(sTemp.getId() == opt.getTemplateId()){
						templateIdSelect.setValue(sTemp);
						sTemplate = sTemp;
						itemsMap.clear();
						// 추가 Template 속성값 적용
						if(opt.getTemplateCondition().size() > 0){
							for (int i = 0 ; i < opt.getTemplateCondition().size() ; i++) {
								setExTemplateItems(opt.getTemplateCondition().get(i));
							}
							// 추가 속성 Items 값 보여주기
							SearchPanel.get().getLeftMenu().refreshFormByFormItems(itemsMap, "template");
						}
						break;
					}
				}
			}else{
				itemsMap.clear();
				templateIdSelect.setValue(SearchUtil.all);
				SearchPanel.get().getLeftMenu().refreshFormByFormItems(itemsMap, "template");
			}
			// Size Set
			if(opt.getSizeMax() != null){
				Long MB = 1000000L;
				if(opt.getSizeMax() == 0L){
					sizeSelect.setValue(SearchUtil.all);
				}else if(opt.getSizeMax() == MB){
					sizeSelect.setValue("~ 1MB");
				}else if(opt.getSizeMax() == 3*MB){
					sizeSelect.setValue("1MB ~ 3MB");
				}else if(opt.getSizeMax() == 10*MB){
					sizeSelect.setValue("3MB ~ 10MB");
				}else if(opt.getSizeMax() == 10000*MB){
					sizeSelect.setValue("10MB ~");
				}
				sizeMin = opt.getSizeMin();
				sizeMax = opt.getSizeMax();
			}else{
				sizeMin = 0;
				sizeMax = 0;
			}
			// 날짜 Set
			createDateSelect.setValue(opt.getCreateDateValue());
			expireDateSelect.setValue(opt.getExDateValue());
			fcreateDate.setValue(SearchUtil.setDatebyString(opt.getCreateDateFrom()));
			bcreateDate.setValue(SearchUtil.setDatebyString(opt.getCreateDateTo()));
			fmodifyDate.setValue(SearchUtil.setDatebyString(opt.getModifyDateFrom()));
			bmodifyDate.setValue(SearchUtil.setDatebyString(opt.getModifyDateTo()));
			fexpireDate.setValue(SearchUtil.setDatebyString(opt.getExpiredDateFrom()));
			bexpireDate.setValue(SearchUtil.setDatebyString(opt.getExpiredDateTo()));
			
			// 폴더
			if(opt.getFolder() != null){
				ServiceUtil.folder().getFolder(Session.get().getSid(), opt.getFolder(), true, false, new AsyncCallback<SFolder>() {
					@Override
					public void onSuccess(SFolder result) {
						folder = result;
						folderText.setValue(I18N.messageForFolderPath(result.getPathExtended()));
					}
					@Override
					public void onFailure(Throwable caught) {
						Log.serverError(caught, false);
					}
				});
			}else{
				folder = null;
				folderText.setValue("");
			}
			
			// 20131223, junsoo, 범위
//			selectRange.setValue(Integer.toString(opt.getRange()));
			
			// 문서 형식
			if(opt.getDocTypeId() != 0){
				docTypeSelect.setValue(SearchUtil.getDoctype().get("/"+opt.getDocTypeId()));
				docTypeId = opt.getDocTypeId();
			}else{
				docTypeSelect.setValue(0+"/"+SearchUtil.all);
			}
			
			// 소유자
//			if(opt.getOwnerId() != null && opt.getOwnerId().length() > 0){
//				ownerInfo = new String[1][2];
//				ServiceUtil.security().getUser(Session.get().getSid(), opt.getOwnerId(), new AsyncCallback<SUser>() {
//					@Override
//					public void onSuccess(SUser result) {
//						ownerInfo[0][0] = String.valueOf(opt.getOwnerId());
//						ownerInfo[0][1] = result.getName();
//						ownerText.setValue(result.getName());
//					}
//					@Override
//					public void onFailure(Throwable caught) {
//						Log.serverError(caught, false);
//					}
//				});
//			}else{
//				ownerInfo = new String[1][2];
//				ownerText.setValue("");
//			}
		}
		SearchPanel.get().getLeftMenu().redrawForm();
	}

	/*
	 * getter, Setter
	 * */
	public int getCurrentOffset() {
		return currentOffset;
	}
	public void setCurrentOffset(int currentOffset) {
		this.currentOffset = currentOffset;
	}
	public HashMap<String, FormItem> getSearchItemsMap() {
		return searchItemsMap;
	}
	public void setOwnerInfo(String[][] ownerInfo) {
		this.ownerInfo = ownerInfo;
	}
	public void setSearchOption(int searchOption) {
		this.searchOption = searchOption;
	}
	public void setFolder(SFolder folder) {
		this.folder = folder;
	}
}
