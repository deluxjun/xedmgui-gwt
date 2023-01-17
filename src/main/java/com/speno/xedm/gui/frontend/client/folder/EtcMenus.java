package com.speno.xedm.gui.frontend.client.folder;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.types.TreeModelType;
import com.smartgwt.client.types.Visibility;
import com.smartgwt.client.widgets.tree.Tree;
import com.smartgwt.client.widgets.tree.TreeNode;
import com.speno.xedm.core.service.serials.SFolder;
import com.speno.xedm.gui.common.client.Constants;
import com.speno.xedm.gui.common.client.I18N;
import com.speno.xedm.gui.common.client.Session;
import com.speno.xedm.gui.common.client.log.Log;
import com.speno.xedm.gui.common.client.util.AdminMenuUtil;
import com.speno.xedm.gui.common.client.util.ReturnHandler;
import com.speno.xedm.gui.common.client.util.ServiceUtil;
import com.speno.xedm.gui.common.client.util.Util;
import com.speno.xedm.gui.frontend.client.panels.StatusBarIcons;

public enum EtcMenus {
	ROOT			("ROOT", I18N.message("ROOT"), "ROOT"),
	CHECKOUTLIST	("documents_etc_checkout", I18N.message("CheckOutList"), "ROOT"),
	SHAREDLIST		("documents_etc_sharing", I18N.message("sharedList"), "ROOT"),
	FAVORITE		("documents_etc_favor", I18N.message("favorites"), "ROOT"),
	TRASH			("documents_etc_trash", I18N.message("trash"), "ROOT"),
	SHAREDTRASH		("documents_etc_sharedtrash", I18N.message("sharedtrash"), "ROOT"),
	// 20140207, junsoo, 폐기함 보여줄 필요가 없을 것 같아 기능 제거함
//	EXPIREDDOC		("documents_etc_expireddocs", I18N.message("second.client.expireddoc"), "ROOT"),
	APPROVE			("documents_etc_approval", I18N.message("second.client.approve"), "ROOT"),
	APPROVESTANDBY	("documents_etc_approval_standby", I18N.message("second.client.approveStandby"), "documents_etc_approval"),
	APPROVEREQUEST	("documents_etc_approval_request", I18N.message("second.client.approveRequest"), "documents_etc_approval"),
	APPROVECOMPLETE	("documents_etc_approval_complete", I18N.message("approveComplete"), "documents_etc_approval"),
	;
	
	private String id;
	private String i18n;
	private String parentId;
	private boolean enabled;	// 20130913, junsoo, 사용가능여부
	
	EtcMenus(String id, String i18n, String parentId) {
	    this.id = id;
	    this.i18n = i18n;
	    this.parentId = parentId;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getI18n() {
		return i18n;
	}
	public void setI18n(String i18n) {
		this.i18n = i18n;
	}
	public String getParentId() {
		return parentId;
	}
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	private static Tree dataTree;
	// get tree
	public static Tree getTree() {

		TreeNode rootNode = new TreeNode();
		rootNode.setID(ROOT.getId());
		rootNode.setAttribute("id", EtcMenus.ROOT.getId());
		rootNode.setAttribute("name", EtcMenus.ROOT.getParentId());

		dataTree = new Tree();
		dataTree.setModelType(TreeModelType.PARENT);
		dataTree.setIdField("id");
		dataTree.setParentIdField("parent");
		dataTree.setAutoOpenRoot(true);
		dataTree.setReportCollisions(false);
		dataTree.setShowRoot(false);
		// 루트와 트리 셋팅
		dataTree.setRoot(rootNode);

		// 모두 완료된 후 모두 오픈.
		Session.get().addActionObserver(Long.toString(Constants.MENUID_DOCUMENTS_ETC), new ReturnHandler() {
			@Override
			public void onReturn(Object param) {
				dataTree.openAll();
				
				// statusbar icon 표시가능한 것만 표시
				if (!CHECKOUTLIST.isEnabled())
					StatusBarIcons.get().hideCheckout();
			}
		});

		// dummy 로 요청하여 cache 되도록 함.
		AdminMenuUtil.get().hasPriv(Long.toString(Constants.MENUID_DOCUMENTS_ETC), Constants.MENUID_DOCUMENTS_ETC, ROOT.getId(), new AsyncCallback<Long>() {
			@Override
			public void onSuccess(Long id) {
				boolean isFoldeShared = Util.getSetting("setting.foldershared");

				for (final EtcMenus menu : EtcMenus.values()) {
					if(menu == EtcMenus.SHAREDLIST) continue;
					if (menu == EtcMenus.ROOT) continue;

					// 위임받은 사람이 아니거나, 위임받은 사람이면서 권한이 있을 때만 호출.
					if (!Session.get().isDelegator() || 
							Session.get().hasDelegationPriv(menu.getId()) || Session.get().hasDelegationPriv(menu.getParentId()))
					{
						AdminMenuUtil.get().hasPriv(Long.toString(Constants.MENUID_DOCUMENTS_ETC), Constants.MENUID_DOCUMENTS_ETC, menu.getId(), new AsyncCallback<Long>() {
							@Override
							public void onSuccess(Long id) {
								if (id != null) {
									TreeNode node = new TreeNode();
									node.setID(menu.getId());
									node.setAttribute("id", menu.getId());
									node.setAttribute("name", menu.getI18n());
									node.setAttribute("parent", menu.getParentId());
									
									TreeNode parentNode = dataTree.findById(menu.getParentId());
									dataTree.add(node, parentNode);
									menu.setEnabled(true);
								} else {
								}
							}
							@Override
							public void onFailure(Throwable caught) {
							}
						});
					}
					//공유 목록 가져오기(메뉴 생성시 가져옴)
					if(menu.getId().equals(EtcMenus.SHAREDLIST.getId())){
						ServiceUtil.folder().listFolderByShare(Session.get().getSid(), 0, "", 0, -1, new AsyncCallback<List<SFolder>>() {
							@Override
							public void onSuccess(List<SFolder> result) {
								for (SFolder folder : result) {
									TreeNode node = new TreeNode();
									node.setID(String.valueOf(folder.getId()));
									node.setAttribute("id", folder.getId());
									node.setAttribute("name", folder.getName());
									node.setAttribute("parent", EtcMenus.SHAREDLIST);
									node.setAttribute("folder", folder);
									node.setIsFolder(true);
									TreeNode parentNode = dataTree.findById(EtcMenus.SHAREDLIST.getId());
									if(parentNode != null)
										dataTree.add(node, parentNode);
								}
							}
							@Override
							public void onFailure(Throwable caught) {
								Log.serverError(caught, false);
							}
						});
					}
				}
				
			}
			@Override
			public void onFailure(Throwable caught) {
			}
		});
		
		return dataTree;
	}

}