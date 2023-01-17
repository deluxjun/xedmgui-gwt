package com.speno.xedm.gui.frontend.client.search;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.ContentsType;
import com.smartgwt.client.widgets.HTMLPane;
import com.smartgwt.client.widgets.Img;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.LinkItem;
import com.smartgwt.client.widgets.form.fields.PickerIcon;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.FocusEvent;
import com.smartgwt.client.widgets.form.fields.events.FocusHandler;
import com.smartgwt.client.widgets.form.fields.events.FormItemClickHandler;
import com.smartgwt.client.widgets.form.fields.events.FormItemIconClickEvent;
import com.smartgwt.client.widgets.form.fields.events.KeyDownEvent;
import com.smartgwt.client.widgets.form.fields.events.KeyDownHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.TabSet;
import com.speno.xedm.core.service.serials.SContent;
import com.speno.xedm.core.service.serials.SHit;
import com.speno.xedm.core.service.serials.SInfo;
import com.speno.xedm.core.service.serials.SSearchOptions;
import com.speno.xedm.gui.common.client.Constants;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.Session;
import com.speno.xedm.gui.common.client.util.ItemFactory;
import com.speno.xedm.gui.common.client.util.ServiceUtil;
import com.speno.xedm.gui.common.client.util.Util;
import com.speno.xedm.gui.frontend.client.PagingToolStrip;
import com.speno.xedm.gui.frontend.client.document.DashboardPanel;
import com.speno.xedm.gui.frontend.client.document.DocumentsPanel;
import com.speno.xedm.gui.frontend.client.panels.MainPanel;
import com.speno.xedm.util.paging.PagingConfig;
import com.speno.xedm.util.paging.PagingResult;
import com.speno.xedm.util.paging.SortDir;

public class FullTextPanel extends VLayout{
	private String defaultUrl = Session.get().getInfo().getConfig("gui.fulltextsearch.url");
	private HTMLPane htmlPane;
	private VLayout searchResultPanel = new VLayout();
	private TextItem searchItem = new TextItem();
	private HLayout searchPanel = new HLayout();
	private HLayout searchPage = new HLayout();
	//키가 눌렸을 때 검색하는 페이지로 넘어감.
	
	public static FullTextPanel instance;
	public static FullTextPanel get(){
		if(instance == null)
			instance = new FullTextPanel();
		return instance;
	}
	
	public FullTextPanel() {
		setWidth100();
		setHeight100();
		
		init();
	}
	
	private void init(){
		if("".equals(defaultUrl)) {
			this.setLayoutLeftMargin(25);
			this.setLayoutRightMargin(25);
			this.setLayoutTopMargin(40);
			
			searchPanel.setAlign(Alignment.CENTER);
			searchPanel.setHeight(80);
			
			//로고
			SInfo info = Session.get().getInfo();
			Img logoImage = ItemFactory.newBrandImg(Util.getNameOfServerImage("logo.png"));
			logoImage.setStyleName("logo_head");
			logoImage.setCursor(com.smartgwt.client.types.Cursor.HAND);
			logoImage.setWidth(Util.getWidthOfServerImage(info.getMainLogo()));
			logoImage.setHeight(40);
			logoImage.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					searchItem.setValue("");
					searchResultPanel.removeMembers(searchResultPanel.getMembers());
					
					searchPanel.setAlign(Alignment.CENTER);
					instance.removeMember(searchPanel);
					instance.removeMember(searchResultPanel);
					instance.removeMembers(searchPanel, searchResultPanel, searchPage);
					
					instance.setMembers(searchPanel);
				}
			});
			
			//검색버튼
			HLayout formLayout = new HLayout();
			formLayout.setHeight(50);
			formLayout.setAutoWidth();
			formLayout.setLayoutTopMargin(10);
			formLayout.setLayoutLeftMargin(10);
			
			DynamicForm searchForm = new DynamicForm();
			searchForm.setSize("400px", "30px");
	        searchForm.setItems(searchItem);
			
			PickerIcon searchPicker = new PickerIcon(PickerIcon.SEARCH);
			searchPicker.setPrompt(I18N.message("search"));
			searchPicker.setWidth(30);
			searchPicker.setHeight(30);
			searchPicker.addFormItemClickHandler(new FormItemClickHandler() {
				@Override
				public void onFormItemClick(FormItemIconClickEvent event) {
					search(searchItem.getValueAsString());
				}
			});
			
			//검색창
			searchItem.setAlign(Alignment.CENTER);
	        searchItem.setRequired(true);  
	        searchItem.setDefaultValue(""); 
	        searchItem.setShowTitle(false);
	        searchItem.setHeight(30);
	        searchItem.setWidth(300);
	        searchItem.setIcons(searchPicker);
	        
	        searchItem.addKeyDownHandler(new KeyDownHandler() {
				@Override
				public void onKeyDown(KeyDownEvent event) {
					if("Enter".equals(event.getKeyName())){
						search(searchItem.getValueAsString());
					}
				}
			});
	        
	        searchItem.addFocusHandler(new FocusHandler() {
				@Override
				public void onFocus(FocusEvent event) {
//					searchItem.setTextBoxStyle("fullSearchItem");
					if(!searchItem.getCellStyle().contains("fullSearchItem")){
						searchItem.setCellStyle("fullSearchItem");
						searchItem.updateState();
					}
				}
			});
	        
	        searchPanel.addMember(logoImage);
	        formLayout.addMember(searchForm);
	        searchPanel.addMember(formLayout);
	        this.addMember(searchPanel);
	        
	        //검색 결과 초기화
	        searchResultPanel.setWidth100();
	        searchResultPanel.setHeight100();
	        
			//검색 페이징
	        searchPage.setLayoutBottomMargin(20);
	        
	        DynamicForm nextForm = new DynamicForm();
	        nextForm.setVisible(false);
			LinkItem nextLink = new LinkItem();
			nextLink.setLinkTitle("Next");
			nextLink.setShowTitle(false);
			nextLink.setWidth(300);
			nextForm.setItems(nextLink);
			nextLink.addClickHandler(new com.smartgwt.client.widgets.form.fields.events.ClickHandler() {
				@Override
				public void onClick(
						com.smartgwt.client.widgets.form.fields.events.ClickEvent event) {
					DynamicForm next =  (DynamicForm) searchPage.getMember(1);
					int pageNum = Integer.parseInt(next.getValue("pageNum").toString());
					
					search(searchItem.getValueAsString(), pageNum);
				}
			});
	        
			DynamicForm previousForm = new DynamicForm();
			previousForm.setVisible(false);
			LinkItem previousLink = new LinkItem();
			previousLink.setLinkTitle("Previous");
			previousLink.setShowTitle(false);
			previousLink.setWidth(300);
			previousForm.setItems(previousLink);
			previousLink.addClickHandler(new com.smartgwt.client.widgets.form.fields.events.ClickHandler() {
				@Override
				public void onClick(
						com.smartgwt.client.widgets.form.fields.events.ClickEvent event) {
					DynamicForm previous =  (DynamicForm) searchPage.getMember(0);
					int pageNum = Integer.parseInt(previous.getValue("pageNum").toString());
					
					search(searchItem.getValueAsString(), pageNum);
				}
			});
			
	        searchPage.addMembers(previousForm, nextForm);
		}
		else{
			htmlPane = new HTMLPane();  
			htmlPane.setShowEdges(true);  
			htmlPane.setWidth("100%");
			htmlPane.setContentsType(ContentsType.PAGE);  
			addMember(htmlPane);
			
			search("");
		}
	}
	
	/**
	 * FullText 검색
	 * context-property 에서 url 정보를 얻어와 검색을 한다.
	 * @param keyword
	 */
	public void search(String keyword){
		if("".equals(defaultUrl)) {
			search(keyword, 1);
		}
		else{
			// 연동시킬 페이지 설정
			defaultUrl += "&userid="+Session.get().getUser().getId();
			String searchUrl = "";
			// 검색 키워드 값으로 url 설정
			if(keyword == null){
				searchUrl = defaultUrl.replaceAll("#SEARCHWORD#", "");
			}else
				searchUrl = defaultUrl.replaceAll("#SEARCHWORD#", keyword);
			
			// 20140124, junsoo, SID 추가
			searchUrl = searchUrl.replaceAll("#SID#", Session.get().getSid());
			
			// 검색 결과 페이지의 높이를 결정한다. 필수!!
			htmlPane.setHeight(DashboardPanel.get().getHeight());
			// 페이지 이동
			htmlPane.setContentsURL(searchUrl);
		}
	}
	
	public void search(String keyword, final int pageNum){
		//검색바를 왼쪽으로 이동
		searchPanel.setAlign(Alignment.LEFT);
		this.removeMember(searchPanel);
		this.setMembers(searchPanel, searchResultPanel, searchPage);
		
		
		searchItem.setValue(keyword);
		int pageSize = 40;
		PagingConfig config = PagingToolStrip.getPagingConfig(pageNum, pageSize, "", SortDir.DESC);
		
		SSearchOptions opt = new SSearchOptions();
		opt.setType(SSearchOptions.TYPE_FULLTEXT);
//		opt.setFields(new String[] { "content", "title" });
		opt.setFields(new String[] { "content"});
		opt.setExpressionLanguage("ko");
		opt.setExpression(keyword);
		
		ServiceUtil.search().searchFullDocument(Session.get().getSid(), opt, config, new AsyncCallback<PagingResult<SHit>>() {
			@Override
			public void onSuccess(PagingResult<SHit> result) {
				searchResultPanel.removeMembers(searchResultPanel.getMembers());
				
				List<SHit> list = result.getData();
				for (final SHit hit : list) {
					VLayout searchResult = new VLayout();
					searchResult.setWidth(1000);
					searchResult.setHeight(100);
//					searchResult.setLayoutAlign(VerticalAlignment.CENTER);
					
					DynamicForm titleForm = new DynamicForm();
					titleForm.setWidth100();
					LinkItem titleLink = new LinkItem();
					titleLink.setWidth(700);
					titleLink.setShowTitle(false);
					titleLink.setLinkTitle(hit.getDocument().getIdStr());
					titleLink.addClickHandler(new com.smartgwt.client.widgets.form.fields.events.ClickHandler() {
						@Override
						public void onClick(
								com.smartgwt.client.widgets.form.fields.events.ClickEvent event) {
							Util.downloadAsFrame(hit.getDocument().getId(), null);
						}
					});
					titleForm.setItems(titleLink);
					titleLink.setLinkTitle(hit.getDocument().getTitle());
					
					DynamicForm pathForm = new DynamicForm();
					pathForm.setWidth100();
					LinkItem pathLink = new LinkItem();
					pathLink.setLinkTitle(hit.getFolder().getPathExtended());
					pathLink.setShowTitle(false);
					pathLink.setWidth(300);
					pathForm.setItems(pathLink);
					pathLink.addClickHandler(new com.smartgwt.client.widgets.form.fields.events.ClickHandler() {
						@Override
						public void onClick(
								com.smartgwt.client.widgets.form.fields.events.ClickEvent event) {
							// Tab 이동 위한 변수
							TabSet tabSet = MainPanel.get().getTabSet();
							// Document Tab으로 이동
							tabSet.selectTab(Constants.MAIN_TAB_DOCUMENT);
							DocumentsPanel.get().expandDocid = hit.getDocument().getId();
							DocumentsPanel.get().getMenu().expandFolder(hit.getDocument().getFolder());
						}
					});
					
					DynamicForm contentsForm = new DynamicForm();
					contentsForm.setWidth100();
					
					SContent[] contents = hit.getDocument().getContents();
					FormItem[] formItem = new FormItem[contents.length];  
					for(int i = 0 ; i < contents.length ; i++){
						final SContent content = contents[i];
						
						LinkItem contentsLink = new LinkItem();
						contentsLink.setLinkTitle(content.getFileName());
						contentsLink.setShowTitle(false);
						contentsLink.setWidth(300);
						contentsLink.addClickHandler(new com.smartgwt.client.widgets.form.fields.events.ClickHandler() {
							@Override
							public void onClick(
									com.smartgwt.client.widgets.form.fields.events.ClickEvent event) {
								Util.downloadAsFrame(hit.getDocument().getId(), content.getElementId());
							}
						});
						
						formItem[i] = contentsLink; 
					}
					contentsForm.setFields(formItem);
					
					
					Label label = new Label();
					label.setContents(hit.getSummary());
					label.setWidth(1000);
					label.setHeight(60);
					
					searchResult.addMember(titleForm);
					searchResult.addMember(pathForm);
					searchResult.addMember(contentsForm);
					searchResult.addMember(label);
					searchResultPanel.addMember(searchResult);
					
					titleLink.setCellStyle("fullSearchResultTileItem");
					titleLink.updateState();
					
					pathLink.setCellStyle("fullSearchResultPathItem");
					pathLink.updateState();
					
				}
				
				DynamicForm previous =  (DynamicForm) searchPage.getMember(0);
				previous.setValue("pageNum", pageNum - 1);
				if(result.getOffset() == 1)
					previous.setVisible(false);
				else 
					previous.setVisible(true);
				
				
				DynamicForm next =  (DynamicForm) searchPage.getMember(1);
				next.setVisible(result.isHavingNextPage());
				next.setValue("pageNum", pageNum + 1);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				System.out.println(caught);
			}
		});
	}
}
