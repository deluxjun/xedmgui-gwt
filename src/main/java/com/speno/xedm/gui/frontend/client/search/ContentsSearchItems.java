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
	// �˻� �޴� ������
	private SelectItem indexSelectItem;
	private TextItem elementIdText;
	private PickerIcon searchPicker;
	private PickerIcon cleanPicker;
	// �߰� �˻� �޴� ������
	private SelectItem	exOperatorSelect;
	private FormItem	exFormItem;
	private SelectItem	exAndOrSelect;

	// Page ����
	private PagingConfig config;
	
	// ECM �˻� �޴� ������ ����(Search Menu���� �޾� �޴��� �����ϰ� ��)
	private HashMap<String, FormItem> ecmSearchItemsMap;
	// �߰� �˻����� ����
	private List condition = new ArrayList();
	// �ε��� ���̵� Select Item ���ý� ���� Grid ȭ�鿡 ������ �ʵ�� ����
	private String[] gridFieldName;
	// �ε��� ���̵� Select Item ���ý� ���� �߰� �˻� ���� ������ ȭ�鿡 �����ֱ� ���� ����
	private LinkedHashMap<Integer, FormItem[]> itemsMap = new LinkedHashMap<Integer, FormItem[]>();
	// ECM Index Id ����
	private LinkedHashMap<String, String> indexIds = new LinkedHashMap<String, String>();
	
	public ContentsSearchItems(){
		initItems();
		initECMSearchItemsMap();
		initActions();
	}
	
	/**
	 *	������ �ʱ�ȭ 
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
	 * Index Select Item ���� set�Ѵ�.
	 */
	private void setIndexSelectItem(){
		// Context Property�� ���� ecm Id�� ȹ��
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
	 * Index Select Item���� �� ���ý� �ش��ϴ� �������� �÷��� Ÿ�� ������ ȹ���Ѵ�.
	 * @param indexId
	 */
	private void getECMColumnInfo(String indexId){
		itemsMap.clear();
		ServiceUtil.security().getEcmColumnInfo(Session.get().getSid(), indexId, new AsyncCallback<Map<String, Integer>>(){
			@Override
			public void onSuccess(Map<String, Integer> result) {
				// �÷��� ����
				gridFieldName = result.keySet().toArray(new String[0]);
				// Ÿ�� ����
				String type = result.values().toString();
				String[] types = type.split("\\,");
				
				for(int i=0 ; i < result.size() ; i++){
					// ELEMENTID �÷��� �߰����� �ʴ´�.
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
	 * �߰� �Է� ���� ������ �ʱ�ȭ
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
		// ���� operator '='���� ������Ŵ
		exOperatorSelect.setDisabled(true);
	}
	
	/**
	 *  �˻� �׼�
	 *  �˻� ������ Set�ϰ�, ������ ��û �� Grid�� �����͸� Set�Ѵ�.
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
	 * ���� �˻� ��û 
	 * @param config
	 */
	public void searchAction(PagingConfig config){
		if(!validation())	return;	
		
		SSearchOptions opt = new SSearchOptions();
//		opt.setCaseSensitive(1);	// 0(True), 1(False)
		// ���� ���� ���̵� Set
		opt.setUserId(Session.get().getUser().getId());
		// �˻� Type Set
		opt.setType(SSearchOptions.TYPE_ECM);
		// ECM IndexId(Table)
		opt.setEcmIndexId(indexSelectItem.getValueAsString());
		// ElementID Set yuk 20140317 Ʈ�� ����
		if (elementIdText.getValueAsString() != null)
		opt.setElementId(elementIdText.getValueAsString().trim());
		
		// �߰� �˻����� �ʱ�ȭ
		condition.clear();
		// �߰� �˻� ���� �Է�
		for(int i=0 ; i < itemsMap.size()+1 ; i++){
			// �߰� �˻������� ������� �߰� �˻������� �����Ѵ�.
			if(itemsMap.get(i) == null) continue;
			String column = 	String.valueOf(itemsMap.get(i)[0].getValue());	// 1. �÷���
		
			String operator = 	String.valueOf(itemsMap.get(i)[1].getValue());	// 2. operator(=,<,> ...)
			String info =		String.valueOf(itemsMap.get(i)[2].getValue());	// 3. ����
			String reOperator = String.valueOf(itemsMap.get(i)[3].getValue());	// 4. relational operator(AND, OR)
			if(!info.equals("null"))
				condition.add(new String[]{reOperator, column, operator, info});	// 4, 1, 2, 3
		}
		// �߰� �˻����� Set
		opt.setEcmCondition(condition);
		
		// �˻� �ε�ȭ���� ������
		Waiting.show(I18N.message("s.nowsearching"));
		config.setOffset(config.getOffset());
		// �˻� �ǽ�
		ServiceUtil.search().searchECM(Session.get().getSid(), opt, config, new AsyncCallback<PagingResult<Map<String,String>>>() {
			@Override
			public void onSuccess(PagingResult<Map<String, String>> result) {
				// �˻� �Ϸ�� �����͸� Grid�� �����ش�.
				Waiting.hide();
				SearchPanel.get().setContentsData(result.getData());
				
			}
			
			@Override
			public void onFailure(Throwable caught) {
				Waiting.hide();
				Log.serverError(caught, false);
			}
		});
		// �˻� �ε�ȭ�� ����
		
	}
	
	/**
	 *  �׼� �ʱ�ȭ
	 */
	private void initActions(){
		indexSelectItem.addChangedHandler(new ChangedHandler() {
			@Override
			public void onChanged(ChangedEvent event) {
				//20140221na 1�������� �ʱ�ȭ
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
	 *  ECM Form Item ���� �� �ʱ�ȭ
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
