package com.speno.xedm.gui.frontend.client.panels;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.thirdparty.javascript.jscomp.CssRenamingMap.Style;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.TextArea;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.BackgroundRepeat;
import com.smartgwt.client.types.Cursor;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Img;
import com.smartgwt.client.widgets.ImgButton;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.PickerIcon;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.FormItemClickHandler;
import com.smartgwt.client.widgets.form.fields.events.FormItemIconClickEvent;
import com.smartgwt.client.widgets.form.fields.events.KeyUpEvent;
import com.smartgwt.client.widgets.form.fields.events.KeyUpHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.menu.IMenuButton;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.menu.MenuItem;
import com.smartgwt.client.widgets.menu.events.MenuItemClickEvent;
import com.speno.xedm.core.service.serials.SDelegation;
import com.speno.xedm.core.service.serials.SInfo;
import com.speno.xedm.core.service.serials.SMessage;
import com.speno.xedm.core.service.serials.SUser;
import com.speno.xedm.gui.common.client.Constants;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.Session;
import com.speno.xedm.gui.common.client.log.Log;
import com.speno.xedm.gui.common.client.util.ItemFactory;
import com.speno.xedm.gui.common.client.util.LengthValidator;
import com.speno.xedm.gui.common.client.util.ServiceUtil;
import com.speno.xedm.gui.common.client.util.Useful;
import com.speno.xedm.gui.common.client.util.Util;
import com.speno.xedm.gui.common.client.util.WindowUtils;
import com.speno.xedm.gui.frontend.client.FrontEndEntry;
import com.speno.xedm.gui.frontend.client.search.FullTextPanel;

public class TopPanel extends VLayout{
	
	private static int HEIGHT = 46;
	
	private HLayout hlayTop = new HLayout();
	private DynamicForm dfTop;
	private Label headerNotice;
    private Timer timer ;
    private String bodyMessage="";
    private String content = "";
    private TextArea test;
    private static TopPanel instance;
	public static TopPanel get() {
		if (instance == null) {
			instance = new TopPanel();
		}
		return instance;
	}
	
	public TopPanel() {
		setStyleName("topPanel");
		setWidth100();
		setHeight(HEIGHT);
		
		// 로고 이미지
		prepareLogo();		
        prepareTopDynamic();
		
	}
	
	private List<String> headerNoticeList = new ArrayList<String>();
	private List<String> headerSubjectList = new ArrayList<String>();
	
	private TextItem searchText;
	private void prepareTopDynamic(){
		SInfo info = Session.get().getInfo();
		dfTop = new DynamicForm();   
//		dfTop.setWidth(250);
        dfTop.setNumCols(2);
        dfTop.setAlign(Alignment.RIGHT);
        dfTop.setShowEdges(false);
        // =================================================
        // 검색바
        searchText = new TextItem("fulltextSearch", I18N.message("second.mandator"));
        searchText.setWidth(230);
        searchText.setShowTitle(false);
        searchText.setWrapTitle(false);
        searchText.setCanEdit(true);
        
        // kimsoeun GS인증용 - 상단 검색창 숨기기 
        String visible = Session.get().getInfo().getConfig("gui.fulltextsearch.visible");
        searchText.setVisible(Boolean.valueOf(visible));
        
        searchText.setDisableIconsOnReadOnly(false);
//        searchText.setLength(info.getIntConfig("gui.topsearch.fieldsize", 255));
        searchText.setValidators(new LengthValidator(searchText, info.getIntConfig("gui.topsearch.fieldsize", 255)));
        searchText.addKeyUpHandler(new KeyUpHandler() {
			public void onKeyUp(KeyUpEvent event) {
				// TODO Auto-generated method stub
				if(event.getKeyName().equals("Enter")){					
					doSearch();
				}
			}
		});
    

        // 검색
        PickerIcon searchPicker = new PickerIcon(PickerIcon.SEARCH, new FormItemClickHandler() {   
            public void onFormItemClick(FormItemIconClickEvent event) {
            	doSearch();
            }   
        });

        // 삭제 버튼
        PickerIcon clearPicker = new PickerIcon(PickerIcon.REFRESH, new FormItemClickHandler() {   
        	public void onFormItemClick(FormItemIconClickEvent event) { 
            	 dfTop.getField("fulltextSearch").clearValue();
            }   
        });
        searchText.setIcons(searchPicker, clearPicker);
        searchText.setShowHintInField(false);
        dfTop.setItems(searchText);
        
//        String fulltexturl = Session.get().getInfo().getConfig("gui.fulltextsearch.url");
//        if (fulltexturl == null || fulltexturl.length() < 1)
//        	dfTop.hide();
        // =================================================
        
        
        // 유저명 로그아웃
        SUser suser = Session.get().getUser();
        Menu menu = new Menu();   
        //menu.setShowShadow(true);   
        menu.setShadowDepth(0);   
        
        List<MenuItem> menuList = new ArrayList<MenuItem>();
        
        MenuItem outItem = new MenuItem(I18N.message("logout"));   
        outItem.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			@Override
			public void onClick(MenuItemClickEvent event) {
				Useful.ask(I18N.message("question"), I18N.message("confirmexit"), new BooleanCallback() {
					@Override
					public void execute(Boolean value) {
						if (value) {
							Session.get().requestLogout();
						}
					}
				});
			}
		});
        menuList.add(outItem);
        
        // 위임리스트 세팅
        if (Session.get().getDelegations() != null){
	        for (final SDelegation d : Session.get().getDelegations()) {
	            MenuItem item = new MenuItem(d.getMandatorName() + " " + I18N.message("delegated"));   
	            item.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
	    			@Override
	    			public void onClick(MenuItemClickEvent event) {
	    				Useful.ask(I18N.message("question"), I18N.message("confirmdelegation"), new BooleanCallback() {
	    					@Override
	    					public void execute(Boolean value) {
	    						if (value) {
	    							// 팝업으로 위임자 로그인 호출
	    							String url = GWT.getHostPageBaseURL() + "?" +
	    									FrontEndEntry.PARAM_SSOSID + "=" + Session.get().getSid() + "&" +
	    									Constants.LOCALE  + "=" + I18N.getLocale() + "&" +
	    									FrontEndEntry.PARAM_MANDATOR + "=" + d.getMandatorName();
	    							WindowUtils.openPopupUrl(url, "_blank", "width=1000, height=800, copyhistory=no");
	    						}
	    					}
	    				});
	    			}
	    		});
	            menuList.add(item);
			}
        }
        
        menu.setItems(menuList.toArray(new MenuItem[0]));
        IMenuButton menuButton = new IMenuButton(suser.getName() + " (" + suser.getUserName() + ")", menu);   
        // kimsoeun GS인증용 - 사용자 계정 정보 너비 늘리기 
        menuButton.setWidth(100);
        menuButton.setTooltip(suser.getName() + " (" + suser.getUserName() + ")");

    //20131126 na topPanel의 로그아웃 ? 버튼 제거
//        // link menu
//        Menu linkMenu = new Menu();   
//        linkMenu.setShowShadow(true);   
//        linkMenu.setShadowDepth(0);   
//        
//        MenuItem linkMenuItem = new MenuItem(I18N.message("Xvarm Admin"));   
//        outItem.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
//			@Override
//			public void onClick(MenuItemClickEvent event) {
//				// TODO:
//			}
//		});
//        
//        linkMenu.setItems(linkMenuItem);
//        IMenuButton linkMenuButton = new IMenuButton(I18N.message("Link"), linkMenu);   
//        linkMenuButton.setWidth(70);
        
        //20140513-01	육용수 알리안츠 쪽 요청으로 ? 이미지 숨김
        ImgButton helpButtonUser = ItemFactory.newImgButton("", "help", 16,16, false, false);
        helpButtonUser.addClickHandler(new com.smartgwt.client.widgets.events.ClickHandler() {
			@Override
			public void onClick(com.smartgwt.client.widgets.events.ClickEvent event) {
				String base = GWT.getHostPageBaseURL();
				WindowUtils.openUrl(base + "user_menual/UsersConsole.html");
			}
		});
        
        ImgButton helpButtonAdmin = ItemFactory.newImgButton("", "help2", 16,16, false, false);
        helpButtonAdmin.addClickHandler(new com.smartgwt.client.widgets.events.ClickHandler() {
			@Override
			public void onClick(com.smartgwt.client.widgets.events.ClickEvent event) {
				String base = GWT.getHostPageBaseURL();
				WindowUtils.openUrl(base + "admin_menual/AdministratorsConsole.html");
			}
		});
        
        // menu and help button
        HLayout rightLayout = new HLayout();
//        rightLayout.setShowEdges(true);
        rightLayout.setAutoHeight();
        rightLayout.setAutoWidth();
        rightLayout.setMembersMargin(5);
//        rightLayout.addMembers(menuButton);
        rightLayout.addMembers(menuButton, helpButtonUser, helpButtonAdmin);
//        rightLayout.addMembers(menuButton, linkMenuButton);
        
        VLayout vlay = new VLayout();
//        vlay.setShowEdges(true);
        vlay.setHeight100();
        vlay.setAutoWidth();
        vlay.setAlign(VerticalAlignment.CENTER);
		vlay.addMembers(rightLayout);

		VLayout searchHolder = new VLayout();
		searchHolder.setHeight100();
		searchHolder.setAutoWidth();
		searchHolder.setAlign(VerticalAlignment.CENTER);
		searchHolder.addMembers(dfTop);

        VLayout vlay2 = new VLayout();
        vlay2.setHeight100();
        vlay2.setWidth100();
        vlay2.setAlign(VerticalAlignment.CENTER);
        vlay2.setPadding(3);
        
        executeFetch();
       
        //수정 20140303 육용수
       headerNotice = ItemFactory.newLabel(HEIGHT, null, false, "");
        headerNotice.setWrap(true);
        headerNotice.setAlign(Alignment.LEFT);
        headerNotice.setOverflow(Overflow.HIDDEN);
        headerNotice.setStyleName("topNotice");
        
       //test = ItemFactory.newTextArea();
        
//        headerNotice.addMouseUpHandler(new MouseUpHandler() {
//			
//			@Override
//			public void onMouseUp(MouseUpEvent event) {
//				// TODO Auto-generated method stub
//				headerNotice.animateFade(500);
//				
//			}
//		});
//        headerNotice.addMouseDownHandler(new MouseDownHandler() {
//			
//			@Override
//			public void onMouseDown(MouseDownEvent event) {
//				// TODO Auto-generated method stub
//				headerNotice.animateFade(0);
//			}
//		});
        
        
        vlay2.addMember(headerNotice);

        
		hlayTop.setHeight(HEIGHT);
		hlayTop.setWidth100();
		hlayTop.setAlign(Alignment.RIGHT);
		hlayTop.addMember(vlay2);

		hlayTop.addMember(searchHolder);
		
		hlayTop.addMember(vlay);

		addMember(hlayTop);
	}

	/**
	 * Full Text 검색 Action
	 * 검색 Tab의 Full Text 검색 창으로 이동한 후 검색 실행
	 * 20130912, teaesu 추가
	 */
	//20140516-2 육용수 전문검색시 한번의 엔터가 무시되는 현상제거
	//사실 무시되는것이 아니고 패널이 이동되지만 않기 때문에 패널을 한번더 강제로 이동시킴 - 모듈수정을 방지함
	private void doSearch(){
		// 메인 Tab에서 Search Tab 선택
		MainPanel.get().selectFullTextSearchTab();
		MainPanel.get().selectFullTextSearchTab();
		FullTextPanel.get().search(searchText.getValueAsString());
//		MainPanel.get().tabSet.selectTab(Constants.MENU_SEARCHS);
		// 검색값을 넘기고 검색을 실행한다.
//		SearchMenu.get().setFullSearchTextAndSearch(searchText.getValueAsString());
	}
	
	private void prepareLogo(){
		SInfo info = Session.get().getInfo();
		
		final Img logoImage = ItemFactory.newBrandImg(Util.getNameOfServerImage(info.getMainLogo()));
		logoImage.setStyleName("logo_head");
		//logoImage.setCursor(Cursor.POINTER);
		logoImage.setShowHover(true);		
		
		logoImage.setWidth(Util.getWidthOfServerImage(info.getMainLogo()));
		logoImage.setHeight(Util.getHeightOfServerImage(info.getMainLogo()));			
//		logoImage.setHeight(HEIGHT);
		hlayTop.addMember(logoImage);				
		logoImage.addClickHandler(new ClickHandler() {			
			//20140516-1	육용수 알리안츠 요구사항 CI클릭시 홈패널이동
			public void onClick(ClickEvent event) {
				// TODO Auto-generated method stub				
				String startPage = Session.get().getInfo().getConfig("settings.welcome.page");
				//2014050526-1	육용수 버그수정
				String url = GWT.getHostPageBaseURL();
				int sharp = url.indexOf('#');
				if (startPage.length() < 1)
					url = url + "#dashboard;dash_home";
				else{					
					url = url + startPage;
				 sharp = url.indexOf('#');
				}
			String tokens = url.substring(sharp+1);			
			MainPanel.get().selectMenu(tokens);		    
			}
		});		

		// 로고 백그라운드를 통 이미지로 설정.
		if (info.getMainBack() != null && info.getMainBack().length() > 0) {
			// TODO: 스트레치가 되지 않아 보류.
		} else {
			setBackgroundRepeat(BackgroundRepeat.REPEAT_X);
			setBackgroundImage(Util.brandUrl(Util.getNameOfServerImage(info.getMainBackRepeat())));
		}			
	}

	public void hideSearch(){
		dfTop.hide();
	}
	
	public TextItem getSearchText() {
		return searchText;
	}
	
	public boolean addHeaderNoticeListAdd(String message){
		boolean state = false;
		state = headerNoticeList.add(message);
		return state;
	}
	
	// 상단 경고 문구 표시
	public void executeFetch() {
//		Log.debug("[ TopPanel executeFetch]");
		ServiceUtil.info().getCheckedNotice(new AsyncCallback<List<SMessage>>(){

			@Override
			public void onSuccess(List<SMessage> result) {
				// TODO Auto-generated method stub
//				Log.debug("Call onSuccess ");
				for(SMessage sMessage : result){
					if(sMessage == null) continue;
//					String temp = sMessage.getSubject()+"<br>"+sMessage.getMessage();
					//20140428 알리안츠 요청으로 제목을 추가하지 않음 - by yys
					//headerSubjectList.add(sMessage.getSubject());
					if(addHeaderNoticeListAdd(sMessage.getMessage())){
//						Log.debug(sMessage.getSubject()+"::"+sMessage.getMessage());
					}else{
						Log.debug("Failed");	
					}					
				}
		        
		        if(headerNoticeList.isEmpty() ){
		        	setBodyMessage("");
//		        	content= "<div style=font-size:10pt;color:white;>"  +bodyMessage+"</div>";
		        	content= bodyMessage;
		        	headerNotice.setContents(content);
		       }else if(headerNoticeList.size()==1){
		    	   setBodyMessage(headerNoticeList.get(0));
//		    	   content= "<div style=font-size:10pt;color:white;>"  +bodyMessage+"</div>";
		        	content= bodyMessage;
		    	   headerNotice.setContents(content);
		    	   adjustNotice();
		       }else{
		    	   // headerNoticeList에 값이 2개 이상 있으면 아래 작업을 진행 함.
		    	   adjustNotice();
		    	   changeBodyMessage(6000);
		       }		       	        
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				Log.serverError(caught, true);
			}

	});
	}
	
	public void setBodyMessage(String message){
		// HTML tag 제거
		message = message.replaceAll("<br>", " ");
		bodyMessage = message.replaceAll("<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>", "");
		//문자열 길이에 따라 ... 처리
//		if(bodyMessage.length()>200){
		//알리안츠 요청으로 110자이내로 잘라냄 영문의 경우 200자 제한 by yys 
		if(bodyMessage.length() > 110)
		bodyMessage= Util.strCut(bodyMessage, 320, "...");
//		}
		
		
	}
	
	private void adjustNotice() {
		if (currentIndex < headerNoticeList.size()){
			//System.out.println(headerNoticeList.get(i));
			setBodyMessage(headerNoticeList.get(currentIndex));
//			content= "<div style=font-size:10pt;color:white;>"  + headerSubjectList.get(i) + "<br>" + bodyMessage+"</div>";			
			
			{//제목에 있던 br태그를 제거함 by yys
			//content= headerSubjectList.get(currentIndex) + "<br>" + bodyMessage;						
			content= bodyMessage;
			}
//			Log.debug("Notice timer was called : " + content);
			headerNotice.setContents(content);
//			headerNotice.setAutoFit(true);
//			Log.debug("bodyMessage::"+bodyMessage);
			currentIndex++;
		}else{
			currentIndex = 0;
		}
	}
	
	private int currentIndex = 0;
	public void changeBodyMessage(int interval){
		// 20140320, junsoo, timer 초기화
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
		
		timer = new Timer() {
			@Override
			public void run() {
//				headerNotice.animateFade(100);
				adjustNotice();
			}		
		};
			
		timer.scheduleRepeating(interval);
	}
}