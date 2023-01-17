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
	 * Ư�� �׼ǿ� ���ؼ� observer ����.
	 * @param action : action ��
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
		// 20140107, junsoo, DocumentsPanel�� �Ǿ�����
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
	
	// 20130802, junsoo, ������ ����Ǿ����Ƿ�, �������� saved �� ��� ȣ��
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
	 * ������ ������ Ȯ�� Path�� ���ͳ�â�� �������� �����ϰ�
	 * ������ Ȯ�� ���� ������ �����´�. 
	 * @param folder
	 * @param bByHistory
	 * @param isFilter : Filter ����� ����
	 * */
	public void setCurrentFolder(SFolder folder, boolean bByHistory, boolean isFilter) {
		this.currentFolder = folder;
		
		
//		2013-11-27 na ���������� ������ DMS System 1.0���θ� ǥ�õǱ�� ����
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
				// ���� �����ϰ� �׳� �α׾ƿ�
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
			// ���� ����
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
	// �޴��� �ٲ�� ȣ���.
	public void setCurrentMenuId(String refid){
		this.currentMenuId = refid;
		
		// documents���� �־�� �� ������ ����
		// 20130820, junsoo, dialog�� ��� modal�� ������.
//		if (!refid.startsWith("documents")) {
//			DocumentPropertiesWindow.get().close();
//			FolderPropertiesWindow.get().close();
//		}

		// 20130801, junsoo, folder �Ӽ� â�� shared doc ������ ǥ��
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
	 * 20131211 na �ϴ� ���¹��� �޽����� ���������� �ٲٱ� ���� �޼ҵ�
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