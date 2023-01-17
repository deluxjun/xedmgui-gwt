package com.speno.xedm.gui.common.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.util.Offline;
import com.speno.xedm.core.service.serials.SDelegation;
import com.speno.xedm.core.service.serials.SDocument;
import com.speno.xedm.core.service.serials.SFolder;
import com.speno.xedm.core.service.serials.SInfo;
import com.speno.xedm.core.service.serials.SParameter;
import com.speno.xedm.core.service.serials.SRecordItem;
import com.speno.xedm.core.service.serials.SSession;
import com.speno.xedm.core.service.serials.SUser;
import com.speno.xedm.gui.common.client.log.Log;
import com.speno.xedm.gui.common.client.util.HistoryUtil;
import com.speno.xedm.gui.common.client.util.ReturnHandler;
import com.speno.xedm.gui.common.client.util.ServiceUtil;
import com.speno.xedm.gui.common.client.util.Util;
import com.speno.xedm.gui.common.client.util.WindowUtils;
import com.speno.xedm.gui.common.client.window.FolderPropertiesWindow;
import com.speno.xedm.gui.frontend.client.document.DocumentObserver;
import com.speno.xedm.gui.frontend.client.document.DocumentsPanel;
import com.speno.xedm.util.GeneralException;

/**
 * 
 * @author deluxjun
 *
 */
public class Session {
	private static Session instance;

	private SInfo info;

	private SSession session;

	private SFolder currentFolder;

	private Set<SessionObserver> sessionObservers = new HashSet<SessionObserver>();

	private List<FolderObserver> folderObservers = new ArrayList<FolderObserver>();
	
	private Set<DocumentObserver> documentObservers = new HashSet<DocumentObserver>();	// 20130725, junsoo, document observer

	private Timer timer;
	
	private String guid;

	public static Session get() {
		if (instance == null)
			instance = new Session();
		return instance;
	}

	public boolean isDemo() {
		return "demo".equals(info.getRunLevel());
	}

	public boolean isDevel() {
		return "devel".equals(info.getRunLevel());
	}

	public String getSid() {
		if (session != null)
			return session.getSid();
		else
			return null;
	}
	

	// get home dir id
	public long getHomeFolderId() {
		if (session != null)
			return session.getUser().getHomeFolderId();
		else
			return Constants.DOCUMENTS_FOLDERID;
	}

	public String getIncomingMessage() {
		if (session != null)
			return session.getIncomingMessage();
		else
			return null;
	}

	public void close() {
		session = null;
		sessionObservers.clear();
		if (timer != null)
			timer.cancel();
	}

	public SUser getUser() {
		return session.getUser();
	}
	
	public String getLastIp() {
		return session.getLastIp();
	}

	public String getLastDate() {
		return session.getLastDate();
	}
	
	// get delegations
	public SDelegation[] getDelegations() {
		return session.getDelegations();
	}
	
	public boolean isDelegator(){
		return session.isDelegator();
	}

	
	public boolean hasDelegationPriv(String menuId) {
		for (String menu : session.getDelegatedMenus()) {
			if (menuId.equals(menu))
				return true;
		}
		return false;
	}

	public void init(SSession session) {
		try {
			this.session = session;
			I18N.init(session);

			if (session.isLoggedIn()) {
				for (SessionObserver listener : sessionObservers) {
					listener.onUserLoggedIn(session.getUser());
				}
			}

			// getting message count
			if (info.getSessionHeartbeat() > 0) {
				boolean bOk = false;
				if (timer != null) {
					timer.cancel();
					timer = null;
				}
				
				timer = new Timer() {
					public void run() {
						ServiceUtil.info().getSessionInfo(getSid(), new AsyncCallback<SParameter[]>() {
							@Override
							public void onFailure(Throwable caught) {
								if (isDevel())
									System.err.println(caught.getStackTrace());

								if(caught instanceof GeneralException) {
									GeneralException e = (GeneralException)caught;

									// check session is expired
									if(Constants.INVALID_SESSION_ERR01.equals(e.getErrorCode()) || Constants.INVALID_SESSION_ERR02.equals(e.getErrorCode())){
										timer.cancel();
									}
								}
							}

							@Override
							public void onSuccess(SParameter[] parameters) {
								if (parameters != null && parameters.length > 0) {
									SUser user = getUser();
									for (SParameter parameter : parameters) {
										if (parameter.getName().equals("messages"))
											user.setUnreadMessages(Integer.parseInt(parameter.getValue()));
									}
								}
							}
						});
					}
				};
				
				timer.scheduleRepeating(info.getSessionHeartbeat() * 1000);
			}
		} catch (Throwable caught) {
			//Log.serverError("", caught, false);
			System.err.println(caught.getStackTrace());
		}
	}
	
	private Map<String,Set<ReturnHandler>> actionHandlers = new HashMap<String,Set<ReturnHandler>>();
	private Map<String,Integer> actionCountDown = new HashMap<String,Integer>();
	
	/**
	 * 특정 액션에 대해서 observer 설정.
	 * @param action : action 명
	 * @param observer
	 */
	public void addActionObserver(String action, ReturnHandler observer) {
		Set<ReturnHandler> handlers = actionHandlers.get(action);

		if (handlers == null) {
			handlers = new HashSet<ReturnHandler>();
			actionHandlers.put(action, handlers);
		}
		handlers.add(observer);
	}
	public void action(String action, Object param){
		Set<ReturnHandler> handlers = actionHandlers.get(action);
		if (handlers == null)
			return;
		for (ReturnHandler listener : handlers) {
			listener.onReturn(param);
		}
	}
	public void addActionCount(String action) {
		Integer count = actionCountDown.get(action);
		if (count == null) {
			count = new Integer(0);
		}
		count = new Integer(count + 1);
		actionCountDown.put(action, count);
	}
	public void reduceActionCount(String action) {
		reduceActionCount(action, null);
	}
	public void reduceActionCount(String action, Object param) {
		Integer count = actionCountDown.get(action);
		if (count == null) {
			return;
		}
		count = new Integer(count - 1);
		actionCountDown.put(action, count);
		
		if (count == 0) {
			action(action, param);
		}
	}

	public void addSessionObserver(SessionObserver observer) {
		sessionObservers.add(observer);
	}

	public void addFolderObserver(FolderObserver observer) {
		// 20140107, junsoo, DocumentsPanel은 맨앞으로
		if (observer instanceof DocumentsPanel)
			folderObservers.add(0, observer);
		else
			folderObservers.add(observer);
	}
	
	// 20130725, junsoo, document selected
	public void addDocumentObserver(DocumentObserver observer) {
		documentObservers.add(observer);
	}
	public void selectDocuments(SRecordItem[] items) {
		for (DocumentObserver listener : documentObservers) {
			listener.onDocumentSelected(items);
		}
	}
	
	// 20130802, junsoo, 문서가 저장되었으므로, 리스너의 saved 를 모두 호출
	public void saveDocument(SDocument doc) {
		for (DocumentObserver listener : documentObservers) {
			listener.onDocumentSaved(doc);
		}
	}


	public SFolder getCurrentFolder() {
		return currentFolder;
	}

	public void setCurrentFolder(SFolder folder) {
		Log.debug("[setCurrentFolder] " +folder.getId());
		setCurrentFolder(folder, false, false);
	}
	
	public void setCurrentFolder(SFolder folder, boolean isFilter) {
		setCurrentFolder(folder, false, isFilter);
	}
	/**
	 * 선택한 폴더의 확장 Path를 인터넷창의 제목으로 설정하고
	 * 폴더의 확장 폴더 값들을 가져온다. 
	 * @param folder
	 * @param bByHistory
	 * @param isFilter : Filter 적용시 동작
	 * */
	public void setCurrentFolder(SFolder folder, boolean bByHistory, boolean isFilter) {
		this.currentFolder = folder;
		
		
//		2013-11-27 na 웹브라우저의 제목을 DMS System 1.0으로만 표시되기로 결정
//		WindowUtils.setTitle(Session.get().getInfo(), folder.getPathExtended());
		if(!isFilter){
			for (FolderObserver listener : folderObservers) {
				listener.onFolderSelected(folder);
			}
		}
		
		// history
		if (!bByHistory) {
			String id = Constants.MENU_DOCUMENTS_MYDOC + ";";
			if (SFolder.TYPE_SHARED == folder.getType())
				id = Constants.MENU_DOCUMENTS_SHAREDDOC + ";";
			else if (SFolder.TYPE_WORKSPACE == folder.getType())
				id = Constants.MENU_DOCUMENTS_MYDOC + ";";
			HistoryUtil.get().newHistory(DocumentsPanel.get().getMenu(), Constants.MENU_DOCUMENTS + ";" + id + folder.getId());
		}
	}

	public void setReloadFolder() {
		for (FolderObserver listener : folderObservers) {
			listener.onFolderReload();
		}
	}
	
	public SInfo getInfo() {
		return info;
	}

	public void setInfo(SInfo info) {
		this.info = info;
	}
	
	public boolean isDefaultSSO() {
		if (info == null)
			return false;
		String sso = info.getConfig("gui.sso");
		if (sso != null && sso.equalsIgnoreCase("true"))
			return true;
		return false;
	}
	
	public void requestLogout(){
		ServiceUtil.security().logout(Session.get().getSid(), Session.get().getCurrentMenuId(), new AsyncCallback<Void>() {
			public void onFailure(Throwable caught) {
				// 오류 무시하고 그냥 로그아웃
//				SCM.warn(caught);
				logout();
			}

			@Override
			public void onSuccess(Void result) {
				logout();
			}
		});
	}
	
	public void logout(){
		try {
			Offline.remove(Constants.COOKIE_SID);
		} catch (Throwable t) {
			// 오류 무시
//			SC.warn(t.getMessage());
		}
		
		Session.get().close();
		
		if (Session.get().getInfo().getConfig("gui.logout.close").equalsIgnoreCase("true")) {
//			Window.alert("");
			WindowUtils.closeWindow();
		}
		else {
			String base = GWT.getHostPageBaseURL();
	//		Util.redirect(base + "?gwt.codesvr=127.0.0.1:9997");
	//		String url = GWT.getModuleName() + ".jsp" + ((isDefaultSSO())? "?sso=true" : "");
			String url = "";
	//		if (isDefaultSSO())
	//			url = "sso.jsp";
	//		else
//			if(!GWT.isScript()){
//				url = "?gwt.codesvr=127.0.0.1:9997&locale=" + I18N.getLocale();
//			}
//			else{
//				url = "?locale=" + I18N.getLocale();
//			}
	//			url = GWT.getModuleName() + ".jsp";
			Util.redirect(base + (base.endsWith("/") ? url : "/" + url));
		}
		
	}
	
	private String currentMenuId;
	// 메뉴가 바뀌면 호출됨.
	public void setCurrentMenuId(String refid){
		this.currentMenuId = refid;
		
		// documents에만 있어야 할 윈도우 제거
		// 20130820, junsoo, dialog를 모두 modal로 변경함.
//		if (!refid.startsWith("documents")) {
//			DocumentPropertiesWindow.get().close();
//			FolderPropertiesWindow.get().close();
//		}

		// 20130801, junsoo, folder 속성 창은 shared doc 에서만 표시
		if (!refid.startsWith(Constants.MENU_DOCUMENTS + ";" + Constants.MENU_DOCUMENTS_SHAREDDOC)) {
			FolderPropertiesWindow.get().close();
		}

	}

	public String getCurrentMenuId() {
		return currentMenuId;
	}

	public void setSession(SSession session) {
		this.session = session;
	}
	
	/**
	 * 20131211 na 하단 상태바의 메시지를 수동적으로 바꾸기 위한 메소드
	 */
	public void checkMessage(){
		timer.run();
		timer.scheduleRepeating(info.getSessionHeartbeat() * 1000);
	}

	public String getGuid() {
		return guid;
	}

	public void setGuid(String gUid) {
		this.guid = gUid;
	}
}