package com.speno.xedm.gui.frontend.client.panels;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.ImageStyle;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.util.Offline;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.HTMLFlow;
import com.smartgwt.client.widgets.Img;
import com.smartgwt.client.widgets.ImgButton;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.CheckboxItem;
import com.smartgwt.client.widgets.form.fields.PasswordItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.KeyPressEvent;
import com.smartgwt.client.widgets.form.fields.events.KeyPressHandler;
import com.smartgwt.client.widgets.form.validator.LengthRangeValidator;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.layout.VStack;
import com.speno.xedm.core.service.serials.SInfo;
import com.speno.xedm.core.service.serials.SMessage;
import com.speno.xedm.core.service.serials.SSession;
import com.speno.xedm.gui.common.client.Constants;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.Session;
import com.speno.xedm.gui.common.client.util.ItemFactory;
import com.speno.xedm.gui.common.client.util.LengthValidator;
import com.speno.xedm.gui.common.client.util.ServiceUtil;
import com.speno.xedm.gui.common.client.util.StringEncrypter;
import com.speno.xedm.gui.common.client.util.Util;
import com.speno.xedm.gui.common.client.widgets.MessageLabel;
import com.speno.xedm.gui.common.client.window.Waiting;
import com.speno.xedm.gui.frontend.client.FrontEndEntry;

/**
 * 
 * @author deluxjun
 *
 */
public class LoginPanel extends VLayout {

	protected TextItem username = new TextItem();

	protected PasswordItem password = new PasswordItem();

	protected CheckboxItem savelogin = new CheckboxItem();

	protected SelectItem language;

	public LoginPanel(SInfo info) {
		setDefaultLayoutAlign(Alignment.CENTER);
		setWidth100();
		setHeight100();

		// 기본 패널
		VLayout vPanel = new VLayout();
				vPanel.setID("vPanel");
				vPanel.setWidth("100%");
				vPanel.setHeight("100%");
				vPanel.setMembersMargin(10);
				vPanel.setDefaultLayoutAlign(VerticalAlignment.CENTER);
				vPanel.setAlign(Alignment.CENTER);
//				vPanel.setShowEdges(true);
				
				// 상단
				HLayout topPanel = new HLayout();
				topPanel.setID("topPanel");
				topPanel.setWidth("100%");
				topPanel.setHeight(70);
				topPanel.setMembersMargin(10);
				topPanel.setAlign(Alignment.LEFT);
//				topPanel.setShowEdges(true);
			
				// 본문
				HLayout bodyPanel = new HLayout();
				bodyPanel.setID("bodyPanel");
				bodyPanel.setWidth("100%");
				bodyPanel.setHeight(100);
				bodyPanel.setAlign(Alignment.CENTER);
				bodyPanel.setDefaultLayoutAlign(VerticalAlignment.CENTER);
				bodyPanel.setLeft("200px");
//				bodyPanel.setShowEdges(true);
				
				
//				// 상단 내용 구성
				Img imgTopLeft =  ItemFactory.newBrandImg(Util.getNameOfServerImage(info.getLoginTopLeftImg()));
				imgTopLeft.setID("imgTopLeft");
				imgTopLeft.setWidth("200");
				imgTopLeft.setHeight100();
				imgTopLeft.setBackgroundColor(Util.getNameOfServerImage(info.getLoginTopBackColor()));
				imgTopLeft.setImageType(ImageStyle.TILE);
				
//				Img imgTopLogo =  ItemFactory.newBrandImg("logo_login.png");
//				imgTopLogo.setWidth(145);
//				imgTopLogo.setHeight(40);
//				imgTopLogo.setImageType(ImageStyle.NORMAL);
				
				// Prepare the logo image to be shown inside the login form
				Img logoImage = ItemFactory.newBrandImg(Util.getNameOfServerImage(info.getLoginLogo()));
				logoImage.setID("logoImage");
				logoImage.setWidth(Util.getWidthOfServerImage(info.getLoginLogo()));
				logoImage.setHeight(Util.getHeightOfServerImage(info.getLoginLogo()));
				
				Img imgTopRight =  ItemFactory.newBrandImg(Util.getNameOfServerImage(info.getLoginTopRightImg()));
				imgTopRight.setID("imgTopRight");
				imgTopRight.setWidth100();
				imgTopRight.setHeight100();
				imgTopRight.setBackgroundColor(Util.getNameOfServerImage(info.getLoginTopBackColor()));
				imgTopRight.setImageType(ImageStyle.TILE);
				
				
				topPanel.addMember(imgTopLeft);
				topPanel.addMember(logoImage);
				topPanel.addMember(imgTopRight);
				
//				//바디 구성
//				VLayout bodyOuterPanel = new VLayout();
//				bodyOuterPanel.setWidth(500);	
//				bodyOuterPanel.setShowEdges(true);
//				bodyOuterPanel.setBackgroundColor("pink");
//				bodyOuterPanel.setAlign(Alignment.CENTER);
				
				HLayout bodyGroupPanel = new HLayout();
				bodyGroupPanel.setID("bodyGroupPanel");
				bodyGroupPanel.setWidth("60%");
				bodyGroupPanel.setHeight100();
				bodyGroupPanel.setAlign(Alignment.CENTER);
				bodyGroupPanel.setMembersMargin(10);
					
					//왼쪽 로고
				Img imgLeftLogo = ItemFactory.newBrandImg(Util.getNameOfServerImage(info.getLoginLeftImg()));
				imgLeftLogo.setWidth(Util.getWidthOfServerImage(info.getLoginLeftImg()));
				imgLeftLogo.setHeight(Util.getHeightOfServerImage(info.getLoginLeftImg()));
				
					//오른쪽 로그인+알림 그룹 레이아웃
				VLayout bodyRightPanel = new VLayout();
				bodyRightPanel.setWidth(400);
				bodyRightPanel.setHeight(300);
				bodyRightPanel.setAlign(Alignment.LEFT);
//				bodyRightPanel.setShowEdges(true);
				
				bodyGroupPanel.addMember(imgLeftLogo);
				bodyGroupPanel.addMember(bodyRightPanel);
				
				
				//하단 보안 문구	
				VLayout bodyFooter = new VLayout();
				bodyFooter.setID("bodyFooter");
//				bodyFooter.setWidth100();
				bodyFooter.setWidth(bodyGroupPanel.getWidthAsString());
				bodyFooter.setHeight("50px");
				bodyFooter.setAlign(VerticalAlignment.CENTER);
				bodyFooter.setAlign(Alignment.CENTER);
				bodyFooter.setMargin(3);
				
//				bodyFooter.setShowEdges(true);
		
//				HTMLFlow htmWarning_ko = new HTMLFlow("<font style=font-size:9pt;>"+info.getConfig("settings.product.warning_ko")+"</font>");
//				htmWarning_ko.setID("htmWarning_ko");
//				htmWarning_ko.setHeight("12px");
				Label lblWarning_ko = ItemFactory.newLabel(10, null, false, null);
				lblWarning_ko.setContents("<font style=font-size:8pt;>"+info.getConfig("settings.product.warning_ko")+"</font>");
				lblWarning_ko.setWrap(true);

				Label lblWarning_en = ItemFactory.newLabel(10, null, false, null);
				lblWarning_en.setContents("<font style=font-size:8pt;>"+info.getConfig("settings.product.warning_en")+"</font>");
				lblWarning_en.setWrap(true);

//				HTMLFlow htmWarning_en = new HTMLFlow("<font style=font-size:9pt;>"+info.getConfig("settings.product.warning_en")+"</font>");
//				htmWarning_ko.setID("htmWarning_en");
//				htmWarning_ko.setHeight("12px");
				
//				bodyFooter.addMember(htmWarning_ko);
//				bodyFooter.addMember(htmWarning_en);
				bodyFooter.addMember(lblWarning_ko);
				bodyFooter.addMember(lblWarning_en);
				
				bodyPanel.addMember(bodyGroupPanel);
				
				vPanel.addMember(topPanel);
				vPanel.addMember(bodyPanel);
				vPanel.addMember(bodyFooter);
		
		// Panel for horizontal centering
		HLayout hPanel = new HLayout();
		hPanel.setDefaultLayoutAlign(Alignment.LEFT);
		hPanel.setDefaultLayoutAlign(VerticalAlignment.CENTER);
		hPanel.setWidth(400);
		hPanel.setHeight100();
		
		// Panel for vertical centering
		VLayout content = new VLayout();
//		Img boxImage = ItemFactory.newBrandImg(Util.getNameOfServerImage(info.getLoginBox()));
//		content.setBackgroundImage(boxImage.getSrc());
//		content.setDefaultLayoutAlign(VerticalAlignment.CENTER);
		content.setAlign(Alignment.LEFT);
		content.setWidth100();
		content.setHeight(250);
//		content.setShowEdges(true);
//		content.setWidth(Util.getWidthOfServerImage(info.getLoginBox())); // 367
//		content.setHeight(Util.getHeightOfServerImage(info.getLoginBox()));	// 250

//		hPanel.addMember(content);
//		addMember(hPanel);
		bodyRightPanel.addMember(content);
		addMember(vPanel);
		
		
		HTMLFlow header = new HTMLFlow("<font style=font-size:15pt;>"+ info.getProductName() + " " + info.getRelease()+"</font>");
		header.setStyleName("loginHeader");
		header.setHeight("20px");
		
		

		// Prepare the form footer that contains copyright and website link
		String htmlString = "\u00A9 " + info.getYear() + " " + info.getVendor();
		if (info.getUrl() != null && !"-".equals(info.getUrl()))
			htmlString += "  &#160; &#8226; &#160; " + info.getUrl();
//		htmlString += "  &#160; &#8226; &#160; <a href='" + info.getUrl() + "'>" + info.getUrl() + "</a>";
		HTMLFlow footer = new HTMLFlow(htmlString);
		footer.setStyleName("loginFooter");
		

		VStack messages = new VStack();
//		높이 조정하고 오버플로우시 스크롤 생성함
		messages.setHeight(100);
		messages.setOverflow(Overflow.AUTO);
		if (info.getMessages().length > 0) {
			for (SMessage message : info.getMessages()) {
				MessageLabel label = new MessageLabel(message);
				label.setWrap(true);
				label.setPadding(3);
				label.setStyleName("richTextEditor");
				messages.addMember(label);
			}
		}

		// ====================
		// notices
		VLayout noticesLayout = new VLayout();
		noticesLayout.setID("noticesLayout");
		noticesLayout.setDefaultLayoutAlign(VerticalAlignment.CENTER);
//		noticesLayout.setWidth(600);
		noticesLayout.setWidth100();
//		noticesLayout.setHeight(250)
		noticesLayout.setHeight(100);
//		hPanel.addMember(noticesLayout);

//		HTMLFlow headerNotice = new HTMLFlow(I18N.message("notice"));
//		headerNotice.setStyleName("loginHeader");
//		headerNotice.setHeight("12px");

		
		
		Layout innerNotice = new VLayout();
		innerNotice.setID("innerNotice");
		innerNotice.setStyleName("loginContent");
		innerNotice.setShowEdges(true);
		innerNotice.setEdgeSize(2);
		innerNotice.setPadding(5);
		innerNotice.addMember(messages);
		

		Layout outerNotice = new VStack();
		outerNotice.setID("outerNotice");

//		outerNotice.addMember(headerNotice);
		outerNotice.addMember(innerNotice);
		outerNotice.setPadding(2);
		
		noticesLayout.addMember(outerNotice);


		// ====================
		
		
		// Prepare the Form and all its fields
		final DynamicForm form = new DynamicForm();
		form.setAlign(Alignment.CENTER);
		
		
//		LengthValidator lengthValidator = new LengthValidator(username, info.getIntConfig("gui.title.fieldsize", 255));
	    
		username.setTitle(I18N.message("username"));
		username.setRequired(true);
		username.setWrapTitle(false);
//		username.setLength(info.getIntConfig("gui.title.fieldsize", 255));
		username.setValidators(new LengthValidator(username, info.getIntConfig("gui.title.fieldsize", 255)));
		
		username.addKeyPressHandler(new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent event) {
				if (event.getKeyName() != null && "enter".equals(event.getKeyName().toLowerCase()))
					onLogin();
			}
		});

		password = ItemFactory.newPasswordItem("password", "password", null);
		password.setRequired(true);
		password.setWrapTitle(false);
//		password.setLength(info.getIntConfig("gui.title.fieldsize", 255));
		password.setValidators(new LengthValidator(password, info.getIntConfig("gui.title.fieldsize", 255)));
		password.addKeyPressHandler(new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent event) {
				if (event.getKeyName() != null && "enter".equals(event.getKeyName().toLowerCase()))
					onLogin();
			}
		});

		savelogin.setTitle(I18N.message("savelogin"));
		savelogin.setRequired(false);
		savelogin.setWrapTitle(false);
		savelogin.setValue("true".equals(Offline.get(Constants.COOKIE_SAVELOGIN)));

		if (I18N.getSupportedGuiLanguages(false).size() > 1) {
			language = ItemFactory.newLanguageSelector("language", false, true);
			// 최초 로그인 및 로그인 정보가 저장이 안되어 있을 경우 설정되어있는 default language 값을 읽어온다.
			if(Offline.get(Constants.COOKIE_USER) != null && Offline.get(Constants.COOKIE_USER).toString() != null && !Offline.get(Constants.COOKIE_USER).toString().equals("")){
				language.setValue(I18N.getSupportedGuiLanguages(false).keySet().iterator().next());
			}
			else if(Offline.get(Constants.COOKIE_LANGUAGE ) != null ){
				language.setValue(Offline.get(Constants.COOKIE_LANGUAGE ));
			}
			else{
				language.setValue(info.getConfig("lang.default"));
			}
//			language.setDefaultValue(I18N.getSupportedGuiLanguages(false).keySet().iterator().next());
//			language.setDefaultValue("");
		} else {
			language = ItemFactory.newLanguageSelector("language", false, true);
			// TODO: lang
//			language.setDefaultValue(I18N.getSupportedGuiLanguages(false).keySet().iterator().next());
			language.setDefaultValue("");
		}

		// If the case, initialize the credentials from client's cookies
		if ("true".equals(info.getConfig("gui.savelogin")) && savelogin.getValueAsBoolean()) {
			username.setValue(Offline.get(Constants.COOKIE_USER));
			password.setValue(Offline.get(Constants.COOKIE_PASSWORD));
			GWT.log("Language" + Offline.get(Constants.COOKIE_LANGUAGE));
			language.setValue(Offline.get(Constants.COOKIE_LANGUAGE));
		}

		
		if ("true".equals(info.getConfig("gui.savelogin")))
			form.setFields(username, password, language, savelogin);
		else
			form.setFields(username, password, language);

        ImgButton loginButton = ItemFactory.newImgButton("", "login_bt", 76,25, false, false);
        loginButton.addClickHandler(new com.smartgwt.client.widgets.events.ClickHandler() {
			@Override
			public void onClick(com.smartgwt.client.widgets.events.ClickEvent event) {
				onLogin();
			}
		});

//		IButton loginButton = new IButton(I18N.message("login"));
//		loginButton.setBackgroundImage(ItemFactory.newBrandImg("login_bt").getSrc());
//		loginButton.addClickHandler(new ClickHandler() {
//			public void onClick(ClickEvent event) {
//				onLogin();
//			}
//		});

        Layout innerGroup = new HLayout();
        innerGroup.setID("innerGroup");
        innerGroup.setEdgeSize(2);
        innerGroup.setShowEdges(true);
        innerGroup.setAlign(Alignment.CENTER);
        innerGroup.setAlign(VerticalAlignment.CENTER);
        
        Layout inner = new VLayout();
		inner.setStyleName("loginContent");
//		inner.setShowEdges(true);
//		inner.setEdgeSize(2);
//		inner.addMember(logoImage);
		
		
		Layout separator = new VLayout();
		separator.setID("separator");
		separator.setHeight(10);
		inner.addMember(separator);
		inner.addMember(form);
		inner.addMember(separator);
		
		Layout inner2 = new HLayout(10);
		inner2.setID("inner2");
		inner2.setHeight(100);
//		bottom.setAlign(Alignment.RIGHT);
		inner2.setAlign(VerticalAlignment.CENTER);
		
//		loginButton.setPadding(10);
//		bottom.setBackgroundColor("green");
		Layout loginLayout = new HLayout();
		loginLayout.setID("loginLayout");
//		loginLayout.setHeight(100);
		loginLayout.addMember(loginButton);
		loginLayout.setAlign(Alignment.CENTER);
		loginLayout.setAlign(VerticalAlignment.CENTER);
		loginLayout.setHeight100();
		loginLayout.setPadding(40);
		
		// TODO: forgot password
//		retrievePwd(info, bottom);
//		inner.addMember(bottom);
		inner.setPadding(5);

		inner2.addMember(inner);
		inner2.addMember(loginLayout);
//		innerGroup.addMember(inner);
		innerGroup.addMember(inner2);
		
		Layout outer = new VStack();
		outer.addMember(header);
		outer.addMember(innerGroup);
//		outer.addMember(inner);
//		outer.addMember(footer);
		outer.setPadding(2);

		content.addMember(outer);
		content.addMember(noticesLayout);
		content.addMember(footer);
		form.focusInItem(username);
		form.setAutoFocus(true);
		form.focus();
		
		username.setSelectOnFocus(true);
	}

	protected void retrievePwd(SInfo info, Layout bottom) {
		SMessage forgotPwd = new SMessage();
		forgotPwd.setMessage(I18N.message("forgotpassword"));
		MessageLabel forgotMessage = new MessageLabel(forgotPwd);
		forgotMessage.setAlign(Alignment.RIGHT);
		forgotMessage.setIcon(null);
		forgotMessage.setStyleName("forgotpwd");
		final String productName = info.getProductName();
		forgotMessage.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				onForgottenPwd(productName);
			}
		});
		bottom.addMember(forgotMessage);
	}

	protected void onLogin() {
		if(username.getValueAsString() != null && password.getValueAsString() != null){
			if(Util.getSetting("setting.waiting")) Waiting.show(I18N.message("wattingforserver"));
			String strUserId = username.getValueAsString();
			
			// check sso
			String sso = FrontEndEntry.getParameter(FrontEndEntry.PARAM_SSO);
			if (Session.get().isDefaultSSO() || (sso != null && sso.equalsIgnoreCase("true")))
				strUserId = SSession.SSOSTR + strUserId;
			
			
			ServiceUtil.security().login("ENC:" + StringEncrypter.encryptDESede(strUserId), "ENC:" + StringEncrypter.encryptDESede((String) password.getValue()), (String) language.getValue(),
//			ServiceUtil.security().login(strUserId, (String) password.getValue(), (String) language.getValue(),
					new AsyncCallback<SSession>() {
				public void onFailure(Throwable caught) {
					SC.warn(caught.getMessage());
					caught.printStackTrace();
					Waiting.hide();
//						Log.serverError(caught.getMessage(), caught, true, false);
				}
				
				@Override
				public void onSuccess(SSession session) {
					if (session.isLoggedIn()) {
						onLoggedIn(session);
						Waiting.hide();
//						} else if (session.getUser() != null && session.getUser().isExpired()) {
//							new ChangePassword(session.getUser(), "needtochangepassword").show();
					} else {
						Waiting.hide();
						String msg = session.getIncomingMessage();
						if (msg != null && msg.length() > 0)
							SC.warn(I18N.message("second.loginFailWithMessage", msg));
						else
							SC.warn(I18N.message("second.usernotfound"));
					}
				}
			});
		}
	}

	private void onForgottenPwd(String productName) {
//		PasswordReset pwdReset = new PasswordReset(productName);
//		pwdReset.show();
	}

	public void onLoggedIn(SSession session) {
		try {
			// session init을 통해 MainPanel의 onUserLoggedIn 호출됨.
			Session.get().init(session);
			FrontEndEntry.get().showMain();
		} catch (Throwable e) {
			e.printStackTrace();
			SC.warn(e.getMessage());
		}
		
		

		// If the case, save the credentials into client cookies
		if ("true".equals(Session.get().getInfo().getConfig("gui.savelogin"))) {
			Offline.put(Constants.COOKIE_SAVELOGIN, (String) savelogin.getValueAsBoolean().toString());
			Offline.put(Constants.COOKIE_USER, savelogin.getValueAsBoolean() ? (String) username.getValue() : "");
			Offline.put(Constants.COOKIE_PASSWORD, savelogin.getValueAsBoolean() ? (String) password.getValue() : "");
			Offline.put(Constants.COOKIE_LANGUAGE, language.getValue() != null ? (String) language.getValue() : "");
			I18N.setLocale(session.getUser().getLanguage());
		} else {
			Offline.put(Constants.COOKIE_SAVELOGIN, "false");
			Offline.put(Constants.COOKIE_USER, "");
			Offline.put(Constants.COOKIE_PASSWORD, "");
			Offline.put(Constants.COOKIE_LANGUAGE, "");
		}

		// In any case save the SID in the browser
		Offline.put(Constants.COOKIE_SID, session.getSid());

		// TODO: user
//		User user = session.getUser();
//		if (user.getQuotaCount() >= user.getQuota() && user.getQuota() >= 0)
//			Log.warn(I18N.message("quotadocsexceeded"), null);
	}
}