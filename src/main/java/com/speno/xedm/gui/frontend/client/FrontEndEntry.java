package com.speno.xedm.gui.frontend.client;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.ajaxloader.client.AjaxLoader;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.GWT.UncaughtExceptionHandler;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.visualizations.corechart.CoreChart;
import com.smartgwt.client.util.Offline;
import com.speno.xedm.core.service.serials.SDocument;
import com.speno.xedm.core.service.serials.SInfo;
import com.speno.xedm.core.service.serials.SSession;
import com.speno.xedm.gui.common.client.Constants;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.Session;
import com.speno.xedm.gui.common.client.util.ConsoleLogHandlerWithIEFix;
import com.speno.xedm.gui.common.client.util.RequestInfo;
import com.speno.xedm.gui.common.client.util.SCM;
import com.speno.xedm.gui.common.client.util.ServiceUtil;
import com.speno.xedm.gui.common.client.util.Util;
import com.speno.xedm.gui.common.client.util.WindowUtils;
import com.speno.xedm.gui.frontend.client.document.popup.DocumentPropertiesDialog;
import com.speno.xedm.gui.frontend.client.document.popup.DocumentUploadDialog;
import com.speno.xedm.gui.frontend.client.document.prop.DocumentDetailsPanel;
import com.speno.xedm.gui.frontend.client.panels.LoginPanel;
import com.speno.xedm.gui.frontend.client.panels.MainPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class FrontEndEntry implements EntryPoint {

	public final static String PARAM_SSO = "sso";
	public final static String PARAM_SSOSID = "ssosid";
	public final static String PARAM_MANDATOR = "mandator";

	// True when the user alreafy entered the main screen
	static boolean entered = false;

	private static FrontEndEntry instance;

	private LoginPanel loginPanel;
	private MainPanel mainPanel;

	private Logger logger = Logger.getLogger("");
	
	// 20130726, junsoo, url 분석 후 점프하기 위해 일시 저장.
	private String initUrl = "";
	
	/**
	 * @return singleton Main instance
	 */
	public static FrontEndEntry get() {
		return instance;
	}
	
	public String getURL(){
		return initUrl;
	}

	@Override
	public void onModuleLoad() {
		if (RootPanel.get("loadingWrapper") == null)
			return;

		// 20131111, GWT2.6 나오기 전에 임시로.. IE9에서 동작하지 않으므로.
		java.util.logging.Handler[] handlers = logger.getHandlers();
		if (handlers != null) {
		    for (java.util.logging.Handler h : handlers) {
		        if (h instanceof ConsoleLogHandler) {
		        	logger.removeHandler(h);
		        	logger.addHandler(new ConsoleLogHandlerWithIEFix());
		        	logger.info("replaced GWT ConsoleLogHandler with custom implementation: " + ConsoleLogHandlerWithIEFix.class.getName());
			}
		    }
		}
		
		GWT.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
			@Override
			public void onUncaughtException(Throwable caught) {
				System.err.print(caught.getStackTrace());
				logger.log(Level.WARNING, "Exception caught!", caught);
			}

		});

		instance = this;
		
		// Setup the language for localization
		RequestInfo loc = WindowUtils.getRequestInfo();

		// Tries to capture locale parameter
		String lang;
		if (loc.getParameter(Constants.LOCALE) != null && !loc.getParameter(Constants.LOCALE).equals("")) {
			lang = loc.getParameter(Constants.LOCALE);
		}
		else if(Offline.get(Constants.COOKIE_LANGUAGE ) != null ){
			lang = Offline.get(Constants.COOKIE_LANGUAGE).toString();
		}
		else {
			// First we initialize language values
			lang = Util.getBrowserLanguage();
		}
		I18N.setLocale(lang);

		
		Window.enableScrolling(false);
		Window.setMargin("0px");
		
		initUrl = Window.Location.getHref();

		mainPanel = MainPanel.get();

//		FrontEndEntry.this.showInitialLogin();

		getInfoAndLogin();
		

	}
	
	// Switch to the login panel
	public void showLogin() {
		mainPanel.hide();
		loginPanel.show();
		entered = false;
	}

	// 초기 설정을 서버로 부터 획득하고 로그인창 표시
	private void getInfoAndLogin(){
		
		ServiceUtil.info().getInfo(I18N.getLocale(), new AsyncCallback<SInfo>() {
			@Override
			public void onFailure(Throwable error) {
				SCM.warn(error);
			}

			@Override
			public void onSuccess(final SInfo info) {
				// Store the release information
				Cookies.setCookie(Constants.COOKIE_VERSION, info.getRelease());

//				Config.init(info);
				I18N.init(info);

				WindowUtils.setTitle(info, null);

				Session.get().setInfo(info);
				
				// load visualization
				if (info.getConfig("gui.visualization.hostname").length() > 0)
					AjaxLoader.init(null, info.getConfig("gui.visualization.hostname"));
				
				if("true".equals(info.getConfig("settings.connect.exNetwork"))){
					VisualizationUtils.loadVisualizationApi(new Runnable() {
					      public void run() {
					    	  GWT.log("[ FrontEndEntry loadVisualizationApi ] CoreChart.PACKAGE loaded.", null);  
					      }}, CoreChart.PACKAGE);
				}
				

				loginPanel = new LoginPanel(info);
				
				String savedSid = null;
				
				// 20130902, junsoo, SSO 처리
				savedSid = getParameter(PARAM_SSOSID);
				if (savedSid == null) {
					try {
						savedSid = Offline.get(Constants.COOKIE_SID).toString();
					} catch (Throwable t) {}
				}

				if (savedSid == null || "".equals(savedSid)) {
					FrontEndEntry.this.showInitialLogin();
					return;
				}

				// delegator 로그인
				String mandator = getParameter(PARAM_MANDATOR);
				if (mandator != null && mandator.length() > 0) {
					loginAsDelegator(savedSid, mandator);
					return;
				}
				
				// 일반적인 로그인
				ServiceUtil.security().login(savedSid, new AsyncCallback<SSession>() {

					@Override
					public void onFailure(Throwable caught) {
						FrontEndEntry.this.showInitialLogin();
					}

					@Override
					public void onSuccess(SSession session) {
						if (session == null || !session.isLoggedIn()) {
							FrontEndEntry.this.showInitialLogin();
						} else {
							MainPanel.get();
							loginPanel.onLoggedIn(session);
							
							// Remove the loading frame
							RootPanel.getBodyElement().removeChild(RootPanel.get("loadingWrapper").getElement());
//								setUploadTrigger(FrontEndEntry.this);
						}
					}
				});
			}
		});
		

	}

	// Setup the initial visualization of the login panel
	private void showInitialLogin() {
		RootPanel.get().add(loginPanel);

		// Remove the loading frame
		try {
			if(RootPanel.get("loadingWrapper") != null && RootPanel.get("loadingWrapper").getElement() != null)
				RootPanel.getBodyElement().removeChild(RootPanel.get("loadingWrapper").getElement());
		} catch (Exception e) {
			e.printStackTrace();
		}
//		setUploadTrigger(FrontEndEntry.this);

		showLogin();
	}

	public void showMain() {
		if (entered)
			return;

		// 20130904, junsoo, 외부인터페이스. command가 있으면 command만 실행하고 리턴.
		if (command())
			return;

		mainPanel.show();
		
		loginPanel.hide();
		entered = true;
	}

	// 20130904, junsoo, 외부 인터페이스
	private boolean command(){
		// 20130904, junsoo, command 동작 추가
		if (initUrl.indexOf("command=addDocuments") >= 0) {
			DocumentUploadDialog dialog = new DocumentUploadDialog(null);
			dialog.show();
			return true;
		}
		else if (initUrl.indexOf("command=documentDetails") >= 0) {
			int pos = initUrl.lastIndexOf("&");
			int pos2 = initUrl.lastIndexOf("?");
			String docIdParam;
			if(pos < pos2) docIdParam = initUrl.substring(pos + 1, pos2);
			else docIdParam = initUrl.substring(pos + 1);
			long docId = Integer.parseInt(docIdParam);
			
//			DocumentPropertiesDialog dialog = new DocumentPropertiesDialog(null);
//			dialog.show();
			ServiceUtil.document().getByIdWithPermission(Session.get().getSid(), docId, new AsyncCallback<SDocument>() {
				@Override
				public void onFailure(Throwable caught) {
					SCM.warn(caught);
				}
				
				@Override
				public void onSuccess(final SDocument result) {
					// 검색은 속성 수정 못하게 막아놨음
					final DocumentPropertiesDialog dialog = new DocumentPropertiesDialog(result, true, Constants.MAIN_TAB_SEARCH);
					dialog.show();
				}
			});
			return true;
		}
		
		return false;
	}
	
	public static String getParameter(String param) {
		return Window.Location.getParameter(param);
	}

	// 위임자 로그인
	private void loginAsDelegator(String sid, String mandator) {
		// 위임자로서 로그인시도하고 성공하면 화면 변경
		ServiceUtil.security().loginAsDelegator(sid, mandator, I18N.getLocale(),
				new AsyncCallback<SSession>(){
					@Override
					public void onSuccess(SSession session) {
						loginPanel.onLoggedIn(session);
						
						// Remove the loading frame
						RootPanel.getBodyElement().removeChild(RootPanel.get("loadingWrapper").getElement());
					}
					@Override
					public void onFailure(Throwable caught) {
						SCM.warn(caught);
					}
				});
	}
}
