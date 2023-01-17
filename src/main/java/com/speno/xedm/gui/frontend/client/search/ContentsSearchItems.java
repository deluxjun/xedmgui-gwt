package com.speno.xedm.gui.frontend.client.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.PickerIcon;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.form.fields.events.FormItemClickHandler;
import com.smartgwt.client.widgets.form.fields.events.FormItemIconClickEvent;
import com.smartgwt.client.widgets.form.fields.events.KeyPressEvent;
import com.smartgwt.client.widgets.form.fields.events.KeyPressHandler;
import com.speno.xedm.core.service.serials.SAdminMenu;
import com.speno.xedm.core.service.serials.SSearchOptions;
import com.speno.xedm.gui.common.client.Constants;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.Session;
import com.speno.xedm.gui.common.client.log.Log;
import com.speno.xedm.gui.common.client.util.LengthValidator;
import com.speno.xedm.gui.common.client.util.SearchUtil;
import com.speno.xedm.gui.common.client.util.ServiceUtil;
import com.speno.xedm.gui.common.client.window.Waiting;
import com.speno.xedm.gui.frontend.client.PagingToolStrip;
import com.speno.xedm.util.paging.PagingConfig;
import com.speno.xedm.util.paging.PagingResult;
import com.speno.xedm.util.paging.SortDir;

public class ContentsSearchItems {
	// 검색 메뉴 아이템
	private SelectItem indexSelectItem;
	private TextItem elementIdText;
	private PickerIcon searchPicker;
	private PickerIcon cleanPicker;
	// 추가 검색 메뉴 아이템
	private SelectItem	exOperatorSelect;
	private FormItem	exFormItem;
	private SelectItem	exAndOrSelect;

	// Page 설정
	private PagingConfig config;
	
	// ECM 검색 메뉴 아이템 저장(Search Menu에서 받아 메뉴를 구성하게 함)
	private HashMap<String, FormItem> ecmSearchItemsMap;
	// 추가 검색조건 저장
	private List condition = new ArrayList();
	// 인덱스 아이디 Select Item 선택시 우측 Grid 화면에 보여줄 필드명 저장
	private String[] gridFieldName;
	// 인덱스 아이디 Select Item 선택시 얻어온 추가 검색 조건 값들을 화면에 보여주기 위한 변수
	private LinkedHashMap<Integer, FormItem[]> itemsMap = new LinkedHashMap<Integer, FormItem[]>();
	// ECM Index Id 저장
	private LinkedHashMap<String, String> indexIds = new LinkedHashMap<String, String>();
	
	public ContentsSearchItems(){
		initItems();
		initECMSearchItemsMap();
		initActions();
	}
	
	/**
	 *	아이템 초기화 
	 */
	private void initItems(){
		indexSelectItem = new SelectItem("indexSelect", I18N.message("indexid"));
		SearchUtil.initItem(indexSelectItem, 130, Alignment.LEFT);
    	searchPicker = new PickerIcon(PickerIcon.SEARCH);
        cleanPicker = new PickerIcon(PickerIcon.REFRESH);
        searchPicker.setPrompt(I18N.message("search"));
        cleanPicker.setPrompt(I18N.message("s.searchreset"));
        indexSelectItem.setIcons(searchPicker, cleanPicker);

		elementIdText = new TextItem("elementIdText", I18N.message("elementId"));
		elementIdText.setValidators(new LengthValidator(elementIdText, Constants.MAX_LEN_NAME));
		SearchUtil.initItem(elementIdText, 130, Alignment.LEFT);
		
		setIndexSelectItem();
	}
	
	/**
	 * Index Select Item 값을 set한다.
	 */
	private void setIndexSelectItem(){
		// Context Property로 부터 ecm Id를 획득
		Long ecmId = Long.parseLong(Session.get().getInfo().getConfig("setting.adminmenu.ecm.id"));
		
		ServiceUtil.security().findByUserIdAndParentId(Session.get().getSid(), ecmId, new AsyncCallback<List<SAdminMenu>>() {
			@Override
			public void onSuccess(List<SAdminMenu> result) {
				for (SAdminMenu adminMenu : result) {
					indexIds.put(adminMenu.getTitle(), adminMenu.getTitle());
				}
				indexSelectItem.setValueMap(indexIds);
			}
			@Override
			public void onFailure(Throwable caught) {
				Log.serverError(caught, false);
			}
		});
	}

	/**
	 * Index Select Item에서 값 선택시 해당하는 데이터의 컬럼과 타입 정보를 획득한다.
	 * @param indexId
	 */
	private void getECMColumnInfo(String indexId){
		itemsMap.clear();
		ServiceUtil.security().getEcmColumnInfo(Session.get().getSid(), indexId, new AsyncCallback<Map<String, Integer>>(){
			@Override
			public void onSuccess(Map<String, Integer> result) {
				// 컬럼명 저장
				gridFieldName = result.keySet().toArray(new String[0]);
				// 타입 저장
				String type = result.values().toString();
				String[] types = type.split("\\,");
				
				for(int i=0 ; i < result.size() ; i++){
					// ELEMENTID 컬럼은 추가하지 않는다.
					if(!gridFieldName[i].equals("ELEMENTID")){
						initExItems();
						StaticTextItem item = new StaticTextItem();
						item.setValue(gridFieldName[i]);
						item.setAttribute("ecmType", types[i]);
						item.setShowTitle(false);
						item.setStartRow(true);
						itemsMap.put(i, new FormItem[]{item, exOperatorSelect, exFormItem, exAndOrSelect});
					}
				}
				SearchMenu.get().refreshFormByFormItems(itemsMap,"ecm");
				
				// set fields of contents
				SearchPanel.get().setContentsFields(gridFieldName);
			}
			@Override
			public void onFailure(Throwable caught) {
				Log.serverError(caught, false);
			}
		});
	}

	/**
	 * 추가 입력 정보 아이템 초기화
	 */
	private void initExItems(){
		exOperatorSelect= new SelectItem();
		exFormItem 		= new FormItem();
		exAndOrSelect	= new SelectItem();
		
		exFormItem.setWidth(80);
		exFormItem.setShowTitle(false);
		exFormItem.addKeyPressHandler(new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent event) {
				if(event.getKeyName().equals("Enter")){
					doSearch();
				}
			}
		});
		SearchUtil.setOperatorItem(exOperatorSelect);
		SearchUtil.setAndOrItem(exAndOrSelect);
		exAndOrSelect.setEndRow(true);
		// 현재 operator '='으로 고정시킴
		exOperatorSelect.setDisabled(true);
	}
	
	/**
	 *  검색 액션
	 *  검색 정보를 Set하고, 서버에 요청 후 Grid에 데이터를 Set한다.
	 */
	
	public void doSearch(){
		SearchPanel.get().setPageNum(1);
		doSearch(1,SearchPanel.get().getPageSize());
	}
	
	public void doSearch(int pageNum, int pageSize) {
		config = PagingToolStrip.getPagingConfig(pageNum , pageSize, "lastModified", SortDir.DESC);
		searchAction(config);
	}
	
	/**
	 * Ecm Search Validation
	 */
	private boolean validation(){
		if(indexSelectItem.getValueAsString() == null)
			return false;
		
		return true;
	}

	/**
	 * 서버 검색 요청 
	 * @param config
	 */
	public void searchAction(PagingConfig config){
		if(!validation())	return;	
		
		SSearchOptions opt = new SSearchOptions();
//		opt.setCaseSensitive(1);	// 0(True), 1(False)
		// 현재 유저 아이디 Set
		opt.setUserId(Session.get().getUser().getId());
		// 검색 Type Set
		opt.setType(SSearchOptions.TYPE_ECM);
		// ECM IndexId(Table)
		opt.setEcmIndexId(indexSelectItem.getValueAsString());
		// ElementID Set yuk 20140317 트림 수정
		if (elementIdText.getValueAsString() != null)
		opt.setElementId(elementIdText.getValueAsString().trim());
		
		// 추가 검색조건 초기화
		condition.clear();
		// 추가 검색 정보 입력
		for(int i=0 ; i < itemsMap.size()+1 ; i++){
			// 추가 검색조건이 없을경우 추가 검색조건을 제거한다.
			if(itemsMap.get(i) == null) continue;
			String column = 	String.valueOf(itemsMap.get(i)[0].getValue());	// 1. 컬럼명
		
			String operator = 	String.valueOf(itemsMap.get(i)[1].getValue());	// 2. operator(=,<,> ...)
			String info =		String.valueOf(itemsMap.get(i)[2].getValue());	// 3. 정보
			String reOperator = String.valueOf(itemsMap.get(i)[3].getValue());	// 4. relational operator(AND, OR)
			if(!info.equals("null"))
				condition.add(new String[]{reOperator, column, operator, info});	// 4, 1, 2, 3
		}
		// 추가 검색조건 Set
		opt.setEcmCondition(condition);
		
		// 검색 로딩화면을 보여줌
		Waiting.show(I18N.message("s.nowsearching"));
		config.setOffset(config.getOffset());
		// 검색 실시
		ServiceUtil.search().searchECM(Session.get().getSid(), opt, config, new AsyncCallback<PagingResult<Map<String,String>>>() {
			@Override
			public void onSuccess(PagingResult<Map<String, String>> result) {
				// 검색 완료된 데이터를 Grid에 보여준다.
				Waiting.hide();
				SearchPanel.get().setContentsData(result.getData());
				
			}
			
			@Override
			public void onFailure(Throwable caught) {
				Waiting.hide();
				Log.serverError(caught, false);
			}
		});
		// 검색 로딩화면 종료
		
	}
	
	/**
	 *  액션 초기화
	 */
	private void initActions(){
		indexSelectItem.addChangedHandler(new ChangedHandler() {
			@Override
			public void onChanged(ChangedEvent event) {
				//20140221na 1페이지로 초기화
				SearchPanel.get().setPageNum(1);
				SearchPanel.get().resetGridForContents();		
				getECMColumnInfo(indexSelectItem.getValueAsString());
			}
		});
		
		elementIdText.addKeyPressHandler(new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent event) {
				if(event.getKeyName().equals("Enter")){
					doSearch();
				}
			}
		});
        searchPicker.addFormItemClickHandler(new FormItemClickHandler() {
			@Override
			public void onFormItemClick(FormItemIconClickEvent event) {
//				getECMColumnInfo(indexSelectItem.getValueAsString());
				doSearch();
			}
		});
        cleanPicker.addFormItemClickHandler(new FormItemClickHandler() {
        	@Override
        	public void onFormItemClick(FormItemIconClickEvent event) {
				itemsMap.clear();
				indexSelectItem.clearValue();
				elementIdText.setValue("");
				SearchMenu.get().refreshFormByFormItems(itemsMap, "ecm");
        	}
        });
	}
	
	/**
	 *  ECM Form Item 생성 및 초기화
	 */
	private void initECMSearchItemsMap(){
		HashMap<String, FormItem> itemMap = new HashMap<String, FormItem>();
		itemMap.put("index", indexSelectItem);
		itemMap.put("elementId", elementIdText);
		
		ecmSearchItemsMap = itemMap;
	}

	public HashMap<String, FormItem> getEcmSearchItemsMap() {
		return ecmSearchItemsMap;
	}
}
