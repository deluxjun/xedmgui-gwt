package com.speno.xedm.gui.frontend.client.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.VisibilityMode;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.ImgButton;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.SectionStack;
import com.smartgwt.client.widgets.layout.SectionStackSection;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.layout.events.SectionHeaderClickEvent;
import com.smartgwt.client.widgets.layout.events.SectionHeaderClickHandler;
import com.speno.xedm.core.service.serials.SSearchOptions;
import com.speno.xedm.gui.common.client.Constants;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.Session;
import com.speno.xedm.gui.common.client.log.Log;
import com.speno.xedm.gui.common.client.util.AdminMenuUtil;
import com.speno.xedm.gui.common.client.util.IFAdminMenuPriv;
import com.speno.xedm.gui.common.client.util.IFHistoryObserver;
import com.speno.xedm.gui.common.client.util.SearchUtil;
import com.speno.xedm.gui.frontend.client.panels.MainPanel;

public class SearchMenu extends SectionStack implements IFAdminMenuPriv, IFHistoryObserver{
	private static SearchMenu instance;
	public static SearchMenu get() {
		if (instance == null)
			instance = new SearchMenu();
		return instance;
	}
	public static final String SEARCH_MENU_NORMAL= "searchs_basic";
//	public static final String SEARCH_MENU_FULL = "searchs_fulltext";
	public static final String SEARCH_MENU_ECM = "searchs_ecm";
	public static final String SEARCH_MENU_GOECM = "searchs_goecm";

	// 섹션
	private SectionStackSection normalSearch;
//	private SectionStackSection fullSearch;
	private SectionStackSection ecmSearch;
	private SectionStackSection goEcmSearch;
	
	ButtonLinkPanel goEcmMenu = new ButtonLinkPanel();

	// Normal Search
	private Canvas normalSearchCanvas;
	private VLayout normalSearchLayout;
	private DynamicForm normalSearchForm;
	private DynamicForm normalTemplateForm;
	private NormalSearchItems searchItems;

	// ECM Search 
	private ContentsSearchItems ecmSearchItems;
	private DynamicForm ecmOptionForm;
	private Canvas ecmSearchCanvas;

	
	// Search Menu 초기화
	public SearchMenu(){
		setWidth("17%");
		setHeight100();
		setVisibilityMode(VisibilityMode.MUTEX);
		initSection();
//		HistoryUtil.get().newHistory(this, "search;" + "normal" + ";");
	}

	// Search Menu Section 초기화
	private void initSection(){
		searchItems = new NormalSearchItems(NormalSearchItems.searchMenu);
		searchItems.setSearchOption(Constants.SEARCH_PLACE_SEARCH);
		ecmSearchItems = new ContentsSearchItems();
		
		// 20130806 taesu, 초기 선택 섹션을 제거하기 위해 설정하였음
//		collapseSection(0);
		controllSectionByClick();
	}
	
	/**
	 *	섹션 선택시 동작
	 */
	private void controllSectionByClick(){
		addSectionHeaderClickHandler(new SectionHeaderClickHandler() {
			@Override
			public void onSectionHeaderClick(SectionHeaderClickEvent event) {
				if (SearchMenu.this.sectionIsExpanded(event.getSection().getID()))
					return;

				if(SEARCH_MENU_NORMAL.equals(event.getSection().getName())) {
					// 20140207, junsoo, saved search 로드는 여기서만. NormalSearchItems 의 resetItems 에 있던 것을 옮김
					searchItems.loadSavedSelectItemValue();
					SearchPanel.get().show(SearchPanel.RESULT_DOCUMENTS);
				}
				else if(SEARCH_MENU_ECM.equals(event.getSection().getName())) {
					SearchPanel.get().show(SearchPanel.RESULT_CONTENTS);
				}
				else if(SEARCH_MENU_GOECM.equals(event.getSection().getName())) {
//					SearchPanel.get().show(SearchPanel.RESULT_CONTENTS);
				}
				else
					SearchPanel.get().showDummy();		// hide
			}
		});
	}
	
	/**
	 * 권한별 Section 생성으로 인해 null 일경우 컨트롤 하지 않게함.(오류 방지)
	 * @param id
	 */
	private void collapseSectionById(String id){
		if(getSection(id) != null)	collapseSection(id);
	}

	/**
	 * Form Item을 초기화하고 재구성한다.
	 * @param itemsMap
	 * @param where : 'template', 'ecm'
	 */
	public void refreshFormByFormItems(Map<Integer, FormItem[]> itemsMap, String where){
		ArrayList<FormItem> arr = new ArrayList<FormItem>();
		for (FormItem[] item : itemsMap.values()) {
			arr.add(item[0]);
			arr.add(item[1]);
			arr.add(item[2]);
			arr.add(item[3]);
		}
		if(where.equals("template")){
			normalTemplateForm.setItems(arr.toArray(new FormItem[0]));
		}else if(where.equals("ecm")){
			ecmOptionForm.setItems(arr.toArray(new FormItem[0]));
		}
	}
	
	/**
	 * 일반 검색 Layout Setting
	 */
	private void setNormalSearchLay(){
		// 스크롤 자동 생성용 ScrollPanel
		normalSearchCanvas = new Canvas();
		normalSearchCanvas.setOverflow(Overflow.AUTO);

		// Form 삽입용 Layout
		normalSearchLayout = new VLayout();
		normalSearchLayout.setAutoHeight();
		normalSearchLayout.setAutoWidth();
		HashMap<String, FormItem> itemMap = searchItems.getSearchItemsMap();
		searchItems.setSearchOption(Constants.SEARCH_PLACE_SEARCH);
		
		/**
		 * 
		*	normalSearchForm
		*
		**/
		normalSearchForm = new DynamicForm();
		SearchUtil.initForm(normalSearchForm, 2, 232);
		
		normalSearchForm.setItems(
				itemMap.get("saved")
//				,itemMap.get("range")			// 20131224, junsoo, 검색범위
				,itemMap.get("folder")
				,itemMap.get("title")			//itemMap.get("search")
				,itemMap.get("fileName")
				,itemMap.get("keyword")
				,itemMap.get("docType")
				,itemMap.get("size")
				,itemMap.get("owner")
				,itemMap.get("templateId")
				);
		
		itemMap.get("saved").setStartRow(true);
		itemMap.get("saved").setColSpan(8);
//		itemMap.get("range").setStartRow(true);
//		itemMap.get("range").setColSpan(8);
		itemMap.get("fileName").setStartRow(true);
		itemMap.get("fileName").setColSpan(8);
		itemMap.get("keyword").setStartRow(true);
		itemMap.get("keyword").setColSpan(8);
		itemMap.get("docType").setStartRow(true);
		itemMap.get("docType").setColSpan(8);
		itemMap.get("templateId").setStartRow(true);
		itemMap.get("templateId").setColSpan(8);
		itemMap.get("folder").setStartRow(true);
		itemMap.get("folder").setColSpan(8);
		itemMap.get("folder").setEndRow(true);
		itemMap.get("owner").setStartRow(true);
		itemMap.get("owner").setColSpan(8);
		itemMap.get("size").setStartRow(true);
		itemMap.get("size").setColSpan(8);

		/**
		 * 
		*	템플릿 추가 속성 Form
		*
		**/
		normalTemplateForm = new DynamicForm();
		SearchUtil.initForm(normalTemplateForm, 3, 230);
		
		/**
		 * 
		*	createDateForm
		*
		**/
		DynamicForm createDateForm = new DynamicForm();
		SearchUtil.setDateForm(createDateForm, I18N.message("createddate") ,itemMap.get("createDate"));		
		createDateForm.setItems(
				itemMap.get("createDate")
				,itemMap.get("fcreateDate"),	itemMap.get("commonDateItem"),	itemMap.get("bcreateDate")
				);
		itemMap.get("fcreateDate").setColSpan(5);
		itemMap.get("fcreateDate").setColSpan(3);
		
		/**
		 * 
		 *	modifiyDateForm
		 *
		 **/
		DynamicForm modifiyDateForm = new DynamicForm();
		SearchUtil.setDateForm(modifiyDateForm, I18N.message("modifieddate"), itemMap.get("modifyDate"));
		modifiyDateForm.setItems(
				itemMap.get("modifyDate")
				,itemMap.get("fmodifyDate"), 	itemMap.get("commonDateItem"),	itemMap.get("bmodifyDate")
				);
		itemMap.get("fmodifyDate").setColSpan(5);
		itemMap.get("bmodifyDate").setColSpan(3);
		
		/**
		 * 
		*	expireDateForm
		*
		**/
		DynamicForm expireDateForm = new DynamicForm();
		SearchUtil.setDateForm(expireDateForm, I18N.message("s.expiredate"), itemMap.get("expireDate"));
		expireDateForm.setItems(
				itemMap.get("expireDate")
				,itemMap.get("fexpireDate"), 	itemMap.get("commonDateItem"),	itemMap.get("bexpireDate")
				);
		itemMap.get("fexpireDate").setColSpan(5);
		itemMap.get("bexpireDate").setColSpan(3);
		
		/**
		 * 
		 *	checkForm
		 *
		 **/
		DynamicForm checkForm = new DynamicForm();
		checkForm.setWidth(225);
		checkForm.setMargin(5);
		checkForm.setNumCols(4);
//		checkForm.setIsGroup(true);
		checkForm.setColWidths("30","30","30","*");
//		checkForm.setGroupTitle(I18N.message("s.searchin"));
		checkForm.setAlign(Alignment.LEFT);
		
		HLayout hLayout = new HLayout();
		

		if(Session.get().getInfo().getConfig("settings.use.fullTextSearch").equals("true")){
			checkForm.setItems(
					itemMap.get("folderCheck"),		itemMap.get("contentCheck")
					);
			itemMap.get("folderCheck").setStartRow(true);
			itemMap.get("folderCheck").setEndRow(false);
			itemMap.get("contentCheck").setStartRow(false);
			hLayout.addMembers(checkForm);
		}else{
			//			SpacerItem spacer = new SpacerItem();
			//20131223na 검색 디자인 변경
			DynamicForm searchForm = new DynamicForm();
			DynamicForm initializeForm = new DynamicForm();
			checkForm.setWidth(100);
			
			checkForm.setItems(itemMap.get("folderCheck"));
			searchForm.setItems(itemMap.get("search"));
			initializeForm.setItems(itemMap.get("initialize"));
			
			hLayout.addMembers(checkForm, searchForm, initializeForm);
		}
		
		normalSearchLayout.addMembers(
				hLayout
				,normalSearchForm
				,normalTemplateForm
				,createDateForm
				,modifiyDateForm
				,expireDateForm
				);
		normalSearchCanvas.addChild(normalSearchLayout);
	}

	/**
	 * 	Form을 Redraw한다.
	 * */
	public void redrawForm(){
		normalSearchForm.redraw();
	}
	
	/**
	 *	ECM 검색 Layout Setting
	 */
	private void setECMSearchLay(){
		HashMap<String, FormItem> itemMap = ecmSearchItems.getEcmSearchItemsMap();

		ecmSearchCanvas = new Canvas();
		ecmSearchCanvas.setOverflow(Overflow.AUTO);

		// Form 삽입용 Layout
		VLayout ecmSearchLayout = new VLayout();
		ecmSearchLayout.setAutoHeight();
		ecmSearchLayout.setAutoWidth();
		
		// 고정 Item Form`
		DynamicForm ecmForm = new DynamicForm();
		SearchUtil.initForm(ecmForm, 2, 230);
		
		ecmForm.setItems(
				itemMap.get("index")
				,itemMap.get("elementId")
				);
		
		ecmOptionForm = new DynamicForm();
		SearchUtil.initForm(ecmOptionForm, 3, 230);
		
		itemMap.get("elementId").setStartRow(true);
		
		ecmSearchLayout.addMembers(ecmForm, ecmOptionForm);
		ecmSearchCanvas.addChild(ecmSearchLayout);
	}
	
	
	/**
	 * History Action
	 * */
	@Override
	public void selectMenu(String name, String guid, boolean bByHistory){
		Log.debug("[SearchMenu] selectMenu");
		MainPanel.get().selectSearchTab();
		
		expandSection(name);
		
		if(name.equals(SEARCH_MENU_NORMAL)){
			searchItems.searchBySaved(guid);
		}
//		else if(name.equals(SEARCH_MENU_FULL)){
////			searchItems.searchBySaved(guid);
//		}
		else if(name.equals(SEARCH_MENU_ECM)){
//			searchItems.searchBySaved(guid);
		}
	}
	
	@Override
	public void buildMenu(final String finalCallbackId, final long parentMenuId, boolean hasHistory) {
		Log.debug("[SearchMenu] building menu..");
		AdminMenuUtil.get().hasPriv(finalCallbackId, parentMenuId, "searchs_basic", new AsyncCallback<Long>() {
			@Override
			public void onSuccess(Long id) {
				if (id != null) {
					setNormalSearchLay();

					normalSearch = new SectionStackSection(I18N.message("dbSearch"));
					normalSearch.setName(SEARCH_MENU_NORMAL);
					normalSearch.setID(SEARCH_MENU_NORMAL);
					normalSearch.setResizeable(false);
					normalSearch.setCanCollapse(true);
					normalSearch.setItems(normalSearchCanvas);
					normalSearch.setExpanded(true);
					SearchPanel.get().show(SearchPanel.RESULT_DOCUMENTS);

					ImgButton saveButton = new ImgButton();  
					saveButton.setSrc("[SKIN]/headerIcons/save_Over.png");  
					saveButton.setSize(16);  
					saveButton.setShowFocused(false);  
					saveButton.setShowRollOver(false);  
					saveButton.setShowDown(false);  
					saveButton.setTooltip(I18N.message("save"));
					saveButton.addClickHandler(new com.smartgwt.client.widgets.events.ClickHandler() {
						@Override
						public void onClick(com.smartgwt.client.widgets.events.ClickEvent event) {
							SSearchOptions searchOptions = searchItems.getSavedSearchCondition();
							SaveDialog saveDialog = new SaveDialog(searchOptions);
							saveDialog.show();
//							searchItems.resetSearchItems(true);
						}
					});
					
					normalSearch.setControls(saveButton);
					addSection(normalSearch);
				}
				
				AdminMenuUtil.get().hasPriv(finalCallbackId, parentMenuId, "searchs_ecm", new AsyncCallback<Long>() {
					@Override
					public void onSuccess(Long id) {
						boolean isAllianz = "Allianz".equalsIgnoreCase(Session.get().getInfo().getConfig("settings.product.vendor"));
						if (id != null && isAllianz) {
							setECMSearchLay();

							// ECM 검색
							ecmSearch = new SectionStackSection(I18N.message("s.ecmSearch"));
							ecmSearch.setName(SEARCH_MENU_ECM);
							ecmSearch.setID(SEARCH_MENU_ECM);
							ecmSearch.setCanCollapse(true);
							ecmSearch.setItems(ecmSearchCanvas);
							
							addSection(ecmSearch);
						}
					}
					@Override
					public void onFailure(Throwable caught) {}
				});

				
				
				
				AdminMenuUtil.get().hasPriv(finalCallbackId, parentMenuId, "searchs_goecm", new AsyncCallback<Long>() {
					@Override
					public void onSuccess(Long id) {
						System.out.println("id  ==== " + id);
						boolean isAllianz = "Allianz".equalsIgnoreCase(Session.get().getInfo().getConfig("settings.product.vendor"));
						if (id != null && isAllianz) {
							// ECM 검색
							goEcmSearch = new SectionStackSection(I18N.message("ecmlog"));
							goEcmSearch.setName(SEARCH_MENU_GOECM);
							goEcmSearch.setID(SEARCH_MENU_GOECM);
							goEcmSearch.addItem(goEcmMenu);
							addSection(goEcmSearch);
							
						}
					}
					@Override
					public void onFailure(Throwable caught) {}
				});
			}
			@Override
			public void onFailure(Throwable caught) {
			}
		});
	}
	

	public NormalSearchItems getSearchItems() {
		return searchItems;
	}

	public ContentsSearchItems getEcmSearchItems() {
		return ecmSearchItems;
	}

	public Canvas getNormalSearchCanvas() {
		return normalSearchCanvas;
	}
	
	@Override
	public void selectByHistory(String refid) {
		String[] tags = refid.split(";");
		if (tags != null && tags.length > 0) {
			if ("search".equals(tags[0]) && tags.length > 2) {
				selectMenu(tags[1], tags[2], true);
			}
		}
		Session.get().setCurrentMenuId(refid);
	}

	@Override
	public void onHistoryAdded(String refid) {
		Session.get().setCurrentMenuId(refid);
	}
}
